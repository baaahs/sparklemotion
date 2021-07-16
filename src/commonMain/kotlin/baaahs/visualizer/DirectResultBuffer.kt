package baaahs.visualizer

import baaahs.fixtures.IResultBuffer
import baaahs.gl.GlContext

expect class DirectResultBuffer(gl: GlContext, resultIndex: Int) : IResultBuffer
