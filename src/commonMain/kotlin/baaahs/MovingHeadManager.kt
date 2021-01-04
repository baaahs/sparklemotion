package baaahs

import baaahs.dmx.Dmx
import baaahs.fixtures.*
import baaahs.model.MovingHead
import baaahs.util.Logger

class MovingHeadManager(
    private val fixtureManager: FixtureManager,
    private val dmxUniverse: Dmx.Universe,
    movingHeads: List<MovingHead>
) {
    init {
        fixtureManager.addFrameListener {
            dmxUniverse.sendFrame()
        }
    }

    private val fixtures = movingHeads.map { movingHead ->
        val movingHeadBuffer = movingHead.newBuffer(dmxUniverse)

        Fixture(movingHead, 1, emptyList(), MovingHeadDevice, transport = object : Transport {
            override val name: String
                get() = "DMX Transport"

            override fun send(fixture: Fixture, resultViews: List<ResultView>) {
                val params = MovingHeadDevice.getResults(resultViews)[0]
                movingHeadBuffer.pan = params.pan
                movingHeadBuffer.tilt = params.tilt
                movingHeadBuffer.colorWheelPosition = params.colorWheel
                movingHeadBuffer.dimmer = params.dimmer
            }
        })
    }

    suspend fun start() {
        fixtureManager.fixturesChanged(fixtures, emptyList())
    }

    companion object {
        private val logger = Logger<MovingHeadManager>()
    }
}
