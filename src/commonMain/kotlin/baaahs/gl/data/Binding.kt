package baaahs.gl.data

interface Binding {
    val feed: Feed?
    val isValid: Boolean

    fun setOnProgram()

    /**
     * Only release any resources specifically allocated by this Binding, not by
     * its parent [Feed].
     */
    fun release() {}
}