package baaahs.util

import baaahs.getBang
import baaahs.randomId
import kotlinx.serialization.Decoder
import kotlinx.serialization.Encoder
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialDescriptor
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.*
import kotlinx.serialization.modules.SerializersModule
import kotlin.reflect.KClass

interface Referable {
    fun suggestId(): String? = null
}

interface ReferableWithId : Referable {
    val id: String

    fun setId(id: String)
}

open class ReferableWithIdImpl : ReferableWithId {
    override val id: String
        get() = if (::idInternal.isInitialized) {
            idInternal
        } else {
            idInternal = randomId(suggestId() ?: "unknown")
            idInternal
        }

    lateinit var idInternal: String

    override fun setId(id: String) {
        idInternal = id
    }
}

class CanonicalizeReferables<T : Any>(
    private val tSerializer: KSerializer<T>,
    transformationName: String,
    private val referenceTypes: Collection<ReferenceType<*>>
) : KSerializer<T> {
    override val descriptor: SerialDescriptor = SerialDescriptor(
        "JsonTransformingSerializer<${tSerializer.descriptor.serialName}>($transformationName)",
        tSerializer.descriptor.kind
    )

    override fun serialize(encoder: Encoder, value: T) {
        val dictionaries = createDictionaries()
        val output = encoder.asJsonOutput()
        val json = wrapJson(output.json, dictionaries)
        val element = json.toJson(tSerializer, value)
        if (element !is JsonObject) error("Must be an object")

        val newTopLevelElement = json {
            element.forEach { (k, v) -> k to v }

            referenceTypes.forEach { referenceType ->
                referenceType.key to referenceType.values(json, dictionaries)
            }
        }

        output.encodeJson(newTopLevelElement)
    }

    override fun deserialize(decoder: Decoder): T {
        val dictionaries = createDictionaries()
        val input = decoder.asJsonInput()
        val objAndDictEntries = input.decodeJson()
        if (objAndDictEntries !is JsonObject) error("Must be an object")

        val refTypesByKey = referenceTypes.associateBy { it.key }

        val json = wrapJson(input.json, dictionaries)

        val newJsonObj = json {
            objAndDictEntries.forEach { (k, v) ->
                val referenceType = refTypesByKey[k]

                if (referenceType == null) {
                    k to v
                } else {
                    v as? JsonObject ?: throw IllegalStateException("Should be JSON object")
                    referenceType.putValues(v, json, dictionaries)
                }
            }
        }
        return json.fromJson(tSerializer, newJsonObj)
    }

    private fun createDictionaries(): MutableMap<String, Dictionary<*>> {
        return referenceTypes.associate { it.key to Dictionary<Nothing>(it.key) }.toMutableMap()
    }

    private fun wrapJson(baseJson: Json, dictionaries: MutableMap<String, Dictionary<*>>): Json {
        return Json(JsonConfiguration.Stable, SerializersModule {
            include(baseJson.context)
            referenceTypes.forEach {
                val dictionary = dictionaries.getBang(it.key, "dictionary")
                val serializer = ReferenceSerializer(it, dictionary)
                @Suppress("UNCHECKED_CAST")
                contextual(it.clazz as KClass<Any>, serializer as KSerializer<Any>)
            }
        })
    }


    private inner class ReferenceSerializer<T : Referable>(
        private val referenceType: ReferenceType<out Referable>,
        private val dictionary: Dictionary<T>
    ) : KSerializer<T> {
        override val descriptor: SerialDescriptor
            get() = String.serializer().descriptor

        override fun deserialize(decoder: Decoder): T {
            return dictionary.get(decoder.decodeString())
        }

        override fun serialize(encoder: Encoder, value: T) {
            encoder.encodeString(dictionary.idFor(value))
        }
    }

    private fun Encoder.asJsonOutput() = this as? JsonOutput
        ?: throw IllegalStateException(
            "This serializer can be used only with Json format." +
                    "Expected Encoder to be JsonOutput, got ${this::class}"
        )

    private fun Decoder.asJsonInput(): JsonInput = this as? JsonInput
        ?: throw IllegalStateException(
            "This serializer can be used only with Json format." +
                    "Expected Decoder to be JsonInput, got ${this::class}"
        )

    class ReferenceType<R : Referable>(
        val key: String,
        val clazz: KClass<R>,
        val serializer: KSerializer<R>
    ) {
        fun list(): MutableList<R> = mutableListOf()

        internal fun values(json: Json, dictionaries: Map<String, Dictionary<*>>): JsonObject {
            return json {
                @Suppress("UNCHECKED_CAST")
                val dictionary = getDictionary(dictionaries) as Dictionary<R>
                dictionary.all().forEach { (k: String, v: R) ->
                    k to json.toJson(serializer, v)
                }
            }
        }

        internal fun putValues(
            values: JsonObject,
            json: Json,
            dictionaries: MutableMap<String, Dictionary<*>>
        ) {
            values.forEach { (id, value) ->
                putValue(id, value, json, dictionaries)
            }
        }

        internal fun putValue(
            id: String,
            jsonElement: JsonElement,
            json: Json,
            dictionaries: MutableMap<String, Dictionary<*>>
        ) {
            @Suppress("UNCHECKED_CAST")
            val dictionary = getDictionary(dictionaries) as Dictionary<R>
            val referable = json.fromJson(serializer, jsonElement)
            if (referable is ReferableWithId) {
                referable.setId(id)
            }
            dictionary.add(id, referable)
        }

        private fun getDictionary(dictionaries: Map<String, Dictionary<*>>) =
            dictionaries.getBang(key, "dictionary")
    }
}

internal class Dictionary<T : Referable>(private val key: String) {
    private val toId = mutableMapOf<T, String>()
    private val keys = mutableMapOf<String, Counter>()
    private val nullIdCounter = Counter()

    private val byId = mutableMapOf<String, T>()

    fun all() = toId.entries.associate { (k, v) -> v to k }

    fun idFor(value: T): String {
        return toId.getOrPut(value) {
            val idPrefix = value.suggestId()
            val id = if (idPrefix.isNullOrEmpty()) {
                nullIdCounter.next()
            } else {
                val counter = keys.getOrPut(idPrefix) { Counter() }
                idPrefix + counter.affix()
            }
            id
        }
    }

    fun add(id: String, value: T) {
        byId[id] = value
    }

    fun get(id: String): T {
        return byId.getBang(id, key)
    }

    class Counter(var counter: Int = 0) {
        fun next(): String {
            return (counter++).toString()
        }

        fun affix(): String {
            val num = counter++
            return if (num == 0) "" else num.toString()
        }
    }
}
