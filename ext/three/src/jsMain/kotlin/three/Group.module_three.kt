@file:JsModule("three")
@file:JsNonModule
package three

open external class Group/*<TEventMap : Object3DEventMap>*/ : Object3D/*<Object3DEventMap>*/ {
    open val isGroup: Boolean
    open var override: Any
    override val type: String /* String | "Group" */
}

//typealias Object3D/*<Object3DEventMap>*/ = Group<Object3DEventMap>