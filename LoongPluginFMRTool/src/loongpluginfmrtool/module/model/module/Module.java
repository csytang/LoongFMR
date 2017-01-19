package loongpluginfmrtool.module.model.module;
import java.io.File;
import java.io.Serializable;
import java.util.Collection;
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
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import loongplugin.source.database.model.LElement;
import loongplugin.source.database.model.LFlyweightElementFactory;
import loongpluginfmrtool.module.model.configuration.ConfTractor;
import loongpluginfmrtool.module.model.configuration.ConfigurationOption;
import loongpluginfmrtool.module.model.constrains.LinkerAndConditionalConstrains;
import loongpluginfmrtool.module.model.constrains.MethodReference;
import loongpluginfmrtool.module.model.constrains.TypeConstrains;
import loongpluginfmrtool.util.ASTNodeWalker;
import loongpluginfmrtool.util.FixedRequiredFinder;
import loongpluginfmrtool.util.HardMethodInvocationFinder;
import loongpluginfmrtool.views.moduleviews.ModuleModel;

public class Module implements Serializable {
	
	private LElement dominate;
	private int moduleIndex=0;
	private Set<LElement> allmethods = new HashSet<LElement>();
	private Set<ModuleComponent> components = new HashSet<ModuleComponent>();
	private LFlyweightElementFactory lElementfactory;
	private ASTNode dominateASTNode;
	private ModuleBuilder abuilder;
	private ConfTractor contflowbuilder;
	private Map<LElement,Set<ConfigurationOption>> method_configurations = new HashMap<LElement,Set<ConfigurationOption>>();
	private Map<ConfigurationOption,LElement> configuration_method = new HashMap<ConfigurationOption,LElement>();
	private Set<ConfigurationOption> configurations;
	private Map<LElement,Set<Module>> methodtohardReference = new HashMap<LElement,Set<Module>>();
	private Map<LElement,Set<LElement>> methodtomethodInvocation = new HashMap<LElement,Set<LElement>>();
	private ModuleModel model = null;
	private IProject aProject = null;
	private Map<Module,Integer> module_calldependency = new HashMap<Module,Integer>();
	
	
	/************external configuration options********/
	// here lists the configuration options from others
	private Map<ConfigurationOption,LElement> externaloption_internalelement = new HashMap<ConfigurationOption,LElement>();
	
	
	/************constrains***************/
	private LinkerAndConditionalConstrains linkcondconstains = null;
	private TypeConstrains typeconstrains = null;
	private MethodReference methodconstrains = null;
	
	
	public Module(LElement element,int index,LFlyweightElementFactory pElementFactory,ModuleBuilder mbuilder,ModuleModel pmodel){
		this.dominate = element;
		this.moduleIndex = index;
		this.lElementfactory = pElementFactory;
		this.abuilder = mbuilder;
		this.dominateASTNode = element.getASTNode();		
		this.contflowbuilder = new ConfTractor(this);
		this.model = pmodel;
		this.aProject = mbuilder.getsubjectProject();
	}
	
	/**
	 * initialize this module
	 */
	public void initialize_configuration(){
		
		initialize();
		
		// resolve variability
		findConfigurations();
		
		configurations =  getAllConfigurationOptions();
		components.addAll(configurations);
		
		extractConstrains();
	}
	
	public void initialize(){
		// resolve body
		resolvebody();
	}
	
	/**
	 * The internal variability will not jump to other module
	 * and only resolve this this module
	 */
	private void findConfigurations(){
		
		// a full parse that get all configuration in the method
		this.contflowbuilder.parse();
		
		// extract the information
		this.method_configurations = this.contflowbuilder.getMethod_To_Configuration();
		this.configuration_method = this.contflowbuilder.getConfiguration_To_Method();
		
		
		
	}
	
	public Map<LElement,Set<ConfigurationOption>> getMethod_To_Configuration(){
		return this.method_configurations;
	}
	
