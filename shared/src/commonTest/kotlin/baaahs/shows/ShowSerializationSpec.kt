package baaahs.shows

import baaahs.control.*
import baaahs.device.PixelLocationFeed
import baaahs.gl.data.FeedContext
import baaahs.gl.glsl.GlslType
import baaahs.gl.kexpect
import baaahs.gl.override
import baaahs.gl.patch.ContentType
import baaahs.gl.shader.InputPort
import baaahs.glsl.Shaders
import baaahs.kotest.value
import baaahs.plugin.*
import baaahs.plugin.beatlink.BeatLinkControl
import baaahs.plugin.beatlink.BeatLinkFeed
import baaahs.plugin.beatlink.BeatLinkPlugin
import baaahs.plugin.core.FixtureInfoFeed
import baaahs.plugin.core.feed.*
import baaahs.show.*
import baaahs.show.mutable.MutableFeedPort
import baaahs.show.mutable.MutableGridTab
import baaahs.show.mutable.MutableShow
import baaahs.sm.server.PinkyArgs
import baaahs.toEqual
import ch.tutteli.atrium.api.verbs.expect
import io.kotest.core.spec.style.DescribeSpec
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.json.*

@Suppress("unused")
object ShowSerializationSpec : DescribeSpec({
    describe("Show serialization") {
        val plugins by value<Plugins> { TestSampleData.plugins }
        val jsonWithDefaults by value { Json(plugins.json) { encodeDefaults = true } }
        val jsonPrettyPrint by value { Json(plugins.json) { prettyPrint = true } }
        val origShow by value { TestSampleData.sampleShowWithBeatLink }
        val showJson by value { origShow.toJson(jsonWithDefaults) }

        it("serializes as expected") {
            plugins.expectJson(forJson(origShow)) { showJson }
        }

        it("deserializes to an equivalent object") {
            plugins.expectJson(forJson(origShow)) {
                val jsonStr = jsonPrettyPrint.encodeToString(JsonElement.serializer(), showJson)
                forJson(Show.fromJson(plugins, jsonStr))
            }
        }

        context("referencing an unresolvable feed") {
            val fakePluginBuilder by value {
                FakePlugin.Builder("some.plugin", listOf(FakeFeed.Builder))
            }
            override(origShow) {
                MutableShow("Untitled").apply {
                    val show = this
                    editLayouts {
                        editLayout("default") {
                            tabs.add(MutableGridTab("Main").apply {
                                addButton("Button", 0, 0, mutableShow = show) {
                                    addPatch(Shaders.red) {
                                        link("somePort", FakeFeed("foo"))
                                    }
                                }
                            })
                        }
                    }
                }.build()
            }
            override(showJson) { origShow.toJson(buildPlugins(fakePluginBuilder).json) }

            it("serializes as expected") {
                plugins.expectJson(buildJsonObject {
                    put("somePluginFeed", buildJsonObject {
                        put("type", "some.plugin:Fake")
                        put("whateverValue", "foo")
                    })
                }) { showJson.jsonObject["feeds"]!! }
            }

            context("when the plugin is unknown") {
                it("deserializes to an UnknownFeed") {
                    val show = Show.fromJson(plugins, showJson.toString())
                    val feed = show.feeds["somePluginFeed"]!!
                    expect(feed).toEqual(
                        UnknownFeed(
                            PluginRef("some.plugin", "Fake"),
                            "Unknown plugin \"some.plugin\".",
                            ContentType.Unknown,
                            buildJsonObject {
                                put("type", "some.plugin:Fake")
                                put("whateverValue", "foo")
                            }
                        )
                    )
                }

                it("reserializes to equivalent JSON") {
                    plugins.expectJson(forJson(origShow)) {
                        val jsonStr = jsonPrettyPrint.encodeToString(JsonElement.serializer(), showJson)
                        forJson(Show.fromJson(plugins, jsonStr))
                    }
                }
            }

            context("when the feed is unknown") {
                override(plugins) { buildPlugins(FakePlugin.Builder("some.plugin")) }

                it("deserializes to an UnknownFeed") {
                    val show = Show.fromJson(plugins, showJson.toString())
                    val feed = show.feeds["somePluginFeed"]!!
                    expect(feed).toEqual(
                        UnknownFeed(
                            PluginRef("some.plugin", "Fake"),
                            "Unknown feed \"some.plugin:Fake\".",
                            ContentType.Unknown,
                            buildJsonObject {
                                put("type", "some.plugin:Fake")
                                put("whateverValue", "foo")
                            }
                        )
                    )
                }
            }
        }
    }
})

private fun buildPlugins(fakePlugin: FakePlugin.Builder) =
    Plugins.buildForServer(Plugins.dummyContext, listOf(fakePlugin), PinkyArgs.defaults)

private fun JsonObjectBuilder.mapTo(k: String, v: JsonElement) = put(k, v)

