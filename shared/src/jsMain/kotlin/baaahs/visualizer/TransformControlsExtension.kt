package baaahs.visualizer

import baaahs.visualizer.entity.ItemVisualizer
import three.examples.jsm.controls.TransformControls

class TransformControlsExtension : Extension(TransformControlsExtension::class) {
    val transformControls by attachment {
        TransformControls(camera, canvas).also {
            it.space = "world"
            it.enabled = false
            it.size = 1.0
        }
    }

    var enabled: Boolean = false
        set(value) {
            field = value
            update()
        }

    private var selection: ItemVisualizer<*>? = null
        set(value) {
            field = value
            if (value == null) transformControls.detach()
            else transformControls.attach(value.obj)
            update()
        }

    private fun update() {
        transformControls.enabled = enabled && selection != null
        transformControls.visible = enabled && selection != null
    }

    override fun VisualizerContext.attach() {
        scene.add(transformControls)
    }

    override fun VisualizerContext.isInUserInteraction(): Boolean =
        transformControls.dragging

    override fun VisualizerContext.beforeRender() {
        transformControls.updateMatrixWorld()
    }

    override fun onSelectionChange(selection: ItemVisualizer<*>?, priorSelection: ItemVisualizer<*>?) {
        this.selection = selection
    }

    override fun VisualizerContext.detach() {
        scene.remove(transformControls)
    }
}