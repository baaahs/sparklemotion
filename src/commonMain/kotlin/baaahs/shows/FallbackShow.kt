package baaahs.shows

import baaahs.shaders.GlslShader

object FallbackShow : GlslShow("Fallback Show",
    /**language=glsl*/
    """
    uniform float time;
    void main() {
        gl_FragColor = (mod(int(time), 2) == 1)
            ? vec4(.75, .1, .1, 1.)
            : vec4(.25, 0., 0., 1.);
    }
""".trimIndent(), GlslShader.globalRenderContext)