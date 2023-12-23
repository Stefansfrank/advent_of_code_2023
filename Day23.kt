package com.sf.aoc2023
import com.sf.aoc.*
import kotlin.math.max

// This is a template to start new days quickly
class Day23: Solver {

    override fun solve(file: String) {

        // reading input
        val map = MapChar(readTxtFile(file))
        val finish = XY(map.xdim - 2, map.ydim - 1)

        // helper structures
        val slope = mapOf('v' to 2, '^' to 0, '<' to 3, '>' to 1)

        // classes and such
        data class Path(var loc:XY, val from:XY = XY(1,0), val vis:Mask = Mask(map))
        data class Seg(val from: XY, val to: XY)
        data class GPath(var curNd:Int, var sum:Int, val visNd: MutableList<Boolean>)
        data class Target(val node:Int, val dist:Int)


        for (part in 1 .. 2) {

            // initialization
            var ndCnt = 2
            val nodes = mutableMapOf(XY(1,0) to 0, finish to 1)
            val segs = mutableMapOf<Seg, Int>()
            val initPath = Path(XY(1, 1)).apply { this.vis.on(XY(1, 0)); this.vis.on(1, 1) }
            val open = ArrayDeque<Path>().apply { this.add(initPath) }

            // BFS to build weighted graph between nodes (crossings)
            while (open.isNotEmpty()) {
                val path = open.removeFirst()
                val next = mutableListOf<XY>()

                // determine next valid positions
                if (path.loc != finish) {
                    for ((ix, loc) in path.loc.neighbors().withIndex()) {
                        val cc = map.get(path.loc)
                        if (map.get(loc) == '#' || path.vis.get(loc)) continue
                        if (part == 1 && cc != '.' && ix != slope[cc]!!) continue
                        next.add(loc)
                    }
                }

                // if crossing add to segment list
                // if crossing and not visited, kick off new paths from the crossing
                if (next.size > 1 || path.loc == finish) {
                    if (nodes[path.loc] == null) {
                        nodes[path.loc] = ndCnt++
                        next.forEach {
                            open.add(Path(it, path.loc).apply { this.vis.on(it); this.vis.on(path.loc) })
                        }
                    }
                    segs[Seg(path.from, path.loc)] = max(path.vis.cnt() - 1, segs[Seg(path.from, path.loc)] ?: 0)

                // no crossing: continue walking the current path
                } else if (next.size == 1) {
                    open.add(path.apply {
                        this.loc = next.first()
                        this.vis.on(next.first())
                    })
                }
            }

            // map segment list to a more convenient format listing all possible goals per crossing
            val segsIx = MutableList(nodes.size) { mutableListOf<Target>() }
            for (si in segs) {
                segsIx[nodes[si.key.from]!!].add(Target(nodes[si.key.to]!!, si.value))
                if (part == 2) segsIx[nodes[si.key.to]!!].add(Target(nodes[si.key.from]!!, si.value))
            }

            // DFS to find the best path through graph
            val initGPath =
                GPath(segsIx[0][0].node, segsIx[0][0].dist, MutableList(nodes.size) { false })
                    .apply { this.visNd[0] = true; this.visNd[2] = true }
            val gPaths = ArrayDeque<GPath>().apply { this.add(initGPath) }
            var maxDist = 0
            while (gPaths.isNotEmpty()) {
                val gpath = gPaths.removeLast()
                for (next in segsIx[gpath.curNd].distinct()) {
                    if (gpath.visNd[next.node]) continue
                    if (next.node == 1) { // exit reached
                        maxDist = max(gpath.sum + next.dist, maxDist)
                        continue
                    }
                    gPaths.add(GPath(next.node, gpath.sum + next.dist,
                        gpath.visNd.toMutableList().apply { this[next.node] = true }))
                }
            }
            println("Part $part: $red$bold${maxDist}$reset")
        }
    }
}