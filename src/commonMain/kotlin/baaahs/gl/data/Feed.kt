package baaahs.gl.data

import baaahs.RefCounted
import baaahs.gl.glsl.GlslProgram

interface Feed : RefCounted {
    fun bind(glslProgram: GlslProgram): Binding
}