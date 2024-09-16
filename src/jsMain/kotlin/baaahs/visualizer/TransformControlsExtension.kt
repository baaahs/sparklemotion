package baaahs.visualizer

import three.examples.jsm.controls.TransformControls

class TransformControlsExtension : Extension(TransformControlsExtension::class) {
    val transformControls by attachment {
        TransformControls(camera, canvas).also {
            it.space = "world"
            it.enabled = false
            it.size = 1.0
        }
    }

    override fun VisualizerContext.attach() {
        scene.add(transformControls)
    }

    override fun VisualizerContext.isInUserInteraction(): Boolean =
        transformControls.dragging

    override fun VisualizerContext.beforeRender() {
        transformControls.updateMatrixWorld()
    }

    override fun VisualizerContext.detach() {
        scene.remove(transformControls)
    }
}