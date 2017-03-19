package loongpluginfmrtool.module.model.configuration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.EnumConstantDeclaration;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclaration;

import loongplugin.source.database.model.LElement;
import loongplugin.source.database.model.LFlyweightElementFactory;
import loongpluginfmrtool.module.model.module.Module;
import loongpluginfmrtool.module.model.module.ModuleComponent;

public class ConfigurationOption extends ModuleComponent{
	
	/**
	 * Only one entry is allowed, but mutiple configuration conditions
	 */
	private Set<ConfigurationRelationLink> confg_relationlik;
	private Module associatedmodule;
	private CompilationUnit unit;
	private MethodDeclaration methoddecl;
	private ConfigurationEntry configentry;
	private ConfigurationCondition configurationcondition;
	
	public ConfigurationOption(Module passociatedmodule,MethodDeclaration pmethoddecl){
		super(passociatedmodule);
		unit = passociatedmodule.getCompilationUnit();
		methoddecl = pmethoddecl;
	}
	
	public MethodDeclaration getMethod(){
		return methoddecl;
	}
	
	
	
	
	/**
	 * 
	 * @param variable  --- local variable based configuration
	 * @param pconfigOption
	 * @param passociatedmodule
	 * @param pmethoddecl
	 */
	public ConfigurationOption(VariableDeclaration variable,LElement element,Expression pconfigOption,Module passociatedmodule,MethodDeclaration pmethoddecl){
		super(passociatedmodule);
		this.configentry = new ConfigurationEntry(this,element);
		this.configurationcondition= new ConfigurationCondition(this,pconfigOption);
		this.associatedmodule = passociatedmodule;
		this.confg_relationlik = new HashSet<ConfigurationRelationLink>();
		unit = passociatedmodule.getCompilationUnit();
		methoddecl = pmethoddecl;
	}
	
	/**
	 * 
	 * @param field --------------field based configuration
	 * @param pconfigOption
	 * @param passociatedmodule
	 * @param pmethoddecl
	 */
	public ConfigurationOption(FieldDeclaration field,LElement element,Expression pconfigOption,Module passociatedmodule,MethodDeclaration pmethoddecl){
		super(passociatedmodule);
		this.configentry = new ConfigurationEntry(this,element);
		this.configurationcondition= new ConfigurationCondition(this,pconfigOption);
		this.associatedmodule = passociatedmodule;
		this.confg_relationlik = new HashSet<ConfigurationRelationLink>();
		unit = passociatedmodule.getCompilationUnit();
		methoddecl = pmethoddecl;
	}
	
	/**
	 * 
	 * @param enumvalue   ------------ enum based configuration
	 * @param pconfigOption
	 * @param passociatedmodule
	 * @param pmethoddecl
	 */
	public ConfigurationOption(EnumDeclaration enumvalue,LElement element,Expression pconfigOption,Module passociatedmodule,MethodDeclaration pmethoddecl){
		super(passociatedmodule);
		this.configentry = new ConfigurationEntry(this,element);
		this.configurationcondition= new ConfigurationCondition(this,pconfigOption);
		this.associatedmodule = passociatedmodule;
		this.confg_relationlik = new HashSet<ConfigurationRelationLink>();
		unit = passociatedmodule.getCompilationUnit();
		methoddecl = pmethoddecl;
	}
	
	/**
	 * 
	 */
	
