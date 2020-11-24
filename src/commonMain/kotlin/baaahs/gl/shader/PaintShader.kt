package baaahs.gl.shader

import baaahs.gl.glsl.GlslCode
import baaahs.plugin.Plugins
import baaahs.show.Shader
import baaahs.show.ShaderType

abstract class PaintShader(
    shader: Shader,
    glslCode: GlslCode,
    plugins: Plugins
) : OpenShader.Base(shader, glslCode, plugins) {
    override val shaderType: ShaderType = ShaderType.Paint
}