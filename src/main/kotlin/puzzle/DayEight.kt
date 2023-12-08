package puzzle

import java.io.File

class DayEight : Puzzle {

    val nodeNetwork = File("src/main/resources/DayEight-NodeNavigation.txt")

    private fun getNodeList(networkInfo: List<String>): MutableMap<String, Map<Char, String>> {
        val nodes = mutableMapOf<String, Map<Char, String>>()
        for (node in networkInfo.subList(2, networkInfo.size)) {
            val matchResult = "(\\w+) = \\((\\w+), (\\w+)\\)".toRegex().matchEntire(node)
            if (matchResult != null) {
                nodes.put(matchResult.groupValues[1],
                    mapOf('L' to matchResult.groupValues[2], 'R' to matchResult.groupValues[3]))
            }
        }

        return nodes
    }

    override fun solveFirst(): String {

        val networkInfo = nodeNetwork.readLines()
        val nodes = getNodeList(networkInfo)
        val lrsequence = networkInfo[0]

        var node = "AAA"
        var pathLength = 0
        var sequenceIdx = 0

        while(true) {

            node = nodes[node]!![lrsequence[sequenceIdx]]!!
            sequenceIdx = (sequenceIdx + 1) % lrsequence.length
            pathLength++

            if(node == "ZZZ") {
                break
            }

        }

        return pathLength.toString()
    }

    override fun solveSecond(): String {

        val networkInfo = nodeNetwork.readLines()
        val nodes = getNodeList(networkInfo)
        val lrsequence = networkInfo[0]

        val currentNodes = nodes.keys.filter { s -> s.endsWith('A') }.toMutableList()
        var currentNodeIdx = 0
        var pathLength = 0
        var pathLengths = mutableListOf<Long>()
        var sequenceIdx = 0

        while(true) {

            currentNodes[currentNodeIdx] = nodes[currentNodes[currentNodeIdx]]!![lrsequence[sequenceIdx]]!!
            sequenceIdx = (sequenceIdx + 1) % lrsequence.length
            pathLength++

            // If all nodes end with Z stop.
            if(currentNodes[currentNodeIdx].endsWith('Z')) {
                pathLengths.add(pathLength.toLong())
                currentNodeIdx++
                sequenceIdx = 0
                pathLength = 0
                if(currentNodes.size == pathLengths.size) {
                    break
                }
            }
        }

        // Find LCM
        val longestPath = pathLengths.max()
        var lcm = longestPath
        while(pathLengths.count { no -> lcm % no == 0L } != pathLengths.size) {
            lcm+=longestPath
        }

        return lcm.toString()
    }

}