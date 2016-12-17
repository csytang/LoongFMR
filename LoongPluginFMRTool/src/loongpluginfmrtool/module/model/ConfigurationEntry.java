package loongpluginfmrtool.module.model;

import org.eclipse.jdt.core.dom.ASTNode;
/*
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclaration;
*/
public class ConfigurationEntry {
	
	private ASTNode entry;
	private ConfigurationOption aoption;
	public ConfigurationEntry(ConfigurationOption option,ASTNode node){
		entry = node;
		aoption = option;
	}
	
	
	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		if(obj instanceof ConfigurationEntry){
			ConfigurationEntry obj_entry = (ConfigurationEntry)obj;
			return this.entry.equals(obj_entry.getASTNode());
		}else
			return false;
	}


	private ASTNode getASTNode() {
		// TODO Auto-generated method stub
		return entry;
	}
	
	

}
