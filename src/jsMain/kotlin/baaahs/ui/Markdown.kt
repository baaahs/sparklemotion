package baaahs.ui

import external.markdownit.MarkdownIt
import kotlinext.js.jsObject
import org.w3c.dom.HTMLElement
import react.RBuilder
import react.RHandler
import react.RProps
import react.child
import react.dom.span

val Markdown = xComponent<MarkdownProps>("Markdown") { props ->
    val mdRef = ref<HTMLElement>()

    onMount(props.children) {
        mdRef.current.innerHTML = MarkdownIt(jsObject {
            html = true
            linkify = true
            typographer = true
        }).render(props.children)
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