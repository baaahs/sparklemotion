package baaahs.show.migration

import baaahs.migrator.DataMigrator
import baaahs.migrator.mapObjsInDict
import baaahs.migrator.toJsonObj
import baaahs.migrator.type
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

@Suppress("ClassName")
object V5_FixFixtureInfoRefs : DataMigrator.Migration(5) {
    private val dataSourceTypeMap = mapOf(
        "baaahs.Core.FixtureInfo" to "baaahs.Core:FixtureInfo"
    )

    override fun migrate(from: JsonObject): JsonObject {
        return from.toMutableMap().apply {
            mapObjsInDict("dataSources") { _, dataSource ->
                val type = dataSource.type
                if (type != null) {
                    dataSourceTypeMap[type]?.let { dataSource["type"] = JsonPrimitive(it) }
                }
            }
        }.toJsonObj()
    }
}