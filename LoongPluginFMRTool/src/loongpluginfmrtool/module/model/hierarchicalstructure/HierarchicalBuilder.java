package loongpluginfmrtool.module.model.hierarchicalstructure;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.swt.widgets.Shell;

import loongplugin.source.database.ApplicationObserver;
import loongplugin.source.database.model.LElement;
import loongplugin.source.database.model.LFlyweightElementFactory;
import loongplugin.source.database.model.LRelation;
import loongpluginfmrtool.module.model.configuration.ConfigurationCondition;
import loongpluginfmrtool.module.model.constrains.LinkerAndConditionalConstrains;
import loongpluginfmrtool.module.model.constrains.TypeConstrains;
import loongpluginfmrtool.module.model.module.Module;
import loongpluginfmrtool.module.model.module.ModuleBuilder;
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
	
	
	// a map to reserve jumpto information
	private Map<Module,HierarchicalNeighbor> sourcetoNeighbor = new HashMap<Module,HierarchicalNeighbor>();
	
	// 
	private LFlyweightElementFactory alElementfactory;
	
	public HierarchicalBuilder(ModuleBuilder pbuilder,LFlyweightElementFactory plElementfactory){
		// initialize
		this.abuilder = pbuilder;
		this.entrymodules = new HashSet<Module>();
		this.AOB = ApplicationObserver.getInstance();
		this.alElementfactory = plElementfactory;
		
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
			Set<Module> calldependencies = module.getAllCallDependency().keySet();
			// type constrains
			TypeConstrains typeconstrains = module.getTypeConstrains();
			Set<LElement> type_typeconstrains_LElements = typeconstrains.getTypeConstrainsLElement();
			HierarchicalNeighbor neighbor = new HierarchicalNeighbor(module);
			// run all elements
			for(LElement element:type_typeconstrains_LElements){
				CompilationUnit unit = element.getCompilationUnit();
				LElement compelement = this.alElementfactory.getElement(unit);
				Module targetmodule = abuilder.getModuleByLElement(compelement);
				// if it is not the source module
				if(targetmodule==null){
					try {
						throw new Exception("unkonw module");
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				if(targetmodule.getIndex()!=module.getIndex()){
					neighbor.addNeighbor(targetmodule);
				}
			}
			// linker and conditional constraints
			LinkerAndConditionalConstrains linkercondconstrains = module.getLinkerAndConditionalConstrains();
			Map<ConfigurationCondition,Set<LElement>> rawenabledconstrains = linkercondconstrains.getRawEnableConstrains();
			for(Map.Entry<ConfigurationCondition, Set<LElement>>enableentry:rawenabledconstrains.entrySet()){
				ConfigurationCondition config = enableentry.getKey();
				Set<LElement> elements = enableentry.getValue();
				// run all elements
				for(LElement element:elements){
					CompilationUnit unit = element.getCompilationUnit();
					LElement compelement = this.alElementfactory.getElement(unit);
					Module targetmodule = abuilder.getModuleByLElement(compelement);
					// if it is not the source module
					if(targetmodule.getIndex()!=module.getIndex()){
						neighbor.addNeighbor(config, targetmodule);
					}
				}
			}
		}
		
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

	/**
	 * export the result to xml file
	 */
	public void writetoxml() {
		//
		String filepath = "";
		
		
		
		
	}
	
}
