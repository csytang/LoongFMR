package loongpluginfmrtool.toolbox.mvs;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import cc.mallet.util.Maths;
import edu.usc.softarch.arcade.topics.DocTopicItem;
import edu.usc.softarch.arcade.topics.TopicUtil;
import edu.usc.softarch.arcade.util.StopWatch;
import loongpluginfmrtool.module.model.configuration.ConfigurationCondition;
import loongpluginfmrtool.module.model.configuration.ConfigurationOption;
import loongpluginfmrtool.module.model.hierarchicalstructure.HierarchicalBuilder;
import loongpluginfmrtool.module.model.hierarchicalstructure.HierarchicalNeighbor;
import loongpluginfmrtool.module.model.module.Module;
import loongpluginfmrtool.module.model.module.ModuleBuilder;
import loongpluginfmrtool.util.ClusteringResultRSFOutput;
import loongpluginfmrtool.util.VectorDistance;

import org.eclipse.core.resources.IProject;
import org.eclipse.swt.widgets.Shell;

public class VariabilityModuleSystem {
	
	private ModuleBuilder builder;
	private int cluster;
	private Map<Integer,Set<Module>> clusterres;
	private Map<Integer, Module> indexToModule;
	private HierarchicalBuilder hbuilder;
	private Map<Module,HierarchicalNeighbor> sourcetoNeighbor;
	private ConfigurationOptionTree tree;
	private Map<Module,Integer> module_clusterIndex = new HashMap<Module,Integer>();
	private double[][] fixedrequired;
	private double[][] conditionalrequired;
	private double[][] methodref;
	private Module mainModule;
	private int modulesize;
	private IProject aProject;
	private Corpus corpus;
	private Set<Module> commonmodules = new HashSet<Module>();
	private Map<Module,DocTopicItem> dtItemMap;
	/**
	 * jensenShannonDivergence get distance
	 * 
	 * 
	 * 
	 */
	
	
	
	public VariabilityModuleSystem(HierarchicalBuilder phbuilder,int pcluster,Module entrancemodule){
		
		this.cluster = pcluster;
		this.hbuilder = phbuilder;
		this.builder = phbuilder.getModuleBuilder();
		this.sourcetoNeighbor = hbuilder.getModuleToNeighbor();
		this.indexToModule = builder.getIndexToModule();
		this.clusterres = new HashMap<Integer,Set<Module>>();
		this.tree = phbuilder.getConfigurationOptionTree();
		this.aProject = this.builder.getsubjectProject();
		
		
		
		// create two tables in terms of module reference
		initModuleReference();
		normalizeReference();
		
		// build module topics
		buildModuleTopics();
		
		// find common module;
		findCommonModule();
		
		
		// do the clustering task
		performClustering();
		
		//ClusteringResultRSFOutput output = new ClusteringResultRSFOutput(clusterres,"modulevariabilitysystem",builder.gettargetProject());
		ClusteringResultRSFOutput.ModuledRSFOutput(clusterres,"vms",builder.getsubjectProject());
	}
	
	

	private void findCommonModule(){
		
		
	}
	

	/**
	 * normalize the reference 
	 */
	private void normalizeReference() {
		/**
		 * private double[][] fixedrequired;
			private double[][] conditionalrequired;
		 */
		double[][] fixedrequirednormalized = new double[fixedrequired.length][fixedrequired.length];
		double[][] conditionalrequirednormalized = new double [fixedrequired.length][fixedrequired.length];
		double[][] methodrefnormalized = new double[fixedrequired.length][fixedrequired.length];
		for(int i = 0;i < fixedrequired.length;i++){
			double maxfixline = getMax(fixedrequired[i]);
			double minfixline = getMin(fixedrequired[i]);
			
			double maxcondline = getMax(conditionalrequired[i]);
			double mincondline = getMin(conditionalrequired[i]);
			
			double maxmethodrefline = getMax(methodref[i]);
			double minmethodrefline = getMin(methodref[i]);
			
			for(int j = 0; j < fixedrequired.length;j++){
				if(maxfixline==minfixline){
					if(maxfixline>1){
						fixedrequirednormalized[i][j] = 1;
					}else{
						fixedrequirednormalized[i][j] = 0;
					}
					
				}else{
					fixedrequirednormalized[i][j] = (fixedrequired[i][j]-minfixline)/(maxfixline-minfixline);
				}
				
				
				// conditional 
				
				if(maxcondline==mincondline){
					if(maxcondline>1){
						conditionalrequirednormalized[i][j] = 1;
					}else{
						conditionalrequirednormalized[i][j] = 0;
					}
					
				}else{
					conditionalrequirednormalized[i][j] = (conditionalrequired[i][j]-mincondline)/(maxcondline-mincondline);
				}
				
				// mehtod ref
				
				if(maxmethodrefline==minmethodrefline){
					if(maxmethodrefline>1){
						methodrefnormalized[i][j] = 0;
					}else{
						methodrefnormalized[i][j] = 0;
					}
					
				}else{
					methodrefnormalized[i][j] = (methodref[i][j]-minmethodrefline)/(maxmethodrefline-minmethodrefline);
				}
				
			}
			
		}
		
		fixedrequired = fixedrequirednormalized;
		conditionalrequired = conditionalrequirednormalized;
		methodref = methodrefnormalized;
	}
	
	

