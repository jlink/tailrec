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

import org.codehaus.groovy.ast.ASTNode
import org.codehaus.groovy.ast.ClassHelper
import org.codehaus.groovy.ast.Parameter
import org.codehaus.groovy.ast.builder.AstBuilder
import org.codehaus.groovy.ast.expr.BinaryExpression
import org.codehaus.groovy.ast.expr.BooleanExpression
import org.codehaus.groovy.ast.expr.ClosureExpression
import org.codehaus.groovy.ast.expr.ConstantExpression
import org.codehaus.groovy.ast.expr.Expression
import org.codehaus.groovy.ast.expr.VariableExpression
import org.codehaus.groovy.ast.stmt.*
import org.codehaus.groovy.syntax.Token
import org.junit.Before
import org.junit.Test

/**
 * @author Johannes Link
 */
class VariableExpressionReplacerTest {

    static final Token EQUALS = Token.newSymbol("==", -1, -1)

    VariableExpressionReplacer replacer
    def replacements = [:]
    Closure<Boolean> when = { VariableExpression variableExpression -> replacements.containsKey variableExpression }
    Closure<VariableExpression> replaceWith = { VariableExpression variableExpression -> replacements[variableExpression] }

    @Before
    void init() {
        replacer = new VariableExpressionReplacer(when: when, replaceWith: replaceWith)
    }

    @Test
    public void replaceInReturnStatement() {
        def toReplace = aVariable("old")
        toReplace.lineNumber = 42
        def replacement = aVariable("new")
        def returnStatement = new ReturnStatement(toReplace)

        replacements[toReplace] = replacement
        replacer.replaceIn(returnStatement)

        assert returnStatement.expression == replacement
        assert replacement.lineNumber == toReplace.lineNumber
    }

    @Test
    public void replaceEmbeddedInBooleanExpression() {
        def toReplace = aVariable("old")
        toReplace.lineNumber = 42
        def replacement = aVariable("new")
        def returnStatement = new ReturnStatement(new BooleanExpression(toReplace))

        replacements[toReplace] = replacement
        replacer.replaceIn(returnStatement)

        assert returnStatement.expression.expression == replacement
        assert replacement.lineNumber == toReplace.lineNumber
    }


    @Test
    public void replaceDeeplyEmbeddedInReturnStatement() {
        def toReplace = aVariable("old")
        def replacement = aVariable("new")
        def returnStatement = new ReturnStatement(new BooleanExpression(new BinaryExpression(toReplace, EQUALS, aConstant('a'))))

        replacements[toReplace] = replacement
        replacer.replaceIn(returnStatement)

        assert returnStatement.expression.expression.leftExpression == replacement
    }


    def aReturnStatement(value) {
        new ReturnStatement(aConstant(value))
    }

    def aConstant(value) {
        new ConstantExpression(value)
    }

    def aVariable(value) {
        new VariableExpression(value)
    }

    def aBooleanExpression(value) {
        new BooleanExpression(new ConstantExpression(value))
    }
}
