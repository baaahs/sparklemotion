package baaahs.shows

import baaahs.shaders.GlslShader

object GuruMeditationErrorShow : GlslShow("Ω Guru Meditation Error Ω",
    /**language=glsl*/
    """
    uniform float time;
    void main() {
        gl_FragColor = (mod(time, 2.) < 1.)
            ? vec4(.75, 0., 0., 1.)
            : vec4(.25, 0., 0., 1.);
    }
""".trimIndent(), GlslShader.globalRenderContext)