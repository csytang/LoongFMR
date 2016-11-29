package loongpluginfmrtool.module.builder;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.MethodRef;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.SuperConstructorInvocation;
import org.eclipse.jdt.core.dom.SuperMethodInvocation;
import org.eclipse.jdt.core.dom.SuperMethodReference;
import org.eclipse.jdt.core.dom.Type;

import loongplugin.source.database.model.LElement;
import loongplugin.source.database.model.LFlyweightElementFactory;
import loongpluginfmrtool.module.model.ConfigRelation;
import loongpluginfmrtool.module.model.ConfigurationOption;
import loongpluginfmrtool.module.model.Module;

public class ExternalConfBuilder {
	private Module module;
	private ModuleBuilder modulebuilder = null;
	private Map<LElement,Set<ConfigurationOption>> method_configurations = new HashMap<LElement,Set<ConfigurationOption>>();
	private Map<ConfigurationOption,LElement>configuration_method = new HashMap<ConfigurationOption,LElement>();
	private List<LElement>allmethods = new LinkedList<LElement>();
	
	private Map<ConfigurationOption,List<Module>>cong_link_modules = new HashMap<ConfigurationOption,List<Module>>();
	private Map<ConfigurationOption,List<Module>>cong_unlink_modules = new HashMap<ConfigurationOption,List<Module>>();
	private LFlyweightElementFactory LElementFactory;
	private boolean debug = true;
	
	public ExternalConfBuilder(Module pmodule,LFlyweightElementFactory pLElementFactory){
		this.module = pmodule;
		this.modulebuilder = ModuleBuilder.instance;
		this.LElementFactory = pLElementFactory;
		this.method_configurations = pmodule.getMethod_To_Configuration();
		this.configuration_method = pmodule.getConfiguration_To_Method();
		this.allmethods = new LinkedList<LElement>(this.module.getallMethods()); 
	}
	public void parse(){
		// process basic module linking information
		for(LElement method:allmethods){
			if(method_configurations.containsKey(method)){
				Set<ConfigurationOption> configurations = method_configurations.get(method);
				for(ConfigurationOption option:configurations){
					Set<Statement> enablestatements = option.getEnable_Statements();
					directToOtherModules(enablestatements,option,true);
					
					Set<Statement> disablestatements = option.getDisable_Statements();
					if(!disablestatements.isEmpty())
						directToOtherModules(disablestatements,option,false);
				}
			}
		}
		
		
		// extracting variability
		// Enable mod
		for(Map.Entry<ConfigurationOption,List<Module>>entry:cong_link_modules.entrySet()){
			ConfigurationOption option = entry.getKey();
			List<Module>related_modules = entry.getValue();
			for(Module module:related_modules){
				Set<ASTNode> remote_affected = module.getExternalEnableConfigurationControl(option);
				if(remote_affected.isEmpty())
					continue;
				Set<ConfigurationOption> remote_configurations = module.getAllConfigurationOptions();
				for(ConfigurationOption remote_option:remote_configurations){
					Expression remotecondition = remote_option.getExpression();
					if(isContains(remote_affected,remotecondition)){
						option.addConfigurationRelation(remote_option, ConfigRelation.CONTAINS);
					}
				}
			}
			
		}
		
		// Disable mod
		for(Map.Entry<ConfigurationOption,List<Module>>entry:cong_unlink_modules.entrySet()){
			ConfigurationOption option = entry.getKey();
			List<Module>related_modules = entry.getValue();
			for(Module module:related_modules){
				Set<ASTNode> remote_affected = module.getExternalEnableConfigurationControl(option);
				if(remote_affected.isEmpty())
					continue;
				Set<ConfigurationOption> remote_configurations = module.getAllConfigurationOptions();
				for(ConfigurationOption remote_option:remote_configurations){
					Expression remotecondition = remote_option.getExpression();
					if(isContains(remote_affected,remotecondition)){
						option.addConfigurationRelation(remote_option, ConfigRelation.EXECLUDE);
					}
				}
			}
			
		}
		
	}
	
