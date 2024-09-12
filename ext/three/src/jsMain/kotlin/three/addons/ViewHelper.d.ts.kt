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

external open class ViewHelper(camera: Camera, domElement: HTMLElement) : Object3D__0 {
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