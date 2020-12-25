package baaahs.gl.patch

import baaahs.app.ui.editor.LinkOption
import baaahs.app.ui.editor.PortLinkOption
import baaahs.fixtures.DeviceType
import baaahs.gl.shader.InputPort
import baaahs.gl.shader.OpenShader
import baaahs.plugin.Plugins
import baaahs.show.ShaderChannel
import baaahs.show.live.OpenPatch
import baaahs.show.live.OpenShow
import baaahs.show.live.OpenShowVisitor
import baaahs.show.mutable.*
import baaahs.util.Logger

class ChannelsInfo {
    internal val shaderChannels: MutableMap<ContentType, MutableSet<MutableShaderChannel>>
    internal val deviceTypes: Collection<DeviceType>

    constructor(
        parentShow: OpenShow? = null,
        deviceTypes: Collection<DeviceType>
    ) {
        val shaderChannels = shaderChannelsFromDeviceTypes(deviceTypes)

        parentShow?.let {
            object : OpenShowVisitor() {
                override fun visitPatch(openPatch: OpenPatch) {
                    super.visitPatch(openPatch)

                    openPatch.shaderInstances.forEach { shaderInstance ->
                        val contentType = shaderInstance.shader.outputPort.contentType
                        shaderChannels.getOrPut(contentType, ::mutableSetOf)
                            .add(shaderInstance.shaderChannel.toMutable())
                    }
                }
            }.visitShow(parentShow)
        }

        this.shaderChannels = shaderChannels
        this.deviceTypes = deviceTypes
    }

    constructor(
        parentMutableShow: MutableShow? = null,
        deviceTypes: Collection<DeviceType>,
        autoWirer: AutoWirer
    ) {
        val shaderChannels = shaderChannelsFromDeviceTypes(deviceTypes)

        parentMutableShow?.accept(object : MutableShowVisitor {
            override fun visit(mutableShaderInstance: MutableShaderInstance) {
                try {
                    val shader = mutableShaderInstance.mutableShader.build()
                    val contentType = autoWirer.glslAnalyzer.openShader(shader).outputPort.contentType
                    shaderChannels.getOrPut(contentType, ::mutableSetOf)
                        .add(mutableShaderInstance.shaderChannel)
                } catch (e: Exception) {
                    // It's okay, just ignore it.
                }
            }
        })
        this.shaderChannels = shaderChannels
        this.deviceTypes = deviceTypes
    }

    private fun shaderChannelsFromDeviceTypes(deviceTypes: Collection<DeviceType>): MutableMap<ContentType, MutableSet<MutableShaderChannel>> {
        val shaderChannels = mutableMapOf<ContentType, MutableSet<MutableShaderChannel>>()

        val likelyPipelineArtifactTypes = deviceTypes.flatMap { it.likelyPipelines }.map { it.second }
        likelyPipelineArtifactTypes.forEach { contentType ->
            shaderChannels.getOrPut(contentType, ::mutableSetOf)
                .add(ShaderChannel.Main.toMutable())
        }
        return shaderChannels
    }

    operator fun get(contentType: ContentType): Set<MutableShaderChannel>? {
        return shaderChannels[contentType]
    }
}

class ShaderInstanceOptions(
    shader: OpenShader,
    val shaderChannel: ShaderChannel = ShaderChannel.Main,
    channelsInfo: ChannelsInfo,
    defaultPorts: Map<ContentType, MutablePort> = emptyMap(),
    currentLinks: Map<String, MutablePort>,
    private val plugins: Plugins
) {
    val shaderChannels: List<MutableShaderChannel>
    val inputPortLinkOptions: Map<String, List<LinkOption>>

    init {
        fun suggestLinksFor(inputPort: InputPort): ArrayList<PortLinkOption> {
            val options = arrayListOf<PortLinkOption>()

            val exactContentType = inputPort.contentType
            val expandedContentTypes = plugins.suggestContentTypes(inputPort) - exactContentType
            val contentTypes = listOfNotNull(exactContentType) + expandedContentTypes

            contentTypes.forEach { contentType ->
                val defaultPort = defaultPorts[contentType]
                options.add(
                    if (defaultPort != null) {
                        PortLinkOption(
                            defaultPort,
                            isExactContentType = contentType == exactContentType,
                            isLocalShaderOut = true,
                            isDefaultBinding = true
                        )
                    } else {
                        PortLinkOption(
                            MutableShaderChannel(shaderChannel.id),
                            isExactContentType = contentType == exactContentType,
                            isDefaultBinding = true,
                        )
                    }
                )

                channelsInfo[contentType]?.forEach { mutableShaderChannel ->
                    options.add(
                        PortLinkOption(
                            // TODO: this is dumb and broken.
                            mutableShaderChannel,
                            isShaderChannel = true,
                            isExactContentType = contentType == exactContentType
                        )
                    )
                }

                if (contentType == shader.outputPort.contentType) {
                    options.add(
                        PortLinkOption(
                            shaderChannel.toMutable(),
                            createsFilter = true,
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

        this.shaderChannels = channelsInfo.shaderChannels.values.flatten().sortedBy { it.title }
        this.inputPortLinkOptions = map
    }

    companion object {
        private val logger = Logger<ShaderInstanceOptions>()
    }
}