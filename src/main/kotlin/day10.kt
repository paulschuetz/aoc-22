package day10

import assertEquals
import benchmark
import readInputLines

fun main() {
    val day = "10"

    val demoInputLines = readInputLines("day$day-demo")
    assertEquals(solvePart1(demoInputLines), 13140)

    val inputLines = readInputLines("day$day")
    benchmark { solvePart1(inputLines).also { println("Solution part one: $it") } }

    benchmark { solvePart2(inputLines) }
}

sealed interface Operation {
    object Noop : Operation
    class MoveSpriteOp(val offset: Int) : Operation
}

private fun solvePart1(input: List<String>): Int {
    val ticks = input.map { parseOperation(line = it.trim()) }
        .scan(Pair(emptyList<Int>(), 1)) { prevOpRes, op ->
            processOperation(op = op, spritePos = prevOpRes.second)
        }.flatMap { it.first }

    val relevantCyclesNum = (ticks.size - 20).floorDiv(40) + 1
    return (0 until relevantCyclesNum)
        .map { 20 + (it.times(40)) }
        .sumOf { index -> index * ticks[index - 1] }
}

private fun solvePart2(input: List<String>) {
    input.map { parseOperation(line = it.trim()) }
        .scan(Pair(emptyList<Int>(), 1)) { prevOpRes, op ->
            processOperation(op = op, spritePos = prevOpRes.second)
        }.flatMap { it.first }
        .mapIndexed { index, spritePos ->
            if (listOf(spritePos - 1, spritePos, spritePos + 1).contains(index % 40)) "#" else ' '
        }.chunked(40).forEach {
            println(it.joinToString(" "))
        }
}

private fun processOperation(op: Operation, spritePos: Int): Pair<List<Int>, Int> {
    return when (op) {
        is Operation.MoveSpriteOp -> {
            val ticks = listOf(spritePos, spritePos)
            return Pair(ticks, spritePos + op.offset)
        }
        else -> Pair(listOf(spritePos), spritePos)
    }
}

private fun parseOperation(line: String): Operation {
    return when (line) {
        "noop" -> Operation.Noop
        else -> Operation.MoveSpriteOp(line.split(" ")[1].toInt())
    }
}
