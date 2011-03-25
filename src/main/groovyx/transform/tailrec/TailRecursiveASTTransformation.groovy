package groovyx.transform.tailrec


import java.util.Map;

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
	private HasRecursiveCalls hasRecursiveCalls = new HasRecursiveCalls()

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
		println(transformationDescription(method) + ": transform recursive calls to iteration.")
		transformToIteration(method)
		checkAllRecursiveCallsHaveBeenTransformed(method)
	}

	void transformToIteration(MethodNode method) {
		if (method.isVoidMethod()) {
			transformVoidMethodToIteration(method)
		} else {
			transformNonVoidMethodToIteration(method)
		}
	}

	private void transformVoidMethodToIteration(MethodNode method) {
		throwException(method: method, message: "void methods are not supported yet!")
	}

	private void transformNonVoidMethodToIteration(MethodNode method) {
		fillInMissingReturns(method)
		wrapMethodBodyWithWhileLoop(method)
		Map nameMapping, positionMapping
		(nameMapping, positionMapping) = parameterMappingFor(method)
		replaceAllAccessToParams(method, nameMapping)
		addLocalVariablesForAllParameters(method, nameMapping) //must happen after replacing access to params
		replaceAllRecursiveReturnsWithIteration(method, positionMapping)
	}
	
	void addLocalVariablesForAllParameters(MethodNode method, Map nameMapping) {
		BlockStatement code = method.code
		nameMapping.each { paramName, localName ->
			code.statements.add(0, AstHelper.createVariableDefinition(localName, new VariableExpression(paramName)))
		}
	}
	
	void replaceAllAccessToParams(MethodNode method, Map nameMapping) {
		def whenParam = { expression ->
			if (! (expression instanceof VariableExpression)) {
				return false
			}
			return nameMapping.containsKey(expression.name)
		}
		def replaceWithLocalVariable = { expression ->
			new VariableExpression(nameMapping[expression.name])
		}
		def replacer = new ASTNodesReplacer(when: whenParam, replaceWith: replaceWithLocalVariable)
		replacer.replaceIn(method.code)
	}

	def parameterMappingFor(MethodNode method) {
		def nameMapping = [:]
		def positionMapping = [:]
		BlockStatement code = method.code
		method.parameters.eachWithIndex { Parameter param, index ->
			def paramName = param.name
			def localName = '_' + paramName + '_'
			nameMapping[paramName] = localName
			positionMapping[index] = localName
		}
		return [nameMapping, positionMapping]
	}
	
	private replaceAllRecursiveReturnsWithIteration(MethodNode method, Map positionMapping) {
		def whenRecursiveReturn = { statement ->
			if (! (statement instanceof ReturnStatement)) {
				return false
			}
			Expression inner = statement.expression
			if (! (inner instanceof MethodCallExpression)) {
				return false
			}
			return isRecursiveIn(inner, method)
		}
		def replaceWithContinueBlock = { statement ->
			def rep = new ReturnStatementToIterationConverter().convert(statement, positionMapping)
			rep
		}
		def replacer = new ASTNodesReplacer(when: whenRecursiveReturn, replaceWith: replaceWithContinueBlock)
		replacer.replaceIn(method.code)
	}

	private void wrapMethodBodyWithWhileLoop(MethodNode method) {
		new InWhileLoopWrapper().wrap(method)
	}

	private void fillInMissingReturns(MethodNode method) {
		new ReturnStatementFiller().fill(method)
	}

	private void checkAllRecursiveCallsHaveBeenTransformed(MethodNode method) {
		if (hasRecursiveMethodCalls(method)) {
			throwException(method: method, message: "not all recursive calls could be transformed!")
		}
	}

	private throwException(Map params) {
		throw new RuntimeException(transformationDescription(params.method) + ": ${params.message}")
	}

	private def transformationDescription(MethodNode method) {
		"$MY_TYPE_NAME transformation on '${method.declaringClass}.${method.name}(${method.parameters.size()} params)'"
	}

	private boolean hasRecursiveMethodCalls(MethodNode method) {
		hasRecursiveCalls.test(method)
	}

	private boolean isRecursiveIn(methodCall, MethodNode method) {
		new RecursivenessTester().isRecursive(method, methodCall)
	}
}