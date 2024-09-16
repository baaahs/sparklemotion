package baaahs.visualizer.entity

import baaahs.app.ui.controllers.brainControllerEditor
import baaahs.app.ui.controllers.directDmxControllerEditor
import baaahs.app.ui.controllers.sacnControllerEditor
import baaahs.app.ui.model.*
import baaahs.device.MovingHeadDevice
import baaahs.device.PixelArrayDevice
import baaahs.device.pixelArrayFixtureConfigEditor
import baaahs.dmx.MutableDmxTransportConfig
import baaahs.dmx.dmxTransportConfigEditor
import baaahs.geom.toThreeEuler
import baaahs.model.Model
import baaahs.scene.*
import baaahs.ui.Observable
import baaahs.ui.View
import baaahs.ui.renderWrapper
import baaahs.visualizer.EntityStyle
import baaahs.visualizer.movers.movingHeadFixtureConfigEditor
import baaahs.visualizer.toVector3

@Suppress("LeakingThis")
abstract class BaseEntityVisualizer<T : Model.Entity>(
    override var item: T
) : Observable(), ItemVisualizer<T> {
    override val title: String
        get() = item.title

    override var isEditing: Boolean = false
    override var mapperIsRunning: Boolean = false
    override var selected: Boolean = false

    protected abstract fun applyStyle(entityStyle: EntityStyle)

    override fun applyStyles() {
        EntityStyle.applyStyles(this) { applyStyle(it) }
    }

    override fun update(newItem: T) {
        item = newItem

        obj.name = newItem.title
        obj.position.copy(newItem.position.toVector3())
        obj.rotation.copy(newItem.rotation.toThreeEuler())
        obj.scale.copy(newItem.scale.toVector3())
    }
}

actual val visualizerBuilder: VisualizerBuilder = object : VisualizerBuilder {
    override fun getTitleAndDescEditorView(editingEntity: EditingEntity<out MutableEntity>): View = renderWrapper {
        titleAndDescriptionEditor {
            attrs.editingEntity = editingEntity
        }
    }

    override fun getTransformEditorView(editingEntity: EditingEntity<out MutableEntity>): View = renderWrapper {
        transformationEditor {
            attrs.editingEntity = editingEntity
        }
    }

    override fun getGridEditorView(editingEntity: EditingEntity<out MutableGridData>): View = renderWrapper {
        gridEditor {
            attrs.editingEntity = editingEntity
        }
    }

    override fun getLightBarEditorView(editingEntity: EditingEntity<out MutableLightBarData>): View = renderWrapper {
        lightBarEditor {
            attrs.editingEntity = editingEntity
        }
    }

    override fun getLightRingEditorView(editingEntity: EditingEntity<out MutableLightRingData>): View = renderWrapper {
        lightRingEditor {
            attrs.editingEntity = editingEntity
        }
    }

    override fun getMovingHeadEditorView(editingEntity: EditingEntity<out MutableMovingHeadData>): View = renderWrapper {
        movingHeadEditor {
            attrs.editingEntity = editingEntity
        }
    }

    override fun getImportedEntityEditorView(editingEntity: EditingEntity<out MutableImportedEntityGroup>): View = renderWrapper {
        objGroupEditor {
            attrs.editingEntity = editingEntity
        }
    }


    // Controllers:

    override fun getBrainControllerEditorView(editingController: EditingController<MutableBrainControllerConfig>): View = renderWrapper {
        brainControllerEditor {
            attrs.editingController = editingController
        }
    }

    override fun getDirectDmxControllerEditorView(editingController: EditingController<MutableDirectDmxControllerConfig>): View = renderWrapper {
        directDmxControllerEditor {
            attrs.editingController = editingController
        }
    }

    override fun getSacnControllerEditorView(editingController: EditingController<MutableSacnControllerConfig>): View = renderWrapper {
        sacnControllerEditor {
            attrs.editingController = editingController
        }
    }


    // FixtureConfigs:

    override fun getMovingHeadFixtureOptionsEditorView(
        editingController: EditingController<*>,
        mutableFixtureOptions: MovingHeadDevice.MutableOptions
    ): View = renderWrapper {
        movingHeadFixtureConfigEditor {
            attrs.editingController = editingController
            attrs.mutableFixtureConfig = mutableFixtureOptions
        }
    }

    override fun getPixelArrayFixtureOptionsEditorView(
        editingController: EditingController<*>,
        mutableFixtureOptions: PixelArrayDevice.MutableOptions
    ): View = renderWrapper {
        pixelArrayFixtureConfigEditor {
            attrs.editingController = editingController
            attrs.mutableFixtureOptions = mutableFixtureOptions
        }
    }


    // TransportConfigs:

    override fun getDmxTransportConfigEditorView(
        editingController: EditingController<*>,
        mutableTransportConfig: MutableDmxTransportConfig
    ): View = renderWrapper {
        dmxTransportConfigEditor {
            attrs.editingController = editingController
            attrs.mutableTransportConfig = mutableTransportConfig
        }
    }
}