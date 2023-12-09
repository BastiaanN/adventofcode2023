package puzzle

import java.io.File

class DayNine : Puzzle {

    val history = File("src/main/resources/DayNine-OasisSensor.txt")

    override fun solveFirst(): String {
        var sum = 0
        for (line in history.readLines()) {
            val sensorData = line.split(' ')
            val differenceTree = buildDifferenceTree(mutableListOf((sensorData.map { s -> s.toInt() })))
            sum += calculateNextValueFromDifferences(differenceTree)
        }
        return sum.toString()
    }

    override fun solveSecond(): String {
        var sum = 0
        for (line in history.readLines()) {
            val sensorData = line.split(' ')
            val differenceTree = buildDifferenceTree(mutableListOf((sensorData.map { s -> s.toInt() })))
            sum += calculatePreviousValueFromDifferences(differenceTree)
        }
        return sum.toString()
    }

    private fun buildDifferenceTree(differencesList: MutableList<List<Int>>) : MutableList<List<Int>> {
        // The list to work on should be the last list in the sequence.
        val lastList = differencesList.last()

        // Calculate new differences for last item in the list
        val newCalculatedDifferences = mutableListOf<Int>()
        for(i in 1..<lastList.size) {
            newCalculatedDifferences.add(lastList[i] - lastList[i -1])
        }
        differencesList.add(newCalculatedDifferences)

        // All zeros, we are done!
        if(newCalculatedDifferences.count { i -> i == 0 } == newCalculatedDifferences.size) {
            return differencesList
        } else {
            return buildDifferenceTree(differencesList)
        }
    }

    private fun calculateNextValueFromDifferences(differencesList: MutableList<List<Int>>, valueToAdd: Int = 0) : Int {
        // Add the value passed in
        val calculatedNextValue = differencesList.last().last() + valueToAdd
        if(differencesList.size == 1) {
            return calculatedNextValue
        } else {
            return calculateNextValueFromDifferences(differencesList.subList(0, differencesList.size - 1), calculatedNextValue)
        }
    }

    private fun calculatePreviousValueFromDifferences(differencesList: MutableList<List<Int>>, valueToSubstract: Int = 0) : Int {
        // Subtract the value passed in
        val calculatedPreviousValue = differencesList.last().first() - valueToSubstract
        if(differencesList.size == 1) {
            return calculatedPreviousValue
        } else {
            return calculatePreviousValueFromDifferences(differencesList.subList(0, differencesList.size - 1), calculatedPreviousValue)
        }
    }

}
