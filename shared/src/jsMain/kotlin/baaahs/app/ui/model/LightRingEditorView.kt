package baaahs.app.ui.model

import baaahs.app.ui.appContext
import baaahs.model.LightRing
import baaahs.scene.EditingEntity
import baaahs.scene.MutableLightRingData
import baaahs.ui.*
import mui.material.*
import react.*
import react.dom.header

private val LightRingEditorView = xComponent<LightRingEditorProps>("LightRingEditor") { props ->
    val appContext = useContext(appContext)
    val editMode = observe(appContext.sceneManager.editMode)
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


    Container {
        attrs.className = -styles.transformEditSection
        header { +"Radius:" }

        with(styles) {
            numberTextField<Float> {
                this.attrs.label = ""
                this.attrs.disabled = editMode.isOff
                this.attrs.value = mutableEntity.radius
                this.attrs.adornment = props.editingEntity.modelUnit.display.asTextNode()
                this.attrs.onChange = handleRadiusChange
            }
        }
    }

    Container {
        attrs.className = -styles.transformEditSection
        header { +"First Pixel:" }

        with(styles) {
            numberTextField<Double> {
                this.attrs.label = "Position"
                this.attrs.disabled = editMode.isOff
                this.attrs.value = mutableEntity.firstPixelRadians.asDegrees
                this.attrs.adornment = "°".asTextNode()
                this.attrs.onChange = handleFirstPixelRadiansChange
            }
        }
    }

    Container {
        attrs.className = -styles.transformEditSection
        header { +"Pixel Direction:" }

        FormControlLabel {
//            attrs.label { +"Pixel Direction" }
            attrs.control = buildElement {
                Select<SelectProps<String>> {
                    attrs.value = mutableEntity.pixelDirection.name
                    attrs.disabled = editMode.isOff
                    attrs.onChange = handlePixelDirectionChange.withSelectEvent()

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