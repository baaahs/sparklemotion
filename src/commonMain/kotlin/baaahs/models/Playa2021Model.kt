package baaahs.models

import baaahs.geom.EulerAngle
import baaahs.geom.Vector3F
import baaahs.model.*

val playa2021ModelData = ModelData(
    "Playa2021",
    listOf(
        ImportedEntityData(
            "templates/scenes/playa-2021-panels.obj", null,
            objData = "templates/scenes/playa-2021-panels.obj", objDataIsFileRef = true,
            metadata = ConstEntityMetadataProvider(16 * 60)
        ),
        GridData(
            "grid", null,
            Vector3F(-24f, 0f, 0f),
            EulerAngle.identity,
            Vector3F.unit3d,
            Model.Entity.nextId(),
            7, 11, 1f, 1f,
            direction = GridData.Direction.RowsThenColumns,
            zigZag = true
        )
    ),
    ModelUnit.Inches
)