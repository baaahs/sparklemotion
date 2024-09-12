@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package three.addons

import org.w3c.dom.HTMLElement
import three.Camera
import three.Object3D
import three.Scene

open external class CSS3DObject(element: HTMLElement) : Object3D {
    open var element: HTMLElement
    open var onBeforeRender: (renderer: Any, scene: Scene, camera: Camera) -> Unit
    open var onAfterRender: (renderer: Any, scene: Scene, camera: Camera) -> Unit
}

open external class CSS3DSprite(element: HTMLElement) : CSS3DObject

external interface CSS3DParameters {
    var element: HTMLElement?
        get() = definedExternally
        set(value) = definedExternally
}

open external class CSS3DRenderer(parameters: CSS3DParameters = definedExternally) {
    open var domElement: HTMLElement
    open fun getSize(): `T$95`
    open fun setSize(width: Number, height: Number)
    open fun render(scene: Scene, camera: Camera)
}