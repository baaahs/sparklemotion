package baaahs.visualizer

import baaahs.util.three.addEventListener
import three.examples.jsm.controls.OrbitControls
import kotlin.math.PI

class OrbitControlsExtension : Extension(OrbitControlsExtension::class) {
    private var orbitControlsActive = false

    val orbitControls: OrbitControls by attachment {
        OrbitControls(camera, canvas).apply {
            minPolarAngle = PI / 2 - .25 // radians
            maxPolarAngle = PI / 2 + .25 // radians

            enableDamping = false
            enableKeys = false

            addEventListener("start") {
                orbitControlsActive = true
            }
            addEventListener("end") {
                orbitControlsActive = false
            }
        }
    }

    override fun VisualizerContext.isInUserInteraction() =
        orbitControlsActive

    fun update() = orbitControls.update()
}