package baaahs.gl.patch

import baaahs.app.ui.editor.LinkOption
import baaahs.app.ui.editor.PortLinkOption
import baaahs.device.FixtureType
import baaahs.gl.Toolchain
import baaahs.gl.glsl.LinkException
import baaahs.gl.openShader
import baaahs.gl.shader.InputPort
import baaahs.gl.shader.OpenShader
import baaahs.plugin.Plugins
import baaahs.show.Stream
import baaahs.show.live.OpenPatch
import baaahs.show.live.OpenShow
import baaahs.show.live.OpenShowVisitor
import baaahs.show.mutable.*
import baaahs.util.Logger

class StreamsInfo {
    internal val streams: MutableMap<ContentType, MutableSet<MutableStream>>
    internal val fixtureTypes: Collection<FixtureType>

    constructor(
        parentShow: OpenShow? = null,
        fixtureTypes: Collection<FixtureType>
    ) {
        val streams = streamsFromFixtureTypes(fixtureTypes)

        parentShow?.let {
            object : OpenShowVisitor() {
                override fun visitPatch(openPatch: OpenPatch) {
                    super.visitPatch(openPatch)

                    val contentType = openPatch.shader.outputPort.contentType
                    streams.getOrPut(contentType, ::mutableSetOf)
                        .add(openPatch.stream.toMutable())
                }
            }.visitShow(parentShow)
        }

        this.streams = streams
        this.fixtureTypes = fixtureTypes
    }

    constructor(
        parentMutableShow: MutableShow? = null,
        fixtureTypes: Collection<FixtureType>,
        toolchain: Toolchain
    ) {
        val streams = streamsFromFixtureTypes(fixtureTypes)

        parentMutableShow?.accept(object : MutableShowVisitor {
            override fun visit(mutablePatch: MutablePatch) {
                try {
                    val shader = mutablePatch.mutableShader.build()
                    val contentType = toolchain.openShader(shader).outputPort.contentType
                    streams.getOrPut(contentType, ::mutableSetOf)
                        .add(mutablePatch.stream)
                } catch (e: Exception) {
                    // It's okay, just ignore it.
                }
            }
        })
        this.streams = streams
        this.fixtureTypes = fixtureTypes
    }

    private fun streamsFromFixtureTypes(fixtureTypes: Collection<FixtureType>): MutableMap<ContentType, MutableSet<MutableStream>> {
        val streams = mutableMapOf<ContentType, MutableSet<MutableStream>>()

        val likelyPipelineArtifactTypes = fixtureTypes.flatMap { it.likelyPipelines }.map { it.second }
        likelyPipelineArtifactTypes.forEach { contentType ->
            streams.getOrPut(contentType, ::mutableSetOf)
                .add(Stream.Main.toMutable())
        }
        return streams
    }

    operator fun get(contentType: ContentType): Set<MutableStream>? {
        return streams[contentType]
    }
}

class PatchOptions(
    shader: OpenShader,
    val stream: Stream = Stream.Main,
    streamsInfo: StreamsInfo,
    defaultPorts: Map<ContentType, MutablePort> = emptyMap(),
    currentLinks: Map<String, MutablePort>,
    private val plugins: Plugins
) {
    val streams: List<MutableStream>
    val inputPortLinkOptions: Map<String, List<LinkOption>>

    init {
        fun suggestLinksFor(inputPort: InputPort): ArrayList<PortLinkOption> {
            val options = arrayListOf<PortLinkOption>()

            val exactContentType = inputPort.contentType
            val expandedContentTypes = plugins.suggestContentTypes(inputPort) - exactContentType
            val contentTypes = listOfNotNull(exactContentType) + expandedContentTypes

            contentTypes.forEach { contentType ->
                val defaultPort = defaultPorts[contentType]
                val isExactContentType = contentType == exactContentType || exactContentType.isUnknown()
                options.add(
                    if (defaultPort != null) {
                        PortLinkOption(
                            defaultPort,
                            isExactContentType = isExactContentType,
                            isLocalShaderOut = true,
                            isDefaultBinding = true
                        )
                    } else {
                        PortLinkOption(
                            MutableStream(stream.id),
                            isExactContentType = isExactContentType,
                            isDefaultBinding = true,
                        )
                    }
                )

                streamsInfo[contentType]?.forEach { mutableStream ->
                    options.add(
                        PortLinkOption(
                            // TODO: this is dumb and broken.
                            mutableStream,
                            isStream = true,
                            isExactContentType = isExactContentType
                        )
                    )
                }

                if (contentType == shader.outputPort.contentType) {
                    options.add(
                        PortLinkOption(
                            stream.toMutable(),
                            createsFilter = true,
                            isStream = true,
                            isExactContentType = isExactContentType
                        )
                    )
                }
            }

            if (inputPort.hasPluginRef()) {
                try {
                    val dataSource = plugins.resolveDataSource(inputPort)
                    options.add(PortLinkOption(MutableDataSourcePort(dataSource), isPluginRef = true))
                } catch (e: LinkException) {
                    logger.warn(e) { "Incorrect plugin reference." }
                } catch (e: Exception) {
                    logger.warn(e) { "Error resolving data source for ${inputPort.id}." }
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

        this.streams = streamsInfo.streams.values.flatten().sortedBy { it.title }
        this.inputPortLinkOptions = map
    }

    companion object {
        private val logger = Logger<PatchOptions>()
    }
}