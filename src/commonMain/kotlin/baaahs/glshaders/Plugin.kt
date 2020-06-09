package baaahs.glshaders

import baaahs.ports.InputPortRef
import baaahs.show.DataSource

interface Plugin {
    val packageName: String
    val title: String

    fun findDataSource(
        resourceName: String,
        inputPortRef: InputPortRef
    ): DataSource?
}