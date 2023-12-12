import kotlin.math.abs

data class SpaceMap(val input: List<String>) {
    val emptyRows = input.mapIndexedNotNull { index, row ->
        if (row.all { it == '.' }) {
            index
        } else {
            null
        }
    }
    val emptyCols = input[0].indices.filter { col ->
        input.all { it[col] == '.' }
    }

    operator fun get(x: Int, y: Int): Char? {
        if (x < 0 || y < 0 || y >= input.size || x >= input[y].length) {
            return null
        }
        return input[y][x]
    }

    fun findGalaxies() : List<Point> {
        val galaxies = mutableListOf<Point>()
        for (y in input.indices) {
            for (x in input[y].indices) {
                if (input[y][x] == '#') {
                    galaxies.add(Point(x, y))
                }
            }
        }
        return galaxies
    }

    fun expand(factor: Int = 2): SpaceMap {
        val expandedCols: List<String> = input.map { it.flatMapIndexed { index, c ->
            if (index in emptyCols) {
                MutableList(factor) { '.' }
            } else {
                listOf(c)
            }
        }.joinToString("") }
        val emptyRow = ".".repeat(expandedCols[0].length)
        val expanded = expandedCols.flatMapIndexed { index, row ->
            if (index in emptyRows) {
                MutableList(factor) { emptyRow }
            } else {
                listOf(row)
            }
        }
        return SpaceMap(expanded)
    }

    fun prettyPrint() {
        input.forEach { println(it) }
    }
}

fun main() {
    fun parse(input: List<String>): SpaceMap {
        return SpaceMap(input)
    }

    fun computeSum(galaxies: List<Point>): Long {
        var sum = 0L
        galaxies.indices.forEach { a ->
            ((a + 1)..<galaxies.size).forEach { b ->
                val galaxyA = galaxies[a]
                val galaxyB = galaxies[b]
                sum += abs(galaxyA.x - galaxyB.x) + abs(galaxyA.y - galaxyB.y)
            }
        }
        return sum
    }

    fun computeSum(parsed: SpaceMap): Long {
        val galaxies = parsed.findGalaxies()
        return computeSum(galaxies)
    }

    fun part1(input: List<String>): Long {
        val parsed = parse(input).expand()
        return computeSum(parsed)
    }

    fun part2(input: List<String>, factor: Int): Long {
        val parsed = parse(input)
        val findGalaxies = parsed.findGalaxies()
        val emptyCols = parsed.emptyCols
        val emptyRows = parsed.emptyRows
        val mappedPoints = findGalaxies.map {
            val x = it.x
            val y = it.y
            val newX = emptyCols.count { it < x } * (factor - 1) + x
            val newY = emptyRows.count { it < y } * (factor - 1) + y
            Point(newX, newY)

        }
        println("findGalaxies: $findGalaxies")
        println("-----")
        println("Mapped points: $mappedPoints")
        return computeSum(mappedPoints)
    }

    fun checkPart1(input: String, expected: Long) {
        val part1 = part1(readInput(input))
        val part1Expected = expected
        check(part1 == part1Expected) {
            "Expected $part1Expected, got $part1"
        }
    }

    fun checkPart2(input: String, factor: Int, expected: Long) {
        val part2 = part2(readInput(input), factor)
        val part2Expected = expected
        check(part2 == part2Expected) {
            "Expected $part2Expected, got $part2"
        }
    }
    checkPart1("Day11_test", 374L)
    checkPart2("Day11_test", 10, 1030)
    checkPart2("Day11_test", 100, 8410)

    val input = readInput("Day11")
    part1(input).println()
    part2(input, 1_000_000).println()
}

