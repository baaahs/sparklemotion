package baaahs.visualizer

import baaahs.Color
import baaahs.geom.Vector3F
import baaahs.model.MovingHead
import baaahs.sim.FakeDmxUniverse
import three.js.*

class VizMovingHead(private val movingHead: MovingHead, dmxUniverse: FakeDmxUniverse) {
    private val buffer = run {
        val dmxBufferReader = dmxUniverse.reader(movingHead.baseDmxChannel, movingHead.dmxChannelCount) {
            receivedDmxFrame()
        }
        movingHead.newBuffer(dmxBufferReader)
    }
    private val beam = when (movingHead.colorModel) {
        MovingHead.ColorModel.ColorWheel -> ColorWheelBeam()
        MovingHead.ColorModel.RGB -> RgbBeam()
        MovingHead.ColorModel.RGBW -> RgbBeam()
    }

    fun addTo(scene: Scene) {
        beam.addTo(scene)
    }

    private fun receivedDmxFrame() {
        beam.receivedDmxFrame()
    }

    interface Beam {
        fun addTo(scene: Scene)
        fun receivedDmxFrame()
    }

    inner class ColorWheelBeam : Beam {
        private val primaryCone = Cone(movingHead.origin, movingHead.heading)
        private val secondaryCone = Cone(movingHead.origin, movingHead.heading)

        override fun addTo(scene: Scene) {
            primaryCone.addTo(scene)
            secondaryCone.addTo(scene)
        }

        override fun receivedDmxFrame() {
            primaryCone.setColor(buffer.primaryColor, buffer.dimmer)
            secondaryCone.setColor(buffer.secondaryColor, buffer.dimmer)

            val rotation = Vector3F(
                movingHead.panRange.scale(buffer.pan),
                0f,
                movingHead.tiltRange.scale(buffer.tilt)
            )
            primaryCone.setRotation(rotation)
            secondaryCone.setRotation(rotation)
        }
    }

    inner class RgbBeam : Beam {
        private val cone = Cone(movingHead.origin, movingHead.heading)

        override fun addTo(scene: Scene) {
            cone.addTo(scene)
        }

        override fun receivedDmxFrame() {
            cone.setColor(buffer.primaryColor, buffer.dimmer)

            val rotation = Vector3F(
                movingHead.panRange.scale(buffer.pan),
                0f,
                movingHead.tiltRange.scale(buffer.tilt)
            )
            cone.setRotation(rotation)
        }
    }

    class Cone(val origin: Vector3F, val heading: Vector3F) {
        private val coneLength = 1000.0

        private val innerBaseOpacity = .75
        private val innerMaterial = MeshBasicMaterial().apply {
            color.set(0xffff00)
            side = DoubleSide
            transparent = true
            opacity = innerBaseOpacity
            depthTest = false
        }
        private val innerGeometry = ConeGeometry(20, coneLength, openEnded = true)
            .also { it.applyMatrix4(Matrix4().makeTranslation(0.0, -coneLength / 2, 0.0)) }
        private val inner = Mesh(innerGeometry, innerMaterial)

        private val outerBaseOpacity = .4
        private val outerMaterial = MeshBasicMaterial().apply {
            color.set(0xffff00)
            side = DoubleSide
            transparent = true
            opacity = outerBaseOpacity
            blending = AdditiveBlending
            depthTest = false
        }
        private val outerGeometry = ConeGeometry(50, coneLength, openEnded = true)
            .also { it.applyMatrix4(Matrix4().makeTranslation(0.0, -coneLength / 2, 0.0)) }
        private val outer = Mesh(outerGeometry, outerMaterial)

        private val baseOpacities = listOf(innerBaseOpacity, outerBaseOpacity)
        private val materials = listOf(innerMaterial, outerMaterial)
        private val cones = listOf(inner, outer)

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