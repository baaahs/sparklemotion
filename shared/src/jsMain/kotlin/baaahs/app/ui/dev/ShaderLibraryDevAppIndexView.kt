package baaahs.app.ui.dev

import baaahs.PubSub
import baaahs.app.settings.UiSettings
import baaahs.app.ui.AllStyles
import baaahs.app.ui.AppContext
import baaahs.app.ui.Themes
import baaahs.app.ui.editor.shaderLibraryDialog
import baaahs.gl.Toolchain
import baaahs.io.FsServerSideSerializer
import baaahs.libraries.ShaderLibraries
import baaahs.libraries.ShaderLibraryManager
import baaahs.model.Model
import baaahs.plugin.Plugins
import baaahs.scene.OpenScene
import baaahs.scene.SceneMonitor
import baaahs.sim.BrowserSandboxFs
import baaahs.sim.HostedWebApp
import baaahs.ui.xComponent
import baaahs.util.JsClock
import baaahs.util.globalLaunch
import js.objects.jso
import react.*

class ShaderLibraryDevApp(
    private val plugins: Plugins,
    private val toolchain: Toolchain,
    private val pubSub: PubSub.Endpoint
    ) : HostedWebApp {
    override fun render(): ReactElement<*> {
        val fs = BrowserSandboxFs("Browser Data", "data")
        val shaderLibraryManager = ShaderLibraryManager(plugins, fs, FsServerSideSerializer(), pubSub, toolchain)
        globalLaunch {
            shaderLibraryManager.start()
        }

        return createElement(ShaderLibraryDevAppIndexView, jso {
            this.plugins = this@ShaderLibraryDevApp.plugins
            this.toolchain = this@ShaderLibraryDevApp.toolchain
            this.shaderLibraries = shaderLibraryManager.Facade()
        })
    }

    override fun onClose() {
    }
}

val ShaderLibraryDevAppIndexView = xComponent<ShaderLibraryDevAppIndexProps>("ShaderLibraryDevAppIndex") { props ->
    val darkMode = true
    val theme = if (darkMode) Themes.Dark else Themes.Light
    val allStyles = memo(theme) { AllStyles(theme) }

    val sceneMonitor = memo {
        SceneMonitor(OpenScene(Model("Empty model", emptyList())))
    }

    val myAppContext = memo(allStyles) {
        jso<AppContext> {
            this.plugins = props.plugins
            this.allStyles = allStyles
            this.clock = JsClock
            this.sceneProvider = sceneMonitor
            this.shaderLibraries = props.shaderLibraries
            this.uiSettings = UiSettings()
        }
    }

    devAppWrapper {
        attrs.appContext = myAppContext
        attrs.toolchain = props.toolchain

        shaderLibraryDialog {
            attrs.devWarning = true
        }
    }
}

external interface ShaderLibraryDevAppIndexProps : Props {
    var plugins: Plugins
    var toolchain: Toolchain
    var shaderLibraries: ShaderLibraries
}

fun RBuilder.shaderLibraryDevAppIndex(handler: RHandler<ShaderLibraryDevAppIndexProps>) =
    child(ShaderLibraryDevAppIndexView, handler = handler)