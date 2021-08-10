package baaahs.show

import baaahs.ShowPlayer
import baaahs.app.ui.editor.PortLinkOption
import baaahs.camelize
import baaahs.gl.data.Feed
import baaahs.gl.glsl.GlslType
import baaahs.gl.patch.ContentType
import baaahs.gl.shader.InputPort
import baaahs.plugin.SerializerRegistrar
import baaahs.show.mutable.MutableControl
import baaahs.show.mutable.MutableDataSourcePort
import kotlinx.serialization.Polymorphic


interface DataSourceBuilder<T : DataSource> {
    /** The unique ID for this resource within a plugin. Should be CamelCase alphanums, like a class name. */
    val resourceName: String
    val contentType: ContentType
    val serializerRegistrar: SerializerRegistrar<T>

    fun suggestDataSources(
        inputPort: InputPort,
        suggestedContentTypes: Set<ContentType> = emptySet()
    ): List<PortLinkOption> {
        return if (looksValid(inputPort, suggestedContentTypes)) {
            listOf(build(inputPort)).map { dataSource ->
                PortLinkOption(
                    MutableDataSourcePort(dataSource),
                    wasPurposeBuilt = dataSource.appearsToBePurposeBuiltFor(inputPort),
                    isPluginSuggestion = true,
                    isExactContentType = dataSource.contentType == inputPort.contentType
                            || inputPort.contentType.isUnknown()
                )
            }
        } else emptyList()
    }

    fun looksValid(inputPort: InputPort, suggestedContentTypes: Set<ContentType>): Boolean =
        inputPort.contentType == contentType ||
                inputPort.dataTypeIs(contentType.glslType)

    fun build(inputPort: InputPort): T
}

internal fun DataSource.appearsToBePurposeBuiltFor(inputPort: InputPort) =
    title.camelize().toLowerCase().contains(inputPort.title.camelize().toLowerCase())

@Polymorphic
interface DataSource {
    val pluginPackage: String
    /** Short English name for this datasource. */
    val title: String

    // TODO: kill this
    fun isImplicit(): Boolean = false
    fun getType(): GlslType
    val contentType: ContentType
    fun getVarName(id: String): String = "in_$id"

    fun createFeed(showPlayer: ShowPlayer, id: String): Feed

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
}

enum class UpdateMode {
    /** The data from this data source will never change. */
    ONCE,

    /** The data from this data source may be different for each frame. */
    PER_FRAME,

    /** The data from this data source may be different for each fixture. */
    PER_FIXTURE
}