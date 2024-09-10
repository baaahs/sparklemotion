package three.js

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

open external class Camera : Object3D__0 {
    open val isCamera: Boolean
    open var override: Any
    override val type: String /* String | "Camera" */
    override var layers: Layers
    open var matrixWorldInverse: Matrix4
    open var projectionMatrix: Matrix4
    open var projectionMatrixInverse: Matrix4
    open var coordinateSystem: Any
    open var viewport: Vector4
    override fun getWorldDirection(target: Vector3): Vector3
}