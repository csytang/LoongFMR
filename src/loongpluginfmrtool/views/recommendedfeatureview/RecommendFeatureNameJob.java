package loongpluginfmrtool.views.recommendedfeatureview;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import loongplugin.LoongPlugin;
import loongpluginfmrtool.views.recommendedfeatureview.RecommendedFeatureView.RSFeatureModelChangeListener;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IImportContainer;
import org.eclipse.jdt.core.IImportDeclaration;
import org.eclipse.jdt.core.IInitializer;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaModel;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.ILocalVariable;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageDeclaration;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeParameter;
import org.eclipse.jdt.core.ITypeRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.NodeFinder;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.internal.corext.refactoring.structure.ASTNodeSearchUtil;

public class RecommendFeatureNameJob extends WorkspaceJob{

	
	private List<IJavaElement> unifiedIJavaElements = new LinkedList<IJavaElement>();
	private List<IJavaElement> ununifiedIJavaElements;
	private FeatureNameDictionary dict;
	private IProject selectProject = null;
	private RSFeatureModelChangeListener listener;
	public RecommendFeatureNameJob(List<IJavaElement>elements,IProject project,RSFeatureModelChangeListener plistener) {
		super("Building recommended name list for project:"+project.getName());
		ununifiedIJavaElements = elements;
		selectProject = project;
		listener = plistener;
	}

	@Override
	public IStatus runInWorkspace(IProgressMonitor monitor)
			throws CoreException {
		// TODO Auto-generated method stub
		// Extract all List into a java file fashion(at least)
		dict = new FeatureNameDictionary(monitor);
		dict.setProject(selectProject);
		dict.addRSFeatureModelChangeListener(listener);
		for(IJavaElement element:ununifiedIJavaElements){
			if(element instanceof IPackageFragment){
				ICompilationUnit[]allcompilationUnit = ((IPackageFragment)element).getCompilationUnits();
				if(allcompilationUnit!=null){
					for(ICompilationUnit unit:allcompilationUnit){
						unifiedIJavaElements.add(unit);
					}
				}
			}else if(element instanceof IJavaProject){
				// 获取里面所有的java文件
				IJavaProject projectelement = (IJavaProject)element;
				IPackageFragment[] allfragment = projectelement.getPackageFragments();
				for(IPackageFragment fragment:allfragment){
					ICompilationUnit[]allcompilationUnit = fragment.getCompilationUnits();
					if(allcompilationUnit!=null){
						for(ICompilationUnit unit:allcompilationUnit){
							unifiedIJavaElements.add(unit);
						}
					}
				}
			}else if(element instanceof IPackageFragmentRoot){
				continue;
			}else{
				unifiedIJavaElements.add(element);
			}
		}
		int processUnit = unifiedIJavaElements.size();
		monitor.beginTask("Building recommended name list", processUnit+3);
		for(IJavaElement element:unifiedIJavaElements){
			if(element instanceof IAnnotation){
				monitor.worked(1);
				continue;
			}else if(element instanceof IClassFile){
				monitor.worked(1);
				continue;
			}else if(element instanceof ICompilationUnit){
				ASTParser parser = ASTParser.newParser(LoongPlugin.AST_VERSION);
				parser.setKind( ASTParser.K_COMPILATION_UNIT );
				parser.setResolveBindings( true );
				parser.setBindingsRecovery( true );
				parser.setSource((ICompilationUnit)element);
			    ASTNode rootNode = parser.createAST( null );
			    String compilationUnitName = ((ICompilationUnit)element).getElementName();
			    compilationUnitName = compilationUnitName.split("\\.")[0];
			    dict.addDictBuiltElement(compilationUnitName, element,rootNode);
			    ASTStringTracker astTracker = new ASTStringTracker(rootNode);
			    Map<String,Set<ASTNode>> recommendfeatureNames = astTracker.getRecommendedFeatureNameList();
			    Map<String,Set<ASTNode>> recommendnonfeatureNames = astTracker.getRecommendedNonFeatureNameList();
			    
			    for(Map.Entry<String, Set<ASTNode>> entry:recommendfeatureNames.entrySet()){
					dict.addDictBuiltElement(entry.getKey(), element,entry.getValue());
				}
				for(Map.Entry<String, Set<ASTNode>> entry:recommendnonfeatureNames.entrySet()){
					dict.addAnyElement(entry.getKey(), element ,entry.getValue());
				}
			}else if(element instanceof IField){
				IField jfieldelement = (IField)element;
				String name = jfieldelement.getElementName();
				ICompilationUnit icomp = jfieldelement.getCompilationUnit();
				ASTParser parser = ASTParser.newParser(LoongPlugin.AST_VERSION);
				parser.setKind( ASTParser.K_COMPILATION_UNIT );
				parser.setResolveBindings( true );
				parser.setBindingsRecovery( true );
				parser.setSource((ICompilationUnit)element);
			    ASTNode rootNode = parser.createAST( null );
			    FieldDeclaration  fieldDecl = ASTNodeSearchUtil.getFieldDeclarationNode(jfieldelement, (CompilationUnit)rootNode);
				dict.addDictBuiltElement(name, element, fieldDecl);
			}else if(element instanceof IImportContainer){
				monitor.worked(1);
				continue;
			}else if(element instanceof IInitializer){
				monitor.worked(1);
				continue;
			}else if(element instanceof IJavaModel){
				monitor.worked(1);
				continue;
			}else if(element instanceof IJavaProject){
				monitor.worked(1);
				continue;
			}else if(element instanceof ILocalVariable){
				monitor.worked(1);
				continue;
			}else if(element instanceof IMember){
				monitor.worked(1);
				continue;
			}else if(element instanceof IMethod){
				String methodname = ((IMethod)element).getElementName();
				ICompilationUnit unit = ((IMethod)element).getCompilationUnit();
				MethodDeclaration methodDecl = convertIMethodToMethodDecl((IMethod)element,unit);
				dict.addDictBuiltElement(methodname, element, methodDecl);
				ASTStringTracker astTracker = new ASTStringTracker(methodDecl);
				Map<String,Set<ASTNode>> recommendfeatureNames = astTracker.getRecommendedFeatureNameList();
				Map<String,Set<ASTNode>> recommendnonfeatureNames = astTracker.getRecommendedNonFeatureNameList();
				for(Map.Entry<String, Set<ASTNode>> entry:recommendfeatureNames.entrySet()){
					dict.addDictBuiltElement(entry.getKey(), element,entry.getValue());
				}
				for(Map.Entry<String, Set<ASTNode>> entry:recommendnonfeatureNames.entrySet()){
					dict.addAnyElement(entry.getKey(), element,entry.getValue());
				}
			}else if(element instanceof IPackageDeclaration){
				monitor.worked(1);
				continue;
			}else if(element instanceof IPackageFragment){
				monitor.worked(1);
				continue;
			}else if(element instanceof IPackageFragmentRoot){
				monitor.worked(1);
				continue;
			}else if(element instanceof IType){
				String name = ((IType)element).getElementName();
				IType itypeElement = (IType)element;
				ICompilationUnit unit = itypeElement.getCompilationUnit();
				ASTParser parser = ASTParser.newParser(LoongPlugin.AST_VERSION);
				parser.setKind( ASTParser.K_COMPILATION_UNIT );
				parser.setResolveBindings( true );
				parser.setBindingsRecovery( true );
				parser.setSource((ICompilationUnit)element);
			    ASTNode rootNode = parser.createAST( null );
			    TypeDeclaration typeDecl = ASTNodeSearchUtil.getTypeDeclarationNode(itypeElement, (CompilationUnit) rootNode);
				dict.addDictBuiltElement(name, element, typeDecl);
			}else if(element instanceof ITypeParameter){
				monitor.worked(1);
				continue;
			}else if(element instanceof ITypeRoot){
				String name = (((ITypeRoot)element).findPrimaryType()).getElementName();
				IType itypeElement = (((ITypeRoot)element).findPrimaryType());
				ICompilationUnit unit = itypeElement.getCompilationUnit();
				ASTParser parser = ASTParser.newParser(LoongPlugin.AST_VERSION);
				parser.setKind( ASTParser.K_COMPILATION_UNIT );
				parser.setResolveBindings( true );
				parser.setBindingsRecovery( true );
				parser.setSource((ICompilationUnit)element);
			    ASTNode rootNode = parser.createAST( null );
			    TypeDeclaration typeDecl = ASTNodeSearchUtil.getTypeDeclarationNode(itypeElement, (CompilationUnit) rootNode);
				dict.addDictBuiltElement(name, element, typeDecl);
			}
			monitor.worked(1);
		}
		
		
		// extract name
		dict.mergeAndOptimizeDict();
		monitor.worked(1);
		
		monitor.done();
		return Status.OK_STATUS;
	}
	

