package puzzle

import java.io.File

class DayFour : Puzzle {

    val scratchCards = File("src/main/resources/DayFour-Scratchcards.txt")

    override fun solveFirst(): String {

        var sum=0
        for (line in scratchCards.readLines()) {
            val splittedLine = line.split(": ").last().split("\\s*\\|\\s*".toRegex())
            val winningNumbers = splittedLine[0].split("\\s+".toRegex())
            val cardNumbers = splittedLine[1].split("\\s+".toRegex())

            val numberOfWinningNumbers = cardNumbers.filter { s -> s in winningNumbers }.size
            if(numberOfWinningNumbers > 0) { sum += 1 shl (numberOfWinningNumbers - 1) }
        }

        return sum.toString()
    }

    override fun solveSecond(): String {
        val cards = scratchCards.readLines()

        val cardToAmountOfCards = (1 .. cards.size).map { i -> i to 1 }.toMap().toMutableMap()
        for(cardIdx in cards.indices) {

            val splittedLine = cards[cardIdx].split(": ").last().split("\\s*\\|\\s*".toRegex())
            val winningNumbers = splittedLine[0].split("\\s+".toRegex())
            val cardNumbers = splittedLine[1].split("\\s+".toRegex())
            val numberOfWinningNumbers = cardNumbers.filter { s -> s in winningNumbers }.size

            if(numberOfWinningNumbers > 0) {
                for(i in 1..numberOfWinningNumbers) {
                    cardToAmountOfCards[1 + (cardIdx + i)] = cardToAmountOfCards[1 + (cardIdx + i)]!! + cardToAmountOfCards[1 + (cardIdx)]!!
                }
            }

        }

        return cardToAmountOfCards.values.sum().toString()
    }

}
