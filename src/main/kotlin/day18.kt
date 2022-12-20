package day18

import assertEquals
import benchmark
import readInputLines

fun main() {
    val day = "18"

    val demoInputLines = readInputLines("day$day-demo")
    assertEquals(solvePart1(demoInputLines), 64)

    val inputLines = readInputLines("day$day")
    benchmark { solvePart1(inputLines).also { println("Solution part one: $it") } }

//     assertEquals(solvePart2(demoInputLines), demoInputLines.size)
//     benchmark { solvePart2(inputLines).also { println("Solution part two: $it") } }
}

data class CubePosition(val x: Int, val y: Int, val z: Int)

private fun solvePart1(input: List<String>): Int {

    val cubes = input.map { parseCube(it) }
    println(cubes)

    val openCubeSides = mutableMapOf<CubePosition, Int>()

    for (cube in cubes) {
        val relevantCubes = listOf(
            CubePosition(x = cube.x, y = cube.y, z = cube.z + 1),
            CubePosition(x = cube.x, y = cube.y, z = cube.z - 1),
            CubePosition(x = cube.x, y = cube.y + 1, z = cube.z),
            CubePosition(x = cube.x, y = cube.y - 1, z = cube.z),
            CubePosition(x = cube.x + 1, y = cube.y, z = cube.z),
            CubePosition(x = cube.x - 1, y = cube.y, z = cube.z),
        )

        val openSides = 6 - relevantCubes.count { openCubeSides[it] != null }
        openCubeSides[cube] = openSides
        relevantCubes.forEach {
            if (openCubeSides[it] != null) openCubeSides[it] = openCubeSides[it]!! - 1
        }
    }

    return openCubeSides.values.sum()
}

val parseCube = { line: String -> line.split(",").map { it.toInt() }.let { CubePosition(it[0], it[1], it[2]) } }

private fun solvePart2(input: List<String>): Int {
    return input.size
}
