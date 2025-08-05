package baaahs.mapper

import baaahs.imaging.Bitmap
import baaahs.imaging.createWritableBitmap
import baaahs.net.Network
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
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

    class Slices(
        val slices: List<Slice>
    ) : List<Slice> by slices {
        fun reconstructPixel(pixelIndex: Int): Bitmap {
            var compositeBitmap: Bitmap? = null

            for (b in 0 until slices.size) {
                val shouldBeOn = pixelIndex ushr b and 1 == 1

                val mask = slices[b]
                    .let { if (shouldBeOn) it.snapshot0 else it.snapshot1 }
                    .exclusiveBitmap

                if (compositeBitmap == null) {
                    compositeBitmap = mask.clone()
                } else {
                    compositeBitmap.darken(mask)
                }
            }
            return compositeBitmap!!
        }
    }

    class Slice(
        snapshot0Bitmap: Bitmap,
        snapshot0FileName: String?,
        snapshot1Bitmap: Bitmap,
        snapshot1FileName: String?
    ) {
        val overlap by lazy { snapshot0.originalBitmap.clone().darken(snapshot1.originalBitmap) }
        val snapshot0 = Snapshot(snapshot0Bitmap, snapshot0FileName)
        val snapshot1 = Snapshot(snapshot1Bitmap, snapshot1FileName)
        val snapshots get() = listOf(snapshot0, snapshot1)

        inner class Snapshot(
            val originalBitmap: Bitmap,
            val originalImageName: String?,
        ) {
            val exclusiveBitmap: Bitmap by lazy { originalBitmap.clone().subtract(overlap) }
        }

        companion object {
            fun build(
                bitmap0: Bitmap,
                bitmap0Image: String?,
                bitmap1: Bitmap,
                bitmap1Image: String?
            ): Slice = Slice(bitmap0, bitmap0Image, bitmap1, bitmap1Image)
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
                slices.map { it.snapshots.map { it.originalImageName } }
            )

            derivePixelLocations(maxPixelForTheseBrains, slices)

            findMissingPixels(maxPixelForTheseBrains)
        }

        private suspend fun buildSlices(sliceCount: Int): Slices {
            val slices = arrayListOf<Slice>()

            ui.message = "Capturing pixel masks…"
            for (sliceIndex in 0 until sliceCount) {
                ui.message2 = "${sliceIndex * 2 + 1} of ${sliceCount * 2} steps"
                val bitmap0 = captureMaskedPixelsImage(sliceIndex, false)
                val deferredImage0Name = saveSliceImage(sliceIndex, Half.First, bitmap0)

                ui.message2 = "${sliceIndex * 2 + 2} of ${sliceCount * 2} steps"
                val bitmap1 = captureMaskedPixelsImage(sliceIndex, true)
                val deferredImage1Name = saveSliceImage(sliceIndex, Half.Second, bitmap1)

                val slice = Slice.build(bitmap0, deferredImage0Name.await(), bitmap1, deferredImage1Name.await())
                slices.add(slice)
            }
            return Slices(slices)
        }

        private fun saveSliceImage(i: Int, half: Half, bitmap: Bitmap): Deferred<String> {
            val origBitmap = bitmap.clone()
            return scope.async {
                mapperBackend.saveImage(session.sessionStartTime, maskedImageName(i, half), origBitmap)
            }
        }

        private suspend fun derivePixelLocations(
            maxPixelForTheseBrains: Int,
            slices: Slices
        ) {
            ui.message = "Deriving pixel locations…"
            for (pixelIndex in 0 until maxPixelForTheseBrains) {
                val progress = (pixelIndex.toFloat() / maxPixelForTheseBrains * 100).roundToInt()
                ui.message2 = "$pixelIndex of $maxPixelForTheseBrains pixels — $progress%"

                val compositeBitmap = slices.reconstructPixel(pixelIndex)

                ui.showDiffImage(compositeBitmap)

                val pixelOnImageName = null

                brainsToMap.values.forEach { brainToMap ->
                    val visibleSurface = brainToMap.guessedVisibleSurface

                    val analysis = stats.diffImage.time {
                        ImageProcessing.analyze(compositeBitmap)
                    }
                    val pixelChangeRegion = stats.detectChangeRegion.time {
                        analysis.detectChangeRegion(.9f)
                    }
                    ui.showDiffImage(compositeBitmap, pixelChangeRegion)
                    //                mapper.pauseForUserInteraction(ui.message2 ?: "???")
                    //                mapper.waitUntilUnpaused()

                    Mapper.logger.debug { "* analysis: hasBrightSpots=${analysis.hasBrightSpots()}" }
                    Mapper.logger.debug { "* pixelChangeRegion=$pixelChangeRegion" }
                    if (/*analysis.hasBrightSpots() &&*/ !pixelChangeRegion.isEmpty() /*&& pixelChangeRegion.changedAmount < .01*/) {
                        val centerUv = pixelChangeRegion.centerUv
                        visibleSurface?.setPixel(pixelIndex, centerUv)
                        brainToMap.pixelMapData[pixelIndex] = Mapper.PixelMapData(
                            pixelChangeRegion,
                            TwoLogNPixelMetadata(pixelOnImageName, null)
                        )
                        Mapper.logger.debug { "$pixelIndex/${brainToMap.brainId}: centerUv = $centerUv" }
                    } else {
                        ui.message2 = "looks like no pixel $pixelIndex for ${brainToMap.brainId}…"
                        Mapper.logger.warn {
                            "looks like no pixel $pixelIndex for ${brainToMap.brainId}…\n" +
                                    "hasBrightSpots == ${analysis.hasBrightSpots()} && !pixelChangeRegion.isEmpty() = ${!pixelChangeRegion.isEmpty()} && changedAmount == ${pixelChangeRegion.changedAmount}"
                        }
                    }
                }

                delay(1)
            }
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
            var totalPixels = 0
            var ambiguousPixels = 0
            brainsToMap.values.forEach { brainToMap ->
                for (pixelIndex in 0 until maxPixelForTheseBrains) {
                    totalPixels++
                    if (brainToMap.pixelMapData[pixelIndex] == null) {
                        ambiguousPixels++
                    }
                }
            }

            ui.message = "Find ambiguous pixels ($ambiguousPixels / $totalPixels)…"
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

    fun maskedImageName(sliceIndex: Int, half: Half) =
        "mask-$sliceIndex${half.shortName}"

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

enum class Half(val shortName: String) {
    First("A"), Second("B")
}