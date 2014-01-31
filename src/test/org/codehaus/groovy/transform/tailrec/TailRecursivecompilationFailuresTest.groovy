package org.codehaus.groovy.transform.tailrec

import org.codehaus.groovy.control.CompilationFailedException

/**
 * @author Johannes Link
 */
class TailRecursiveCompilationFailuresTest extends GroovyShellTestCase {

	void testFailIfNotAllRecursiveCallsCanBeTransformed() {
		shouldFail(CompilationFailedException) { evaluate("""
            import groovy.transform.TailRecursive
            class TargetClass {
            	@TailRecursive
            	int aNonTailRecursiveMethod() {
            		return 1 + aNonTailRecursiveMethod() 
            	}
            }
        """) }
	}

	void testFailIfNotAllStaticRecursiveCallsCanBeTransformed() {
		shouldFail(CompilationFailedException) { evaluate("""
            import groovy.transform.TailRecursive
            class TargetClass {
            	@TailRecursive
            	static int aNonTailRecursiveMethod() {
            		return 1 + aNonTailRecursiveMethod() 
            	}
            }
        """) }
	}

}
