package baaahs.show.migration

import baaahs.show.DataMigrator

val AllShowMigrations: List<DataMigrator.Migration> = listOf(
    V1_UpdateDataSourceRefs,
    V2_RemoveShaderType,
    V3_UpdateLayouts,
    V4_FlattenGadgetControls,
    V5_FixFixtureInfoRefs
)

val AllSceneMigrations: List<DataMigrator.Migration> = listOf(
)