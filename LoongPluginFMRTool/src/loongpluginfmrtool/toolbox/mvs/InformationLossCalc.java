package loongpluginfmrtool.toolbox.mvs;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import loongpluginfmrtool.module.ModuledFeature;
import loongpluginfmrtool.module.featuremodelbuilder.ModuleDependencyTable;
import loongpluginfmrtool.module.model.Module;

public class InformationLossCalc {
	private ModuleDependencyTable atable;
	private int[][]dependentarray;
	
	private Set<ModuledFeature> features = new HashSet<ModuledFeature>();
	private Map<Integer, ModuledFeature> indexToFeature = new HashMap<Integer, ModuledFeature>();
	private int num_module;
	private double[][] information_loss_table_array;
	private int[][] featuredependency_table_array;
	private double[][] featuredependency_table_normalized_array;
	private double[][] kullback_leibler_table;
	private double MAXVALUE = 10000;
	
	public InformationLossCalc(){
		
	}
	
	public double computeILNeg(Set<ModuleWrapper>msetgroup,ModuleDependencyTable ptable,Map<Integer, Module>pindexToModule){
		double informationloss = 0.0;
		Set<Module>allmodules= new HashSet<Module>();
		
		// initialize
		for(ModuleWrapper wrapper:msetgroup){
			allmodules.addAll(wrapper.getModuleSet());
		}
		
		this.atable = ptable;
		
		dependentarray = atable.getTable();
		
		
		num_module = allmodules.size();
		
		int featureindex = 0;
		for(Module module:allmodules){
			ModuledFeature module_feature = new ModuledFeature(module,num_module);
			// add to set
			features.add(module_feature);
			// add the mapping
			indexToFeature.put(featureindex,module_feature);
			featureindex++;
		}
		
		// computing information loss
		
		while(features.size()!=1){
			createDependencyTable();
			normalizedDependencyTable();
			buildKullbackLeiblerTable();
			computeInformationLossTable();
			informationloss += getMinialInfoLossAndMerge();
		}
		return informationloss;
	}
	
	
	private void createDependencyTable() {
		// TODO Auto-generated method stub
		featuredependency_table_array = new int[features.size()][features.size()];
		for(int i = 0;i < features.size();i++){
			ModuledFeature feature_1 = indexToFeature.get(i);
			for(int j = 0;j < features.size();j++){
				ModuledFeature feature_2 = indexToFeature.get(j);
				featuredependency_table_array[i][j] = computeDependencyBetweenFeatures(feature_1,feature_2);
			}
		}
	}
	
	private int computeDependencyBetweenFeatures(ModuledFeature feature_1,
			ModuledFeature feature_2) {
		// TODO Auto-generated method stub
		Set<Module>module_feature_1 = feature_1.getModules();
		Set<Module>module_feature_2 = feature_2.getModules();
		int dependency = 0;
		for(Module module_1:module_feature_1){
			int index1 = module_1.getIndex();
			for(Module module_2:module_feature_2){
				int index2 = module_2.getIndex();
				dependency += dependentarray[index1][index2];
			}
		}
		return dependency;
	}
	

	private double getMinialInfoLossAndMerge() {
		// TODO Auto-generated method stub
		int indexi = 0;
		int indexj = 0;
		
		double minimalvalue = MAXVALUE;
		
		
		for(int i = 0;i < features.size();i++){
			for(int j = i+1;j < features.size();j++){
				if(minimalvalue>information_loss_table_array[i][j]){
					minimalvalue = information_loss_table_array[i][j];
					indexi = i;
					indexj = j;	
				}
			}
		}
		
		ModuledFeature feature1 = indexToFeature.get(indexi);
		ModuledFeature feature2 = indexToFeature.get(indexj);
		
		feature1.mergeModuledFeature(feature2);
		features.remove(feature2);
		
		featureinitupdate();
		
		return minimalvalue;
	}
	
	protected void featureinitupdate(){
		int index = 0;
		indexToFeature.clear();
		
		for(ModuledFeature feature:features){
			indexToFeature.put(index,feature);
			index++;
		}
	}

	protected void buildKullbackLeiblerTable() {
		// TODO Auto-generated method stub
		kullback_leibler_table = new double[features.size()][features.size()];
		for(int i = 0;i < features.size();i++){
			for(int j = i;j < features.size();j++){
				double kullbackleibler;
				if(i==j){
					kullbackleibler = 0.0;
				}else{
				    kullbackleibler = compute_Single_Kullback_Leibler(i,j);
				    kullback_leibler_table[i][j] = kullbackleibler; //DK(p||q)
				    kullback_leibler_table[j][i] = kullbackleibler;
				}
			}
		}
	}
	
