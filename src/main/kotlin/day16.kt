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

    return maxPressureReleaseTogether(Pair("AA", "AA"), Pair(26, 26), relevantValves.filterKeys { it != "AA" }, shortestPaths)
}

operator fun <T: Any> Pair<T, T>.get(index: Int) = if(index == 0) this.first else this.second

fun maxPressureReleaseTogether(
    positions: Pair<String, String>,
    remainingSeconds: Pair<Int, Int>,
    closedValves: Map<String, Valve>,
    shortestPaths: Map<Pair<String, String>, Int>,
): Int {
    var maxPressureRelease = 0

    val actor = if(remainingSeconds[0] > remainingSeconds[1]) 0 else 1

    for (valve in closedValves.filterKeys { it != positions[actor] }.values) {
        val pathLength = shortestPaths[positions[actor] to valve.name]!!
        val newRemainingSeconds = remainingSeconds[actor] - pathLength - 1
        if (newRemainingSeconds > 0) {
            // basically we try each path that is possible to go in 30 seconds and always open the valve
            val pressureRelease = newRemainingSeconds * valve.flowRate + maxPressureReleaseTogether(
                if (actor == 0) Pair(valve.name, positions[1]) else Pair(positions[0], valve.name),
                if (actor == 0) Pair(newRemainingSeconds, remainingSeconds[1]) else Pair(remainingSeconds[0], newRemainingSeconds),
                closedValves.filterKeys { it != valve.name },
                shortestPaths,
            )
            // if recursion stack comes back we always update with max pressure release for that subpath
            if (pressureRelease > maxPressureRelease) {
                maxPressureRelease = pressureRelease
            }
        }
    }
    // we return once there are no more meaningful
    return maxPressureRelease
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
