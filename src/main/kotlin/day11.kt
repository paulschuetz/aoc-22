package day11

import benchmark
import java.math.BigInteger

fun main() {
    val m = listOf(2, 7, 11, 19, 3, 5, 17, 13).reduce { product, num -> product * num }

    benchmark { solvePart1(monkeys(m)).also { println("Solution part one: $it") } }
    benchmark { solvePart2(monkeys(m)).also { println("Solution part two: $it") } }
}

val monkeys = { m: Int ->
    listOf(
        Monkey(
            id = 0,
            items = mutableListOf(66, 59, 64, 51),
            worryLevelCalc = { (it * 3) % m },
            throwTest = { if (it % 2 == 0) 1 else 4 }
        ),
        Monkey(
            id = 1,
            items = mutableListOf(67, 61),
            worryLevelCalc = { (it * 19) % m },
            throwTest = { if (it % 7 == 0) 3 else 5 }
        ),
        Monkey(
            id = 2,
            items = mutableListOf(86, 93, 80, 70, 71, 81, 56),
            worryLevelCalc = { (it + 2) % m },
            throwTest = { if (it % 11 == 0) 4 else 0 }
        ),
        Monkey(
            id = 3,
            items = mutableListOf(94),
            worryLevelCalc = { (BigInteger.valueOf(it.toLong()).pow(2) % BigInteger.valueOf(m.toLong())).toInt() },
            throwTest = { if (it % 19 == 0) 7 else 6 }
        ),
        Monkey(
            id = 4,
            items = mutableListOf(71, 92, 64),
            worryLevelCalc = { (it + 8) % m },
            throwTest = { if (it % 3 == 0) 5 else 1 }
        ),
        Monkey(
            id = 5,
            items = mutableListOf(58, 81, 92, 75, 56),
            worryLevelCalc = { (it + 6) % m },
            throwTest = { if (it % 5 == 0) 3 else 6 }
        ),
        Monkey(
            id = 6,
            items = mutableListOf(82, 98, 77, 94, 86, 81),
            worryLevelCalc = { (it + 7) % m },
            throwTest = { if (it % 17 == 0) 7 else 2 }
        ),
        Monkey(
            id = 7,
            items = mutableListOf(54, 95, 70, 93, 88, 93, 63, 50),
            worryLevelCalc = { (it + 4) % m },
            throwTest = { if (it % 13 == 0) 2 else 0 }
        )
    )
}

private fun solvePart1(monkeys: List<Monkey>): Int {
    repeat(20) { monkeys.onEach { monkey -> monkey.playRound(monkeys, relief = true) } }
    return monkeys.sortedByDescending { it.inspections }.take(2).map { it.inspections }.reduce { m1, m2 -> m1 * m2 }
}


private fun solvePart2(monkeys: List<Monkey>): Int {
    repeat(10000) { round ->
        monkeys.onEach { monkey -> monkey.playRound(monkeys, relief = false) }
    }

    return monkeys.sortedByDescending { it.inspections }.take(2).map { it.inspections }
        .reduce { m1, m2 -> m1 * m2 }
}

private fun Monkey.playRound(monkeys: List<Monkey>, relief: Boolean) {
    for (item in items) {
        this.inspections += 1
        val newWorryLevel = this.worryLevelCalc(item).let { if (relief) it.floorDiv(3) else it }

        val itemDestination = this.throwTest(newWorryLevel)
        monkeys[itemDestination].items.add(newWorryLevel)
    }
    // monkey will have no items after round
    this.items.clear()
}

data class Monkey(
    val id: Int,
    val items: MutableList<Int>,
    val worryLevelCalc: (oldWl: Int) -> Int,
    val throwTest: (worryLevel: Int) -> Int,
    var inspections: Int = 0
)
