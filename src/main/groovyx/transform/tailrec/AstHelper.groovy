package groovyx.transform.tailrec

import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.expr.BinaryExpression
import org.codehaus.groovy.ast.expr.DeclarationExpression
import org.codehaus.groovy.ast.expr.Expression
import org.codehaus.groovy.ast.expr.VariableExpression
import org.codehaus.groovy.ast.stmt.ExpressionStatement
import org.codehaus.groovy.syntax.Token

class AstHelper {

	static final Token ASSIGN = Token.newSymbol("=", -1, -1)
	static final Token PLUS = Token.newSymbol("+", -1, -1)

	static ExpressionStatement createVariableDefinition(String variableName, ClassNode type, Expression value ) {
        //todo use type information for creating the new variable
		new ExpressionStatement(new DeclarationExpression(new VariableExpression(variableName), AstHelper.ASSIGN, value))
	}

	static ExpressionStatement createVariableAlias(String aliasName, ClassNode type, String variableName ) {
		createVariableDefinition(aliasName, type, new VariableExpression(variableName))
	}

	static ExpressionStatement createAssignment(String variableName, Expression value ) {
		new ExpressionStatement(new BinaryExpression(new VariableExpression(variableName), AstHelper.ASSIGN, value))
	}
}
