@file:Suppress("INTERFACE_WITH_SUPERCLASS", "EXTERNAL_DELEGATION")

package external.mosaic

//external fun createDefaultToolbarButton(title: String, className: String, onClick: (event: React.MouseEvent<Any>) -> Any, text: String = definedExternally): React.ReactElement<Any>

external interface MosaicButtonProps {
    var onClick: (() -> Unit)?
        get() = definedExternally
        set(value) = definedExternally
}