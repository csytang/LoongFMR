package loongpluginfmrtool.views.recommendedfeatureview;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.AnnotationTypeDeclaration;
import org.eclipse.jdt.core.dom.AnnotationTypeMemberDeclaration;
import org.eclipse.jdt.core.dom.AnonymousClassDeclaration;
import org.eclipse.jdt.core.dom.ArrayAccess;
import org.eclipse.jdt.core.dom.ArrayCreation;
import org.eclipse.jdt.core.dom.ArrayInitializer;
import org.eclipse.jdt.core.dom.ArrayType;
import org.eclipse.jdt.core.dom.AssertStatement;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.BlockComment;
import org.eclipse.jdt.core.dom.BooleanLiteral;
import org.eclipse.jdt.core.dom.BreakStatement;
import org.eclipse.jdt.core.dom.CastExpression;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.CharacterLiteral;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ConditionalExpression;
import org.eclipse.jdt.core.dom.ConstructorInvocation;
import org.eclipse.jdt.core.dom.ContinueStatement;
import org.eclipse.jdt.core.dom.CreationReference;
import org.eclipse.jdt.core.dom.Dimension;
import org.eclipse.jdt.core.dom.DoStatement;
import org.eclipse.jdt.core.dom.EmptyStatement;
import org.eclipse.jdt.core.dom.EnhancedForStatement;
import org.eclipse.jdt.core.dom.EnumConstantDeclaration;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ExpressionMethodReference;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.Initializer;
import org.eclipse.jdt.core.dom.InstanceofExpression;
import org.eclipse.jdt.core.dom.IntersectionType;
import org.eclipse.jdt.core.dom.Javadoc;
import org.eclipse.jdt.core.dom.LabeledStatement;
import org.eclipse.jdt.core.dom.LambdaExpression;
import org.eclipse.jdt.core.dom.LineComment;
import org.eclipse.jdt.core.dom.MarkerAnnotation;
import org.eclipse.jdt.core.dom.MemberRef;
import org.eclipse.jdt.core.dom.MemberValuePair;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.MethodRef;
import org.eclipse.jdt.core.dom.MethodRefParameter;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.NameQualifiedType;
import org.eclipse.jdt.core.dom.NormalAnnotation;
import org.eclipse.jdt.core.dom.NullLiteral;
import org.eclipse.jdt.core.dom.NumberLiteral;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.ParameterizedType;
import org.eclipse.jdt.core.dom.ParenthesizedExpression;
import org.eclipse.jdt.core.dom.PostfixExpression;
import org.eclipse.jdt.core.dom.PrefixExpression;
import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.QualifiedType;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.SingleMemberAnnotation;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.SuperConstructorInvocation;
import org.eclipse.jdt.core.dom.SuperFieldAccess;
import org.eclipse.jdt.core.dom.SuperMethodInvocation;
import org.eclipse.jdt.core.dom.SuperMethodReference;
import org.eclipse.jdt.core.dom.SwitchCase;
import org.eclipse.jdt.core.dom.SwitchStatement;
import org.eclipse.jdt.core.dom.SynchronizedStatement;
import org.eclipse.jdt.core.dom.TagElement;
import org.eclipse.jdt.core.dom.TextElement;
import org.eclipse.jdt.core.dom.ThisExpression;
import org.eclipse.jdt.core.dom.ThrowStatement;
import org.eclipse.jdt.core.dom.TryStatement;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclarationStatement;
import org.eclipse.jdt.core.dom.TypeLiteral;
import org.eclipse.jdt.core.dom.TypeMethodReference;
import org.eclipse.jdt.core.dom.TypeParameter;
import org.eclipse.jdt.core.dom.UnionType;
import org.eclipse.jdt.core.dom.VariableDeclarationExpression;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.core.dom.WhileStatement;
import org.eclipse.jdt.core.dom.WildcardType;


public class ASTStringTracker extends ASTVisitor {
	
	private Map<String,Set<ASTNode>> recommendfeatureMapping = new HashMap<String,Set<ASTNode>>();
	private Map<String,Set<ASTNode>> recommendnonfeatureMapping = new HashMap<String,Set<ASTNode>>();

	
	public ASTStringTracker(ASTNode node){
		node.accept(this);
	}
	public Map<String,Set<ASTNode>> getRecommendedFeatureNameList(){
		return recommendfeatureMapping;
	}
	public Map<String,Set<ASTNode>> getRecommendedNonFeatureNameList(){
		return recommendnonfeatureMapping;
	}
	
