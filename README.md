# TailRecursion

This project adds a @TailRecursive annotation for Groovy methods.

Starting with Groovy 2.3 it will be incorporated into Groovy's core library.

For now, have a look at my [blogpost](http://blog.johanneslink.net/2011/02/11/tail-recursion-optimization-with-groovys-ast-transformations/) which explains the reasoning behind tailrec and gives you an example.
But beware the pachage names have changed, the annotation now is groovy.transform.TailRecursive

If you are on Groovy 2.2.1 (or a similar version) you can try out tail recursions using
[prerelease 0.6](https://github.com/jlink/tailrec/releases/tag/v0.6).

Download the sourcecode to find examples in
- org.codehaus.groovy.transform.tailrec.TailRecursiveExamples.groovy
- org.codehaus.groovy.transform.tailrec.RecursiveListExamples.groovy