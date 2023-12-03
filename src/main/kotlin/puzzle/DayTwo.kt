package puzzle

import java.io.File

class DayTwo : Puzzle {

    val games = File("src/main/resources/DayTwo-Games.txt")

    override fun solveFirst(): String {

        val maxRed = 12
        val maxBlue = 14
        val maxGreen = 13

        val gameList = parseGameList()
        return gameList
            .filter { game -> game.rounds.find { rnd -> rnd.red > maxRed || rnd.blue > maxBlue || rnd.green > maxGreen } == null }
            .map { game -> game.gameNumber }.reduce { acc, i -> acc.plus(i) }.toString()

    }

    override fun solveSecond(): String {
        val gameList = parseGameList()
        return gameList
            .map(::toPowerOfMinPossibleCubes)
            .reduce { acc, i -> acc.plus(i) }.toString()
    }

    private fun DayTwo.parseGameList(): MutableList<Game> {
        val gameList = mutableListOf<Game>()
        for ((index, line) in games.readLines().withIndex()) {
            val game = Game(index + 1)
            gameList.add(game)
            line.split(":").last().split(";")
                .map(::toGameRound)
                .forEach { gameRound -> game.rounds.add(gameRound) }
        }
        return gameList
    }

    private fun toPowerOfMinPossibleCubes(game: Game) : Int {
        var highRed = 0
        var highGreen = 0
        var highBlue = 0
        game.rounds.forEach { gameRound ->
            if(gameRound.red > highRed) highRed = gameRound.red
            if(gameRound.green > highGreen) highGreen = gameRound.green
            if(gameRound.blue > highBlue) highBlue = gameRound.blue
        }
        return highRed * highGreen * highBlue
    }

    private fun toGameRound(string: String): GameRound {
        var red = 0
        var green = 0
        var blue = 0

        val colors = string.trim().split(", ")
        for (color in colors) {
            val amountAndColor = color.split(" ")
            when (amountAndColor.last()) {
                "red" -> red = amountAndColor.first().toInt()
                "green" -> green = amountAndColor.first().toInt()
                "blue" -> blue = amountAndColor.first().toInt()
            }
        }

        return GameRound(red, green, blue)
    }

    private class Game(val gameNumber: Int) {
        var rounds: MutableList<GameRound> = mutableListOf()
    }

    private data class GameRound(
        val red: Int,
        val green: Int,
        val blue: Int
    )
}