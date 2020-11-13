package baaahs

import baaahs.dmx.Dmx
import baaahs.dmx.Shenzarpy
import baaahs.model.MovingHead

class Config {
    companion object {
        private val DMX_DEVICE_MAP = listOf(
            DmxChannelMapping("leftEye", 1, 16, Shenzarpy),
            DmxChannelMapping("rightEye", 17, 16, Shenzarpy)
        ).associateBy { it.modelEntityName }

        fun findDmxChannelMapping(movingHead: MovingHead) =
            DMX_DEVICE_MAP.getBang(movingHead.name, "dmx device base channel")
    }

    data class DmxChannelMapping(
        val modelEntityName: String,
        val baseChannel: Int,
        val channelCount: Int,
        val adapter: Dmx.AdapterBuilder
    )
}
