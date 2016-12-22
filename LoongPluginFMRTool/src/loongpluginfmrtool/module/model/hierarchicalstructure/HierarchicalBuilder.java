package loongpluginfmrtool.module.model.hierarchicalstructure;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import loongplugin.source.database.ApplicationObserver;
import loongplugin.source.database.model.LElement;
import loongplugin.source.database.model.LRelation;
import loongpluginfmrtool.module.model.module.Module;
import loongpluginfmrtool.module.model.module.ModuleBuilder;
import loongpluginfmrtool.views.moduledependencyviews.HierarchicalBuilderChangedEvent;
import loongpluginfmrtool.views.moduledependencyviews.IHierarchicalBuilderChangeListener;
import soot.Modifier;

public class HierarchicalBuilder {
	/**
	 * @author tangchris
	 * This class aims to build a hierarchical representation of module relations 
	 * along with presence conditions
	 * starts from the entry point, which is the class contains main method
	 * goes with all modules that the class jumps to
	 * and represented in a hierarchical manner
	 * 
	 * 
	 * 
	 */
	private ModuleBuilder abuilder = null;
	protected ApplicationObserver AOB;
	// 
	/*
	 *  entry modules set normally contains one member, which is the 
	 *  module contains main method,
	 *  if there are multiple modules contains main method, then they all should
	 *  be covered in this this#entrymodules
	 */
	private Set<Module> entrymodules = null;
	
	// index to modules
	private Map<Integer, Module> indextoModules = null;
	
	// builder change listener
	private IHierarchicalBuilderChangeListener listener = null;
	
	// a map to reserve jumpto information
	private Map<Module,HierarchicalNeighbor> sourcetoNeighbor = new HashMap<Module,HierarchicalNeighbor>();
	
	public HierarchicalBuilder(ModuleBuilder pbuilder){
		// initialize
		this.abuilder = pbuilder;
		this.entrymodules = new HashSet<Module>();
		this.AOB = ApplicationObserver.getInstance();
		
		// obtain all modules
		this.indextoModules = this.abuilder.getIndexToModule();
		
		// invoke entry module finder
		findentryModules();
		
		// find logical connections with relations
		exploreAllHierchicalRelations();
	}
	
	/**
	 * this function will explore all modules and find the hierarchical relation
	 * dependencies
	 * @return
	 */
	private void exploreAllHierchicalRelations() {
		for(Map.Entry<Integer, Module>entry:this.indextoModules.entrySet()){
			Module module = entry.getValue();
			// get all call dependencies
			Set<Module> dependencies = module.getAllCallDependency().keySet();
			// 
			if(dependencies.size()!=0){
				
			}
		}
		
	}

	public void setListener(IHierarchicalBuilderChangeListener plistener){
		this.listener = plistener;
	}
			
	
	/**
	 * Find the entry method to the application, namely the main method
	 * @return
	 */
	private void findentryModules() {
		LRelation invokedbyRelation = LRelation.T_ACCESS_METHOD;
		
		for(Map.Entry<Integer, Module>entry:indextoModules.entrySet()){
			 Set<LElement> allmethods = entry.getValue().getallMethods();
			 for(LElement elem:allmethods){
				 MethodDeclaration methoddecl = (MethodDeclaration)elem.getASTNode();
				 if(methoddecl.getName().toString().equals("main")&&
						 Modifier.isPublic(methoddecl.getModifiers())){
					 // is a main method
					 // also check it has no caller
					 Set<LElement> targetElements = AOB.getRange(elem,invokedbyRelation);
					 if(targetElements==null){
						 entrymodules.add(entry.getValue());
						 break;
					 }else if(targetElements.size()==0){
						 entrymodules.add(entry.getValue());
						 break;
					 }
				 }
			 }
		}
		
		if(entrymodules.size()==0){
			try {
				throw new Exception("Cannot find the entry module, which means no main method find in program");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	public void notifyFeatureModelListener(){
		HierarchicalBuilderChangedEvent event = new HierarchicalBuilderChangedEvent(this, this);
		listener.hierarchicalBuilderChanged(event);
		
	}

	public Object[] getNodes() {
		// return all subjective underlying node in the builder
		return indextoModules.values().toArray();
	}

	public Object[] getConnectedTo(Module node) {
		// TODO Auto-generated method stub
		if(this.sourcetoNeighbor.containsKey(node)){
			HierarchicalNeighbor neighbor = this.sourcetoNeighbor.get(node);
			return neighbor.getAllNeighbors().toArray();
		}else
			return null;
	}
	
}
