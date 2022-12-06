fun main() {
    assertEquals(solveDay06Part1("bvwbjplbgvbhsrlpgdmjqwftvncz"), 5)
    assertEquals(solveDay06Part1("mjqjpqmgbljsphdztnvjfqwrcgsmlb"), 7)
    assertEquals(solveDay06Part1("nznrnfrfntjfmvfwmzdfjlvtqnbhcprsg"), 10)
    assertEquals(solveDay06Part1("zcfzfwzzqfrljwzlrfnpqdbhtmscgvjw"), 11)

    val input = readInput("day06")
    benchmark { solveDay06Part1(input).also { println("Solution part one: $it") } }

    assertEquals(solveDay06Part2("mjqjpqmgbljsphdztnvjfqwrcgsmlb"), 19)
    benchmark { solveDay06Part2(input).also { println("Solution part two: $it") } }
}

private fun solveDay06Part1(input: String): Int = getIndexOfFirstUniqueSubsequence(input, 4)

private fun solveDay06Part2(input: String): Int = getIndexOfFirstUniqueSubsequence(input, 14)

private fun getIndexOfFirstUniqueSubsequence(s: String, length: Int): Int {
    for (index in 0..s.length - length) {
        if (s.subSequence(index, index + length).consistsOfUniqueChars()) return index + length
    }
    throw IllegalArgumentException("input does not contain sequence of $length unique chars!")
}

fun CharSequence.consistsOfUniqueChars() = this.toSet().size == this.length
