package baaahs.plugin.beatlink

import baaahs.show.live.ControlProps
import baaahs.ui.renderWrapper

object JsBeatLinkViews : BeatLinkViews {
    override fun forControl(openButtonControl: OpenBeatLinkControl, controlProps: ControlProps) = renderWrapper {
        beatLinkControl {
            attrs.controlProps = controlProps
            attrs.beatLinkControl = openButtonControl
        }
    }
}

actual fun getBeatLinkViews(): BeatLinkViews = JsBeatLinkViews
