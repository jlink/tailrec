package groovyx.transform.tailrec

import static net.sf.cglib.asm.Opcodes.*
import static org.junit.Assert.*

import org.codehaus.groovy.ast.builder.AstAssert
import org.codehaus.groovy.ast.builder.AstBuilder
import org.codehaus.groovy.ast.expr.ConstantExpression
import org.codehaus.groovy.ast.stmt.ReturnStatement
import org.junit.Test

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
		assert method.code.statements[-1] == ReturnStatement.RETURN_NULL_OR_VOID
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
		assert (method.code.statements[-1] instanceof ReturnStatement)
	}

	@Test
	public void wrapExpressionStatementsWithReturn() {
		def method = new AstBuilder().buildFromSpec {
			method('myMethod', ACC_PUBLIC, int.class) {
				parameters {}
				exceptions {}
				block {  expression { constant 4 } }
			}
		}[0];

		transformer.fill(method);

		def lastStatement = method.code.statements[-1]
		def expected = new ReturnStatement(new ConstantExpression(4))

		AstAssert.assertSyntaxTree([expected], [lastStatement])
	}
}
