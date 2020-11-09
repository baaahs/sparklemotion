package baaahs.fixtures

interface Transport {
    val name: String
    fun send(fixture: Fixture, resultViews: List<ResultView>)
}

object NullTransport : Transport {
    override val name: String
        get() = "Null Transport"

    override fun send(fixture: Fixture, resultViews: List<ResultView>) {
        // No-op.
    }
}