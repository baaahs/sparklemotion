package baaahs.gl.preview

import baaahs.app.ui.RenderEngineProvider
import baaahs.gl.GlContext
import baaahs.gl.SharedGlContext
import baaahs.model.Model
import kotlinx.css.LinearDimension
import react.RefObject
import web.html.HTMLCanvasElement
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
        open val isRenderedToOpaqueCanvas: Boolean
            get() = true

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