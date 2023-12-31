import puzzle.*
import kotlin.system.measureTimeMillis

val puzzles =
    listOf(
        DayOne(),
        DayTwo(),
        DayThree(),
        DayFour(),
        DayFive(),
        DaySix(),
        DaySeven(),
        DayEight(),
        DayNine(),
        DayTen(),
        DayEleven(),
        DayTwelve()
    )

fun main(args: Array<String>) {
    if (args.isEmpty()) {
        puzzles.forEach(::runPuzzle)
    } else {
        val puzzleNo = args.get(0).toIntOrNull()
        if (puzzleNo != null) {
            runPuzzle(puzzles[puzzleNo - 1])
        }
    }
}


fun runPuzzle(puzzle: Puzzle) {
    var time = measureTimeMillis {
        println("Puzzle '${puzzle::class.simpleName}' solution first part: ${puzzle.solveFirst()}")
    }
    println("\tFirst part completed in ${time / 1000.0} seconds")

    time = measureTimeMillis {
        println("Puzzle '${puzzle::class.simpleName}' solution second part: ${puzzle.solveSecond()}")
    }
    println("\tSecond part completed in ${time / 1000.0} seconds")
}