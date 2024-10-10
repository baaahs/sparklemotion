package baaahs.sim

import baaahs.model.LightRing
import baaahs.model.Model
import baaahs.model.MovingHead
import baaahs.model.PixelArray
import baaahs.visualizer.EntityAdapter

actual fun getSimulations(): PlatformSimulations {
    return object : PlatformSimulations {
        override fun forLightBar(pixelArray: PixelArray, adapter: EntityAdapter): FixtureSimulation =
            LightBarSimulation(pixelArray, adapter)

        override fun forLightRing(lightRing: LightRing, adapter: EntityAdapter): FixtureSimulation =
            LightRingSimulation(lightRing, adapter)

        override fun forMovingHead(movingHead: MovingHead, adapter: EntityAdapter): FixtureSimulation =
            MovingHeadSimulation(movingHead, adapter)

        override fun forSurface(surface: Model.Surface, adapter: EntityAdapter): FixtureSimulation =
            BrainSurfaceSimulation(surface, adapter)
    }
}