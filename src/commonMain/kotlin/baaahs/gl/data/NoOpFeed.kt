package baaahs.gl.data

import baaahs.RefCounted
import baaahs.RefCounter
import baaahs.gl.glsl.GlslProgram

class NoOpFeed : Feed, RefCounted by RefCounter() {
    override fun bind(glslProgram: GlslProgram): Binding {
        return object : Binding {
            override val feed: Feed
                get() = this@NoOpFeed
            override val isValid: Boolean
                get() = true

            override fun setOnProgram() {
                // No-op.
            }
        }
    }
}