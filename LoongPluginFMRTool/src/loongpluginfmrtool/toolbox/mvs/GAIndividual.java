package loongpluginfmrtool.toolbox.mvs;

import java.util.BitSet;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Vector;

import loongpluginfmrtool.module.model.Module;


public class GAIndividual {
	private Vector<Integer> genes = new Vector<Integer>();
	private double fitness = 0;
	private GenticClustering clustering;
	private int lenth;
	private int clustersize;
	private double informationlosscount;
	private double modulequalitycount;
	private double variabilitygain;
	
	public GAIndividual(GenticClustering pclustering,int pclustersize){
		this.clustering = pclustering;
		this.clustersize = pclustersize;
	}
	public void initialize(){
		for(int i = 0;i < lenth;i++){
			Random random = new Random();
			int randomcluster = random.nextInt(clustersize);
			this.genes.add(i,randomcluster);
		}
	}
	public GAIndividual(GAIndividual individual) {
		// TODO Auto-generated constructor stub
		this.clustering = individual.clustering;
		this.lenth = individual.lenth;
		for(int i = 0;i < lenth;i++){
			this.genes.add(i, individual.getGene(i));
		}
	}

	public GenticClustering getGeneClustering(){
		return this.clustering;
	}
	// Create a random individual
	
    
	/* Getters and setters */
    // Use this if you want to create individuals with different gene lengths
    public void setDefaultGeneLength(int length) {
    	lenth = length;
    	genes = new Vector<Integer>(length);
    }
    
    /* Public methods */
    public int size() {
        return genes.capacity();
    }
    
    public int getGene(int index) {
        return genes.get(index);
    }
    
    public void setGene(int index, int value) {
    	if(genes.isEmpty()){
    		genes.add(index,value);
    	}else if(genes.size()<=index){
    		genes.add(index,value);
    	}else if(genes.elementAt(index)==null){
    		genes.add(index,value);
    	}else
    		genes.set(index,value);
        fitness = 0;
    }
    
    
    public Vector<Integer> getGene(){
    	return genes;
    }

	public double getFitness() {
		// TODO Auto-generated method stub
		if (fitness == 0) {
			FitnessCalc cal = new FitnessCalc();
            fitness = cal.getFitnessValue(this,clustering.getIndextoModule(),clustersize);
            informationlosscount = cal.getInformationLoss();
            modulequalitycount = cal.getModuleQuality();
            variabilitygain = cal.getVariabilityLoss();
        }
        return fitness;
	}
	
	

	public void printIndividual() {
		// TODO Auto-generated method stub
		for(int ge:genes){
			System.out.print(ge);
			System.out.print("\t");
		}
		System.out.println();
	}
	public double getVariabilityLoss() {
		// TODO Auto-generated method stub
		return variabilitygain;
	}
	public double getInformationLoss() {
		// TODO Auto-generated method stub
		return informationlosscount;
	}
	public double getModuleQuality() {
		// TODO Auto-generated method stub
		return modulequalitycount;
	}
	
	
	

	
    
}
