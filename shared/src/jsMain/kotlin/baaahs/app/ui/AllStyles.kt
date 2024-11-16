package baaahs.app.ui

import baaahs.app.ui.document.FileUploadStyles
import baaahs.app.ui.editor.ShaderEditorStyles
import baaahs.app.ui.editor.ShaderLibraryStyles
import baaahs.app.ui.editor.ThemedEditableStyles
import baaahs.app.ui.editor.help.ShaderHelpStyles
import baaahs.app.ui.editor.layout.LayoutEditorStyles
import baaahs.app.ui.gadgets.color.ColorWheelStyles
import baaahs.app.ui.gadgets.slider.ThemedStyles
import baaahs.app.ui.layout.LayoutStyles
import baaahs.app.ui.model.ModelEditorStyles
import baaahs.app.ui.patchmod.PatchModStyles
import baaahs.mapper.ControllerEditorStyles
import baaahs.mapper.MapperStyles
import baaahs.ui.components.ListAndDetailStyles
import baaahs.ui.components.UiComponentStyles
import baaahs.ui.diagnostics.DiagnosticsStyles
import mui.material.styles.Theme
import styled.StyleSheet
import styled.injectGlobal

class AllStyles(val theme: Theme) {
    val appUi by lazy { inject(ThemeStyles(theme)) }
    val controls by lazy { inject(baaahs.app.ui.controls.ThemeStyles(theme)) }
    val gadgetsSlider by lazy { inject(ThemedStyles(theme)) }
    val editableManager by lazy { inject(ThemedEditableStyles(theme)) }
    val layout by lazy { inject(LayoutStyles(theme)) }
    val layoutEditor by lazy { inject(LayoutEditorStyles(theme)) }
    val controllerEditor by lazy { inject(ControllerEditorStyles(theme)) }
    val modelEditor by lazy { inject(ModelEditorStyles(theme)) }
    val mapper by lazy { inject(MapperStyles(theme)) }
    val shaderEditor by lazy { inject(ShaderEditorStyles(theme)) }
    val shaderLibrary by lazy { inject(ShaderLibraryStyles(theme)) }
    val shaderHelp by lazy { inject(ShaderHelpStyles(theme)) }
    val uiComponents by lazy { inject(UiComponentStyles(theme)) }
    val listAndDetail by lazy { inject(ListAndDetailStyles(theme)) }
    val fileUploadStyles by lazy { inject(FileUploadStyles(theme)) }
    val diagnosticsStyles by lazy { inject(DiagnosticsStyles(theme)) }
    val patchModStyles by lazy { inject(PatchModStyles(theme)) }

    fun injectGlobals() {
        injectGlobal(Styles.global)
        injectGlobal(appUi.global)
        injectGlobal(baaahs.app.ui.controls.Styles.global)
        injectGlobal(ColorWheelStyles.global)
        injectGlobal(layout.global)
//        baaahs.app.ui.controls.Styles.inject()
    }

    private fun <T: StyleSheet> inject(styleSheet: T) = styleSheet.also { it.inject() }
}