@file:UseContextualSerialization(Feed::class)

package baaahs.show

import baaahs.app.ui.editor.Editable
import baaahs.camelize
import baaahs.control.MutableVisualizerControl
import baaahs.device.FixtureType
import baaahs.device.PixelLocationFeed
import baaahs.fixtures.Fixture
import baaahs.getBang
import baaahs.plugin.Plugins
import baaahs.plugin.core.feed.ModelInfoFeed
import baaahs.show.mutable.*
import kotlinx.serialization.*
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement

@Serializable
data class Show(
    override val title: String,
    override val patchIds: List<String> = emptyList(),
    override val eventBindings: List<EventBinding> = emptyList(),
    @Deprecated("Only used for legacy layout.")
    override val controlLayout: Map<String, List<String>> = emptyMap(),
    val layouts: Layouts = Layouts(),
    val shaders: Map<String, Shader> = emptyMap(),
    val patches: Map<String, Patch> = emptyMap(),
    val controls: Map<String, Control> = emptyMap(),
    val feeds: Map<String, Feed> = emptyMap()
) : PatchHolder, Editable {
    init {
        validatePatchHolder()
    }

    fun toJson(json: Json): JsonElement {
        return json.encodeToJsonElement(serializer(), this)
    }

    fun edit(): MutableShow = MutableShow(this)

    fun findImplicitControls(): Map<String, Control> = buildMap {
        val implicitControlsShowBuilder = ShowBuilder.forImplicitControls(controls, feeds)
        feeds
            .map { (_, feed) ->
                feed.buildControl()?.let { mutableControl ->
                    val control = mutableControl.buildControl(implicitControlsShowBuilder)
                    val id = implicitControlsShowBuilder.idFor(control)
                    put(id, control)
                }
            }
    }

    fun getControl(id: String): Control = controls.getBang(id, "control")

    fun getFeed(id: String): Feed = feeds.getBang(id, "feed")

    fun getShader(id: String): Shader = shaders.getBang(id, "shader")

    companion object {
        val EmptyShow = Show("Empty Show")
        val ShowTemplate = EmptyShow.edit().apply {
            layouts.formats["default"] = MutableLayout(null, mutableListOf(
                MutableGridTab("Main", columns = 6, rows = 6)
            ))
            addPatch(Shader("XY Projection", """
                // XY Projection

                struct ModelInfo {
                    vec3 center;
                    vec3 extents;
                };
                uniform ModelInfo modelInfo; // @@ModelInfo
                
                // @return uv-coordinate
                // @param pixelLocation xyz-coordinate
                vec2 main(vec3 pixelLocation) {
                    vec3 extents = modelInfo.extents;
                    vec3 pixelOffset = (pixelLocation - modelInfo.center) / extents + .5;
                    return vec2(pixelOffset.x, pixelOffset.y);
                }
            """.trimIndent()
            )) {
                link("pixelLocation", PixelLocationFeed())
                link("modelInfo", ModelInfoFeed())
            }
        }.build()

        fun fromJson(plugins: Plugins, s: String): Show {
            val json = Json { serializersModule = plugins.serialModule }
            return json.decodeFromString(serializer(), s)
        }
    }
}

@Serializable
data class Patch(
    val shaderId: String,
    val incomingLinks: Map<String, PortRef>,
    val stream: Stream = Stream.Main,
    val priority: Float = 0f

    // TODO: Fixture matcher (previously called "Surfaces") will eventually go here.
)

fun <T> List<T>.assertNoDuplicates(items: String = "items") {
    val duplicates = groupBy { it }.mapValues { (_, v) -> v.size }.filterValues { it > 1 }
    if (duplicates.isNotEmpty()) {
        error("duplicate $items: $duplicates")
    }
}

@Polymorphic
interface EventBinding

@Serializable
data class MidiChannelEventBinding(
    val channel: Int
) : EventBinding

@Serializable
data class Surfaces(
    val name: String,
    /** Set of [FixtureType]s that this object matches. If the set is empty, every FixtureType matches. */
    @Contextual
    val fixtureTypes: Set<FixtureType> = emptySet()
) {
    fun matches(fixture: Fixture): Boolean {
        return true
    }

    fun accept(visitor: MutableShowVisitor, log: VisitationLog = VisitationLog()) {
        if (log.surfaces.add(this)) visitor.visit(this)
    }

    companion object {
        val AllSurfaces = Surfaces("All Surfaces", emptySet())
    }
}

@Serializable
data class Shader(
    val title: String,
    /**language=glsl*/
    val src: String,
    val description: String? = null,
    val author: String? = null,
    val tags: List<Tag> = emptyList()
) {
    fun suggestId(): String = title.camelize()
}

@Serializable(with = TagSerializer::class)
data class Tag(
    val category: String = "",
    val value: String
) {
    val fullString get() =
        when {
            category == "" -> value
            value == "" -> "@$category"
            else -> "@$category=$value"
        }
    val minusString get() = "-$fullString"

    companion object {
        private val tagPattern = Regex("(?:@([^=]+)(=)?)?(.*)")

        fun fromString(fullTag: String): Tag {
            val match = tagPattern.matchEntire(fullTag)!!
            val (_, type, equals, value) = match.groupValues
            return when {
                type != "" && equals == "" -> Tag(type, "")
                type != "" -> Tag(type, value)
                else -> Tag("", value)
            }
        }
    }

}

object TagSerializer : KSerializer<Tag> {
    override val descriptor: SerialDescriptor
        get() = String.serializer().descriptor

    override fun serialize(encoder: Encoder, value: Tag) {
        String.serializer().serialize(encoder, value.fullString)
    }

    override fun deserialize(decoder: Decoder): Tag {
        return Tag.fromString(String.serializer().deserialize(decoder))
    }
}

@Serializable(with = Stream.Serializer::class)
data class Stream(val id: String) {
    fun toMutable(): MutableStream = MutableStream(id)

    companion object {
        val Main = Stream("main")
    }

    class Serializer : KSerializer<Stream> {
        override val descriptor: SerialDescriptor
            get() = PrimitiveSerialDescriptor(
                "id",
                PrimitiveKind.STRING
            )


        override fun deserialize(decoder: Decoder): Stream {
            return Stream(decoder.decodeString())
        }

        override fun serialize(encoder: Encoder, value: Stream) {
            encoder.encodeString(value.id)
        }
    }
}

fun buildEmptyShow(): Show {
    return MutableShow("Untitled").apply {
        editLayouts {
            editLayout("default") {
                tabs.add(MutableGridTab("Main").apply {
                    items.add(MutableGridItem(MutableVisualizerControl(), 9, 0, 3, 2))
                })
            }
        }
    }.getShow()
}
