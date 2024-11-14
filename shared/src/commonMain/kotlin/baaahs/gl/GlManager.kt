package baaahs.gl

import com.danielgergely.kgl.Kgl

abstract class GlManager {
    abstract val available: Boolean

    abstract fun createContext(name: String, trace: Boolean = false): GlContext

    protected fun maybeTrace(kgl: Kgl, trace: Boolean): Kgl {
        return if (true || trace) KglTracer(kgl) else kgl
    }
}