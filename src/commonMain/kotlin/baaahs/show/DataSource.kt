package baaahs.show

import baaahs.ShowPlayer
import baaahs.app.ui.editor.PortLinkOption
import baaahs.camelize
import baaahs.gl.data.Feed
import baaahs.gl.glsl.GlslType
import baaahs.gl.patch.ContentType
import baaahs.gl.shader.InputPort
import baaahs.show.mutable.MutableDataSourcePort
import baaahs.show.mutable.MutableGadgetControl
import kotlinx.serialization.Polymorphic


interface DataSourceBuilder<T : DataSource> {
    /** The unique ID for this resource within a plugin. Should be CamelCase alphanums, like a class name. */
    val resourceName: String

    val contentType: ContentType

    fun suggestDataSources(
        inputPort: InputPort,
        suggestedContentTypes: Set<ContentType> = emptySet()
    ): List<PortLinkOption> {
        return if (looksValid(inputPort)) {
            listOf(build(inputPort)).map { dataSource ->
                PortLinkOption(
                    MutableDataSourcePort(dataSource),
                    wasPurposeBuilt = dataSource.appearsToBePurposeBuiltFor(inputPort),
                    isPluginSuggestion = true,
                    isExactContentType = dataSource.contentType == inputPort.contentType
                )
            }
        } else emptyList()
    }

    fun looksValid(inputPort: InputPort): Boolean = false

    fun build(inputPort: InputPort): T
}

internal fun DataSource.appearsToBePurposeBuiltFor(inputPort: InputPort) =
    title.camelize().toLowerCase().contains(inputPort.id.toLowerCase())

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

    fun buildControl(): MutableGadgetControl? = null

    fun appendDeclaration(buf: StringBuilder, varName: String) {
        if (!isImplicit())
            buf.append("uniform ${getType().glslLiteral} ${getVarName(varName)};\n")
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