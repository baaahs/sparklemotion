package baaahs.plugin.beatlink

import baaahs.show.live.ControlProps
import baaahs.ui.Renderer

interface BeatLinkViews {
    fun forControl(openButtonControl: OpenBeatLinkControl, controlProps: ControlProps): Renderer
}

val beatLinkViews by lazy { getBeatLinkViews() }
expect fun getBeatLinkViews(): BeatLinkViews