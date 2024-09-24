package baaahs.gl

import com.danielgergely.kgl.Kgl
import web.geometry.DOMRect
import web.html.HTMLCanvasElement
import web.html.HTMLElement
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

class SharedGlContext(
    name: String,
    private val glContext: GlBase.JsGlContext = GlBase.jsManager.createContext("Shared GL Context")
) : GlBase.JsGlContext(name, glContext.canvas, glContext.kgl, glContext.glslVersion, glContext.webgl) {
    private val sharedCanvas = glContext.canvas
    private var sharedLastFrameTimestamp = 0.0
    private var sharedCanvasRect: Rect = sharedCanvas.getBoundingClientRect().asRect()
    private var subContextCount = 0

    init {
        glContext.webgl.enable(glContext.webgl.SCISSOR_TEST)
        updateVisibility()
    }

    fun createSubContext(name: String, container: HTMLElement): GlBase.JsGlContext =
        SubJsGlContext(name, SubKgl(container, kgl), this).also {
            subContextCount++
            updateVisibility()
        }

    override fun requestAnimationFrame(callback: (Double) -> Unit) {
        error("Don't call requestAnimationFrame() on a SharedGlContext!")
    }

    private fun maybeUpdateSharedCanvasRect(frameTimestamp: Double) {
        if (frameTimestamp != sharedLastFrameTimestamp) {
            sharedCanvasRect = sharedCanvas.getBoundingClientRect().asRect()
            sharedCanvas.resizeToMatch(sharedCanvasRect)
            sharedLastFrameTimestamp = frameTimestamp
        }
    }

    private fun releaseSubContext() {
        subContextCount--
        updateVisibility()
    }

    private fun updateVisibility() {
        if (subContextCount == 0) {
            sharedCanvas.style.display = "none"
        } else {
            sharedCanvas.style.removeProperty("display")
        }
    }

    inner class SubJsGlContext(
        name: String,
        private val subKgl: SubKgl,
        sharedGlContext: SharedGlContext
    ) : GlBase.JsGlContext(
        name, canvas, subKgl, glslVersion, webgl, checkForErrors, state
    ) {
        private val parentId = sharedGlContext.id
        override val allocatedContext: AllocatedContext
            get() = AllocatedContext(id, name, this::class, parentId)

        private var subLastFrameTimestamp = 0.0

        override val rasterOffset: RasterOffset
            get() = RasterOffset(
                (sharedCanvasRect.bottom - subKgl.containerRect.bottom) + 1,
                (subKgl.containerRect.left - sharedCanvasRect.left)
            )

        override fun requestAnimationFrame(callback: (Double) -> Unit) {
            super.requestAnimationFrame { timestamp ->
                if (timestamp != subLastFrameTimestamp) {
                    maybeUpdateSharedCanvasRect(timestamp)
                    subKgl.updateContainerRect()
                    subLastFrameTimestamp = timestamp
                }

                callback(timestamp)
            }
        }

        override fun release() {
            this@SharedGlContext.releaseSubContext()
        }

        override fun toString() = "${this::class.simpleName}#$id(${glContext})"
    }

    inner class SubKgl(
        private val container: HTMLElement,
        private val delegate: Kgl
    ) : Kgl by delegate {
        internal var containerRect = Rect(0, 0, 0, 0)
        internal var scissorRect = Rect(0, 0, 0, 0)

        init { calculateBounds() }

        internal fun updateContainerRect() {
            calculateBounds()
        }

        private fun calculateBounds() {
            var bounds = container.getBoundingClientRect().asRect()
            containerRect = bounds

            var el = container.parentElement
            while (el != null) {
                val parentEl = el.parentElement
                bounds = el.getBoundingClientRect().asRect()
                    .offsetBy(parentEl?.scrollTop?.toInt() ?: 0, parentEl?.scrollLeft?.toInt() ?: 0)
                    .intersectionWith(bounds)
                el = parentEl
                if (el == sharedCanvas.parentElement) break
            }
            scissorRect = bounds
        }

        override fun viewport(x: Int, y: Int, width: Int, height: Int) {
            val rect = containerRect
                .intersectionWith(sharedCanvasRect)
                .relativeToBottomLeftOf(sharedCanvasRect)
                .withWidthAndHeightNoLessThanZero()

            setViewport(rect.left, rect.bottom + 1, rect.width, max(rect.height - 1, 0))

            val scissor = scissorRect
                .relativeToBottomLeftOf(sharedCanvasRect)
                .withWidthAndHeightNoLessThanZero()
            glContext.webgl.scissor(scissor.left, scissor.bottom + 1, scissor.width, max(scissor.height - 1, 0))
        }
    }

    private fun DOMRect.asRect(): Rect = Rect(top, left, bottom, right)

    private fun HTMLCanvasElement.resizeToMatch(rect: Rect) {
        if (width != rect.width) width = rect.width
        if (height != rect.height) height = rect.height
    }

    data class Rect(
        val top: Int,
        val left: Int,
        val bottom: Int,
        val right: Int
    ) {
        constructor(
            top: Double, left: Double, bottom: Double, right: Double
        ): this(top.roundToInt(), left.roundToInt(), bottom.roundToInt(), right.roundToInt())

        val height: Int get() = bottom - top
        val width: Int get() = right - left

        fun isEmpty(): Boolean = top == bottom || left == right

        fun relativeToBottomLeftOf(other: Rect): Rect {
            val subLeft: Int = left - other.left
            val subBottom: Int = other.bottom - bottom

            return Rect(
                subBottom - (bottom - top),
                subLeft,
                subBottom,
                subLeft + right - left
            )
        }

        fun intersectionWith(other: Rect) =
            Rect(
                max(top, other.top),
                max(left, other.left),
                min(bottom, other.bottom),
                min(right, other.right)
            )

        fun offsetBy(offsetTop: Int, offsetLeft: Int) =
            Rect(
                top + offsetTop,
                left + offsetLeft,
                bottom + offsetTop,
                right + offsetLeft
            )

        fun withWidthAndHeightNoLessThanZero() =
            Rect(
                top,
                left,
                max(bottom, top),
                max(right, left)
            )
    }
}
