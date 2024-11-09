package baaahs.mapper.migration

import baaahs.describe
import baaahs.geom.Matrix4F
import baaahs.geom.identity
import baaahs.kotest.value
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.*
import kotlinx.serialization.json.*

@Suppress("unused", "ClassName")
object V1_EpochToIso8601AndFixCameraMatrixSpec : DescribeSpec({
    describe<V1_EpochToIso8601AndFixCameraMatrix> {
        val fromJson by value {
            /**language=json*/
            """
                {
                    "startedAt": 1.566620051322E12,
                    "savedAt": 1.566620263433E12,
                    "cameraMatrix": {
                        "elements": [1,0,0,0,0,1,0,0,0,0,1,0,0,0,0,1] 
                    },
                    "other": "pass through"
                }
            """.trimIndent()
        }
        val fromJsonEl by value { Json.Default.parseToJsonElement(fromJson).jsonObject }
        fun subject() = V1_EpochToIso8601AndFixCameraMatrix.migrate(fromJsonEl)

        it("converts epoch doubles to iso 8601 strings") {
            subject().shouldBe(
                buildJsonObject {
                    put("startedAt", JsonPrimitive("2019-08-24T04:14:11.322Z"))
                    put("savedAt", JsonPrimitive("2019-08-24T04:17:43.433Z"))
                    put("cameraMatrix", JsonArray(Matrix4F.identity.elements.map { JsonPrimitive(it.toInt()) }))
                    put("other", JsonPrimitive("pass through"))
                }
            )
        }
    }
})