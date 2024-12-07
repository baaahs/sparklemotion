package three.addons

import org.khronos.webgl.ArrayBuffer
import three.CompressedTextureLoader
import three.CompressedTextureMipmap
import three.LoadingManager

external interface KTX {
    var mipmaps: Array<CompressedTextureMipmap>
    var width: Number
    var height: Number
    var format: Any
    var mipmapCount: Number
    var isCubemap: Boolean
}

open external class KTXLoader(manager: LoadingManager = definedExternally) : CompressedTextureLoader {
    open fun parse(buffer: ArrayBuffer, loadMipmaps: Boolean): KTX
}