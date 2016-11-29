package loongpluginfmrtool.module.builder;

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
import loongpluginfmrtool.module.featuremodelbuilder.InformationLossTable;
import loongpluginfmrtool.module.featuremodelbuilder.KullbackLeiblerTable;
import loongpluginfmrtool.module.featuremodelbuilder.ModuleDependencyTable;
import loongpluginfmrtool.module.featuremodelbuilder.ModuleHelper;
import loongpluginfmrtool.module.model.ConfigurationOption;
import loongpluginfmrtool.module.model.ConfigurationRelationLink;
import loongpluginfmrtool.module.model.Module;
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
	private KullbackLeiblerTable kullback_leibler_table;
	private InformationLossTable information_loss_table;
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
		 
		try {
			job.join();
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		if(withvarability){
			 // Create and check cross module(external variability)
			 WorkspaceJob externalcheckerjob=new WorkspaceJob("CheckVariabilityModule & Dependency"){
				    @Override 
				    public IStatus runInWorkspace(IProgressMonitor monitor) throws CoreException {
				    	variabilityModules_buildDependency(monitor);
				    	return Status.OK_STATUS;
				    }
			};
			
			externalcheckerjob.setRule(root);
			externalcheckerjob.setUser(true);
			externalcheckerjob.setPriority(Job.INTERACTIVE);
			externalcheckerjob.schedule();
			try {
				externalcheckerjob.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
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
	public void variabilityModules_buildDependency(IProgressMonitor pProgress){
		if(pProgress != null){ 
			int size = indexToModule.size();
    		pProgress.beginTask("Extract External Variability", size);
    	}

		for(Map.Entry<Integer, Module>entry:indexToModule.entrySet()){
			Module module = entry.getValue();
			module.resolveExternalVariability();
			
			pProgress.worked(1);
		}
		pProgress.done();
	}
	
	public void buildModules(IProgressMonitor pProgress,boolean withvariability){
		assert pProgress != null;
		if(pProgress != null){ 
    		pProgress.beginTask("Building basic modules & Internal Varability", this.pd.getAllElements().size());
    	}
		int module_index = 0;
		System.out.println("total size:"+this.pd.getAllElements().size());
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
    		pProgress.beginTask("Building basic modules & Internal Varability", indexToModule.size());
    	}
		subcount = 0;
		System.out.println("total count:"+indexToModule.size());
		for(Map.Entry<Integer, Module>entry:indexToModule.entrySet()){
			subcount++;
		//	pProgress.subTask("Initializing modules:"+entry.getValue().getModuleName());
		//	System.out.println("Process\t"+subcount+ " element:"+entry.getValue().getModuleName());
			Module module = entry.getValue();
			if(withvariability)
				module.variabilityinitialize();
			else
				module.normailinitialize();
			//module.initialize();
			pProgress.worked(1);
			System.out.println("processing module:"+subcount);
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
	
	protected void computeVariabilityLevel(){
		for(Module module:amodel.getModules()){
			for(ConfigurationOption config:module.getAllConfigurationOptions()){
				config.computeVariability();
			}
		}
		
	}
	
	/**
	 * this function will compute the over all variability level that 
	 * represent the degree that a configuration can decide the program execution.
	 */
	protected void computeOverallVariabilityLevel(){
		// 获得所有configuration option
		Set<ConfigurationOption>allconfigurationOptions = new HashSet<ConfigurationOption>();
		for(Module module:allmodules){
			allconfigurationOptions.addAll(module.getAllConfigurationOptions());
		}
		
		// 做第二件事 將所有的configuration option都拷貝到一個 list中
		while(!allconfigurationOptions.isEmpty()){
			Set<ConfigurationOption>needToRemove = new HashSet<ConfigurationOption>();
			for(ConfigurationOption option:allconfigurationOptions){
				if(option.getlinks().size()==0){
					option.setOverallVariabilityCount(1);
					needToRemove.add(option);
				}else{
					Set<ConfigurationRelationLink> links = option.getlinks();
					Set<ConfigurationOption>alreadyset = new HashSet<ConfigurationOption>();
					boolean canset = true;
					int value = 0;
					for(ConfigurationRelationLink link:links){
						ConfigurationOption target = link.getTargetConfigurationOption();
						if(target.isOverallVariabilitySet()){
							if(!alreadyset.contains(target)){
								value+=target.getOverallVariability();
								alreadyset.add(target);
							}
						}else if(target.equals(option)){
							if(!alreadyset.contains(target)){
								alreadyset.add(target);
							}
						}else{
							canset = false;
							break;
						}
					}
					alreadyset.clear();
					if(canset){
						option.setOverallVariabilityCount(value);
						needToRemove.add(option);
					}
				}
			}
			if(!needToRemove.isEmpty()){
				allconfigurationOptions.removeAll(needToRemove);
			}else{
				// 这里出现死锁 单独处理
				break;
			}
		}
		while(!allconfigurationOptions.isEmpty()){
			Set<ConfigurationOption>needToRemove = new HashSet<ConfigurationOption>();
			for(ConfigurationOption option:allconfigurationOptions){
				Set<ConfigurationRelationLink> links = option.getlinks();
				Set<ConfigurationOption>alreadyset = new HashSet<ConfigurationOption>();
				boolean canset = true;
				int value = 0;
				for(ConfigurationRelationLink link:links){
					ConfigurationOption target = link.getTargetConfigurationOption();
					if(target.isOverallVariabilitySet()){
						if(!alreadyset.contains(target)){
							value+=target.getOverallVariability();
							alreadyset.add(target);
						}
					}else if(target.equals(option)){
						if(!alreadyset.contains(target)){
							alreadyset.add(target);
						}
					}else if(allconfigurationOptions.contains(target)){
						if(!alreadyset.contains(target)){
							alreadyset.add(target);
						}
					}else{
						canset = false;
						break;
					}
				}
				alreadyset.clear();
				if(canset){
					option.setOverallVariabilityCount(value);
					needToRemove.add(option);
				}
				
			}
			if(!needToRemove.isEmpty()){
				allconfigurationOptions.removeAll(needToRemove);
			}else{
				try {
					throw new Exception("new error in processing overall variability");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	public void computeStatistic() {
		// TODO Auto-generated method stub
		
		computeVariabilityLevel();
		
		computeOverallVariabilityLevel();
	}
	public Map<Integer, Module> getIndexToModule() {
		// TODO Auto-generated method stub
		return indexToModule;
	}
	public void buildModuleHelper() {
		// TODO Auto-generated method stub
		dependency_table = new ModuleDependencyTable(this);
		dependency_table.buildTable();
		kullback_leibler_table = new KullbackLeiblerTable(this);
		kullback_leibler_table.buildTable();
		// build the module helper for each module
		for(Map.Entry<Integer, Module>entry:indexToModule.entrySet()){
			Module module = entry.getValue();
			ModuleHelper helper = new ModuleHelper(module,this);
			module.addModuleHelper(helper);
		}
		information_loss_table = new InformationLossTable(this);
		information_loss_table.buildTable();
	}
	public ModuleDependencyTable getDependencyTable() {
		// TODO Auto-generated method stub
		if(dependency_table==null){
			dependency_table = new ModuleDependencyTable(this);
			dependency_table.buildTable();
		}
		return dependency_table;
	}
	
	public KullbackLeiblerTable getKullbackLeiblerTable(){
		if(kullback_leibler_table==null){
			kullback_leibler_table = new KullbackLeiblerTable(this);
			kullback_leibler_table.buildTable();
		}
		return kullback_leibler_table;
	}
	public IProject gettargetProject() {
		// TODO Auto-generated method stub
		return targetProject;
	}
	
	
}
