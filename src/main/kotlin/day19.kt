package day19

import assertEquals
import benchmark
import readInputLines
import kotlin.math.max

fun main() {
    val day = "19"

    val demoInputLines = readInputLines("day$day-demo")
    assertEquals(solvePart1(demoInputLines), 33)

    val inputLines = readInputLines("day$day")
    benchmark { solvePart1(inputLines).also { println("Solution part one: $it") } }

//     assertEquals(solvePart2(demoInputLines), demoInputLines.size)
//     benchmark { solvePart2(inputLines).also { println("Solution part two: $it") } }
}

data class ConstructionCost(
    val oreCost: Int = 0,
    val clayCost: Int = 0,
    val obsidianCost: Int = 0
)

data class Inventory(
    val ore: Int = 0,
    val clay: Int = 0,
    val obsidian: Int = 0,
    val totalGeode: Int = 0,
    val oreRobots: Int = 1,
    val clayRobots: Int = 0,
    val obsidianRobots: Int = 0,
    val geodeRobot: Int = 0
)

data class SimulationState(
    val inventory: Inventory,
    val remainingTime: Int = 24
)

data class Blueprint(
    val id: Int,
    val oreRobotCost: ConstructionCost,
    val clayRobotCost: ConstructionCost,
    val obsidianRobotCost: ConstructionCost,
    val geodeRobotCost: ConstructionCost
)

private fun solvePart1(input: List<String>): Int {
    val blueprints = input.map { parseBlueprint(it) }
    // bfs https://topaz.github.io/paste/#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
    return blueprints.sumOf { it.id * maxGeodeProductionForBlueprint(it) }
}

fun maxGeodeProductionForBlueprint(blueprint: Blueprint): Int {
    val queue = ArrayDeque(listOf(SimulationState(inventory = Inventory(), remainingTime = 24)))

    var maxGeodes = 0

    while (queue.isNotEmpty()) {
        val currentState = queue.removeFirst()
        // if we have only one time unit left building anything does not make any sense
        if (currentState.remainingTime <= 1) continue
        // branch to build all four robot types (if it makes sense)
        buildOreRobot(blueprint, currentState)?.also { queue.addLast(it); if(it.inventory.totalGeode > maxGeodes) maxGeodes = it.inventory.totalGeode }
        buildClayRobot(blueprint, currentState)?.also { queue.addLast(it); if(it.inventory.totalGeode > maxGeodes) maxGeodes = it.inventory.totalGeode }
        buildObsidianRobot(blueprint, currentState)?.also { queue.addLast(it); if(it.inventory.totalGeode > maxGeodes) maxGeodes = it.inventory.totalGeode }
        buildGeodeRobot(blueprint, currentState)?.also { queue.addLast(it); if(it.inventory.totalGeode > maxGeodes) maxGeodes = it.inventory.totalGeode }
    }

    return maxGeodes;
}

fun buildOreRobot(blueprint: Blueprint, simulationState: SimulationState): SimulationState? {
    val timeUnitsToSkip = if (simulationState.inventory.ore >= blueprint.oreRobotCost.oreCost) 1
    else {
        (blueprint.oreRobotCost.oreCost - simulationState.inventory.ore).floorDiv(simulationState.inventory.oreRobots)
    }
    // should we produce another ore robot?
    if(simulationState.remainingTime - timeUnitsToSkip <= 5) return null;

    return SimulationState(
        inventory = Inventory(
            ore = simulationState.inventory.ore + (timeUnitsToSkip * simulationState.inventory.oreRobots) - blueprint.oreRobotCost.oreCost,
            clay = simulationState.inventory.clay + timeUnitsToSkip * simulationState.inventory.clayRobots,
            obsidian = simulationState.inventory.obsidian + timeUnitsToSkip * simulationState.inventory.obsidianRobots,
            oreRobots = simulationState.inventory.oreRobots + 1
        ),
        remainingTime = simulationState.remainingTime - timeUnitsToSkip
    )
}

