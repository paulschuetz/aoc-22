package day17

import assertEquals
import benchmark
import readInputLines

fun main() {
    val day = "17"

    val demoInputLines = readInputLines("day$day-demo")
    assertEquals(solvePart1(demoInputLines), 3068)

    val inputLines = readInputLines("day$day")
    benchmark { solvePart1(inputLines).also { println("Solution part one: $it") } }

     assertEquals(solvePart2(demoInputLines), demoInputLines.size)
     benchmark { solvePart2(inputLines).also { println("Solution part two: $it") } }
}

data class Point(val x: Int, val y: Int)

sealed class Shape(val shapeForm: Set<Point>) {

    object HorizontalLine : Shape(shapeForm = setOf(Point(0, 0), Point(1, 0), Point(2, 0), Point(3, 0)))
    object PlusSign : Shape(shapeForm = setOf(Point(1, 0), Point(1, 1), Point(0, 1), Point(2, 1), Point(1, 2)))
    object MirroredL : Shape(shapeForm = setOf(Point(0, 0), Point(1, 0), Point(2, 0), Point(2, 1), Point(2, 2)))
    object VerticalLine : Shape(shapeForm = setOf(Point(0, 0), Point(0, 1), Point(0, 2), Point(0, 3)))
    object SquareBlock : Shape(shapeForm = setOf(Point(0, 0), Point(0, 1), Point(1, 0), Point(1, 1)))

}

private fun solvePart1(input: List<String>): Int {

    val shapes = newShapeSequence().iterator()
    val pushDirection = newGasPushSequence(input.first()).iterator()
    val settledShapes: MutableMap<Point, Boolean> = mutableMapOf()
    var maxY = 0
    // we have 2022 rocks
    repeat(2022) {
        // spawn new block with bottom left of shape: y = topOfRocks + 4 x = 2 and add to shape
        val shape = shapes.next()
        var absoluteShapePos = shape.shapeForm.map { it + Point(x = 2, y = maxY + 4) }

        while(true){
            val curPushDirection = pushDirection.next()
            val pushedShape = absoluteShapePos.map { it + Point(x = if(curPushDirection == PushDirection.LEFT) -1 else 1, y = 0) }
            if(pushedShape.none { settledShapes[it] == true } && pushedShape.none { it.x > 6 || it.x < 0 }){
                // if we have no collision with other shapes or borders apply the push
                absoluteShapePos = pushedShape
            }

            val droppedShape = absoluteShapePos.map { it + Point(x = 0, y = -1) }
            if(droppedShape.none { settledShapes[it] == true  } && droppedShape.none { it.y == 0 }){
                absoluteShapePos = droppedShape
            } else {
                // else shape is settled
                absoluteShapePos.forEach { settledShapes[it] = true }
                if(absoluteShapePos.maxOf { it.y } > maxY){
                    maxY = absoluteShapePos.maxOf { it.y }
                }
                break
            }
        }
    }
    return maxY
}

private fun solvePart2(input: List<String>): Int {
    // IDEA: Only hold reference to the Kruste
    return input.size
}

enum class PushDirection {
    LEFT, RIGHT
}

operator fun Point.plus(other: Point) = Point(x = this.x + other.x, y = this.y + other.y)

val newGasPushSequence =
    { input: String -> input.map { if (it == '>') PushDirection.RIGHT else PushDirection.LEFT }.asSequence().repeat() }

fun <T> Sequence<T>.repeat() = sequence { while (true) yieldAll(this@repeat) }

val newShapeSequence = {
    generateSequence(Shape.HorizontalLine as Shape) { lastShape ->
        when (lastShape) {
            is Shape.HorizontalLine -> Shape.PlusSign
            is Shape.PlusSign -> Shape.MirroredL
            is Shape.MirroredL -> Shape.VerticalLine
            is Shape.VerticalLine -> Shape.SquareBlock
            is Shape.SquareBlock -> Shape.HorizontalLine
        }
    }
}
