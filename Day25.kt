package com.sf.aoc2023
import com.sf.aoc.*

class Day25 : Solver {

    override fun solve(file: String) {

        // Component class
        data class Comp(val id:String, val conn:MutableSet<String> = mutableSetOf()) {
            var visited = false
        }

        // Connection class ('active' used to cut a connection and 'cnt' to count the usage of a connection
        data class Conn(var active:Boolean, var cnt:Int)

        // lists and maps holding components and connections optimized for fast access
        val comps = mutableMapOf<String, Comp>()
        val conns = mutableListOf<Conn>()
        val conMp = mutableMapOf<Pair<String, String>,Conn>()

        // parsing into these lists and maps
        val data  = readTxtFile(file)
        data.forEach { line ->
            val from = line.substringBefore(':')
            val cFrom = (comps[from] ?: Comp(from)).also{ comps[from] = it }
            val tos = line.substringAfter(": ").split(' ')
            tos.forEach { to ->
                val cTo = (comps[to] ?: Comp(to).also{ comps[to] = it })
                cTo.conn.add(from)
                cFrom.conn.add(to)
                conns.add(Conn(true, 0))
                conMp[Pair(from, to)] = conns.last()
                conMp[Pair(to, from)] = conns.last()
            }
        }

        // function using BFS to find shortest path between two given components
        // all connections used in the shortest connection are getting their counter increased by one
        data class Path(val curId:String, val usedIds:List<String> = listOf())
        fun short(c1:String, c2:String):Int {
            comps.values.forEach { it.visited = false }
            var que = listOf(Path(c1, listOf(c1)))
            var length = 0
            while (que.isNotEmpty()) {
                val newQue = mutableListOf<Path>()
                que.forEach { pt ->
                    val cm = comps[pt.curId]!!
                    if (cm.id == c2) {
                        for ((ix, p) in (pt.usedIds.drop(1)).withIndex())
                            conMp[Pair(p, pt.usedIds[ix])]!!.cnt += 1 // count the connections used in shortest
                        return length
                    }
                    cm.visited = true
                    cm.conn.forEach { if (!comps[it]!!.visited) newQue.add(Path(it, pt.usedIds + it)) }
                }
                length += 1
                que = newQue
            }
            return -1
        }

        // Calculates the size of segments that are not connected (one segment if no connections are cut
        // and for most if not all of the cuts that are not the solution)
        fun segments():List<Int> {
            comps.values.forEach { it.visited = false }
            val result = mutableListOf<Int>()
            while (comps.values.any { !it.visited }) {
                val que = ArrayDeque<Comp>().apply { add(comps.values.filter { !it.visited }[0]) }
                result.add(0)
                while (que.isNotEmpty()) {
                    val cm = que.removeFirst()
                    if (!cm.visited) {
                        cm.visited = true; result[result.lastIndex] += 1
                        cm.conn.forEach {
                            if (!comps[it]!!.visited && conMp[Pair(cm.id, it)]!!.active) que.add(comps[it]!!)
                        }
                    }
                }
            }
            return result
        }

        // the list of components
        val cl = comps.values.toList()

        // here we calculate shortest path between the first ~200 components and count the usage of connections
        for (i in 0..200) for (j in i .. 201) short(cl[i].id, cl[j].id)

        // now we cut the three most used connections
        val edges = conns.sortedByDescending { it.cnt }
        (0 .. 2).forEach { edges[it].active = false }

        // ... and hope there is enough statistical certainty
        println("Part 1: $red$bold${segments().fold(1) { p, n -> p * n }}$reset")
        println("Part 2: $red$bold-$reset")
    }
}