fun buildClayRobot(blueprint: Blueprint, simulationState: SimulationState): SimulationState? {
    val timeUnitsToSkip = if (simulationState.inventory.ore >= blueprint.clayRobotCost.oreCost) 1
    else {
        (blueprint.clayRobotCost.oreCost - simulationState.inventory.ore).floorDiv(simulationState.inventory.oreRobots)
    }
    // should we produce another clay robot?
    if(simulationState.remainingTime - timeUnitsToSkip <= 3) return null;

    return SimulationState(
        inventory = Inventory(
            ore = simulationState.inventory.ore + (timeUnitsToSkip * simulationState.inventory.oreRobots) - blueprint.clayRobotCost.oreCost,
            clay = simulationState.inventory.clay + timeUnitsToSkip * simulationState.inventory.clayRobots,
            obsidian = simulationState.inventory.obsidian + timeUnitsToSkip * simulationState.inventory.obsidianRobots,
            clayRobots = simulationState.inventory.clayRobots + 1
        ),
        remainingTime = simulationState.remainingTime - timeUnitsToSkip
    )
}

fun buildObsidianRobot(blueprint: Blueprint, simulationState: SimulationState): SimulationState? {
    if(simulationState.inventory.clayRobots == 0) return null;
    val timeUnitsToSkip = if (simulationState.inventory.ore >= blueprint.clayRobotCost.oreCost && simulationState.inventory.clay >= blueprint.clayRobotCost.clayCost) 1
    else {
        val timeToWaitForOre = (blueprint.obsidianRobotCost.oreCost - simulationState.inventory.ore).floorDiv(simulationState.inventory.oreRobots)
        val timeToWaitForClay = (blueprint.obsidianRobotCost.clayCost - simulationState.inventory.clay).floorDiv(simulationState.inventory.clayRobots)
        max(timeToWaitForOre, timeToWaitForClay)
    }

    return SimulationState(
        inventory = Inventory(
            ore = simulationState.inventory.ore + (timeUnitsToSkip * simulationState.inventory.oreRobots) - blueprint.obsidianRobotCost.oreCost,
            clay = simulationState.inventory.clay + (timeUnitsToSkip * simulationState.inventory.clayRobots) - blueprint.obsidianRobotCost.clayCost,
            obsidian = simulationState.inventory.obsidian + (timeUnitsToSkip * simulationState.inventory.obsidianRobots),
            obsidianRobots = simulationState.inventory.obsidianRobots + 1
        ),
        remainingTime = simulationState.remainingTime - timeUnitsToSkip
    )
}

fun buildGeodeRobot(blueprint: Blueprint, simulationState: SimulationState): SimulationState? {
    if(simulationState.inventory.obsidianRobots == 0) return null;
    val timeUnitsToSkip = if (simulationState.inventory.ore >= blueprint.geodeRobotCost.oreCost && simulationState.inventory.obsidian >= blueprint.geodeRobotCost.obsidianCost) 1
    else {
        val timeToWaitForOre = (blueprint.geodeRobotCost.oreCost - simulationState.inventory.ore).floorDiv(simulationState.inventory.oreRobots)
        val timeToWaitForObsidian = (blueprint.geodeRobotCost.obsidianCost - simulationState.inventory.obsidian).floorDiv(simulationState.inventory.obsidianRobots)
        max(timeToWaitForOre, timeToWaitForObsidian)
    }

    return SimulationState(
        inventory = Inventory(
            ore = simulationState.inventory.ore + (timeUnitsToSkip * simulationState.inventory.oreRobots) - blueprint.geodeRobotCost.oreCost,
            clay = simulationState.inventory.clay + (timeUnitsToSkip * simulationState.inventory.clayRobots),
            obsidian = simulationState.inventory.obsidian + (timeUnitsToSkip * simulationState.inventory.obsidianRobots) - blueprint.geodeRobotCost.obsidianCost,
            totalGeode = simulationState.inventory.totalGeode + (simulationState.remainingTime - timeUnitsToSkip)
        ),
        remainingTime = simulationState.remainingTime - timeUnitsToSkip
    )
}

fun parseBlueprint(line: String): Blueprint {
    val matches = Regex("""[0-9]+""").findAll(line).map { it.value.toInt() }.toList()
    return Blueprint(
        id = matches[0],
        oreRobotCost = ConstructionCost(oreCost = matches[1]),
        clayRobotCost = ConstructionCost(oreCost = matches[2]),
        obsidianRobotCost = ConstructionCost(oreCost = matches[3], clayCost = matches[4]),
        geodeRobotCost = ConstructionCost(oreCost = matches[5], obsidianCost = matches[6])
    )
}

private fun solvePart2(input: List<String>): Int {
    return input.size
}
