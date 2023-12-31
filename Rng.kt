package com.sf.aoc

import kotlin.math.min
import kotlin.math.max

// A list of ranges is reduced to the minimum list of non-overlapping ranges withe same coverage
// recursively tries to merge two ranges in the list
fun reduceRngs(rngs:List<Rng>):List<Rng> {
    if (rngs.size < 2) return rngs
    for (a in 0 until rngs.size - 1) {
        for (b in a+1 until rngs.size) {
            if (rngs[a].overlap(rngs[b]) || rngs[a].adjoint(rngs[b])) {
                val ret = (0 until a).map { rngs[it] }.toMutableList()
                ret.addAll((a+1 until b).map { rngs[it] })
                ret.addAll((b+1 until rngs.size).map { rngs[it] })
                ret.add(rngs[a].mergeUnchecked(rngs[b]))
                return reduceRngs(ret)
            }
        }
    }
    return rngs
}

// a range of numbers only defined by its limits
// with some functions to help manipulating multiple ranges
// without expanding them into lists
data class Rng(var from:Int, var to:Int) {

    // this constructor uses a Pair and does reorder from/to if necessary
    constructor(rng:Pair<Int, Int>) : this(
        if (rng.first < rng.second) rng.first else rng.second,
        if (rng.first > rng.second) rng.first else rng.second)

    // do two ranges overlap?
    fun overlap(oth:Rng) = (from <= oth.to && oth.from <= to)

    // does range contain number?
    fun contains(num:Int):Boolean = (num in from .. to)

    // are two ranges right next to each other?
    fun adjoint(oth:Rng) = (from == oth.to + 1 || oth.from == to + 1)

    // shift a range
    fun move(add:Int):Rng = Rng(from + add, to + add)

    // this functionality compares (this) with a masking range. all parts of (this) that are not
    // overlapping with the masking range will be in "unmasked" (can be 0-2 ranges). The overlap
    // range (0-1) will be in "masked"
    data class MaskedRanges(var masked:Rng?, var unmasked:MutableList<Rng>)

    fun maskWith(oth:Rng):MaskedRanges {

        val result = MaskedRanges(null, mutableListOf())
        if (this.overlap(oth)) {
            var mf = this.from; var mt = this.to
            if (this.from < oth.from) {
                result.unmasked.add(Rng(this.from, oth.from - 1))
                mf = oth.from
            }
            if (this.to > oth.to) {
                result.unmasked.add(Rng(oth.to + 1, this.to))
                mt = oth.to
            }
            result.masked = Rng(mf, mt)
        } else {
            result.unmasked.add(this)
        }
        return result
    }

    // merge two ranges into one if possible
    fun merge(oth: Rng):List<Rng> = if (overlap(oth) || adjoint(oth))
        listOf(Rng(min(from, oth.from), max(to, oth.to))) else listOf(this, oth)

    // merge two ranges into one without checking for overlap / adjoint
    fun mergeUnchecked(oth: Rng):Rng = Rng(min(from, oth.from), max(to, oth.to))

    // determine the overlap as range
    fun cross(oth: Rng):List<Rng> = if (overlap(oth))
        listOf(Rng(max(from, oth.from), min(to, oth.to))) else listOf()

    // the length of the range
    fun length() = to - from + 1
}

// a range of numbers only defined by its limits
// with some functions to help manipulating multiple ranges
// without expanding them into lists
data class RngL(val from:Long, val to:Long) {

    // this constructor uses a Pair and does reorder from/to if necessary
    constructor(rng:Pair<Long, Long>) : this(
        if (rng.first < rng.second) rng.first else rng.second,
        if (rng.first > rng.second) rng.first else rng.second)

    // do two ranges overlap?
    fun overlap(oth:RngL) = (from <= oth.to && oth.from <= to)

    // does range contain number?
    fun contains(num:Long):Boolean = (num in from..to)

    // are two ranges right next to each other?
    fun adjoint(oth:RngL) = (from == oth.to + 1 || oth.from == to + 1)

    // shift a range
    fun move(add:Long):RngL = RngL(this.from + add, this.to + add)

    // this functionality compares (this) with a masking range. all parts of (this) that are not
    // overlapping with the masking range will be in "unmasked" (can be 0-2 ranges). The overlap
    // range (only if overlapping) will be in "masked"
    data class MaskedRanges(var masked:RngL?, var unmasked:MutableList<RngL>)
    fun maskWith(oth:RngL):MaskedRanges {

        val result = MaskedRanges(null, mutableListOf())
        if (this.overlap(oth)) {
            var mf = this.from; var mt = this.to
            if (this.from < oth.from) {
                result.unmasked.add(RngL(this.from, oth.from - 1))
                mf = oth.from
            }
            if (this.to > oth.to) {
                result.unmasked.add(RngL(oth.to + 1, this.to))
                mt = oth.to
            }
            result.masked = RngL(mf, mt)
        } else {
            result.unmasked.add(this)
        }
        return result
    }

    // merge two ranges into one if possible
    fun merge(oth: RngL):List<RngL> = if (overlap(oth) || adjoint(oth))
        listOf(RngL(min(from, oth.from), max(to, oth.to))) else listOf(this, oth)

    // merge two ranges into one without checking for overlap / adjoint
    fun mergeUnchecked(oth: RngL):RngL = RngL(min(from, oth.from), max(to, oth.to))

    // determine the overlap as range
    fun cross(oth: RngL):List<RngL> = if (overlap(oth))
        listOf(RngL(max(from, oth.from), min(to, oth.to))) else listOf()

    // the length of the range
    fun length() = to - from + 1
}