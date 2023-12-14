package com.sf.aoc2023
import com.sf.aoc.*
import kotlin.math.min

class Day13 : Solver {

    override fun solve(file: String) {

        // tile class internally computing a column-wise representation and mirror axes right away
        data class Tile(val rows:MutableList<String>) {

            // column representation
            val cols = rows[0].indices.map { rows.fold("") { sst, row -> sst + row[it]} }.toMutableList()
            val vAxes = findVAxes()
            val hAxes = findHAxes()

            // helper for the flip function
            val flp:Map<Char, Char> = mapOf('.' to '#', '#' to '.')

            // finds all vertical mirror axes
            fun findVAxes():List<Int> {
                val result = mutableListOf<Int>()
                vAxisLoop@for (vAxis in 1 until cols.size) {
                    val stps = min(vAxis, cols.size - vAxis)
                    for (vIx in 1 .. stps) if (cols[vAxis-vIx] != cols[vAxis+vIx-1]) continue@vAxisLoop
                    result.add(vAxis)
                }
                return result
            }

            // finds all horizontal mirror axis
            fun findHAxes():List<Int> {
                val result = mutableListOf<Int>()
                hAxisLoop@for (hAxis in 1 until rows.size) {
                    val stps = min(hAxis, rows.size - hAxis)
                    for (hIx in 1 .. stps) if (rows[hAxis-hIx] != rows[hAxis+hIx-1]) continue@hAxisLoop
                    result.add(hAxis)
                }
                return result
            }

            // tries to flip every tile and returns index of any new axis found
            // negative result = horizontal axis, positive = vertical axis
            fun smudgeScan():Int {
                var result = 0
                found@for (y in rows.indices) {
                    for (x in cols.indices) {
                        flip(x,y)
                        val nVAxes = findVAxes()
                        if (nVAxes != vAxes && nVAxes.isNotEmpty()) {
                            result = if (nVAxes.size == 1) nVAxes[0]
                                        else if (nVAxes[0] == vAxes[0]) nVAxes[1] else nVAxes[0]
                            break@found
                        }
                        val nHAxes = findHAxes()
                        if (nHAxes != hAxes && nHAxes.isNotEmpty()) {
                            result = if (nHAxes.size == 1) -nHAxes[0]
                                        else if (nHAxes[0] == hAxes[0]) -nHAxes[1] else -nHAxes[0]
                            break@found
                        }
                        flip(x,y)
                    }
                }
                return result
            }

            // flip one tile
            fun flip(x:Int, y:Int) {
                rows[y] = rows[y].substring(0,x) + flp[rows[y][x]]!! + rows[y].substring(x+1)
                cols[x] = cols[x].substring(0,y) + flp[cols[x][y]]!! + cols[x].substring(y+1)
            }
        }

        // reading input
        val data = readTxtFile(file)
        val tiles:MutableList<Tile> = mutableListOf()

        // parse into tiles
        var tile:MutableList<String> = mutableListOf()
        for (line in data) {
            if (line.isEmpty()) { tiles.add(Tile(tile)); tile = mutableListOf<String>() }
            else tile.add(line)
        }
        tiles.add(Tile(tile))

        // Part 1
        var cnt = 0
        tileLoop@for (tile in tiles) cnt += if (tile.vAxes.isNotEmpty()) tile.vAxes[0] else tile.hAxes[0] * 100
        println("Part 1: $red$bold${cnt}$reset")

        // Part 2
        cnt = 0
        tileLoop@for ((ix,tile) in tiles.withIndex()) {
            val sax = tile.smudgeScan()
            cnt += if (sax > 0) sax else sax * -100
        }
        println("Part 2: $red$bold${cnt}$reset")
    }
}