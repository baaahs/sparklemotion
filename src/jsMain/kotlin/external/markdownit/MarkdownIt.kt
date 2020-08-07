package external.markdownit

@JsModule("markdown-it")
@JsName("MarkdownIt")
external class MarkdownIt(options: Options? = definedExternally) {
    fun render(s: String): String
}

external interface Options {
    var html: Boolean?
    var xhtmlOut: Boolean?
    var breaks: Boolean?
    var langPrefix: String?
    var linkify: Boolean?
    var typographer: Boolean?
    var quotes: String?
    var highlight: ((str: String, lang: String) -> String)?
}