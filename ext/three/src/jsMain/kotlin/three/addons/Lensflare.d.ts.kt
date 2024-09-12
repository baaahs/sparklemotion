@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package three.addons

import three.*

open external class LensflareElement(texture: Texture, size: Number = definedExternally, distance: Number = definedExternally, color: Color = definedExternally) {
    open var texture: Texture
    open var size: Number
    open var distance: Number
    open var color: Color
}

open external class Lensflare : Mesh<BufferGeometry<NormalOrGLBufferAttributes>, Material> {
    open val isLensflare: Boolean
    open fun addElement(element: LensflareElement)
    open fun dispose()
}