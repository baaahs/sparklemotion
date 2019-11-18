package baaahs

interface SoundAnalyzer {
    val frequencies: FloatArray

    fun listen(analysisListener: AnalysisListener)
    fun unlisten(analysisListener: AnalysisListener)

    interface AnalysisListener {
        fun onSample(analysis: Analysis)
    }

    class Analysis(val frequencies: FloatArray, val magnitudes: FloatArray)
}