	@Override
	public boolean visit(AnnotationTypeDeclaration node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}
	@Override
	public boolean visit(AnnotationTypeMemberDeclaration node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}
	@Override
	public boolean visit(AnonymousClassDeclaration node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}
	@Override
	public boolean visit(Block node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}
	@Override
	public boolean visit(BlockComment node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}
	@Override
	public boolean visit(BooleanLiteral node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}
	@Override
	public boolean visit(BreakStatement node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}
	@Override
	public boolean visit(CatchClause node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}
	@Override
	public boolean visit(CharacterLiteral node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}
	@Override
	public boolean visit(CompilationUnit node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}
	@Override
	public boolean visit(ConditionalExpression node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}
	@Override
	public boolean visit(ContinueStatement node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}
	@Override
	public boolean visit(CreationReference node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}
	@Override
	public boolean visit(Dimension node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}
	@Override
	public boolean visit(DoStatement node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}
	@Override
	public boolean visit(EmptyStatement node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}
	@Override
	public boolean visit(EnhancedForStatement node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}
	@Override
	public boolean visit(ForStatement node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}
	@Override
	public boolean visit(IfStatement node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}
	@Override
	public boolean visit(InfixExpression node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}
	@Override
	public boolean visit(Initializer node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}
	@Override
	public boolean visit(IntersectionType node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}
	@Override
	public boolean visit(LabeledStatement node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}
	@Override
	public boolean visit(LambdaExpression node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}
	@Override
	public boolean visit(MarkerAnnotation node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}
	@Override
	public boolean visit(MemberRef node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}
	@Override
	public boolean visit(MemberValuePair node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}
	@Override
	public boolean visit(Modifier node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}
	@Override
	public boolean visit(NormalAnnotation node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}
	@Override
	public boolean visit(NullLiteral node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}
	@Override
	public boolean visit(NumberLiteral node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}
	@Override
	public boolean visit(ParameterizedType node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}
	@Override
	public boolean visit(ParenthesizedExpression node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}
	@Override
	public boolean visit(PostfixExpression node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}
	@Override
	public boolean visit(PrefixExpression node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}
	@Override
	public boolean visit(PrimitiveType node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}
	@Override
	public boolean visit(QualifiedName node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}
	@Override
	public boolean visit(QualifiedType node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}
	@Override
	public boolean visit(SimpleName node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}
	@Override
	public boolean visit(SimpleType node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}
	@Override
	public boolean visit(SingleMemberAnnotation node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}
	@Override
	public boolean visit(SwitchCase node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}
	@Override
	public boolean visit(SwitchStatement node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}
	@Override
	public boolean visit(SynchronizedStatement node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}
	@Override
	public boolean visit(TagElement node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}
	@Override
	public boolean visit(TextElement node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}
	@Override
	public boolean visit(ThisExpression node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}
	@Override
	public boolean visit(ThrowStatement node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}
	@Override
	public boolean visit(TryStatement node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}
	@Override
	public boolean visit(TypeLiteral node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}
	@Override
	public boolean visit(TypeParameter node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}
	@Override
	public boolean visit(UnionType node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}
	@Override
	public boolean visit(VariableDeclarationStatement node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}
	@Override
	public boolean visit(WhileStatement node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}
	@Override
	public boolean visit(WildcardType node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}
	@Override
	public boolean visit(ArrayAccess node) {
		// TODO Auto-generated method stub
		final Expression expression = node.getArray();
		expression.accept(new ASTVisitor(){
			@Override
			public boolean visit(SimpleName node) {
				// TODO Auto-generated method stub
				Set<ASTNode>bindingnodes;
				if(node.getFullyQualifiedName().isEmpty())
					return super.visit(node);
				if(recommendnonfeatureMapping.containsKey(node.getFullyQualifiedName())){
					bindingnodes = recommendnonfeatureMapping.get(node.getFullyQualifiedName());
					bindingnodes.add(expression);
				}else{
					bindingnodes = new HashSet<ASTNode>();
					bindingnodes.add(expression);
				}
				recommendnonfeatureMapping.put(node.getFullyQualifiedName(), bindingnodes);
				return super.visit(node);
			}
			
		});
		return super.visit(node);
	}

