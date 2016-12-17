package loongpluginfmrtool.util;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.ThrowStatement;

public class ASTVisitorHelper {
	
	public static boolean containsReturn = false;
	
	public static boolean containsReturn(ASTNode node){
		
		ASTVisitorHelper.containsReturn = false;
		node.accept(new ASTVisitor(){

			@Override
			public boolean visit(ReturnStatement node) {
				// TODO Auto-generated method stub
				containsReturn = true;
				return false;
			}

			@Override
			public boolean visit(ThrowStatement node) {
				// TODO Auto-generated method stub
				containsReturn = true;
				return false;
			}
			
		});
		
		
		return ASTVisitorHelper.containsReturn;
		
	}
}
