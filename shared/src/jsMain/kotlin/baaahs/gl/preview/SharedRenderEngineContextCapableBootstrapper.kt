package baaahs.gl.preview

import baaahs.device.FixtureType
import baaahs.gl.render.ComponentRenderEngine
import baaahs.model.Model
import react.RefObject
import web.html.HTMLCanvasElement

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