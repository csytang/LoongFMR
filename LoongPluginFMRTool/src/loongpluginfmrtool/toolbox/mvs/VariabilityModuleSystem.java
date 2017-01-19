package loongpluginfmrtool.toolbox.mvs;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import edu.usc.softarch.arcade.topics.DocTopicItem;
import edu.usc.softarch.arcade.topics.TopicUtil;
import edu.usc.softarch.arcade.util.StopWatch;
import loongplugin.source.database.model.LElement;
import loongplugin.source.database.model.LFlyweightElementFactory;
import loongpluginfmrtool.module.model.configuration.ConfigurationOption;
import loongpluginfmrtool.module.model.hierarchicalstructure.HierarchicalBuilder;
import loongpluginfmrtool.module.model.hierarchicalstructure.HierarchicalNeighbor;
import loongpluginfmrtool.module.model.module.Module;
import loongpluginfmrtool.module.model.module.ModuleBuilder;
import loongpluginfmrtool.util.ClusteringResultRSFOutput;
import loongpluginfmrtool.util.MathUtil;
import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.dom.CompilationUnit;

public class VariabilityModuleSystem {
	
	private ModuleBuilder builder;
	private int optional_cluster;
	private int common_cluster;
	private Map<Integer,Set<Module>> commonclusterres;
	private Map<Integer,Set<Module>> optionalclusterres;
	private Map<Integer, Module> indexToModule;
	private Map<Module,Integer> module_clusterIndex = new HashMap<Module,Integer>();
	private Module mainModule;
	private LElement mainmethod;
	private int modulesize;
	private IProject aProject;
	private Corpus corpus;
	private LFlyweightElementFactory aLElementFactory;
	private Set<Module> commonmodules = new HashSet<Module>();
	private Set<Module> optionalmodules = new HashSet<Module>();
	private Map<Module,DocTopicItem> dtItemMap;
	private boolean debug = true;
	private HierarchicalBuilder hbuilder;
	private Map<Module,HierarchicalNeighbor> sourcetoNeighbor;
	private double[][] typecheckref;
	private double[][] methodref;
	/**
	 * jensenShannonDivergence get distance
	 * 
	 * 
	 * 
	 */
	
	
	
	public VariabilityModuleSystem(ModuleBuilder pbuilder,int pcommoncluster,int poptionalcluster,Module entrancemodule,LElement method){
		this.common_cluster = pcommoncluster;
		this.optional_cluster = poptionalcluster;
		this.builder = pbuilder;
		this.indexToModule = builder.getIndexToModule();
		
		this.optionalclusterres = new HashMap<Integer,Set<Module>>();
		this.commonclusterres = new HashMap<Integer,Set<Module>>();
		
		this.aProject = this.builder.getsubjectProject();
		this.aLElementFactory = builder.getLElementFactory();
		this.mainmethod = method;
	
		// build module topics
		buildModuleTopics();
		
		// find common module;
		findCommonModule();
		if(debug)
			printCommonModules();
		
		// create the profile for optional module set;
		createProfileOptional();
		
		// initial the reference set
		initialRefernces();
		normalizeReference();
		
		
		// initial index to cluster
		clusterIndexInitialize();
		
		// do the clustering task
		performClustering();
		
		//ClusteringResultRSFOutput output = new ClusteringResultRSFOutput(clusterres,"modulevariabilitysystem",builder.gettargetProject());
		ClusteringResultRSFOutput.ModuledRSFOutput(commonclusterres,optionalclusterres,"vms",builder.getsubjectProject());
	}
	

	

