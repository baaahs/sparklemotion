package three.addons

import three.*

open external class TextureHelper(texture: Texture, width: Number = definedExternally, height: Number = definedExternally, depth: Number = definedExternally) : Mesh<BufferGeometry<NormalOrGLBufferAttributes>, Material> {
    open var texture: Texture
    override var type: String /* "TextureHelper" */
    open fun dispose()
}