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