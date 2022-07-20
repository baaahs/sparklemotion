package baaahs.mapper

import baaahs.imaging.Bitmap
import baaahs.imaging.NativeBitmap
import baaahs.net.Network
import kotlinx.coroutines.delay
import kotlin.math.ceil
import kotlin.math.log2
import kotlin.math.roundToInt

class TwoLogNMappingStrategy : MappingStrategy() {
    override suspend fun capturePixelData(
        mapper: Mapper,
        session: Mapper.Session,
        brainsToMap: MutableMap<Network.Address, Mapper.BrainToMap>
    ) {
        Context(mapper, session, brainsToMap).capturePixelData()
    }

    class Context(
        val mapper: Mapper,
        val session: Mapper.Session,
        val brainsToMap: MutableMap<Network.Address, Mapper.BrainToMap>
    ) {
        suspend fun capturePixelData() {
            val maxPixelForTheseBrains = brainsToMap.values.maxOf { it.expectedPixelCountOrDefault }

            val neededBits = ceil(log2(maxPixelForTheseBrains.toDouble())).toInt()
            val maskBitmaps = arrayListOf<Pair<Bitmap, Bitmap>>()

            for (b in 0 until neededBits) {
                val bitmap0 = captureMaskedPixelsImage(b, false)
                delay(250)
                val bitmap1 = captureMaskedPixelsImage(b, true)
                delay(250)
                maskBitmaps.add(bitmap0 to bitmap1)
            }

            for (pixelIndex in 0 until maxPixelForTheseBrains) {
                val progress = (pixelIndex.toFloat() / maxPixelForTheseBrains * 100).roundToInt()
                mapper.showMessage("MAPPING PIXEL $pixelIndex / $maxPixelForTheseBrains ($progress%)…")
                var compositeBitmap: Bitmap? = null

                for (b in 0 until neededBits) {
                    val shouldBeOn = pixelIndex ushr b and 1 == 1

                    val mask = maskBitmaps[b].let { if (shouldBeOn) it.first else it.second }
                    if (compositeBitmap == null) {
                        compositeBitmap = mask.clone()
                    } else {
                        compositeBitmap.darken(mask)
                    }
                    mapper.showDiffImage(compositeBitmap)
                }

                val analysis = mapper.stats.diffImage.time {
                    ImageProcessing.analyze(compositeBitmap!!)
                }
                val pixelChangeRegion = mapper.stats.detectChangeRegion.time {
                    analysis.detectChangeRegion(.9f)
                }
                mapper.showDiffImage(compositeBitmap!!, pixelChangeRegion)
                //                        delay(500)

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
                        mapper.showMessage2("looks like no pixel $pixelIndex for ${brainToMap.brainId}…")
                        Mapper.logger.debug { "looks like no pixel $pixelIndex for ${brainToMap.brainId}…" }
                    }
                }
            }
        }

        private suspend fun captureMaskedPixelsImage(maskBit: Int, invert: Boolean): Bitmap {
            turnOnPixelsByMask(maskBit, invert)
            val pixelOnBitmap = mapper.stats.captureImage.stime {
                mapper.slowCamDelay()
                mapper.getBrightImageBitmap(2)
            }

            mapper.showBaseImage(session.baseBitmap!!)
            mapper.stats.diffImage.time {
                ImageProcessing.diff(pixelOnBitmap, session.baseBitmap!!, session.deltaBitmap)
            }
            mapper.showDiffImage(session.deltaBitmap)
            val thisDelta = session.deltaBitmap
            session.deltaBitmap = NativeBitmap(session.deltaBitmap.width, session.deltaBitmap.height)
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