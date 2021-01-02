package baaahs.visualizer.movers

import baaahs.Color
import baaahs.model.MovingHead
import baaahs.sim.FakeDmxUniverse
import three.js.Scene

class VizMovingHead(private val movingHead: MovingHead, dmxUniverse: FakeDmxUniverse) {
    private val buffer = run {
        val dmxBufferReader = dmxUniverse.reader(movingHead.baseDmxChannel, movingHead.dmxChannelCount) {
            receivedDmxFrame()
        }
        movingHead.newBuffer(dmxBufferReader)
    }
    private val beam = when (movingHead.colorModel) {
        MovingHead.ColorModel.ColorWheel -> ColorWheelBeam(movingHead, buffer)
        MovingHead.ColorModel.RGB -> RgbBeam(movingHead, buffer)
        MovingHead.ColorModel.RGBW -> RgbBeam(movingHead, buffer)
    }

    fun addTo(scene: Scene) {
        beam.addTo(scene)
    }

    private fun receivedDmxFrame() {
        beam.receivedDmxFrame()
    }
}

enum class ClipMode(
    val isClipped: Boolean,
    val getColor: MovingHead.Buffer.() -> Color
) {
    Solo(false, { primaryColor }),
    Primary(true, { primaryColor }),
    Secondary(true, { secondaryColor })
}

fun ClosedRange<Float>.scale(value: Float) =
    (endInclusive - start) * value + start
