package baaahs.fixtures

import baaahs.gl.render.RenderTarget

object MovingHeadDevice : DeviceType {
    override val id: String get() = "MovingHead"
    override val title: String get() = "Moving Head"

    override val params: List<Param> get() = emptyList()

    override val resultParams: List<ResultParam> get() = listOf(
        ResultParam("Pan/Tilt", Vec2ResultType)
    )

    override fun initPixelParams(renderTarget: RenderTarget, paramBuffers: List<ParamBuffer>) {
//        TODO("not implemented")
    }

    override fun setFixtureParamUniforms(renderTarget: RenderTarget, paramBuffers: List<ParamBuffer>) {
//        TODO("not implemented")
    }
}