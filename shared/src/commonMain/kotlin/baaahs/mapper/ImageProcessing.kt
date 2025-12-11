package baaahs.mapper

import baaahs.MediaDevices
import baaahs.imaging.Bitmap
import baaahs.imaging.Dimen
import kotlin.math.max

class ImageProcessing {

    class Histogram(val data: IntArray, val total: Int) {
        fun sumValues(range: IntRange): Int {
            var total = 0
            for (i in range) {
                total += data[i]
            }
            return total
        }
    }

    companion object {
        val rgbaPixelDetectionIndex = 1 // green

        fun channelHistogram(
            channel: Int,
            bitmap: Bitmap
        ): Histogram {
            val hist = IntArray(256) { 0 }
            bitmap.withData {
                val totalBytes = it.size
                for (i in channel until totalBytes step 4) {
                    hist[it[i].toInt() and 0xFF]++
                }
                false
            }
            return Histogram(hist, bitmap.width * bitmap.height)
        }

        /**
         * @param newBitmap Newly-captured bitmap from camera with some element lit.
         * @param baseBitmap Base bitmap from camera with all elements off.
         * @param deltaBitmap Bitmap to receive diff.
         * @param withinRegion Region within which to find changes.
         */
        fun diff(
            newBitmap: Bitmap,
            baseBitmap: Bitmap,
            deltaBitmap: Bitmap,
            maskBitmap: Bitmap? = null,
            withinRegion: MediaDevices.Region = MediaDevices.Region.containing(baseBitmap)
        ): Analysis {
            deltaBitmap.copyFrom(baseBitmap)
            deltaBitmap.subtract(newBitmap)
            if (maskBitmap != null) {
                deltaBitmap.darken(maskBitmap)
            }

            return analyze(deltaBitmap, withinRegion)
        }

        @OptIn(ExperimentalUnsignedTypes::class)
        fun pixels(
            bitmap: Bitmap,
            regionOfInterest: MediaDevices.Region = MediaDevices.Region.containing(bitmap),
            fn: (x: Int, y: Int, value: Int) -> Unit
        ) {
            bitmap.withData { data ->
                for (y in regionOfInterest.yRange) {
                    for (x in regionOfInterest.xRange) {
                        val pixelByteIndex = (x + y * bitmap.width) * 4
                        val pixValue = data[pixelByteIndex + rgbaPixelDetectionIndex]
                        fn(x, y, pixValue)
                    }
                }

                false
            }
        }

        @OptIn(ExperimentalUnsignedTypes::class)
        fun analyze(
            bitmap: Bitmap,
            regionOfInterest: MediaDevices.Region = MediaDevices.Region.containing(bitmap)
        ): Analysis {
            val hist = IntArray(256) { 0 }
            val xMin = ShortArray(bitmap.width) { if (regionOfInterest.xRange.contains(it)) 0xFF else 0 }
            val xMax = ShortArray(bitmap.width) { 0x00 }
            val yMin = ShortArray(bitmap.height) { if (regionOfInterest.yRange.contains(it)) 0xFF else 0 }
            val yMax = ShortArray(bitmap.height) { 0x00 }

            bitmap.withData { data ->
                // Get histogram and per-row and -column min and max diffs.

                for (y in regionOfInterest.yRange) {
                    for (x in regionOfInterest.xRange) {
                        val pixelByteIndex = (x + y * bitmap.width) * 4
                        val pixValue = data[pixelByteIndex + rgbaPixelDetectionIndex].toShort()

                        if (pixValue < xMin[x]) xMin[x] = pixValue
                        if (pixValue > xMax[x]) xMax[x] = pixValue
                        if (pixValue < yMin[y]) yMin[y] = pixValue
                        if (pixValue > yMax[y]) yMax[y] = pixValue
                        hist[pixValue.toInt()]++
                    }
                }

                false
            }

            return Analysis(
                bitmap.dimen,
                regionOfInterest,
                Histogram(hist, bitmap.width * bitmap.height),
                xMin,
                xMax,
                yMin,
                yMax
            )
        }

        fun Collection<Int>.histogram(range: IntRange): IntArray {
            val hist = IntArray(range.last - range.first)
            forEach { i -> hist[i - range.first]++ }
            return hist
        }
    }

    class Analysis(
        val sourceDimen: Dimen,
        val regionOfInterest: MediaDevices.Region,
        val hist: Histogram,
        val xMin: ShortArray,
        val xMax: ShortArray,
        val yMin: ShortArray,
        val yMax: ShortArray
    ) {
        val minValue: Int by lazy { xMin.copyOfRange(regionOfInterest.xRange).minOrNull()!!.toInt() }
        val maxValue: Int by lazy { xMax.copyOfRange(regionOfInterest.xRange).maxOrNull()!!.toInt() }

        val minChangeToDetect = 10f
        val scale: Float by lazy { max(minChangeToDetect, (maxValue - minValue).toFloat()) }
        fun thresholdValueFor(threshold: Float) = (threshold * scale).toInt().toShort() + minValue

        fun detectChangeRegion(
            threshold: Float
        ): MediaDevices.Region {
//            val b255 = 255.toUByte()

            val thresholdValue = thresholdValueFor(threshold)
            val minX = xMax.indexOfFirst { rowMaxValue -> rowMaxValue >= thresholdValue }
            val minY = yMax.indexOfFirst { colMaxValue -> colMaxValue >= thresholdValue }
            val maxX = xMax.indexOfLast { rowMaxValue -> rowMaxValue >= thresholdValue }
            val maxY = yMax.indexOfLast { colMaxValue -> colMaxValue >= thresholdValue }
            return MediaDevices.Region(minX, minY, maxX + 1, maxY + 1, sourceDimen)
        }

        private fun ShortArray.copyOfRange(intRange: IntRange): ShortArray {
            return copyOfRange(intRange.first, intRange.last)
        }

        fun hasBrightSpots(): Boolean {
            this.hist.data.reduce { acc, i ->
                if (i - acc > 3) { // one or two brighter pixels don't count
                    return true
                }
                i
            }
            return false
        }
    }
}

