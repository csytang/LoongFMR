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
	
	private Set<Module> roots;
	private HierarchicalBuilder hbuilder;
	private Map<Module,Set<Module>> option_parentChildren = new HashMap<Module,Set<Module>>();
	private Map<Module,Module> option_childParent = new HashMap<Module,Module>();
	private Map<Module,HierarchicalNeighbor> module_Neighbor;
	
	

	/**
	 * multiple roots
	 */
	public ConfigurationOptionTree(Set<Module> pmultipleRoots,HierarchicalBuilder phbuilder){
		this.roots = pmultipleRoots;
		this.hbuilder = phbuilder;
		build();
	}
	
	
	private void build() {
		Set<Module> visited = new HashSet<Module>();
		Queue<Module> tree_queue = new LinkedList<Module>();
		// initial
		
		tree_queue.addAll(roots);
		
		//  run all elements recursively
		while(!tree_queue.isEmpty()){
			Module head = tree_queue.poll();
			
			// already visited
			if(visited.contains(head))
				continue;
			
			visited.add(head);
			Set<Module> childrens = getOptionChildren(head);
			if(childrens==null||childrens.size()==0)
				continue;
			option_parentChildren.put(head, childrens);
			
			for(Module child:childrens){
				option_childParent.put(child, head);
				tree_queue.add(child);
			}
		}
		
		
	}
	
	
	private Set<Module> getOptionChildren(Module module){
		assert this.module_Neighbor.containsKey(module);
		HierarchicalNeighbor neighbor = this.module_Neighbor.get(module);
		Set<Module> conditionalmodules = neighbor.getconditionalModulesSet();
		return conditionalmodules;
	}
}
