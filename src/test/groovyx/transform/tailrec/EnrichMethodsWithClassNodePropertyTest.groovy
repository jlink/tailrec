package groovyx.transform.tailrec

import org.codehaus.groovy.ast.ClassHelper
import org.codehaus.groovy.ast.ModuleNode
import org.codehaus.groovy.ast.builder.AstBuilder
import org.junit.Test

import com.sun.org.apache.bcel.internal.generic.Type;

import static net.sf.cglib.asm.Opcodes.*

public class EnrichMethodsWithClassNodePropertyTest {

	@Test
	public void oneClassOneMethod() throws Exception {
		def methodNode = createMethod("myMethod")
		def classNode = ClassHelper.make("MyClass")
		classNode.addMethod(methodNode)
		def ast = new ModuleNode(null)
		ast.addClass(classNode)

		def enricher = new EnrichMethodsWithClassNodeProperty(ast: ast)
		enricher.enrich()

		assert methodNode.classNode == classNode
	}

	@Test
	public void twoClassesTwoMethod() throws Exception {
		def methodNode1 = createMethod("method1")
		def classNode1 = ClassHelper.make("MyClass1")
		classNode1.addMethod(methodNode1)
		def methodNode2 = createMethod("method2")
		def classNode2 = ClassHelper.make("MyClass2")
		classNode2.addMethod(methodNode2)
		def ast = new ModuleNode(null)
		ast.addClass(classNode1)
		ast.addClass(classNode2)
		
		def enricher = new EnrichMethodsWithClassNodeProperty(ast: ast)
		enricher.enrich()

		assert methodNode1.classNode == classNode1
		assert methodNode2.classNode == classNode2
	}
	
	def createMethod(name) {
		new AstBuilder().buildFromSpec {
			method(name, ACC_PUBLIC, Void.TYPE) {
				parameters {}
				exceptions {}
				block {
				}
			}
		}[0]
	}
}
