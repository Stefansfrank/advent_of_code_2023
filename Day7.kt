package com.sf.aoc2023
import com.sf.aoc.*

class Day7 : Solver {

    override fun solve(file: String) {

        // reading input and calculate the strength of the hand for algorithms in part 1 & 2
        val data = readTxtFile(file)
        val hands:MutableList<Hand> = mutableListOf()
        data.forEach {
            val handBet = it.split(" ")
            hands.add( Hand( handBet[0], handBet[1].toLong(),
                calcStrength(handBet[0], false), calcStrength(handBet[0], true)))
        }

        // sort by strength and fold into result
        hands.sortBy { it.strengthP1 }
        println("Part 1: $red$bold${hands.foldIndexed(0L) { ix, sm, hnd -> sm + hnd.bid * (ix + 1) }}$reset")
        hands.sortBy { it.strengthP2 }
        println("Part 2: $red$bold${hands.foldIndexed(0L) { ix, sm, hnd -> sm + hnd.bid * (ix + 1) }}$reset")
    }

    // the strength of the individual cards for the secondary ranking for part 1 and part 2
    private val cards = mapOf( 'A' to Pair(12,12), 'K' to Pair(11,11), 'Q' to Pair(10,10), 'J' to Pair(9,0),
        'T' to Pair(8,9), '9' to Pair(7,8), '8' to Pair(6,7), '7' to Pair(5,6), '6' to Pair(4,5),
        '5' to Pair(3,4), '4' to Pair(2,3), '3' to Pair(1,2), '2' to Pair(0,1))

    // calculate the type of hand (6 = five of a kind ... 0 = highest card)
    fun typeOfHand(hand: String):Long {
        val cnt: MutableMap<Char, Int> = mutableMapOf()
        for (c in hand) cnt[c] = (cnt[c] ?: 0) + 1
        val srtCnt = cnt.values.sortedDescending()
        if (srtCnt[0] == 5) return 6L
        if (srtCnt[0] == 4) return 5L
        if (srtCnt[0] == 3) return if (srtCnt[1] == 2) 4L else 3L
        if (srtCnt[0] == 2) return if (srtCnt[1] == 2) 2L else 1L
        return 0L
    }

    // find best type if 'J' is Joker
    // there is optimization potential due to testing a high number of hands that could be excluded
    // but execution is super fast ....
    fun bestTypeOfHand(hand: String):Long {
        var hands = listOf(hand)

        // go through letters to identify any 'J'
        for ((ix, c) in hand.withIndex()) {
            if (c == 'J') {

                // create 12 hands for each hand that still contains 'J'
                val newHands: MutableList<String> = mutableListOf()
                for (hnd in hands) {
                    for (cc in cards.keys) {
                        if (cc != 'J') {
                            newHands.add(hnd.substring(0, ix) + cc + hnd.substring(ix + 1))
                        }
                    }
                }
                hands = newHands
            }
        }

        // determine maximum
        return hands.maxOfOrNull { typeOfHand(it) }!!
    }

    // calculate strength of hand for part 1 and 2
    // I define the strength as a Long combining the two rankings into one number that can be compared
    // the number is an 11 digit Int64 with the first digit being the strength of the hand type and then 5 pairs
    // of digits representing the strength of the individual cards in order
    fun calcStrength(hand: String, part2: Boolean):Long {
        return hand.fold(if (part2) bestTypeOfHand(hand) else typeOfHand(hand)) {
            strength, c -> strength * 100 + if (part2) cards[c]!!.second else cards[c]!!.first
        }
    }

    data class Hand(val hand: String, val bid: Long, val strengthP1: Long, val strengthP2: Long)
}