package baaahs.plugin

import baaahs.Gadget
import baaahs.app.ui.editor.PortLinkOption
import baaahs.fixtures.DeviceType
import baaahs.getBang
import baaahs.gl.patch.ContentType
import baaahs.gl.shader.InputPort
import baaahs.show.Control
import baaahs.show.DataSource
import baaahs.util.Logger
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.modules.SerializersModule

@Serializable
data class PluginRef(
    /** A unique ID for plugin. Should be lowercase alphanums and dots, like a package name. */
    val pluginId: String,
    /** A unique ID for a resource within a plugin. Should be CamelCase alphanums, like a class name. */
    val resourceName: String,
    val pluginIdNotSpecified: Boolean = false
) {
    fun toRef() = "$pluginId:$resourceName"

    companion object {
        fun hasPackage(identString: String): Boolean {
            return identString.contains(":")
        }

        fun from(identString: String): PluginRef {
            val result = Regex("(([\\w.]+):)?(\\w+)").matchEntire(identString)
            return if (result != null) {
                val (_, pluginId, resourceName) = result.destructured
                PluginRef(pluginId, resourceName)
            } else {
                PluginRef(Plugins.default, identString, pluginIdNotSpecified = true)
            }
        }
    }
}

class Plugins(plugins: List<Plugin>) {
    private val byPackage: Map<String, Plugin> = plugins.associateBy { it.packageName }

    val serialModule = SerializersModule {
        include(Gadget.serialModule)
//        include(portRefModule)
        include(Control.serialModule)
        include(DataSource.serialModule)
        include(DeviceType.serialModule)
//            contextual(DataSource::class, DataSourceSerializer(this@Plugins))
    }

    val json = Json { serializersModule = this@Plugins.serialModule }

    private fun findPlugin(pluginRef: PluginRef): Plugin {
        return byPackage[pluginRef.pluginId]
            ?: error("unknown plugin \"${pluginRef.pluginId}\"")
    }

    fun resolveContentType(name: String): ContentType? {
        val pluginRef = PluginRef.from(name)
        return try {
            if (pluginRef.pluginIdNotSpecified) {
                byPackage.values
                    .mapNotNull { it.resolveContentType(pluginRef.resourceName) }
                    .firstOrNull()
            } else {
                findPlugin(pluginRef).resolveContentType(pluginRef.resourceName)
            }
        } catch (e: Exception) {
            logger.debug { "Failed to resolve content type $name: ${e.message}" }
            null
        }
    }

    fun suggestContentTypes(inputPort: InputPort): Set<ContentType> {
        return byPackage.values.map { plugin -> plugin.suggestContentTypes(inputPort) }.flatten().toSet()
    }

    fun resolveDataSource(inputPort: InputPort): DataSource {
        return findPlugin(inputPort.pluginRef ?: error("no plugin specified")).resolveDataSource(inputPort)
    }

    fun suggestDataSources(
        inputPort: InputPort,
        suggestedContentTypes: Set<ContentType> = emptySet()
    ): List<PortLinkOption> {
        return byPackage.values.map { plugin ->
            plugin.suggestDataSources(inputPort, suggestedContentTypes)
        }.flatten()
    }

    // name would be in form:
    //   [baaahs.Core:]resolution
    //   [baaahs.Core:]time
    //   [baaahs.Core:]pixelCoords
    //   com.example.Plugin:data
    //   baaahs.SoundAnalysis:coq
    fun findDataSource(inputPort: InputPort): DataSource? {
        val pluginRef = inputPort.pluginRef
//        val (plugin, arg) = if (pluginRef != null) {
//            pluginRef
//        } else {
//            PluginType()
//        }
//        val result = pluginId?.let { Regex("(([\\w.]+):)?(\\w+)").matchEntire(it) }
        val plugin = byPackage[pluginRef!!.pluginId]

        val dataSource = pluginRef.resourceName.let {
            plugin!!.findDataSource(it, inputPort)
        }

        dataSource?.let {
//            val supportedTypes = dataSourceProvider.supportedTypes
//            if (!supportedTypes.contains(inputPort.type)) {
//                throw CompiledShader.LinkException(
//                    "can't set uniform ${inputPort.type} ${inputPort.title}: expected $supportedTypes)"
//                )
//            }
        }
        return dataSource
    }

//    fun createDataSourceProvider(dataSourceDescription: DataSourceDescription): DataSourceProvider?

    private fun getPlugin(packageName: String): Plugin {
        return byPackage[packageName]
            ?: error("no such plugin \"$packageName\"")
    }

    fun decodeDataSource(pluginId: String, pluginData: JsonObject): DataSource {
        TODO("not implemented")
    }

    operator fun plus(plugin: Plugin): Plugins {
        return Plugins(byPackage.values + plugin)
    }

    fun find(packageName: String): Plugin {
        return byPackage.getBang(packageName, "package")
    }

    companion object {
        fun safe(): Plugins = Plugins(listOf(CorePlugin()))
        val default = CorePlugin.id

        private val logger = Logger("Plugins")
    }
}