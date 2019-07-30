package baaahs.mapper

import baaahs.Mapper
import baaahs.MediaDevices
import baaahs.imaging.Bitmap
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
        fun findChanges(
            newBitmap: Bitmap,
            baseBitmap: Bitmap,
            deltaBitmap: Bitmap,
            detector: Mapper.Detector,
            withinRegion: MediaDevices.Region = MediaDevices.Region.containing(baseBitmap)
        ): MediaDevices.Region {
            deltaBitmap.copyFrom(baseBitmap)
            deltaBitmap.subtract(newBitmap)

            val changeRegion: MediaDevices.Region = detectChangeRegion(
                deltaBitmap,
                detector,
                withinRegion
            )

            println("changeRegion = $changeRegion ${changeRegion.width} ${changeRegion.height}")
            return changeRegion
        }

        @UseExperimental(ExperimentalUnsignedTypes::class)
        private fun detectChangeRegion(
            deltaBitmap: Bitmap,
            detector: Mapper.Detector,
            withinRegion: MediaDevices.Region = MediaDevices.Region.containing(deltaBitmap)
        ): MediaDevices.Region {
            var changeRegion: MediaDevices.Region = MediaDevices.Region.EMPTY

            val minChangeToDetect = 10f
            deltaBitmap.withData { data ->
                // First pass: get per-row and -column min and max diffs.
                val xMin = ShortArray(withinRegion.width) { 0xFF }
                val xMax = ShortArray(withinRegion.width) { 0x00 }
                val yMin = ShortArray(withinRegion.height) { 0xFF }
                val yMax = ShortArray(withinRegion.height) { 0x00 }

                for (y in withinRegion.yRange) {
                    for (x in withinRegion.xRange) {
                        val pixelByteIndex = (x + y * deltaBitmap.width) * 4
                        val pixDiff = data[pixelByteIndex + detector.rgbaIndex].toShort()

                        val rX = x - withinRegion.x0
                        val rY = y - withinRegion.y0
                        if (pixDiff < xMin[rX]) xMin[rX] = pixDiff
                        if (pixDiff > xMax[rX]) xMax[rX] = pixDiff
                        if (pixDiff < yMin[rY]) yMin[rY] = pixDiff
                        if (pixDiff > yMax[rY]) yMax[rY] = pixDiff
                    }
                }

//            println("xMin: ${xMin.joinToString("") { (it / 16).toString(16) }}")
//            println("xMax: ${xMax.joinToString("") { (it / 16).toString(16) }}")
//            println("yMin: ${yMin.joinToString("") { (it / 16).toString(16) }}")
//            println("yMax: ${yMax.joinToString("") { (it / 16).toString(16) }}")
//
//            println("xMin: ${xMin.min()} <-> ${xMin.max()}")
//            println("xMax: ${xMax.min()} <-> ${xMax.max()}")
//            println("yMin: ${yMin.min()} <-> ${yMin.max()}")
//            println("yMax: ${yMax.min()} <-> ${yMax.max()}")

//            println("xMax hist: ${xMax.map { it / 16 }.histogram(0 until 15).joinToString(" ") { it.toString(16) }}")
//            println("yMax hist: ${yMax.map { it / 16 }.histogram(0 until 15).joinToString(" ") { it.toString(16) }}")
                val xMinMin = xMin.min() ?: 0
                val xMinMax = xMin.max() ?: 0
                val xMaxMin = xMax.min() ?: 0
                val xMaxMax = xMax.max() ?: 0
                val yMinMin = yMin.min() ?: 0
                val yMinMax = yMin.max() ?: 0
                val yMaxMin = yMax.min() ?: 0
                val yMaxMax = yMax.max() ?: 0

                // Second pass: do we need this?
                var x0 = -1
                var y0 = -1
                var x1 = -1
                var y1 = -1

                val scale = max(minChangeToDetect, (xMaxMax - xMinMin).toFloat())
                val b255 = 255.toUByte()

                for (y in withinRegion.yRange) {
                    var yAnyDiff = false

                    for (x in withinRegion.xRange) {
                        val pixelByteIndex = (x + y * deltaBitmap.width) * 4
                        val pixDiff = data[pixelByteIndex + detector.rgbaIndex].toInt()
                        val scaledPixDiff = (pixDiff - xMinMin) / scale

                        if (scaledPixDiff > .5f) {
                            if (x0 == -1 || x0 > x) x0 = x
                            if (x > x1) x1 = x
                            yAnyDiff = true

//                            data[pixelByteIndex + 0] = b255
//                            data[pixelByteIndex + 1] = -1
//                            data[pixelByteIndex + 2] = -1
                        }
                    }

                    if (yAnyDiff) {
                        if (y0 == -1) y0 = y
                        y1 = y
                    }
                }
                changeRegion = MediaDevices.Region(x0, y0, x1, y1)
                true
            }
            return changeRegion
        }

        fun Collection<Int>.histogram(range: IntRange): IntArray {
            val hist = IntArray(range.last - range.first)
            forEach { i -> hist[i - range.first]++ }
            return hist
        }
    }
}
