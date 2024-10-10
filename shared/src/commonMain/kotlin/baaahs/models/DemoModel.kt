package baaahs.models

import baaahs.geom.EulerAngle
import baaahs.geom.Vector3F
import baaahs.model.*

val demoModelData = ModelData(
    "Decom2019",
    listOf(
//        ImportData("A Panels", null, Matrix4F.identity, "decom-2019-panels.obj", true),
        ImportedEntityData("B Panels", null,
            Vector3F(170f, 0f, 0f),
            EulerAngle(1.0, .5, .25),
            objData = "templates/scenes/decom-2019-panels.obj", objDataIsFileRef = true,
            metadata = ConstEntityMetadataProvider(16 * 60)
        ),
        LightBarData("bar 1", "Vertical between Panel 1 and 2",
            startVertex = Vector3F(54f, 66f, 0f), endVertex = Vector3F(54f, 102f, 0f)),
        LightBarData("bar 2", "Vertical between Panel 2 and 3",
            startVertex = Vector3F(114f, 66f, 0f), endVertex = Vector3F(114f, 102f, 0f)),
        LightBarData("bar 3", "Vertical below Panel 2",
            startVertex = Vector3F(66f, 47f, 0f), endVertex = Vector3F(102f, 47f, 0f)),
        GridData("Grid", null,
            Vector3F(-40f, -5f, 5f),
            EulerAngle(1.0, .5, .25),
            Vector3F.unit3d,
            Model.Entity.nextId(),
            20, 25, 1f, 1f,
            GridData.Direction.RowsThenColumns
        ),
        LightRingData(
            "Light Ring", null,
            Vector3F(-40f, -25f, 5f),
            EulerAngle(1.0, .5, .25),
            Vector3F.unit3d,
            Model.Entity.nextId(),
            24f
        )
    ),
    units = ModelUnit.Inches
)