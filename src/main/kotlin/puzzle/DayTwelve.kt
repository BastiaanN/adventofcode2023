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
                println(String.format("%.2f%% complete..", (100.00 / lines.size) * completedLines))
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

        // Count number of questionmarks and number of hashes.
        val segmentInfo = SegmentInfo(
            arrangements,
            arrangements.sum(),
            lineParts[0].count { c -> c == '?' },
            lineParts[0].count { c -> c == '#' },
            0,
            -1)

        // We need to calculate...
        return calculatePossibilities(lineParts[0].trim { c -> c == '.' }.toCharArray(), segmentInfo)
    }

    data class SegmentInfo(
        val arrangements: Array<Int>,
        val arrangementSum: Int,
        var numQuestionMarks: Int,
        var numHashes: Int,
        var arrangementIdxToCheck: Int,
        var lastSegmentIdx: Int
    )

    private fun calculatePossibilities(
        segment: CharArray,
        info: SegmentInfo,
        prevFirstQuestionMarkIdx: Int = 0
    ): Long {
        var possibilities = 0L

//       println("Trying segment: ${segment.concatToString()}")

        // Check if there is still a chance this ever becomes a valid arrangement.
        var arrangementIdx = info.arrangementIdxToCheck
        var lastCheckedSegmentIdx = info.lastSegmentIdx
        var hashLength = 0
        var foundQuestionMark = false

        for (idx in IntRange(lastCheckedSegmentIdx + 1, segment.size - 1)) {

            // If there still is a place to fill in we can probably stop as we are not
            // smart enough to predict the rest...
            if (segment[idx] == '?') foundQuestionMark = true

            // The number of hashes in this part should equal the expected number of arrangements. If not this would
            // always lead to an impossible combination.
            if (!foundQuestionMark || hashLength > 0) {
                if (segment[idx] == '#') ++hashLength
                if ((segment[idx] != '#' || idx == segment.size - 1) && hashLength > 0) {
                    if (arrangementIdx >= info.arrangements.size ||
                        (!foundQuestionMark && hashLength != info.arrangements[arrangementIdx++]) ||
                        (foundQuestionMark  && hashLength > info.arrangements[arrangementIdx++])
                    ) return 0
                    // Reset hashLength.
                    hashLength = 0
                    // Save idx
                    if(!foundQuestionMark) lastCheckedSegmentIdx = idx
                    else arrangementIdx--
                }
            } else {
                break
            }
        }

        // Not enough #/? to get the total arrangement size? Then we stop this flow too.
        if (info.arrangementSum > info.numHashes + info.numQuestionMarks) return 0

        // More hashes than expected? stop too.
        if (info.numHashes > info.arrangementSum) return 0

        var questionMarkIdx = -1
        for (charIdx in IntRange(prevFirstQuestionMarkIdx, segment.size)) {
            if (segment[charIdx] == '?') {
                questionMarkIdx = charIdx
                break
            }
        }

        // Create copy of array.
        val mutatedSegment = segment.copyOf()
        val mutatedSegmentInfo = info.copy()

        // Question mark will get decreased for next iteration, we can also add the idxes of the valid parts
        // that we already have checked that do not need to be checked again if we go deeper.
        mutatedSegmentInfo.numQuestionMarks--
        mutatedSegmentInfo.lastSegmentIdx = lastCheckedSegmentIdx
        mutatedSegmentInfo.arrangementIdxToCheck = arrangementIdx

        mutatedSegment[questionMarkIdx] = '.'
        if (isValidSegment(mutatedSegment, mutatedSegmentInfo)) {
            ++possibilities
        } else if (mutatedSegmentInfo.numQuestionMarks > 0) {
            possibilities += calculatePossibilities(mutatedSegment, mutatedSegmentInfo, questionMarkIdx)
        }

        mutatedSegment[questionMarkIdx] = '#'
        mutatedSegmentInfo.numHashes++
        if (isValidSegment(mutatedSegment, mutatedSegmentInfo)) {
            ++possibilities
        } else if (mutatedSegmentInfo.numQuestionMarks > 0) {
            possibilities += calculatePossibilities(mutatedSegment, mutatedSegmentInfo, questionMarkIdx)
        }

        return possibilities
    }

    private fun isValidSegment(
        segment: CharArray,
        segmentInfo: SegmentInfo
    ): Boolean {
        // If contains ? we are not done yet.
        if (segmentInfo.numQuestionMarks > 0) return false
        // Not same
        if (segmentInfo.numHashes != segmentInfo.arrangementSum) return false

        var arrangementIdx = segmentInfo.arrangementIdxToCheck
        var hashLength = 0
        // All the other parts were already checked by the callee.
        for (charIdx in IntRange(segmentInfo.lastSegmentIdx + 1, segment.size - 1)) {
            if (segment[charIdx] == '#') hashLength++
            if ((segment[charIdx] != '#' || charIdx == segment.size - 1) && hashLength > 0) {
                if (arrangementIdx >= segmentInfo.arrangements.size || hashLength != segmentInfo.arrangements[arrangementIdx++]) {
                    return false
                }
                hashLength = 0
            }
        }

        return true

    }

}
