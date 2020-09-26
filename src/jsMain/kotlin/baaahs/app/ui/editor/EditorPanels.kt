package baaahs.app.ui.editor

import baaahs.show.mutable.MutablePatch
import baaahs.show.mutable.MutablePatchHolder
import baaahs.show.mutable.MutableShaderInstance
import baaahs.show.mutable.MutableShow
import baaahs.ui.Renderer
import react.dom.div

actual fun getEditorPanelViews(): EditorPanelViews = object : EditorPanelViews {
    override fun forPatchHolder(
        editableManager: EditableManager,
        mutablePatchHolder: MutablePatchHolder
    ): Renderer = renderWrapper {
        fixturesList {
            attrs.editableManager = editableManager
            attrs.mutablePatchHolder = mutablePatchHolder
        }
    }

    override fun forPatch(
        editableManager: EditableManager,
        mutablePatch: MutablePatch
    ): Renderer = renderWrapper {
        patchOverview {
            attrs.editableManager = editableManager
            attrs.mutablePatch = mutablePatch
//            attrs.onSelectShaderInstance =
        }
    }

    override fun forShaderInstance(
        editableManager: EditableManager,
        mutablePatch: MutablePatch,
        mutableShaderInstance: MutableShaderInstance
    ): Renderer =
        renderWrapper {
            shaderInstanceEditor {
                attrs.editableManager = editableManager
                attrs.mutablePatch = mutablePatch
                attrs.mutableShaderInstance = mutableShaderInstance
                attrs.shaderChannels = emptySet() // TODO
            }
        }

    override fun forShow(
        editableManager: EditableManager,
        mutableShow: MutableShow
    ): Renderer = renderWrapper {
        div {}
    }
}