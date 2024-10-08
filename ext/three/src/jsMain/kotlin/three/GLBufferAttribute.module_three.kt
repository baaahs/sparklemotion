@file:JsModule("three")
@file:JsNonModule
package three

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

open external class GLBufferAttribute(buffer: WebGLBuffer, type: GLenum, itemSize: Number, elementSize: Number /* 1 | 2 | 4 */, count: Number) {
    open val isGLBufferAttribute: Boolean
    open var name: String
    open var buffer: WebGLBuffer
    open var type: GLenum
    open var itemSize: Number
    open var elementSize: Number /* 1 | 2 | 4 */
    open var count: Number
    open var version: Number
    open fun setBuffer(buffer: WebGLBuffer): GLBufferAttribute /* this */
    open fun setType(type: GLenum, elementSize: Number /* 1 | 2 | 4 */): GLBufferAttribute /* this */
    open fun setItemSize(itemSize: Number): GLBufferAttribute /* this */
    open fun setCount(count: Number): GLBufferAttribute /* this */
}