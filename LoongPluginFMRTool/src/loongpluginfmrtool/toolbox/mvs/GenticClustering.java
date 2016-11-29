package loongpluginfmrtool.toolbox.mvs;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import loongpluginfmrtool.module.featuremodelbuilder.ModuleDependencyTable;
import loongpluginfmrtool.module.model.Module;



public class GenticClustering {
	
	 /* GA parameters */
    private static final double uniformRate = 0.5;
    private static final int tournamentSize = 5;
    private static final double mutationRate = 0.015;

    private static final boolean elitism = true;
    private Map<Integer, Module>indexToModule;
    private int cluster;
    private ModuleDependencyTable table;
    private GAPopulation initpoluation;
    private int populationsize;
    /**
     * 
     * @param pindexToModule
     * @param pcluster # of cluster
     * @param population # of population
     * @param ptable # table
     */
	public GenticClustering(Map<Integer, Module>pindexToModule,int pcluster,int population,ModuleDependencyTable ptable){
		this.indexToModule = pindexToModule;
		this.cluster = pcluster;
		this.table = ptable;
		this.populationsize = population;
		this.initpoluation = new GAPopulation(this,pcluster,populationsize,true);
	}
	
	public GAPopulation getInitialGAPopulation(){
		return initpoluation;
	}
	
	public ModuleDependencyTable getDependencyTable(){
		return this.table;
	}
	
	// Evolve a population种群进化
	public GAPopulation evolvePopulation(GAPopulation pop) {
		// Keep our best individual
		/**
		 * GenticClustering pclustering,int clustercount,int populationsize, boolean initialise
		 */
		GAPopulation newPopulation = new GAPopulation(this,pop.getClusterCount(),this.populationsize,false);
		
        if (elitism) {
            newPopulation.saveIndividual(0, pop.getFittest());
            
        }

        // Crossover population
        int elitismOffset;
        if (elitism) {
            elitismOffset = 1;
        } else {
            elitismOffset = 0;
        }
        
        // Loop over the population size and create new individuals with
        // crossover 交叉
        //  交叉 的 处理
        
        for (int i = elitismOffset; i < pop.size(); i++) {
            GAIndividual indiv1 = tournamentSelection(pop);
            GAIndividual indiv2 = tournamentSelection(pop);
            GAIndividual newIndiv = GACrossOver.crossover(indiv1, indiv2,this,cluster,uniformRate);
            newPopulation.saveIndividual(i, newIndiv);
        }
       
        // mutate
        // Mutate population
        for (int i = elitismOffset; i < newPopulation.size(); i++) {
            mutate(newPopulation.getIndividual(i));
        }
        

        return newPopulation;
	}

	private void mutate(GAIndividual individual) {
		// TODO Auto-generated method stub
		// Loop through genes
        for (int i = 0; i < individual.size(); i++) {
            if (Math.random() <= mutationRate) {
                // Create random gene
            	Random rad =  new Random();
                int gene = rad.nextInt(cluster);
                individual.setGene(i, gene);
            }
        }
	}

	/**
	 * 锦标赛选择算法 会选择出来一个在种群 和另一个进行交换 
	 * @param pop
	 * @return
	 */
	public GAIndividual tournamentSelection(GAPopulation pop) {
		// TODO Auto-generated method stub
		// Create a tournament population
        GAPopulation tournament = new GAPopulation(this,cluster,tournamentSize,false);
        // For each place in the tournament get a random individual
        for (int i = 0; i < tournamentSize; i++) {
            int randomId = (int) (Math.random() * pop.size());
            tournament.saveIndividual(i, pop.getIndividual(randomId));
        }
        // Get the fittest
        GAIndividual fittest = tournament.getFittest();
		return fittest;
	}
	
	
    
    


	

	public Map<Integer, Module> getIndextoModule() {
		// TODO Auto-generated method stub
		return indexToModule;
	}
    
	
}
