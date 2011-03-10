package groovyx.transform.tailrec

import org.codehaus.groovy.ast.MethodNode
import org.codehaus.groovy.ast.expr.*
import org.codehaus.groovy.ast.stmt.ExpressionStatement
import org.codehaus.groovy.syntax.Token

class RecursivenessTester {
	boolean isRecursive(params) {
		assert params.method.class == MethodNode
		assert params.call.class == MethodCallExpression || StaticMethodCallExpression

		isRecursive(params.method, params.call)
	}

	boolean isRecursive(MethodNode method, MethodCallExpression call) {
		if (call.method.text != method.name)
			return false

		true
	}

	boolean isRecursive(MethodNode method, StaticMethodCallExpression call) {
		false
	}
}
