package baaahs.gl.render

import baaahs.gl.GlBase
import baaahs.gl.result.ColorResultType
import baaahs.plugin.core.MovingHeadParams
import baaahs.show.Shader

fun glslAvailable(): Boolean {
    val available = GlBase.manager.available
    if (!available) {
        println("WARNING: OpenGL not available, skipping test!")
    }
    return available
}

val directXyProjection = Shader(
    "Direct XY Projection",
    /**language=glsl*/
    /**language=glsl*/
    """
        // @return uv-coordinate
        // @param pixelLocation xyz-coordinate
        vec2 main(vec3 pixelLocation) {
            return vec2(pixelLocation.x, pixelLocation.y);
        }
    """.trimIndent()
)

val ComponentRenderTarget.colors: ColorResultType.ColorFixtureResults get() =
    this.fixtureResults as ColorResultType.ColorFixtureResults

val ComponentRenderTarget.movingHeadParams: MovingHeadParams.ResultBuffer.FixtureResults get() =
    this.fixtureResults as MovingHeadParams.ResultBuffer.FixtureResults