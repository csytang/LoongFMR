package loongpluginfmrtool.toolbox.mvs;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.eclipse.core.resources.IProject;

import loongpluginfmrtool.module.builder.ModuleBuilder;
import loongpluginfmrtool.module.featuremodelbuilder.ModuleDependencyTable;
import loongpluginfmrtool.module.model.Module;
import loongpluginfmrtool.util.ClusteringResultRSFOutput;

public class VariabilityModuleSystem {
	
	private ModuleBuilder builder;
	private Map<Integer, Module>indexToModule = new HashMap<Integer, Module>();
	private ModuleDependencyTable dependency_table;
	private int populationcount;
	private int cluster;
	
	private int evoluation;
	private boolean debug = true;
	private Map<Integer,Set<Module>>clusterres;
	public VariabilityModuleSystem(ModuleBuilder pbuilder,int pcluster,int ppopulationcount,int pevoluation){
		this.builder = pbuilder;
		this.cluster = pcluster;
		this.indexToModule = this.builder.getIndexToModule();
		this.dependency_table = this.builder.getDependencyTable();
		this.populationcount = ppopulationcount;
		this.evoluation = pevoluation;
		
		//ClusteringResultRSFOutput output = new ClusteringResultRSFOutput(clusterres,"modulevariabilitysystem",builder.gettargetProject());
		ClusteringResultRSFOutput.ModuledRSFOutput(clusterres,"vms",builder.gettargetProject());
	}
	
	
}