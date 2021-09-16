@file:JsModule("react-mosaic-component")
@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS", "EXTERNAL_DELEGATION")

package external.mosaic

import react.RClass
import react.RProps
import react.ReactElement

external interface MosaicWindowProps<T>: RProps {
    var title: String
    var path: Array<String /* 'first' | 'second' */>
    var className: String?
        get() = definedExternally
        set(value) = definedExternally
    var toolbarControls: Any?
        get() = definedExternally
        set(value) = definedExternally
    var additionalControls: Any?
        get() = definedExternally
        set(value) = definedExternally
    var additionalControlButtonText: String?
        get() = definedExternally
        set(value) = definedExternally
    var draggable: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var createNode: CreateNode<T>?
        get() = definedExternally
        set(value) = definedExternally
    var renderPreview: ((props: MosaicWindowProps<T>) -> ReactElement)?
        get() = definedExternally
        set(value) = definedExternally
    var renderToolbar: ((props: MosaicWindowProps<T>, draggable: Boolean?) -> ReactElement)?
        get() = definedExternally
        set(value) = definedExternally
    var onDragStart: (() -> Unit)?
        get() = definedExternally
        set(value) = definedExternally
    var onDragEnd: ((type: String /* 'drop' | 'reset' */) -> Unit)?
        get() = definedExternally
        set(value) = definedExternally
}

//abstract external class MosaicWindow<T> : Component<MosaicWindowProps<T>, RState>

external val MosaicWindow: RClass<MosaicWindowProps<*>>