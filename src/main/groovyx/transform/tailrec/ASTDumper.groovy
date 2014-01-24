package groovyx.transform.tailrec

import groovy.inspect.TextTreeNodeMaker
import groovy.inspect.swingui.AstNodeToScriptAdapter
import groovy.inspect.swingui.AstNodeToScriptVisitor
import groovy.inspect.swingui.ScriptToTreeNodeAdapter
import org.codehaus.groovy.ast.ASTNode
import org.codehaus.groovy.ast.MethodNode

class ASTDumper {

    static void dump(MethodNode node) {
        def writer = new StringWriter()
        AstNodeToScriptVisitor adapter = new AstNodeToScriptVisitor(writer)
        adapter.visitMethod(node)
        println writer
    }
}
