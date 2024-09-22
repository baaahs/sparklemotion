package baaahs.visualizer

import baaahs.visualizer.entity.ItemVisualizer
import three.*

val Material.color: Color
    get() = when (this) {
        is MeshBasicMaterial -> color
        is MeshLambertMaterial -> color
        else -> error("No color for this material!")
    }

enum class EntityStyle {
    /** Default is always applied first. */
    Default {
        override fun appliesTo(itemVisualizer: ItemVisualizer<*>) =
            true

        override fun applyToLine(material: LineDashedMaterial, use: Use?) {
            material.color.set(0x666666)
            material.gapSize = 0
            material.linewidth = 2
            material.visible = true
            material.opacity = .5

            if (use == Use.GroupOutline) {
                material.visible = false
            }
            if (use == Use.LightStrand) {
                material.color.set(0x001100)
            }
        }

        override fun applyToMesh(material: Material, use: Use?) {
            material.color.set(0x222222)
            material.side = FrontSide
            material.transparent = false
            material.visible = true
            material.opacity = 1

            when (use) {
                Use.LightStrand -> material.color.set(0x226622)
                Use.FixtureHardware -> material.color.set(0x444444)
                else -> {}
            }
        }

        override fun applyToPoints(material: PointsMaterial, use: Use?) {
            material.visible = false
        }
    },
    Editing {
        override fun appliesTo(itemVisualizer: ItemVisualizer<*>) =
            itemVisualizer.isEditing

        override fun applyToLine(material: LineDashedMaterial, use: Use?) {
            material.color.set(0xaaaaaa)
            material.linewidth = 3
            material.visible = true

            when (use) {
                Use.LightStrand -> material.color.set(0x226622)
                Use.FixtureHardware -> material.color.set(0x444444)
                else -> {}
            }
        }

        override fun applyToMesh(material: Material, use: Use?) {
            when (use) {
                Use.LightStrandHint -> {
                    material.color.set(0x662222)
                    material.transparent = true
                    material.opacity = .5
                }
                else -> {}
            }
        }

        override fun applyToPoints(material: PointsMaterial, use: Use?) {
            material.color.set(0x222288)
            material.visible = true
            material.opacity = .5
        }
    },
    Selected {
        private val lineColorMultiplier = Color(4.0, 4.0, .75)
        private val meshColorMultiplier = Color(.75, .75, 4.0)

        override fun appliesTo(itemVisualizer: ItemVisualizer<*>) =
            itemVisualizer.selected

        override fun applyToLine(material: LineDashedMaterial, use: Use?) {
            material.color.multiply(lineColorMultiplier)
            material.linewidth = 5
            material.visible = true
        }

        override fun applyToMesh(material: Material, use: Use?) {
            material.color.multiply(meshColorMultiplier)

            when (use) {
                Use.FixtureHardware -> material.color.multiply(Color(2, 2, 2))
                else -> {}
            }
        }

        override fun applyToPoints(material: PointsMaterial, use: Use?) {
            material.color.set(0x2222CC)
        }
    },
    ParentSelected {
        override fun appliesTo(itemVisualizer: ItemVisualizer<*>) =
            false // TODO entityVisualizer.parentSelected?

        override fun applyToLine(material: LineDashedMaterial, use: Use?) {
            material.color.set(0xffccaa)
            material.dashSize = 1
            material.gapSize = 1
            material.visible = true
        }

        override fun applyToMesh(material: Material, use: Use?) {
        }
    },
    MapperRunning {
        override fun appliesTo(itemVisualizer: ItemVisualizer<*>) =
            itemVisualizer.mapperIsRunning

        override fun applyToLine(material: LineDashedMaterial, use: Use?) {
            material.visible = true
        }

        override fun applyToMesh(material: Material, use: Use?) {
            material.transparent = true
            material.opacity = .2
        }

        override fun applyToPoints(material: PointsMaterial, use: Use?) {
            material.color.set(0x2222CC)
            material.visible = false
        }
    };

    abstract fun appliesTo(itemVisualizer: ItemVisualizer<*>): Boolean
    open fun applyToLine(material: LineDashedMaterial, use: Use? = null) {}
    open fun applyToMesh(material: Material, use: Use? = null) {}
    open fun applyToPoints(material: PointsMaterial, use: Use? = null) {}

    enum class Use {
        BacklitSurface,
        LightStrand,
        LightStrandHint,
        GroupOutline,
        Pixel,
        FixtureHardware
    }

    companion object {
        fun applyStyles(itemVisualizer: ItemVisualizer<*>, block: (EntityStyle) -> Unit) {
            values().forEach {
                if (it.appliesTo(itemVisualizer)) block.invoke(it)
            }
        }

        fun meshMaterial(): MeshBasicMaterial = MeshBasicMaterial()
        fun lineMaterial(): LineDashedMaterial = LineDashedMaterial()
        fun pointsMaterial(): PointsMaterial = PointsMaterial()
    }
}