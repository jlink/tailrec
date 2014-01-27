package org.codehaus.groovy.transform.tailrec

import org.codehaus.groovy.ast.MethodNode
import org.codehaus.groovy.ast.expr.BooleanExpression
import org.codehaus.groovy.ast.expr.ConstantExpression
import org.codehaus.groovy.ast.stmt.BlockStatement
import org.codehaus.groovy.ast.stmt.WhileStatement

class InWhileLoopWrapper {
	
	final static String LOOP_LABEL = '_RECUR_HERE_'

	void wrap(MethodNode method) {
		BlockStatement oldBody = method.code
		WhileStatement whileLoop = new WhileStatement(new BooleanExpression(new ConstantExpression(true)), oldBody)
		if (whileLoop.loopBlock.statements.size() > 0)
			whileLoop.loopBlock.statements[0].statementLabel = LOOP_LABEL
		BlockStatement newBody = new BlockStatement([], method.variableScope)
		newBody.addStatement(whileLoop)
		method.code = newBody
	}
}