	private boolean isContains(Set<ASTNode> region,ASTNode condition){
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
	
	protected void directToOtherModules(Set<Statement> statements,ConfigurationOption pconfig,boolean pisEnable){
		MethodInvocationVisitor visitor = new MethodInvocationVisitor(pconfig,pisEnable);
		for(Statement statement:statements){
			statement.accept(visitor);
		}
	}
	
	class MethodInvocationVisitor extends ASTVisitor{
		private ConfigurationOption config;
		private boolean isEnable = true;
		public MethodInvocationVisitor(ConfigurationOption pconfig,boolean pisEnable){
			config = pconfig;
			isEnable = pisEnable;
		}
		
		private void addToEnableConfigurationControl(ASTNode node,Module remote_module){
			
			if(cong_link_modules.containsKey(config)){
				List<Module> modules = cong_link_modules.get(config);
				modules.add(remote_module);
				remote_module.addExternalEnableConfigurationControl(config, node);
				cong_link_modules.put(config, modules);
			}else{
				List<Module> modules = new LinkedList<Module>();
				modules.add(remote_module);
				remote_module.addExternalEnableConfigurationControl(config, node);
				cong_link_modules.put(config, modules);
			}
		}
		private void addToDisableConfigurationControl(ASTNode node,Module remote_module){
			if(cong_unlink_modules.containsKey(config)){
				List<Module> modules = cong_unlink_modules.get(config);
				
				modules.add(remote_module);
				remote_module.addExternalDisableConfigurationControl(config, node);
				cong_unlink_modules.put(config, modules);
				
			}else{
				List<Module> modules = new LinkedList<Module>();
				modules.add(remote_module);
				remote_module.addExternalDisableConfigurationControl(config, node);
				cong_unlink_modules.put(config, modules);
			}
		}
		
		@Override
		public boolean visit(ClassInstanceCreation node) {
			// TODO Auto-generated method stub
			Type instance_type = node.getType();
			ITypeBinding typebinding = instance_type.resolveBinding();
			if(typebinding!=null){
				LElement declelement = LElementFactory.getElement(typebinding);
				if(declelement!=null){
					CompilationUnit compilation_unit = declelement.getCompilationUnit();
					LElement compilation_unit_element = LElementFactory.getElement(compilation_unit);
					IMethodBinding method_binding = node.resolveConstructorBinding();
					LElement method_element = LElementFactory.getElement(method_binding);
					if(method_element!=null){
						Module remote_module = ModuleBuilder.instance.getModuleByLElement(compilation_unit_element);
						if(isEnable){
							addToEnableConfigurationControl(method_element.getASTNode(),remote_module);
						}else{
							addToDisableConfigurationControl(method_element.getASTNode(),remote_module);
						}
					}
				}
			}
			return super.visit(node);
		}


		@Override
		public boolean visit(MethodInvocation node) {
			// TODO Auto-generated method stub
			IMethodBinding method = node.resolveMethodBinding();
			if(method!=null){
				LElement declelement = LElementFactory.getElement(method);
				if(declelement!=null){
					CompilationUnit compilation_unit = declelement.getCompilationUnit();
					LElement compilation_unit_element = LElementFactory.getElement(compilation_unit);
					if(compilation_unit_element!=null){
						Module remote_module = ModuleBuilder.instance.getModuleByLElement(compilation_unit_element);
						if(isEnable){
							addToEnableConfigurationControl(declelement.getASTNode(),remote_module);
						}else{
							addToDisableConfigurationControl(declelement.getASTNode(),remote_module);
						}
					}
				}
			}
			return super.visit(node);
		}

		@Override
		public boolean visit(SuperConstructorInvocation node) {
			// TODO Auto-generated method stub
			IMethodBinding method = node.resolveConstructorBinding();
			if(method!=null){
				LElement declelement = LElementFactory.getElement(method);
				if(declelement!=null&& method!=null){
					CompilationUnit compilation_unit = declelement.getCompilationUnit();
					LElement compilation_unit_element = LElementFactory.getElement(compilation_unit);
				
					Module remote_module = ModuleBuilder.instance.getModuleByLElement(compilation_unit_element);
					
					if(isEnable){
						addToEnableConfigurationControl(declelement.getASTNode(),remote_module);
					}else{
						addToDisableConfigurationControl(declelement.getASTNode(),remote_module);
					}
				}
			}
			return super.visit(node);
		}

		@Override
		public boolean visit(SuperMethodInvocation node) {
			// TODO Auto-generated method stub
			IMethodBinding method = node.resolveMethodBinding();
			if(method!=null){
				LElement declelement = LElementFactory.getElement(method);
				if(declelement!=null){
					CompilationUnit compilation_unit = declelement.getCompilationUnit();
					LElement compilation_unit_element = LElementFactory.getElement(compilation_unit);
					Module remote_module = ModuleBuilder.instance.getModuleByLElement(compilation_unit_element);
					
					if(isEnable){
						addToEnableConfigurationControl(declelement.getASTNode(),remote_module);
					}else{
						addToDisableConfigurationControl(declelement.getASTNode(),remote_module);
					}
				}
			}
			return super.visit(node);
		}
		
	}

}
