package org.codehaus.groovy.transform.tailrec

import org.codehaus.groovy.ast.ASTNode
import org.codehaus.groovy.ast.expr.*

/**
 * @author Johannes Link
 */
class VariableAccessReplacer {

    Map nameAndTypeMapping = [:] //e.g.: ['myVar': [name: '_myVar', type: MyType]]
    Closure replaceBy = { nameAndType -> AstHelper.createVariableReference(nameAndType) }

    void replaceIn(ASTNode root) {
        replaceAccessToParams(root)
    }

    private void replaceAccessToParams(ASTNode root) {
        replaceAllAccessToParamsInVariableExpressions(root)
        replaceAllAccessToParamsInBooleanExpression(root)
        replaceAllAccessToParamsInNotExpression(root)
        replaceAllAccessToParamsInUnaryMinusExpression(root)
    }

    private void replaceAllAccessToParamsInVariableExpressions(ASTNode root) {
        def whenParam = { expression ->
            if (!(expression instanceof VariableExpression)) {
                return false
            }
            return nameAndTypeMapping.containsKey(expression.name)
        }
        def replaceWithLocalVariable = { expression ->
            def nameAndType = nameAndTypeMapping[expression.name]
            replaceBy(nameAndType)
        }
        def replacer = new ASTNodesReplacer(when: whenParam, replaceWith: replaceWithLocalVariable)
        replacer.replaceIn(root)
    }

    /**
     * BooleanExpressions need special handling since inner field expression is readonly
     */
    private void replaceAllAccessToParamsInBooleanExpression(ASTNode root) {
        def whenParamInNotExpression = { expression ->
            if (!(expression instanceof BooleanExpression)) {
                return false
            }
            Expression inner = expression.expression
            if (!(inner instanceof VariableExpression)) {
                return false
            }
            return nameAndTypeMapping.containsKey(inner.name)
        }
        def replaceWithLocalVariableInNotExpression = { expression ->
            def nameAndType = nameAndTypeMapping[expression.expression.name]
            new BooleanExpression(replaceBy(nameAndType))
        }
        def replacer = new ASTNodesReplacer(when: whenParamInNotExpression, replaceWith: replaceWithLocalVariableInNotExpression)
        replacer.replaceIn(root)
    }

    /**
     * NotExpressions (within BooleanExpressions) need special handling since inner field expression is readonly
     */
    private void replaceAllAccessToParamsInNotExpression(ASTNode root) {
        def whenParamInNotExpression = { expression ->
            if (!(expression instanceof BooleanExpression)) {
                return false
            }
            Expression inner = expression.expression
            if (!(inner instanceof NotExpression)) {
                return false
            }
            if (!(inner.expression instanceof VariableExpression)) {
                return false
            }
            return nameAndTypeMapping.containsKey(inner.expression.name)
        }
        def replaceWithLocalVariableInNotExpression = { expression ->
            def nameAndType = nameAndTypeMapping[expression.expression.expression.name]
            new BooleanExpression(new NotExpression(replaceBy(nameAndType)))
        }
        def replacer = new ASTNodesReplacer(when: whenParamInNotExpression, replaceWith: replaceWithLocalVariableInNotExpression)
        replacer.replaceIn(root)
    }

    /**
     * UnaryMinusExpression need special handling since inner field expression is readonly
     */
    private void replaceAllAccessToParamsInUnaryMinusExpression(ASTNode root) {
        def whenParamInUnaryMinusExpression = { expression ->
            if (!(expression instanceof UnaryMinusExpression)) {
                return false
            }
            Expression inner = expression.expression
            if (!(inner instanceof VariableExpression)) {
                return false
            }
            return nameAndTypeMapping.containsKey(inner.name)
        }
        def replaceWithLocalVariableInUnaryMinusExpression = { expression ->
            def nameAndType = nameAndTypeMapping[expression.expression.name]
            new UnaryMinusExpression(replaceBy(nameAndType))
        }
        def replacer = new ASTNodesReplacer(when: whenParamInUnaryMinusExpression, replaceWith: replaceWithLocalVariableInUnaryMinusExpression)
        replacer.replaceIn(root)
    }

}
