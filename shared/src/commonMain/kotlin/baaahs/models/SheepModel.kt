package baaahs.models

import baaahs.geom.EulerAngle
import baaahs.geom.Vector3F
import baaahs.model.*

val sheepModelData = ModelData(
    "BAAAHS",
    listOf(
        ImportedEntityData(
            "Decom2019", null,
            objData = "templates/scenes/baaahs-model.obj", objDataIsFileRef = true,
            metadata = StrandCountEntityMetadataProvider.openResource("templates/scenes/baaahs-panel-info.txt")
        ),
        MovingHeadData(
            "leftEye",
            "Left Eye",
            Vector3F(-11f, 202.361f, -24.5f),
            EulerAngle(0.0, 0.15708, 1.5708),
            baseDmxChannel = 1
        ),
        MovingHeadData(
            "rightEye",
            "Right Eye",
            Vector3F(-11f, 202.361f, 27.5f),
            EulerAngle(0.0, -0.15708, 1.5708),
            baseDmxChannel = 17
        )
    ),
    ModelUnit.Inches
)