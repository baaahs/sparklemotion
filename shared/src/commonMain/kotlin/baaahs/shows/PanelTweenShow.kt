//package baaahs.shows
//
//import baaahs.Color
//import baaahs.Show
//import baaahs.ShowContext
//import baaahs.gadgets.PalettePicker
//import baaahs.model.Model
// TODO: Reimplement using GLSL.
//object PanelTweenShow : Show("PanelTweenShow") {
//    override fun createRenderer(model: Model, showContext: ShowContext): Renderer {
//        val initialColors = listOf(
//            Color.from("#FF8A47"),
//            Color.from("#FC6170"),
//            Color.from("#8CEEEE"),
//            Color.from("#26BFBF"),
//            Color.from("#FFD747")
//        )
//
//        return object : Renderer {
//            val palettePicker = showContext.getGadget("palette", PalettePicker("Palette", initialColors))
//
////            val solidShader = SolidShader()
////
////            val shaderBuffers = showContext.allSurfaces.map { surface ->
////                showContext.getShaderBuffer(surface, solidShader)
////            }
////            val fadeTimeMs = 500
//
//            override fun nextFrame() {
////                val now = getTimeMillis().and(0xfffffff).toInt()
////                val colors = palettePicker.colors
////                shaderBuffers.forEachIndexed() { number, buf ->
////                    val colorIndex = (now / fadeTimeMs + number) % colors.size
////                    val startColor = colors[colorIndex]
////                    val endColor = colors[(colorIndex + 1) % colors.size]
////                    val tweenedColor = startColor.fade(endColor, (now % fadeTimeMs) / fadeTimeMs.toFloat())
////
////                    buf.color = tweenedColor
////                }
//            }
//        }
//    }
//}
