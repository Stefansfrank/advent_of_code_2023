package com.sf.aoc2023
import com.sf.aoc.*

class Day18 : Solver {

    override fun solve(file: String) {

        // Directions representation
        data class Dir(var dir:Int, var steps:Long, val col:String) {
            // correct function resets values according to part 2
            fun correct() {
                dir = (col[7].code - '0'.code + 1) % 4
                steps = col.substring(2, 7).toLong(radix = 16)
            }
        }

        // translation helper
        val dirIx = mapOf("D" to 2, "U" to 0, "R" to 1, "L" to 3)

        // reading input
        val data = readTxtFile(file)
        val dirs = mutableListOf<Dir>()
        data.forEach {
            it.split(' ').apply{
                dirs.add( Dir( dirIx[this[0]]!!, this[1].toLong(), this[2]))
            }
        }

        // computes the area using the Shoelace formula
        // (does not work here directly since we have 1x1x1 cubes and not points)
        fun area():Long {
            var sum = 0L
            var loc = XYL(0,0)
            dirs.forEach {dir ->
                val pos = loc.mv(dir.dir, dir.steps)
                sum += loc.x * pos.y - loc.y * pos.x
                loc = pos
            }
            return sum / 2
        }

        // the amount of cubes forming the border
        fun border():Long = dirs.sumOf { it.steps }

        // Pick's theorem in a form where it computes inner vertices with known area and border vertices
        fun picks(area:Long, border:Long):Long = area - border/2 + 1

        println("Part 1: $red$bold${picks(area(), border()) + border()}$reset")

        dirs.forEach { it.correct() }
        println("Part 2: $red$bold${picks(area(), border()) + border()}$reset")


        /*var loc2 = XYL(0,0)
        var shoe = 0L
        var perim = 0L
        for (dir in dirs) {
            val pos = loc2.mv(dir.dir2, dir.stp2)
            shoe += loc2.x * pos.y - loc2.y * pos.x
            perim += abs(pos.x - loc2.x) + abs(pos.y - loc2.y)
            loc2 = pos
        }
        shoe /= 2
        val interior = shoe - perim / 2 + 1
        println("Part 2: $red$bold${interior + perim}$reset")*/
    }
}
