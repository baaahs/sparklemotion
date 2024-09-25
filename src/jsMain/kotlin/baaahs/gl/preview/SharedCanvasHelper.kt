package baaahs.gl.preview

import baaahs.app.ui.ShaderPreviewStyles
import baaahs.document
import baaahs.gl.GlContext
import baaahs.gl.SharedGlContext
import baaahs.model.Model
import baaahs.ui.inPixels
import baaahs.ui.name
import react.RefObject
import web.html.HTMLDivElement

class SharedCanvasHelper(
    private val bootstrapper: SharedCanvasContextCapableBootstrapper,
    private val sharedGlContext: SharedGlContext
) : ShaderPreviewBootstrapper.Helper() {
    override val isRenderedToOpaqueCanvas: Boolean
        get() = false

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