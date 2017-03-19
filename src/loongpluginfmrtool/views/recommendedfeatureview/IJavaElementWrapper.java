package loongpluginfmrtool.views.recommendedfeatureview;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.dom.ASTNode;

public class IJavaElementWrapper {
	private IJavaElement element;
	private Set<ASTNodeWrapper> allastnodeswrapper;
	private RSFeature pfeature;
	public IJavaElementWrapper(IJavaElement pelement,Set<ASTNode> pallastnodes,RSFeature feature){
		this.element = pelement;
		allastnodeswrapper = new HashSet<ASTNodeWrapper>();
		for(ASTNode node:pallastnodes){
			ASTNodeWrapper wrapper = new ASTNodeWrapper(node,this);
			allastnodeswrapper.add(wrapper);
		}
		this.pfeature = feature;
	}
	public Set<ASTNodeWrapper> getChildren(){
		return allastnodeswrapper;
	}
	public RSFeature getParent(){
		return pfeature;
	}
	public IJavaElement getIJavaElement(){
		return element;
	}
}
