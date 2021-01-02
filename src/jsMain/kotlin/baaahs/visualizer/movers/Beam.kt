package baaahs.visualizer.movers

import baaahs.model.MovingHead
import baaahs.visualizer.toVector3
import three.js.Scene
import three.js.Vector3

interface Beam {
    fun addTo(scene: Scene)
    fun receivedDmxFrame()
}

class ColorWheelBeam(
    private val movingHead: MovingHead,
    private val buffer: MovingHead.Buffer
) : Beam {
    private val coneOrigin = movingHead.origin.toVector3()
    private val coneHeading = movingHead.heading.toVector3()
    private val primaryCone = Cone(movingHead, coneOrigin, coneHeading, ClipMode.Primary)
    private val secondaryCone = Cone(movingHead, coneOrigin, coneHeading, ClipMode.Secondary)

    override fun addTo(scene: Scene) {
        primaryCone.addTo(scene)
        secondaryCone.addTo(scene)
    }

    override fun receivedDmxFrame() {
        primaryCone.update(buffer)
        secondaryCone.update(buffer)
    }
}

class RgbBeam(
    private val movingHead: MovingHead,
    private val buffer: MovingHead.Buffer
) : Beam {
    private val cone = Cone(
        movingHead,
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