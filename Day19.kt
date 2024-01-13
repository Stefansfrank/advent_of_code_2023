package com.sf.aoc2023
import com.sf.aoc.*

class Day19 : Solver {

    override fun solve(file: String) {

        // reading input
        val data = readTxtFile(file)

        // parsing helper
        val rrx = "([asxm])([<>])(\\d+):(\\w+),".toRegex()

        // holding ranges of values for each register and the next rule
        data class RegRng(val regs: Map<Char, Rng>, var nxt: String) {
            fun copyMod(reg: Char, rng:Rng?, modNxt:String):RegRng {
                val modRegs = mutableMapOf<Char,Rng>()
                for (r in regs.keys) modRegs[r] = regs[r]!!
                if (rng != null) modRegs[reg] = rng
                return RegRng(modRegs, modNxt)
            }
        }

        // a comparator e.g. "s<1351:px"
        data class Comp(val reg:Char, val gt:Boolean, val num:Int, val tgt:String) {

            // evaluates comparator and returns next rule if matched or "" if not
            fun eval(part:Map<Char,Int>):String =
                if (if (gt) (part[reg]!! > num) else (part[reg]!! < num)) tgt else ""

            // evaluates comparator for one set of register ranges
            // and returns list of sub-ranges that are matched and/or not
            fun evalRng(rng:RegRng):List<RegRng> {
                val ret = mutableListOf<RegRng>()
                if (rng.regs[reg]!!.contains(num)) {
                    if (gt) {
                        if (num < rng.regs[reg]!!.to) ret.add(rng.copyMod(reg, Rng(num+1, rng.regs[reg]!!.to), tgt))
                        ret.add(rng.copyMod(reg, Rng(rng.regs[reg]!!.from, num), ""))
                    } else {
                        if (num > rng.regs[reg]!!.from) ret.add(rng.copyMod(reg, Rng(rng.regs[reg]!!.from, num-1), tgt))
                        ret.add(rng.copyMod(reg, Rng(num, rng.regs[reg]!!.to), ""))
                    }
                } else {
                    if ((gt && rng.regs[reg]!!.from > num) || (!gt && rng.regs[reg]!!.to < num))
                        ret.add(rng.copyMod('X',null, tgt))
                    else
                        ret.add(rng.copyMod('X', null, ""))
                }
                return ret
            }
        }

        // a complete rule
        data class Rule(val id:String, val def:String, val comps:List<Comp> ) {

            // evaluates one part and returns next rule (could be "A" or "R")
            fun eval(part:Map<Char, Int>):String {
                comps.forEach { val ev = it.eval(part); if (ev.isNotEmpty()) return ev }
                return def
            }

            // evaluates one set of register ranges and returns list of new sets of register ranges with next rule
            fun evalRng(rng:RegRng):List<RegRng> {
                val ret = mutableListOf<RegRng>()
                var inp:RegRng? = rng
                for (comp in comps) {
                    val evs = comp.evalRng(inp!!)
                    inp = null
                    for (ev in evs) if (ev.nxt == "") inp = ev else ret.add(ev)
                    if (inp == null) break
                }
                if (inp != null) ret.add(inp.copyMod('X',null,def))
                return ret
            }
        }

        // parses rules
        val rules = data.dropLast(data.size - data.indexOf("")).map { line ->
            Rule( line.substringBefore('{'), line.substringAfterLast(',').dropLast(1),
                rrx.findAll(line).map {Comp( it.groupValues[1].toCharArray()[0], it.groupValues[2] == ">",
                    it.groupValues[3].toInt(), it.groupValues[4]) }.toList() )}
        val rulIx = rules.associateBy( {it.id}, {it} )

        // parses parts
        val parts = data.drop(data.indexOf("") + 1).map { line ->
            mutableMapOf<Char,Int>().apply{
                line.drop(1).dropLast(1).split(',').forEach {
                    this[it.substringBefore('=')[0]] = it.substringAfter('=').toInt()
                }
            }
        }

        // part 1 evaluates individual parts
        var sum = 0
        for (part in parts) {
            var next = "in"
            while (next != "A" && next != "R") next = rulIx[next]!!.eval(part)
            if (next == "A") sum += part.values.sum()
        }
        println("Part 1: $red$bold${sum}$reset")

        // part 2 starts with one set of register ranges 1..4000 and runs range evaluation
        val todo = ArrayList<RegRng>().also { it.add( RegRng(mapOf('x' to Rng(1,4000), 'a' to Rng(1,4000),
            'm' to Rng(1,4000), 's' to Rng(1,4000)), "in") )}
        val acc = mutableListOf<RegRng>()
        val rej = mutableListOf<RegRng>()

        while (todo.isNotEmpty()) {
            val nxt = todo.removeFirst()
            val evs = rulIx[nxt.nxt]!!.evalRng(nxt)
            for (ev in evs) when (ev.nxt) {"A" -> acc; "R" -> rej; else -> todo}.add(ev)
        }

        println("Part 2: $red$bold${acc.fold(0L) { sm, a -> sm + a.regs.values.fold(1L) { p, r -> p * r.length().toLong()} }}$reset")
    }
}