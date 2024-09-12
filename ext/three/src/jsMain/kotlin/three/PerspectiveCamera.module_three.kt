@file:JsModule("three")
@file:JsNonModule
package three

external interface `T$30` {
    var enabled: Boolean
    var fullWidth: Number
    var fullHeight: Number
    var offsetX: Number
    var offsetY: Number
    var width: Number
    var height: Number
}

external interface PerspectiveCameraJSONObject : Object3DJSONObject {
    var fov: Number
    var zoom: Number
    var near: Number
    var far: Number
    var focus: Number
    var aspect: Number
    var view: `T$30`?
        get() = definedExternally
        set(value) = definedExternally
    var filmGauge: Number
    var filmOffset: Number
}

external interface PerspectiveCameraJSON : Object3DJSON {
//    override var `object`: PerspectiveCameraJSONObject
}

open external class PerspectiveCamera(fov: Number = definedExternally, aspect: Number = definedExternally, near: Number = definedExternally, far: Number = definedExternally) : Camera {
    open val isPerspectiveCamera: Boolean
    override var override: Any
    override val type: String /* String | "PerspectiveCamera" */
    open var zoom: Number
    open var fov: Number
    open var aspect: Number
    open var near: Number
    open var far: Number
    open var focus: Number
    open var view: `T$30`?
    open var filmGauge: Number
    open var filmOffset: Number
    open fun getFocalLength(): Number
    open fun setFocalLength(focalLength: Number)
    open fun getEffectiveFOV(): Number
    open fun getFilmWidth(): Number
    open fun getFilmHeight(): Number
    open fun getViewBounds(distance: Number, minTarget: Vector2, maxTarget: Vector2)
    open fun getViewSize(distance: Number, target: Vector2): Vector2
    open fun setViewOffset(fullWidth: Number, fullHeight: Number, x: Number, y: Number, width: Number, height: Number)
    open fun clearViewOffset()
    open fun updateProjectionMatrix()
    open fun setLens(focalLength: Number, frameHeight: Number = definedExternally)
    override fun toJSON(meta: JSONMeta): PerspectiveCameraJSON
}