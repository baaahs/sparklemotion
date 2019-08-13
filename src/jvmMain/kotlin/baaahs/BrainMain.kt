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
import kotlin.random.Random

fun main(args: Array<String>) {
    val sheepModel = SheepModel()
    sheepModel.load()

    val network = JvmNetwork()
    val brain = Brain(JvmNetwork.myAddress.toString(), network, object : BrainDisplay {
        override var id: String? = null
        override var surface: Surface? = null
        override var onReset: suspend () -> Unit = {}
        override fun haveLink(link: Network.Link) {
            println("Brain has a link!")
        }
    }, JvmPixelsDisplay(2000))

    val myPanel = if (Random.nextBoolean()) { sheepModel.allPanels.random()!! } else { null }
    println("I'll be ${myPanel?.name ?: "anonymous"}!")
    myPanel?.let { brain.forcedSurfaceName(myPanel.name) }

    GlobalScope.launch { brain.run() }

    doRunBlocking {
        delay(200000L)
    }
}

class JvmPixelsDisplay(pixelCount: Int) : Pixels {
    override val size = pixelCount
    private val colors = Array(size) { Color.BLACK }
    private val pixelsPerRow = ceil(sqrt(size.toFloat())).roundToInt()
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

                for (i in 0 until this@JvmPixelsDisplay.size) {
                    val row = i % pixelsPerRow
                    val col = i / pixelsPerRow

                    val pixWidth = width / pixelsPerCol
                    val pixHeight = height / pixelsPerRow
                    val pixGap = if (pixWidth > 3) 2 else if (pixWidth > 1) 1 else 0

                    val color = colors[i]
                    bufG.color = java.awt.Color(color.rgb)

                    bufG.fillRect(
                        col * pixWidth, row * pixHeight,
                        pixWidth - pixGap, pixHeight - pixGap
                    )
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

    override fun get(i: Int): Color = colors[i]

    override fun set(i: Int, color: Color) {
        colors[i] = color
    }

    override fun set(colors: Array<Color>) {
        val pixCount = min(colors.size, size)
        colors.copyInto(this.colors, 0, 0, pixCount)
    }

    override fun finishedFrame() {
        canvas.repaint()
    }
}