@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package three.js

import kotlin.js.*
import org.khronos.webgl.*
import org.w3c.dom.*
import org.w3c.dom.events.*
import org.w3c.dom.parsing.*
import org.w3c.dom.svg.*
import org.w3c.dom.url.*
import org.w3c.fetch.*
import org.w3c.files.*
import org.w3c.notifications.*
import org.w3c.performance.*
import org.w3c.workers.*
import org.w3c.xhr.*

external var REVISION: String

external enum class MOUSE {
    LEFT /* = 0 */,
    MIDDLE /* = 1 */,
    RIGHT /* = 2 */,
    ROTATE /* = 0 */,
    DOLLY /* = 1 */,
    PAN /* = 2 */
}

external enum class TOUCH {
    ROTATE /* = 0 */,
    PAN /* = 1 */,
    DOLLY_PAN /* = 2 */,
    DOLLY_ROTATE /* = 3 */
}

external var CullFaceNone: Number /* 0 */

external var CullFaceBack: Number /* 1 */

external var CullFaceFront: Number /* 2 */

external var CullFaceFrontBack: Number /* 3 */

external var BasicShadowMap: Number /* 0 */

external var PCFShadowMap: Number /* 1 */

external var PCFSoftShadowMap: Number /* 2 */

external var VSMShadowMap: Number /* 3 */

external var FrontSide: Number /* 0 */

external var BackSide: Number /* 1 */

external var DoubleSide: Number /* 2 */

external var NoBlending: Number /* 0 */

external var NormalBlending: Number /* 1 */

external var AdditiveBlending: Number /* 2 */

external var SubtractiveBlending: Number /* 3 */

external var MultiplyBlending: Number /* 4 */

external var CustomBlending: Number /* 5 */

external var AddEquation: Number /* 100 */

external var SubtractEquation: Number /* 101 */

external var ReverseSubtractEquation: Number /* 102 */

external var MinEquation: Number /* 103 */

external var MaxEquation: Number /* 104 */

external var ZeroFactor: Number /* 200 */

external var OneFactor: Number /* 201 */

external var SrcColorFactor: Number /* 202 */

external var OneMinusSrcColorFactor: Number /* 203 */

external var SrcAlphaFactor: Number /* 204 */

external var OneMinusSrcAlphaFactor: Number /* 205 */

external var DstAlphaFactor: Number /* 206 */

external var OneMinusDstAlphaFactor: Number /* 207 */

external var DstColorFactor: Number /* 208 */

external var OneMinusDstColorFactor: Number /* 209 */

external var SrcAlphaSaturateFactor: Number /* 210 */

external var ConstantColorFactor: Number /* 211 */

external var OneMinusConstantColorFactor: Number /* 212 */

external var ConstantAlphaFactor: Number /* 213 */

external var OneMinusConstantAlphaFactor: Number /* 214 */

external var NeverDepth: Number /* 0 */

external var AlwaysDepth: Number /* 1 */

external var LessDepth: Number /* 2 */

external var LessEqualDepth: Number /* 3 */

external var EqualDepth: Number /* 4 */

external var GreaterEqualDepth: Number /* 5 */

external var GreaterDepth: Number /* 6 */

external var NotEqualDepth: Number /* 7 */

external var MultiplyOperation: Number /* 0 */

external var MixOperation: Number /* 1 */

external var AddOperation: Number /* 2 */

external var NoToneMapping: Number /* 0 */

external var LinearToneMapping: Number /* 1 */

external var ReinhardToneMapping: Number /* 2 */

external var CineonToneMapping: Number /* 3 */

external var ACESFilmicToneMapping: Number /* 4 */

external var CustomToneMapping: Number /* 5 */

external var AgXToneMapping: Number /* 6 */

external var NeutralToneMapping: Number /* 7 */

external var AttachedBindMode: String /* "attached" */

external var DetachedBindMode: String /* "detached" */

external var UVMapping: Number /* 300 */

external var CubeReflectionMapping: Number /* 301 */

external var CubeRefractionMapping: Number /* 302 */

external var CubeUVReflectionMapping: Number /* 306 */

