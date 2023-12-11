import Direction.DOWN
import Direction.DOWN_LEFT
import Direction.DOWN_RIGHT
import Direction.LEFT
import Direction.RIGHT
import Direction.UP
import Direction.UP_LEFT
import Direction.UP_RIGHT

enum class Direction {
    UP,
    UP_RIGHT,
    RIGHT,
    DOWN_RIGHT,
    DOWN,
    DOWN_LEFT,
    LEFT,
    UP_LEFT,
    ;

    fun opposite() = when (this) {
        UP -> DOWN
        DOWN -> UP
        LEFT -> RIGHT
        RIGHT -> LEFT
        UP_LEFT -> DOWN_RIGHT
        UP_RIGHT -> DOWN_LEFT
        DOWN_LEFT -> UP_RIGHT
        DOWN_RIGHT -> UP_LEFT
    }

    operator fun next() = entries[(ordinal + 1) % entries.size]

    operator fun rangeUntil(other: Direction): List<Direction> {
        val list = mutableListOf<Direction>()
        var current = this
        while (current != other) {
            list.add(current)
            current = current.next()
        }
        return list
    }
}

val pipeMap = mapOf(
    '┐' to Pipe(LEFT, DOWN),
    '┌' to Pipe(RIGHT, DOWN),
    '┘' to Pipe(LEFT, UP),
    '└' to Pipe(RIGHT, UP),
    '─' to Pipe(LEFT, RIGHT),
    '│' to Pipe(UP, DOWN),
)

data class Pipe(val exit: Direction, val exit2: Direction) {
    val oneSide = exit.next() ..< exit2
    val otherSide = exit2.next() ..< exit
}

data class Point(val x: Int, val y: Int) {
    fun left() = if (x - 1 >= 0) Point(x - 1, y) else null
    fun right(max: Int = Int.MAX_VALUE) = if (x + 1 < max) Point(x + 1, y) else null

    fun up() = if (y - 1 >= 0) Point(x, y - 1) else null

    fun down(max: Int = Int.MAX_VALUE) = if (y + 1 < max) Point(x, y + 1) else null

    fun move(direction: Direction, maxX: Int, maxY: Int) = when (direction) {
        UP -> up()
        DOWN -> down(maxY)
        LEFT -> left()
        RIGHT -> right(maxX)
        UP_LEFT -> up()?.left()
        UP_RIGHT -> up()?.right(maxX)
        DOWN_LEFT -> down(maxY)?.left()
        DOWN_RIGHT -> down(maxY)?.right(maxX)
        else -> throw Exception("Invalid direction $direction")
    }

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

data class State(val outsideDirections: Set<Direction>) {
    constructor(vararg outsideDirections: Direction) : this(outsideDirections.toSet())
    constructor(outsideDirections: Iterable<Direction>) : this(outsideDirections.toSet())

    override fun toString(): String {
        return outsideDirections.joinToString(",")
    }
}

class Maze(val input: List<String>) {
    val start: Point = run {
        val index = input.indexOfFirst { it.contains('S') }
        Point(input[index].indexOf('S'), index)
    }
    val maxX = input[0].length
    val maxY = input.size
    operator fun get(point: Point) = if (point == start) startChar else input[point.y][point.x]

    fun prettyGet(point: Point) = if (point in pipePoints) this[point] else '.'
    fun prettyGet2(point: Point) = when (point) {
        in pipePoints -> this[point]
        in insidePoints -> 'I'
        else -> 'O'
    }

    fun getState(point: Point) = state[point.y][point.x]
    fun setState(point: Point, state: State) {
        val state1 = this.state[point.y][point.x]
        require(state1 == null || state1 == state) {
            "State already set for $point to $state, tried to set to $state1"
        }
        this.state[point.y][point.x] = state
    }

    private val findPipe = findPipe()

    val pipePoints = findPipe.first
    val startChar = findPipe.second

    val state: Array<Array<State?>> = Array(maxY) {
        Array(maxX) {
            null

        }
    }

    val insidePoints: Set<Point>

