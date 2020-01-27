package baaahs.plugins

import baaahs.BeatData
import baaahs.BeatSource
import kotlinx.serialization.json.JsonObject

class BeatPlugin(val beatSource: BeatSource) : InputPlugin {
    override val name: String = "Beat"

    override fun createDataSource(config: JsonObject): InputPlugin.DataSource<BeatData> {
        return object : InputPlugin.DataSource<BeatData> {
            override val name: String = "Beat"
            override fun getValue(): BeatData = beatSource.getBeatData()
        }
    }
}