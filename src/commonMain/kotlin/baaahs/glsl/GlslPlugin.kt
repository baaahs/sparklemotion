package baaahs.glsl

import baaahs.shows.GlslShow
import com.danielgergely.kgl.Kgl
import kotlinx.serialization.json.JsonObject

interface GlslPlugin {
    val name: String

    fun createDataSource(config: JsonObject): DataSource

    interface DataSource : GlslShow.DataSource

    fun forProgram(gl: Kgl, program: Program): ProgramContext

    interface ProgramContext {
        val plugin: GlslPlugin

        val glslPreamble: String

        fun afterCompile() {}

        fun forRenderer(): RendererContext?

        fun release() {}
    }

    interface RendererContext {
        val plugin: GlslPlugin

        fun before() {}

        fun after() {}

        fun release() {}
    }

    companion object {
        val slider = GadgetDataSourceProvider("Slider")
    }

    abstract class DataSourceProvider(val category: Category, val name: String) {
        companion object {
            fun from(category: String, name: String): DataSourceProvider {
                return try {
                    Category.valueOf(category)
                } catch (e: Exception) {
                    throw IllegalArgumentException("unsupported category $category")
                }.createDataSource(name)
            }
        }

        enum class Category {
            GADGET {
                override fun createDataSource(name: String): DataSourceProvider = GadgetDataSourceProvider(name)
            },

            PLUGIN {
                override fun createDataSource(name: String): DataSourceProvider = PluginDataSourceProvider(name)
            };

            abstract fun createDataSource(name: String): DataSourceProvider
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is DataSourceProvider) return false

            if (category != other.category) return false
            if (name != other.name) return false

            return true
        }

        override fun hashCode(): Int {
            var result = category.hashCode()
            result = 31 * result + name.hashCode()
            return result
        }
    }

    class GadgetDataSourceProvider(gadgetType: String) :
        DataSourceProvider(Category.GADGET, gadgetType)

    class PluginDataSourceProvider(name: String) :
        DataSourceProvider(Category.PLUGIN, name)
}