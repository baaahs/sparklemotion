package baaahs.sm.brain.sim

import baaahs.Color
import baaahs.client.document.sceneStore
import baaahs.io.RealFs
import baaahs.model.Model
import baaahs.net.JvmNetwork
import baaahs.plugin.Plugins
import baaahs.sm.brain.proto.Pixels
import baaahs.util.SystemClock
import baaahs.util.globalLaunch
import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.default
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import java.awt.Canvas
import java.awt.Dimension
import java.awt.Frame
import java.awt.Graphics
import java.io.File
import kotlin.math.ceil
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sqrt
import kotlin.random.Random

suspend fun main(args: Array<String>) {
    val argParser = ArgParser(BrainMain::class.simpleName ?: "Brain")
    val brainArgs = BrainMain.Args(argParser)
    argParser.parse(args)
    BrainMain(brainArgs).run().join()
}

class BrainMain(private val args: Args) {
    fun run() = globalLaunch {
        val plugins = Plugins.safe(Plugins.dummyContext)
        val fs = RealFs("Files", File(".").toPath())
        val sceneFile = fs.resolve(args.scene ?: error("No scene specified."))
        val model = plugins.sceneStore.load(sceneFile)
            ?.open()?.model
            ?: error("No such scene file: \"$sceneFile\"")

        val network = JvmNetwork()
        val brainId = args.brainId ?: JvmNetwork.myAddress.toString()
        val brainSimulator = BrainSimulator(
            brainId, network, JvmPixelsDisplay(2000), SystemClock, CoroutineScope(Dispatchers.Main)
        )

        val mySurface = if (args.anonymous) {
            null
        } else if (args.entityName == null) {
            if (Random.nextBoolean())
                model.allEntities.filterIsInstance<Model.Surface>().random()
            else null
        } else {
            args.entityName?.let { model.findEntityByName(it) }
        }
        println("I'll be ${mySurface?.name ?: "anonymous"}!")
        mySurface?.let { brainSimulator.forcedFixtureName(mySurface.name) }

        brainSimulator.start()
    }

    class Args(parser: ArgParser) {
        val scene by parser.option(ArgType.String)

        val brainId by parser.option(ArgType.String, description = "brain ID")

        val entityName by parser.option(ArgType.String, description = "entity name")

        val anonymous by parser.option(ArgType.Boolean, description = "anonymous surface")
            .default(false)
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