	@Override
	public boolean visit(ArrayCreation node) {
		// TODO Auto-generated method stub
		final ArrayType type = node.getType();
		Type elementtype = type.getElementType();
		
		if(elementtype.toString().isEmpty())
			return super.visit(node);
		
		Set<ASTNode>bindingnodes;
			
		if(recommendnonfeatureMapping.containsKey(elementtype.toString())){
				bindingnodes = recommendnonfeatureMapping.get(elementtype.toString());
				bindingnodes.add(type);
		}else{
				bindingnodes = new HashSet<ASTNode>();
				bindingnodes.add(type);
		}
		recommendnonfeatureMapping.put(elementtype.toString(), bindingnodes);
		
		return super.visit(node);
	}

	@Override
	public boolean visit(ArrayInitializer node) {
		// TODO Auto-generated method stub
		List<Expression> expressions = node.expressions();
		for(Expression exp:expressions){
			Set<ASTNode>bindingnodes;
			
			if(recommendnonfeatureMapping.containsKey(exp.toString())){
				bindingnodes = recommendnonfeatureMapping.get(exp.toString());
				bindingnodes.add(node);
			}else{
				bindingnodes = new HashSet<ASTNode>();
				bindingnodes.add(node);
			}
			recommendnonfeatureMapping.put(exp.toString(), bindingnodes);
		}
		return super.visit(node);
	}

	@Override
	public boolean visit(ArrayType node) {
		// TODO Auto-generated method stub
		Type elementtype = node.getElementType();
		if(!elementtype.isSimpleType()){
			Set<ASTNode>bindingnodes;
			
			if(recommendnonfeatureMapping.containsKey(elementtype.toString())){
				bindingnodes = recommendnonfeatureMapping.get(elementtype.toString());
				bindingnodes.add(node);
			}else{
				bindingnodes = new HashSet<ASTNode>();
				bindingnodes.add(node);
			}
			recommendnonfeatureMapping.put(elementtype.toString(), bindingnodes);
		}
		return super.visit(node);
	}

	@Override
	public boolean visit(AssertStatement node) {
		// TODO Auto-generated method stub
		Expression expression = node.getExpression();
		Set<ASTNode>bindingnodes;
		if(recommendnonfeatureMapping.containsKey(expression.toString())){
			bindingnodes = recommendnonfeatureMapping.get(expression.toString());
			bindingnodes.add(node);
		}else{
			bindingnodes = new HashSet<ASTNode>();
			bindingnodes.add(node);
		}
		recommendnonfeatureMapping.put(expression.toString(), bindingnodes);
		return super.visit(node);
	}

	@Override
	public boolean visit(Assignment node) {
		// TODO Auto-generated method stub
		Expression leftSide = node.getLeftHandSide();
		Expression rightSide = node.getRightHandSide();
		Set<ASTNode>bindingnodes;
		
		if(recommendnonfeatureMapping.containsKey(leftSide.toString())){
			bindingnodes = recommendnonfeatureMapping.get(leftSide.toString());
			bindingnodes.add(node);
		}else{
			bindingnodes = new HashSet<ASTNode>();
			bindingnodes.add(node);
		}
		recommendnonfeatureMapping.put(leftSide.toString(), bindingnodes);
		if(recommendnonfeatureMapping.containsKey(rightSide.toString())){
			bindingnodes = recommendnonfeatureMapping.get(rightSide.toString());
			bindingnodes.add(node);
		}else{
			bindingnodes = new HashSet<ASTNode>();
			bindingnodes.add(node);
		}
		recommendnonfeatureMapping.put(rightSide.toString(), bindingnodes);
		return super.visit(node);
	}

	@Override
	public boolean visit(CastExpression node) {
		// TODO Auto-generated method stub
		Expression exp  = node.getExpression();
		Set<ASTNode>bindingnodes;
	
		if(recommendnonfeatureMapping.containsKey(exp.toString())){
			bindingnodes = recommendnonfeatureMapping.get(exp.toString());
			bindingnodes.add(node);
		}else{
			bindingnodes = new HashSet<ASTNode>();
			bindingnodes.add(node);
		}
		recommendnonfeatureMapping.put(exp.toString(), bindingnodes);
		return super.visit(node);
	}

	@Override
	public boolean visit(ClassInstanceCreation node) {
		// TODO Auto-generated method stub
		Type nodetype = node.getType();
		Set<ASTNode>bindingnodes;
		
		if(recommendnonfeatureMapping.containsKey(nodetype.toString())){
			bindingnodes = recommendnonfeatureMapping.get(nodetype.toString());
			bindingnodes.add(node);
		}else{
			bindingnodes = new HashSet<ASTNode>();
			bindingnodes.add(node);
		}
		recommendnonfeatureMapping.put(nodetype.toString(), bindingnodes);
		return super.visit(node);
	}

