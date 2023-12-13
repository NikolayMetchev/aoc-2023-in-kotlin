data class SpringData(val specs: String, val springLengths: List<Int>) {
    fun expand(): SpringData {
        val newSpringLengths = mutableListOf<Int>()
        val newString = buildString {
            repeat(5) {
                append(specs)
                if (it < 4) {
                    append("?")
                }
                newSpringLengths.addAll(springLengths)
            }
        }
        return SpringData(newString, newSpringLengths)
    }
}

val invalidEnds = listOf('?', '#')

fun main() {

    fun parse(input: List<String>): List<SpringData> {
        return input.map {
            val (left, right) = it.split(" ")
            val springLengths = right.split(",").map { it.toInt() }
            SpringData(left, springLengths)
        }
    }

    fun computeArrangements(
        spec: String,
        startingIndex: Int,
        startLengthsIndex: Int,
        springLengths: List<Int>,
        foundSpecs: MutableSet<String>
    ) {
        if (startLengthsIndex == springLengths.size) {
            val newSpec = spec.replace("?", ".")
//            println("adding spec = $newSpec")
            if (!newSpec.contains('#')) {
                foundSpecs.add(newSpec)
            }
            return
        }
        if (startingIndex >= spec.length) {
            return
        }
        if ((0 ..< startingIndex).any { spec[it] == '#' }) {
            return
        }
        val firstSize = springLengths[startLengthsIndex]
//        val rest = springLengths.drop(1)
//        val restSum = springLengths.sumOf {  }
        val intRange: IntRange = startLengthsIndex + 1..<springLengths.size
        val restSum = intRange.sumOf { springLengths[it] } + intRange.count() - 1
        val substring2 = spec.substring(startingIndex, spec.length)
//        println("Substring to check $substring2")
        val newSpecs =
            substring2.withIndex().windowed(firstSize).mapNotNull { substring: List<IndexedValue<Char>> ->
                val firstIndex = substring.first().index + startingIndex
                val lastIndex = substring.last().index + startingIndex

                if (lastIndex + restSum > spec.length) {
                    null
                } else if (firstIndex > 0 && spec[firstIndex - 1] == '#') {
                    null
                } else if (lastIndex < spec.length - 1 && spec[lastIndex + 1] == '#') {
                    null
                } else if (!substring.all {it.value in invalidEnds }) {
                    null
                } else {
//                println(
//                    "spec=$spec, startIndex=$startingIndex, first.index=$firstIndex, last.index=$lastIndex, size=$firstSize, rest=$rest, substring=${
//                        substring.joinToString(
//                            ""
//                        ) { it.value.toString() }
//                    }"
//                )
                    val newString = buildString {
                        val prevPrevIndex = firstIndex - 2
                        if (prevPrevIndex >= 0) {
                            val substring1 = spec.substring(0, prevPrevIndex + 1)
//                        println("appending preString=$substring1")
                            append(substring1)
                        }
                        val prevIndex = firstIndex - 1
                        if (prevIndex >= 0) {
                            val prevChar = spec[prevIndex]
                            if (prevChar == '?') {
//                            println("convert from ? previusIndex=.")
                                append(".")
                            } else {
//                            println("appending previusIndex=$prevChar")
                                append(prevChar)
                            }
                        }
//                    println("appending ${substring.size} Xs")
                        repeat(substring.size) {
                            append("X")
                        }

                        val nextIndex = lastIndex + 1
                        if (nextIndex <= spec.lastIndex) {
                            val nextChar = spec[nextIndex]
                            if (nextChar == '?') {
//                            println("convert from ? nextIndex=.")
                                append(".")
                            } else {
//                            println("appending nextIndex=$nextChar")
                                append(nextChar)
                            }
                        }
                        val nextNextIndex = nextIndex + 1
                        if (nextNextIndex <= spec.lastIndex) {
                            val postString = spec.substring(nextNextIndex, spec.length)
//                        println("appending poststring $postString")
                            append(postString)
                        }
                    }
                    newString to lastIndex + 2
                }
            }
//        println("spec = $spec, size=$firstSize, rest = $rest, newSpecs = $newSpecs")
        newSpecs.forEach {
            if (it.first.length != spec.length) {
                error("newSpecs.size != spec.length")
            }
//            println("${it} rest=$rest")
            computeArrangements(it.first, it.second, startLengthsIndex + 1, springLengths, foundSpecs)
        }
    }

    fun computeAnswer(springSpec: List<SpringData>) = springSpec.sumOf {
        val foundSpecs = mutableSetOf<String>()
        computeArrangements(it.specs, 0, 0, it.springLengths, foundSpecs = foundSpecs)
        val ans = foundSpecs.size.toLong()
//        println("${it.specs} foundSpecs = $foundSpecs, size=${it.springLengths}: $ans")
        println("${it.specs} size=${it.springLengths}: $ans")
        ans
    }

    fun part1(input: List<String>): Long {
        val springSpec = parse(input)
//        println(springSpec)
        return computeAnswer(springSpec)
    }

    fun part2(input: List<String>): Long {
        val springSpec = parse(input).map { it.expand() }
//        println(springSpec)
        return computeAnswer(springSpec)
    }

    fun checkPart1(input: String, expected: Long) {
        val part1 = part1(readInput(input))
        val part1Expected = expected
        check(part1 == part1Expected) {
            "Expected $part1Expected, got $part1"
        }
    }

    fun checkPart2(input: String, expected: Long) {
        val part2 = part2(readInput(input))
        val part2Expected = expected
        check(part2 == part2Expected) {
            "Expected $part2Expected, got $part2"
        }
    }

    checkPart1("Day12_test0", 1L)
    checkPart1("Day12_test1", 4L)
    checkPart1("Day12_test2", 1L)
    checkPart1("Day12_test3", 1L)
    checkPart1("Day12_test4", 4L)
    checkPart1("Day12_test5", 10L)
    checkPart1("Day12_test", 21L)
    checkPart1("Day12", 7732L)
    checkPart2("Day12_test", 525152)
//    checkPart2("Day12_test", 6)

    val input = readInput("Day12")
//    part1(input).println()
    part2(input).println()
}
