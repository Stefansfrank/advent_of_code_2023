package com.sf.aoc2023
import com.sf.aoc.*

class Day14 : Solver {

    override fun solve(file: String) {

        // reading input
        val data = readTxtFile(file)

        // the main tilt function
        fun (MapChar).tilt(dir:Int) {

            // set the limit vector containing the next hard object balls roll to
            val limit = (if (dir.isVertical()) this.colIx else this.rowIx).map {
                when (dir) {
                    0 -> 0
                    1 -> this.xdim - 1
                    2 -> this.ydim - 1
                    else -> 0
                }
            }.toMutableList()

            // go through the balls and push them to the next limit
            val add = if (dir.isDescending()) 1 else -1
            for (loc in (if (dir.isDescending()) this.xyIx else this.xyIx.asReversed())) {
                val lmtCrd = if (dir.isVertical()) loc.y else loc.x
                val curCrd = if (dir.isVertical()) loc.x else loc.y
                when (this.get(loc)) {
                    'O' -> {
                        if (limit[lmtCrd] != curCrd) {
                            if (dir.isVertical())
                                this.set(limit[lmtCrd], loc.y, 'O')
                            else
                                this.set(loc.x, limit[lmtCrd], 'O')
                            this.set(loc.x, loc.y, '.')
                        }
                        limit[lmtCrd] += add
                   }
                    '#' -> limit[lmtCrd] = curCrd + add
                }
            }

        }

        fun (MapChar).calcLoad():Int {
            var load = 0
            for (loc in this.xyIx) if (this.get(loc) == 'O') load += this.ydim - loc.y
            return load
        }

        fun (MapChar).cycle() {
            this.tilt(0)
            this.tilt(3)
            this.tilt(2)
            this.tilt(1)
        }

        val panel = MapChar(data[0].length, data.size)
        panel.xyIx.forEach { panel.set(it, data[it.y][it.x]) }
        panel.tilt(0)
        println("Part 1: $red$bold${panel.calcLoad()}$reset")

        // reset the panel
        panel.xyIx.forEach { panel.set(it, data[it.y][it.x]) }

        // cycle the panel a few times until assumed to be settled into a cyclic pattern
        repeat(500) { panel.cycle() }

        // determine the period
        val loads = mutableListOf<Int>()
        var period = 0
        repeat(100) { loads.add(panel.calcLoad()) ; panel.cycle() }
        for (len in 3 .. 100)
            if (loads.subList(0, len) == loads.subList(len, 2*len)) {
                period = len
                break
            }

        println("Part 2: $red$bold${loads[(1_000_000_000 - 500) % period]}$reset")
    }
}