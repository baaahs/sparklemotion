package baaahs.shows

class AllShows {
    companion object {
        val allShows = listOf(
            SomeDumbShow.Meta(),
            RandomShow.Meta(),
            CompositeShow.Meta(),
            ThumpShow.Meta()
        )
    }
}