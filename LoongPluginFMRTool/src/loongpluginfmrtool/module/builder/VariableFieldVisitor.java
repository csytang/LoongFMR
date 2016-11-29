package loongpluginfmrtool.module.builder;

import java.util.HashSet;
import java.util.Set;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.SimpleName;


public class VariableFieldVisitor extends ASTVisitor{
	/*
	 * A collector will collect all variables and fields that defined in 
	 * astnode that accept this visitor; 
	 */
	private Set<IVariableBinding>variableBindings = new HashSet<IVariableBinding>();
	
	public Set<IVariableBinding> getVariableBindings(){
		return variableBindings;
	}
	public boolean visit(SimpleName nameNode) {
		IBinding binding = nameNode.resolveBinding();
		if(binding!=null && binding instanceof IVariableBinding){
			IVariableBinding varbinding = (IVariableBinding)binding;
			variableBindings.add(varbinding);
		}
		return super.visit(nameNode);
	}



	
}
