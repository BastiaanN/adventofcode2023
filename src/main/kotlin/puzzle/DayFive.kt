package puzzle

import java.io.File
import java.util.concurrent.CompletableFuture

class DayFive : Puzzle {

    val almanac = File("src/main/resources/DayFive-Almanac.txt")

    var seedToSoil = mutableMapOf<LongRange, Long>()
    var soilToFertilizer = mutableMapOf<LongRange, Long>()
    var fertilizerToWater = mutableMapOf<LongRange, Long>()
    var waterToLight = mutableMapOf<LongRange, Long>()
    var lightToTemperature = mutableMapOf<LongRange, Long>()
    var temperatureToHumidity = mutableMapOf<LongRange, Long>()
    var humidityToLocation = mutableMapOf<LongRange, Long>()

    override fun solveFirst(): String {
        var almanacLines = almanac.readLines()
        val seeds = "\\d+".toRegex().findAll(almanacLines[0]).toList().map { matchResult -> matchResult.value }

        // Pre-fill seed map
        fillMaps(almanacLines)

        return findLowestLocationFromSeeds(seeds.map { s -> s.toLong() }).toString()
    }

    override fun solveSecond(): String {

        var almanacLines = almanac.readLines()

        val seedRanges = mutableListOf<LongRange>()
        for (matchResult in "(\\d+)\\s+(\\d+)".toRegex().findAll(almanacLines[0]).toList()) {
            var rangeStart = matchResult.groups[1]!!.value.toLong()
            val rangeEnd = rangeStart + matchResult.groups[2]!!.value.toLong()

            val offset=10000000
            while(rangeEnd - rangeStart > offset) {
                val subrangeEnd = Math.min(rangeEnd, rangeStart + offset)
                seedRanges.add(LongRange(rangeStart, subrangeEnd))
                rangeStart = subrangeEnd
            }
        }

        // Pre-fill seed map
        fillMaps(almanacLines)

        var lowestLocation = Long.MAX_VALUE
        var futures = mutableListOf<CompletableFuture<Long>>()
        seedRanges.forEach { seedRange ->
            val future = CompletableFuture.supplyAsync {
                findLowestLocationFromSeeds(seedRange)
            }
            futures.add(future)
        }

        // Wait for all the futures.
        futures.forEach { future ->
            val futureValue = future.get()
            if(futureValue < lowestLocation) {
                lowestLocation = futureValue
            }
        }

        return lowestLocation.toString()
    }

    private fun fillMaps(almanacLines: List<String>) {
        var activeMap : MutableMap<LongRange, Long>? = null
        for (item in almanacLines) {
            // Switch map
            if (item.endsWith("map:")) {
                activeMap = when (item.split(' ').first()) {
                    "seed-to-soil" -> seedToSoil
                    "soil-to-fertilizer" -> soilToFertilizer
                    "fertilizer-to-water" -> fertilizerToWater
                    "water-to-light" -> waterToLight
                    "light-to-temperature" -> lightToTemperature
                    "temperature-to-humidity" -> temperatureToHumidity
                    "humidity-to-location" -> humidityToLocation
                    else -> throw IllegalArgumentException("Unknown map: ${item}")
                }
            } else if (item.length > 1 && item[0].isDigit() && activeMap != null) {
                val mapParts = item.split("\\s+".toRegex()).map { s -> s.toLong() }
                activeMap.put(mapParts[1].rangeUntil(mapParts[1] + mapParts[2]), mapParts[0])
            }
        }
    }

    private fun findLowestLocationFromSeeds(seeds : Iterable<Long>) : Long {
        return seeds
            .asSequence()
            .map { i -> getMappedValue(i, seedToSoil) }
            .map { i -> getMappedValue(i, soilToFertilizer) }
            .map { i -> getMappedValue(i, fertilizerToWater) }
            .map { i -> getMappedValue(i, waterToLight) }
            .map { i -> getMappedValue(i, lightToTemperature) }
            .map { i -> getMappedValue(i, temperatureToHumidity) }
            .map { i -> getMappedValue(i, humidityToLocation) }.min()
    }


    private fun getMappedValue(source: Long, destinationMap: Map<LongRange, Long>) : Long {
        val matchingRange = destinationMap.keys.find { key -> key.contains(source) }
        if(matchingRange == null) {
            return source
        } else {
            val offset = source - matchingRange.first
            return destinationMap[matchingRange]!!.plus(offset)
        }
    }
}