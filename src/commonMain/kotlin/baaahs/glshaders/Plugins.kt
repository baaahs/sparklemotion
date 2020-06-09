package baaahs.glshaders

import baaahs.glsl.CompiledShader
import baaahs.ports.InputPortRef
import baaahs.ports.portRefModule
import baaahs.show.DataSource
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.modules.SerializersModule

class PluginRef(type: String) {
    val pluginId: String
    val resource: String

    init {
        val result = Regex("(([\\w.]+):)?(\\w+)").matchEntire(type)
        if (result != null) {
            val (_, pluginPackage, pluginArg) = result.destructured
            pluginId = pluginPackage
            resource = pluginArg
        } else {
            pluginId = Plugins.default
            resource = pluginId
        }
    }
}

class Plugins(private val byPackage: Map<String, Plugin>) {
    val serialModule = SerializersModule {
        include(portRefModule)
        include(dataSourceProviderModule)
//            contextual(DataSource::class, DataSourceSerializer(this@Plugins))
    }

    val json = Json(
        JsonConfiguration.Stable.copy(classDiscriminator = "#type"),
        context = serialModule
    )

    fun validateAndCanonicalize(uniformInput: InputPortRef): InputPortRef {
        // TODO
        return uniformInput
    }

    // name would be in form:
    //   [baaahs.Core:]resolution
    //   [baaahs.Core:]time
    //   [baaahs.Core:]uvCoords
    //   com.example.Plugin:data
    //   baaahs.SoundAnalysis:coq
    fun findDataSource(inputPortRef: InputPortRef): DataSource? {
        val pluginRef = inputPortRef.pluginId?.let { PluginRef(it) }
//        val (plugin, arg) = if (pluginRef != null) {
//            pluginRef
//        } else {
//            PluginType()
//        }
//        val result = pluginId?.let { Regex("(([\\w.]+):)?(\\w+)").matchEntire(it) }
        val plugin = byPackage[pluginRef!!.pluginId]

        val dataSourceProvider = pluginRef.resource.let {
            plugin!!.findDataSource(it, inputPortRef)
        }

        dataSourceProvider?.let {
            val supportedTypes = dataSourceProvider.supportedTypes
            if (!supportedTypes.contains(inputPortRef.type)) {
                throw CompiledShader.LinkException(
                    "can't set uniform ${inputPortRef.type} ${inputPortRef.title}: expected $supportedTypes)"
                )
            }
        }
        return dataSourceProvider
    }

//    fun createDataSourceProvider(dataSourceDescription: DataSourceDescription): DataSourceProvider?

    private fun getPlugin(packageName: String): Plugin {
        return byPackage[packageName]
            ?: error("no such plugin \"$packageName\"")
    }

    fun decodeDataSource(pluginId: String, pluginData: JsonObject): DataSource {
        TODO("not implemented")
    }

    companion object {
        val default = "baaahs.Core"
        private val plugins = Plugins(
            listOf(CorePlugin())
                .associateBy(Plugin::packageName)
        )

        fun safe(): Plugins {
            return Plugins(mapOf(default to CorePlugin()))
        }

        fun findAll(): Plugins {
            return plugins
        }
    }
}