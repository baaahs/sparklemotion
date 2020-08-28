package baaahs.show.live

import baaahs.getBang
import baaahs.gl.glsl.GlslAnalyzer
import baaahs.gl.shader.OpenShader
import baaahs.show.*

open class ShowComponents(show: Show) {
    val shaders = mutableMapOf<String, Shader>()
    val shaderInstances = mutableMapOf<String, ShaderInstance>()
    val controls = mutableMapOf<String, Control>()
    val dataSources = mutableMapOf<String, DataSource>()
    val patchHolders = mutableMapOf<String, PatchHolder>()

    init {
        object : ShowVisitor() {
            override fun visitControl(control: Control, depth: Int) {
                controls[control.id] = control
                super.visitControl(control, depth)
            }

            override fun visitDataSource(dataSource: DataSource) {
                dataSources[dataSource.id] = dataSource
                super.visitDataSource(dataSource)
            }

            override fun visitShaderInstance(shaderInstance: ShaderInstance) {
                shaderInstances[shaderInstance.id] = shaderInstance
                super.visitShaderInstance(shaderInstance)
            }

            override fun visitShader(shader: Shader) {
                shaders[shader.id] = shader
                super.visitShader(shader)
            }
        }.visitShow(show)
    }
}

class ShowOpener(
    private val glslAnalyzer: GlslAnalyzer,
    show: Show
): ShowComponents(show), ShowContext {

//    val controls = show.controls.mapValues { (_, control) ->
//        control.open(this)
//    }
    override val allControls: List<Control> get() = controls.values.toList()
    override val allDataSources: List<DataSource> get() = dataSources.values.toList()

    private val openShaders = shaders.mapValues { (_, shader) ->
        glslAnalyzer.openShader(shader)
    }

    override fun getControl(it: String): Control = controls.getBang(it, "control")

    override fun getDataSource(id: String): DataSource = dataSources.getBang(id, "data source")

    override fun getOpenShader(shader: Shader): OpenShader = openShaders.getBang(shader.id, "open shader")

    override fun release() {
//        allControls.forEach { it.release() }
//        openShaders.forEach { it.release() }
//        allShaderInstances.forEach { it.release() }
    }
}

abstract class ShowVisitor {
    open fun visitShow(show: Show) {
        visitPatchHolder(show, 0)

        show.scenes.forEach { scene ->
            visitPatchHolder(scene, 1)

            scene.patchSets.forEach { patchSet ->
                visitPatchHolder(patchSet, 2)
            }
        }
    }

    open fun visitPatchHolder(patchHolder: PatchHolder, depth: Int) {
        patchHolder.patches.forEach { visitPatch(it, depth + 1) }

        patchHolder.controlLayout.forEach { (_, controls) ->
            controls.forEach { visitControl(it, depth + 2) }
        }
    }

    open fun visitControl(control: Control, depth: Int) {
        if (control is GadgetControl) {
            visitDataSource(control.controlledDataSource)
        }
    }

    open fun visitDataSource(dataSource: DataSource) {}

    open fun visitPatch(patch: Patch, depth: Int) {
        visitSurfaces(patch.surfaces)
        patch.shaderInstances.forEach { visitShaderInstance(it) }
    }

    open fun visitShader(shader: Shader) {}

    open fun visitShaderInstance(shaderInstance: ShaderInstance) {
        visitShader(shaderInstance.shader)
        shaderInstance.incomingLinks.values.forEach { sourcePort ->
            when (sourcePort) {
                is DataSourceSourcePort -> visitDataSource(sourcePort.dataSource)
                is ShaderOutSourcePort -> visitShaderInstance(sourcePort.shaderInstance)
                is ShaderChannelSourcePort -> visitShaderChannel(sourcePort.shaderChannel)
                is ConstSourcePort -> { }
                is NoOpSourcePort -> { }
            }
        }
    }

    open fun visitShaderChannel(shaderChannel: ShaderChannel) {}

    open fun visitSurfaces(surfaces: Surfaces) {}
}