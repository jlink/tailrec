package org.codehaus.groovy.transform.tailrec

import groovy.inspect.swingui.AstNodeToScriptVisitor
import org.codehaus.groovy.ast.MethodNode

class ASTDumper {

    static void dump(MethodNode node) {
        def writer = new StringWriter()
        AstNodeToScriptVisitor adapter = new AstNodeToScriptVisitor(writer)
        adapter.visitMethod(node)
        println writer
    }
}
