package groovyx.transform.tailrec

import groovy.lang.Closure;

import org.codehaus.groovy.ast.CodeVisitorSupport
import org.codehaus.groovy.ast.stmt.BlockStatement
import org.codehaus.groovy.ast.stmt.Statement

class CallOnEachNode extends CodeVisitorSupport {

	private Closure closure
	
	def synchronized onEachNode(node, closure) {
		this.closure = closure
		callOn(node, null)
		//node.visit(this)
	}
	
	private callOn(node, parent) {
		if (closure.getParameterTypes().size() < 2) {
			closure(node)
		} else {
			closure(node, parent)
		}
	}
	
	void visitBlockStatement(BlockStatement block) {
        for (Statement statement : block.getStatements()) {
            statement.visit(this);
        }
	}
}
