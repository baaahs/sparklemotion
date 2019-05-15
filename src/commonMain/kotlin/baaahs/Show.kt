package baaahs

/** A show takes input from gadgets and uses it to configure shaders, creating pretty stuff on surfaces. */
interface Show {
    /**
     * Renders the next frame of the show.
     *
     * Try to keep this under 30ms or so.
     */
    fun nextFrame()

    abstract class MetaData(val name: String) {
        abstract fun createShow(sheepModel: SheepModel, showRunner: ShowRunner): Show
    }
}
