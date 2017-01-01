package loongpluginfmrtool.toolbox.mvs;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jdt.core.dom.Statement;

import cc.mallet.util.Maths;
import edu.usc.softarch.arcade.util.StopWatch;
import loongpluginfmrtool.module.model.configuration.ConfigurationCondition;
import loongpluginfmrtool.module.model.configuration.ConfigurationOption;
import loongpluginfmrtool.module.model.hierarchicalstructure.HierarchicalBuilder;
import loongpluginfmrtool.module.model.hierarchicalstructure.HierarchicalNeighbor;
import loongpluginfmrtool.module.model.module.Module;
import loongpluginfmrtool.module.model.module.ModuleBuilder;
import loongpluginfmrtool.util.ClusteringResultRSFOutput;

public class VariabilityModuleSystem {
	
	private ModuleBuilder builder;
	private int cluster;
	private Map<Integer,Set<Module>> clusterres;
	private Map<Integer, Module> indexToModule;
	private HierarchicalBuilder hbuilder;
	private Map<Module,HierarchicalNeighbor> sourcetoNeighbor;
	private ConfigurationOptionTree tree;
	private Map<Module,Integer> module_clusterIndex = new HashMap<Module,Integer>();
	private boolean debug = true;
	private int[][] fixedrequired;
	private int[][] conditionalrequired;
	private int[][] fixedrequiredCluster;
	private int[][] conditionalrequiredCluster;
	private List<Integer> clusterIds = new LinkedList<Integer>();
	private Map<Module,Set<Module>> md_conditionalmodulesmodules;
	private Map<Module,Set<Module>> md_fixedrequiredmodulesmodules;
	private int modulesize;
	/**
	 * jensenShannonDivergence get distance
	 * 
	 * 
	 * 
	 */
	
	
	
	public VariabilityModuleSystem(HierarchicalBuilder phbuilder,int pcluster){
		
		this.cluster = pcluster;
		this.hbuilder = phbuilder;
		this.builder = phbuilder.getModuleBuilder();
		this.sourcetoNeighbor = hbuilder.getModuleToNeighbor();
		this.indexToModule = builder.getIndexToModule();
		this.clusterres = new HashMap<Integer,Set<Module>>();
		this.tree = phbuilder.getConfigurationOptionTree();
		
		// create two tables in terms of module reference
		initModuleReference();
		
		// do the clustering task
		performClustering();
		
		//ClusteringResultRSFOutput output = new ClusteringResultRSFOutput(clusterres,"modulevariabilitysystem",builder.gettargetProject());
		ClusteringResultRSFOutput.ModuledRSFOutput(clusterres,"vms",builder.getsubjectProject());
	}
	
