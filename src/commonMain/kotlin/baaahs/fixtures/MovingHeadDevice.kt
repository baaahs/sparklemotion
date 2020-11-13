package baaahs.fixtures

import baaahs.show.DataSource

object MovingHeadDevice : DeviceType {
    override val id: String get() = "MovingHead"
    override val title: String get() = "Moving Head"

    override val dataSources: List<DataSource> get() = emptyList()

    override val resultParams: List<ResultParam> get() = listOf(
        ResultParam("Pan/Tilt", Vec2ResultType)
    )

    fun getResults(resultViews: List<ResultView>) =
        resultViews[0] as Vec2ResultType.Vec2ResultView
}