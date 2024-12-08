@file:JsModule("@mui/base/Unstable_NumberInput")

package external.react

import react.ElementType
import react.PropsWithChildren
import react.PropsWithRef
import react.ReactNode
import react.dom.html.InputHTMLAttributes
import web.html.HTMLInputElement

@JsName("Unstable_NumberInput")
external val BaseNumberInput : ElementType<BaseNumberInputProps>

external interface BaseNumberInputProps : react.Props, PropsWithRef<dynamic>, PropsWithChildren {
    var error: Boolean?
    var helperText: String?
    var placeholder: InputHTMLAttributes<HTMLInputElement>?
    var label: ReactNode?
    var startAdornment: ReactNode?
    var endAdornment: ReactNode?
    var slots: NumberInputSlots?
    var slotProps: NumberInputSlotProps?
    var value: Number?
    var disabled: Boolean?
    var onChange: ((value: Number?) -> Unit)?
    var onInputChange: ((value: Number?) -> Unit)?
}

external interface NumberInputSlots {
    var decrementButton: ElementType<*>?
    var incrementButton: ElementType<*>?
    var input: ElementType<*>?
    var root: ElementType<*>?
}

external interface NumberInputSlotProps {
    var input: dynamic
    var root: dynamic
    var incrementButton: dynamic
    var decrementButton: dynamic
}
