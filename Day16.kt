package com.sf.aoc2023
import com.sf.aoc.*
import kotlin.math.max
import kotlin.math.sign

class Day16 : Solver {

    override fun solve(file: String) {

        // helper constants
        val dirBit = listOf(1,2,4,8) // the bits I use to mark 'visited in this direction'
        val bsMap = listOf(3,2,1,0)  // direction change when encountering '\'
        val fsMap = listOf(1,0,3,2)  // direction change when encountering '/'

        // reading input
        val lab = MapChar(readTxtFile(file))

        // a beam consists of loc and direction
        data class Beam(val loc:XY, val dir:Int)

        // compute coverage
        fun coverage(start:Beam):Int {
            val cover = MapInt(lab.xdim, lab.ydim) // tracking visits with direction in lowest 4 bits
            var beams = listOf(start)

            while (beams.isNotEmpty()) {
                val newBeams = mutableListOf<Beam>()
                for (beam in beams) {

                    // if visited in same direction, drop beam
                    if (cover.get(beam.loc) and dirBit[beam.dir] > 0) continue

                    // mark visit
                    cover.set(beam.loc, cover.get(beam.loc) or dirBit[beam.dir])

                    // set next location(s)
                    when (lab.get(beam.loc)) {
                        '.' -> newBeams.add(Beam(beam.loc.mv(beam.dir), beam.dir))
                        '\\' -> newBeams.add(Beam(beam.loc.mv(bsMap[beam.dir]), bsMap[beam.dir]))
                        '/' -> newBeams.add(Beam(beam.loc.mv(fsMap[beam.dir]), fsMap[beam.dir]))
                        '|' -> if (beam.dir == 0 || beam.dir == 2) {
                            newBeams.add(Beam(beam.loc.mv(beam.dir), beam.dir))
                        } else {
                            newBeams.add(Beam(beam.loc.mv(0), 0))
                            newBeams.add(Beam(beam.loc.mv(2), 2))
                        }
                        '-' -> if (beam.dir == 1 || beam.dir == 3) {
                            newBeams.add(Beam(beam.loc.mv(beam.dir), beam.dir))
                        } else {
                            newBeams.add(Beam(beam.loc.mv(1), 1))
                            newBeams.add(Beam(beam.loc.mv(3), 3))
                        }
                    }
                }

                // drop out of bounds beams
                beams = newBeams.filter { lab.contains(it.loc) }
            }
            return cover.xyIx.fold(0) { sum, loc -> sum + cover.get(loc).sign }
        }

        // part 1
        println("Part 1: $red$bold${coverage(Beam(XY(0,0), 1))}$reset")

        // brut forcing part 2 is fast enough (1.5 sec)
        var maxCover =
            lab.colIx.maxOf { max(coverage(Beam(XY(0, it), 1)), coverage(Beam(XY(lab.xdim - 1, it), 3)))}
        maxCover = max(maxCover,
            lab.rowIx.maxOf { max(coverage(Beam(XY(it,0), 2)), coverage(Beam(XY(it,lab.ydim - 1), 0)))})
        println("Part 2: $red$bold${maxCover}$reset")
    }
}