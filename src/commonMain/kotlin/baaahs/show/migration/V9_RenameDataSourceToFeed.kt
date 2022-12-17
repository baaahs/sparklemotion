package baaahs.show.migration

import baaahs.show.DataMigrator
import kotlinx.serialization.json.*

/**
 * Old "data sources" are now "feeds".
 */
@Suppress("ClassName")
object V9_RenameDataSourceToFeed : DataMigrator.Migration(9) {
    override fun migrate(from: JsonObject): JsonObject {
        return from.edit {
            rename("dataSources", "feeds")

            replaceMapValues("controls") { _, control ->
                control.jsonObject.edit {
                    rename("controlledDataSourceId", "controlledFeedId")
                }
            }

            replaceMapValues("patches") { _, patch ->
                patch.jsonObject.edit {
                    replaceMapValues("incomingLinks") { _, incomingLink ->
                        incomingLink.jsonObject.edit {
                            if (this["type"]?.jsonPrimitive?.contentOrNull == "datasource") {
                                put("type", JsonPrimitive("feed"))
                                rename("dataSourceId", "feedId")
                            }
                        }
                    }
                }
            }
        }.also {
            println("migrated to 9: $it")
        }
    }
}