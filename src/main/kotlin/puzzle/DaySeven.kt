package puzzle

import java.io.File

class DaySeven : Puzzle {

    val camelCards = File("src/main/resources/DaySeven-CamelCards.txt")

    private enum class HandType {
        FIVE_OF_A_KIND,
        FOUR_OF_A_KIND,
        FULL_HOUSE,
        THREE_OF_A_KIND,
        TWO_PAIR,
        ONE_PAIR,
        HIGH_CARD
    }
    override fun solveFirst(): String {
        val strength = listOf('A', 'K', 'Q', 'J', 'T', '9', '8', '7', '6', '5', '4', '3', '2').reversed()
        return solve(strength, false)
    }

    override fun solveSecond(): String {
        val strength = listOf('A', 'K', 'Q', 'T', '9', '8', '7', '6', '5', '4', '3', '2', 'J').reversed()
        return solve(strength, true)
    }

    private class HandComparator(val strength: List<Char>) : Comparator<Hand> {

        override fun compare(o1: Hand?, o2: Hand?): Int {
            if (o1 != null && o2 != null) {
                if (o1.handType == o2.handType) {
                    for (index in o1.hand.indices) {
                        val strengthComp = strength.indexOf(o2.hand[index]).compareTo(strength.indexOf(o1.hand[index]))
                        if (strengthComp != 0) {
                            return strengthComp
                        }
                    }
                } else {
                    return o1.handType.compareTo(o2.handType)
                }
            }
            return 0
        }
    }

    private data class Hand(val hand: String, val bid: Int, var handType: HandType)

    private fun determineHandType(hand: String, useJokers: Boolean): HandType {
        val cardsPerKind = hand.fold(mutableMapOf<Char, Int>()) { acc, c ->
            acc.merge(c, 1) { curVal, addVal -> curVal.plus(addVal) }
            acc
        }.toList()
            .sortedBy { pair -> pair.second }
            .reversed()
            .toMap()
            .toMutableMap()

        if (useJokers) {
            var jokers = cardsPerKind['J'] ?: 0
            if (jokers < 5) {
                cardsPerKind.remove('J')

                while (jokers > 0) {
                    for (key in cardsPerKind.keys) {
                        if (cardsPerKind[key]!! < 5) {
                            cardsPerKind[key] = cardsPerKind[key]!! + 1
                            jokers--
                            break
                        }
                    }
                }
            }
        }

        return when {
            cardsPerKind.containsValue(5) -> HandType.FIVE_OF_A_KIND
            cardsPerKind.containsValue(4) -> HandType.FOUR_OF_A_KIND
            cardsPerKind.containsValue(3) && cardsPerKind.containsValue(2) -> HandType.FULL_HOUSE
            cardsPerKind.containsValue(3) -> HandType.THREE_OF_A_KIND
            cardsPerKind.containsValue(2) && cardsPerKind.size == 3 -> HandType.TWO_PAIR
            cardsPerKind.containsValue(2) -> HandType.ONE_PAIR
            else -> HandType.HIGH_CARD
        }
    }

    private fun solve(strength: List<Char>, useJokers: Boolean): String {
        val hands = mutableListOf<Hand>()

        for (handWithAmount in camelCards.readLines()) {
            val handAndAmount = handWithAmount.split(" ")
            val hand = Hand(handAndAmount[0], handAndAmount[1].toInt(), determineHandType(handAndAmount[0], useJokers))
            hands.add(hand)
        }

        val rankedHands = hands.sortedWith(HandComparator(strength)).reversed()
        return rankedHands.foldIndexed(0) { index, acc, hand -> acc.plus(hand.bid * (index + 1)) }.toString()
    }

}