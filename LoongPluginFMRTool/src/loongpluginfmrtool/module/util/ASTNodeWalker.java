package loongpluginfmrtool.module.util;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.jdt.core.dom.*;


public class ASTNodeWalker {
	/**
	 * a  method walker will walk through the node 
	 * @param node
	 * @return
	 */
	public static Set<ASTNode> methodWalker(ASTNode node){
		final Set<ASTNode> results = new HashSet<ASTNode>();
		node.accept(new ASTVisitor(){

			@Override
			public boolean visit(MethodDeclaration node) {
				// TODO Auto-generated method stub
				results.add(node);
				return super.visit(node);
			}
			
		});
		return results;
	}
	
	public static Set<ASTNode> allWalker(ASTNode node){
		final Set<ASTNode> results = new HashSet<ASTNode>();
		node.accept(new ASTVisitor(){

			@Override
			public void preVisit(ASTNode node) {
				// TODO Auto-generated method stub
				results.add(node);
				super.preVisit(node);
			}

			
			
		});
		return results;
	}
}
