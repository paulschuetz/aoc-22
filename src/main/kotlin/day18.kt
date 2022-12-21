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

    assertEquals(solvePart2(demoInputLines), 58)
    benchmark { solvePart2(inputLines).also { println("Solution part two: $it") } }
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

private fun solvePart2(input: List<String>): Int {
    val cubes = input.map { parseCube(it) }.toSet()

    val minX = cubes.minOf { it.x } - 1
    val maxX = cubes.maxOf { it.x } + 1
    val minY = cubes.minOf { it.y } - 1
    val maxY = cubes.maxOf { it.y } + 1
    val minZ = cubes.minOf { it.z } - 1
    val maxZ = cubes.maxOf { it.z } + 1

    val start = CubePosition(minX, minY, minZ)
    val surfaceCubes = mutableMapOf<CubePosition, Int>()
    val visited = mutableSetOf(start)
    val queue = ArrayDeque(listOf(start))

    // dfs flood fill
    while (queue.isNotEmpty()) {
        // check top, left, right, down, front, back if within bounds
        // If it is surface, add to surface else push onto stack
        val currentPosition = queue.removeLast()

        val right = currentPosition.copy(x = currentPosition.x + 1)
        if (right.x in minX..maxX && right !in visited) {
            if (right in cubes) surfaceCubes[right] = (surfaceCubes[right] ?: 0) + 1
            else {
                visited.add(right)
                queue.addLast(right)
            }
        }

        val left = currentPosition.copy(x = currentPosition.x - 1)
        if (left.x in minX..maxX && left !in visited) {
            if (left in cubes) surfaceCubes[left] = (surfaceCubes[left] ?: 0) + 1
            else {
                visited.add(left)
                queue.addLast(left)
            }
        }

        val top = currentPosition.copy(y = currentPosition.y - 1)
        if (top.y in minY..maxY && top !in visited) {
            if (top in cubes) surfaceCubes[top] = (surfaceCubes[top] ?: 0) + 1
            else {
                visited.add(top)
                queue.addLast(top)
            }
        }

        val down = currentPosition.copy(y = currentPosition.y + 1)
        if (down.y in minY..maxY && down !in visited) {
            if (down in cubes) surfaceCubes[down] = (surfaceCubes[down] ?: 0) + 1
            else {
                visited.add(down)
                queue.addLast(down)
            }
        }

        val back = currentPosition.copy(z = currentPosition.z - 1)
        if (back.z in minZ..maxZ && back !in visited) {
            if (back in cubes) surfaceCubes[back] = (surfaceCubes[back] ?: 0) + 1
            else {
                visited.add(back)
                queue.addLast(back)
            }
        }

        val front = currentPosition.copy(z = currentPosition.z + 1)
        if (front.z in minZ..maxZ && front !in visited) {
            if (front in cubes) surfaceCubes[front] = (surfaceCubes[front] ?: 0) + 1
            else {
                visited.add(front)
                queue.addLast(front)
            }
        }
    }

    return surfaceCubes.values.sum()
}

val parseCube = { line: String -> line.split(",").map { it.toInt() }.let { CubePosition(it[0], it[1], it[2]) } }
