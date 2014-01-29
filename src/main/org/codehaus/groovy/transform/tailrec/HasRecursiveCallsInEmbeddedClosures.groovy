package org.codehaus.groovy.transform.tailrec

import org.codehaus.groovy.ast.CodeVisitorSupport
import org.codehaus.groovy.ast.MethodNode
import org.codehaus.groovy.ast.expr.ClosureExpression
import org.codehaus.groovy.ast.expr.Expression
import org.codehaus.groovy.ast.expr.MethodCallExpression
import org.codehaus.groovy.ast.expr.StaticMethodCallExpression;

/**
 * @author Johannes Link
 */
class HasRecursiveCallsInEmbeddedClosures extends CodeVisitorSupport {
	MethodNode method
    int closureLevel = 0
    Expression guiltyExpression = null

    public void visitClosureExpression(ClosureExpression expression) {
        closureLevel++
        super.visitClosureExpression(expression)
        closureLevel--
    }

    public void visitMethodCallExpression(MethodCallExpression call) {
		if (inClosure() && isRecursive(call)) {
            guiltyExpression = call
		} else {
			super.visitMethodCallExpression(call)
		}
	}

	public void visitStaticMethodCallExpression(StaticMethodCallExpression call) {
		if (inClosure() && isRecursive(call)) {
            guiltyExpression = call
		} else {
			super.visitStaticMethodCallExpression(call)
		}
	}

    private boolean inClosure() {
        closureLevel > 0
    }

	private boolean isRecursive(call) {
		new RecursivenessTester().isRecursive(method: method, call: call)
	}

	synchronized boolean test(method) {
		guiltyExpression = null
		this.method = method
		this.method.code.visit(this)
		guiltyExpression != null
	}
}
