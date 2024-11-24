package baaahs.app.settings

import baaahs.ui.Observable
import kotlinx.serialization.Serializable

@Serializable
data class FeatureFlags(
    val shows: DocumentFeatureFlags = DocumentFeatureFlags(),
    val scenes: DocumentFeatureFlags = DocumentFeatureFlags(),
)

@Serializable
data class DocumentFeatureFlags(
    val autoSync: Boolean = true,
    val autoSave: Boolean = true,
    val canSwitch: Boolean = false,
)

abstract class Provider<T> : Observable() {
    abstract fun get(): T
}

open class ObservableProvider<T>(
    initialValue: T
) : Provider<T>() {
    var value: T = initialValue
        set(value) {
            field = value
            notifyChanged()
        }

    override fun get(): T = value
}