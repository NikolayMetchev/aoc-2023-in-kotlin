
fun main() {
    fun parse(input: List<String>): Array<Array<Char>> {
        val size = input[0].length
        return Array(size) { i -> Array(size) { j -> input[i][j] } }
    }

    fun part1(input: List<String>): Int {
        var sum = 0
        val grid = parse(input)
        val size = grid[0].size
        fun isSymbol(row: Int, col: Int) = when {
            row < 0 || row >= size -> false
            col < 0 || col >= size -> false
            else -> grid[row][col] != '.' && !grid[row][col].isDigit()
        }

        fun process(row: Int, col: Int, sb: StringBuilder) {
            val prevColumn = col - sb.length - 1
            val nextColumn = col
            val prevRow = row - 1
            val nextRow = row + 1
            var isPart = false
            (prevColumn .. nextColumn).forEach { c ->
                if(isSymbol(prevRow,c)) {
                    isPart = true
                }
                if (isSymbol(nextRow,c)) {
                    isPart = true
                }
            }
            if (!isPart) {
                if (isSymbol(row, prevColumn)) {
                    isPart = true
                }
                if (isSymbol(row, nextColumn)) {
                    isPart = true
                }
            }
            if (isPart) {
                val toInt = sb.toString().toInt()
                println("Adding $toInt")
                sum += toInt
            } else {
                println("Skipping $sb")
            }
            sb.clear()
        }
        (0 ..< size).forEach {row ->
            val sb = StringBuilder(size)
            (0 ..< size).forEach { col ->
                val current = grid[row][col]
                when {
                    current.isDigit() -> sb.append(current)
                    sb.isNotEmpty() -> {
                        process(row, col, sb)
                    }
                    else -> sb.clear()
                }
            }
            if (sb.isNotEmpty()) {
                process(row, size, sb)
            }
        }

        return sum
    }

    fun part2(input: List<String>): Int {
        var sum = 0
        val grid = parse(input)
        val size = grid[0].size
        val stars = mutableMapOf<Pair<Int, Int>, Int>()

        fun isSymbol(row: Int, col: Int) = when {
            row < 0 || row >= size -> false
            col < 0 || col >= size -> false
            else -> grid[row][col] == '*'
        }

        fun process2(row: Int, col: Int, sb: StringBuilder) {
            val prevColumn = col - sb.length - 1
            val nextColumn = col
            val prevRow = row - 1
            val nextRow = row + 1
            fun x(row: Int, col: Int) {
                val toInt = sb.toString().toInt()
                val otherGear = stars[Pair(row, col)]
                if (otherGear != null) {
                    println("Adding $otherGear * $toInt")
                    sum += otherGear * toInt
                } else {
                    println("found $toInt")
                    stars[Pair(row, col)] = toInt
                }
                sb.clear()
            }
            (prevColumn .. nextColumn).forEach { c ->
                if(isSymbol(prevRow,c)) {
                    x(prevRow, c)
                    return
                }
                if (isSymbol(nextRow,c)) {
                    x(nextRow, c)
                    return
                }
            }
            if (isSymbol(row, prevColumn)) {
                x(row, prevColumn)
                return
            }
            if (isSymbol(row, nextColumn)) {
                x(row, nextColumn)
                return
            }
            println("Skipping $sb")
            sb.clear()
        }

        (0 ..< size).forEach {row ->
            val sb = StringBuilder(size)
            (0 ..< size).forEach { col ->
                val current = grid[row][col]
                when {
                    current.isDigit() -> sb.append(current)
                    sb.isNotEmpty() -> {
                        process2(row, col, sb)
                    }
                    else -> sb.clear()
                }
            }
            if (sb.isNotEmpty()) {
                process2(row, size, sb)
            }
        }

        return sum
    }

    val testInput = readInput("Day03_test")
//    val part1 = part1(testInput)
    val part2 = part2(testInput)
//    println(part1)
//    check(part1 == 4361)
    println(part2)
    check(part2 == 467835)

    val input = readInput("Day03")
//    part1(input).println()
    part2(input).println()
}
