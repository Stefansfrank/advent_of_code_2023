package com.sf.aoc2023
import com.sf.aoc.*
import kotlin.math.abs

class Day22 : Solver {

    override fun solve(file: String) {

        // reading input
        val data = readTxtFile(file)

        // parsing blocks
        // z is the height the block settles, height is the height of the block
        // p2D is a projection onto the XY plane as a list of 2D points
        // restsOn contains the indices of all blocks this one rests on once settled
        data class Block(var z:Int, val height:Int, val p2D:List<XY>, var restsOn:Set<Int> = setOf())
        val blocks = mutableListOf<Block>()
        data.forEach { line ->
            val loc = xyzFromList( line.substringBefore('~').split(',').map { it.toInt() } )
            val loc2 = xyzFromList( line.substringAfter('~').split(',').map { it.toInt() } )
            val del = loc.del(loc2)
            val len = del.length()
            val start = if (len < 0) loc2 else loc
            val dir = if (del.y != 0) 1 else 0 + if (del.z != 0) 2 else 0
            val p2D = when (dir) {
                0 -> (0 .. abs(len)).map { XY( start.x + it, start.y)}
                1 -> (0 .. abs(len)).map { XY( start.x, start.y + it)}
                else -> listOf(XY(start.x, start.y)) }
            val height = if (dir == 2) abs(len) + 1 else 1
            blocks.add(Block(start.z, height, p2D))
            blocks.sortBy { it.z } // as this is a snapshot, we need to fall them in order
        }

        // height map describes in the XY-plane for each xy coordinate
        // - the current height of blocks
        // - the index of the block highest at that coordinate
        data class Square(val high:Int, val id:Int)
        val hmap = Map(10,10) { _, _ -> Square(0, -1)}

        // simulate the falling
        for ((bix, blk) in blocks.withIndex()) {

            // land block
            blk.z = blk.p2D.maxOf { hmap.get(it).high }

            // determine dependencies
            blk.restsOn = blk.p2D.filter{ hmap.get(it).high == blk.z }.map { hmap.get(it).id }.toSet()

            // maintain map
            blk.p2D.forEach { hmap.set(it, Square(blk.z + blk.height, bix)) }
        }

        // determine critical blocks by finding blocks that rest on just one block
        val critical = mutableSetOf<Int>()
        blocks.forEach{ if (it.restsOn.size == 1) critical.add(it.restsOn.first()) }
        critical.remove(-1) // remove the floor as critical block
        println("Part 1: $red$bold${blocks.size - critical.size}$reset")

        // as we simulate the removal of blocks,
        // this class simplifies making copies of the dependency data of blocks
        data class Dependency(val restsOn: MutableSet<Int> = mutableSetOf(), var gone:Boolean = false)

        // go only through critical blocks as others don't create chain reaction
        var chainCount = 0
        for (block in critical) {

            // fresh copy the dependencies between blocks
            val depNet = mutableListOf<Dependency>()
            blocks.forEach { depNet.add(Dependency(it.restsOn.toMutableSet())) }

            // mark current critical block for disintegration and add to queue
            val disQue = ArrayDeque<Int>().apply { this.add(block); depNet[block].gone = true }

            // remove block from dependencies and add all newly unstable blocks to queue
            while (disQue.isNotEmpty()) {
                val dis = disQue.removeFirst()
                depNet.forEach { it.restsOn.remove(dis) }
                depNet.forEachIndexed { ix, dep ->
                    if (dep.restsOn.size == 0 && (!dep.gone)) { disQue.add(ix); depNet[ix].gone = true } }
            }
            
            // count disintegrated blocks
            chainCount += depNet.filter { it.gone }.size
        }
        println("Part 2: $red$bold${chainCount - critical.size}$reset")
    }
}