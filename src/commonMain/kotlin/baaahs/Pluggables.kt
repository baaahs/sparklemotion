package baaahs

import kotlin.js.JsName

object Pluggables {
    @JsName("defaultModel")
    const val defaultModel = "BAAAHS"

    @JsName("loadModel")
    fun loadModel(name: String): Model<*> {
        return when (name) {
            "Decom2019" -> Decom2019Model()
            "SuiGeneris" -> SuiGenerisModel()
            "BAAAHS" -> SheepModel()
            else -> throw IllegalArgumentException("unknown model \"$name\"")
        }.apply { load() }
    }
}