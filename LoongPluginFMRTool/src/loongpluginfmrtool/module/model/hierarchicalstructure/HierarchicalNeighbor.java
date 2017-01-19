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
	private Set<Module> typerequired = new HashSet<Module>();
	private Set<Module> methodreference = new HashSet<Module>();
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
	 * add type required to this module
	 * @return
	 */
	public void addTypeRef(Module module){
		typerequired.add(module);
	}
	
	public void addMethodRef(Module module){
		methodreference.add(module);
	}
	
	public Set<Module> getTypeModuleRequired(){
		return this.typerequired;
	}
	
	public Set<Module> getMethodRefence(){
		return this.methodreference;
	}
	
	public Map<ConfigurationCondition,Set<Module>> getconditionalModules(){
		return this.condition_targetmodule;
	}

	public Set<Module> getconditionalModulesSet() {
		// TODO Auto-generated method stub
		Set<Module>mds = new HashSet<Module>();
		for(Map.Entry<ConfigurationCondition, Set<Module>>entry:this.condition_targetmodule.entrySet()){
			mds.addAll(entry.getValue());
		}
		
		return mds;
	}
}
