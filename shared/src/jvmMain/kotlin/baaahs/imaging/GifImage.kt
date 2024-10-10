package baaahs.imaging

import baaahs.util.Clock
import baaahs.util.SystemClock
import com.madgag.gif.fmsware.GifDecoder
import java.io.ByteArrayInputStream

class GifImage(data: ByteArray, clock: Clock = SystemClock) : Image {
    val decoder = GifDecoder().apply {
        read(ByteArrayInputStream(data))
    }
    private val frameIndices = 0 until decoder.frameCount
    private val frames = frameIndices.map { i ->
        decoder.getFrame(i)
    }
    private val animator = Animator(frameIndices.map { decoder.getDelay(it) }, clock)

    override val width: Int
        get() = frames[0].width
    override val height: Int
        get() = frames[0].height

    private var recentFrame: Int = -1

    override fun toBitmap(): Bitmap {
        val frameIndex = animator.getCurrentFrame()
        recentFrame = frameIndex

        return BufferedImageBitmap(frames[frameIndex])
    }

    override fun hasChanged(): Boolean {
        val currentFrame = animator.getCurrentFrame()
        return currentFrame != recentFrame
    }
}
