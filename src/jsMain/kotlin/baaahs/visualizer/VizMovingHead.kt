package baaahs.visualizer

import baaahs.Config
import baaahs.model.MovingHead
import baaahs.sim.FakeDmxUniverse
import info.laht.threekt.THREE
import info.laht.threekt.geometries.ConeBufferGeometry
import info.laht.threekt.materials.MeshBasicMaterial
import info.laht.threekt.math.Matrix4
import info.laht.threekt.objects.Mesh
import info.laht.threekt.scenes.Scene

class VizMovingHead(private val movingHead: MovingHead, dmxUniverse: FakeDmxUniverse) {
    private val coneLength = 1000.0
    
    private val dmxChannelMapping = Config.findDmxChannelMapping(movingHead)
    private val adapter = run {
        val dmxBufferReader = dmxUniverse.reader(dmxChannelMapping.baseChannel, dmxChannelMapping.channelCount) {
            receivedDmxFrame()
        }
        dmxChannelMapping.adapter.build(dmxBufferReader) as MovingHead.Buffer
    }

    private val innerConeMaterial = MeshBasicMaterial().apply {
        color.set(0xffff00)
        side = THREE.DoubleSide
        transparent = true
        opacity = .75
        depthTest = false
    }
    private val innerConeGeometry = ConeBufferGeometry(20, coneLength, openEnded = true)
        .also { it.applyMatrix(Matrix4().makeTranslation(0.0, -coneLength / 2, 0.0)) }
    private val innerCone = Mesh(innerConeGeometry, innerConeMaterial)

    private val outerConeMaterial = MeshBasicMaterial().apply {
        color.set(0xffff00)
        side = THREE.DoubleSide
        transparent = true
        opacity = .4
        blending = THREE.AdditiveBlending
        depthTest = false
    }
    private val outerConeGeometry = ConeBufferGeometry(50, coneLength, openEnded = true)
        .also { it.applyMatrix(Matrix4().makeTranslation(0.0, -coneLength / 2, 0.0)) }
    private val outerCone = Mesh(outerConeGeometry, outerConeMaterial)

    private val materials = listOf(innerConeMaterial, outerConeMaterial)
    private val cones = listOf(innerCone, outerCone)

    init {
        cones.forEach { cone ->
            cone.position.set(movingHead.origin.x - 500, movingHead.origin.y, movingHead.origin.z)
            cone.rotation.set(movingHead.heading.x, movingHead.heading.y, movingHead.heading.z)
        }
    }

    fun addTo(scene: Scene) {
        cones.forEach { cone -> scene.add(cone) }
    }

    private fun receivedDmxFrame() {
        materials.forEach { material ->
            material.color.set(adapter.color.rgb)
            material.visible = adapter.dimmer > .1
        }

        cones.forEach { cone ->
            cone.rotation.set(
                movingHead.heading.x + adapter.panRange.scale(adapter.pan),
                movingHead.heading.y,
                movingHead.heading.z + adapter.tiltRange.scale(adapter.tilt)
            )
        }
    }

    fun ClosedRange<Float>.scale(value: Float) =
        (endInclusive - start) * value + start
}