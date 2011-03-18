package groovyx.transform.tailrec

import org.codehaus.groovy.ast.MethodNode
import org.codehaus.groovy.ast.stmt.BlockStatement
import org.codehaus.groovy.ast.stmt.ExpressionStatement;
import org.codehaus.groovy.ast.stmt.ReturnStatement
import org.codehaus.groovy.ast.stmt.Statement

class ReturnStatementFiller {

	void fill(MethodNode method) {
		addMissingReturnStatements(method)
	}
	
	private addMissingReturnStatements(MethodNode method) {
		BlockStatement code = method.code
		if (code.statements.isEmpty()) {
			method.code.addStatement(ReturnStatement.RETURN_NULL_OR_VOID)
			return
		}
		Statement lastStatement = code.statements[-1]
		if (lastStatement instanceof ReturnStatement) {
			return
		}		
		if (! (lastStatement instanceof ExpressionStatement)) {
			method.code.addStatement(ReturnStatement.RETURN_NULL_OR_VOID)
			return
		}
//		code.statements[-1] = new ReturnStatement(lastStatement)
	}

}
