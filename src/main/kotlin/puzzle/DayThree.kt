package puzzle

import java.io.File

class DayThree : Puzzle {

    val engineSchematic = File("src/main/resources/DayThree-EngineSchematic.txt")

    override fun solveFirst(): String {

        val schematicLines = engineSchematic.readLines()
        var sum = 0
        for (rowNo in schematicLines.indices) {

            val currentRow = schematicLines[rowNo]

            "\\d+".toRegex().findAll(currentRow).forEach { matchResult ->

                val boxSize = calcBoxSize(
                    matchResult.range.start, matchResult.range.endInclusive,
                    rowNo, schematicLines.size, currentRow.length
                )

                val hasAdjancentSymbol = schematicLines.subList(boxSize.rowStart, boxSize.rowEnd + 1)
                    .map { s -> s.substring(boxSize.colStart, boxSize.colEnd + 1) }
                    .filter { s -> s.contains("[^\\d.]".toRegex()) }
                    .firstOrNull() != null

                if (hasAdjancentSymbol) {
                    sum += matchResult.value.toInt()
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

            "\\d+".toRegex().findAll(currentRow).forEach { matchResult ->

                // Find adjacent gears and store the part number with the gear index if found.
                val boxSize = calcBoxSize(
                    matchResult.range.start, matchResult.range.endInclusive,
                    rowNo, schematicLines.size, currentRow.length
                )

                for (searchRowIdx in boxSize.rowStart..<boxSize.rowEnd + 1) {
                    for (searchColIdx in boxSize.colStart..<boxSize.colEnd + 1) {
                        if (schematicLines[searchRowIdx][searchColIdx] == '*') {
                            gearIdxToPartNumbers.putIfAbsent("${searchRowIdx}_${searchColIdx}", mutableListOf())
                            gearIdxToPartNumbers["${searchRowIdx}_${searchColIdx}"]?.add(matchResult.value.toInt())
                        }
                    }
                }
            }
        }

        return gearIdxToPartNumbers
            .filter { entry -> entry.value.size == 2 }
            .map { entry -> entry.value.reduce { acc, i -> acc.times(i) } }
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