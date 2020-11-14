package baaahs.plugin

import baaahs.app.ui.editor.PortLinkOption
import baaahs.gl.patch.ContentType
import baaahs.gl.shader.InputPort
import baaahs.show.DataSource
import baaahs.util.Clock

interface Plugin {
    val packageName: String
    val title: String

    fun resolveDataSource(inputPort: InputPort): DataSource

    fun suggestDataSources(
        inputPort: InputPort,
        suggestedContentTypes: Set<ContentType>
    ): List<PortLinkOption>

    fun resolveContentType(type: String): ContentType?

    fun suggestContentTypes(inputPort: InputPort): Collection<ContentType>

    fun findDataSource(
        resourceName: String,
        inputPort: InputPort
    ): DataSource?
}

interface PluginBuilder {
    val id: String
    fun build(pluginContext: PluginContext): Plugin
}

class PluginContext(
    val clock: Clock
)