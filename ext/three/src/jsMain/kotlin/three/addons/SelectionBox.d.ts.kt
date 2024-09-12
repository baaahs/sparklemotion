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

external open class SelectionBox(camera: Camera, scene: Scene, deep: Number = definedExternally) {
    open var camera: Camera
    open var collection: Array<Mesh__0>
    open var deep: Number
    open var endPoint: Vector3
    open var scene: Scene
    open var startPoint: Vector3
    open var instances: Record<String, Array<Number>>
    open fun select(startPoint: Vector3 = definedExternally, endPoint: Vector3 = definedExternally): Array<Mesh__0>
    open fun updateFrustum(startPoint: Vector3, endPoint: Vector3)
    open fun searchChildInFrustum(frustum: Frustum, obj: Object3D__0)
}