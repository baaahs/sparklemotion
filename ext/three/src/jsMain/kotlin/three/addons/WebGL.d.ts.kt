@file:JsModule("three")
@file:JsNonModule
@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package three.addons

import kotlin.js.*
import org.khronos.webgl.*
import org.w3c.dom.*
import org.w3c.dom.events.*
import org.w3c.dom.parsing.*
import org.w3c.dom.svg.*
import org.w3c.dom.url.*
import org.w3c.fetch.*
import org.w3c.files.*
import org.w3c.notifications.*
import org.w3c.performance.*
import org.w3c.workers.*
import org.w3c.xhr.*

@JsName("default")
external open class WebGL {
    companion object {
        fun isWebGL2Available(): Boolean
        fun isColorSpaceAvailable(colorSpace: PredefinedColorSpace): Boolean
        fun getWebGL2ErrorMessage(): HTMLElement
        fun getErrorMessage(version: Number): HTMLElement
        fun isWebGLAvailable(): Boolean
        fun getWebGLErrorMessage(): HTMLElement
    }
}