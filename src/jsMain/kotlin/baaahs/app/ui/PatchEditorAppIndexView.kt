package baaahs.app.ui

import baaahs.app.ui.editor.ControlEditIntent
import baaahs.app.ui.editor.ShowEditableManager
import baaahs.app.ui.editor.editableManagerUi
import baaahs.client.WebClient
import baaahs.client.document.ShowManager
import baaahs.gl.Toolchain
import baaahs.gl.withCache
import baaahs.model.Model
import baaahs.plugin.Plugins
import baaahs.scene.OpenScene
import baaahs.scene.SceneMonitor
import baaahs.show.SampleData
import baaahs.sim.HostedWebApp
import baaahs.ui.xComponent
import baaahs.util.JsClock
import kotlinx.js.jso
import mui.material.CssBaseline
import mui.material.Paper
import mui.material.styles.ThemeProvider
import react.*
import react.dom.div

class PatchEditorApp(
    private val plugins: Plugins,
    private val toolchain: Toolchain,
    private val showManager: ShowManager,
    ) : HostedWebApp {
    override fun render(): ReactElement<*> {
        return createElement(PatchEditorAppIndexView, jso {
            this.plugins = this@PatchEditorApp.plugins
            this.toolchain = this@PatchEditorApp.toolchain
            this.showManager = this@PatchEditorApp.showManager.facade
        })
    }

    override fun onClose() {
    }

}

val PatchEditorAppIndexView = xComponent<PatchEditorAppIndexProps>("PatchEditorAppIndex") { props ->
    val showManager = props.showManager
    observe(showManager)

    val darkMode = true
    val theme = if (darkMode) Themes.Dark else Themes.Light
    val allStyles = memo(theme) { AllStyles(theme) }

    val show = memo { SampleData.sampleShow }
    val editableManager by state {
        ShowEditableManager { newShow -> showManager.onEdit(newShow) }
            .also {
                var controlId = "none"
                show.controls.forEach { (k, v) ->
                    if (v.title == "Ripple") controlId = k
                }
                it.openEditor(show, ControlEditIntent(controlId), props.toolchain)
            }
    }
    val sceneMonitor = memo {
        SceneMonitor(OpenScene(Model("Empty model", emptyList())))
    }

    val myAppContext = memo(allStyles) {
        jso<AppContext> {
            this.plugins = props.plugins
            this.toolchain = props.toolchain
            this.allStyles = allStyles
            this.clock = JsClock
            this.showManager = showManager
            this.sceneProvider = sceneMonitor

            this.openEditor = { editIntent ->
                editableManager.openEditor(
                    show, editIntent, props.toolchain.withCache("Edit Session")
                )
            }
        }
    }

    val myAppGlContext = memo { jso<AppGlContext> { this.sharedGlContext = null } }

    appContext.Provider {
        attrs.value = myAppContext

        appGlContext.Provider {
            attrs.value = myAppGlContext

            ThemeProvider {
                attrs.theme = theme
                CssBaseline {}

                Paper {
                    div {
                        editableManagerUi {
                            attrs.editableManager = editableManager
                        }
                    }
                }
            }
        }
    }
}

external interface PatchEditorAppIndexProps : Props {
    var webClient: WebClient.Facade
    var plugins: Plugins
    var toolchain: Toolchain
    var showManager: ShowManager.Facade
}

fun RBuilder.patchEditorAppIndex(handler: RHandler<PatchEditorAppIndexProps>) =
    child(PatchEditorAppIndexView, handler = handler)