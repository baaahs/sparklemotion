package baaahs.gl

import com.danielgergely.kgl.Kgl

abstract class GlManager {
    abstract val available: Boolean

    abstract fun createContext(trace: Boolean = false): GlContext

    abstract fun createContext(monitor: Monitor, mode: Mode, trace: Boolean = false): GlContext

    protected fun maybeTrace(kgl: Kgl, trace: Boolean): Kgl {
        return if (trace) KglTracer(kgl) else kgl
    }

    abstract fun observeMonitors(monitors: Monitors)
}