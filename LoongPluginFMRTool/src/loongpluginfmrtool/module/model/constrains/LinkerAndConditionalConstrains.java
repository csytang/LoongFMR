package loongpluginfmrtool.module.model.constrains;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.Statement;

import loongplugin.source.database.ApplicationObserver;
import loongplugin.source.database.model.LElement;
import loongplugin.source.database.model.LFlyweightElementFactory;
import loongplugin.source.database.model.LRelation;
import loongpluginfmrtool.module.model.configuration.ConfigurationCondition;
import loongpluginfmrtool.module.model.configuration.ConfigurationEntry;
import loongpluginfmrtool.module.model.configuration.ConfigurationOption;
import loongpluginfmrtool.module.model.module.Module;

public class LinkerAndConditionalConstrains {
	private Module amodule;
	private Map<LElement,Set<ConfigurationOption>> amethod_configuration;
	private Map<Expression,Set<LElement>> enableconstrains = new HashMap<Expression,Set<LElement>>();
	private Map<Expression,Set<LElement>> unableconstrains = new HashMap<Expression,Set<LElement>>();
	private LFlyweightElementFactory alElementfactory;
	
	
	protected ApplicationObserver AOB;
	/**
	 * invoked in {@link Module#extractConstrains()}
	 * 
	 * @param pmodule
	 * @param pmethod_configuration
	 */
	public LinkerAndConditionalConstrains(Module pmodule,LFlyweightElementFactory plElementfactory,Map<LElement,Set<ConfigurationOption>> pmethod_configuration){
		this.amodule = pmodule;
		this.amethod_configuration = pmethod_configuration;
		this.alElementfactory = plElementfactory;
		this.AOB = ApplicationObserver.getInstance();
		findConditionalLinkerConstrains(amodule,amethod_configuration);
	}
	public void findConditionalLinkerConstrains(Module module, Map<LElement, Set<ConfigurationOption>> method_configuration) {
		/**
		 * defines the def-use chain constrains in module,
		 * however this should only be considered into configuration option(op)
		 * 
		 * 
		 * op: ON ----> block 1
		 * op: OFF -----> block 2
		 * 
		 * def-chain to:
		 * (1) op
		 * (2) block 1
		 * (3) block 2
		 */
		
		
		// run though every configuration option
		for(Map.Entry<LElement, Set<ConfigurationOption>>entry:method_configuration.entrySet()){
			LElement element = entry.getKey();
			Set<ConfigurationOption> optionset  = entry.getValue();
			for(ConfigurationOption option:optionset){
				// first find the option entry point;
				ConfigurationEntry configentry = option.getConfigurationEntry();
				LElement configelment = configentry.getLElement();
				
				// # of conditions in a raw sense;
				ConfigurationCondition conficondi = option.getConfigurationCondition();
				Map<Expression,Set<Statement>> confcond_select_statement = conficondi.getAllEnableStatement();
				Map<Expression,Set<Statement>> confcond_unselect_statement = conficondi.getAllDisabledStatement();

				// check all configuration enable selection statements
				for(Expression exp:confcond_select_statement.keySet()){
					Set<Statement> enabledstatements = confcond_select_statement.get(exp);
					for(Statement statement:enabledstatements){
						LElement statement_element = this.alElementfactory.getElement(statement);
						// all possible relation 
						Set<LRelation> validTransponseRelations = LRelation.getAllRelations(statement_element.getCategory(), true, false);
						validTransponseRelations.addAll(LRelation.getAllRelations(element.getCategory(), true, true));
						
						for(LRelation validrelation:validTransponseRelations){
							Set<LElement> backwardElements = AOB.getRange(element,validrelation);
							for (LElement backwardElement : backwardElements) {
								addconstraints(enableconstrains,exp,backwardElement);
							}
						}
					
					}
				}
				
				// check all configuration disable selection statements
				for(Expression exp:confcond_unselect_statement.keySet()){
					Set<Statement> disablestatements = confcond_unselect_statement.get(exp);
					for(Statement statement:disablestatements){
						LElement statement_element = this.alElementfactory.getElement(statement);
						// all possible relation 
						Set<LRelation> validTransponseRelations = LRelation.getAllRelations(statement_element.getCategory(), true, false);
						validTransponseRelations.addAll(LRelation.getAllRelations(element.getCategory(), true, true));
						
						for(LRelation validrelation:validTransponseRelations){
							Set<LElement> backwardElements = AOB.getRange(element,validrelation);
							for (LElement backwardElement : backwardElements) {
								addconstraints(unableconstrains,exp,backwardElement);
							}
						}
					
					}
				}
				
				
			}
		}
		
		
	}
	
	protected void addconstraints(Map<Expression,Set<LElement>> constrains,Expression exp, LElement element){
		if(constrains.containsKey(exp)){
			Set<LElement> elementset = constrains.get(exp);
			elementset.add(element);
			constrains.put(exp, elementset);
		}else{
			Set<LElement> elementset = new HashSet<LElement>();
			elementset.add(element);
			constrains.put(exp, elementset);
		}
	}
}
