package baaahs.sm.server

import baaahs.PubSub
import baaahs.gl.glsl.GlslType
import baaahs.plugin.OpenPlugin
import baaahs.plugin.PluginRef
import baaahs.plugin.Plugins
import baaahs.show.FeedBuilder
import baaahs.util.Logger
import baaahs.util.globalLaunch
import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.assistant.AssistantId
import com.aallam.openai.api.assistant.AssistantRequest
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
    pubSub: PubSub.Server,
    private val plugins: Plugins
) : AiAssistantService {
    private lateinit var openAi: OpenAI
    private var assistantId: AssistantId = AssistantId("invalid")

    init {
        AiAssistantCommands.IMPL.Receiver(pubSub, this)
    }

    override fun start() {
        val propsFile = File("openai.properties")
        val props = Properties().apply { load(propsFile.reader()) }
        val key = props.getProperty("key")
        assistantId = AssistantId(props.getProperty("assistant"))
        logger.info { "key: $key assistant: $assistantId" }

        openAi = OpenAI(
            token = key,
            timeout = Timeout(socket = 60.seconds),
            // additional configurations...
        )

        val assistantRequest = generateAssistantRequest()
        logger.info { "assistantRequest: ${assistantRequest.name}" }
        logger.info { "  instructions:\n${assistantRequest.instructions}" }
        logger.error { openAi.toString() }
        globalLaunch {
            val models = openAi.models()
            logger.error { "openai = $models" }
            models.forEach { println(it) }

            // TODO: What's the intended lifecycle of assistants?
            val assistant = openAi.assistant(
                assistantId,
                assistantRequest.also {
                    logger.info { "assistant = $it" }
                }
            )
        }
    }

    internal fun generateAssistantRequest(): AssistantRequest {
        val instructions = buildString {
            appendLine(
                """
                    You are "Sparkle Motion shader assistant," designed to help an artist create and edit GLSL shaders for
                    use in Sparkle Motion, an open-source light show authoring environment.
        
                    When responding:
                    1. Shaders must be written in GLSL and compatible with Sparkle Motion's requirements.
                    2. Return the response in this strict JSON format:
                    ```json
                    {
                        "updatedSource": "[full updated source goes here]",
                        "responseMessage": "[brief description of the changes or additions goes here]",
                        "details": "[more detailed explanation of the changes and how they work goes here]",
                        "success": true
                    }
                    ```
                    3. Ensure the output is valid JSON with all strings properly escaped.
                    4. Sparkle Motion makes it easy to pass in data using comment-annotated uniforms. For example, you can
                       use the following code to create a slider:
                    ```glsl
                    uniform float gristleThrob; // @@Slider min=0 max=1 default=.2
                    ```
                       Here's a full list of input types:
                    ```glsl
                """.trimIndent()
            )
            append(generateFeedExamples())
            append("```")
        }
        return AssistantRequest(
            name = "Sparkle Motion shader assistant",
            instructions = instructions
        )
    }

    private fun generateFeedExamples() =
        plugins.feedBuilders.withPlugin
            .filterNot { (_, b) -> b.internalOnly }
            .sortedBy { (_, b) -> b.title }
            .joinToString("\n") { (plugin, builder) ->
                generateFeedExample(plugin, builder)
            }

    internal fun generateFeedExample(plugin: OpenPlugin, feedBuilder: FeedBuilder<*>): String = buildString {
        val title = feedBuilder.title
        val description = feedBuilder.description
        val contentType = feedBuilder.contentType
        val type = contentType.glslType
        val pluginRef = PluginRef(plugin.packageName, feedBuilder.resourceName)

        val feedName = "${feedBuilder.resourceName.replaceFirstChar { it.lowercase() }}Data"

        append("/* $title — $description Returns ${contentType.title} as ")
        if (type is GlslType.Struct) {
            append("struct ${type.name} {\n")

            type.fields.forEach { field ->
                val typeStr = if (field.type is GlslType.Struct) field.type.name else field.type.glslLiteral
                append("    $typeStr ${field.name};")
                val comment = if (field.deprecated) "Deprecated. ${field.description}" else field.description
                comment?.run { append(" // $comment") }
                append("\n")
            }
            append("};\n")
        } else {
            append(type.glslLiteral)
        }
        if (feedBuilder.isFunctionFeed) {
            append("\nNote that this is declared and invoked as a function.")
        }
        append(" */\n")

        appendLine("${feedBuilder.exampleDeclaration(feedName)} // @@${pluginRef.shortRef()}")
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
                assistantId,
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
                                append("```\nuniform float value; // @@Slider min=0 max=1 default=.2\n```\n")
                                append("XY Pad (2-dimensional slider):")
                                append("```\nuniform vec2 value; // @@XyPad min=[0,0] max=[1,1] default=[.5,.5]\n```\n")
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