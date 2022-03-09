package baaahs.dmx

import baaahs.fixtures.TransportConfig
import baaahs.fixtures.TransportType
import baaahs.scene.EditingController
import baaahs.scene.MutableTransportConfig
import baaahs.ui.View
import baaahs.visualizer.visualizerBuilder
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * @param startChannel Zero-based.
 * @param endChannel Zero-based.
 */
@Serializable
@SerialName("DMX")
data class DmxTransportConfig(
    val startChannel: Int,
    val endChannel: Int,
    val componentsStartAtUniverseBoundaries: Boolean = true
) : TransportConfig {
    override val transportType: TransportType
        get() = DmxTransport

    override fun edit(): MutableTransportConfig =
        MutableDmxTransportConfig(this)

    override fun plus(other: TransportConfig?): TransportConfig =
        if (other == null) this
        else plus(other as DmxTransportConfig)

    /** Merges two configs, preferring values from [other]. */
    operator fun plus(other: DmxTransportConfig): DmxTransportConfig = DmxTransportConfig(
        other.startChannel ?: startChannel,
        other.endChannel ?: endChannel,
        other.componentsStartAtUniverseBoundaries ?: componentsStartAtUniverseBoundaries
    )
}

class MutableDmxTransportConfig(config: DmxTransportConfig) : MutableTransportConfig {
    override val transportType: TransportType
        get() = DmxTransport

    var startChannel: Int = config.startChannel
    var endChannel: Int = config.endChannel
    var componentsStartAtUniverseBoundaries: Boolean = config.componentsStartAtUniverseBoundaries

    override fun build(): TransportConfig =
        DmxTransportConfig(startChannel, endChannel, componentsStartAtUniverseBoundaries)

    override fun getEditorView(
        editingController: EditingController<*>
    ): View = visualizerBuilder.getDmxTransportConfigEditorView(editingController, this)

    override fun toSummaryString(): String =
        "$startChannel–$endChannel"
}
