package baaahs.models

import baaahs.geom.EulerAngle
import baaahs.geom.Matrix4F
import baaahs.geom.Vector3F
import baaahs.model.*

val sheepModelData = ModelData(
    "BAAAHS",
    listOf(
        ObjModelData("Decom2019", null, Matrix4F.identity, "baaahs-model.obj", true),
        MovingHeadData(
            "leftEye",
            "Left Eye",
            Matrix4F.fromPositionAndRotation(
                Vector3F(-11f, 202.361f, -24.5f),
                EulerAngle(0.0, 0.15708, 1.5708)
            )
        ),
        MovingHeadData(
            "rightEye",
            "Right Eye",
            Matrix4F.fromPositionAndRotation(
                Vector3F(-11f, 202.361f, 27.5f),
                EulerAngle( 0.0, -0.15708, 1.5708)
            )
        )
    ),
    ModelUnit.Inches
)

val sheepModelMetadata = StrandCountEntityMetadataProvider.openResource("baaahs-panel-info.txt")