package baaahs

import baaahs.dmx.Dmx
import baaahs.fixtures.*
import baaahs.io.Fs
import baaahs.model.MovingHead
import baaahs.util.Logger
import kotlinx.serialization.json.Json

class MovingHeadManager(
    private val fixtureManager: FixtureManager,
    private val dmxUniverse: Dmx.Universe,
    movingHeads: List<MovingHead>,
    private val fs: Fs
) {
    init {
        fixtureManager.addFrameListener {
            dmxUniverse.sendFrame()
        }
    }

    private val fixtures = movingHeads.map { movingHead ->
        val adapter = movingHead.newBuffer(dmxUniverse)

        Fixture(movingHead, 1, emptyList(), MovingHeadDevice, transport = object : Transport {
            override val name: String
                get() = "DMX Transport"

            override fun send(fixture: Fixture, resultViews: List<ResultView>) {
                val panAndTilt = MovingHeadDevice.getResults(resultViews)[0]
                adapter.pan = panAndTilt.x
                adapter.tilt = panAndTilt.y

                adapter.dimmer = 1f
            }
        })
    }

    private val defaultPosition = MovingHead.MovingHeadPosition(127, 127)
    private val currentPositions = mutableMapOf<MovingHead, MovingHead.MovingHeadPosition>()
    private val listeners = mutableMapOf<MovingHead, (MovingHead.MovingHeadPosition) -> Unit>()

    private val movingHeadPresets = mutableMapOf<String, MovingHead.MovingHeadPosition>()
    private val json = Json

    private val presetsFileName = fs.resolve("presets/moving-head-positions.json")

    suspend fun start() {
        fixtureManager.fixturesChanged(fixtures, emptyList())

        val presetsJson = fs.loadFile(presetsFileName)
        if (presetsJson != null) {
            val map = json.decodeFromString(Topics.movingHeadPresets.serializer, presetsJson)
            movingHeadPresets.putAll(map)
        }
    }

    fun listen(movingHead: MovingHead, onUpdate: (MovingHead.MovingHeadPosition) -> Unit) {
        listeners[movingHead] = onUpdate
    }

    companion object {
        private val logger = Logger<MovingHeadManager>()
    }
}
