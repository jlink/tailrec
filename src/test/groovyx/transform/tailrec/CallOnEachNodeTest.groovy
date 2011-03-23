package groovyx.transform.tailrec

import static net.sf.cglib.asm.Opcodes.*
import static org.junit.Assert.*

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

		def calledOn = []
		visitor.onEachNode(myMethod.code) {calledOn << it}
		assert calledOn.contains(myMethod.code) 
		
		def calledWithParent = [:] 
		visitor.onEachNode(myMethod.code) {node, parent ->
			calledWithParent[node] = parent
		}
		assert calledWithParent[myMethod.code] == null
	}

	@Test
	public void statementsInBlock() {
		def myMethod = new AstBuilder().buildFromSpec {
			method('myMethod', ACC_PUBLIC, Void.TYPE) {
				parameters {}
				exceptions {}
				block {
					expression {
						constant { 1 }
					}
				}
			}
		}[0]

		def calledOn = []
		visitor.onEachNode(myMethod.code) {calledOn << it}
		assert calledOn.contains(myMethod.code) 
		assert calledOn.contains(myMethod.code.statements[0])
		
		def calledWithParent = [:] 
		visitor.onEachNode(myMethod.code) {node, parent ->
			calledWithParent[node] = parent
		}
		assert calledWithParent[myMethod.code.statements[0]] == myMethod.code
	}
}
