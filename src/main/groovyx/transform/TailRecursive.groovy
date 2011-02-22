package groovyx.transform


import java.lang.annotation.*
import org.codehaus.groovy.transform.GroovyASTTransformationClass

@Retention(RetentionPolicy.SOURCE)
@Target([ElementType.METHOD])
@GroovyASTTransformationClass(["groovyx.transform.tailrec.TailRecursiveASTTransformation"])
public @interface TailRecursive {
}
