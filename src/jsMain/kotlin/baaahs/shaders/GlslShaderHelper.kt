package baaahs.shaders

import baaahs.Brain
import baaahs.Surface
import info.laht.threekt.renderers.WebGLRenderer
import org.khronos.webgl.Float32Array
import org.khronos.webgl.WebGLRenderingContext
import org.khronos.webgl.WebGLRenderingContext.Companion.FRAGMENT_SHADER
import org.khronos.webgl.set
import org.w3c.dom.HTMLCanvasElement
import kotlin.browser.document

val helpers = mutableMapOf<Pair<String, Surface>, GlslShaderHelper>()

actual fun getShaderHelper(glslShader: GlslShader, surface: Surface): GlslShaderHelper =
    helpers.getOrPut(glslShader.glslShader to surface) { GlslShaderHelper(glslShader, surface) }

actual class GlslShaderHelper(private val glslShader: GlslShader, surface: Surface) {
    val renderer = WebGLRenderer()
    private val pixelVertices = (surface as? Brain.MappedSurface)?.pixelVertices

    init {
        val canvasEl = document.createElement("canvas") as HTMLCanvasElement
        val gl = canvasEl.getContext("webgl") as WebGLRenderingContext
        val program = gl.createProgram()
        val shader = gl.createShader(FRAGMENT_SHADER)!!
        gl.shaderSource(shader, glslShader.glslShader)
        gl.compileShader(shader)
        gl.attachShader(program, shader)
        gl.useProgram(program)

        val vertexPositionAttribute = gl.getAttribLocation(program, "v_position");
        val quad_vertex_buffer = gl.createBuffer();
        val uvBuffer = Float32Array(surface.pixelCount * 2)
        pixelVertices?.forEachIndexed { index, (u, v) ->
            uvBuffer[index * 2] = u
            uvBuffer[index * 2 + 1] = v
        }

//        gl.bindBuffer(gl.ARRAY_BUFFER, quad_vertex_buffer);
//        gl.bufferData(gl.ARRAY_BUFFER, uvBuffer, gl.STATIC_DRAW);
//        gl.vertexAttribPointer(vertexPositionAttribute, 3, gl.FLOAT, false, 0, 0);
//        gl.enableVertexAttribArray(vertexPositionAttribute)
//        gl.drawArrays(gl.TRIANGLES, 0, 6);


        renderer.setPixelRatio(1)
//        renderer.setSize(surface.pixelCount)
    }

}



/*
const vs = `
    attribute vec4 v_position;

    void main() {
      gl_Position = v_position;
    }
`;

const fs = `
    precision mediump float;

    void main() {
       gl_FragColor = vec4(0,1,0,1); // green
    }
`;

var gl = document.querySelector("canvas").getContext("webgl");
var shader_program = twgl.createProgram(gl, [vs, fs]);
gl.useProgram(shader_program);
var vertexPositionAttribute = gl.getAttribLocation(shader_program, "v_position");
var quad_vertex_buffer = gl.createBuffer();
var quad_vertex_buffer_data = new Float32Array([
    -1.0, -1.0, 0.0,
     1.0, -1.0, 0.0,
    -1.0,  1.0, 0.0,
    -1.0,  1.0, 0.0,
     1.0, -1.0, 0.0,
     1.0,  1.0, 0.0]);
gl.bindBuffer(gl.ARRAY_BUFFER, quad_vertex_buffer);
gl.bufferData(gl.ARRAY_BUFFER, quad_vertex_buffer_data, gl.STATIC_DRAW);
gl.vertexAttribPointer(vertexPositionAttribute, 3, gl.FLOAT, false, 0, 0);
gl.enableVertexAttribArray(vertexPositionAttribute)
gl.drawArrays(gl.TRIANGLES, 0, 6);*/