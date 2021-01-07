package baaahs.util

import baaahs.show.live.ShowOpener
import kotlin.reflect.KClass

@Suppress("MemberVisibilityCanBePrivate", "RemoveExplicitTypeArguments", "unused")
object LoggerConfig {
    val stringLevels = mapOf<String, LogLevel>(

    )

    val classLevels = mapOf<KClass<*>, LogLevel>(
        ShowOpener::class to LogLevel.DEBUG
    )

    private val allLevels = (stringLevels + classLevels.mapKeys { (k, _) -> k.simpleName })
        .toMutableMap()

    fun levelFor(id: String) = allLevels[id] ?: LogLevel.WARN

    fun setLevel(id: String, level: String?) {
        if (level == null) {
            allLevels.remove(id)
        } else {
            allLevels[id] = LogLevel.valueOf(level)
        }
    }
}