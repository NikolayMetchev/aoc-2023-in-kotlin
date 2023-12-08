tailrec fun traverse(directions: String, index: Int, currentNode: String, nodes:  Map<String, Pair<String, String>>, currentStep: Long): Long {
    if (currentNode == "ZZZ") {
        return currentStep
    }
    val (left, right) = nodes[currentNode]!!
    val currentDirection = directions[index]
    val nextIndex = (index + 1) % directions.length
    return when(currentDirection) {
        'L' -> return traverse(directions, nextIndex, left, nodes, currentStep + 1)
        'R' -> return traverse(directions, nextIndex, right, nodes, currentStep + 1)
        else -> throw RuntimeException("Invalid direction $currentDirection")
    }
}

tailrec fun traverse2(directions: String, index: Int, currentNode: String, nodes:  Map<String, Pair<String, String>>, currentStep: Long): Long {
    if (currentNode.endsWith("Z")) {
        return currentStep
    }
    val (left, right) = nodes[currentNode]!!
    val currentDirection = directions[index]
    val nextIndex = (index + 1) % directions.length
    return when(currentDirection) {
        'L' -> return traverse2(directions, nextIndex, left, nodes, currentStep + 1)
        'R' -> return traverse2(directions, nextIndex, right, nodes, currentStep + 1)
        else -> throw RuntimeException("Invalid direction $currentDirection")
    }
}

//tailrec fun traverse(directions: String, index: Int, currentNodes: List<String>, nodes:  Map<String, Pair<String, String>>, currentStep: Long): Long {
//    if (currentNodes.all { it.endsWith("Z") } ) {
//        return currentStep
//    }
//    val currentDirection = directions[index]
//    val nextNodes = currentNodes.map {
//        val (left, right) = nodes[it]!!
//        when(currentDirection) {
//            'L' -> left
//            'R' -> right
//            else -> throw RuntimeException("Invalid direction $currentDirection")
//        }
//    }
//    val nextIndex = (index + 1) % directions.length
//    return traverse(directions, nextIndex, nextNodes, nodes, currentStep + 1)
//}

fun lcm(a: Long, b: Long) = a * (b / gcd(a, b))

fun gcd(a: Long, b: Long): Long = when {
    a < b -> gcd(b, a)
    b == 0L -> a
    else -> gcd(b, a % b)
}

fun main() {

    fun parse(input: List<String>): Pair<String, Map<String, Pair<String, String>>> {
        val directions = input[0]
        val nodes = input.drop(2).associate {
            val nodeName = it.substring(0, 3)
            val (left, right) = it.substringAfter(" = (").substringBefore(")").split(",")
            nodeName to (left.trim() to right.trim())
        }
        return directions to nodes
    }

    fun part1(input: List<String>): Long {
        val (directions, nodes) = parse(input)
        return traverse(directions, 0, "AAA", nodes, 0)
    }

    fun findMatch(
        lengths: List<Long>,
    ): Long {
        var currentLengths = lengths.map { it }
        while (!currentLengths.all { currentLengths[0] == it }) {
            val min = currentLengths.min()
            currentLengths = currentLengths.mapIndexed { index, l ->
                if (l == min) {
                    l + lengths[index]
                } else {
                    l
                }
            }
        }
        return currentLengths[0]
    }

    fun part2(input: List<String>): Long {
        val (directions, nodes) = parse(input)
//        println(directions)
//        println(nodes)
        val startingNodes = nodes.keys.filter { it.endsWith("A") }
        val lengths: List<Long> = startingNodes.map {
            traverse2(directions, 0, it, nodes, 0)
        }
        println(lengths)
        return lengths.reduce(::lcm)
    }

    val part1 = part1(readInput("Day08_test"))
    val part1v2 = part1(readInput("Day08_test2"))
    val part1Expected = 2L
    val part1v2Expected = 6L
    check(part1 == part1Expected) {
        "Expected $part1Expected, got $part1"
    }
    check(part1v2 == part1v2Expected) {
        "Expected $part1Expected, got $part1"
    }
    val part2 = part2(readInput("Day08_test3"))
    val part2Expected = 6L
    check(part2 == part2Expected) {
        "Expected $part2Expected, got $part2"
    }

    val input = readInput("Day08")
    part1(input).println()
    part2(input).println()
}
