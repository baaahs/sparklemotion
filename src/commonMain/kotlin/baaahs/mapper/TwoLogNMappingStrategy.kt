package baaahs.mapper

import baaahs.imaging.Bitmap
import baaahs.imaging.createWritableBitmap
import baaahs.net.Network
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
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
        scope: CoroutineScope,
        mapper: Mapper,
        session: Mapper.Session,
        stats: MapperStats,
        ui: MapperUi,
        brainsToMap: MutableMap<Network.Address, MappableBrain>,
        mapperBackend: MapperBackend
    ) = Session(scope, mapper, stats, ui, session, brainsToMap, mapperBackend)

    class Slices(val slices: List<Slice>) : List<Slice> by slices

    class Slice(
        val frame0: Frame,
        val frame1: Frame,
        val overlapBitmap: Bitmap
    ) {
        val frames get() = listOf(frame0, frame1)

        class Frame(
            val originalBitmap: Bitmap,
            val originalImageName: String?,
            val exclusiveBitmap: Bitmap
        )

        companion object {
            fun build(
                bitmap0: Bitmap,
                bitmap0Image: String?,
                bitmap1: Bitmap,
                bitmap1Image: String?
            ): Slice {
                val overlap = bitmap0.clone().darken(bitmap1)
                val excl0 = bitmap0.clone().subtract(overlap)
                val excl1 = bitmap1.clone().subtract(overlap)
                return Slice(
                    Frame(bitmap0, bitmap0Image, excl0),
                    Frame(bitmap1, bitmap1Image, excl1),
                    overlap
                )
            }
        }
    }

    class Session(
        val scope: CoroutineScope,
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

            val sliceCount = ceil(log2(maxPixelForTheseBrains.toDouble())).toInt()
            val slices = buildSlices(sliceCount)
            session.metadata = TwoLogNSessionMetadata(
                sliceCount,
                slices.map { it.frames.map { it.originalImageName } }
            )

            derivePixelLocations(maxPixelForTheseBrains, slices)

            findMissingPixels(maxPixelForTheseBrains)
        }

        private suspend fun buildSlices(sliceCount: Int): Slices {
            val slices = arrayListOf<Slice>()

            ui.message = "Capturing pixel masks…"
            for (b in 0 until sliceCount) {
                ui.message2 = "${b * 2 + 1} of ${sliceCount * 2} steps"
                val bitmap0 = captureMaskedPixelsImage(b, false)
                val deferredImage0Name = scope.async {
                    mapperBackend.saveImage(session.sessionStartTime, maskedImageName(b, false), bitmap0)
                }

                ui.message2 = "${b * 2 + 2} of ${sliceCount * 2} steps"
                val bitmap1 = captureMaskedPixelsImage(b, true)
                val deferredImage1Name = scope.async {
                    mapperBackend.saveImage(session.sessionStartTime, maskedImageName(b, true), bitmap1)
                }

                val bitmapBoth = bitmap0.clone()
                bitmapBoth.darken(bitmap1)
                bitmap0.subtract(bitmapBoth)
                bitmap1.subtract(bitmapBoth)
                val slice = Slice.build(bitmap0, deferredImage0Name.await(), bitmap1, deferredImage1Name.await())
//                val slice = Slice.build(
//                    bitmap0.clone().subtract(bitmapBoth), deferredImage0Name.await(),
//                    bitmap1.clone().subtract(bitmapBoth), deferredImage1Name.await())

                slices.add(slice)

                mapperBackend.saveImage(session.sessionStartTime, maskedImageName(b, "both"), bitmapBoth)
            }
            return Slices(slices)
        }

        private suspend fun derivePixelLocations(
            maxPixelForTheseBrains: Int,
            slices: Slices
        ) {
            ui.message = "Deriving pixel locations…"
            for (pixelIndex in 0 until maxPixelForTheseBrains) {
                val progress = (pixelIndex.toFloat() / maxPixelForTheseBrains * 100).roundToInt()
                ui.message2 = "$pixelIndex of $maxPixelForTheseBrains pixels — $progress%"

                val compositeBitmap = reconstructFromSlices(slices, pixelIndex)

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

                val pixelOnImageName =
                    mapperBackend.saveImage(session.sessionStartTime, "pixel-$pixelIndex", compositeBitmap)

                brainsToMap.values.forEach { brainToMap ->
                    val visibleSurface = brainToMap.guessedVisibleSurface

                    Mapper.logger.debug { "* analysis: hasBrightSpots=${analysis.hasBrightSpots()}" }
                    Mapper.logger.debug { "* pixelChangeRegion=$pixelChangeRegion" }
                    if (analysis.hasBrightSpots() && !pixelChangeRegion.isEmpty() && pixelChangeRegion.changedAmount < .01) {
                        val centerUv = pixelChangeRegion.centerUv
                        visibleSurface?.setPixel(pixelIndex, centerUv)
                        brainToMap.pixelMapData[pixelIndex] = Mapper.PixelMapData(
                            pixelChangeRegion,
                            TwoLogNPixelMetadata(pixelOnImageName, null)
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
            slices: Slices,
            pixelIndex: Int
        ): Bitmap {
            var compositeBitmap: Bitmap? = null

            for (b in 0 until slices.size) {
                val shouldBeOn = pixelIndex ushr b and 1 == 1

                val mask = slices[b]
                    .let { if (shouldBeOn) it.frame0 else it.frame1 }
                    .exclusiveBitmap

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

        private suspend fun findMissingPixels(maxPixelForTheseBrains: Int) {
            brainsToMap.values.forEach { brainToMap ->
                val visibleSurface = brainToMap.guessedVisibleSurface

                for (pixelIndex in 0 until maxPixelForTheseBrains) {
                    session.resetToBase()

                    if (brainToMap.pixelMapData[pixelIndex] == null) {
                        ui.message2 = "Find pixel $pixelIndex for ${brainToMap.brainId}"

                        delay(125)
                        stats.turnOnPixel.stime { session.turnOnPixel(pixelIndex) }
                        delay(125)

                        val pixelOnBitmap = stats.captureImage.stime {
                            mapper.slowCamDelay()
                            mapper.getBrightImageBitmap(2)
                        }


                        ui.showBaseImage(session.baseBitmap!!)
                        val analysis = stats.diffImage.time {
                            ImageProcessing.diff(pixelOnBitmap, session.baseBitmap!!, session.deltaBitmap)
                        }

                        val pixelChangeRegion = stats.detectChangeRegion.time {
                            analysis.detectChangeRegion(.9f)
                        }

                        ui.showDiffImage(session.deltaBitmap, pixelChangeRegion)
                        val pixelOnImageName = mapperBackend.saveImage(session.sessionStartTime, "pixel-$pixelIndex", session.deltaBitmap)

                        if (analysis.hasBrightSpots() && !pixelChangeRegion.isEmpty()) {
                            val centerUv = pixelChangeRegion.centerUv
                            visibleSurface?.setPixel(pixelIndex, centerUv)
                            brainToMap.pixelMapData[pixelIndex] = Mapper.PixelMapData(
                                pixelChangeRegion,
                                TwoLogNPixelMetadata(null, pixelOnImageName)
                            )
                            Mapper.logger.debug { "$pixelIndex/${brainToMap.brainId}: centerUv = $centerUv" }
                        }
                    }

                    delay(1)
                }
            }
        }
    }

    fun maskedImageName(mapBit: Int, invert: Boolean) =
        maskedImageName(mapBit, if (invert) "B" else "A")

    fun maskedImageName(mapBit: Int, type: String) =
        "mask-$mapBit$type"

    @Serializable @SerialName("TwoLogN")
    data class TwoLogNSessionMetadata(
        val sliceCount: Int,
        val sliceImageNames: List<List<String?>>?
    ) : SessionMetadata

//    @Serializable @SerialName("TwoLogN")
//    data class TwoLogNEntityMetadata(
//        val deltaImage: String? = null
//    ) : EntityMetadata

    @Serializable @SerialName("TwoLogN")
    data class TwoLogNPixelMetadata(
        val calculatedImage: String? = null,
        val singleImage: String? = null
    ) : PixelMetadata
}