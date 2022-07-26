package baaahs.mapper

import baaahs.imaging.Bitmap
import baaahs.imaging.createWritableBitmap
import baaahs.net.Network
import kotlinx.coroutines.delay
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.serializer
import kotlin.math.ceil
import kotlin.math.log2
import kotlin.math.roundToInt

object TwoLogNMappingStrategy : MappingStrategy() {
    override val title: String
        get() = "2 log(n)"

    override val sessionMetadataSerializer: KSerializer<out SessionMetadata>
        get() = serializer()
//    override val entityMetadataSerializer: KSerializer<out EntityMetadata>
//        get() = serializer()
    override val pixelMetadataSerializer: KSerializer<out PixelMetadata>
        get() = TwoLogNPixelMetadata.serializer()

    override fun beginSession(
        mapper: Mapper,
        session: Mapper.Session,
        stats: MapperStats,
        ui: MapperUi,
        brainsToMap: MutableMap<Network.Address, MappableBrain>,
        mapperBackend: MapperBackend
    ) = Session(mapper, stats, ui, session, brainsToMap, mapperBackend)

    class Slice(
        val bitmapA: Bitmap,
        val bitmapB: Bitmap,
        val bitmapAnd: Bitmap
    )

    class Session(
        val mapper: Mapper,
        val stats: MapperStats,
        val ui: MapperUi,
        val session: Mapper.Session,
        val brainsToMap: MutableMap<Network.Address, MappableBrain>,
        val mapperBackend: MapperBackend
    ) : MappingStrategy.Session {
        override suspend fun captureControllerData(mappableBrain: MappableBrain) {
        }

        override suspend fun capturePixelData() {
            val maxPixelForTheseBrains = brainsToMap.values.maxOf { it.expectedPixelCountOrDefault }

            val pixelSlices = ceil(log2(maxPixelForTheseBrains.toDouble())).toInt()
            session.metadata = TwoLogNSessionMetadata(pixelSlices)
            val slices = arrayListOf<Slice>()

            ui.message = "Capturing pixel masks…"
            for (b in 0 until pixelSlices) {
                ui.message2 = "${b * 2 + 1} of ${pixelSlices * 2} steps"
                val bitmap0 = captureMaskedPixelsImage(b, false)

                ui.message2 = "${b * 2 + 2} of ${pixelSlices * 2} steps"
                val bitmap1 = captureMaskedPixelsImage(b, true)

                val bitmapBoth = bitmap0.clone()
                bitmapBoth.darken(bitmap1)
                bitmap0.subtract(bitmapBoth)
                bitmap1.subtract(bitmapBoth)
                slices.add(Slice(bitmap0, bitmap1, bitmapBoth))

                mapperBackend.saveImage(session.sessionStartTime, maskedImageName(b, false), bitmap0)
                mapperBackend.saveImage(session.sessionStartTime, maskedImageName(b, true), bitmap1)
                mapperBackend.saveImage(session.sessionStartTime, maskedImageName(b, "both"), bitmapBoth)
            }

            ui.message = "Deriving pixel locations…"
            for (pixelIndex in 0 until maxPixelForTheseBrains) {
                val progress = (pixelIndex.toFloat() / maxPixelForTheseBrains * 100).roundToInt()
                ui.message2 = "$pixelIndex of $maxPixelForTheseBrains pixels — $progress%"

                val compositeBitmap = reconstructFromSlices(slices, pixelIndex)

//                ui.showPanelMask(mask)
                ui.showDiffImage(compositeBitmap)

                val analysis = stats.diffImage.time {
                    ImageProcessing.analyze(compositeBitmap)
                }
                val pixelChangeRegion = stats.detectChangeRegion.time {
                    analysis.detectChangeRegion(.9f)
                }
                ui.showDiffImage(compositeBitmap, pixelChangeRegion)
//                mapper.pauseForUserInteraction(ui.message2 ?: "???")
//                mapper.waitUntilUnpaused()

                val pixelOnImageName = mapperBackend.saveImage(session.sessionStartTime, "pixel-$pixelIndex", compositeBitmap)

                brainsToMap.values.forEach { brainToMap ->
                    val visibleSurface = brainToMap.guessedVisibleSurface

                    Mapper.logger.debug { "* analysis: hasBrightSpots=${analysis.hasBrightSpots()}" }
                    Mapper.logger.debug { "* pixelChangeRegion=$pixelChangeRegion" }
                    if (analysis.hasBrightSpots() && !pixelChangeRegion.isEmpty()) {
                        val centerUv = pixelChangeRegion.centerUv
                        visibleSurface?.setPixel(pixelIndex, centerUv)
                        brainToMap.pixelMapData[pixelIndex] = Mapper.PixelMapData(
                            pixelChangeRegion,
                            TwoLogNPixelMetadata(pixelOnImageName)
                        )
                        Mapper.logger.debug { "$pixelIndex/${brainToMap.brainId}: centerUv = $centerUv" }
                    } else {
                        ui.message2 = "looks like no pixel $pixelIndex for ${brainToMap.brainId}…"
                        Mapper.logger.debug { "looks like no pixel $pixelIndex for ${brainToMap.brainId}…" }
                    }
                }

                delay(1)
            }
        }

        private fun reconstructFromSlices(
            slices: ArrayList<Slice>,
            pixelIndex: Int
        ): Bitmap {
            var compositeBitmap: Bitmap? = null

            for (b in 0 until slices.size) {
                val shouldBeOn = pixelIndex ushr b and 1 == 1

                val mask = slices[b].let { if (shouldBeOn) it.bitmapA else it.bitmapB }
                if (compositeBitmap == null) {
                    compositeBitmap = mask.clone()
                } else {
                    compositeBitmap.darken(mask)
                }
            }
            return compositeBitmap!!
        }

        private suspend fun captureMaskedPixelsImage(maskBit: Int, invert: Boolean): Bitmap {
            turnOnPixelsByMask(maskBit, invert)
            delay(125)

            val pixelOnBitmap = stats.captureImage.stime {
                mapper.slowCamDelay()
                mapper.getBrightImageBitmap(2)
            }

            ui.showBaseImage(session.baseBitmap!!)
            stats.diffImage.time {
                ImageProcessing.diff(pixelOnBitmap, session.baseBitmap!!, session.deltaBitmap)
            }
            ui.showDiffImage(session.deltaBitmap)
            val thisDelta = session.deltaBitmap
            session.deltaBitmap = createWritableBitmap(session.deltaBitmap.width, session.deltaBitmap.height)

            delay(125)
            return thisDelta
        }

        private suspend fun turnOnPixelsByMask(maskBit: Int, invert: Boolean) {
            session.resetToBase()
            val mask = 1 shl maskBit
            brainsToMap.values.forEach { brainToMap ->
                for (i in 0 until brainToMap.expectedPixelCountOrDefault) {
                    val pixelOn = if (i and mask != 0) !invert else invert
                    if (pixelOn) {
                        brainToMap.pixelShaderBuffer[i] = 1
                    }
                }
            }

            mapper.sendToAllReliably(brainsToMap.values) { it.pixelShaderBuffer }
        }
    }

    fun maskedImageName(mapBit: Int, invert: Boolean) =
        maskedImageName(mapBit, if (invert) "B" else "A")

    fun maskedImageName(mapBit: Int, type: String) =
        "mask-$mapBit$type"

    @Serializable @SerialName("TwoLogN")
    data class TwoLogNSessionMetadata(
        val pixelSlices: Int
    ) : SessionMetadata

//    @Serializable @SerialName("TwoLogN")
//    data class TwoLogNEntityMetadata(
//        val deltaImage: String? = null
//    ) : EntityMetadata

    @Serializable @SerialName("TwoLogN")
    data class TwoLogNPixelMetadata(
        val deltaImage: String? = null
    ) : PixelMetadata
}