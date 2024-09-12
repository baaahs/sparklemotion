package baaahs.visualizer.movers

import baaahs.model.ModelUnit
import baaahs.model.MovingHeadAdapter
import baaahs.visualizer.EntityStyle
import baaahs.visualizer.ignoreForCameraFit
import js.objects.jso
import three.*
import kotlin.math.PI

class SharpyVisualizer(
    adapter: MovingHeadAdapter,
    private val units: ModelUnit
) {
    val group = Group()

    private val sharpyMaterial = MeshLambertMaterial()
    private val base = Mesh(BoxGeometry(), sharpyMaterial).also { group.add(it) }
    private val armature = Group().also { group.add(it) }

    private val leftArm = Mesh(BoxGeometry(), sharpyMaterial).also { armature.add(it) }
    private val rightArm = Mesh(BoxGeometry(), sharpyMaterial).also { armature.add(it) }
    private val armBase = Mesh(BoxGeometry(), sharpyMaterial).also { armature.add(it) }

    private val can = Mesh(CylinderGeometry(), sharpyMaterial).also { armature.add(it) }
    private val beam = Beam.selectFor(adapter, units).also { can.add(it.vizObj) }
        .also { it.vizObj.ignoreForCameraFit() }

    private val idealBeamArmature = Group().also { group.add(it) }
        .also { it.ignoreForCameraFit() }
    private val idealBeamCan = Group().also { idealBeamArmature.add(it) }
        .also { it.ignoreForCameraFit() }

    init {
        updateGeometry(adapter.visualizerInfo)

        // Ideal beam; where the moving head would be pointing if it weren't for physics.
        Line(BufferGeometry<NormalOrGLBufferAttributes>().apply {
            setFromPoints(arrayOf(Vector3(0, 0, 0), Vector3(0, 4000.cm, 0)))
        }, LineDashedMaterial(jso {
            color = 0xff7700
            linewidth = 5
            dashSize = 1
        })).also {
            it.computeLineDistances()
            idealBeamCan.add(it)
        }
    }

    private val Int.cm: Double get() = units.fromCm(this.toDouble())
    private val Float.cm: Float get() = units.fromCm(this)
    private val Double.cm: Double get() = units.fromCm(this)

    fun updateGeometry(visualizerInfo: MovingHeadAdapter.VisualizerInfo) {
        val canRadius = visualizerInfo.canRadius.cm
        val canLength = visualizerInfo.canLength.cm

        can.geometry = CylinderGeometry(canRadius, canRadius, canLength)

        // TODO: All dimensions should be expressed in cm, then converted to the model's units.
        val narrowGap = 1.cm
        val wideGap = 2.cm
        val armThickness = 1.5.cm
        val armWidth = 3.cm
        val armTopPadding = 2.cm
        val armLength = (canLength / 2 + armTopPadding + wideGap).cm

        leftArm.geometry = BoxGeometry(armThickness, armLength, armWidth).apply {
            translate(-(canRadius + narrowGap + armThickness / 2), armTopPadding - armLength / 2, 0)
        }
        rightArm.geometry = BoxGeometry(armThickness, armLength, armWidth).apply {
            translate(canRadius + narrowGap + armThickness / 2, armTopPadding - armLength / 2, 0)
        }

        val baseSize = Vector3(
            canRadius * 2 + armWidth,
            4.0.cm,
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

        group.updateMatrixWorld()
    }

    fun applyStyle(entityStyle: EntityStyle) {
        entityStyle.applyToMesh(sharpyMaterial, EntityStyle.Use.FixtureHardware)
    }

    fun update(physicalModel: PhysicalModel) {
        val state = physicalModel.currentState

        // When tilt is at minimum, start pan 0 degrees at right, per convention.
        val rotationOffset = (PI / 2).toFloat()
        armature.rotation.y = state.pan + rotationOffset
        can.rotation.x = -state.tilt

        // Update beam appearance.
        beam.update(state)

        val idealState = physicalModel.momentumState
        idealBeamArmature.rotation.y = idealState.pan + rotationOffset
        idealBeamCan.rotation.x = -idealState.tilt

        group.updateMatrixWorld()
    }
}