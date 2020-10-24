package baaahs.gl.patch

import baaahs.app.ui.editor.LinkOption
import baaahs.app.ui.editor.PortLinkOption
import baaahs.gl.glsl.GlslAnalyzer
import baaahs.gl.shader.InputPort
import baaahs.gl.shader.OpenShader
import baaahs.plugin.Plugins
import baaahs.show.ShaderChannel
import baaahs.show.mutable.*
import baaahs.util.Logger

class ShaderInstanceOptions(
    shader: OpenShader,
    val shaderChannel: ShaderChannel = ShaderChannel.Main,
    parentMutableShow: MutableShow? = null,
    parentMutablePatch: MutablePatch? = null,
    defaultPorts: Map<ContentType, MutablePort> = emptyMap(),
    currentLinks: Map<String, MutablePort>,
    glslAnalyzer: GlslAnalyzer,
    private val plugins: Plugins
) {
    val shaderChannels: List<MutableShaderChannel>
    val inputPortLinkOptions: Map<String, List<LinkOption>>

    init {
        val shaderOutsInPatch: MutableMap<ContentType, MutableSet<MutableShaderInstance>> = mutableMapOf()
        val shaderChannels: MutableMap<ContentType, MutableSet<MutableShaderChannel>> = mutableMapOf()

        // Gather shader output ports.
        parentMutablePatch?.mutableShaderInstances?.forEach { shaderInstance ->
            val openShader = glslAnalyzer.openShader(shaderInstance.mutableShader.build())

            openShader.shaderType.defaultUpstreams.forEach { (contentType, shaderChannel) ->
                shaderChannels.getOrPut(contentType) { mutableSetOf() }
                    .add(MutableShaderChannel(shaderChannel.id))
            }

            // Never include this shader in its own suggestions.
            if (openShader.shader == shader.shader) return@forEach

            shaderOutsInPatch.getOrPut(openShader.outputPort.contentType) { mutableSetOf() }
                .add(shaderInstance)
        }

        parentMutableShow?.accept(object : MutableShowVisitor {
            override fun visit(mutableShaderInstance: MutableShaderInstance) {
                val contentType = mutableShaderInstance.mutableShader.type.resultContentType
                shaderChannels.getOrPut(contentType) { mutableSetOf() }
                    .add(mutableShaderInstance.shaderChannel)
            }
        })

        fun suggestLinksFor(inputPort: InputPort): ArrayList<PortLinkOption> {
            val options = arrayListOf<PortLinkOption>()

            val exactContentType = inputPort.contentType
            val expandedContentTypes = plugins.suggestContentTypes(inputPort).let { expanded ->
                exactContentType?.let { expanded - exactContentType } ?: expanded
            }
            val contentTypes =
                (exactContentType?.let { listOf(it) } ?: emptyList()) + expandedContentTypes

            contentTypes.forEach { contentType ->
                defaultPorts[contentType]?.let { defaultPort ->
                    options.add(
                        PortLinkOption(
                            defaultPort,
                            isExactContentType = contentType == exactContentType,
                            isDefaultBinding = true,
                        )
                    )
                }
            }

            contentTypes.forEach { contentType ->
                shaderOutsInPatch[contentType]?.let { shaderInstances ->
                    shaderInstances.forEach { shaderInstance ->
                        options.add(
                            PortLinkOption(
                                MutableShaderOutPort(shaderInstance),
                                isLocalShaderOut = true,
                                isExactContentType = contentType == exactContentType
                            )
                        )
                    }
                }

                shaderChannels[contentType]?.forEach { shaderChannel ->
                    options.add(
                        PortLinkOption(
                            shaderChannel,
                            isShaderChannel = true,
                            isExactContentType = contentType == exactContentType
                        )
                    )
                }
            }

            if (inputPort.hasPluginRef()) {
                try {
                    val dataSource = plugins.resolveDataSource(inputPort)
                    options.add(PortLinkOption(MutableDataSourcePort(dataSource), isPluginRef = true))
                } catch (e: IllegalStateException) {
                    logger.warn(e) { "Incorrect plugin reference." }
                }
            }

            plugins.suggestDataSources(inputPort, contentTypes.toSet()).forEach { portLinkOption ->
                options.add(portLinkOption)
            }
            return options
        }

        val map = shader.inputPorts.associate { inputPort ->
            val currentLink = currentLinks[inputPort.id]
            val options = suggestLinksFor(inputPort) +
                    listOfNotNull(currentLink).map { PortLinkOption(it) }

            val alreadyAdded = mutableSetOf<MutablePort?>()
            val sortedOptions = options
                .sortedBy { 0f - it.priority }
                .filter { alreadyAdded.add(it.getMutablePort()) }

            inputPort.id to sortedOptions
        }

        this.shaderChannels = shaderChannels.values.flatten().toList().sortedBy { it.title }
        this.inputPortLinkOptions = map
    }

    companion object {
        private val logger = Logger<ShaderInstanceOptions>()
    }
}