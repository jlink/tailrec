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
	}

	def transformationDescription(MethodNode method) {
		"$MY_TYPE_NAME transformation on method '${method.name}(${method.parameters.size()} params)'"
	}

	boolean hasRecursiveMethodCalls(MethodNode method) {
		false
	}
}