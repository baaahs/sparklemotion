package baaahs.mapper

import baaahs.imaging.Bitmap
import baaahs.net.Network
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

class OneAtATimeMappingStrategy : MappingStrategy() {
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

            val pixelStep = 4
            fun actualPixelIndex(pixelIndexX: Int) =
                pixelIndexX * pixelStep % maxPixelForTheseBrains + pixelIndexX * pixelStep / maxPixelForTheseBrains

            for (pixelIndexX in 0 until maxPixelForTheseBrains) {
                // Reorder so we get e.g. 0, 4, 8, ..., 1, 5, 9, ..., 2, 6, 10, ..., 3, 7, 11, ...
                val pixelIndex = actualPixelIndex(pixelIndexX)
                identifyPixel(pixelIndex, maxPixelForTheseBrains, pixelIndexX)

                //                    pauseForUserInteraction()
                mapper.waitUntilUnpaused()
                allPixelsOff()
            }
        }

        private suspend fun identifyPixel(pixelIndex: Int, maxPixelForTheseBrains: Int, pixelIndexX: Int) {
            val progress = (pixelIndexX.toFloat() / maxPixelForTheseBrains * 100).roundToInt()
            mapper.showMessage("MAPPING PIXEL $pixelIndex / $maxPixelForTheseBrains ($progress%)…")

            if (pixelIndex % 128 == 0) Mapper.logger.debug { "pixel $pixelIndex... isRunning is $mapper.isRunning" }
            mapper.stats.turnOnPixel.stime { session.turnOnPixel(pixelIndex) }

            val pixelOnBitmap = mapper.stats.captureImage.stime {
                mapper.slowCamDelay()
                mapper.getBrightImageBitmap(2)
            }

            // TODO: for now we're doing this later so the pixel remains lit while debugging.
//            // turn off pixel now so it doesn't leak into next frame...
//            resetToBase()
//            stats.turnOffPixel.stime {
//                sendToAllReliably(brainsWithPixel(pixelIndex)) { it.pixelShaderBuffer }
//            }
//            // we won't block here yet...

            mapper.showBaseImage(session.baseBitmap!!)
            mapper.stats.diffImage.time {
                ImageProcessing.diff(pixelOnBitmap, session.baseBitmap!!, session.deltaBitmap)
            }
            mapper.showDiffImage(session.deltaBitmap)
            val pixelOnImageName = mapper.webSocketClient.saveImage(session.sessionStartTime, "pixel-$pixelIndex", session.deltaBitmap)

            brainsToMap.values.forEach { brainToMap ->
                mapper.stats.identifyPixel.time {
                    identifyBrainPixel(pixelIndex, brainToMap, pixelOnBitmap, session.deltaBitmap, pixelOnImageName)
                }

                delay(1)
//                pauseForUserInteraction()
                mapper.waitUntilUnpaused()
            }

            // turn off pixel now so it doesn't leak into next frame...
            session.resetToBase()
            mapper.stats.turnOffPixel.stime {
                mapper.sendToAllReliably(session.brainsWithPixel(pixelIndex)) { it.pixelShaderBuffer }
            }
            // we won't block here yet...

            mapper.waitForDelivery() // ... of resetting to black above.
        }

        private fun identifyBrainPixel(
            pixelIndex: Int,
            brainToMap: Mapper.BrainToMap,
            pixelOnBitmap: Bitmap,
            deltaBitmap: Bitmap,
            pixelOnImageName: String
        ) {
            mapper.showMessage("MAPPING PIXEL $pixelIndex / ${Mapper.maxPixelsPerBrain} (${brainToMap.brainId})…")
            val surfaceChangeRegion = brainToMap.changeRegion
            val visibleSurface = brainToMap.guessedVisibleSurface

            if (surfaceChangeRegion != null && surfaceChangeRegion.sqPix() > 0 && visibleSurface != null) {
                mapper.showPanelMask(brainToMap.panelDeltaBitmap!!)

                mapper.showBaseImage(session.baseBitmap!!)
                mapper.showSnapshot(pixelOnBitmap)
                val analysis = mapper.stats.diffImage.time {
                    ImageProcessing.diff(
                        pixelOnBitmap,
                        session.baseBitmap!!,
                        deltaBitmap,
                        brainToMap.panelDeltaBitmap!!,
                        surfaceChangeRegion
                    )
                }
                val pixelChangeRegion = mapper.stats.detectChangeRegion.time {
                    analysis.detectChangeRegion(.9f)
                }
                mapper.showDiffImage(deltaBitmap, pixelChangeRegion)
                mapper.showPanelMask(brainToMap.panelDeltaBitmap!!, pixelChangeRegion)
                Mapper.logger.debug {
                    "pixelChangeRegion($pixelIndex,${brainToMap.guessedEntity?.name} =" +
                            " $pixelChangeRegion ${pixelChangeRegion.width}x${pixelChangeRegion.height}"
                }

                Mapper.logger.debug { "* analysis: hasBrightSpots=${analysis.hasBrightSpots()}" }
                Mapper.logger.debug { "* pixelChangeRegion=$pixelChangeRegion" }
                Mapper.logger.debug { "* surfaceChangeRegion=$surfaceChangeRegion" }
                if (analysis.hasBrightSpots() && !pixelChangeRegion.isEmpty()) {
                    val centerUv = pixelChangeRegion.centerUv
                    visibleSurface.setPixel(pixelIndex, centerUv)
                    brainToMap.pixelMapData[pixelIndex] = Mapper.PixelMapData(pixelChangeRegion, pixelOnImageName)
                    Mapper.logger.debug { "$pixelIndex/${brainToMap.brainId}: centerUv = $centerUv" }
                } else {
                    mapper.showMessage2("looks like no pixel $pixelIndex for ${brainToMap.brainId}…")
                    Mapper.logger.debug { "looks like no pixel $pixelIndex for ${brainToMap.brainId}…" }
                }
            }
        }

        private suspend fun allPixelsOff() {
            session.resetToBase()
            mapper.sendToAllReliably(brainsToMap.values) { it.pixelShaderBuffer }
        }
    }
}