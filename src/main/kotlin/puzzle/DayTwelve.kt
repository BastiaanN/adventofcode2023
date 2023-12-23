package puzzle

import java.io.File
import java.util.concurrent.CompletableFuture

class DayTwelve : Puzzle {

    val springs = File("src/main/resources/DayTwelve-Springs.txt")

    override fun solveFirst(): String {
        var sum = 0L
        for (line in springs.readLines()) {
            val possibilities = calculatePossibilitiesForLine(line)
            sum += possibilities
        }

        // 7047 --> RIGHT!!!!!
        return sum.toString()
    }

    override fun solveSecond(): String {

        var sum = 0L
        var completedLines = 0
        val futures = mutableListOf<CompletableFuture<Long>>()
        val lines = springs.readLines()
        for (line in lines) {

            var lineParts = line.split(" ")
            val extendedSprings = ("${lineParts[0]}?").repeat(5)
            val unfoldedLine = extendedSprings.substring(0, extendedSprings.length - 1) +
                    " " + (lineParts[1] + ",").repeat(5).trimEnd(',')

            val future = CompletableFuture.supplyAsync {
                calculatePossibilitiesForLine(unfoldedLine)
            }

            future.whenComplete { result, _ ->
//                println("${result} possibilities for line ${unfoldedLine}")
                completedLines += 1
                println(String.format("%.2f%% complete..", (100.00/lines.size) * completedLines))
            }

            futures.add(future)
        }

        // Calculate all futures.
        for (future in futures) {
            sum += future.get()
        }

        return sum.toString()
    }

    private fun calculatePossibilitiesForLine(line: String): Long {
        // Get rid of extra "."
        val lineParts = line.replace("\\.{2,}".toRegex(), ".").split(" ")
        val arrangements = lineParts[1].split(",").map { s -> s.toInt() }.toTypedArray()

        // We need to calculate...
        return calculatePossibilities(lineParts[0].trim { c -> c == '.' }.toCharArray(), arrangements, arrangements.sum())
    }

    private fun calculatePossibilities(segment: CharArray, arrangements: Array<Int>, arrangementSum: Int, prevFirstQuestionMarkIdx: Int = 0): Long {
        var possibilities = 0L

//        println("Trying segment: ${segment.concatToString()}")

        // Check if there is still a chance this ever becomes a valid arrangement.
        var arrangementIdx = 0
        var hashLength = 0
        var numPossibleHashes = 0
        var foundQuestionMark = false
        for (segChar in segment) {

            // If there still is a place to fill in we can probably stop as we are not
            // smart enough to predict the rest...
            if (segChar == '?') foundQuestionMark = true
            if (segChar == '#' || segChar == '?') numPossibleHashes++

            // The number of hashes in this part should equal the expected number of arrangements. If not this would
            // always lead to an impossible combination.
            if(foundQuestionMark) {
                if (numPossibleHashes > arrangementSum) break
            } else {
                if (segChar == '#') hashLength++
                else if (hashLength > 0) {
                    if (arrangementIdx >= arrangements.size || hashLength != arrangements[arrangementIdx++]) return 0
                    // Reset hashLength.
                    hashLength = 0
                }
            }
        }

        // Not enough #/? to get the total arrangement size? Then we stop this flow too.
        if(arrangementSum > numPossibleHashes) return 0

        var questionMarkIdx = -1
        for (charIdx in IntRange(prevFirstQuestionMarkIdx, segment.size)) {
            if(segment[charIdx] == '?') {
                questionMarkIdx = charIdx
                break
            }
        }

        // Create copy of array.
        val mutatedSegment = segment.copyOf()

        mutatedSegment[questionMarkIdx] = '.'
        if (isValidSegment(mutatedSegment, arrangements, arrangementSum)) {
            possibilities++
        } else if (mutatedSegment.contains('?')) {
            possibilities += calculatePossibilities(mutatedSegment, arrangements, arrangementSum, questionMarkIdx)
        }

        mutatedSegment[questionMarkIdx] = '#'
        if (isValidSegment(mutatedSegment, arrangements, arrangementSum)) {
            possibilities++
        } else if (mutatedSegment.contains('?')) {
            possibilities += calculatePossibilities(mutatedSegment, arrangements, arrangementSum, questionMarkIdx)
        }

        return possibilities
    }

    private fun isValidSegment(segment: CharArray, arrangements: Array<Int>, arrangementSum: Int): Boolean {
        // If contains ? we are not done yet.
        if (segment.contains('?')) return false
        // Not same
        if (segment.count { c -> c == '#' } != arrangementSum) return false

//        val splittedSegments = segment.concatToString().split(".").filter { s -> s.isNotEmpty() }
//
//        return splittedSegments.size == arrangements.size &&
//                arrangements.filterIndexed() { index, i -> splittedSegments[index].length != i }.isEmpty()

        var arrangementIdx = 0
        var hashLength = 0
        for (charIdx in segment.indices) {
            if(segment[charIdx] == '#') hashLength++
            if((segment[charIdx] != '#' || charIdx == segment.size -1) && hashLength > 0) {
                if(arrangementIdx >= arrangements.size || hashLength != arrangements[arrangementIdx++]) {
                    return false
                }
                hashLength = 0
            }
        }

        return true

    }

}
