package baaahs.plugin.sound_analysis

import baaahs.plugin.PluginContext
import baaahs.plugin.SoundAnalysis.soundAnalysisControl
import baaahs.show.live.ControlProps
import baaahs.ui.View
import baaahs.ui.renderWrapper

internal actual fun createServerSoundAnalyzer(pluginContext: PluginContext): SoundAnalyzer =
    SoundAnalysisPlugin.PubSubSubscriber(pluginContext.pubSub)

actual fun getSoundAnalysisViews(): SoundAnalysisViews =
    object : SoundAnalysisViews {
        override fun forControl(openButtonControl: OpenSoundAnalysisControl, controlProps: ControlProps): View =
            renderWrapper {
                soundAnalysisControl {
                    attrs.controlProps = controlProps
                    attrs.soundAnalysisControl = openButtonControl
                }
            }

        override fun forSettingsPanel(): View = renderWrapper {
            soundAnalysisSettingsPanel {}
        }
    }