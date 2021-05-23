package baaahs.util

import baaahs.Brain
import baaahs.BrainManager
import baaahs.Mapper
import baaahs.MapperUi
import baaahs.net.BrowserUdpProxy
import baaahs.show.live.ShowOpener
import baaahs.sim.FakeNetwork
import kotlin.reflect.KClass

@Suppress("MemberVisibilityCanBePrivate", "RemoveExplicitTypeArguments", "unused")
object LoggerConfig {
    val stringLevels = mapOf<String, LogLevel>(

    )

    val classLevels = mapOf<KClass<*>, LogLevel>(
        ShowOpener::class to LogLevel.DEBUG,
        Mapper::class to LogLevel.DEBUG,
        MapperUi::class to LogLevel.DEBUG,
        Brain::class to LogLevel.DEBUG,
        BrainManager::class to LogLevel.DEBUG,
        BrowserUdpProxy::class to LogLevel.DEBUG,
        FakeNetwork::class to LogLevel.DEBUG,
    )

    private val allLevels = (stringLevels + classLevels.mapKeys { (k, _) -> k.simpleName })
        .toMutableMap()

    @JsName("levelFor")
    fun levelFor(id: String) = allLevels[id] ?: LogLevel.WARN

    @JsName("setLevel")
    fun setLevel(id: String, level: String?) {
        if (level == null) {
            allLevels.remove(id)
        } else {
            allLevels[id] = LogLevel.valueOf(level)
        }
    }
}