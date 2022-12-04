fun main() {
    val demoInputLines = readInputLines("day04-demo")
    assertEquals(solveDay04Part1(demoInputLines), 2)

    val inputLines = readInputLines("day04")
    benchmark { solveDay04Part1(inputLines).also { println("Solution part one: $it") } }

    assertEquals(solveDay04Part2(demoInputLines), 4)
    benchmark { solveDay04Part2(inputLines).also { println("Solution part two: $it") } }
}

private fun solveDay04Part1(input: List<String>): Int {
    return input.map {
        parseRangePair(it)
    }.count {
        it.first fullyContains it.second || it.second fullyContains it.first
    }
}

private fun solveDay04Part2(input: List<String>): Int {
    return input.map {
        parseRangePair(it)
    }.count {
        it.first overlaps it.second
    }
}

private fun parseRangePair(line: String) = line.split(",").map {
    val (start, end) = it.split("-")
    Range(start = start.trim().toInt(), end = end.trim().toInt())
}.let { it[0] to it[1] }

data class Range(val start: Int, val end: Int)

private infix fun Range.fullyContains(other: Range): Boolean = this.start <= other.start && this.end >= other.end

private infix fun Range.overlaps(other: Range): Boolean =
    this.end >= other.start && this.start <= other.start
            || this.start <= other.end && this.end >= other.end
            || this fullyContains other
            || other fullyContains this
