import kotlin.math.abs
import kotlin.math.sign

fun main() {
    val inputLines = readInputLines("day09")
    benchmark { solve(inputLines, 2).also { println("Solution part one: $it") } }

    val demoInputLines = readInputLines("day09-demo-part2")
    assertEquals(solve(demoInputLines, 10), 36)

    benchmark { solve(inputLines, 10).also { println("Solution part two: $it") } }
}

fun solve(input: List<String>, knots: Int): Int {
    val startingPoints = List(knots) { Coordinate(x = 0, y = 0) }
    val visited = mutableSetOf(Coordinate(0, 0))

    input.fold(startingPoints) { acc, command -> moveRope(ropeKnots = acc, command = command, visited = visited) }

    return visited.size
}

fun moveRope(
    ropeKnots: List<Coordinate>,
    command: String,
    visited: MutableSet<Coordinate>
): List<Coordinate> {
    val (direction, times) = command.trim().split(" ")
    return IntRange(1, times.toInt()).fold(ropeKnots) { acc, _ ->
        val newHead = moveHead(acc.first(), direction.first())
        val newPositions = acc.drop(1).scan(newHead) { head, tail -> tail follow head }
        visited.add(newPositions.last())
        newPositions
    }
}

infix fun Coordinate.follow(head: Coordinate): Coordinate {
    val dx = head.x - this.x
    val dy = head.y - this.y

    return if (abs(dx) <= 1 && abs(dy) <= 1) this
    else this + Coordinate(dx.sign, dy.sign)
}

fun moveHead(coordinate: Coordinate, direction: Char): Coordinate = when (direction) {
    'R' -> Coordinate(x = coordinate.x + 1, y = coordinate.y)
    'L' -> Coordinate(x = coordinate.x - 1, y = coordinate.y)
    'U' -> Coordinate(x = coordinate.x, y = coordinate.y - 1)
    'D' -> Coordinate(x = coordinate.x, y = coordinate.y + 1)
    else -> error("bad direction")
}
