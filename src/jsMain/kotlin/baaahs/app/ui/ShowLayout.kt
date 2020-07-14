package baaahs.app.ui

import baaahs.app.ui.controls.SpecialControlProps
import baaahs.app.ui.controls.control
import baaahs.show.Control
import baaahs.show.Layout
import baaahs.ui.and
import baaahs.ui.on
import baaahs.ui.unaryPlus
import baaahs.ui.useCallback
import external.mosaic.*
import kotlinx.css.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import kotlinx.serialization.json.JsonElementSerializer
import materialui.components.paper.enums.PaperStyle
import materialui.components.paper.paper
import react.*
import react.dom.div
import styled.css
import styled.styledDiv
import kotlin.reflect.KClass

val ShowLayout = functionalComponent<ShowLayoutProps> { props ->
    val handleCreateNode = useCallback { args: Array<Any> ->
        console.log("ShowLayout:handleCreateNode", args)
    }

    val editModeStyle = if (props.editMode)
        Styles.editModeOn
    else
        Styles.editModeOff

    styledDiv {
        css {
            width = 100.pct
            height = 100.pct
            display = Display.flex
            flexDirection = FlexDirection.column
            position = Position.absolute
            top = 40.px
            left = 0.px
        }

//    <MosiacMenuBar />
        mosaic<String> {
            renderTile = { type, path ->
                MosaicWindow {
                    attrs {
                        draggable = false
//                    additionalControls = if (type === "") additionalControls else emptyArray<String>()
                        title = type
                        createNode = handleCreateNode
                        this.path = path
                        onDragStart = { console.log("MosaicWindow.onDragStart") }
                        onDragEnd = { type -> console.log("MosaicWindow.onDragEnd", type) }
                        renderToolbar = { props: MosaicWindowProps<*>, draggable: Boolean? ->
                            div { +props.title }
                        }
                    }

                    paper(Styles.layoutPanel and editModeStyle on PaperStyle.root) {
                        props.panelControls[type]?.let { layoutControls ->
                            div(+Styles.layoutControls and Styles.showControls) {
                                div(+Styles.controlPanelHelpText) { +"Show Controls" }
                                renderControls(layoutControls.showControls, props)
                            }
                            div(+Styles.layoutControls and Styles.sceneControls) {
                                div(+Styles.controlPanelHelpText) { +"Scene Controls" }
                                renderControls(layoutControls.sceneControls, props)
                            }
                            div(+Styles.layoutControls and Styles.patchControls) {
                                div(+Styles.controlPanelHelpText) { +"Patch Controls" }
                                renderControls(layoutControls.patchControls, props)
                            }
                        }
//                    css { +windowContainer }
//                            React.createElement(WINDOWS_BY_TYPE[type])
                    }
                }
//                mosaicWindow<String> {
//                    draggable = false
////                    additionalControls = if (type === "") additionalControls else emptyArray<String>()
//                    title = type
//                    createNode = handleCreateNode
//                    this.path = path
//                    onDragStart = { console.log("MosaicWindow.onDragStart") }
//                    onDragEnd = { type -> console.log("MosaicWindow.onDragEnd", type) }
//                    renderToolbar = { props: MosaicWindowProps<*>, draggable: Boolean? ->
//                        styledDiv {
////                        css { +panelToolbar }
//                            +props.title
//                        }
//                    }
//
//                    styledDiv {
//                        +"panel for $type!"
//                        props.layoutControls[type]?.forEach { layoutControl ->
//                            styledDiv {
//                                +"Control: $type"
//                                this.layoutControl()
//                            }
//                        }
////                    css { +windowContainer }
////                            React.createElement(WINDOWS_BY_TYPE[type])
//                    }
//                }
            }

//        zeroStateView={<MosaicZeroState createNode={createNode} />}
            val jsonInst =
                Json(JsonConfiguration.Stable)
            val layoutRoot = props.layout.rootNode
            val asJson =
                jsonInst.stringify(JsonElementSerializer, layoutRoot)
            val layoutRootJs = JSON.parse<dynamic>(asJson)
            println("asJson = ${asJson}")
            @Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE", "UNCHECKED_CAST")
            value = layoutRootJs as MosaicParent<String>
            //            onChange = { onChange }
//            onRelease = { onRelease }
//            className = "mosaic mosaic-blueprint-theme bp3-dark"
        }
    }
}

private fun RBuilder.renderControls(controls: MutableList<Control>, props: ShowLayoutProps) {
    controls.forEach {
        control {
            attrs.control = it
            attrs.specialControlProps = props.specialControlProps
        }
    }
}

external interface ShowLayoutProps : RProps {
    var layout: Layout
    var panelControls: Map<String, PanelControls>
    var specialControlProps: SpecialControlProps
    var editMode: Boolean
}

fun RBuilder.showLayout(handler: RHandler<ShowLayoutProps>): ReactElement =
    child(ShowLayout, handler = handler)

fun <T> RBuilder.mosaic(handler: MosaicControlledProps<T>.() -> Unit): ReactElement =
    child(Mosaic::class as KClass<out Component<MosaicControlledProps<T>, *>>) { attrs { handler() } }

fun <T> RBuilder.mosaicWindow(handler: MosaicWindowProps<T>.() -> Unit): ReactElement =
    child(MosaicWindow::class as KClass<out Component<MosaicWindowProps<T>, *>>) { attrs { handler() } }
