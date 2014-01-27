package org.codehaus.groovy.transform.tailrec

import groovy.transform.TailRecursive
import org.codehaus.groovy.ast.ASTNode
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.MethodNode
import org.codehaus.groovy.ast.Parameter
import org.codehaus.groovy.ast.expr.*
import org.codehaus.groovy.ast.stmt.BlockStatement
import org.codehaus.groovy.ast.stmt.ReturnStatement
import org.codehaus.groovy.classgen.ReturnAdder
import org.codehaus.groovy.control.CompilePhase
import org.codehaus.groovy.control.SourceUnit
import org.codehaus.groovy.transform.AbstractASTTransformation
import org.codehaus.groovy.transform.GroovyASTTransformation

@GroovyASTTransformation(phase = CompilePhase.CANONICALIZATION)
class TailRecursiveASTTransformation extends AbstractASTTransformation {

    private static final Class MY_CLASS = TailRecursive.class;
    private static final ClassNode MY_TYPE = new ClassNode(MY_CLASS);
    static final String MY_TYPE_NAME = "@" + MY_TYPE.getNameWithoutPackage()
    private HasRecursiveCalls hasRecursiveCalls = new HasRecursiveCalls()
    private TernaryToIfStatementConverter ternaryToIfStatement = new TernaryToIfStatementConverter()


    @Override
    public void visit(ASTNode[] nodes, SourceUnit source) {
        init(nodes, source);

        MethodNode method = nodes[1]
        if (!hasRecursiveMethodCalls(method)) {
            println(transformationDescription(method) + " skipped: No recursive calls detected.")
            return;
        }
        println(transformationDescription(method) + ": transform recursive calls to iteration.")
        transformToIteration(method)
        ensureAllRecursiveCallsHaveBeenTransformed(method)
    }

    void transformToIteration(MethodNode method) {
        if (method.isVoidMethod()) {
            transformVoidMethodToIteration(method)
        } else {
            transformNonVoidMethodToIteration(method)
        }
    }

    private void transformVoidMethodToIteration(MethodNode method) {
        addError("Void methods are not supported yet", method)
    }

    private void transformNonVoidMethodToIteration(MethodNode method) {
        addMissingDefaultReturnStatement(method)
        replaceReturnsWithTernariesToIfStatements(method)
        wrapMethodBodyWithWhileLoop(method)
        def (nameAndTypeMapping, positionMapping) = parameterMappingFor(method)
        replaceAllAccessToParams(method, nameAndTypeMapping)
        replaceAllAccessToParamsInBooleanExpression(method, nameAndTypeMapping)
        replaceAllAccessToParamsInNotExpression(method, nameAndTypeMapping)
        addLocalVariablesForAllParameters(method, nameAndTypeMapping) //must happen after replacing access to params
        replaceAllRecursiveReturnsWithIteration(method, positionMapping)

//        ASTDumper.dump(method)
    }

    private void replaceReturnsWithTernariesToIfStatements(MethodNode method) {
        def whenReturnWithTernary = { expression ->
            if (!(expression instanceof ReturnStatement)) {
                return false
            }
            return (expression.expression instanceof TernaryExpression)
        }
        def replaceWithIfStatement = { expression ->
            ternaryToIfStatement.convert(expression)
        }
        def replacer = new ASTNodesReplacer(when: whenReturnWithTernary, replaceWith: replaceWithIfStatement)
        replacer.replaceIn(method.code)

    }

    void addLocalVariablesForAllParameters(MethodNode method, Map nameAndTypeMapping) {
        BlockStatement code = method.code
        nameAndTypeMapping.each { paramName, localNameAndType ->
            code.statements.add(0, AstHelper.createVariableDefinition(localNameAndType.name, localNameAndType.type, new VariableExpression(paramName, localNameAndType.type)))
        }
    }

    void replaceAllAccessToParams(MethodNode method, Map nameAndTypeMapping) {
        def whenParam = { expression ->
            if (!(expression instanceof VariableExpression)) {
                return false
            }
            return nameAndTypeMapping.containsKey(expression.name)
        }
        def replaceWithLocalVariable = { expression ->
            def nameAndType = nameAndTypeMapping[expression.name]
            AstHelper.createVariableReference(nameAndType)
        }
        def replacer = new ASTNodesReplacer(when: whenParam, replaceWith: replaceWithLocalVariable)
        replacer.replaceIn(method.code)
    }

    /**
     * BooleanExpressions need special handling since inner field expression is readonly
     */
    void replaceAllAccessToParamsInBooleanExpression(MethodNode method, Map nameAndTypeMapping) {
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
            new BooleanExpression(AstHelper.createVariableReference(nameAndType))
        }
        def replacer = new ASTNodesReplacer(when: whenParamInNotExpression, replaceWith: replaceWithLocalVariableInNotExpression)
        replacer.replaceIn(method.code)
    }

    /**
     * NotExpressions (within BooleanExpressions) need special handling since inner field expression is readonly
     */
    void replaceAllAccessToParamsInNotExpression(MethodNode method, Map nameAndTypeMapping) {
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
            new BooleanExpression(new NotExpression(AstHelper.createVariableReference(nameAndType)))
        }
        def replacer = new ASTNodesReplacer(when: whenParamInNotExpression, replaceWith: replaceWithLocalVariableInNotExpression)
        replacer.replaceIn(method.code)
    }

    def parameterMappingFor(MethodNode method) {
        def nameAndTypeMapping = [:]
        def positionMapping = [:]
        BlockStatement code = method.code
        method.parameters.eachWithIndex { Parameter param, index ->
            def paramName = param.name
            def paramType = param.type
            def localName = '_' + paramName + '_'
            nameAndTypeMapping[paramName] = [name: localName, type: paramType]
            positionMapping[index] = [name: localName, type: paramType]
        }
        return [nameAndTypeMapping, positionMapping]
    }

    private replaceAllRecursiveReturnsWithIteration(MethodNode method, Map positionMapping) {
        def whenRecursiveReturn = { statement ->
            if (!(statement instanceof ReturnStatement)) {
                return false
            }
            Expression inner = statement.expression
            if (!(inner instanceof MethodCallExpression) && !(inner instanceof StaticMethodCallExpression)) {
                return false
            }
            return isRecursiveIn(inner, method)
        }
        def replaceWithContinueBlock = { statement ->
            new ReturnStatementToIterationConverter().convert(statement, positionMapping)
        }
        def replacer = new ASTNodesReplacer(when: whenRecursiveReturn, replaceWith: replaceWithContinueBlock)
        replacer.replaceIn(method.code)
    }

    private void wrapMethodBodyWithWhileLoop(MethodNode method) {
        new InWhileLoopWrapper().wrap(method)
    }

    private void addMissingDefaultReturnStatement(MethodNode method) {
        new ReturnAdder().visitMethod(method)
    }

    private void ensureAllRecursiveCallsHaveBeenTransformed(MethodNode method) {
        def remainingRecursiveCalls = new CollectRecursiveCalls().collect(method)
        remainingRecursiveCalls.each {
            addError("Recursive call could not be transformed.", it)
        }
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