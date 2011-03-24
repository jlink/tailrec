package groovyx.transform.tailrec

import static net.sf.cglib.asm.Opcodes.*
import static org.junit.Assert.*

import org.codehaus.groovy.ast.builder.AstBuilder
import org.junit.Test

class ParameterMappingTest {

	TailRecursiveASTTransformation transformation = new TailRecursiveASTTransformation()

	@Test
	public void emptyMethod() throws Exception {
		def myMethod = new AstBuilder().buildFromSpec {
			method('myMethod', ACC_PUBLIC, int.class) {
				parameters {}
				exceptions {}
				block {
				}
			}
		}[0]

		def nameMapping, positionMapping
		(nameMapping, positionMapping) = transformation.parameterMappingFor(myMethod)

		assert nameMapping == [:]
		assert positionMapping == [:]
	}

	@Test
	public void oneParameter() throws Exception {
		def myMethod = new AstBuilder().buildFromSpec {
			method('myMethod', ACC_PUBLIC, int.class) {
				parameters { parameter 'one': int.class }
				exceptions {}
				block {
				}
			}
		}[0]

		def nameMapping, positionMapping
		(nameMapping, positionMapping) = transformation.parameterMappingFor(myMethod)

		assert nameMapping == [one: '_one_']
		assert positionMapping == [0: '_one_']
	}

	@Test
	public void severalParameters() throws Exception {
		def myMethod = new AstBuilder().buildFromSpec {
			method('myMethod', ACC_PUBLIC, int.class) {
				parameters {
					parameter 'one': int.class
					parameter 'two': int.class
					parameter 'three': int.class
				}
				exceptions {}
				block {
				}
			}
		}[0]

		def nameMapping, positionMapping
		(nameMapping, positionMapping) = transformation.parameterMappingFor(myMethod)

		assert nameMapping == [one: '_one_', two: '_two_', three: '_three_']
		assert positionMapping == [0: '_one_', 1: '_two_', 2: '_three_']
	}
}
