package baaahs.sm.brain.sim

import baaahs.Color
import baaahs.sm.brain.proto.Pixels
import java.awt.Canvas
import java.awt.Dimension
import java.awt.Frame
import java.awt.Graphics
import kotlin.math.min

class JvmPixelsDisplay(layout: Layout) {
    private var layout: Layout = layout
        set(value) {
            if (value != field) {
                canvas = PanelCanvas(value)
                field = value
            }
        }
    private var canvas: PanelCanvas = PanelCanvas(layout)
    val pixels = PixelsDelegate()

    private val frame = Frame("Pixels!")

    init {
        frame.size = Dimension(300, 300)
        frame.isVisible = true

        canvas.preferredSize = frame.size
        canvas.background = java.awt.Color.BLACK
        frame.add(canvas)
        frame.pack()
        frame.invalidate()
    }

    inner class PixelsDelegate() : Pixels by canvas.pixels

    inner class PanelCanvas(
        val layout: Layout
    ) : Canvas() {
        private val pixelColors = Array(layout.pixelCount) { Color.Companion.BLACK }
        private val pixelsPerRow = layout.columns
        private val pixelsPerCol = layout.rows

        val pixels = object : Pixels {
            override val size: Int
                get() = layout.pixelCount

            override fun get(i: Int): Color = pixelColors[i]

            override fun set(i: Int, color: Color) {
                pixelColors[i] = color
            }

            override fun set(colors: Array<Color>) {
                val pixCount = min(colors.size, size)
                colors.copyInto(this@PanelCanvas.pixelColors, 0, 0, pixCount)
            }

            override fun finishedFrame() {
                canvas.repaint()
            }
        }

        override fun paint(g: Graphics?) {
            g?.apply {
                val pixWidth = width / pixelsPerCol
                val pixHeight = height / pixelsPerRow
                val pixGap = if (pixWidth > 3) 2 else if (pixWidth > 1) 1 else 0

                val doubleBuffer = createImage(width, height)
                val bufG: Graphics = doubleBuffer.graphics
                bufG.color = java.awt.Color.BLACK
                bufG.clearRect(0, 0, width, height)

                for (i in 0 until layout.pixelCount) {
                    val row = i % pixelsPerRow
                    val col = i / pixelsPerRow

                    bufG.color = java.awt.Color(pixelColors[i].rgb)

                    val pixX = col * pixWidth
                    val pixY = row * pixHeight
                    bufG.fillRect(
                        pixX, pixY,
                        pixWidth - pixGap, pixHeight - pixGap
                    )

                    // Hint where pixel 0 is.
                    if (i == 0) {
                        bufG.color = java.awt.Color.RED
                        bufG.drawRect(
                            pixX, pixY,
                            pixWidth - pixGap, pixHeight - pixGap
                        )

                        bufG.color = java.awt.Color.BLACK
                        bufG.drawRect(
                            pixX + 1, pixY + 1,
                            pixWidth - pixGap - 2, pixHeight - pixGap - 2
                        )
                    }
                }

                g.drawImage(doubleBuffer, 0, 0, this@PanelCanvas)
            }
        }
    }

    data class Layout(
        val columns: Int,
        val rows: Int,
        val direction: Direction = Direction.COLUMNS_THEN_ROWS,
        val zigZag: Boolean = false,
        val pixelZeroAt: Location = Location.TOP_LEFT
    ) {
        val pixelCount = columns * rows
    }

    enum class Direction {
        COLUMNS_THEN_ROWS,
        ROWS_THEN_COLUMNS
    }

    enum class Location {
        TOP_LEFT,
        TOP_RIGHT,
        BOTTOM_LEFT,
        BOTTOM_RIGHT
    }
}