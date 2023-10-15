package baaahs.controller

import baaahs.util.Clock
import kotlin.coroutines.CoroutineContext

actual class NanoleafAdapter actual constructor(coroutineContext: CoroutineContext, clock: Clock) {
    actual fun start(callback: (NanoleafDeviceMetadata) -> Unit) {
    }

    actual fun stop() {
    }

    actual fun getAccessToken(deviceMetadata: NanoleafDeviceMetadata): String {
        TODO("not implemented")
    }

    actual fun openDevice(
        deviceMetadata: NanoleafDeviceMetadata,
        accessToken: String
    ): NanoleafDevice = TODO("not implemented")
}