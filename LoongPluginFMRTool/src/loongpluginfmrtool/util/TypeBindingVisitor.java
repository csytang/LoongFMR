package loongpluginfmrtool.util;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.AnnotationTypeDeclaration;
import org.eclipse.jdt.core.dom.AnnotationTypeMemberDeclaration;
import org.eclipse.jdt.core.dom.AnonymousClassDeclaration;
import org.eclipse.jdt.core.dom.ArrayCreation;
import org.eclipse.jdt.core.dom.ArrayInitializer;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ConstructorInvocation;
import org.eclipse.jdt.core.dom.CreationReference;
import org.eclipse.jdt.core.dom.EnumConstantDeclaration;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.QualifiedType;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.SuperConstructorInvocation;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclarationStatement;

public class TypeBindingVisitor extends ASTVisitor{

	/*
	 * A collector will collect all types in 
	 * astnode that accept this visitor; 
	 */
	private Set<ITypeBinding>typeBindings = new HashSet<ITypeBinding>();
	
	public Set<ITypeBinding> getVariableBindings(){
		return typeBindings;
	}
	

	@Override
	public boolean visit(ClassInstanceCreation node) {
		// TODO Auto-generated method stub
		ITypeBinding binding = node.resolveTypeBinding();
		typeBindings.add(binding);
		return super.visit(node);
	}


	@Override
	public boolean visit(QualifiedName node) {
		// TODO Auto-generated method stub
		ITypeBinding binding =  node.resolveTypeBinding();
		typeBindings.add(binding);
		return super.visit(node);
	}

	@Override
	public boolean visit(QualifiedType node) {
		// TODO Auto-generated method stub
		ITypeBinding binding = node.resolveBinding();
		typeBindings.add(binding);
		return super.visit(node);
	}

	@Override
	public boolean visit(SimpleType node) {
		// TODO Auto-generated method stub
		ITypeBinding binding =node.resolveBinding();
		
		typeBindings.add(binding);
		
		return super.visit(node);
	}

	@Override
	public boolean visit(SuperConstructorInvocation node) {
		// TODO Auto-generated method stub
		ITypeBinding binding = node.resolveConstructorBinding().getDeclaredReceiverType();
		typeBindings.add(binding);
		return super.visit(node);
	}

}
