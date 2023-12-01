package puzzle

class DayOne : Puzzle {
    override fun solveFirst(): String {
        val calibrationDocument = this::class.java.getResource("/DayOne-CalibrationDocument.txt")

        var sum = 0
        for (line in calibrationDocument.openStream().bufferedReader().readLines()) {
            val onlyDigits = line.filter { c -> c.isDigit() }
            sum += "${onlyDigits.first()}${onlyDigits.last()}".toInt()
        }
        return sum.toString()
    }

    override fun solveSecond(): String {
        val calibrationDocument = this::class.java.getResource("/DayOne-CalibrationDocument.txt")

        var sum = 0
        for (lineWithWrittenNumbers in calibrationDocument.openStream().bufferedReader().readLines()) {
            val onlyDigits = transformStringToOnlyDigits(lineWithWrittenNumbers)
            sum += "${onlyDigits.first()}${onlyDigits.last()}".toInt()
        }
        return sum.toString()
    }

    val writtenNumbersToNumeric = mapOf(
        "one" to "1",
        "two" to "2",
        "three" to "3",
        "four" to "4",
        "five" to "5",
        "six" to "6",
        "seven" to "7",
        "eight" to "8",
        "nine" to "9"
    )

    fun transformStringToOnlyDigits(input: String): String {
        var numericString = ""
        for (idx in input.indices) {
            if (input[idx].isDigit()) {
                numericString += input[idx]
            } else {
                val stringToCheck = input.substring(idx)
                for (writtenNumber in writtenNumbersToNumeric) {
                    if (stringToCheck.startsWith(writtenNumber.key)) {
                        numericString += writtenNumber.value
                        break
                    }
                }
            }
        }
        return numericString
    }
}
