package baaahs.gl.preview

import baaahs.document
import baaahs.gl.GlBase
import baaahs.gl.GlContext
import baaahs.gl.SharedGlContext
import baaahs.model.Model
import baaahs.ui.inPixels
import kotlinx.css.LinearDimension
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLElement
import react.RMutableRef
import react.RReadableRef

actual interface ShaderPreviewBootstrapper {
    fun createHelper(sharedGlContext: SharedGlContext?): Helper =
        if (this is SharedGlContextCapableBootstrapper && sharedGlContext != null)
            SharedCanvasHelper(this, sharedGlContext)
        else
            StandaloneCanvasHelper(this)

    fun bootstrap(
        visibleCanvas: HTMLCanvasElement,
        model: Model,
        preRenderHook: RReadableRef<() -> Unit>
    ): ShaderPreview

    abstract class Helper {
        abstract val container: HTMLElement
        protected var width: LinearDimension? = null
        protected var height: LinearDimension? = null

        fun resize(width: LinearDimension, height: LinearDimension) {
            if (this.width?.value != width.value) {
                container.setAttribute("width", width.toString())
                this.width = width
            }

            if (this.height?.value != height.value) {
                container.setAttribute("height", height.toString())
                this.height = height
            }
        }

        abstract fun bootstrap(model: Model, preRenderHook: RMutableRef<() -> Unit>): ShaderPreview

        abstract fun release(gl: GlContext)
    }
}

interface SharedGlContextCapableBootstrapper : ShaderPreviewBootstrapper {
    fun bootstrapShared(
        container: HTMLElement,
        width: Int,
        height: Int,
        sharedGlContext: SharedGlContext,
        model: Model,
        preRenderHook: RMutableRef<() -> Unit>
    ): ShaderPreview
}

class StandaloneCanvasHelper(
    private val bootstrapper: ShaderPreviewBootstrapper
) : ShaderPreviewBootstrapper.Helper() {
    override val container: HTMLCanvasElement =
        document.createElement("canvas") as HTMLCanvasElement


    override fun bootstrap(model: Model, preRenderHook: RMutableRef<() -> Unit>): ShaderPreview {
        return bootstrapper.bootstrap(container, model, preRenderHook)
    }

    override fun release(gl: GlContext) {}
}

class SharedCanvasHelper(
    private val bootstrapper: SharedGlContextCapableBootstrapper,
    private val sharedGlContext: SharedGlContext
) : ShaderPreviewBootstrapper.Helper() {
    override val container: HTMLDivElement =
        document.createElement("div") as HTMLDivElement

    override fun bootstrap(model: Model, preRenderHook: RMutableRef<() -> Unit>): ShaderPreview =
        bootstrapper.bootstrapShared(
            container,
            width?.inPixels() ?: 10,
            height?.inPixels() ?: 10,
            sharedGlContext,
            model,
            preRenderHook
        )

    override fun release(gl: GlContext) {
        gl.release()
    }
}

actual object MovingHeadPreviewBootstrapper : ShaderPreviewBootstrapper {
    override fun bootstrap(
        visibleCanvas: HTMLCanvasElement,
        model: Model,
        preRenderHook: RReadableRef<() -> Unit>
    ): ShaderPreview {
        @Suppress("UnnecessaryVariable")
        val canvas2d = visibleCanvas
        val glslContext = GlBase.manager.createContext()

        return MovingHeadPreview(canvas2d, glslContext, canvas2d.width, canvas2d.height, model) {
            preRenderHook.current.invoke()
        }
    }
}

actual object ProjectionPreviewBootstrapper : ShaderPreviewBootstrapper {
    override fun bootstrap(
        visibleCanvas: HTMLCanvasElement,
        model: Model,
        preRenderHook: RReadableRef<() -> Unit>
    ): ShaderPreview {
        @Suppress("UnnecessaryVariable")
        val canvas2d = visibleCanvas
        val glslContext = GlBase.manager.createContext()

        return ProjectionPreview(canvas2d, glslContext, canvas2d.width, canvas2d.height, model) {
            preRenderHook.current.invoke()
        }
    }
}

actual object QuadPreviewBootstrapper : ShaderPreviewBootstrapper, SharedGlContextCapableBootstrapper {
    override fun bootstrap(
        visibleCanvas: HTMLCanvasElement,
        model: Model,
        preRenderHook: RReadableRef<() -> Unit>
    ): ShaderPreview {
        val glslContext = GlBase.jsManager.createContext(visibleCanvas)
        return QuadPreview(glslContext, visibleCanvas.width, visibleCanvas.height) {
            preRenderHook.current.invoke()
        }
    }

    override fun bootstrapShared(
        container: HTMLElement,
        width: Int,
        height: Int,
        sharedGlContext: SharedGlContext,
        model: Model,
        preRenderHook: RMutableRef<() -> Unit>
    ): ShaderPreview {
        val glslContext = sharedGlContext.createSubContext(container)
        return QuadPreview(glslContext, width, height) {
            preRenderHook.current.invoke()
        }
    }
}