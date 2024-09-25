package baaahs.gl.preview

import baaahs.gl.SharedGlContext
import baaahs.model.Model
import react.RefObject
import web.html.HTMLElement

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