import kotlinx.coroutines.*
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.random.Random
import kotlin.system.measureTimeMillis

data class Point(val features: List<Double>, val label: String)

fun euclideanDistance(p1: Point, p2: Point): Double {
    return sqrt(p1.features.zip(p2.features) { a, b -> (a - b).pow(10) }.sum())
}

fun knn(trainData: List<Point>, testData: Point, k: Int): String {
    val distances = trainData.map { point ->
        Pair(euclideanDistance(point, testData), point)
    }

    val sortedDistances = distances.sortedBy { it.first }

    val nearestNeighbors = sortedDistances.take(k).map { it.second }

    val labelCounts = nearestNeighbors.groupingBy { it.label }.eachCount()
    return labelCounts.maxByOrNull { it.value }?.key ?: "Unknown"
}

suspend fun knnParallelOptimized(trainData: List<Point>, testData: Point, k: Int, numThreads: Int): String = coroutineScope {
    val chunkSize = trainData.size / numThreads
    val deferredDistances = mutableListOf<Deferred<List<Pair<Double, Point>>>>()

    for (i in 0 until numThreads) {
        val start = i * chunkSize
        val end = if (i == numThreads - 1) trainData.size else (i + 1) * chunkSize
        deferredDistances.add(async {
            trainData.subList(start, end).map { point ->
                Pair(euclideanDistance(point, testData), point)
            }
        })
    }

    val distances = deferredDistances.awaitAll().flatten()

    val sortedDistances = distances.sortedBy { it.first }

    val nearestNeighbors = sortedDistances.take(k).map { it.second }

    val labelCounts = nearestNeighbors.groupingBy { it.label }.eachCount()
    return@coroutineScope labelCounts.maxByOrNull { it.value }?.key ?: "Unknown"
}

fun generateTrainData(numPoints: Int, numFeatures: Int, noiseFactor: Double): List<Point> {
    return List(numPoints) {
        val features = List(numFeatures) { Random.nextDouble() }
        val label = if (Random.nextDouble() > 0.5) "A" else "B"
        val noisyFeatures = features.map { it + Random.nextDouble(-noiseFactor, noiseFactor) }
        Point(noisyFeatures, label)
    }
}

fun main() = runBlocking {
    val numPoints = 100000
    val numFeatures = 50
    val noiseFactor = 0.05
    val k = 10
    val numThreads = 8

    val trainData = generateTrainData(numPoints, numFeatures, noiseFactor)

    val testData = Point(List(numFeatures) { Random.nextDouble() }, "")

    val standardTime = measureTimeMillis {
        val result = knn(trainData, testData, k)
        println("Standard: Predicted label = $result")
    }
    println("Standard time: $standardTime ms")

    val parallelTime = measureTimeMillis {
        val result = knnParallelOptimized(trainData, testData, k, numThreads)
        println("Parallel: Predicted label = $result")
    }
    println("Parallel time: $parallelTime ms")
}