package baaahs

import kotlinx.html.div
import kotlinx.html.dom.create
import kotlin.browser.document

class JsUiDisplay(private val domContainer: DomContainer) : UiDisplay {
    private val div = document.create.div {}

    private lateinit var frame: DomContainer.Frame
    private var jsApp: dynamic = null

    override fun createApp(uiContext: UiContext) {
        frame = domContainer.getFrame(
            "UI",
            div,
            { jsApp.close() },
            { width, height -> println("Resize to $width, $height") })
        jsApp = js("document.createUiApp")(div, uiContext)
    }


}
