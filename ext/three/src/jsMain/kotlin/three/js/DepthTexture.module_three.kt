package three.js

open external class DepthTexture(width: Number, height: Number, type: Any = definedExternally, mapping: Any = definedExternally, wrapS: Any = definedExternally, wrapT: Any = definedExternally, magFilter: Any = definedExternally, minFilter: Any = definedExternally, anisotropy: Number = definedExternally, format: Any = definedExternally) : Texture {
    open val isDepthTexture: Boolean
    override var flipY: Boolean
    override var magFilter: Any
    override var minFilter: Any
    override var generateMipmaps: Boolean
    override var format: Any
    override var type: Any
    open var compareFunction: Any?
}