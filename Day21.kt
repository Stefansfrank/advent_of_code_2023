package com.sf.aoc2023
import com.sf.aoc.*
import java.util.LinkedList

class Day21 : Solver {

    override fun solve(file: String) {

        // reading input
        val map = MapChar(readTxtFile(file))
        val start = map.find('S')!!
        map.set(start, '.')

        // BFS to determine all possible locations with their shortest distance to start
        data class Vis(val loc:XY, val dst:Int)
        val locQue  = ArrayDeque<Vis>().also { it.add(Vis(start,0)) }
        val visited = mutableMapOf<XY, Int>()
        while (locQue.isNotEmpty()) {
            val loc = locQue.removeFirst()
            if (visited[loc.loc] != null || !map.contains(loc.loc) || map.get(loc.loc) == '#') continue
            visited[loc.loc] = loc.dst
            locQue.addAll(loc.loc.neighbors(false).map { Vis(it, loc.dst + 1) })
        }
        println("Part 1: $red$bold${visited.filter { it.value <= 64 && it.value % 2 == 0}.size}$reset")

        val dim = map.xdim

        // the areas if corners that are cut off or added
        val evnCornArea = visited.filter { it.value > 65 && it.value % 2 == 0 }.size
        val oddCornArea = visited.filter { it.value > 65 && it.value % 2 == 1 }.size

        // the max amount of box repetitions
        val reps = (((26501365) - dim/2) / dim).toLong()

        // the quadratic equation based on geometric assumptions
        val result = (reps+1) * (reps+1) * visited.filter { it.value % 2 == 1}.size +
                reps * reps * visited.filter { it.value % 2 == 0 }.size -
                (reps + 1) * oddCornArea + reps * evnCornArea
        println("Part 2: $red$bold${result}$reset")
    }
}