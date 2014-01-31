package groovy.transform

import org.codehaus.groovy.transform.GroovyASTTransformationClass

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

/**
 * Class annotation used to transform method with tail recursive calls into iterative methods automagically
 * since the JVM cannot do this itself. This works for both static and non-static methods.
 * <p/>
 * It allows you to write a method like this:
 * <pre>
 * class Target {
 *      {@code @TailRecursive}
 *      long sumUp(long number, long sum = 0) {
 *          if (number == 0)
 *              return sum;
 *          sumUp(number - 1, sum + number)
 *      }
 * }
 * def target = new Target()
 * assert target.sumUp(100) == 5050
 * assert target.sumUp(1000000) == 500000500000 //will blow the stack on most machines when used without {@code @TailRecursive}
 * </pre>
 *
 * {@code @TailRecursive} is supposed to work in combination with {@code @CompileStatic}
 *
 * Known shortcomings:
 * <ul>
 * <li>Only non-void methods are currently being handled. Void methods will fail compilation.
 * <li>Only direct recursion (calling the exact same method again) is supported.
 * <li>Mixing of tail calls and non-tail calls is not possible. The compiler will complain if some recursive calls cannot be handled.
 * <li>Checking if a recursive call is really tail-recursive is not very strict. You might run into cases where non-tail calls will be considered tail calls.
 * <li>All method calls with the same name and same number of arguments are considered to be recursive; no argument type matching happens.
 * <li>Non trivial continuation passing style examples do not work.
 * <li>Probably many unrecognized edge cases.
 * </ul>
 *
 * @author Johannes Link
 * @since 2.3
 */
@Retention(RetentionPolicy.SOURCE)
@Target([ElementType.METHOD])
@GroovyASTTransformationClass(["org.codehaus.groovy.transform.tailrec.TailRecursiveASTTransformation"])
public @interface TailRecursive {
}
