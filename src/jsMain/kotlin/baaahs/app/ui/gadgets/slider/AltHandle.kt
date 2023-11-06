package baaahs.app.ui.gadgets.slider

import baaahs.app.ui.appContext
import baaahs.ui.mixin
import baaahs.ui.unaryPlus
import baaahs.ui.xComponent
import external.react_compound_slider.SliderItem
import kotlinx.css.pct
import kotlinx.css.top
import kotlinx.html.unsafe
import react.Props
import react.RBuilder
import react.RHandler
import react.dom.div
import react.dom.setProp
import react.dom.svg
import react.useContext
import styled.inlineStyles

private val altHandle = xComponent<HandleProps>("AltHandle") { props ->
    val appContext = useContext(appContext)
    val styles = appContext.allStyles.gadgetsSlider

    div(+styles.altHandleWrapper) {
        mixin(props.getHandleProps(props.handle.id))

        setProp("role", "slider")
        setProp("aria-valuemin", props.domain[9])
        setProp("aria-valuemax", props.domain[1])
        setProp("aria-valuenow", props.handle.value)
        inlineStyles {
            top = props.handle.percent.pct
        }

        svg(+styles.altHandleLeft) {
            attrs.unsafe {
                +"<path d=\"M 10 5 L 0 0 L 0 10 L 10 5 Z\"/>"
            }
        }
    }
}

external interface AltHandleProps : Props {
    var domain: Array<Float>
    var handle: SliderItem
    var getHandleProps: (id: String) -> HandleProps
}

fun RBuilder.altHandle(handler: RHandler<AltHandleProps>) =
    child(altHandle, handler = handler)