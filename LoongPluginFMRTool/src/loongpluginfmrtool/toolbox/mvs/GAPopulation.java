package loongpluginfmrtool.toolbox.mvs;




import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import loongpluginfmrtool.module.model.Module;

public class GAPopulation {
	
	private GAIndividual[] individuals;
	private int acluster_count;
	private Map<Integer,Module> indextomodules;
	private GenticClustering clustering;
	private int populationcount;
	private boolean debug = true;
	public GAPopulation(GenticClustering pclustering,int clustercount,int populationsize, boolean initialise){
		this.clustering = pclustering;
		this.indextomodules = pclustering.getIndextoModule();
		this.acluster_count = clustercount;
		individuals = new GAIndividual[populationsize];
		populationcount = populationsize;
		// update default size;
		if(initialise){
			int defaultsize = 0;
			for(int i = 0;i < populationsize;i++){
				GAIndividual individul = new GAIndividual(clustering,acluster_count);
				defaultsize = indextomodules.size();
				individul.setDefaultGeneLength(defaultsize);
				individul.initialize();
				saveIndividual(i,individul);
			}
			
		}
	}
	
	
	 /* Getters */
    public GAIndividual getIndividual(int index) {
        return individuals[index];
    }
    
    
    public void printPopulation(){
    	for(int i = 0;i < populationcount;i++){
    		GAIndividual  ind = individuals[i];
    		ind.printIndividual();
    	}
    }
    
    /* Public methods */
    // Get population size
    public int size() {
        return populationcount;
    }

    // Save individual
    public void saveIndividual(int index, GAIndividual indiv) {
        individuals[index] = indiv;
    }
	
	public GAIndividual getFittest() {
		GAIndividual fittest = individuals[0];
        // Loop through individuals to find fittest
        for (int i = 0; i < size(); i++) {
        	GAIndividual individuali = getIndividual(i);
            if (fittest.getFitness() <= individuali.getFitness()) {
                fittest = individuali;
            }
        }
        return fittest;
    }


	public int getClusterCount() {
		// TODO Auto-generated method stub
		return acluster_count;
	}
	
	
	
    
}