	private void clusterIndexInitialize() {
		// run through the full list of index to module
		for(Map.Entry<Integer, Module>entry:indexToModule.entrySet()){
			int id = entry.getKey();
			Module module = entry.getValue();
			Set<Module> moduleset = new HashSet<Module>();
			moduleset.add(module);
			
			//module_clusterIndex
			module_clusterIndex.put(module, id);
			
			// clusterres
			if(this.commonmodules.contains(module)){
				// add to the common set
				this.commonclusterres.put(id, moduleset);
				
			}else if(this.optionalmodules.contains(module)){
				// add to the optional set
				this.optionalclusterres.put(id, moduleset);
				
			}else{
				try {
					throw new Exception("a module has not been considerred as optional or a common");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		
		}
		
	}




	private void initialRefernces() {
		
		System.out.println("Start Initialize");
		this.hbuilder = new HierarchicalBuilder(builder,this.aLElementFactory);
		this.sourcetoNeighbor = hbuilder.getModuleToNeighbor();
		
		modulesize = indexToModule.size();
		typecheckref = new double[modulesize][modulesize];
		methodref = new double[modulesize][modulesize];
		
		for(int i =0;i < modulesize;i++){
			for(int j = 0;j < modulesize;j++){
				if(i==j){
					typecheckref[i][j] = 1;
					methodref[i][j] = 0;
				}else{
					typecheckref[i][j] = 0;
					methodref[i][j] = 0;
				}
			}
		}
	
		
		
		for(Map.Entry<Integer,Module>entry:indexToModule.entrySet()){
			// reset
			
			// get all modules in the iteration
			Module md = entry.getValue();
			
			
			// run these modules
			
				
			int md_index = md.getIndex();
			// get the hierarchical neighbor of this module
			HierarchicalNeighbor md_neigbhor = this.sourcetoNeighbor.get(md);
				
			// get all type reference modules
			Set<Module> md_typemodulerequired = md_neigbhor.getTypeModuleRequired();
				
			
			for(Module md_type:md_typemodulerequired){
				if(md!=md_type){
					int md_fix_index = md_type.getIndex();
					typecheckref[md_index][md_fix_index]=+1;
				}
			}
				
			Set<Module> md_methodrequired = md_neigbhor.getMethodRefence();
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
			
		}
		System.out.println("Finish Initialize");
	}



	private void printCommonModules() {
		System.out.println("------Following modules are common modules--------");
		for(Module md:commonmodules){
			System.out.println(md.getDisplayName());
		}
		System.out.println("-------------------------------------------------");
		
	}


	/**
	 * normalize the reference 
	 */
	private void normalizeReference() {
		/**
		 * private double[][] fixedrequired;
			private double[][] conditionalrequired;
		 */
		double[][] fixedrequirednormalized = new double[typecheckref.length][typecheckref.length];
		double[][] methodrefnormalized = new double[typecheckref.length][typecheckref.length];
		for(int i = 0;i < typecheckref.length;i++){
			double maxfixline = getMax(typecheckref[i]);
			double minfixline = getMin(typecheckref[i]);
			
			double maxmethodrefline = getMax(methodref[i]);
			double minmethodrefline = getMin(methodref[i]);
			
			for(int j = 0; j < typecheckref.length;j++){
				if(maxfixline==minfixline){
					if(maxfixline>1){
						fixedrequirednormalized[i][j] = 1;
					}else{
						fixedrequirednormalized[i][j] = 0;
					}
					
				}else{
					fixedrequirednormalized[i][j] = (typecheckref[i][j]-minfixline)/(maxfixline-minfixline);
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
		
		typecheckref = fixedrequirednormalized;
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
	  
	private void findCommonModule(){
		Queue<LElement> methodtoProcess = new LinkedList<LElement>();
		methodtoProcess.add(mainmethod);
		
		assert mainmethod!=null;
		
		// reserve a list for visited lelement
		Set<LElement> visisted_lelement = new HashSet<LElement>();
		
		while(!methodtoProcess.isEmpty()){
			LElement head = methodtoProcess.poll();
			visisted_lelement.add(head);
			CompilationUnit compilunit_head = head.getCompilationUnit();
			LElement compilunit_element = aLElementFactory.getElement(compilunit_head);
			Module head_module = builder.getModuleByLElement(compilunit_element);
			
			// add the head module into common modules
			if(!this.commonmodules.contains(head_module)){
				this.commonmodules.add(head_module);
			}
			assert head_module!=null;
			Map<LElement,Set<LElement>> hardmethodinvocation = head_module.getHardMethodInvocation();
			Map<LElement, Set<Module>> hardtyperef = head_module.getHardReferencebyMethod();
			if(hardtyperef.containsKey(head)){
				Set<Module> requiredmodules = hardtyperef.get(head);
				if(requiredmodules!=null){
					this.commonmodules.addAll(requiredmodules);
				}
			}
			if(hardmethodinvocation.containsKey(head)){
				Set<LElement> methodinvoked = hardmethodinvocation.get(head);
				for(LElement method:methodinvoked){
					if(!visisted_lelement.contains(method))
						methodtoProcess.add(method);
				}
			}
		}
		
		// print a statistic 
		System.out.println(this.commonmodules.size()+"\t common modules are found from\t"+this.indexToModule.size()+"\t modules[persentage:"+100*(double)this.commonmodules.size()/this.indexToModule.size()+"]");
	}
	

	

	/**
	 * build the topic modelling
	 */
	private void buildModuleTopics() {
		corpus = new Corpus(this.indexToModule.size(),this.indexToModule,this.aProject);
		
		dtItemMap = corpus.getModuleToDocTopic();
	}

	
	public void createProfileOptional(){
		// create set for optional set
		//optionalmodules;
		// A- B
		Set<Module> allmodules = new HashSet<Module>(indexToModule.values());
		for(Module md:allmodules){
			if(!this.commonmodules.contains(md)){
				optionalmodules.add(md);
			}
		}
		
		
	}
	
	public void performClustering(){
		
		
		StopWatch stopwatch = new StopWatch();
		
		stopwatch.start();
			
////////******************************RUN****************************////////
		/**
		 * Do the clustering on common set
		 */
		
		while(this.commonclusterres.size()>this.common_cluster){
			// find the one with max similarty
			double maxsim = Double.MIN_VALUE;
			int sourceIdToMerge = -1;
			int targetIdToMerge = -1;
			for(Map.Entry<Integer, Set<Module>>entry:this.commonclusterres.entrySet()){
				//Map<Integer,Set<Module>>
				int source_commonindex = entry.getKey();
				Set<Module> source_commonmodules = entry.getValue();
				for(Map.Entry<Integer, Set<Module>>entry_target:this.commonclusterres.entrySet()){
					int target_commonindex = entry_target.getKey();
					if(source_commonindex==target_commonindex){
						continue;
					}
					Set<Module> target_commonmodules = entry.getValue();
					double simiarity = computesimiarity(source_commonmodules,target_commonmodules);
					if(simiarity > maxsim){
						sourceIdToMerge = source_commonindex;
						targetIdToMerge = target_commonindex;
						maxsim = simiarity;
					}
				}
			}
			if(sourceIdToMerge!=-1 && targetIdToMerge!=-1){
				mergecommoncluster(sourceIdToMerge,targetIdToMerge);
			}else{
				try {
					throw new Exception("cannot find module set to merge[in common set]");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}
		
		/**
		 * Do the clustering on optional set
		 */
		
		while(this.optionalclusterres.size()>this.optional_cluster){
			// find the one with max similarty
			double maxsim = Double.MIN_VALUE;
			int sourceIdToMerge = -1;
			int targetIdToMerge = -1;
			for(Map.Entry<Integer, Set<Module>>entry:this.optionalclusterres.entrySet()){
					//Map<Integer,Set<Module>>
					int source_optionalindex = entry.getKey();
					Set<Module> source_optionalmodules = entry.getValue();
					for(Map.Entry<Integer, Set<Module>>entry_target:this.optionalclusterres.entrySet()){
							int target_optionalindex = entry_target.getKey();
							if(source_optionalindex==target_optionalindex){
								continue;
							}
							Set<Module> target_optionalmodules = entry.getValue();
							double simiarity = computesimiarity(source_optionalmodules,target_optionalmodules);
							if(simiarity > maxsim){
								sourceIdToMerge = source_optionalindex;
								targetIdToMerge = target_optionalindex;
								maxsim = simiarity;
							}
						}
					}
					if(sourceIdToMerge!=-1 && targetIdToMerge!=-1){
						mergeoptionalcluster(sourceIdToMerge,targetIdToMerge);
					}else{
						try {
							throw new Exception("cannot find module set to merge[in optional set]");
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
			
		}
		
		
		
		/**
		 * Merge and resolve dependency
		 */
		
		
		
		
//////////***************************************************************/////////
		
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
	
	
	
	private void mergecommoncluster(int merge_sourceclusterid, int merge_targetclusterid) {
		assert this.commonclusterres.containsKey(merge_sourceclusterid);
		assert this.commonclusterres.containsKey(merge_targetclusterid);
		Set<Module> clustersource = this.commonclusterres.get(merge_sourceclusterid);
		Set<Module> clustertarget = this.commonclusterres.get(merge_targetclusterid);
		for(Module md:clustertarget){
			clustersource.add(md);
			module_clusterIndex.put(md, merge_sourceclusterid);
		}
		this.commonclusterres.put(merge_sourceclusterid, clustersource);
		this.commonclusterres.remove(merge_targetclusterid);
		
	}
	
	private void mergeoptionalcluster(int merge_sourceclusterid, int merge_targetclusterid){
		assert this.optionalclusterres.containsKey(merge_sourceclusterid);
		assert this.optionalclusterres.containsKey(merge_targetclusterid);
		Set<Module> clustersource = this.optionalclusterres.get(merge_sourceclusterid);
		Set<Module> clustertarget = this.optionalclusterres.get(merge_targetclusterid);
		for(Module md:clustertarget){
			clustersource.add(md);
			module_clusterIndex.put(md, merge_sourceclusterid);
		}
		this.optionalclusterres.put(merge_sourceclusterid, clustersource);
		this.optionalclusterres.remove(merge_targetclusterid);
		
	}


	private double computesimiarity(Set<Module> cluster,Set<Module>othercluster){
		// complete-linkage agglomerative algorithm
		double averagedisance = 0.0;
		for(Module md:cluster){
			double mddistance = getAverage(md,othercluster);
			averagedisance += mddistance;
		}
		averagedisance = averagedisance/cluster.size();
		return averagedisance;
	}
	
	
	private double getAverage(Module module, Set<Module> cluster){
		
		double methodref_moduletocluster_count = 0;// targets(m) N f
		double methodref_clustertomodule_count = 0;// sources(m) N f
		double typeref_moduletocluster_count = 0;
		double typeref_clustertomodule_count = 0;
		double targetm = 0;// targets(m)
		double sourcesm = 0;// sources(m)
		double totaltopic_sim = 0;
		assert this.dtItemMap.containsKey(module);
		DocTopicItem moduledoc = this.dtItemMap.get(module);
		int sourceindex = module.getIndex();
		double [] source_typeref_vector = typecheckref[sourceindex];
		double [] source_method_vector = methodref[sourceindex];
		
		double overalldistance = 0.0;
		
		// compute targets(m)
		for(int i =0 ;i < source_typeref_vector.length;i++){
			targetm += source_method_vector[i];
		}
		
		// compute sources(m)
		/*
		 *这里要获取 m 那一列的和 
		 */
		for(int i = 0;i < methodref.length;i++){
			sourcesm += methodref[i][sourceindex];
		}
		
		
		
		for(Module md:cluster){
			if(md==module)
				continue;
			int targetindex = md.getIndex();
			assert this.dtItemMap.containsKey(md);
			DocTopicItem mddoc = this.dtItemMap.get(md);
			double [] target_typeref_vector = typecheckref[targetindex];
			double [] target_method_vector = methodref[targetindex];
			
			// topology analysis
			if(target_method_vector[sourceindex]!=0){
				methodref_clustertomodule_count+=target_method_vector[sourceindex];
			}
			if(source_method_vector[targetindex]!=0){// from m to cluster
				methodref_moduletocluster_count+=source_method_vector[targetindex];
			}
			
			// type reference
			if(target_typeref_vector[sourceindex]!=0){
				typeref_clustertomodule_count+=target_typeref_vector[sourceindex];
			}
			
			if(source_typeref_vector[targetindex]!=0){
				typeref_moduletocluster_count+=source_typeref_vector[targetindex];
			}
			
			// topic laten dirichlet allocation
			double distance_method = MathUtil.cosineSimilarity(source_method_vector, target_method_vector);
			totaltopic_sim+=distance_method;
			
		}
		
		// average totaltopic_sim
		double avg_totaltopic_sim = totaltopic_sim/cluster.size();
		// average  reference method
		double avg_refercen_sim = (1+methodref_moduletocluster_count)/targetm * methodref_clustertomodule_count/sourcesm;
		// average type reference sim
		double avg_typeref_sim = (typeref_clustertomodule_count+typeref_moduletocluster_count)/2*cluster.size();
		
		double partialresult = avg_totaltopic_sim+avg_refercen_sim-avg_totaltopic_sim*avg_refercen_sim;
		overalldistance = partialresult+avg_typeref_sim-partialresult*avg_typeref_sim;
		
		return overalldistance;
	}
	
	public void setselectedmodule(Module selected) {
		// TODO Auto-generated method stub
		mainModule = selected;
	}
	
	
}