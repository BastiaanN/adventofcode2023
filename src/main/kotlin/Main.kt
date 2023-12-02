import puzzle.DayOne
import puzzle.DayTwo
import puzzle.Puzzle


val puzzles = listOf(DayOne(), DayTwo())

fun main(args: Array<String>) {
    if(args.isEmpty()) {
        puzzles.forEach(::runPuzzle)
    } else {
        val puzzleNo = args.get(0).toIntOrNull()
        if(puzzleNo != null) {
            runPuzzle(puzzles[puzzleNo-1])
        }
    }
}

fun runPuzzle(puzzle: Puzzle) {
    println("Puzzle '${puzzle::class.simpleName}' solution first part: ${puzzle.solveFirst()}")
    println("Puzzle '${puzzle::class.simpleName}' solution second part: ${puzzle.solveSecond()}")
}