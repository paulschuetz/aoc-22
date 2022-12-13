package day13

import assertEquals
import benchmark
import readInputLines

fun main() {
    val day = "13"

    val demoInputLines = readInputLines("day$day-demo").filter { it.trim().isNotBlank() }
    assertEquals(solvePart1(demoInputLines), 13)

    val inputLines = readInputLines("day$day").filter { it.trim().isNotBlank() }
    benchmark { solvePart1(inputLines).also { println("Solution part one: $it") } }

    val demoInputLinesWithDividers = demoInputLines + listOf("[[2]]", "[[6]]")
    assertEquals(solvePart2(demoInputLinesWithDividers), 140)

    val inputWithDividers = inputLines + listOf("[[2]]", "[[6]]")
    benchmark { solvePart2(inputWithDividers).also { println("Solution part two: $it") } }
}

private fun solvePart1(input: List<String>): Int {
    return input.asSequence()
        .map { parsePacket(it) }
        .chunked(2)
        .withIndex()
        .filter { it.value[0] inRightOrderWith it.value[1] == ORDER.RIGHT }
        .sumOf { it.index + 1 }
}

private fun solvePart2(input: List<String>): Int {
    val sorted = input
        .map { parsePacket(it) }
        .sortedWith { p1, p2 -> if ((p1 inRightOrderWith p2) == ORDER.WRONG) 1 else -1 }

    val firstDivider = sorted.indexOfFirst { packet -> packet.singleOrNull()?.let { it is List<*> && it.isNotEmpty() && it.first() == 2 } ?: false } + 1
    val secondDivider = sorted.indexOfFirst { packet -> packet.singleOrNull()?.let { it is List<*> && it.isNotEmpty() && it.first() == 6 } ?: false } + 1

    return firstDivider * secondDivider
}

private infix fun List<*>.inRightOrderWith(rightPacket: List<*>): ORDER {
    rightPacket.withIndex().forEach { right ->
        // if left side ran out of items we have right order
        if (this.size < right.index + 1) return ORDER.RIGHT
        val order = this[right.index]!! compareElement right.value!!
        if (order != ORDER.CONTINUE) return order
        if (right.index + 1 == rightPacket.size && this.size > rightPacket.size) return ORDER.WRONG
    }
    return if (this.size > rightPacket.size) ORDER.WRONG
    else ORDER.CONTINUE
}

private infix fun Any.compareElement(el2: Any): ORDER {
    val el1 = this
    if (el1 is List<*> && el2 is List<*>) {
        return el1 inRightOrderWith el2
    } else if (el1 is Int && el2 is Int) {
        if (el2 < el1) return ORDER.WRONG
        else if (el1 < el2) return ORDER.RIGHT
    } else {
        return if (el1 is Int) listOf(el1) inRightOrderWith el2 as List<*>
        else el1 as List<*> inRightOrderWith listOf(el2)
    }
    return ORDER.CONTINUE
}

enum class ORDER {
    RIGHT, WRONG, CONTINUE
}

fun parsePacket(input: String): List<Any> {
    val stack = ArrayDeque<MutableList<Any>>()
    var currentNumDigits = ""

    input.forEach { char ->
        when (char) {
            '[' -> stack.add(mutableListOf())
            ']' -> {
                if (currentNumDigits.isNotEmpty()) stack.last().add(currentNumDigits.toInt().also { currentNumDigits = "" })
                val currentElement = stack.removeLast()
                if (stack.isNotEmpty()) stack.last().add(currentElement)
                else return currentElement
            }
            ',' -> if (currentNumDigits.isNotBlank()) stack.last().add(currentNumDigits.toInt().also { currentNumDigits = "" })
            else -> currentNumDigits = "$currentNumDigits$char"
        }
    }
    throw IllegalArgumentException("Bad input")
}
