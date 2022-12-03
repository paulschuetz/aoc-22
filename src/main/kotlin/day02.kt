fun main() {
    assertEquals(solveDay02Part1(readInputLines("day02-demo")), 15)
    assertEquals(solveDay02Part2(readInputLines("day02-demo")), 12)

    val inputLines = readInputLines("day02")
    benchmark { solveDay02Part1(inputLines) }.also { println("Solution part one: $it") }
    benchmark { solveDay02Part2(inputLines) }.also { println("Solution part one: $it") }
}

private fun solveDay02Part1(input: List<String>) =
    input.sumOf { line ->
        val (opponentSign, mySign) = line.split(" ").map { it.first() }
        outcomeScore(
            outcome(
                opponentShape = parseOpponentShape(opponentSign),
                myShape = parseMyShape(mySign)
            )
        ) + shapeScore(parseMyShape(mySign))
    }

fun solveDay02Part2(input: List<String>) =
    input.sumOf { line ->
        val (opponentSign, expectedOutcome) = line.split(" ").map { it.first() }
        shapeScore(
            calcShape(
                parseOpponentShape(opponentSign),
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

private val parseMyShape = { shapeChar: Char ->
    when (shapeChar) {
        'X' -> Shape.ROCK
        'Y' -> Shape.PAPER
        'Z' -> Shape.SCISSORS
        else -> throw IllegalArgumentException("invalid shape code $shapeChar")
    }
}

private val parseOpponentShape = { shapeChar: Char ->
    when (shapeChar) {
        'A' -> Shape.ROCK
        'B' -> Shape.PAPER
        'C' -> Shape.SCISSORS
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

private val getWinningShape = { shape: Shape ->
    when (shape) {
        Shape.ROCK -> Shape.PAPER
        Shape.PAPER -> Shape.SCISSORS
        Shape.SCISSORS -> Shape.ROCK
    }
}

private val getLosingShape = { shape: Shape ->
    when (shape) {
        Shape.ROCK -> Shape.SCISSORS
        Shape.PAPER -> Shape.ROCK
        Shape.SCISSORS -> Shape.PAPER
    }
}

private val calcShape = { opponentShape: Shape, expectedOutcome: Outcome ->
    when (expectedOutcome) {
        Outcome.WIN -> getWinningShape(opponentShape)
        Outcome.LOSE -> getLosingShape(opponentShape)
        Outcome.DRAW -> opponentShape
    }
}