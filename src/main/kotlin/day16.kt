package day16

import assertEquals
import benchmark
import readInputLines
import java.util.*

fun main() {
    val day = "16"

    val demoInputLines = readInputLines("day$day-demo")
    assertEquals(solvePart1(demoInputLines), 1651)

    val inputLines = readInputLines("day$day")
    benchmark { solvePart1(inputLines).also { println("Solution part one: $it") } }

    assertEquals(solvePart2(demoInputLines), 1707)
    benchmark { solvePart2(inputLines).also { println("Solution part two: $it") } }
}

private fun solvePart1(input: List<String>): Int {
    val valves = input.map { readNode(it) }.associateBy { it.name }
    val relevantValves = valves.filter { it.value.flowRate > 0 || it.key == "AA" }

    val shortestPaths = relevantValves.values.flatMap { valve ->
        relevantValves.values.filter { it.name != valve.name }
            .map { otherValve -> (valve.name to otherValve.name) to dfs(valves, valve.name, otherValve.name) }
    }.toMap()

    return maxPressureRelease("AA", 30, relevantValves.filterKeys { it != "AA" }, shortestPaths)
}

private fun solvePart2(input: List<String>): Int {
    val valves = input.map { readNode(it) }.associateBy { it.name }
    val relevantValves = valves.filter { it.value.flowRate > 0 || it.key == "AA" }

    val shortestPaths = relevantValves.values.flatMap { valve ->
        relevantValves.values.filter { it.name != valve.name }
            .map { otherValve -> (valve.name to otherValve.name) to dfs(valves, valve.name, otherValve.name) }
    }.toMap()

    val bestPaths = allPossiblePaths(
        currentPath = Path(valves = setOf("AA"), pressureRelease = 0),
        remainingSeconds = 26,
        closedValves = relevantValves.filterKeys { it != "AA" },
        shortestPaths = shortestPaths
    )
        .map { path -> path.copy(valves = path.valves.filter { it != "AA" }.toSet()) }
        .sortedByDescending { it.pressureRelease }

    var max = 0

    for (humanPath in bestPaths) {
        if (humanPath.pressureRelease + bestPaths.first().pressureRelease < max) break
        for (elePath in bestPaths.filter { path -> !path.valves.any { it in humanPath.valves } }) {
            if (humanPath.pressureRelease + elePath.pressureRelease > max) {
                max = humanPath.pressureRelease + elePath.pressureRelease
            } else break
        }
    }

    return max
}

operator fun <T : Any> Pair<T, T>.get(index: Int) = if (index == 0) this.first else this.second

data class Path(
    val valves: Set<String>,
    val pressureRelease: Int
)

fun allPossiblePaths(
    currentPath: Path,
    remainingSeconds: Int,
    closedValves: Map<String, Valve>,
    shortestPaths: Map<Pair<String, String>, Int>,
): List<Path> {

    val subsequentPaths = mutableListOf(currentPath)

    for (valve in closedValves.filterKeys { it != currentPath.valves.last() }.values) {
        val pathLength = shortestPaths[currentPath.valves.last() to valve.name]!!
        val newRemainingSeconds = remainingSeconds - pathLength - 1
        if (newRemainingSeconds > 0) {
            // basically we compute each valve path that is possible to travel in t seconds
            val allSubsequentPathsFromSelectedValve = allPossiblePaths(
                currentPath = Path(
                    valves = currentPath.valves + valve.name,
                    pressureRelease = currentPath.pressureRelease + newRemainingSeconds * valve.flowRate
                ),
                remainingSeconds = newRemainingSeconds,
                closedValves = closedValves - valve.name,
                shortestPaths = shortestPaths
            )
            subsequentPaths += allSubsequentPathsFromSelectedValve
        }
    }
    // we return once there are no more valves to travel to or we have no more time to travel
    return subsequentPaths
}

fun maxPressureRelease(
    currentValve: String,
    remainingSeconds: Int,
    closedValves: Map<String, Valve>,
    shortestPaths: Map<Pair<String, String>, Int>,
): Int {
    var maxPressureRelease = 0

    for (valve in closedValves.filterKeys { it != currentValve }.values) {
        val pathLength = shortestPaths[currentValve to valve.name]!!
        val newRemainingSeconds = remainingSeconds - pathLength - 1
        if (newRemainingSeconds > 0) {
            // basically we try each path that is possible to go in 30 seconds and always open the valve
            val pressureRelease = newRemainingSeconds * valve.flowRate + maxPressureRelease(
                valve.name,
                newRemainingSeconds,
                closedValves.filterKeys { it != valve.name },
                shortestPaths,
            )
            // if recursion stack came back we always update with max pressure release for that path
            if (pressureRelease > maxPressureRelease) {
                maxPressureRelease = pressureRelease
            }
        }
    }
    // we return once there are no more meaningful
    return maxPressureRelease
}

data class Valve(
    val name: String,
    val flowRate: Int,
    val neighbours: List<String>,
)

fun dfs(nodes: Map<String, Valve>, start: String, end: String): Int {
    val visited = mutableSetOf(start)
    val distances = nodes.mapValues { Integer.MAX_VALUE }.toMutableMap()
    val queue: Queue<String> = ArrayDeque<String>().apply { add(start) }
    distances[start] = 0

    while (queue.isNotEmpty()) {
        val current = queue.poll()
        if (current == end) return distances[current]!!

        nodes[current]!!.neighbours
            .filter { it !in visited }
            .forEach {
                distances[it] = distances[current]!! + 1
                queue.add(it)
                visited.add(it)
            }
    }
    throw IllegalArgumentException("bad input")
}

private fun readNode(line: String): Valve {
    val valves = Regex("""([A-Z]{2})""").findAll(line).toList().map { it.value }
    val flowRate = Regex("""(\d+)""").findAll(line).toList().map { it.value }
    return Valve(name = valves[0], flowRate = flowRate.single().toInt(), neighbours = valves.drop(1))
}
