package loongpluginfmrtool.util;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.AnnotationTypeDeclaration;
import org.eclipse.jdt.core.dom.AnonymousClassDeclaration;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.ConstructorInvocation;
import org.eclipse.jdt.core.dom.DoStatement;
import org.eclipse.jdt.core.dom.EnhancedForStatement;
import org.eclipse.jdt.core.dom.EnumConstantDeclaration;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.SuperConstructorInvocation;
import org.eclipse.jdt.core.dom.SuperMethodInvocation;
import org.eclipse.jdt.core.dom.SuperMethodReference;
import org.eclipse.jdt.core.dom.SwitchCase;
import org.eclipse.jdt.core.dom.SwitchStatement;
import org.eclipse.jdt.core.dom.ThrowStatement;
import org.eclipse.jdt.core.dom.TryStatement;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclarationStatement;
import org.eclipse.jdt.core.dom.WhileStatement;

import loongplugin.source.database.model.LElement;
import loongplugin.source.database.model.LFlyweightElementFactory;
import loongpluginfmrtool.module.model.module.Module;

public class FixedRequiredFinder extends ASTVisitor {
	
	private Set<Module> requiredmodules = new HashSet<Module>();
	
	private LFlyweightElementFactory aElementFactory;
	private  Map<LElement,Module> elementToModule;
	
	public FixedRequiredFinder(LFlyweightElementFactory pElementFactory,Map<LElement,Module> pelementToModule){
		this.elementToModule = pelementToModule;
		this.aElementFactory = pElementFactory;
	}
	
	
	public Set<Module> getrequiredmodules(){
		return this.requiredmodules;
	}
	
	
	
	@Override
	public boolean visit(AnnotationTypeDeclaration node) {
		ITypeBinding binding = node.resolveBinding();
		if(binding!=null){
			LElement element = this.aElementFactory.getElement(binding);
			if(element!=null){
				LElement compileunitelement = this.aElementFactory.getElement(element.getCompilationUnit());
				Module md = this.elementToModule.get(compileunitelement);
				requiredmodules.add(md);
			}
		}
		return false;
	}

	@Override
	public boolean visit(AnonymousClassDeclaration node) {
		return super.visit(node);
	}


	@Override
	public boolean visit(Block node) {
		return true;
	}


	@Override
	public boolean visit(ConstructorInvocation node) {
		return false;
	}


	@Override
	public boolean visit(DoStatement node) {
		return false;
	}

	

	@Override
	public boolean visit(EnhancedForStatement node) {
		return false;
	}

	@Override
	public boolean visit(EnumConstantDeclaration node) {
		IMethodBinding binding = node.resolveConstructorBinding();
		if(binding!=null){
			LElement element = this.aElementFactory.getElement(binding);
			if(element!=null){
				LElement compileunitelement = this.aElementFactory.getElement(element.getCompilationUnit());
				Module md = this.elementToModule.get(compileunitelement);
				requiredmodules.add(md);
			}
		}
		return false;
	}

	@Override
	public boolean visit(EnumDeclaration node) {
		return super.visit(node);
	}


	@Override
	public boolean visit(FieldAccess node) {
		return super.visit(node);
	}

	@Override
	public boolean visit(FieldDeclaration node) {
		return super.visit(node);
	}

	@Override
	public boolean visit(ForStatement node) {
		return false;
	}

	@Override
	public boolean visit(IfStatement node) {
		return false;
	}



	@Override
	public boolean visit(MethodDeclaration node) {
		return true;
	}

	@Override
	public boolean visit(MethodInvocation node) {
		return true;
	}


	@Override
	public boolean visit(SuperConstructorInvocation node) {
		return true;
	}


	@Override
	public boolean visit(SuperMethodInvocation node) {
		return true;
	}

	@Override
	public boolean visit(SuperMethodReference node) {
		return true;
	}

	@Override
	public boolean visit(SwitchCase node) {
		return false;
	}

	@Override
	public boolean visit(SwitchStatement node) {
		return false;
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
	public boolean visit(TypeDeclaration node) {
		ITypeBinding binding = node.resolveBinding();
		if(binding!=null){
			LElement element = this.aElementFactory.getElement(binding);
			if(element!=null){
				LElement compileunitelement = this.aElementFactory.getElement(element.getCompilationUnit());
				Module md = this.elementToModule.get(compileunitelement);
				requiredmodules.add(md);
			}
		}
		return true;
	}

	@Override
	public boolean visit(TypeDeclarationStatement node) {
		ITypeBinding binding = node.resolveBinding();
		if(binding!=null){
			LElement element = this.aElementFactory.getElement(binding);
			if(element!=null){
				LElement compileunitelement = this.aElementFactory.getElement(element.getCompilationUnit());
				Module md = this.elementToModule.get(compileunitelement);
				requiredmodules.add(md);
			}
		}
		return true;
	}

	@Override
	public boolean visit(WhileStatement node) {
		return false;
	}


}
