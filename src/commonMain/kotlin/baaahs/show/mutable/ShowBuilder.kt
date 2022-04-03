package baaahs.show.mutable

import baaahs.show.*
import baaahs.util.UniqueIds

class ShowBuilder {
    private val panelIds = UniqueIds<Panel>()
    private val controlIds = UniqueIds<Control>()
    private val dataSourceIds = UniqueIds<DataSource>()
    private val shaderIds = UniqueIds<Shader>()
    private val patchIds = UniqueIds<Patch>()

    fun idFor(panel: Panel): String {
        return panelIds.idFor(panel) { panel.suggestId() }
    }

    fun idFor(control: Control): String {
        return controlIds.idFor(control) { control.suggestId() }
    }

    fun idFor(dataSource: DataSource): String {
        return dataSourceIds.idFor(dataSource) { dataSource.suggestId() }
    }

    fun idFor(shader: Shader): String {
        return shaderIds.idFor(shader) { shader.suggestId() }
    }

    fun idFor(patch: Patch): String {
        return patchIds.idFor(patch) { "${patch.shaderId}-patch" }
    }

    fun getControls(): Map<String, Control> = controlIds.all()
    fun getDataSources(): Map<String, DataSource> = dataSourceIds.all()
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
            existingDataSources: Map<String, DataSource>
        ): ShowBuilder = ShowBuilder().apply {
            controlIds.putAll(existingControls)
            dataSourceIds.putAll(existingDataSources)
        }
    }
}