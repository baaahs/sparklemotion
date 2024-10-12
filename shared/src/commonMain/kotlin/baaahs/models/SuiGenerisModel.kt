package baaahs.models

import baaahs.model.ConstEntityMetadataProvider
import baaahs.model.ImportedEntityData
import baaahs.model.ModelData
import baaahs.model.ModelUnit

val suiGenerisModelData = ModelData(
    "SuiGeneris",
    listOf(
        ImportedEntityData(
            "templates/scenes/sui-generis.obj", null,
            objData = "templates/scenes/sui-generis.obj", objDataIsFileRef = true,
            metadata = ConstEntityMetadataProvider(16 * 60)
        )
    ),
    ModelUnit.Inches
)