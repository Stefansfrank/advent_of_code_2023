package com.sf.aoc2023
import com.sf.aoc.*

class Day15 : Solver {

    override fun solve(file: String) {

        // reading & parsing input
        val data  = readTxtFile(file)
        val instructions = data[0].split(',')

        // the HASH function
        fun (String).hash():Int = this.fold(0) { cur, c -> ((cur + c.code) * 17) % 256 }

        // part 1 is super simple
        println("Part 1: $red$bold${instructions.sumOf { it.hash() }}$reset")

        // 'focus' maps the label to the focal length
        // thus I can override the focal length without changing the position of the lens in a box
        val focus:MutableMap<String, Int> = mutableMapOf()

        // empty boxes (I keep only the label in the boxes, the focal length is mapped externally
        val boxes = (0..255).map { mutableListOf<String>() }.toMutableList()

        // loop through instructions
        for (inst in instructions) {

            // removal case
            if (inst.contains('-')) {
                val lbl = inst.dropLast(1)
                boxes[lbl.hash()].removeIf { it == lbl }

            // lens addition / focus correction
            } else {
                val ix  = inst.indexOf('=')
                val lbl = inst.substring(0, ix)
                val hsh = lbl.hash()
                focus[lbl] = inst.substring(ix+1).toInt() // sets or overrides the focal length of this lens
                if (!boxes[hsh].contains(lbl)) boxes[hsh].add(lbl)  // if it wasn't there already, added at the end
            }
        }

        val result = boxes. foldIndexed(0) { boxIx, sum, box -> sum + box.foldIndexed(0)
                { slot, fPower, lens ->  fPower + (boxIx + 1) * (slot + 1) * focus[lens]!! }  }
        println("Part 2: $red$bold${result}$reset")
    }
}