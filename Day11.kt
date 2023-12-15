package com.sf.aoc2023
import com.sf.aoc.*

class Day11 : Solver {

    override fun solve(file: String) {

        // reading input
        val data = readTxtFile(file)

        // determine empty axes
        val hEmpty = mutableListOf<Int>()
        val vEmpty = mutableListOf<Int>()
        for ((ix,line) in data.withIndex()) if (!line.contains('#')) hEmpty.add(ix)
        for (x in data[0].indices)
            if (!data.indices.fold(false) { res, y -> res || (data[y][x] == '#')}) vEmpty.add(x)

        // this is special version of the Manhattan distance between twp points
        // using the detected empty axes above to add the expansion
        fun (XY).expDist(g2:XY, amt:Long):Long {
            var result = this.mDist(g2).toLong()
            hEmpty.forEach { if (Rng(Pair(this.y,g2.y)).contains(it)) result += amt }
            vEmpty.forEach { if (Rng(Pair(this.x,g2.x)).contains(it)) result += amt }
            return result
        }

        // find galaxies
        val galaxies = mutableListOf<XY>()
        for ((lix, line) in data.withIndex()) for ((cix, c) in line.withIndex())
            if (c == '#') galaxies.add(XY(cix, lix))

        // compute the total distance
        println("Part 1: $red$bold${galaxies.dropLast(1).foldIndexed(0L) { ix, dist, glx1 ->
            dist + galaxies.drop(ix+1).fold(0L) { sm, glx2 ->
                sm + glx1.expDist(glx2, 1L)
            } }}$reset")

       println("Part 2: $red$bold${galaxies.dropLast(1).foldIndexed(0L) { ix, dist, glx1 ->
           dist + galaxies.drop(ix+1).fold(0L) { sm, glx2 ->
               sm + glx1.expDist(glx2, 999_999L)
           } }}$reset")
    }
}