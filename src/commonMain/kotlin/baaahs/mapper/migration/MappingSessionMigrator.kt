package baaahs.mapper.migration

import baaahs.mapper.MappingSession
import baaahs.migrator.DataMigrator

object MappingSessionMigrator : DataMigrator<MappingSession>(MappingSession.serializer(), AllMappingSessionMigrations)

val AllMappingSessionMigrations = listOf<DataMigrator.Migration>(
    V1_EpochToIso8601
)