package baaahs.scene

class OpenScene(private val scene: Scene) {
    val model = scene.model.open()

    fun edit(): MutableScene = MutableScene(scene)
}