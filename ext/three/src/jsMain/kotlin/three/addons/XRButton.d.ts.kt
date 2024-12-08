package three.addons

import org.w3c.dom.HTMLElement
import three.WebGLRenderer
import three.XRSessionInit

open external class XRButton {
    companion object {
        fun createButton(renderer: WebGLRenderer, sessionInit: XRSessionInit = definedExternally): HTMLElement
    }
}