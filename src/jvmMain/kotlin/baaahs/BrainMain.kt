package baaahs

import baaahs.net.JvmNetwork
import com.xenomachina.argparser.ArgParser
import com.xenomachina.argparser.default
import com.xenomachina.argparser.mainBody
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.awt.Canvas
import java.awt.Dimension
import java.awt.Frame
import java.awt.Graphics
import kotlin.math.ceil
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sqrt
import kotlin.random.Random

fun main(args: Array<String>) {
    mainBody(BrainMain::class.simpleName) {
        BrainMain(ArgParser(args).parseInto(BrainMain::Args)).run()
    }
}

class BrainMain(private val args: Args) {
    fun run() {
        val model = Pluggables.loadModel(args.model)

        val network = JvmNetwork()
        val brainId = args.brainId ?: JvmNetwork.myAddress.toString()
        val brain = Brain(brainId, network, JvmPixelsDisplay(2000), model)

        val mySurface = if (args.anonymous) {
            null
        } else if (args.surfaceName == null) {
            if (Random.nextBoolean()) model.allSurfaces.random() else null
        } else {
            model.allSurfaces.find { it.name == args.surfaceName }
                ?: throw IllegalArgumentException("unknown surface \"${args.surfaceName}")
        }
        println("I'll be ${mySurface?.name ?: "anonymous"}!")
        mySurface?.let { brain.forcedFixtureName(mySurface.name) }

        GlobalScope.launch { brain.run() }

        doRunBlocking {
            delay(200000L)
        }
    }

    class Args(parser: ArgParser) {
        val model by parser.storing("model").default(Pluggables.defaultModel)
        val brainId by parser.storing("brain ID").default<String?>(null)
        val surfaceName by parser.storing("surface name").default<String?>(null)
        val anonymous by parser.flagging("anonymous surface").default(false)
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