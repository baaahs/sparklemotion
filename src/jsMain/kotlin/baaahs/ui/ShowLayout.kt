package baaahs.ui

import baaahs.show.Layout
import external.mosaic.*
import kotlinx.css.*
import react.*
import styled.css
import styled.styledDiv
import kotlin.reflect.KClass

val ShowLayout = functionalComponent<ShowLayoutProps> { props ->
    val handleCreateNode = useCallback { args: Array<Any> ->
        console.log("ShowLayout:handleCreateNode", args)
    }

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
                            styledDiv {
//                          css { +panelToolbar }
                                +props.title
                            }
                        }
                    }

                    styledDiv {
                        +"panel for $type!"
                        props.layoutControls[type]?.forEach { layoutControl ->
                            styledDiv {
                                this.layoutControl()
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
            @Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE", "UNCHECKED_CAST")
            value = props.layout.mosaicConfig as MosaicParent<String>
            //            onChange = { onChange }
//            onRelease = { onRelease }
//            className = "mosaic mosaic-blueprint-theme bp3-dark"
        }
    }

}

typealias ControlRenderer = RBuilder.() -> Unit

external interface ShowLayoutProps : RProps {
    var layout: Layout
    var layoutControls: Map<String, List<ControlRenderer>>
}

fun RBuilder.showLayout(handler: ShowLayoutProps.() -> Unit): ReactElement =
    child(ShowLayout) { attrs { handler() } }

fun <T> RBuilder.mosaic(handler: MosaicControlledProps<T>.() -> Unit): ReactElement =
    child(Mosaic::class as KClass<out Component<MosaicControlledProps<T>, *>>) { attrs { handler() } }

fun <T> RBuilder.mosaicWindow(handler: MosaicWindowProps<T>.() -> Unit): ReactElement =
    child(MosaicWindow::class as KClass<out Component<MosaicWindowProps<T>, *>>) { attrs { handler() } }

var <T> MosaicParent<T>.firstItem: T
    get() = first
    set(value) { first = value }

var <T> MosaicParent<T>.firstSplit: MosaicParent<T>
    get() = first
    set(value) { first = value }

var <T> MosaicParent<T>.secondItem: T
    get() = second
    set(value) { second = value }

var <T> MosaicParent<T>.secondSplit: MosaicParent<T>
    get() = second
    set(value) { second = value }
