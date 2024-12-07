package three.addons

import three.Clock
import three.WebGLRenderTarget
import three.WebGLRenderer

open external class EffectComposer(renderer: WebGLRenderer, renderTarget: WebGLRenderTarget<*> = definedExternally) {
    open var renderer: WebGLRenderer
    open var renderTarget1: WebGLRenderTarget<*>
    open var renderTarget2: WebGLRenderTarget<*>
    open var writeBuffer: WebGLRenderTarget<*>
    open var readBuffer: WebGLRenderTarget<*>
    open var passes: Array<Pass>
    open var copyPass: ShaderPass
    open var clock: Clock
    open var renderToScreen: Boolean
    open fun swapBuffers()
    open fun addPass(pass: Pass)
    open fun insertPass(pass: Pass, index: Number)
    open fun removePass(pass: Pass)
    open fun isLastEnabledPass(passIndex: Number): Boolean
    open fun render(deltaTime: Number = definedExternally)
    open fun reset(renderTarget: WebGLRenderTarget<*> = definedExternally)
    open fun setSize(width: Number, height: Number)
    open fun setPixelRatio(pixelRatio: Number)
    open fun dispose()
}