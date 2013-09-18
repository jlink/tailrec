package groovyx.transform

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
 *  {@code @TailRecursive} 
 *  long sumUp(long number, long sum) {
 *    if (number == 0)
 *      return sum;
 *    sumUp(number - 1, sum + number)
 *  }
 * </pre>
 * Known shortcomings:
 * <ul>
 * <li>Only non-void methods are currently being handled.
 * <li>The recursive call must not use default parameters.
 * <li>Many special cases (eg. returns of ternary operator) are not yet being handled.
 * </ul>
*/
@Retention(RetentionPolicy.SOURCE)
@Target([ElementType.METHOD])
@GroovyASTTransformationClass(["groovyx.transform.tailrec.TailRecursiveASTTransformation"])
public @interface TailRecursive {
}