	public Map<ConfigurationOption,LElement> getConfiguration_To_Method(){
		return this.configuration_method;
	}
	
	
	public Set<LElement> getallMethods(){
		return allmethods;
	}
	
	
	public  Map<Module,Integer> getAllCallDependency(){
		return module_calldependency;
	}
	
	private void resolvebody(){
		// find the method body inside the class
		if(dominateASTNode!=null){
			Set<ASTNode> methodASTNodes = ASTNodeWalker.methodWalker(dominateASTNode);
			for(ASTNode method:methodASTNodes){
				MethodDeclaration methoddecl = (MethodDeclaration)method;
				IMethodBinding binding = methoddecl.resolveBinding();
				if(binding==null){
					// there should be an API call
					return;
				}
				LElement methodelement = lElementfactory.getElement(binding);
				if(methodelement!=null)
					allmethods.add(methodelement);
			}
			
			// obtain the dependency information by the way
			resoveDependency();
		}
	}
	
	/**
	 * obtain the dependency information for this class
	 */
	private void resoveDependency() {
		ModuleCallDependencyBuilder dependencybuilder = new ModuleCallDependencyBuilder(this,lElementfactory);
		dependencybuilder.parse();
		this.module_calldependency = dependencybuilder.getmoduleCallDependencyResult();
	}

	public LElement getDominateElement(){
		return dominate;
	}

	public LFlyweightElementFactory getelementfactory() {
		// TODO Auto-generated method stub
		return lElementfactory;
	}
	

	
	public Set<ConfigurationOption> getAllConfigurationOptions(){
		if(configurations==null){
			configurations = configuration_method.keySet();
		}
		return configurations;
	}

	public String getDisplayName() {
		// TODO Auto-generated method stub
		CompilationUnit unit = (CompilationUnit)dominateASTNode;
		List types = unit.types();    
		TypeDeclaration typeDec = (TypeDeclaration) types.get(0); //typeDec is the class 
		String packageName = unit.getPackage().getName().toString();
		String fullName = packageName+"."+typeDec.getName().toString();
		return fullName;
	}
	public String getShortName(){
		CompilationUnit unit = (CompilationUnit)dominateASTNode;
		List types = unit.types();    
		TypeDeclaration typeDec = (TypeDeclaration) types.get(0); //typeDec is the class 
		String packageName = unit.getPackage().getName().toString();
		String fullName = packageName+"."+typeDec.getName().toString();
		IWorkspace workspace = ResourcesPlugin.getWorkspace();  
		String projectPath = workspace.getRoot().getLocation().toOSString()+File.separatorChar+aProject.getName().toString();
		if(fullName.startsWith(projectPath)){
			fullName = fullName.substring(projectPath.length());
		}
		return fullName;
	}
	
	public String getModuleName(){
		CompilationUnit unit = (CompilationUnit)dominateASTNode;
		List types = unit.types();    
		AbstractTypeDeclaration typeDec = (AbstractTypeDeclaration) types.get(0); //typeDec is the class  
		return typeDec.getName().toString();
	}
	
	public String getNameWithPackage(){
		CompilationUnit unit = (CompilationUnit)dominateASTNode;
		List types = unit.types();    
		TypeDeclaration typeDec = (TypeDeclaration) types.get(0); //typeDec is the class  
		String packagename = unit.getPackage().getName().toString();
		packagename=packagename+".";
		packagename = packagename+typeDec.getName().toString();
		return packagename;
	}

	public Set<ModuleComponent> getComponents() {
		// TODO Auto-generated method stub
		return components;
	}

	public ModuleModel getParent() {
		// TODO Auto-generated method stub
		return model;
	}

	public String getId() {
		return moduleIndex+"";
	}

