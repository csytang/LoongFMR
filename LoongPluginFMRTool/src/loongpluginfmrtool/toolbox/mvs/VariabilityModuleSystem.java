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
import edu.usc.softarch.arcade.util.StopWatch;
import loongpluginfmrtool.module.model.configuration.ConfigurationCondition;
import loongpluginfmrtool.module.model.configuration.ConfigurationOption;
import loongpluginfmrtool.module.model.hierarchicalstructure.HierarchicalBuilder;
import loongpluginfmrtool.module.model.hierarchicalstructure.HierarchicalNeighbor;
import loongpluginfmrtool.module.model.module.Module;
import loongpluginfmrtool.module.model.module.ModuleBuilder;
import loongpluginfmrtool.util.ClusteringResultRSFOutput;
import org.eclipse.core.resources.IProject;

public class VariabilityModuleSystem {
	
	private ModuleBuilder builder;
	private int cluster;
	private Map<Integer,Set<Module>> clusterres;
	private Map<Integer, Module> indexToModule;
	private Map<Module,ModuleTopic> moduleToTopics;
	private HierarchicalBuilder hbuilder;
	private Map<Module,HierarchicalNeighbor> sourcetoNeighbor;
	private ConfigurationOptionTree tree;
	private Map<Module,Integer> module_clusterIndex = new HashMap<Module,Integer>();
	private double[][] fixedrequired;
	private double[][] conditionalrequired;
	private Map<Module,Set<Module>> md_conditionalmodulesmodules;
	private Map<Module,Set<Module>> md_fixedrequiredmodulesmodules;
	private int modulesize;
	private IProject aProject;
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
		this.aProject = this.builder.getsubjectProject();
		
		// build module topics
		buildModuleTopics();
		
		// create two tables in terms of module reference
		initModuleReference();
		initCenter();
		
		// do the clustering task
		performClustering();
		
