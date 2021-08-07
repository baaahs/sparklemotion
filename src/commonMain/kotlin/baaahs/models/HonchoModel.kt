package baaahs.models

import baaahs.geom.Vector3F
import baaahs.model.LightRing
import baaahs.model.Model

class HonchoModel : Model() {
    override val name: String = "Honcho"

    override val allEntities: List<Entity> = listOf(
        lightRing("ring 1", Vector3F(54f, 66f, 0f), 1.7.m),

        lightRing("ring 2", Vector3F(114f, 66f, 0f), 1.4.m),

        lightRing("ring 3a", Vector3F(66f, 47f, 0f), 1.9.m),
        lightRing("ring 3b", Vector3F(66f, 47f, 0f), 1.7.m),
        lightRing("ring 3c", Vector3F(66f, 47f, 0f), 1.5.m),
    )

    fun lightRing(name: String, center: Vector3F, radius: Float) =
        LightRing(name, name, center, radius, Vector3F(0f, 0f, 1f))

    val Number.m: Float get() = toFloat() * 100f / 2.54f
}