import Direction.DOWN
import Direction.LEFT
import Direction.RIGHT
import Direction.UP

enum class Direction {
    UP, DOWN, LEFT, RIGHT
}

data class Point(val x: Int, val y: Int) {
    fun left() = if (x - 1 >= 0) Point(x - 1, y) else null
    fun right(max: Int = Int.MAX_VALUE) = if (x + 1 < max) Point(x + 1, y) else null

    fun up() = if (y - 1 >= 0) Point(x, y - 1) else null

    fun down(max: Int = Int.MAX_VALUE) = if (y + 1 < max) Point(x, y + 1) else null

    fun next(char: Char, direction: Direction): Pair<Point, Direction> =
        when (char) {
            '┐' -> when (direction) {
                UP -> left()!! to LEFT
                RIGHT -> down()!! to DOWN
                else -> throw Exception("Invalid direction $direction")
            }

            '┌' -> when (direction) {
                UP -> right()!! to RIGHT
                LEFT -> down()!! to DOWN
                else -> throw Exception("Invalid direction $direction")
            }

            '┘' -> when (direction) {
                DOWN -> left()!! to LEFT
                RIGHT -> up()!! to UP
                else -> throw Exception("Invalid direction $direction")
            }

            '└' -> when (direction) {
                DOWN -> right()!! to RIGHT
                LEFT -> up()!! to UP
                else -> throw Exception("Invalid direction $direction")
            }

            '─' -> when (direction) {
                LEFT -> left()!! to LEFT
                RIGHT -> right()!! to RIGHT
                else -> throw Exception("Invalid direction $direction")
            }

            '│' -> when (direction) {
                UP -> up()!! to UP
                DOWN -> down()!! to DOWN
                else -> throw Exception("Invalid direction $direction")
            }

            else -> throw Exception("Invalid char $char")
        }
}

class Maze(val input: List<String>) {
    val start: Point = run {
        val index = input.indexOfFirst { it.contains('S') }
        Point(input[index].indexOf('S'), index)
    }
    val maxX = input[0].length
    val maxY = input.size
    operator fun get(point: Point) = input[point.y][point.x]

    val pipePoints = findPipe()

    private fun findPipe(): MutableSet<Point> {
//        println(start)
//        println(input.joinToString("\n"))
        val traversed = mutableSetOf(start)
        var currentPoints = mutableSetOf<Pair<Point, Direction>>()
        val left = start.left()
        if (left != null && this[left] in listOf('─', '└', '┌')) {
            currentPoints.add(left to LEFT)
        }
        val right = start.right(this.maxX)
        if (right != null && this[right] in listOf('─', '┘', '┐')) {
            currentPoints.add(right to RIGHT)
        }
        val up = start.up()
        if (up != null && this[up] in listOf('│', '┌', '┐')) {
            currentPoints.add(up to UP)
        }
        val down = start.down(this.maxY)
        if (down != null && this[down] in listOf('│', '└', '┘')) {
            currentPoints.add(down to DOWN)
        }
        while (true) {
            traversed.addAll(currentPoints.map { it.first })
            val newPairs = currentPoints.mapTo(HashSet()) { it.first.next(this[it.first], it.second) }
            val newPoints = newPairs.mapTo(HashSet()) { it.first }
            if (traversed.intersect(newPoints).isNotEmpty()) {
                break
            }
            currentPoints = newPairs.toMutableSet()
            traversed.addAll(newPoints)
        }
        return traversed
    }

    fun prettyPrint() {
        input.indices.forEach { y ->
            input[0].indices.forEach { x ->
                val point = Point(x, y)
                val ch: Char = this[point]
                if (point in pipePoints || ch == '.' || ch == 'S') {
                    print(ch)
                } else {
                    print('.')
                }
            }
            println("")
        }
    }

    fun isInside(point: Point): Boolean {
        val upPipeHits = pipeHits(point, UP)
        val downPipeHits = pipeHits(point, DOWN)
        val leftPipeHits = pipeHits(point, LEFT)
        val rightPipeHits = pipeHits(point, RIGHT)
        return upPipeHits % 2 == downPipeHits % 2 && leftPipeHits % 2 == rightPipeHits % 2 &&
                upPipeHits > 0 && downPipeHits > 0 && leftPipeHits > 0 && rightPipeHits > 0
    }

    private fun pipeHits(point: Point, direction: Direction, pipeHits: Int = 0): Int {
        while (true) {
            val nextPoint = when (direction) {
                UP -> point.up() ?: return pipeHits
                DOWN -> point.down(maxY) ?: return pipeHits
                LEFT -> point.left() ?: return pipeHits
                RIGHT -> point.right(maxX) ?: return pipeHits
            }
            val currentChar = get(nextPoint)
            val ignorePipe = when {
                currentChar == '─' && (direction == LEFT || direction == RIGHT) -> true
                currentChar == '│' && (direction == UP || direction == DOWN) -> true
                else -> false
            }
            val inPipePoints = nextPoint in pipePoints
            val nextPipeHits = if (inPipePoints && !ignorePipe) pipeHits + 1 else pipeHits
            return pipeHits(
                nextPoint,
                direction,
                nextPipeHits
            )
        }
    }
}

fun main() {

    fun parse(input: List<String>): Maze {
        return Maze(input.map {
            it.replace('7', '┐')
                .replace('F', '┌')
                .replace('J', '┘')
                .replace('L', '└')
                .replace('-', '─')
                .replace('|', '│')
        })
    }

    fun part1(input: List<String>): Long {
        val maze = parse(input)
        return (maze.pipePoints.size.toLong() / 2) + 1L
        //7012
    }

    fun part2(input: List<String>): Long {
        val maze = parse(input)
        maze.prettyPrint()
        var count = 0L
        input.indices.forEach { y ->
            input[0].indices.forEach { x ->
                val point = Point(x, y)
                if (point !in maze.pipePoints && maze.isInside(point)) {
                    println("!!INSIDE $x, $y")
                    count++
                }
            }
        }
        return count
    }

//    val part1 = part1(readInput("Day10_test"))
//    val part1Expected = 8L
//    check(part1 == part1Expected) {
//        "Expected $part1Expected, got $part1"
//    }

    fun checkPart2(input: String, expected: Long) {
        val part2 = part2(readInput(input))
        val part2Expected = expected
        check(part2 == part2Expected) {
            "Expected $part2Expected, got $part2"
        }
    }
//    checkPart2("Day10_test2", 4L)
//    checkPart2("Day10_test3", 4L)
//    checkPart2("Day10_test4", 8L)
//    checkPart2("Day10_test5", 10L)

    val input = readInput("Day10")
//    part1(input).println() // 7012
    part2(input).println()
}

