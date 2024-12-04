package baaahs.app.ui.model

import baaahs.app.ui.appContext
import baaahs.mapper.styleIf
import baaahs.ui.and
import baaahs.ui.asColor
import baaahs.ui.selector
import baaahs.ui.unaryMinus
import external.react.BaseNumberInput
import external.react.BaseNumberInputProps
import js.objects.Object
import js.objects.jso
import kotlinx.css.*
import kotlinx.css.properties.scale
import kotlinx.css.properties.transform
import kotlinx.css.properties.translate
import mui.material.FormControl
import mui.material.FormLabel
import mui.material.Typography
import mui.material.styles.Theme
import mui.material.styles.useTheme
import react.*
import styled.StyleSheet
import styled.styledInput
import web.events.Event
import web.events.EventHandler
import web.html.HTMLDivElement
import web.html.HTMLElement
import web.html.HTMLInputElement

external interface NumberInputProps : BaseNumberInputProps

val NumberInput = forwardRef<dynamic, NumberInputProps> { props, ref ->
    val fragRef = useRef<Any>()

    BaseNumberInput {
        attrs.slots = jso {
            root = StyledInputRoot
            input = StyledInputElement
        }
        attrs.slotProps = jso {
            root = jso {
                error = props.error
            }
            incrementButton = jso {
                children = "▴"
            }
            decrementButton = jso {
                children = "▾"
            }
        }
        Object.assign(attrs, props)
        Object.getOwnPropertyNames(props).forEach { key ->
            if (key != "children") {
                attrs.asDynamic()[key] = props.asDynamic()[key]
            }
        }
        attrs.ref = ref
    }
}

val StyledInputRoot = fc<NumberInputProps> { props ->
    val appContext = useContext(appContext)
    val styles = appContext.allStyles.numberInput
    val isError = props.asDynamic().ownerState.error == true
    val helperText = props.asDynamic().ownerState.helperText as? String
    val formControlDiv = useRef<HTMLDivElement>()

    useEffectWithCleanup {
        val div = formControlDiv.current
        if (div != null) {
            div.onpointerup = EventHandler { e: Event ->
                (div.querySelector("input") as? HTMLElement)?.focus()
            }

            onCleanup {
                div.onpointerup = null
            }
        }
    }
    FormControl {
        attrs.ref = formControlDiv

        Object.getOwnPropertyNames(props).forEach { key ->
            if (key != "children") {
                attrs.asDynamic()[key] = props.asDynamic()[key]
            }
        }

        props.label?.let {
            FormLabel {
                attrs.className = -styles.inputLabel
                +it
            }
        }
        attrs.className = (attrs.className?.let { it and styles.root } ?: -styles.root) and
                styleIf(isError == true, styles.error)

        props.children?.let { child(it) }

        if (isError == true) {
            helperText?.let {
                child(
                    buildElement {
                        Typography {
                            attrs.className = -styles.errorMessage
                            +it
                        }
                    }
                )
            }
        }
    }
}

class NumberInputStyles(val theme: Theme) : StyleSheet("app-ui-numberinput", isStatic = true) {
    val root by css {
        position = Position.relative
        display = Display.inlineGrid
        gridTemplateColumns = GridTemplateColumns(1.fr, 1.em, 19.px)
        gridTemplateRows = GridTemplateRows(1.fr, 1.fr)
        borderRadius = 8.px
        paddingTop = 1.1.em
        marginLeft = .25.em
        marginRight = 1.em
        paddingBottom = 1.em
        alignContent = Align.center
//        backgroundColor = Color.transparent

        button {
            backgroundColor = Color.transparent
            color = theme.palette.text.primary.asColor().withAlpha(.6)
            border = Border.none
            paddingRight = 2.px
        }
        "button:nth-of-type(1)" {
            gridColumn = GridColumn("3")
            gridRow = GridRow("2")
        }
        "button:nth-of-type(1)::after" {
            content = QuotedString("⬇")
        }
        "button:nth-of-type(2)" {
            gridColumn = GridColumn("3")
            gridRow = GridRow("1")
        }
        "button:nth-of-type(2)::after" {
            content = QuotedString("⬆")
        }
        ".MuiInputAdornment-root" {
            gridColumn = GridColumn("2")
            gridRow = GridRow("1 / 3")
            marginTop = 1.3.em
            marginLeft = .3.em
            marginRight = .5.em
        }
        input {
            gridRow = GridRow("1 / 3")
        }
    }

    val helperText by css {}

    val error by css {
        input {
            borderColor = Color.red
        }

        child(selector(::helperText)) {
            color = Color.red
        }
    }

    val errorMessage by css {
        position = Position.absolute
        bottom = 0.em
        fontSize = .7.em
        color = Color.red
        userSelect = UserSelect.none
    }

    val inputLabel by css {
        position = Position.absolute
        top = 0.px
        left = 0.px
        transform {
            translate(0.px, (-1.5).px)
            scale(0.75)
        }
        whiteSpace = WhiteSpace.nowrap
        userSelect = UserSelect.none
    }
}

val StyledInputElement = fc<NumberInputProps> { props ->
    val theme = useTheme<Theme>()
    val inputRef = useRef<HTMLInputElement>()

    styledInput {
        ref = inputRef
        css.apply {
            backgroundColor = Color.transparent
            color = theme.palette.text.primary.asDynamic()
            textAlign = TextAlign.right
            padding = Padding(4.px)
            border = Border(1.px, BorderStyle.solid, Color.gray)
            borderRadius = 4.px

            gridColumn = GridColumn("1")
            gridRow = GridRow("1")
        }
        Object.getOwnPropertyNames(props).forEach { key ->
            attrs[key] = props.asDynamic()[key]
        }
    }
}