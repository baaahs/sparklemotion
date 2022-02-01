package baaahs.visualizer.movers

import baaahs.model.MovingHeadAdapter
import baaahs.visualizer.EntityStyle
import three.js.*
import kotlin.math.PI

class SharpyVisualizer(
    private val adapter: MovingHeadAdapter
) {
    val group = Group()

    private val sharpyMaterial = MeshLambertMaterial()
    private val base = Mesh(BoxGeometry(), sharpyMaterial).also { group.add(it) }
    private val armature = Group().also { group.add(it) }

    private val leftArm = Mesh(BoxGeometry(), sharpyMaterial).also { armature.add(it) }
    private val rightArm = Mesh(BoxGeometry(), sharpyMaterial).also { armature.add(it) }
    private val armBase = Mesh(BoxGeometry(), sharpyMaterial).also { armature.add(it) }

    private val can = Mesh(CylinderBufferGeometry(), sharpyMaterial).also { armature.add(it) }
    private val beam = Beam.selectFor(adapter).also { can.add(it.vizObj) }

    init {
        updateGeometry(adapter.visualizerInfo)
    }

    fun updateGeometry(visualizerInfo: MovingHeadAdapter.VisualizerInfo) {
        with(visualizerInfo) {
            can.geometry = CylinderBufferGeometry(canRadius, canRadius, canLength)

            // TODO: All dimensions should be expressed in cm, then converted to the model's units.
            val narrowGap = 1
            val wideGap = 2
            val armThickness = 1.5
            val armWidth = 3
            val armTopPadding = 2
            val armLength = canLength / 2 + armTopPadding + wideGap

            leftArm.geometry = BoxGeometry(armThickness, armLength, armWidth).apply {
                translate(-(canRadius + narrowGap + armThickness / 2), armTopPadding - armLength / 2, 0)
            }
            rightArm.geometry = BoxGeometry(armThickness, armLength, armWidth).apply {
                translate(canRadius + narrowGap + armThickness / 2, armTopPadding - armLength / 2, 0)
            }

            val baseSize = Vector3(
                canRadius * 2 + armWidth,
                4.0,
                canRadius * 2 + armThickness
            )
            armBase.geometry = BoxGeometry(
                (canRadius + narrowGap + armThickness) * 2,
                armWidth, armWidth
            ).apply {
                translate(0, -(canLength / 2 + wideGap + armWidth / 2), 0)
            }

            base.geometry = BoxGeometry(baseSize.x, baseSize.y, baseSize.z).apply {
                translate(0, -(canLength / 2 + wideGap + armWidth + narrowGap + baseSize.y / 2), 0)
            }
        }
    }

    fun applyStyle(entityStyle: EntityStyle) {
        entityStyle.applyToMesh(sharpyMaterial, EntityStyle.Use.FixtureHardware)
    }

    fun update(state: State) {
        // When tilt is at minimum, start pan 0 degrees at right, per convention.
        val rotationOffset = (PI / 2).toFloat()
        armature.rotation.y = state.pan + rotationOffset
        can.rotation.x = -state.tilt

        beam.update(state)
    }

}