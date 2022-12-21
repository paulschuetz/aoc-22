package day19

import assertEquals
import benchmark
import readInputLines
import kotlin.math.ceil
import kotlin.math.max

fun main() {
    val day = "19"

    val demoInputLines = readInputLines("day$day-demo")
    assertEquals(solvePart1(demoInputLines), 33)
    println("part 1 solution succeeded for demo input")

    val inputLines = readInputLines("day$day")
    benchmark { solvePart1(inputLines).also { println("Solution part one: $it") } }

    benchmark { solvePart2(inputLines).also { println("Solution part two: $it") } }
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
    return blueprints.sumOf { it.id * maxGeodeProductionForBlueprint(it, 24) }
}

private fun solvePart2(input: List<String>): Int {
    return input.map { parseBlueprint(it) }
        .take(3)
        .map { maxGeodeProductionForBlueprint(it, 32) }
        .reduce { a, b -> a * b }
}

fun maxGeodeProductionForBlueprint(blueprint: Blueprint, remainingTime: Int): Int {
    val queue = ArrayDeque(listOf(SimulationState(inventory = Inventory(), remainingTime = remainingTime)))
    var maxGeodes = 0

    while (queue.isNotEmpty()) {
        val currentState = queue.removeFirst()
        // if we have only one time unit left building anything does not make any sense
        if (currentState.remainingTime <= 1) continue
        // branch to build all four robot types (if it makes sense) and push to stack
        buildOreRobot(blueprint, currentState)
            ?.also { queue.addLast(it); if (it.inventory.totalGeode > maxGeodes) maxGeodes = it.inventory.totalGeode }

        buildClayRobot(blueprint, currentState)
            ?.also { queue.addLast(it); if (it.inventory.totalGeode > maxGeodes) maxGeodes = it.inventory.totalGeode }

        buildObsidianRobot(blueprint, currentState)
            ?.also { queue.addLast(it); if (it.inventory.totalGeode > maxGeodes) maxGeodes = it.inventory.totalGeode }

        buildGeodeRobot(blueprint, currentState)
            ?.also { queue.addLast(it); if (it.inventory.totalGeode > maxGeodes) maxGeodes = it.inventory.totalGeode }
    }

    println("max geode prod for blueprint ${blueprint.id} is $maxGeodes")

    return maxGeodes
}

fun buildOreRobot(blueprint: Blueprint, simulationState: SimulationState): SimulationState? {
    val timeUnitsToSkip = if (simulationState.inventory.ore >= blueprint.oreRobotCost.oreCost) 1
    else {
        ceil((blueprint.oreRobotCost.oreCost - simulationState.inventory.ore).toDouble() / simulationState.inventory.oreRobots).toInt() + 1
    }
    val newRemainingTime = simulationState.remainingTime - timeUnitsToSkip
    // should we produce another ore robot? if it brings us more ore in the future than we currently have
    if (newRemainingTime <= 5 || newRemainingTime <= blueprint.oreRobotCost.oreCost
        || simulationState.inventory.oreRobots >= maxOf(
            blueprint.clayRobotCost.oreCost,
            blueprint.obsidianRobotCost.oreCost,
            blueprint.geodeRobotCost.oreCost
        )
    ) return null

    return SimulationState(
        inventory = simulationState.inventory.copy(
            ore = simulationState.inventory.ore + (timeUnitsToSkip * simulationState.inventory.oreRobots) - blueprint.oreRobotCost.oreCost,
            clay = simulationState.inventory.clay + timeUnitsToSkip * simulationState.inventory.clayRobots,
            obsidian = simulationState.inventory.obsidian + timeUnitsToSkip * simulationState.inventory.obsidianRobots,
            oreRobots = simulationState.inventory.oreRobots + 1
        ),
        remainingTime = newRemainingTime
    )
}

