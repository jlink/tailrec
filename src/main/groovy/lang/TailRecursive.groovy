package groovy.lang


import java.lang.annotation.*
import org.codehaus.groovy.transform.GroovyASTTransformationClass

@Retention(RetentionPolicy.SOURCE)
@Target([ElementType.METHOD])
@GroovyASTTransformationClass(["org.codehaus.groovy.transform.tailrec.TailRecursiveASTTransformation"])
public @interface TailRecursive {
}
