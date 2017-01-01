package loongpluginfmrtool.module.model.configuration;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import loongpluginfmrtool.module.model.module.Module;
import loongpluginfmrtool.util.ASTNodeHelper;
import loongpluginfmrtool.util.ASTVisitorHelper;
import loongpluginfmrtool.util.MethodBindingFinder;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.DoStatement;
import org.eclipse.jdt.core.dom.EnhancedForStatement;
import org.eclipse.jdt.core.dom.EnumConstantDeclaration;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.SwitchStatement;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclaration;
import org.eclipse.jdt.core.dom.WhileStatement;

import loongplugin.source.database.model.LElement;
import loongplugin.source.database.model.LFlyweightElementFactory;

public class CongVisitor extends ASTVisitor{
	
	/**
	 * Configuration Visitor inherit from ASTVisitor
	 */
	private Set<ConfigurationOption> configurationOptions = new HashSet<ConfigurationOption>();
	
	private Module associatemdule;
	private MethodDeclaration methoddecl;
	private LFlyweightElementFactory aLElementFactory;
	private Map<ASTNode,ConfigurationOption>configuration_map_ASTNodesOption = new HashMap<ASTNode,ConfigurationOption>();
	
	
	public CongVisitor(Module mainmodule,MethodDeclaration pmethoddecl,LFlyweightElementFactory pLElementFactory){
		associatemdule = mainmodule;
		methoddecl = pmethoddecl;
		aLElementFactory = pLElementFactory;
	}
	
	public boolean containsModuleJump(Statement statement,Module currmodule){
		boolean containsjump = false;
		MethodBindingFinder mfinder = new MethodBindingFinder();
		statement.accept(mfinder);
		Set<LElement> allmethods = currmodule.getallMethods();
		Set<IMethodBinding> methodbindings = mfinder.getMethodBinding();
		for(IMethodBinding bind:methodbindings){
			LElement element = aLElementFactory.getElement(bind);
			if(element!=null){
				if(!allmethods.contains(element)){
					return true;
				}
			}
		}
		
		return containsjump;
	}
	public boolean visit(IfStatement node) {
		IfStatement if_node = (IfStatement)node;
		Expression condition = if_node.getExpression();
		Statement then_statement = if_node.getThenStatement();
		Statement else_statement = if_node.getElseStatement();
		
		// check whether it is a valid configuration
		if(then_statement!=null){
			if(else_statement==null){
				if(!containsModuleJump(then_statement,associatemdule)){
					return false;
				}
			}else{
				if(containsModuleJump(then_statement,associatemdule)||
						containsModuleJump(else_statement,associatemdule)){
					;
				}else{
					return false;
				}
			}
		}else{
			return false;
		}
		
		Set<LElement> elements = ConfigurationEntryFinder.getConfigurations(if_node,aLElementFactory);
		for(LElement element:elements){
			
			ConfigurationOption option = createConfigurationOption(element,condition,associatemdule,methoddecl);
			assert option!=null;
			if(then_statement!=null){
				//configurationOptions.add(option);
				option.addEnable_Statements(condition,then_statement);
			}
			if(else_statement!=null){
				//configurationOptions.add(option);
				option.addDisable_Statements(condition,else_statement);
			}else{
				// in the if statement there is a return, then the code rests are else
				boolean hasreturn = ASTVisitorHelper.containsReturn(then_statement);
				if(hasreturn){
					ASTNode parentnode = node.getParent();
					assert parentnode instanceof Statement;
					Statement parentstatement = (Statement)parentnode;
					Set<Statement> state_after_if_node = ASTNodeHelper.getStatementAfter(if_node,parentstatement);
					state_after_if_node.remove(then_statement);
					for(Statement state_after:state_after_if_node){
						option.addDisable_Statements(condition,state_after);
					}
				}
				
			}
		}
		return true;
    }

    public boolean visit(WhileStatement node) {
    	WhileStatement while_statement = (WhileStatement)node;
    	Expression condition = while_statement.getExpression();
    	Statement body = while_statement.getBody();
    	
    	// check whether it is a valid configuration
    	if(body!=null){
    		if(!containsModuleJump(body,associatemdule)){
    			return false;
    			
    		}
    	}else{
    		return false;
    	}
    	
    	
    	Set<LElement> elements = ConfigurationEntryFinder.getConfigurations(condition,aLElementFactory);
		for(LElement element:elements){
			ConfigurationOption option = createConfigurationOption(element,condition,associatemdule,methoddecl);
			if(body!=null){
	    		//configurationOptions.add(option);
	    		option.addEnable_Statements(condition,body);
	    	}
		}
    	return true;
    }

    
    @Override 
    public boolean visit(DoStatement node) {
    	DoStatement do_statement = (DoStatement) node;
    	Expression condition = do_statement.getExpression();
    	Statement body = do_statement.getBody();
    	
    	// check whether it is a valid configuration
    	if(body!=null){
    		if(!containsModuleJump(body,associatemdule)){
    			return false;
    			
    		}
    	}else{
    		return false;
    	}
    	
    	Set<LElement> elements = ConfigurationEntryFinder.getConfigurations(condition,aLElementFactory);
		for(LElement element:elements){
	    	ConfigurationOption option = createConfigurationOption(element,condition,associatemdule,methoddecl);
	    	if(body!=null){
	    		//configurationOptions.add(option);
	    		option.addEnable_Statements(condition,body);
	    	}
		}
        return true;
	}
    
