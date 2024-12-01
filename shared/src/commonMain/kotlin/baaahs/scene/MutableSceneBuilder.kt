package baaahs.scene

import baaahs.getBang
import baaahs.model.EntityId
import baaahs.model.ModelData

class MutableSceneBuilder(
    val scene: Scene,
) {
    val entityMap = hashMapOf<EntityId, MutableEntity>()

    fun buildMutableEntity(id: EntityId): MutableEntity {
        if (entityMap.containsKey(id))
            error("Entity \"$id\" already exists in the mutable scene.")

        val entityData = scene.entities.getBang(id, "entity")
        return entityData.edit()
            .also { entityMap[id] = it }
    }

    fun build(): MutableScene {
        val model = scene.model.edit()
        val controllers = scene.controllers
            .mapValues { (_, controllerConfig) -> controllerConfig.edit() }
            .toMutableMap()
        val fixtureMappings = scene.fixtureMappings
            .mapValues { (_, fixtureMappings) -> fixtureMappings.map { it.edit() }.toMutableList() }
            .toMutableMap()

        return MutableScene(model, controllers, fixtureMappings)
    }

    fun ModelData.edit(): MutableModel {
        return MutableModel(
            title,
            entityIds.map { buildMutableEntity(it) }.toMutableList(),
            units,
            initialViewingAngle
        )
    }

    fun FixtureMappingData.edit(): MutableFixtureMapping {
        return MutableFixtureMapping(
            entityId?.let { entityMap[it] },
            fixtureOptions?.edit(),
            transportConfig?.edit()
        )
    }
}