package baaahs.sm.webapi

import baaahs.app.ui.CommonIcons
import baaahs.io.Fs
import baaahs.libraries.ShaderLibrary
import baaahs.randomId
import baaahs.rpc.Service
import baaahs.ui.Icon
import kotlinx.serialization.Serializable

@Serializable
data class ClientData(
    val fsRoot: Fs.File
)

@Service
interface DocumentCommands<T> {
    suspend fun new(template: T? = null)
    suspend fun switchTo(file: Fs.File?)
    suspend fun save()
    suspend fun saveAs(file: Fs.File)
}

@Serializable
class SearchShaderLibraries(val terms: String) {
    @Serializable
    class Response(val matches: List<ShaderLibrary.Entry>)
}

@Serializable
data class Problem(
    val title: String,
    val message: String? = null,
    val severity: Severity = Severity.ERROR,
    val id: String = randomId("error")
)

fun Collection<Problem>.severity() = maxOfOrNull { it.severity }

enum class Severity(val icon: Icon) {
    INFO(CommonIcons.Info),
    WARN(CommonIcons.Warning),
    ERROR(CommonIcons.Error)
}