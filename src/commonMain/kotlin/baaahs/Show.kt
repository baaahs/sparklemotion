package baaahs

interface Show {
    fun nextFrame()

    abstract class MetaData(val name: String) {
        abstract fun createShow(sheepModel: SheepModel, showRunner: ShowRunner): Show
    }
}
