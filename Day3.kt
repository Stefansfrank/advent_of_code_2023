package com.sf.aoc2023
import com.sf.aoc.*

// Disclaimer: this solution would not work if any number on the diagram would be used in more than one gear
// if that would be the case, the running variable keeping the coordinate for the associated gear symbol for
// each number would have to be a set ... not too difficult but was not necessary
class Day3 : Solver {

    override fun solve(file: String) {

        // reading input
        val data = readTxtFile(file)
        val dx = data[0].length
        val dy = data.size

        // create padded version
        val pdx = dx + 2
        val pData: List<String> = (0 until pdx).map {
            if (it == 0 || it == (dy + 1)) ".".repeat(pdx) else ".${data[it-1]}."
        }

        // running variables for the loop
        var prsNum = false   // currently parsing a number?
        var num    = 0       // the value of the number
        var symbol = false   // already found a symbol for this number?
        var gear:XY? = null  // the coordinates of the gear symbol for this number (null if none found)
                             // this would need to be a set if there are cases where one number works for several gears

        var total = 0        // the running total of numbers with a symbol  (pt 1)
        val gears: MutableMap<XY,List<Int>> = mutableMapOf() // all numbers found for gear at XY (pt 2)

        // loop through map
        for (y in 1 .. dy) {
            for (x in 1 until pdx) {

                // encountered a digit
                if (pData[y][x].isDigit()) {

                    // already parsing a number
                    if (prsNum) {
                        num    = num * 10 + pData[y][x].digitToInt()
                        symbol = symbol || pData.symbol(nxtPts(x,y))
                        if (gear == null) gear = pData.gear(nxtPts(x,y))

                    // start of a number
                    } else {
                        prsNum = true
                        symbol = pData.symbol(startPts(x,y))
                        gear   = pData.gear(startPts(x,y))
                        num    = pData[y][x].digitToInt()
                    }

                // encountered the end of a number
                } else if (prsNum) {
                    prsNum = false
                    symbol = symbol || pData.symbol(endPts(x,y))
                    if (gear == null) gear = pData.gear(endPts(x,y))

                    // running total for part 1
                    if (symbol) total += num

                    // add the number to the gear symbol map indexed by gear symbol coordinate
                    if (gear != null) gears[gear] = ((gears[gear] ?: listOf()) + num)
                }
            }
        }

        println("Part 1: $red$bold${total}$reset")
        println("Part 2: $red$bold${
            // for each gear symbol with exactly 2 numbers, we add the product
            gears.values.fold(0) { sm, gr -> sm + if (gr.size == 2) gr[0]*gr[1] else 0 }}$reset")
    }

    // the lists of neighboring points to be checked when starting, continuing and ending number parsing
    private fun nxtPts(x:Int, y:Int)   = listOf(XY(x, y-1), XY(x, y+1))
    private fun endPts(x:Int, y:Int)   = listOf(XY(x, y-1), XY(x, y), XY(x, y+1))
    private fun startPts(x:Int, y:Int) =
        listOf(XY(x-1, y-1), XY(x-1, y), XY(x-1, y+1), XY(x, y-1), XY(x, y+1))

    // checks whether symbol is present
    private fun List<String>.symbol(pts:List<XY>) =
        pts.fold(false) { sym, pt -> sym || this[pt.y][pt.x].isSym() }

    // checks whether gear is present and returns coordinates
    private fun List<String>.gear(pts:List<XY>) =
        pts.fold(null as XY?) { gear, pt -> gear ?: if (this[pt.y][pt.x] == '*') pt else null }

    // helpers
    private fun Char.isDigit() = this in '0'..'9'
    private fun Char.isSym()   = this != '.' && !this.isDigit()
}