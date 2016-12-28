package loongpluginfmrtool.toolbox.mvs;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import loongpluginfmrtool.module.model.hierarchicalstructure.HierarchicalBuilder;
import loongpluginfmrtool.module.model.module.Module;
import loongpluginfmrtool.module.model.module.ModuleBuilder;
import loongpluginfmrtool.util.ClusteringResultRSFOutput;

public class VariabilityModuleSystem {
	
	private ModuleBuilder builder;
	private int cluster;
	private Map<Integer,Set<Module>> clusterres;
	private Map<Integer, Module> indexToModule;
	public VariabilityModuleSystem(ModuleBuilder pbuilder,HierarchicalBuilder hbuilder,int pcluster,int ppopulationcount,int pevoluation){
		this.builder = pbuilder;
		this.cluster = pcluster;
		this.indexToModule = pbuilder.getIndexToModule();
		this.clusterres = new HashMap<Integer,Set<Module>>();
		
		// do the clustering task
		performClustering();
		
		//ClusteringResultRSFOutput output = new ClusteringResultRSFOutput(clusterres,"modulevariabilitysystem",builder.gettargetProject());
		ClusteringResultRSFOutput.ModuledRSFOutput(clusterres,"vms",builder.getsubjectProject());
	}
	
	public void performClustering(){
		// initial full set mapping
		for(Map.Entry<Integer, Module>entry:indexToModule.entrySet()){
			int index = entry.getKey();
			Module module = entry.getValue();
			Set<Module> module_set = new HashSet<Module>();
			module_set.add(module);
			clusterres.put(index, module_set);
		}
		
		// do clustering
		// step 1.
		while(clusterres.size() > this.cluster){
			/*
			 * run all  clustering in clusterres and 
			 * cluster without and configuration parent
			 */
			int recruteid = -1;
			for(Map.Entry<Integer,Set<Module>>entry:this.clusterres.entrySet()){
				Set<Module> moduleset = entry.getValue();
				/*
				 * find all fixed required for the module in the set
				 */
				Set<Module> temp_fixed_required = new HashSet<Module>();
				for(Module md:moduleset){
					
				}
				
			}
			
			
		}
		
	}
	
}