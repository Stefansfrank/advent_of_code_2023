package com.sf.aoc2023
import com.sf.aoc.*
import kotlin.math.floor
import kotlin.math.sqrt

class Day6 : Solver {

    override fun solve(file: String) {

        // reading input
        val data = readTxtFile(file)
        val times = data[0].substring(9).trim().split("\\s+".toRegex()).map{ it.toLong() }
        val dists = data[1].substring(9).trim().split("\\s+".toRegex()).map{ it.toLong() }
        println("Part 1: $red$bold${(times.indices).fold(1L)
            { res, ix -> res * winSpread(times[ix], dists[ix])}}$reset")

        // re-interpret input with spaces removed
        val time = data[0].substring(9).trim().replace(" ","").toLong()
        val dist = data[1].substring(9).trim().replace(" ","").toLong()
        println("Part 2: $red$bold${winSpread(time, dist)}$reset")
    }

    // computes the amount of solutions using the quadratic equation
    fun winSpread(time: Long, dist: Long):Long {
        val disc = time * time - 4 * dist  // the discriminant of the quadratic equation
        val rtDisc = sqrt(disc.toDouble()) // the square root of the discriminant
        val result = if (time % 2 == 0L) rtDisc.roundToNextOdd() else rtDisc.roundToNextEven() // rounding solutions
        return if (result * result == disc) result - 2 else result // if the solutions are exact, they don't count
    }

    // this rounds to the nearest odd integer - rounding down if exactly even
    fun (Double).roundToNextOdd():Long {
        var tmp = floor(this + 0.5).toLong()
        if (tmp % 2 == 0L) if (this > tmp) tmp += 1 else tmp -= 1
        return tmp
    }

    // this rounds to the nearest odd integer - rounding down if exactly odd
    fun (Double).roundToNextEven():Long {
        var tmp = floor(this + 0.5).toLong()
        if (tmp % 2 == 1L) if (this > tmp) tmp += 1 else tmp -= 1
        return tmp
    }

}