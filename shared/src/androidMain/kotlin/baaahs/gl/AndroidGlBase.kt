package baaahs.gl

import android.opengl.EGL14
import android.opengl.EGLConfig
import android.opengl.EGLContext
import android.opengl.EGLDisplay
import android.opengl.EGLSurface
import android.opengl.GLES20.glGetIntegerv
import android.util.Log
import baaahs.util.Logger
import com.danielgergely.kgl.Kgl
import com.danielgergely.kgl.KglAndroid

actual object GlBase {
    actual val manager: GlManager by lazy { AndroidGlManager() }
}

class AndroidGlManager : GlManager() {
    override val available: Boolean
        get() = true

    override fun createContext(name: String, trace: Boolean): GlContext {
        return AndroidGlContext(name, maybeTrace(KglAndroid, true))
    }

    inner class AndroidGlContext(name: String, kgl: Kgl) : GlContext(name, kgl, "300 es", checkForErrors = true) {
        private val eglDisplay: EGLDisplay
        private val eglContext: EGLContext
        private val eglSurface: EGLSurface

        init {
            // Step 1: Get the EGL display connection
            eglDisplay = EGL14.eglGetDisplay(EGL14.EGL_DEFAULT_DISPLAY)
            if (eglDisplay == EGL14.EGL_NO_DISPLAY) {
                throw RuntimeException("Unable to get EGL14 display")
            }

            // Step 2: Initialize the EGL display
            val version = IntArray(2)
            if (!EGL14.eglInitialize(eglDisplay, version, 0, version, 1)) {
                throw RuntimeException("Unable to initialize EGL14")
            }
            logger.warn { "EGL version: ${version[0]}.${version[1]}" }

            // Step 3: Choose an EGLConfig
            val attribList = intArrayOf(
                EGL14.EGL_RENDERABLE_TYPE, EGL14.EGL_OPENGL_ES2_BIT,
                EGL14.EGL_SURFACE_TYPE, EGL14.EGL_PBUFFER_BIT,  // PBUFFER for offscreen rendering
                EGL14.EGL_BLUE_SIZE, 8,
                EGL14.EGL_GREEN_SIZE, 8,
                EGL14.EGL_RED_SIZE, 8,
                EGL14.EGL_ALPHA_SIZE, 8,
                EGL14.EGL_NONE
            )
            val configs = arrayOfNulls<EGLConfig>(1)
            val numConfigs = IntArray(1)
            if (!EGL14.eglChooseConfig(eglDisplay, attribList, 0, configs, 0, configs.size, numConfigs, 0)) {
                throw RuntimeException("Unable to choose EGL config")
            }
            val eglConfig: EGLConfig = configs[0] ?: throw RuntimeException("No config chosen")

            // Step 4: Create an EGL context
            val attribListContext = intArrayOf(
                EGL14.EGL_CONTEXT_CLIENT_VERSION, 3,
                EGL14.EGL_NONE
            )
            eglContext = EGL14.eglCreateContext(eglDisplay, eglConfig, EGL14.EGL_NO_CONTEXT, attribListContext, 0)
            if (eglContext == EGL14.EGL_NO_CONTEXT) {
                throw RuntimeException("Unable to create EGL context")
            }

            // Step 5: Create a PBuffer surface for offscreen rendering
            val surfaceAttribs = intArrayOf(
                EGL14.EGL_WIDTH, 1,
                EGL14.EGL_HEIGHT, 1,
                EGL14.EGL_NONE
            )
            eglSurface = EGL14.eglCreatePbufferSurface(eglDisplay, eglConfig, surfaceAttribs, 0)
            if (eglSurface == EGL14.EGL_NO_SURFACE) {
                throw RuntimeException("Unable to create EGL surface")
            }

            // Now the OpenGL context is initialized and you can make OpenGL calls.
            // Don't forget to release resources (context, surface, display) after use.
        }

        private var nestLevel = 0
        override fun <T> runInContext(fn: () -> T): T {
            if (++nestLevel == 1) {
                Log.d("AndroidGlManager", "${Thread.currentThread().name}: -> runInContext($eglContext)")
                if (!EGL14.eglMakeCurrent(eglDisplay, eglSurface, eglSurface, eglContext)) {
                    throw RuntimeException("Unable to make EGL context current")
                }
            }

            try {
                return fn()
            } finally {
                if (--nestLevel == 0) {
                    Log.d("AndroidGlManager", "${Thread.currentThread().name}: <- runInContext")
                    EGL14.eglMakeCurrent(eglDisplay, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_CONTEXT)
                }
            }
        }

        override suspend fun <T> asyncRunInContext(fn: suspend () -> T): T {
            if (++nestLevel == 1) {
                if (!EGL14.eglMakeCurrent(eglDisplay, eglSurface, eglSurface, eglContext)) {
                    throw RuntimeException("Unable to make EGL context current")
                }
            }

            try {
                return fn()
            } finally {
                if (--nestLevel == 0) {
                    EGL14.eglMakeCurrent(eglDisplay, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_CONTEXT)
                }
            }
        }

        override fun getGlInt(parameter: Int): Int {
            val value = IntArray(1)
            glGetIntegerv(parameter, value, 0)
            return value[0]
        }
    }

    companion object {
        private val logger = Logger<AndroidGlManager>()
    }
}