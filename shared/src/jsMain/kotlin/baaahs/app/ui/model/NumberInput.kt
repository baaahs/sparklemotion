@file:OptIn(ExperimentalEncodingApi::class)

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
import kotlinx.css.properties.BoxShadow
import kotlinx.css.properties.BoxShadows
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
import web.cssom.PropertyName.Companion.border
import web.cssom.PropertyName.Companion.borderColor
import web.cssom.PropertyName.Companion.margin
import web.events.Event
import web.events.EventHandler
import web.html.HTMLDivElement
import web.html.HTMLElement
import web.html.HTMLInputElement
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

external interface NumberInputProps : BaseNumberInputProps {
    var autoFocusOnTouch: Boolean?
}

val NumberInput = forwardRef<dynamic, NumberInputProps> { props, ref ->
    BaseNumberInput {
        attrs.slots = jso {
            root = StyledInputRoot
            input = StyledInputElement
        }
        attrs.slotProps = jso {
            root = jso {
                error = props.error
            }
        }
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

    if (props.autoFocusOnTouch == true) {
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
        gridTemplateColumns = GridTemplateColumns(1.fr, LinearDimension.auto, 1.fr)
        gridTemplateRows = GridTemplateRows(1.fr, 1.fr)
        borderRadius = 8.px
        paddingTop = 1.1.em
        marginLeft = .25.em
        marginRight = 1.em
        paddingBottom = 1.em
        alignContent = Align.center
//        backgroundColor = Color.transparent

        val buttonColor = theme.palette.text.primary.asColor().withAlpha(.6)

        button {
            backgroundColor = Color.transparent
            color = buttonColor
            border = Border.none
            paddingRight = 2.px
        }
        "button:disabled" {
            opacity = .3
        }
        // Down arrow.
        "button:nth-of-type(1)" {
            gridColumn = GridColumn("3")
            gridRow = GridRow("2")
            backgroundImage = Image("url('data:image/svg+xml;base64,${downArrowSvg(buttonColor)}')")
            marginLeft = 6.px
            display = Display.inlineBlock
            backgroundSize = "contain"
            backgroundRepeat = BackgroundRepeat.noRepeat
        }
        // Up arrow.
        "button:nth-of-type(2)" {
            gridColumn = GridColumn("3")
            gridRow = GridRow("1")
            backgroundImage = Image("url('data:image/svg+xml;base64,${upArrowSvg(buttonColor)}')")
            marginLeft = 6.px
            display = Display.inlineBlock
            backgroundSize = "contain"
            backgroundRepeat = BackgroundRepeat.noRepeat
        }
        ".MuiInputAdornment-root" {
            gridColumn = GridColumn("2")
            gridRow = GridRow("1 / 3")
            marginTop = .8.em
            marginLeft = .3.em
            marginRight = .5.em
        }
        input {
            gridRow = GridRow("1 / 3")
        }
        "input:disabled" {
            borderColor = theme.palette.text.primary.asColor().withAlpha(.1)
        }
    }

    val newRoot by css {
        position = Position.relative
        display = Display.inlineGrid
        gridTemplateColumns = GridTemplateColumns(1.fr, LinearDimension.auto, 1.fr)
        gridTemplateRows = GridTemplateRows(1.fr, 1.fr)
        borderRadius = 8.px
        paddingTop = 1.1.em
//        marginLeft = .25.em
//        marginRight = 1.em
        paddingBottom = 1.em
        alignContent = Align.center
//        backgroundColor = Color.transparent

        val buttonColor = theme.palette.text.primary.asColor().withAlpha(.6)

        button {
            backgroundColor = Color.transparent
            color = buttonColor
            border = Border.none
            paddingRight = 2.px
            width = 1.em
            height = 1.em
        }
        "button:disabled" {
            opacity = .3
        }
        // Down arrow.
        ".decrementButton" {
            gridColumn = GridColumn("3")
            gridRow = GridRow("2")
            backgroundImage = Image("url('data:image/svg+xml;base64,${downArrowSvg(buttonColor)}')")
            marginLeft = 6.px
            display = Display.inlineBlock
            backgroundSize = "contain"
            backgroundRepeat = BackgroundRepeat.noRepeat
        }

        // Up arrow.
        ".incrementButton" {
            gridColumn = GridColumn("3")
            gridRow = GridRow("1")
            backgroundImage = Image("url('data:image/svg+xml;base64,${upArrowSvg(buttonColor)}')")
            marginLeft = 6.px
            display = Display.inlineBlock
            backgroundSize = "contain"
            backgroundRepeat = BackgroundRepeat.noRepeat
        }


        ".MuiInputAdornment-root" {
            gridColumn = GridColumn("2")
            gridRow = GridRow("1 / 3")

            p {
                fontSize = .8.em
            }
        }

        input {
            borderRadius = 4.px
            border = Border(1.px, BorderStyle.solid, Color.white.withAlpha(.8))
            gridRow = GridRow("1 / 3")
            boxSizing = BoxSizing.borderBox
            margin = Margin(4.px)
            width = 5.em
            minHeight = 2.em
            paddingRight = .5.em
            paddingTop = 4.px
            paddingBottom = 4.px
            textAlign = TextAlign.right
        }
        "input:focus-within" {
            outlineOffset = 0.px
            borderColor = theme.palette.primary.dark.asColor().saturate(50)
            borderWidth = 2.px
            boxShadow += BoxShadow(theme.palette.text.primary.asColor(), spreadRadius = 2.px)
        }
        "input:disabled" {
            borderColor = theme.palette.text.primary.asColor().withAlpha(.1)
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
        attrs.disabled = props.disabled == true
        for (key in Object.getOwnPropertyNames(props)) {
            if (key in noCopyKeys) continue
            attrs[key] = props.asDynamic()[key]
        }
    }
}

private fun upArrowSvg(color: Color) = Base64.encode(
    /** language=svg */
    """
        <svg height="1024" width="640" xmlns="http://www.w3.org/2000/svg">
            <path fill="$color" d="M320 192L0 576h192v256h256V576h192L320 192z" />
        </svg>
    """.trimIndent().encodeToByteArray()
)
private fun downArrowSvg(color: Color) = Base64.encode(
    /** language=svg */
    """
        <svg height="1024" width="640" xmlns="http://www.w3.org/2000/svg">
            <path fill="$color" d="M320 192L0 576h192v256h256V576h192L320 192z" transform="rotate(180, 320, 512)" />
        </svg>
    """.trimIndent().encodeToByteArray()
)

private val noCopyKeys = setOf("ownerState", "helperText", "shrink")