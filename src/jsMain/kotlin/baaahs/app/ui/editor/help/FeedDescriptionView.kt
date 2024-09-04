package baaahs.app.ui.editor.help

import baaahs.app.ui.appContext
import baaahs.gl.glsl.GlslType
import baaahs.plugin.OpenPlugin
import baaahs.plugin.PluginRef
import baaahs.show.FeedBuilder
import baaahs.ui.unaryMinus
import baaahs.ui.unaryPlus
import baaahs.ui.xComponent
import js.objects.jso
import mui.material.Button
import react.Props
import react.RBuilder
import react.RHandler
import react.dom.code
import react.dom.pre
import react.dom.span
import react.useContext
import web.html.HTMLElement
import web.navigator.navigator

private val FeedDeclarationView = xComponent<FeedDeclarationsProps>("FeedDeclaration") { props ->
    val appContext = useContext(appContext)
    val styles = appContext.allStyles.shaderHelp

    val plugin = props.plugin
    val feedBuilder = props.feedBuilder
    val type = feedBuilder.contentType.glslType

    pre(+styles.code) {
        if (type is GlslType.Struct) {
            code { +"struct ${type.name} {\n" }

            type.fields.forEach { field ->
                val typeStr =
                    if (field.type is GlslType.Struct) field.type.name else field.type.glslLiteral
                val style = if (field.deprecated) styles.deprecated else styles.normal
                val comment =
                    if (field.deprecated) "Deprecated. ${field.description}" else field.description

                code {
                    +"    "
                    span(+style) { +"$typeStr ${field.name};" }
                    comment?.run { +" "; span(+styles.comment) { +"// $comment" } }
                    +"\n"
                }
            }
            code { +"};\n" }
        }
        val varName = feedBuilder.resourceName.replaceFirstChar { it.lowercase() }

        code {
            +feedBuilder.exampleDeclaration(varName)

            val pluginRef = PluginRef(plugin.packageName, feedBuilder.resourceName)
            span(+styles.comment) { +" // @@${pluginRef.shortRef()}" }
        }
    }

    Button {
        attrs.classes = jso { this.root = -styles.copyButton }
        attrs.onClick = { event ->
            val target = event.currentTarget as HTMLElement?
            val pre = target
                ?.parentElement
                ?.getElementsByTagName("pre")
                ?.get(0) as HTMLElement?
            pre?.innerText?.let {
                navigator.clipboard.writeText(it)
                target?.innerText = "Copied!"
            }
        }
        +"Copy…"
    }
}

external interface FeedDeclarationsProps : Props {
    var plugin: OpenPlugin
    var feedBuilder: FeedBuilder<*>
}

fun RBuilder.feedDeclaration(handler: RHandler<FeedDeclarationsProps>) =
    child(FeedDeclarationView, handler = handler)