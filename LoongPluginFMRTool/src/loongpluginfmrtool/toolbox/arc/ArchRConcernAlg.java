package loongpluginfmrtool.toolbox.arc;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;

import edu.usc.softarch.arcade.clustering.ConcernClusteringRunner;
import edu.usc.softarch.arcade.clustering.FastCluster;
import edu.usc.softarch.arcade.clustering.FastFeatureVectors;
import edu.usc.softarch.arcade.clustering.FeatureVectorMap;
import edu.usc.softarch.arcade.clustering.PreSelectedStoppingCriterion;
import edu.usc.softarch.arcade.clustering.WcaRunner;
import edu.usc.softarch.arcade.config.Config;
import edu.usc.softarch.arcade.config.Config.SimMeasure;
import edu.usc.softarch.arcade.config.Config.StoppingCriterionConfig;
import edu.usc.softarch.arcade.functiongraph.TypedEdgeGraph;
import edu.usc.softarch.arcade.topics.TopicModelExtractionMethod;
import edu.usc.softarch.arcade.util.StopWatch;
import loongplugin.source.database.ApplicationObserver;
import loongplugin.source.database.ProgramDatabase;
import loongplugin.source.database.model.LElement;
import loongplugin.source.database.model.LRelation;
import loongpluginfmrtool.module.model.module.ModuleBuilder;
import loongpluginfmrtool.util.ClusteringResultRSFOutput;


public class ArchRConcernAlg {

	private ApplicationObserver aAO;
	private Set<LElement> allelements;
	private Map<ICompilationUnit,Set<ICompilationUnit>>dependsrelationmapping = new HashMap<ICompilationUnit,Set<ICompilationUnit>>();
	private int numTopics = 0;
	private ProgramDatabase aDB;
	private Set<File>allsourcefiles = new HashSet<File>();
//	private Map<CompilationUnit,File> compilationUnitFile = new HashMap<CompilationUnit,File>();
	private Set<LRelation>allcontainsrelations = new HashSet<LRelation>();
	public static FastFeatureVectors ffVecs = null;
	private String projectPath;
	private String sourcecodeDir;
	private String topicModelFilename;
	private String docTopicsFilename;
	private String topWordsFilename;
	private String arcClustersFilename;
	private IProject aProject;
	private Shell shell;
	private boolean configrationset = false;
	private String relativeprojectcfgpath;
	private IFile cfgfile;
	private Map<Integer,Set<String>>clusterresult = new HashMap<Integer,Set<String>>();
	
	/**
	 * 
	 * @param pAO application obersever by default
	 * @param pbuilder mobuild builder
	 * @param pstoppingCriterion the stopping criterion for architecture recovery with concern algorithm
	 * @param pcluster # of clusters
	 */
	public ArchRConcernAlg(ApplicationObserver pAO){
		this.aAO = pAO;
		this.aDB = this.aAO.getProgramDatabase();
		this.aProject = this.aAO.getInitializedProject();
		this.relativeprojectcfgpath = this.aProject.getName()+".cfg";
		preconfig();
		configuration();
		setConfigurationFile();
		run();
	}
	
