fun main() {
    val input = readInput("day01")
    benchmark { solveDay01Part1(input) }.also { println("Solution part one: $it") }
    benchmark { solveDay01Part2(input) }.also { println("Solution part two: $it") }
}

fun solveDay01Part1(input: String) = input.split("\n\n").maxOf { elf ->
    elf.lines().sumOf { it.toInt() }
}

fun solveDay01Part2(input: String) = input.split("\n\n").map { elf ->
    elf.lines().sumOf { it.toInt() }
}.sortedDescending().take(3).sum()