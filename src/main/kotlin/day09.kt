import kotlin.math.abs
import kotlin.math.sign

fun main() {

    val demoInputLines = readInputLines("day09-demo-part2")
    assertEquals(solveDay09Part2(demoInputLines), 36)

    val inputLines = readInputLines("day09")
    benchmark { solveDay09Part2(inputLines).also { println("Solution part two: $it") } }
}

private fun solveDay09Part2(input: List<String>): Int {

    val startingPoints = sequence {
        repeat(10) { yield(Coordinate(x = 0, y = 0)) }
    }.toList()

    val visited = mutableSetOf(Coordinate(0, 0))

    val finalPos = input.fold(startingPoints) { acc, command ->
        applyMoveOperationToAllKnots(
            knotsStartPosition = acc,
            command = command,
            visited = visited
        )
    }

    println("Final position is $finalPos and we visited ${visited.size} unique coordinates.")

    return visited.size
}

fun applyMoveOperationToAllKnots(
    knotsStartPosition: List<Coordinate>,
    command: String,
    visited: MutableSet<Coordinate>
): List<Coordinate> {
    val (direction, times) = command.trim().split(" ")
    return IntRange(1, times.toInt()).fold(knotsStartPosition) { acc, _ ->
        // after we move the head we move all other
        val newHead = moveHead(acc.first(), direction.first())
        val k1 = follow(acc[1], newHead)
        val k2 = follow(acc[2], k1)
        val k3 = follow(acc[3], k2)
        val k4 = follow(acc[4], k3)
        val k5 = follow(acc[5], k4)
        val k6 = follow(acc[6], k5)
        val k7 = follow(acc[7], k6)
        val k8 = follow(acc[8], k7)
        val k9 = follow(acc[9], k8)
        visited.add(k9)
        listOf(newHead, k1, k2, k3, k4, k5, k6, k7, k8, k9)
    }
}

fun follow(tail: Coordinate, head: Coordinate): Coordinate {
    val dx = head.x - tail.x
    val dy = head.y - tail.y
    return when {
        abs(dx) <= 1 && abs(dy) <= 1 -> tail
        abs(dx) < abs(dy) -> Coordinate(x = head.x, head.y - dy.sign)
        abs(dx) > abs(dy) -> Coordinate(head.x - dx.sign, head.y)
        // needed for part 2 diagonal scenario
        else -> Coordinate(head.x - dx.sign, head.y - dy.sign)
    }
}

fun moveHead(coordinate: Coordinate, direction: Char): Coordinate = when (direction) {
    'R' -> Coordinate(x = coordinate.x + 1, y = coordinate.y)
    'L' -> Coordinate(x = coordinate.x - 1, y = coordinate.y)
    'U' -> Coordinate(x = coordinate.x, y = coordinate.y - 1)
    'D' -> Coordinate(x = coordinate.x, y = coordinate.y + 1)
    else -> error("bad direction")
}
