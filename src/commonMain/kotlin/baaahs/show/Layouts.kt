package baaahs.show

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class Layouts(
    val panels: Map<String, PanelConfig> = emptyMap(),
    val formats: Map<String, Layout> = emptyMap()
)

@Serializable
data class PanelConfig(
    @Transient val `_`: Boolean = false
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