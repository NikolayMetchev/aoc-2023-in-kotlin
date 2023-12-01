import kotlin.math.max
import kotlin.math.min

fun main() {
    val digitsMap = mapOf(
        "one" to 1,
        "two" to 2,
        "three" to 3,
        "four" to 4,
        "five" to 5,
        "six" to 6,
        "seven" to 7,
        "eight" to 8,
        "nine" to 9,
    )

    fun part1(input: List<String>): Int {
        val ans = input.map {
            it.filter { it.isDigit() }
        }.sumOf {
            "${it[0]}${it[it.length - 1]}".toInt()
        }

        return ans
    }

    // find all occurrences of a substring in a string
    fun String.findFirstAndLast(substring: String): Pair<Int, Int>? {
        val ans = mutableListOf<Int>()
        var i = 0
        while (i < this.length) {
            val j = this.indexOf(substring, i)
            if (j == -1) break
            ans.add(j)
            i = j + 1
        }
        return if (ans.isNotEmpty()) ans[0] to ans[ans.size - 1] else null
    }

    fun part2(input: List<String>): Int {


        return input.sumOf { inp ->
            var minDigit: Int? = null
            var minDigitIndex: Int = Int.MAX_VALUE
            var maxDigit: Int? = null
            var maxDigitIndex: Int = -1
            digitsMap.forEach {
                val (digitStr, digit) = it
                val findFirstAndLast = inp.findFirstAndLast(digitStr)
                val (first, last) = findFirstAndLast?.first to findFirstAndLast?.second
                val findFirstAndLast1 = inp.findFirstAndLast(digit.toString())
                val (first2, last2) = findFirstAndLast1?.first to findFirstAndLast1?.second
                val min = nullMin(first, first2)
                if (min != null && min < minDigitIndex) {
                    minDigit = digit
                    minDigitIndex = min
                }
                val max = nullMax(last, last2)
                if (max != null && max > maxDigitIndex) {
                    maxDigit = digit
                    maxDigitIndex = max
                }
            }
            val str = "${minDigit}${maxDigit}"
            println("$inp=$str")
                str.toInt()
        }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day01_test")
//    val part1 = part2(testInput)
//    println(part1)
//    check(part1 == 281)
//    println(part1)

    val input = readInput("Day01")
//    part1(input).println()
    part2(input).println()
}

fun nullMin(a: Int?, b: Int?): Int? {
    if (a == null) return b
    if (b == null) return a
    return min(a, b)
}

fun nullMax(a: Int?, b: Int?): Int? {
    if (a == null) return b
    if (b == null) return a
    return max(a, b)
}
