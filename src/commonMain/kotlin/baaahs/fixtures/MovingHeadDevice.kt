package baaahs.fixtures

import baaahs.gl.render.FixtureRenderPlan

object MovingHeadDevice : DeviceType {
    override val id: String get() = "MovingHead"
    override val title: String get() = "Moving Head"
    override val params: List<Param> get() = emptyList()
    override val resultParams: List<ResultParam> get() = emptyList()

    override fun initPixelParams(fixtureRenderPlan: FixtureRenderPlan, paramBuffers: List<ParamBuffer>) {
//        TODO("not implemented")
    }

    override fun setFixtureParamUniforms(fixtureRenderPlan: FixtureRenderPlan, paramBuffers: List<ParamBuffer>) {
//        TODO("not implemented")
    }
}