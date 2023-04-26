package baaahs.show

import baaahs.Gadget
import baaahs.app.ui.editor.PortLinkOption
import baaahs.camelize
import baaahs.device.FixtureType
import baaahs.gl.data.FeedContext
import baaahs.gl.glsl.GlslType
import baaahs.gl.patch.ContentType
import baaahs.gl.patch.ProgramBuilder
import baaahs.gl.render.LocationStrategy
import baaahs.gl.shader.InputPort
import baaahs.plugin.Plugins
import baaahs.plugin.SerializerRegistrar
import baaahs.scene.SceneProvider
import baaahs.show.live.OpenPatch
import baaahs.show.mutable.MutableControl
import baaahs.show.mutable.MutableFeedPort
import baaahs.ui.Markdown
import baaahs.util.Clock
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

    fun open(feedOpenContext: FeedOpenContext, id: String): FeedContext

    fun link(varName: String) = OpenPatch.FeedLink(this, varName, emptyMap())

    fun suggestId(): String = title.camelize()

    fun buildControl(): MutableControl? = null

    fun appendDeclaration(buf: ProgramBuilder, id: String) {
        if (!isImplicit())
            buf.append("uniform ${getType().glslLiteral} ${getVarName(id)};\n")
    }

    fun invocationGlsl(varName: String, locationStrategy: LocationStrategy): String? = null

    fun appendInvokeAndSet(buf: ProgramBuilder, varName: String) {
        val invocationGlsl = invocationGlsl(varName, buf.renderRegime.locationStrategy)
        if (invocationGlsl != null) {
            buf.append("    // Invoke ", title, "\n")
            buf.append("    ", invocationGlsl, ";\n")
            buf.append("\n")
        }
    }

    fun appendInvoke(buf: ProgramBuilder, varName: String, inputPort: InputPort) = Unit

    fun forFixtureType(fixtureType: FixtureType): Feed = this
}

interface FeedOpenContext {
    val clock: Clock
    val plugins: Plugins

    /**
     * This is for [baaahs.plugin.core.feed.ModelInfoFeed], but we should probably find
     * a better way to get it. Don't add more uses.
     */
    @Deprecated("Get it some other way", level = DeprecationLevel.WARNING)
    val sceneProvider: SceneProvider

    fun <T : Gadget> useGadget(id: String): T = error("override me?")
    fun <T : Gadget> useGadget(feed: Feed): T?
}

interface FeedOpenContext {
    val clock: Clock
    val plugins: Plugins

    /**
     * This is for [baaahs.plugin.core.feed.ModelInfoFeed], but we should probably find
     * a better way to get it. Don't add more uses.
     */
    @Deprecated("Get it some other way", level = DeprecationLevel.WARNING)
    val sceneProvider: SceneProvider

    fun <T : Gadget> useGadget(id: String): T = error("override me?")
    fun <T : Gadget> useGadget(feed: Feed): T?
}

enum class UpdateMode {
    /** The data from this feed will never change. */
    ONCE,

    /** The data from this feed may be different for each frame. */
    PER_FRAME,

    /** The data from this feed may be different for each fixture. */
    PER_FIXTURE
}