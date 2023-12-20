package com.sf.aoc2023
import com.sf.aoc.*

class Day20 : Solver {

    override fun solve(file: String) {

        // reading input
        val data = readTxtFile(file)

        // parsing into class with type (0:Button, 1:Broadcast, 2:Flip-Flop, 3:Conjunction, 4:Output)
        // state (true = high/on) and cross-linked inbound and outbound modules
        data class Mod(val id:String, var type:Int, var state:Boolean = false,
                       val inb:MutableList<Mod> = mutableListOf(), val out:MutableList<Mod> = mutableListOf())

        val mods = mutableMapOf("push" to Mod("push", 0))
        for (line in data) {

            // first create Mods
            val name = line.substringBefore(" -> ")
            var nMod = when (name[0]) {
                '%' -> Mod(name.drop(1), 2)
                '&' -> Mod(name.drop(1), 3)
                else -> Mod(name, 1)
            }
            if (mods[nMod.id] == null) mods[nMod.id] = nMod
            else {
                mods[nMod.id]!!.type = nMod.type
                nMod = mods[nMod.id]!!
            }

            // then set cross-links
            val outs = line.substringAfter(" -> ").split(", ")
            outs.forEach {
                if (mods[it] == null) mods[it] = (Mod(it, 4))
                nMod.out.add(mods[it]!!)
                mods[it]!!.inb.add(nMod)
            }
        }

        // add initial cross-link
        mods["push"]!!.out.add(mods["broadcaster"]!!)
        mods["broadcaster"]!!.inb.add(mods["push"]!!)

        // some global counters
        var highCnt = 0L
        var lowCnt = 0L
        var pushCnt = 0L

        // find the feeders to "xf"
        val period = mutableMapOf<String, Long>()
        val feeds  = mods["rx"]!!.inb[0].inb.map{ it.id }.onEach { period[it] = 0L }

        // push button
        fun pushButton() {
            pushCnt++
            var active = listOf(mods["push"]!!)
            while (active.isNotEmpty()) {
                val newAct = mutableListOf<Mod>()
                for (send in active) {

                    // measure period of the four feeder branches of "rx"
                    if (feeds.contains(send.id) && send.state && period[send.id]!! == 0L) period[send.id] = pushCnt
                    for (recv in send.out) {
                        when (recv.type) {
                            1 -> newAct.add(recv.also { it.state = send.state })
                            2 -> if (!send.state) newAct.add(recv.also { it.state = !it.state })
                            3 -> newAct.add(recv.also {
                                it.state = !recv.inb.fold(true) { o, inb -> o && inb.state }})
                            4 -> recv.state = send.state
                        }
                        if (send.state) highCnt++ else lowCnt++
                    }
                }
                active = newAct
            }
        }

        repeat(1000) { pushButton() }
        println("Part 1: $red$bold${lowCnt*highCnt}$reset")

        repeat(4000) { pushButton() }
        println("Part 2: $red$bold${lcmList(period.values.toList())}$reset")
    }
}