external var EquirectangularReflectionMapping: Number /* 303 */

external var EquirectangularRefractionMapping: Number /* 304 */

external var RepeatWrapping: Number /* 1000 */

external var ClampToEdgeWrapping: Number /* 1001 */

external var MirroredRepeatWrapping: Number /* 1002 */

external var NearestFilter: Number /* 1003 */

external var NearestMipmapNearestFilter: Number /* 1004 */

external var NearestMipMapNearestFilter: Number /* 1004 */

external var NearestMipmapLinearFilter: Number /* 1005 */

external var NearestMipMapLinearFilter: Number /* 1005 */

external var LinearFilter: Number /* 1006 */

external var LinearMipmapNearestFilter: Number /* 1007 */

external var LinearMipMapNearestFilter: Number /* 1007 */

external var LinearMipmapLinearFilter: Number /* 1008 */

external var LinearMipMapLinearFilter: Number /* 1008 */

external var UnsignedByteType: Number /* 1009 */

external var ByteType: Number /* 1010 */

external var ShortType: Number /* 1011 */

external var UnsignedShortType: Number /* 1012 */

external var IntType: Number /* 1013 */

external var UnsignedIntType: Number /* 1014 */

external var FloatType: Number /* 1015 */

external var HalfFloatType: Number /* 1016 */

external var UnsignedShort4444Type: Number /* 1017 */

external var UnsignedShort5551Type: Number /* 1018 */

external var UnsignedInt248Type: Number /* 1020 */

external var UnsignedInt5999Type: Number /* 35902 */

external var AlphaFormat: Number /* 1021 */

external var RGBFormat: Number /* 1022 */

external var RGBAFormat: Number /* 1023 */

external var LuminanceFormat: Number /* 1024 */

external var LuminanceAlphaFormat: Number /* 1025 */

external var DepthFormat: Number /* 1026 */

external var DepthStencilFormat: Number /* 1027 */

external var RedFormat: Number /* 1028 */

external var RedIntegerFormat: Number /* 1029 */

external var RGFormat: Number /* 1030 */

external var RGIntegerFormat: Number /* 1031 */

external var RGBIntegerFormat: Number /* 1032 */

external var RGBAIntegerFormat: Number /* 1033 */

external var RGB_S3TC_DXT1_Format: Number /* 33776 */

external var RGBA_S3TC_DXT1_Format: Number /* 33777 */

external var RGBA_S3TC_DXT3_Format: Number /* 33778 */

external var RGBA_S3TC_DXT5_Format: Number /* 33779 */

external var RGB_PVRTC_4BPPV1_Format: Number /* 35840 */

external var RGB_PVRTC_2BPPV1_Format: Number /* 35841 */

external var RGBA_PVRTC_4BPPV1_Format: Number /* 35842 */

external var RGBA_PVRTC_2BPPV1_Format: Number /* 35843 */

external var RGB_ETC1_Format: Number /* 36196 */

external var RGB_ETC2_Format: Number /* 37492 */

external var RGBA_ETC2_EAC_Format: Number /* 37496 */

external var RGBA_ASTC_4x4_Format: Number /* 37808 */

external var RGBA_ASTC_5x4_Format: Number /* 37809 */

external var RGBA_ASTC_5x5_Format: Number /* 37810 */

external var RGBA_ASTC_6x5_Format: Number /* 37811 */

external var RGBA_ASTC_6x6_Format: Number /* 37812 */

external var RGBA_ASTC_8x5_Format: Number /* 37813 */

external var RGBA_ASTC_8x6_Format: Number /* 37814 */

external var RGBA_ASTC_8x8_Format: Number /* 37815 */

external var RGBA_ASTC_10x5_Format: Number /* 37816 */

external var RGBA_ASTC_10x6_Format: Number /* 37817 */

external var RGBA_ASTC_10x8_Format: Number /* 37818 */

external var RGBA_ASTC_10x10_Format: Number /* 37819 */

external var RGBA_ASTC_12x10_Format: Number /* 37820 */

external var RGBA_ASTC_12x12_Format: Number /* 37821 */

