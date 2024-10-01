package baaahs.gl.glsl

interface State<S: State<S>> {
    fun visit(token: Token): S
}