fun buildClayRobot(blueprint: Blueprint, simulationState: SimulationState): SimulationState? {
    val timeUnitsToSkip = if (simulationState.inventory.ore >= blueprint.clayRobotCost.oreCost) 1
    else {
        ceil((blueprint.clayRobotCost.oreCost - simulationState.inventory.ore).toDouble() / simulationState.inventory.oreRobots).toInt() + 1
    }
    val newRemainingTime = simulationState.remainingTime - timeUnitsToSkip
    // should we produce another clay robot?
    if (newRemainingTime <= 4 || simulationState.inventory.clayRobots >= maxOf(
            blueprint.obsidianRobotCost.clayCost,
            blueprint.geodeRobotCost.clayCost
        )
    ) return null

    return SimulationState(
        inventory = simulationState.inventory.copy(
            ore = simulationState.inventory.ore + (timeUnitsToSkip * simulationState.inventory.oreRobots) - blueprint.clayRobotCost.oreCost,
            clay = simulationState.inventory.clay + timeUnitsToSkip * simulationState.inventory.clayRobots,
            obsidian = simulationState.inventory.obsidian + timeUnitsToSkip * simulationState.inventory.obsidianRobots,
            clayRobots = simulationState.inventory.clayRobots + 1
        ),
        remainingTime = newRemainingTime
    )
}

fun buildObsidianRobot(blueprint: Blueprint, simulationState: SimulationState): SimulationState? {
    if (simulationState.inventory.clayRobots == 0) return null
    val timeUnitsToSkip =
        if (simulationState.inventory.ore >= blueprint.obsidianRobotCost.oreCost && simulationState.inventory.clay >= blueprint.obsidianRobotCost.clayCost) 1
        else {
            val timeToWaitForOre =
                ceil((blueprint.obsidianRobotCost.oreCost - simulationState.inventory.ore).toDouble() / simulationState.inventory.oreRobots).toInt()
            val timeToWaitForClay =
                ceil((blueprint.obsidianRobotCost.clayCost - simulationState.inventory.clay).toDouble() / simulationState.inventory.clayRobots).toInt()
            max(timeToWaitForOre, timeToWaitForClay) + 1
        }
    val newRemainingTime = simulationState.remainingTime - timeUnitsToSkip
    if (newRemainingTime <= 2 || simulationState.inventory.obsidianRobots >= blueprint.geodeRobotCost.obsidianCost) return null

    return SimulationState(
        inventory = simulationState.inventory.copy(
            ore = simulationState.inventory.ore + (timeUnitsToSkip * simulationState.inventory.oreRobots) - blueprint.obsidianRobotCost.oreCost,
            clay = simulationState.inventory.clay + (timeUnitsToSkip * simulationState.inventory.clayRobots) - blueprint.obsidianRobotCost.clayCost,
            obsidian = simulationState.inventory.obsidian + (timeUnitsToSkip * simulationState.inventory.obsidianRobots),
            obsidianRobots = simulationState.inventory.obsidianRobots + 1
        ),
        remainingTime = newRemainingTime
    )
}

fun buildGeodeRobot(blueprint: Blueprint, simulationState: SimulationState): SimulationState? {
    if (simulationState.inventory.obsidianRobots == 0) return null
    val timeUnitsToSkip =
        if (simulationState.inventory.ore >= blueprint.geodeRobotCost.oreCost && simulationState.inventory.obsidian >= blueprint.geodeRobotCost.obsidianCost) 1
        else {
            val timeToWaitForOre =
                ceil((blueprint.geodeRobotCost.oreCost - simulationState.inventory.ore).toDouble() / simulationState.inventory.oreRobots).toInt()
            val timeToWaitForObsidian =
                ceil((blueprint.geodeRobotCost.obsidianCost - simulationState.inventory.obsidian).toDouble() / simulationState.inventory.obsidianRobots).toInt()
            max(timeToWaitForOre, timeToWaitForObsidian) + 1
        }

    val newRemainingTime = simulationState.remainingTime - timeUnitsToSkip
    if (newRemainingTime <= 0) return null

    return SimulationState(
        inventory = simulationState.inventory.copy(
            ore = simulationState.inventory.ore + (timeUnitsToSkip * simulationState.inventory.oreRobots) - blueprint.geodeRobotCost.oreCost,
            clay = simulationState.inventory.clay + (timeUnitsToSkip * simulationState.inventory.clayRobots),
            obsidian = simulationState.inventory.obsidian + (timeUnitsToSkip * simulationState.inventory.obsidianRobots) - blueprint.geodeRobotCost.obsidianCost,
            totalGeode = simulationState.inventory.totalGeode + newRemainingTime
        ),
        remainingTime = newRemainingTime
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
