package loongpluginfmrtool.toolbox.mvs;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
	private double variabilitygain = 0.0;
	
	public double getFitnessValue(GAIndividual gaIndividual,Map<Integer, Module>pindexToModule,int cluster) {
		// TODO Auto-generated method stub
		// Information Loss; Variability Loss; Modularity
		double fitness = 0.0;
		Vector<Integer> gene = gaIndividual.getGene();
		modulesize = gene.size();
		dependency_table = gaIndividual.getGeneClustering().getDependencyTable();
		indexToModules = pindexToModule;
		clustercount = cluster;
		// convert the indextoModuel 
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
		
		variabilitygain = 0.0;
		
		for(Map.Entry<Integer, Set<ModuleWrapper>>entry:clusterToModules.entrySet()){
			Set<ModuleWrapper> wrapperset = entry.getValue();
			
			// variability loss
			double variabilitylossiteration = VariabilityLoss.computeVLossPos(wrapperset);
			variabilitygain+=variabilitylossiteration;
		}
		
		fitness = variabilitygain;
		
		return fitness;
	}

	public double getVariabilityLoss() {
		// TODO Auto-generated method stub
		return variabilitygain;
	}

	
	
	

}
