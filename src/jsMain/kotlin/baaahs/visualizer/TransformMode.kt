package baaahs.visualizer

import baaahs.model.ModelUnit
import baaahs.util.deg2rad
import baaahs.util.rad2deg
import materialui.Icon
import materialui.icons.AspectRatio
import materialui.icons.PanTool
import materialui.icons.ThreeDRotation
import kotlin.math.roundToInt

enum class TransformMode(val modeName: String, val icon: Icon) {
    Move("translate", PanTool) {
        override fun getGridUnitAdornment(modelUnit: ModelUnit): String = modelUnit.display

        override fun getGridSize(visualizer: ModelVisualEditor.Facade): Double? =
            visualizer.moveSnap

        override fun setGridSize(visualizer: ModelVisualEditor.Facade, value: Double?) {
            visualizer.moveSnap = value
        }
    },

    Rotate("rotate", ThreeDRotation) {
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

    Scale("scale", AspectRatio) {
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