package three_ext

import kotlinext.js.Object
import kotlinext.js.jsObject

@JsModule("three")
external val three: Object

fun installCameraControls() {
    CameraControls.install(jsObject { THREE = three });
}