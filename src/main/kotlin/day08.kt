fun main() {
    val demoInputLines = readInputLines("day08-demo")
    assertEquals(solveDay08Part1(demoInputLines), 21)

    val inputLines = readInputLines("day08")
    benchmark { solveDay08Part1(inputLines).also { println("Solution part one: $it") } }

    assertEquals(solveDay08Part2(demoInputLines), 8)
    benchmark { solveDay08Part2(inputLines).also { println("Solution part two: $it") } }
}

private fun solveDay08Part1(input: List<String>): Int {
    val trees = input.flatMapIndexed { y, line -> line.mapIndexed { x, char -> Coordinate(x = x, y = y) to char.digitToInt() } }.toMap()

    val width = input.size - 1

    val visibleInnerTreesCount =
        trees.filterNot { it.key.x == 0 || it.key.y == 0 || it.key.x == input.size - 1 || it.key.y == input.size - 1 }
            .count { Tree(coordinate = it.key, height = it.value).isVisible(trees, width) }

    val visibleOuterTreesCount = (input.size - 1) * 4

    return visibleInnerTreesCount + visibleOuterTreesCount
}

private fun solveDay08Part2(input: List<String>): Int {
    val trees = input.flatMapIndexed { y, line -> line.mapIndexed { x, char -> Coordinate(x = x, y = y) to char.digitToInt() } }.toMap()

    val optimalTree = trees.map {
        it.key to Tree(coordinate = it.key, height = it.value).calcScenicScore(
            trees = trees,
            inputWidth = input.size - 1
        )
    }.maxBy { it.second }

    return optimalTree.second
}

data class Coordinate(val x: Int, val y: Int){
    operator fun plus(other: Coordinate) = Coordinate(other.x + x, other.y + y)
}

data class Tree(val coordinate: Coordinate, val height: Int)

private fun Tree.isVisible(trees: Map<Coordinate, Int>, width: Int) = (this.coordinate.y - 1 downTo 0).map { trees[Coordinate(x = this.coordinate.x, y = it)]!! }.all { it < this.height } ||
            (this.coordinate.y + 1..width).map { trees[Coordinate(x = this.coordinate.x, y = it)]!! }.all { it < this.height } ||
            (this.coordinate.x - 1 downTo 0).map { trees[Coordinate(x = it, y = this.coordinate.y)]!! }.all { it < this.height } ||
            (this.coordinate.x + 1..width).map { trees[Coordinate(x = it, y = this.coordinate.y)]!! }.all { it < this.height }

fun Tree.calcScenicScore(trees: Map<Coordinate, Int>, inputWidth: Int): Int {

    var leftScore = 0
    for (x in this.coordinate.x - 1 downTo 0) {
        if (trees[Coordinate(x = x, y = this.coordinate.y)]!! < this.height) leftScore++
        if (trees[Coordinate(x = x, y = this.coordinate.y)]!! >= this.height) {
            leftScore++
            break
        }
    }

    var rightScore = 0
    for (x in this.coordinate.x + 1..inputWidth) {
        if (trees[Coordinate(x = x, y = this.coordinate.y)]!! < this.height) rightScore++
        if (trees[Coordinate(x = x, y = this.coordinate.y)]!! >= this.height) {
            rightScore++
            break
        }
    }

    var topScore = 0
    for (y in this.coordinate.y - 1 downTo 0) {
        if (trees[Coordinate(x = this.coordinate.x, y = y)]!! < this.height) topScore++
        if (trees[Coordinate(x = this.coordinate.x, y = y)]!! >= this.height) {
            topScore++
            break
        }
    }

    var downScore = 0
    for (y in this.coordinate.y + 1..inputWidth) {
        if (trees[Coordinate(x = this.coordinate.x, y = y)]!! < this.height) downScore++
        if (trees[Coordinate(x = this.coordinate.x, y = y)]!! >= this.height) {
            downScore++
            break
        }
    }

    return topScore * rightScore * downScore * leftScore
}