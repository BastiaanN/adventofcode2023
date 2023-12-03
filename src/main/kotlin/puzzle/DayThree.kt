package puzzle

import java.io.File

class DayThree : Puzzle {

    val engineSchematic = File("src/main/resources/DayThree-EngineSchematic.txt")

    override fun solveFirst(): String {

        val schematicLines = engineSchematic.readLines()
        var sum = 0
        for (rowNo in schematicLines.indices) {

            val currentRow = schematicLines[rowNo]

            var numStartIdx = -1
            var numEndIdx = -1
            var columnNo = 0

            while (columnNo < currentRow.length) {

                // Find start of numeric part
                if (numStartIdx < 0 && currentRow[columnNo].isDigit()) {
                    numStartIdx = columnNo
                }

                // Find end of numeric part
                if (numStartIdx >= 0 && !currentRow[columnNo].isDigit()) {
                    numEndIdx = columnNo - 1
                } else if (numStartIdx >= 0 && columnNo == currentRow.length - 1) {
                    numEndIdx = columnNo
                }

                // If we have a startidx AND endidx, we should do something with it
                if (numStartIdx >= 0 && numEndIdx >= 0) {
                    // Yay, digits!
                    val numericPart = currentRow.substring(numStartIdx, numEndIdx + 1)

                    val boxSize = calcBoxSize(numStartIdx, numEndIdx, rowNo, schematicLines.size, currentRow.length)
                    val hasAdjancentSymbol = schematicLines.subList(boxSize.rowStart, boxSize.rowEnd + 1)
                        .map { s -> s.substring(boxSize.colStart, boxSize.colEnd + 1) }
                        .filter { s -> s.contains("[^\\d.]".toRegex()) }
                        .firstOrNull() != null

                    if (hasAdjancentSymbol) {
                        sum += numericPart.toInt()
                    }

                    // Skip over all the matched columns.
                    columnNo = numEndIdx + 1

                    // Reset start and end index.
                    numStartIdx = -1
                    numEndIdx = -1
                } else {
                    columnNo += 1
                }
            }
        }
        return sum.toString()
    }

    override fun solveSecond(): String {

        val gearIdxToPartNumbers = mutableMapOf<String, MutableList<Int>>()
        val schematicLines = engineSchematic.readLines()

        for (rowNo in schematicLines.indices) {

            val currentRow = schematicLines[rowNo]

            var numStartIdx = -1
            var numEndIdx = -1
            var columnNo = 0

            while (columnNo < currentRow.length) {

                // Find start of numeric part
                if (numStartIdx < 0 && currentRow[columnNo].isDigit()) {
                    numStartIdx = columnNo
                }

                // Find end of numeric part
                if (numStartIdx >= 0 && !currentRow[columnNo].isDigit()) {
                    numEndIdx = columnNo - 1
                } else if (numStartIdx >= 0 && columnNo == currentRow.length - 1) {
                    numEndIdx = columnNo
                }

                // If we have a startidx AND endidx, we should do something with it
                if (numStartIdx >= 0 && numEndIdx >= 0) {

                    // Yay, digits!
                    val numericPart = currentRow.substring(numStartIdx, numEndIdx + 1)

                    // Find adjacent gears and store the part number with the gear index if found.
                    val boxSize = calcBoxSize(numStartIdx, numEndIdx, rowNo, schematicLines.size, currentRow.length)
                    for(searchRowIdx in boxSize.rowStart..<boxSize.rowEnd + 1) {
                        for(searchColIdx in boxSize.colStart ..<boxSize.colEnd + 1) {
                            if(schematicLines[searchRowIdx][searchColIdx] == '*') {
                                gearIdxToPartNumbers.putIfAbsent("${searchRowIdx}_${searchColIdx}", mutableListOf())
                                gearIdxToPartNumbers["${searchRowIdx}_${searchColIdx}"]?.add(numericPart.toInt())
                            }
                        }
                    }

                    // Skip over all the matched columns.
                    columnNo = numEndIdx + 1

                    // Reset start and end index.
                    numStartIdx = -1
                    numEndIdx = -1
                } else {
                    columnNo += 1
                }
            }
        }

        return gearIdxToPartNumbers
            .filter { entry -> entry.value.size == 2 }
            .map { entry -> entry.value.reduce {acc, i -> acc.times(i) } }
            .sum().toString()

    }

    private fun calcBoxSize(startIndex: Int, endIndex: Int, rowIndex: Int, rows: Int, columns: Int): BoxSize {
        var rowStart = if (rowIndex > 0) rowIndex - 1 else rowIndex
        var rowEnd = if (rowIndex < columns - 1) rowIndex + 1 else rowIndex
        val colStart = if (startIndex > 0) startIndex - 1 else startIndex
        val colEnd = if (endIndex < rows - 1) endIndex + 1 else endIndex
        return BoxSize(rowStart, rowEnd, colStart, colEnd)
    }

    private data class BoxSize(var rowStart: Int, var rowEnd: Int, var colStart: Int, var colEnd: Int)
}