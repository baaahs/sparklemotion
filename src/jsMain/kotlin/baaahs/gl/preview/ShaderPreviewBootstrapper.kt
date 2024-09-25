package baaahs.gl.preview

import baaahs.app.ui.RenderEngineProvider
import baaahs.app.ui.ShaderPreviewStyles
import baaahs.device.FixtureType
import baaahs.device.MovingHeadDevice
import baaahs.document
import baaahs.gl.GlBase
import baaahs.gl.GlContext
import baaahs.gl.SharedGlContext
import baaahs.gl.render.ComponentRenderEngine
import baaahs.gl.render.pickResultDeliveryStrategy
import baaahs.model.Model
import baaahs.ui.inPixels
import baaahs.ui.name
import kotlinx.css.LinearDimension
import react.RefObject
import web.html.HTMLCanvasElement
import web.html.HTMLDivElement
import web.html.HTMLElement

actual interface ShaderPreviewBootstrapper {
    fun createHelper(
        sharedGlContext: SharedGlContext?,
        renderEngineProvider: RenderEngineProvider?
    ): Helper =
        if (this is SharedCanvasContextCapableBootstrapper && sharedGlContext != null)
            SharedCanvasHelper(this, sharedGlContext)
        else if (this is SharedRenderEngineContextCapableBootstrapper && renderEngineProvider != null) {
            SharedRenderEngineContextHelper(this, renderEngineProvider)
        } else
            StandaloneCanvasHelper(this)

    fun bootstrap(
        visibleCanvas: HTMLCanvasElement,
        model: Model,
        preRenderHook: RefObject<(ShaderPreview) -> Unit>
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

        abstract fun bootstrap(model: Model, preRenderHook: RefObject<(ShaderPreview) -> Unit>): ShaderPreview

        abstract fun release(gl: GlContext)
    }
}

interface SharedCanvasContextCapableBootstrapper : ShaderPreviewBootstrapper {
    fun bootstrapShared(
        container: HTMLElement,
        width: Int,
        height: Int,
        sharedGlContext: SharedGlContext,
        model: Model,
        preRenderHook: RefObject<(ShaderPreview) -> Unit>
    ): ShaderPreview
}

interface SharedRenderEngineContextCapableBootstrapper : ShaderPreviewBootstrapper {
    val fixtureType: FixtureType

    fun bootstrapShared(
        visibleCanvas: HTMLCanvasElement,
        width: Int,
        height: Int,
        renderEngine: ComponentRenderEngine,
        model: Model,
        preRenderHook: RefObject<(ShaderPreview) -> Unit>
    ): ShaderPreview
}

class StandaloneCanvasHelper(
    private val bootstrapper: ShaderPreviewBootstrapper
) : ShaderPreviewBootstrapper.Helper() {
    override val container: HTMLCanvasElement =
        (document.createElement("canvas") as HTMLCanvasElement)
            .also { it.className = ShaderPreviewStyles.canvas.name }

    override fun bootstrap(model: Model, preRenderHook: RefObject<(ShaderPreview) -> Unit>): ShaderPreview {
        return bootstrapper.bootstrap(container, model, preRenderHook)
    }

    override fun release(gl: GlContext) {}
}

