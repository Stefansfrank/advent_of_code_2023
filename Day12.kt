package com.sf.aoc2023
import com.sf.aoc.*

class Day12: Solver {

    override fun solve(file: String) {

        // the core algorithms goes left to right through the pattern and keeps
        // a list of permutations that are still possibly leading to a valid solution
        // this class saves these permutations

        // 'covered' tracks how many groups (from the left) have been covered from the required groups
        // 'currGrp' tracks the length of the group that is currently traversed
        // 'numPerms' is increased if two permutations are merged (possible if the same groups are covered)
        data class Perm(val covered:Int, val currGrp:Int, var numPerms:Long = 1L)

        // this is one line containing the pattern and the check-list
        class Line(val pattern: String, val checkList: List<Int>) {

            // the core algorithms counting permutations
            fun countPerms():Long {

                // left-to-right through the pattern
                var perms = listOf(Perm( 0, 0, 1L))
                for ((charIx,c) in pattern.withIndex()) {

                    // this algorithms generates a new list of permutations for each character encountered
                    val nextPerms = mutableListOf<Perm>()
                    // loop through each permutation
                    for (perm in perms) {

                        // try both '.' and '#' if allowed
                        for (nc in listOf('.', '#')) {
                            if (c != '?' && c != nc) continue

                            // encountered a '#'
                            if (nc == '#') {

                                // throw away if either all groups are already covered
                                // or if the current group would grow longer than the next expected group
                                if (perm.covered == checkList.size ||
                                    checkList[perm.covered] <= perm.currGrp) continue
                                nextPerms.add(Perm( perm.covered, perm.currGrp+1, perm.numPerms))

                            // encountered a '.'
                            } else {

                                // is this the first '.' after a '#'?
                                var nowCovered  = perm.covered
                                if (perm.currGrp > 0) {

                                    // throw away if it has not reached the next required length
                                    if (perm.currGrp < checkList[nowCovered]) continue

                                    // next group is covered
                                    nowCovered += 1
                                }

                                // do I already have a permutation in the new list with the same coverage?
                                // if so, and that permutation is not mid-group increase that counter instead of
                                // adding a new permutation
                                val existingPerm = nextPerms.find { it.covered == nowCovered && it.currGrp == 0}
                                if (existingPerm != null) {
                                    existingPerm.numPerms += perm.numPerms
                                    continue
                                }

                                // now check whether there are enough characters left to cover the uncovered groups
                                // and add permutation if so
                                val stillToCover = if (checkList.size == nowCovered) 0 else
                                    checkList.drop(nowCovered).sum() + checkList.size - nowCovered - 1
                                if ((pattern.length) - charIx > stillToCover)
                                    nextPerms.add(Perm( nowCovered , 0, perm.numPerms))
                            }
                        }
                    }
                    perms = nextPerms
                }
                return perms.sumOf { it.numPerms }
            }
        }

        // reading input and parse into Line classes
        val data = readTxtFile(file)
        val lines = mutableListOf<Line>()
        data.forEach { line ->
            val dLine = line.split(' ')
            lines.add(Line(dLine[0], dLine[1].split(',').map { it.toInt() }))
        }

        println("Part 1: $red$bold${lines.sumOf { it.countPerms() }}$reset")

        // parse into lines with the 5x extension requested in part 2
        val extLines = mutableListOf<Line>()
        data.forEach { line ->
            val dLine = line.split(' ')
            var mLine1 = dLine[0]
            var mLine2 = dLine[1]
            repeat(4) { mLine1 += '?' + dLine[0] ; mLine2 += ',' + dLine[1]}
            extLines.add(Line(mLine1, mLine2.split(',').map { it.toInt() }))
        }

        println("Part 2: $red$bold${extLines.sumOf { it.countPerms() }}$reset")
    }
}