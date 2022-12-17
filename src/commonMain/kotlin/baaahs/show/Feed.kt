package baaahs.show

import baaahs.ShowPlayer
import baaahs.app.ui.editor.PortLinkOption
import baaahs.camelize
import baaahs.gl.data.FeedContext
import baaahs.gl.glsl.GlslType
import baaahs.gl.patch.ContentType
import baaahs.gl.shader.InputPort
import baaahs.plugin.SerializerRegistrar
import baaahs.show.live.OpenPatch
import baaahs.show.mutable.MutableControl
import baaahs.show.mutable.MutableFeedPort
import baaahs.ui.Markdown
import baaahs.util.Logger
import kotlinx.serialization.Polymorphic


interface FeedBuilder<T : Feed> {
    val title: String
    @Markdown
    val description: String

    /** The unique ID for this resource within a plugin. Should be CamelCase alphanums, like a class name. */
    val resourceName: String
    val contentType: ContentType
    val serializerRegistrar: SerializerRegistrar<T>
    val internalOnly: Boolean get() = false

    fun suggestFeeds(
        inputPort: InputPort,
        suggestedContentTypes: Set<ContentType> = emptySet()
    ): List<PortLinkOption> {
        return if (looksValid(inputPort, suggestedContentTypes)) {
            listOfNotNull(safeBuild(inputPort)).map { feed ->
                PortLinkOption(
                    MutableFeedPort(feed),
                    wasPurposeBuilt = feed.appearsToBePurposeBuiltFor(inputPort),
                    isPluginSuggestion = true,
                    isExactContentType = feed.contentType == inputPort.contentType
                            || inputPort.contentType.isUnknown()
                )
            }
        } else emptyList()
    }

    fun looksValid(inputPort: InputPort, suggestedContentTypes: Set<ContentType>): Boolean =
        inputPort.contentType == contentType ||
                inputPort.dataTypeIs(contentType.glslType)

    fun build(inputPort: InputPort): T

    fun safeBuild(inputPort: InputPort): Feed? = try {
        build(inputPort)
    } catch (e: Exception) {
        logger.error(e) { "Error building feed for $inputPort." }
        null
    }

    fun funDef(varName: String): String? = null

    companion object {
        private val logger = Logger<FeedBuilder<*>>()
    }
}

internal fun Feed.appearsToBePurposeBuiltFor(inputPort: InputPort) =
    title.camelize().toLowerCase().contains(inputPort.title.camelize().toLowerCase())

/**
 * Descriptor of an external feed which can be used by a shader program.
 */
@Polymorphic
interface Feed {
    val pluginPackage: String
    /** Short English name for this feed. */
    val title: String
    val isUnknown: Boolean get() = false

    // TODO: kill this
    fun isImplicit(): Boolean = false
    val contentType: ContentType

    val dependencies: Map<String, Feed>
        get() = emptyMap()

    fun getType(): GlslType
    fun getVarName(id: String): String = "in_$id"

    fun open(showPlayer: ShowPlayer, id: String): FeedContext

    fun link(varName: String) = OpenPatch.FeedLink(this, varName, emptyMap())

    fun suggestId(): String = title.camelize()

    fun buildControl(): MutableControl? = null

    fun appendDeclaration(buf: StringBuilder, id: String) {
        if (!isImplicit())
            buf.append("uniform ${getType().glslLiteral} ${getVarName(id)};\n")
    }

    fun invocationGlsl(varName: String): String? = null

    fun appendInvokeAndSet(buf: StringBuilder, varName: String) {
        val invocationGlsl = invocationGlsl(varName)
        if (invocationGlsl != null) {
            buf.append("    // Invoke ", title, "\n")
            buf.append("    ", invocationGlsl, ";\n")
            buf.append("\n")
        }
    }

    fun appendInvoke(buf: StringBuilder, varName: String, inputPort: InputPort) = Unit
}

enum class UpdateMode {
    /** The data from this feed will never change. */
    ONCE,

    /** The data from this feed may be different for each frame. */
    PER_FRAME,

    /** The data from this feed may be different for each fixture. */
    PER_FIXTURE
}