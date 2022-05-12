package baaahs.app.ui.layout

import baaahs.app.ui.StyleConstants
import baaahs.app.ui.controls.Styles
import baaahs.ui.asColor
import baaahs.ui.selector
import kotlinx.css.*
import kotlinx.css.properties.*
import mui.material.styles.Theme
import styled.StyleSheet

class LayoutStyles(val theme: Theme) : StyleSheet("app-ui-layout", isStatic = true) {
    private val transitionTime = .2.s

    val gridOuterContainer by css {
        position = Position.absolute
        width = 100.pct
        height = 100.pct
    }

    val gridBackground by css {
        position = Position.absolute
        width = 100.pct
        height = 100.pct
        transition(::opacity, duration = Styles.editTransitionDuration, timing = Timing.linear)
    }

    val gridContainer by css {
        position = Position.relative
        width = 100.pct
        height = 100.pct

        // So clicks fall through to placeholder squares:
        pointerEvents = PointerEvents.none

        children {
            pointerEvents = PointerEvents.auto
        }

        descendants(".app-ui-layout-resize-handle") {
            zIndex = StyleConstants.Layers.aboveSharedGlCanvas
        }
    }

    val gridCell by css {
        display = Display.grid

        descendants(selector(::gridContainer)) {
            pointerEvents = PointerEvents.auto
        }

        zIndex = StyleConstants.Layers.aboveSharedGlCanvas
    }

    val groupGridCell by css {
        put(::zIndex.name, "unset")
    }

    private val emptyCellDimColor = theme.palette.text.primary.asColor()
        .withAlpha(.5).blend(
            Color(theme.palette.background.default)
        )

    private val emptyCellColor = theme.palette.text.primary.asColor()
        .withAlpha(.1).blend(
            Color(theme.palette.background.default)
        )

    val emptyGridCell by css {
        position = Position.absolute
        display = Display.flex
        justifyContent = JustifyContent.center
        alignItems = Align.center
        color = emptyCellDimColor
        backgroundColor = theme.palette.background.default.asDynamic()
        border(3.px, BorderStyle.solid, theme.palette.text.primary.asColor().withAlpha(.25))
        transition(::opacity, transitionTime)
        transition(::border, transitionTime)

//        outlineWidth = 3.px
//        put("outlineStyle", "dashed")
//        outlineColor = theme.palette.text.primary.asColor().withAlpha(.5)
//        outlineOffset = 6.px

    }

    val dropPlaceholder by css {
        outlineWidth = 3.px
        put("outlineStyle", "dashed")
        outlineColor = theme.palette.text.secondary.asColor().withAlpha(.5)
        outlineOffset = (-6).px
        put("-webkit-transition", "-webkit-transform 0.2s")
        transition(::transform, 0.2.s)
    }

    val addControl by css {
        border = "2px solid pink"
        backgroundColor = Color.yellow
    }

    val controlBox by css {
        display = Display.grid
        position = Position.relative
//        width = 100.pct
//        height = 100.pct
        marginRight = 0.em

        hover {
            child(deleteButton.selector) {
                opacity = .7
                filter = "drop-shadow(0px 0px 2px black)"
            }

            child(editButton.selector) {
                opacity = .7
                filter = "drop-shadow(0px 0px 2px black)"
            }

            child(Styles.dragHandle.selector) {
                opacity = 1
                filter = "drop-shadow(0px 0px 2px black)"
                cursor = Cursor.move
            }
        }

        transition(::transform, duration = Styles.editTransitionDuration, timing = Timing.linear)

        child(Styles.controlButton.selector) {
            display = Display.grid
//            width = 100.pct
//            height = 100.pct

            child("button") {
                position = Position.absolute
                top = 0.px
                left = 0.px
                right = 0.px
                bottom = 0.px
            }
        }
    }

    val editModeControl by css {
        transition(::opacity, duration = Styles.editTransitionDuration, timing = Timing.linear)
    }
    val deleteModeControl by css {
        transition(::opacity, duration = Styles.editTransitionDuration, timing = Timing.linear)
    }

