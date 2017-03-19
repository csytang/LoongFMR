package loongpluginfmrtool.toolbox.wca;
import java.net.URI;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import loongplugin.source.database.ApplicationObserver;
import loongplugin.source.database.ProgramDatabase;
import loongplugin.source.database.model.LElement;
import loongplugin.source.database.model.LRelation;
import loongplugin.utils.ASTCreator;
import edu.usc.softarch.arcade.clustering.ClusterGainStoppingCriterion;
import edu.usc.softarch.arcade.clustering.ConcernClusteringRunner;
import edu.usc.softarch.arcade.clustering.FastCluster;
import edu.usc.softarch.arcade.clustering.FastFeatureVectors;
import edu.usc.softarch.arcade.clustering.FeatureVectorMap;
import edu.usc.softarch.arcade.clustering.PreSelectedStoppingCriterion;
import edu.usc.softarch.arcade.clustering.SingleClusterStoppingCriterion;
import edu.usc.softarch.arcade.clustering.StoppingCriterion;
import edu.usc.softarch.arcade.clustering.WcaRunner;
import edu.usc.softarch.arcade.config.Config;
import edu.usc.softarch.arcade.config.Config.SimMeasure;
import edu.usc.softarch.arcade.config.Config.StoppingCriterionConfig;
import edu.usc.softarch.arcade.functiongraph.TypedEdgeGraph;
import edu.usc.softarch.arcade.topics.TopicModelExtractionMethod;
import edu.usc.softarch.arcade.util.StopWatch;
import loongpluginfmrtool.module.model.module.ModuleBuilder;
import loongpluginfmrtool.util.ClusteringResultRSFOutput;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class WeightedClusteringAlg {
	private int acluster;
	private IProject aProject;
	//private Map<CompilationUnit,File> compilationUnitFile = new HashMap<CompilationUnit,File>();
	public static FastFeatureVectors ffVecs = null;
	private Set<LElement> allelements;
	private Map<ICompilationUnit,Set<ICompilationUnit>>dependsrelationmapping = new HashMap<ICompilationUnit,Set<ICompilationUnit>>();
	private Set<LRelation>allcontainsrelations = new HashSet<LRelation>();
	private ApplicationObserver aAO;
	private ProgramDatabase aDB;
	private StoppingCriterionConfig stoppingCriterion;
	private Map<Integer,Set<String>>clusterresult = new HashMap<Integer,Set<String>>();
	private String sim;
	Set<ICompilationUnit>artifects = new HashSet<ICompilationUnit>();
	public WeightedClusteringAlg(ApplicationObserver pAO,StoppingCriterionConfig pstoppingCriterion,int pcluster){
	//	this.builder = pbuilder;
		this.acluster = pcluster;
		this.aProject = pAO.getInitializedProject();
		this.aAO = pAO;
		this.stoppingCriterion = pstoppingCriterion;
		this.aDB = aAO.getProgramDatabase();
		SimMeasure simmeasure = Config.getCurrSimMeasure();
		sim = "uem";
		switch(simmeasure){
		case uem:{
			sim = "uem";
			break;
		}
		case uemnm:{
			sim = "uemnm";
			break;
		}
		}
		// all relations that have dependences
		allcontainsrelations.add(LRelation.ACCESS_FIELD);
		allcontainsrelations.add(LRelation.ACCESS_LOCAL_VARIABLE);
		allcontainsrelations.add(LRelation.ACCESS_METHOD);
		allcontainsrelations.add(LRelation.ACCESS_TYPE);
		allcontainsrelations.add(LRelation.ACCESSES);
		allcontainsrelations.add(LRelation.EXTENDS_TYPE);
		allcontainsrelations.add(LRelation.IMPLEMENTS_METHOD);
		allcontainsrelations.add(LRelation.OVERRIDES_METHOD);
		allcontainsrelations.add(LRelation.REFERENCES);
		allcontainsrelations.add(LRelation.REQUIRES);
		
		
		run();
				
		
	}
	protected void run(){
		
		IWorkspace workspace = ResourcesPlugin.getWorkspace();  
		String projectPath = workspace.getRoot().getLocation().toOSString();
		
		allelements = aDB.getAllElements();
		
		TypedEdgeGraph typedEdgeGraph = new TypedEdgeGraph();
		
		/*
		
		
		// build the relation mapping
		for(LElement element:allelements){
			CompilationUnit sourceunit = element.getCompilationUnit();
			if(!compilationUnitFile.containsKey(sourceunit)){
				ICompilationUnit iunit = (ICompilationUnit) sourceunit.getJavaElement();
				IPath path = iunit.getPath().makeAbsolute();
				String fullsourcePath = projectPath+path.toOSString();
				File absolutefile = new File(fullsourcePath);
				compilationUnitFile.put(sourceunit, absolutefile);
			}
			for(LRelation relation:allcontainsrelations){
					Set<LElement> alltargetelement = aAO.getRange(element, relation);
					if(alltargetelement!=null){
						for(LElement target:alltargetelement){
							CompilationUnit targetunit = target.getCompilationUnit();
							if(!compilationUnitFile.containsKey(targetunit)){
								ICompilationUnit iunit = (ICompilationUnit) targetunit.getJavaElement();
								IPath path = iunit.getPath().makeAbsolute();
								String fullsourcePath = projectPath+path.toOSString();
								File absolutefile = new File(fullsourcePath);
								compilationUnitFile.put(targetunit, absolutefile);
							}
								if(dependsrelationmapping.containsKey(sourceunit)){
									Set<CompilationUnit>targestunits = dependsrelationmapping.get(sourceunit);
									targestunits.add(targetunit);
									dependsrelationmapping.put(sourceunit, targestunits);
								}else{
									Set<CompilationUnit>targestunits = new HashSet<CompilationUnit>();
									targestunits.add(targetunit);
									dependsrelationmapping.put(sourceunit, targestunits);
								}
							
						}
				}
				
			}
		}
		
		// put the dependence mapping into type edge graph
		for(Map.Entry<CompilationUnit, Set<CompilationUnit>>entry:dependsrelationmapping.entrySet()){
			CompilationUnit sourceunit = entry.getKey();
			String sourcename = getFullName(sourceunit);
			Set<CompilationUnit> targetunits = entry.getValue();
			for(CompilationUnit unit:targetunits){
				typedEdgeGraph.addEdge("depends", sourcename, getFullName(unit));
			}
		}
		*/
		
		
		List<ICompilationUnit> ICompilUnits = this.aAO.getICompilationUnits();
		for(ICompilationUnit iunit:ICompilUnits){
			//String artName =  getFullNameICompilationUnit(iunit);
			artifects.add(iunit);
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
		
		
		FeatureVectorMap fvMap = new FeatureVectorMap(typedEdgeGraph);
		ffVecs = fvMap.convertToFastFeatureVectors();
		
		
		// Run WCA algortihm
		WcaRunner.setFastFeatureVectors(ffVecs);
		
		StopWatch stopwatch = new StopWatch();

		stopwatch.start();
		
		if(this.stoppingCriterion.equals(Config.StoppingCriterionConfig.preselected)){
			StoppingCriterion stopCriterion = new PreSelectedStoppingCriterion();
			WcaRunner.computeClustersWithPQAndWCA(stopCriterion,acluster);
			
		}
		
		else if(this.stoppingCriterion.equals(Config.StoppingCriterionConfig.clustergain)){
			StoppingCriterion singleClusterStopCriterion = new SingleClusterStoppingCriterion();
			WcaRunner.computeClustersWithPQAndWCA(singleClusterStopCriterion,acluster);
			StoppingCriterion clusterGainStopCriterion = new ClusterGainStoppingCriterion();
			WcaRunner.computeClustersWithPQAndWCA(clusterGainStopCriterion,acluster);
		}
		
		stopwatch.stop();

		// Statistics
		String timeInSecsToComputeClusters = "Time in seconds to compute clusters: "
				+ stopwatch.getElapsedTimeSecs();
		String timeInMilliSecondsToComputeClusters = "Time in milliseconds to compute clusters: "
				+ stopwatch.getElapsedTime();
		System.out.println(timeInSecsToComputeClusters);
		System.out.println(timeInSecsToComputeClusters);
		System.out.println(timeInMilliSecondsToComputeClusters);
		System.out.println(timeInMilliSecondsToComputeClusters);
		System.out.println("Finish clustering");
		
		
		ArrayList<FastCluster> fasterClusters = WcaRunner.getFastClusters();
		
		int index = 0;
		for(FastCluster fc:fasterClusters){
			String fullcontents = fc.getName();
			String[]allsubs = fullcontents.split(",");
			Set<String> allsubsset = new HashSet<String>(Arrays.asList(allsubs));
			clusterresult.put(index, allsubsset);
			index++;
		}
		
		// Output result
		ClusteringResultRSFOutput.ModuledStrOutput(clusterresult, "wca_"+this.sim, aProject);
		System.out.println("Finish output");
	}
	
	
	public static IFile getResource(ICompilationUnit cu) {
		IPath path = cu.getPath();
		return ResourcesPlugin.getWorkspace().getRoot().getFile(path);
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
		//String sourcefullName = sourcepackageName+File.pathSeparator+fulltypeName;
		
	}
}
