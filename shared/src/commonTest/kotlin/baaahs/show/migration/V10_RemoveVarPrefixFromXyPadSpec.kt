package baaahs.show.migration

import baaahs.describe
import baaahs.kotest.value
import baaahs.toEqual
import ch.tutteli.atrium.api.verbs.expect
import io.kotest.core.spec.style.DescribeSpec
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject

object V10_RemoveVarPrefixFromXyPadSpec : DescribeSpec({
    describe<V10_RemoveVarPrefixFromXyPad> {
        val from by value { /*language=json*/
            """
                {
                    "feeds": {
                        "control1": {
                            "type": "baaahs.Core:XyPad",
                            "varPrefix": "foo"
                        },
                        "control2": {
                            "type": "baaahs.Core:Slider",
                            "varPrefix": "bar"
                        }
                    }
                }
            """.toJsonObject()
        }

        it("should remove `varPrefix` key from `baaahs.Core:XyPad` controls") {
            val to = V10_RemoveVarPrefixFromXyPad.migrate(from)

            expect(to).toEqual( /*language=json*/
                """
                    {
                        "feeds": {
                            "control1": {
                                "type": "baaahs.Core:XyPad"
                            },
                            "control2": {
                                "type": "baaahs.Core:Slider",
                                "varPrefix": "bar"
                            }
                        }
                    }
                """.toJsonObject()
            )
        }
    }
})

fun String.toJsonObject() = Json.parseToJsonElement(this).jsonObject
