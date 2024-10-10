package baaahs.gl.preview

import baaahs.gl.GlBase
import baaahs.gl.SharedGlContext
import baaahs.model.Model
import react.RefObject
import web.html.HTMLCanvasElement
import web.html.HTMLElement

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