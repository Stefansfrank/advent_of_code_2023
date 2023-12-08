package com.sf.aoc

fun gcd(a: Long, b: Long):Long {
    var y = b; var x = a
    while (y > 0) {
        val tmp = y
        y = x % y
        x = tmp
    }
    return x
}

fun gcdList(inp: List<Long>):Long {
    var result = inp[0]
    (1 until inp.size).forEach { result = gcd(result, inp[it]) }
    return result
}

fun lcm(a:Long, b:Long):Long {
    return a * (b / gcd(a, b));
}

fun lcmList(inp: List<Long>): Long {
    var result = inp[0]
    (1 until inp.size).forEach { result = lcm(result, inp[it]) }
    return result;
}
