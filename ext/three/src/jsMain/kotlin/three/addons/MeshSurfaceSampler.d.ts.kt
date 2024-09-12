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

external open class MeshSurfaceSampler(mesh: Mesh__0) {
    open var distribution: Float32Array?
    open var geometry: BufferGeometry__0
    open var positionAttribute: Float32Array
    open var weightAttribute: String?
    open fun binarySearch(x: Number): Number
    open fun build(): MeshSurfaceSampler /* this */
    open fun sample(targetPosition: Vector3, targetNormal: Vector3 = definedExternally, targetColor: Color = definedExternally, targetUV: Vector2 = definedExternally): MeshSurfaceSampler /* this */
    open fun sampleFace(faceIndex: Number, targetPosition: Vector3, targetNormal: Vector3 = definedExternally, targetColor: Color = definedExternally, targetUV: Vector2 = definedExternally): MeshSurfaceSampler /* this */
    open fun setWeightAttribute(name: String?): MeshSurfaceSampler /* this */
}