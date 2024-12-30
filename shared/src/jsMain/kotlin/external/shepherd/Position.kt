package external.shepherd

enum class Position {
    auto,
    autoStart,
    autoEnd,
    top,
    topStart,
    topEnd,
    bottom,
    bottomStart,
    bottomEnd,
    right,
    rightStart,
    rightEnd,
    left,
    leftStart,
    leftEnd
}

fun loadCss() {
    kotlinext.js.require<Any>("shepherd.js/dist/css/shepherd.css")
}