	public static double getMax(double[] inputArray){ 
	    double maxValue = inputArray[0]; 
	    for(int i=1;i < inputArray.length;i++){ 
	      if(inputArray[i] > maxValue){ 
	         maxValue = inputArray[i]; 
	      } 
	    } 
	    return maxValue; 
	  }
	 
	  // Method for getting the minimum value
	  public static double getMin(double[] inputArray){ 
	    double minValue = inputArray[0]; 
	    for(int i=1;i<inputArray.length;i++){ 
	      if(inputArray[i] < minValue){ 
	        minValue = inputArray[i]; 
	      } 
	    } 
	    return minValue; 
	  } 

	/**
	 * build the topic modelling
	 */
	private void buildModuleTopics() {
		corpus = new Corpus(this.indexToModule.size(),this.indexToModule,this.aProject);
		
		dtItemMap = corpus.getModuleToDocTopic();
	}

	/**
	 * 
	 */
	private void initModuleReference() {
		// reset
		
		
		modulesize = indexToModule.size();
		fixedrequired = new double[modulesize][modulesize];
		conditionalrequired = new double[modulesize][modulesize];
		methodref = new double[modulesize][modulesize];
		
		for(int i =0;i < modulesize;i++){
			for(int j = 0;j < modulesize;j++){
				if(i==j){
					fixedrequired[i][j] = 1;
					conditionalrequired[i][j] = 0;
					methodref[i][j] = 0;
				}else{
					fixedrequired[i][j] = 0;
					conditionalrequired[i][j] = 0;
					methodref[i][j] = 0;
				}
			}
		}
	
		// initial full set mapping
		for(Map.Entry<Integer, Module>entry:indexToModule.entrySet()){
			int index = entry.getKey();
			Module module = entry.getValue();
			Set<Module> module_set = new HashSet<Module>();
			module_set.add(module);
		}
		
		
		for(Map.Entry<Integer,Module>entry:indexToModule.entrySet()){
			// reset
			
			
			
			
			// get all modules in the iteration
			Module md = entry.getValue();
			
			
			// run these modules
			
				
			int md_index = md.getIndex();
			// get the hierarchical neighbor of this module
			HierarchicalNeighbor md_neigbhor = this.sourcetoNeighbor.get(md);
				
			// get all fixed required
			Set<Module> md_fixedrequired = md_neigbhor.getfixedRequired();
				
			
			for(Module md_fix:md_fixedrequired){
				if(md!=md_fix){
					int md_fix_index = md_fix.getIndex();
					fixedrequired[md_index][md_fix_index]=+1;
				}
			}
				
			Set<Module> md_methodrequired = md_neigbhor.getmethodRefence();
			for(Module md_fix:md_methodrequired){
				if(md!=md_fix){
					int md_fix_index = md_fix.getIndex();
					methodref[md_index][md_fix_index]=+1;
				}
			}
			
				
			// just check module starts from this iteratively
			Set<ConfigurationOption> options = md.getAllConfigurationOptions();
			if(options.isEmpty())
				continue;
				
			// add modules under all options
			for(ConfigurationOption op:options){
				ConfigurationCondition confcond = op.getConfigurationCondition();
				Set<Module> alloptionalmds = confcond.getAllAffectedModule();
				if(alloptionalmds.isEmpty())
						continue;
			
				for(Module optionalmd:alloptionalmds){
					int md_optional_index = optionalmd.getIndex();
					conditionalrequired[md_index][md_optional_index]=+1;
				}
			}
				
				
			
			
		}
	}
	
	
	public void performClustering(){
		StopWatch stopwatch = new StopWatch();
		
		stopwatch.start();
		// do clustering
		
////////******************************RUN****************************////////
		// hierarchial clustering
		
////////******************************RUN****************************////////

		stopwatch.stop();

		// Statistics
		String timeInSecsToComputeClusters = "Time in seconds to compute clusters: "
				+ stopwatch.getElapsedTimeSecs();
		String timeInMilliSecondsToComputeClusters = "Time in milliseconds to compute clusters: "
				+ stopwatch.getElapsedTime();
		System.out.println(timeInSecsToComputeClusters);
		System.out.println(timeInMilliSecondsToComputeClusters);
		System.out.println("Finish clustering");
		
		
	}
	
	
	
