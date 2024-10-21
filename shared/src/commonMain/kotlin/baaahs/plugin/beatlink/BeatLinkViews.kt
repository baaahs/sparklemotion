package baaahs.plugin.beatlink

import baaahs.show.live.ControlProps
import baaahs.ui.View

interface BeatLinkViews {
    fun forControl(openButtonControl: OpenBeatLinkControl, controlProps: ControlProps): View
}

val beatLinkViews by lazy { getBeatLinkViews() }
expect fun getBeatLinkViews(): BeatLinkViews