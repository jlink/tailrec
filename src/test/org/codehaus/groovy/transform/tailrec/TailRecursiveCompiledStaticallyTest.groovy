package org.codehaus.groovy.transform.tailrec

import org.codehaus.groovy.control.CompilationFailedException

class TailRecursiveCompiledStaticallyTest extends GroovyShellTestCase {

    void testStaticallyCompiledRecursiveMethod() {
        def target = evaluate("""
            import groovy.transform.TailRecursive
            import groovy.transform.CompileStatic

            @CompileStatic
            class TargetClass {
            	@TailRecursive
            	static int staticCountDown(int zahl) {
            		if (zahl == 0)
            			return zahl
            		return staticCountDown(zahl - 1)
            	}
            }
            new TargetClass()
        """)
        assert target.staticCountDown(5) == 0
        assert target.staticCountDown(100000) == 0
    }

    void testRecursiveMethodWhichCannotBeStaticallyCompiled() {
        shouldFail(CompilationFailedException) { evaluate("""
            import groovy.transform.TailRecursive
            import groovy.transform.CompileStatic

            @CompileStatic
            class TargetClass {
            	@TailRecursive
            	static int staticCountDown(zahl) {
            		if (zahl == 0)
            			return zahl
            		return staticCountDown(zahl - 1)
            	}
            }
            new TargetClass()
        """) }
    }

    void testTypeCheckedRecursiveMethod() {
        def target = evaluate('''
            import groovy.transform.TailRecursive
            import groovy.transform.TypeChecked

            @TypeChecked
            class TargetClass {
				@TailRecursive
				String fillString(long number, String filled) {
					if (number == 0)
						return filled;
					fillString(number - 1, filled + "+")
				}
        	}
            new TargetClass()
		''')

        assert target.fillString(0, "") == ""
        assert target.fillString(1, "") == "+"
        assert target.fillString(5, "") == "+++++"
        assert target.fillString(10000, "") == "+" * 10000
    }

    void testStaticallyCompiledRecursiveFunctionWithTwoParameters() {
        def target = evaluate('''
            import groovy.transform.TailRecursive
            import groovy.transform.CompileStatic

            @CompileStatic
            class TargetClass {
				@TailRecursive
				String fillString(long number, String filled) {
					if (number == 0)
						return filled;
					fillString(number - 1, filled + "+")
				}
        	}
            new TargetClass()
		''')

        assert target.fillString(0, "") == ""
        assert target.fillString(1, "") == "+"
        assert target.fillString(5, "") == "+++++"
        assert target.fillString(10000, "") == "+" * 10000
    }

}
