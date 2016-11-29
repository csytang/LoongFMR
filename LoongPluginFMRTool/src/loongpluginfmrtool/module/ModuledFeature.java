package loongpluginfmrtool.module;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import loongpluginfmrtool.module.model.Module;
import loongpluginfmrtool.module.model.Variability;

public class ModuledFeature {
	private String featurename = "unknown";
	private Set<Module>modules = new HashSet<Module>();
	private int totalsize;
	
	
	public ModuledFeature(){
		
	}
		
	public ModuledFeature(Module module,int ptotalsize){
		this.modules.add(module);
		this.totalsize = ptotalsize;
		
	}
	
	
	
	
	public double getProbability(){
		return ((double)modules.size())/this.totalsize;
	}
	public boolean containModule(Module module){
		return this.modules.contains(module);
	}
	
	public void addModule(Module module){
		this.modules.add(module);
	}
	
	public void mergeModuledFeature(ModuledFeature other){
		this.modules.addAll(other.modules);
		
	}

	public Set<Module> getModules(){
		return this.modules;
	}
	public int size() {
		// TODO Auto-generated method stub
		return modules.size();
	}
	
}
