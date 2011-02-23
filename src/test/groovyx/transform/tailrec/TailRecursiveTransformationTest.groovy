package groovyx.transform.tailrec

class TailRecursiveTransformationTest extends GroovyShellTestCase {

	void testIgnoreMethodsWithoutRecursiveCall() {
		def target = evaluate("""
            import groovyx.transform.TailRecursive
            class TargetClass {
            	@TailRecursive
            	void aVoidMethod() {
            		new Object()
            	}
				@TailRecursive
				static void aStaticVoidMethod() {
					new Object()
				}
				@TailRecursive
				int aFunction() {
					42
				}
				@TailRecursive
				static int aStaticFunction() {
					43
				}
            }
            new TargetClass()
        """)
		target.aVoidMethod()
		target.aStaticVoidMethod()
		assert target.aFunction() == 42
		assert target.aStaticFunction() == 43
	}
}
