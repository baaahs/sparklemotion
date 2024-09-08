package baaahs.sim.ui

import baaahs.sim.ui.SimulatorStyles.brainIndicator
import baaahs.sm.brain.sim.BrainSimulator
import baaahs.ui.and
import baaahs.ui.unaryPlus
import baaahs.ui.xComponent
import kotlinx.html.js.onMouseOverFunction
import react.Props
import react.RBuilder
import react.RHandler
import react.dom.div
import react.dom.onClick

private val BrainIndicatorView = xComponent<BrainIndicatorProps>("BrainIndicator") { props ->
    observe(props.brainSimulator)

    val handleMouseOver by handler(props.onSelect, props.brainSimulator) { _: kotlinx.html.org.w3c.dom.events.Event ->
        props.onSelect(props.brainSimulator)
    }
    val handleClick by mouseEventHandler(props.brainSimulator) {
        props.brainSimulator.reset()
    }


    val brainStateClass = (+when (props.brainSimulator.state) {
        BrainSimulator.State.Booting -> SimulatorStyles.brainStateBooting
        BrainSimulator.State.Linked -> SimulatorStyles.brainStateLinked
        BrainSimulator.State.Hello -> SimulatorStyles.brainStateHello
        BrainSimulator.State.Mapped -> SimulatorStyles.brainStateMapped
        BrainSimulator.State.Shading -> SimulatorStyles.brainStateShading
        BrainSimulator.State.Rebooting -> SimulatorStyles.brainStateRebooting
        BrainSimulator.State.Stopped -> SimulatorStyles.brainStateStopped
    })

    div(+brainIndicator and brainStateClass) {
        attrs.onClick = handleClick
        attrs.onMouseOverFunction = handleMouseOver
    }
}

external interface BrainIndicatorProps : Props {
    var brainSimulator: BrainSimulator.Facade
    var onSelect: (BrainSimulator.Facade) -> Unit
}

fun RBuilder.brainIndicator(handler: RHandler<BrainIndicatorProps>) =
    child(BrainIndicatorView, handler = handler)