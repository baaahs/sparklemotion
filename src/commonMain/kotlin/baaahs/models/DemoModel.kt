package baaahs.models

import baaahs.geom.EulerAngle
import baaahs.geom.Matrix4F
import baaahs.geom.Vector3F
import baaahs.model.*

val demoModelData = ModelData(
    "Decom2019",
    listOf(
//        ObjModelData("A Panels", null, Matrix4F.identity, "decom-2019-panels.obj", true),
        ObjModelData("B Panels", null,
            Matrix4F.fromPositionAndRotation(Vector3F(170f, 0f, 0f), EulerAngle(1.0, .5, .25)), "decom-2019-panels.obj", true,
            ConstEntityMetadataProvider(16 * 60)
        ),
        LightBarData("bar 1", "Vertical between Panel 1 and 2", Matrix4F.identity, Vector3F(54f, 66f, 0f), Vector3F(54f, 102f, 0f)),
        LightBarData("bar 2", "Vertical between Panel 2 and 3", Matrix4F.identity, Vector3F(114f, 66f, 0f), Vector3F(114f, 102f, 0f)),
        LightBarData("bar 3", "Vertical below Panel 2", Matrix4F.identity, Vector3F(66f, 47f, 0f), Vector3F(102f, 47f, 0f)),
        GridData("Grid", null,
            Matrix4F.fromPositionAndRotation(Vector3F(-40f, -5f, 5f), EulerAngle(1.0, .5, .25)),
            20, 25, 1f, 1f
        ),
        LightRingData("Light Ring", null,
            Matrix4F.fromPositionAndRotation(Vector3F(-40f, -25f, 5f), EulerAngle(1.0, .5, .25)),
            Vector3F.origin, 24f, Vector3F.facingForward
        )
    )
)