package three.addons

import three.Matrix4
import three.Vector3

external interface CSMFrustumVerticies {
    var near: Array<Vector3>
    var far: Array<Vector3>
}

external interface CSMFrustumParameters {
    var projectionMatrix: Matrix4?
        get() = definedExternally
        set(value) = definedExternally
    var maxFar: Number?
        get() = definedExternally
        set(value) = definedExternally
}

open external class CSMFrustum(data: CSMFrustumParameters = definedExternally) {
    open var vertices: CSMFrustumVerticies
    open fun setFromProjectionMatrix(projectionMatrix: Matrix4, maxFar: Number): CSMFrustumVerticies
    open fun split(breaks: Array<Number>, target: Array<CSMFrustum>)
    open fun toSpace(cameraMatrix: Matrix4, target: CSMFrustum)
}