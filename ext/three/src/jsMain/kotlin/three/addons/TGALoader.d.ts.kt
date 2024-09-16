@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package three.addons

import org.khronos.webgl.ArrayBuffer
import three.DataTexture
import three.DataTextureLoader
import three.LoadingManager

open external class TGALoader(manager: LoadingManager = definedExternally) : DataTextureLoader {
    open fun parse(data: ArrayBuffer): DataTexture
}