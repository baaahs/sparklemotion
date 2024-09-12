@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package three.addons

import org.khronos.webgl.Float32Array
import three.*

open external class MeshSurfaceSampler(mesh: Mesh<*, *>) {
    open var distribution: Float32Array?
    open var geometry: BufferGeometry<NormalOrGLBufferAttributes>
    open var positionAttribute: Float32Array
    open var weightAttribute: String?
    open fun binarySearch(x: Number): Number
    open fun build(): MeshSurfaceSampler /* this */
    open fun sample(targetPosition: Vector3, targetNormal: Vector3 = definedExternally, targetColor: Color = definedExternally, targetUV: Vector2 = definedExternally): MeshSurfaceSampler /* this */
    open fun sampleFace(faceIndex: Number, targetPosition: Vector3, targetNormal: Vector3 = definedExternally, targetColor: Color = definedExternally, targetUV: Vector2 = definedExternally): MeshSurfaceSampler /* this */
    open fun setWeightAttribute(name: String?): MeshSurfaceSampler /* this */
}