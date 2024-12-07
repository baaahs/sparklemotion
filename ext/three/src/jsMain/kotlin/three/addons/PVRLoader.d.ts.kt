package three.addons

import org.khronos.webgl.ArrayBuffer
import three.CompressedTextureLoader
import three.CompressedTextureMipmap
import three.LoadingManager

external interface PVR {
    var mipmaps: Array<CompressedTextureMipmap>
    var width: Number
    var height: Number
    var format: Any
    var mipmapCount: Number
    var isCubemap: Boolean
}

open external class PVRLoader(manager: LoadingManager = definedExternally) : CompressedTextureLoader {
    open fun parse(buffer: ArrayBuffer, loadMipmaps: Boolean): PVR
}