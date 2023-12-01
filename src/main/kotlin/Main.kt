import puzzle.DayOne


val puzzles = listOf(DayOne())

fun main(args: Array<String>) {
    puzzles.forEach { puzzle ->
        println("Puzzle '${puzzle::class.simpleName}' solution first part: ${puzzle.solveFirst()}")
        println("Puzzle '${puzzle::class.simpleName}' solution second part: ${puzzle.solveSecond()}")
    }
}