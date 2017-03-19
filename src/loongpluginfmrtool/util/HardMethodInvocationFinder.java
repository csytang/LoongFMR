package loongpluginfmrtool.util;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.AnnotationTypeDeclaration;
import org.eclipse.jdt.core.dom.AnonymousClassDeclaration;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.ConstructorInvocation;
import org.eclipse.jdt.core.dom.DoStatement;
import org.eclipse.jdt.core.dom.EnhancedForStatement;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.SuperConstructorInvocation;
import org.eclipse.jdt.core.dom.SuperMethodInvocation;
import org.eclipse.jdt.core.dom.SwitchCase;
import org.eclipse.jdt.core.dom.SwitchStatement;

import org.eclipse.jdt.core.dom.ThrowStatement;
import org.eclipse.jdt.core.dom.TryStatement;
import org.eclipse.jdt.core.dom.WhileStatement;

import loongplugin.source.database.model.LElement;
import loongplugin.source.database.model.LFlyweightElementFactory;
import loongpluginfmrtool.module.model.module.Module;
public class HardMethodInvocationFinder extends ASTVisitor {

	private Set<LElement> methodinvocation;
	private LFlyweightElementFactory aElementFactory;
	
	public HardMethodInvocationFinder(LFlyweightElementFactory pElementFactory){
		this.aElementFactory = pElementFactory;
		this.methodinvocation = new HashSet<LElement>();
	}
	@Override
	public boolean visit(Block node) {
		// search the nodes
		return true;
	}

	@Override
	public boolean visit(ConstructorInvocation node) {
		IMethodBinding binding = node.resolveConstructorBinding();
		if(binding!=null){
			LElement element = this.aElementFactory.getElement(binding);
			if(element!=null){
				methodinvocation.add(element);
			}
		}
		return true;
	}


	@Override
	public boolean visit(DoStatement node) {
		// skip the child node
		return false;
	}

	@Override
	public boolean visit(EnhancedForStatement node) {
		// skip the child node
		return false;
	}


	
	@Override
	public boolean visit(CatchClause node) {
		return true;
	}
	@Override
	public boolean visit(ThrowStatement node) {
		return true;
	}
	@Override
	public boolean visit(TryStatement node) {
		return true;
	}
	@Override
	public boolean visit(ForStatement node) {
		// skip the child node
		return false;
	}

	@Override
	public boolean visit(IfStatement node) {
		// skip the child node
		return false;
	}

	
	
	@Override
	public boolean visit(MethodDeclaration node) {
		IMethodBinding binding = node.resolveBinding();
		if(binding!=null){
			LElement element = this.aElementFactory.getElement(binding);
			if(element!=null){
				methodinvocation.add(element);
			}
		}
		return true;
	}

	@Override
	public boolean visit(MethodInvocation node) {
		IMethodBinding binding = node.resolveMethodBinding();
		if(binding!=null){
			LElement element = this.aElementFactory.getElement(binding);
			if(element!=null){
				methodinvocation.add(element);
			}
		}
		return super.visit(node);
	}

	
	@Override
	public boolean visit(SuperConstructorInvocation node) {
		IMethodBinding binding = node.resolveConstructorBinding();
		if(binding!=null){
			LElement element = this.aElementFactory.getElement(binding);
			if(element!=null){
				methodinvocation.add(element);
			}
		}
		return true;
	}


	@Override
	public boolean visit(SuperMethodInvocation node) {
		IMethodBinding binding = node.resolveMethodBinding();
		if(binding!=null){
			LElement element = this.aElementFactory.getElement(binding);
			if(element!=null){
				methodinvocation.add(element);
			}
		}
		return true;
	}

	
	@Override
	public boolean visit(ClassInstanceCreation node) {
		IMethodBinding binding = node.resolveConstructorBinding();
		if(binding!=null){
			LElement element = this.aElementFactory.getElement(binding);
			if(element!=null){
				methodinvocation.add(element);
			}
		}
		return super.visit(node);
	}
	@Override
	public boolean visit(SwitchCase node) {
		// skip the child node
		return false;
	}

	@Override
	public boolean visit(SwitchStatement node) {
		// skip the child
		return false;
	}

	
	@Override
	public boolean visit(WhileStatement node) {
		// skip the child node
		return false;
	}
	public Collection<? extends LElement> getMethodInovaction() {
		// TODO Auto-generated method stub
		return methodinvocation;
	}

	
}
