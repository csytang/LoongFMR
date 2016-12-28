package loongpluginfmrtool.toolbox.mvs;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import loongpluginfmrtool.module.model.hierarchicalstructure.HierarchicalBuilder;
import loongpluginfmrtool.module.model.hierarchicalstructure.HierarchicalNeighbor;
import loongpluginfmrtool.module.model.module.Module;
import loongpluginfmrtool.module.model.module.ModuleBuilder;
import loongpluginfmrtool.util.ClusteringResultRSFOutput;

public class VariabilityModuleSystem {
	
	private ModuleBuilder builder;
	private int cluster;
	private Map<Integer,Set<Module>> clusterres;
	private Map<Integer, Module> indexToModule;
	private HierarchicalBuilder hbuilder;
	private Map<Module,HierarchicalNeighbor> sourcetoNeighbor;
	private ConfigurationOptionTree tree;
	private Map<Module,Integer> module_clusterIndex = new HashMap<Module,Integer>();
	
	public VariabilityModuleSystem(HierarchicalBuilder phbuilder,int pcluster,int ppopulationcount,int pevoluation){
		
		this.cluster = pcluster;
		this.hbuilder = phbuilder;
		this.builder = phbuilder.getModuleBuilder();
		this.sourcetoNeighbor = hbuilder.getModuleToNeighbor();
		this.indexToModule = builder.getIndexToModule();
		this.clusterres = new HashMap<Integer,Set<Module>>();
		this.tree = phbuilder.getConfigurationOptionTree();
		
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
			module_clusterIndex.put(module, index);
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
			int requiredcountforinfluence = 0;
			for(Map.Entry<Integer,Set<Module>>entry:this.clusterres.entrySet()){
				Set<Module> moduleset = entry.getValue();
				/*
				 * find all fixed required for the module in the set
				 */
				Set<Module> temp_fixed_required = new HashSet<Module>();
				for(Module md:moduleset){
					assert this.sourcetoNeighbor.containsKey(md);
					HierarchicalNeighbor md_neigbhor = this.sourcetoNeighbor.get(md);
					/**
					 * fixed required
					 */
					Set<Module> fixedmodules = md_neigbhor.getfixedRequired();
					
					/**
					 * optional path from current node to its root
					 */
					LinkedList<Module> rootPath = getRootToNode(md);
					if(fixedmodules!=null && fixedmodules.size()>0){
						// there is a fixed required
						int remark = -1;
						for(int i = 0;i < rootPath.size();i++){
							Module parent_i = rootPath.get(i);
							if(fixedmodules.contains(parent_i)){
								temp_fixed_required.add(parent_i);
								remark = i;
								break;
							}
						}
						// there is a value but not count as the root
						if(remark!=-1){
							for(int i = remark;i < rootPath.size();i++){
								temp_fixed_required.add(rootPath.get(i));
							}
						}		
					}
				}
				
				// if the influence if larger than the current value
				if(requiredcountforinfluence < temp_fixed_required.size()){
					requiredcountforinfluence = temp_fixed_required.size();
					recruteid = entry.getKey();
				}
				
				
				
				
			}
		}

		
		
		
		
		
	}
	
	
	public LinkedList<Module> getRootToNode(Module md){
		LinkedList<Module> rootPath = new LinkedList<Module>();
		
		
		return rootPath;
	}
	
	
}