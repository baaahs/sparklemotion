package baaahs.plugin.sound_analysis

interface SoundAnalyzer {
    val numberOfBuckets: Int

    fun listen(analysisListener: AnalysisListener)
    fun unlisten(analysisListener: AnalysisListener)

    interface AnalysisListener {
        fun onSample(analysis: Analysis)
    }

    class Analysis(val frequencies: FloatArray, val magnitudes: FloatArray)
}