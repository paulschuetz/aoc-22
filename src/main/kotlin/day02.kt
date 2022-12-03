fun main() {
    assertEquals(solveDay02Part1(readInputLines("day02-demo")), 15)
    assertEquals(solveDay02Part2(readInputLines("day02-demo")), 12)

    val inputLines = readInputLines("day02")
    benchmark { solveDay02Part1(inputLines) }.also { println("Solution part one: $it") }
    benchmark { solveDay02Part2(inputLines) }.also { println("Solution part two: $it") }
}

private fun solveDay02Part1(input: List<String>) =
    input.sumOf { line ->
        val (opponentSign, mySign) = line.split(" ").map { it.first() }
        outcomeScore(
            outcome(
                opponentShape = parseShape(opponentSign),
                myShape = parseShape(mySign)
            )
        ) + shapeScore(parseShape(mySign))
    }

fun solveDay02Part2(input: List<String>) =
    input.sumOf { line ->
        val (opponentSign, expectedOutcome) = line.split(" ").map { it.first() }
        shapeScore(
            calcShape(
                parseShape(opponentSign),
                parseExpectedOutcome(expectedOutcome)
            )
        ) + outcomeScore(parseExpectedOutcome(expectedOutcome))
    }

enum class Shape {
    ROCK, PAPER, SCISSORS;
}

enum class Outcome {
    WIN, LOSE, DRAW
}

private val parseShape = { shapeChar: Char ->
    when (shapeChar) {
        'X', 'A' -> Shape.ROCK
        'Y', 'B' -> Shape.PAPER
        'Z', 'C' -> Shape.SCISSORS
        else -> throw IllegalArgumentException("invalid shape code $shapeChar")
    }
}

private fun outcome(opponentShape: Shape, myShape: Shape): Outcome {
    return when (myShape to opponentShape) {
        Shape.ROCK to Shape.SCISSORS, Shape.SCISSORS to Shape.PAPER, Shape.PAPER to Shape.ROCK -> Outcome.WIN
        Shape.ROCK to Shape.PAPER, Shape.PAPER to Shape.SCISSORS, Shape.SCISSORS to Shape.ROCK -> Outcome.LOSE
        else -> Outcome.DRAW
    }
}

private val shapeScore = { shape: Shape ->
    when (shape) {
        Shape.ROCK -> 1
        Shape.PAPER -> 2
        Shape.SCISSORS -> 3
    }
}

private val outcomeScore = { outcome: Outcome ->
    when (outcome) {
        Outcome.WIN -> 6
        Outcome.DRAW -> 3
        Outcome.LOSE -> 0
    }
}

private val parseExpectedOutcome = { c: Char ->
    when (c) {
        'X' -> Outcome.LOSE
        'Y' -> Outcome.DRAW
        'Z' -> Outcome.WIN
        else -> throw IllegalArgumentException()
    }
}

val winningShape = mapOf(
    Shape.ROCK to Shape.PAPER,
    Shape.PAPER to Shape.SCISSORS,
    Shape.SCISSORS to Shape.ROCK
)

private val calcShape = { opponentShape: Shape, expectedOutcome: Outcome ->
    when (expectedOutcome) {
        Outcome.WIN -> winningShape[opponentShape]!!
        Outcome.LOSE -> winningShape.entries.associateBy({ it.value }) { it.key }[opponentShape]!!
        Outcome.DRAW -> opponentShape
    }
}