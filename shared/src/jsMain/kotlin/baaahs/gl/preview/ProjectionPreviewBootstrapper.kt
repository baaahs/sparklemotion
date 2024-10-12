package baaahs.gl.preview

import baaahs.device.FixtureType
import baaahs.device.MovingHeadDevice
import baaahs.gl.GlBase
import baaahs.gl.render.ComponentRenderEngine
import baaahs.gl.render.pickResultDeliveryStrategy
import baaahs.model.Model
import react.RefObject
import web.html.HTMLCanvasElement

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