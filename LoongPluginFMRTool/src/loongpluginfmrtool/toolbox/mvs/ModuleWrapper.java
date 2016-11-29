package loongpluginfmrtool.toolbox.mvs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import loongpluginfmrtool.module.featuremodelbuilder.ModuleDependencyTable;
import loongpluginfmrtool.module.model.Module;
import loongpluginfmrtool.module.model.Variability;
import loongpluginfmrtool.module.model.Configuration;

public class ModuleWrapper {
	private Set<Module> amodules = new HashSet<Module>();
	private ModuleDependencyTable table;
	private int clusterid;
	private int totalmodulesize;	
	
	private Map<Module,Variability>moduletoVariability = new HashMap<Module,Variability>();
	private Set<Configuration>validConfigs = null;
	
	public ModuleWrapper(Module pmodule,ModuleDependencyTable ptable,int pclusterid){
		this.table = ptable;
		this.totalmodulesize = table.getTable().length;
		this.clusterid = pclusterid;
		addModule(pmodule);
	}
	
	public ModuleWrapper(Set<Module> pallmodules, ModuleDependencyTable ptable,
			int pclusterid) {
		// TODO Auto-generated constructor stub
		this.table = ptable;
		this.totalmodulesize = table.getTable().length;
		this.clusterid = pclusterid;
		addModules(pallmodules);
	}

	
	private void setModuleToVariability() {
		// TODO Auto-generated method stub
		for(Module module:this.amodules){
			Variability variab = module.getVariability();
			moduletoVariability.put(module, variab);
		}
	}

	public int getClusterId(){
		return this.clusterid;
	}
	
	public void setClusterId(int id){
		this.clusterid = id;
	}
	
	public int computeDependency(ModuleWrapper set){
		int count = 0;
		Set<Module> othermoduleset = set.getModuleSet();
		for(Module curr:amodules){
			for(Module other:othermoduleset){
				count+=table.getDependencyCount(curr, other);
			}
		}
		return count;
	}
	
	public void addModule(Module module){
		this.amodules.add(module);
		setModuleToVariability();
		updateIntraConfigurations();
	}
	public void addModules(Set<Module>modules){
		this.amodules.addAll(modules);
		setModuleToVariability();
		updateIntraConfigurations();
	}

	protected Set<Module> getModuleSet() {
		// TODO Auto-generated method stub
		return amodules;
	}

	

	public double getProbability() {
		// TODO Auto-generated method stub 
		return ((double)amodules.size())/totalmodulesize;
	}

	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		if(obj instanceof ModuleWrapper){
			ModuleWrapper set_obj = (ModuleWrapper)obj;
			return set_obj.getClusterId()==clusterid;
		}else{
			return false;
		}
		
	}
	
	public int getConfigurationCount(){
		//if(validConfigs==null){
		updateIntraConfigurations();
		//}
		return validConfigs.size();
	}
	
	
	protected void updateIntraConfigurations(){
		validConfigs = new HashSet<Configuration>();
		for(Map.Entry<Module,Variability>entry:moduletoVariability.entrySet()){
			Variability variability = entry.getValue();
			Set<Configuration> options = variability.getAllValidConfigurations();
			validConfigs.addAll(options);
		}
		Set<Configuration>needRemove = new HashSet<Configuration>();
	
		for(Configuration config:validConfigs){
			if(needRemove.contains(config))
				continue;
			Set<Configuration> conflict = hasConflict(config,validConfigs);
			if(conflict==null){
				continue;
			}else{
				needRemove.addAll(conflict);
			}
		}
		validConfigs.removeAll(needRemove);
		
	}
	
	
	/**
	 * 判断是否有冲突  如果 有 返回一个有冲突的 其中无效的
	 * null 表示无冲突
	 * @param config
	 * @param validConfigs
	 * @return
	 */
	private Set<Configuration> hasConflict(Configuration config,
			Set<Configuration> validConfigs) {
		// TODO Auto-generated method stub
		Set<Configuration> conflict = null;
		for(Configuration conf:validConfigs){
			if(conf.conflictwith(config)){
				if(conflict==null){
					conflict = new HashSet<Configuration>();
					if(conf.isInvalidUnder(config)){
						conflict.add(conf);
					}else{
						conflict.add(config);
						break;// stop checking for the rest for time saving
					}
				}
			}
		}
		
		return conflict;
	}

	public ModuleDependencyTable getDependencyTable() {
		// TODO Auto-generated method stub
		return table;
	}
	
}
