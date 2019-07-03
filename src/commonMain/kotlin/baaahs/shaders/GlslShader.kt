package baaahs.shaders

import baaahs.*
import baaahs.io.ByteArrayReader
import baaahs.io.ByteArrayWriter
import kotlin.math.*

class GlslShader(val glslShader: String) : Shader<GlslShader.Buffer>(ShaderId.GLSL_SANDBOX_55301) {
    override fun createBuffer(surface: Surface): Buffer = Buffer()

    override fun readBuffer(reader: ByteArrayReader): Buffer = Buffer().apply { read(reader) }

    override fun serializeConfig(writer: ByteArrayWriter) {
        writer.writeString(glslShader)
    }

    override fun createRenderer(surface: Surface): Renderer = Renderer(surface, this)

    companion object : ShaderReader<GlslShader> {
        override fun parse(reader: ByteArrayReader) = GlslShader(reader.readString())
    }

    inner class Buffer : Shader.Buffer {
        override val shader: Shader<*>
            get() = this@GlslShader

        var color: Color = Color.WHITE
        var centerX: Float = 0.5f
        var centerY: Float = 0.5f
        var radius: Float = 0.75f

        override fun serialize(writer: ByteArrayWriter) {
            color.serialize(writer)
            writer.writeFloat(centerX)
            writer.writeFloat(centerY)
            writer.writeFloat(radius)
        }

        override fun read(reader: ByteArrayReader) {
            color = Color.parse(reader)
            centerX = reader.readFloat()
            centerY = reader.readFloat()
            radius = reader.readFloat()
        }
    }

    class Renderer(surface: Surface, glslShader: GlslShader) : Shader.Renderer<Buffer> {
        private val glslShaderHelper = getShaderHelper(glslShader, surface)
        private val pixelVertices = (surface as? Brain.MappedSurface)?.pixelVertices

        override fun draw(buffer: Buffer, pixelIndex: Int): Color {
            if (pixelVertices == null || pixelIndex >= pixelVertices.size) return Color.BLACK

            val (pixX, pixY) = pixelVertices[pixelIndex]
            val time = getTimeMillis().toDouble() / 1000.0
            val resolution = Vector2(1f, 1f)

//            return glslSandbox55306(Vector2(pixX, pixY), resolution, time)
            return glslSandbox55301(Vector2(pixX, pixY), resolution, time)
        }

        private fun glslSandbox55301(coord: Vector2, resolution: Vector2, time: Double): Color {
//            vec2 v=(gl_FragCoord.xy-(resolution*0.5))/min(resolution.y,resolution.x)*10.0;
            var v = coord - resolution.x * 0.5f

//            float t=time * 0.4,r=0.0;
            val t = time * 0.4
            var r = 0f
//            for (int i=0;i<N;i++){
//                float d=(3.14159265 / float(N))*(float(i)*5.0);
//                r+=length(vec2(v.x,v.y))+0.01;
//                v = vec2(v.x+cos(v.y+cos(r)+d)+cos(t),v.y-sin(v.x+cos(r)+d)+sin(t));
//            }
            val N = 6
            for (i in 0..5) {
                val d: Float = (3.14159265f / N) * (i * 5f)
                r += v.length + 0.01f
                v = Vector2(
                    (v.x + cos(v.y + cos(r) + d) + cos(t)).toFloat(),
                    (v.y - sin(v.x + cos(r) + d) + sin(t)).toFloat()
                )
            }
//            r = (sin(r*0.1)*0.5)+0.5;
            r = (sin(r * 0.1f) * 0.5f) + 0.5f;
//            r = pow(r, 128.0);
            r = r.pow(128f)
//            gl_FragColor = vec4(r,pow(max(r-0.75,0.0)*4.0,2.0),pow(max(r-0.875,0.0)*8.0,4.0), 1.0 );
            return Color(r, (max(r - 0.75f, 0.0f) * 4f).pow(2f), (max(r - 0.875f, 0.0f) * 8f).pow(4f))
        }

        private fun glslSandbox55306(
            coord: Vector2,
            resolution: Vector2,
            time: Double
        ): Color {
            //            val position = gl_FragCoord.xy / resolution.x - 0.5
            val position = coord / resolution.x - 0.5f
            //            float r = length(position);
            val r = position.length
            //            float a = atan(position.y, position.x);
            val a = atan(position.y / position.x)
            //            float t = time + 100.0/(r+1.0);
            val t = time + 100.0 / (r + 1f)
            //
            //            float light = 15.0*abs(0.05*(sin(t)+sin(time+a*8.0)));
            val light: Float = (15.0 * abs(0.05 * (sin(t) + sin(time + a * 8.0)))).toFloat()
            //            vec3 color = vec3(-sin(r*5.0-a-time+sin(r+t)), sin(r*3.0+a-cos(time)+sin(r+t)), cos(r+a*2.0+log(5.001-(a/4.0))+time)-sin(r+t));
            val color = Color.normalized(
                -sin(r * 5.0 - a - time + sin(r + t)),
                sin(r * 3.0 + a - cos(time) + sin(r + t)),
                cos(r + a * 2.0 + ln(5.001 - (a / 4.0)) + time) - sin(r + t)
            );
            //
            //            gl_FragColor = vec4((normalize(color)+0.9) * light , 1.0);
            return Color(
                (color.redF + 0.9f) * light,
                (color.greenF + 0.9f) * light,
                (color.blueF + 0.9f) * light,
                1f
            )
        }
    }
}

private fun Color.Companion.normalized(r: Double, g: Double, b: Double): Color {
    val length = sqrt(r * r + g * g + b * b)
    return Color((r / length).toFloat(), (g / length).toFloat(), (b / length).toFloat())
}

expect fun getShaderHelper(glslShader: GlslShader, surface: Surface): GlslShaderHelper
expect class GlslShaderHelper