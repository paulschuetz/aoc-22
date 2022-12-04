fun main() {
    val inputLines = readInputLines("day03")

    benchmark { solveDay03Part1(inputLines).also { println("Solution part one: $it") } }
    benchmark { solveDay03Part2(inputLines).also { println("Solution part two: $it") } }
}

private fun solveDay03Part1(input: List<String>): Int =
    input.sumOf { line ->
        val (compartmentA, compartmentB) = line.chunked(line.length / 2).map { it.toCharArray().toSet() }
        val commonChar = compartmentA.intersect(compartmentB).single()
        priorityScore(commonChar)
    }

private fun solveDay03Part2(input: List<String>) =
    input.chunked(3).sumOf { line ->
        val (first, second, third) = line.map { it.toCharArray().toSet() }
        val commonChar = first.intersect(second).intersect(third).single()
        priorityScore(commonChar)
    }

val priorityScore = { c: Char ->
    when {
        c.isUpperCase() -> c.code - 38
        c.isLowerCase() -> c.code - 96
        else -> throw IllegalArgumentException()
    }
}