package baaahs.sim.ui

import baaahs.Pinky
import baaahs.app.ui.appContext
import baaahs.ui.and
import baaahs.ui.on
import baaahs.ui.unaryPlus
import baaahs.ui.xComponent
import external.react_draggable.Draggable
import materialui.components.paper.enums.PaperStyle
import materialui.components.paper.paper
import materialui.components.portal.portal
import materialui.components.typography.typographyH6
import materialui.icon
import materialui.icons.Icons
import react.*
import react.dom.div
import react.dom.header
import react.dom.i
import react.dom.pre

val GeneratedGlslPalette = xComponent<GeneratedGlslPaletteProps>("GeneratedGlslPalette") { props ->
    observe(props.pinky.stageManager)

    val appContext = useContext(appContext)
    val styles = appContext.allStyles.simUi

    portal {
        Draggable {
            val randomStyleForHandle = "PinkyPanelHandle"
            attrs.handle = ".$randomStyleForHandle"

            div(+styles.glslCodeSheet) {
                div(+styles.dragHandle and randomStyleForHandle) {
                    icon(Icons.DragIndicator)
                }

                paper(styles.glslCodePaper on PaperStyle.root) {
                    attrs.elevation = 3

                    typographyH6 { +"Generated GLSL" }

                    div(+styles.glslCodeDiv) {
                        val renderPlan = props.pinky.stageManager.currentRenderPlan
                        if (renderPlan == null) {
                            i { +"No plans!" }
                        } else {
                            renderPlan.forEach { (deviceType, deviceTypeRenderPlans) ->
                                deviceTypeRenderPlans.forEach { programRenderPlan ->
                                    header {
                                        +"${deviceType.title}: ${programRenderPlan.renderTargets.size} fixtures"
                                    }

                                    programRenderPlan.program?.fragShader?.source
                                        ?.let { glsl -> pre { +glsl } }
                                        ?: i { +"No program!" }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

external interface GeneratedGlslPaletteProps : RProps {
    var pinky: Pinky.Facade
}

fun RBuilder.generatedGlslPalette(handler: RHandler<GeneratedGlslPaletteProps>) =
    child(GeneratedGlslPalette, handler = handler)