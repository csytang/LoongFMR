package loongpluginfmrtool.module.builder;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import loongpluginfmrtool.module.model.ConfigurationOption;
import loongpluginfmrtool.module.model.Module;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.DoStatement;
import org.eclipse.jdt.core.dom.EnhancedForStatement;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.SwitchStatement;
import org.eclipse.jdt.core.dom.WhileStatement;

public class CongVisitor extends ASTVisitor{

	private Set<ConfigurationOption> configurationOptions = new HashSet<ConfigurationOption>();
	
	private Module associatemdule;
	private MethodDeclaration methoddecl;
	public CongVisitor(Module mainmodule,MethodDeclaration pmethoddecl){
		associatemdule = mainmodule;
		methoddecl = pmethoddecl;
	}
	
	public boolean visit(IfStatement node) {
		IfStatement if_node = (IfStatement)node;
		Expression condition = if_node.getExpression();
		Statement then_statement = if_node.getThenStatement();
		Statement else_statement = if_node.getElseStatement();
		ConfigurationOption option = new ConfigurationOption(condition,associatemdule,methoddecl);
		if(then_statement!=null){
			configurationOptions.add(option);
			option.addEnable_Statements(then_statement);
		}
		if(else_statement!=null){
			configurationOptions.add(option);
			option.addDisable_Statements(else_statement);
		}
		
		return true;
    }

    public boolean visit(WhileStatement node) {
    	WhileStatement while_statement = (WhileStatement)node;
    	Expression condition = while_statement.getExpression();
    	Statement body = while_statement.getBody();
    	ConfigurationOption option = new ConfigurationOption(condition,associatemdule,methoddecl);
    	if(body!=null){
    		configurationOptions.add(option);
    		option.addEnable_Statements(body);
    	}
    	return true;
    }

    
    @Override 
    public boolean visit(DoStatement node) {
    	DoStatement do_statement = (DoStatement) node;
    	Expression condition = do_statement.getExpression();
    	Statement body = do_statement.getBody();
    	ConfigurationOption option = new ConfigurationOption(condition,associatemdule,methoddecl);
    	if(body!=null){
    		configurationOptions.add(option);
    		option.addEnable_Statements(body);
    	}
    	
        return true;
	}
    
	@Override 
	public boolean visit(EnhancedForStatement node) {
		EnhancedForStatement enhance_statement = (EnhancedForStatement)node;
		Expression condition = enhance_statement.getExpression();
		Statement body = enhance_statement.getBody();
    	ConfigurationOption option = new ConfigurationOption(condition,associatemdule,methoddecl);
		if(body!=null){
			configurationOptions.add(option);
    		option.addEnable_Statements(body);
    	}
        return true;
	}
	
	@Override 
	public boolean visit(ForStatement node) {
		ForStatement for_statement  = (ForStatement)node;
		Expression condition = for_statement.getExpression();
		Statement body = for_statement.getBody();
    	ConfigurationOption option = new ConfigurationOption(condition,associatemdule,methoddecl);
		if(body!=null){
			configurationOptions.add(option);
    		option.addEnable_Statements(body);
		}
        return true;
	}
	
	/** TODO: handle <i>continue</i>*/
	@Override 
	public boolean visit(SwitchStatement node) {
		SwitchStatement switch_node = (SwitchStatement)node;
		Expression condition = switch_node.getExpression();
    	ConfigurationOption option = new ConfigurationOption(condition,associatemdule,methoddecl);
    	List<Statement> sub_statements = switch_node.statements();
    	if(sub_statements!=null){
    		if(!sub_statements.isEmpty()){
    			configurationOptions.add(option);
    			for(Statement sub_statement:sub_statements){
    	    		option.addEnable_Statements(sub_statement);
    			}
    		}
    	}
		return true;
	}

	public Set<ConfigurationOption> getConfigurationOptions() {
		// TODO Auto-generated method stub
		return configurationOptions;
	}

	
	
}
