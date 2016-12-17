package loongpluginfmrtool.module.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.Statement;

public class ConfigurationCondition {
	
	private List<Expression> conditions = new LinkedList<Expression>();
	private ConfigurationOption option;
	private Map<Expression,Set<Statement>>confcond_select_statement = new HashMap<Expression,Set<Statement>>();
	private Map<Expression,Set<Statement>>confcond_unselect_statement = new HashMap<Expression,Set<Statement>>();
	
	public ConfigurationCondition(ConfigurationOption poption,Expression pconfigOption){
		option = poption;
		conditions.add(pconfigOption);
	}
	public void addConditionExpression(Expression pconfigOption) {
		// TODO Auto-generated method stub
		conditions.add(pconfigOption);
	}
	
	/**
	 * add a conditional expression to its enable statements mapping
	 * @param exp
	 * @param statements
	 */
	public void addEnableStatement(Expression exp,Set<Statement> statements){
		if(confcond_select_statement.containsKey(exp)){
			Set<Statement> curr_statements = confcond_select_statement.get(exp);
			curr_statements.addAll(statements);
			confcond_select_statement.put(exp,curr_statements);
		}else{
			confcond_select_statement.put(exp,statements);
		}
	}
	public void addEnableStatement(Expression condition, Statement element) {
		// TODO Auto-generated method stub
		if(confcond_select_statement.containsKey(condition)){
			Set<Statement> curr_statements = confcond_select_statement.get(condition);
			curr_statements.add(element);
			confcond_select_statement.put(condition,curr_statements);
		}else{
			Set<Statement> curr_statements = new HashSet<Statement>();
			curr_statements.add(element);
			confcond_select_statement.put(condition,curr_statements);
		}
	}
	
	/**
	 * add a conditional expression to its unable statements mapping
	 * @param exp
	 * @param statements
	 */
	public void addDisabledStatement(Expression exp,Set<Statement> statements){
		if(confcond_unselect_statement.containsKey(exp)){
			Set<Statement> curr_statements = confcond_unselect_statement.get(exp);
			curr_statements.addAll(statements);
			confcond_unselect_statement.put(exp,statements);
		}else{
			confcond_unselect_statement.put(exp,statements);
		}
		
	}
	public void addDisabledStatement(Expression exp,Statement statement){
		if(confcond_unselect_statement.containsKey(exp)){
			Set<Statement> curr_statements = confcond_unselect_statement.get(exp);
			curr_statements.add(statement);
			confcond_unselect_statement.put(exp,curr_statements);
		}else{
			Set<Statement> curr_statements = new HashSet<Statement>();
			curr_statements.add(statement);
			confcond_unselect_statement.put(exp,curr_statements);
		}
		
	}
	
	
	
	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		if(obj instanceof ConfigurationCondition){
			ConfigurationCondition obj_concod = (ConfigurationCondition)obj;
			List<Expression> obj_conditions = obj_concod.getConditions();
			if(this.conditions.size()==obj_conditions.size()){
				for(Expression exp:conditions){
					if(!obj_conditions.contains(exp)){
						return false;
					}
				}
				return true;
			}else{
				return false;
			}
		}else
			return false;
	}
	private List<Expression> getConditions() {
		// TODO Auto-generated method stub
		return conditions;
	}
	
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		String conditionstr = "conditions:{";
		for(Expression exp:conditions){
			conditionstr+=exp.toString();
			conditionstr+=",";
		}
		if(conditions.size()>=1){
			conditionstr = conditionstr.substring(0,conditionstr.length()-1);
		}
		conditionstr+="}";
		return conditionstr;
	}
	public Expression getFirstExpression() {
		// TODO Auto-generated method stub
		return conditions.get(0);
	}
	public boolean containsCondition(Expression condition) {
		// TODO Auto-generated method stub
		return conditions.contains(condition);
	}
	
	
	public Set<Statement> getEnableStatement(Expression condition) {
		// TODO Auto-generated method stub
		assert confcond_select_statement.containsKey(condition);
		return confcond_select_statement.get(condition);
	}
	public Set<Statement> getDisabledStatement(Expression condition) {
		// TODO Auto-generated method stub
		assert confcond_unselect_statement.containsKey(condition);
		return confcond_unselect_statement.get(condition);
	}
	
	
	

}
