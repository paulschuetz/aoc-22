import java.util.*

fun main() {

    val input = readInput("day05")
    val (initialCratesInputRaw, instructionsInputRaw) = input.split("\n\n")

    val instructions = parseInstructions(rawInput = instructionsInputRaw)

    benchmark {
        solveDay05Part1(stacks = parseStacks(rawInput = initialCratesInputRaw), instructions = instructions)
            .also { println("Solution part one: $it") }
    }

    benchmark {
        solveDay05Part2(stacks = parseStacks(rawInput = initialCratesInputRaw), instructions = instructions)
            .also { println("Solution part two: $it") }
    }
}

private fun solveDay05Part1(stacks: Map<Int, Stack<Char>>, instructions: List<Instruction>): String {
    instructions.forEach { moveCrates(from = stacks[it.from]!!, to = stacks[it.to]!!, amount = it.amount) }
    return stacks.toSortedMap().map { it.value.peek() }.joinToString("")
}

private fun solveDay05Part2(stacks: Map<Int, Stack<Char>>, instructions: List<Instruction>): String {
    instructions.forEach { moveCratesTogether(from = stacks[it.from]!!, to = stacks[it.to]!!, amount = it.amount) }
    return stacks.toSortedMap().map { it.value.peek() }.joinToString("")
}

data class Instruction(val from: Int, val to: Int, val amount: Int)

private fun parseInstructions(rawInput: String): List<Instruction> {
    val instrRegex = Regex("[0-9]+")

    return rawInput.split("\n").map { line ->
        val (amount, from, to) = instrRegex.findAll(line).map { it.value.toInt() }.toList()
        Instruction(from = from, to = to, amount = amount)
    }
}

private fun parseStacks(rawInput: String): Map<Int, Stack<Char>> {
    val initialCratesInputLines = rawInput.split("\n").dropLast(1)

    val regex = Regex("[A-Z]")
    val stacks = mutableMapOf<Int, Stack<Char>>()

    initialCratesInputLines.reversed().forEach { line ->
        regex.findAll(line).map { it.range.first to it.value[0] }
            .forEach { match ->
                val index = ((match.first - 1) / 4) + 1
                if (stacks[index] == null) stacks[index] = Stack()
                stacks[index]!!.push(match.second)
            }
    }

    return stacks
}

private fun moveCrates(from: Stack<Char>, to: Stack<Char>, amount: Int) = repeat(amount) {
    to.push(from.pop())
}

private fun moveCratesTogether(from: Stack<Char>, to: Stack<Char>, amount: Int) {
    (1..amount).map {
        from.pop()
    }.reversed().forEach { to.push(it) }
}
