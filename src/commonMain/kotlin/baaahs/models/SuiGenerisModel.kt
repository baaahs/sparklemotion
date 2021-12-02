package baaahs.models

import baaahs.geom.Matrix4
import baaahs.model.ModelData
import baaahs.model.ObjModelData

val suiGenerisModelData = ModelData(
    "SuiGeneris",
    listOf(
        ObjModelData("sui-generis.obj", null, Matrix4.identity, "sui-generis.obj", true)
    )
)