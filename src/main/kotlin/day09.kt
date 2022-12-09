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

    input.fold(startingPoints) { acc, command ->
        applyMoveOperationToAllKnots(
            knotsStartPosition = acc,
            command = command,
            visited = visited
        )
    }

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
        val k1 = acc[1] follow newHead
        val k2 = acc[2] follow k1
        val k3 = acc[3] follow k2
        val k4 = acc[4] follow k3
        val k5 = acc[5] follow k4
        val k6 = acc[6] follow k5
        val k7 = acc[7] follow k6
        val k8 = acc[8] follow k7
        val k9 = acc[9] follow k8
        visited.add(k9)
        listOf(newHead, k1, k2, k3, k4, k5, k6, k7, k8, k9)
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
