package baaahs

import baaahs.geom.Vector3F
import baaahs.mapper.MappingResults
import baaahs.net.Network
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.list
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration

class PinkyHttp(val httpServer: Network.HttpServer) {
    private val json = Json(JsonConfiguration.Stable)

    fun register(
        brainInfos: MutableMap<BrainId, BrainInfo>,
        mappingResults: MappingResults,
        model: Model<*>
    ) {
        httpServer.routing {
            get("/brains/{name}") { call ->
                val brainInfo = call.param("name")?.let { name -> brainInfos[BrainId(name)] }
                val mappedInfo = call.param("name")?.let { name -> mappingResults.dataForBrain(BrainId(name)) }
                if (brainInfo == null && mappedInfo == null) {
                    Network.TextResponse(404, "not found")
                } else {
                    val brainData = BrainData(
                        name = call.param("name"),
                        surface = mappedInfo?.surface?.name ?: (brainInfo?.surface as? IdentifiedSurface)?.name,
                        mapped = mappedInfo?.pixelLocations?.isNotEmpty() ?: false,
                        online = brainInfo != null,
                        pixels = mappedInfo?.pixelLocations ?: emptyList()
                    )
                    Network.JsonResponse.create(json, 200, brainData, BrainData.serializer())
                }
            }

            get("/brains") {
                val allBrains = mutableMapOf<BrainId, TerseBrainData>()
                for ((k, v) in brainInfos) {
                    val info = allBrains.getOrPut(k, { TerseBrainData(null, false, false) })
                    val surfaceName = (v.surface as? IdentifiedSurface)?.name
                    val hasPixels = (v.surface as? IdentifiedSurface)?.pixelLocations?.isNotEmpty() ?: false
                    allBrains[k] = info.copy(surface = surfaceName, mapped = hasPixels, online = true)
                }
                mappingResults.forEachBrain {
                    val info = allBrains.getOrPut(it.key, { TerseBrainData(null, false, false) })
                    val surfaceName = it.value.surface.name
                    val hasPixels = it.value.pixelLocations?.isNotEmpty() ?: false
                    allBrains[it.key] = info.copy(surface = surfaceName, mapped = hasPixels)
                }

                Network.JsonResponse.create(json, 200,
                    allBrains, MapSerializer(BrainId.serializer(), TerseBrainData.serializer()))
            }

            get("/surfaces/{name}") { call ->
                val surface = call.param("name")?.let { name -> model.findModelSurface(name) }
                if (surface != null) {
                    val allVertices = model.geomVertices
                    val vertices = surface.allVertices()
                    val mappedBrains = mappingResults.dataForSurface(surface.name)

                    Network.JsonResponse.create(json, 200,
                        SurfaceData(name = surface.name,
                            description = surface.description,
                            expectedPixelCount = surface.expectedPixelCount ?: -1,
                            vertices = vertices.toList(),
                            faces = surface.faces.map { face ->
                                face.vertexIds.map { vertexIndex ->
                                    vertices.indexOf(
                                        allVertices.get(vertexIndex)
                                    )
                                }
                            },
                            lines = surface.lines.map {
                                it.vertices.map { vertex -> vertices.indexOf(vertex) }
                            },
                            brains = mappedBrains?.entries?.associate { (brainId, info) ->
                                brainId to TerseBrainData(
                                    surface = null,
                                    mapped = info.pixelLocations?.isNotEmpty() ?: false,
                                    online = brainInfos[brainId] != null
                                )
                            } ?: emptyMap()
                        ),
                        SurfaceData.serializer()
                    )
                } else {
                    Network.TextResponse(404, "not found")
                }
            }

            get("/surfaces") {
                Network.JsonResponse.create(
                    json, 200,
                    model.allSurfaces.map { surface -> surface.name },
                    String.serializer().list
                )
            }
        }
    }

    @Serializable
    data class BrainData(
        val name: String?,
        val surface: String?,
        val mapped: Boolean,
        val online: Boolean,
        val pixels: List<Vector3F?>
    )

    @Serializable
    data class TerseBrainData(
        val surface: String?,
        val mapped: Boolean,
        val online: Boolean
    )

    @Serializable
    data class SurfaceData(
        val name: String,
        val description: String,
        val expectedPixelCount: Int?,
        val vertices: List<Vector3F>,
        val faces: List<List<Int>>,
        val lines: List<List<Int>>,
        val brains: Map<BrainId, TerseBrainData>
    )
}