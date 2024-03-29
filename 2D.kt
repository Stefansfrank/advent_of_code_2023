package com.sf.aoc

import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sign

// a point with coordinates c and y
data class XY(val x: Int, val y: Int) {

    // adds a vector to a point
    fun add(p:XY) = XY(x + p.x, y + p.y)
    fun sub(p:XY) = XY( x - p.x, y - p.y)
    fun add(px:Int, py:Int) = XY(x + px, y + py)
    fun sub(px:Int, py:Int) = XY( x - px, y - py)

    // returns the next point in the given direction
    // (0 = up, 1 = right, 2 = down, 3 = left)
    private val delX = listOf(0, 1, 0, -1)
    private val delY = listOf(-1, 0, 1, 0)
    fun mv(dir: Int) = XY( x + delX[dir], y + delY[dir])
    fun mv(dir: Int, dist: Int) = XY( x + delX[dir] * dist, y + delY[dir] * dist)
    fun mv(dir: Int, box:Rect) = XY( (x + delX[dir]).coerceIn(box.xRange()), (y + delY[dir]).coerceIn(box.yRange()))

    // returns the 4 or 8 neighbours depending on the flag 'diagonal'
    fun neighbors(diagonal: Boolean = false, wrap: Rect? = null):List<XY> {
        var delX = listOf(0, 1, 0, -1)
        var delY = listOf(-1, 0, 1, 0)
        if (diagonal) {
            delX = listOf(0, 1, 1, 1, 0, -1, -1, -1)
            delY = listOf(-1, -1, 0, 1, 1, 1, 0, -1)
        }
        var i = 0
        return if (wrap == null) List(if (diagonal) 8 else 4) { XY(x + delX[i], y + delY[i++]) }
        else List(if (diagonal) 8 else 4) { wrap.wrap(XY(x + delX[i], y + delY[i++])) }
    }

    // Manhattan distance to origin or another point
    fun mDist() = abs(x) + abs(y)
    fun mDist(p: XY) = abs(p.x - x) + abs(p.y - y)

    // The highest absolute coordinate value
    fun maxAbs() = max(abs(x), abs(y))

    // The signum function for the whole point
    fun sign() = XY(x.sign, y.sign)

    override fun equals(other:Any?):Boolean {
        return (x == (other as XY).x && y == other.y)
    }
}

// a point with coordinates c and y
data class XYL(val x: Long, val y: Long) {

    // adds a vector to a point
    fun add(p:XYL) = XYL(x + p.x, y + p.y)
    fun sub(p:XYL) = XYL( x - p.x, y - p.y)
    fun add(px:Long, py:Long) = XYL(x + px, y + py)
    fun sub(px:Long, py:Long) = XYL( x - px, y - py)

    // returns the next point in the given direction
    // (0 = up, 1 = right, 2 = down, 3 = left)
    private val delX = listOf(0, 1, 0, -1)
    private val delY = listOf(-1, 0, 1, 0)
    fun mv(dir: Int) = XYL( x + delX[dir], y + delY[dir])
    fun mv(dir: Int, dist: Long) = XYL( x + delX[dir] * dist, y + delY[dir] * dist)

    // returns the 4 or 8 neighbours depending on the flag 'diagonal'
    fun neighbors(diagonal: Boolean = false):List<XYL> {
        var delX = listOf(0, 1, 0, -1)
        var delY = listOf(-1, 0, 1, 0)
        if (diagonal) {
            delX = listOf(0, 1, 1, 1, 0, -1, -1, -1)
            delY = listOf(-1, -1, 0, 1, 1, 1, 0, -1)
        }
        var i = 0
        return List(if (diagonal) 8 else 4) { XYL(x + delX[i], y + delY[i++]) }
    }

    // Manhattan distance to origin or another point
    fun mDist() = abs(x) + abs(y)
    fun mDist(p: XYL) = abs(p.x - x) + abs(p.y - y)

    // The highest absolute coordinate value
    fun maxAbs() = max(abs(x), abs(y))

