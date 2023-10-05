package baaahs.sim

import baaahs.gl.Display
import baaahs.gl.Displays
import baaahs.gl.Mode
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import react.buildElement
import react.createRef
import react.dom.html.ReactHTML.div
import web.html.HTMLElement

class DisplaySimulator(
    private val simulatorStorage: SimulatorStorage,
    private val displays: Displays
) {
    val facade = Facade()
    private val windows = mutableMapOf<Display, Launcher.Window>()

    suspend fun loadSettings() {
        simulatorStorage.loadSettings()?.displays?.forEach {
            attachDisplay(it)
        }
    }

    fun attachDisplay(display: Display) {
        displays.add(display)
        val divRef = createRef<HTMLElement>()

        val window = Launcher.launch(display.name) {
            object : HostedWebApp {
                override fun render() = buildElement {
                    div {
                        ref = divRef
                    }
                }

                override fun onClose() {
                    detachDisplay(display)
                }
            }
        }
        windows[display] = window
    }

    fun detachDisplay(display: Display) {
        displays.remove(display)
        windows.remove(display)
    }

    inner class Facade : baaahs.ui.Facade() {
        fun createNew() {
            val ids = displays.all.map { it.id }
            var nextId = 0L
            while (ids.contains(nextId)) nextId++
            val defaultMode = Mode(320, 200)
            val display = Display(nextId, "Screen $nextId", listOf(defaultMode), defaultMode, false)
            attachDisplay(display)
        }
    }
}

@Serializable
data class SimulatorSettings(
    val displays: List<Display> = emptyList()
)

class SimulatorStorage(private val browserSandboxFs: BrowserSandboxFs) {
    private val settingsFile get() = browserSandboxFs.resolve("simulator-settings.json")

    suspend fun loadSettings(): SimulatorSettings? =
        settingsFile.read()?.let { json.decodeFromString(SimulatorSettings.serializer(), it) }

    suspend fun saveSettings(settings: SimulatorSettings) {
        this.settingsFile.write(json.encodeToString(SimulatorSettings.serializer(), settings), allowOverwrite = true)
    }

    companion object {
        val json = Json { isLenient = true }
    }
}