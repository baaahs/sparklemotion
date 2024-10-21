package baaahs.gl.render

import baaahs.fixtures.Fixture
import baaahs.gl.GlContext

abstract class FixtureRenderEngine(gl: GlContext) : RenderEngine(gl) {
    abstract fun addFixture(fixture: Fixture): FixtureRenderTarget
}