    init {
        prettyPrint()
        val origin = Point(0, 0)
        val toProcess = mutableSetOf<Point>()
        toProcess.add(origin)
        val unknowns = mutableSetOf<Point>()
        while (true) {
            val unknownSizeBefore = unknowns.size
            unknowns.clear()
            while (toProcess.isNotEmpty()) {
                val point = toProcess.first()
                toProcess.remove(point)
                process(point, toProcess, unknowns)
            }
            if (unknowns.isEmpty() || unknowns.size == unknownSizeBefore) {
                break
            }
            toProcess.addAll(unknowns)
        }
        insidePoints = unknowns
    }

    private fun isOutside(point: Point, direction: Direction): Boolean? {
        val newPoint = point.move(direction, maxX, maxY) ?: return true
        val newState = getState(newPoint)
        if (newState != null) {
            return newState.outsideDirections.contains(direction.opposite())
        }
        return null
    }

    private fun isInside(point: Point, direction: Direction): Boolean? {
        val newPoint = point.move(direction, maxX, maxY) ?: return false
        val newState = getState(newPoint)
        if (newState != null) {
            return newState.outsideDirections.contains(direction.opposite()).not()
        }
        return null
    }

    private fun process(point: Point, toProcess: MutableSet<Point>, unknowns: MutableSet<Point>) {
        if (getState(point) != null) {
            return
        }
        val pipe = pipeMap[prettyGet(point)]
        if (pipe == null) {
            val allStates = State(UP, DOWN, LEFT, RIGHT, UP_LEFT, UP_RIGHT, DOWN_LEFT, DOWN_RIGHT)
            if (Direction.entries.any { isOutside(point, it) == true }) {
                setState(point, allStates)
            }
        } else {
            when {
                pipe.oneSide.any { isOutside(point, it) == true } -> setState(point, State(pipe.oneSide))
                pipe.otherSide.any { isOutside(point, it) == true } -> setState(point, State(pipe.otherSide))
                pipe.oneSide.any { isInside(point, it) == true } -> setState(point, State(pipe.otherSide))
                pipe.otherSide.any { isInside(point, it) == true } -> setState(point, State(pipe.oneSide))
            }
        }
        if (getState(point) == null) {
            unknowns.add(point)
        }
        toProcess.addAll(listOfNotNull(point.up(), point.down(maxY), point.left(), point.right(maxX)).filter {
            getState(it) == null && unknowns.contains(it).not()
        })
    }

    private fun findPipe(): Pair<MutableSet<Point>, Char> {
//        println(start)
//        println(input.joinToString("\n"))
        val traversed = mutableSetOf(start)
        var currentPoints = mutableSetOf<Pair<Point, Direction>>()
        val left = start.left()
        var l = false
        var r = false
        var u = false
        var d = false
        if (left != null && this[left] in listOf('─', '└', '┌')) {
            currentPoints.add(left to LEFT)
            l = true
        }
        val right = start.right(this.maxX)
        if (right != null && this[right] in listOf('─', '┘', '┐')) {
            currentPoints.add(right to RIGHT)
            r = true
        }
        val up = start.up()
        if (up != null && this[up] in listOf('│', '┌', '┐')) {
            currentPoints.add(up to UP)
            u = true
        }
        val down = start.down(this.maxY)
        if (down != null && this[down] in listOf('│', '└', '┘')) {
            currentPoints.add(down to DOWN)
            d = true
        }

        val s = when {
            l && r -> '─'
            l && d -> '┐'
            l && u -> '┘'
            u && d -> '│'
            u && r -> '└'
            r && d -> '┌'
            else -> throw Exception("Invalid start")
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
        return traversed to s
    }

    fun prettyPrint() {
        input.indices.forEach { y ->
            input[0].indices.forEach { x ->
                print(prettyGet(Point(x, y)))
            }
            println("")
        }
    }

    fun prettyPrint2() {
        input.indices.forEach { y ->
            input[0].indices.forEach { x ->
                print(prettyGet2(Point(x, y)))
            }
            println("")
        }
    }


    fun isInside(point: Point): Boolean {
        return false
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
        return (maze.pipePoints.size.toLong() / 2)
        //7012
    }

    fun part2(input: List<String>): Long {
        val maze = parse(input)
        maze.prettyPrint2()
        val insidePoints = maze.insidePoints
        val filter = insidePoints.filter { maze.pipePoints.contains(it).not() }
        return filter.size.toLong()
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
    checkPart2("Day10_test4", 8L)
//    checkPart2("Day10_test5", 10L)

    val input = readInput("Day10")
    part1(input).println() // 7012
    part2(input).println() // 395
}

