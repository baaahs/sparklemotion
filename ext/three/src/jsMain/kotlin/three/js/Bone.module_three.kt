package three.js

open external class Bone<TEventMap : Object3DEventMap> : Object3D/*<Object3DEventMap>*/ {
    open val isBone: Boolean
    open var override: Any
    override val type: String /* String | "Bone" */
}

typealias Bone__0 = Bone<Object3DEventMap>