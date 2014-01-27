package org.codehaus.groovy.transform.tailrec

import org.codehaus.groovy.ast.expr.*
import org.codehaus.groovy.ast.stmt.*

class ReturnStatementToIterationConverter {

    Statement convert(ReturnStatement statement, Map positionMapping) {
        def recursiveCall = statement.expression
        if (!recursiveCall.class in [
                MethodCallExpression,
                StaticMethodCallExpression
        ])
            return statement

        Map tempMapping = [:]
        Map tempDeclarations = [:]
        List<ExpressionStatement> argAssignments = []

        BlockStatement result = new BlockStatement()
        result.statementLabel = statement.statementLabel
        recursiveCall.arguments.expressions.eachWithIndex { Expression expression, index ->
            def argName = positionMapping[index].name
            def tempName = "_${argName}_"
            def argAndTempType = positionMapping[index].type
            tempMapping[argName] = [name: tempName, type: argAndTempType]
            def tempDeclaration = AstHelper.createVariableAlias(tempName, argAndTempType, argName)
            tempDeclarations[tempName] = tempDeclaration
            result.addStatement(tempDeclaration)
            def argAssignment = AstHelper.createAssignment(argName, argAndTempType, expression)
            argAssignments << argAssignment
            result.addStatement(argAssignment)
        }
        def unusedTemps = replaceAllArgUsages(argAssignments, tempMapping)
        for (String temp : unusedTemps) {
            result.statements.remove(tempDeclarations[temp])
        }
        result.addStatement(new ContinueStatement(InWhileLoopWrapper.LOOP_LABEL))

        return result
    }

    private replaceAllArgUsages(List<ExpressionStatement> nodes, tempMapping) {
        def unusedTempNames = new HashSet(tempMapping.values()*.name)
        for (ExpressionStatement statement : nodes) {
            unusedTempNames.removeAll(replaceArgUsageByTempUsage(statement.expression, tempMapping))
        }
        return unusedTempNames
    }

    private replaceArgUsageByTempUsage(BinaryExpression binary, tempMapping) {
        def usedTempNames = [] as Set
        def right = binary.rightExpression
        def argUsed = { expression ->
            if (!(expression instanceof VariableExpression)) {
                return false
            }
            if (!tempMapping.containsKey(expression.name)) {
                return false
            }
            return true
        }
        def useTempInstead = { expression ->
            def temp = tempMapping[expression.name]
            usedTempNames << temp.name
            AstHelper.createVariableReference(temp)
        }
        def replacer = new ASTNodesReplacer(when: argUsed, replaceWith: useTempInstead)
        replacer.replaceIn(right)
        return usedTempNames
    }
}