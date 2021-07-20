package baaahs.ui

import external.markdownit.MarkdownIt
import kotlinext.js.jsObject
import org.w3c.dom.HTMLElement
import react.RBuilder
import react.RHandler
import react.RProps
import react.child
import react.dom.span

val Markdown = xComponent<MarkdownProps>("Markdown", isPure = true) { props ->
    val mdRef = ref<HTMLElement>()
    val mdHtml = memo(props.children) {
        MarkdownIt(jsObject {
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

external interface MarkdownProps : RProps {
    var children : dynamic
}

fun RBuilder.markdown(handler: RHandler<MarkdownProps>) =
    child(Markdown, handler = handler)