package loongpluginfmrtool.module.model.module;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.AnnotationTypeDeclaration;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import loongplugin.featureconfiguration.Configuration;
import loongplugin.source.database.ApplicationObserver;
import loongplugin.source.database.ProgramDatabase;
import loongplugin.source.database.model.LElement;
import loongplugin.source.database.model.LFlyweightElementFactory;
import loongplugin.source.database.model.LICategories;
import loongpluginfmrtool.module.featuremodelbuilder.ModuleDependencyTable;
import loongpluginfmrtool.views.moduleviews.IModuleModelChangeListener;
import loongpluginfmrtool.views.moduleviews.ModuleModel;
import loongpluginfmrtool.views.moduleviews.ModuleViewPart;
import loongpluginfmrtool.views.moduleviews.ModuleViewPart.ModuleModelChangeListener;
import loongpluginfmrtool.views.moduleviews.moduleModelChangedEvent;

public class ModuleBuilder {
	
	public static ModuleBuilder instance;
	private static IProject targetProject;
	private Map<Integer,Module> indexToModule = new HashMap<Integer,Module>();
	private static ApplicationObserver lDB;
	final private ProgramDatabase pd;
	private LFlyweightElementFactory LElementFactory;
	private static Map<LElement,Module> elementToModule = new HashMap<LElement,Module>();
	private List<IModuleModelChangeListener>listeners = new LinkedList<IModuleModelChangeListener>();
	private ModuleModel amodel = new ModuleModel();
	private Set<Module>allmodules = new HashSet<Module>();
	private ModuleDependencyTable dependency_table;
	public static ModuleBuilder getInstance(IProject selectedProject,ApplicationObserver pDB){
		if(ModuleBuilder.targetProject!=selectedProject || lDB != pDB){
			instance = new ModuleBuilder(selectedProject,pDB);
		}
		return instance;
	}
	public ModuleBuilder(IProject selectedProject,ApplicationObserver pDB){
		ModuleBuilder.targetProject = selectedProject;
		this.lDB = pDB;
		this.LElementFactory = pDB.getLFlyweightElementFactory();
		this.pd = pDB.getProgramDatabase();
	}
	
	public void init(final boolean withvarability){
		try {
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(ModuleViewPart.ID);
		} catch (PartInitException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		ModuleViewPart view_instance = ModuleViewPart.getInstance();
		ModuleModelChangeListener listener = view_instance.getModuleListener();
		if(listener!=null){
			listeners.add(listener);
		}else{
			view_instance.resetModuleListener();
			listener = view_instance.getModuleListener();
			assert listener!=null;
			listeners.add(listener);
		}
		final IContainer root=ResourcesPlugin.getWorkspace().getRoot();
		WorkspaceJob job=new WorkspaceJob("CreateModule"){
		    @Override 
		    public IStatus runInWorkspace(IProgressMonitor monitor) throws CoreException {
		    	buildModules(monitor,withvarability);
		    	return Status.OK_STATUS;
		    }
		 };
		 
		 job.setRule(root);
		 job.setUser(true);
		 job.setPriority(Job.INTERACTIVE);
		 job.schedule();
		 
		
	}
	
	public void notifyModuleListener() {
		// TODO Auto-generated method stub
		moduleModelChangedEvent event = new moduleModelChangedEvent(this,amodel);
		for(IModuleModelChangeListener listener:listeners){
			listener.moduleModelChanged(event);
		}
	}
	public Module getModuleByIndex(int index){
		if(indexToModule.containsKey(index)){
			return indexToModule.get(index);
		}else
			return null;
	}
	
	
	public void buildModules(IProgressMonitor pProgress,boolean withvariability){
		assert pProgress != null;
		/**
		 * build the basic compilationunit to module
		 */
		if(pProgress != null){ 
    		pProgress.beginTask("Building basic modules configuration", this.pd.getAllElements().size());
    	}
		int module_index = 0;
		int subcount = 0;
		for(LElement element:this.pd.getAllElements()){
			pProgress.subTask("Process element:"+element.getCompilationUnitName());
			subcount++;
			//System.out.println("Process element:"+element.getCompilationUnitName()+"subcount:"+subcount);
			if(element.getCategory().equals(LICategories.COMPILATION_UNIT)){
				// initialize a module
				ASTNode node_element = element.getASTNode();
				if(node_element instanceof AnnotationTypeDeclaration){
					continue;
				}
				Module module = new Module(element,module_index,this.LElementFactory,this,amodel);
				elementToModule.put(element, module);
				allmodules.add(module);
				indexToModule.put(module_index, module);
				amodel.addModule(module);
				// over the index
				
				System.out.println("processing astnode index:"+module_index);
				module_index++;
			}
			pProgress.worked(1);
		}
		
		if(pProgress != null){ 
    		pProgress.beginTask("Building basic modules & Internal Varability", indexToModule.size()*2);
    	}
		subcount = 0;
		
		/**
		 * extract variability configuration from all modules
		 */
		
		System.out.println("total count:"+indexToModule.size());
		for(Map.Entry<Integer, Module>entry:indexToModule.entrySet()){
			subcount++;
			pProgress.subTask("Initializing modules:"+entry.getValue().getModuleName());
		//	System.out.println("Process\t"+subcount+ " element:"+entry.getValue().getModuleName());
			Module module = entry.getValue();
			if(withvariability)
				module.initialize_configuration();
			else
				module.initialize();
			//module.initialize();
			pProgress.worked(1);
			System.out.println("intialize module:"+subcount);
		}
		
		
		for(Map.Entry<Integer, Module>entry:indexToModule.entrySet()){
			subcount++;
			pProgress.subTask("Extracting presence condition:"+entry.getValue().getModuleName());
			Module module = entry.getValue();
			if(withvariability)
				module.extractConstrains();
			
			pProgress.worked(1);
			System.out.println("extracting presence condition"+subcount);
		}
		
		
		
		
		
		
		
		System.out.println("Process done");
		pProgress.done();
		
		
	}
	
	
	
	public Module getModuleByLElement(LElement useelement) {
		// TODO Auto-generated method stub
		if(useelement!=null)
			return elementToModule.get(useelement);
		else
			return null;
	}
	
	
	public Map<Integer, Module> getIndexToModule() {
		// TODO Auto-generated method stub
		return indexToModule;
	}
	
	public ModuleDependencyTable getDependencyTable() {
		// TODO Auto-generated method stub
		if(dependency_table==null){
			dependency_table = new ModuleDependencyTable(this);
			dependency_table.buildTable();
		}
		return dependency_table;
	}
	
	
	public IProject getsubjectProject() {
		// TODO Auto-generated method stub
		return targetProject;
	}
	
	
}
