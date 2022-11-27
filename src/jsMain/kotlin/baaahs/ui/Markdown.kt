package baaahs.ui

import dom.html.HTMLElement
import external.markdownit.MarkdownIt
import kotlinx.js.jso
import react.Props
import react.RBuilder
import react.RHandler
import react.dom.span

private val MarkdownView = xComponent<MarkdownProps>("Markdown", isPure = true) { props ->
    val mdRef = ref<HTMLElement>()
    val mdHtml = memo(props.children) {
        MarkdownIt(jso {
            html = true
            linkify = true
            typographer = true
        }).render(props.children)
    }

    onMount(props.children) {
        mdRef.current!!.innerHTML = mdHtml
    }

    span {
        ref = mdRef
    }
}

external interface MarkdownProps : Props {
    var children : dynamic
}

fun RBuilder.markdown(handler: RHandler<MarkdownProps>) =
    child(MarkdownView, handler = handler)