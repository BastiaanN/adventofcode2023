package puzzle

import java.io.File
import kotlin.math.ceil

// RUN WITH BIG STACK -> -Xss10m
class DayTen : Puzzle {

    private val nodesFile = File("src/main/resources/DayTen-Nodes.txt")

    private enum class Direction {
        UP,
        RIGHT,
        DOWN,
        LEFT
    }

    private val pipeInfoMap = mapOf(
        'F' to PipeInfo(listOf(Direction.DOWN, Direction.RIGHT), true, '┌'),
        '-' to PipeInfo(listOf(Direction.LEFT, Direction.RIGHT), false, '─'),
        '7' to PipeInfo(listOf(Direction.LEFT, Direction.DOWN), true, '┐'),
        '|' to PipeInfo(listOf(Direction.UP, Direction.DOWN), false, '│'),
        'J' to PipeInfo(listOf(Direction.UP, Direction.LEFT), true, '┘'),
        'L' to PipeInfo(listOf(Direction.UP, Direction.RIGHT), true, '└'),
        'S' to PipeInfo(listOf(Direction.UP, Direction.DOWN, Direction.LEFT, Direction.RIGHT), null, 'S')
    )

    override fun solveFirst(): String {

        var pathLength = 0
        val lines = nodesFile.readLines()
        val lineToStartWith = lines.indexOfFirst { string -> string.contains('S') }
        val startNode = Node('S', lineToStartWith, lines[lineToStartWith].indexOf('S'))

        if (findClosedLoopRecursive(lines, startNode, startNode, null) != null) {
            // Yay! We can now find the length of the path.
            var node: Node? = startNode
            while (node != startNode || pathLength == 0) {
                node = node?.nextNode
                if (node?.type != 'S') pathLength++
            }
        }

        val furthestPath = ceil(pathLength / 2.0).toInt()
        return furthestPath.toString()
    }

    override fun solveSecond(): String {
        val tilesInside = mutableMapOf<Int, MutableSet<Int>>()

        val lines = nodesFile.readLines()
        val lineToStartWith = lines.indexOfFirst { string -> string.contains('S') }
        val startNode = Node('S', lineToStartWith, lines[lineToStartWith].indexOf('S'))

        if(findClosedLoopRecursive(lines, startNode, startNode, null) != null) {

            // go from linked list to map with lines and rows.
            val rowsContainingPipes = mutableMapOf(startNode.lineIdx to mutableListOf(startNode.rowIdx))
            var node = startNode.nextNode!!
            while (node != startNode) {
                if (node.type != 'S') {
                    rowsContainingPipes.getOrPut(node.lineIdx) { mutableListOf() }.add(node.rowIdx)
                }
                node = node.nextNode!!
            }

            var direction = getInitialDirection(startNode)
            node = startNode.nextNode!!
            while(node != startNode) {

                // Fill current inside
                floodFill(lines, rowsContainingPipes, tilesInside, direction, node)

                // If corner we should change direction.
                if(pipeInfoMap[node.type]?.isCorner!!) {
                    direction = when {
                        (node.type == 'J' || node.type == 'F') && direction == Direction.LEFT -> Direction.UP
                        (node.type == 'J' || node.type == 'F') && direction == Direction.UP -> Direction.LEFT
                        (node.type == 'J' || node.type == 'F') && direction == Direction.DOWN -> Direction.RIGHT
                        (node.type == 'J' || node.type == 'F') && direction == Direction.RIGHT -> Direction.DOWN
                        (node.type == 'L' || node.type == '7') && direction == Direction.UP -> Direction.RIGHT
                        (node.type == 'L' || node.type == '7') && direction == Direction.RIGHT -> Direction.UP
                        (node.type == 'L' || node.type == '7') && direction == Direction.LEFT -> Direction.DOWN
                        (node.type == 'L' || node.type == '7') && direction == Direction.DOWN -> Direction.LEFT
                        else -> throw IllegalArgumentException("BIEM")
                    }
                    floodFill(lines, rowsContainingPipes, tilesInside, direction, node)
                }

                node = node.nextNode!!
            }

            visualizePipe(lines, rowsContainingPipes, tilesInside)

        }

        return tilesInside.values.sumOf { s -> s.size }.toString()
    }

