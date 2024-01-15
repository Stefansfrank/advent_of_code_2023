package com.sf.aoc2023
import com.sf.aoc.*

class Day17 : Solver {

    override fun solve(file: String) {

        // reading input
        val data = readTxtFile(file)
        val mp   = Map<Int>(data[0].length, data.size) { x,y -> data[y][x].code - '0'.code}
        val mpBox = Rect(XY(0,0), XY(mp.xdim - 1, mp.ydim - 1))
        val fin = XY(mp.xdim - 1,mp.ydim - 1)

        // a Path as defined by a location, current direction and # of repeated steps in that direction
        data class Path (val loc:XY, val dir:Int, val rpt:Int)

        // The two parts are different just in path restrictions
        for (part in 0 .. 1) {

            // minimal cost for each path
            val costs = mutableMapOf<Path, Int>()

            // queue of open paths
            val que = ArrayDeque<Path>().also { qq ->
                XY(0, 0).neighbors().mapIndexed { ix, xy -> Path(xy, ix, 1) }
                .filter { mpBox.contains(it.loc) }.forEach { qq.add(it); costs[it] = mp.get(it.loc) } }

            // it's faster to go through all possible paths and find the best on that reaches finish at the end
            // compared to always continue the shortest path and stop when the end is encountered the first time
            while (que.isNotEmpty()) {
                val path = que.removeFirst()
                for ((dir, next) in path.loc.neighbors().withIndex()) {

                    // don't continue if you are already at the end
                    if (path.loc == fin) continue

                    // no turnaround
                    if ((dir + 2) % 4 == path.dir) continue

                    // enforce max straight line
                    if (path.rpt == 3 + 7 * part && dir == path.dir) continue

                    // enforce min straight line (will not do anything in part 1)
                    if (path.rpt < 4 * part && dir != path.dir) continue

                    // catch if out of the bounding box
                    if (!mpBox.contains(next)) continue

                    // ok, let's construct this path
                    val nPath = Path(next, dir, if (dir == path.dir) path.rpt + 1 else 1)
                    val nCost = costs[path]!! + mp.get(next)

                    // only add to que if that path has not yet been hit with a faster cost
                    if (costs[nPath] == null || costs[nPath]!! > nCost) {
                        que.add(nPath)
                        costs[nPath] = nCost
                    }
                }
            }

            println("Part ${part + 1}: $red$bold${costs.keys.
                filter { it.loc == fin && it.rpt > 4 * part}.minOf { costs[it]!! }}$reset")
        }
    }
}