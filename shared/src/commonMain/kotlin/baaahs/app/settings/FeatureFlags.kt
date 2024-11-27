package baaahs.app.settings

import baaahs.PubSub
import baaahs.ui.Observable
import kotlinx.serialization.Serializable

@Serializable
data class FeatureFlags(
    val shows: DocumentFeatureFlags = DocumentFeatureFlags(),
    val scenes: DocumentFeatureFlags = DocumentFeatureFlags(),
) {
    companion object {
        val JVM = FeatureFlags(
            DocumentFeatureFlags(autoSync = true, autoSave = false, multiDoc = true),
            DocumentFeatureFlags(autoSync = true, autoSave = false, multiDoc = true)
        )
        val MOBILE = FeatureFlags(
            DocumentFeatureFlags(autoSync = true, autoSave = true, multiDoc = false),
            DocumentFeatureFlags(autoSync = true, autoSave = true, multiDoc = false)
        )
    }
}

@Serializable
data class DocumentFeatureFlags(
    val autoSync: Boolean = true,
    val autoSave: Boolean = false,
    val multiDoc: Boolean = true,
)

abstract class Provider<T> : Observable() {
    abstract fun get(): T
}

object FeatureFlagsManager {
    private val topic =
        PubSub.Topic("featureFlagsState", FeatureFlags.serializer())

    class Server(pubSub: PubSub.Server, initialFeatureFlags: FeatureFlags) : Provider<FeatureFlags>() {
        private val featureFlags by pubSub.state(topic, initialFeatureFlags, allowClientUpdates = false) {
            this@Server.notifyChanged()
        }

        override fun get() = featureFlags
    }

    class Client(pubSub: PubSub.Client) : Provider<FeatureFlags>() {
        private val featureFlags by pubSub.state(topic, FeatureFlags()) {
            this@Client.notifyChanged()
        }

        override fun get() = featureFlags
    }
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