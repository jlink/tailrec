package groovyx.transform.tailrec

import org.codehaus.groovy.ast.MethodNode
import org.codehaus.groovy.ast.expr.*
import org.codehaus.groovy.ast.stmt.ExpressionStatement
import org.codehaus.groovy.syntax.Token

class AstHelper {

	static final Token ASSIGN = Token.newSymbol("=", -1, -1)
	static final Token PLUS = Token.newSymbol("+", -1, -1)

	static ExpressionStatement createVariableDefinition(String variableName, Expression value ) {
		new ExpressionStatement(new DeclarationExpression(new VariableExpression(variableName), AstHelper.ASSIGN, value))
	}

	static ExpressionStatement createVariableAlias(String aliasName, String variableName ) {
		createVariableDefinition(aliasName, new VariableExpression(variableName))
	}

	static ExpressionStatement createAssignment(String variableName, Expression value ) {
		new ExpressionStatement(new BinaryExpression(new VariableExpression(variableName), AstHelper.ASSIGN, value))
	}
}
