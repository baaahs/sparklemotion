package baaahs.plugin.beatlink

import baaahs.app.ui.editor.renderWrapper
import baaahs.show.live.ControlProps

object JsBeatLinkViews : BeatLinkViews {
    override fun forControl(openButtonControl: OpenBeatLinkControl, controlProps: ControlProps) = renderWrapper {
        beatLinkControl {
            attrs.controlProps = controlProps
            attrs.beatLinkControl = openButtonControl
        }
    }
}

actual fun getBeatLinkViews(): BeatLinkViews = JsBeatLinkViews
