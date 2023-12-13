package com.sf.aoc2023
import com.sf.aoc.*
import kotlin.collections.Map
import kotlin.math.max

class Day10 : Solver {

    override fun solve(file: String) {

        // reading input into a map with Ints representing the ascii code of the symbols
        val data = readTxtFile(file)
        val pMap = MapInt(data[0].length, data.size)
        var start:XY = XY(0,0)
        data.forEachIndexed() { ix, line ->
            pMap.setLine(ix, line.map{ it.code })
            if (line.contains('S')) start = XY(line.indexOf('S'), ix)
        }

        // translates symbols into a sequence of connections
        // used to determine connected neighbours
        val tileConns: Map<Int, List<Boolean>> = mapOf(
            '-'.code to listOf(false, true, false, true),
            '|'.code to listOf(true, false, true, false),
            'L'.code to listOf(true, true, false, false),
            'F'.code to listOf(false, true, true, false),
            'J'.code to listOf(true, false, false, true),
            '7'.code to listOf(false, false, true, true),
            '.'.code to listOf(false, false, false, false)
        )

        // reverse translation of the above expressed as 4 bit number
        // again only used to determine the symbol replacing 'S'
        val typs: Map<Int, Char> = mapOf(
            10 to '-',
            5 to '|',
            3 to 'L',
            6 to 'F',
            9 to 'J',
            12 to '7'
        )

        // initialize generally useful variables
        val stp = MapInt(data[0].length, data.size) // x,y map displaying the distance to 'S' for tiles in the loop

        // initialize traversing through the loop in both directions until they meet
        val loc:MutableList<XY> = mutableListOf()   // List(2) of the current location on the loop
        val dir:MutableList<Int> = mutableListOf()  // List(2) of the current direction
        var typ = 0

        // determine the first two steps from 'S'
        for ((ix,pt) in start.neighbors(false).withIndex()) {
            if (tileConns[pMap.get(pt)]!![(ix + 2) % 4]) {
                dir.add((ix + 2) % 4)
                loc.add(pt)
                stp.set(pt, 1)
                typ += 1 shl ix
            }
        }

        // set the starting point to the correct symbol and set the stp-variable to a non-zero value
        pMap.set(start, typs[typ]!!.code )
        stp.set(start, -1)

        // traverse until the next step is already having an entry in the stp[] variable
        traverse@while(true) {
            for (trc in 0 .. 1) {
                for ((dirIx, connected) in (tileConns[pMap.get(loc[trc])])!!.withIndex()) {
                    if (connected && dirIx != dir[trc]) {
                        val newLoc = loc[trc].mv(dirIx)
                        if (stp.get(newLoc) == 0) {
                            dir[trc] = (dirIx + 2) % 4
                            stp.set(newLoc, stp.get(loc[trc]) + 1)
                            loc[trc] = newLoc
                            break
                        } else break@traverse
                    }
                }
            }
        }
        println("Part 1: $red$bold${max(stp.get(loc[0]), stp.get(loc[1]))}$reset")

        // Counting enclosed fields with a simple logic parsing input line-by-line (input ignoring unconnected pieces)
        // for each line scanned left to right the inside / outside state changes whenever:
        // - there is a '|' symbol encountered
        // - there is a combination of 'F' with a subsequent 'J' encountered (arbitrary amount of '-' in between)
        // - there is a combination of 'L' with a subsequent '7' encountered (arbitrary amount of '-' in between)
        // the combinations 'F''7' and 'L''J' with arbitrary amounts of '-' in between do not change state
        var insideCnt = 0
        var state = false // true if inside
        var hang  = 0     // hang indicates a "hanging" 'F' or 'L' waiting for resolution by a 'J' or '7'

        for (pt in pMap.xyIx) {

            if (stp.get(pt) == 0) { // we are not on a connected line - if inside, it adds to the result
                if (state) insideCnt += 1
                continue
            }

            when (pMap.get(pt)) { // dealing with all possible state changes
                ('|'.code) -> state = !state
                ('F'.code) -> hang = -1
                ('L'.code) -> hang = 1
                ('J'.code) -> {
                    if (hang == -1) state = !state
                    hang = 0
                }

                ('7'.code) -> {
                    if (hang == 1) state = !state
                    hang = 0
                }
            }
        }

        println("Part 2: $red$bold${insideCnt}$reset")
    }
}