	public ConfigurationOption(EnumConstantDeclaration enumvalue,LElement element,Expression pconfigOption,Module passociatedmodule,MethodDeclaration pmethoddecl){
		super(passociatedmodule);
		this.configentry = new ConfigurationEntry(this,element);
		this.configurationcondition= new ConfigurationCondition(this,pconfigOption);
		this.associatedmodule = passociatedmodule;
		this.confg_relationlik = new HashSet<ConfigurationRelationLink>();
		unit = passociatedmodule.getCompilationUnit();
		methoddecl = pmethoddecl;
	}
	
	
	/**
	 * 
	 * @param method  ------------ method overriding, overloading
	 * @param pconfigExpression
	 * @param passociatedmodule
	 */
	public ConfigurationOption(MethodDeclaration method,LElement element,Expression pconfigExpression,Module passociatedmodule){
		super(passociatedmodule);
		this.configentry = new ConfigurationEntry(this,element);
		this.configurationcondition= new ConfigurationCondition(this,pconfigExpression);
		this.associatedmodule = passociatedmodule;
		this.confg_relationlik = new HashSet<ConfigurationRelationLink>();
		unit = passociatedmodule.getCompilationUnit();
		methoddecl = method;
	}
	
	
	public ConfigurationOption(TypeDeclaration typedecl,LElement element,Expression pinstancecreation,Module passociatedmodule,MethodDeclaration pmethoddecl){
		super(passociatedmodule);
		this.configentry = new ConfigurationEntry(this,element);
		this.configurationcondition= new ConfigurationCondition(this,pinstancecreation);
		this.associatedmodule = passociatedmodule;
		this.confg_relationlik = new HashSet<ConfigurationRelationLink>();
		unit = passociatedmodule.getCompilationUnit();
		methoddecl = pmethoddecl;
	}
	
	
	public void addConfigurationRelation(ConfigurationOption option,ConfigRelation relation){
		ConfigurationRelationLink link = new ConfigurationRelationLink(this,option,relation);
		// check whether there is a conflict
		if(!isAlreadyAdded(link)){
			confg_relationlik.add(link);
		}
	}
	
	private boolean isAlreadyAdded(ConfigurationRelationLink link) {
		// TODO Auto-generated method stub
		for(ConfigurationRelationLink conf:confg_relationlik){
			if(conf.equals(link))
				return true;
		}
		return false;
	}
	
	public void addEnable_Statements(Expression condition,Statement element){
		configurationcondition.addEnableStatement(condition,element);
	}
	
	
	public void addDisable_Statements(Expression condition,Statement element){
		configurationcondition.addDisabledStatement(condition, element);
	}
	
	public void addEnable_Statements(Expression condition,Set<Statement> elements){
		configurationcondition.addEnableStatement(condition,elements);
	}
	
	
	public void addDisable_Statements(Expression condition,Set<Statement> elements){
		configurationcondition.addDisabledStatement(condition, elements);
	}
	public Set<Statement> getEnable_Statements(Expression condition){
		return configurationcondition.getEnableStatement(condition);
	}
	
	public Set<Statement> getDisable_Statements(Expression condition){
		return configurationcondition.getDisabledStatement(condition);
	}

	
	
	public ConfigurationEntry getConfigurationEntry(){
		return this.configentry;
	}

	
	
	public Module getAssociatedModule(){
		return associatedmodule;
	}

	
	
	
	public Set<ConfigurationRelationLink> getlinks(){
		return confg_relationlik;
	}
	
	
	
	@Override
	public Object[] getChildren() {
		// TODO Auto-generated method stub
		List<ConfigurationRelationLink>arraylist = new ArrayList<ConfigurationRelationLink>(confg_relationlik);
		return arraylist.toArray();
	}
	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		if(obj instanceof ConfigurationOption){
			ConfigurationOption cong_obj = (ConfigurationOption)obj;
			if(cong_obj.getAssociatedModule().equals(associatedmodule)){
				if(cong_obj.getConfigurationEntry().equals(this.configentry)){
					if(cong_obj.getConfigurationCondition().equals(this.configurationcondition)){
						return true;
					}else
						return false;
				}else{
					return false;
				}
			}else{
				return false;
			}
		}else
			return false;
		
	}
	public ConfigurationCondition getConfigurationCondition() {
		// TODO Auto-generated method stub
		return this.configurationcondition;
	}

	

	public boolean isParentOf(ConfigurationOption res) {
		// TODO Auto-generated method stub
		if(!methoddecl.equals(res.methoddecl)){
			return false;
		}
		for(ConfigurationRelationLink link:confg_relationlik){
			ConfigurationOption target = link.getTargetConfigurationOption();
			if(target.equals(res)){
				if(link.getRelation().equals(ConfigRelation.CONTAINS)){
					return true;
				}else
					return false;
			}
		}
		return false;
	}
	
	
	
}
