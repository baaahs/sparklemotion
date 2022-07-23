package baaahs.mapper

import baaahs.imaging.Bitmap
import baaahs.imaging.createWritableBitmap
import baaahs.net.Network
import kotlinx.coroutines.delay
import kotlin.math.ceil
import kotlin.math.log2
import kotlin.math.roundToInt

object TwoLogNMappingStrategy : MappingStrategy() {
    override val title: String
        get() = "2 log(n)"

    override suspend fun capturePixelData(
        mapper: Mapper,
        stats: MapperStats,
        ui: MapperUi,
        session: Mapper.Session,
        brainsToMap: MutableMap<Network.Address, MappableBrain>,
        mapperBackend: MapperBackend
    ) {
        Context(mapper, stats, ui, session, brainsToMap).capturePixelData()
    }

    class Context(
        val mapper: Mapper,
        val stats: MapperStats,
        val ui: MapperUi,
        val session: Mapper.Session,
        val brainsToMap: MutableMap<Network.Address, MappableBrain>
    ) {
        suspend fun capturePixelData() {
            val maxPixelForTheseBrains = brainsToMap.values.maxOf { it.expectedPixelCountOrDefault }

            val neededBits = ceil(log2(maxPixelForTheseBrains.toDouble())).toInt()
            val maskBitmaps = arrayListOf<Pair<Bitmap, Bitmap>>()

            ui.message = "Capturing pixel masks…"
            for (b in 0 until neededBits) {
                ui.message2 = "${b * 2 + 1} of ${neededBits * 2} steps"
                val bitmap0 = captureMaskedPixelsImage(b, false)

                ui.message2 = "${b * 2 + 2} of ${neededBits * 2} steps"
                val bitmap1 = captureMaskedPixelsImage(b, true)

                maskBitmaps.add(bitmap0 to bitmap1)
            }

            ui.message = "Deriving pixel locations…"
            for (pixelIndex in 0 until maxPixelForTheseBrains) {
                val progress = (pixelIndex.toFloat() / maxPixelForTheseBrains * 100).roundToInt()
                ui.message2 = "$pixelIndex of $maxPixelForTheseBrains pixels — $progress%"

                var compositeBitmap: Bitmap? = null

                for (b in 0 until neededBits) {
                    val shouldBeOn = pixelIndex ushr b and 1 == 1

                    val mask = maskBitmaps[b].let { if (shouldBeOn) it.first else it.second }
                    if (compositeBitmap == null) {
                        compositeBitmap = mask.clone()
                    } else {
                        compositeBitmap.darken(mask)
                    }
                    ui.showDiffImage(compositeBitmap)
                }

                val analysis = stats.diffImage.time {
                    ImageProcessing.analyze(compositeBitmap!!)
                }
                val pixelChangeRegion = stats.detectChangeRegion.time {
                    analysis.detectChangeRegion(.9f)
                }
                ui.showDiffImage(compositeBitmap!!, pixelChangeRegion)
//                mapper.pauseForUserInteraction(ui.message2 ?: "???")
//                mapper.waitUntilUnpaused()

                brainsToMap.values.forEach { brainToMap ->
                    val visibleSurface = brainToMap.guessedVisibleSurface

                    Mapper.logger.debug { "* analysis: hasBrightSpots=${analysis.hasBrightSpots()}" }
                    Mapper.logger.debug { "* pixelChangeRegion=$pixelChangeRegion" }
                    if (analysis.hasBrightSpots() && !pixelChangeRegion.isEmpty()) {
                        val centerUv = pixelChangeRegion.centerUv
                        visibleSurface?.setPixel(pixelIndex, centerUv)
                        val pixelOnImageName = "fancy-mask-pixel-$pixelIndex.png"
                        brainToMap.pixelMapData[pixelIndex] = Mapper.PixelMapData(pixelChangeRegion, pixelOnImageName)
                        Mapper.logger.debug { "$pixelIndex/${brainToMap.brainId}: centerUv = $centerUv" }
                    } else {
                        ui.message2 = "looks like no pixel $pixelIndex for ${brainToMap.brainId}…"
                        Mapper.logger.debug { "looks like no pixel $pixelIndex for ${brainToMap.brainId}…" }
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
    }
}