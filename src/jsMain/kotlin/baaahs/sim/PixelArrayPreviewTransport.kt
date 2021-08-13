package baaahs.sim

import baaahs.Color
import baaahs.fixtures.Transport
import baaahs.io.ByteArrayReader
import baaahs.io.ByteArrayWriter
import baaahs.visualizer.VizPixels

class PixelArrayPreviewTransport(
    override val name: String,
    private val vizPixels: VizPixels
) : Transport {
    override fun deliverBytes(byteArray: ByteArray) {
        val buf = ByteArrayReader(byteArray)
        for (i in vizPixels.indices) {
            buf.offset = i * 3
            vizPixels[i] = Color.parseWithoutAlpha(buf)
        }
    }

    override fun deliverComponents(
        componentCount: Int,
        bytesPerComponent: Int,
        fn: (componentIndex: Int, buf: ByteArrayWriter) -> Unit
    ) {
        val buf = ByteArrayWriter(bytesPerComponent)
        for (componentIndex in 0 until componentCount) {
            buf.offset = 0
            fn(componentIndex, buf)

            vizPixels[componentIndex] = Color.parseWithoutAlpha(ByteArrayReader(buf.toBytes()))
        }
    }
}