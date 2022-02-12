package baaahs.visualizer.movers

import baaahs.Color
import baaahs.model.MovingHeadAdapter
import baaahs.visualizer.VizObj
import baaahs.visualizer.toVector3
import three.js.*
import three_ext.toVector3F
import kotlin.math.PI

actual class Cone actual constructor(
    private val movingHeadAdapter: MovingHeadAdapter,
    private val colorMode: ColorMode
) {
    private val coneLength = 1000.0

    private val clipPlane = Plane(Vector3(0, 0, Float.MAX_VALUE), 0)

    private val innerBaseOpacity = .75
    private val innerMaterial = MeshBasicMaterial().apply {
        color.set(0xffff00)
        side = DoubleSide
        transparent = true
        opacity = innerBaseOpacity
        if (colorMode.isClipped) {
            clippingPlanes = arrayOf(clipPlane)
        }
    }
    private val visualizerInfo = movingHeadAdapter.visualizerInfo
    private val innerGeometry = CylinderGeometry(visualizerInfo.lensRadius * .4, 20, coneLength, openEnded = true)
        .also {
            it.translate(0.0, -coneLength / 2 - visualizerInfo.canLength / 2, 0.0)
            it.rotateX(PI)
        }
    private val inner = Mesh(innerGeometry, innerMaterial)

    private val outerBaseOpacity = .4
    private val outerMaterial = MeshBasicMaterial().apply {
        color.set(0xffff00)
        side = DoubleSide
        transparent = true
        opacity = outerBaseOpacity
        blending = AdditiveBlending
        if (colorMode.isClipped) {
            clippingPlanes = arrayOf(clipPlane)
        }
    }
    private val outerGeometry = CylinderGeometry(visualizerInfo.lensRadius, 50, coneLength, openEnded = true)
        .also {
            it.translate(0.0, -coneLength / 2 - visualizerInfo.canLength / 2, 0.0)
            it.rotateX(PI)
        }
    private val outer = Mesh(outerGeometry, outerMaterial)

    private val baseOpacities = listOf(innerBaseOpacity, outerBaseOpacity)
    private val materials = listOf(innerMaterial, outerMaterial)
    private val cones = listOf(inner, outer)

    init {
        update(State())
    }

    actual fun addTo(parent: VizObj) {
        cones.forEach { cone -> parent.add(cone) }
    }

    actual fun update(state: State) {
        setColor(colorMode.getColor(movingHeadAdapter, state), state.dimmer)

        // `0` indicates just the primary color, `.5` indicates a 50/50 mix, and `1.` indicates
        // just the adjacent color.
        if (colorMode.isClipped) {
            val colorSplit = (state.colorWheelPosition * movingHeadAdapter.colorWheelColors.size) % 1f
            val start = Vector3(
                0,
                visualizerInfo.canLength,
                visualizerInfo.lensRadius * (colorSplit - .5)
            )
            val end = Vector3(
                0,
                coneLength,
                50 * (colorSplit - .5)
            )

            outer.updateWorldMatrix(updateParents = true, updateChildren = false)
            val normal = (end.toVector3F() - start.toVector3F()).normalize().toVector3()
            normal.set(normal.x, -normal.z, normal.y)
            if (colorMode == ColorMode.Secondary) normal.negate()
            clipPlane.setFromNormalAndCoplanarPoint(normal, start)
            clipPlane.applyMatrix4(outer.matrixWorld)
        }
    }

    private fun setColor(color: Color, dimmer: Float) {
        materials.zip(baseOpacities).forEach { (material, baseOpacity) ->
            material.color.set(color.rgb)
            material.opacity = baseOpacity * dimmer
            material.visible = dimmer > .01f
        }
    }
}