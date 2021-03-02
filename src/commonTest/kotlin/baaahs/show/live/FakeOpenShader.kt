package baaahs.show.live

import baaahs.RefCounted
import baaahs.RefCounter
import baaahs.gl.glsl.GlslCode
import baaahs.gl.glsl.GlslError
import baaahs.gl.shader.InputPort
import baaahs.gl.shader.OpenShader
import baaahs.gl.shader.OutputPort
import baaahs.gl.shader.dialect.ShaderDialect
import baaahs.gl.shader.type.ShaderType
import baaahs.show.Shader

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

    override fun invoker(
        namespace: GlslCode.Namespace,
        portMap: Map<String, String>
    ): GlslCode.Invoker = object : GlslCode.Invoker {
        override fun toGlsl(resultVar: String): String = "// invocationGlsl for $title;\n"
    }

    override val shaderType: ShaderType
        get() = TODO("not implemented")

    override val shaderDialect: ShaderDialect
        get() = TODO("not implemented")

    override val errors: List<GlslError>
        get() = emptyList()
}
