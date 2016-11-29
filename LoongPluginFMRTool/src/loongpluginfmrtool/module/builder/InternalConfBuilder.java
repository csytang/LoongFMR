package loongpluginfmrtool.module.builder;

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
import loongpluginfmrtool.module.model.ConfigRelation;
import loongpluginfmrtool.module.model.ConfigurationOption;
import loongpluginfmrtool.module.model.Module;
import loongpluginfmrtool.module.util.ASTNodeWalker;

public class InternalConfBuilder {
	private Module module;
	private Set<LElement> method_elements = new HashSet<LElement>();
	private ASTNode moduleastnode;
	private LFlyweightElementFactory LElementFactory = null;
	private Map<LElement,Set<ConfigurationOption>> method_configurations = new HashMap<LElement,Set<ConfigurationOption>>();
	private Map<ConfigurationOption,LElement>configuration_method = new HashMap<ConfigurationOption,LElement>();
	
	public InternalConfBuilder(Module pmodule){
		this.module = pmodule;
		this.moduleastnode = this.module.getDominateElement().getASTNode();
		this.LElementFactory = this.module.getelementfactory();
	}
	public void parse(){
		// 1. process to a collection of method
		obtainMethodInside();
		
		// 2. build the control flow graph for each method
		processMethodCongBuilder();

		// 3. extend the step2 with reference controlled
		collectdatadependencyincofiguration();
		
		// DEBUG
		//printconfig();
		// FINISH
		
		// 4. extract internal variability
		collectinternalConfigRelations();
	}
	
	
	
	public void printconfig(){
		for(Map.Entry<LElement,Set<ConfigurationOption>>entry:method_configurations.entrySet()){
			LElement method = entry.getKey();
			System.out.println("----------------------------------");
			MethodDeclaration methoddecl = (MethodDeclaration)method.getASTNode();
			System.out.println("Method:"+methoddecl.getName());
			Set<ConfigurationOption> configs = entry.getValue();
			for(ConfigurationOption option:configs){
				System.out.println("\t Option:\t "+option.getExpression().toString());
				for(ASTNode node:option.getAffectedASTNodes()){
					System.out.println("\t \t:: "+node.toString());
				}
			}
			System.out.println("----------------------------------");
			
		}
	}
	
	private void collectinternalConfigRelations() {
		// TODO Auto-generated method stub
		for(Map.Entry<LElement,Set<ConfigurationOption>>entry:method_configurations.entrySet()){
			LElement method = entry.getKey();
			Set<ConfigurationOption> configs = entry.getValue();
			for(ConfigurationOption option:configs){
				if(!configuration_method.containsKey(option)){
					configuration_method.put(option, method);
				}
			}
		}
		
		// check the relations and store
		// Internal checking
		internalChecking();
		
	}
	
	public Map<LElement,Set<ConfigurationOption>> getMethod_To_Configuration(){
		return method_configurations;
	}
	
	public Map<ConfigurationOption,LElement> getConfiguration_To_Method(){
		return configuration_method;
	}
	