	@Override
	public boolean visit(ConstructorInvocation node) {
		// TODO Auto-generated method stub
		List<Expression> arguments = node.arguments();
		IMethodBinding constructorbinding = node.resolveConstructorBinding();
		Set<ASTNode>bindingnodes;
		
		if(recommendfeatureMapping.containsKey(constructorbinding.getName())){
			bindingnodes = recommendfeatureMapping.get(constructorbinding.getName());
			bindingnodes.add(node);
		}else{
			bindingnodes = new HashSet<ASTNode>();
			bindingnodes.add(node);
		}
		recommendfeatureMapping.put(constructorbinding.getName(), bindingnodes);
		
		for(Expression argument:arguments){
			
			bindingnodes.clear();
			
			if(recommendnonfeatureMapping.containsKey(argument.toString())){
				bindingnodes = recommendnonfeatureMapping.get(argument.toString());
				bindingnodes.add(node);
			}else{
				bindingnodes = new HashSet<ASTNode>();
				bindingnodes.add(node);
			}
			recommendnonfeatureMapping.put(argument.toString(), bindingnodes);
			
		}
		
		return super.visit(node);
	}

	@Override
	public boolean visit(EnumConstantDeclaration node) {
		// TODO Auto-generated method stub
		SimpleName enumName = node.getName();
		Set<ASTNode>bindingnodes;
		
		if(recommendfeatureMapping.containsKey(enumName.getFullyQualifiedName())){
			bindingnodes = recommendfeatureMapping.get(enumName.getFullyQualifiedName());
			bindingnodes.add(node);
		}else{
			bindingnodes = new HashSet<ASTNode>();
			bindingnodes.add(node);
		}
		recommendfeatureMapping.put(enumName.getFullyQualifiedName(), bindingnodes);
		return super.visit(node);
	}

	@Override
	public boolean visit(EnumDeclaration node) {
		// TODO Auto-generated method stub
		// could be feature name, use the type declaration
		/*
		 * EnumDeclaration:
     		[ Javadoc ] { ExtendedModifier } enum Identifier
         	[ implements Type { , Type } ]
         	{
         		[ EnumConstantDeclaration { , EnumConstantDeclaration } ] [ , ]
         		[ ; { ClassBodyDeclaration | ; } ]
         	}
		 */
		SimpleName nodeName = node.getName();
		
		Set<ASTNode>bindingnodes;
		if(recommendfeatureMapping.containsKey(nodeName.getFullyQualifiedName())){
			bindingnodes = recommendfeatureMapping.get(nodeName.getFullyQualifiedName());
			bindingnodes.add(node);
		}else{
			bindingnodes = new HashSet<ASTNode>();
			bindingnodes.add(node);
		}
		recommendfeatureMapping.put(nodeName.getFullyQualifiedName(), bindingnodes);
		return super.visit(node);
	}

	@Override
	public boolean visit(ExpressionMethodReference node) {
		// TODO Auto-generated method stub
		SimpleName methodName = node.getName();
		Set<ASTNode>bindingnodes;
		
		if(recommendfeatureMapping.containsKey(methodName.getFullyQualifiedName())){
			bindingnodes = recommendfeatureMapping.get(methodName.getFullyQualifiedName());
			bindingnodes.add(node);
		}else{
			bindingnodes = new HashSet<ASTNode>();
			bindingnodes.add(node);
		}
		recommendfeatureMapping.put(methodName.getFullyQualifiedName(), bindingnodes);
		return super.visit(node);
	}

	@Override
	public boolean visit(ExpressionStatement node) {
		// TODO Auto-generated method stub
		String expStatement = node.toString();
		Set<ASTNode>bindingnodes;
		
		if(recommendnonfeatureMapping.containsKey(expStatement.toString())){
			bindingnodes = recommendnonfeatureMapping.get(expStatement.toString());
			bindingnodes.add(node);
		}else{
			bindingnodes = new HashSet<ASTNode>();
			bindingnodes.add(node);
		}
		recommendnonfeatureMapping.put(expStatement.toString(), bindingnodes);
		return super.visit(node);
	}

