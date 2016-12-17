package loongpluginfmrtool.util;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.StructuralPropertyDescriptor;

public class ASTNodeHelper {
	
	/**
	 * 
	 * @param parentstatement parent statement
	 * @param statement  check the all statement after
	 * @return
	 */
	public static Set<Statement>getStatementAfter(Statement statement,Statement parentstatement){
		Set<Statement>afterstatements = new HashSet<Statement>();
		int statementstartPos = statement.getStartPosition();
		if(parentstatement instanceof Block){
			Block blockstatement = (Block)parentstatement;
			List<Statement>liststatements = blockstatement.statements();
			for(Statement substatiatestatement:liststatements){
				//StructuralPropertyDescriptor substdescripter = substatiatestatement.getLocationInParent();
				if(substatiatestatement.getStartPosition()>statementstartPos)
					afterstatements.add(substatiatestatement);
			}
			
		}
		
		return afterstatements;
	}
}
