package loongpluginfmrtool.groundTruth;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import loongplugin.color.ColorManager;
import loongplugin.color.coloredfile.ASTID;
import loongplugin.color.coloredfile.CLRAnnotatedSourceFile;
import loongplugin.color.coloredfile.CompilationUnitColorManager;
import loongplugin.feature.Feature;
import loongplugin.feature.FeatureModel;
import loongplugin.feature.FeatureModelManager;
import loongplugin.feature.guidsl.GuidslReader;
import loongplugin.feature.guidsl.UnsupportedModelException;
import loongplugin.modelcolor.ModelIDCLRFileReader;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

import loongplugin.color.coloredfile.IColoredJavaSourceFile;;

public class CreateGroundTruthJob extends WorkspaceJob {

	private final IProject project;
	private IFile modelm;
	private IFile modelcolors;
	private FeatureModel fmodel;
	private Map<IColoredJavaSourceFile,Set<Feature>>groundTruthMapping = new HashMap<IColoredJavaSourceFile,Set<Feature>>();
	
	public CreateGroundTruthJob(IProject sourceProject) {
		super("Creating ground truth file for this project:"+sourceProject.getName()+"_loong");
		// TODO Auto-generated constructor stub
		this.project = sourceProject;
		
	}
	@Override
	public IStatus runInWorkspace(IProgressMonitor monitor)
			throws CoreException {
		// TODO Auto-generated method stub
		IJavaProject sourceJavaProject = JavaCore.create(project);
		int compUnitCount = countJavaProject(sourceJavaProject);
		monitor.beginTask("Creating Loong Project", compUnitCount+2);

		modelm = project.getFile("model.m");
		if(modelm.exists()){
			//分析源代码 生产相应的内容
			//CIDE create feature model
			fmodel = new FeatureModel();
			GuidslReader gReader = new GuidslReader(fmodel);
			if(modelm!=null){
				try {
					gReader.parseInputStream(modelm.getContents());
				} catch (UnsupportedModelException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else{
				try {
					gReader.parseInputStream(modelm.getContents());
				} catch (UnsupportedModelException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}else{
			MessageDialog.openError(Display.getDefault().getActiveShell(), "Error", "Cannot convert to Loong, not model.m file found");
			monitor.done();
			return Status.OK_STATUS;
		}
		
		modelcolors = project.getFile("modelidclr.xml");
		
		ModelIDCLRFileReader dclrreader = new ModelIDCLRFileReader(modelcolors,FeatureModelManager.getInstance(project).getFeatureModel());
		
		obtainFeatureGroundMapping(monitor);
		
		writeGroundTruthFile(monitor);
		
		return Status.OK_STATUS;
	}

	private void obtainFeatureGroundMapping(IProgressMonitor monitor) {
		// TODO Auto-generated method stub
		processContainer(project,monitor);
	}
	
	private void processContainer(IContainer container,IProgressMonitor monitor){
		 try {
			   IResource[] members = container.members();
			   for (IResource member : members)
			   {
			      if (member instanceof IContainer) 
			      {
			    	  processContainer((IContainer)member,monitor);
			      }
			      else if (member instanceof IFile)
			      {	
			    	  IFile memberfile = (IFile)member;
			    	  
			    	  IPath relativefilePath = memberfile.getProjectRelativePath();
			    	  String fileName = relativefilePath.toOSString();
			    	 
					  if(fileName.endsWith(".clr")){
						 if(relativefilePath.segment(0).contains("bin"))
							  continue;
						 CLRAnnotatedSourceFile clrfile = (CLRAnnotatedSourceFile) CLRAnnotatedSourceFile.getColoredJavaSourceFile(memberfile);
						 CompilationUnitColorManager clrcolormanager = (CompilationUnitColorManager)clrfile.getColorManager();
						 HashMap<ASTID, Set<Feature>> node2features = clrcolormanager.getNode2Colors();
						 Set<Feature> features = new HashSet<Feature>();
						 for(Map.Entry<ASTID, Set<Feature>>entry:node2features.entrySet()){
							 features.addAll(entry.getValue());
						 }
						 groundTruthMapping.put(clrfile, features);
						 
					  }
			      }
			   }
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	
	
	private void writeGroundTruthFile(IProgressMonitor monitor) throws CoreException{
		
		IFile file = project.getFile("data"+File.separatorChar+project.getName()+"_ground_truth_recovery.rsf");
		IFolder filedir = project.getFolder("data");
		
		
		if(!filedir.exists()){
			filedir.create(false, true, monitor);
			file = project.getFile("data"+File.separatorChar+project.getName()+"_ground_truth_recovery.rsf");
		}
		
		if(file.exists()){
			//
			file.delete(true, monitor);
		}
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		
		for(Map.Entry<IColoredJavaSourceFile,Set<Feature>>entry:groundTruthMapping.entrySet()){
			IColoredJavaSourceFile colorfile = entry.getKey();
			Set<Feature> features = entry.getValue();
			CompilationUnit unit = colorfile.getAST();
			String packageName = unit.getPackage().getName().toString();
			List types = unit.types();    
			TypeDeclaration typeDec = (TypeDeclaration) types.get(0); //typeDec is the class  
			String fullName = packageName+"."+typeDec.getName().toString();
			for(Feature feature:features){
				String fullString = "";
				fullString = "contain";
				fullString += "\t";
				fullString += feature.getName()+".ss";
				fullString += "\t";
				fullString += fullName+".java";
				fullString += "\n";
				try {
					out.write(fullString.getBytes());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		
		InputStream inputsource = new ByteArrayInputStream(out.toByteArray());
        file.create(inputsource, EFS.NONE, null);
        try {
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
	}
	
	private int countJavaProject(IJavaProject sourceJavaProject)
			throws CoreException {
		int sum = 0;
		for (IPackageFragmentRoot root : sourceJavaProject
				.getPackageFragmentRoots()) {
			if (!root.exists())
				continue;
			if (root.getKind() == IPackageFragmentRoot.K_BINARY)
				continue;

			sum += countPackageFragementRoot(sourceJavaProject, root);
		}
		return sum;
	}
	
	private int countPackageFragementRoot(IJavaProject sourceJavaProject,
			IPackageFragmentRoot sourceRoot) throws CoreException {
		int sum = 0;
		for (IPackageFragment pkg : sourceJavaProject.getPackageFragments()) {
			if (pkg.getKind() == IPackageFragmentRoot.K_BINARY)
				continue;
			if (!sourceRoot.getPackageFragment(pkg.getElementName()).exists())
				continue;

			sum += countPackage(pkg);
		}
		return sum;
	}

	private int countPackage(IPackageFragment sourcePackage)
			throws CoreException {
		return sourcePackage.getCompilationUnits().length;
	}
	
}
