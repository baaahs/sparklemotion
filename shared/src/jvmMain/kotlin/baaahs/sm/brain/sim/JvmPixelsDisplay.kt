package baaahs.sm.brain.sim

import baaahs.Color
import baaahs.sm.brain.proto.Pixels
import com.sun.java.accessibility.util.SwingEventMonitor.addChangeListener
import java.awt.BorderLayout
import java.awt.Canvas
import java.awt.Component
import java.awt.Dimension
import java.awt.Graphics
import java.awt.GridLayout
import javax.swing.BorderFactory
import javax.swing.Box
import javax.swing.BoxLayout
import javax.swing.JCheckBox
import javax.swing.JComboBox
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.JList
import javax.swing.JPanel
import javax.swing.JSpinner
import javax.swing.ListCellRenderer
import kotlin.math.min

class JvmPixelsDisplay(
    private var pixelLayout: PixelLayout
) {
    val pixels = PixelsDelegator()
    private val canvas: Canvas
    private var pixelCanvas: PixelCanvas
    private val controls: Controls

    init {
        val frame = JFrame("Pixels!").apply {
            defaultCloseOperation = JFrame.EXIT_ON_CLOSE
            this.layout = BorderLayout()
        }

        canvas = object : Canvas() {
            override fun paint(g: Graphics?) {
                with (pixelCanvas) { paintOnto(g ) }
            }
        }.apply {
            preferredSize = Dimension(300, 300)
            background = java.awt.Color.BLACK
            frame.add(this, BorderLayout.CENTER)
        }

        pixelCanvas = PixelCanvas(pixelLayout, canvas)

        controls = Controls().apply {
            frame.add(this, BorderLayout.SOUTH)
        }
        controls.update()

        frame.pack()
        frame.invalidate()
        frame.isVisible = true
    }

    fun updateLayout(block: (PixelLayout) -> PixelLayout) {
        this.pixelLayout = block(pixelLayout)
        pixelCanvas = PixelCanvas(pixelLayout, canvas)
        controls.update()
    }

    inner class Controls : JPanel() {
        val colsInput: JSpinner
        val rowsInput: JSpinner

        init {
            val controlsPanel = this
            this.layout = BoxLayout(this, BoxLayout.Y_AXIS)

            // Columns/rows buttons:
            val buttonPanel = JPanel().apply {
                this.layout = BoxLayout(this, BoxLayout.X_AXIS)
                controlsPanel.add(this)
            }
            buttonPanel.add(Box.createRigidArea(Dimension(10, 0)))

            JLabel("Columns: ").apply { buttonPanel.add(this) }
            colsInput = JSpinner().apply {
        //            horizontalAlignment = JTextField.RIGHT
                buttonPanel.add(this)
                model.addChangeListener {
                    updateLayout { it.copy(columns = model.value as Int) }
                }
            }

            buttonPanel.add(Box.createRigidArea(Dimension(10, 0)))

            JLabel("Rows: ").apply { buttonPanel.add(this) }
            rowsInput = JSpinner().apply {
        //            horizontalAlignment = JTextField.RIGHT
                buttonPanel.add(this)
                model.addChangeListener {
                    updateLayout { it.copy(rows = model.value as Int) }
                }
            }

            buttonPanel.add(Box.createRigidArea(Dimension(10, 0)))

            // Other stuff:
            JPanel().apply {
                this.layout = GridLayout(3, 2)
                border = BorderFactory.createEmptyBorder(0, 10, 0, 10)
                controlsPanel.add(this)

                add(JLabel("Direction:"))
                add(JComboBox<Direction>(Direction.entries.toTypedArray()).apply {
                    this.selectedItem = pixelLayout.direction
                    renderer = RenderableRenderer()
                    addChangeListener { e -> updateLayout { it.copy(direction = selectedItem as Direction) } }
                })

                add(JLabel("Pixel Zero at:"))
                add(JComboBox<PixelZeroLocation>(PixelZeroLocation.entries.toTypedArray()).apply {
                    this.selectedItem = pixelLayout.pixelZeroAt
                    renderer = RenderableRenderer()
                    addChangeListener { e -> updateLayout { it.copy(pixelZeroAt = selectedItem as PixelZeroLocation) } }
                })

                add(JLabel("Zig zag?"))
                add(JCheckBox().apply {
                    this.isSelected = pixelLayout.zigZag
                    addChangeListener { e -> updateLayout { it.copy(zigZag = this.isSelected) } }
                })
            }

        }

        fun update() {
            colsInput.model.value = pixelLayout.columns
            rowsInput.model.value = pixelLayout.rows
        }
    }

    inner class PixelsDelegator : Pixels {
        override val size: Int
            get() = pixelCanvas.pixels.size

        override val indices: IntRange
            get() = pixelCanvas.pixels.indices

        override fun get(i: Int): Color =
            pixelCanvas.pixels[i]

        override fun set(i: Int, color: Color) {
            pixelCanvas.pixels[i] = color
        }

        override fun set(colors: Array<Color>) {
            pixelCanvas.pixels.set(colors)
        }

        override fun finishedFrame() =
            pixelCanvas.pixels.finishedFrame()

        override fun iterator(): Iterator<Color> =
            pixelCanvas.pixels.iterator()
    }

    inner class PixelCanvas(
        private val pixelLayout: PixelLayout,
        private val canvas: Canvas
    ) {
        private val pixelColors = Array(pixelLayout.pixelCount) { Color.Companion.BLACK }
        private val pixelsPerRow = pixelLayout.rows
        private val pixelsPerCol = pixelLayout.columns

        val pixels = object : Pixels {
            override val size: Int
                get() = pixelLayout.pixelCount

            override fun get(i: Int): Color = pixelColors[i]

            override fun set(i: Int, color: Color) {
                pixelColors[i] = color
            }

            override fun set(colors: Array<Color>) {
                val pixCount = min(colors.size, size)
                colors.copyInto(this@PixelCanvas.pixelColors, 0, 0, pixCount)
            }

            override fun finishedFrame() {
                canvas.repaint()
            }
        }

        fun Canvas.paintOnto(g: Graphics?) {
            g?.apply {
                val pixWidth = width / pixelsPerCol
                val pixHeight = height / pixelsPerRow
                val pixGap = if (pixWidth > 3) 2 else if (pixWidth > 1) 1 else 0

                val doubleBuffer = createImage(width, height)
                val bufG: Graphics = doubleBuffer.graphics
                bufG.color = java.awt.Color.BLACK
                bufG.clearRect(0, 0, width, height)

                for (i in 0 until pixelLayout.pixelCount) {
                    bufG.color = java.awt.Color(pixelColors[i].rgb)

                    val (row, col) = pixelLayout.gridPositionOf(i)
                    val pixX = col * pixWidth
                    val pixY = row * pixHeight
                    bufG.fillRect(
                        pixX, pixY,
                        pixWidth - pixGap, pixHeight - pixGap
                    )
                }

                // Hint where pixel 0 is.
                run {
                    val (row, col) = pixelLayout.gridPositionOf(0)
                    val pixX = col * pixWidth
                    val pixY = row * pixHeight
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

                g.drawImage(doubleBuffer, 0, 0, canvas)
            }
        }
    }

    data class PixelLayout(
        val columns: Int,
        val rows: Int,
        val direction: Direction = Direction.COLUMNS_THEN_ROWS,
        val zigZag: Boolean = false,
        val pixelZeroAt: PixelZeroLocation = PixelZeroLocation.TOP_LEFT
    ) {
        val pixelCount = columns * rows

        fun gridPositionOf(i: Int): Pair<Int, Int> {
            var row: Int
            var col: Int

            when (direction) {
                Direction.COLUMNS_THEN_ROWS -> {
                    row = i % rows
                    col = i / rows

                    if (zigZag && col % 2 == 1) row = rows - row - 1
                }

                Direction.ROWS_THEN_COLUMNS -> {
                    col = i % columns
                    row = i / columns

                    if (zigZag && row % 2 == 1) col = columns - col - 1
                }
            }

            pixelZeroAt.maybeFlip(row, col, this).let {
                row = it.first
                col = it.second
            }

            return row to col
        }
    }

    enum class Direction(
        override val humanReadable: String
    ) : Renderable {
        COLUMNS_THEN_ROWS("Columns then Rows"),
        ROWS_THEN_COLUMNS("Rows then Columns")
    }

    enum class PixelZeroLocation(
        override val humanReadable: String
    ) : Renderable {
        TOP_LEFT("Top Left") {
            override fun maybeFlip(row: Int, column: Int, pixelLayout: PixelLayout) =
                row to column
        },
        TOP_RIGHT("Top Right") {
            override fun maybeFlip(row: Int, column: Int, pixelLayout: PixelLayout) =
                row to (pixelLayout.columns - column - 1)
        },
        BOTTOM_LEFT("Bottom Left") {
            override fun maybeFlip(row: Int, column: Int, pixelLayout: PixelLayout) =
                (pixelLayout.rows - row - 1) to column
        },
        BOTTOM_RIGHT("Bottom Right") {
            override fun maybeFlip(row: Int, column: Int, pixelLayout: PixelLayout) =
                (pixelLayout.rows - row - 1) to (pixelLayout.columns - column - 1)
        };

        abstract fun maybeFlip(row: Int, column: Int, pixelLayout: PixelLayout): Pair<Int, Int>
    }

    interface Renderable {
        val humanReadable: String
    }

    class RenderableRenderer<T : Renderable> : ListCellRenderer<T> {
        override fun getListCellRendererComponent(
            list: JList<out T?>?,
            value: T?,
            index: Int,
            isSelected: Boolean,
            cellHasFocus: Boolean
        ): Component? {
            return JLabel(value?.humanReadable ?: "???")
        }
    }
}