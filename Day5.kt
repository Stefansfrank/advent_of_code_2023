package com.sf.aoc2023
import com.sf.aoc.*

class Day5 : Solver {

    // This is a brut force approach for day 5 since I only had less than an hour to finish
    // both parts. One of these days I'll code the elegant approach I have in my head
    // Brute force finished in 12 minutes on my MacBook
    override fun solve(file: String) {

        // reading input
        val data = readTxtFile(file)
        val maps: MutableList<MutableList<MapLine>> = mutableListOf()

        // parsing
        var ix = -2
        var seeds: List<Long> = listOf()
        for (line in data) {
            if (ix == -2) {
                seeds = line.substring(7).split(" ").map { it.toLong() }
                ix = -1
                continue
            }
            if (line.isEmpty()) continue
            if (line.endsWith("map:")) {
                ix += 1
                maps.add(mutableListOf())
                continue
            }
            val mp = line.split(" ").map { it.toLong()}
            maps[ix].add(MapLine(LRng(mp[1], mp[1]+mp[2]-1),mp[0]-mp[1]))
        }

        // simple approach for part 1
        var result: MutableList<Long> = mutableListOf()
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

        // brut forcing part 2 since I have no time to think today, and it's already 11:30 pm
        // the clever approach would be to calculate the resulting ranges through the cuts at each stage
        // and at the end only look at the lowest numbers of each final range ...
        var minseed = 10000000000000L
        for (six in seeds.indices step 2) {
            println(".")
            for (add in 0L until seeds[six+1]) {
                var seed = seeds[six] + add
                map@for (map in maps) {
                    for (ml in map) {
                        if (ml.rng.contains(seed)) {
                            seed += ml.op
                            continue@map
                        }
                    }
                }
                if (seed < minseed) minseed = seed
            }
        }
        println("Part 2: $red$bold${minseed}$reset")
    }

    data class MapLine(val rng: LRng, val op: Long)
}