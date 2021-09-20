@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS", "EXTERNAL_DELEGATION")

package external.mosaic

import react.Component
import react.Props
import react.State


external interface MosaicZeroStateProps<T>: Props {
    var createNode: CreateNode<T>?
        get() = definedExternally
        set(value) = definedExternally
}

abstract external class MosaicZeroState<T> : Component<MosaicZeroStateProps<T>, State> {
    open var context: MosaicContext<T>
    open var replace: Any

    companion object {
        var contextType: Any
    }
}