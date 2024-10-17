package external.dagre_d3

@JsModule("dagre-d3")
external val dagreD3: DagreD3

external class DagreD3 {
    fun render(): Renderer
    fun layout(graph: Graph)
}