	@Override
	public boolean visit(FieldAccess node) {
		// TODO Auto-generated method stub
		SimpleName field = node.getName();
		Set<ASTNode>bindingnodes;
		
		if(recommendfeatureMapping.containsKey(field.getFullyQualifiedName())){
			bindingnodes = recommendfeatureMapping.get(field.getFullyQualifiedName());
			bindingnodes.add(node);
		}else{
			bindingnodes = new HashSet<ASTNode>();
			bindingnodes.add(node);
		}
		recommendfeatureMapping.put(field.getFullyQualifiedName(), bindingnodes);
		return super.visit(node);
	}

	@Override
	public boolean visit(FieldDeclaration node) {
		// TODO Auto-generated method stub
		List<VariableDeclarationFragment>fields = node.fragments();
		for(VariableDeclarationFragment fieldfrag:fields){
			Set<ASTNode>bindingnodes;
			
			if(recommendfeatureMapping.containsKey(fieldfrag.getName().getFullyQualifiedName())){
				bindingnodes = recommendfeatureMapping.get(fieldfrag.getName().getFullyQualifiedName());
				bindingnodes.add(node);
			}else{
				bindingnodes = new HashSet<ASTNode>();
				bindingnodes.add(node);
			}
			recommendfeatureMapping.put(fieldfrag.getName().getFullyQualifiedName(), bindingnodes);
		}
		return super.visit(node);
	}

	

	@Override
	public boolean visit(ImportDeclaration node) {
		// TODO Auto-generated method stub
		Name importedName = node.getName();
		Set<ASTNode>bindingnodes;
		
		if(recommendnonfeatureMapping.containsKey(importedName.getFullyQualifiedName())){
			bindingnodes = recommendnonfeatureMapping.get(importedName.getFullyQualifiedName());
			bindingnodes.add(node);
		}else{
			bindingnodes = new HashSet<ASTNode>();
			bindingnodes.add(node);
		}
		recommendnonfeatureMapping.put(importedName.getFullyQualifiedName(), bindingnodes);
		return super.visit(node);
	}
	

	@Override
	public boolean visit(InstanceofExpression node) {
		// TODO Auto-generated method stub
		Expression leftOpenand = node.getLeftOperand();
		Type rightOpenand = node.getRightOperand();
		Set<ASTNode>bindingnodes;
		
		if(recommendfeatureMapping.containsKey(rightOpenand.toString())){
			bindingnodes = recommendfeatureMapping.get(rightOpenand.toString());
			bindingnodes.add(node);
		}else{
			bindingnodes = new HashSet<ASTNode>();
			bindingnodes.add(node);
		}
		recommendfeatureMapping.put(rightOpenand.toString(), bindingnodes);
		bindingnodes.clear();
		if(recommendnonfeatureMapping.containsKey(leftOpenand.toString())){
			bindingnodes = recommendnonfeatureMapping.get(leftOpenand.toString());
			bindingnodes.add(node);
		}else{
			bindingnodes = new HashSet<ASTNode>();
			bindingnodes.add(node);
		}
		
		recommendnonfeatureMapping.put(leftOpenand.toString(), bindingnodes);
		return super.visit(node);
	}

	@Override
	public boolean visit(Javadoc node) {
		// TODO Auto-generated method stub
		Set<ASTNode>bindingnodes;
		
		if(recommendnonfeatureMapping.containsKey(node.toString())){
			bindingnodes = recommendnonfeatureMapping.get(node.toString());
			bindingnodes.add(node);
		}else{
			bindingnodes = new HashSet<ASTNode>();
			bindingnodes.add(node);
		}
		recommendnonfeatureMapping.put(node.toString(), bindingnodes);
		return super.visit(node);
	}


	@Override
	public boolean visit(LineComment node) {
		// TODO Auto-generated method stub
		Set<ASTNode>bindingnodes;
		
		if(recommendnonfeatureMapping.containsKey(node.toString())){
			bindingnodes = recommendnonfeatureMapping.get(node.toString());
			bindingnodes.add(node);
		}else{
			bindingnodes = new HashSet<ASTNode>();
			bindingnodes.add(node);
		}
		recommendnonfeatureMapping.put(node.toString(), bindingnodes);
		return super.visit(node);
	}

