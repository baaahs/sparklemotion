@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package three.addons

import org.w3c.dom.HTMLElement
import three.Camera
import three.Object3D
import three.Scene
import three.Vector2

open external class CSS2DObject(element: HTMLElement) : Object3D {
    open var element: HTMLElement
    open var center: Vector2
    open var onBeforeRender: (renderer: Any, scene: Scene, camera: Camera) -> Unit
    open var onAfterRender: (renderer: Any, scene: Scene, camera: Camera) -> Unit
}

external interface CSS2DParameters {
    var element: HTMLElement?
        get() = definedExternally
        set(value) = definedExternally
}

external interface `T$95` {
    var width: Number
    var height: Number
}

open external class CSS2DRenderer(parameters: CSS2DParameters = definedExternally) {
    open var domElement: HTMLElement
    open fun getSize(): `T$95`
    open fun setSize(width: Number, height: Number)
    open fun render(scene: Scene, camera: Camera)
}