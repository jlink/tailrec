package groovyx.transform.tailrec

import org.codehaus.groovy.ast.builder.AstAssert
import org.codehaus.groovy.ast.builder.AstBuilder
import org.codehaus.groovy.ast.expr.ConstantExpression
import org.codehaus.groovy.ast.stmt.ReturnStatement
import org.junit.Test

import static org.objectweb.asm.Opcodes.ACC_PUBLIC

class ReturnStatementFillerTest {

	ReturnStatementFiller transformer = new ReturnStatementFiller()

	@Test
	public void addReturnStatementToEmptyMethod() {
		def method = new AstBuilder().buildFromSpec {
			method('myMethod', ACC_PUBLIC, Object.class) {
				parameters {}
				exceptions {}
				block {
				}
			}
		}[0]
		transformer.fill(method)
		println method.code.dump()
		assert method.code.expression == ConstantExpression.NULL
//		assert method.code.statements[-1] == ReturnStatement.RETURN_NULL_OR_VOID
	}

	@Test
	public void addReturnStatementIfLastExpressionIsWhileStatement() {
		def method = new AstBuilder().buildFromSpec {
			method('myMethod', ACC_PUBLIC, Object.class) {
				parameters {}
				exceptions {}
				block {
					whileStatement {
						booleanExpression { constant true }
						block {
						}
					}
				}
			}
		}[0]
		transformer.fill(method)
//		assert (method.code.statements[-1] == ReturnStatement.RETURN_NULL_OR_VOID)
		assert method.code.statements[-1].statements[-1].expression == ConstantExpression.NULL
	}

	@Test
	public void wrapExpressionStatementsWithReturn() {
		def method = new AstBuilder().buildFromSpec {
			method('myMethod', ACC_PUBLIC, int.class) {
				parameters {}
				exceptions {}
				block {  expression { constant 4
					} }
			}
		}[0];

		transformer.fill(method);

		def lastStatement = method.code.statements[-1]
		def expected = new ReturnStatement(new ConstantExpression(4))

		AstAssert.assertSyntaxTree([expected], [lastStatement])
	}
}
