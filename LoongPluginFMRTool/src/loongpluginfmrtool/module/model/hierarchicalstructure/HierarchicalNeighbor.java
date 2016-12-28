package loongpluginfmrtool.module.model.hierarchicalstructure;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import loongpluginfmrtool.module.model.configuration.ConfigurationCondition;
import loongpluginfmrtool.module.model.module.Module;

public class HierarchicalNeighbor {
	private Module asource;
	private Map<ConfigurationCondition,Set<Module>> condition_targetmodule = new HashMap<ConfigurationCondition,Set<Module>>();
	private Set<Module>fixedrequired = new HashSet<Module>();
	
	public HierarchicalNeighbor(Module psource){
		this.asource = psource;
	}
	
	/**
	 * this method will allow user/program to add
	 * a jump from 
	 */
	public void addNeighbor(ConfigurationCondition cond,Module module){
		if(condition_targetmodule.containsKey(cond)){
			// if it is already recorded as cond
			Set<Module> targets = condition_targetmodule.get(cond);
			targets.add(module);
			this.condition_targetmodule.put(cond, targets);
		}else{
			Set<Module> targets = new HashSet<Module>();
			targets.add(module);
			this.condition_targetmodule.put(cond, targets);
		}
	}
	
	/**
	 * add fix module to this module
	 * @return
	 */
	public void addNeighbor(Module module){
		fixedrequired.add(module);
	}
	
	public Set<Module> getfixedRequired(){
		return this.fixedrequired;
	}
	
	
	
	public Map<ConfigurationCondition,Set<Module>> getconditionalModules(){
		return this.condition_targetmodule;
	}
}
