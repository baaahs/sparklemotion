@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package three.addons

import three.*

open external class TextureHelper(texture: Texture, width: Number = definedExternally, height: Number = definedExternally, depth: Number = definedExternally) : Mesh<BufferGeometry<NormalOrGLBufferAttributes>, Material> {
    open var texture: Texture
    override var type: String /* "TextureHelper" */
    open fun dispose()
}