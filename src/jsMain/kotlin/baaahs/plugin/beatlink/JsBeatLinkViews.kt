package baaahs.plugin.beatlink

import baaahs.app.ui.editor.renderWrapper
import baaahs.show.live.ControlProps
import react.dom.div

object JsBeatLinkViews : BeatLinkViews {
    override fun forControl(openButtonControl: OpenBeatLinkControl, controlProps: ControlProps) = renderWrapper {
        div {
            +"BeatLink!"
        }
    }
}

actual fun getBeatLinkViews(): BeatLinkViews = JsBeatLinkViews