	private void mergecluster(int merge_sourceclusterid, int merge_targetclusterid) {
		Set<Module> clustersource = clusterres.get(merge_sourceclusterid);
		Set<Module> clustertarget = clusterres.get(merge_targetclusterid);
		for(Module md:clustertarget){
			clustersource.add(md);
			module_clusterIndex.put(md, merge_sourceclusterid);
		}
		clusterres.put(merge_sourceclusterid, clustersource);
		clusterres.remove(merge_targetclusterid);
		
	}



	private double computedistance(Set<Module> cluster,Set<Module>othercluster){
		// complete-linkage agglomerative algorithm
		double averagedisance = -1;
		for(Module md:cluster){
			double mddistance = getAverage(md,othercluster);
			if(mddistance>averagedisance){
				averagedisance = mddistance;
			}
		}
		return averagedisance;
	}
	
	private double getAverage(Module module, Set<Module> cluster){
		double maxdistance = 0.0;
		assert this.dtItemMap.containsKey(module);
		DocTopicItem moduledoc = this.dtItemMap.get(module);
		int sourceindex = module.getIndex();
		double [] source_fix_vector = fixedrequired[sourceindex];
		double [] source_cond_vector = conditionalrequired[sourceindex];
		double [] source_method_vector = methodref[sourceindex];
		for(Module md:cluster){
			if(md==module)
				continue;
			int targetindex = md.getIndex();
			assert this.dtItemMap.containsKey(md);
			DocTopicItem mddoc = this.dtItemMap.get(md);
			double [] target_fix_vector = fixedrequired[targetindex];
			double [] target_cond_vector = conditionalrequired[targetindex];
			double [] target_method_vector = methodref[targetindex];
			//double distance_typelinkconstrain = VectorDistance.generalizedjaccardDistance(source_fix_vector, target_fix_vector);
			//double distance_conditionalreference = VectorDistance.generalizedjaccardDistance(source_cond_vector, target_cond_vector);
			double distance_typelinkconstrain = Maths.jensenShannonDivergence(source_fix_vector, target_fix_vector);
			//System.out.println("typelink distance1:"+distance_typelinkconstrain);
			double distance_conditionalreference = Maths.jensenShannonDivergence(source_cond_vector, target_cond_vector);
			//System.out.println("conditional reference distance2:"+distance_conditionalreference);
			double distance_topic = TopicUtil.jsDivergence(moduledoc, mddoc);
			//System.out.println("topic distance2:"+distance_topic);
			double distance_method = Maths.jensenShannonDivergence(source_method_vector, target_method_vector);
			double overalldis = distance_typelinkconstrain+distance_method-distance_typelinkconstrain*distance_method;
			overalldis = overalldis+distance_topic-distance_topic*overalldis;
			overalldis = overalldis+distance_conditionalreference-distance_conditionalreference*overalldis;
			//double overalldis = 0.3*distance_typelinkconstrain+0.3*distance_method+0.2*distance_conditionalreference+0.2*distance_topic;
			maxdistance+=overalldis;
		}
		return maxdistance/cluster.size();
	}



	public void setselectedmodule(Module selected) {
		// TODO Auto-generated method stub
		mainModule = selected;
	}
	
	
}