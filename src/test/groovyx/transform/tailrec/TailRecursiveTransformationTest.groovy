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

	void testThrowExceptionIfNotAllRecursiveCallsCanBeTransformed() {
		shouldFail(RuntimeException) { evaluate("""
            import groovyx.transform.TailRecursive
            class TargetClass {
            	@TailRecursive
            	int aNonTailRecursiveMethod() {
            		return 1 + aNonTailRecursiveMethod() 
            	}
            }
        """)
		}
	}

	void testThrowExceptionIfNotAllStaticRecursiveCallsCanBeTransformed() {
		shouldFail(RuntimeException) { evaluate("""
            import groovyx.transform.TailRecursive
            class TargetClass {
            	@TailRecursive
            	static int aNonTailRecursiveMethod() {
            		return 1 + aNonTailRecursiveMethod() 
            	}
            }
        """)
		}
	}
	
	void _testSimpleRecursiveMethod() {
		def target = evaluate("""
            import groovyx.transform.TailRecursive
            class TargetClass {
            	@TailRecursive
            	int countDown(int zahl) {
            		if (zahl == 0)
            			return zahl
            		countDown(zahl - 1)
            	}
            }
            new TargetClass()
        """)
		assert target.countDown(5) == 0
	}

}
