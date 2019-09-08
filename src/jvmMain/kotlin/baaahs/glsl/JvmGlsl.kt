package baaahs.glsl

import baaahs.IdentifiedSurface
import baaahs.SheepModel
import baaahs.geom.Vector3F
import kotlin.random.Random

fun main() {
    // On a Mac, it's necessary to start the JVM with this arg: `-XstartOnFirstThread`
    val renderer = GlslBase.manager.createRenderer("// TBD!", emptyList()) as JvmGlslRenderer

    renderer.addSurface(
        IdentifiedSurface(
            SheepModel.Panel("Panel"),
            600,
            List(600) { Vector3F(Random.nextFloat(), Random.nextFloat(), Random.nextFloat()) }),
        PanelSpaceUvTranslator
    )

    renderer.runStandalone()
}

actual object GlslBase {
    actual val manager: GlslManager by lazy { JvmGlslManager() }
}

