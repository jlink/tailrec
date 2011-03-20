package groovyx.transform.tailrec

import org.codehaus.groovy.ast.MethodNode
import org.codehaus.groovy.ast.stmt.BlockStatement
import org.codehaus.groovy.ast.stmt.ExpressionStatement;
import org.codehaus.groovy.ast.stmt.ReturnStatement
import org.codehaus.groovy.ast.stmt.Statement
import org.codehaus.groovy.ast.stmt.WhileStatement;

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
		if (lastStatement instanceof WhileStatement) {
			method.code.addStatement(ReturnStatement.RETURN_NULL_OR_VOID)
			return
		}
		if (lastStatement instanceof ExpressionStatement) {
			code.statements[-1] = new ReturnStatement(lastStatement)
			return
		}
		//todo many unhandled non expression statements, e.g. if, switch etc
	}
}
