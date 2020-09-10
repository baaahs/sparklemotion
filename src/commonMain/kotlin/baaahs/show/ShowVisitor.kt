//package baaahs.show
//
//abstract class ShowVisitor {
//    open fun visitShow(show: Show) {
//        visitPatchHolder(show)
//
//        visitControl(ButtonGroupControl("Scenes"))
//        show.scenes.forEach { scene ->
//            visitPatchHolder(scene)
//
//            scene.patchSets.forEach { patchSet ->
//                visitPatchHolder(patchSet)
//            }
//        }
//    }
//
//    open fun visitPatchHolder(patchHolder: PatchHolder) {
//        patchHolder.patches.forEach { visitPatch(it, depth + 1) }
//
//        patchHolder.controlLayout.forEach { (_, controls) ->
//            controls.forEach { visitControl(it, depth + 2) }
//        }
//    }
//
//    open fun visitControl(control: Control, depth: Int) {
//        if (control is GadgetControl) {
//            visitDataSource(control.controlledDataSource)
//        }
//    }
//
//    open fun visitDataSource(dataSource: DataSource) {}
//
//    open fun visitPatch(patch: Patch, depth: Int) {
//        visitSurfaces(patch.surfaces)
//        patch.shaderInstances.forEach { visitShaderInstance(it) }
//    }
//
//    open fun visitShader(shader: Shader) {}
//
//    open fun visitShaderInstance(shaderInstance: ShaderInstance) {
//        visitShader(shaderInstance.shader)
//        shaderInstance.incomingLinks.values.forEach { sourcePort ->
//            when (sourcePort) {
//                is DataSourceSourcePort -> visitDataSource(sourcePort.dataSource)
//                is ShaderOutSourcePort -> visitShaderInstance(sourcePort.shaderInstance)
//                is ShaderChannelSourcePort -> visitShaderChannel(sourcePort.shaderChannel)
//                is ConstSourcePort -> {
//                }
//                is NoOpSourcePort -> {
//                }
//            }
//        }
//    }
//
//    open fun visitShaderChannel(shaderChannel: ShaderChannel) {}
//
//    open fun visitSurfaces(surfaces: Surfaces) {}
//}