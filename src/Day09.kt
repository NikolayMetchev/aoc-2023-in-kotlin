fun main() {

    fun parse(input: List<String>): List<List<Long>> {
        return input.map { it.split(" ").map { it.toLong() } }
    }

    fun computeNext(sequence: List<Long>): Long =
        if (sequence.all { it == 0L }) {
            0L
        } else {
            val nextSequence = sequence.windowed(2).map { (a, b) -> b - a }
            sequence.last() + computeNext(nextSequence)
        }

    fun computeFirst(sequence: List<Long>): Long =
        if (sequence.all { it == 0L }) {
            0L
        } else {
            val nextSequence = sequence.windowed(2).map { (a, b) -> b - a }
            sequence.first() - computeFirst(nextSequence)
        }

    fun part1(input: List<String>): Long {
        val sequences = parse(input)
//        println(sequences)
        return sequences.sumOf { computeNext(it) }
    }

    fun part2(input: List<String>): Long {
        val sequences = parse(input)
        return sequences.sumOf { computeFirst(it) }
    }

    val part1 = part1(readInput("Day09_test"))
    val part1Expected = 114L
    check(part1 == part1Expected) {
        "Expected $part1Expected, got $part1"
    }
    val part2 = part2(readInput("Day09_test"))
    val part2Expected = 2L
    check(part2 == part2Expected) {
        "Expected $part2Expected, got $part2"
    }

    val input = readInput("Day09")
    part1(input).println()
    part2(input).println()
}
