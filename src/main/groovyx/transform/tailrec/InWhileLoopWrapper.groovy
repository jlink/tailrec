package groovyx.transform.tailrec

import org.codehaus.groovy.ast.MethodNode
import org.codehaus.groovy.ast.expr.BooleanExpression
import org.codehaus.groovy.ast.expr.ConstantExpression
import org.codehaus.groovy.ast.stmt.BlockStatement
import org.codehaus.groovy.ast.stmt.WhileStatement

class InWhileLoopWrapper {

	void wrap(MethodNode method) {
		WhileStatement whileLoop = new WhileStatement(new BooleanExpression(new ConstantExpression(true)), method.code)
		BlockStatement newBody = new BlockStatement([], method.variableScope)
		newBody.addStatement(whileLoop)
		method.code = newBody
	}
}
