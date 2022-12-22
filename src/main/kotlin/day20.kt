package day20

import assertEquals
import benchmark
import readInputLines
import repeat
import java.math.BigInteger
import java.util.*

fun main() {
    val day = "20"

    val demoInputLines = readInputLines("day$day-demo")
    assertEquals(solvePart1(demoInputLines), 3)

    val inputLines = readInputLines("day$day")
    benchmark { solvePart1(inputLines).also { println("Solution part one: $it") } }

    assertEquals(solvePart2(demoInputLines), BigInteger("1623178306"))
    benchmark { solvePart2(inputLines).also { println("Solution part two: $it") } }
}

data class EncryptedNumber(
    val initialIndex: Int,
    val value: Int,
)

data class EncryptedNumberBig(
    val initialIndex: Int,
    val value: BigInteger,
)

private fun solvePart1(input: List<String>): Int {

    val encryptedNums = input.mapIndexed { idx, num -> EncryptedNumber(initialIndex = idx, value = num.toInt()) }
    val decryptedNums = LinkedList(encryptedNums.toMutableList())

    for (encryptedNum in encryptedNums) {
        val currentIndex: Int = decryptedNums.indexOfFirst { it.initialIndex == encryptedNum.initialIndex }
        // calculate new index
        val index = (currentIndex + (encryptedNum.value % (encryptedNums.size - 1))) % (encryptedNums.size - 1)
        val finalIndex = if (index > 0) index else encryptedNums.size - 1 + index
        // remove and insert at new loc
        decryptedNums.removeAt(currentIndex)
        decryptedNums.add(finalIndex, encryptedNum)
    }

    val decryptedSeq = decryptedNums.map { it.value }.asSequence().repeat()
    val zeroIndex = decryptedSeq.indexOfFirst { it == 0 }

    return decryptedSeq.elementAt(zeroIndex + 1000) + decryptedSeq.elementAt(zeroIndex + 2000) + decryptedSeq.elementAt(
        zeroIndex + 3000
    )
}

private fun solvePart2(input: List<String>): BigInteger {
    val encryptedNums = input.mapIndexed { idx, num ->
        EncryptedNumberBig(
            initialIndex = idx,
            value = BigInteger.valueOf(num.toLong()).times(BigInteger("811589153"))
        )
    }
    val decryptedNums = LinkedList(encryptedNums.toMutableList())

    repeat(10) {
        for (encryptedNum in encryptedNums) {
            val currentIndex: Int = decryptedNums.indexOfFirst { it.initialIndex == encryptedNum.initialIndex }
            // calculate new index
            val index =
                (currentIndex + (encryptedNum.value.mod(BigInteger.valueOf((encryptedNums.size - 1).toLong()))).toInt()) % (encryptedNums.size - 1)
            val finalIndex = if (index > 0) index else encryptedNums.size - 1 + index
            // remove and insert at new loc
            decryptedNums.removeAt(currentIndex)
            decryptedNums.add(finalIndex, encryptedNum)
        }
    }

    val decryptedSeq = decryptedNums.map { it.value }.asSequence().repeat()
    val zeroIndex = decryptedSeq.indexOfFirst { it == BigInteger.ZERO }

    return decryptedSeq.elementAt(zeroIndex + 1000).add(decryptedSeq.elementAt(zeroIndex + 2000))
        .add(decryptedSeq.elementAt(zeroIndex + 3000))
}
