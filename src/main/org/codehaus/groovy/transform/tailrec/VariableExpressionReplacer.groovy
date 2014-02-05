/*
 * Copyright 2013-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.codehaus.groovy.transform.tailrec

import groovy.transform.CompileStatic
import org.codehaus.groovy.ast.ASTNode
import org.codehaus.groovy.ast.CodeVisitorSupport
import org.codehaus.groovy.ast.expr.Expression
import org.codehaus.groovy.ast.expr.ExpressionTransformer
import org.codehaus.groovy.ast.expr.VariableExpression
import org.codehaus.groovy.ast.stmt.*

import java.lang.reflect.Method

/**
 * Tool for replacing VariableExpression instances in an AST by other VariableExpression instances.
 *
 * Within @TailRecursive it is used to swap the access of arg with the access of temp vars
 *
 * @author Johannes Link
 */
@CompileStatic
class VariableExpressionReplacer extends CodeVisitorSupport {

    Closure<Boolean> when = { VariableExpression node -> false }
    Closure<VariableExpression> replaceWith = { VariableExpression variableExpression -> variableExpression }

    private ExpressionTransformer transformer

    synchronized void replaceIn(ASTNode root) {
        transformer = new VariableExpressionTransformer(when: when, replaceWith: replaceWith)
        root.visit(this)
    }

    public void visitReturnStatement(ReturnStatement statement) {
        replaceExpressionPropertyWhenNecessary(statement)
        super.visitReturnStatement(statement);
    }

    private void replaceExpressionPropertyWhenNecessary(ASTNode node, String propName = "expression") {
        Expression expr = getExpression(node, propName)

        if (expr instanceof VariableExpression) {
            if (when(expr)) {
                VariableExpression newExpr = replaceWith(expr)
                replaceExpression(node, propName, expr, newExpr)
            }
        } else {
            Expression newExpr = expr.transformExpression(transformer)
            replaceExpression(node, propName, expr, newExpr)
        }
    }

    private void replaceExpression(ASTNode node, String propName, Expression oldExpr, Expression newExpr) {
        //Use reflection to enable CompileStatic
        String setterName = 'set' + capitalizeFirst(propName)
        Method setExpressionMethod = node.class.getMethod(setterName, [Expression].toArray(new Class[1]))
        newExpr.setSourcePosition(oldExpr);
        newExpr.copyNodeMetaData(oldExpr);
        setExpressionMethod.invoke(node, [newExpr].toArray())
    }

    private Expression getExpression(ASTNode node, String propName) {
        //Use reflection to enable CompileStatic
        String getterName = 'get' + capitalizeFirst(propName)
        Method getExpressionMethod = node.class.getMethod(getterName, new Class[0])
        getExpressionMethod.invoke(node, new Object[0]) as Expression
    }

    private String capitalizeFirst(String propName) {
        propName[0].toUpperCase() + propName[1..-1]
    }


    public void visitIfElse(IfStatement ifElse) {
//        replaceIfNecessary(ifElse.booleanExpression) { BooleanExpression ex -> ifElse.booleanExpression = ex }
        super.visitIfElse(ifElse);
    }

    public void visitForLoop(ForStatement forLoop) {
        super.visitForLoop(forLoop);
    }

    public void visitWhileLoop(WhileStatement loop) {
        super.visitWhileLoop(loop);
    }

    public void visitDoWhileLoop(DoWhileStatement loop) {
        super.visitDoWhileLoop(loop);
    }

    public void visitSwitch(SwitchStatement statement) {
//        replaceInnerExpressionIfNecessary(statement)
        super.visitSwitch(statement)
    }

    public void visitCaseStatement(CaseStatement statement) {
//        replaceInnerExpressionIfNecessary(statement)
        super.visitCaseStatement(statement)
    }

    //todo: test
    public void visitExpressionStatement(ExpressionStatement statement) {
//        replaceIfNecessary(statement.expression) { Expression ex -> statement.expression = ex }
        super.visitExpressionStatement(statement);
    }

    //todo: test
    public void visitThrowStatement(ThrowStatement statement) {
//        replaceInnerExpressionIfNecessary(statement)
        super.visitThrowStatement(statement)
    }

    //todo: test
    public void visitAssertStatement(AssertStatement statement) {
//        replaceIfNecessary(statement.booleanExpression) { BooleanExpression ex -> statement.booleanExpression = ex }
//        replaceIfNecessary(statement.messageExpression) { Expression ex -> statement.messageExpression = ex }
        super.visitAssertStatement(statement)
    }

    //todo: test
    public void visitSynchronizedStatement(SynchronizedStatement statement) {
//        replaceInnerExpressionIfNecessary(statement)
        super.visitSynchronizedStatement(statement)
    }

    //todo: test
    public void visitCatchStatement(CatchStatement statement) {
        //CatchStatement.variable is readonly todo: Handle in VariableAccessReplacer or w/ reflection
        //replaceIfNecessary(statement.variable) { Parameter p -> statement.variable = p }
        super.visitCatchStatement(statement)
    }

}

@CompileStatic
class VariableExpressionTransformer implements ExpressionTransformer {

    Closure<Boolean> when
    Closure<VariableExpression> replaceWith

    @Override
    Expression transform(Expression expr) {
        if ((expr instanceof VariableExpression) && when(expr)) {
            VariableExpression newExpr = replaceWith(expr)
            newExpr.setSourcePosition(expr);
            newExpr.copyNodeMetaData(expr);
            return newExpr
        }
        return expr.transformExpression(this)
    }
}

