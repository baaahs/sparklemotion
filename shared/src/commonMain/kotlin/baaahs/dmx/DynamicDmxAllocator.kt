package baaahs.dmx

import baaahs.fixtures.ConfigPreview
import baaahs.fixtures.ConfigPreviewNugget
import baaahs.io.ByteArrayWriter
import kotlin.math.max
import kotlin.math.min

class DynamicDmxAllocator(
    private val channelsPerUniverse: Int = Dmx.channelsPerUniverse
) {
    private var nextChannel = 0

    fun allocate(
        componentCount: Int,
        bytesPerComponent: Int,
        config: DmxTransportConfig? = null
    ): StaticDmxMapping {
        val startChannel = config?.startChannel
        if (startChannel != null) {
            if (startChannel >= nextChannel) {
                nextChannel = startChannel
            } else error("$startChannel has already been allocated.")
        }

        val componentMaySpanUniverses = config?.componentMaySpanUniverses ?: false

        val fixtureStartsInFreshUniverse = config?.fixtureStartsInFreshUniverse ?: false
        if (fixtureStartsInFreshUniverse && nextChannel % channelsPerUniverse != 0) {
            nextChannel = (nextChannel / channelsPerUniverse + 1) * channelsPerUniverse
        }

        return StaticDmxMapping(
            nextChannel, componentCount, bytesPerComponent,
            !componentMaySpanUniverses
        ).also { nextChannel = it.calculateEndChannel(channelsPerUniverse) + 1 }
    }
}

class DmxUniverses(
    val universeCount: Int,
    val channelsPerUniverse: Int = Dmx.channelsPerUniverse
) {
    val channels = ByteArray(channelsPerUniverse * universeCount)
    val universeMaxChannel = IntArray(universeCount)

    fun validate(staticDmxMapping: StaticDmxMapping) {
        val startChannel = staticDmxMapping.startChannel
        val endChannel = staticDmxMapping.calculateEndChannel(channelsPerUniverse)

        if (startChannel >= channels.size)
            error("Start channel $startChannel won't fit in $universeCount universes")
        if (endChannel >= channels.size)
            error("End channel $endChannel won't fit in $universeCount universes")
    }

    fun bumpUniverseMax(universeIndex: Int, channelIndex: Int) {
        universeMaxChannel[universeIndex] =
            max(channelIndex, universeMaxChannel[universeIndex])
    }

    fun newWriter(): ByteArrayWriter = ByteArrayWriter(channels)

    fun copyBytes(
        byteArray: ByteArray,
        startChannel: Int,
        startIndex: Int = 0,
        endIndex: Int = byteArray.size
    ) {
        byteArray.copyInto(channels, startChannel, startIndex, endIndex)
    }

    fun describeChannel(channel: Int): String = "${channel / channelsPerUniverse + 1}u${channel % channelsPerUniverse + 1}"
}

/**
 * @param startChannel Zero-based DMX channel.
 */
data class StaticDmxMapping(
    val startChannel: Int,
    val componentCount: Int,
    val bytesPerComponent: Int,
    val componentsStartAtUniverseBoundaries: Boolean
) {
    fun calculateEndChannel(channelsPerUniverse: Int = Dmx.channelsPerUniverse): Int =
        if (componentsStartAtUniverseBoundaries) {
            val componentsPerUniverse = channelsPerUniverse / bytesPerComponent
            val startUniverseIndex = startChannel / channelsPerUniverse
            val startChannelIndex = startChannel % channelsPerUniverse
            val componentsInFirstUniverse = min(
                componentCount,
                (channelsPerUniverse - startChannelIndex) / bytesPerComponent
            )
            val remainingComponents = max(componentCount - componentsInFirstUniverse, 0)
            val additionalWholeUniverses = remainingComponents / componentsPerUniverse
            val componentsInLeftoversUniverse = remainingComponents % componentsPerUniverse
            val lastUniverseIndex = startUniverseIndex + additionalWholeUniverses +
                    if (componentsInLeftoversUniverse == 0) 0 else 1
            val lastChannelInLastUniverse = if (componentsInFirstUniverse == componentCount) {
                startChannelIndex + componentsInFirstUniverse * bytesPerComponent
            } else if (componentsInLeftoversUniverse == 0) {
                componentsPerUniverse * bytesPerComponent
            } else {
                componentsInLeftoversUniverse * bytesPerComponent
            }

            lastUniverseIndex * channelsPerUniverse + lastChannelInLastUniverse - 1
        } else startChannel + componentCount * bytesPerComponent - 1

    fun calculateEndUniverse(channelsPerUniverse: Int = Dmx.channelsPerUniverse): Int =
        (calculateEndChannel(channelsPerUniverse) + channelsPerUniverse - 1) / channelsPerUniverse

    fun writeBytes(
        byteArray: ByteArray,
        dmxUniverses: DmxUniverses,
        startIndex: Int = 0,
        endIndex: Int = byteArray.size
    ) {
        val channelCount = calculateEndChannel(dmxUniverses.channelsPerUniverse) + 1
        val safeEndIndex = min(endIndex, channelCount)
        dmxUniverses.copyBytes(byteArray, startChannel, startIndex, safeEndIndex)
    }

    fun writeComponents(
        componentCount: Int,
        bytesPerComponent: Int,
        dmxUniverses: DmxUniverses,
        fn: (componentIndex: Int, buf: ByteArrayWriter) -> Unit
    ) {
        val channelsPerUniverse = dmxUniverses.channelsPerUniverse
        val writer = dmxUniverses.newWriter()
        if (componentsStartAtUniverseBoundaries) {
            val componentsPerUniverse = channelsPerUniverse / bytesPerComponent
            val effectiveChannelsPerUniverse = componentsPerUniverse * bytesPerComponent
            val startUniverseIndex = startChannel / channelsPerUniverse
            val startChannelIndex = startChannel % channelsPerUniverse

            for (componentIndex in 0 until componentCount) {
                val componentByteOffset = startChannelIndex + componentIndex * bytesPerComponent
                val universeOffset = componentByteOffset / effectiveChannelsPerUniverse
                val channelIndex = componentByteOffset % effectiveChannelsPerUniverse
                val universeIndex = startUniverseIndex + universeOffset
                writer.offset = universeIndex * channelsPerUniverse + channelIndex
                fn(componentIndex, writer)
                dmxUniverses.bumpUniverseMax(universeIndex, channelIndex + bytesPerComponent)
            }
        } else {
            for (componentIndex in 0 until componentCount) {
                val channelIndex = startChannel + componentIndex * bytesPerComponent
                writer.offset = channelIndex
                for (i in channelIndex until channelIndex + bytesPerComponent) {
                    val universeIndex = i / channelsPerUniverse
                    val bIndex = i % channelsPerUniverse
                    dmxUniverses.bumpUniverseMax(universeIndex, bIndex + 1)
                }
                fn(componentIndex, writer)
            }
        }
    }

    fun preview(dmxUniverses: DmxUniverses): ConfigPreview = object : ConfigPreview {
        override fun summary(): List<ConfigPreviewNugget> = listOf(
            ConfigPreviewNugget("Start", dmxUniverses.describeChannel(startChannel), "start"),
            ConfigPreviewNugget(
                "End", dmxUniverses.describeChannel(
                    calculateEndChannel(dmxUniverses.channelsPerUniverse)
                ), "end"
            )
        )
    }
}