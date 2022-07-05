package baaahs.show.migration

import baaahs.show.DataMigrator

val AllShowMigrations: List<DataMigrator.Migration> = listOf(
    V1_UpdateDataSourceRefs,
    V2_RemoveShaderType,
    V3_UpdateLayouts,
    V4_FlattenGadgetControls,
    V5_FixFixtureInfoRefs,
    V6_FlattenPatches,
    V7_LegacyTabs,
    V8_RenameShaderChannelToStream
)

val AllSceneMigrations: List<DataMigrator.Migration> = listOf(
)