class SharedCanvasHelper(
    private val bootstrapper: SharedCanvasContextCapableBootstrapper,
    private val sharedGlContext: SharedGlContext
) : ShaderPreviewBootstrapper.Helper() {
    override val container: HTMLDivElement =
        (document.createElement("div") as HTMLDivElement)
            .also { it.className = ShaderPreviewStyles.canvas.name }

    override fun bootstrap(model: Model, preRenderHook: RefObject<(ShaderPreview) -> Unit>): ShaderPreview =
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

class SharedRenderEngineContextHelper(
    private val bootstrapper: SharedRenderEngineContextCapableBootstrapper,
    private val renderEngineProvider: RenderEngineProvider
) : ShaderPreviewBootstrapper.Helper() {
    override val container: HTMLCanvasElement =
        (document.createElement("canvas") as HTMLCanvasElement)
            .also { it.className = ShaderPreviewStyles.canvas.name }

    override fun bootstrap(model: Model, preRenderHook: RefObject<(ShaderPreview) -> Unit>): ShaderPreview =
        bootstrapper.bootstrapShared(
            container,
            width?.inPixels() ?: 10,
            height?.inPixels() ?: 10,
            renderEngineProvider.getRenderEngine(bootstrapper.fixtureType),
            model,
            preRenderHook
        )

    override fun release(gl: GlContext) {
        gl.release()
    }
}

actual object MovingHeadPreviewBootstrapper : ShaderPreviewBootstrapper, SharedRenderEngineContextCapableBootstrapper {
    override val fixtureType: FixtureType
        get() = MovingHeadDevice

    override fun bootstrap(
        visibleCanvas: HTMLCanvasElement,
        model: Model,
        preRenderHook: RefObject<(ShaderPreview) -> Unit>
    ): ShaderPreview {
        val glslContext = GlBase.manager.createContext("MovingHeadPreview")
        val renderEngine = ComponentRenderEngine(
            glslContext, MovingHeadDevice,
            resultDeliveryStrategy = glslContext.pickResultDeliveryStrategy()
        )

        @Suppress("UnnecessaryVariable")
        val canvas2d = visibleCanvas
        return MovingHeadPreview(renderEngine, canvas2d, canvas2d.width, canvas2d.height, model) {
            preRenderHook.current!!.invoke(it)
        }
    }

    override fun bootstrapShared(
        visibleCanvas: HTMLCanvasElement,
        width: Int,
        height: Int,
        renderEngine: ComponentRenderEngine,
        model: Model,
        preRenderHook: RefObject<(ShaderPreview) -> Unit>
    ): ShaderPreview {
        @Suppress("UnnecessaryVariable")
        val canvas2d = visibleCanvas
        return MovingHeadPreview(renderEngine, canvas2d, canvas2d.width, canvas2d.height, model) {
            preRenderHook.current!!.invoke(it)
        }
    }
}

actual object ProjectionPreviewBootstrapper : ShaderPreviewBootstrapper, SharedRenderEngineContextCapableBootstrapper {
    override val fixtureType: FixtureType
        get() = ProjectionPreviewDevice

    override fun bootstrap(
        visibleCanvas: HTMLCanvasElement,
        model: Model,
        preRenderHook: RefObject<(ShaderPreview) -> Unit>
    ): ShaderPreview {
        val glslContext = GlBase.manager.createContext("ProjectionPreview")
        val renderEngine = ComponentRenderEngine(
            glslContext, MovingHeadDevice,
            resultDeliveryStrategy = glslContext.pickResultDeliveryStrategy()
        )
        @Suppress("UnnecessaryVariable")
        val canvas2d = visibleCanvas

        return ProjectionPreview(renderEngine, canvas2d, canvas2d.width, canvas2d.height, model) {
            preRenderHook.current!!.invoke(it)
        }
    }

    override fun bootstrapShared(
        visibleCanvas: HTMLCanvasElement,
        width: Int,
        height: Int,
        renderEngine: ComponentRenderEngine,
        model: Model,
        preRenderHook: RefObject<(ShaderPreview) -> Unit>
    ): ShaderPreview {
        @Suppress("UnnecessaryVariable")
        val canvas2d = visibleCanvas
        return ProjectionPreview(renderEngine, canvas2d, canvas2d.width, canvas2d.height, model) {
            preRenderHook.current!!.invoke(it)
        }
    }
}

actual object QuadPreviewBootstrapper : ShaderPreviewBootstrapper, SharedCanvasContextCapableBootstrapper {
    override fun bootstrap(
        visibleCanvas: HTMLCanvasElement,
        model: Model,
        preRenderHook: RefObject<(ShaderPreview) -> Unit>
    ): ShaderPreview {
        val glslContext = GlBase.jsManager.createContext("Quad Preview", visibleCanvas)
        return QuadPreview(glslContext, visibleCanvas.width, visibleCanvas.height) {
            preRenderHook.current!!.invoke(it)
        }
    }

    override fun bootstrapShared(
        container: HTMLElement,
        width: Int,
        height: Int,
        sharedGlContext: SharedGlContext,
        model: Model,
        preRenderHook: RefObject<(ShaderPreview) -> Unit>
    ): ShaderPreview {
        val glslContext = sharedGlContext.createSubContext("Quad Preview", container)
        return QuadPreview(glslContext, width, height) {
            preRenderHook.current!!.invoke(it)
        }
    }
}