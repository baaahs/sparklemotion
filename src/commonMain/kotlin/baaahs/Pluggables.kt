package baaahs

import kotlin.js.JsName

object Pluggables {
    @JsName("defaultModel")
    const val defaultModel = "BAAAHS"

    @JsName("loadModel")
    fun loadModel(name: String): Model<*> {
        return when (name) {
            "Decom2019" -> Decom2019Model().apply { load() }
            "SuiGeneris" -> SuiGenerisModel().apply { load() }
            "BAAAHS" -> SheepModel().apply { load() }
            else -> throw IllegalArgumentException("unknown model \"$name\"")
        }
    }
}