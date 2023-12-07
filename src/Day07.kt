import Face.J

enum class Face {
    A, K, Q, T, `9`, `8`, `7`, `6`, `5`, `4`, `3`, `2`, J
}

interface IsType {
    fun isType(hand: List<Face>): Boolean
}

enum class Type : IsType {
    FIVE_OF_A_KIND {
        override fun isType(hand: List<Face>): Boolean {
            val groupBy = hand.groupBy { it }
            return groupBy.size == 1 || (groupBy.size == 2 && groupBy.contains(J))
        }
    },
    FOUR_OF_A_KIND {
        override fun isType(hand: List<Face>): Boolean {
            val groupBy = hand.groupBy { it }
            return groupBy.values.any { it.size == 4 } ||
                    (groupBy.any { it.value.size == 3  && it.key != J } && groupBy.containsKey(J)) ||
                    (groupBy.any { it.value.size == 2  && it.key != J } && groupBy[J]?.size == 2) ||
                    groupBy[J]?.size == 3
        }
    },
    FULL_HOUSE {
        override fun isType(hand: List<Face>): Boolean {
            val groupBy = hand.groupBy { it }
            return (groupBy.size == 2 && groupBy.values.any { it.size in 2 .. 3 }) ||
                    (groupBy.size == 3 && groupBy[J]?.size == 1)
        }
    },
    THREE_OF_A_KIND {
        override fun isType(hand: List<Face>): Boolean {
            val groupBy = hand.groupBy { it }
            return groupBy.values.any { it.size == 3 } ||
                    groupBy.size == 4 && groupBy[J]?.size == 2 ||
                    groupBy.size == 4 && groupBy[J]?.size == 1
        }

    },
    TWO_PAIR {
        override fun isType(hand: List<Face>): Boolean {
            val groupBy = hand.groupBy { it }
            return groupBy.size == 3 && groupBy.count { it.value.size == 2 } == 2
        }
    },
    ONE_PAIR {
        override fun isType(hand: List<Face>): Boolean {
            val groupBy = hand.groupBy { it }
            return groupBy.values.any { it.size == 2 } || groupBy.containsKey(J)
        }
    },
    HIGH_CARD {
        override fun isType(hand: List<Face>) = true
    },
}

data class HandBid(val hand: List<Face>, val bid: Long, val type: Type = Type.entries.first {it.isType(hand)}) : Comparable<HandBid> {
    override fun compareTo(other: HandBid): Int {
        val typeCompare = type.compareTo(other.type)
        return if (typeCompare == 0) {
            hand.zip(other.hand).fold(0) { acc, (a, b) ->
                if (acc != 0) {
                    acc
                } else {
                    a.compareTo(b)
                }
            }
        } else {
            typeCompare
        }
    }
}
fun main() {

    fun parse(input: List<String>): List<HandBid> {
        return input.map {
            val (hand, bid) = it.split(" ")
            val handList = hand.map { Face.valueOf(it.toString()) }
            HandBid(handList, bid.toLong())
        }
    }

    fun part1(input: List<String>): Long {
        val parsed = parse(input)
        return parsed.sorted().reversed().
            mapIndexed {
                index, handBid ->
                (index + 1) * handBid.bid
            }.sum()
    }

    fun part2(input: List<String>): Long {
        val parsed = parse(input)
        return parsed.sorted().reversed().
        mapIndexed {
                index, handBid ->
            (index + 1) * handBid.bid
        }.sum()
    }

    val testInput = readInput("Day07_test")
//    val part1 = part1(testInput)
    val part2 = part2(testInput)
    val part1Expected = 6440L
//    check(part1 == part1Expected) {
//        "Expected $part1Expected, got $part1"
//    }
    val part2Expected = 5905L
    check(part2 == part2Expected) {
        "Expected $part2Expected, got $part2"
    }

    val input = readInput("Day07")
    part1(input).println()
    part2(input).println()
}
