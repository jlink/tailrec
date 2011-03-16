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
		transformRecursiveCalls(method)
		checkAllRecursiveCallsHaveBeenTransformed(method)
	}

	void transformRecursiveCalls(MethodNode method) {
		//todo
	}
	
	void checkAllRecursiveCallsHaveBeenTransformed(MethodNode method) {
		if (hasRecursiveMethodCalls(method)) {
			throw new RuntimeException(transformationDescription(method) + ": not all recursive calls could be transformed!")
		}
	}
	
	def transformationDescription(MethodNode method) {
		"$MY_TYPE_NAME transformation on '${method.declaringClass}.${method.name}(${method.parameters.size()} params)'"
	}

	boolean hasRecursiveMethodCalls(MethodNode method) {
		hasRecursiveCalls.test(method)
	}

		
}