package baaahs.models

import baaahs.geom.Matrix4F
import baaahs.model.ConstEntityMetadataProvider
import baaahs.model.ModelData
import baaahs.model.ModelUnit
import baaahs.model.ObjModelData

val suiGenerisModelData = ModelData(
    "SuiGeneris",
    listOf(
        ObjModelData(
            "sui-generis.obj", null, Matrix4F.identity, "sui-generis.obj", true,
            ConstEntityMetadataProvider(16 * 60)
        )
    ),
    ModelUnit.Inches
)