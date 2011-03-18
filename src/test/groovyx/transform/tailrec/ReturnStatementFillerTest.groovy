package groovyx.transform.tailrec

import static net.sf.cglib.asm.Opcodes.*
import static org.junit.Assert.*

import org.codehaus.groovy.ast.builder.AstBuilder
import org.codehaus.groovy.ast.stmt.ReturnStatement
import org.junit.Test

class ReturnStatementFillerTest {

	ReturnStatementFiller transformer = new ReturnStatementFiller()

	@Test
	public void addReturnStatementToEmptyMethod() {
		def method = new AstBuilder().buildFromSpec {
			method('myMethod', ACC_PUBLIC, Void.TYPE) {
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
	public void addReturnStatementIfLastExpressionIsNotExpressionStatement() {
		def method = new AstBuilder().buildFromSpec {
			method('myMethod', ACC_PUBLIC, Void.TYPE) {
				parameters {}
				exceptions {}
				block {
					whileStatement {
						booleanExpression {
							constant true
						}
						block {
						}
					}
				}
			}
		}[0]
		transformer.fill(method)
		assert (method.code.statements[-1] instanceof ReturnStatement)
	}
	
	//@Test
	public void wrapExpressionsIfStatementWithReturn() {
		def method = new AstBuilder().buildFromSpec {
			method('myMethod', ACC_PUBLIC, Void.TYPE) {
				parameters {}
				exceptions {}
				block {
					ifStatement {
						booleanExpression {
							constant true
						}
						expression {
							returnStatement {     
								constant 3
							}
						}
						expression {     
							constant 4
						}
					}
		
				}
			}
		}[0]
		transformer.fill(method)
		//what to assert?
	}
	
	@Test
	public void tt() throws Exception {
		assert method() == 2
	}
	
	public def method() {
		if (true) {
			if (false) {
				1
			} else {
				2
			}
		} else {
			3
		}
		
	}
}
