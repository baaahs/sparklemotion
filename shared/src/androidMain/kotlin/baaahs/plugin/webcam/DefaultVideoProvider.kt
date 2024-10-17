package baaahs.plugin.webcam

import com.danielgergely.kgl.TextureResource

actual val DefaultVideoProvider: VideoProvider
    get() = WebcamCaptureVideoProvider


object WebcamCaptureVideoProvider : VideoProvider {
    private var textureResource: TextureResource? = null
    private var isOpen = false

//    private val webcam: Webcam? = run {
//        Webcam.setDriver(NativeDriver())
//        Webcam.getDefault()
//    }
//
//    private val noImage = BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB)

    override fun isReady(): Boolean = isOpen.also { ensureOpen() }

    override fun getTextureResource(): TextureResource {
        TODO()
//        ensureOpen()
//
//        val image: BufferedImage = webcam?.image ?: noImage
//        if (textureResource == null || textureResource!!.width != image.width || textureResource!!.height != image.height) {
//            val vals = ByteBuffer(image.width * image.height * 3)
//            textureResource = TextureResource(
//                width = image.width,
//                height = image.height,
//                format = GL11.GL_RGB,
//                type = GL11.GL_UNSIGNED_BYTE,
//                data = vals
//            )
//        }
//        textureResource!!.data.withJavaBuffer {
//            for (y in 0 until image.height) {
//                for (x in 0 until image.width) {
//                    val pixel = image.getRGB(x, y)
//                    val color = Color(pixel)
//                    val bufOffset = (y * image.width + x) * 3
//                    val buffer = it.array() as ByteBuffer
//                    buffer[bufOffset + 0] = color.redB
//                    buffer[bufOffset + 1] = color.greenB
//                    buffer[bufOffset + 2] = color.blueB
//                }
//            }
//        }
//
//
//        return textureResource!!
    }

    private fun ensureOpen() {
//        if (!isOpen) {
//            if (webcam != null)
//            isOpen = webcam.open(true)
//        }
    }
}
