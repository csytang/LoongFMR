package loongpluginfmrtool.toolbox.limbo;

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

import edu.usc.softarch.arcade.clustering.ClusterGainStoppingCriterion;
import edu.usc.softarch.arcade.clustering.FastCluster;
import edu.usc.softarch.arcade.clustering.FastFeatureVectors;
import edu.usc.softarch.arcade.clustering.FeatureVectorMap;
import edu.usc.softarch.arcade.clustering.LimboRunner;
import edu.usc.softarch.arcade.clustering.PreSelectedStoppingCriterion;
import edu.usc.softarch.arcade.clustering.SingleClusterStoppingCriterion;
import edu.usc.softarch.arcade.clustering.StoppingCriterion;
import edu.usc.softarch.arcade.clustering.WcaRunner;
import edu.usc.softarch.arcade.config.Config;
import edu.usc.softarch.arcade.config.Config.SimMeasure;
import edu.usc.softarch.arcade.config.Config.StoppingCriterionConfig;
import edu.usc.softarch.arcade.functiongraph.TypedEdgeGraph;
import edu.usc.softarch.arcade.util.StopWatch;
import loongplugin.source.database.ApplicationObserver;
import loongplugin.source.database.ProgramDatabase;
import loongplugin.source.database.model.LElement;
import loongplugin.source.database.model.LRelation;
import loongpluginfmrtool.util.ClusteringResultRSFOutput;

public class LIMBOClusteringAlg {
	private int acluster;
	private IProject aProject;
	//private Map<CompilationUnit,File> compilationUnitFile = new HashMap<CompilationUnit,File>();
	public static FastFeatureVectors ffVecs = null;
	private Set<LElement> allelements;
	private Map<ICompilationUnit,Set<ICompilationUnit>>dependsrelationmapping = new HashMap<ICompilationUnit,Set<ICompilationUnit>>();
	private Set<LRelation>allcontainsrelations = new HashSet<LRelation>();
	private ApplicationObserver aAO;
	private ProgramDatabase aDB;
	private PreSelectedStoppingCriterion stoppingCriterion;
	private Map<Integer,Set<String>>clusterresult = new HashMap<Integer,Set<String>>();
	
	Set<ICompilationUnit>artifects = new HashSet<ICompilationUnit>();
	public LIMBOClusteringAlg(ApplicationObserver pAO,int pcluster){
	//	this.builder = pbuilder;
		this.acluster = pcluster;
		this.aProject = pAO.getInitializedProject();
		this.aAO = pAO;
		this.stoppingCriterion = new PreSelectedStoppingCriterion();
		this.aDB = aAO.getProgramDatabase();
		
		
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
		
		System.out.println("Run LIMBO clustering");
		FeatureVectorMap fvMap = new FeatureVectorMap(typedEdgeGraph);
		ffVecs = fvMap.convertToFastFeatureVectors();
		
		Config.setNumClusters(acluster);
		
		// Run WCA algortihm
		LimboRunner.setFastFeatureVectors(ffVecs);
		
		StopWatch stopwatch = new StopWatch();
		
		stopwatch.start();
		System.out.println("Run LIMBO start");
		LimboRunner.computeClusters(new PreSelectedStoppingCriterion());
		System.out.println("Run LIMBO end");
		stopwatch.stop();

		// Statistics
		String timeInSecsToComputeClusters = "Time in seconds to compute clusters: "
				+ stopwatch.getElapsedTimeSecs();
		String timeInMilliSecondsToComputeClusters = "Time in milliseconds to compute clusters: "
				+ stopwatch.getElapsedTime();
		System.out.println(timeInSecsToComputeClusters);
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
		ClusteringResultRSFOutput.ModuledStrOutput(clusterresult, "limbo", aProject);
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

