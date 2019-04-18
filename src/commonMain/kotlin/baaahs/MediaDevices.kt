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
        fun subtract(other: MonoBitmap) {
            if (data.size != other.data.size) throw IllegalStateException("Bitmap sizes don't match")

            for (i in 0 until data.size) {
                data[i] = (data[i].toInt() - other.data[i].toInt()).toUByte()
            }
        }
    }
}
