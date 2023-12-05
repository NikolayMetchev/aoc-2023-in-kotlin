import kotlin.math.pow

fun main() {

    fun parse(line: String): Pair<List<Long>, List<Long>> {
        val (winningNumbers, numbers) = line.substringAfter(": ").split(" | ")
        val w = winningNumbers.split(" ").filter { it.isNotEmpty() }.map { it.trim().toLong() }
        val n = numbers.split(" ").filter { it.isNotEmpty() }.map { it.trim().toLong() }
        return Pair(w, n)
    }

    fun part1(input: List<String>): Int {
        return input.sumOf { line ->
            val (winningNumbers, numbers) = parse(line)
            val numberOfWinningNumbers: Int = numbers.count(winningNumbers::contains)
            2.0.pow((numberOfWinningNumbers - 1).toDouble()).toInt()
        }
    }

    fun part2(input: List<String>): Int {
        val counts = Array(input.size) { 1 }
        input.forEachIndexed { index, line ->
            val (winningNumbers, numbers) = parse(line)
            val numberOfWinningNumbers: Int = numbers.count(winningNumbers::contains)
            (1 .. numberOfWinningNumbers).forEach { i ->
                counts[index + i] += counts[index]
            }
        }
        return counts.sum()
    }

    val testInput = readInput("Day04_test")
    val part1 = part1(testInput)
    val part2 = part2(testInput)
    check(part1 == 13) {
        "Expected 13, got $part1"
    }
    check(part2 == 30) {
        "Expected 30, got $part2"
    }

    val input = readInput("Day04")
    part1(input).println()
    part2(input).println()
}
