package baaahs.visualizer

import baaahs.document
import baaahs.util.Clock
import baaahs.visualizer.entity.ItemVisualizer
import baaahs.visualizer.entity.itemVisualizer
import three.Object3D
import web.html.HTMLElement
import web.html.HTMLSpanElement

class Visualizer(
    clock: Clock
) : IVisualizer, BaseVisualizer(clock) {
    override val facade = Facade()

    private val selectionSpan = document.createElement("span") as HTMLSpanElement

    var selectedEntity: ItemVisualizer<*>? = null

    private var container: HTMLElement? = null
        set(value) {
            if (value != null) {
                field = value
                containerAttached()
            } else {
                containerWillDetach()
                field = value
            }
        }

    private val itemVisualizers = arrayListOf<ItemVisualizer<*>>()

    init {
        addPrerenderListener {
            itemVisualizers.forEach { it.applyStyles() }
        }
    }

    private fun containerAttached() {
        container!!.appendChild(canvas)
        container!!.appendChild(selectionSpan)

        resize()
        startRendering()
    }

    private fun containerWillDetach() {
        container?.removeChild(canvas)
        container?.removeChild(selectionSpan)
        stopRendering()
    }

    override fun clear() {
        itemVisualizers.clear()
        super.clear()
    }

    override fun add(itemVisualizer: ItemVisualizer<*>) {
        itemVisualizers.add(itemVisualizer)
        scene.add(itemVisualizer.obj)
        fitCamera = true
        sceneNeedsUpdate = true
    }

    override fun onSelectionChange(obj: Object3D?, priorObj: Object3D?) {
        val vizObj = findParentEntityVisualizer(obj)

        if (vizObj == null) {
            selectionSpan.style.display = "none"
            selectionSpan.innerText = ""
        } else {
            val entityVisualizer = vizObj.itemVisualizer!!
            entityVisualizer.selected = true
            console.log("Selecting ${entityVisualizer.title}")
            selectionSpan.style.display = "inherit"
            selectionSpan.innerText = "Selected: ${entityVisualizer.title}"
//            entityVisualizer.obj.let { transformControls.attach(it) }
        }
        super.onSelectionChange(obj, priorObj)
    }

    private fun findParentEntityVisualizer(obj: Object3D?): Object3D? {
        var curObj = obj
        while (curObj != null && curObj.itemVisualizer == null) {
            curObj = curObj.parent
        }
        return curObj
    }

    public override fun stopRendering() {
        super.stopRendering()
    }

    inner class Facade : BaseVisualizer.Facade() {
        val haveScene get() = this@Visualizer.itemVisualizers.isNotEmpty()
        val selectedEntity get() = this@Visualizer.selectedEntity

        var container: HTMLElement?
            get() = this@Visualizer.container
            set(value) {
                this@Visualizer.container = value
            }

        var units by this@Visualizer::units
        fun clear() = this@Visualizer.clear()

        fun select(itemVisualizer: ItemVisualizer<*>) {
            this@Visualizer.selectedEntity = itemVisualizer
        }
    }
}