package loongpluginfmrtool.util;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.MethodRef;
import org.eclipse.jdt.core.dom.SuperConstructorInvocation;
import org.eclipse.jdt.core.dom.SuperMethodInvocation;
import org.eclipse.jdt.core.dom.SuperMethodReference;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;

public class MethodBindingFinder extends ASTVisitor {

	private Set<IMethodBinding> methodbindings = new HashSet<IMethodBinding>();
	@Override
	public boolean visit(ClassInstanceCreation node) {
		IMethodBinding methodbinding = node.resolveConstructorBinding();
		this.methodbindings.add(methodbinding);
		return super.visit(node);
	}

	@Override
	public boolean visit(MethodDeclaration node) {
		// TODO Auto-generated method stub
		IMethodBinding methodbinding = node.resolveBinding();
		this.methodbindings.add(methodbinding);
		return super.visit(node);
		
	}

	@Override
	public boolean visit(MethodInvocation node) {
		IMethodBinding methodbinding = node.resolveMethodBinding();
		this.methodbindings.add(methodbinding);
		return super.visit(node);
	}

	

	@Override
	public boolean visit(SuperConstructorInvocation node) {
		IMethodBinding methodbinding = node.resolveConstructorBinding();
		this.methodbindings.add(methodbinding);
		return super.visit(node);
	}

	@Override
	public boolean visit(SuperMethodInvocation node) {
		IMethodBinding methodbinding = node.resolveMethodBinding();
		this.methodbindings.add(methodbinding);
		return super.visit(node);
	}

	@Override
	public boolean visit(SuperMethodReference node) {
		IMethodBinding methodbinding = node.resolveMethodBinding();
		this.methodbindings.add(methodbinding);
		return super.visit(node);
	}

	public Set<IMethodBinding> getMethodBinding(){
		return this.methodbindings;
	}
}
