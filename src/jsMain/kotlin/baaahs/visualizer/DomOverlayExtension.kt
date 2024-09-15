package baaahs.visualizer

import baaahs.clamp
import baaahs.geom.toThreeEuler
import baaahs.model.Model
import baaahs.model.ModelUnit
import baaahs.sim.SimulationEnv
import baaahs.visualizer.entity.ItemVisualizer
import three.*
import three.addons.CSS3DObject
import three.addons.CSS3DRenderer
import web.html.HTMLElement
import kotlin.math.PI

class DomOverlayExtension(
    private val renderDomElement: (ItemVisualizer<Model.Entity>) -> HTMLElement
) : Extension(DomOverlayExtension::class) {
    private val domRenderer = CSS3DRenderer()
    val domElement = domRenderer.domElement
    private val domScene = Scene().apply { name = "${this::class.simpleName} Scene" }
    var groupContext: Object3D = domScene
    private val domNodes = mutableMapOf<ItemVisualizer<*>, DomNode>()

    override fun VisualizerContext.attach() {
    }

    fun clear() {
        console.log("domScene.clear() (had ${domScene.children.size} children)")
        domNodes.clear()
        domScene.clear()
    }

    fun createDomElement(itemVisualizer: ItemVisualizer<Model.Entity>) {
        domNodes[itemVisualizer]?.objectCSS?.removeFromParent()
        domNodes[itemVisualizer] = DomNode(itemVisualizer).also {
            groupContext.add(it.objectCSS)
        }
    }

    override fun VisualizerContext.resize(width: Int, height: Int) {
        domRenderer.setSize(width, height)
    }

    override fun VisualizerContext.render() {
        domNodes.forEach { (itemVisualizer, domNode) ->
            itemVisualizer.normal?.let { normal ->
                // Transform normal from object to world space.
                val worldNormal = normal.toVector3().transformDirection(itemVisualizer.obj.matrixWorld)

                // Camera's looking direction.
                val cameraDirection = camera.getWorldDirection(Vector3()).negate()

                // Calculate dot product and determine opacity.
                val dot = worldNormal.dot(cameraDirection)
                val opacity = dot.clamp(0.0, 1.0)
                domNode.div.style.opacity = opacity.toString()
            }
        }
        domRenderer.render(domScene, camera)
    }

    override fun VisualizerContext.release() {
    }

    inner class DomNode(private val itemVisualizer: ItemVisualizer<Model.Entity>) {
        val div = renderDomElement(itemVisualizer)
        val objectCSS = CSS3DObject(div).also { domObj ->
            itemVisualizer.obj.updateMatrixWorld()
            val matrixWorld = itemVisualizer.obj.matrixWorld
            with(itemVisualizer.item) {
                // TODO: these should be multiplied by the item's world matrix.
                domObj.position.copy(centroid.toVector3().applyMatrix4(matrixWorld))
                val eulerRotation = rotation.toThreeEuler()
                val q = Quaternion().setFromEuler(eulerRotation)
                q.premultiply(Quaternion().setFromRotationMatrix(matrixWorld))
                val worldEulerRotation = Euler().setFromQuaternion(q)
                domObj.rotation.copy(worldEulerRotation)
                itemVisualizer.normal?.let { normal ->
                    // Direct the CSS3DObject to look at a point along the normal vector from the centroid
                    val lookAtTarget = Vector3().addVectors(domObj.position, normal.toVector3())
                    domObj.lookAt(lookAtTarget)
                    // Correct the orientation to ensure text is upright, may need to adjust based on your scene
                    domObj.rotation.z = PI  // Rotate 90 degrees around Z-axis if necessary
                }
            }
        }
    }

    inner class DomOverlayEntityAdapter(
        simulationEnv: SimulationEnv,
        units: ModelUnit,
        isEditing: Boolean
    ) : EntityAdapter(simulationEnv, units, isEditing) {
        override fun createVisualizer(entity: Model.Entity): ItemVisualizer<Model.Entity> =
            super.createVisualizer(entity).also {
                createDomElement(it)
            }

        override fun createOrUpdateVisualizer(
            oldVisualizer: ItemVisualizer<Model.Entity>?,
            entity: Model.Entity
        ): ItemVisualizer<Model.Entity> {
            return super.createOrUpdateVisualizer(oldVisualizer, entity).also {
                if (it === oldVisualizer) {
                    createDomElement(it)
                }
            }
        }

        override fun <T> withinGroup(title: String, block: () -> T): T {
            val restoreGroupContext = groupContext
            groupContext = Group().also {
                it.name = title
                println("Create group $title, adding it to ${restoreGroupContext.name} (${restoreGroupContext.children.size} children)")
                restoreGroupContext.add(it)
            }
            println("withinGroup($title): outer is ${restoreGroupContext.name}, inner is ${groupContext.name}")
            return block().also {
                println("withinGroup($title): restoring group context to ${restoreGroupContext.name} (${restoreGroupContext.children.size} children)")
                groupContext = restoreGroupContext
            }
        }
    }
}