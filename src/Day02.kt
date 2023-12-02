import kotlin.math.max

fun main() {
    val maxSize = mapOf("red" to 12, "green" to 13, "blue" to 14)
    fun parse(line: String): Pair<Int, MutableMap<String, Int>> {
        val (gameNo, games) = line.split(":")
        val gNo = gameNo.substring(5).toInt()
        val sets = games.split(";")
        val setCounts = mutableMapOf<String, Int>()
        sets.forEach { set ->
            set.split(",").forEach { draw ->
                val (count, colour) = draw.trim().split(" ")
                setCounts.merge(colour, count.toInt()) { a, b -> max(a, b) }
            }
        }
        return Pair(gNo, setCounts)
    }

    fun part1(input: List<String>): Int {
        var ans = 0
        input.forEach { line ->
            val (gNo, setCounts) = parse(line)
            var isValid = true
            setCounts.forEach { (colour, count) ->
                if (count > maxSize[colour]!!) {
                    isValid = false
                }
            }
            if (isValid) {
                ans += gNo
            }
        }
        return ans
    }

    fun part2(input: List<String>): Int {
        return input.sumOf { line ->
            val (gNo, setCounts) = parse(line)
            val x: Int = setCounts.values.fold(1) { a, b -> a * b }
            x
        }
    }

    val testInput = readInput("Day02_test")
    val part1 = part1(testInput)
    check(part1 == 8)

    val input = readInput("Day02")
    part1(input).println()
    part2(input).println()
}

enum class COLOUR {
    RED, GREEN, BLUE
}