package baaahs.visualizer.movers

import baaahs.Color
import baaahs.model.MovingHead
import baaahs.visualizer.toVector3
import three.js.*

class Cone(
    private val movingHead: MovingHead,
    private val colorMode: ColorMode = ColorMode.Rgb
) {
    private val origin = movingHead.origin.toVector3()
    private val heading = movingHead.heading.toVector3()
    private val coneLength = 1000.0

    private val clipPlane = Plane(Vector3(0, 0, 1), 0)

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
        if (colorMode.isClipped) {
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

    fun update(state: State) {
        setColor(colorMode.getColor(movingHead, state), state.dimmer)

        val rotation = Vector3(
            movingHead.panRange.scale(state.pan),
            0f,
            movingHead.tiltRange.scale(state.tilt)
        )

        // `0` indicates just the primary color, `.5` indicates a 50/50 mix, and `1.` indicates
        // just the adjacent color.
        val colorSplit = (state.colorWheelPosition * movingHead.colorWheelColors.size) % 1f
        setRotation(rotation, colorSplit)
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

        if (colorMode.isClipped) {
            aim.y += (1f - colorSplit - .5f) * .25f
            val planeRotation = Euler().setFromVector3(aim)
            val normal = Vector3(0, 0, 1).applyEuler(planeRotation)
            if (colorMode == ColorMode.Secondary) normal.negate()
            val planeOrigin = origin.clone()
            planeOrigin.x -= coneLength / 2
            clipPlane.setFromNormalAndCoplanarPoint(normal, planeOrigin)
        }
    }
}