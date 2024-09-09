@file:JsModule("three")
@file:JsNonModule
@file:Suppress("PackageDirectoryMismatch")
package three.js

import org.w3c.dom.ImageData

open external class DepthTexture(width: Number, height: Number, type: TextureDataType = definedExternally, mapping: Mapping = definedExternally, wrapS: Wrapping = definedExternally, wrapT: Wrapping = definedExternally, magFilter: TextureFilter = definedExternally, minFilter: TextureFilter = definedExternally, anisotropy: Number = definedExternally) : Texture {
    override var image: ImageData
    override var flipY: Boolean
    override var generateMipmaps: Boolean
}