package baaahs

import kotlinx.html.div
import kotlinx.html.dom.create
import kotlin.browser.document

class JsUiDisplay(private val domContainer: DomContainer) {
    private val div = document.create.div {}

    private lateinit var frame: DomContainer.Frame
    private var jsApp: dynamic = null

}
