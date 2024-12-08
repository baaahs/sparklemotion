package three.addons

import three.ShaderMaterial
import three.Vector2

open external class DotScreenPass(center: Vector2 = definedExternally, angle: Number = definedExternally, scale: Number = definedExternally) : Pass {
    open var uniforms: Any?
    open var material: ShaderMaterial
    open var fsQuad: FullScreenQuad
}