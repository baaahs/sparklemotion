package external.dagre_d3

@JsModule("d3")
external val d3: D3

external class D3 {
    val curveBasis: dynamic
    val event: dynamic
    val zoomIdentity: dynamic

    fun select(arg: dynamic): dynamic
    fun transition(): dynamic
    fun zoom(): dynamic
}