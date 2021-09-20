@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS", "EXTERNAL_DELEGATION")

package external.mosaic

import react.Component
import react.RProps
import react.RState


external interface MosaicZeroStateProps<T>: RProps {
    var createNode: CreateNode<T>?
        get() = definedExternally
        set(value) = definedExternally
}

abstract external class MosaicZeroState<T> : Component<MosaicZeroStateProps<T>, RState> {
    open var context: MosaicContext<T>
    open var replace: Any

    companion object {
        var contextType: Any
    }
}