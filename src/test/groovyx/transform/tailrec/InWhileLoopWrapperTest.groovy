package groovyx.transform.tailrec

import static net.sf.cglib.asm.Opcodes.*
import static org.junit.Assert.*

import org.codehaus.groovy.ast.MethodNode
import org.codehaus.groovy.ast.builder.AstAssert;
import org.codehaus.groovy.ast.builder.AstBuilder
import org.junit.Test

class InWhileLoopWrapperTest {

	InWhileLoopWrapper wrapper = new InWhileLoopWrapper()

	@Test
	public void wrapWholeMethodBody() throws Exception {
		MethodNode method = createMethod()
		wrapper.wrap(method)
		MethodNode expected = createIdenticalMethodWithBodyInWhileLoop()
		AstAssert.assertSyntaxTree([expected], [method])
	}

	private createMethod() {
		def method = new AstBuilder().buildFromSpec {
			method('myMethod', ACC_PUBLIC, int.class) {
				parameters {}
				exceptions {}
				block { returnStatement{ constant 2 } }
			}
		}[0]
		return method
	}

	private createIdenticalMethodWithBodyInWhileLoop() {
		def method = new AstBuilder().buildFromSpec {
			method('myMethod', ACC_PUBLIC, int.class) {
				parameters {}
				exceptions {}
				block {
					whileStatement {
						booleanExpression { constant true }
						block {   returnStatement{  constant 2 } }
					}
				}
			}
		}[0]
		return method
	}
}
