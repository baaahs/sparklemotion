package baaahs.show.migration

import baaahs.show.DataMigrator
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonObject

/**
 * Old "shader channels" are now "streams".
 */
@Suppress("ClassName")
object V8_RenameShaderChannelToStream : DataMigrator.Migration(8) {
    override fun migrate(from: JsonObject): JsonObject {
        return from.toMutableMap().apply {
            replaceJsonObj("patches") { patches ->
                JsonObject(patches.toMutableMap().apply {
                    keys.forEach { key ->
                        replaceJsonObj(key) { patch ->
                            patch.toMutableMap().apply {
                                replaceJsonObj("incomingLinks") { incomingLinks ->
                                    JsonObject(incomingLinks.mapValues { (_, format) ->
                                        if (format.type == "shader-channel") {
                                            format.jsonObject.edit {
                                                put("type", JsonPrimitive("stream"))
                                                put("stream", remove("shaderChannel")!!)
                                            }
                                        } else format
                                    })
                                }

                                val shaderChannel = remove("shaderChannel")
                                if (shaderChannel != null) {
                                    put("stream", shaderChannel)
                                }
                            }.let { JsonObject(it) }
                        }
                    }
                })
            }
        }.toJsonObj()
    }
}