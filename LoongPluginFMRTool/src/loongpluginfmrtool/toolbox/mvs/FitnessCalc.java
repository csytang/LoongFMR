package loongpluginfmrtool.toolbox.mvs;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.Vector;

import loongpluginfmrtool.module.featuremodelbuilder.ModuleDependencyTable;
import loongpluginfmrtool.module.model.Module;


public class FitnessCalc {

	private ModuleDependencyTable dependency_table;
	private int modulesize;
	private int clustercount;
	private Map<Integer,Set<ModuleWrapper>>clusterToModules = new HashMap<Integer,Set<ModuleWrapper>>();// 类id 到 类内 module
	private Map<Integer,Module>indexToModules = new HashMap<Integer,Module>();
	private List<Set<ModuleWrapper>>listedWrapperGroup = new LinkedList<Set<ModuleWrapper>>();
	private double informationloss = 0.0;
	private double variabilitygain = 0.0;
	private double modularityvalue = 0.0;
	
	public double getFitnessValue(GAIndividual gaIndividual,Map<Integer, Module>pindexToModule,int cluster) {
		// TODO Auto-generated method stub
		// Information Loss; Variability Loss; Modularity
		double fitness = 0.0;
		Vector<Integer> gene = gaIndividual.getGene();
		modulesize = gene.size();
		dependency_table = gaIndividual.getGeneClustering().getDependencyTable();
		indexToModules = pindexToModule;
		clustercount = cluster;
		//convert the indextoModuel 
		/**
		 * mdid : the id of the module
		 * mdcluster: the id of the cluster
		 */
		
		//////////////////////////////////////////////////////////////////////////////////////////

		for(int mdid = 0;mdid <gene.size();mdid++){
			int mdcluster = gene.get(mdid);
			if(clusterToModules.containsKey(mdcluster)){
				Set<ModuleWrapper> mset = clusterToModules.get(mdcluster);
				mset.add(new ModuleWrapper(indexToModules.get(mdid),dependency_table,mdcluster));
			}else{
				Set<Module>moduleset = new HashSet<Module>();
				moduleset.add(indexToModules.get(mdid));
				ModuleWrapper mwrapper = new ModuleWrapper(moduleset,dependency_table,mdcluster);
				Set<ModuleWrapper> mset= new HashSet<ModuleWrapper>();
				mset.add(mwrapper);
				clusterToModules.put(mdcluster, mset);
			}
		}
		//////////////////////////////////////////////////////////////////////////////////////////
		modularityvalue = 0.0;
		variabilitygain = 0.0;
		double intramodularity = 0.0;
		double intermodularity = 0.0;
		
		informationloss = 0.0;
		
		
		for(Map.Entry<Integer, Set<ModuleWrapper>>entry:clusterToModules.entrySet()){
			Set<ModuleWrapper> wrapperset = entry.getValue();
			
			// variability loss
			double variabilitylossiteration = VariabilityLoss.computeVLossPos(wrapperset);
			variabilitygain+=variabilitylossiteration;
			
			ModuleQualityMetrics metrics = new ModuleQualityMetrics(wrapperset);
			intramodularity+=metrics.getIntraConnectMSet1();
			listedWrapperGroup.add(wrapperset);
			
			//information loss 
			InformationLossCalc infolosscal = new InformationLossCalc();
			informationloss+=infolosscal.computeILNeg(wrapperset, dependency_table, indexToModules);
			
		}
		
		intramodularity = intramodularity / clustercount;
		for(int i= 0;i < listedWrapperGroup.size();i++){
			Set<ModuleWrapper> wrapperseti = listedWrapperGroup.get(i);
			for(int j = i+1;j < listedWrapperGroup.size();j++){
				Set<ModuleWrapper> wrappersetj = listedWrapperGroup.get(j);
				ModuleQualityMetrics metrics = new ModuleQualityMetrics(wrapperseti,wrappersetj);
				intermodularity+=metrics.getInterConnect();
			}
		}
		intermodularity = intermodularity/ (clustercount*(clustercount-1)/2);
		
		modularityvalue = intramodularity-intermodularity;
		
		fitness = modularityvalue-informationloss+variabilitygain;
		//fitness = modularityvalue-informationloss;
		
		return fitness;
	}

	public double getVariabilityLoss() {
		// TODO Auto-generated method stub
		return variabilitygain;
	}

	public double getModuleQuality() {
		// TODO Auto-generated method stub
		return modularityvalue;
	}

	public double getInformationLoss() {
		// TODO Auto-generated method stub
		return informationloss;
	}
	
	

}