	public CompilationUnit getCompilationUnit() {
		return this.dominate.getCompilationUnit();
	}

	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof Module)){
			return false;
		}else{
			Module module_obj = (Module)obj;
			if(module_obj.moduleIndex==this.moduleIndex)
				return true;
			else
				return false;
		}
		
	}

	public IFile getIFile() {
		IFile file = null;
		CompilationUnit unit = this.dominate.getCompilationUnit();
		ICompilationUnit iunit = (ICompilationUnit) unit.getJavaElement();
		IPath path = iunit.getPath();
		file = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
		return file;
	}


	public int getIndex() {
		return moduleIndex;
	}

	/**
	 * Extract the presence condition for this module
	 * see paper:
	 * Where do Configuration Constraints Stem From?
		An Extraction Approach and an Empirical Study
		http://ieeexplore.ieee.org/document/7065312/
	 */
	
	public void extractConstrains() {
		// 1. linker constrains
		// 3. conditional constrains
		/**
		 * @link LinkerConstrains;
		 */
		linkcondconstains = new LinkerAndConditionalConstrains(this,this.lElementfactory,this.method_configurations);
		
		// 2. type constrains
		
		typeconstrains = new TypeConstrains(this,this.lElementfactory);
		
		// 3. method reference
		
		methodconstrains = new MethodReference(this,this.lElementfactory);
		
		// 4. map method to hard reference
	//	methodtohardReference = findhardReference();
	//	methodtomethodInvocation = findhardMethodInvocation();
		
	}

	public Map<LElement, Set<Module>> getHardReferencebyMethod(){
		methodtohardReference = findhardReference();
		return methodtohardReference;
	}
	
	public Map<LElement,Set<LElement>> getHardMethodInvocation(){
		methodtomethodInvocation = findhardMethodInvocation();
		return methodtomethodInvocation;
	}
	
	private Map<LElement,Set<LElement>> findhardMethodInvocation(){
		Map<LElement,Set<LElement>> methodinvocation = new HashMap<LElement,Set<LElement>>();
		for(LElement method:allmethods){
			Set<LElement> method_hardinvocation = new HashSet<LElement>();
			MethodDeclaration methoddecl = (MethodDeclaration)method.getASTNode();
			Block body = methoddecl.getBody();
			if(body==null)
				continue;
			List<Statement> allstatements = body.statements();
			for(Statement statement:allstatements){
				HardMethodInvocationFinder finder = new HardMethodInvocationFinder(lElementfactory);
				statement.accept(finder);
				Collection<LElement>methodinvocationcollection = (Collection<LElement>) finder.getMethodInovaction();
				Set<LElement> invokedmethods = new HashSet<LElement>(methodinvocationcollection);
				method_hardinvocation.addAll(invokedmethods);
			}
			// put into the map
			methodinvocation.put(method,method_hardinvocation);
		}
		return methodinvocation;
	}
	
	private Map<LElement, Set<Module>> findhardReference() {
		Map<LElement,Set<Module>>hardreferencebyMethod = new HashMap<LElement,Set<Module>>();
		for(LElement method:allmethods){
			Set<Module> hardref_method = new HashSet<Module>();
			MethodDeclaration methoddecl = (MethodDeclaration)method.getASTNode();
			Block body = methoddecl.getBody();
			if(body==null)
				continue;
			List<Statement> allstatements =body.statements();
			for(Statement statement:allstatements){
				FixedRequiredFinder finder = new FixedRequiredFinder(lElementfactory,abuilder.getModuleByLElementMapping());
				statement.accept(finder);
				hardref_method.addAll(finder.getrequiredmodules());
			}
			// put into the map
			hardreferencebyMethod.put(method, hardref_method);
		}
		
		return hardreferencebyMethod;
	}

	/**
	 * return the type constrains of this module
	 * @return
	 */
	public TypeConstrains getTypeConstrains() {
		
		return typeconstrains;
	}
	
	/**
	 * return the linker and conditional constrain of this module
	 * @return
	 */
	public LinkerAndConditionalConstrains getLinkerAndConditionalConstrains(){
		return linkcondconstains;
	}
	
	
	public MethodReference getMethodReference(){
		return methodconstrains;
	}

	public boolean hasMainMethod() {
		for(LElement method:allmethods){
			MethodDeclaration methoddecl = (MethodDeclaration)method.getASTNode();
			
			if(methoddecl.getName().toString().equals("main")){
				return true;
			}else{
				System.out.println(methoddecl.getName()+" is not main method");
			}
		}
		return false;
	}

	
}
