package baaahs.visualizer.movers

import baaahs.Color
import baaahs.geom.Vector3F
import baaahs.geom.toThreeEuler
import baaahs.model.MovingHead
import baaahs.visualizer.VizObj
import baaahs.visualizer.VizScene
import baaahs.visualizer.toVector3
import three.js.*
import three_ext.plus
import three_ext.set
import three_ext.vector3FacingForward

actual class Cone actual constructor(
    private val movingHead: MovingHead,
    private val colorMode: ColorMode
) {
    private val position = movingHead.position.toVector3()
    private val rotation = movingHead.rotation.toThreeEuler()
    private val coneLength = 1000.0

    private val clipPlane = Plane(Vector3F.facingForward.toVector3(), 0)

    private val innerBaseOpacity = .75
    private val innerMaterial = MeshBasicMaterial().apply {
        color.set(0xffff00)
        side = DoubleSide
        transparent = true
        opacity = innerBaseOpacity
        depthTest = false
        if (colorMode.isClipped) {
            clippingPlanes = arrayOf(clipPlane)
        }
    }
    private val innerGeometry = ConeGeometry(20, coneLength, openEnded = true)
        .also { it.translate(0.0, -coneLength / 2, 0.0) }
    private val inner = Mesh(innerGeometry, innerMaterial)

    private val outerBaseOpacity = .4
    private val outerMaterial = MeshBasicMaterial().apply {
        color.set(0xffff00)
        side = DoubleSide
        transparent = true
        opacity = outerBaseOpacity
        blending = AdditiveBlending
        depthTest = false
        if (colorMode.isClipped) {
            clippingPlanes = arrayOf(clipPlane)
        }
    }
    private val outerGeometry = ConeGeometry(50, coneLength, openEnded = true)
        .also { it.translate(0.0, -coneLength / 2, 0.0) }
    private val outer = Mesh(outerGeometry, outerMaterial)

    private val baseOpacities = listOf(innerBaseOpacity, outerBaseOpacity)
    private val materials = listOf(innerMaterial, outerMaterial)
    private val cones = listOf(inner, outer)

    init {
        cones.forEach { cone ->
            cone.position.set(position)
            cone.rotation.set(rotation)
        }
    }

    actual fun addTo(scene: VizScene) {
        cones.forEach { cone -> scene.add(VizObj(cone)) }
    }

    actual fun update(state: State) {
        setColor(colorMode.getColor(movingHead, state), state.dimmer)

        val rotation = Euler(
            movingHead.adapter.panRange.scale(state.pan),
            0f,
            movingHead.adapter.tiltRange.scale(state.tilt)
        )

        // `0` indicates just the primary color, `.5` indicates a 50/50 mix, and `1.` indicates
        // just the adjacent color.
        val colorSplit = (state.colorWheelPosition * movingHead.adapter.colorWheelColors.size) % 1f
        setRotation(rotation, colorSplit)
    }

    fun setColor(color: Color, dimmer: Float) {
        materials.zip(baseOpacities).forEach { (material, baseOpacity) ->
            material.color.set(color.rgb)
            material.opacity = baseOpacity * dimmer
            material.visible = dimmer > .01f
        }
    }

    fun setRotation(rotation: Euler, colorSplit: Float) {
        val aim = this.rotation + rotation
        cones.forEach { cone ->
            cone.rotation.set(aim)
        }

        if (colorMode.isClipped) {
            aim.y = aim.y.toDouble() + (1f - colorSplit - .5f) * .25f
            val planeRotation = aim
            val normal = vector3FacingForward.applyEuler(planeRotation)
            if (colorMode == ColorMode.Secondary) normal.negate()
            val planeOrigin = position.clone()
            clipPlane.setFromNormalAndCoplanarPoint(normal, planeOrigin)
        }
    }
}