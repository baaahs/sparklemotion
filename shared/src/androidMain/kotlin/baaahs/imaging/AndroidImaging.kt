package baaahs.imaging

actual fun imageFromDataUrl(dataUrl: String): Image {
    TODO()
//    return if (dataUrl.looksLikeGif()) {
//        GifImage(decodeBase64(dataUrl.substringAfter(",")))
//    } else {
//        val image = decodeBase64(dataUrl.substringAfter(","))
//            .let { ByteArrayInputStream(it) }
//            .let { BufferedInputStream(it) }
//            .let { ImageIO.read(it) }
//
//        return BufferedImageImage(image)
//    }
}

//class BufferedImageImage(
//    private val bufferedImage: BufferedImage
//) : Image {
//    override val width: Int
//        get() = bufferedImage.width
//    override val height: Int
//        get() = bufferedImage.width
//
//    override fun toBitmap(): Bitmap {
//        return BufferedImageBitmap(bufferedImage)
//    }
//}

actual fun createWritableBitmap(width: Int, height: Int): Bitmap {
    TODO("createWritableBitmap() not implemented!")
}