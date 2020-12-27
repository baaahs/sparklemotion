package baaahs.show.live

import baaahs.RefCounted
import baaahs.RefCounter
import baaahs.gl.glsl.GlslCode
import baaahs.gl.glsl.GlslError
import baaahs.gl.shader.InputPort
import baaahs.gl.shader.OpenShader
import baaahs.gl.shader.OutputPort
import baaahs.gl.shader.ShaderDialect
import baaahs.show.Shader
import baaahs.show.ShaderType

class FakeOpenShader(
    override val inputPorts: List<InputPort>,
    override val outputPort: OutputPort,
    override val title: String = "Test shader"
) : OpenShader, RefCounted by RefCounter() {
    override val shader: Shader
        get() = Shader(title, "fake src for $title")

    override val glslCode: GlslCode
        get() = GlslCode(shader.src, emptyList())

    override val entryPoint: GlslCode.GlslFunction
        get() = TODO("not implemented")

    override fun toGlsl(namespace: GlslCode.Namespace, portMap: Map<String, String>): String =
        "// GLSL for $title"

    override fun invocationGlsl(
        namespace: GlslCode.Namespace,
        resultVar: String,
        portMap: Map<String, String>
    ): String = "// invocationGlsl for $title;\n"

    override val shaderType: ShaderType
        get() = TODO("not implemented")

    override val shaderDialect: ShaderDialect
        get() = TODO("not implemented")

    override val errors: List<GlslError>
        get() = emptyList()
}
