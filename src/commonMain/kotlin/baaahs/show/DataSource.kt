package baaahs.show

import baaahs.ShowPlayer
import baaahs.camelize
import baaahs.gl.data.Feed
import baaahs.gl.glsl.GlslType
import baaahs.gl.patch.ContentType
import baaahs.gl.shader.InputPort
import baaahs.show.mutable.MutableGadgetControl
import kotlinx.serialization.Polymorphic


interface DataSourceBuilder<T : DataSource> {
    /** The unique ID for this resource within a plugin. Should be CamelCase alphanums, like a class name. */
    val resourceName: String
    fun suggestDataSources(inputPort: InputPort): List<T> {
        return if (looksValid(inputPort)) {
            listOf(build(inputPort))
        } else emptyList()
    }
    fun looksValid(inputPort: InputPort): Boolean = false
    fun build(inputPort: InputPort): T
}

@Polymorphic
interface DataSource {
    val pluginPackage: String
    /** Short English name for this datasource. */
    val title: String

    // TODO: kill this
    fun isImplicit(): Boolean = false
    fun getType(): GlslType
    fun getContentType(): ContentType
    fun getVarName(id: String): String = "in_$id"

    fun createFeed(showPlayer: ShowPlayer, id: String): Feed

    /** Yuck. Merge this with [createFeed]. */
    fun createFixtureFeed(): Feed = error("unsupported")

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