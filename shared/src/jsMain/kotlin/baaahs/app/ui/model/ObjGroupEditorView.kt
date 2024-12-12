package baaahs.app.ui.model

import baaahs.app.ui.CommonIcons
import baaahs.app.ui.appContext
import baaahs.app.ui.editor.betterSelect
import baaahs.model.ConstEntityMetadataProvider
import baaahs.model.StrandCountEntityMetadataProvider
import baaahs.scene.EditingEntity
import baaahs.scene.MutableImportedEntityGroup
import baaahs.ui.asTextNode
import baaahs.ui.checked
import baaahs.ui.unaryMinus
import baaahs.ui.value
import baaahs.ui.withTChangeEvent
import baaahs.ui.xComponent
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import materialui.icon
import mui.material.Container
import mui.material.Typography
import mui.material.FormControlLabel
import mui.material.IconButton
import mui.material.Switch
import mui.material.TextField
import mui.system.sx
import react.Props
import react.RBuilder
import react.RHandler
import react.buildElement
import react.dom.br
import react.dom.events.FormEvent
import react.dom.header
import react.dom.li
import react.dom.onChange
import react.dom.ul
import react.useContext
import web.cssom.em

private val ObjGroupEditorView = xComponent<ObjGroupEditorProps>("ObjGroupEditor") { props ->
    val appContext = useContext(appContext)
    val editMode = observe(appContext.sceneManager.editMode)
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

    Container {
        attrs.className = -styles.propertiesEditSection
        FormControlLabel {
            attrs.control = buildElement {
                Switch {
                    attrs.disabled = editMode.isOff
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
                attrs.disabled = editMode.isOff
                attrs.onChange = handleObjDataChange
                attrs.value = mutableEntity.objData
                attrs.label = buildElement { +"File" }
            }
        } else {
            TextField {
                attrs.className = -styles.jsonEditorTextField
                attrs.fullWidth = true
                attrs.multiline = true
                attrs.rows = 6
                attrs.disabled = editMode.isOff
                attrs.onChange = handleObjDataChange
                attrs.value = mutableEntity.objData
                attrs.label = buildElement { +"OBJ Data" }
            }
        }

        Container {
            if (mutableEntity.problems.isEmpty()) {
                Typography {
                    +"Imported ${mutableEntity.children.size} surfaces."
                }
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
        attrs.sx { marginTop = 1.em}
        betterSelect<String?> {
            attrs.label = "Strategy"
            attrs.disabled = editMode.isOff
            attrs.values = listOf(null, "Constant", "Per Entity")
            attrs.renderValueOption = { adapter, _ -> buildElement { +(adapter ?: "None" ) } }
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
                        attrs.disabled = editMode.isOff
                        this.attrs.value = metadata.pixelCount
                        this.attrs.onChange = handleConstMetadataChange
                    }
                }
            }
            is StrandCountEntityMetadataProvider -> {
                TextField {
                    attrs.className = -styles.jsonEditorTextField
                    attrs.fullWidth = true
                    attrs.multiline = true
                    attrs.rows = 6
                    attrs.disabled = editMode.isOff
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
        }
    }
}

external interface ObjGroupEditorProps : Props {
    var editingEntity: EditingEntity<out MutableImportedEntityGroup>
}

fun RBuilder.objGroupEditor(handler: RHandler<ObjGroupEditorProps>) =
    child(ObjGroupEditorView, handler = handler)