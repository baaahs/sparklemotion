package baaahs.show.migration

import baaahs.migrator.DataMigrator
import baaahs.migrator.mapObjsInDict
import baaahs.migrator.toJsonObj
import baaahs.migrator.type
import kotlinx.serialization.json.JsonObject

/**
 * Delete vestigial `varPrefix` key from `baaahs.Core:XyPad` feeds.
 */
@Suppress("ClassName")
object V10_RemoveVarPrefixFromXyPad : DataMigrator.Migration(10) {
    override fun migrate(from: JsonObject): JsonObject {
        return from.toMutableMap().apply {
            mapObjsInDict("feeds") { _, control ->
                println("from = $control")
                if (control.type == "baaahs.Core:XyPad") {
                    control.remove("varPrefix")
                }
            }
        }.toJsonObj()
    }
}