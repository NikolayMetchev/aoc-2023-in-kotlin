import java.util.BitSet

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

    fun String.toBitSet() : BitSet {
        val bitSet = BitSet(this.length)
        this.forEachIndexed { index, c ->
            if (c == 'X') {
                bitSet.set(index)
            }
        }
        return bitSet
    }
    data class Key(val spec: String, val lengths: IntArray) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Key

            if (spec != other.spec) return false
            if (!lengths.contentEquals(other.lengths)) return false

            return true
        }

        override fun hashCode(): Int {
            var result = spec.hashCode()
            result = 31 * result + lengths.contentHashCode()
            return result
        }
    }

    fun computeArrangements(
        spec: String,
        startingIndex: Int,
        springLengthsIndex: Int,
        springLengths: IntArray,
        cache: MutableMap<Key, Long>,
    ) : Long {
        if (springLengthsIndex == springLengths.size) {
            val newSpec = spec.replace("?", ".")
//            println("adding spec = $newSpec")
            if (!newSpec.contains('#')) {
                return 1
            }
            return 0
        }
        if (startingIndex >= spec.length) {
            return 0
        }
        if (startingIndex >= spec.length) {
            return 0
        }
        if ((0 ..< startingIndex).any { spec[it] == '#' }) {
            return 0
        }
        val key = Key(spec.substring(startingIndex), springLengths.copyOfRange(springLengthsIndex, springLengths.lastIndex))
        val cached = cache[key]
        if (cached != null) {
            return cached
        }
        val firstSize = springLengths[springLengthsIndex]
//        val rest = springLengths.drop(1)
//        val restSum = springLengths.sumOf {  }
        val restRange: IntRange = springLengthsIndex + 1.. springLengths.lastIndex
        val restSum = restRange.sumOf { springLengths[it] } + (springLengths.lastIndex - springLengthsIndex)
        val substring2 = spec.substring(startingIndex, spec.length)
//        println("Substring to check $substring2")
        val newSpecs = mutableListOf<Pair<String, Int>>()

        val lastValidIndex = (spec.length - restSum).coerceAtMost(spec.length - firstSize)
        val validRange = startingIndex..lastValidIndex
        val firstHashIndex = validRange.firstOrNull() { spec[it] == '#' } ?: Int.MAX_VALUE
        val minFirstIndex = if (springLengthsIndex == springLengths.lastIndex) {
            spec.lastIndexOf('#') - firstSize + 1
        } else {
            0
        }
        for (firstIndex in startingIndex.coerceAtLeast(minFirstIndex)..lastValidIndex.coerceAtMost(firstHashIndex)) {
                val lastIndex = firstIndex + firstSize - 1
                if (firstIndex > 0 && spec[firstIndex - 1] == '#') {
                    continue
                } else if (lastIndex < spec.length - 1 && spec[lastIndex + 1] == '#') {
                    continue
                } else if (!(firstIndex ..< firstIndex + firstSize).all {spec[it] in invalidEnds }) {
                    continue
                } else {
//                println("spec=$spec, startIndex=$startingIndex, first.index=$firstIndex, last.index=$lastIndex, size=$firstSize")
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
//                        println("appending ${firstSize} Xs")
                        repeat(firstSize) {
                            append("X")
                        }

                        val nextIndex = lastIndex + 1
                        if (nextIndex <= spec.lastIndex) {
                            val nextChar = spec[nextIndex]
                            if (nextChar == '?') {
//                            println("convert from $nextChar nextIndex=$nextIndex")
                                append(".")
                            } else {
//                            println("appending nextIndex=$nextChar, nextIndex=$nextIndex")
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
                    newSpecs.add(newString to lastIndex + 2)
                }
            }
//        println("spec = $spec, size=$firstSize, rest = $rest, newSpecs = $newSpecs")
        val sumOf = newSpecs.sumOf {
            if (it.first.length != spec.length) {
                error("newSpecs.size != spec.length")
            }
//            println("${it} rest=$rest")
            computeArrangements(it.first, it.second, springLengthsIndex + 1, springLengths, cache)
        }
        cache[key] = sumOf
        return sumOf
    }

    fun computeAnswer(springSpec: List<SpringData>) = springSpec.sumOf {
        val cache = HashMap<Key, Long>()
        val ans  = computeArrangements(it.specs, 0, 0, it.springLengths.toIntArray(), cache)
//        println("${it.specs} foundSpecs = $foundSpecs, size=${it.springLengths}: $ans")
        println("${it.specs} size=${it.springLengths}: $ans. cacheSize=${cache.size}")
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

//    checkPart1("Day12_test0", 1L)
    checkPart1("Day12_test1", 4L)
    checkPart1("Day12_test2", 1L)
    checkPart1("Day12_test3", 1L)
    checkPart1("Day12_test4", 4L)
    checkPart1("Day12_test5", 10L)
    checkPart1("Day12_test", 21L)
    checkPart1("Day12", 7732L)
    checkPart2("Day12_test", 525152L)
//    checkPart2("Day12_test", 6)

    val input = readInput("Day12")
//    part1(input).println()
    part2(input).println()
}