    override fun equals(other:Any?):Boolean {
        return (x == (other as XYL).x && y == other.y)
    }
}

// a rectangle defined with the corner points
data class Rect(val from:XY, val to:XY) {

    // standardized from / to points enforcing from < to for each coordinate
    private val stdFrom = XY(min(from.x, to.x), min(from.y, to.y))
    private val stdTo   = XY(max(from.x, to.x), max(from.y, to.y))

    // range / contains
    fun xRange() = (stdFrom.x .. stdTo.x)
    fun yRange() = (stdFrom.y .. stdTo.y)
    fun contains(loc: XY) = (loc.x in xRange() && loc.y in yRange())

    // size
    fun size() = abs((1L + to.y - from.y) *(1L + to.x - from.x))

    fun wrap(loc: XY): XY {
        var nLoc = loc
        if (loc.x !in xRange()) {
            if (loc.x > stdTo.x) nLoc = XY(stdFrom.x + (loc.x - stdTo.x) - 1, loc.y)
            if (loc.x < stdFrom.x) nLoc = XY(stdTo.x - (stdFrom.x - loc.x) + 1, loc.y)
        }
        if (loc.y !in yRange()) {
            if (loc.y > stdTo.y) nLoc = XY( loc.x,stdFrom.y + (loc.y - stdTo.y) - 1)
            if (loc.x < stdFrom.y) nLoc = XY(loc.x,stdTo.y - (stdFrom.y - loc.y) + 1)
        }
        return nLoc
    }
}

// a 2d mutable list of Booleans of dimensions xDim, yDim
// used for positive coordinate systems starting at 0,0
class Mask(val xdim:Int, val ydim:Int, private val default:Boolean = false,
           private val defMsk:MutableList<MutableList<Boolean>>? = null) {

    // constructor using another mask as template
    constructor(temp:Mask): this(temp.xdim, temp.ydim, false,
        mutableListOf<MutableList<Boolean>>().apply {
            for (y in 0 until temp.ydim) { this.add( temp.msk[y].toMutableList() )}})

    // constructor using another mask as template
    constructor(temp:MapChar): this(temp.xdim, temp.ydim)

    // the underlying mask accessible with [y][x] sequence
    val msk = defMsk
        ?: mutableListOf<MutableList<Boolean>>().apply {
            repeat(ydim) { this.add( MutableList(xdim) { default })} }

    // sets a whole region to true (default - whole mask)
    fun on(bx: Rect = Rect(XY(0,0), XY(xdim, ydim))) {
        msk.forEachIndexed{ y, ln -> if (y in bx.yRange())
            ln.forEachIndexed{ x, _ -> if (x in bx.xRange())
                msk[y][x] = true }}
    }

    // sets a whole region to false (default whole mask)
    fun off(bx: Rect = Rect(XY(0,0), XY(xdim, ydim))) {
        msk.forEachIndexed{ y, ln -> if (y in bx.yRange())
            ln.forEachIndexed{ x, _ -> if (x in bx.xRange())
                msk[y][x] = false }}
    }

    // sets a whole region to the opposite it is set (default whole mask)
    fun tgl(bx: Rect = Rect(XY(0,0), XY(xdim, ydim))) {
        msk.forEachIndexed{ y, ln -> if (y in bx.yRange())
            ln.forEachIndexed{ x, bt -> if (x in bx.xRange())
                msk[y][x] = !bt }}
    }

    // counts the amount of 'true' in the map
    fun cnt() = msk.fold(0) { acc, ln -> acc + ln.fold(0) { acc2, flg -> acc2 + if (flg) 1 else 0 } }

    // prints out a representation to stdout
    fun print() = msk.forEach { it.forEach{ b -> if (b) print("#") else print(".") }; println() }

    // prints out a representation to stdout with a position marked
    fun print(loc: XY, inv: Boolean) = msk.forEachIndexed { y, ln ->
        ln.forEachIndexed { x, b ->
            if (loc == XY(x,y)) print("$red$bold*$reset") else if (b xor inv) print("#") else print(".")
        }
        println("")
    }

    // returns a snapshot of the inner mask
    fun snap():List<List<Boolean>> {
        val nMsk = mutableListOf<List<Boolean>>()
        for (ln in msk) nMsk.add( ln.map{ it } )
        return nMsk
    }

    // simple getters and setters using XY as coordinates
    fun set(loc:XY, value:Boolean) { msk[loc.y][loc.x] = value }
    fun get(loc:XY) = msk[loc.y][loc.x]
    fun getSafe(loc:XY, nul:Boolean) =
        if (loc.x in 0 until xdim && loc.y in 0 until ydim) msk[loc.y][loc.x] else nul
    fun on(loc:XY)  = set(loc, true)
    fun off(loc:XY) = set(loc, false)
    fun tgl(loc:XY) = set(loc, !get(loc))

    // same with individual coordinates
    fun set(x:Int, y:Int, value:Boolean) { msk[y][x] = value }
    fun get(x:Int, y:Int) = msk[y][x]
    fun on(x:Int, y:Int)  = set(x, y, true)
    fun off(x:Int, y:Int) = set(x, y, false)
    fun tgl(x:Int, y:Int) = set(x, y, !get(x, y))

}

