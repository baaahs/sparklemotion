package baaahs.glsl

import com.danielgergely.kgl.Kgl

interface GlslPlugin {
    fun forProgram(gl: Kgl, program: Program): ProgramContext

    interface ProgramContext {
        val glslPreamble: String

        fun afterCompile() {}

        fun forRender(): RenderContext?

        fun release() {}
    }

    interface RenderContext {
        fun before() {}

        fun after() {}

        fun release() {}
    }
}