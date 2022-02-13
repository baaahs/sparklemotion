package baaahs.app.ui.model

import baaahs.ui.unaryMinus
import baaahs.ui.value
import kotlinx.css.*
import kotlinx.css.properties.LineHeight
import kotlinx.js.jso
import mui.material.*
import mui.material.styles.Theme
import org.w3c.dom.events.Event
import react.RBuilder
import react.RElementBuilder
import react.buildElement
import react.dom.html.InputType
import react.dom.onChange
import styled.StyleSheet
import baaahs.app.ui.controls.Styles as ControlStyles

class ModelEditorStyles(val theme: Theme) : StyleSheet("app-ui-model-editor", isStatic = true) {
    val editorPanes by css {
        display = Display.grid
        gridTemplateColumns = GridTemplateColumns(
            GridAutoRows.minMax(15.em, 15.pct),
            GridAutoRows.auto,
            GridAutoRows.minMax(20.em, 20.pct)
        )
        gridTemplateRows = GridTemplateRows(100.pct)
        height = 100.pct
    }

    val navigatorPane by css {
        display = Display.grid
        gridTemplateRows = GridTemplateRows(GridAutoRows.minContent, GridAutoRows.auto)
        height = 100.pct
    }
    val navigatorPaneContent by css {
        minHeight = 0.px
        overflow = Overflow.scroll
    }

    val entityList by css {
        child("li") {
            display = Display.flex
            flexDirection = FlexDirection.column
            alignItems = Align.inherit
        }
    }

    val visualizerPane by css {
        position = Position.relative
        height = 100.pct
    }

    val visualizer by css {
        position = Position.absolute
        top = 0.px
        left = 0.px
        bottom = 0.px
        right = 0.px
    }

    val visualizerToolbar by css {
        position = Position.absolute
        display = Display.flex
        flexDirection = FlexDirection.column
        top = 10.px
        left = 10.px
        backgroundColor = theme.palette.background.paper.withAlpha(.8)
    }

    val propertiesPane by css {
        display = Display.grid
        gridTemplateRows = GridTemplateRows(GridAutoRows.minContent, GridAutoRows.auto)
        height = 100.pct
    }
    val propertiesPaneContent by css {
        minHeight = 0.px
        overflow = Overflow.scroll
    }

    val propertiesEditSection by css {
        whiteSpace = WhiteSpace.nowrap
//        color = theme.palette.primary.contrastText
//        backgroundColor = theme.palette.primary.main

        input {
//            color = theme.palette.primary.contrastText
            fontSize = .8.em
        }

        header {
            backgroundColor = Color.inherit
            fontSize = LinearDimension.inherit
            fontWeight = FontWeight.inherit
            lineHeight = LineHeight.inherit
            padding = ".5em 0 0 0"
        }
    }

    val gridSizeInput by css(propertiesEditSection) {
        width = 5.em
        textAlign = TextAlign.right
    }

    val transformEditSection by css(propertiesEditSection) {
        input {
            width = 5.em
            textAlign = TextAlign.right
        }
    }

    val partialUnderline by css {
        before {
            right = LinearDimension.inherit
            width = 100.pct - 1.5.em
        }
    }

    fun <T : Number?> RBuilder.numberTextField(
        label: String,
        value: T,
        adornment: (RElementBuilder<InputAdornmentProps>.() -> Unit)? = null,
        placeholder: String? = null,
        onChange: (T) -> Unit
    ) {
        val cachedOnChange = onChange.asDynamic().cachedOnClick ?: run {
            { event: Event ->
                val numericValue = event.currentTarget.value
                    .ifBlank { null }
                    ?.toDouble() as T
                onChange(numericValue)
            }
                .also { onChange.asDynamic().cachedOnClick = it }
        }

        TextField {
            attrs.type = InputType.number
            attrs.size = Size.small
            attrs.placeholder = placeholder
            attrs.inputProps = jso<InputProps> {
                classes = jso { this.underline = -partialUnderline }
                if (adornment != null) {
                    endAdornment = buildElement {
                        InputAdornment {
                            attrs.position = InputAdornmentPosition.end
                            adornment()
                        }
                    }
                }
            } as InputBaseComponentProps
            attrs.InputLabelProps = jso {
                attrs.classes = jso { this.root = -ControlStyles.inputLabel }
                shrink = true
            }
            attrs.onChange = cachedOnChange
            if (value != null) attrs.value(value)
            attrs.label = buildElement { +label }
        }
    }
}