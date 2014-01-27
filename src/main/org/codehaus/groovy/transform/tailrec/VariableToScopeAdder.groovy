package org.codehaus.groovy.transform.tailrec

import org.codehaus.groovy.ast.ASTNode
import org.codehaus.groovy.ast.CodeVisitorSupport
import org.codehaus.groovy.ast.Variable
import org.codehaus.groovy.ast.VariableScope
import org.codehaus.groovy.ast.stmt.BlockStatement

/**
 * @author Johannes Link
 */
class VariableToScopeAdder extends CodeVisitorSupport {

    private Variable newVariable

    def synchronized addVariable(ASTNode node, Variable variable) {
        newVariable = variable
        node.visit(this)
    }

    @Override
    void visitBlockStatement(BlockStatement block) {
        VariableScope variableScope = block.variableScope
//        println "#### $variableScope"
        if (!knowsLocalVariable(variableScope)) {
//            println "######## add: " + newVariable
            variableScope.putReferencedLocalVariable(newVariable)
//            println "############# " + variableScope + ":" + variableScope.parent
//            variableScope.referencedLocalVariablesIterator.each {
//                println "####rlv: " + it.name
//            }
        } else {
//            println "######## already knows $newVariable.name"
        }
        super.visitBlockStatement(block)
    }

    private boolean knowsLocalVariable(VariableScope scope) {
        if (scope.getReferencedLocalVariable(newVariable.name))
            return true
        if (scope.parent && knowsLocalVariable(scope.parent))
            return true
        return false
    }

}
