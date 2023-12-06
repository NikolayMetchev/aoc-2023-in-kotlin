fun main() {

    fun parse(input: List<String>): Pair<List<Long>, List<Long>> {
        val time = input[0].substringAfter("Time: ").split(" ").mapNotNull { it.trim().toLongOrNull() }
        val distance = input[1].substringAfter("Distance: ").split(" ").mapNotNull { it.trim().toLongOrNull() }
        return time to distance
    }

    fun computeWinningCombos(time: Long, distance: Long) = (1..<time).count { x ->
        val y = time - x
        x * y > distance
    }

    fun part1(input: List<String>): Long {
        val (times, distances) = parse(input)
        // x + y == time
        // y * x >= distance
        // x + y == 7
        // x * Y >= 12
        // x >= 0
        // y >= 0
        val solutions = times.mapIndexed { index, time ->
            val time = times[index]
            val distance = distances[index]
            computeWinningCombos(time, distance)
        }
        return solutions.fold(1L) { acc, i -> acc * i }
    }

    fun part2(input: List<String>): Long {
        val time = input[0].substringAfter("Time: ").replace(" ", "").toLong()
        val distance = input[1].substringAfter("Distance: ").replace(" ", "").toLong()
        println("Time: $time, Distance: $distance")

        return computeWinningCombos(time, distance).toLong()
    }

    val testInput = readInput("Day06_test")
    val part1 = part1(testInput)
    val part2 = part2(testInput)
    check(part1 == 288L) {
        "Expected 288L, got $part1"
    }
    check(part2 == 71503L) {
        "Expected 46, got $part2"
    }

    val input = readInput("Day06")
    part1(input).println()
    part2(input).println()
}
