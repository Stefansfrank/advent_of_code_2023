package com.sf.aoc2023
import com.sf.aoc.*

// Day 1
class Day1 : Solver {

    override fun solve(file: String) {

        // reading input
        val data = readTxtFile(file)

        println("Part 1: $red$bold${data.fold(0) { sum, line -> sum + compLine(line, false) } }$reset")
        println("Part 2: $red$bold${data.fold(0) { sum, line -> sum + compLine(line, true) } }$reset")
    }

    // digit words
    private val digs = listOf("one", "two", "three", "four", "five", "six", "seven", "eight", "nine")

    // computes the value for one line with a boolean for the additional text parsing in part 2
    // goes from left to right through the line and identifies digits and words (for part 2)
    // could be faster if I stop searching after first encounter and search from the right for last ...
    // ... but given the size of input and speed seems not worth the work ...
    private fun compLine(line: String, parse: Boolean):Int {
        var c0 = -1 // int value of first digit encountered
        var c1 = 0  // int value of last digit encountered
        for ((ix,c) in line.withIndex()) {

            // part 1 logic
            if (c in '0'..'9') {
                c1 = c.digitToInt()
                if (c0 == -1) c0 = c.digitToInt()

            // additional part 2 logic
            } else if (parse) {
                for ((dix,dig) in digs.withIndex()) {
                    if (line.substring(ix).startsWith(dig)) {
                        c1 = dix + 1
                        if (c0 == -1) c0 = dix + 1
                        break
                    }
                }
            }
        }
        return c0*10 + c1
    }
}