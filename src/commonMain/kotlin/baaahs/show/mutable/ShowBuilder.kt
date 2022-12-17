package baaahs.show.mutable

import baaahs.show.*
import baaahs.util.UniqueIds

class ShowBuilder {
    private val panelIds = UniqueIds<Panel>()
    private val controlIds = UniqueIds<Control>()
    private val feedIds = UniqueIds<Feed>()
    private val shaderIds = UniqueIds<Shader>()
    private val patchIds = UniqueIds<Patch>()

    fun idFor(panel: Panel): String {
        return panelIds.idFor(panel) { panel.suggestId() }
    }

    fun idFor(control: Control): String {
        return controlIds.idFor(control) { control.suggestId() }
    }

    fun idFor(feed: Feed): String {
        return feedIds.idFor(feed) { feed.suggestId() }
    }

    fun idFor(shader: Shader): String {
        return shaderIds.idFor(shader) { shader.suggestId() }
    }

    fun idFor(patch: Patch): String {
        return patchIds.idFor(patch) { "${patch.shaderId}-patch" }
    }

    fun getControls(): Map<String, Control> = controlIds.all()
    fun getDataSources(): Map<String, Feed> = feedIds.all()
    fun getShaders(): Map<String, Shader> = shaderIds.all()
    fun getPatches(): Map<String, Patch> = patchIds.all()

    // Make sure we include data source dependencies, otherwise their feeds aren't opened.
    // This is pretty janky, find a better way.
    fun includeDependencyDataSources() {
        getDataSources().forEach { (_, dataSource) ->
            dataSource.dependencies.forEach { (_, dependency) ->
                idFor(dependency)
            }
        }
    }

    companion object {
        fun forImplicitControls(
            existingControls: Map<String, Control>,
            existingDataSources: Map<String, Feed>
        ): ShowBuilder = ShowBuilder().apply {
            controlIds.putAll(existingControls)
            feedIds.putAll(existingDataSources)
        }
    }
}