	/**
	 * 
	 */
	private void initModuleReference() {
		// reset
		Set<Module> temp_fixed_required = new HashSet<Module>();
		Set<Module> temp_conditional_required = new HashSet<Module>();
		md_conditionalmodulesmodules = new HashMap<Module,Set<Module>>();
		md_fixedrequiredmodulesmodules = new HashMap<Module,Set<Module>>();
		modulesize = indexToModule.size();
		fixedrequired = new int[modulesize][modulesize];
		fixedrequiredCluster = new int[modulesize][modulesize];
		conditionalrequired = new int[modulesize][modulesize];
		conditionalrequiredCluster = new int[modulesize][modulesize];
		for(int i =0;i < modulesize;i++){
			for(int j = 0;j < modulesize;j++){
				if(i==j){
					fixedrequired[i][j] = 1;
					fixedrequiredCluster[i][j]=1;
					conditionalrequired[i][j] = 1;
					conditionalrequiredCluster[i][j] = 1;
				}else{
					fixedrequired[i][j] = 0;
					fixedrequiredCluster[i][j] = 0;
					conditionalrequired[i][j] = 0;
					conditionalrequiredCluster[i][j] = 0;
				}
			}
		}
		
		
		
	
		// initial full set mapping
		for(Map.Entry<Integer, Module>entry:indexToModule.entrySet()){
			int index = entry.getKey();
			clusterIds.add(index);
			Module module = entry.getValue();
			Set<Module> module_set = new HashSet<Module>();
			
			module_set.add(module);
			module_clusterIndex.put(module, index);
			clusterres.put(index, module_set);
			
		}
		
		
		for(Map.Entry<Integer,Set<Module>>entry:this.clusterres.entrySet()){
			// reset
			
			temp_fixed_required.clear();
			temp_conditional_required.clear();
			
			
			
			// get all modules in the iteration
			Set<Module> modules = entry.getValue();
			assert modules.size()==1;
			
			
			// run these modules
			for(Module md:modules){
				
				int md_index = md.getIndex();
				// get the hierarchical neighbor of this module
				HierarchicalNeighbor md_neigbhor = this.sourcetoNeighbor.get(md);
				
				// get all fixed required
				Set<Module> md_fixedrequired = md_neigbhor.getfixedRequired();
				
				Set<Module> md_fixedrequired_update = new HashSet<Module>();
				// remove fixed required that already in the cluster
				for(Module md_fix:md_fixedrequired){
					if(!modules.contains(md_fix)){
						int md_fix_index = md_fix.getIndex();
						md_fixedrequired_update.add(md_fix);
						fixedrequired[md_index][md_fix_index]+=1;
						fixedrequiredCluster[md_index][md_fix_index]+=1;
					}
				}
				
				// release this object
				md_fixedrequired = md_fixedrequired_update;
				md_fixedrequired_update = null;
				
				temp_fixed_required.addAll(md_fixedrequired);
				
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
					temp_conditional_required.addAll(alloptionalmds);
					
					for(Module optionalmd:alloptionalmds){
						int md_optional_index = optionalmd.getIndex();
						//conditionalrequired[md_optional_index][md_index]+=1;
						//conditionalrequiredCluster[md_optional_index][md_index]+=1;
						conditionalrequired[md_index][md_optional_index]+=1;
						conditionalrequiredCluster[md_index][md_optional_index]+=1;
					}
				}
				
				
				// check statistic values
				md_conditionalmodulesmodules.put(md, temp_conditional_required);
				md_fixedrequiredmodulesmodules.put(md, temp_fixed_required);
			}
		}
	}

	public void performClustering(){
		
		
		StopWatch stopwatch = new StopWatch();
		
		stopwatch.start();
		// do clustering
////////******************************RUN****************************////////
		while(clusterres.size() > this.cluster){
			System.out.println("current size:"+this.clusterres.size());
			double smallestdistance = Double.MAX_VALUE;
			int cluster_1 = -1;
			int cluster_2 = -1;
			for(int i = 0;i < clusterIds.size();i++){
				int sourceclusterId = clusterIds.get(i);
				int[] sourcefixed = fixedrequiredCluster[sourceclusterId];
				double[] sourcefixed_double = copyFromIntArray(sourcefixed);
				int[] sourceconditionfix = conditionalrequiredCluster[sourceclusterId];
				double[] sourceconditionfix_double = copyFromIntArray(sourceconditionfix);
				for(int j = i+1;j < clusterIds.size();j++){
					int targetclusterId = clusterIds.get(j);
					int[] targetfixed = fixedrequiredCluster[targetclusterId];
					double[] targetfixed_double = copyFromIntArray(targetfixed);
					int[] targetconditionfix = conditionalrequiredCluster[targetclusterId];
					double[] targetconditionfix_double = copyFromIntArray(targetconditionfix);
					
					double fix_required_distance = Maths.jensenShannonDivergence(sourcefixed_double, targetfixed_double);
					double conditional_distance = Maths.jensenShannonDivergence(sourceconditionfix_double, targetconditionfix_double);
					
					double overalldistance = fix_required_distance+conditional_distance;
					//double overalldistance = fix_required_distance;
					if(overalldistance < smallestdistance){
						smallestdistance = overalldistance;
						cluster_1 = sourceclusterId;
						cluster_2 = targetclusterId;
					}
				}
			}
			assert cluster_1!=-1;
	     	assert cluster_2!=-1;
	     	System.out.println("merge:"+cluster_1+"\t\t"+cluster_2);
			mergecluster(cluster_1,cluster_2);
			updateReferenceArrays(cluster_1,cluster_2);
		}
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
	
	/**
	 * this method will merge cluster with id cluster_1 and cluster with id cluster_2 into
	 * cluster_1 and leave cluster_2 unchanged
	 * @param cluster_1
	 * @param cluster_2
	 */
	private void mergecluster(int cluster_1, int cluster_2) {
		/*
		 * Make changes on followings
	 		clusterres;
			module_clusterIndex 
			clusterIds
		 */
		// process clusterIds : remove cluster_2
		
		Set<Module> mds_cluster_2 = clusterres.get(cluster_2);
		Set<Module> mds_cluster_1 = clusterres.get(cluster_1);
		
		for(Module md_2:mds_cluster_2){
			
			// move module in cluster_2 into cluster_1
			mds_cluster_1.add(md_2);
			
			module_clusterIndex.put(md_2, cluster_1);
		}
		
		// update the clusterres set
		clusterres.put(cluster_1, mds_cluster_1);
		clusterres.remove(cluster_2);
		int cluster_2_index = clusterIds.indexOf(cluster_2);
		clusterIds.remove(cluster_2_index);
	}

	/**
	 * this method will merge cluster_2 into cluster_1 and then update all 
	 * reference information
	 * 
	 * @param cluster_1
	 * @param cluster_2
	 */
	private void updateReferenceArrays(int cluster_1, int cluster_2) {
		/*
	private int[][] fixedrequiredCluster;
	private int[][] conditionalrquiredCluster;
		 */
		Set<Module> md_cluster_1  = clusterres.get(cluster_1);
		
		// fixed required module by cluster 1
		Set<Module> md_fixedrequired_by_cluster_1 = new HashSet<Module>();
		
		// conditional required by cluster 1
		Set<Module> md_conditional_by_cluster_1 = new HashSet<Module>();
		
		for(Module md:md_cluster_1){
			if(md_fixedrequiredmodulesmodules.containsKey(md)){
				md_fixedrequired_by_cluster_1.addAll(md_fixedrequiredmodulesmodules.get(md));
			}
			if(md_conditionalmodulesmodules.containsKey(md)){
				md_conditional_by_cluster_1.addAll(md_conditionalmodulesmodules.get(md));
			}
		}
		for(int i = 0;i < this.modulesize;i++){
			/*
			 * process fixedrequiredCluster[cluster_1][i]
			 * process conditionalrequiredCluster[cluster_1][i]
			 */
			if(!clusterres.containsKey(i)){
				// already merged into other clusters
				continue;
			}
			Set<Module> md_cluster = clusterres.get(i);
			Set<Module> md_fixedrequired_by_cluster_i = getfixedrequiredbymodules(md_fixedrequiredmodulesmodules,md_cluster);
			Set<Module> md_conditionalrequired_by_cluster_i = getconditionalrequiredbymodules(md_conditionalmodulesmodules,md_cluster);
			if(i==cluster_1){
				fixedrequiredCluster[cluster_1][i]=0;
				conditionalrequiredCluster[cluster_1][i]=0;
			}else{
				fixedrequiredCluster[cluster_1][i]=0;
				conditionalrequiredCluster[cluster_1][i]=0;
				for(Module md:md_cluster){
					if(md_fixedrequired_by_cluster_1.contains(md)){
						fixedrequiredCluster[cluster_1][i]++;
					}
					if(md_conditional_by_cluster_1.contains(md)){
						conditionalrequiredCluster[cluster_1][i]++;
					}
				}
				
				// for i --> cluster_1
				fixedrequiredCluster[i][cluster_1]=0;
				conditionalrequiredCluster[i][cluster_1]=0;
				for(Module md:md_fixedrequired_by_cluster_i){
					if(md_cluster.contains(md)){
						fixedrequiredCluster[i][cluster_1]++;
					}
				}
				
				for(Module md:md_conditionalrequired_by_cluster_i){
					if(md_cluster.contains(md)){
						conditionalrequiredCluster[i][cluster_1]++;
					}
				}
				
			}
			
		}
		
		
	}

	private Set<Module> getfixedrequiredbymodules(Map<Module, Set<Module>> md_fixedrequiredmodulesmodules,
			Set<Module> md_cluster) {
		// TODO Auto-generated method stub
		Set<Module> fixedrequired = new HashSet<Module>();
		for(Module md:md_cluster){
			if(md_fixedrequiredmodulesmodules.containsKey(md)){
				fixedrequired.addAll(md_fixedrequiredmodulesmodules.get(md));
			}
		}
		return fixedrequired;
	}
	
	private Set<Module> getconditionalrequiredbymodules(Map<Module, Set<Module>> md_conditionalrequiredmodulesmodules,
			Set<Module> md_cluster){
		Set<Module> conditionalrequired = new HashSet<Module>();
		for(Module md:md_cluster){
			if(md_conditionalrequiredmodulesmodules.containsKey(md)){
				conditionalrequired.addAll(md_conditionalrequiredmodulesmodules.get(md));
			}
		}
		return conditionalrequired;
	}

	public LinkedList<Module> getRootToNode(Module md){
		LinkedList<Module> rootPath = new LinkedList<Module>();
		rootPath.addFirst(md);
		Set<Module> roots = tree.getparentModules();
		Module curr = md;
		while(!roots.contains(curr)){
		   Module temp= tree.getOptionalParent(curr);
		   if(temp==null)
			   break;
		   rootPath.addFirst(temp);
		   curr = temp;
		}
		if(curr!=md)
			rootPath.addFirst(curr);
		return rootPath;
	}
	
	public static double[] copyFromIntArray(int[] source) {
	    double[] dest = new double[source.length];
	    for(int i=0; i<source.length; i++) {
	        dest[i] = source[i];
	    }
	    return dest;
	}
}