package loongpluginfmrtool.module.model.configuration;

import org.eclipse.jdt.core.dom.ASTNode;

import loongplugin.source.database.model.LElement;
/*
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclaration;
*/
public class ConfigurationEntry {
	
	private LElement entryelement;
	private ConfigurationOption aoption;
	public ConfigurationEntry(ConfigurationOption option,LElement element){
		entryelement = element;
		aoption = option;
	}
	
	
	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		if(obj instanceof ConfigurationEntry){
			ConfigurationEntry obj_entry = (ConfigurationEntry)obj;
			return this.entryelement.equals(obj_entry.getLElement());
		}else
			return false;
	}


	public LElement getLElement() {
		// TODO Auto-generated method stub
		return entryelement;
	}
	
	

}
