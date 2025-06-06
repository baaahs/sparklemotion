@file:JsModule("three")
@file:JsNonModule
package three.addons

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
import three.*
import web.images.PredefinedColorSpace
import kotlin.js.*

@JsName("default")
open external class WebGL {
    companion object {
        fun isWebGL2Available(): Boolean
        fun isColorSpaceAvailable(colorSpace: PredefinedColorSpace): Boolean
        fun getWebGL2ErrorMessage(): HTMLElement
        fun getErrorMessage(version: Number): HTMLElement
        fun isWebGLAvailable(): Boolean
        fun getWebGLErrorMessage(): HTMLElement
    }
}