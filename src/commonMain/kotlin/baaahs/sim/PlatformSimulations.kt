package baaahs.sim

import baaahs.model.LightRing
import baaahs.model.Model
import baaahs.model.MovingHead
import baaahs.model.PixelArray
import baaahs.visualizer.EntityAdapter

interface PlatformSimulations {
    fun forLightBar(pixelArray: PixelArray, adapter: EntityAdapter): FixtureSimulation
    fun forLightRing(lightRing: LightRing, adapter: EntityAdapter): FixtureSimulation
    fun forMovingHead(movingHead: MovingHead, adapter: EntityAdapter): FixtureSimulation
    fun forSurface(surface: Model.Surface, adapter: EntityAdapter): FixtureSimulation
}

val simulations by lazy { getSimulations() }
expect fun getSimulations(): PlatformSimulations