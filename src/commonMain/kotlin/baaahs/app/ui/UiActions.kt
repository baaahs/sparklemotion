package baaahs.app.ui

import baaahs.plugin.Plugins
import baaahs.scene.Scene
import baaahs.show.Show

expect object UiActions {
    fun downloadShow(show: Show, plugins: Plugins)
    fun downloadScene(scene: Scene, plugins: Plugins)
}