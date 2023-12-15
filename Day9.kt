package com.sf.aoc2023
import com.sf.aoc.*

// This is a template to start new days quickly
class Day9 : Solver {

    override fun solve(file: String) {

        // reading input
        val data = readTxtFile(file)
        val samples = data.map { line -> line.split(" ").map { it.toInt() } }

        fun (List<Int>).extrapolate():Pair<Int, Int> {
            val devs:MutableList<List<Int>> = mutableListOf(this)
            while (devs.last().filter { it != 0 }.isNotEmpty())
                devs.add(devs.last().drop(1).mapIndexed { ix, it -> it - devs.last()[ix] })
            val exBack = mutableListOf(0)
            val exFront = mutableListOf(0)
            for (d in devs.dropLast(1).asReversed()) {
                exBack.add(d.last() + exBack.last())
                exFront.add(d.first() - exFront.last())
            }
            return Pair(exBack.last(), exFront.last())
        }

        val result = samples.map{ it.extrapolate() }
        println("Part 1: $red$bold${result.sumOf { it.first }}$reset")
        println("Part 2: $red$bold${result.sumOf { it.second }}$reset")
    }
}