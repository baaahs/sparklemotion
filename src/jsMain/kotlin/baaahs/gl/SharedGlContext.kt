package baaahs.gl

import com.danielgergely.kgl.Kgl
import org.khronos.webgl.WebGLRenderingContext
import org.w3c.dom.DOMRect
import org.w3c.dom.HTMLElement

class SharedGlContext(
    private val glContext: GlBase.JsGlContext = GlBase.jsManager.createContext(trace = true)
) : GlBase.JsGlContext(glContext.canvas, glContext.kgl, glContext.glslVersion, glContext.webgl) {
    init {
        glContext.webgl.enable(WebGLRenderingContext.SCISSOR_TEST)
    }

    fun createSubContext(container: HTMLElement): GlContext {
        val subKgl: Kgl = SubKgl(container, kgl)
        return subContext(subKgl)
    }

//    inner class JsGlContext(
//        private val subKgl: Kgl
//    ) : GlBase.JsGlContext(glContext.canvas, subKgl, glContext.glslVersion, glContext.webgl) {
//
//    }

    inner class SubKgl(
        private val container: HTMLElement,
        private val delegate: Kgl
    ) : Kgl by delegate {
        override fun viewport(x: Int, y: Int, width: Int, height: Int) {
            // get element position relative to the page's viewport
            val rect = container.getBoundingClientRect()

            val sharedCanvas = glContext.canvas
            val sharedRect = sharedCanvas.getBoundingClientRect()

            if (sharedCanvas.width != sharedRect.width.toInt()) {
                sharedCanvas.width = sharedRect.width.toInt()
            }

            if (sharedCanvas.height != sharedRect.height.toInt()) {
                sharedCanvas.height = sharedRect.height.toInt()
            }

            // check if it's offscreen. If so skip it
            if (!rect.intersects(sharedRect)) return

            // set the viewport
            val subWidth: Int = (rect.right - rect.left).toInt()
            val subHeight: Int = (rect.bottom - rect.top).toInt()
            val subLeft: Int = (rect.left - sharedRect.left).toInt()
            val subBottom: Int = (sharedRect.bottom - rect.bottom).toInt()

            setViewport(subLeft, subBottom, subWidth, subHeight)
            glContext.webgl.scissor(subLeft, subBottom, subWidth, subHeight)
        }
    }

    fun DOMRect.intersects(other: DOMRect): Boolean {
        return !(other.left > right ||
                other.right < left ||
                other.top > bottom ||
                other.bottom < top)
    }
}
