package day00

import assertEquals
import benchmark
import readInputLines

fun main() {
    val day = "00"

    val demoInputLines = readInputLines("day$day-demo")
    assertEquals(solvePart1(demoInputLines), demoInputLines.size)

    val inputLines = readInputLines("day$day")
    benchmark { solvePart1(inputLines).also { println("Solution part one: $it") } }

//     assertEquals(solvePart2(demoInputLines), demoInputLines.size)
//     benchmark { solvePart2(inputLines).also { println("Solution part two: $it") } }
}

private fun solvePart1(input: List<String>): Int {
    return input.size
}

private fun solvePart2(input: List<String>): Int {
    return input.size
}
