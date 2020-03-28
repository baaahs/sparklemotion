package baaahs.glsl

abstract class GlslManager {
    abstract val available: Boolean

    abstract fun createContext(): GlslContext
}