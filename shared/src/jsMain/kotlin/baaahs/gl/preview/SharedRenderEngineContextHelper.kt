package baaahs.gl.preview

import baaahs.app.ui.RenderEngineProvider
import baaahs.app.ui.ShaderPreviewStyles
import baaahs.document
import baaahs.gl.GlContext
import baaahs.model.Model
import baaahs.ui.inPixels
import baaahs.ui.name
import react.RefObject
import web.html.HTMLCanvasElement

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