package loongpluginfmrtool.module.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Statement;

public class ConfigurationOption extends ModuleComponent{
	public Expression aconfigOption;
	private Set<ConfigurationRelationLink> confg_relationlik;
	private Set<Statement>select_statement = new HashSet<Statement>();
	private Set<Statement>unselect_statement = new HashSet<Statement>();
	private Set<ASTNode>affected_astnodes = new HashSet<ASTNode>();
	private Module associatedmodule;
	private CompilationUnit unit;
	private int internalVariabilityCount = 0;
	private int externalVariabilityCount = 0;
	private int overallVariabilityCount = 0;
	private boolean overallVariabilityisCount = false;
	private MethodDeclaration methoddecl;
	public ConfigurationOption(Module passociatedmodule,MethodDeclaration pmethoddecl){
		super(passociatedmodule);
		unit = passociatedmodule.getCompilationUnit();
		methoddecl = pmethoddecl;
	}
	
	public MethodDeclaration getMethod(){
		return methoddecl;
	}
	public ConfigurationOption(Expression pconfigOption,Module passociatedmodule,MethodDeclaration pmethoddecl){
		super(passociatedmodule);
		this.aconfigOption = pconfigOption;
		this.associatedmodule = passociatedmodule;
		this.confg_relationlik = new HashSet<ConfigurationRelationLink>();
		unit = passociatedmodule.getCompilationUnit();
		methoddecl = pmethoddecl;
	}
	
	public void setOverallVariabilityCount(int value){
		this.overallVariabilityCount = value;
		this.overallVariabilityisCount = true;
	}
	
	public boolean isOverallVariabilitySet(){
		return this.overallVariabilityisCount;
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
	public void addEnable_Statements(Statement element){
		select_statement.add(element);
		// 也计入到影响的 astnode部分
		affected_astnodes.add(element);
	}
	
	public void addAffected_ASTNode(ASTNode node){
		affected_astnodes.add(node);
	}
	
	public void addDisable_Statements(Statement element){
		unselect_statement.add(element);
		// 也计入到影响的 astnode部分
		affected_astnodes.add(element);
	}
	
	
	public Set<Statement> getEnable_Statements(){
		return select_statement;
	}
	
	public Set<Statement> getDisable_Statements(){
		return unselect_statement;
	}

	public Expression getExpression() {
		// TODO Auto-generated method stub
		return aconfigOption;
	}

	public Set<ASTNode> getAffectedASTNodes() {
		// TODO Auto-generated method stub
		return affected_astnodes;
	}
	
	public Module getAssociatedModule(){
		return associatedmodule;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return aconfigOption.toString();
	}
	public String getAffectedASTNodesRange() {
		// TODO Auto-generated method stub
		Set<Integer>selected_range = new HashSet<Integer>();
		for(Statement statement:select_statement){
			int start = unit.getLineNumber(statement.getStartPosition());
			int end = unit.getLineNumber(statement.getStartPosition()+statement.getLength());
			for(int i = start;i<=end;i++){
				selected_range.add(i);
			}
			
		}
		int []sorted  = new int[selected_range.size()];
		int index = 0;
		for(Integer it:selected_range){
			sorted[index] = it;
			index++;
		}
		
		Arrays.sort(sorted);
		String selected = "selected:[";
		for(int i = 0;i < sorted.length;i++){
			selected+= sorted[i];
			selected+= ",";
		}
		if(sorted.length!=0)
			selected = selected.substring(0, selected.length()-1);
		selected+="]";
		
		Set<Integer>unselected_range = new HashSet<Integer>();
		for(Statement statement:unselect_statement){
			int start = unit.getLineNumber(statement.getStartPosition());
			int end = unit.getLineNumber(statement.getStartPosition()+statement.getLength());
			for(int i = start;i<=end;i++){
				unselected_range.add(i);
			}
		}
		
		int []unsele_sorted  = new int[unselected_range.size()];
		index = 0;
		for(Integer it:unselected_range){
			unsele_sorted[index] = it;
			index++;
		}
		
		Arrays.sort(unsele_sorted);
		
		String unselected = "unselected:[";
		for(int i = 0;i < unsele_sorted.length;i++){
			unselected+= unsele_sorted[i];
			unselected+= ",";
		}
		if(unsele_sorted.length!=0)
			unselected = unselected.substring(0, unselected.length()-1);
		unselected+="]";
		
		return selected+unselected;
	}
	
	public Set<ConfigurationRelationLink> getlinks(){
		return confg_relationlik;
	}
	
	public void computeVariability(){
		for(ConfigurationRelationLink link:confg_relationlik){
			Module remote_module = link.getTargetConfigurationOption().getAssociatedModule();
			if(remote_module.equals(associatedmodule)){
				this.internalVariabilityCount++;
			}else{
				this.externalVariabilityCount++;
			}
		}
	}
	
	public int getInternalVariability(){
		return this.internalVariabilityCount;
	}
	
	public int getExternalVariability(){
		return this.externalVariabilityCount;
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
				if(cong_obj.getExpression().equals(aconfigOption)){
					if(cong_obj.getExpression().getStartPosition()==aconfigOption.getStartPosition() &&
							cong_obj.getExpression().getLength()==aconfigOption.getLength()){
						return true;
					}else{
						return false;
					}
				}else{
					return false;
				}
			}else{
				return false;
			}
		}else
			return false;
		
	}
	public int getOverallVariability() {
		// TODO Auto-generated method stub
		return overallVariabilityCount;
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
