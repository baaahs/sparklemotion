package baaahs.app.ui.model

import baaahs.app.ui.appContext
import baaahs.model.LightRing
import baaahs.scene.EditingEntity
import baaahs.scene.MutableLightRingData
import baaahs.ui.unaryMinus
import baaahs.ui.value
import baaahs.ui.xComponent
import kotlinx.js.jso
import mui.material.*
import react.*
import react.dom.header

private val LightRingEditorView = xComponent<LightRingEditorProps>("LightRingEditor") { props ->
    val appContext = useContext(appContext)
    val styles = appContext.allStyles.modelEditor

    observe(props.editingEntity)
    val mutableEntity = props.editingEntity.mutableEntity

    val handleRadiusChange by handler(mutableEntity) { radius: Float ->
        mutableEntity.radius = radius
        props.editingEntity.onChange()
    }

    val handleFirstPixelRadiansChange by handler(mutableEntity) { rad: Double ->
        mutableEntity.firstPixelRadians = rad.fromDegrees.toFloat()
        props.editingEntity.onChange()
    }

    val handlePixelDirectionChange by eventHandler(mutableEntity) {
        mutableEntity.pixelDirection = LightRing.PixelDirection.valueOf(it.target.value)
        props.editingEntity.onChange()
    }


    header { +"Light Ring" }

    Container {
        attrs.classes = jso { this.root = -styles.transformEditSection }
        header { +"Radius:" }

        with(styles) {
            numberTextField("", mutableEntity.radius, { +props.editingEntity.modelUnit.display }, onChange = handleRadiusChange)
        }
    }

    Container {
        attrs.classes = jso { this.root = -styles.transformEditSection }
        header { +"First Pixel:" }

        with(styles) {
            numberTextField("Position", mutableEntity.firstPixelRadians.asDegrees, {  +"°" }, onChange = handleFirstPixelRadiansChange)
        }
    }

    Container {
        attrs.classes = jso { this.root = -styles.transformEditSection }
        header { +"Pixel Direction:" }

        FormControlLabel {
//            attrs.label { +"Pixel Direction" }
            attrs.control = buildElement {
                Select {
                    this as RElementBuilder<SelectProps<String>>

                    attrs.value = mutableEntity.pixelDirection.name
                    attrs.onChange = handlePixelDirectionChange

                    LightRing.PixelDirection.values().forEach { direction ->
                        MenuItem {
                            attrs.value = direction.name
                            ListItemText { +direction.name }
                        }
                    }
                }
            }
        }
    }

}

external interface LightRingEditorProps : Props {
    var editingEntity: EditingEntity<out MutableLightRingData>
}

fun RBuilder.lightRingEditor(handler: RHandler<LightRingEditorProps>) =
    child(LightRingEditorView, handler = handler)