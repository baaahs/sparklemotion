package baaahs.mapper.migration

import baaahs.describe
import baaahs.toEqual
import ch.tutteli.atrium.api.verbs.expect
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonObject
import org.spekframework.spek2.Spek

@Suppress("unused", "ClassName")
object V1_EpochToIso8601Spec : Spek({
    describe<V1_EpochToIso8601> {
        val fromJson by value {
            /**language=json*/
            """
                {
                    "startedAt": 1.566620051322E12,
                    "savedAt": 1.566620263433E12,
                    "other": "pass through"
                }
            """.trimIndent()
        }
        val fromJsonEl by value { Json.Default.parseToJsonElement(fromJson).jsonObject }
        fun subject() = V1_EpochToIso8601.migrate(fromJsonEl)

        it("converts epoch doubles to iso 8601 strings") {
            expect(subject()).toEqual(
                buildJsonObject {
                    put("startedAt", JsonPrimitive("2019-08-24T04:14:11.322Z"))
                    put("savedAt", JsonPrimitive("2019-08-24T04:17:43.433Z"))
                    put("other", JsonPrimitive("pass through"))
                }
            )
        }
    }
})