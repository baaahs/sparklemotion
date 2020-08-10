package baaahs.gl.patch

import baaahs.Logger
import baaahs.gl.glsl.GlslAnalyzer
import baaahs.gl.shader.OpenShader
import baaahs.plugin.Plugins
import baaahs.show.Shader
import baaahs.show.ShaderChannel
import baaahs.show.Surfaces
import baaahs.show.live.OpenPatch
import baaahs.show.live.OpenPatchHolder
import baaahs.show.mutable.MutableDataSource
import baaahs.show.mutable.MutableLink
import baaahs.show.mutable.MutableShader
import baaahs.show.mutable.MutableShaderChannel

class AutoWirer(
    val plugins: Plugins,
    val glslAnalyzer: GlslAnalyzer = GlslAnalyzer()
) {
    fun autoWire(
        vararg shaders: Shader,
        defaultPorts: Map<ContentType, MutableLink.Port> = emptyMap()
    ): UnresolvedPatch {
        val openShaders = shaders.associate { it to glslAnalyzer.openShader(it) }
        return autoWire(openShaders.values, defaultPorts = defaultPorts)
    }

    fun autoWire(
        vararg shaders: OpenShader,
        defaultPorts: Map<ContentType, MutableLink.Port> = emptyMap()
    ): UnresolvedPatch {
        return autoWire(shaders.toList(), defaultPorts = defaultPorts)
    }

    fun autoWire(
        shaders: Collection<OpenShader>,
        shaderChannel: ShaderChannel = ShaderChannel.Main,
        defaultPorts: Map<ContentType, MutableLink.Port> = emptyMap()
    ): UnresolvedPatch {
        val locallyAvailable: MutableMap<ContentType, MutableSet<MutableLink.Port>> = mutableMapOf()

        defaultPorts.forEach { (contentType, port) ->
            locallyAvailable[contentType] = hashSetOf(port)
        }

        // First pass: gather shader output ports.
        val shaderInstances =
            shaders.associateWith { openShader ->
                val unresolvedShaderInstance = UnresolvedShaderInstance(
                    MutableShader(openShader.shader),
                    openShader.inputPorts
                        .map { it.id }
                        .associateWith { hashSetOf<MutableLink.Port>() },
                    shaderChannel,
                    0f
                )

                locallyAvailable.getOrPut(openShader.outputPort.contentType) { mutableSetOf() }
                    .add(
                        UnresolvedShaderOutPort(
                            unresolvedShaderInstance,
                            openShader.outputPort.id
                        )
                    )

                openShader.shaderType.defaultUpstreams.forEach { (contentType, shaderChannel) ->
                    locallyAvailable.getOrPut(contentType) { mutableSetOf() }
                        .add(MutableShaderChannel(shaderChannel))
                }

                unresolvedShaderInstance
            }

        // Second pass: link datasources/output ports to input ports.
        val unresolvedShaderInstances = shaderInstances.map { (openShader, unresolvedShaderInstance) ->
            openShader.inputPorts.forEach { inputPort ->
                val localSuggestions: Set<MutableLink.Port>? = locallyAvailable[inputPort.contentType]
                val suggestions = localSuggestions
                    ?: plugins.suggestDataSources(inputPort).map { MutableDataSource(it) }
                val portLinkOptions = unresolvedShaderInstance.incomingLinksOptions[inputPort.id]
                portLinkOptions!!.addAll(suggestions.filter {
                    // Don't suggest linking back to ourself.
                    !(it is UnresolvedShaderOutPort && it.unresolvedShaderInstance === unresolvedShaderInstance)
                })
            }
            unresolvedShaderInstance
        }
        return UnresolvedPatch(unresolvedShaderInstances)
    }

    fun merge(vararg patchHolders: OpenPatchHolder): Map<Surfaces, PortDiagram> {
        val patchesBySurfaces = mutableMapOf<Surfaces, MutableList<OpenPatch>>()
        patchHolders.forEach { openPatchHolder ->
            openPatchHolder.patches.forEach { patch ->
                patchesBySurfaces.getOrPut(patch.surfaces) { arrayListOf() }
                    .add(patch)
            }
        }

        return patchesBySurfaces.mapValues { (_, openPatches) ->
            buildPortDiagram(*openPatches.toTypedArray())
        }
    }

    fun buildPortDiagram(vararg patches: OpenPatch): PortDiagram {
        val portDiagram = PortDiagram()
        patches.forEach { patch ->
            portDiagram.add(patch)
        }
        return portDiagram
    }

    companion object {
        private val logger = Logger("AutoWirer")
    }
}