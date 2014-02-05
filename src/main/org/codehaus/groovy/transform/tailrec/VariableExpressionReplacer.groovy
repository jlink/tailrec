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
import org.codehaus.groovy.ast.expr.BooleanExpression
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

    public void visitIfElse(IfStatement ifElse) {
        replaceExpressionPropertyWhenNecessary(ifElse, 'booleanExpression', BooleanExpression)
        super.visitIfElse(ifElse);
    }

    public void visitForLoop(ForStatement forLoop) {
        replaceExpressionPropertyWhenNecessary(forLoop, 'collectionExpression')
        super.visitForLoop(forLoop);
    }

    //todo: test
    public void visitWhileLoop(WhileStatement loop) {
        replaceExpressionPropertyWhenNecessary(loop, 'booleanExpression', BooleanExpression)
        super.visitWhileLoop(loop);
    }

    //todo: test
    public void visitDoWhileLoop(DoWhileStatement loop) {
        replaceExpressionPropertyWhenNecessary(loop, 'booleanExpression', BooleanExpression)
        super.visitDoWhileLoop(loop);
    }

    //todo: test
    public void visitSwitch(SwitchStatement statement) {
        replaceExpressionPropertyWhenNecessary(statement)
        super.visitSwitch(statement)
    }

    //todo: test
    public void visitCaseStatement(CaseStatement statement) {
        replaceExpressionPropertyWhenNecessary(statement)
        super.visitCaseStatement(statement)
    }

    //todo: test
    public void visitExpressionStatement(ExpressionStatement statement) {
        replaceExpressionPropertyWhenNecessary(statement)
        super.visitExpressionStatement(statement);
    }

    //todo: test
    public void visitThrowStatement(ThrowStatement statement) {
        replaceExpressionPropertyWhenNecessary(statement)
        super.visitThrowStatement(statement)
    }

    //todo: test
    public void visitAssertStatement(AssertStatement statement) {
        replaceExpressionPropertyWhenNecessary(statement, 'booleanExpression', BooleanExpression)
        replaceExpressionPropertyWhenNecessary(statement, 'messageExpression', BooleanExpression)
        super.visitAssertStatement(statement)
    }

    //todo: test
    public void visitSynchronizedStatement(SynchronizedStatement statement) {
        replaceExpressionPropertyWhenNecessary(statement)
        super.visitSynchronizedStatement(statement)
    }

    private void replaceExpressionPropertyWhenNecessary(ASTNode node, String propName = "expression", Class propClass = Expression) {
        Expression expr = getExpression(node, propName)

        if (expr instanceof VariableExpression) {
            if (when(expr)) {
                VariableExpression newExpr = replaceWith(expr)
                replaceExpression(node, propName, propClass, expr, newExpr)
            }
        } else {
            Expression newExpr = expr.transformExpression(transformer)
            replaceExpression(node, propName, propClass, expr, newExpr)
        }
    }

    private void replaceExpression(ASTNode node, String propName, Class propClass, Expression oldExpr, Expression newExpr) {
        //Use reflection to enable CompileStatic
        String setterName = 'set' + capitalizeFirst(propName)
        Method setExpressionMethod = node.class.getMethod(setterName, [propClass].toArray(new Class[1]))
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


}


