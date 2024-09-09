@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
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

external interface `T$88` {
    @nativeGetter
    operator fun get(id: String): Array<Number>?
    @nativeSetter
    operator fun set(id: String, value: Array<Number>)
}

external open class CameraHelper(camera: Camera) : LineSegments__0 {
    override var override: Any
    override val type: String /* String | "CameraHelper" */
    open var camera: Camera
    open var pointMap: `T$88`
    override var matrix: Matrix4
    override var matrixAutoUpdate: Boolean
    open fun setColors(frustum: Color, cone: Color, up: Color, target: Color, cross: Color): CameraHelper /* this */
    open fun update()
    open fun dispose()
}