private fun JsonObjectBuilder.addPatchHolder(patchHolder: PatchHolder) {
    put("patchIds", patchHolder.patchIds.jsonMap { JsonPrimitive(it) })
    put("eventBindings", patchHolder.eventBindings.jsonMap { jsonFor(it) })
    put("controlLayout", patchHolder.controlLayout.jsonMap { it.jsonMap { JsonPrimitive(it) } })
}

private fun <V> Map<String, V>.jsonMap(block: JsonObjectBuilder.(V) -> JsonElement): JsonObject {
    return buildJsonObject { entries.forEach { (k, v) -> put(k, block(v)) } }
}

private fun <T> List<T>.jsonMap(block: (T) -> JsonElement): JsonArray {
    return buildJsonArray { forEach { add(block(it)) } }
}

private fun forJson(show: Show): JsonObject {
    return buildJsonObject {
        put("title", show.title)
        addPatchHolder(show)
        put("layouts", buildJsonObject {
            put("panels", buildJsonObject {
                show.layouts.panels.forEach { (title, info) ->
                    put(title, jsonFor(info))
                }
            })
            put("formats", show.layouts.formats.jsonMap {
                buildJsonObject {
                    put("mediaQuery", it.mediaQuery)
                    put("tabs", it.tabs.jsonMap { jsonFor(it as GridTab) })
                }
            })
        })
        put("shaders", show.shaders.jsonMap { jsonFor(it) })
        put("patches", show.patches.jsonMap { jsonFor(it) })
        put("controls", show.controls.jsonMap { jsonFor(it) })
        put("feeds", show.feeds.jsonMap { jsonFor(it) })
    }
}

private fun jsonFor(eventBinding: EventBinding) = buildJsonObject { }

fun jsonFor(panel: Panel): JsonElement {
    return buildJsonObject {
        put("title", panel.title)
    }
}

fun jsonFor(gridTab: GridTab): JsonElement = buildJsonObject {
    put("type", "Grid")
    put("title", gridTab.title)
    put("columns", gridTab.columns)
    put("rows", gridTab.rows)
    put("items", gridTab.items.jsonMap { jsonFor(it) })
}

fun jsonFor(gridLayout: GridLayout?): JsonElement = if (gridLayout == null) JsonNull else buildJsonObject {
    put("columns", gridLayout.columns)
    put("rows", gridLayout.rows)
    put("matchParent", gridLayout.matchParent)
    put("items", gridLayout.items.jsonMap { jsonFor(it) })
}

fun jsonFor(gridItem: GridItem?): JsonElement = if (gridItem == null) JsonNull else buildJsonObject {
    put("controlId", gridItem.controlId)
    put("column", gridItem.column)
    put("row", gridItem.row)
    put("width", gridItem.width)
    put("height", gridItem.height)
    put("layout", jsonFor(gridItem.layout))
}

fun jsonFor(control: Control): JsonElement {
    return when (control) {
        is BeatLinkControl -> buildJsonObject {
            put("type", "baaahs.BeatLink:BeatLink")
        }
        is ButtonControl -> buildJsonObject {
            put("type", "baaahs.Core:Button")
            put("title", control.title)
            put("activationType", control.activationType.name)
            addPatchHolder(control)
            put("controlledFeedId", control.controlledFeedId)
        }
        is ButtonGroupControl -> buildJsonObject {
            put("type", "baaahs.Core:ButtonGroup")
            put("title", control.title)
            put("direction", control.direction.name)
            put("showTitle", control.showTitle)
            put("allowMultiple", control.allowMultiple)
            put("buttonIds", control.buttonIds.jsonMap { JsonPrimitive(it) })
        }
        is ColorPickerControl -> buildJsonObject {
            put("type", "baaahs.Core:ColorPicker")
            put("title", control.title)
            put("initialValue", control.initialValue.toInt())
            put("controlledFeedId", control.controlledFeedId)
        }
        is SliderControl -> buildJsonObject {
            put("type", "baaahs.Core:Slider")
            put("title", control.title)
            put("initialValue", control.initialValue)
            put("minValue", control.minValue)
            put("maxValue", control.maxValue)
            put("stepValue", control.stepValue)
            put("controlledFeedId", control.controlledFeedId)
            put("eventBindings", control.eventBindings.jsonMap { jsonFor(it) })
        }
        is VisualizerControl -> buildJsonObject {
            put("type", "baaahs.Core:Visualizer")
            put("surfaceDisplayMode", "Continuous")
            put("rotate", false)
        }
        is VacuityControl -> buildJsonObject {
            put("type", "baaahs.Core:Vacuity")
            put("title", "Vacuity")
        }
        else -> buildJsonObject { put("type", "unknown") }
    }
}

