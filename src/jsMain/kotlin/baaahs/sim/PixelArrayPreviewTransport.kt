package baaahs.sim

import baaahs.Color
import baaahs.fixtures.Transport
import baaahs.io.ByteArrayReader
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
}