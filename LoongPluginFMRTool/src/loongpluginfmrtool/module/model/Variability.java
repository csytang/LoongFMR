package loongpluginfmrtool.module.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;

public class Variability {
	/**
	 * 算一个varability
	 * 然后再算每一个configuration 的比重
	 */
	public Module module;
	public int totalValidConfig = 0;
	private Set<Configuration>configurations;
	private Set<ConfigurationOption> options;
	private ConfigurationOptionTree tree;
	private Set<ConfigurationOption> roots;
	
	public Variability(Module pmodule){
		this.module = pmodule;
		this.configurations = new HashSet<Configuration>();
		this.options = this.module.getAllConfigurationOptions();
	}
	
	public Set<Configuration> getAllValidConfigurations(){
		return configurations;
	}
	
	
	protected void Collect(ConfigurationOptionTree ptree){
		this.tree = ptree;
		this.roots = this.tree.getRoots();
		for(ConfigurationOption root:roots){
			encoding(root);
		}
		totalValidConfig = configurations.size();
	}
	
	/**
	 * 从根节点到叶子节点
	 * @param queue
	 * encoding an option from a single root
	 * 
	 */
	protected void encoding(ConfigurationOption root){
		Queue<ConfigurationOption> visited = new LinkedList<ConfigurationOption>();
		Queue<ConfigurationOption> unvisited = new LinkedList<ConfigurationOption>();
		unvisited.add(root);
		
		while(!unvisited.isEmpty()){// unvisited is not empty
			ConfigurationOption head = unvisited.peek();
			if(tree.getSameMethodDirectChildren(head)!=null){
				Set<ConfigurationOption> children = tree.getSameMethodDirectChildren(head);
				for(ConfigurationOption child:children){
					if(!visited.contains(child)){
						//if(){
							unvisited.add(child);
						//}
					}
				}
			}
			visited.add(head);
			createConfigurationbyPath(head,root);
			unvisited.poll();
		}
	}
	
	private void createConfigurationbyPath(ConfigurationOption leaf,ConfigurationOption root){
		Map<ConfigurationOption,Boolean>configurationdetail = new HashMap<ConfigurationOption,Boolean>();
		ConfigurationOption curr = leaf;
		Configuration configuration ;
		if(leaf==root){
			configurationdetail.put(leaf, false);
			configuration = new Configuration(module,configurationdetail);
			configurationdetail.clear();
			configurations.add(configuration);
			
			configurationdetail.put(leaf, true);
			configuration = new Configuration(module,configurationdetail);
			configurations.add(configuration);
			return;
		}
		configurationdetail.put(curr, false);
		while(tree.getSameMethodDirectParent(curr)!=root){
			curr = tree.getSameMethodDirectParent(curr);
			configurationdetail.put(curr, true);
		}
		configurationdetail.put(root, true);
		configuration = new Configuration(module,configurationdetail);
		configurations.add(configuration);
	}
	public boolean hasConflict(Variability othervariability){
		boolean conflict = false;
		for(Configuration config:configurations){
			for(Configuration other_config:othervariability.configurations){
				if(config.conflictwith(other_config)){
					return true;
				}
			}
		}
		
		return conflict;
	}

	public int getValidConfigurationCount() {
		// TODO Auto-generated method stub
		return totalValidConfig;
	}

	public Module getModule() {
		// TODO Auto-generated method stub
		return module;
	}
	
}
