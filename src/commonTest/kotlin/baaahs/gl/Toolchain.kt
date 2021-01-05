package baaahs.gl

import baaahs.gl.glsl.GlslAnalyzer
import baaahs.gl.shader.OpenShader

val testToolchain = RootToolchain(testPlugins())

fun GlslAnalyzer.openShader(src: String): OpenShader {
    return openShader(import(src))
}