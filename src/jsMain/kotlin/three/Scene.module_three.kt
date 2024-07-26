@file:JsModule("three")
@file:JsNonModule
@file:Suppress("PackageDirectoryMismatch")
package three.js

open external class Scene : Object3D {
    override var type: String /* 'Scene' */
    open var fog: IFog?
    open var overrideMaterial: Material?
    open var autoUpdate: Boolean
    open var background: dynamic /* Color? | Texture? | WebGLCubeRenderTarget? */
    open var environment: Texture?
    open var isScene: Boolean
    open fun toJSON(meta: Any = definedExternally): Any
    override fun toJSON(meta: `T$0`): Any
    open fun dispose()
}