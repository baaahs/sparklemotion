package baaahs.visualizer

import baaahs.app.ui.jsIcon
import baaahs.model.ModelUnit
import baaahs.ui.Icon
import baaahs.util.deg2rad
import baaahs.util.rad2deg
import mui.icons.material.AspectRatio
import mui.icons.material.PanTool
import mui.icons.material.ThreeDRotation
import kotlin.math.roundToInt

enum class TransformMode(val modeName: String, val icon: Icon) {
    Move("translate", jsIcon(PanTool)) {
        override fun getGridUnitAdornment(modelUnit: ModelUnit): String = modelUnit.display

        override fun getGridSize(visualizer: ModelVisualEditor.Facade): Double? =
            visualizer.moveSnap

        override fun setGridSize(visualizer: ModelVisualEditor.Facade, value: Double?) {
            visualizer.moveSnap = value
        }
    },

    Rotate("rotate", jsIcon(ThreeDRotation)) {
        override val defaultGridSize: Double
            get() = deg2rad(15.0)

        override fun getGridUnitAdornment(modelUnit: ModelUnit): String = "°"

        override fun getGridSize(visualizer: ModelVisualEditor.Facade): Double? =
            visualizer.rotateSnap

        override fun setGridSize(visualizer: ModelVisualEditor.Facade, value: Double?) {
            visualizer.rotateSnap = value
        }

        override fun toDisplayValue(size: Double): Double =
            (rad2deg(size) * 1000.0).roundToInt() / 1000.0

        override fun fromDisplayValue(size: Double?): Double? =
            size?.let { deg2rad(size) }
    },

    Scale("scale", jsIcon(AspectRatio)) {
        override fun getGridUnitAdornment(modelUnit: ModelUnit): String = modelUnit.display

        override fun getGridSize(visualizer: ModelVisualEditor.Facade): Double? =
            visualizer.scaleSnap

        override fun setGridSize(visualizer: ModelVisualEditor.Facade, value: Double?) {
            visualizer.scaleSnap = value
        }
    };

    abstract fun getGridUnitAdornment(modelUnit: ModelUnit): String
    abstract fun getGridSize(visualizer: ModelVisualEditor.Facade): Double?
    abstract fun setGridSize(visualizer: ModelVisualEditor.Facade, value: Double?)
    open fun toDisplayValue(size: Double) = size
    open fun fromDisplayValue(size: Double?) = size
    open val defaultGridSize: Double = 1.0

    companion object {
        fun find(name: String) =
            values().find { it.modeName == name }
                ?: error("huh?")
    }
}