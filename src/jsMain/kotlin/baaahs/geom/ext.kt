package baaahs.geom

import three.js.Vector3

fun Vector3.toVector3F(): Vector3F =
    Vector3F(
        x.toFloat(),
        y.toFloat(),
        z.toFloat()
    )
