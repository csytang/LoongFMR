package loongpluginfmrtool.module.featuremodelbuilder;

import java.util.Map;

import loongpluginfmrtool.module.builder.ModuleBuilder;
import loongpluginfmrtool.module.model.Module;

public class ModuleHelper {
	private Module amodule;
	private ModuleBuilder abuilder;
	private double probability;// p(m_{i})
	private Map<Integer, Module>indexToModule;
	private double[][] normalizedtable; // p(f|m_{i})
	private int size;
	private ModuleDependencyTable dependency_table;
	private KullbackLeiblerTable kullback_table;
	public ModuleHelper(Module pmodule,ModuleBuilder pbuilder){
		this.amodule  = pmodule;
		this.abuilder = pbuilder;
		this.dependency_table = pbuilder.getDependencyTable();
		build();
	}
	protected void build(){
		this.indexToModule = this.abuilder.getIndexToModule();
		this.size = indexToModule.size();
		this.probability = (double)1.0/this.size;
		this.normalizedtable = dependency_table.getNormalizedTable();
		this.kullback_table = this.abuilder.getKullbackLeiblerTable();
	}
	
	
	public double getProbability(){
		return probability;
	}
	
	
	
}