		//ClusteringResultRSFOutput output = new ClusteringResultRSFOutput(clusterres,"modulevariabilitysystem",builder.gettargetProject());
		ClusteringResultRSFOutput.ModuledRSFOutput(clusterres,"vms",builder.getsubjectProject());
	}
	
	/**
	 * build the topic modelling
	 */
	private void buildModuleTopics() {
		moduleToTopics = new HashMap<Module,ModuleTopic>();
		
		
		for(int i = 0;i < indexToModule.size();i++){
			Module module = indexToModule.get(i);
			ModuleTopic mtopics = new ModuleTopic(module,indexToModule.size(),this.aProject);
		}
		
		
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
		fixedrequired = new double[modulesize][modulesize];
		conditionalrequired = new double[modulesize][modulesize];
		for(int i =0;i < modulesize;i++){
			for(int j = 0;j < modulesize;j++){
				if(i==j){
					fixedrequired[i][j] = 1;
					conditionalrequired[i][j] = 1;
				}else{
					fixedrequired[i][j] = 0;
					conditionalrequired[i][j] = 0;
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
			
			temp_fixed_required.clear();
			temp_conditional_required.clear();
			
			
			
			// get all modules in the iteration
			Module md = entry.getValue();
			
			
			// run these modules
			
				
			int md_index = md.getIndex();
			// get the hierarchical neighbor of this module
			HierarchicalNeighbor md_neigbhor = this.sourcetoNeighbor.get(md);
				
			// get all fixed required
			Set<Module> md_fixedrequired = md_neigbhor.getfixedRequired();
				
			Set<Module> md_fixedrequired_update = new HashSet<Module>();
			// remove fixed required that already in the cluster
			for(Module md_fix:md_fixedrequired){
				if(md!=md_fix){
					int md_fix_index = md_fix.getIndex();
					md_fixedrequired_update.add(md_fix);
					fixedrequired[md_index][md_fix_index]=1;
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
					conditionalrequired[md_optional_index][md_index]=1;
				}
			}
				
				
			// check statistic values
			md_conditionalmodulesmodules.put(md, temp_conditional_required);
			md_fixedrequiredmodulesmodules.put(md, temp_fixed_required);
			
		}
	}

	/**
	 * 初始化质心
	 */
	public void initCenter(){
		// init seed
		Set<Module> rootsModule = tree.getparentModules();
		List<Module> rootsModuleList = new LinkedList<Module>(rootsModule);
		Set<Module> mdset;
		if(rootsModule.size()>=cluster){
			for(int i = 0;i < cluster;i++){
				mdset = new HashSet<Module>();
				mdset.add(rootsModuleList.get(i));
				clusterres.put(i, mdset);
				module_clusterIndex.put(rootsModuleList.get(i), i);
			}
		}else{
			for(int i =0 ;i < rootsModuleList.size();i++){
				mdset = new HashSet<Module>();
				mdset.add(rootsModuleList.get(i));
				clusterres.put(i, mdset);
				module_clusterIndex.put(rootsModuleList.get(i), i);
			}
			
			// select other into clusterres
			int need = cluster-clusterres.size();
			
			List<Module>randomselect = randomselect(need,rootsModule,indexToModule.values());
			
			// put these other seed into clusterres
			for(int i = 0;i < randomselect.size();i++){
				Module randmd = randomselect.get(i);
				mdset = new HashSet<Module>();
				mdset.add(randmd);
				
				int currsize = clusterres.size();
				module_clusterIndex.put(randmd, currsize);
				clusterres.put(currsize, mdset);
			}
		}
	}
	
	
	public void performClustering(){
		StopWatch stopwatch = new StopWatch();
		
		stopwatch.start();
		// do clustering
		
////////******************************RUN****************************////////
		// k-means
		// initialize
		int loop = 1;
		boolean centerchange = true;
		while(centerchange){
			centerchange = false;
			loop++;
			System.out.println("Loop:"+loop);
			for(int i = 0;i < indexToModule.size();i++){
				Module module = indexToModule.get(i);
				double cloesetdistance = Double.MAX_VALUE;
				int mergeclusterId = -1;
				for(Map.Entry<Integer,Set<Module>>entry:clusterres.entrySet()){
					int clusterId = entry.getKey();
					Set <Module> cluster = entry.getValue();
					double distance = computedistance(module,cluster);
					if(distance < cloesetdistance){
						cloesetdistance = distance;
						mergeclusterId = clusterId;
					}
				}
				// remove module from another cluster if not in current cluster
				//module_clusterIndex.get(key)
				if(module_clusterIndex.containsKey(module)){
					int oldclusterId = module_clusterIndex.get(module);
					if(oldclusterId!=mergeclusterId){
						// remove from old cluster
						Set<Module> oldcluser = clusterres.get(oldclusterId);
						oldcluser.remove(module);
						clusterres.put(oldclusterId, oldcluser);
						//System.out.println("alter module "+module.getId()+ "from "+ oldclusterId +" into cluster "+mergeclusterId);	
						// add into new cluster
						module_clusterIndex.put(module, mergeclusterId);
						Set<Module> newcluster = clusterres.get(mergeclusterId);
						newcluster.add(module);
						clusterres.put(mergeclusterId, newcluster);
						centerchange = true;
					}
				}else{
					centerchange = true;
					//System.out.println("add module "+module.getId()+" into cluster "+mergeclusterId);
					module_clusterIndex.put(module, mergeclusterId);
					Set<Module> newcluster = clusterres.get(mergeclusterId);
					newcluster.add(module);
					clusterres.put(mergeclusterId, newcluster);
				}
			}
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
	
	
	

	
	private double computedistance(Module module, Set<Module> cluster) {
		// compute the distance from module to a cluster
		double averagedisance = 0.0;
		
		int sourceindex = module.getIndex();
		double [] source_fix_vector = fixedrequired[sourceindex];
		double [] source_cond_vector = conditionalrequired[sourceindex];
		for(Module md:cluster){
			if(md==module)
				continue;
			int targetindex = md.getIndex();
			double [] target_fix_vector = fixedrequired[targetindex];
			double [] target_cond_vector = conditionalrequired[targetindex];
			double distance1 = Maths.jensenShannonDivergence(source_fix_vector, target_fix_vector);
			double distance2 = Maths.jensenShannonDivergence(source_cond_vector, target_cond_vector);
			averagedisance+=distance1;
			averagedisance+=distance2;
		}
		averagedisance = averagedisance/cluster.size();
		return averagedisance;
	}
	
	

	/**
	 * randomly select n modules in values
	 * but not in rootsModule;
	 * @param need
	 * @param rootsModule
	 * @param values
	 * @return
	 */
	private List<Module> randomselect(int n, Set<Module> rootsModule, Collection<Module> values) {
		assert n>0;
		Set<Module> values_copy = new HashSet<Module>();
		values_copy.addAll(values);
		values_copy.removeAll(rootsModule);
		assert n <= values_copy.size();
		List<Module> values_copy_list = new LinkedList<Module>(values_copy);
		Set<Integer> random_n_int = new HashSet<Integer>();
		while(random_n_int.size()<n){
			Random rm = new Random();
			int randomval = rm.nextInt(n);
			random_n_int.add(randomval);
		}
		List<Module> randomseeds = new LinkedList<Module>();
		for(int i:random_n_int){
			Module ranmd = values_copy_list.get(i);
			randomseeds.add(ranmd);
		}
		
		
		
		return randomseeds;
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
	
	
}