    val deleteButton by css {
        position = Position.absolute
        right = 1.em
        bottom = (-2).px + (2.5).em
        zIndex = StyleConstants.Layers.aboveSharedGlCanvas

        child("svg") {
            width = .75.em
            height = .75.em
        }
    }
    val editButton by css {
        position = Position.absolute
        right = 1.em
        bottom = (-2).px + 1.em
        zIndex = StyleConstants.Layers.aboveSharedGlCanvas

        child("svg") {
            width = .75.em
            height = .75.em
        }
    }

    val editModeOff by css {
        descendants(
            selector(::gridBackground),
            selector(::deleteModeControl),
            selector(::editModeControl),
        ) {
            pointerEvents = PointerEvents.none
            opacity = 0
        }

        descendants(selector(::emptyGridCell)) {
            opacity = 0
            border = "0px inset $emptyCellDimColor"

            hover {
                color = emptyCellDimColor
            }
        }
    }
    val editModeOn by css {
        descendants(selector(::editModeControl)) {
            opacity = 1
        }

        descendants(selector(::emptyGridCell)) {
            color = emptyCellColor
            border = "3px inset ${emptyCellColor.withAlpha(.5)}"
            transition(::border, transitionTime)

            hover {
                backgroundColor = theme.palette.primary.main.asColor()
                border = "3px inset $emptyCellColor"
            }
        }
    }

    val dragging by css {
        descendants(selector(::gridContainer)) {
            pointerEvents = PointerEvents.auto
        }
    }
    val notDragging by css {
    }

    val buttonGroupHeader by css {
        padding(2.px, 1.em)
        lineHeight = LineHeight.normal
        userSelect = UserSelect.none
    }

    val global = CssBuilder().apply {
        ".react-grid-placeholder" {
            +dropPlaceholder
        }

        ".app-ui-layout-resize-handle" {
            position = Position.absolute
            right = (-12).px
            bottom = (-12).px
        }

        ".react-resizable-hide > .app-ui-layout-resize-handle" {
            display = Display.none
        }

        ".react-grid-item.resizing.grid-item-resizing" {
            zIndex = StyleConstants.Layers.aboveSharedGlCanvas + 1
        }

        ".react-grid-item > .app-ui-layout-resize-handle" {
            position = Position.absolute
            width = 20.px
            height = 20.px
        }

        ".react-grid-item > .app-ui-layout-resize-handle::after" {
            content = QuotedString("")
            position = Position.absolute
            right = 3.px
            bottom = 3.px
            width = 5.px
            height = 5.px
            borderRight = "2px solid rgba(0, 0, 0, 0.4)"
            borderBottom = "2px solid rgba(0, 0, 0, 0.4)"
        }

        ".react-draggable-dragging, .react-draggable-dragging *" {
            pointerEvents = PointerEvents.none
        }

        val inset = 2.px

        ".react-resizable-hide > .app-ui-layout-resize-handle" {
            display = Display.none
        }

        ".app-ui-layout-resize-handle-sw" {
            bottom = inset
            left = inset
            cursor = Cursor.swResize
            transform { rotate(90.deg) }
        }
        ".app-ui-layout-resize-handle-se" {
            bottom = inset
            right = inset
            cursor = Cursor.seResize
        }
        ".app-ui-layout-resize-handle-nw" {
            top = inset
            left = inset
            cursor = Cursor.nwResize
            transform { rotate(180.deg) }
        }
        ".app-ui-layout-resize-handle-ne" {
            top = inset
            right = inset
            cursor = Cursor.neResize
            transform { rotate(270.deg) }
        }
        ".app-ui-layout-resize-handle-w" {
            top = 50.pct
            left = inset
            cursor = Cursor.ewResize
            transform { rotate(90.deg) }
        }
        ".app-ui-layout-resize-handle-e" {
            top = 50.pct
            right = inset
            cursor = Cursor.ewResize
            transform { rotate(270.deg) }
        }
        ".app-ui-layout-resize-handle-n" {
            top = inset
            left = 50.pct
            cursor = Cursor.nsResize
            transform { rotate(180.deg) }
        }
        ".app-ui-layout-resize-handle-s" {
            bottom = inset
            left = 50.pct
            cursor = Cursor.nsResize
        }

    }
}