package baaahs.plugin

import baaahs.gl.patch.ContentType
import baaahs.gl.shader.InputPort
import baaahs.show.DataSource

interface Plugin {
    val packageName: String
    val title: String

    fun resolveDataSource(inputPort: InputPort): DataSource

    fun suggestDataSources(
        inputPort: InputPort,
        suggestedContentTypes: Set<ContentType>
    ): List<DataSource>

    fun resolveContentType(type: PluginRef): ContentType?

    fun suggestContentTypes(inputPort: InputPort): Collection<ContentType>

    fun findDataSource(
        resourceName: String,
        inputPort: InputPort
    ): DataSource?
}