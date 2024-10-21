package baaahs.models

import baaahs.geom.Vector3F
import baaahs.model.*

val decom2019ModelData = ModelData(
    "Decom2019",
    listOf(
        ImportedEntityData(
            "Decom2019",
            null,
            objData = "templates/scenes/decom-2019-panels.obj",
            objDataIsFileRef = true,
            metadata = ConstEntityMetadataProvider(16 * 60)
        ),
        LightBarData(
            "bar 1",
            "Vertical between Panel 1 and 2",
            startVertex = Vector3F(54f, 66f, 0f),
            endVertex = Vector3F(54f, 102f, 0f)
        ),
        LightBarData(
            "bar 2",
            "Vertical between Panel 2 and 3",
            startVertex = Vector3F(114f, 66f, 0f),
            endVertex = Vector3F(114f, 102f, 0f)
        ),
        LightBarData(
            "bar 3",
            "Vertical below Panel 2",
            startVertex = Vector3F(66f, 47f, 0f),
            endVertex = Vector3F(102f, 47f, 0f)
        ),
    ),
    ModelUnit.Inches
)