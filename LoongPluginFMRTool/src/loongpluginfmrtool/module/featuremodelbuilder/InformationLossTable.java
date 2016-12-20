package loongpluginfmrtool.module.featuremodelbuilder;

import java.util.Map;

import loongpluginfmrtool.module.model.module.Module;
import loongpluginfmrtool.module.model.module.ModuleBuilder;

public class InformationLossTable {
	private ModuleBuilder abuilder;
	private double[][] kullback_leibler_table;
	private double[][] dependency_table;
	private double[][] information_loss;
	private int totalsize;
	private Map<Integer,Module> indexToModule;
	private boolean debug = true;
	public InformationLossTable(ModuleBuilder pbuilder){
		this.abuilder = pbuilder;
		this.dependency_table = this.abuilder.getDependencyTable().getNormalizedTable();
		this.kullback_leibler_table = this.abuilder.getKullbackLeiblerTable().getTable();
		this.indexToModule = this.abuilder.getIndexToModule();
		this.totalsize = this.indexToModule.size();
		information_loss = new double[totalsize][totalsize];
	}
	public void buildTable(){
		for(int i = 0;i < totalsize;i++){
			for(int j=i;j < totalsize;j++){
				if(j==i){
					information_loss[i][j] = 0;
				}else{
					information_loss[i][j] = compute_Single_InformationLoss(i,j);
					information_loss[j][i] = information_loss[i][j];
				}
			}
		}
		if(debug){
			printTable();
		}
	}
	
	public void printTable(){
		for(int i = 0;i < totalsize;i++){
			for(int j=0;j < totalsize;j++){
				
				System.out.print(information_loss[i][j]);
				System.out.print("\t");
			}
			System.out.println();
		}
	}
	
	protected double compute_Single_InformationLoss(int index_1,int index_2){
		double information_loss = 0.0;
		Module module_1 = indexToModule.get(index_1);
		Module module_2 = indexToModule.get(index_2);
		double pro_module_1 = module_1.getModuleHelper().getProbability();
		double pro_module_2 = module_2.getModuleHelper().getProbability();
		double temp_1 = pro_module_1+pro_module_2;
		double pro_module_ = temp_1;
		assert Double.isNaN(pro_module_)==false;
		double[] mergedkl_vector_module_1 = new double[totalsize];
		double[] mergedkl_vector_module_2 = new double[totalsize];
		double total_mergedkl_vector_module_1 = 0.0;
		double total_mergedkl_vector_module_2 = 0.0;
		// 计算 D-KL Kullback-Leibler divergence
		for(int i = 0;i < totalsize;i++){
			
			double p_bar_i = pro_module_1/pro_module_*dependency_table[index_1][i]+pro_module_2/pro_module_*dependency_table[index_2][i];
			if(p_bar_i==0||dependency_table[index_1][i]==0){
				mergedkl_vector_module_1[i] = 0;
			}else
				mergedkl_vector_module_1[i] =  dependency_table[index_1][i]*Math.log(dependency_table[index_1][i]/p_bar_i);
			total_mergedkl_vector_module_1+=mergedkl_vector_module_1[i];
			assert Double.isNaN(total_mergedkl_vector_module_1)==false;
			if(p_bar_i==0||dependency_table[index_2][i]==0){
				mergedkl_vector_module_2[i] = 0;
			}else
				mergedkl_vector_module_2[i] =  dependency_table[index_2][i]*Math.log(dependency_table[index_2][i]/p_bar_i);
			total_mergedkl_vector_module_2+=mergedkl_vector_module_2[i];
			assert Double.isNaN(total_mergedkl_vector_module_2)==false;
		}
		
		double temp_2 = pro_module_1/pro_module_*total_mergedkl_vector_module_1+pro_module_2/pro_module_*total_mergedkl_vector_module_2;
		assert Double.isNaN(temp_2)==false;
		information_loss = temp_1*temp_2;
		assert Double.isNaN(information_loss)==false;
		return information_loss;
	}
	
}
