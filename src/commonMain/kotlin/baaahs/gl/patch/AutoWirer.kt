package baaahs.gl.patch

import baaahs.Logger
import baaahs.getBang
import baaahs.gl.glsl.GlslAnalyzer
import baaahs.gl.shader.InputPort
import baaahs.gl.shader.OpenShader
import baaahs.plugin.Plugins
import baaahs.show.*
import baaahs.show.live.ShowContext
import baaahs.show.live.OpenShaders
import baaahs.show.mutable.MutableDataSourceSourcePort
import baaahs.show.mutable.MutableShader
import baaahs.show.mutable.MutableShaderChannelSourcePort
import baaahs.show.mutable.MutableSourcePort

class AutoWirer(
    val plugins: Plugins,
    val glslAnalyzer: GlslAnalyzer = GlslAnalyzer(plugins)
) {
    fun autoWire(
        vararg shaders: Shader,
        defaultPorts: Map<ContentType, MutableSourcePort> = emptyMap()
    ): UnresolvedPatch {
        val openShaders = shaders.associate { it to glslAnalyzer.openShader(it) }
        return autoWire(openShaders.values, defaultPorts = defaultPorts)
    }

    fun autoWire(
        vararg shaders: OpenShader,
        defaultPorts: Map<ContentType, MutableSourcePort> = emptyMap()
    ): UnresolvedPatch {
        return autoWire(shaders.toList(), defaultPorts = defaultPorts)
    }

    fun autoWire(
        shaders: Collection<OpenShader>,
        shaderChannel: ShaderChannel = ShaderChannel.Main,
        defaultPorts: Map<ContentType, MutableSourcePort> = emptyMap()
    ): UnresolvedPatch {
        val locallyAvailable: MutableMap<ContentType, MutableSet<SourcePortOption>> = mutableMapOf()

        defaultPorts.forEach { (contentType, port) ->
            locallyAvailable[contentType] = hashSetOf<SourcePortOption>(ResolvedSourcePortOption(port))
        }

        // First pass: gather shader output ports.
        val shaderInstances =
            shaders.associateWith { openShader ->
                val unresolvedShaderInstance = UnresolvedShaderInstance(
                    MutableShader(openShader.shader),
                    openShader.inputPorts
                        .map { it.id }
                        .associateWith { hashSetOf<SourcePortOption>() },
                    shaderChannel,
                    0f
                )

                locallyAvailable.getOrPut(openShader.outputPort.contentType) { mutableSetOf() }
                    .add(UnresolvedInstanceSourcePortOption(unresolvedShaderInstance))

                openShader.shaderType.defaultUpstreams.forEach { (contentType, shaderChannel) ->
                    locallyAvailable.getOrPut(contentType) { mutableSetOf() }
                        .add(ResolvedSourcePortOption(MutableShaderChannelSourcePort(shaderChannel)))
                }

                unresolvedShaderInstance
            }

        // Second pass: link datasources/output ports to input ports.
        val unresolvedShaderInstances = shaderInstances.map { (openShader, unresolvedShaderInstance) ->
            openShader.inputPorts.forEach { inputPort ->
                val suggestions = collectLinkOptions(inputPort, locallyAvailable)

                unresolvedShaderInstance.incomingLinksOptions
                    .getBang(inputPort.id, "port")
                    .addAll(suggestions.filter {
                        // Don't suggest linking back to ourself.
                        !(it is UnresolvedInstanceSourcePortOption &&
                                it.unresolvedShaderInstance === unresolvedShaderInstance)
                    })
            }
            unresolvedShaderInstance
        }
        return UnresolvedPatch(unresolvedShaderInstances)
    }

    private fun collectLinkOptions(
        inputPort: InputPort,
        locallyAvailable: MutableMap<ContentType, MutableSet<SourcePortOption>>
    ): Collection<SourcePortOption> {
        val contentTypes = inputPort.contentType?.let { setOf(it) }
            ?: plugins.suggestContentTypes(inputPort)

        val localSuggestions = contentTypes.mapNotNull { locallyAvailable[it] }.flatten().toSet()

        if (localSuggestions.isNotEmpty())
            return localSuggestions

        if (inputPort.hasPluginRef()) {
            val dataSource = plugins.resolveDataSource(inputPort)
            return listOf(ResolvedSourcePortOption(MutableDataSourceSourcePort(dataSource)))
        }

        val pluginSuggestions = plugins.suggestDataSources(inputPort).map {
            ResolvedSourcePortOption(MutableDataSourceSourcePort(it))
        }
        if (pluginSuggestions.isNotEmpty())
            return pluginSuggestions

        return plugins.suggestDataSources(inputPort, contentTypes).map {
            ResolvedSourcePortOption(MutableDataSourceSourcePort(it))
        }
    }

    fun merge(showContext: ShowContext, vararg patchHolders: PatchHolder): Map<Surfaces, PortDiagram> {
        val patchesBySurfaces = mutableMapOf<Surfaces, MutableList<Patch>>()
        patchHolders.forEach { patchHolder ->
            patchHolder.patches.forEach { patch ->
                patchesBySurfaces.getOrPut(patch.surfaces) { arrayListOf() }
                    .add(patch)
            }
        }

        return patchesBySurfaces.mapValues { (_, patches) ->
            buildPortDiagram(showContext, *patches.toTypedArray())
        }
    }

    fun buildPortDiagram(openShaders: OpenShaders, vararg patches: baaahs.show.Patch): PortDiagram {
        val portDiagram = PortDiagram(openShaders)
        patches.forEach { patch -> portDiagram.add(patch) }
        return portDiagram
    }

    companion object {
        private val logger = Logger("AutoWirer")
    }
}