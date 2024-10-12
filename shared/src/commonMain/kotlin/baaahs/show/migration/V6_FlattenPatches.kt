package baaahs.show.migration

import baaahs.migrator.DataMigrator
import baaahs.migrator.mapObjsInDict
import baaahs.migrator.toJsonObj
import kotlinx.serialization.json.*

/**
 * This migration simplifies things by merging patches and shader instances.
 *
 * In V5 shows:
 *
 * {
 *   // Control-level patches:
 *   "controls": {
 *     "aControl": { ...
 *       "patches": [
 *         { "shaderInstanceIds": ["a"], "surfaces": {...} }
 *       ]
 *     }
 *   },
 *   // Show-level patches:
 *   "patches": [
 *     { "shaderInstanceIds": ["b"], "surfaces": {...} }
 *   ],
 *   // ShaderInstance dict:
 *   "shaderInstanceIds": {
 *     "a": {...}
 *     "b": {...}
 *   }
 * }
 *
 * In V6 shows, Patches and ShaderInstances are merged:
 *
 * {
 *   // Control-level patches:
 *   "controls": [
 *     { ... "patchIds": ["a"] }
 *   ],
 *   // Show-level patches:
 *   "patchIds": ["b"],
 *   "patches": {
 *     "a": {..., "surfaces": {...} }
 *   }
 * }
 *
 * Since surfaces currently always match everything, we can safely drop it.
 */
@Suppress("ClassName")
object V6_FlattenPatches : DataMigrator.Migration(6) {
    private val dataSourceTypeMap = mapOf(
        "baaahs.Core.FixtureInfo" to "baaahs.Core:FixtureInfo"
    )

    override fun migrate(from: JsonObject): JsonObject {
        return from.toMutableMap().apply {
            // Convert patches found in the top-level Show:
            this.remove("patches")?.jsonArray?.let { showPatches ->
                this["patchIds"] = extractShaderInstanceIds(showPatches)
            }

            // Convert patches found in any Control:
            mapObjsInDict("controls") { _, control ->
                control.remove("patches")?.jsonArray?.let { controlPatches ->
                    control["patchIds"] = extractShaderInstanceIds(controlPatches)
                }
            }

            this.remove("shaderInstances")?.jsonObject?.let { shaderInstances ->
                this["patches"] = shaderInstances
            }
        }.toJsonObj()
    }

    private fun extractShaderInstanceIds(patches: JsonArray): JsonArray =
        buildJsonArray {
            patches.forEach { patch ->
                patch.jsonObject["shaderInstanceIds"]!!.jsonArray.forEach { shaderInstanceId ->
                    add(shaderInstanceId)
                }
            }
        }
}