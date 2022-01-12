package baaahs.scene

class OpenScene(
    val scene: Scene
) {
    val model = scene.model.open()

    fun edit(): MutableScene = MutableScene(scene)
}