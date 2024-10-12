package baaahs.gl.preview

import baaahs.app.ui.ShaderPreviewStyles
import baaahs.document
import baaahs.gl.GlContext
import baaahs.model.Model
import baaahs.ui.name
import react.RefObject
import web.html.HTMLCanvasElement

class StandaloneCanvasHelper(
    private val bootstrapper: ShaderPreviewBootstrapper
) : ShaderPreviewBootstrapper.Helper() {
    override val container: HTMLCanvasElement =
        (document.createElement("canvas") as HTMLCanvasElement)
            .also { it.className = ShaderPreviewStyles.canvas.name }

    override fun bootstrap(model: Model, preRenderHook: RefObject<(ShaderPreview) -> Unit>): ShaderPreview {
        return bootstrapper.bootstrap(container, model, preRenderHook)
    }

    override fun release(gl: GlContext) {
        gl.release()
    }
}