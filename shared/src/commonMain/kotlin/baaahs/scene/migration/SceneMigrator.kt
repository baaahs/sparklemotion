package baaahs.scene.migration

import baaahs.migrator.DataMigrator
import baaahs.scene.Scene

object SceneMigrator : DataMigrator<Scene>(Scene.serializer(), AllSceneMigrations)