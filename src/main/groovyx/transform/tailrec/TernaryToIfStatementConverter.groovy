package groovyx.transform.tailrec

import org.codehaus.groovy.ast.expr.TernaryExpression
import org.codehaus.groovy.ast.stmt.IfStatement
import org.codehaus.groovy.ast.stmt.ReturnStatement
import org.codehaus.groovy.ast.stmt.Statement

class TernaryToIfStatementConverter {

    Statement convert(ReturnStatement statementWithInnerTernaryExpression) {
        if (!(statementWithInnerTernaryExpression.expression instanceof TernaryExpression))
            return statementWithInnerTernaryExpression
        TernaryExpression ternary = statementWithInnerTernaryExpression.expression
        return new IfStatement(ternary.booleanExpression, new ReturnStatement(ternary.trueExpression), new ReturnStatement(ternary.falseExpression))
    }
}