package groovyx.transform.tailrec

import org.codehaus.groovy.ast.builder.*
import org.codehaus.groovy.ast.expr.*
import org.codehaus.groovy.ast.stmt.*


class ReturnStatementToIterationConverter {

	Statement convert(ReturnStatement statement, Map positionMapping) {
		if (! (statement.getExpression() instanceof MethodCallExpression))
			return statement
		MethodCallExpression recursiveCall = statement.getExpression()

		Map tempMapping = [:]
		Map tempDeclarations = [:]
		List<ExpressionStatement> argAssignments = []

		BlockStatement result = new BlockStatement()
		recursiveCall.arguments.expressions.eachWithIndex { Expression expression, index ->
			def argName = positionMapping[index]
			def tempName = "_${argName}_"
			tempMapping[argName] = tempName
			def tempDeclaration = AstHelper.createVariableAlias(tempName, argName)
			tempDeclarations[tempName] = tempDeclaration
			result.addStatement(tempDeclaration)
			def argAssignment = AstHelper.createAssignment(argName, expression)
			argAssignments << argAssignment
			result.addStatement(argAssignment)
		}
		def unusedTemps = replaceAllArgUsages(argAssignments, tempMapping)
		for (String temp : unusedTemps) {
			result.statements.remove(tempDeclarations[temp])
		}
		result.addStatement(new ContinueStatement())

		return result
	}

	private replaceAllArgUsages(List<ExpressionStatement> nodes, tempMapping) {
		def unUsedTemps = new HashSet(tempMapping.values())
		for (ExpressionStatement statement : nodes) {
			unUsedTemps.removeAll(replaceArgUsageByTempUsage(statement.expression, tempMapping))
		}
		return unUsedTemps
	}

	private replaceArgUsageByTempUsage(BinaryExpression binary, tempMapping) {
		def usedTemps = []as Set
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
		def useTempInstead = {expression ->
			def tempName = tempMapping[expression.name]
			usedTemps << tempName
			new VariableExpression(tempName)
		}
		def replacer = new ASTNodesReplacer(when: argUsed, replaceWith: useTempInstead)
		replacer.replaceIn(right)
		return usedTemps
	}
}
