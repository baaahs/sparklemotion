package baaahs.show.migration

import baaahs.show.ShowMigrator

val AllMigrations: List<ShowMigrator.Migration> = listOf(
    V1_UpdateDataSourceRefs,
    V2_RemoveShaderType
)