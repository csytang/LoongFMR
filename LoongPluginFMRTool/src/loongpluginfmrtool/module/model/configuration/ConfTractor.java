package loongpluginfmrtool.module.model.configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Statement;

import loongplugin.source.database.ApplicationObserver;
import loongplugin.source.database.model.LElement;
import loongplugin.source.database.model.LFlyweightElementFactory;
import loongplugin.source.database.model.LRelation;
import loongpluginfmrtool.module.model.module.Module;
import loongpluginfmrtool.module.util.ASTNodeWalker;

public class ConfTractor {
	/**
	 * 
	 */
	private Module module;
	private Set<LElement> method_elements = new HashSet<LElement>();
	private ASTNode moduleastnode;
	private LFlyweightElementFactory LElementFactory = null;
	private Map<LElement,Set<ConfigurationOption>> method_configurations = new HashMap<LElement,Set<ConfigurationOption>>();
	private Map<ConfigurationOption,LElement>configuration_method = new HashMap<ConfigurationOption,LElement>();
	
	/**
	 * TODO Cannot find the option with same variable
	 * 
	 * 
	 * @param pmodule
	 */
	public ConfTractor(Module pmodule){
		this.module = pmodule;
		this.moduleastnode = this.module.getDominateElement().getASTNode();
		this.LElementFactory = this.module.getelementfactory();
	}
	public void parse(){
		// 1. process to a collection of method
		obtainMethodInside();
		
		// 2. collect the configuration options in methods
		processMethodCongBuilder();
		
	}
	
	public Map<LElement,Set<ConfigurationOption>> getMethod_To_Configuration(){
		return method_configurations;
	}
	
	public Map<ConfigurationOption,LElement> getConfiguration_To_Method(){
		return configuration_method;
	}
	
	
	
	
	
	/**
	 * get all method declared in this module
	 */
	protected void obtainMethodInside(){
		assert module!=null;
		Set<ASTNode> method_astnodes = ASTNodeWalker.methodWalker(moduleastnode);
		for(ASTNode method_ast:method_astnodes){
			MethodDeclaration methoddecl = (MethodDeclaration)method_ast;
			IBinding methodbind = methoddecl.resolveBinding();
			assert LElementFactory!=null;
			if(methodbind!=null){
				LElement method_element = LElementFactory.getElement(methodbind);
				if(method_element!=null){
					this.method_elements.add(method_element);
				}
			}
		}
		
	}
	
	/**
	 * get the configuration option in each method
	 */
	protected void processMethodCongBuilder(){
		for(LElement method:this.method_elements){
			MethodDeclaration methoddecl_astnode = (MethodDeclaration)method.getASTNode();
			// obtain the configuration in the method
			CongVisitor confvisitor = new CongVisitor(module,methoddecl_astnode,LElementFactory);
			methoddecl_astnode.accept(confvisitor);
			Set<ConfigurationOption> configurationoption_set = confvisitor.getConfigurationOptions();
			if(!configurationoption_set.isEmpty()){
				method_configurations.put(method, configurationoption_set);
			}
			for(ConfigurationOption option:configurationoption_set){
				configuration_method.put(option, method);
			}
		}
	}
	
	
}
