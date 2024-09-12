@file:JsModule("three")
@file:JsNonModule
package three

open external class CubeTexture(images: Array<Any> = definedExternally, mapping: Any = definedExternally, wrapS: Any = definedExternally, wrapT: Any = definedExternally, magFilter: Any = definedExternally, minFilter: Any = definedExternally, format: Any = definedExternally, type: Any = definedExternally, anisotropy: Number = definedExternally, colorSpace: Any = definedExternally) : Texture {
    open val isCubeTexture: Boolean
    override var mapping: Any
    override var flipY: Boolean
}