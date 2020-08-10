package baaahs.plugin

import baaahs.gl.shader.InputPort
import baaahs.show.DataSource

interface Plugin {
    val packageName: String
    val title: String

    fun suggestDataSources(inputPort: InputPort): List<DataSource>

    fun findDataSource(
        resourceName: String,
        inputPort: InputPort
    ): DataSource?
}