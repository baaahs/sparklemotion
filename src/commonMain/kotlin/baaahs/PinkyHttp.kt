package baaahs

import baaahs.mapper.MappingResults
import baaahs.net.Network
import kotlinx.serialization.json.*

class PinkyHttp(val httpServer: Network.HttpServer) {
    private val json =
        Json(JsonConfiguration.Stable)

    fun register(
        brainInfos: MutableMap<BrainId, BrainInfo>,
        mappingResults: MappingResults,
        model: Model<*>
    ) {
        httpServer.routing {
            get("/brains/{name}") { call ->
                val brainInfo = call.param("name")?.let { name -> brainInfos[BrainId(name)] }
                val mappedInfo = call.param("name")?.let { name -> mappingResults.dataForBrain(
                    BrainId(
                        name
                    )
                ) }
                if (brainInfo == null && mappedInfo == null) {
                    Network.TextResponse(404, "not found")
                } else {
                    Network.JsonResponse(json,
                        200,
                        JsonObject(mapOf(
                            "name" to JsonPrimitive(call.param("name")),
                            "surface" to JsonPrimitive(
                                mappedInfo?.surface?.name
                                    ?: (brainInfo?.surface as? IdentifiedSurface)?.name
                            ),
                            "mapped" to JsonPrimitive(
                                mappedInfo?.pixelLocations?.isNotEmpty() ?: false
                            ),
                            "online" to JsonPrimitive(brainInfo != null),
                            "pixels" to JsonArray(mappedInfo?.pixelLocations?.map { pixel ->
                                JsonObject(
                                    mapOf(
                                        "x" to JsonPrimitive(pixel?.x),
                                        "y" to JsonPrimitive(pixel?.y),
                                        "z" to JsonPrimitive(pixel?.z)
                                    )
                                )
                            } ?: listOf())
                        )))
                }
            }
            get("/brains") {
                val allBrains = mutableMapOf<BrainId, Triple<String?, Boolean, Boolean>>()
                for ((k, v) in brainInfos) {
                    val info = allBrains.getOrPut(k, { Triple(null, false, false) })
                    val surfaceName = (v.surface as? IdentifiedSurface)?.name
                    val hasPixels = (v.surface as? IdentifiedSurface)?.pixelLocations?.isNotEmpty() ?: false
                    allBrains[k] = info.copy(first = surfaceName, second = hasPixels, third = true)
                }
                mappingResults.forEachBrain {
                    val info = allBrains.getOrPut(it.key, { Triple(null, false, false) })
                    val surfaceName = it.value.surface.name
                    val hasPixels = it.value.pixelLocations?.isNotEmpty() ?: false
                    allBrains[it.key] = info.copy(first = surfaceName, second = hasPixels)
                }

                Network.JsonResponse(
                    json, 200, JsonObject(
                        mapOf(
                            *allBrains.entries.map { (brainId, triple) ->
                                Pair(
                                    brainId.uuid, JsonObject(
                                        mapOf(
                                            "surface" to JsonPrimitive(
                                                triple.first
                                            ),
                                            "mapped" to JsonPrimitive(
                                                triple.second
                                            ),
                                            "online" to JsonPrimitive(
                                                triple.third
                                            )
                                        )
                                    )
                                )
                            }.toTypedArray()
                        )
                    )
                )
            }
            get("/surfaces/{name}") { call ->
                val surface = call.param("name")?.let { name -> model.findModelSurface(name) }
                if (surface != null) {
                    val allVertices = model.geomVertices
                    val vertices = surface.allVertices()
                    val mappedBrains = mappingResults.dataForSurface(surface.name)

                    Network.JsonResponse(
                        json, 200, JsonObject(
                            mapOf(
                                "name" to JsonPrimitive(surface.name),
                                "description" to JsonPrimitive(surface.description),
                                "expectedPixelCount" to JsonPrimitive(
                                    surface.expectedPixelCount ?: -1
                                ),
                                "vertices" to JsonArray(vertices.map { vertex ->
                                    JsonObject(
                                        mapOf(
                                            "x" to JsonPrimitive(vertex.x),
                                            "y" to JsonPrimitive(vertex.y),
                                            "z" to JsonPrimitive(vertex.z)
                                        )
                                    )
                                }),
                                "faces" to JsonArray(surface.faces.map { face ->
                                    JsonArray(face.vertexIds.map { vertexIndex ->
                                        JsonPrimitive(
                                            vertices.indexOf(
                                                allVertices.get(vertexIndex)
                                            )
                                        )
                                    })
                                }),
                                "lines" to JsonArray(surface.lines.map {
                                    JsonArray(it.vertices.map { vertex ->
                                        JsonPrimitive(
                                            vertices.indexOf(
                                                vertex
                                            )
                                        )
                                    })
                                }),
                                "brains" to JsonObject(mapOf(*mappedBrains?.entries?.map { (brainId, info) ->
                                    Pair(
                                        brainId.uuid, JsonObject(
                                            mapOf(
                                                "mapped" to JsonPrimitive(
                                                    info.pixelLocations?.isNotEmpty() ?: false
                                                ),
                                                "online" to JsonPrimitive(
                                                    brainInfos[brainId] != null
                                                )
                                            )
                                        )
                                    )
                                }?.toTypedArray() ?: arrayOf()))
                            )
                        )
                    )
                } else {
                    Network.TextResponse(404, "not found")
                }
            }
            get("/surfaces") {
                Network.JsonResponse(
                    json, 200,
                    JsonArray(model.allSurfaces.map { surface ->
                        JsonPrimitive(
                            surface.name
                        )
                    })
                )
            }
        }
    }
}