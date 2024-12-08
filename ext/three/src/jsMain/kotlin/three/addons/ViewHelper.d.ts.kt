package three.addons

import org.w3c.dom.HTMLElement
import org.w3c.dom.events.MouseEvent
import three.Camera
import three.Object3D
import three.Vector3
import three.WebGLRenderer

open external class ViewHelper(camera: Camera, domElement: HTMLElement) : Object3D {
    open val isViewHelper: Boolean
    open var animating: Boolean
    open var center: Vector3
    open var render: (renderer: WebGLRenderer) -> Unit
    open var handleClick: (event: MouseEvent) -> Boolean
    open var setLabels: (labelX: String, labelY: String, labelZ: String) -> Unit
    open var setLabelStyle: (font: String, color: String, radius: Number) -> Unit
    open var update: (delta: Number) -> Unit
    open var dispose: () -> Unit
}