package groovyx.transform.tailrec


import org.codehaus.groovy.ast.*
import org.codehaus.groovy.ast.expr.*
import org.codehaus.groovy.ast.stmt.*
import org.codehaus.groovy.control.*
import org.codehaus.groovy.transform.*

@GroovyASTTransformation(phase=CompilePhase.SEMANTIC_ANALYSIS)
class TailRecursiveASTTransformation implements ASTTransformation {

	@Override
	public void visit(ASTNode[] nodes, SourceUnit source) {
	}
}