	protected double compute_Single_Kullback_Leibler(int index_1,int index_2){
		double distance = 0.0;
		int module_index1 = index_1;
		int module_index2 = index_2;
		
		for(int j = 0;j < features.size();j++){
			double value_index_1 = featuredependency_table_normalized_array[module_index1][j];
			double value_index_2 = featuredependency_table_normalized_array[module_index2][j];
			if(value_index_1==0||value_index_2==0){
				continue;
			}
			double temp = Math.log(value_index_1/value_index_2);
			distance += temp*value_index_1;
		}
		
		return distance;
	}
	

	protected void normalizedDependencyTable() {
		// TODO Auto-generated method stub
		// normalize normal table
		featuredependency_table_normalized_array = new double[features.size()][features.size()];
		for(int i = 0;i < features.size();i++){
			for(int j = 0;j < features.size();j++){
				if(featuredependency_table_array[i][j]>0){
					featuredependency_table_array[i][j]=1;
				}
			}
		}
		for(int i = 0;i < features.size();i++){
			int rowtotal = 0;
			for(int j = 0;j < features.size();j++){
				rowtotal+=featuredependency_table_array[i][j];
			}
			for(int j = 0;j < features.size();j++){
				if(featuredependency_table_array[i][j]==0)
					featuredependency_table_normalized_array[i][j] = 0.0;
				else{
					double double_table = ((double)featuredependency_table_array[i][j])/rowtotal;
					featuredependency_table_normalized_array[i][j]  = double_table;
				}
			}
		}
		
	}
	
	
	private void computeInformationLossTable() {
		// TODO Auto-generated method stub
		information_loss_table_array = new double[features.size()][features.size()];
		int size = features.size();
		for(int i = 0;i < size;i++){
			for(int j = i;j < size;j++){
				if(i==j){
					information_loss_table_array[i][j] = 0;
				}else{
					information_loss_table_array[i][j] = compute_Single_InformationLoss(i,j);
					information_loss_table_array[j][i] = information_loss_table_array[i][j];
				}
			}
			
		}
	}

	protected double compute_Single_InformationLoss(int index_1,int index_2){
		double information_loss = 0.0;
		ModuledFeature module_feature1 = indexToFeature.get(index_1);
		ModuledFeature module_feature2 = indexToFeature.get(index_2);
		double pro_module_1 = module_feature1.getProbability();
		double pro_module_2 = module_feature2.getProbability();
		double temp_1 = pro_module_1+pro_module_2;
		
		double pro_module_ = pro_module_1+pro_module_2;;
		assert Double.isNaN(pro_module_)==false;
		double[] mergedkl_vector_module_1 = new double[features.size()];
		double[] mergedkl_vector_module_2 = new double[features.size()];
		double total_mergedkl_vector_module_1 = 0.0;
		double total_mergedkl_vector_module_2 = 0.0;
		// 计算 D-KL Kullback-Leibler divergence
		for(int i = 0;i < features.size();i++){
			
			double p_bar_i = pro_module_1/pro_module_*featuredependency_table_normalized_array[index_1][i]+pro_module_2/pro_module_*featuredependency_table_normalized_array[index_2][i];
			if(p_bar_i==0||featuredependency_table_normalized_array[index_1][i]==0){
				mergedkl_vector_module_1[i] = 0;
			}else
				mergedkl_vector_module_1[i] =  pro_module_1*Math.log(pro_module_1/p_bar_i);
			if(!Double.isNaN(mergedkl_vector_module_1[i]))
				total_mergedkl_vector_module_1+=mergedkl_vector_module_1[i];
			assert Double.isNaN(total_mergedkl_vector_module_1)==false;
			//assert total_mergedkl_vector_module_1>=0;
			if(p_bar_i==0||featuredependency_table_normalized_array[index_2][i]==0){
				mergedkl_vector_module_2[i] = 0;
			}else
				mergedkl_vector_module_2[i] =  pro_module_2*Math.log(pro_module_2/p_bar_i);
			if(!Double.isNaN(mergedkl_vector_module_2[i]))
				total_mergedkl_vector_module_2+=mergedkl_vector_module_2[i];
			assert Double.isNaN(total_mergedkl_vector_module_2)==false;
		}
		assert pro_module_!=0;
		double temp_2 = (pro_module_1/pro_module_)*total_mergedkl_vector_module_1+(pro_module_2/pro_module_)*total_mergedkl_vector_module_2;
		assert Double.isNaN(temp_2)==false;
		//assert temp_2>=0;
		information_loss = temp_1*temp_2;
		assert Double.isNaN(information_loss)==false;
		if(information_loss<0)
			information_loss = 0;
		return information_loss;
	}
}
