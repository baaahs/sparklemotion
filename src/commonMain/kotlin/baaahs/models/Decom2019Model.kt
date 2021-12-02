package baaahs.models

import baaahs.geom.Matrix4
import baaahs.geom.Vector3F
import baaahs.model.ConstEntityMetadataProvider
import baaahs.model.LightBarData
import baaahs.model.ModelData
import baaahs.model.ObjModelData

val decom2019ModelData = ModelData(
    "Decom2019",
    listOf(
        ObjModelData("Decom2019", null, Matrix4.identity, "decom-2019-panels.obj", true),
        LightBarData("bar 1", "Vertical between Panel 1 and 2", Matrix4.identity, Vector3F(54f, 66f, 0f), Vector3F(54f, 102f, 0f)),
        LightBarData("bar 2", "Vertical between Panel 2 and 3", Matrix4.identity, Vector3F(114f, 66f, 0f), Vector3F(114f, 102f, 0f)),
        LightBarData("bar 3", "Vertical below Panel 2", Matrix4.identity, Vector3F(66f, 47f, 0f), Vector3F(102f, 47f, 0f)),
    )
)

val decom2019ModelMetadata = ConstEntityMetadataProvider(16 * 60)