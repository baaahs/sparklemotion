package baaahs.show.live

import baaahs.getBang
import baaahs.gl.glsl.GlslType
import baaahs.gl.shader.OpenShader
import baaahs.show.*
import baaahs.util.CacheBuilder
import baaahs.util.Logger

fun DataSource.link(varName: String) = OpenPatch.DataSourceLink(this, varName, emptyMap())

class ShaderInstanceResolver(
    private val openShaders: CacheBuilder<String, OpenShader>,
    private val shaderInstances: Map<String, Patch>,
    private val dataSources: Map<String, DataSource>
) {
    private val openPatches = hashMapOf<String, OpenPatch>()

    init {
        shaderInstances.keys.forEach { shaderInstanceId ->
            resolve(shaderInstanceId)
        }
    }

    private fun findDataSource(id: String) = dataSources.getBang(id, "data source")
    private fun findShader(id: String): OpenShader = openShaders.getBang(id, "open shader")
    private fun findShaderInstance(id: String): Patch = shaderInstances.getBang(id, "shader instance")

    private fun resolve(id: String): OpenPatch {
        openPatches[id]?.let { return it }

        val shaderInstance = findShaderInstance(id)
        val shader = findShader(shaderInstance.shaderId)
        val knownInputPorts = shader.inputPorts.associateBy { it.id }

        val links = shaderInstance.incomingLinks
            .filterKeys { portId ->
                knownInputPorts.contains(portId).also { containsKey ->
                    if (!containsKey)
                        logger.warn {
                            "Unknown port mapping \"$portId\" for shader \"${shader.title}\" " +
                                    "(have ${knownInputPorts.keys.sorted()})"
                        }
                }
            }
            .mapValues { (_, portRef) ->
                when (portRef) {
                    is DataSourceRef -> findDataSource(portRef.dataSourceId).link(portRef.dataSourceId)
                    is ShaderChannelRef -> OpenPatch.ShaderChannelLink(portRef.shaderChannel)
                    is OutputPortRef -> TODO()
                    is ConstPortRef -> OpenPatch.ConstLink(portRef.glsl, GlslType.from(portRef.type))
                }
            }

        return build(shader, shaderInstance, links)
            .also { openPatches[id] = it }
    }

    fun getResolvedShaderInstances() = openPatches

    companion object {
        fun build(
            shader: OpenShader,
            patch: Patch,
            links: Map<String, OpenPatch.Link>
        ): OpenPatch {
            val ports = shader.inputPorts.map { it.id }
            val extraLinks = patch.incomingLinks.keys - ports
            val missingLinks = ports - patch.incomingLinks.keys

            return OpenPatch(
                shader,
                links,
                patch.shaderChannel,
                patch.priority,
                extraLinks = extraLinks,
                missingLinks = missingLinks.toSet()
            );
        }

        private val logger = Logger("ShaderInstanceResolver")
    }
}