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

external interface `T$4` {
    var isInterleavedBufferAttribute: Boolean
    var itemSize: Number
    var data: String
    var offset: Number
    var normalized: Boolean
}

open external class InterleavedBufferAttribute(interleavedBuffer: InterleavedBuffer, itemSize: Number, offset: Number, normalized: Boolean = definedExternally) {
    open var name: String
    open var data: InterleavedBuffer
    open var itemSize: Number
    open var offset: Number
    open var normalized: Boolean
    open val isInterleavedBufferAttribute: Boolean
    open fun applyMatrix4(m: Matrix4): InterleavedBufferAttribute /* this */
    open fun applyNormalMatrix(m: Matrix3): InterleavedBufferAttribute /* this */
    open fun transformDirection(m: Matrix4): InterleavedBufferAttribute /* this */
    open fun getComponent(index: Number, component: Number): Number
    open fun setComponent(index: Number, component: Number, value: Number): InterleavedBufferAttribute /* this */
    open fun getX(index: Number): Number
    open fun setX(index: Number, x: Number): InterleavedBufferAttribute /* this */
    open fun getY(index: Number): Number
    open fun setY(index: Number, y: Number): InterleavedBufferAttribute /* this */
    open fun getZ(index: Number): Number
    open fun setZ(index: Number, z: Number): InterleavedBufferAttribute /* this */
    open fun getW(index: Number): Number
    open fun setW(index: Number, z: Number): InterleavedBufferAttribute /* this */
    open fun setXY(index: Number, x: Number, y: Number): InterleavedBufferAttribute /* this */
    open fun setXYZ(index: Number, x: Number, y: Number, z: Number): InterleavedBufferAttribute /* this */
    open fun setXYZW(index: Number, x: Number, y: Number, z: Number, w: Number): InterleavedBufferAttribute /* this */
    open fun clone(data: Any = definedExternally): BufferAttribute
    open fun toJSON(data: Any = definedExternally): `T$4`
}