package baaahs.plugin

import baaahs.app.ui.editor.PortLinkOption
import baaahs.gl.patch.ContentType
import baaahs.gl.shader.InputPort
import baaahs.show.AddControlMenuItem
import baaahs.show.Control
import baaahs.show.DataSource
import baaahs.util.Clock
import kotlinx.serialization.KSerializer
import kotlinx.serialization.modules.PolymorphicModuleBuilder
import kotlin.reflect.KClass

interface Plugin {
    val packageName: String
    val title: String

    fun resolveDataSource(inputPort: InputPort): DataSource

    fun suggestDataSources(
        inputPort: InputPort,
        suggestedContentTypes: Set<ContentType>
    ): List<PortLinkOption>

    fun resolveContentType(type: String): ContentType?

    fun suggestContentTypes(inputPort: InputPort): Collection<ContentType>

    fun findDataSource(
        resourceName: String,
        inputPort: InputPort
    ): DataSource?

    fun getAddControlMenuItems(): List<AddControlMenuItem>

    fun getControlSerializers(): List<ClassSerializer<out Control>>
    fun getDataSourceSerializers(): List<ClassSerializer<out DataSource>>

    class ClassSerializer<T : Any>(val klass: KClass<T>, val serializer: KSerializer<T>) {
        fun register(polymorphicModuleBuilder: PolymorphicModuleBuilder<T>) {
            polymorphicModuleBuilder.subclass(klass, serializer)
        }
    }
}

inline fun <reified T : Any> classSerializer(serializer: KSerializer<T>) =
    Plugin.ClassSerializer(T::class, serializer)

interface PluginBuilder {
    val id: String
    fun build(pluginContext: PluginContext): Plugin
}

class PluginContext(
    val clock: Clock
)