package day14

import assertEquals
import benchmark
import readInputLines
import toward

fun main() {
    val day = "14"

    val demoInputLines = readInputLines("day$day-demo")
    assertEquals(solvePart1(demoInputLines), 24)

    val inputLines = readInputLines("day$day")
    benchmark { solvePart1(inputLines).also { println("Solution part one: $it") } }

    assertEquals(solvePart2(demoInputLines), 93)
    println("worked for demo input")
    benchmark { solvePart2(inputLines).also { println("Solution part two: $it") } }
}

data class Point(val x: Int, val y: Int)

private fun solvePart1(input: List<String>): Int {

    val rocks = scanRocks(input).toMutableSet()
    val maxY = rocks.maxOf { it.y }
    var restingSandCount = 0
    var sand = Point(500, 0)

    while (true) {
        val movedSand = sand.move(obstacles = rocks)

        if (movedSand == sand) {
            rocks += movedSand
            restingSandCount++
            sand = Point(500, 0)
        } else {
            sand = movedSand
        }

        if (movedSand.y >= maxY) return restingSandCount
    }
}

private fun solvePart2(input: List<String>): Int {

    val obstacles = scanRocks(input).toMutableSet()
    val maxY = obstacles.maxOf { it.y }
    var restingSandCount = 0
    var sand = Point(500, 0)

    while (true) {
        val movedSand = sand.move(obstacles = obstacles, floorY = maxY + 2)

        if (movedSand == sand) {
            obstacles += movedSand
            restingSandCount++
            if (movedSand == Point(500, 0)) return restingSandCount
            sand = Point(500, 0)
        } else {
            sand = movedSand
        }
    }
}

private fun scanRocks(input: List<String>) =
    input.flatMap { line ->
        line.split(" -> ")
            .map { Point(x = it.split(",")[0].toInt(), y = it.split(",")[1].toInt()) }
            .zipWithNext { start, end ->
                if (start.x == end.x) (start.y toward end.y).map { Point(start.x, it) }
                else (start.x toward end.x).map { Point(it, start.y) }
            }.flatten()
    }.toSet()

private fun Point.move(obstacles: Set<Point>, floorY: Int? = null): Point {
    return if (this.y + 1 == floorY) return this
    else if (this.copy(y = this.y + 1) !in obstacles) this.copy(y = this.y + 1)
    else if (this.copy(x = this.x - 1, y = this.y + 1) !in obstacles) this.copy(x = this.x - 1, y = this.y + 1)
    else if (this.copy(x = this.x + 1, y = this.y + 1) !in obstacles) this.copy(x = this.x + 1, y = this.y + 1)
    else this
}
