package three.js

open external class Group<TEventMap : Object3DEventMap> : Object3D<TEventMap> {
    open val isGroup: Boolean
    open var override: Any
    override val type: String /* String | "Group" */
}

typealias Group__0 = Group<Object3DEventMap>