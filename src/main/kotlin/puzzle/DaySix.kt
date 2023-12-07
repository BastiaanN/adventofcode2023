package puzzle

import java.io.File

class DaySix : Puzzle {

    val races = File("src/main/resources/DaySix-Races.txt")

    override fun solveFirst(): String {
        val raceFileLines = races.readLines()
        val times = raceFileLines[0].split("\\s+".toRegex())
        val distances = raceFileLines[1].split("\\s+".toRegex())

        var listOfWinningOptions = mutableListOf<Long>()
        for(idx in 1..<times.size) {
            listOfWinningOptions.add(getAmountOfBetterOutcomes(times[idx].toLong(), distances[idx].toLong()))
        }

        return listOfWinningOptions.reduce { acc, i -> acc.times(i) }.toString()

    }

    override fun solveSecond(): String {
        val raceFileLines = races.readLines()
        val time = raceFileLines[0].replace("\\s+".toRegex(), "").split(":").last()
        val distance = raceFileLines[1].replace("\\s+".toRegex(), "").split(":").last()
        
        return getAmountOfBetterOutcomes(time.toLong(), distance.toLong()).toString()
    }

    private fun getAmountOfBetterOutcomes(raceTime: Long, recordDistance: Long): Long {
        var amountOfBetterOutcomes = 0L
        for (i in raceTime.downTo(0)) {
            val speed = raceTime - i
            val distanceTravelled = i * speed
            if(distanceTravelled > recordDistance) {
                amountOfBetterOutcomes += 1
            }
        }
        return amountOfBetterOutcomes
    }
}
