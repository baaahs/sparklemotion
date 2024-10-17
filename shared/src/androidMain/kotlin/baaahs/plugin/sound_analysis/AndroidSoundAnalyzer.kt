package baaahs.plugin.sound_analysis

import baaahs.plugin.PluginContext

//class IosSoundAnalyzer(
//    private val clock: Clock
//) : SoundAnalyzer {
//    private val listeners = hashSetOf<SoundAnalyzer.AnalysisListener>()
//    private val inputsListeners = hashSetOf<SoundAnalyzer.InputsListener>()
//
//    private var mixerInfos = getPlaybackMixerInfos()
//    private var audioInputs = mixerInfos.map { it.toAudioInput() }
//
//    override var currentAudioInput: AudioInput? = null
//    private var currentAnalyzerStream: AnalyzerStream? = null
//
//    // 40hz/12 bins/spread of 2.3 fits into an 8k buffer with 87 buckets; not quite the piano keys but close.
//    private val constantQ = ConstantQ(sampleRate, 40f, 6000f, 12f, 0.001f, 2.3f)
//
//    init {
//        logger.debug { "FFT length: ${constantQ.ffTlength}; bins: ${constantQ.numberOfOutputBands}" }
//
//        thread(isDaemon = true, name = "JvmSoundAnalyzer Source Watcher") {
//            while (true) {
//                sleep(5000)
//                val newMixerInfos = getPlaybackMixerInfos()
//                val newAudioInputs = newMixerInfos.map { it.toAudioInput() }
//                if (newAudioInputs != audioInputs) {
//                    mixerInfos = newMixerInfos
//                    audioInputs = newAudioInputs
//
//                    notifyChanged()
//                }
//            }
//        }
//    }
//
//    override fun listAudioInputs(): List<AudioInput> = audioInputs
//
//    override suspend fun switchTo(audioInput: AudioInput?) {
//        if (audioInput == currentAudioInput) return
//
//        if (audioInput == null) {
//            currentAnalyzerStream?.release()
//            currentAnalyzerStream = null
//            currentAudioInput = audioInput
//        } else {
//            val mixerInfo = mixerInfos.find { audioInput.matches(it) }
//                ?: return
//
//            val mixer = AudioSystem.getMixer(mixerInfo)
//
//            currentAnalyzerStream?.release()
//            currentAnalyzerStream = AnalyzerStream(mixer, audioFormat, constantQ, clock) { magnitudes, time ->
//                listeners.forEach { it.onSample(SoundAnalyzer.Analysis(constantQ.freqencies, magnitudes, time)) }
//            }
//            currentAudioInput = audioInput
//        }
//
//        notifyChanged()
//    }
//
//    override fun listen(analysisListener: SoundAnalyzer.AnalysisListener): SoundAnalyzer.AnalysisListener {
//        listeners.add(analysisListener)
//        return analysisListener
//    }
//
//    override fun unlisten(analysisListener: SoundAnalyzer.AnalysisListener) {
//        listeners.remove(analysisListener)
//    }
//
//    override fun listen(inputsListener: SoundAnalyzer.InputsListener): SoundAnalyzer.InputsListener {
//        inputsListeners.add(inputsListener)
//        return inputsListener
//    }
//
//    override fun unlisten(inputsListener: SoundAnalyzer.InputsListener) {
//        inputsListeners.remove(inputsListener)
//    }
//
//    private fun notifyChanged() {
//        inputsListeners.forEach { it.onChange(audioInputs, currentAudioInput) }
//    }
//
//    private fun getPlaybackMixerInfos(): List<Mixer.Info> {
//        return AudioSystem.getMixerInfo().mapNotNull { mixerInfo ->
//            val mixer = AudioSystem.getMixer(mixerInfo)
//            if (mixer.targetLineInfo.any { it is DataLine.Info }) mixerInfo else null
//        }.also {
//            logger.debug { "${it.size} playback mixers available:" }
//            it.forEach {
//                logger.debug { "* ${it.name} (${it.description})" }
//            }
//        }
//    }
//
//    private fun AudioInput.matches(info: Mixer.Info): Boolean {
//        return info.toAudioInput() == this
//    }
//
//    private fun Mixer.Info.toAudioInput() = AudioInput(name, name)
//
//    companion object {
//        private val logger = Logger<IosSoundAnalyzer>()
//
//        private const val sampleRate = 44100f
//        private val audioFormat = AudioFormat(
//            sampleRate, 16, /*channels =*/ 1,
//            /*signed =*/ true, /*bigEndian =*/ false
//        )
//    }
//}

internal actual fun createServerSoundAnalyzer(pluginContext: PluginContext): SoundAnalyzer =
    TODO() // IosSoundAnalyzer(pluginContext.clock)

actual fun getSoundAnalysisViews(): SoundAnalysisViews =
    TODO("Sound analysis plugin view not implemented on JVM")
