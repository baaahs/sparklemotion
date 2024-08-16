package baaahs.sm.server

import baaahs.rpc.Service
import kotlinx.serialization.Serializable

interface AiAssistantService : AiAssistantCommands {
    fun start() {}
}

object NoOpAiAssistantService : AiAssistantService {
    override suspend fun listModels(): List<AiAssistantModel> = emptyList()
    override suspend fun regenerateGlsl(modelId: String?, baseProgram: String, request: String): AiAssistantResponse =
        AiAssistantResponse(baseProgram, "Done!", "Did it!", true)
}

@Serializable
data class AiAssistantModel(
    val id: String,
    val name: String,
    val description: String
)

@Serializable
data class AiAssistantResponse(
    val updatedSource: String,
    val responseMessage: String,
    val details: String,
    val success: Boolean
)

@Service
interface AiAssistantCommands {
    suspend fun listModels(): List<AiAssistantModel>
    suspend fun regenerateGlsl(
        modelId: String?,
        baseProgram: String,
        request: String
    ): AiAssistantResponse

    companion object {
        val IMPL = AiAssistantCommands.getImpl("pinky/aiAssistant")
    }
}