package baaahs.glshaders

class Plugins(private val byPackage: Map<String, Plugin>) {
    // name would be in form:
    //   [baaahs.Core:]resolution
    //   [baaahs.Core:]time
    //   [baaahs.Core:]uvCoords
    //   com.example.Plugin:data
    //   baaahs.SoundAnalysis:coq
    fun matchUniformProvider(type: String, name: String, program: GlslProgram): GlslProgram.UniformProvider? {
        val result = Regex("(([\\w.]+):)?(\\w+)").matchEntire(name)
        val (plugin, dataName) = if (result != null) {
            val (_, pluginPackage, dataName) = result.destructured
            val plugin = findAll().getPlugin(pluginPackage)
            plugin to dataName
        } else {
            findAll().getPlugin(default) to name
        }

        return plugin.matchUniformProvider(type, dataName, program)
    }

    private fun getPlugin(packageName: String): Plugin {
        return byPackage[packageName] ?: error("no such plugin \"$packageName\"")
    }

    companion object {
        private val default = "baaahs.Core"
        private val plugins = Plugins(mapOf(default to CorePlugin()))

        fun findAll(): Plugins {
            return plugins
        }
    }
}