	protected void preconfig(){
		
		IWorkspace workspace = ResourcesPlugin.getWorkspace();  
		
		projectPath = workspace.getRoot().getLocation().toOSString()+File.separatorChar+aProject.getName().toString();
		sourcecodeDir = projectPath;
		
		assert isValidFilePath(sourcecodeDir);
		
		allelements = aDB.getAllElements();
		// creating the basic dependency information in the map mode
		TypedEdgeGraph typedEdgeGraph = new TypedEdgeGraph();
		Set<ICompilationUnit>artifects = new HashSet<ICompilationUnit>();
		
		// build the relation mapping
		List<ICompilationUnit> ICompilUnits = this.aAO.getICompilationUnits();
		for(ICompilationUnit iunit:ICompilUnits){
			//String artName =  getFullNameICompilationUnit(iunit);
			artifects.add(iunit);
			IPath path = iunit.getPath().makeAbsolute();
			String fullsourcePath = projectPath+path.toOSString();
			File absolutefile = new File(fullsourcePath);
			if(!allsourcefiles.contains(absolutefile)){
				allsourcefiles.add(absolutefile);
			}
			
		}

		for(LElement element:allelements){
			ICompilationUnit isourceunit = element.getICompilationUnit();
			if(artifects.contains(isourceunit)){
				for(LRelation relation:allcontainsrelations){
					Set<LElement> alltargetelement = aAO.getRange(element, relation);
					if(alltargetelement!=null){
						for(LElement target:alltargetelement){
							ICompilationUnit targetunit = target.getICompilationUnit();
							if(artifects.contains(targetunit)){
								if(dependsrelationmapping.containsKey(isourceunit)){
									Set<ICompilationUnit>targestunits = dependsrelationmapping.get(isourceunit);
									targestunits.add(targetunit);
									dependsrelationmapping.put(isourceunit, targestunits);
								}else{
									Set<ICompilationUnit>targestunits = new HashSet<ICompilationUnit>();
									targestunits.add(targetunit);
									dependsrelationmapping.put(isourceunit, targestunits);
								}
							}
						}
					}
				}
			}
		}
	
		for(Map.Entry<ICompilationUnit, Set<ICompilationUnit>>entry:dependsrelationmapping.entrySet()){
			ICompilationUnit sourceunit = entry.getKey();
			String sourcefullName = getFullNameICompilationUnit(sourceunit);
			
			Set<ICompilationUnit> targetunits = entry.getValue();
			for(ICompilationUnit target:targetunits){
				
				String targetfullName = getFullNameICompilationUnit(target);
				
				//String artdepString = "depends "+sourcefullName+" "+targetfullName+"\n";
				typedEdgeGraph.addEdge("depends", sourcefullName, targetfullName);
				
			}
			
		}
		
		// build the topic based vectors
		
		FeatureVectorMap fvMap = new FeatureVectorMap(typedEdgeGraph);
		ffVecs = fvMap.convertToFastFeatureVectors();
		
		numTopics = (int)(((double)allsourcefiles.size())* 0.18);
		
		// write the archifect 
		int numClusters = (int) ((double) allsourcefiles.size() * .20); // number of clusters to obtain is based
								// on the number of entities
		// all folders should be scanned and output redirected
		topicModelFilename = projectPath + File.separatorChar + numTopics + "_topics.mallet";
		docTopicsFilename = projectPath + File.separatorChar + numTopics + "-doc-topics.txt";
		topWordsFilename = projectPath + File.separatorChar + numTopics + "_top_words_per_topic.txt";
		arcClustersFilename =projectPath + File.separatorChar + numTopics + "_topics_"+numClusters + "_arc_clusters.rsf";
	}
	
	private void configuration(){
		WizardDialog dialog = new WizardDialog(shell,new ARCConfigurationWizard(this,aProject,aAO,shell,topicModelFilename,docTopicsFilename,arcClustersFilename,numTopics,allsourcefiles.size()));
		dialog.create();
		dialog.open();
	}
	
	public void setConfigurationFile(){
		cfgfile = aProject.getFile(relativeprojectcfgpath);
		String fulllocationpath = cfgfile.getLocation().toOSString();
		Config.initConfigFromFile(fulllocationpath);
		configrationset = true;
	}
	
	private void run(){
		if(configrationset){
			ConcernClusteringRunner runner = new ConcernClusteringRunner(ffVecs,TopicModelExtractionMethod.MALLET_API, sourcecodeDir,sourcecodeDir, numTopics, topicModelFilename, docTopicsFilename, topWordsFilename);	
			// have to set some Config settings before executing the runner
			Config.stoppingCriterion = StoppingCriterionConfig.preselected;
			Config.setCurrSimMeasure(SimMeasure.js);
			StopWatch stopwatch = new StopWatch();

			stopwatch.start();
			
			runner.computeClustersWithConcernsAndFastClusters(new PreSelectedStoppingCriterion());
			
			stopwatch.stop();

			// Statistics
			String timeInSecsToComputeClusters = "Time in seconds to compute clusters: "
					+ stopwatch.getElapsedTimeSecs();
			String timeInMilliSecondsToComputeClusters = "Time in milliseconds to compute clusters: "
					+ stopwatch.getElapsedTime();
			System.out.println(timeInSecsToComputeClusters);
			System.out.println(timeInMilliSecondsToComputeClusters);
			
			
			ArrayList<FastCluster> fasterClusters = runner.getFastClusters();
			
			int index = 0;
			for(FastCluster fc:fasterClusters){
				String fullcontents = fc.getName();
				String[]allsubs = fullcontents.split(",");
				Set<String> allsubsset = new HashSet<String>(Arrays.asList(allsubs));
				clusterresult.put(index, allsubsset);
				index++;
			}
			ClusteringResultRSFOutput.ModuledStrOutput(clusterresult, "arc", aProject);
			
			System.out.println("Finish ARC Clustering...");
		}
	}
	private boolean isValidFilePath(String filePath) {
		// TODO Auto-generated method stub
		File file = new File(filePath);
		return file.exists();
	}

	public String getFullName(CompilationUnit unit){
		String packageName = unit.getPackage().getName().toString();
		List types = unit.types();    
		TypeDeclaration typeDec = (TypeDeclaration) types.get(0); //typeDec is the class  
		String fullName = packageName+"."+typeDec.getName().toString();
		
		return fullName;
	}
	
	
	private String getFullNameICompilationUnit(ICompilationUnit unit){
		//String sourcepackageName = unit.getPackageDeclarations().toString();
		String fulltypeName = "";
		try {
			fulltypeName = unit.getTypes()[0].getFullyQualifiedName();
			
		} catch (JavaModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return fulltypeName;
		
	}
	
}
