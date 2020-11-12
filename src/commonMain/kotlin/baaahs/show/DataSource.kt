package baaahs.show

import baaahs.ShowPlayer
import baaahs.camelize
import baaahs.gl.data.Feed
import baaahs.gl.glsl.GlslType
import baaahs.gl.patch.ContentType
import baaahs.gl.shader.InputPort
import baaahs.plugin.BeatLinkPlugin
import baaahs.plugin.CorePlugin
import baaahs.plugin.Plugin
import baaahs.plugin.Plugins
import baaahs.show.mutable.MutableGadgetControl
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic


interface DataSourceBuilder<T : DataSource> {
    /** The unique ID for this resource within a plugin. Should be CamelCase alphanums, like a class name. */
    val resourceName: String
    fun suggestDataSources(inputPort: InputPort): List<T> {
        return if (looksValid(inputPort)) {
            listOf(build(inputPort))
        } else emptyList()
    }
    fun looksValid(inputPort: InputPort): Boolean = false
    fun build(inputPort: InputPort): T
}

interface DataSource {
    val pluginPackage: String
    /** Short English name for this datasource. */
    val title: String

    // TODO: kill this
    fun isImplicit(): Boolean = false
    fun getType(): GlslType
    fun getContentType(): ContentType
    fun getVarName(id: String): String = "in_$id"

    // TODO: We shouldn't need to pass in the [Plugin] here; when plugins handle their own serialization,
    //  then the [DataSource] could be created with whatever dependencies it needs.
    fun createFeed(showPlayer: ShowPlayer, plugin: Plugin, id: String): Feed

    fun createFeed(showPlayer: ShowPlayer, plugins: Plugins, id: String): Feed {
        return createFeed(showPlayer, plugins.find(pluginPackage), id)
    }

    /** Yuck. Merge this with [createFeed]. */
    fun createFixtureFeed(): Feed = error("unsupported")

    fun suggestId(): String = title.camelize()

    fun buildControl(): MutableGadgetControl? = null

    fun appendDeclaration(buf: StringBuilder, varName: String) {
        if (!isImplicit())
            buf.append("uniform ${getType().glslLiteral} ${getVarName(varName)};\n")
    }

    companion object {
        val serialModule = SerializersModule {
            this.polymorphic(DataSource::class) {
//        CorePlugin.NoOp::class with CorePlugin.NoOp.serializer()
                subclass(CorePlugin.ResolutionDataSource::class, CorePlugin.ResolutionDataSource.serializer())
                subclass(CorePlugin.PreviewResolutionDataSource::class, CorePlugin.PreviewResolutionDataSource.serializer())
                subclass(CorePlugin.TimeDataSource::class, CorePlugin.TimeDataSource.serializer())
                subclass(CorePlugin.PixelCoordsTextureDataSource::class, CorePlugin.PixelCoordsTextureDataSource.serializer())
                subclass(CorePlugin.ModelInfoDataSource::class, CorePlugin.ModelInfoDataSource.serializer())
                subclass(CorePlugin.SliderDataSource::class, CorePlugin.SliderDataSource.serializer())
                subclass(CorePlugin.ColorPickerDataSource::class, CorePlugin.ColorPickerDataSource.serializer())
                subclass(CorePlugin.ColorPickerDataSource::class, CorePlugin.ColorPickerDataSource.serializer())
                subclass(CorePlugin.RadioButtonStripDataSource::class, CorePlugin.RadioButtonStripDataSource.serializer())
                subclass(CorePlugin.XyPadDataSource::class, CorePlugin.XyPadDataSource.serializer())

                subclass(BeatLinkPlugin.BeatLinkDataSource::class, BeatLinkPlugin.BeatLinkDataSource.serializer())
            }

//    polymorphic(ControlRef::class) {
//        SpecialControlRef::class with SpecialControlRef.serializer()
//        DataSourceRef::class with DataSourceRef.serializer()
//    }
        }
    }
}

enum class UpdateMode {
    /** The data from this data source will never change. */
    ONCE,

    /** The data from this data source may be different for each frame. */
    PER_FRAME,

    /** The data from this data source may be different for each fixture. */
    PER_FIXTURE
}