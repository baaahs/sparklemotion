package baaahs.gl.shader

import baaahs.RefCounted
import baaahs.RefCounter
import baaahs.gl.glsl.GlslCode
import baaahs.gl.glsl.GlslError
import baaahs.show.Shader

class FakeOpenShader(
    override val inputPorts: List<InputPort>,
    override val outputPort: OutputPort
) : OpenShader, RefCounted by RefCounter() {
    override val shader: Shader
        get() = Shader("Test shader", GenericPaintShader, "")
    override val errors: List<GlslError>
        get() = TODO("not implemented")

    override val title: String
        get() = shader.title

    override val glslCode: GlslCode
        get() = GlslCode("", emptyList())

    override val entryPoint: GlslCode.GlslFunction
        get() = TODO("not implemented")
    override val defaultPriority: Int
        get() = TODO("not implemented")

    override fun findInputPortOrNull(portId: String): InputPort? {
        TODO("not implemented")
    }

    override fun toGlsl(namespace: GlslCode.Namespace, portMap: Map<String, String>): String =
        TODO("not implemented")

    override fun invocationGlsl(
        namespace: GlslCode.Namespace,
        resultVar: String,
        portMap: Map<String, String>
    ): String = TODO("not implemented")
}
