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

external interface LUT3dlResult {
    var size: Number
    var texture3D: Data3DTexture
}

external open class LUT3dlLoader(manager: LoadingManager = definedExternally) : Loader__1<LUT3dlResult> {
    open var type: Any
    open fun setType(type: Any): LUT3dlLoader /* this */
    open fun parse(input: String): LUT3dlResult
}