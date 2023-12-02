package com.sf.aoc2023
import com.sf.aoc.*
import kotlin.math.max

// This is a template to start new days quickly
class Day2 : Solver {

    override fun solve(file: String) {

        // reading input
        val data  = readTxtFile(file)
        val gmNo = "Game (\\d+):".toRegex()
        val draw = " (\\d+) (red|green|blue)".toRegex()

        // prep the results
        val limit = mapOf("red" to 12, "green" to 13, "blue" to 14)
        var resP1 = 0
        var resP2 = 0

        // loop through games
        games@for (game in data) {

            // determine max for each color
            val mx = mutableMapOf("red" to 0, "blue" to 0, "green" to 0)
            draw.findAll(game).forEach {
                mx[it.groupValues[2]] = max(mx[it.groupValues[2]]!!, it.groupValues[1].toInt())
            }

            // add product for part 2
            resP2 += mx.values.reduce { a, b -> a*b }

            // next game if part 1 limits were exceeded
            for (c in listOf("red", "green", "blue")) if (mx[c]!! > limit[c]!!) continue@games

            // count game for part 1
            resP1 += gmNo.find(game)!!.groupValues[1].toInt()
        }

        println("Part 1: $red$bold${resP1}$reset")
        println("Part 2: $red$bold${resP2}$reset")
    }
}