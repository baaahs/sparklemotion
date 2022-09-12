package baaahs.app.ui

import baaahs.app.ui.document.FileUploadStyles
import baaahs.app.ui.editor.ShaderEditorStyles
import baaahs.app.ui.editor.ShaderHelpStyles
import baaahs.app.ui.editor.ThemedEditableStyles
import baaahs.app.ui.editor.layout.LayoutEditorStyles
import baaahs.app.ui.gadgets.color.ColorWheelStyles
import baaahs.app.ui.gadgets.slider.ThemedStyles
import baaahs.app.ui.layout.LayoutStyles
import baaahs.app.ui.model.ModelEditorStyles
import baaahs.mapper.ControllerEditorStyles
import baaahs.mapper.MapperStyles
import baaahs.ui.components.UiComponentStyles
import baaahs.ui.diagnostics.DiagnosticsStyles
import mui.material.styles.Theme
import styled.injectGlobal

class AllStyles(val theme: Theme) {
    val appUi by lazy { ThemeStyles(theme) }
    val editor by lazy { baaahs.ui.editor.Styles(theme) }
    val controls by lazy { baaahs.app.ui.controls.ThemeStyles(theme) }
    val gadgetsSlider by lazy { ThemedStyles(theme) }
    val editableManager by lazy { ThemedEditableStyles(theme) }
    val layout by lazy { LayoutStyles(theme) }
    val layoutEditor by lazy { LayoutEditorStyles(theme) }
    val controllerEditor by lazy { ControllerEditorStyles(theme) }
    val modelEditor by lazy { ModelEditorStyles(theme) }
    val mapper by lazy { MapperStyles(theme) }
    val shaderEditor by lazy { ShaderEditorStyles(theme) }
    val shaderHelp by lazy { ShaderHelpStyles(theme) }
    val uiComponents by lazy { UiComponentStyles(theme) }
    val fileUploadStyles by lazy { FileUploadStyles(theme) }
    val diagnosticsStyles by lazy { DiagnosticsStyles(theme) }

    fun injectGlobals() {
        injectGlobal(Styles.global)
        injectGlobal(appUi.global)
        injectGlobal(baaahs.app.ui.controls.Styles.global)
        injectGlobal(ColorWheelStyles.global)
        injectGlobal(layout.global)
    }
}