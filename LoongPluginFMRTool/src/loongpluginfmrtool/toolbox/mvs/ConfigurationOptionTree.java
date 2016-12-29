package loongpluginfmrtool.toolbox.mvs;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import loongpluginfmrtool.module.model.configuration.ConfigurationCondition;
import loongpluginfmrtool.module.model.hierarchicalstructure.HierarchicalBuilder;
import loongpluginfmrtool.module.model.hierarchicalstructure.HierarchicalNeighbor;
import loongpluginfmrtool.module.model.module.Module;

public class ConfigurationOptionTree {
	
	
	private HierarchicalBuilder hbuilder;
	private Map<Module,Set<Module>> option_parentChildren = new HashMap<Module,Set<Module>>();
	private Map<Module,Module> option_childParent = new HashMap<Module,Module>();
	private Map<Module,HierarchicalNeighbor> module_Neighbor;
	private Set<Module> parentModules;
	private Map<Module,Integer> moduleLevel = new HashMap<Module,Integer>();
	private int treeDepth = 0;
	/**
	 * multiple roots
	 */
	public ConfigurationOptionTree(Set<Module> aparentModules,HierarchicalBuilder phbuilder){
		this.parentModules = aparentModules;
		this.hbuilder = phbuilder;
		this.module_Neighbor = this.hbuilder.getModuleToNeighbor();
		build();
	}
	
	
	private void build() {
		
		Set<Module> visited = new HashSet<Module>();
		Queue<Module> tree_queue = new LinkedList<Module>();
		// initial
		
		tree_queue.addAll(parentModules);
		
		for(Module md:parentModules){
			moduleLevel.put(md, 1);
		}
		
		//  run all elements recursively
		while(!tree_queue.isEmpty()){
			Module head = tree_queue.poll();
			
			// already visited
			if(visited.contains(head))
				continue;
			
			visited.add(head);
			Set<Module> childrens = getOptionalChildren(head);
			if(childrens==null||childrens.size()==0)
				continue;
			option_parentChildren.put(head, childrens);
			
			
			for(Module child:childrens){
				option_childParent.put(child, head);
				tree_queue.add(child);
				int parentlevel = moduleLevel.get(head);
				moduleLevel.put(child, 1+parentlevel);
				treeDepth = 1+parentlevel;
			}
		}
		
		
	}
	
	
	public Set<Module> getOptionalChildren(Module module){
		assert this.module_Neighbor.containsKey(module);
		HierarchicalNeighbor neighbor = this.module_Neighbor.get(module);
		Set<Module> conditionalmodules = neighbor.getconditionalModulesSet();
		return conditionalmodules;
	}
	
	public Module getOptionalParent(Module module){
		if(option_childParent.containsKey(module)){
			return option_childParent.get(module);
		}else if(parentModules.contains(module)){
			return null;
		}else{
			return null;
		}
			
	}


	public Set<Module> getparentModules() {
		return parentModules;
	}
	
	public int getLevel(Module md){
		if(moduleLevel.containsKey(md)){
			return this.treeDepth-this.moduleLevel.get(md)+1;
		}else
			return 1;
	}
}
