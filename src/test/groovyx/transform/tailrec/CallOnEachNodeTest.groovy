package groovyx.transform.tailrec

import static net.sf.cglib.asm.Opcodes.*
import static org.junit.Assert.*

import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.builder.AstBuilder
import org.codehaus.groovy.ast.stmt.BlockStatement
import org.junit.Test

class CallOnEachNodeTest {

	def visitor = new CallOnEachNode()

	@Test
	public void rootNode() {
		def myMethod = new AstBuilder().buildFromSpec {
			method('myMethod', ACC_PUBLIC, Void.TYPE) {
				parameters {}
				exceptions {}
				block {
				}
			}
		}[0]

		assertOnEachNode(callOn: myMethod.code, wasCalled: myMethod.code)
		assertOnEachNode(callOn: myMethod.code, wasCalledWithParent: [(myMethod.code): null])
	}

	@Test
	public void statementsInBlock() {
		def myMethod = new AstBuilder().buildFromSpec {
			method('myMethod', ACC_PUBLIC, Void.TYPE) {
				parameters {}
				exceptions {}
				block {
					expression { constant 1  }
					returnStatement {constant null}
				}
			}
		}[0]

		assertOnEachNode(callOn: myMethod.code, wasCalled: myMethod.code.statements)
		assertOnEachNode(callOn: myMethod.code, wasCalledWithParent: [(myMethod.code.statements[0]): myMethod.code])
	}

	@Test
	public void expressions() {
		def myExpression = new AstBuilder().buildFromSpec { expression { constant 1 } }[0]

		assertOnEachNode(callOn: myExpression, wasCalled: myExpression.expression)
		assertOnEachNode(callOn: myExpression, wasCalledWithParent: [(myExpression.expression): myExpression])
	}

	@Test
	public void returns() {
		def myReturn = new AstBuilder().buildFromSpec { returnStatement { constant 1 } }[0]

		assertOnEachNode(callOn: myReturn, wasCalled: myReturn.expression)
		assertOnEachNode(callOn: myReturn, wasCalledWithParent: [(myReturn.expression): myReturn])
	}

	private void assertOnEachNode(params) {
		if (params.wasCalled) {
			def calledOn = []
			visitor.onEachNode(params.callOn) {calledOn << it}
			params.wasCalled.each({
				assert calledOn.contains(it), "$it should have been called"
			})
		}
		if (params.wasCalledWithParent) {
			def calledWithParent = [:]
			visitor.onEachNode(params.callOn) {node, parent ->
				calledWithParent[node] = parent
			}
			params.wasCalledWithParent.each({key, value ->
				assert calledWithParent[key] == value, "$key should have been called with parent $value"
			})
		}
	}
}
