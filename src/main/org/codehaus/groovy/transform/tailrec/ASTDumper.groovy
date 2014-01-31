package org.codehaus.groovy.transform.tailrec

import groovy.inspect.swingui.AstNodeToScriptVisitor
import org.codehaus.groovy.ast.ASTNode
import org.codehaus.groovy.ast.MethodNode

/**
 * Used for debugging AST transformations
 *
 * @author Johannes Link
 */
class ASTDumper {

    static void dump(ASTNode node) {
        def writer = new StringWriter()
        AstNodeToScriptVisitor adapter = new AstNodeToScriptVisitor(writer)
        node.visit(adapter)
        println writer
    }

    static void dump(MethodNode methodNode) {
        dump(methodNode.code)
    }
}
