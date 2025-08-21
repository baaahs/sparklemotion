package baaahs.mapper

import baaahs.app.ui.appContext
import baaahs.ui.*
import react.*
import react.dom.button
import react.dom.div
import react.dom.onClick
import react.dom.span

private enum class Direction {
    LOWER,
    HIGHER
}
private val FindLastPixelView = xComponent<FindLastPixelProps>("FindLastPixel") { props ->
    val appContext = useContext(appContext)
    val keyboard = appContext.keyboard

    var currentPixel by state { 0 }
    var currentLowerBound by state { 0 }
    var currentUpperBound by state { props.maxPossiblePixel }
    var lastDirection by state<Direction?> { null }

    val handleLower by handler(props.mapper) {
        if (currentPixel <= currentLowerBound) {
            currentPixel--
        } else {
            if (lastDirection == Direction.LOWER) {
                currentUpperBound = currentPixel
            }
            currentPixel = (currentPixel - currentLowerBound) / 2 + currentLowerBound
        }
        lastDirection = Direction.LOWER

        props.mapper.selectEntityPixel(null, currentPixel)
    }

    val handleDown by handler(props.mapper) { withShift: Boolean ->
        currentPixel -= if (withShift) 10 else 1
        props.mapper.selectEntityPixel(null, currentPixel)
    }

    val handleHigher by handler(props.mapper) {
        if (currentPixel >= currentUpperBound) {
            currentPixel++
        } else {
            if (lastDirection == Direction.HIGHER) {
                currentLowerBound = currentPixel
            }
            currentPixel = (currentUpperBound - currentPixel) / 2 + currentPixel
        }
        lastDirection = Direction.HIGHER

        props.mapper.selectEntityPixel(null, currentPixel)
    }

    val handleUp by handler(props.mapper) { withShift: Boolean ->
        currentPixel += if (withShift) 10 else 1
        props.mapper.selectEntityPixel(null, currentPixel)
    }

    val handleSpace by handler(props.mapper) {
        props.mapper.selectEntityPixel(null, currentPixel)
    }
    val handleDone by handler(props.onFoundPixel) {
        props.onFoundPixel(currentPixel + 1)
    }
    val handleEsc by handler(props.onCancel) {
        props.onCancel()
    }

    onMount(keyboard, handleLower, handleHigher, handleDone, handleUp, handleDown, handleSpace, handleDone, handleEsc) {
        keyboard.handle { keypress, _ ->
            var result: KeypressResult? = null
            when (keypress) {
                Keypress("ArrowLeft") -> handleLower()
                Keypress("ArrowRight") -> handleHigher()
                Keypress("ArrowUp") -> handleUp(false)
                Keypress("ArrowUp", shiftKey = true) -> handleUp(true)
                Keypress("ArrowDown") -> handleDown(false)
                Keypress("ArrowDown", shiftKey = true) -> handleDown(true)
                Keypress("Space") -> handleSpace()
                Keypress("Enter") -> handleDone()
                Keypress("Z") -> handleDone()
                Keypress("Esc") -> handleEsc()
                Keypress("x") -> handleDone()
//                Keypress("KeyL", metaKey = true) -> props.onShaderLibraryDialogToggle()
                else -> result = KeypressResult.NotHandled
            }
            result ?: KeypressResult.Handled
        }
    }

    div {
        span {
            +"Pixel count: $currentPixel"
        }
        button {
            attrs.onClick = handleDone.withMouseEvent()
            +"OK"
        }
    }
}

external interface FindLastPixelProps : Props {
    var maxPossiblePixel: Int
    var onFoundPixel: (Int) -> Unit
    var onCancel: () -> Unit
    var mapper: JsMapper
}

fun RBuilder.findLastPixel(handler: RHandler<FindLastPixelProps>) =
    child(FindLastPixelView, handler = handler)