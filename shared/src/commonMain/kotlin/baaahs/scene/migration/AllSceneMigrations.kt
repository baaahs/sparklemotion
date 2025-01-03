package baaahs.scene.migration

import baaahs.migrator.DataMigrator

val AllSceneMigrations: List<DataMigrator.Migration> = listOf(
    V1_GridDirectionBackwards,
    V2_ModelEntityIds,
    V3_MoveFixtureMappings,
)