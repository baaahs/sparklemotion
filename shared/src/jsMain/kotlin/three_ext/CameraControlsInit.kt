package three_ext

import js.objects.jso

@JsModule("three")
external val three: dynamic

fun installCameraControls() {
    CameraControls.install(jso { THREE = three });
}