fun jsonFor(feed: Feed): JsonElement {
    return when (feed) {
        is SliderFeed -> {
            buildJsonObject {
                put("type", "baaahs.Core:Slider")
                put("title", feed.sliderTitle)
                put("initialValue", feed.initialValue)
                put("minValue", feed.minValue)
                put("maxValue", feed.maxValue)
                put("stepValue", feed.stepValue)
            }
        }
        is ColorPickerFeed -> {
            buildJsonObject {
                put("type", "baaahs.Core:ColorPicker")
                put("title", feed.colorPickerTitle)
                put("initialValue", feed.initialValue.toInt())
            }
        }
        is ResolutionFeed -> {
            buildJsonObject {
                put("type", "baaahs.Core:Resolution")
            }
        }
        is TimeFeed -> {
            buildJsonObject {
                put("type", "baaahs.Core:Time")
            }
        }
        is PixelCoordsTextureFeed -> {
            buildJsonObject {
                put("type", "baaahs.Core:PixelCoordsTexture")
            }
        }
        is PixelLocationFeed -> {
            buildJsonObject {
                put("type", "baaahs.Core:PixelLocation")
            }
        }
        is FixtureInfoFeed -> {
            buildJsonObject {
                put("type", "baaahs.Core:FixtureInfo")
            }
        }
        is ModelInfoFeed -> {
            buildJsonObject {
                put("type", "baaahs.Core:ModelInfo")
            }
        }
        is RasterCoordinateFeed -> {
            buildJsonObject {
                put("type", "baaahs.Core:RasterCoordinate")
            }
        }
        is BeatLinkFeed -> buildJsonObject {
            put("type", "baaahs.BeatLink:BeatLink")
        }
        else -> buildJsonObject { put("type", "unknown") }
    }
}

private fun jsonFor(portRef: PortRef): JsonObject {
    return when (portRef) {
        is FeedRef -> buildJsonObject {
            put("type", "feed")
            put("feedId", portRef.feedId)
        }
        is StreamRef -> buildJsonObject {
            put("type", "stream")
            put("stream", portRef.stream.id)
        }
        is OutputPortRef -> buildJsonObject {
            put("type", "output")
            put("portId", portRef.portId)
        }
        else -> error("huh? $portRef")
    }
}

private fun jsonFor(shader: Shader) = buildJsonObject {
    put("title", shader.title)
    put("src", shader.src)
}

private fun jsonFor(patch: Patch) = buildJsonObject {
    put("shaderId", patch.shaderId)
    put("incomingLinks", patch.incomingLinks.jsonMap { jsonFor(it) })
    put("stream", patch.stream.id)
    put("priority", patch.priority)
}

fun Plugins.expectJson(expected: JsonElement, block: () -> JsonElement) {
    val serialModule = serialModule
    val json = Json {
        prettyPrint = true
        serializersModule = serialModule
    }

    fun JsonElement.toStr() = json.encodeToString(JsonElement.serializer(), this)
    kexpect(block().toStr()).toBe(expected.toStr())
}

@Serializable
@SerialName("some.plugin:Fake")
class FakeFeed(
    @Suppress("unused")
    val whateverValue: String
) : Feed {
    @Transient
    override val pluginPackage = "some.plugin"

    @Transient
    override val title: String = "$pluginPackage Feed"

    @Transient
    override val contentType: ContentType = ContentType.Unknown

    override fun getType(): GlslType = TODO("not implemented")
    override fun open(feedOpenContext: FeedOpenContext, id: String): FeedContext = TODO("not implemented")

    object Builder : FeedBuilder<FakeFeed> {
        override val title: String get() = TODO("not implemented")
        override val description: String get() = TODO("not implemented")
        override val resourceName: String get() = "some.plugin:Fake"
        override val contentType: ContentType get() = ContentType.Unknown
        override val serializerRegistrar: SerializerRegistrar<FakeFeed>
            get() = classSerializer(serializer())

        override fun build(inputPort: InputPort): FakeFeed = TODO("not implemented")
    }
}

class FakePlugin(
    override val packageName: String,
    override val title: String,
    override val feedBuilders: List<FeedBuilder<out Feed>> = emptyList()
) : OpenServerPlugin {
    class Builder(
        override val id: String,
        private val feedBuilders: List<FeedBuilder<out Feed>> = emptyList()
    ) : Plugin {
        override fun openForServer(pluginContext: PluginContext): OpenServerPlugin =
            FakePlugin(id, "$id Plugin", feedBuilders)

        override fun openForClient(pluginContext: PluginContext): OpenClientPlugin =
            TODO("not implemented")
    }
}

object TestSampleData {
    val plugins = Plugins.buildForClient(Plugins.dummyContext, listOf(BeatLinkPlugin))
    private val beatLinkPlugin = plugins.getPlugin<BeatLinkPlugin>()

    val sampleShowWithBeatLink: Show
        get() = MutableShow(SampleData.sampleShow).apply {
            addPatch(
                Shader(
                    "BeatLink",
                    /**language=glsl*/
                    """
                    uniform float beat;
                    void main(void) {
                        gl_FragColor = vec4(beat, 0., 0., 1.);
                    }
                """.trimIndent()
                )
            ) {
                link("beat", MutableFeedPort(beatLinkPlugin.beatLinkFeed))
            }
        }.getShow()
}