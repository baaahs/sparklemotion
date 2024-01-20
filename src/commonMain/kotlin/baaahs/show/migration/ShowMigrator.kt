package baaahs.show.migration

import baaahs.migrator.DataMigrator
import baaahs.show.Show

object ShowMigrator : DataMigrator<Show>(Show.serializer(), AllShowMigrations)