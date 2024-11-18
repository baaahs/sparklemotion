package baaahs.scene.mutable

import baaahs.model.EntityData
import baaahs.util.UniqueIds

class SceneBuilder {
    val entityIds = UniqueIds<EntityData>()

    fun idFor(entity: EntityData): String {
        return entityIds.idFor(entity) { entity.suggestId() }
    }

    fun getEntities(): Map<String, EntityData> = entityIds.all()
}