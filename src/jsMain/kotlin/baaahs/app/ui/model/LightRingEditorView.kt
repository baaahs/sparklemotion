package baaahs.app.ui.model

import baaahs.app.ui.appContext
import baaahs.model.LightRing
import baaahs.scene.EditingEntity
import baaahs.scene.MutableLightRingData
import baaahs.ui.on
import baaahs.ui.value
import baaahs.ui.xComponent
import kotlinx.html.js.onChangeFunction
import materialui.components.container.container
import materialui.components.container.enums.ContainerStyle
import materialui.components.formcontrollabel.formControlLabel
import materialui.components.listitemtext.listItemText
import materialui.components.menuitem.menuItem
import materialui.components.select.select
import react.Props
import react.RBuilder
import react.RHandler
import react.dom.header
import react.useContext

private val LightRingEditorView = xComponent<LightRingEditorProps>("LightRingEditor") { props ->
    val appContext = useContext(appContext)
    val styles = appContext.allStyles.modelEditor

    observe(props.editingEntity)
    val mutableEntity = props.editingEntity.mutableEntity as MutableLightRingData

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

    container(styles.transformEditSection on ContainerStyle.root) {
        header { +"Radius:" }

        with(styles) {
            numberTextField("", mutableEntity.radius, { +props.editingEntity.modelUnit.display }, handleRadiusChange)
        }
    }

    container(styles.transformEditSection on ContainerStyle.root) {
        header { +"First Pixel:" }

        with(styles) {
            numberTextField("Position", mutableEntity.firstPixelRadians.asDegrees, {  +"°" }, handleFirstPixelRadiansChange)
        }
    }

    container(styles.transformEditSection on ContainerStyle.root) {
        header { +"Pixel Direction:" }

        formControlLabel {
//            attrs.label { +"Pixel Direction" }
            attrs.control {
                select {
                    attrs.value(mutableEntity.pixelDirection.name)
                    attrs.onChangeFunction = handlePixelDirectionChange

                    LightRing.PixelDirection.values().forEach { direction ->
                        menuItem {
                            attrs.value = direction.name
                            listItemText { +direction.name }
                        }
                    }
                }
            }
        }
    }

}

external interface LightRingEditorProps : Props {
    var editingEntity: EditingEntity<out LightRing>
}

fun RBuilder.lightRingEditor(handler: RHandler<LightRingEditorProps>) =
    child(LightRingEditorView, handler = handler)