fun main() {
    benchmark { solveDay01Part1(readInput("day01")) }
    benchmark { solveDay01Part2(readInput("day01")) }
}

fun solveDay01Part1(input: String) {

    val max = input.split("\n\n").maxOf { elf ->
        elf.lines().sumOf { it.toInt() }
    }
}

fun solveDay01Part2(input: String) {

    val max = input.split("\n\n").map { elf ->
        elf.lines().sumOf { it.toInt() }
    }.sortedDescending().take(3).sum()

    println("The top three elves together carry $max calories with them.")
}