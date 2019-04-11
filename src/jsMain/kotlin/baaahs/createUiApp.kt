package baaahs

actual fun createUiApp(elementId: String, uiContext: UiContext): Any =
    js("document.createUiApp")(elementId, uiContext)