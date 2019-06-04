package baaahs

import baaahs.net.JvmNetwork
import baaahs.net.Network
import io.ktor.server.engine.ApplicationEngine
import io.ktor.server.engine.ApplicationEngineEnvironment
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.awt.Canvas
import java.awt.Dimension
import java.awt.Frame
import java.awt.Graphics
import java.util.concurrent.TimeUnit
import kotlin.math.ceil
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sqrt

fun main(args: Array<String>) {
    val network = JvmNetwork(notReallyAnHttpServer())
    val brain = Brain(JvmNetwork.myAddress.toString(), network, object: BrainDisplay {
        override fun haveLink(link: Network.Link) {
            println("Brain has a link!")
        }
    }, JvmPixelsDisplay(2000))

    brain.forcedSurfaceName("33R")

    GlobalScope.launch { brain.run() }

    doRunBlocking {
        delay(200000L)
    }
}

class JvmPixelsDisplay(pixelCount: Int) : Pixels {
    override val count = pixelCount
    private val colors = Array(count) { Color.BLACK }
    private val pixelsPerRow = ceil(sqrt(count.toFloat())).roundToInt()
    private val pixelsPerCol = pixelsPerRow

    private val frame = Frame("Pixels!")
    private val canvas = PanelCanvas()

    inner class PanelCanvas : Canvas() {
        override fun paint(g: Graphics?) {
            g?.apply {
                val doubleBuffer = createImage(width, height)
                val bufG: Graphics = doubleBuffer.graphics
                bufG.color = java.awt.Color.BLACK
                bufG.clearRect(0, 0, width, height)

                for (i in 0 until count) {
                    val row = i % pixelsPerRow
                    val col = i / pixelsPerRow

                    val pixWidth = width / pixelsPerCol
                    val pixHeight = height / pixelsPerRow
                    val pixGap = if (pixWidth > 3) 2 else if (pixWidth > 1) 1 else 0

                    val color = colors[i]
                    bufG.color = java.awt.Color(color.rgb)

                    bufG.fillRect(col * pixWidth, row * pixHeight,
                        pixWidth - pixGap, pixHeight - pixGap)
                }

                g.drawImage(doubleBuffer, 0, 0, this@PanelCanvas)
            }
        }
    }

    init {
        frame.size = Dimension(300, 300)
        frame.isVisible = true

        canvas.preferredSize = frame.size
        canvas.background = java.awt.Color.BLACK
        frame.add(canvas)
        frame.pack()
        frame.invalidate()
    }

    override fun set(colors: Array<Color>) {
        val pixCount = min(colors.size, count)
        colors.copyInto(this.colors, 0, 0, pixCount)
        canvas.repaint()
    }
}

private fun notReallyAnHttpServer(): ApplicationEngine {
    return object : ApplicationEngine {
        override val environment: ApplicationEngineEnvironment
            get() = TODO("FakeHttpServer.environment not implemented")

        override fun start(wait: Boolean): ApplicationEngine {
            TODO("FakeHttpServer.start not implemented")
        }

        override fun stop(gracePeriod: Long, timeout: Long, timeUnit: TimeUnit) {
            TODO("FakeHttpServer.stop not implemented")
        }
    }
}