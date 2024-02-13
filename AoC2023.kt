package com.sf.aoc2023

// This class allows me to pick the day I want to work on using a command line parameter without having to
// have all Days on the machine I am working on (I work on multiple machines)
// It uses reflection which I had to add to the libraries

// Each Day class is written in a way that 'solve(file)' can be renamed as 'main()'
// as long as the 'file' variable within is replaced with the filename of the input
// (if there is an input file used)
import kotlin.reflect.full.createInstance
import com.sf.aoc.*

interface Solver {
    fun solve(file: String)
}

fun main(args: Array<String>) {

    println("\nAoC 2023 - Day $yellow$bold${args[0]}$reset - File: $yellow${bold}d${args[0]}.${args[1]}.txt$reset\n")
    val kClass = Class.forName("com.sf.aoc2023.Day${args[0]}").kotlin
    val solveFun = kClass.members.filter { it.name == "solve" }[0]
    val day = kClass.createInstance()

    val start = System.nanoTime()
    solveFun.call( day, "src/main/kotlin/Data/d${args[0]}.${args[1]}.txt" )
    println("\nElapsed time: $green$bold${"%,d".format(System.nanoTime()-start)}$reset ns")
}