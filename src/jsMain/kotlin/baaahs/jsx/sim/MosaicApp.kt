@file:JsModule("js/simulator/MosaicApp.jsx")
@file:JsNonModule

package baaahs.jsx.sim

import baaahs.SheepSimulator
import baaahs.sim.HostedWebApp
import react.RClass
import react.RProps

@JsName("default")
external val MosaicApp: RClass<MosaicAppProps>

external interface MosaicAppProps : RProps {
    var simulator: SheepSimulator
    var hostedWebApp: HostedWebApp
}
