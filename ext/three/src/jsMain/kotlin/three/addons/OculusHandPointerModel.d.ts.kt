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

external open class OculusHandPointerModel(hand: Object3D__0, controller: Object3D__0) : Object3D__0 {
    open var hand: Object3D__0
    open var controller: Object3D__0
    open var motionController: XRHandMeshModel?
    open var envMap: Texture?
    open var mesh: Mesh__0?
    open var pointerGeometry: BufferGeometry__0?
    open var pointerMesh: Mesh__2<BufferGeometry__0, MeshBasicMaterial>?
    open var pointerObject: Object3D__0?
    open var pinched: Boolean
    open var attached: Boolean
    open var cursorObject: Mesh__2<SphereGeometry, MeshBasicMaterial>?
    open var raycaster: Raycaster
    override var visible: Boolean
    open var xrInputSource: XRInputSource
    open fun _drawVerticesRing(vertices: Array<Number>, baseVector: Vector3, ringIndex: Number)
    open fun _updatePointerVertices(rearRadius: Number)
    open fun createPointer()
    open fun _updateRaycaster()
    open fun _updatePointer()
    override fun updateMatrixWorld(force: Boolean)
    open fun isPinched(): Boolean
    open fun setAttached(attached: Boolean)
    open fun isAttached(): Boolean
    open fun intersectObject(obj: Object3D__0, recursive: Boolean = definedExternally): dynamic /* Array<Intersection__0> | Unit */
    open fun intersectObjects(objects: Array<Object3D__0>, recursive: Boolean = definedExternally): dynamic /* Array<Intersection__0> | Unit */
    open fun checkIntersections(objects: Array<Object3D__0>, recursive: Boolean = definedExternally)
    open fun setCursor(distance: Number)
}