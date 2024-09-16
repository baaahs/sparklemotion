@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package three.addons

import org.w3c.dom.HTMLElement
import three.WebGLRenderer
import three.XRSessionInit

open external class VRButton {
    companion object {
        fun createButton(renderer: WebGLRenderer, sessionInit: XRSessionInit = definedExternally): HTMLElement
    }
}