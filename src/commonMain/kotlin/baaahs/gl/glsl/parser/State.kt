package baaahs.gl.glsl.parser

interface State<S: State<S>> {
    fun visit(token: Token): S
}