    private fun floodFill(lines: List<String>, rowsContainingPipes: Map<Int, List<Int>>,
                          tilesInside: MutableMap<Int, MutableSet<Int>>, direction: Direction, node: Node) {

        var lineIdx = node.lineIdx
        var rowIdx = node.rowIdx

        when(direction) {
            Direction.LEFT ->
                while(rowsContainingPipes[lineIdx]?.contains(--rowIdx) == false && rowIdx >= 0) {
                    tilesInside.getOrPut(lineIdx) { mutableSetOf() }.add(rowIdx)}
            Direction.RIGHT ->
                while(rowsContainingPipes[lineIdx]?.contains(++rowIdx) == false && rowIdx < lines[lineIdx].length) {
                    tilesInside.getOrPut(lineIdx) { mutableSetOf() }.add(rowIdx)}
            Direction.UP ->
                while(rowsContainingPipes[--lineIdx]?.contains(rowIdx) == false && lineIdx >= 0) {
                    tilesInside.getOrPut(lineIdx) { mutableSetOf() }.add(rowIdx)}
            Direction.DOWN ->
                while(rowsContainingPipes[++lineIdx]?.contains(rowIdx) == false && lineIdx < lines.size) {
                    tilesInside.getOrPut(lineIdx) { mutableSetOf() }.add(rowIdx)}
        }
    }

    private fun getInitialDirection(startNode: Node): Direction {

        val isClockwise = startNode.rowIdx < startNode.nextNode!!.rowIdx || startNode.lineIdx > startNode.nextNode!!.lineIdx
        val firstCorner = getFirstCornerFromNode(startNode)

        return when {
            isClockwise && firstCorner.type == 'F' -> Direction.RIGHT
            isClockwise && firstCorner.type == 'J' -> Direction.UP
            isClockwise && firstCorner.type == '7' && firstCorner.lineIdx == startNode.lineIdx -> Direction.DOWN
            isClockwise && firstCorner.type == '7' && firstCorner.lineIdx < startNode.lineIdx -> Direction.LEFT

            !isClockwise && firstCorner.type == 'J' -> Direction.LEFT
            !isClockwise && firstCorner.type == 'F' -> Direction.DOWN
            !isClockwise && firstCorner.type == 'L' && firstCorner.lineIdx == startNode.lineIdx -> Direction.UP
            !isClockwise && firstCorner.type == 'L' && firstCorner.lineIdx > startNode.lineIdx -> Direction.RIGHT

            else -> throw IllegalStateException("Unexpected :( clockwise=${isClockwise}, type='${firstCorner.type}")
        }
    }

    private fun getFirstCornerFromNode(node: Node) : Node {
        var cornerNode = node.nextNode
        while(pipeInfoMap[cornerNode?.type]?.isCorner != true) {
            cornerNode = cornerNode?.nextNode
        }
        return cornerNode!!
    }