	@Override 
	public boolean visit(EnhancedForStatement node) {
		EnhancedForStatement enhance_statement = (EnhancedForStatement)node;
		Expression condition = enhance_statement.getExpression();
		Statement body = enhance_statement.getBody();
		
		// check whether it is a valid configuration
    	if(body!=null){
    		if(!containsModuleJump(body,associatemdule)){
    			return false;
    			
    		}
    	}else{
    		return false;
    	}
    	
		
		Set<LElement> elements = ConfigurationEntryFinder.getConfigurations(condition,aLElementFactory);
		for(LElement element:elements){
	    	ConfigurationOption option = createConfigurationOption(element,condition,associatemdule,methoddecl);
			if(body!=null){
				//configurationOptions.add(option);
	    		option.addEnable_Statements(condition,body);
	    	}
		}
        return true;
	}
	
	@Override 
	public boolean visit(ForStatement node) {
		ForStatement for_statement  = (ForStatement)node;
		Expression condition = for_statement.getExpression();
		Statement body = for_statement.getBody();
		
		// check whether it is a valid configuration
    	if(body!=null){
    		if(!containsModuleJump(body,associatemdule)){
    			return false;
    			
    		}
    	}else{
    		return false;
    	}
    	
    	
		Set<LElement> elements = ConfigurationEntryFinder.getConfigurations(condition,aLElementFactory);
		for(LElement element:elements){
	    	ConfigurationOption option = createConfigurationOption(element,condition,associatemdule,methoddecl);
			if(body!=null){
				//configurationOptions.add(option);
	    		option.addEnable_Statements(condition,body);
			}
		}
        return true;
	}
	
	/** TODO: handle <i>continue</i>
	 * TODO: each case 
	 * */
	@Override 
	public boolean visit(SwitchStatement node) {
		SwitchStatement switch_node = (SwitchStatement)node;
		Expression condition = switch_node.getExpression();
		boolean constainsjump = false;
		List<Statement> sub_statements = switch_node.statements();
    	if(sub_statements!=null){
    		if(!sub_statements.isEmpty()){
    			for(Statement casestate:sub_statements){
    				if(containsModuleJump(casestate,associatemdule)){
    					constainsjump = true;
    					break;
    				}
    			}
    		}
    	}
    	if(constainsjump==false)
    		return false;
		Set<LElement> elements = ConfigurationEntryFinder.getConfigurations(condition,aLElementFactory);
		for(LElement element:elements){
	    	ConfigurationOption option = createConfigurationOption(element,condition,associatemdule,methoddecl);
	    	Set<Statement>sub_statementset = new HashSet<Statement>(sub_statements);
	    	option.addEnable_Statements(condition,sub_statementset);
		}
		return true;
	}

	public Set<ConfigurationOption> getConfigurationOptions() {
		// TODO Auto-generated method stub
		return configurationOptions;
	}

	
	public ConfigurationOption createConfigurationOption(LElement element,Expression condition,Module associatemdule,MethodDeclaration methoddecl){
		ConfigurationOption option = null;
		ASTNode node = element.getASTNode();
		if(node instanceof VariableDeclaration){
			VariableDeclaration variable = (VariableDeclaration)node;
			if(configuration_map_ASTNodesOption.containsKey(node)){
				return configuration_map_ASTNodesOption.get(node);
			}else{
				option = new ConfigurationOption(variable,element,condition,associatemdule,methoddecl);
				configuration_map_ASTNodesOption.put(node, option);
				configurationOptions.add(option);
			}
		}else if(node instanceof FieldDeclaration){
			FieldDeclaration field = (FieldDeclaration)node;
			if(configuration_map_ASTNodesOption.containsKey(node)){
				return configuration_map_ASTNodesOption.get(node);
			}else{
				option = new ConfigurationOption(field,element,condition,associatemdule,methoddecl);
				configuration_map_ASTNodesOption.put(node, option);
				configurationOptions.add(option);
			}
		}else if(node instanceof EnumDeclaration){
			EnumDeclaration enumdecl = (EnumDeclaration)node;
			if(configuration_map_ASTNodesOption.containsKey(node)){
				return configuration_map_ASTNodesOption.get(node);
			}else{
				option = new ConfigurationOption(enumdecl,element,condition,associatemdule,methoddecl);
				configuration_map_ASTNodesOption.put(node, option);
				configurationOptions.add(option);
			}
		}else if(node instanceof MethodDeclaration){
			MethodDeclaration methdecl = (MethodDeclaration)node;
			if(configuration_map_ASTNodesOption.containsKey(node)){
				return configuration_map_ASTNodesOption.get(node);
			}else{
				option = new ConfigurationOption(methdecl,element,condition,associatemdule);
				configuration_map_ASTNodesOption.put(node, option);
				configurationOptions.add(option);
			}
		}else if(node instanceof TypeDeclaration){
			TypeDeclaration typedecl = (TypeDeclaration)node;
			if(configuration_map_ASTNodesOption.containsKey(node)){
				return configuration_map_ASTNodesOption.get(node);
			}else{
				option = new ConfigurationOption(typedecl,element,condition,associatemdule,methoddecl);
				configuration_map_ASTNodesOption.put(node, option);
				configurationOptions.add(option);
			}
		}else if(node instanceof EnumConstantDeclaration){
			EnumConstantDeclaration enumconstdecl = (EnumConstantDeclaration)node;
			if(configuration_map_ASTNodesOption.containsKey(node)){
				return configuration_map_ASTNodesOption.get(node);
			}else{
				option = new ConfigurationOption(enumconstdecl,element,condition,associatemdule,methoddecl);
				configuration_map_ASTNodesOption.put(node, option);
				configurationOptions.add(option);
			}
		}else{
			try {
				throw new Exception("cannot find the type for node:"+node.toString());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return option;
	}
}
