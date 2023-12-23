package puzzle

import java.io.File

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
        for (line in springs.readLines()) {

            var lineParts = line.split(" ")
            val extendedSprings = ("${lineParts[0]}?").repeat(5)
            val unfoldedLine = extendedSprings.substring(0, extendedSprings.length - 1) +
                    " " + (lineParts[1] + ",").repeat(5).trimEnd(',')


            // We should do it times 5.
            val possibilities = calculatePossibilitiesForLine(unfoldedLine)
            println("${possibilities} possibilities for line=${line}")
            sum += possibilities
        }

        // 7047 --> RIGHT!!!!!
        return sum.toString()
    }

    private fun calculatePossibilitiesForLine(line: String): Long {
        // Get rid of extra "."
        val lineParts = line.replace("\\.{2,}".toRegex(), ".").split(" ")
        val arrangements = lineParts[1].split(",").map { s -> s.toInt() }.toTypedArray()

        // We need to calculate...
        return calculatePossibilities(lineParts[0].trim { c -> c == '.' }, arrangements, arrangements.sum())
    }

    private fun calculatePossibilities(segment: String, arrangements: Array<Int>, arrangementSum: Int): Long {
        var possibilities = 0L

//        println("Trying segment: ${segment}")

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

        val mutatedSegmentWithDot = segment.replaceFirst('?', '.')
        if (isValidSegment(mutatedSegmentWithDot, arrangements, arrangementSum)) {
            possibilities++
        } else if (mutatedSegmentWithDot.contains('?')) {
            possibilities += calculatePossibilities(mutatedSegmentWithDot, arrangements, arrangementSum)
        }

        val mutatedSegmentWithHash = segment.replaceFirst('?', '#')
        if (isValidSegment(mutatedSegmentWithHash, arrangements, arrangementSum)) {
            possibilities++
        } else if (mutatedSegmentWithHash.contains('?')) {
            possibilities += calculatePossibilities(mutatedSegmentWithHash, arrangements, arrangementSum)
        }

        return possibilities
    }

    private fun isValidSegment(segment: String, arrangements: Array<Int>, arrangementSum: Int): Boolean {
        // If contains ? we are not done yet.
        if (segment.contains("?")) return false
        // Not same
        if (segment.count { c -> c == '#' } != arrangementSum) return false

        val splittedSegments = segment.split(".").filter { s -> s.isNotEmpty() }
        
        return splittedSegments.size == arrangements.size &&
                arrangements.filterIndexed() { index, i -> splittedSegments[index].length != i }.isEmpty()
    }

}
