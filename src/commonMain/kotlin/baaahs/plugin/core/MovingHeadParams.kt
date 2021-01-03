package baaahs.plugin.core

import baaahs.gl.glsl.GlslType
import baaahs.gl.patch.ContentType

class MovingHeadParams {
    companion object {
        val contentType = ContentType("moving-head-params", "Moving Head Params", GlslType.Vec4)
    }
}