// a 2D integer map of dimensions xdim, ydim
class MapInt(private val xin:Int, private val yin:Int, private val default:Int = 0) :
    Map<Int>(xin, yin, { _, _ -> default}) {

    // adds n to a whole region (defaults n = 1, region = all map)
    fun add(n: Int = 1, bx: Rect = Rect(XY(0,0), XY(xdim, ydim))) {
        mp.forEachIndexed{ y, ln -> if (y in bx.yRange())
            ln.forEachIndexed{ x, vl -> if (x in bx.xRange())
                mp[y][x] = vl + n }}
    }

    // subtracts n from a whole region (defaults n = 1, region = all map)
    // allows the enforcement of positive values with flag 'pos' (default off)
    fun sub(n: Int =1, bx: Rect = Rect(XY(0,0), XY(xdim, ydim)), pos: Boolean = false) {
        if (pos) mp.forEachIndexed{ y, ln -> if (y in bx.yRange())
            ln.forEachIndexed{ x, vl -> if (x in bx.xRange())
                mp[y][x] = max(0, vl - n) }}
        else mp.forEachIndexed{ y, ln -> if (y in bx.yRange())
            ln.forEachIndexed{ x, vl -> if (x in bx.xRange())
                mp[y][x] = vl - n }}
    }

    // adds all values of the map together
    fun cnt() = mp.fold(0) { acc, ln -> acc + ln.fold(0) { acc2, n -> acc2 + n } }

    // sets a line value
    fun setLine(y:Int, ln:List<Int>) {
       mp[y] = ln.toMutableList()
    }

    // prints out a representation to stdout
    fun printChar() = mp.forEach { it.forEach{ i -> print(i.toChar()) }; println() }
}

// a 2D integer map of dimensions xdim, ydim
class MapChar(val xdim:Int, val ydim:Int, private val default:Char = '.') {

    constructor(data:List<String>):this(data[0].length, data.size) {
        for ((y,line) in data.withIndex()) for ((x, c) in line.withIndex()) mp[y][x] = c
    }

    fun contains(loc:XY):Boolean {
        return (loc.x in 0 until xdim && loc.y in 0 until ydim)
    }

    // the actual map accessible with [y][x] sequence
    val mp = mutableListOf<MutableList<Char>>().apply { repeat(ydim) { this.add( MutableList(xdim) { default })} }

    val rowIx = (0 until xdim).toList()
    val colIx = (0 until ydim).toList()
    val xyIx:List<XY> = colIx.fold(mutableListOf()) { lst, y -> (lst + rowIx.map { x -> XY(x,y)}).toMutableList()}

