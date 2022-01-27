package baaahs.visualizer

import three.js.*

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

            if (use == Use.Strand) {
                material.color.set(0x001100)
            }
        }

        override fun applyToMesh(material: MeshBasicMaterial, use: Use?) {
            material.color.set(0x222222)
            material.side = FrontSide
            material.transparent = false
            material.visible = true
            material.opacity = 1

            if (use == Use.Strand) {
                material.color.set(0x226622)
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

            if (use == Use.Strand) {
                material.color.set(0x226622)
            }
        }

        override fun applyToMesh(material: MeshBasicMaterial, use: Use?) {
            material.color.set(0x222222)
            material.side = FrontSide

            if (use == Use.Strand) {
                material.color.set(0x226622)
            }
        }

        override fun applyToPoints(material: PointsMaterial, use: Use?) {
            material.color.set(0x222288)
            material.visible = true
            material.opacity = 1
        }
    },
    Selected {
        override fun appliesTo(itemVisualizer: ItemVisualizer<*>) =
            itemVisualizer.selected

        override fun applyToLine(material: LineDashedMaterial, use: Use?) {
            (material.color as Color).multiplyScalar(1.2)
            material.linewidth = 5
        }

        override fun applyToMesh(material: MeshBasicMaterial, use: Use?) {
            material.color.multiplyScalar(1.2)
//            material.color.set(0x443322)
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
        }

        override fun applyToMesh(material: MeshBasicMaterial, use: Use?) {
        }
    },
    MapperRunning {
        override fun appliesTo(itemVisualizer: ItemVisualizer<*>) =
            itemVisualizer.mapperIsRunning

        override fun applyToMesh(material: MeshBasicMaterial, use: Use?) {
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
    open fun applyToMesh(material: MeshBasicMaterial, use: Use? = null) {}
    open fun applyToPoints(material: PointsMaterial, use: Use? = null) {}

    enum class Use {
        Container,
        Strand
    }

    companion object {
        fun applyStyles(itemVisualizer: ItemVisualizer<*>, block: (EntityStyle) -> Unit) {
            values().forEach {
                if (it.appliesTo(itemVisualizer)) block.invoke(it)
            }
        }
    }
}