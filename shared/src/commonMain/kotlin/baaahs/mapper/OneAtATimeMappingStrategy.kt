package baaahs.mapper

import baaahs.imaging.Bitmap
import baaahs.net.Network
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.serializer
import kotlin.math.roundToInt

object OneAtATimeMappingStrategy : MappingStrategy() {
    override val title: String
        get() = "One At A Time"

    override val sessionMetadataSerializer: KSerializer<out SessionMetadata>
        get() = serializer()
//    override val entityMetadataSerializer: KSerializer<out EntityMetadata>
//        get() = serializer()
    override val pixelMetadataSerializer: KSerializer<out PixelMetadata>
        get() = OneAtATimePixelMetadata.serializer()

    override fun beginSession(
        scope: CoroutineScope,
        mapper: Mapper,
        session: Mapper.Session,
        stats: MapperStats,
        ui: MapperUi,
        brainsToMap: MutableMap<Network.Address, MappableBrain>,
        mapperBackend: MapperBackend
    ) = Session(mapper, stats, ui, session, brainsToMap, mapperBackend)

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
            ui.showMessage("MAPPING PIXEL $pixelIndex / $maxPixelForTheseBrains ($progress%)…")

            if (pixelIndex % 128 == 0) Mapper.logger.debug { "pixel $pixelIndex... isRunning is $mapper.isRunning" }
            stats.turnOnPixel.stime { session.turnOnPixel(pixelIndex) }

            val pixelOnBitmap = stats.captureImage.stime {
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

            ui.showBaseImage(session.baseBitmap!!)
            stats.diffImage.time {
                ImageProcessing.diff(pixelOnBitmap, session.baseBitmap!!, session.deltaBitmap)
            }
            ui.showDiffImage(session.deltaBitmap)
            val pixelOnImageName = mapperBackend.saveImage(session.sessionStartTime, "pixel-$pixelIndex", session.deltaBitmap)

            brainsToMap.values.forEach { brainToMap ->
                stats.identifyPixel.time {
                    identifyBrainPixel(pixelIndex, brainToMap, pixelOnBitmap, session.deltaBitmap, pixelOnImageName)
                }

                delay(1)
//                pauseForUserInteraction()
                mapper.waitUntilUnpaused()
            }

            // turn off pixel now so it doesn't leak into next frame...
            session.resetToBase()
            stats.turnOffPixel.stime {
                mapper.sendToAllReliably(session.brainsWithPixel(pixelIndex)) { it.pixelShaderBuffer }
            }
            // we won't block here yet...

            mapper.waitForDelivery() // ... of resetting to black above.
        }

        private fun identifyBrainPixel(
            pixelIndex: Int,
            mappableBrain: MappableBrain,
            pixelOnBitmap: Bitmap,
            deltaBitmap: Bitmap,
            pixelOnImageName: String
        ) {
            ui.showMessage("MAPPING PIXEL $pixelIndex / ${Mapper.maxPixelsPerBrain} (${mappableBrain.brainId})…")
            val surfaceChangeRegion = mappableBrain.changeRegion
            val visibleSurface = mappableBrain.guessedVisibleSurface

            if (surfaceChangeRegion != null && surfaceChangeRegion.sqPix() > 0 && visibleSurface != null) {
                ui.showPanelMask(mappableBrain.panelDeltaBitmap!!)

                ui.showBaseImage(session.baseBitmap!!)
                ui.showSnapshot(pixelOnBitmap)
                val analysis = stats.diffImage.time {
                    ImageProcessing.diff(
                        pixelOnBitmap,
                        session.baseBitmap!!,
                        deltaBitmap,
                        mappableBrain.panelDeltaBitmap!!,
                        surfaceChangeRegion
                    )
                }
                val pixelChangeRegion = stats.detectChangeRegion.time {
                    analysis.detectChangeRegion(.9f)
                }
                ui.showDiffImage(deltaBitmap, pixelChangeRegion)
                ui.showPanelMask(mappableBrain.panelDeltaBitmap!!, pixelChangeRegion)
                Mapper.logger.debug {
                    "pixelChangeRegion($pixelIndex,${mappableBrain.guessedEntity?.name} =" +
                            " $pixelChangeRegion ${pixelChangeRegion.width}x${pixelChangeRegion.height}"
                }

                Mapper.logger.debug { "* analysis: hasBrightSpots=${analysis.hasBrightSpots()}" }
                Mapper.logger.debug { "* pixelChangeRegion=$pixelChangeRegion" }
                Mapper.logger.debug { "* surfaceChangeRegion=$surfaceChangeRegion" }
                if (analysis.hasBrightSpots() && !pixelChangeRegion.isEmpty()) {
                    val centerUv = pixelChangeRegion.centerUv
                    visibleSurface.setPixel(pixelIndex, centerUv)
                    mappableBrain.pixelMapData[pixelIndex] = Mapper.PixelMapData(
                        pixelChangeRegion,
                        OneAtATimePixelMetadata(pixelOnImageName)
                    )
                    Mapper.logger.debug { "$pixelIndex/${mappableBrain.brainId}: centerUv = $centerUv" }
                } else {
                    ui.showMessage2("looks like no pixel $pixelIndex for ${mappableBrain.brainId}…")
                    Mapper.logger.debug { "looks like no pixel $pixelIndex for ${mappableBrain.brainId}…" }
                }
            }
        }

        private suspend fun allPixelsOff() {
            session.resetToBase()
            mapper.sendToAllReliably(brainsToMap.values) { it.pixelShaderBuffer }
        }
    }

    @Serializable @SerialName("OneAtATime")
    class OneAtATimeSessionMetadata() : SessionMetadata

//    @Serializable @SerialName("OneAtATime")
//    data class OneAtATimeEntityMetadata(
//        val deltaImage: String? = null
//    ) : EntityMetadata

    @Serializable @SerialName("OneAtATime")
    data class OneAtATimePixelMetadata(
        val deltaImage: String? = null
    ) : PixelMetadata
}