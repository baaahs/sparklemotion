package baaahs.plugin.sound_analysis

import baaahs.util.Logger
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.Line
import javax.sound.sampled.Mixer
import javax.sound.sampled.SourceDataLine

class JvmSoundAnalysisPlatform : SoundAnalysisPlatform {
    override suspend fun listAudioInputs(): List<AudioInput> {
        return getPlaybackMixerInfos().map { JvmAudioInput(it) }
    }

    override fun createConstantQAnalyzer(audioInput: AudioInput, sampleRate: Float): SoundAnalyzer {
        audioInput as JvmAudioInput

        val mixer = AudioSystem.getMixer(audioInput.mixerInfo)
        return JvmSoundAnalyzer(mixer, sampleRate)
    }

    private fun getPlaybackMixerInfos(): List<Mixer.Info> {
        return AudioSystem.getMixerInfo().mapNotNull { mixerInfo ->
            if (mixerInfo.name != "DJM-900NXS2") return@mapNotNull null

            val mixer = AudioSystem.getMixer(mixerInfo)
            logger.warn { "* ${mixer.mixerInfo.name}" }
            mixer.sourceLineInfo.forEach { lineInfo: Line.Info? ->
                logger.warn { "** ${lineInfo?.lineClass}"}
                (lineInfo as? SourceDataLine)?.let {
                    logger.warn { "** $lineInfo"}
                }
            }
            // Mixer capable of audio play back if source LineWavelet length != 0
            if (mixer.sourceLineInfo.isNotEmpty()) mixerInfo else null
        }.also {
            logger.warn { "${it.size} playback mixers available:" }
            it.forEach {
                logger.debug { "* ${it.name} (${it.description})" }
            }
        }
    }

    private class JvmAudioInput(val mixerInfo: Mixer.Info) : AudioInput {
        override val id: String
            get() = mixerInfo.name
        override val title: String
            get() = mixerInfo.name
    }

    companion object {
        private val logger = Logger<JvmSoundAnalysisPlatform>()
    }
}