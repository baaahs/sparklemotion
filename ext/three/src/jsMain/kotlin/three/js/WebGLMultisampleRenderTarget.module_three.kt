@file:JsModule("three")
@file:JsNonModule
@file:Suppress("PackageDirectoryMismatch")
package three.js

open external class WebGLMultisampleRenderTarget(width: Number, height: Number, options: WebGLRenderTargetOptions = definedExternally) : WebGLRenderTarget {
    open var isWebGLMultisampleRenderTarget: Boolean
    open var samples: Number
}