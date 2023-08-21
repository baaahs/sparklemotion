package three_ext

import js.core.jso

@JsModule("three")
external val three: dynamic

fun installCameraControls() {
    CameraControls.install(jso { THREE = three });
}