    private fun findClosedLoopRecursive(
        lines: List<String>,
        startNode: Node,
        node: Node,
        directionToSkip: Direction?
    ): Node? {

        // Try to find a closed loop in all directions of this node.
        val nextNodes = mutableListOf<SearchResult>()

        if (directionToSkip != Direction.UP)
            nextNodes.add(SearchResult(findNextNode(lines, node, Direction.UP), getOpposite(Direction.UP)))
        if (directionToSkip != Direction.DOWN)
            nextNodes.add(SearchResult(findNextNode(lines, node, Direction.DOWN), getOpposite(Direction.DOWN)))
        if (directionToSkip != Direction.LEFT)
            nextNodes.add(SearchResult(findNextNode(lines, node, Direction.LEFT), getOpposite(Direction.LEFT)))
        if (directionToSkip != Direction.RIGHT)
            nextNodes.add(SearchResult(findNextNode(lines, node, Direction.RIGHT), getOpposite(Direction.RIGHT)))

        // For every node that is not null, we should try to find the next one recursive.
        for (nodeSearchResult in nextNodes) {
            val nextNode = nodeSearchResult.node
            val skipNext = nodeSearchResult.nextDirectionToSkip
            if (nextNode != null) {
                if (nextNode != startNode) {
                    val closedLoop = findClosedLoopRecursive(lines, startNode, nextNode, skipNext)
                    if (closedLoop != null) {
                        node.nextNode = nextNode
                        return closedLoop
                    }
                } else if (nextNode == startNode) {
                    // Make sure to use the REAL startNode as next node in this case
                    node.nextNode = startNode
                    return node
                }
            }
        }
        return null
    }

    private fun findNextNode(lines: List<String>, node: Node, direction: Direction): Node? {
        // First check if this is a valid search direction for this node.
        if (pipeInfoMap[node.type]?.possibleConnections?.contains(direction) == true) {
            val nextNode = when {
                // UP
                direction == Direction.UP && node.lineIdx > 0 -> Node(
                    lines[node.lineIdx - 1][node.rowIdx],
                    node.lineIdx - 1,
                    node.rowIdx
                )
                // DOWN
                direction == Direction.DOWN && node.lineIdx + 1 < lines.size -> Node(
                    lines[node.lineIdx + 1][node.rowIdx],
                    node.lineIdx + 1,
                    node.rowIdx
                )
                // LEFT
                direction == Direction.LEFT && node.rowIdx > 0 -> Node(
                    lines[node.lineIdx][node.rowIdx - 1],
                    node.lineIdx,
                    node.rowIdx - 1
                )
                // RIGHT
                direction == Direction.RIGHT && node.rowIdx + 1 < lines[node.lineIdx].length -> Node(
                    lines[node.lineIdx][node.rowIdx + 1],
                    node.lineIdx,
                    node.rowIdx + 1
                )

                else -> return null
            }

            if (pipeInfoMap[nextNode.type]?.possibleConnections?.contains(getOpposite(direction)) == true) {
                return nextNode
            }

        }

        return null
    }

    private fun getOpposite(direction: Direction): Direction {
        return when (direction) {
            Direction.DOWN -> Direction.UP
            Direction.UP -> Direction.DOWN
            Direction.LEFT -> Direction.RIGHT
            Direction.RIGHT -> Direction.LEFT
        }
    }

    private fun visualizePipe(lines: List<String>, rowsContainingPipes: Map<Int, List<Int>>, tilesInside: MutableMap<Int, MutableSet<Int>>) {
        println("")
        for (lineIdx in lines.indices) {
            for (rowIdx in lines[lineIdx].indices) {

                if(tilesInside[lineIdx]?.contains(rowIdx) == true) print("\u001b[32m")
                if(rowsContainingPipes[lineIdx]?.contains(rowIdx) == true) print("\u001b[31m")

                print(pipeInfoMap[lines[lineIdx][rowIdx]]?.boxChar ?: lines[lineIdx][rowIdx])

                if(rowsContainingPipes[lineIdx]?.contains(rowIdx) == true ||
                    tilesInside[lineIdx]?.contains(rowIdx) == true) print("\u001b[0m")
            }
            println("")
        }
    }

    private data class SearchResult(val node: Node?, val nextDirectionToSkip: Direction)

    private data class PipeInfo(val possibleConnections: List<Direction>, val isCorner: Boolean?, val boxChar: Char)

    data class Node(
        val type: Char,
        val lineIdx: Int,
        val rowIdx: Int
    ) {
        var nextNode: Node? = null
    }

}
