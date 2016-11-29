package loongpluginfmrtool.module.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.TypeParameter;
import org.eclipse.jdt.core.dom.VariableDeclaration;


public class ASTSubBindingFinder {
	public static Map<ASTNode,IBinding> astBindingFinder(ASTNode node){
		final Map<ASTNode,IBinding> results = new HashMap<ASTNode,IBinding>();
		node.accept(new ASTVisitor(){

			@Override
			public void preVisit(ASTNode node) {
				// TODO Auto-generated method stub
				if (node instanceof Type) {
					IBinding binding = ((Type) node).resolveBinding();
					if (binding != null )
						results.put(node, binding);
				}
				if (node instanceof VariableDeclaration) {
					IBinding binding = ((VariableDeclaration) node).resolveBinding();
					if (binding != null)
						results.put(node,binding);
				}
				if (node instanceof AbstractTypeDeclaration) {
					IBinding binding = ((AbstractTypeDeclaration) node)
							.resolveBinding();
					if (binding != null)
						results.put(node,binding);
				}
				
				super.preVisit(node);
			}
			
			public boolean visit(TypeDeclaration node) {
				IBinding binding = node.resolveBinding();
				if (binding != null)
					results.put(node,binding);
				return super.visit(node);
			}

			public boolean visit(MethodDeclaration node) {
				IBinding binding = node.resolveBinding();
				if (binding != null)
					results.put(node,binding);
				return super.visit(node);
			}

			public boolean visit(ImportDeclaration node) {
				IBinding binding = node.resolveBinding();
				if (binding != null)
					results.put(node,binding);
				return super.visit(node);
			}

			public boolean visit(PackageDeclaration node) {
				IBinding binding = node.resolveBinding();
				if (binding != null)
					results.put(node,binding);
				return super.visit(node);
			}

			public boolean visit(TypeParameter node) {
				IBinding binding = node.resolveBinding();
				if (binding != null)
					results.put(node,binding);
				return super.visit(node);
			}
			
			
		});
		return results;
	}
}
