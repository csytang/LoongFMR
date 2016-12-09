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
	private GAPopulation population;
	private GenticClustering clustering;
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
		performClustering();
		outputResult();
		//ClusteringResultRSFOutput output = new ClusteringResultRSFOutput(clusterres,"modulevariabilitysystem",builder.gettargetProject());
		ClusteringResultRSFOutput.ModuledRSFOutput(clusterres,"vms",builder.gettargetProject());
	}
	
	private void outputResult() {
		// TODO Auto-generated method stub
		
	}

	protected void performClustering(){
		/*
		 * 
		 */
		clustering = new GenticClustering(indexToModule,cluster,populationcount,dependency_table);
		population =  clustering.getInitialGAPopulation();
		for(int i = 0;i < evoluation;i++){
			population = clustering.evolvePopulation(population);
			if(debug){
				GAIndividual fitnessind = population.getFittest();
				System.out.println(fitnessind.getFitness()+"["+"VL:"+fitnessind.getVariabilityLoss()+"]");
			}
		}
		translatePopulation(population.getFittest(),cluster,indexToModule);
	}
	
	protected void translatePopulation(GAIndividual fitness,int cluster,Map<Integer, Module>indexToModule){
		// range 0-cluster-1
		Map<Integer,Set<Module>>clusters = new HashMap<Integer,Set<Module>>();
		Vector<Integer> genes = fitness.getGene();
		for(int i = 0;i < genes.size();i++){
			int clusterid = genes.get(i);
			Module module = indexToModule.get(i);
			if(clusters.containsKey(clusterid)){
				Set<Module> modules = clusters.get(clusterid);
				modules.add(module);
				clusters.put(clusterid, modules);
			}else{
				Set<Module> modules = new HashSet<Module>();
				modules.add(module);
				clusters.put(clusterid, modules);
			}
		}
		clusterres = clusters;
		for(Map.Entry<Integer, Set<Module>>entry:clusters.entrySet()){
			System.out.println("\t index:"+entry.getKey());
			System.out.println("\t feature contains:");
			for(Module submodule:entry.getValue()){
				System.out.println("\t \tModule:"+submodule.getDisplayName());
			}
		}
	}
}