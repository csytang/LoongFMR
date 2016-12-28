package loongpluginfmrtool.util;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.EnumConstantDeclaration;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import loongplugin.source.database.model.LElement;
import loongplugin.source.database.model.LFlyweightElementFactory;

public class ASTVisitorLElementVisitor extends ASTVisitor{

	/*
	 * A collector will collect all types in 
	 * astnode that accept this visitor; 
	 */
	private Set<LElement>elements = new HashSet<LElement>();
	private LFlyweightElementFactory aelementfactory;
	public ASTVisitorLElementVisitor(LFlyweightElementFactory pelementfactory){
		this.aelementfactory = pelementfactory;
	}
	
	public Set<LElement> getAllLElements(){
		return elements;
	}

	@Override
	public boolean visit(CompilationUnit node) {
		LElement element = aelementfactory.getElement(node);
		assert element!=null;
		elements.add(element);
		return super.visit(node);
	}

	@Override
	public boolean visit(ImportDeclaration node) {
		LElement element = aelementfactory.getElement(node);
		assert element!=null;
		elements.add(element);
		return super.visit(node);
	}

	@Override
	public boolean visit(EnumDeclaration node) {
		ITypeBinding binding = node.resolveBinding();
		LElement element = aelementfactory.getElement(binding);
		assert element!=null;
		elements.add(element);
		return super.visit(node);
	}

	@Override
	public boolean visit(TypeDeclaration node) {
		ITypeBinding binding = node.resolveBinding();
		LElement element = aelementfactory.getElement(binding);
		assert element!=null;
		elements.add(element);
		return super.visit(node);
	}

	public boolean visit(MethodDeclaration node) {
		IMethodBinding binding = node.resolveBinding();
		if (binding != null) {
			LElement element = aelementfactory.getElement(binding);
			assert element!=null;
			elements.add(element);
		}
		return super.visit(node);
	}

	public boolean visit(VariableDeclarationFragment node) {
		IVariableBinding binding = node.resolveBinding();
		LElement element = aelementfactory.getElement(binding);
		assert element!=null;
		elements.add(element);
		return super.visit(node);
	}

	public boolean visit(SingleVariableDeclaration node) {
		IVariableBinding binding = node.resolveBinding();
		LElement element = aelementfactory.getElement(binding);
		assert element!=null;
		elements.add(element);
		return super.visit(node);
	}

	public boolean visit(EnumConstantDeclaration node) {
		IVariableBinding binding = node.resolveVariable();
		LElement element = aelementfactory.getElement(binding);
		assert element!=null;
		elements.add(element);
		return super.visit(node);
	}

}
