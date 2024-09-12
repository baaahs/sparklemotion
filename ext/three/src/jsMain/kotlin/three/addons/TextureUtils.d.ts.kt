@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package three.addons

import three.Texture
import three.WebGLRenderer

external fun decompress(texture: Texture, maxTextureSize: Number = definedExternally, renderer: WebGLRenderer = definedExternally): Texture