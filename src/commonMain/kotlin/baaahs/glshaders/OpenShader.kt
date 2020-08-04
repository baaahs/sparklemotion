package baaahs.glshaders

import baaahs.RefCounted
import baaahs.RefCounter
import baaahs.glshaders.GlslCode.GlslFunction
import baaahs.glshaders.GlslCode.Namespace
import baaahs.show.Shader
import baaahs.show.ShaderChannel
import baaahs.unknown
import kotlin.collections.set

interface OpenShader : RefCounted {
    enum class Type(
        val priority: Int,
        val defaultUpstreams: Map<ContentType, ShaderChannel>,
        /**language=glsl*/
        val template: String
    ) {
        Projection(0, emptyMap(), """
                uniform sampler2D pixelCoordsTexture;
                
                struct ModelInfo {
                    vec3 center;
                    vec3 extents;
                };
                uniform ModelInfo modelInfo;

                vec2 project(vec3 pixelLocation) {
                    vec3 start = modelInfo.center - modelInfo.extents / 2.;
                    vec3 rel = (pixelLocation - start) / modelInfo.extents;
                    return rel.xy;
                }
                
                vec2 mainUvFromRaster(vec2 rasterCoord) {
                    int rasterX = int(rasterCoord.x);
                    int rasterY = int(rasterCoord.y);
                    
                    vec3 pixelCoord = texelFetch(pixelCoordsTexture, ivec2(rasterX, rasterY), 0).xyz;
                    return project(pixelCoord);
                }
        """.trimIndent()),

        Distortion(1, mapOf(ContentType.UvCoordinate to ShaderChannel.Main), """
            // ... TODO
        """.trimIndent()),

        Color(0, mapOf(ContentType.UvCoordinate to ShaderChannel.Main), """
            uniform vec2 resolution;
            uniform float time;

            void main(void) {
                vec2 position = gl_FragCoord.xy / resolution.xy;
                gl_FragColor = vec4(position.xy, mod(time, 1.), 1.);
            }
        """.trimIndent()),

        Filter(1, mapOf(ContentType.Color to ShaderChannel.Main), """
            vec4 filterImage(vec4 inColor) {
                return inColor;
            }
        """.trimIndent());
    }

    val shader: Shader get() = Shader(src)
    val src: String get() = glslCode.src
    val glslCode: GlslCode
    val title: String
    val description: String?
    val shaderType: Type
    val entryPoint: GlslFunction
    val inputPorts: List<InputPort>
    val outputPort: OutputPort
//    TODO val inputDefaults: Map<String, InputDefault>

    fun findInputPort(portId: String): InputPort
    fun toGlsl(namespace: Namespace, portMap: Map<String, String> = emptyMap()): String
    fun invocationGlsl(
        namespace: Namespace,
        resultVar: String,
        portMap: Map<String, String> = emptyMap()
    ): String

    abstract class Base(final override val glslCode: GlslCode) : OpenShader, RefCounted by RefCounter() {
        override val title: String = glslCode.title
        override val description: String? = null

        protected fun toInputPort(it: GlslCode.GlslVar): InputPort {
            return InputPort(
                it.name, it.dataType, it.name.capitalize(),
                pluginRef = it.hint?.pluginRef,
                pluginConfig = it.hint?.config,
                glslVar = it
            )
        }

        override fun findInputPort(portId: String): InputPort {
            return inputPorts.find { it.id == portId }
                ?: error(unknown("input port", portId, inputPorts))
        }

        override fun toGlsl(namespace: Namespace, portMap: Map<String, String>): String {
            val buf = StringBuilder()

            val nonUniformGlobalsMap = hashMapOf<String, String>()
            glslCode.globalVars.forEach { glslVar ->
                if (!glslVar.isUniform) {
                    nonUniformGlobalsMap[glslVar.name] = namespace.qualify(glslVar.name)
                    buf.append(glslVar.toGlsl(namespace, glslCode.symbolNames, emptyMap()))
                    buf.append("\n")
                }
            }

            val symbolsToNamespace = glslCode.symbolNames.toSet()
            val symbolMap = portMap + nonUniformGlobalsMap
            glslCode.functions.forEach { glslFunction ->
                buf.append(glslFunction.toGlsl(namespace, symbolsToNamespace, symbolMap))
                buf.append("\n")
            }

            return buf.toString()
        }

        override fun equals(other: Any?): Boolean =
            other != null
                    && other is Base
                    && this::class == other::class
                    && this.src == other.src

        override fun hashCode(): Int =
            src.hashCode()
    }

    companion object {
        fun tryColorShader(glslCode: GlslCode): ColorShader? {
            return when {
                glslCode.functionNames.contains("main") ->
                    GenericColorShader(glslCode)

                glslCode.functionNames.contains("mainImage") ->
                    ShaderToyColorShader(glslCode)

                else -> null
            }
        }

        fun tryFilterShader(glslCode: GlslCode): FilterShader? {
            return when {
                glslCode.functionNames.contains("filterImage") ->
                    FilterShader(glslCode)

                else -> null
            }
        }

        fun tryUvTranslatorShader(glslCode: GlslCode): UvShader? {
            return when {
                glslCode.functionNames.contains("mainUvFromRaster") ->
                    UvShader(glslCode)

                else -> null
            }
        }
    }
}