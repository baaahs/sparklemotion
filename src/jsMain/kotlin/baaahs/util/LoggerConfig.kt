package baaahs.util

import baaahs.gl.KglTracer
import baaahs.plugin.midi.JsMidiSource
import baaahs.plugin.midi.JsMidiSystem
import baaahs.show.live.ShowOpener
import kotlin.reflect.KClass

@Suppress("MemberVisibilityCanBePrivate", "RemoveExplicitTypeArguments", "unused")
object LoggerConfig {
    var defaultLevel = LogLevel.WARN

    val stringLevels = mapOf<String, LogLevel>(

    )

    val classLevels = mapOf<KClass<*>, LogLevel>(
//        PubSub::class to LogLevel.DEBUG,
        KglTracer::class to LogLevel.DEBUG,
        ShowOpener::class to LogLevel.DEBUG,
        JsMidiSystem::class to LogLevel.DEBUG,
        JsMidiSource::class to LogLevel.DEBUG,
    )

    private val allLevels = (stringLevels + classLevels.mapKeys { (k, _) -> k.simpleName })
        .toMutableMap()

    @JsName("levelFor")
    fun levelFor(id: String) = allLevels[id] ?: defaultLevel

    @JsName("setLevel")
    fun setLevel(id: String, level: String?) {
        if (level == null) {
            allLevels.remove(id)
        } else {
            allLevels[id] = LogLevel.valueOf(level)
        }
    }

    @JsName("setDefault")
    fun setDefault(level: String) {
        defaultLevel = LogLevel.valueOf(level)
    }
}