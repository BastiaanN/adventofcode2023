package puzzle

import java.io.File
import kotlin.math.max
import kotlin.math.min

class DayEleven : Puzzle {

    val galaxies = File("src/main/resources/DayEleven-Galaxies.txt")

    override fun solveFirst(): String {
        val galaxyPositions = buildListWithGalaxyPositions(galaxies.readLines(), 1)
        return calculateDistances(galaxyPositions).toString()
    }

    override fun solveSecond(): String {
        val galaxyPositions = buildListWithGalaxyPositions(galaxies.readLines(), 1000000 - 1)
        return calculateDistances(galaxyPositions).toString()
    }

    private fun calculateDistances(galaxyPositions: MutableList<Pair<Long, Long>>): Long {
        var distances = 0L
        val galaxiesToCalculate = galaxyPositions.toMutableList()
        while(galaxiesToCalculate.isNotEmpty()) {
            val firstGalaxy = galaxiesToCalculate.first()
            for (otherGalaxy in galaxiesToCalculate) {
                if(firstGalaxy == otherGalaxy) continue
                val lineDiff = max(firstGalaxy.first, otherGalaxy.first) - min(firstGalaxy.first, otherGalaxy.first)
                val rowDiff = max(firstGalaxy.second, otherGalaxy.second) - min(firstGalaxy.second, otherGalaxy.second)
                distances += lineDiff + rowDiff
            }
            galaxiesToCalculate.remove(firstGalaxy)
        }

        return distances
    }

    private fun buildListWithGalaxyPositions(lines: List<String>, emptyExpand: Long): MutableList<Pair<Long, Long>> {

        val galaxyPositions = mutableListOf<Pair<Long,Long>>()

        // Get column indexes that only contain '*'
        val emptyColumnIndexes = transpose(lines)
            .mapIndexed { i, s -> if(s == ".".repeat(lines.size)) i else -1 }
            .filter { i -> i >= 0 }

        // Find galaxies
        var lineOffset = 0L
        for ((lineIndex, line) in lines.withIndex()) {
            if(line == ".".repeat(line.length)) {
                lineOffset += emptyExpand
            } else if(line.contains("#")) {
                val galaxyColumnIndexes = line
                    .mapIndexed { rowIndex, c -> if (c == '#') rowIndex else -1 }
                    .filter { i -> i >= 0 }

                for (galaxyColumnIndex in galaxyColumnIndexes) {
                    galaxyPositions.add(Pair(lineIndex + lineOffset,
                        galaxyColumnIndex + emptyColumnIndexes.count { i -> i < galaxyColumnIndex }.times(emptyExpand)))
                }
            }
        }

        return galaxyPositions
    }

    private fun transpose(lines: List<String>): MutableList<String> {
        val transposed = Array(lines.first().length) { CharArray(lines.size) { ' ' } }
        for (lineIdx in lines.indices) {
            for(rowIdx in lines[lineIdx].indices) {
                transposed[rowIdx][lineIdx] = lines[lineIdx][rowIdx]
            }
        }
        return transposed.map { chars -> String(chars) }.toMutableList()
    }

}
