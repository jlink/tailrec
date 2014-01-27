package org.codehaus.groovy.transform.tailrec

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

	static ExpressionStatement createVariableDefinition(String variableName, ClassNode variableType, Expression value ) {
		new ExpressionStatement(new DeclarationExpression(new VariableExpression(variableName, variableType), AstHelper.ASSIGN, value))
	}

	static ExpressionStatement createVariableAlias(String aliasName, ClassNode variableType, String variableName ) {
		createVariableDefinition(aliasName, variableType, new VariableExpression(variableName, variableType))
	}

	static ExpressionStatement createAssignment(String variableName, ClassNode variableType, Expression value ) {
		new ExpressionStatement(new BinaryExpression(new VariableExpression(variableName, variableType), AstHelper.ASSIGN, value))
	}

    static VariableExpression createVariableReference(Map variableSpec) {
        new VariableExpression(variableSpec.name, variableSpec.type)
    }
}
