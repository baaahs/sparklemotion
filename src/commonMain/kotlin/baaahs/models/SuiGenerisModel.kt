package baaahs.models

import baaahs.model.ConstEntityMetadataProvider
import baaahs.model.ModelData
import baaahs.model.ModelUnit
import baaahs.model.ObjModelData

val suiGenerisModelData = ModelData(
    "SuiGeneris",
    listOf(
        ObjModelData(
            "sui-generis.obj", null,
            objData = "sui-generis.obj", objDataIsFileRef = true,
            metadata = ConstEntityMetadataProvider(16 * 60)
        )
    ),
    ModelUnit.Inches
)