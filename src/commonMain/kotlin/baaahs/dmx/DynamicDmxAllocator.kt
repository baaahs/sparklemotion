package baaahs.dmx

import baaahs.io.ByteArrayWriter
import kotlin.math.max
import kotlin.math.min

class DynamicDmxAllocator(
    val universeCount: Int,
    private val channelsPerUniverse: Int = Dmx.channelsPerUniverse
) {
    private var nextChannel = 0

    fun allocate(config: DmxTransportConfig?, componentCount: Int, bytesPerComponent: Int): StaticDmxMapping {
        val componentMaySpanUniverses = config?.componentMaySpanUniverses ?: false

        val fixtureStartsInFreshUniverse = config?.fixtureStartsInFreshUniverse ?: false
        if (fixtureStartsInFreshUniverse && nextChannel % channelsPerUniverse != 0) {
            nextChannel = (nextChannel / channelsPerUniverse + 1) * channelsPerUniverse
        }

        val channelCount = componentCount * bytesPerComponent

        return StaticDmxMapping(
            nextChannel, channelCount,
            !componentMaySpanUniverses
        ).also { nextChannel += channelCount }
    }
}

class DmxUniverses(
    universeCount: Int,
    val channelsPerUniverse: Int = Dmx.channelsPerUniverse
) {
    val channels = ByteArray(channelsPerUniverse * universeCount)
    val universeMaxChannel = IntArray(universeCount)

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
}

/**
 * @param startChannel Zero-based DMX channel.
 * @param channelCount Number of channels.
 */
data class StaticDmxMapping(
    val startChannel: Int,
    val channelCount: Int,
    val componentsStartAtUniverseBoundaries: Boolean
) {
    fun writeBytes(
        byteArray: ByteArray,
        dmxUniverses: DmxUniverses,
        startIndex: Int = 0,
        endIndex: Int = byteArray.size
    ) {
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
}