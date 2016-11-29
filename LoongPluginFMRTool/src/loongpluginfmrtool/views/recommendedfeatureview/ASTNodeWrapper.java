package loongpluginfmrtool.views.recommendedfeatureview;

import org.eclipse.jdt.core.dom.ASTNode;

public class ASTNodeWrapper {
	private ASTNode node;
	private IJavaElementWrapper wrapper;
	public ASTNodeWrapper(ASTNode pnode,IJavaElementWrapper pwrapper){
		this.node = pnode;
		this.wrapper = pwrapper;
	}
	public IJavaElementWrapper getParent(){
		return this.wrapper;
	}
	public ASTNode getASTNode() {
		// TODO Auto-generated method stub
		return node;
	}
}
