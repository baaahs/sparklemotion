package baaahs.visualizer.movers

import baaahs.Color
import baaahs.model.MovingHead
import baaahs.sim.FakeDmxUniverse
import baaahs.visualizer.toVector3
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
        private val coneOrigin = movingHead.origin.toVector3()
        private val coneHeading = movingHead.heading.toVector3()
        private val primaryCone = Cone(coneOrigin, coneHeading, ClipMode.Primary)
        private val secondaryCone = Cone(coneOrigin, coneHeading, ClipMode.Secondary)

        override fun addTo(scene: Scene) {
            primaryCone.addTo(scene)
            secondaryCone.addTo(scene)
        }

        override fun receivedDmxFrame() {
            primaryCone.update(buffer)
            secondaryCone.update(buffer)
        }
    }

    inner class RgbBeam : Beam {
        private val cone = Cone(
            movingHead.origin.toVector3(),
            movingHead.heading.toVector3()
        )

        override fun addTo(scene: Scene) {
            cone.addTo(scene)
        }

        override fun receivedDmxFrame() {
            cone.setColor(buffer.primaryColor, buffer.dimmer)

            val rotation = Vector3(
                movingHead.panRange.scale(buffer.pan),
                0f,
                movingHead.tiltRange.scale(buffer.tilt)
            )
            cone.setRotation(rotation, buffer.colorSplit)
        }
    }

    inner class Cone(
        private val origin: Vector3,
        private val heading: Vector3,
        private val clipMode: ClipMode = ClipMode.Solo
    ) {
        private val coneLength = 1000.0

        private val clipPlane = Plane(Vector3(0, 0, 1), 0)

        private val innerBaseOpacity = .75
        private val innerMaterial = MeshBasicMaterial().apply {
            color.set(0xffff00)
            side = DoubleSide
            transparent = true
            opacity = innerBaseOpacity
            depthTest = false
            if (clipMode.isClipped) {
                clippingPlanes = arrayOf(clipPlane)
            }
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
            if (clipMode.isClipped) {
                clippingPlanes = arrayOf(clipPlane)
            }
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
                cone.rotation.setFromVector3(heading)
            }
        }

        fun addTo(scene: Scene) {
            cones.forEach { cone -> scene.add(cone) }
        }

        fun update(buffer: MovingHead.Buffer) {
            val color = clipMode.getColor(buffer)
            setColor(color, buffer.dimmer)

            val rotation = Vector3(
                movingHead.panRange.scale(buffer.pan),
                0f,
                movingHead.tiltRange.scale(buffer.tilt)
            )
            setRotation(rotation, buffer.colorSplit)
        }

        fun setColor(color: Color, dimmer: Float) {
            materials.zip(baseOpacities).forEach { (material, baseOpacity) ->
                material.color.set(color.rgb)
                material.opacity = baseOpacity * dimmer
                material.visible = dimmer > .01f
            }
        }

        fun setRotation(rotation: Vector3, colorSplit: Float) {
            val aim = heading.clone().add(rotation)
            cones.forEach { cone ->
                cone.rotation.setFromVector3(aim)
            }

            if (clipMode.isClipped) {
                aim.y += (1f - colorSplit - .5f) * .25f
                val planeRotation = Euler().setFromVector3(aim)
                val normal = Vector3(0, 0, 1).applyEuler(planeRotation)
                if (clipMode == ClipMode.Secondary) normal.negate()
                val planeOrigin = origin.clone()
                planeOrigin.x -= coneLength / 2
                clipPlane.setFromNormalAndCoplanarPoint(normal, planeOrigin)
            }
        }
    }

    enum class ClipMode(
        val isClipped: Boolean,
        val getColor: MovingHead.Buffer.() -> Color
    ) {
        Solo(false, { primaryColor }),
        Primary(true, { primaryColor }),
        Secondary(true, { secondaryColor })
    }

    fun ClosedRange<Float>.scale(value: Float) =
        (endInclusive - start) * value + start
}