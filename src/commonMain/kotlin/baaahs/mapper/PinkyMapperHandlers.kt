package baaahs.mapper

import baaahs.api.ws.WebSocketRouter
import baaahs.decodeBase64
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive

open class PinkyMapperHandlers(val storage: Storage) {
    fun register(builder: WebSocketRouter.HandlerBuilder) {
        builder.apply {
            handle("listImages") { args ->
                val sessionName = args[1].jsonPrimitive.contentOrNull
                json.encodeToJsonElement(
                    ListSerializer(String.serializer()),
                    storage.listImages(sessionName).map {
                        it.relativeTo(storage.imagesDir)
                    }
                )
            }

            handle("saveImage") { args ->
                val name = args[1].jsonPrimitive.contentOrNull
                val imageDataBase64 = args[2].jsonPrimitive.contentOrNull
                val imageData = decodeBase64(imageDataBase64!!)
                storage.saveImage(name!!, imageData)
                JsonNull
            }

            handle("getImageUrl") { args ->
                val name = args[1].jsonPrimitive.contentOrNull
                JsonPrimitive(name?.let { getImageUrl(it) })
            }

            handle("listSessions") {
                json.encodeToJsonElement(
                    ListSerializer(String.serializer()),
                    storage.listSessions().map {
                        it.relativeTo(storage.mappingSessionsDir)
                    }
                )
            }

            handle("saveSession") { args ->
                val mappingSession = json.decodeFromJsonElement(
                    MappingSession.serializer(), args[1]
                )
                val file = storage.saveSession(mappingSession)
                val sessionName = file.relativeTo(storage.mappingSessionsDir)
                JsonPrimitive(sessionName)
            }

            handle("loadSession") { args ->
                val sessionName = json.decodeFromJsonElement(String.serializer(), args[1])
                val mappingSession = storage.loadMappingSession(sessionName)
                json.encodeToJsonElement(MappingSession.serializer(), mappingSession)
            }
        }
    }

    open suspend fun getImageUrl(name: String): String {
        return "/data/mapping-sessions/images/$name"
    }
}