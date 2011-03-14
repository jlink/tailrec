package groovyx.transform.tailrec

import static net.sf.cglib.asm.Opcodes.*
import static org.junit.Assert.*

import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.builder.AstBuilder
import org.codehaus.groovy.ast.expr.ArgumentListExpression;
import org.codehaus.groovy.ast.expr.StaticMethodCallExpression;
import org.junit.Before
import org.junit.Test


class RecursivenessTesterTest {

	RecursivenessTester tester

	@Before
	void init() {
		tester = new RecursivenessTester()
	}

	@Test
	public void recursiveVoidCallWithoutParameter() throws Exception {
		/*
		 public void myMethod() {}
		 */
		def method = new AstBuilder().buildFromSpec {
			method('myMethod', ACC_PUBLIC, Void.TYPE) {
				parameters {}
				exceptions {}
				block {
				}
			}
		}[0]

		/*
		 myMethod();
		 */
		def innerCall = new AstBuilder().buildFromSpec {
			methodCall {
				variable "this"
				constant "myMethod"
				argumentList {}
			}
		}[0]

		assert tester.isRecursive(method: method, call: innerCall)
	}

	@Test
	public void callWithDifferentName() {
		/*
		 public void myMethod() {}
		 */
		def method = new AstBuilder().buildFromSpec {
			method('myMethod', ACC_PUBLIC, Void.TYPE) {
				parameters {}
				exceptions {}
				block {
				}
			}
		}[0]

		/*
		 myMethod();
		 */
		def innerCall = new AstBuilder().buildFromSpec {
			methodCall {
				variable "this"
				constant "yourMethod"
				argumentList {}
			}
		}[0]

		assert !tester.isRecursive(method: method, call: innerCall)
	}

	@Test
	public void staticCallOnNonStaticMethod() {
		/*
		 public void myMethod() {}
		 */
		def method = new AstBuilder().buildFromSpec {
			method('myMethod', ACC_PUBLIC, Void.TYPE) {
				parameters {}
				exceptions {}
				block {
				}
			}
		}[0]
		method.metaClass.classNode = ClassHelper.make("MyClass")

		/*
		 myMethod();
		 */
		def innerCall = new StaticMethodCallExpression(ClassHelper.make("MyClass"), "myMethod", new ArgumentListExpression())

		assert !tester.isRecursive(method: method, call: innerCall)
	}

	@Test
	public void staticRecursiveCall() {
		/*
		 public static void myMethod() {}
		 */
		def method = new AstBuilder().buildFromSpec {
			method('myMethod', (ACC_PUBLIC | ACC_STATIC), Void.TYPE) {
				parameters {}
				exceptions {}
				block {
				}
			}
		}[0]
		method.metaClass.classNode = ClassHelper.make("MyClass")

		/*
		 myMethod();
		 */
		def innerCall = new StaticMethodCallExpression(ClassHelper.make("MyClass"), "myMethod", new ArgumentListExpression())

		assert tester.isRecursive(method: method, call: innerCall)
	}
}
