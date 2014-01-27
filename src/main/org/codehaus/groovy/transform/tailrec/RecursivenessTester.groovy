package org.codehaus.groovy.transform.tailrec

import org.codehaus.groovy.ast.MethodNode
import org.codehaus.groovy.ast.expr.ConstantExpression
import org.codehaus.groovy.ast.expr.MethodCallExpression
import org.codehaus.groovy.ast.expr.StaticMethodCallExpression

/**
 * 
 * @author link
 *
 * Test if a method call is recursive if called within a given method node.
 * Tries to handle static calls as well.
 * 
 * Currently known simplifications:
 * Does not check for matching argument types but only considers the number of arguments.
 * Does not handle recursive call that make use of default values
 * Does not check for matching return types; even void and any object type are considered to be compatible.
 * 
 */
class RecursivenessTester {
	public boolean isRecursive(params) {
		assert params.method.class == MethodNode
		assert params.call.class == MethodCallExpression || StaticMethodCallExpression

		isRecursive(params.method, params.call)
	}

	public boolean isRecursive(MethodNode method, MethodCallExpression call) {
		if (!isCallToThis(call))
			return false
        // Could be a GStringExpression
        if (! (call.method instanceof ConstantExpression))
            return false
		if (call.method.value != method.name)
			return false
		methodParamsMatchCallArgs(method, call)
	}

	private boolean isCallToThis(MethodCallExpression call) {
		if (call.objectExpression == null)
			return call.isImplicitThis()
		call.objectExpression.isThisExpression()
	}
	
	private boolean methodParamsMatchCallArgs(method, call) {
		method.parameters.size() == call.arguments.expressions.size()
	}

	public boolean isRecursive(MethodNode method, StaticMethodCallExpression call) {
		if (!method.isStatic())
			return false
		if (method.declaringClass != call.ownerType)
			return false
		if (call.method != method.name)
			return false
		methodParamsMatchCallArgs(method, call)
	}
}
