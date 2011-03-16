package groovyx.transform.tailrec


import org.codehaus.groovy.ast.CodeVisitorSupport;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.ast.expr.*;

class HasRecursiveCalls extends CodeVisitorSupport {
	MethodNode method
	boolean hasRecursiveCalls = false

	public void visitMethodCallExpression(MethodCallExpression call) {
		if (isRecursive(call)) {
			hasRecursiveCalls = true
		} else {
			super.visitMethodCallExpression(call)
		}
	}

	public void visitStaticMethodCallExpression(StaticMethodCallExpression call) {
		if (isRecursive(call)) {
			hasRecursiveCalls = true
		} else {
			super.visitStaticMethodCallExpression(call)
		}
	}
	
	private boolean isRecursive(call) {
		new RecursivenessTester().isRecursive(method: method, call: call)
	}
	
	synchronized boolean test(method) {
		this.method = method
		this.method.code.visit(this)
		hasRecursiveCalls
	}
}
