package baaahs.gl

abstract class GlManager {
    abstract val available: Boolean

    abstract fun createContext(): GlContext
}