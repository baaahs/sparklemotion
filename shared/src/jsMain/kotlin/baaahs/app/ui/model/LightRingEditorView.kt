package baaahs.app.ui.model

import baaahs.app.ui.appContext
import baaahs.app.ui.editor.betterSelect
import baaahs.app.ui.editor.numberFieldEditor
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

    val handlePixelDirectionChange by handler(mutableEntity) { newValue: LightRing.PixelDirection ->
        mutableEntity.pixelDirection = newValue
        props.editingEntity.onChange()
    }


    Container {
        attrs.className = -styles.propertiesEditSection and styles.twoColumns

        numberFieldEditor<Float> {
            this.attrs.label = "Radius"
            this.attrs.disabled = editMode.isOff
            this.attrs.adornment = props.editingEntity.modelUnit.display.asTextNode()
            this.attrs.getValue = { mutableEntity.radius }
            this.attrs.setValue = handleRadiusChange
        }

        numberFieldEditor<Double> {
            this.attrs.label = "First Pixel Position"
            this.attrs.disabled = editMode.isOff
            this.attrs.adornment = "°".asTextNode()
            this.attrs.getValue = { mutableEntity.firstPixelRadians.asDegrees }
            this.attrs.setValue = handleFirstPixelRadiansChange
        }
    }

    Container {
        attrs.className = -styles.transformEditSection

        betterSelect<LightRing.PixelDirection> {
            attrs.label = "Pixel Direction"
            attrs.disabled = editMode.isOff
            attrs.value = mutableEntity.pixelDirection
            attrs.values = LightRing.PixelDirection.entries
            attrs.onChange = handlePixelDirectionChange
        }
    }

}

external interface LightRingEditorProps : Props {
    var editingEntity: EditingEntity<out MutableLightRingData>
}

fun RBuilder.lightRingEditor(handler: RHandler<LightRingEditorProps>) =
    child(LightRingEditorView, handler = handler)