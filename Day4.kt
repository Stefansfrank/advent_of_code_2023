package com.sf.aoc2023
import com.sf.aoc.*

class Day4 : Solver {

    override fun solve(file: String) {

        // reading input
        val data = readTxtFile(file)
        val cards = data.map { parseLine(it) }

        // Part 1
        println("Part 1: $red$bold${cards.fold(0) {sm, it -> sm + it.winFac}}$reset")

        // part 2
        val cardCount = MutableList(data.size) { 1 }
        for ((ix,card) in cards.withIndex()) for (adIx in ix + 1 .. (ix + card.wins)) cardCount[adIx] += cardCount[ix]
        println("Part 2: $red$bold${cardCount.fold(0) {sm, it -> sm + it}}$reset")

    }

    // creates a card object out of an input line
    private fun parseLine(line: String):Card {
        val win = line.substring(line.indexOf(':') + 1, line.indexOf('|')).trim().split("\\s+".toRegex()).map { it.toInt() }
        val num = line.substring(line.indexOf('|') + 1).trim().split("\\s+".toRegex()).map { it.toInt() }
        return Card(win, num)
    }

    // represents one card and computes win number and win factor on initialization
    class Card(win: List<Int>, private val num: List<Int>) {
        val wins   = num.fold(0) { sm, it -> sm + if (win.contains(it)) 1 else 0 }
        val winFac = 1 shl (wins - 1)
    }

}
