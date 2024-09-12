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

external open class CSS3DObject(element: HTMLElement) : Object3D__0 {
    open var element: HTMLElement
    open var onBeforeRender: (renderer: Any, scene: Scene, camera: Camera) -> Unit
    open var onAfterRender: (renderer: Any, scene: Scene, camera: Camera) -> Unit
}

external open class CSS3DSprite(element: HTMLElement) : CSS3DObject

external interface CSS3DParameters {
    var element: HTMLElement?
        get() = definedExternally
        set(value) = definedExternally
}

external open class CSS3DRenderer(parameters: CSS3DParameters = definedExternally) {
    open var domElement: HTMLElement
    open fun getSize(): `T$95`
    open fun setSize(width: Number, height: Number)
    open fun render(scene: Scene, camera: Camera)
}