	private void internalChecking(){
		for(Map.Entry<LElement,Set<ConfigurationOption>>entry:method_configurations.entrySet()){
			LElement method = entry.getKey();
			Set<ConfigurationOption> configs = entry.getValue();
			List<ConfigurationOption> config_list = new ArrayList<ConfigurationOption>(configs);
			for(int i = 0;i < config_list.size();i++){
				ConfigurationOption config1 = config_list.get(i);
				Set<Statement>enable_statements_config1 = config1.getEnable_Statements();
				Set<Statement>disable_statements_config1 = config1.getDisable_Statements();
				for(int j = i+1;j < config_list.size();j++){
					ConfigurationOption config2 = config_list.get(j);
					Set<Statement>enable_statements_config2 = config2.getEnable_Statements();
					Set<Statement>disable_statements_config2 = config2.getDisable_Statements();
					/*
					 *  区域包含 config1的影响区域是否 包含 config2的 expression
					 *  如果有 这是 imples
					 */
					boolean config1_contains_config2 = isContains(enable_statements_config1,config2.getExpression());
					boolean config2_contains_config1 = isContains(enable_statements_config2,config1.getExpression());
					boolean isContain_relation = config1_contains_config2||config2_contains_config1;
					if(isContain_relation){
						if(config1_contains_config2){
							config1.addConfigurationRelation(config2, ConfigRelation.CONTAINS);
						}else{
							config2.addConfigurationRelation(config1, ConfigRelation.CONTAINS);
						}
						continue;
					}
					/**
					 * mutually excluded
					 */
					boolean config1_excluded_config2 = isExclude(disable_statements_config1,config2.getExpression());
					boolean config2_excluded_config1 = isExclude(disable_statements_config2,config1.getExpression());
					boolean isMutuallyExclude = config1_excluded_config2||config2_excluded_config1;
					if(isMutuallyExclude){
						config1.addConfigurationRelation(config2, ConfigRelation.EXECLUDE);
						config2.addConfigurationRelation(config1, ConfigRelation.EXECLUDE);
					}					
				}
			}
		}
	}
	
	
	private boolean isExclude(Set<Statement> disable_Statements,
			Expression condition) {
		// TODO Auto-generated method stub
		if(disable_Statements.isEmpty()){
			return false;
		}
		int condstart = condition.getStartPosition();
		int condend = condstart+condition.getLength();
		for(ASTNode node:disable_Statements){
			int startposition = node.getStartPosition();
			int endposition = node.getLength()+startposition;
			if(startposition<=condstart && endposition>=condend){
				return true;
			}
		}
		return false;
	}
	private boolean isContains(Set<Statement> region,ASTNode condition){
		int condstart = condition.getStartPosition();
		int condend = condstart+condition.getLength();
		for(ASTNode node:region){
			int startposition = node.getStartPosition();
			int endposition = node.getLength()+startposition;
			if(startposition<=condstart && endposition>=condend){
				return true;
			}
		}
		return false;
	}
	
	
	/**
	 * collect the data flow information and extend the controllable region for 
	 * 
	 */
	private void collectdatadependencyincofiguration() {
		// TODO Auto-generated method stub
		//
		for(Map.Entry<LElement,Set<ConfigurationOption>>entry:method_configurations.entrySet()){
			LElement method = entry.getKey();
			Set<ConfigurationOption> configuraitons = entry.getValue();
			
			for(ConfigurationOption option:configuraitons){
				Expression conditionalExp = option.getExpression();
				
				// get the variable and field used
				VariableFieldVisitor varvisitor = new VariableFieldVisitor();
				conditionalExp.accept(varvisitor);
				Set<IVariableBinding>variableBindings = varvisitor.getVariableBindings();
				Set<LElement> variableelemnts = new HashSet<LElement>();
				if(variableBindings!=null &&
						!variableBindings.isEmpty()){
					
					// extend the bindings with their usage and references
					for(IVariableBinding binding:variableBindings){
						LElement element = LElementFactory.getElement(binding);
						if(element!=null)
							variableelemnts.add(element);
					}
					
					// check all elements
					for(LElement element:variableelemnts){
						ASTNode node = element.getASTNode();
						option.addAffected_ASTNode(node);
					}
				}
			}
			
			
		}
		
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
	 * create the control flow graph for all methods declared
	 */
	protected void processMethodCongBuilder(){
		for(LElement method:this.method_elements){
			MethodDeclaration methoddecl_astnode = (MethodDeclaration)method.getASTNode();
			CongVisitor confvisitor = new CongVisitor(module,methoddecl_astnode);
			methoddecl_astnode.accept(confvisitor);
			Set<ConfigurationOption> configurationoption_set = confvisitor.getConfigurationOptions();
			if(!configurationoption_set.isEmpty())
				method_configurations.put(method, configurationoption_set);
		}
	}
}
