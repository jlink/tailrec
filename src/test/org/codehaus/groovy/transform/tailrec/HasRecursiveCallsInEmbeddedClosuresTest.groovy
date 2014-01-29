package org.codehaus.groovy.transform.tailrec

import org.codehaus.groovy.ast.builder.AstBuilder
import org.codehaus.groovy.control.CompilePhase
import org.junit.Before
import org.junit.Test

/**
 * @author Johannes Link
 */
class HasRecursiveCallsInEmbeddedClosuresTest {

    HasRecursiveCallsInEmbeddedClosures tester

    @Before
    void init() {
        tester = new HasRecursiveCallsInEmbeddedClosures()
    }

    @Test
    public void recursiveCallEmbeddedInClosure() throws Exception {
        def method = new AstBuilder().buildFromString(CompilePhase.SEMANTIC_ANALYSIS, true, '''
            class Target {
                int myMethod(int n) {
                    def next = { r1 ->
                        return myMethod(n - 2)
                    }
                    return myMethod(n - 1)
                }
            }
		''')[1].getMethods('myMethod')[0]

        assert tester.test(method)
    }

    @Test
    public void recursiveCallDeeplyEmbeddedInClosure() throws Exception {
        def method = new AstBuilder().buildFromString(CompilePhase.SEMANTIC_ANALYSIS, true, '''
            class Target {
                int myMethod(int n) {
                    def next = { r1 ->
                        def inner = { return myMethod(n-2) }
                        return inner()
                    }
                    return myMethod(n - 1)
                }
            }
		''')[1].getMethods('myMethod')[0]

        assert tester.test(method)
    }

    @Test
    public void recursiveCallNotEmbeddedInClosure() throws Exception {
        def method = new AstBuilder().buildFromString(CompilePhase.SEMANTIC_ANALYSIS, true, '''
            class Target {
                int myMethod(int n) {
                    def next = { r1 ->
                        return yourMethod(n - 2)
                    }
                    return myMethod(n - 1)
                }
            }
		''')[1].getMethods('myMethod')[0]

        assert !tester.test(method)
    }

}
