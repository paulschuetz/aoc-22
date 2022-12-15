package day15

import assertEquals
import benchmark
import readInputLines
import toward
import java.math.BigInteger
import kotlin.math.abs

fun main() {
    val day = "15"

    val demoInputLines = readInputLines("day$day-demo")
    assertEquals(solvePart1(demoInputLines, 10), 26)

    val inputLines = readInputLines("day$day")
    benchmark { solvePart1(inputLines, 2_000_000).also { println("Solution part one: $it") } }

    println(solvePart2(demoInputLines))
    benchmark {
        solvePart2(inputLines).also {
            val solution = BigInteger.valueOf(it.x.toLong()) * BigInteger.valueOf(4_000_000L) + BigInteger.valueOf(it.y.toLong())
            println(solution)
        }
    }
}

data class Point(val x: Int, val y: Int)
data class SignalingSensor(val sensorPos: Point, val beaconPos: Point)

private fun solvePart1(input: List<String>, lineY: Int): Int {

    // parse input sensor | beacon
    val signalingSensors = input.map { parseSignalingSensor(it) }
    // for each pair -> Set<Points> on horizontal line where no beacon can exist
    return signalingSensors.flatMap { getPointsWhereNoBeaconCanBe(it, lineY) }.toSet().size
}

private fun solvePart2(input: List<String>): Point {
    val signalingSensors = input.map { parseSignalingSensor(it) }

    // for each signaling sensor we check each point around the edges of the area of influence, because somewhere there the isolated beacon must be
    for (signalingSensor in signalingSensors) {
        val distance = signalingSensor.sensorPos manhattanDistTo signalingSensor.beaconPos

        var yDiff = 0
        for (x in signalingSensor.sensorPos.x - distance..signalingSensor.sensorPos.x + distance) {
            // for each point on each edge check if it is not in the area of influence of ALL signaling sensors -> that's the one <3
            if (x < signalingSensor.sensorPos.x) {
                // look left
                if (isIsolated(Point(x - 1, signalingSensor.sensorPos.y - yDiff), signalingSensors)) return Point(
                    x - 1,
                    signalingSensor.sensorPos.y - yDiff
                )
                if (isIsolated(Point(x - 1, signalingSensor.sensorPos.y + yDiff), signalingSensors)) return Point(
                    x - 1,
                    signalingSensor.sensorPos.y + yDiff
                )
            } else if (x > signalingSensor.sensorPos.x) {
                // look right
                if (isIsolated(Point(x + 1, signalingSensor.sensorPos.y - yDiff), signalingSensors)) return Point(
                    x + 1,
                    signalingSensor.sensorPos.y - yDiff
                )
                if (isIsolated(Point(x + 1, signalingSensor.sensorPos.y + yDiff), signalingSensors)) return Point(
                    x + 1,
                    signalingSensor.sensorPos.y + yDiff
                )
            } else {
                // look top right left
                if (isIsolated(Point(x + 1, signalingSensor.sensorPos.y + yDiff), signalingSensors)) return Point(
                    x + 1,
                    signalingSensor.sensorPos.y + yDiff
                )
                if (isIsolated(Point(x - 1, signalingSensor.sensorPos.y + yDiff), signalingSensors)) return Point(
                    x - 1,
                    signalingSensor.sensorPos.y + yDiff
                )
                if (isIsolated(Point(x, signalingSensor.sensorPos.y + yDiff + 1), signalingSensors)) return Point(
                    x,
                    signalingSensor.sensorPos.y + yDiff + 1
                )
                // look down right left
                if (isIsolated(Point(x + 1, signalingSensor.sensorPos.y - yDiff), signalingSensors)) return Point(
                    x + 1,
                    signalingSensor.sensorPos.y - yDiff
                )
                if (isIsolated(Point(x - 1, signalingSensor.sensorPos.y - yDiff), signalingSensors)) return Point(
                    x - 1,
                    signalingSensor.sensorPos.y - yDiff
                )
                if (isIsolated(Point(x, signalingSensor.sensorPos.y - yDiff - 1), signalingSensors)) return Point(
                    x,
                    signalingSensor.sensorPos.y - yDiff + 1
                )
            }
            if (yDiff < distance) yDiff++ else yDiff--
        }
    }

    error("bad input")
}

private fun isIsolated(point: Point, signalingSensors: List<SignalingSensor>): Boolean {
    return if (point.x < 0 || point.x > 4_000_000 || point.y < 0 || point.y > 4_000_000) return false
    else signalingSensors.all { !isWithinAreaOfInfluence(point, it) }
}

private fun isWithinAreaOfInfluence(point: Point, signalingSensor: SignalingSensor): Boolean =
    point manhattanDistTo signalingSensor.sensorPos <= signalingSensor.sensorPos manhattanDistTo signalingSensor.beaconPos


private fun parseSignalingSensor(line: String): SignalingSensor {
    val regex = """Sensor at x=(-?\d+), y=(-?\d+): closest beacon is at x=(-?\d+), y=(-?\d+)""".toRegex()
    val match = regex.matchEntire(line)
    val groups = match!!.groups
    return SignalingSensor(
        sensorPos = Point(x = groups[1]!!.value.toInt(), y = groups[2]!!.value.toInt()),
        beaconPos = Point(x = groups[3]!!.value.toInt(), y = groups[4]!!.value.toInt())
    )
}

private fun getPointsWhereNoBeaconCanBe(signalingSensor: SignalingSensor, lineY: Int): Set<Int> {
    val distance = signalingSensor.sensorPos manhattanDistTo signalingSensor.beaconPos

    if (lineY !in signalingSensor.sensorPos.y toward signalingSensor.sensorPos.y + distance && lineY !in signalingSensor.sensorPos.y toward signalingSensor.sensorPos.y - distance) return emptySet()

    val xRadius = distance - abs(signalingSensor.sensorPos.y - lineY)
    val xWhereNoBeaconCanBe = (signalingSensor.sensorPos.x - xRadius..signalingSensor.sensorPos.x + xRadius).toSet()

    return if (signalingSensor.beaconPos.y == lineY) xWhereNoBeaconCanBe - signalingSensor.beaconPos.x else xWhereNoBeaconCanBe
}

infix fun Point.manhattanDistTo(other: Point): Int = abs(this.x - other.x) + abs(this.y - other.y)
