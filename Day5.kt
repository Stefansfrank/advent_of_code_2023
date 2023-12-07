package com.sf.aoc2023
import com.sf.aoc.*
import java.util.LinkedList
import java.util.Queue

class Day5 : Solver {

    override fun solve(file: String) {

        // reading input
        val data = readTxtFile(file)
        val maps: MutableList<MutableList<MapLine>> = mutableListOf()

        // parsing
        val seeds = data[0].substring(7).split(" ").map { it.toLong() }
        var ix = -1
        for (line in data.drop(1)) {
            if (line.isEmpty()) continue
            if (line.endsWith("map:")) {
                ix += 1
                maps.add(mutableListOf())
                continue
            }
            val mp = line.split(" ").map { it.toLong()}
            maps[ix].add(MapLine(RngL(mp[1], mp[1]+mp[2]-1),mp[0]-mp[1]))
        }

        // simple approach mapping individual seeds for part 1
        val result: MutableList<Long> = mutableListOf()
        for (six in seeds.indices) {
            var seed = seeds[six]
            map@for (map in maps) {
                for (ml in map) {
                    if (ml.rng.contains(seed)) {
                        seed += ml.op
                        continue@map
                    }
                }
            }
            result += seed
        }
        println("Part 1: $red$bold${result.min()}$reset")

        // Part 2 is taking the incoming set of ranges and applies mapping ranges to them
        // this mapping process might cut ranges into subsequent ranges if there is only partial overlap

        // initialize the queue with the given ranges
        var rngQueue:Queue<RngL> = LinkedList()
        for (six in seeds.indices step 2) rngQueue.add(RngL(seeds[six], seeds[six] + seeds[six+1] -1))

        // for each map we build a list of mapped ranges
        for (map in maps) {
            val mappedRngQueue:Queue<RngL> = LinkedList()

            // the individual lines of a map
            for (ml in map) {
                val tmpRngQueue:Queue<RngL> = LinkedList() // temporary collector for unmapped ranges

                // loop through unmapped ranges
                while (!rngQueue.isEmpty()) {
                    val msk = rngQueue.remove().maskWith(ml.rng)
                    tmpRngQueue.addAll(msk.unmasked)
                    if (msk.masked != null) mappedRngQueue.add(msk.masked!!.move(ml.op))
                }

                rngQueue = tmpRngQueue
            }

            // add newly mapped ranges to the ones not mapped at all
            rngQueue.addAll(mappedRngQueue)
        }

        println("Part 2: $red$bold${rngQueue.toList().minByOrNull { it.from }!!.from}$reset")
    }

    data class MapLine(val rng: RngL, val op: Long)
}