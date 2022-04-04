package three_ext

import kotlinx.js.jso

@JsModule("three")
external val three: dynamic

fun installCameraControls() {
    CameraControls.install(jso { THREE = three });
}