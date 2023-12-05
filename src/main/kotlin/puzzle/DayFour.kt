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
        return checkCards(scratchCards.readLines()).toString()
    }

    fun checkCards(cards : List<String>): Int {

        //println("First line is: ${cards[0]}")

        var copiedCards = 0
        for(cardidx in cards.indices) {
            val splittedLine = cards[cardidx].split(": ").last().split("\\s*\\|\\s*".toRegex())
            val winningNumbers = splittedLine[0].split("\\s+".toRegex())
            val cardNumbers = splittedLine[1].split("\\s+".toRegex())

            val numberOfWinningNumbers = cardNumbers.filter { s -> s in winningNumbers }.size
            val maxCardsToCopy = Math.min(numberOfWinningNumbers, (cards.size - 1) - cardidx)
            if(maxCardsToCopy > 0) {
                for (i in 1.rangeUntil(maxCardsToCopy + 1)) {
                    copiedCards += checkCards(cards.subList(cardidx + i, cards.size))
                }
            }

            copiedCards += maxCardsToCopy
        }

        return copiedCards
    }
}