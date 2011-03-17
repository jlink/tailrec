package groovyx.transform.tailrec

import static org.junit.Assert.*
import static net.sf.cglib.asm.Opcodes.*

import org.codehaus.groovy.ast.MethodNode
import org.codehaus.groovy.ast.builder.AstBuilder
import org.junit.Test

class CallOnEachNodeTest {

	def visitor = new CallOnEachNode()

	@Test
	public void rootNode() {
		def method = new AstBuilder().buildFromSpec {
			method('myMethod', ACC_PUBLIC, Void.TYPE) {
				parameters {}
				exceptions {}
				block {
				}
			}
		}[0]

		def params

		visitor.onEachNode(method.code) {params = it}
		assert params.is(method.code)

		visitor.onEachNode(method.code) {node, parent -> 
			params = [node, parent]
		}
		assert params == [method.code, null]
	}
}
