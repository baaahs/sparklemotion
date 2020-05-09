package baaahs.glshaders

import baaahs.ShowContext
import baaahs.glsl.GlslContext

class Plugins(private val byPackage: Map<String, Plugin>) {
    // name would be in form:
    //   [baaahs.Core:]resolution
    //   [baaahs.Core:]time
    //   [baaahs.Core:]uvCoords
    //   com.example.Plugin:data
    //   baaahs.SoundAnalysis:coq
    fun matchUniformProvider(
        uniformPort: Patch.UniformPortRef,
        showContext: ShowContext,
        glslContext: GlslContext
    ): GlslProgram.DataSourceProvider? {
        val pluginId = uniformPort.pluginId
        val result = pluginId?.let { Regex("(([\\w.]+):)?(\\w+)").matchEntire(it) }
        val (plugin, arg) = if (result != null) {
            val (_, pluginPackage, pluginArg) = result.destructured
            getPlugin(pluginPackage) to pluginArg
        } else {
            getPlugin(default) to pluginId
        }

        return arg?.let { plugin.matchUniformProvider(it, uniformPort, showContext, glslContext) }
    }

    private fun getPlugin(packageName: String): Plugin {
        return byPackage[packageName]
            ?: error("no such plugin \"$packageName\"")
    }

    companion object {
        private val default = "baaahs.Core"
        private val plugins = Plugins(
            listOf(CorePlugin())
                .associateBy(Plugin::packageName)
        )

        fun findAll(): Plugins {
            return plugins
        }
    }
}