package loongpluginfmrtool.module.builder;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.ConstructorInvocation;
import org.eclipse.jdt.core.dom.EnumConstantDeclaration;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.MethodRef;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.Statement;

import loongplugin.source.database.model.LFlyweightElementFactory;
import loongpluginfmrtool.module.model.ConfigurationEntry;

public class ConfigurationEntryFinder {
	private static IBinding binding;
	private static LFlyweightElementFactory aLElementFactory;
	public static Set<ASTNode> getConfigurations(ASTNode statement,LFlyweightElementFactory pLElementFactory){
		final Set<ASTNode>astnodes = new HashSet<ASTNode>();
		aLElementFactory = pLElementFactory;
		statement.accept(new ASTVisitor(){
			/**
			 * start from a local variable or a field
			 */
			@Override
			public boolean visit(SimpleName node) {
				//
				ConfigurationEntryFinder.binding = node.resolveBinding();
				if(aLElementFactory.getElement(binding)!=null){
					/*
					 *  defined in system type like int number = 5;
					 */
					ASTNode variablefieldnode = aLElementFactory.getElement(binding).getASTNode();
					astnodes.add(variablefieldnode);
				}
				return false;
			}

			@Override
			public boolean visit(ClassInstanceCreation node) {
				// TODO Auto-generated method stub
				ConfigurationEntryFinder.binding = node.resolveTypeBinding();
				if(aLElementFactory.getElement(binding)!=null){// method defined in API
					ASTNode isntancecreatenode = aLElementFactory.getElement(binding).getASTNode();
					astnodes.add(isntancecreatenode);
				}
				return false;
			}

			@Override
			public boolean visit(ConstructorInvocation node) {
				// return an IMethodBinding
				ConfigurationEntryFinder.binding = node.resolveConstructorBinding();
				if(aLElementFactory.getElement(binding)!=null){// method defined in API
					ASTNode isntancecreatenode = aLElementFactory.getElement(binding).getASTNode();
					astnodes.add(isntancecreatenode);
				}
				return false;
			}

			

			@Override
			public boolean visit(EnumConstantDeclaration node) {
				ConfigurationEntryFinder.binding = node.resolveVariable();
				ASTNode enumcreatenode = aLElementFactory.getElement(binding).getASTNode();
				astnodes.add(enumcreatenode);
				return false;
			}

			@Override
			public boolean visit(EnumDeclaration node) {
				ConfigurationEntryFinder.binding = node.resolveBinding();
				ASTNode enumcreatenode = aLElementFactory.getElement(binding).getASTNode();
				astnodes.add(enumcreatenode);
				return false;
			}

			@Override
			public boolean visit(MethodDeclaration node) {
				ConfigurationEntryFinder.binding = node.resolveBinding();
				ASTNode methodnode = aLElementFactory.getElement(binding).getASTNode();
				astnodes.add(methodnode);
				return false;
			}

			@Override
			public boolean visit(MethodInvocation node) {
				ConfigurationEntryFinder.binding = node.resolveMethodBinding();
				if(aLElementFactory.getElement(binding)!=null){// method defined in API
					ASTNode methodnode = aLElementFactory.getElement(binding).getASTNode();
					astnodes.add(methodnode);
				}
				return false;
			}

			@Override
			public boolean visit(MethodRef node) {
				ConfigurationEntryFinder.binding = node.resolveBinding();
				if(aLElementFactory.getElement(binding)!=null){// method defined in API
					ASTNode methodnode = aLElementFactory.getElement(binding).getASTNode();
					astnodes.add(methodnode);
				}
				return false;
			}
			
			
		});
		
		
		
		return astnodes;
	}
}
