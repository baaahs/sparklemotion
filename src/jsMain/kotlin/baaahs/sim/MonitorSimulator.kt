package baaahs.sim

import baaahs.gl.Mode
import baaahs.gl.Monitor
import baaahs.gl.Monitors
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import react.buildElement
import react.createRef
import react.dom.html.ReactHTML.div
import web.html.HTMLElement

class MonitorSimulator(
    private val simulatorStorage: SimulatorStorage,
    private val monitors: Monitors
) {
    val facade = Facade()
    private val windows = mutableMapOf<Monitor, Launcher.Window>()

    suspend fun loadSettings() {
        simulatorStorage.loadSettings()?.monitors?.forEach {
            attachMonitor(it)
        }
    }

    fun attachMonitor(monitor: Monitor) {
        monitors.add(monitor)
        val divRef = createRef<HTMLElement>()

        val window = Launcher.launch(monitor.name) {
            object : HostedWebApp {
                override fun render() = buildElement {
                    div {
                        ref = divRef
                    }
                }

                override fun onClose() {
                    detachMonitor(monitor)
                }
            }
        }
        windows[monitor] = window
    }

    fun detachMonitor(monitor: Monitor) {
        monitors.remove(monitor.id)
        windows.remove(monitor)
    }

    inner class Facade : baaahs.ui.Facade() {
        fun createNew() {
            val ids = monitors.all.map { it.id }
            var nextId = 0L
            while (ids.contains(nextId)) nextId++
            val defaultMode = Mode(320, 200)
            val monitor = Monitor(nextId, "Screen $nextId", listOf(defaultMode), defaultMode, false)
            attachMonitor(monitor)
        }
    }
}

@Serializable
data class SimulatorSettings(
    val monitors: List<Monitor> = emptyList()
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