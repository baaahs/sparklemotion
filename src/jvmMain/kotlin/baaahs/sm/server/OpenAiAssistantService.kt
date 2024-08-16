package baaahs.sm.server

import baaahs.PubSub
import baaahs.util.Logger
import baaahs.util.globalLaunch
import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.assistant.AssistantId
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.core.Status
import com.aallam.openai.api.http.Timeout
import com.aallam.openai.api.message.MessageContent
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.api.run.Run
import com.aallam.openai.api.run.ThreadRunRequest
import com.aallam.openai.api.thread.ThreadMessage
import com.aallam.openai.api.thread.ThreadRequest
import com.aallam.openai.client.OpenAI
import kotlinx.coroutines.delay
import kotlinx.serialization.json.Json
import java.io.File
import java.util.*
import kotlin.time.Duration.Companion.seconds

@OptIn(BetaOpenAI::class)
class OpenAiAssistantService(
    pubSub: PubSub.Server
) : AiAssistantService {
    private val propsFile = File("openai.properties")
    private val props = Properties().apply { load(propsFile.reader()) }
    private val assistant = props.getProperty("assistant")
    private val openAi = run {
        val key = props.getProperty("key")
        logger.info { "key: $key assistant: $assistant" }

        OpenAI(
            token = key,
            timeout = Timeout(socket = 60.seconds),
            // additional configurations...
        )
    }

    init {
        AiAssistantCommands.IMPL.Receiver(pubSub, this)
    }

    init {
        logger.error { openAi.toString() }
        globalLaunch {
            val models = openAi.models()
            logger.error { "openai = $models" }
            models.forEach { println(it) }

        }
    }

    override suspend fun listModels(): List<AiAssistantModel> =
        openAi.models().map {
            AiAssistantModel(it.id.id, it.id.id, it.ownedBy ?: "???")
        }

    override suspend fun regenerateGlsl(modelId: String?, baseProgram: String, request: String): AiAssistantResponse {
        val model = ModelId(modelId ?: "gpt-3.5-turbo")

//        val completion: ChatCompletion = openAi.chatCompletion(chatCompletionRequest)
        var run = openAi.createThreadRun(
            ThreadRunRequest(
                AssistantId(assistant),
                ThreadRequest(
                    listOf(
                        ThreadMessage(
                            role = ChatRole.User,
                            content = buildString {
                                append(request)
                                append("\n\nHere's original GLSL shader:\n\n```")
                                append(baseProgram)
                                append("\n```\n\n")
                                append("Preserve whitespace and comments in the shader.\n\n")
                                append("Sparkle Motion provides some GLSL extensions that you can use:\n")
                                append("Slider:\n")
                                append("`uniform float value; // @@Slider min=0 max=1 default=.2`\n")
                                append("XY Pad (2-dimensional slider):")
                                append("`uniform vec2 value; // @@XyPad min=[0,0] max=[1,1] default=[.5,.5]`\n")
                            }
                        )
                    )
                ), model
            )
                .also { logger.info { "ThreadRunRequest: $it" } }
        )

        while (run.isStillRunning()) {
            logger.info { "Waiting for run to complete: ${run.id}" }
            delay(500)
            run = openAi.getRun(threadId = run.threadId, runId = run.id)
        }

        val responses = openAi.messages(run.threadId)[0]

// or, as flow
//        val completions: Flow<ChatCompletionChunk> = openAI.chatCompletions(chatCompletionRequest)

        responses.content.forEach { logger.info { it.toString() } }
        val firstMessage = (responses.content.first() as? MessageContent.Text)?.text?.value
            ?: error("Huh? ${responses.content}")
        logger.warn { firstMessage }
        return Json.decodeFromString(AiAssistantResponse.serializer(), firstMessage)
    }

    private fun Run.isStillRunning() =
        status == Status.Queued ||
                status == Status.Running ||
                status == Status.InProgress ||
                status == Status.ValidatingFiles

    companion object {
        private val logger = Logger<OpenAiAssistantService>()
    }
}