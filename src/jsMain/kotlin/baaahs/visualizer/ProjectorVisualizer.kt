package baaahs.visualizer

import baaahs.model.Projector
import three.js.BoxGeometry
import three.js.Group
import three.js.Mesh
import three.js.MeshBasicMaterial
import three_ext.clear

class ProjectorVisualizer(
    private val projector: Projector,
    entityAdapter: EntityAdapter
) : BaseEntityVisualizer<Projector>(projector) {
    private val wrapper = Group()
    override val obj get() = wrapper

    private val projectionSurface = Mesh(
        BoxGeometry(10, 10, 1),
        MeshBasicMaterial()
    )

    init { update(projector) }

    override fun isApplicable(newItem: Any): Projector? =
        newItem as? Projector

    override fun update(newItem: Projector) {
        super.update(newItem)

        wrapper.clear()
        wrapper.name = "${projector.name} Wrapper"

        wrapper.add(projectionSurface)
    }

    override fun applyStyle(entityStyle: EntityStyle) {
//        TODO("not implemented")
    }
}