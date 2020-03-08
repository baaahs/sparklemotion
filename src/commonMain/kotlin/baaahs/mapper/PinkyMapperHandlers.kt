package baaahs.mapper

import baaahs.api.ws.WebSocketRouter
import baaahs.decodeBase64
import kotlinx.serialization.builtins.list
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.JsonNull

class PinkyMapperHandlers(val storage: Storage) {
    fun register(builder: WebSocketRouter.HandlerBuilder) {
        builder.apply {
            handle("listSessions") {
                json.toJson(String.serializer().list, storage.listSessions())
            }

            handle("saveImage") { args ->
                val name = args[1].primitive.contentOrNull
                val imageDataBase64 = args[2].primitive.contentOrNull
                val imageData = decodeBase64(imageDataBase64!!)
                storage.saveImage(name!!, imageData)
                JsonNull
            }

            handle("saveSession") { args ->
                val mappingSession = json.fromJson(
                    MappingSession.serializer(), args[1]
                )
                storage.saveSession(mappingSession)
                JsonNull
            }
        }
    }
}