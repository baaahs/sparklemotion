package baaahs.app.ui.model

import baaahs.app.ui.CommonIcons
import baaahs.app.ui.appContext
import baaahs.app.ui.editor.betterSelect
import baaahs.model.ConstEntityMetadataProvider
import baaahs.model.StrandCountEntityMetadataProvider
import baaahs.scene.EditingEntity
import baaahs.scene.MutableImportedEntityGroup
import baaahs.ui.*
import js.core.jso
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import materialui.icon
import mui.material.*
import react.*
import react.dom.*
import react.dom.events.FormEvent

private val ObjGroupEditorView = xComponent<ObjGroupEditorProps>("ObjGroupEditor") { props ->
    val appContext = useContext(appContext)
    val styles = appContext.allStyles.modelEditor

    val json = memo {
        Json {
            isLenient = true
            prettyPrint = true
            serializersModule = appContext.plugins.serialModule
        }
    }

    observe(props.editingEntity)
    val mutableEntity = props.editingEntity.mutableEntity

    val handleIsFileClick by changeEventHandler(mutableEntity, props.editingEntity) {
        mutableEntity.objDataIsFileRef = it.target.checked
        props.editingEntity.onChange()
    }

    val handleObjDataChange by formEventHandler(mutableEntity, props.editingEntity) {
        mutableEntity.objData = it.target.value
        props.editingEntity.onChange()
    }

    val handleMetadataTypeChange by handler(mutableEntity, props.editingEntity) { value: String? ->
        mutableEntity.metadata = when (value) {
            "Constant" -> ConstEntityMetadataProvider(null)
            "Per Entity" -> StrandCountEntityMetadataProvider(emptyMap())
            null -> null
            else -> error("Unknown metadata type $value.")
        }
        props.editingEntity.onChange()
    }

    val handleConstMetadataChange by handler(mutableEntity, props.editingEntity) { newValue: Int? ->
        mutableEntity.metadata = ConstEntityMetadataProvider(newValue)
        props.editingEntity.onChange()
    }

    var metadataError by state<String?> { null }
    val handleStrandCountMetadataChange by formEventHandler(
        mutableEntity,
        props.editingEntity
    ) { event: FormEvent<*>? ->
        try {
            mutableEntity.metadata =
                StrandCountEntityMetadataProvider(
                    json.decodeFromString(
                        MapSerializer(String.serializer(), Int.serializer()),
                        event!!.target.value
                    )
                )
            metadataError = null
        } catch (e: Exception) {
            metadataError = e.message
        }
        props.editingEntity.onChange()
    }

    val handleReloadClick by mouseEventHandler(mutableEntity) {
        mutableEntity.reloadFile()
        forceRender()
    }

    header { +"OBJ Import" }

    Container {
        attrs.classes = jso { this.root = -styles.propertiesEditSection }
        FormControlLabel {
            attrs.control = buildElement {
                Switch {
                    attrs.checked = mutableEntity.objDataIsFileRef
                    attrs.onChange = handleIsFileClick.withTChangeEvent()
                }
            }
            attrs.label = buildElement { +"From File" }
        }

        if (mutableEntity.objDataIsFileRef) {
            IconButton {
                attrs.onClick = handleReloadClick
                attrs.title = "Reload"
                icon(CommonIcons.Reload)
            }
        }

        br {}
        if (mutableEntity.objDataIsFileRef) {
            TextField {
                attrs.fullWidth = true
                attrs.onChange = handleObjDataChange
                attrs.value = mutableEntity.objData
                attrs.label = buildElement { +"File" }
            }
        } else {
            TextField {
                attrs.classes = jso { this.root = -styles.jsonEditorTextField }
                attrs.fullWidth = true
                attrs.multiline = true
                attrs.rows = 6
                attrs.onChange = handleObjDataChange
                attrs.value = mutableEntity.objData
                attrs.label = buildElement { +"OBJ Data" }
            }
        }

        Container {
            if (mutableEntity.problems.isEmpty()) {
                +"Imported ${mutableEntity.children.size} surfaces."
            } else {
                header { +"Problems Importing…" }
                ul {
                    mutableEntity.problems.forEach {
                        li { +(it.message ?: "Unknown problem.") }
                    }
                }
            }
        }
    }

    header { +"Metadata" }

    val metadata = mutableEntity.metadata
    Container {
        betterSelect<String?> {
            attrs.label = "Adapter"
            attrs.values = listOf(null, "Constant", "Per Entity")
            attrs.renderValueOption = { adapter -> buildElement { +(adapter ?: "None" ) } }
            attrs.value = mutableEntity.metadata?.let {
                when (it) {
                    is ConstEntityMetadataProvider -> "Constant"
                    is StrandCountEntityMetadataProvider -> "Per Entity"
                }
            }
            attrs.onChange = handleMetadataTypeChange
        }

        when (metadata) {
            is ConstEntityMetadataProvider -> {
                with(styles) {
                    numberTextField<Int?> {
                        this.attrs.label = "Expected Pixels:"
                        this.attrs.value = metadata.pixelCount
                        this.attrs.onChange = handleConstMetadataChange
                    }
                }
            }
            is StrandCountEntityMetadataProvider -> {
                TextField {
                    attrs.classes = jso { this.root = -styles.jsonEditorTextField }
                    attrs.fullWidth = true
                    attrs.multiline = true
                    attrs.rows = 6
                    attrs.defaultValue =
                        json.encodeToString(
                            MapSerializer(String.serializer(), Int.serializer()),
                            metadata.data
                        )
                    attrs.error = metadataError != null
                    attrs.label = "Pixel counts:".asTextNode()
                    attrs.onChange = handleStrandCountMetadataChange
                }
            }
            null -> {}
            else -> error("Unknown metadata type ${metadata::class}.")
        }
    }
}

external interface ObjGroupEditorProps : Props {
    var editingEntity: EditingEntity<out MutableImportedEntityGroup>
}

fun RBuilder.objGroupEditor(handler: RHandler<ObjGroupEditorProps>) =
    child(ObjGroupEditorView, handler = handler)