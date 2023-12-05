data class PuzzleMap(val dest: Long, val source: Long, val distance: Long) {
    fun map(n: Long?): Long? {
        n ?: return null
        return if (n in source..<source + distance) {
            dest + (n - source)
        } else {
            null
        }
    }
}

val mapNames = listOf(
    "seed-to-soil",
    "soil-to-fertilizer",
    "fertilizer-to-water",
    "water-to-light",
    "light-to-temperature",
    "temperature-to-humidity",
    "humidity-to-location"
)

fun main() {

    fun parse(input: List<String>): Pair<List<Long>, Map<String, List<PuzzleMap>>> {
        val puzzleMaps = mutableMapOf<String, MutableList<PuzzleMap>>()
        var seeds: List<Long> = emptyList()
        var currentMap: String = ""
        input.forEachIndexed { index, line ->
            when {
                index == 0 -> seeds = line.substringAfter(": ").split(" ").map { it.toLong() }
                line.isEmpty() -> {}
                line.endsWith(" map:") -> {
                    currentMap = line.substringBefore(" map:")
                }

                else -> {
                    val (dest, source, distance) = line.split(" ").map { it.toLong() }
                    val puzzleMap = PuzzleMap(dest, source, distance)
                    val list = puzzleMaps.computeIfAbsent(currentMap) { mutableListOf() }
                    list.add(puzzleMap)
                }
            }
        }
        return Pair(seeds, puzzleMaps)
    }

    fun map(n: Long, mapName: String, maps: Map<String, List<PuzzleMap>>): Long =
        maps[mapName]!!.firstNotNullOfOrNull { it.map(n) } ?: n

    fun maps(seed: Long, maps: Map<String, List<PuzzleMap>>): Long =
        mapNames.fold(seed) { acc: Long, mapName ->
            map(acc, mapName, maps)
        }

    fun part1(input: List<String>): Long {
        val (seeds, maps) = parse(input)
        return seeds.minOf { maps(it, maps) }
    }

    fun part2(input: List<String>): Long {
        val (seeds, maps) = parse(input)
        return seeds.windowed(2, 2).minOf { (start, size) ->
            println("Checking $start, $size: ")
            (start ..< start + size).minOf {
                maps(it, maps)
            }
        }
    }

    val testInput = readInput("Day05_test")
    val part1 = part1(testInput)
    val part2 = part2(testInput)
    check(part1 == 35L) {
        "Expected 35, got $part1"
    }
    check(part2 == 46L) {
        "Expected 46, got $part2"
    }

    val input = readInput("Day05")
    part1(input).println()
    part2(input).println()
}
