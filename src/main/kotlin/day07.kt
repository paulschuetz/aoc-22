fun main() {
    val demoInput = readInput("day07-demo")
    assertEquals(solveDay07Part1(demoInput), 95437)

    val input = readInput("day07")
    benchmark { solveDay07Part1(input).also { println("Solution part one: $it") } }

    assertEquals(solveDay07Part2(demoInput), 24933642)
    benchmark { solveDay07Part2(input).also { println("Solution part two: $it") } }
}

private fun solveDay07Part1(input: String): Int {
    val commands = input.split("$").drop(2).map { it.trim() }

    val rootFolder = Folder("/")
    commands.fold(rootFolder) { folder, command -> applyCommand(folder, command) }

    val folders = rootFolder.allSubfolders() + rootFolder
    return folders.map { it.size() }.filter { it <= 100000 }.sum()
}

private fun solveDay07Part2(input: String): Int {
    val commands = input.split("$").drop(2).map { it.trim() }

    val rootFolder = Folder("/")
    commands.fold(rootFolder) { folder, command -> applyCommand(folder, command) }

    val folders = rootFolder.allSubfolders() + rootFolder

    val totalDiskSpace = 70000000
    val needed = 30000000
    val obtained = rootFolder.size()
    val free = totalDiskSpace - obtained
    val minSpaceToBeFreed = needed - free

    return folders.map { it.size() }.filter { it >= minSpaceToBeFreed }.min()
}

class Folder(
    val name: String,
    val parent: Folder? = null,
    val files: MutableSet<File> = mutableSetOf(),
    val folders: MutableSet<Folder> = mutableSetOf()
)

class File(val name: String, val size: Int)

fun applyCommand(folder: Folder, command: String): Folder {
    return when {
        command.startsWith("cd") -> changeDir(folder, command)
        command.startsWith("ls") -> listContents(folder, command)
        else -> error("illegal command")
    }
}

fun changeDir(folder: Folder, cdCommand: String): Folder {
    return when (val destination = cdCommand.split(" ")[1].trim()) {
        ".." -> folder.parent!!
        "/" -> folder.root()
        else -> {
            // if folder does not exist on this level, create it
            if (folder.folders.none { it.name == destination }) {
                val newSubFolder = Folder(name = destination, parent = folder)
                folder.folders.add(newSubFolder)
                return newSubFolder
            } else folder.folders.single { it.name == destination }
        }
    }
}

fun listContents(folder: Folder, lsCommand: String): Folder {
    lsCommand.split("\n").drop(1).forEach { line ->
        when {
            line.first().isDigit() -> folder.files.add(parseFile(root = folder, line))
            else -> folder.folders.add(Folder(name = line.split(" ")[1], parent = folder))
        }
    }
    return folder
}

fun parseFile(root: Folder, line: String): File {
    val (size, name) = line.split(" ")
    return File(name = name, size = size.toInt())
}

fun Folder.root(): Folder {
    return parent?.root() ?: this
}

fun Folder.size(): Int {
    return this.files.sumOf { it.size } + this.folders.sumOf { it.size() }
}

fun Folder.allSubfolders(): Set<Folder> {
    return if (this.folders.size == 0) setOf(this)
    else return setOf(this) + this.folders.flatMap { it.allSubfolders() }.toSet()
}


