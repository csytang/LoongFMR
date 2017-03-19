package loongpluginfmrtool.util;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import loongplugin.color.coloredfile.CLRAnnotatedSourceFile;
import loongplugin.source.database.ApplicationObserver;
import loongplugin.source.database.ApplicationObserverException;
import loongplugin.source.database.ProgramDatabase;
import loongplugin.source.database.model.LElement;
import loongplugin.source.database.model.LICategories;
import loongplugin.source.database.model.LRelation;
import loongplugin.utils.ASTCreator;

public class ACDCRsfBuilder {
	private ApplicationObserver aAO;
	private IProject aProject;
	private ProgramDatabase aDB;
	private IFile file;
	private Set<LElement> allelements;
	private Set<LRelation>allcontainsrelations = new HashSet<LRelation>();
	private boolean debug = true;
	private List<String>contentRecordList = new LinkedList<String>();
	private String projectPath = "";
	private Map<ICompilationUnit,Set<ICompilationUnit>>dependsrelationmapping = new HashMap<ICompilationUnit,Set<ICompilationUnit>>();
	//private Map<ICompilationUnit,CompilationUnit>icompToCompUnit = new HashMap<ICompilationUnit,CompilationUnit>();
	
	public ACDCRsfBuilder(ApplicationObserver pAO,IProject pProject){
		aAO = pAO;
		aProject = pProject;
		IWorkspace workspace = ResourcesPlugin.getWorkspace();  
		projectPath = workspace.getRoot().getLocation().toOSString();
		
		aDB = pAO.getProgramDatabase();
		file = pProject.getFile("data"+File.separatorChar+aProject.getName()+"_deps.rsf");		
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
		allelements = aDB.getAllElements();
		
	}
	
	public void build(IProgressMonitor monitor){
		// if the file exist, delete it
		if(file.exists()){
			//delete the old file
			try {
				file.delete(true, monitor);
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
		
		Set<ICompilationUnit>artifects = new HashSet<ICompilationUnit>();
		
		IJavaProject aJavaProject = JavaCore.create(aProject);
		
		List<ICompilationUnit> ICompilUnits = this.aAO.getICompilationUnits();
		if( monitor != null ){ 
			monitor.beginTask( "Extracting ACDC RSF facts", ICompilUnits.size()*2+1);
    	}
		
		for(ICompilationUnit iunit:ICompilUnits){
			/*
			String artType = "Module";
			
			String artName =  getFullNameICompilationUnit(iunit);
			if(monitor!=null){
				monitor.subTask("Process CompilationUnit: "+artName);
			}
			String artString = "$INSTANCE\t"+artName+"\t"+artType+"\n";
			this.contentRecordList.add(artString);*/
			
			artifects.add(iunit);
			if( monitor != null ) 
				monitor.worked(1);
			
		}

		for(LElement element:allelements){

			if(monitor!=null){
				monitor.subTask("Process CompilationUnit Relations:"+element.getId());
			}
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
		monitor.worked(1);
		
		for(Map.Entry<ICompilationUnit, Set<ICompilationUnit>>entry:dependsrelationmapping.entrySet()){
			ICompilationUnit sourceunit = entry.getKey();
			String sourcefullName = getFullNameICompilationUnit(sourceunit);
			if(monitor!=null){
				monitor.subTask("Writing Relation of CompilationUnit: "+sourcefullName);
			}
			Set<ICompilationUnit> targetunits = entry.getValue();
			for(ICompilationUnit target:targetunits){
				
				String targetfullName = getFullNameICompilationUnit(target);
				
				String artdepString = "depends "+sourcefullName+" "+targetfullName+"\n";
				
				this.contentRecordList.add(artdepString);
				
			}
			monitor.worked(1);
		}
		
		
		
		IFolder filedir = aProject.getFolder("data");
		
		if(file.exists()){
			return;
		}
		if(!filedir.exists()){
			try {
				filedir.create(false, true, monitor);
				file = aProject.getFile("data"+File.separatorChar+aProject.getName()+"_ground_truth_recovery.rsf");
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		IPath path = file.getFullPath();
		String fullpathStr = projectPath;
		if(fullpathStr.endsWith(File.separatorChar+"")){
			fullpathStr = fullpathStr.substring(0, fullpathStr.length()-1);
		}
		if(path.toOSString().startsWith(File.separatorChar+"")){
			fullpathStr = fullpathStr+path.toOSString();
		}else{
			fullpathStr = fullpathStr+File.separatorChar+path.toOSString();
		}
		File targetfile = new File(fullpathStr);
		try {
			FileWriter filewriter = new FileWriter(targetfile);
			write(this.contentRecordList,filewriter);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        monitor.worked(1);
		monitor.done();
	}
	
	private String getFullSourceName(CompilationUnit unit){
		String sourcepackageName = unit.getPackage().getName().toString();
		List sourcetypes = unit.types();    
		AbstractTypeDeclaration sourcetypeDec = (AbstractTypeDeclaration) sourcetypes.get(0); //typeDec is the class  
		String sourcefullName = sourcepackageName+File.separatorChar+sourcetypeDec.getName().toString();
		return sourcefullName;
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
	

	private static void write(List<String> records, Writer writer) throws IOException {
	    long start = System.currentTimeMillis();
	    for (String record: records) {
	        writer.write(record);
	    }
	    writer.flush();
	    writer.close();
	    long end = System.currentTimeMillis();
	    System.out.println((end - start) / 1000f + " seconds");
	}
}
