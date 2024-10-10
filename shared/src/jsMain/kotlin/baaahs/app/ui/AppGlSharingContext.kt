package baaahs.app.ui

import baaahs.device.FixtureType
import baaahs.gl.GlBase
import baaahs.gl.SharedGlContext
import baaahs.gl.render.ComponentRenderEngine
import baaahs.gl.render.pickResultDeliveryStrategy
import js.objects.jso
import react.createContext

/**
 * WebGL only allows up to 16 contexts per page, so we need to share a
 * few contexts between all the previews that'll be on screen.
 */
val appGlSharingContext = createContext<AppGlSharingContext>(jso {})

external interface AppGlSharingContext {
    var sharedGlContext: SharedGlContext?
    var renderEngineProvider: RenderEngineProvider?
}

class RenderEngineProvider {
    private val sharedGlContext by lazy {
        GlBase.manager.createContext("SharedRenderEngineProvider Context")
    }

    fun getRenderEngine(fixtureType: FixtureType): ComponentRenderEngine =
        ComponentRenderEngine(
            sharedGlContext,
            fixtureType,
            resultDeliveryStrategy = sharedGlContext.pickResultDeliveryStrategy()
        )
}