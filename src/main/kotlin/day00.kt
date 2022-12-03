fun main() {
    val demoInputLines = readInputLines("day00-demo")
    assertEquals(solveDay00Part1(demoInputLines), 1337)

    val inputLines = readInputLines("day00")
    benchmark { solveDay00Part1(inputLines) }.also { println("Solution part one: $it") }

    // assertEquals(solveDay00Part2(demoInputLines), 1337)
    // benchmark { solveDay00Part2(inputLines) }.also { println("Solution part two: $it") }
}

private fun solveDay00Part1(input: List<String>): Int = throw NotImplementedError()

private fun solveDay00Part2(input: List<String>): Int = throw NotImplementedError()
