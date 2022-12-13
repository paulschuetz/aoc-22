package day12

import assertEquals
import benchmark
import readInputLines
import java.util.*

fun main() {
    val day = "12"

    val demoInputLines = readInputLines("day$day-demo")
    assertEquals(solvePart1(demoInputLines), 31)

    val inputLines = readInputLines("day$day")
    benchmark { solvePart1(inputLines).also { println("Solution part one: $it") } }

    // assertEquals(solvePart2(demoInputLines), 29)
    benchmark { solvePart2(inputLines).also { println("Solution part two: $it") } }
}

data class Node(
    val point: Point,
    val height: Char
)

data class Point(
    val x: Int,
    val y: Int
)

private fun solvePart1(lines: List<String>): Int {
    val nodes = lines.flatMapIndexed { y, line ->
        line.mapIndexed { x, char ->
            Point(x, y) to Node(point = Point(x, y), height = char)
        }
    }.toMap()

    val startPoint = nodes.values.single { it.height == 'S' }.point
    val destinationPoint = nodes.values.single { it.height == 'E' }.point

    return dfs(nodes, start = destinationPoint, destinations = listOf(startPoint))
}

private fun solvePart2(lines: List<String>): Int {
    val nodes = lines.flatMapIndexed { y, line ->
        line.mapIndexed { x, char ->
            Point(x, y) to Node(point = Point(x, y), height = char)
        }
    }.toMap()

    val startingPointCandidates = nodes.values.filter { it.height == 'S' || it.height == 'a' }.map { it.point }
    val destinationPoint = nodes.values.single { it.height == 'E' }.point

    return dfs(nodes, start = destinationPoint, destinations = startingPointCandidates)
}

private fun dfs(nodes: Map<Point, Node>, start: Point, destinations: List<Point>): Int {
    val visited = mutableSetOf(start)
    val distances = nodes.mapValues { Integer.MAX_VALUE }.toMutableMap()
    val queue: Queue<Point> = ArrayDeque<Point>().apply { add(start) }
    distances[start] = 0

    while (queue.isNotEmpty()) {
        val current = queue.poll()
        if (current in destinations) return distances[current]!!

        nodes[current]!!.getNeighboursThatCanReachMe(nodes).filter { neighbour ->
            distances[current]!! + 1 < distances[neighbour.point]!!
        }.filter { it.point !in visited }.forEach {
            distances[it.point] = distances[current]!! + 1
            queue.add(it.point)
            visited.add(it.point)
        }
    }
    throw IllegalArgumentException("bad input")
}
private fun Node.getNeighboursThatCanReachMe(nodes: Map<Point, Node>): List<Node> {
    // get all neighbours - left right up down
    val left = nodes[Point(this.point.x - 1, this.point.y)]
    val right = nodes[Point(this.point.x + 1, this.point.y)]
    val up = nodes[Point(this.point.x, this.point.y - 1)]
    val down = nodes[Point(this.point.x, this.point.y + 1)]
    // filter only reachable ones
    return listOfNotNull(left, right, up, down)
        .filter { neighbour -> neighbour canReach this }
}

private infix fun Node.canReach(other: Node) = normalizeElevation(other.height) - normalizeElevation(this.height) <= 1

private val normalizeElevation = { height: Char ->
    when (height) {
        'S' -> 'a'
        'E' -> 'z'
        else -> height
    }
}
