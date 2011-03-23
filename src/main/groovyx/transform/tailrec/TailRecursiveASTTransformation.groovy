package groovyx.transform.tailrec


import groovyx.transform.TailRecursive

import org.codehaus.groovy.ast.*
import org.codehaus.groovy.ast.expr.*
import org.codehaus.groovy.ast.stmt.*
import org.codehaus.groovy.control.*
import org.codehaus.groovy.transform.*

@GroovyASTTransformation(phase=CompilePhase.SEMANTIC_ANALYSIS)
class TailRecursiveASTTransformation implements ASTTransformation {

	private static final Class MY_CLASS = TailRecursive.class;
	private static final ClassNode MY_TYPE = new ClassNode(MY_CLASS);
	static final String MY_TYPE_NAME = "@" + MY_TYPE.getNameWithoutPackage()
	private HasRecursiveCalls hasRecursiveCalls = new HasRecursiveCalls()

	@Override
	public void visit(ASTNode[] nodes, SourceUnit source) {
		if (!nodes) return;
		if (!nodes[0]) return;
		if (!nodes[1]) return;
		if (!(nodes[0] instanceof AnnotationNode)) return;
		if (!(nodes[1] instanceof MethodNode)) return;

		MethodNode method = nodes[1];
		if (!hasRecursiveMethodCalls(method)) {
			println(transformationDescription(method) + " skipped: No recursive calls detected.")
			return;
		}
		println(transformationDescription(method) + ": transform recursive calls to iteration.")
		transformToIteration(method)
		checkAllRecursiveCallsHaveBeenTransformed(method)
	}

	void transformToIteration(MethodNode method) {
		if (method.isVoidMethod()) {
			transformVoidMethodToIteration(method)
		} else {
			transformNonVoidMethodToIteration(method)
		}
	}

	private void transformVoidMethodToIteration(MethodNode method) {
		//todo
		throwException(method: method, message: "void methods are not supported yet!")
	}

	private void transformNonVoidMethodToIteration(MethodNode method) {
		fillInMissingReturns(method)
		wrapMethodBodyWithWhileLoop(method)
		//		Map nameMapping, positionMapping
		//		(nameMapping, positionMapping) = findAllParameterMappings(method)
		//		replaceAllAccessToParams(method, nameMapping)
		//		addLocalVariablesForAllParameters(method, nameMapping)
		//		replaceAllRecursiveReturnsWithVariableAssignment(method, positionMapping)

	}
	
	private void wrapMethodBodyWithWhileLoop(MethodNode method) {
		new InWhileLoopWrapper().wrap(method)
	}

	private void fillInMissingReturns(MethodNode method) {
		new ReturnStatementFiller().fill(method)
	}

	private void checkAllRecursiveCallsHaveBeenTransformed(MethodNode method) {
		if (hasRecursiveMethodCalls(method)) {
			throwException(method: method, message: "not all recursive calls could be transformed!")
		}
	}

	private throwException(Map params) {
		throw new RuntimeException(transformationDescription(params.method) + ": ${params.message}")
	}

	private def transformationDescription(MethodNode method) {
		"$MY_TYPE_NAME transformation on '${method.declaringClass}.${method.name}(${method.parameters.size()} params)'"
	}

	private boolean hasRecursiveMethodCalls(MethodNode method) {
		hasRecursiveCalls.test(method)
	}
}