external var RGBA_BPTC_Format: Number /* 36492 */

external var RGB_BPTC_SIGNED_Format: Any

external var RGB_BPTC_UNSIGNED_Format: Any

external var RED_RGTC1_Format: Number /* 36283 */

external var SIGNED_RED_RGTC1_Format: Number /* 36284 */

external var RED_GREEN_RGTC2_Format: Number /* 36285 */

external var SIGNED_RED_GREEN_RGTC2_Format: Number /* 36286 */

external var LoopOnce: Number /* 2200 */

external var LoopRepeat: Number /* 2201 */

external var LoopPingPong: Number /* 2202 */

external var InterpolateDiscrete: Number /* 2300 */

external var InterpolateLinear: Number /* 2301 */

external var InterpolateSmooth: Number /* 2302 */

external var ZeroCurvatureEnding: Number /* 2400 */

external var ZeroSlopeEnding: Number /* 2401 */

external var WrapAroundEnding: Number /* 2402 */

external var NormalAnimationBlendMode: Number /* 2500 */

external var AdditiveAnimationBlendMode: Number /* 2501 */

external var TrianglesDrawMode: Number /* 0 */

external var TriangleStripDrawMode: Number /* 1 */

external var TriangleFanDrawMode: Number /* 2 */

external var BasicDepthPacking: Number /* 3200 */

external var RGBADepthPacking: Number /* 3201 */

external var RGBDepthPacking: Number /* 3202 */

external var RGDepthPacking: Number /* 3203 */

external var TangentSpaceNormalMap: Number /* 0 */

external var ObjectSpaceNormalMap: Number /* 1 */

external var NoColorSpace: String /* "" */

external var SRGBColorSpace: String /* "srgb" */

external var LinearSRGBColorSpace: String /* "srgb-linear" */

external var DisplayP3ColorSpace: String /* "display-p3" */

external var LinearDisplayP3ColorSpace: Any

external var LinearTransfer: String /* "linear" */

external var SRGBTransfer: String /* "srgb" */

external var Rec709Primaries: String /* "rec709" */

external var P3Primaries: String /* "p3" */

external var ZeroStencilOp: Number /* 0 */

external var KeepStencilOp: Number /* 7680 */

external var ReplaceStencilOp: Number /* 7681 */

external var IncrementStencilOp: Number /* 7682 */

external var DecrementStencilOp: Number /* 7283 */

external var IncrementWrapStencilOp: Number /* 34055 */

external var DecrementWrapStencilOp: Number /* 34056 */

external var InvertStencilOp: Number /* 5386 */

external var NeverStencilFunc: Number /* 512 */

external var LessStencilFunc: Number /* 513 */

external var EqualStencilFunc: Number /* 514 */

external var LessEqualStencilFunc: Number /* 515 */

external var GreaterStencilFunc: Number /* 516 */

external var NotEqualStencilFunc: Number /* 517 */

external var GreaterEqualStencilFunc: Number /* 518 */

external var AlwaysStencilFunc: Number /* 519 */

external var NeverCompare: Number /* 512 */

external var LessCompare: Number /* 513 */

external var EqualCompare: Number /* 514 */

external var LessEqualCompare: Number /* 515 */

external var GreaterCompare: Number /* 516 */

external var NotEqualCompare: Number /* 517 */

external var GreaterEqualCompare: Number /* 518 */

external var AlwaysCompare: Number /* 519 */

external var StaticDrawUsage: Number /* 35044 */

external var DynamicDrawUsage: Number /* 35048 */

external var StreamDrawUsage: Number /* 35040 */

external var StaticReadUsage: Number /* 35045 */

external var DynamicReadUsage: Number /* 35049 */

external var StreamReadUsage: Number /* 35041 */

external var StaticCopyUsage: Number /* 35046 */

external var DynamicCopyUsage: Number /* 35050 */

external var StreamCopyUsage: Number /* 35042 */

external var GLSL1: String /* "100" */

external var GLSL3: String /* "300 es" */

external var WebGLCoordinateSystem: Number /* 2000 */

external var WebGPUCoordinateSystem: Number /* 2001 */