    // adds simple XY getter / setter
    fun get(xy:XY):Char = mp[xy.y][xy.x]
    fun get(x:Int, y:Int) = mp[y][x]
    fun set(xy:XY, value:Char) { mp[xy.y][xy.x] = value }
    fun set(x:Int, y:Int, value:Char) { mp[y][x] = value }
    fun getRow(y:Int):List<Char> = mp[y]
    fun getCol(x:Int):List<Char> = rowIx.map { mp[it][x] }

    // sets a line value
    fun setLine(y:Int, ln:List<Char>) {
        mp[y] = ln.toMutableList()
    }

    // prints out a representation to stdout
    fun print() = mp.forEach { it.forEach{ i -> print(i) }; println() }

    // find
    fun find(c:Char):XY? {
        for (y in 0 until ydim) {
            for (x in 0 until xdim) {
                if (mp[y][x] == c) return XY(x,y)
            }
        }
        return null
    }
}

// a generic 2D map
open class Map<T>(val xdim:Int, val ydim:Int, private val default: (Int, Int) -> T) {

    // the actual map accessible with [y][x] sequence
    val mp = (0 until ydim).map{ y ->
        (0 until xdim).map { x -> default(x, y) }.toMutableList() }.toMutableList()
    val rowIx = (0 until xdim).toList()
    val colIx = (0 until ydim).toList()
    val xyIx:List<XY> = colIx.fold(mutableListOf()) { lst, y -> (lst + rowIx.map { x -> XY(x,y)}).toMutableList()}

    fun contains(loc:XY):Boolean {
        return (loc.x in 0 until xdim && loc.y in 0 until ydim)
    }

    // adds simple XY getter / setter
    fun get(xy: XY): T = mp[xy.y][xy.x]
    fun set(xy: XY, value: T) {
        mp[xy.y][xy.x] = value
    }

    // adds standard getter / setter
    fun get(x: Int, y: Int): T = mp[y][x]
    fun set(x: Int, y: Int, value: T) {
        mp[y][x] = value
    }

    // adds row/col getters
    fun getRow(y:Int):List<T> = mp[y]
    fun getCol(x:Int):List<T> = rowIx.map { mp[it][x] }

    // sets a row
    fun setRow(y:Int, ln:List<T>) {
        mp[y] = ln.toMutableList()
    }

    // sets content of a whole region using lambda (x,y)
    fun set(op: (Int, Int) -> (T), bx: Rect = Rect(XY(0, 0), XY(xdim, ydim))) {
        mp.forEachIndexed { y, ln ->
            if (y in bx.yRange())
                ln.forEachIndexed { x, _ ->
                    if (x in bx.xRange())
                        mp[y][x] = op(x, y)
                }
        }
    }

    // modifies content of a whole region using lambda (current value, x, y)
    fun mod(op: (T, Int, Int) -> (T), bx: Rect = Rect(XY(0, 0), XY(xdim, ydim))) {
        mp.forEachIndexed { y, ln ->
            if (y in bx.yRange())
                ln.forEachIndexed { x, _ ->
                    if (x in bx.xRange())
                        mp[y][x] = op(mp[y][x], x, y)
                }
        }
    }

    // executes on each content
    fun exec(op: (T, Int, Int) -> Unit, bx: Rect = Rect(XY(0, 0), XY(xdim, ydim))) {
        mp.forEachIndexed { y, ln ->
            if (y in bx.yRange())
                ln.forEachIndexed { x, _ ->
                    if (x in bx.xRange())
                        op(mp[y][x], x, y)
            }
        }
    }

    // finds an entry
    fun find(entry:T):XY? {
        for (y in 0 until ydim) {
            for (x in 0 until xdim) {
                if (mp[y][x] == entry) return XY(x,y)
            }
        }
        return null
    }

    // prints out a representation to stdout
    fun print(op: (T) -> String = { t -> t.toString() }) = mp.forEach { it.forEach{ i -> print(i) }; println() }
}

// these should be used on Directions
fun (Int).isVertical():Boolean = (this == 1 || this == 3)
fun (Int).isDescending():Boolean = (this == 0 || this == 3)
fun (Int).isOpposing(od:Int):Boolean = ((this + 2) % 4 == od)



