package groovyx.transform.tailrec

import static org.junit.Assert.*

import org.codehaus.groovy.ast.*
import org.codehaus.groovy.ast.builder.AstAssert
import org.codehaus.groovy.ast.builder.AstBuilder
import org.codehaus.groovy.ast.expr.*
import org.codehaus.groovy.ast.stmt.*
import org.junit.Test

class ReturnStatementToIterationConverterTest {


	@Test
	public void oneConstantParameter() {
		ReturnStatement statement = new AstBuilder().buildFromSpec {
			returnStatement {
				methodCall {
					variable "this"
					constant "myMethod"
					argumentList { constant 1 }
				}
			}
		}[0]

		BlockStatement expected = new AstBuilder().buildFromSpec {
			block {
				expression {
					binary {
						variable '_a'
						token '='
						constant 1
					}
				}
				continueStatement()
			}
		}[0]

		Map positionMapping = [0:'_a']
		def block = new ReturnStatementToIterationConverter().convert(statement, positionMapping)

		AstAssert.assertSyntaxTree([expected], [block])
	}

	@Test
	public void twoParametersOnlyOneUsedInRecursiveCall() {
		BlockStatement expected = new BlockStatement()
		expected.addStatement(new ExpressionStatement(new DeclarationExpression(new VariableExpression("_a_"), AstHelper.ASSIGN, new VariableExpression("_a"))))
		expected.addStatement(new ExpressionStatement(new BinaryExpression(new VariableExpression("_a"), AstHelper.ASSIGN, new ConstantExpression(1))))
		expected.addStatement(new ExpressionStatement(new BinaryExpression(new VariableExpression("_b"), AstHelper.ASSIGN,
				new BinaryExpression(new VariableExpression("_a_"), AstHelper.PLUS, new ConstantExpression(1)))))
		expected.addStatement(new ContinueStatement())

		ReturnStatement statement = new AstBuilder().buildFromString( """
		return(myMethod(1, _a + 1))
		""")[0].statements[0]


		Map positionMapping = [0:'_a', 1:'_b']

		def block = new ReturnStatementToIterationConverter().convert(statement, positionMapping)

		AstAssert.assertSyntaxTree([expected], [block])
	}

	@Test
	public void twoParametersBothUsedInRecursiveCall() {
		BlockStatement expected = new BlockStatement()
		expected.addStatement(new ExpressionStatement(new DeclarationExpression(new VariableExpression("_a_"), AstHelper.ASSIGN, new VariableExpression("_a"))))
		expected.addStatement(new ExpressionStatement(new BinaryExpression(new VariableExpression("_a"), AstHelper.ASSIGN,
				new BinaryExpression(new VariableExpression("_a_"), AstHelper.PLUS, new ConstantExpression(1)))))
		expected.addStatement(new ExpressionStatement(new DeclarationExpression(new VariableExpression("_b_"), AstHelper.ASSIGN, new VariableExpression("_b"))))
		expected.addStatement(new ExpressionStatement(
				new BinaryExpression(new VariableExpression("_b"), AstHelper.ASSIGN,
				new BinaryExpression(new VariableExpression("_b_"), AstHelper.PLUS, new VariableExpression("_a_")))))
		expected.addStatement(new ContinueStatement())

		ReturnStatement statement = new AstBuilder().buildFromString( """
		return(myMethod(_a + 1, _b + _a))
				""")[0].statements[0]


		Map positionMapping = [0:'_a', 1:'_b']

		def block = new ReturnStatementToIterationConverter().convert(statement, positionMapping)

		AstAssert.assertSyntaxTree([expected], [block])
	}
}
