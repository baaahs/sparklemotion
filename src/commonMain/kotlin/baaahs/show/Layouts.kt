package baaahs.show

import kotlinx.serialization.Serializable

@Serializable
data class Layouts(
    val panelNames: List<String> = emptyList(),
    val formats: Map<String, Layout> = emptyMap()
)

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