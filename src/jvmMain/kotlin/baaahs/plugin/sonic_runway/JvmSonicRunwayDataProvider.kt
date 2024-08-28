package baaahs.plugin.sonic_runway

import baaahs.Color
import baaahs.plugin.PluginContext
import baaahs.util.globalLaunch
import kotlinx.coroutines.delay
import kotlin.random.Random

internal actual fun createServerSonicRunwayDataProvider(pluginContext: PluginContext): SonicRunwayDataProvider {
    return JvmSonicRunwayThing(pluginContext)
}

class JvmSonicRunwayThing(
    private val pluginContext: PluginContext
) : SonicRunwayDataProvider {
    private val listeners = mutableListOf<SonicRunwayListener>()
    private val colors = Array<Color>(5) { Color.BLACK }

    init {
        globalLaunch {
            while (true) {
                colors[Random.nextInt(5)] = Color.random()
                notify(SonicRunwayData(colors.toList(), pluginContext.clock.now()))
                delay(100)
            }
        }
    }

    private fun notify(sonicRunwayData: SonicRunwayData) {
        listeners.forEach { it.onUpdate(sonicRunwayData) }
    }

    override fun listen(listener: SonicRunwayListener) {
        listeners.add(listener)
    }

    override fun unlisten(listener: SonicRunwayListener) {
        listeners.remove(listener)
    }
}