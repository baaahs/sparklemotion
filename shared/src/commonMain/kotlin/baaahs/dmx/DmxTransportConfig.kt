package baaahs.dmx

import baaahs.fixtures.ConfigPreview
import baaahs.fixtures.ConfigPreviewNugget
import baaahs.fixtures.TransportConfig
import baaahs.fixtures.TransportType
import baaahs.scene.EditingController
import baaahs.scene.MutableTransportConfig
import baaahs.ui.View
import baaahs.visualizer.entity.visualizerBuilder
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * @param startChannel Zero-based; universe * 512 + channel.
 * @param fixtureStartsInFreshUniverse If true, the fixture will start at channel 0 on the next available universe.
 * @param componentMaySpanUniverses If false, components will never be split across universes.
 */
@Serializable
@SerialName("DMX")
data class DmxTransportConfig(
    val startChannel: Int? = null,
    val fixtureStartsInFreshUniverse: Boolean? = null,
    val componentMaySpanUniverses: Boolean? = null
) : TransportConfig {
    override val transportType: TransportType
        get() = DmxTransportType

    override fun edit(): MutableTransportConfig =
        MutableDmxTransportConfig(
            startChannel, fixtureStartsInFreshUniverse, componentMaySpanUniverses
        )

    override fun plus(other: TransportConfig?): TransportConfig =
        if (other == null) this
        else plus(other as DmxTransportConfig)

    /** Merges two configs, preferring values from [other]. */
    operator fun plus(other: DmxTransportConfig): DmxTransportConfig = DmxTransportConfig(
        other.startChannel ?: startChannel,
        other.fixtureStartsInFreshUniverse ?: fixtureStartsInFreshUniverse,
        other.componentMaySpanUniverses ?: componentMaySpanUniverses
    )

    override fun preview(): ConfigPreview = object : ConfigPreview {
        override fun summary(): List<ConfigPreviewNugget> = listOf(
            ConfigPreviewNugget("Start Channel", startChannel?.toString()),
            ConfigPreviewNugget("Start In Fresh Universe", fixtureStartsInFreshUniverse?.toString())
        )
    }
}

class MutableDmxTransportConfig(
    var startChannel: Int?,
    var fixtureStartsInFreshUniverse: Boolean?,
    var componentMaySpanUniverses: Boolean?,
) : MutableTransportConfig {
    override val transportType: TransportType
        get() = DmxTransportType

    override fun build(): TransportConfig =
        DmxTransportConfig(startChannel, fixtureStartsInFreshUniverse, componentMaySpanUniverses)

    override fun getEditorView(
        editingController: EditingController<*>
    ): View = visualizerBuilder.getDmxTransportConfigEditorView(editingController, this)

    override fun toSummaryString(): String =
        "$startChannel–$fixtureStartsInFreshUniverse-$componentMaySpanUniverses"
}
