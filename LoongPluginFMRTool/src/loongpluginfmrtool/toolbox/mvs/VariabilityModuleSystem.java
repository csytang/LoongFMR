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
	private boolean debug = true;
	
	public VariabilityModuleSystem(HierarchicalBuilder phbuilder,int pcluster){
		
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
////////******************************RUN****************************////////
		while(clusterres.size() > this.cluster){
			/*
			 * run all  clustering in clusterres and 
			 * cluster without and configuration parent
			 */
			System.out.println("Current cluster size:"+clusterres.size());
			int recruteid = -1;
			int minrequiredcountforinfluence = Integer.MAX_VALUE;
			Set<Module> temp_fixed_required = new HashSet<Module>();
			
			
			Set<Integer> mergedIds = new HashSet<Integer>();
			////////*********clustering run*************************///////////
			for(Map.Entry<Integer,Set<Module>>entry:this.clusterres.entrySet()){
				Set<Module> moduleset = entry.getValue();
				/*
				 * find all fixed required for the module in the set
				 */
				// clean all required merge need
				temp_fixed_required.clear();
				int influenceValue = 0;
				LinkedList<Module> rootPath;
				for(Module md:moduleset){
					assert this.sourcetoNeighbor.containsKey(md);
					HierarchicalNeighbor md_neigbhor = this.sourcetoNeighbor.get(md);
					int sourceIndex = module_clusterIndex.get(md);
					/**
					 * fixed required
					 */
					Set<Module> fixedmodules = md_neigbhor.getfixedRequired();
					// if the fixed require module is not in the same cluster, then we will add
				
					
					/**
					 * optional path from current node to its root
					 */
					rootPath = getRootToNode(md);
					
					// run all in the fixed required part
					
					if(fixedmodules!=null && fixedmodules.size()>0){
						// there is a fixed required
						int remark = -1;
						for(int i = 0;i < rootPath.size();i++){
							Module parent_i = rootPath.get(i);
							if(fixedmodules.contains(parent_i)){
								if(module_clusterIndex.get(parent_i)!=sourceIndex){
									if(!temp_fixed_required.contains(parent_i)){
										temp_fixed_required.add(parent_i);
										influenceValue+=tree.getLevel(parent_i);
									}
									remark = i;
									break;
								}
							}
						}
						// there is a value but not count as the root
						if(remark!=-1){
							for(int i = remark;i < rootPath.size();i++){
								if(module_clusterIndex.get(rootPath.get(i))!=sourceIndex){
									if(!temp_fixed_required.contains(rootPath.get(i))){
										temp_fixed_required.add(rootPath.get(i));
										influenceValue+=tree.getLevel(rootPath.get(i));
									}
								}
							}
						}		
					}
					
					// run all fixed module in the module set
					for(Module fixedmd:fixedmodules){
						if(module_clusterIndex.get(fixedmd)!=sourceIndex){
							if(!temp_fixed_required.contains(fixedmd)){
								temp_fixed_required.add(fixedmd);
								influenceValue+=tree.getLevel(fixedmd);
							}
							rootPath = getRootToNode(fixedmd);
							if(fixedmodules!=null && fixedmodules.size()>0){
								// there is a fixed required
								int remark = -1;
								for(int i = 0;i < rootPath.size();i++){
									Module parent_i = rootPath.get(i);
									if(fixedmodules.contains(parent_i)){
										if(module_clusterIndex.get(parent_i)!=sourceIndex){
											if(!temp_fixed_required.contains(parent_i)){
												influenceValue+=tree.getLevel(parent_i);
												temp_fixed_required.add(parent_i);
											}
											remark = i;
											break;
										}
									}
								}
								// there is a value but not count as the root
								if(remark!=-1){
									for(int i = remark;i < rootPath.size();i++){
										if(module_clusterIndex.get(rootPath.get(i))!=sourceIndex)
											if(!temp_fixed_required.contains(rootPath.get(i))){
												influenceValue+=tree.getLevel(rootPath.get(i));
												temp_fixed_required.add(rootPath.get(i));
											}
									}
								}		
							}
							
						}
					}
					
					
			   }
				
				
				// if the influence if larger than the current value
				if(minrequiredcountforinfluence > influenceValue && temp_fixed_required.size()>1){
					mergedIds.clear();
					minrequiredcountforinfluence = influenceValue;
					recruteid = entry.getKey();
					mergedIds.add(recruteid);
					for(Module required_module:temp_fixed_required){
						int required_module_id = module_clusterIndex.get(required_module);
						mergedIds.add(required_module_id);
					}
				}
			
			}
			
			if(mergedIds.size()==0)
				break;
			////////*********clustering run*************************///////////			
			// merge the set in mergeIds
			/*
			 * change ::
			 * clusterres
			 * module_clusterIndex
			 */
			List<Integer> mergedIdList = new LinkedList<Integer>(mergedIds);
			if(debug){
				System.out.print("Merge:\t");
				for(int id:mergedIdList){
					System.out.print(id+"\t");
				}
				System.out.println();
			}
			int source = mergedIdList.get(0);
			Set<Module> sourcemoduleset = clusterres.get(source);
			for(int i = 1;i < mergedIdList.size();i++){
				Set<Module> shouldmerge = clusterres.get(mergedIdList.get(i));
				for(Module shouldmergemd:shouldmerge){
					module_clusterIndex.put(shouldmergemd, source);
				}
				sourcemoduleset.addAll(shouldmerge);
				clusterres.remove(mergedIdList.get(i));
			}
			clusterres.put(source, sourcemoduleset);
			
			
			minrequiredcountforinfluence = Integer.MAX_VALUE;
		}
////////******************************RUN****************************////////

		
		
		
		
		
	}
	
	
	public LinkedList<Module> getRootToNode(Module md){
		LinkedList<Module> rootPath = new LinkedList<Module>();
		rootPath.addFirst(md);
		Set<Module> roots = tree.getparentModules();
		Module curr = md;
		while(!roots.contains(curr)){
		   Module temp= tree.getOptionalParent(curr);
		   if(temp==null)
			   break;
		   rootPath.addFirst(temp);
		   curr = temp;
		}
		if(curr!=md)
			rootPath.addFirst(curr);
		return rootPath;
	}
	
	
}