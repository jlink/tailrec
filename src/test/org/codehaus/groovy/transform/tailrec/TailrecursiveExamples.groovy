package org.codehaus.groovy.transform.tailrec

import groovy.transform.CompileStatic
import groovy.transform.TailRecursive
import org.junit.Test

class TailRecursiveExamples {

    @Test
    void staticallyCompiledFactorial() {
        assert StaticTargetClass.factorial(1) == 1
        assert StaticTargetClass.factorial(3) == 6
        assert StaticTargetClass.factorial(10) == 3628800
        assert StaticTargetClass.factorial(20) == 2432902008176640000L
        assert StaticTargetClass.factorial(10000).bitCount() == 54134
    }

    @Test
    void staticallyCompiledSumDown() {
        def target = new StaticTargetClass()
        assert target.sumDown(0) == 0
        assert target.sumDown(5) == 5 + 4 + 3 + 2 + 1
        assert target.sumDown(100) == 5050
        assert target.sumDown(1000000) == 500000500000
    }

    @Test
    void dynamicallyCompiledRevert() {
        assert DynamicTargetClass.revert([]) == []
        assert DynamicTargetClass.revert([1]) == [1]
        assert DynamicTargetClass.revert([1, 2, 3, 4, 5]) == [5, 4, 3, 2, 1]
    }

    @Test
    void dynamicallyCompiledStringSize() {
        def target = new DynamicTargetClass()
        assert target.stringSize("") == 0
        assert target.stringSize("a") == 1
        assert target.stringSize("abcdefghijklmnopqrstuvwxyz") == 26
    }

    @Test
    void dynamicallyCompiledReduce() {
        def target = new DynamicTargetClass()
        def plus = { Number x, Number y -> x + y }
        assert target.reduce(0, plus, ) == 0
        assert target.reduce(0, plus, 1) == 1
        assert target.reduce(0, plus, 1, 5, 10) == 16
        assert target.reduce(99, plus, 1, 5, 10, 98) == 213

        def numbersFrom1to1000 = (1..1000).collect{new BigInteger(it)}.toArray()
        assert target.reduce(new BigInteger(1), {BigInteger a, BigInteger b -> a * b}, numbersFrom1to1000).bitCount() == 3788
    }
}


@CompileStatic
class StaticTargetClass {

    @TailRecursive
    static BigInteger factorial(BigInteger number, BigInteger result = 1) {
        if (number == 1)
            return result
        return factorial(number - 1, number * result)
    }

    @TailRecursive
    long sumDown(long number, long sum = 0) {
        (number == 0) ? sum : sumDown(number - 1, sum + number)
    }
}

class DynamicTargetClass {

    @TailRecursive
    static revert(List elements, result = []) {
        if (!elements)
            return result
        else {
            def element = elements.pop()
            result.add(element)
            return revert(elements, result)
        }
    }

    @TailRecursive
    def stringSize(aString, int size = 0) {
        if (!aString)
            return size
        stringSize(aString.substring(1), ++size)
    }

    @TailRecursive
    def reduce(startValue, function, Object...elements) {
        if (elements.length == 0)
            return startValue
        def newValue = function(startValue, elements[0])
        def rest = elements.drop(1)
        return reduce(newValue, function, rest)
    }
}
