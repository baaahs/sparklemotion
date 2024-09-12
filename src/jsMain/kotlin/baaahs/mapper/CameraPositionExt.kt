package baaahs.mapper

import three.Matrix4
import three.PerspectiveCamera
import three_ext.CameraControls
import three_ext.toVector3F
import kotlin.math.atan2

fun CameraPosition.Companion.from(
    camera: PerspectiveCamera,
    controls: CameraControls
): CameraPosition =
    CameraPosition(
        controls.getPosition().toVector3F(),
        controls.getTarget().toVector3F(),
        camera.zoom.toDouble(),
        controls.getFocalOffset().toVector3F(),
        camera.getZRotation()
    )

fun CameraPosition.update(camera: PerspectiveCamera, controls: CameraControls) {
    camera.setZRotation(zRotation)

    controls.setLookAt(
        position.x, position.y, position.z,
        target.x, target.y, target.z,
        true
    )
    controls.setFocalOffset(focalOffset.x, focalOffset.y, focalOffset.z, true)
}

fun PerspectiveCamera.getZRotation(): Double =
    atan2(up.y, up.x) - kotlin.math.PI / 2

fun PerspectiveCamera.setZRotation(angle: Double) {
    up.set(0, 1, 0)
    val cameraAngle = Matrix4()
    val rotated = cameraAngle.makeRotationZ(angle)
    up.applyMatrix4(rotated)
}