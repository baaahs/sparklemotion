package baaahs.gl.patch

import baaahs.getBang
import baaahs.gl.glsl.GlslAnalyzer
import baaahs.gl.shader.InputPort
import baaahs.gl.shader.OpenShader
import baaahs.plugin.Plugins
import baaahs.show.Shader
import baaahs.show.ShaderChannel
import baaahs.show.Surfaces
import baaahs.show.live.OpenPatch
import baaahs.show.live.OpenPatchHolder
import baaahs.show.mutable.*
import baaahs.util.Logger

class AutoWirer(
    val plugins: Plugins,
    val glslAnalyzer: GlslAnalyzer = GlslAnalyzer(plugins)
) {
    fun autoWire(
        vararg shaders: Shader,
        defaultPorts: Map<ContentType, MutablePort> = emptyMap()
    ): UnresolvedPatch {
        val openShaders = shaders.associate { it to glslAnalyzer.openShader(it) }
        return autoWire(openShaders.values, defaultPorts = defaultPorts)
    }

    fun autoWire(
        vararg shaders: OpenShader,
        defaultPorts: Map<ContentType, MutablePort> = emptyMap()
    ): UnresolvedPatch {
        return autoWire(shaders.toList(), defaultPorts = defaultPorts)
    }

    fun autoWire(
        shaders: Collection<OpenShader>,
        shaderChannel: ShaderChannel = ShaderChannel.Main,
        parentMutableShow: MutableShow? = null,
        defaultPorts: Map<ContentType, MutablePort> = emptyMap()
    ): UnresolvedPatch {
        val locallyAvailable: MutableMap<ContentType, MutableSet<MutablePort>> = mutableMapOf()

        defaultPorts.forEach { (contentType, port) ->
            locallyAvailable[contentType] = hashSetOf(port)
        }

        // First pass: gather shader output ports.
        val shaderInstances =
            shaders.associateWith { openShader ->
                val unresolvedShaderInstance = UnresolvedShaderInstance(
                    MutableShader(openShader.shader),
                    openShader.inputPorts
                        .associateWith { hashSetOf() },
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
                        .add(MutableShaderChannel(shaderChannel.id))
                }

                unresolvedShaderInstance
            }

        // Second pass: link datasources/output ports to input ports.
        val unresolvedShaderInstances = shaderInstances.map { (openShader, unresolvedShaderInstance) ->
            openShader.inputPorts.forEach { inputPort ->
                val suggestions = collectLinkOptions(inputPort, locallyAvailable)

                unresolvedShaderInstance.linkOptionsFor(inputPort)
                    .addAll(suggestions.filter {
                        // Don't suggest linking back to ourself.
                        !(it is UnresolvedShaderOutPort &&
                                it.unresolvedShaderInstance === unresolvedShaderInstance)
                    })
            }
            unresolvedShaderInstance
        }
        return UnresolvedPatch(unresolvedShaderInstances)
    }

    fun autoWire(
        shader: OpenShader,
        shaderChannel: ShaderChannel = ShaderChannel.Main,
        parentMutableShow: MutableShow? = null,
        parentMutablePatch: MutablePatch? = null,
        defaultPorts: Map<ContentType, MutablePort> = emptyMap(),
        currentLinks: Map<String, MutablePort> = emptyMap()
    ): ShaderInstanceOptions = ShaderInstanceOptions(
        shader,
        shaderChannel,
        parentMutableShow,
        parentMutablePatch,
        defaultPorts,
        currentLinks,
        glslAnalyzer,
        plugins
    )

    private fun collectLinkOptions(
        inputPort: InputPort,
        locallyAvailable: MutableMap<ContentType, MutableSet<MutablePort>>
    ): Collection<MutablePort> {
        val contentTypes = inputPort.contentType?.let { setOf(it) }
            ?: plugins.suggestContentTypes(inputPort)

        val localSuggestions = contentTypes.mapNotNull { locallyAvailable[it] }.flatten().toSet()

        if (localSuggestions.isNotEmpty())
            return localSuggestions

        if (inputPort.hasPluginRef())
            return listOf(MutableDataSourcePort(plugins.resolveDataSource(inputPort)))

        val pluginSuggestions = plugins.suggestDataSources(inputPort).map { it.getMutablePort()!! }
        if (pluginSuggestions.isNotEmpty())
            return pluginSuggestions

        return plugins.suggestDataSources(inputPort, contentTypes).map { it.getMutablePort()!! }
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