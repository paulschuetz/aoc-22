package day21

import assertEquals
import benchmark
import readInputLines
import java.math.BigDecimal
import java.math.BigInteger

fun main() {
    val day = "21"

    val demoInputLines = readInputLines("day$day-demo")
    assertEquals(solvePart1(demoInputLines), BigInteger("152"))

    val inputLines = readInputLines("day$day")
    benchmark { solvePart1(inputLines).also { println("Solution part one: $it") } }

    assertEquals(solvePart2(demoInputLines), BigInteger("301"))
    benchmark { solvePart2(inputLines).also { println("Solution part two: $it") } }
}

sealed interface Operation {
    fun getResult(): BigDecimal

    class AddOp(val x: Operation, val y: Operation) : Operation {
        override fun getResult() = x.getResult().add(y.getResult())
    }

    class MultiplyOp(val x: Operation, val y: Operation) : Operation {
        override fun getResult() = x.getResult().multiply(y.getResult())
    }

    class SubtractOp(val x: Operation, val y: Operation) : Operation {
        override fun getResult() = x.getResult().subtract(y.getResult())
    }

    class DivideOp(val x: Operation, val y: Operation) : Operation {
        override fun getResult() = x.getResult().divide(y.getResult())
    }

    class NoOp(val value: BigDecimal) : Operation {
        override fun getResult() = value
    }
}

private fun solvePart1(input: List<String>): BigInteger {

    val operations = input.associate { line ->
        line.split(":").let { it[0].trim() to it[1].trim() }
    }

    val rootOp = parseOperation(rawOperation = operations["root"]!!,  operations)
    return rootOp.getResult().toBigInteger()
}

private fun parseOperation(rawOperation: String, operations: Map<String, String>): Operation {
    return if (' ' !in rawOperation) Operation.NoOp(BigDecimal(rawOperation))
    else {
        val (first, second) = rawOperation.split("+", "-", "*", "/")
            .map { it.trim() }
            .map { parseOperation(rawOperation = operations[it]!!, operations) }

        when (rawOperation.split(" ")[1].first()) {
            '+' -> Operation.AddOp(first, second)
            '-' -> Operation.SubtractOp(first, second)
            '*' -> Operation.MultiplyOp(first, second)
            '/' -> Operation.DivideOp(first, second)
            else -> error("bruh")
        }
    }
}

private fun parseOperationReversed(
    rawOperation: String,
    resultName: String,
    solveForOperand: String,
    operations: Map<String, String>
): Operation {
    val (first, second) = rawOperation.split("+", "-", "*", "/")
        .map { it.trim() }

    val parentOp = operations.filter { it.value.contains(resultName) }.firstNotNullOfOrNull { it }
        ?.let {
            parseOperationReversed(
                rawOperation = it.value,
                resultName = it.key,
                solveForOperand = resultName,
                operations
            )
        }

    return if (parentOp == null) {
        // if we reached root get value of other tree and return Noop Operation with that value
        if (operations[resultName]!!.indexOf(first) < operations[resultName]!!.indexOf(first)) {
            parseOperation(rawOperation = operations[first]!!, operations)
        } else {
            parseOperation(rawOperation = operations[second]!!, operations)
        }.let { Operation.NoOp(value = it.getResult()) }
    } else {
        if (first == solveForOperand) {
            val secondOp =
                parseOperation(rawOperation = operations[second]!!, operations = operations)
            when (rawOperation.split(" ")[1].first()) {
                '+' -> Operation.SubtractOp(parentOp, secondOp)
                '-' -> Operation.AddOp(parentOp, secondOp)
                '*' -> Operation.DivideOp(parentOp, secondOp)
                '/' -> Operation.MultiplyOp(parentOp, secondOp)
                else -> error("bruh")
            }
        } else {
            val firstOp =
                parseOperation(rawOperation = operations[first]!!, operations = operations)
            when (rawOperation.split(" ")[1].first()) {
                '+' -> Operation.SubtractOp(parentOp, firstOp)
                '-' -> Operation.SubtractOp(firstOp, parentOp)
                '*' -> Operation.DivideOp(parentOp, firstOp)
                '/' -> Operation.MultiplyOp(firstOp, parentOp)
                else -> error("bruh")
            }
        }
    }
}

private fun solvePart2(input: List<String>): BigInteger {

    val operations = input.associate { line ->
        line.split(":").let { it[0].trim() to it[1].trim() }
    }

    val parentOfHuman = operations.filter { "humn" in it.value }.firstNotNullOf { it }
    val rootOp = parseOperationReversed(
        rawOperation = operations[parentOfHuman.key]!!,
        resultName = parentOfHuman.key,
        solveForOperand = "humn",
        operations
    )
    return rootOp.getResult().toBigInteger()
}
