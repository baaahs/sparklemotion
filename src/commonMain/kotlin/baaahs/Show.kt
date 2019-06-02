package baaahs

/** A show takes input from gadgets and uses it to configure shaders, creating pretty stuff on surfaces. */
interface Show {
    /**
     * Renders the next frame of the show.
     *
     * Try to keep this under 30ms or so.
     */
    fun nextFrame()

    /**
     * Called when surfaces are newly or no longer available to the show.
     *
     * If the show is able to reconfigure itself for the new set of shaders, it should do so and return `true`.
     *
     * @return true if the show should be reinitialized.
     */
    fun surfacesChanged(newSurfaces: List<Surface>, removedSurfaces: List<Surface>): Unit =
        throw RestartShowException()

    abstract class MetaData(val name: String) {
        abstract fun createShow(sheepModel: SheepModel, showRunner: ShowRunner): Show
    }

    class RestartShowException : Exception()
}
