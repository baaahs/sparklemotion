package three_ext

import com.danielgergely.kgl.Texture
import three.js.WebGLRenderTarget
import three.js.WebGLRenderer

fun WebGLRenderer.getTexture(renderTarget: WebGLRenderTarget): Texture {
    return properties.get(renderTarget.texture).asDynamic().__webglTexture as Texture
}