package baaahs.util

import baaahs.gl.KglTracer
import baaahs.show.live.ShowOpener
import kotlin.reflect.KClass

@Suppress("MemberVisibilityCanBePrivate", "RemoveExplicitTypeArguments", "unused")
object LoggerConfig {
    val stringLevels = mapOf<String, LogLevel>(

    )

    val classLevels = mapOf<KClass<*>, LogLevel>(
        KglTracer::class to LogLevel.DEBUG,
        ShowOpener::class to LogLevel.DEBUG
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