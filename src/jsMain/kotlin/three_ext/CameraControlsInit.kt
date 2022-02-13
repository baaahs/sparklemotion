package three_ext

import kotlinext.js.Object
import kotlinx.js.jso

@JsModule("three")
external val three: Object

fun installCameraControls() {
    CameraControls.install(jso { THREE = three });
}