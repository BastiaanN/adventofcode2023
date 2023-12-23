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
        val arrangements = lineParts[1].split(",").map { s -> s.toInt() }

        // We need to calculate...
        return calculatePossibilities(lineParts[0].trim { c -> c == '.' }, arrangements)
    }

    private fun calculatePossibilities(segment: String, arrangements: List<Int>): Long {
        var possibilities = 0L

      //  println("Trying segment: ${segment}")

        // Check if there is still a chance this ever becomes a valid arrangement.
        var arrangementIdx = 0
        var hashLength = 0
        for (segChar in segment) {

            // If there still is a place to fill in we can stop as we are not
            // smart enough to predict the rest...
            if(segChar == '?') {
                // We can try to be a liiiitttle smarter...
                // Not enough #/? to get the total arrangement size. So we can stop this flow too.
//                if(segment.count { c -> c == '#' || c == '?'} < arrangements.sum()) return 0
                break
            }

            // The number of hashes in this part should equal the expected number of arrangements. If not this would
            // always lead to an impossible combination.
            if(segChar == '#') hashLength++
            else if(hashLength > 0) {
                if(arrangementIdx >= arrangements.size || hashLength != arrangements[arrangementIdx++]) return 0
                // Reset hashLength.
                hashLength = 0
            }
        }

        val mutatedSegmentWithDot = segment.replaceFirst('?', '.')
        if (isValidSegment(mutatedSegmentWithDot, arrangements)) {
            possibilities++
        } else if (mutatedSegmentWithDot.contains('?')) {
            possibilities += calculatePossibilities(mutatedSegmentWithDot, arrangements)
        }

        val mutatedSegmentWithHash = segment.replaceFirst('?', '#')
        if (isValidSegment(mutatedSegmentWithHash, arrangements)) {
            possibilities++
        } else if (mutatedSegmentWithHash.contains('?')) {
            possibilities += calculatePossibilities(mutatedSegmentWithHash, arrangements)
        }

        return possibilities
    }

    private fun isValidSegment(segment: String, arrangements: List<Int>): Boolean {
        // If contains ? we are not done yet.
        if (segment.contains("?")) return false
        // Not same
        if (segment.count { c -> c == '#' } != arrangements.sum()) return false

        val splittedSegments = segment.split(".").filter { s -> s.isNotEmpty() }
        
        return splittedSegments.size == arrangements.size &&
                arrangements.filterIndexed() { index, i -> splittedSegments[index].length != i }.isEmpty()
    }

}