	@Override
	public boolean visit(MethodRef node) {
		// TODO Auto-generated method stub
		SimpleName methodName = node.getName();
		
		Set<ASTNode>bindingnodes;
		if(recommendfeatureMapping.containsKey(methodName.getFullyQualifiedName())){
			bindingnodes = recommendfeatureMapping.get(methodName.getFullyQualifiedName());
			bindingnodes.add(node);
		}else{
			bindingnodes = new HashSet<ASTNode>();
			bindingnodes.add(node);
		}
		recommendfeatureMapping.put(methodName.getFullyQualifiedName(), bindingnodes);
		return super.visit(node);
	}

	@Override
	public boolean visit(MethodRefParameter node) {
		// TODO Auto-generated method stub
		SimpleName name = node.getName();
		Type type = node.getType();
		Set<ASTNode>bindingnodes;
		
		if(recommendfeatureMapping.containsKey(name.getFullyQualifiedName())){
			bindingnodes = recommendfeatureMapping.get(name.getFullyQualifiedName());
			bindingnodes.add(node);
		}else{
			bindingnodes = new HashSet<ASTNode>();
			bindingnodes.add(node);
		}
		recommendfeatureMapping.put(name.getFullyQualifiedName(), bindingnodes);
		bindingnodes.clear();
		
		if(recommendnonfeatureMapping.containsKey(type.toString())){
			bindingnodes = recommendnonfeatureMapping.get(type.toString());
			bindingnodes.add(node);
		}else{
			bindingnodes = new HashSet<ASTNode>();
			bindingnodes.add(node);
		}
		recommendnonfeatureMapping.put(type.toString(), bindingnodes);
		return super.visit(node);
	}

	@Override
	public boolean visit(MethodDeclaration node) {
		// TODO Auto-generated method stub
		SimpleName methodName = node.getName();
		Set<ASTNode>bindingnodes;
		
		if(recommendfeatureMapping.containsKey(methodName.getFullyQualifiedName())){
			bindingnodes = recommendfeatureMapping.get(methodName.getFullyQualifiedName());
			bindingnodes.add(node);
		}else{
			bindingnodes = new HashSet<ASTNode>();
			bindingnodes.add(node);
		}
		recommendfeatureMapping.put(methodName.getFullyQualifiedName(), bindingnodes);
		return super.visit(node);
	}

	@Override
	public boolean visit(MethodInvocation node) {
		// TODO Auto-generated method stub
		SimpleName methodName = node.getName();
		List<Expression> arguments = node.arguments();
		Set<ASTNode>bindingnodes;
		
		if(recommendfeatureMapping.containsKey(methodName.getFullyQualifiedName())){
			bindingnodes = recommendfeatureMapping.get(methodName.getFullyQualifiedName());
			bindingnodes.add(node);
		}else{
			bindingnodes = new HashSet<ASTNode>();
			bindingnodes.add(node);
		}
		recommendfeatureMapping.put(methodName.getFullyQualifiedName(), bindingnodes);
		for(Expression exp:arguments){
			bindingnodes.clear();
			
			if(recommendnonfeatureMapping.containsKey(exp.toString())){
				bindingnodes = recommendnonfeatureMapping.get(exp.toString());
				bindingnodes.add(node);
			}else{
				bindingnodes = new HashSet<ASTNode>();
				bindingnodes.add(node);
			}
			recommendnonfeatureMapping.put(exp.toString(), bindingnodes);
		}
		
		return super.visit(node);
	}


	@Override
	public boolean visit(NameQualifiedType node) {
		// TODO Auto-generated method stub
		SimpleName name = node.getName();
		Set<ASTNode>bindingnodes;
		
		if(recommendnonfeatureMapping.containsKey(name.getFullyQualifiedName().toString())){
			bindingnodes = recommendnonfeatureMapping.get(name.getFullyQualifiedName().toString());
			bindingnodes.add(node);
		}else{
			bindingnodes = new HashSet<ASTNode>();
			bindingnodes.add(node);
		}
		recommendnonfeatureMapping.put(name.getFullyQualifiedName().toString(), bindingnodes);
		return super.visit(node);
	}

	@Override
	public boolean visit(PackageDeclaration node) {
		// TODO Auto-generated method stub
		Name packageName = node.getName();
		Set<ASTNode>bindingnodes;
		
		if(recommendfeatureMapping.containsKey(packageName.getFullyQualifiedName().toString())){
			bindingnodes = recommendfeatureMapping.get(packageName.getFullyQualifiedName().toString());
			bindingnodes.add(node);
		}else{
			bindingnodes = new HashSet<ASTNode>();
			bindingnodes.add(node);
		}
		recommendfeatureMapping.put(packageName.getFullyQualifiedName().toString(), bindingnodes);
		return super.visit(node);
	}


