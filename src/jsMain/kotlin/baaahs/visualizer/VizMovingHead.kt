package baaahs.visualizer

import baaahs.Color
import baaahs.Config
import baaahs.geom.Vector3F
import baaahs.model.MovingHead
import baaahs.sim.FakeDmxUniverse
import info.laht.threekt.THREE
import info.laht.threekt.geometries.ConeBufferGeometry
import info.laht.threekt.materials.MeshBasicMaterial
import info.laht.threekt.math.Matrix4
import info.laht.threekt.objects.Mesh
import info.laht.threekt.scenes.Scene

class VizMovingHead(private val movingHead: MovingHead, dmxUniverse: FakeDmxUniverse) {
    private val dmxChannelMapping = Config.findDmxChannelMapping(movingHead)
    private val adapter = run {
        val dmxBufferReader = dmxUniverse.reader(dmxChannelMapping.baseChannel, dmxChannelMapping.channelCount) {
            receivedDmxFrame()
        }
        dmxChannelMapping.adapter.build(dmxBufferReader) as MovingHead.Buffer
    }

    val cone = Cone(movingHead.origin, movingHead.heading)

    fun addTo(scene: Scene) {
        cone.addTo(scene)
    }

    private fun receivedDmxFrame() {
        cone.setColor(adapter.color, adapter.dimmer)

        val rotation = Vector3F(
            adapter.panRange.scale(adapter.pan),
            0f,
            adapter.tiltRange.scale(adapter.tilt)
        )
        cone.setRotation(rotation)
    }

    class Cone(val origin: Vector3F, val heading: Vector3F) {
        private val coneLength = 1000.0

        private val innerConeBaseOpacity = .75
        private val innerConeMaterial = MeshBasicMaterial().apply {
            color.set(0xffff00)
            side = THREE.DoubleSide
            transparent = true
            opacity = innerConeBaseOpacity
            depthTest = false
        }
        private val innerConeGeometry = ConeBufferGeometry(20, coneLength, openEnded = true)
            .also { it.applyMatrix(Matrix4().makeTranslation(0.0, -coneLength / 2, 0.0)) }
        private val innerCone = Mesh(innerConeGeometry, innerConeMaterial)

        private val outerConeBaseOpacity = .4
        private val outerConeMaterial = MeshBasicMaterial().apply {
            color.set(0xffff00)
            side = THREE.DoubleSide
            transparent = true
            opacity = outerConeBaseOpacity
            blending = THREE.AdditiveBlending
            depthTest = false
        }
        private val outerConeGeometry = ConeBufferGeometry(50, coneLength, openEnded = true)
            .also { it.applyMatrix(Matrix4().makeTranslation(0.0, -coneLength / 2, 0.0)) }
        private val outerCone = Mesh(outerConeGeometry, outerConeMaterial)

        private val baseOpacities = listOf(innerConeBaseOpacity, outerConeBaseOpacity)
        private val materials = listOf(innerConeMaterial, outerConeMaterial)
        private val cones = listOf(innerCone, outerCone)

        init {
            cones.forEach { cone ->
                cone.position.set(origin.x - coneLength / 2, origin.y, origin.z)
                cone.rotation.setFromVector3(heading.toVector3())
            }
        }

        fun addTo(scene: Scene) {
            cones.forEach { cone -> scene.add(cone) }
        }

        fun setColor(color: Color, dimmer: Float) {
            materials.zip(baseOpacities).forEach { (material, baseOpacity) ->
                material.color.set(color.rgb)
                material.opacity = baseOpacity * dimmer
                material.visible = dimmer > .01f
            }
        }

        fun setRotation(rotation: Vector3F) {
            cones.forEach { cone ->
                cone.rotation.setFromVector3((heading + rotation).toVector3())
            }
        }
    }

    fun ClosedRange<Float>.scale(value: Float) =
        (endInclusive - start) * value + start
}