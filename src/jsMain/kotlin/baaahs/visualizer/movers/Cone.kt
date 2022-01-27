package baaahs.visualizer.movers

import baaahs.Color
import baaahs.geom.Vector3F
import baaahs.model.MovingHeadAdapter
import baaahs.visualizer.VizObj
import baaahs.visualizer.toVector3
import three.js.*
import three_ext.set
import three_ext.vector3FacingForward

actual class Cone actual constructor(
    private val movingHeadAdapter: MovingHeadAdapter,
    private val colorMode: ColorMode
) {
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
    private val visualizerInfo = movingHeadAdapter.visualizerInfo
    private val innerGeometry = CylinderGeometry(visualizerInfo.lensRadius * .4, 20, coneLength, openEnded = true)
        .also { it.translate(0.0, -coneLength / 2 - visualizerInfo.canLengthInFrontOfLight, 0.0) }
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
    private val outerGeometry = CylinderGeometry(visualizerInfo.lensRadius, 50, coneLength, openEnded = true)
        .also { it.translate(0.0, -coneLength / 2 - visualizerInfo.canLengthInFrontOfLight, 0.0) }
    private val outer = Mesh(outerGeometry, outerMaterial)

    private val baseOpacities = listOf(innerBaseOpacity, outerBaseOpacity)
    private val materials = listOf(innerMaterial, outerMaterial)
    private val cones = listOf(inner, outer)

    actual fun addTo(parent: VizObj) {
        cones.forEach { cone -> parent.add(cone) }
    }

    actual fun update(state: State) {
        setColor(colorMode.getColor(movingHeadAdapter, state), state.dimmer)

        val rotation = Euler(
            movingHeadAdapter.panRange.scale(state.pan),
            0f,
            movingHeadAdapter.tiltRange.scale(state.tilt)
        )

        // `0` indicates just the primary color, `.5` indicates a 50/50 mix, and `1.` indicates
        // just the adjacent color.
        val colorSplit = (state.colorWheelPosition * movingHeadAdapter.colorWheelColors.size) % 1f
        setRotation(rotation, colorSplit)
    }

    private fun setColor(color: Color, dimmer: Float) {
        materials.zip(baseOpacities).forEach { (material, baseOpacity) ->
            material.color.set(color.rgb)
            material.opacity = baseOpacity * dimmer
            material.visible = dimmer > .01f
        }
    }

    private fun setRotation(rotation: Euler, colorSplit: Float) {
        val aim = rotation
        cones.forEach { cone ->
            cone.rotation.set(aim)
        }

        if (colorMode.isClipped) {
            aim.y = aim.y.toDouble() + (1f - colorSplit - .5f) * .25f
            val planeRotation = aim
            val normal = vector3FacingForward.applyEuler(planeRotation)
            if (colorMode == ColorMode.Secondary) normal.negate()
            clipPlane.setFromNormalAndCoplanarPoint(normal, Vector3(0, 0, 0))
        }
    }
}