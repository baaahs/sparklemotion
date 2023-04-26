package baaahs.gl.render

import baaahs.device.FixtureType
import baaahs.fixtures.Fixture
import baaahs.fixtures.FixtureTypeRenderPlan
import baaahs.gl.GlContext

interface FixtureRenderEngine {
    val fixtureType: FixtureType

    fun addFixture(fixture: Fixture): FixtureRenderTarget
    fun setRenderPlan(fixtureTypeRenderPlan: FixtureTypeRenderPlan?)
    fun draw()
    suspend fun finish()
    fun release()

    fun logStatus()
}