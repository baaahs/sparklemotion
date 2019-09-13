package baaahs.glsl

expect fun glFinish()

expect fun glDrawArrays(mode: Int, first: Int, count: Int)

expect fun glReadPixels(x: Int, y: Int, width: Int, height: Int, format: Int, type: Int, pixels: Any?)

expect fun glTexImage2D(
    target: Int,
    level: Int,
    internalformat: Int,
    width: Int,
    height: Int,
    border: Int,
    format: Int,
    type: Int,
    pixels: Any
)

val GL_RED = 0x1903
val GL_R32F = 0x822E
