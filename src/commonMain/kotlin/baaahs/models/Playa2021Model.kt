package baaahs.models

import baaahs.geom.EulerAngle
import baaahs.geom.Matrix4
import baaahs.geom.Vector3F
import baaahs.model.GridData
import baaahs.model.ModelData
import baaahs.model.ObjModelData

val playa2021ModelData = ModelData(
    "Playa2021",
    listOf(
        ObjModelData("playa-2021-panels.obj", null, Matrix4.identity, "playa-2021-panels.obj", true),
        GridData(
            "grid", null,
            Matrix4.fromPositionAndRotation(Vector3F(-24f, 0f, 0f), EulerAngle.identity),
            7, 11, 1f, 1f, zigZag = true
        )
    )
)