	public MethodDeclaration convertIMethodToMethodDecl(IMethod method,ICompilationUnit unit){
		MethodDeclaration methodDecl = null;
		ASTParser parser = ASTParser.newParser(LoongPlugin.AST_VERSION);
		parser.setKind( ASTParser.K_COMPILATION_UNIT );
		parser.setResolveBindings( true );
		parser.setBindingsRecovery( true );
		parser.setSource(unit);
	    final ASTNode rootNode = parser.createAST( null );

	    final CompilationUnit compilationUnitNode = (CompilationUnit) rootNode;

	    final String key = method.getKey();
	    ASTNode javaElement=null;
	    if(method.isResolved()){
	    	javaElement = compilationUnitNode.findDeclaringNode( key );
	    }else{
	    	try {
				javaElement= NodeFinder.perform(rootNode, method.getSourceRange());
			} catch (JavaModelException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
	    methodDecl = (MethodDeclaration) javaElement;
		
		return methodDecl;
	}
	

	public void processContainer(IContainer container,final Set<IFile>allContainedFile)
	{
		IResource[] members = null;
		try {
			members = container.members();
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	   for (IResource member : members)
	   {
		   	if (member instanceof IContainer) 
	       	{
	    	  	processContainer((IContainer)member,allContainedFile);
	       	}
	      	else if (member instanceof IFile)
	      	{
	      		allContainedFile.add((IFile)member);
	      	}
	   }
	}

	public FeatureNameDictionary getDictionary() {
		// TODO Auto-generated method stub
		return dict;
	}
}
