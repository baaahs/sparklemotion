package baaahs.gl.preview

import baaahs.document
import baaahs.gl.GlBase
import baaahs.model.Model
import org.w3c.dom.HTMLCanvasElement
import react.RMutableRef

actual interface ShaderPreviewBootstrapper {
    fun bootstrap(
        visibleCanvas: HTMLCanvasElement,
        model: Model,
        preRenderHook: RMutableRef<() -> Unit>
    ): ShaderPreview
}

actual object ProjectionPreviewBootstrapper : ShaderPreviewBootstrapper {
    override fun bootstrap(
        visibleCanvas: HTMLCanvasElement,
        model: Model,
        preRenderHook: RMutableRef<() -> Unit>
    ): ShaderPreview {
        @Suppress("UnnecessaryVariable")
        val canvas2d = visibleCanvas
        val canvas3d = document.createElement("canvas") as HTMLCanvasElement
        val glslContext = GlBase.jsManager.createContext(canvas3d)

        return ProjectionPreview(canvas2d, glslContext, canvas2d.width, canvas2d.height, model) {
            preRenderHook.current.invoke()
        }
    }
}

actual object QuadPreviewBootstrapper : ShaderPreviewBootstrapper {
    override fun bootstrap(
        visibleCanvas: HTMLCanvasElement,
        model: Model,
        preRenderHook: RMutableRef<() -> Unit>
    ): ShaderPreview {
        val glslContext = GlBase.jsManager.createContext(visibleCanvas)
        return QuadPreview(glslContext, visibleCanvas.width, visibleCanvas.height) {
            preRenderHook.current.invoke()
        }
    }
}