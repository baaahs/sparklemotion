package baaahs

interface MediaDevices {
    fun getCamera(width: Int, height: Int): Camera

    interface Camera {
        var onImage: (image: Image) -> Unit

        fun close()
    }

    interface Image {
        fun toMonoBitmap(): MonoBitmap
    }

    @ExperimentalUnsignedTypes
    class MonoBitmap(val width: Int, val height: Int, val data: UByteArray = UByteArray(width * height)) {
        fun copyFrom(other: MonoBitmap) {
            if (data.size != other.data.size) throw IllegalStateException("Bitmap sizes don't match")

            other.data.copyInto(data)
        }

        fun subtract(other: MonoBitmap): Region {
            if (data.size != other.data.size) throw IllegalStateException("Bitmap sizes don't match")

            var i = 0
            var x0 = -1
            var y0 = -1
            var x1 = -1
            var y1 = -1

            for (y in 0 until height) {
                var yAnyDiff = false

                for (x in 0 until width) {
                    val pixDiff = data[i].toInt() - other.data[i].toInt()
                    data[i] = pixDiff.toUByte()

                    if (pixDiff != 0) {
                        if (x0 == -1) x0 = x
                        if (x > x1) x1 = x
                        yAnyDiff = true
                    }

                    i++
                }

                if (yAnyDiff) {
                    if (y0 == -1) y0 = y
                    y1 = y
                }
            }
            return Region(x0, y0, x1, y1)
        }
    }

    data class Region(val x0: Int, val y0: Int, val x1: Int, val y1: Int) {
        val width = x1 - x0
        val height = x1 - x0
    }
}
