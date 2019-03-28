package baaahs

abstract class ShowMeta(val name: String) {
    abstract fun createShow(sheepModel: SheepModel, showRunner: ShowRunner): Show
}

interface Show {
    fun nextFrame()
}
