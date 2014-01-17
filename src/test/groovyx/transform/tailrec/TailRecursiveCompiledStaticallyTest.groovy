package groovyx.transform.tailrec

class TailRecursiveCompiledStaticallyTest extends GroovyShellTestCase {

	void testSimpleStaticAndStaticallyCompiledRecursiveMethod() {
		def target = evaluate("""
            import groovyx.transform.TailRecursive
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
	
}
