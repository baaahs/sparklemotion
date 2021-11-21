package baaahs.geom

import kotlinx.serialization.Serializable

@Serializable
data class EulerAngle(
    val pitchRad: Double,
    val yawRad: Double,
    val rollRad: Double
) {
    val bankRad get() = pitchRad
    val headingRad get() = yawRad
    val attitudeRad get() = rollRad

    val xRad get() = pitchRad
    val yRad get() = yawRad
    val zRad get() = rollRad

    companion object {
        val identity: EulerAngle = EulerAngle(0.0, 0.0, 0.0)

        fun fromBankHeadingAttitude(bankRad: Double, headingRad: Double, attitudeRad: Double) =
            EulerAngle(bankRad, headingRad, attitudeRad)

        fun fromXyz(x: Double, y: Double, z: Double) =
            EulerAngle(x, y, z)
    }
}