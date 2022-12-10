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
    var registerNum = 1

    val ticks = input.map { parseOperation(line = it.trim()) }
        .flatMap { op ->
            processOperation(op = op, registerNum = registerNum).also {
                registerNum = it.second
            }.first
        }

    val relevantCyclesNum = (ticks.size - 20).floorDiv(40) + 1
    return (0 until relevantCyclesNum)
        .map { 20 + (it.times(40)) }
        .sumOf { index -> index * ticks[index - 1] }
}

private fun solvePart2(input: List<String>) {
    var spritePos = 1

    input.map { parseOperation(line = it.trim()) }
        .flatMap { op ->
            val (ticks, newRegisterNum) = processOperation(op = op, registerNum = spritePos)
            spritePos = newRegisterNum
            ticks
        }.mapIndexed { index, spritePos ->
            // draw if circle nr (where CRT is drawing) is contained by sprite
            if(listOf(spritePos - 1, spritePos, spritePos + 1).contains(index % 40)) "#" else ' '
        }.chunked(40).forEach {
            println(it.joinToString(" "))
        }
}

private fun processOperation(op: Operation, registerNum: Int) : Pair<List<Int>, Int> {
    return when(op){
        is Operation.MoveSpriteOp -> {
            val ticks = listOf(registerNum, registerNum)
            return Pair(ticks, registerNum + op.offset)
        }
        else -> Pair(listOf(registerNum), registerNum)
    }
}

private fun parseOperation(line: String): Operation {
    return when(line){
        "noop" -> Operation.Noop
        else -> Operation.MoveSpriteOp(line.split(" ")[1].toInt())
    }
}
