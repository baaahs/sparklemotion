package baaahs.visualizer

import baaahs.gl.GlBase
import com.danielgergely.kgl.*
import kotlinext.js.jsObject
import three.js.NearestFilter
import three.js.Vector4
import three.js.WebGLRenderTarget
import three.js.WebGLRenderer
import three_ext.getTexture
import kotlin.math.roundToInt

class ThreeJsGlContext(
    private val renderer: WebGLRenderer,
    baseContext: GlBase.JsGlContext
) : GlBase.JsGlContext(
    renderer.domElement, ThreeJsKgl(renderer, baseContext.kgl, baseContext.webgl),
    baseContext.glslVersion, baseContext.webgl,
    baseContext.checkForErrors
) {
    private var inContext = false

    override fun <T> runInContext(fn: () -> T): T {
        if (inContext) error("Already in context.")

        return super.runInContext {
            state.activeProgram = null

            restoreSubState()
            try {
                fn()
            } finally {
                (kgl as ThreeJsKgl).restoreState()
                inContext = false
            }
        }
    }

    override fun createTexture(): GlTexture {
        val renderTarget = WebGLRenderTarget(1, 1, jsObject {
            this.magFilter = NearestFilter
            this.minFilter = NearestFilter
        })
        renderer.setRenderTarget(renderTarget, 0, 0)
        renderer.setRenderTarget(null, 0, 0)
        return ThreeTexture(renderTarget)
    }

    inner class ThreeTexture(val renderTarget: WebGLRenderTarget) : GlTexture {
        override val texture: Texture
            get() = renderer.getTexture(renderTarget)

        override fun release() {
            TODO("not implemented")
        }
    }

    fun restoreSubState() {
        with(state.viewport) {
            if (isNotEmpty()) {
                kgl.viewport(get(0), get(1), get(2), get(3))
            }
        }

        state.activeFrameBuffer?.rebind()
        state.activeRenderBuffer?.rebind()
    }

    companion object {
        fun create(renderer: WebGLRenderer): ThreeJsGlContext {
            val canvas = renderer.domElement
            val glContext = GlBase.jsManager.createContext(canvas, trace = true)
            return ThreeJsGlContext(renderer, glContext)
        }
    }
}

class ThreeJsKgl(
    private val renderer: WebGLRenderer,
    private val delegate: Kgl,
    private val webgl: WebGL2RenderingContext
) : Kgl by delegate {
    private var threeState: State? = null

    override fun viewport(x: Int, y: Int, width: Int, height: Int) {
        captureState()

        delegate.viewport(x, y, width, height)
    }

    override fun useProgram(programId: Program) {
        renderer.state.useProgram(programId)
    }

    private fun captureState() {
        if (threeState == null) threeState = State().capture(renderer)
    }

    fun restoreState() {
        bindFramebuffer(GL_FRAMEBUFFER, null)
        bindRenderbuffer(GL_RENDERBUFFER, null)
        threeState?.let {
            it.restore(delegate, webgl)
            threeState = null
        }
    }
}

class State(
    private val viewport: Vector4 = Vector4(),
    private val scissor: Vector4 = Vector4()
) {
    fun capture(renderer: WebGLRenderer): State {
        renderer.getViewport(viewport)
        renderer.getScissor(scissor)
        return this
    }

    fun restore(kgl: Kgl, webgl: WebGL2RenderingContext) {
        with(viewport) {
            kgl.viewport(
                x.roundToInt(), y.roundToInt(),
                width.roundToInt(), height.roundToInt()
            )
        }

        with(scissor) {
            webgl.scissor(
                x.roundToInt(), y.roundToInt(),
                width.roundToInt(), height.roundToInt()
            )
        }
    }
}