	@Override
	public boolean visit(ReturnStatement node) {
		// TODO Auto-generated method stub
		Expression returnexp = node.getExpression();
		Set<ASTNode>bindingnodes;
		
		if(recommendnonfeatureMapping.containsKey(returnexp.toString())){
			bindingnodes = recommendnonfeatureMapping.get(returnexp.toString());
			bindingnodes.add(node);
		}else{
			bindingnodes = new HashSet<ASTNode>();
			bindingnodes.add(node);
		}
		recommendnonfeatureMapping.put(returnexp.toString(), bindingnodes);
		return super.visit(node);
	}

	@Override
	public boolean visit(SingleVariableDeclaration node) {
		// TODO Auto-generated method stub]
		SimpleName nodeName = node.getName();
		Type nodeType = node.getType();
		Set<ASTNode>bindingnodes;
		
		if(recommendfeatureMapping.containsKey(nodeName.toString())){
			bindingnodes = recommendfeatureMapping.get(nodeName.toString());
			bindingnodes.add(node);
		}else{
			bindingnodes = new HashSet<ASTNode>();
			bindingnodes.add(node);
		}
		recommendfeatureMapping.put(nodeName.toString(), bindingnodes);
		bindingnodes.clear();
		
		if(recommendnonfeatureMapping.containsKey(nodeType.toString())){
			bindingnodes = recommendnonfeatureMapping.get(nodeType.toString());
			bindingnodes.add(node);
		}else{
			bindingnodes = new HashSet<ASTNode>();
			bindingnodes.add(node);
		}
		recommendnonfeatureMapping.put(nodeType.toString(), bindingnodes);
		return super.visit(node);
	}

	@Override
	public boolean visit(StringLiteral node) {
		// TODO Auto-generated method stub
		String value = node.getLiteralValue();
		Set<ASTNode>bindingnodes;
		
		if(recommendnonfeatureMapping.containsKey(value)){
			bindingnodes = recommendnonfeatureMapping.get(value);
			bindingnodes.add(node);
		}else{
			bindingnodes = new HashSet<ASTNode>();
			bindingnodes.add(node);
		}
		recommendnonfeatureMapping.put(value, bindingnodes);
		return super.visit(node);
	}

	@Override
	public boolean visit(SuperConstructorInvocation node) {
		// TODO Auto-generated method stub
		String supermethodName = node.resolveConstructorBinding().getName();
		Set<ASTNode>bindingnodes;
		
		if(recommendfeatureMapping.containsKey(supermethodName)){
			bindingnodes = recommendfeatureMapping.get(supermethodName);
			bindingnodes.add(node);
		}else{
			bindingnodes = new HashSet<ASTNode>();
			bindingnodes.add(node);
		}
		recommendfeatureMapping.put(supermethodName, bindingnodes);
		return super.visit(node);
	}

	@Override
	public boolean visit(SuperFieldAccess node) {
		// TODO Auto-generated method stub
		SimpleName fieldName = node.getName();
		Set<ASTNode>bindingnodes;
		if(fieldName.getFullyQualifiedName().isEmpty())
			return super.visit(node);
		if(recommendfeatureMapping.containsKey(fieldName.getFullyQualifiedName())){
			bindingnodes = recommendfeatureMapping.get(fieldName.getFullyQualifiedName());
			bindingnodes.add(node);
		}else{
			bindingnodes = new HashSet<ASTNode>();
			bindingnodes.add(node);
		}
		recommendfeatureMapping.put(fieldName.getFullyQualifiedName(), bindingnodes);
		return super.visit(node);
	}

	@Override
	public boolean visit(SuperMethodInvocation node) {
		// TODO Auto-generated method stub
		String methodName = node.resolveMethodBinding().getName();
		Set<ASTNode>bindingnodes;
		
		if(recommendfeatureMapping.containsKey(methodName)){
			bindingnodes = recommendfeatureMapping.get(methodName);
			bindingnodes.add(node);
		}else{
			bindingnodes = new HashSet<ASTNode>();
			bindingnodes.add(node);
		}
		recommendfeatureMapping.put(methodName, bindingnodes);
		return super.visit(node);
	}

