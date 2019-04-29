package baaahs.imaging

public interface Image {
    val width: Int
    val height: Int

    fun toBitmap(): Bitmap
}

public interface Bitmap {
    val width: Int
    val height: Int

    fun drawImage(image: Image)

    fun drawImage(image: Image,
                  sX: Int, sY: Int, sWidth: Int, sHeight: Int,
                  dX: Int, dY: Int, dWidth: Int, dHeight: Int)

    fun copyFrom(other: Bitmap)

    fun subtract(other: Bitmap)

    fun withData(fn: (data: ByteArray) -> Boolean)
    fun asImage(): Image
}

expect class NativeBitmap(width: Int, height: Int) : Bitmap