package baaahs.show

import baaahs.camelize
import kotlinx.serialization.Serializable

@Serializable
data class Layouts(
    val panels: Map<String, Panel> = emptyMap(),
    val formats: Map<String, Layout> = emptyMap()
)

@Serializable
data class Panel(val title: String) {
    fun suggestId(): String = title.camelize()
}

@Serializable
data class Layout(
    val mediaQuery: String?,
    val tabs: List<Tab>
)

@Serializable
data class Tab(
    val title: String,
    val columns: List<String>,
    val rows: List<String>,
    val areas: List<String>
)