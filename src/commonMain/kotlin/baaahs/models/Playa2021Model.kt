package baaahs.models

import baaahs.geom.EulerAngle
import baaahs.geom.Matrix4F
import baaahs.geom.Vector3F
import baaahs.model.*

val playa2021ModelData = ModelData(
    "Playa2021",
    listOf(
        ObjModelData(
            "playa-2021-panels.obj", null, Matrix4F.identity, "playa-2021-panels.obj", true,
            ConstEntityMetadataProvider(16 * 60)
        ),
        GridData(
            "grid", null,
            Matrix4F.fromPositionAndRotation(Vector3F(-24f, 0f, 0f), EulerAngle.identity),
            7, 11, 1f, 1f, zigZag = true
        )
    ),
    ModelUnit.Inches
)