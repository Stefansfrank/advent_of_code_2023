package com.sf.aoc2023
import com.sf.aoc.*

class Day8 : Solver {

    override fun solve(file: String) {

        // reading and parsing input resulting in a map with entries like mp["AAA"] = Array("BBB","CCC")
        val data = readTxtFile(file)
        val dirs = data[0].map { if (it == 'L') 0 else 1}
        val rx = "[A-Z0-9][A-Z0-9][A-Z0-9]".toRegex()
        val mp: MutableMap<String, Array<String>> = mutableMapOf()
        data.drop(2).forEach {
            val tmp = rx.findAll(it).map { fnd -> fnd.value }.toList()
            mp[tmp[0]] = arrayOf(tmp[1],tmp[2])
        }

        // part 1 is straight forward - implemented in a helper function for re-use in part 2
        // navigate goes from a given start string until it hits a string ending in 'Z' and returns the step count
        fun navigate(start:String):Long {
            var loc = start; var dirIx = 0; var cnt = 0L
            do {
                loc = (mp[loc]!!)[dirs[dirIx]]
                cnt++
                dirIx = (dirIx + 1) % dirs.size
            } while (loc[2] != 'Z')
            return cnt
        }

        println("Part 1: $red$bold${navigate("AAA")}$reset")

        // part 2 is only so simple because playing with the data have shown that
        // each start in the concrete data is reaching an end cyclically with no initial offset
        // thus the LCM of all the cycle lengths is the solution
        val periods  = mp.keys.filter { it[2] == 'A' }.map{ navigate(it) }
        println("Part 2: $red$bold${lcmList(periods)}$reset")
    }
}