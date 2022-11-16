package baaahs.app.ui.dev

import baaahs.app.ui.AllStyles
import baaahs.app.ui.AppContext
import baaahs.app.ui.Themes
import baaahs.app.ui.editor.ControlEditIntent
import baaahs.app.ui.editor.ShowEditableManager
import baaahs.app.ui.editor.editableManagerUi
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
import js.objects.jso
import mui.material.Paper
import react.*
import react.dom.div

class PatchEditorDevApp(
    private val plugins: Plugins,
    private val toolchain: Toolchain,
    private val showManager: ShowManager,
    ) : HostedWebApp {
    override fun render(): ReactElement<*> {
        return createElement(PatchEditorDevAppIndexView, jso {
            this.plugins = this@PatchEditorDevApp.plugins
            this.toolchain = this@PatchEditorDevApp.toolchain
            this.showManager = this@PatchEditorDevApp.showManager.facade
        })
    }

    override fun onClose() {
    }

}

val PatchEditorDevAppIndexView = xComponent<PatchEditorDevAppIndexProps>("PatchEditorDevAppIndex") { props ->
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

    devAppWrapper {
        attrs.appContext = myAppContext
        attrs.toolchain = props.toolchain

        Paper {
            div {
                editableManagerUi {
                    attrs.editableManager = editableManager
                }
            }
        }
    }
}

external interface PatchEditorDevAppIndexProps : Props {
    var plugins: Plugins
    var toolchain: Toolchain
    var showManager: ShowManager.Facade
}

fun RBuilder.patchEditorDevAppIndex(handler: RHandler<PatchEditorDevAppIndexProps>) =
    child(PatchEditorDevAppIndexView, handler = handler)