	@Override
	public boolean visit(SuperMethodReference node) {
		// TODO Auto-generated method stub
		SimpleName supermethodName = node.getName();
		Set<ASTNode>bindingnodes;
		if(recommendfeatureMapping.containsKey(supermethodName.toString())){
			bindingnodes = recommendfeatureMapping.get(supermethodName.toString());
			bindingnodes.add(node);
		}else{
			bindingnodes = new HashSet<ASTNode>();
			bindingnodes.add(node);
		}
		recommendfeatureMapping.put(supermethodName.toString(), bindingnodes);
		return super.visit(node);
	}



	@Override
	public boolean visit(TypeDeclaration node) {
		// TODO Auto-generated method stub
		Set<ASTNode>bindingnodes;
		for(TypeDeclaration type:node.getTypes()){	
			if(recommendfeatureMapping.containsKey(type.getName().getFullyQualifiedName())){
				bindingnodes = recommendfeatureMapping.get(type.getName().getFullyQualifiedName());
				bindingnodes.add(node);
			}else{
				bindingnodes = new HashSet<ASTNode>();
				bindingnodes.add(node);
			}
			recommendfeatureMapping.put(type.getName().getFullyQualifiedName(), bindingnodes);
			bindingnodes.clear();
		}
		return super.visit(node);
	}

	@Override
	public boolean visit(TypeDeclarationStatement node) {
		// TODO Auto-generated method stub
		TypeDeclaration typeDeclaration = node.getTypeDeclaration();
		for(TypeDeclaration type:typeDeclaration.getTypes()){
			
			Set<ASTNode>bindingnodes;
			if(recommendnonfeatureMapping.containsKey(type.getName().getFullyQualifiedName())){
				bindingnodes = recommendnonfeatureMapping.get(type.getName().getFullyQualifiedName());
				bindingnodes.add(node);
			}else{
				bindingnodes = new HashSet<ASTNode>();
				bindingnodes.add(node);
			}
			recommendnonfeatureMapping.put(type.getName().getFullyQualifiedName(), bindingnodes);
			bindingnodes.clear();
		}
		
		return super.visit(node);
	}

	@Override
	public boolean visit(TypeMethodReference node) {
		// TODO Auto-generated method stub
		SimpleName name = node.getName();
		Type type = node.getType();
		
		Set<ASTNode>bindingnodes;
		if(recommendfeatureMapping.containsKey(name.getFullyQualifiedName())){
			bindingnodes = recommendfeatureMapping.get(name.getFullyQualifiedName());
			bindingnodes.add(node);
		}else{
			bindingnodes = new HashSet<ASTNode>();
			bindingnodes.add(node);
		}
		recommendfeatureMapping.put(name.getFullyQualifiedName(), bindingnodes);
		bindingnodes.clear();
		
		if(recommendnonfeatureMapping.containsKey(type.toString())){
			bindingnodes = recommendnonfeatureMapping.get(type.toString());
			bindingnodes.add(node);
		}else{
			bindingnodes = new HashSet<ASTNode>();
			bindingnodes.add(node);
		}
		recommendnonfeatureMapping.put(type.toString(), bindingnodes);
		
		return super.visit(node);
	}


	@Override
	public boolean visit(VariableDeclarationExpression node) {
		// TODO Auto-generated method stub
		List<VariableDeclarationFragment>fragements = node.fragments();
		for(VariableDeclarationFragment frag:fragements){
			SimpleName name = frag.getName();
			
			Set<ASTNode>bindingnodes;
			if(recommendfeatureMapping.containsKey(name.getFullyQualifiedName())){
				bindingnodes = recommendfeatureMapping.get(name.getFullyQualifiedName());
				bindingnodes.add(node);
			}else{
				bindingnodes = new HashSet<ASTNode>();
				bindingnodes.add(node);
			}
			recommendfeatureMapping.put(name.getFullyQualifiedName(), bindingnodes);
			bindingnodes.clear();
		}
		return super.visit(node);
	}


	@Override
	public boolean visit(VariableDeclarationFragment node) {
		// TODO Auto-generated method stub
		SimpleName name = node.getName();
		Set<ASTNode>bindingnodes;
		
		if(recommendfeatureMapping.containsKey(name.getFullyQualifiedName())){
			bindingnodes = recommendfeatureMapping.get(name.getFullyQualifiedName());
			bindingnodes.add(node);
		}else{
			bindingnodes = new HashSet<ASTNode>();
			bindingnodes.add(node);
		}
		recommendfeatureMapping.put(name.getFullyQualifiedName(), bindingnodes);
		return super.visit(node);
	}

	
}
