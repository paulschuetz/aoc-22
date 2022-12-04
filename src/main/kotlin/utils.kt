import java.io.File
import java.lang.RuntimeException
import java.util.*
import kotlin.system.measureTimeMillis

val benchmark = { block: () -> Unit ->
    measureTimeMillis(block).also { println("Took $it millis to compute.") }
}

fun readInputLines(filename: String) = File("src/main/resources/$filename").readLines()

val readInput = { filename: String -> File("src/main/resources/$filename").readText() }

fun <T: Any?> assertEquals(value: T, expected: T){
    if (!Objects.equals(value, expected)){
        throw RuntimeException("Expected value $expected but got $value")
    }
}
