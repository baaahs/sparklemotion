package baaahs.gl.render

import baaahs.gl.GlContext

actual fun pickResultDeliveryStrategy(gl: GlContext): ResultDeliveryStrategy =
    SyncResultDeliveryStrategy()