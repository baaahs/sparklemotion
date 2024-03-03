package baaahs.show.migration

import baaahs.describe
import baaahs.toEqual
import ch.tutteli.atrium.api.verbs.expect
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import org.spekframework.spek2.Spek

object V10_RemoveVarPrefixFromXyPadSpec : Spek({
    describe<V10_RemoveVarPrefixFromXyPad> {
        val from by value { /*language=json*/
            """
                {
                    "controls": {
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
                        "controls": {
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