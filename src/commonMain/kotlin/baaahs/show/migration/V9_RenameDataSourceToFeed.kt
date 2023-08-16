package baaahs.show.migration

import baaahs.show.DataMigrator
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonObject

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
                            if (this.type == "datasource") {
                                put("type", JsonPrimitive("feed"))
                                rename("dataSourceId", "feedId")
                            }
                        }
                    }
                }
            }
        }
    }
}