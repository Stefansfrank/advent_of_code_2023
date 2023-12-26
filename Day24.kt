package com.sf.aoc2023
import com.sf.aoc.*
import kotlin.Double.Companion.POSITIVE_INFINITY
import kotlin.math.abs
import kotlin.math.sign

class Day24 : Solver {

    override fun solve(file: String) {

        // class representing hailstone
        data class Hail(val p:XYZL, val v:XYZL) {

            // vector forms
            val pp = listOf(p.x, p.y, p.z)
            val vv = listOf(v.x, v.y, v.z)

            // 2D slope in xy plane
            val a = if (v.x == 0L) POSITIVE_INFINITY else v.y.toDouble() / v.x

            // determine 2D crossing point in XY plane
            fun intersect(h2: Hail): Pair<Double, Double>? {
                // parallel
                if (a == h2.a) return null
                // calculate crossing
                val result:Pair<Double, Double> =
                    if (a == POSITIVE_INFINITY) Pair(p.x.toDouble(), h2.a * (p.x - h2.p.x) + h2.p.y)
                    else if (h2.a == POSITIVE_INFINITY) Pair(h2.p.x.toDouble(), a * (h2.p.x - p.x) + p.y)
                    else Pair( (p.y - h2.p.y - a * p.x + h2.p.x * h2.a) / (h2.a - a),
                        p.y + a * ((p.y - h2.p.y - a * p.x + h2.p.x * h2.a) / (h2.a - a) - p.x))
                // test whether forward in time
                return if (sign(result.first - p.x) == sign(v.x.toDouble()) &&
                    sign(result.first - h2.p.x) == sign(h2.v.x.toDouble())) result else null
            }
        }

        // reading input
        val rfrom = 200_000_000_000_000.0
        val rto   = 400_000_000_000_000.0
        val hails = readTxtFile(file).map { line -> Hail(
            xyzFromListL( line.substringBefore(" @ ").split(",").map { it.trim().toLong() }),
            xyzFromListL( line.substringAfter(" @ ").split(",").map { it.trim().toLong() }))
        }

        var sum = 0
        for ((ix, hail1) in hails.dropLast(1).withIndex()) for (hail2 in hails.drop(ix + 1))
            hail1.intersect(hail2).run {
                if (this != null && first in rfrom.. rto && second in rfrom .. rto) sum++
            }
        println("Part 1: $red$bold${sum}$reset")

        // determine the velocity of the rock
        // by comparing integer candidates from hailstones of the same speed in one dimension
        val vCands = MutableList(3) {setOf<Long>()}
        for (dim in 0..2) {
            for ((ix, h1) in hails.dropLast(1).withIndex()) for (h2 in hails.drop(ix + 1)) {
                if (h1.vv[dim] == h2.vv[dim] && abs(h1.vv[dim]) > 100) {
                    val vNew = mutableSetOf<Long>()
                    for (v in -1000L..1000L)
                        if (v != h1.vv[dim] && (h2.pp[dim] - h1.pp[dim]) % (v - h1.vv[dim]) == 0L) vNew.add(v)
                    vCands[dim] = if (vCands[dim].isEmpty()) vNew else vCands[dim].intersect(vNew)
                }
            }
        }
        val rockV = XYZL(vCands[0].first(), vCands[1].first(), vCands[2].first())

        // switch to double algorithms in order to avoid rounding errors
        // calculate the slopes and offsets using two arbitrary points
        // (I picked [0] and [2] since [1] led to a division by zero for my example)
        val slopes = listOf((hails[0].v.y - rockV.y).toDouble()/(hails[0].v.x - rockV.x),
            (hails[2].v.y - rockV.y).toDouble()/(hails[2].v.x - rockV.x))
        val offsets = listOf(hails[0].p.y - (slopes[0] * hails[0].p.x),
            hails[2].p.y - (slopes[1] * hails[2].p.x))

        // derive the initial location using these two points
        val rockX = (offsets[1]-offsets[0])/(slopes[0]-slopes[1])
        val rockY = slopes[0] * rockX + offsets[0]
        val rockZ = hails[0].p.z + (hails[0].v.z - rockV.z) * (rockX - hails[0].p.x) / (hails[0].v.x - rockV.x)

        println("Part 2: $red$bold${(rockX + rockY + rockZ).toLong()}$reset")
    }
}