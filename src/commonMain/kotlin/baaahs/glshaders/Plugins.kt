package baaahs.glshaders

import baaahs.ShowContext

class Plugins(private val byPackage: Map<String, Plugin>) {
    // name would be in form:
    //   [baaahs.Core:]resolution
    //   [baaahs.Core:]time
    //   [baaahs.Core:]uvCoords
    //   com.example.Plugin:data
    //   baaahs.SoundAnalysis:coq
    fun matchUniformProvider(
        uniformPort: Patch.UniformPort,
        program: GlslProgram,
        showContext: ShowContext
    ): GlslProgram.UniformProvider? {
        val pluginId = uniformPort.pluginId
        val result = Regex("(([\\w.]+):)?(\\w+)").matchEntire(pluginId)
        val (plugin, dataName) = if (result != null) {
            val (_, pluginPackage, dataName) = result.destructured
            val plugin = findAll().getPlugin(pluginPackage)
            plugin to dataName
        } else {
            findAll().getPlugin(default) to pluginId
        }

        return plugin.matchUniformProvider(dataName, uniformPort, program, showContext)
    }

    private fun getPlugin(packageName: String): Plugin {
        return byPackage[packageName]
            ?: error("no such plugin \"$packageName\"")
    }

    companion object {
        private val default = "baaahs.Core"
        private val plugins = Plugins(
            listOf(CorePlugin(), GadgetsPlugin())
                .associateBy(Plugin::packageName)
        )

        fun findAll(): Plugins {
            return plugins
        }
    }
}