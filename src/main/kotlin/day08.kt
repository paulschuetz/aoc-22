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

    val visibleInnerTreesCount =
        trees.filterNot { it.key.x == 0 || it.key.y == 0 || it.key.x == input.size - 1 || it.key.y == input.size - 1 }
            .count { Tree(coordinate = it.key, height = it.value).isVisible(trees) }

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

data class Coordinate(val x: Int, val y: Int)

data class Tree(val coordinate: Coordinate, val height: Int)

private fun Tree.isVisible(trees: Map<Coordinate, Int>) =
    trees.filter { it.key.x == this.coordinate.x && it.key.y < this.coordinate.y }.all { it.value < this.height } ||
            trees.filter { it.key.x == this.coordinate.x && it.key.y > this.coordinate.y }.all { it.value < this.height } ||
            trees.filter { it.key.y == this.coordinate.y && it.key.x < this.coordinate.x }.all { it.value < this.height } ||
            trees.filter { it.key.y == this.coordinate.y && it.key.x > this.coordinate.x }.all { it.value < this.height }

private fun Tree.calcScenicScore(trees: Map<Coordinate, Int>, inputWidth: Int): Int {

    val sideScores = mutableListOf<Int>()

    // left
    var currentSideScenicScore = 0
    for (x in this.coordinate.x - 1 downTo 0) {
        if (trees[Coordinate(x = x, y = this.coordinate.y)]!! < this.height) currentSideScenicScore++
        if (trees[Coordinate(x = x, y = this.coordinate.y)]!! >= this.height) {
            currentSideScenicScore++
            break
        }
    }
    sideScores.add(currentSideScenicScore)
    currentSideScenicScore = 0

    // right
    for (x in this.coordinate.x + 1..inputWidth) {
        if (trees[Coordinate(x = x, y = this.coordinate.y)]!! < this.height) currentSideScenicScore++
        if (trees[Coordinate(x = x, y = this.coordinate.y)]!! >= this.height) {
            currentSideScenicScore++
            break
        }
    }
    sideScores.add(currentSideScenicScore)
    currentSideScenicScore = 0

    // up
    for (y in this.coordinate.y - 1 downTo 0) {
        if (trees[Coordinate(x = this.coordinate.x, y = y)]!! < this.height) currentSideScenicScore++
        if (trees[Coordinate(x = this.coordinate.x, y = y)]!! >= this.height) {
            currentSideScenicScore++
            break
        }
    }
    sideScores.add(currentSideScenicScore)
    currentSideScenicScore = 0

    // down
    for (y in this.coordinate.y + 1..inputWidth) {
        if (trees[Coordinate(x = this.coordinate.x, y = y)]!! < this.height) currentSideScenicScore++
        if (trees[Coordinate(x = this.coordinate.x, y = y)]!! >= this.height) {
            currentSideScenicScore++
            break
        }
    }
    sideScores.add(currentSideScenicScore)

    // calc final scenic score for tree
    return sideScores.reduce { a, b -> a * b }
}