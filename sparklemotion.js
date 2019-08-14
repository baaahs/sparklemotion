(function (root, factory) {
  if (typeof define === 'function' && define.amd)
    define(['exports', 'kotlin', 'kotlinx-serialization-kotlinx-serialization-runtime', 'kotlinx-coroutines-core', 'klock-root-klock', 'kotlinx-html-js', 'threejs-wrapper'], factory);
  else if (typeof exports === 'object')
    factory(module.exports, require('kotlin'), require('kotlinx-serialization-kotlinx-serialization-runtime'), require('kotlinx-coroutines-core'), require('klock-root-klock'), require('kotlinx-html-js'), require('threejs-wrapper'));
  else {
    if (typeof kotlin === 'undefined') {
      throw new Error("Error loading module 'sparklemotion'. Its dependency 'kotlin' was not found. Please, check whether 'kotlin' is loaded prior to 'sparklemotion'.");
    }
    if (typeof this['kotlinx-serialization-kotlinx-serialization-runtime'] === 'undefined') {
      throw new Error("Error loading module 'sparklemotion'. Its dependency 'kotlinx-serialization-kotlinx-serialization-runtime' was not found. Please, check whether 'kotlinx-serialization-kotlinx-serialization-runtime' is loaded prior to 'sparklemotion'.");
    }
    if (typeof this['kotlinx-coroutines-core'] === 'undefined') {
      throw new Error("Error loading module 'sparklemotion'. Its dependency 'kotlinx-coroutines-core' was not found. Please, check whether 'kotlinx-coroutines-core' is loaded prior to 'sparklemotion'.");
    }
    if (typeof this['klock-root-klock'] === 'undefined') {
      throw new Error("Error loading module 'sparklemotion'. Its dependency 'klock-root-klock' was not found. Please, check whether 'klock-root-klock' is loaded prior to 'sparklemotion'.");
    }
    if (typeof this['kotlinx-html-js'] === 'undefined') {
      throw new Error("Error loading module 'sparklemotion'. Its dependency 'kotlinx-html-js' was not found. Please, check whether 'kotlinx-html-js' is loaded prior to 'sparklemotion'.");
    }
    if (typeof this['threejs-wrapper'] === 'undefined') {
      throw new Error("Error loading module 'sparklemotion'. Its dependency 'threejs-wrapper' was not found. Please, check whether 'threejs-wrapper' is loaded prior to 'sparklemotion'.");
    }
    root.sparklemotion = factory(typeof sparklemotion === 'undefined' ? {} : sparklemotion, kotlin, this['kotlinx-serialization-kotlinx-serialization-runtime'], this['kotlinx-coroutines-core'], this['klock-root-klock'], this['kotlinx-html-js'], this['threejs-wrapper']);
  }
}(this, function (_, Kotlin, $module$kotlinx_serialization_kotlinx_serialization_runtime, $module$kotlinx_coroutines_core, $module$klock_root_klock, $module$kotlinx_html_js, $module$threejs_wrapper) {
  'use strict';
  var Kind_OBJECT = Kotlin.Kind.OBJECT;
  var SerialClassDescImpl = $module$kotlinx_serialization_kotlinx_serialization_runtime.kotlinx.serialization.internal.SerialClassDescImpl;
  var equals = Kotlin.equals;
  var UnknownFieldException = $module$kotlinx_serialization_kotlinx_serialization_runtime.kotlinx.serialization.UnknownFieldException;
  var internal = $module$kotlinx_serialization_kotlinx_serialization_runtime.kotlinx.serialization.internal;
  var GeneratedSerializer = $module$kotlinx_serialization_kotlinx_serialization_runtime.kotlinx.serialization.internal.GeneratedSerializer;
  var MissingFieldException = $module$kotlinx_serialization_kotlinx_serialization_runtime.kotlinx.serialization.MissingFieldException;
  var Kind_CLASS = Kotlin.Kind.CLASS;
  var Kind_INTERFACE = Kotlin.Kind.INTERFACE;
  var throwUPAE = Kotlin.throwUPAE;
  var COROUTINE_SUSPENDED = Kotlin.kotlin.coroutines.intrinsics.COROUTINE_SUSPENDED;
  var CoroutineImpl = Kotlin.kotlin.coroutines.CoroutineImpl;
  var println = Kotlin.kotlin.io.println_s8jyv4$;
  var L0 = Kotlin.Long.ZERO;
  var L5000 = Kotlin.Long.fromInt(5000);
  var delay = $module$kotlinx_coroutines_core.kotlinx.coroutines.delay_s8cxhz$;
  var contentEquals = Kotlin.arrayEquals;
  var throwCCE = Kotlin.throwCCE;
  var ensureNotNull = Kotlin.ensureNotNull;
  var toByte = Kotlin.toByte;
  var Exception_init = Kotlin.kotlin.Exception_init_pdl1vj$;
  var toString = Kotlin.kotlin.text.toString_dqglrj$;
  var Random = Kotlin.kotlin.random.Random;
  var trimStart = Kotlin.kotlin.text.trimStart_wqw3xr$;
  var toInt = Kotlin.kotlin.text.toInt_6ic1pp$;
  var IllegalArgumentException_init = Kotlin.kotlin.IllegalArgumentException_init_pdl1vj$;
  var numberToInt = Kotlin.numberToInt;
  var withName = $module$kotlinx_serialization_kotlinx_serialization_runtime.kotlinx.serialization.withName_8new1j$;
  var KSerializer = $module$kotlinx_serialization_kotlinx_serialization_runtime.kotlinx.serialization.KSerializer;
  var Math_0 = Math;
  var Pair = Kotlin.kotlin.Pair;
  var mapOf = Kotlin.kotlin.collections.mapOf_qfcya0$;
  var Unit = Kotlin.kotlin.Unit;
  var IllegalStateException_init = Kotlin.kotlin.IllegalStateException_init_pdl1vj$;
  var ReadWriteProperty = Kotlin.kotlin.properties.ReadWriteProperty;
  var getKClass = Kotlin.getKClass;
  var PolymorphicSerializer = $module$kotlinx_serialization_kotlinx_serialization_runtime.kotlinx.serialization.PolymorphicSerializer;
  var kotlin_js_internal_StringCompanionObject = Kotlin.kotlin.js.internal.StringCompanionObject;
  var serializer = $module$kotlinx_serialization_kotlinx_serialization_runtime.kotlinx.serialization.serializer_6eet4j$;
  var JsonElement = $module$kotlinx_serialization_kotlinx_serialization_runtime.kotlinx.serialization.json.JsonElement;
  var to = Kotlin.kotlin.to_ujzrz7$;
  var get_map = $module$kotlinx_serialization_kotlinx_serialization_runtime.kotlinx.serialization.get_map_kgqhr1$;
  var SerializersModule = $module$kotlinx_serialization_kotlinx_serialization_runtime.kotlinx.serialization.modules.SerializersModule_q4tcel$;
  var JsonConfiguration = $module$kotlinx_serialization_kotlinx_serialization_runtime.kotlinx.serialization.json.JsonConfiguration;
  var Json = $module$kotlinx_serialization_kotlinx_serialization_runtime.kotlinx.serialization.json.Json;
  var ReferenceArraySerializer = $module$kotlinx_serialization_kotlinx_serialization_runtime.kotlinx.serialization.internal.ReferenceArraySerializer;
  var LinkedHashSet_init = Kotlin.kotlin.collections.LinkedHashSet_init_287e2$;
  var HashMap_init = Kotlin.kotlin.collections.HashMap_init_q3lmfv$;
  var ArrayList_init = Kotlin.kotlin.collections.ArrayList_init_287e2$;
  var copyToArray = Kotlin.kotlin.collections.copyToArray;
  var emptyMap = Kotlin.kotlin.collections.emptyMap_q3lmfv$;
  var zip = Kotlin.kotlin.collections.zip_45mdf7$;
  var toString_0 = Kotlin.toString;
  var LinkedHashMap_init = Kotlin.kotlin.collections.LinkedHashMap_init_q3lmfv$;
  var collectionSizeOrDefault = Kotlin.kotlin.collections.collectionSizeOrDefault_ba2ldo$;
  var mapCapacity = Kotlin.kotlin.collections.mapCapacity_za3lpa$;
  var coerceAtLeast = Kotlin.kotlin.ranges.coerceAtLeast_dqglrj$;
  var LinkedHashMap_init_0 = Kotlin.kotlin.collections.LinkedHashMap_init_bwtc7$;
  var Enum = Kotlin.kotlin.Enum;
  var throwISE = Kotlin.throwISE;
  var launch = $module$kotlinx_coroutines_core.kotlinx.coroutines.launch_s496o7$;
  var L1000 = Kotlin.Long.fromInt(1000);
  var L2000 = Kotlin.Long.fromInt(2000);
  var coroutineScope = $module$kotlinx_coroutines_core.kotlinx.coroutines.coroutineScope_awg8ri$;
  var cancelAndJoin = $module$kotlinx_coroutines_core.kotlinx.coroutines.cancelAndJoin_5dx9u$;
  var DateTime = $module$klock_root_klock.com.soywiz.klock.DateTime;
  var joinToString = Kotlin.kotlin.collections.joinToString_fmv235$;
  var L1 = Kotlin.Long.ONE;
  var L50 = Kotlin.Long.fromInt(50);
  var L10 = Kotlin.Long.fromInt(10);
  var CoroutineName = $module$kotlinx_coroutines_core.kotlinx.coroutines.CoroutineName;
  var L10000 = Kotlin.Long.fromInt(10000);
  var ArrayList_init_0 = Kotlin.kotlin.collections.ArrayList_init_mqih57$;
  var toList = Kotlin.kotlin.collections.toList_964n91$;
  var withTimeoutOrNull = $module$kotlinx_coroutines_core.kotlinx.coroutines.withTimeoutOrNull_ms3uf5$;
  var Channel = $module$kotlinx_coroutines_core.kotlinx.coroutines.channels.Channel_ww73n8$;
  var Exception = Kotlin.kotlin.Exception;
  var L2 = Kotlin.Long.fromInt(2);
  var first = Kotlin.kotlin.collections.first_2p1efm$;
  var MainScope = $module$kotlinx_coroutines_core.kotlinx.coroutines.MainScope;
  var CoroutineScope = $module$kotlinx_coroutines_core.kotlinx.coroutines.CoroutineScope;
  var ArrayList_init_1 = Kotlin.kotlin.collections.ArrayList_init_ww73n8$;
  var checkIndexOverflow = Kotlin.kotlin.collections.checkIndexOverflow_za3lpa$;
  var sortedWith = Kotlin.kotlin.collections.sortedWith_eknfly$;
  var wrapFunction = Kotlin.wrapFunction;
  var Comparator = Kotlin.kotlin.Comparator;
  var until = Kotlin.kotlin.ranges.until_dqglrj$;
  var mutableMapOf = Kotlin.kotlin.collections.mutableMapOf_qfcya0$;
  var coroutines = $module$kotlinx_coroutines_core.kotlinx.coroutines;
  var emptyList = Kotlin.kotlin.collections.emptyList_287e2$;
  var plus = $module$kotlinx_serialization_kotlinx_serialization_runtime.kotlinx.serialization.modules.plus_7n7cf$;
  var modules = $module$kotlinx_serialization_kotlinx_serialization_runtime.kotlinx.serialization.modules;
  var NotImplementedError_init = Kotlin.kotlin.NotImplementedError;
  var lazy = Kotlin.kotlin.lazy_klfg04$;
  var IntRange = Kotlin.kotlin.ranges.IntRange;
  var Iterator = Kotlin.kotlin.collections.Iterator;
  var Iterable = Kotlin.kotlin.collections.Iterable;
  var RuntimeException_init = Kotlin.kotlin.RuntimeException_init_pdl1vj$;
  var Regex_init = Kotlin.kotlin.text.Regex_init_61zpoe$;
  var split = Kotlin.kotlin.text.split_ip8yn$;
  var toInt_0 = Kotlin.kotlin.text.toInt_pdl1vz$;
  var sorted = Kotlin.kotlin.collections.sorted_exjks8$;
  var toList_0 = Kotlin.kotlin.collections.toList_7wnvza$;
  var arrayListOf = Kotlin.kotlin.collections.arrayListOf_i5x0yv$;
  var hashCode = Kotlin.hashCode;
  var trim = Kotlin.kotlin.text.trim_gw00vp$;
  var toDouble = Kotlin.kotlin.text.toDouble_pdl1vz$;
  var addAll = Kotlin.kotlin.collections.addAll_ipc267$;
  var Exception_init_0 = Kotlin.kotlin.Exception_init;
  var minus = Kotlin.kotlin.collections.minus_q4559j$;
  var toList_1 = Kotlin.kotlin.collections.toList_abgq59$;
  var get_list = $module$kotlinx_serialization_kotlinx_serialization_runtime.kotlinx.serialization.get_list_gekvwj$;
  var UnsupportedOperationException_init = Kotlin.kotlin.UnsupportedOperationException_init;
  var toList_2 = Kotlin.kotlin.collections.toList_us0mfu$;
  var rangeTo = Kotlin.kotlin.ranges.rangeTo_38ydlf$;
  var PropertyMetadata = Kotlin.PropertyMetadata;
  var ArrayListSerializer = $module$kotlinx_serialization_kotlinx_serialization_runtime.kotlinx.serialization.internal.ArrayListSerializer;
  var NullableSerializer = $module$kotlinx_serialization_kotlinx_serialization_runtime.kotlinx.serialization.internal.NullableSerializer;
  var kotlin_js_internal_FloatCompanionObject = Kotlin.kotlin.js.internal.FloatCompanionObject;
  var serializer_0 = $module$kotlinx_serialization_kotlinx_serialization_runtime.kotlinx.serialization.serializer_y9phqa$;
  var PrimitiveClasses$doubleClass = Kotlin.kotlin.reflect.js.internal.PrimitiveClasses.doubleClass;
  var arrayCopy = Kotlin.kotlin.collections.arrayCopy;
  var toShort = Kotlin.toShort;
  var toChar = Kotlin.toChar;
  var toBoxedChar = Kotlin.toBoxedChar;
  var L4294967295 = new Kotlin.Long(-1, 0);
  var decodeToString = Kotlin.kotlin.text.decodeToString_964n91$;
  var copyOfRange = Kotlin.kotlin.collections.copyOfRange_ietg8x$;
  var toBits = Kotlin.floatToBits;
  var encodeToByteArray = Kotlin.kotlin.text.encodeToByteArray_pdl1vz$;
  var copyOf = Kotlin.kotlin.collections.copyOf_mrm5p$;
  var copyOfRange_0 = Kotlin.kotlin.collections.copyOfRange_qxueih$;
  var min = Kotlin.kotlin.collections.min_i2lc79$;
  var max = Kotlin.kotlin.collections.max_i2lc79$;
  var get_indices = Kotlin.kotlin.collections.get_indices_i2lc79$;
  var reversed = Kotlin.kotlin.ranges.reversed_zf1xzc$;
  var indexOf = Kotlin.kotlin.text.indexOf_l5u8uk$;
  var JsonPrimitive = $module$kotlinx_serialization_kotlinx_serialization_runtime.kotlinx.serialization.json.JsonPrimitive_pdl1vj$;
  var jsonArray = $module$kotlinx_serialization_kotlinx_serialization_runtime.kotlinx.serialization.json.jsonArray_mb52fq$;
  var L5 = Kotlin.Long.fromInt(5);
  var json = $module$kotlinx_serialization_kotlinx_serialization_runtime.kotlinx.serialization.json;
  var getValue = Kotlin.kotlin.collections.getValue_t9ocha$;
  var get_contentOrNull = $module$kotlinx_serialization_kotlinx_serialization_runtime.kotlinx.serialization.json.get_contentOrNull_u3sd3g$;
  var JsonParsingException = $module$kotlinx_serialization_kotlinx_serialization_runtime.kotlinx.serialization.json.JsonParsingException;
  var UnsupportedOperationException_init_0 = Kotlin.kotlin.UnsupportedOperationException_init_pdl1vj$;
  var json_0 = $module$kotlinx_serialization_kotlinx_serialization_runtime.kotlinx.serialization.json.json_s5o6vg$;
  var DateFormat = $module$klock_root_klock.com.soywiz.klock.DateFormat;
  var endsWith = Kotlin.kotlin.text.endsWith_7epoxm$;
  var removeAll = Kotlin.kotlin.collections.removeAll_qafx1e$;
  var HashSet_init = Kotlin.kotlin.collections.HashSet_init_287e2$;
  var until_0 = Kotlin.kotlin.ranges.until_c8b4g4$;
  var map = Kotlin.kotlin.sequences.map_z5avom$;
  var toList_3 = Kotlin.kotlin.sequences.toList_veqyi0$;
  var JsonObject = $module$kotlinx_serialization_kotlinx_serialization_runtime.kotlinx.serialization.json.JsonObject;
  var Array_0 = Array;
  var math = Kotlin.kotlin.math;
  var AbstractMutableList = Kotlin.kotlin.collections.AbstractMutableList;
  var get_indices_0 = Kotlin.kotlin.collections.get_indices_m7z4lg$;
  var IllegalStateException_init_0 = Kotlin.kotlin.IllegalStateException_init;
  var listOf = Kotlin.kotlin.collections.listOf_i5x0yv$;
  var toMutableMap = Kotlin.kotlin.collections.toMutableMap_abgq59$;
  var trimIndent = Kotlin.kotlin.text.trimIndent_pdl1vz$;
  var contains = Kotlin.kotlin.collections.contains_2ws7j4$;
  var L268435455 = Kotlin.Long.fromInt(268435455);
  var Random_0 = Kotlin.kotlin.random.Random_za3lpa$;
  var roundToInt = Kotlin.kotlin.math.roundToInt_yrwdxr$;
  var startsWith = Kotlin.kotlin.text.startsWith_7epoxm$;
  var coroutines_0 = Kotlin.kotlin.coroutines;
  var clear = Kotlin.kotlin.dom.clear_asww5s$;
  var appendText = Kotlin.kotlin.dom.appendText_46n0ku$;
  var appendElement = Kotlin.kotlin.dom.appendElement_ldvnw0$;
  var removeSuffix = Kotlin.kotlin.text.removeSuffix_gsj5wt$;
  var addClass = Kotlin.kotlin.dom.addClass_hhb33f$;
  var substring = Kotlin.kotlin.text.substring_fc3b62$;
  var get_create = $module$kotlinx_html_js.kotlinx.html.dom.get_create_4wc2mh$;
  var option = $module$kotlinx_html_js.kotlinx.html.option_k09m0r$;
  var asList = Kotlin.org.w3c.dom.asList_kt9thq$;
  var Matrix4_init = THREE.Matrix4;
  var contains_0 = Kotlin.kotlin.text.contains_li3zpu$;
  var Vector3 = THREE.Vector3;
  var Geometry = THREE.Geometry;
  var Face3_init = THREE.Face3;
  var MeshBasicMaterial = THREE.MeshBasicMaterial;
  var Color_init = THREE.Color;
  var Mesh_init = THREE.Mesh;
  var LineBasicMaterial = THREE.LineBasicMaterial;
  var BufferGeometry = THREE.BufferGeometry;
  var plus_0 = $module$threejs_wrapper.info.laht.threekt.math.plus_gulir3$;
  var Line_init = THREE.Line;
  var SphereBufferGeometry = THREE.SphereBufferGeometry;
  var Box3 = THREE.Box3;
  var max_0 = Kotlin.kotlin.collections.max_exjks8$;
  var Raycaster_init = THREE.Raycaster;
  var Vector2 = THREE.Vector2;
  var first_0 = Kotlin.kotlin.collections.first_us0mfu$;
  var PointsMaterial = THREE.PointsMaterial;
  var Points = THREE.Points;
  var PerspectiveCamera_init = THREE.PerspectiveCamera;
  var toDoubleArray = Kotlin.kotlin.collections.toDoubleArray_pnorak$;
  var toTypedArray = Kotlin.kotlin.collections.toTypedArray_bvy38s$;
  var List = Kotlin.kotlin.collections.List;
  var th = $module$kotlinx_html_js.kotlinx.html.th_bncpyi$;
  var tr = $module$kotlinx_html_js.kotlinx.html.tr_7wec05$;
  var td = $module$kotlinx_html_js.kotlinx.html.td_vlzo05$;
  var table = $module$kotlinx_html_js.kotlinx.html.js.table_uk5qws$;
  var append = $module$kotlinx_html_js.kotlinx.html.dom.append_k9bwru$;
  var Box2 = THREE.Box2;
  var Clock = THREE.Clock;
  var WebGLRenderer_init = THREE.WebGLRenderer;
  var Scene = THREE.Scene;
  var Object3D = THREE.Object3D;
  var set_tabIndex = $module$kotlinx_html_js.kotlinx.html.set_tabIndex_ueiko3$;
  var set_onClickFunction = $module$kotlinx_html_js.kotlinx.html.js.set_onClickFunction_pszlq2$;
  var button = $module$kotlinx_html_js.kotlinx.html.button_i4xb7r$;
  var i = $module$kotlinx_html_js.kotlinx.html.i_5g1p9k$;
  var select = $module$kotlinx_html_js.kotlinx.html.select_9klr40$;
  var div = $module$kotlinx_html_js.kotlinx.html.div_ri36nr$;
  var canvas = $module$kotlinx_html_js.kotlinx.html.canvas_dwb9fz$;
  var div_0 = $module$kotlinx_html_js.kotlinx.html.div_59el9d$;
  var Triangle = THREE.Triangle;
  var getPropertyCallableRef = Kotlin.getPropertyCallableRef;
  var L200000 = Kotlin.Long.fromInt(200000);
  var CoroutineScope_0 = $module$kotlinx_coroutines_core.kotlinx.coroutines.CoroutineScope_1fupul$;
  var L134217727 = Kotlin.Long.fromInt(134217727);
  var replace = Kotlin.kotlin.text.replace_680rmw$;
  var toTypedArray_0 = Kotlin.kotlin.collections.toTypedArray_rjqryz$;
  var canvas_0 = $module$kotlinx_html_js.kotlinx.html.js.canvas_o2d15m$;
  var promise = $module$kotlinx_coroutines_core.kotlinx.coroutines.promise_pda6u4$;
  var trimEnd = Kotlin.kotlin.text.trimEnd_wqw3xr$;
  var toTypedArray_1 = Kotlin.kotlin.collections.toTypedArray_964n91$;
  var unboxChar = Kotlin.unboxChar;
  var joinToString_0 = Kotlin.kotlin.collections.joinToString_s78119$;
  var get_js = Kotlin.kotlin.js.get_js_1yb8b7$;
  var contentHashCode = Kotlin.arrayHashCode;
  var toMap = Kotlin.kotlin.collections.toMap_6hr0sd$;
  var Quaternion = THREE.Quaternion;
  var Line3 = THREE.Line3;
  var ConeBufferGeometry = THREE.ConeBufferGeometry;
  var OrbitControls = THREE.OrbitControls;
  var TextureLoader_init = THREE.TextureLoader;
  var minus_0 = $module$threejs_wrapper.info.laht.threekt.math.minus_gulir3$;
  var Float32BufferAttribute = THREE.Float32BufferAttribute;
  var BufferGeometryUtils$Companion = THREE.BufferGeometryUtils;
  var PlaneBufferGeometry = THREE.PlaneBufferGeometry;
  var indexOf_0 = Kotlin.kotlin.collections.indexOf_mjy6jw$;
  var sorted_0 = Kotlin.kotlin.collections.sorted_pbinho$;
  Mapper$Detector.prototype = Object.create(Enum.prototype);
  Mapper$Detector.prototype.constructor = Mapper$Detector;
  Mapper$TimeoutException.prototype = Object.create(Exception.prototype);
  Mapper$TimeoutException.prototype.constructor = Mapper$TimeoutException;
  MovingHead$ColorMode.prototype = Object.create(Enum.prototype);
  MovingHead$ColorMode.prototype.constructor = MovingHead$ColorMode;
  Pinky$PrerenderingSurfaceReceiver.prototype = Object.create(ShowRunner$SurfaceReceiver.prototype);
  Pinky$PrerenderingSurfaceReceiver.prototype.constructor = Pinky$PrerenderingSurfaceReceiver;
  PubSub$Connection$receive$ObjectLiteral.prototype = Object.create(PubSub$Listener.prototype);
  PubSub$Connection$receive$ObjectLiteral.prototype.constructor = PubSub$Connection$receive$ObjectLiteral;
  PubSub$Connection.prototype = Object.create(PubSub$Origin.prototype);
  PubSub$Connection.prototype.constructor = PubSub$Connection;
  PubSub$Server$PublisherListener.prototype = Object.create(PubSub$Listener.prototype);
  PubSub$Server$PublisherListener.prototype.constructor = PubSub$Server$PublisherListener;
  PubSub$Server.prototype = Object.create(PubSub$Endpoint.prototype);
  PubSub$Server.prototype.constructor = PubSub$Server;
  PubSub$Client$subscribe$lambda$lambda$ObjectLiteral.prototype = Object.create(PubSub$Listener.prototype);
  PubSub$Client$subscribe$lambda$lambda$ObjectLiteral.prototype.constructor = PubSub$Client$subscribe$lambda$lambda$ObjectLiteral;
  PubSub$Client$subscribe$ObjectLiteral.prototype = Object.create(PubSub$Listener.prototype);
  PubSub$Client$subscribe$ObjectLiteral.prototype.constructor = PubSub$Client$subscribe$ObjectLiteral;
  PubSub$Client.prototype = Object.create(PubSub$Endpoint.prototype);
  PubSub$Client.prototype.constructor = PubSub$Client;
  ShaderId.prototype = Object.create(Enum.prototype);
  ShaderId.prototype.constructor = ShaderId;
  SheepModel.prototype = Object.create(Model.prototype);
  SheepModel.prototype.constructor = SheepModel;
  Show$RestartShowException.prototype = Object.create(Exception.prototype);
  Show$RestartShowException.prototype.constructor = Show$RestartShowException;
  LixadaMiniMovingHead$Channel.prototype = Object.create(Enum.prototype);
  LixadaMiniMovingHead$Channel.prototype.constructor = LixadaMiniMovingHead$Channel;
  LixadaMiniMovingHead.prototype = Object.create(Dmx$DeviceType.prototype);
  LixadaMiniMovingHead.prototype.constructor = LixadaMiniMovingHead;
  Shenzarpy$WheelColor.prototype = Object.create(Enum.prototype);
  Shenzarpy$WheelColor.prototype.constructor = Shenzarpy$WheelColor;
  Shenzarpy$Channel.prototype = Object.create(Enum.prototype);
  Shenzarpy$Channel.prototype.constructor = Shenzarpy$Channel;
  Shenzarpy.prototype = Object.create(Dmx$DeviceType.prototype);
  Shenzarpy.prototype.constructor = Shenzarpy;
  ColorPicker.prototype = Object.create(Gadget.prototype);
  ColorPicker.prototype.constructor = ColorPicker;
  PalettePicker.prototype = Object.create(Gadget.prototype);
  PalettePicker.prototype.constructor = PalettePicker;
  Slider.prototype = Object.create(Gadget.prototype);
  Slider.prototype.constructor = Slider;
  Type.prototype = Object.create(Enum.prototype);
  Type.prototype.constructor = Type;
  BrainHelloMessage.prototype = Object.create(Message.prototype);
  BrainHelloMessage.prototype.constructor = BrainHelloMessage;
  BrainShaderMessage.prototype = Object.create(Message.prototype);
  BrainShaderMessage.prototype.constructor = BrainShaderMessage;
  UseFirmwareMessage.prototype = Object.create(Message.prototype);
  UseFirmwareMessage.prototype.constructor = UseFirmwareMessage;
  MapperHelloMessage.prototype = Object.create(Message.prototype);
  MapperHelloMessage.prototype.constructor = MapperHelloMessage;
  BrainIdRequest.prototype = Object.create(Message.prototype);
  BrainIdRequest.prototype.constructor = BrainIdRequest;
  BrainMappingMessage.prototype = Object.create(Message.prototype);
  BrainMappingMessage.prototype.constructor = BrainMappingMessage;
  PingMessage.prototype = Object.create(Message.prototype);
  PingMessage.prototype.constructor = PingMessage;
  CompositorShader.prototype = Object.create(Shader.prototype);
  CompositorShader.prototype.constructor = CompositorShader;
  CompositingMode.prototype = Object.create(Enum.prototype);
  CompositingMode.prototype.constructor = CompositingMode;
  CompositingMode$NORMAL.prototype = Object.create(CompositingMode.prototype);
  CompositingMode$NORMAL.prototype.constructor = CompositingMode$NORMAL;
  CompositingMode$ADD.prototype = Object.create(CompositingMode.prototype);
  CompositingMode$ADD.prototype.constructor = CompositingMode$ADD;
  GlslSandbox55301Shader.prototype = Object.create(Shader.prototype);
  GlslSandbox55301Shader.prototype.constructor = GlslSandbox55301Shader;
  GlslShader$AdjustableValue$Type.prototype = Object.create(Enum.prototype);
  GlslShader$AdjustableValue$Type.prototype.constructor = GlslShader$AdjustableValue$Type;
  GlslShader.prototype = Object.create(Shader.prototype);
  GlslShader.prototype.constructor = GlslShader;
  HeartShader.prototype = Object.create(Shader.prototype);
  HeartShader.prototype.constructor = HeartShader;
  PixelShader$Encoding.prototype = Object.create(Enum.prototype);
  PixelShader$Encoding.prototype.constructor = PixelShader$Encoding;
  PixelShader$Encoding$DIRECT_ARGB.prototype = Object.create(PixelShader$Encoding.prototype);
  PixelShader$Encoding$DIRECT_ARGB.prototype.constructor = PixelShader$Encoding$DIRECT_ARGB;
  PixelShader$Encoding$DIRECT_RGB.prototype = Object.create(PixelShader$Encoding.prototype);
  PixelShader$Encoding$DIRECT_RGB.prototype.constructor = PixelShader$Encoding$DIRECT_RGB;
  PixelShader$Encoding$INDEXED_2.prototype = Object.create(PixelShader$Encoding.prototype);
  PixelShader$Encoding$INDEXED_2.prototype.constructor = PixelShader$Encoding$INDEXED_2;
  PixelShader$Encoding$INDEXED_4.prototype = Object.create(PixelShader$Encoding.prototype);
  PixelShader$Encoding$INDEXED_4.prototype.constructor = PixelShader$Encoding$INDEXED_4;
  PixelShader$Encoding$INDEXED_16.prototype = Object.create(PixelShader$Encoding.prototype);
  PixelShader$Encoding$INDEXED_16.prototype.constructor = PixelShader$Encoding$INDEXED_16;
  PixelShader$DirectColorBuffer$get_PixelShader$DirectColorBuffer$colors$ObjectLiteral.prototype = Object.create(AbstractMutableList.prototype);
  PixelShader$DirectColorBuffer$get_PixelShader$DirectColorBuffer$colors$ObjectLiteral.prototype.constructor = PixelShader$DirectColorBuffer$get_PixelShader$DirectColorBuffer$colors$ObjectLiteral;
  PixelShader$DirectColorBuffer.prototype = Object.create(PixelShader$Buffer.prototype);
  PixelShader$DirectColorBuffer.prototype.constructor = PixelShader$DirectColorBuffer;
  PixelShader$IndexedBuffer$get_PixelShader$IndexedBuffer$colors$ObjectLiteral.prototype = Object.create(AbstractMutableList.prototype);
  PixelShader$IndexedBuffer$get_PixelShader$IndexedBuffer$colors$ObjectLiteral.prototype.constructor = PixelShader$IndexedBuffer$get_PixelShader$IndexedBuffer$colors$ObjectLiteral;
  PixelShader$IndexedBuffer.prototype = Object.create(PixelShader$Buffer.prototype);
  PixelShader$IndexedBuffer.prototype.constructor = PixelShader$IndexedBuffer;
  PixelShader.prototype = Object.create(Shader.prototype);
  PixelShader.prototype.constructor = PixelShader;
  RandomShader.prototype = Object.create(Shader.prototype);
  RandomShader.prototype.constructor = RandomShader;
  SimpleSpatialShader.prototype = Object.create(Shader.prototype);
  SimpleSpatialShader.prototype.constructor = SimpleSpatialShader;
  SineWaveShader.prototype = Object.create(Shader.prototype);
  SineWaveShader.prototype.constructor = SineWaveShader;
  SolidShader.prototype = Object.create(Shader.prototype);
  SolidShader.prototype.constructor = SolidShader;
  SparkleShader.prototype = Object.create(Shader.prototype);
  SparkleShader.prototype.constructor = SparkleShader;
  CompositeShow.prototype = Object.create(Show.prototype);
  CompositeShow.prototype.constructor = CompositeShow;
  CreepingPixelsShow.prototype = Object.create(Show.prototype);
  CreepingPixelsShow.prototype.constructor = CreepingPixelsShow;
  GlslShow.prototype = Object.create(Show.prototype);
  GlslShow.prototype.constructor = GlslShow;
  GlslOtherShow.prototype = Object.create(GlslShow.prototype);
  GlslOtherShow.prototype.constructor = GlslOtherShow;
  GlslSandbox55301KotlinShow.prototype = Object.create(Show.prototype);
  GlslSandbox55301KotlinShow.prototype.constructor = GlslSandbox55301KotlinShow;
  GlslSandbox55301Show.prototype = Object.create(GlslShow.prototype);
  GlslSandbox55301Show.prototype.constructor = GlslSandbox55301Show;
  GlslSandbox56555Show.prototype = Object.create(GlslShow.prototype);
  GlslSandbox56555Show.prototype.constructor = GlslSandbox56555Show;
  GlslSandbox56594Show.prototype = Object.create(GlslShow.prototype);
  GlslSandbox56594Show.prototype.constructor = GlslSandbox56594Show;
  HeartbleatShow.prototype = Object.create(Show.prototype);
  HeartbleatShow.prototype.constructor = HeartbleatShow;
  LifeyShow.prototype = Object.create(Show.prototype);
  LifeyShow.prototype.constructor = LifeyShow;
  PanelTweenShow.prototype = Object.create(Show.prototype);
  PanelTweenShow.prototype.constructor = PanelTweenShow;
  PixelTweenShow.prototype = Object.create(Show.prototype);
  PixelTweenShow.prototype.constructor = PixelTweenShow;
  RandomShow.prototype = Object.create(Show.prototype);
  RandomShow.prototype.constructor = RandomShow;
  SimpleSpatialShow.prototype = Object.create(Show.prototype);
  SimpleSpatialShow.prototype.constructor = SimpleSpatialShow;
  SolidColorShow.prototype = Object.create(Show.prototype);
  SolidColorShow.prototype.constructor = SolidColorShow;
  SomeDumbShow.prototype = Object.create(Show.prototype);
  SomeDumbShow.prototype.constructor = SomeDumbShow;
  ThumpShow.prototype = Object.create(Show.prototype);
  ThumpShow.prototype.constructor = ThumpShow;
  FakeDmxUniverse.prototype = Object.create(Dmx$Universe.prototype);
  FakeDmxUniverse.prototype.constructor = FakeDmxUniverse;
  Vector2_0.prototype = Object.create(Vector2.prototype);
  Vector2_0.prototype.constructor = Vector2_0;
  JsGlslRenderer$SurfacePixels.prototype = Object.create(SurfacePixels.prototype);
  JsGlslRenderer$SurfacePixels.prototype.constructor = JsGlslRenderer$SurfacePixels;
  JsGlslRenderer$Instance.prototype = Object.create(GlslRenderer$Instance.prototype);
  JsGlslRenderer$Instance.prototype.constructor = JsGlslRenderer$Instance;
  JsGlslRenderer.prototype = Object.create(GlslRenderer.prototype);
  JsGlslRenderer.prototype.constructor = JsGlslRenderer;
  NativeBitmap.prototype = Object.create(CanvasBitmap.prototype);
  NativeBitmap.prototype.constructor = NativeBitmap;
  CanvasBitmap$asImage$ObjectLiteral.prototype = Object.create(JsImage.prototype);
  CanvasBitmap$asImage$ObjectLiteral.prototype.constructor = CanvasBitmap$asImage$ObjectLiteral;
  ImageBitmapImage.prototype = Object.create(JsImage.prototype);
  ImageBitmapImage.prototype.constructor = ImageBitmapImage;
  VideoElementImage.prototype = Object.create(JsImage.prototype);
  VideoElementImage.prototype.constructor = VideoElementImage;
  function BeatData(measureStartTimeMs, beatIntervalMs, beatsPerMeasure, confidence) {
    BeatData$Companion_getInstance();
    if (beatsPerMeasure === void 0)
      beatsPerMeasure = 4;
    if (confidence === void 0)
      confidence = 1.0;
    this.measureStartTimeMs = measureStartTimeMs;
    this.beatIntervalMs = beatIntervalMs;
    this.beatsPerMeasure = beatsPerMeasure;
    this.confidence = confidence;
  }
  Object.defineProperty(BeatData.prototype, 'bpm', {
    get: function () {
      return 60000 / this.beatIntervalMs | 0;
    }
  });
  BeatData.prototype.beatWithinMeasure_rnw5ii$ = function (clock) {
    var elapsedSinceStartOfMeasure = clock.now() - this.measureStartTimeMs;
    return elapsedSinceStartOfMeasure / this.beatIntervalMs % this.beatsPerMeasure;
  };
  function BeatData$Companion() {
    BeatData$Companion_instance = this;
  }
  BeatData$Companion.prototype.serializer = function () {
    return BeatData$$serializer_getInstance();
  };
  BeatData$Companion.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'Companion',
    interfaces: []
  };
  var BeatData$Companion_instance = null;
  function BeatData$Companion_getInstance() {
    if (BeatData$Companion_instance === null) {
      new BeatData$Companion();
    }
    return BeatData$Companion_instance;
  }
  function BeatData$$serializer() {
    this.descriptor_7q0ok7$_0 = new SerialClassDescImpl('baaahs.BeatData', this);
    this.descriptor.addElement_ivxn3r$('measureStartTimeMs', false);
    this.descriptor.addElement_ivxn3r$('beatIntervalMs', false);
    this.descriptor.addElement_ivxn3r$('beatsPerMeasure', true);
    this.descriptor.addElement_ivxn3r$('confidence', true);
    BeatData$$serializer_instance = this;
  }
  Object.defineProperty(BeatData$$serializer.prototype, 'descriptor', {
    get: function () {
      return this.descriptor_7q0ok7$_0;
    }
  });
  BeatData$$serializer.prototype.serialize_awe97i$ = function (encoder, obj) {
    var output = encoder.beginStructure_r0sa6z$(this.descriptor, []);
    output.encodeDoubleElement_imzr5k$(this.descriptor, 0, obj.measureStartTimeMs);
    output.encodeIntElement_4wpqag$(this.descriptor, 1, obj.beatIntervalMs);
    if (!equals(obj.beatsPerMeasure, 4) || output.shouldEncodeElementDefault_3zr2iy$(this.descriptor, 2))
      output.encodeIntElement_4wpqag$(this.descriptor, 2, obj.beatsPerMeasure);
    if (!equals(obj.confidence, 1.0) || output.shouldEncodeElementDefault_3zr2iy$(this.descriptor, 3))
      output.encodeFloatElement_t7qhdx$(this.descriptor, 3, obj.confidence);
    output.endStructure_qatsm0$(this.descriptor);
  };
  BeatData$$serializer.prototype.deserialize_nts5qn$ = function (decoder) {
    var index, readAll = false;
    var bitMask0 = 0;
    var local0
    , local1
    , local2
    , local3;
    var input = decoder.beginStructure_r0sa6z$(this.descriptor, []);
    loopLabel: while (true) {
      index = input.decodeElementIndex_qatsm0$(this.descriptor);
      switch (index) {
        case -2:
          readAll = true;
        case 0:
          local0 = input.decodeDoubleElement_3zr2iy$(this.descriptor, 0);
          bitMask0 |= 1;
          if (!readAll)
            break;
        case 1:
          local1 = input.decodeIntElement_3zr2iy$(this.descriptor, 1);
          bitMask0 |= 2;
          if (!readAll)
            break;
        case 2:
          local2 = input.decodeIntElement_3zr2iy$(this.descriptor, 2);
          bitMask0 |= 4;
          if (!readAll)
            break;
        case 3:
          local3 = input.decodeFloatElement_3zr2iy$(this.descriptor, 3);
          bitMask0 |= 8;
          if (!readAll)
            break;
        case -1:
          break loopLabel;
        default:throw new UnknownFieldException(index);
      }
    }
    input.endStructure_qatsm0$(this.descriptor);
    return BeatData_init(bitMask0, local0, local1, local2, local3, null);
  };
  BeatData$$serializer.prototype.childSerializers = function () {
    return [internal.DoubleSerializer, internal.IntSerializer, internal.IntSerializer, internal.FloatSerializer];
  };
  BeatData$$serializer.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: '$serializer',
    interfaces: [GeneratedSerializer]
  };
  var BeatData$$serializer_instance = null;
  function BeatData$$serializer_getInstance() {
    if (BeatData$$serializer_instance === null) {
      new BeatData$$serializer();
    }
    return BeatData$$serializer_instance;
  }
  function BeatData_init(seen1, measureStartTimeMs, beatIntervalMs, beatsPerMeasure, confidence, serializationConstructorMarker) {
    var $this = serializationConstructorMarker || Object.create(BeatData.prototype);
    if ((seen1 & 1) === 0)
      throw new MissingFieldException('measureStartTimeMs');
    else
      $this.measureStartTimeMs = measureStartTimeMs;
    if ((seen1 & 2) === 0)
      throw new MissingFieldException('beatIntervalMs');
    else
      $this.beatIntervalMs = beatIntervalMs;
    if ((seen1 & 4) === 0)
      $this.beatsPerMeasure = 4;
    else
      $this.beatsPerMeasure = beatsPerMeasure;
    if ((seen1 & 8) === 0)
      $this.confidence = 1.0;
    else
      $this.confidence = confidence;
    return $this;
  }
  BeatData.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'BeatData',
    interfaces: []
  };
  BeatData.prototype.component1 = function () {
    return this.measureStartTimeMs;
  };
  BeatData.prototype.component2 = function () {
    return this.beatIntervalMs;
  };
  BeatData.prototype.component3 = function () {
    return this.beatsPerMeasure;
  };
  BeatData.prototype.component4 = function () {
    return this.confidence;
  };
  BeatData.prototype.copy_vie62r$ = function (measureStartTimeMs, beatIntervalMs, beatsPerMeasure, confidence) {
    return new BeatData(measureStartTimeMs === void 0 ? this.measureStartTimeMs : measureStartTimeMs, beatIntervalMs === void 0 ? this.beatIntervalMs : beatIntervalMs, beatsPerMeasure === void 0 ? this.beatsPerMeasure : beatsPerMeasure, confidence === void 0 ? this.confidence : confidence);
  };
  BeatData.prototype.toString = function () {
    return 'BeatData(measureStartTimeMs=' + Kotlin.toString(this.measureStartTimeMs) + (', beatIntervalMs=' + Kotlin.toString(this.beatIntervalMs)) + (', beatsPerMeasure=' + Kotlin.toString(this.beatsPerMeasure)) + (', confidence=' + Kotlin.toString(this.confidence)) + ')';
  };
  BeatData.prototype.hashCode = function () {
    var result = 0;
    result = result * 31 + Kotlin.hashCode(this.measureStartTimeMs) | 0;
    result = result * 31 + Kotlin.hashCode(this.beatIntervalMs) | 0;
    result = result * 31 + Kotlin.hashCode(this.beatsPerMeasure) | 0;
    result = result * 31 + Kotlin.hashCode(this.confidence) | 0;
    return result;
  };
  BeatData.prototype.equals = function (other) {
    return this === other || (other !== null && (typeof other === 'object' && (Object.getPrototypeOf(this) === Object.getPrototypeOf(other) && (Kotlin.equals(this.measureStartTimeMs, other.measureStartTimeMs) && Kotlin.equals(this.beatIntervalMs, other.beatIntervalMs) && Kotlin.equals(this.beatsPerMeasure, other.beatsPerMeasure) && Kotlin.equals(this.confidence, other.confidence)))));
  };
  function BeatSource() {
  }
  BeatSource.$metadata$ = {
    kind: Kind_INTERFACE,
    simpleName: 'BeatSource',
    interfaces: []
  };
  function Clock_0() {
  }
  Clock_0.$metadata$ = {
    kind: Kind_INTERFACE,
    simpleName: 'Clock',
    interfaces: []
  };
  function Brain(id, network, display, pixels) {
    this.id = id;
    this.network_0 = network;
    this.display_0 = display;
    this.pixels_0 = pixels;
    this.link_q2tdi4$_0 = this.link_q2tdi4$_0;
    this.udpSocket_cf1pha$_0 = this.udpSocket_cf1pha$_0;
    this.lastInstructionsReceivedAtMs_0 = L0;
    this.surfaceName_0 = null;
    this.surface_6p23av$_0 = new AnonymousSurface(new BrainId(this.id));
    this.currentShaderDesc_0 = null;
    this.currentRenderTree_0 = null;
  }
  Object.defineProperty(Brain.prototype, 'link_0', {
    get: function () {
      if (this.link_q2tdi4$_0 == null)
        return throwUPAE('link');
      return this.link_q2tdi4$_0;
    },
    set: function (link) {
      this.link_q2tdi4$_0 = link;
    }
  });
  Object.defineProperty(Brain.prototype, 'udpSocket_0', {
    get: function () {
      if (this.udpSocket_cf1pha$_0 == null)
        return throwUPAE('udpSocket');
      return this.udpSocket_cf1pha$_0;
    },
    set: function (udpSocket) {
      this.udpSocket_cf1pha$_0 = udpSocket;
    }
  });
  Object.defineProperty(Brain.prototype, 'surface_0', {
    get: function () {
      return this.surface_6p23av$_0;
    },
    set: function (value) {
      this.surface_6p23av$_0 = value;
      this.display_0.surface = value;
    }
  });
  function Coroutine$Brain$run$lambda(this$Brain_0, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.exceptionState_0 = 1;
    this.local$this$Brain = this$Brain_0;
  }
  Coroutine$Brain$run$lambda.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$Brain$run$lambda.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$Brain$run$lambda.prototype.constructor = Coroutine$Brain$run$lambda;
  Coroutine$Brain$run$lambda.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            println('Resetting Brain ' + this.local$this$Brain.id + '!');
            this.state_0 = 2;
            this.result_0 = this.local$this$Brain.reset_0(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 1:
            throw this.exception_0;
          case 2:
            return this.result_0;
          default:this.state_0 = 1;
            throw new Error('State Machine Unreachable execution');
        }
      }
       catch (e) {
        if (this.state_0 === 1) {
          this.exceptionState_0 = this.state_0;
          throw e;
        }
         else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  function Brain$run$lambda(this$Brain_0) {
    return function (continuation_0, suspended) {
      var instance = new Coroutine$Brain$run$lambda(this$Brain_0, continuation_0);
      if (suspended)
        return instance;
      else
        return instance.doResume(null);
    };
  }
  function Coroutine$run($this, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.exceptionState_0 = 1;
    this.$this = $this;
  }
  Coroutine$run.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$run.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$run.prototype.constructor = Coroutine$run;
  Coroutine$run.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.$this.link_0 = new FragmentingUdpLink(this.$this.network_0.link());
            this.$this.udpSocket_0 = this.$this.link_0.listenUdp_a6m852$(8003, this.$this);
            this.$this.display_0.id = this.$this.id;
            this.$this.display_0.haveLink_9m0ekx$(this.$this.link_0);
            this.$this.display_0.onReset = Brain$run$lambda(this.$this);
            this.state_0 = 2;
            this.result_0 = this.$this.sendHello_0(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 1:
            throw this.exception_0;
          case 2:
            return;
          default:this.state_0 = 1;
            throw new Error('State Machine Unreachable execution');
        }
      }
       catch (e) {
        if (this.state_0 === 1) {
          this.exceptionState_0 = this.state_0;
          throw e;
        }
         else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  Brain.prototype.run = function (continuation_0, suspended) {
    var instance = new Coroutine$run(this, continuation_0);
    if (suspended)
      return instance;
    else
      return instance.doResume(null);
  };
  function Coroutine$reset_0($this, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.exceptionState_0 = 1;
    this.$this = $this;
  }
  Coroutine$reset_0.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$reset_0.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$reset_0.prototype.constructor = Coroutine$reset_0;
  Coroutine$reset_0.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            var tmp$, tmp$_0, tmp$_1, tmp$_2;
            this.$this.lastInstructionsReceivedAtMs_0 = L0;
            this.$this.surfaceName_0 = null;
            this.$this.surface_0 = new AnonymousSurface(new BrainId(this.$this.id));
            this.$this.currentShaderDesc_0 = null;
            this.$this.currentRenderTree_0 = null;
            tmp$ = this.$this.pixels_0.indices;
            tmp$_0 = tmp$.first;
            tmp$_1 = tmp$.last;
            tmp$_2 = tmp$.step;
            for (var i = tmp$_0; i <= tmp$_1; i += tmp$_2)
              this.$this.pixels_0.set_ibd5tj$(i, Color$Companion_getInstance().WHITE);
            this.state_0 = 2;
            this.result_0 = this.$this.sendHello_0(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 1:
            throw this.exception_0;
          case 2:
            return;
          default:this.state_0 = 1;
            throw new Error('State Machine Unreachable execution');
        }
      }
       catch (e) {
        if (this.state_0 === 1) {
          this.exceptionState_0 = this.state_0;
          throw e;
        }
         else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  Brain.prototype.reset_0 = function (continuation_0, suspended) {
    var instance = new Coroutine$reset_0(this, continuation_0);
    if (suspended)
      return instance;
    else
      return instance.doResume(null);
  };
  Brain.prototype.forcedSurfaceName_61zpoe$ = function (name) {
    this.surfaceName_0 = name;
  };
  function Coroutine$sendHello_0($this, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.exceptionState_0 = 1;
    this.$this = $this;
  }
  Coroutine$sendHello_0.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$sendHello_0.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$sendHello_0.prototype.constructor = Coroutine$sendHello_0;
  Coroutine$sendHello_0.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.state_0 = 2;
            continue;
          case 1:
            throw this.exception_0;
          case 2:
            var elapsedSinceMessageMs = getTimeMillis().subtract(this.$this.lastInstructionsReceivedAtMs_0);
            if (elapsedSinceMessageMs.toNumber() > 10000) {
              if (!equals(this.$this.lastInstructionsReceivedAtMs_0, L0)) {
                logger$Companion_getInstance().info_61zpoe$(this.$this.id + ": haven't heard from Pinky in " + elapsedSinceMessageMs.toString() + 'ms');
              }
              this.$this.udpSocket_0.broadcastUdp_68hu5j$(8002, new BrainHelloMessage(this.$this.id, this.$this.surfaceName_0));
            }

            this.state_0 = 3;
            this.result_0 = delay(L5000, this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 3:
            this.state_0 = 2;
            continue;
          default:this.state_0 = 1;
            throw new Error('State Machine Unreachable execution');
        }
      }
       catch (e) {
        if (this.state_0 === 1) {
          this.exceptionState_0 = this.state_0;
          throw e;
        }
         else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  Brain.prototype.sendHello_0 = function (continuation_0, suspended) {
    var instance = new Coroutine$sendHello_0(this, continuation_0);
    if (suspended)
      return instance;
    else
      return instance.doResume(null);
  };
  Brain.prototype.receive_ytpeqp$ = function (fromAddress, fromPort, bytes) {
    var tmp$, tmp$_0, tmp$_1;
    var now = getTimeMillis();
    this.lastInstructionsReceivedAtMs_0 = now;
    var reader = new ByteArrayReader(bytes);
    var type = Type$Companion_getInstance().get_s8j3t7$(reader.readByte());
    switch (type.name) {
      case 'BRAIN_PANEL_SHADE':
        if (reader.readBoolean()) {
          tmp$ = reader.readBytes();
        }
         else {
          tmp$ = null;
        }

        var pongData = tmp$;
        var shaderDesc = reader.readBytes();
        var theCurrentShaderDesc = this.currentShaderDesc_0;
        if (theCurrentShaderDesc == null || !contentEquals(theCurrentShaderDesc, shaderDesc)) {
          this.currentShaderDesc_0 = shaderDesc;
          var shader = Kotlin.isType(tmp$_0 = Shader$Companion_getInstance().parse_100t80$(new ByteArrayReader(shaderDesc)), Shader) ? tmp$_0 : throwCCE();
          this.currentRenderTree_0 = new Brain$RenderTree(shader, shader.createRenderer_ppt8xj$(this.surface_0), shader.createBuffer_ppt8xj$(this.surface_0));
        }

        var $receiver = ensureNotNull(this.currentRenderTree_0);
        $receiver.read_100t80$(reader);
        $receiver.draw_bbfl1t$(this.pixels_0);
        if (pongData != null) {
          this.udpSocket_0.sendUdp_wpmaqi$(fromAddress, fromPort, new PingMessage(pongData, true));
        }

        break;
      case 'BRAIN_ID_REQUEST':
        this.udpSocket_0.sendUdp_wpmaqi$(fromAddress, fromPort, new BrainHelloMessage(this.id, this.surfaceName_0));
        break;
      case 'BRAIN_MAPPING':
        var message = BrainMappingMessage$Companion_getInstance().parse_100t80$(reader);
        this.surfaceName_0 = message.surfaceName;
        if (message.surfaceName != null) {
          var fakeModelSurface = new Brain$FakeModelSurface(message.surfaceName);
          tmp$_1 = new IdentifiedSurface(fakeModelSurface, message.pixelCount, message.pixelVertices);
        }
         else {
          tmp$_1 = new AnonymousSurface(new BrainId(this.id));
        }

        this.surface_0 = tmp$_1;
        this.currentShaderDesc_0 = null;
        this.currentRenderTree_0 = null;
        this.udpSocket_0.broadcastUdp_68hu5j$(8002, new BrainHelloMessage(this.id, this.surfaceName_0));
        break;
      case 'PING':
        var ping = PingMessage$Companion_getInstance().parse_100t80$(reader);
        if (!ping.isPong) {
          this.udpSocket_0.sendUdp_wpmaqi$(fromAddress, fromPort, new PingMessage(ping.data, true));
        }

        break;
      default:break;
    }
  };
  function Brain$RenderTree(shader, renderer, buffer) {
    this.shader = shader;
    this.renderer = renderer;
    this.buffer = buffer;
  }
  Brain$RenderTree.prototype.read_100t80$ = function (reader) {
    this.buffer.read_100t80$(reader);
  };
  Brain$RenderTree.prototype.draw_bbfl1t$ = function (pixels) {
    var tmp$, tmp$_0, tmp$_1, tmp$_2;
    this.renderer.beginFrame_b23bvv$(this.buffer, pixels.size);
    tmp$ = pixels.indices;
    tmp$_0 = tmp$.first;
    tmp$_1 = tmp$.last;
    tmp$_2 = tmp$.step;
    for (var i = tmp$_0; i <= tmp$_1; i += tmp$_2) {
      pixels.set_ibd5tj$(i, this.renderer.draw_b23bvv$(this.buffer, i));
    }
    this.renderer.endFrame();
    pixels.finishedFrame();
  };
  Brain$RenderTree.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'RenderTree',
    interfaces: []
  };
  function Brain$FakeModelSurface(name, description) {
    if (description === void 0)
      description = name;
    this.name_mkr12i$_0 = name;
    this.description_487evr$_0 = description;
  }
  Object.defineProperty(Brain$FakeModelSurface.prototype, 'name', {
    get: function () {
      return this.name_mkr12i$_0;
    }
  });
  Object.defineProperty(Brain$FakeModelSurface.prototype, 'description', {
    get: function () {
      return this.description_487evr$_0;
    }
  });
  Brain$FakeModelSurface.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'FakeModelSurface',
    interfaces: [Model$Surface]
  };
  Brain.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Brain',
    interfaces: [Network$UdpListener]
  };
  function Color(argb) {
    Color$Companion_getInstance();
    this.argb = argb;
  }
  Color.prototype.serialize_3kjoo0$ = function (writer) {
    writer.writeInt_za3lpa$(this.argb);
  };
  Object.defineProperty(Color.prototype, 'alphaB', {
    get: function () {
      return toByte(this.alphaI_za3lpa$(this.argb));
    }
  });
  Object.defineProperty(Color.prototype, 'redB', {
    get: function () {
      return toByte(this.redI_za3lpa$(this.argb));
    }
  });
  Object.defineProperty(Color.prototype, 'greenB', {
    get: function () {
      return toByte(this.greenI_za3lpa$(this.argb));
    }
  });
  Object.defineProperty(Color.prototype, 'blueB', {
    get: function () {
      return toByte(this.blueI_za3lpa$(this.argb));
    }
  });
  Object.defineProperty(Color.prototype, 'alphaI', {
    get: function () {
      return this.alphaI_za3lpa$(this.argb);
    }
  });
  Object.defineProperty(Color.prototype, 'redI', {
    get: function () {
      return this.redI_za3lpa$(this.argb);
    }
  });
  Object.defineProperty(Color.prototype, 'greenI', {
    get: function () {
      return this.greenI_za3lpa$(this.argb);
    }
  });
  Object.defineProperty(Color.prototype, 'blueI', {
    get: function () {
      return this.blueI_za3lpa$(this.argb);
    }
  });
  Object.defineProperty(Color.prototype, 'alphaF', {
    get: function () {
      return this.alphaI / 255;
    }
  });
  Object.defineProperty(Color.prototype, 'redF', {
    get: function () {
      return this.redI / 255;
    }
  });
  Object.defineProperty(Color.prototype, 'greenF', {
    get: function () {
      return this.greenI / 255;
    }
  });
  Object.defineProperty(Color.prototype, 'blueF', {
    get: function () {
      return this.blueI / 255;
    }
  });
  Color.prototype.alphaI_za3lpa$ = function (value) {
    return value >> 24 & 255;
  };
  Color.prototype.redI_za3lpa$ = function (value) {
    return value >> 16 & 255;
  };
  Color.prototype.greenI_za3lpa$ = function (value) {
    return value >> 8 & 255;
  };
  Color.prototype.blueI_za3lpa$ = function (value) {
    return value & 255;
  };
  Object.defineProperty(Color.prototype, 'rgb', {
    get: function () {
      return this.argb & 16777215;
    }
  });
  Color.prototype.toInt = function () {
    return this.argb;
  };
  Color.prototype.toHexString = function () {
    return '#' + this.maybe_0(this.alphaI) + this.toHexString_s8ev3n$(this.redI) + this.toHexString_s8ev3n$(this.greenI) + this.toHexString_s8ev3n$(this.blueI);
  };
  Color.prototype.maybe_0 = function (alphaI) {
    return alphaI === 255 ? '' : this.toHexString_s8ev3n$(alphaI);
  };
  Color.prototype.toHexString_s8ev3n$ = function ($receiver) {
    if ($receiver < 0) {
      throw Exception_init("can't toHexString() negative ints");
    }
    if ($receiver < 16) {
      return '0' + toString($receiver, 16);
    }
     else {
      return toString($receiver, 16);
    }
  };
  Color.prototype.withSaturation_mx4ult$ = function (saturation) {
    var desaturation = 1 - saturation;
    return Color_init_0(this.redF + (1 - this.redF) * desaturation, this.greenF + (1 - this.greenF) * desaturation, this.blueF + (1 - this.blueF) * desaturation, this.alphaF);
  };
  Color.prototype.distanceTo_rny0jj$ = function (other) {
    var dist = this.square_0(other.redF - this.redF) + this.square_0(other.greenF - this.greenF) + this.square_0(other.blueF - this.blueF);
    var x = dist / 3;
    return Math_0.sqrt(x);
  };
  Color.prototype.square_0 = function (f) {
    return f * f;
  };
  Color.prototype.plus_rny0jj$ = function (other) {
    return Color_init_1(this.redI + other.redI | 0, this.greenI + other.greenI | 0, this.blueI + other.blueI | 0, this.alphaI);
  };
  Color.prototype.fade_6zkv30$ = function (other, amount) {
    if (amount === void 0)
      amount = 0.5;
    var amountThis = 1 - amount;
    return Color_init_0(this.redF * amountThis + other.redF * amount, this.greenF * amountThis + other.greenF * amount, this.blueF * amountThis + other.blueF * amount, this.alphaF * amountThis + other.alphaF * amount);
  };
  Color.prototype.opaque = function () {
    return new Color(this.argb | -16777216);
  };
  Color.prototype.toString = function () {
    return 'Color(' + this.toHexString() + ')';
  };
  function Color$Companion() {
    Color$Companion_instance = this;
    this.BLACK = Color_init_1(0, 0, 0);
    this.WHITE = Color_init_1(255, 255, 255);
    this.RED = Color_init_1(255, 0, 0);
    this.ORANGE = Color_init_1(255, 127, 0);
    this.YELLOW = Color_init_1(255, 255, 0);
    this.GREEN = Color_init_1(0, 255, 0);
    this.CYAN = Color_init_1(0, 255, 255);
    this.BLUE = Color_init_1(0, 0, 255);
    this.MAGENTA = Color_init_1(255, 0, 255);
    this.PURPLE = Color_init_1(200, 0, 212);
    this.TRANSPARENT = Color_init_1(0, 0, 0, 0);
    this.descriptor_dxdv46$_0 = withName(internal.IntDescriptor, 'Color');
  }
  Color$Companion.prototype.random = function () {
    return Color_init_1(Random.Default.nextInt() & 255, Random.Default.nextInt() & 255, Random.Default.nextInt() & 255);
  };
  Color$Companion.prototype.parse_100t80$ = function (reader) {
    return new Color(reader.readInt());
  };
  Color$Companion.prototype.fromInt = function (i) {
    return new Color(i);
  };
  Color$Companion.prototype.fromInts = function (r, g, b) {
    return Color_init_1(r, g, b);
  };
  Color$Companion.prototype.fromString = function (hex) {
    var tmp$;
    var hexDigits = {v: trimStart(hex, Kotlin.charArrayOf(35))};
    if (hexDigits.v.length === 8) {
      var $receiver = toInt(hexDigits.v.substring(0, 2), 16);
      hexDigits.v = hexDigits.v.substring(2);
      tmp$ = $receiver;
    }
     else {
      tmp$ = 255;
    }
    var alpha = tmp$;
    if (hexDigits.v.length === 6) {
      return new Color(alpha << 24 | toInt(hexDigits.v, 16));
    }
    throw IllegalArgumentException_init('unknown color ' + '"' + hex + '"');
  };
  Color$Companion.prototype.asArgb_1 = function (red, green, blue, alpha) {
    if (alpha === void 0)
      alpha = 1.0;
    var asArgb = this.asArgb_0(this.asInt_0(red), this.asInt_0(green), this.asInt_0(blue), this.asInt_0(alpha));
    return asArgb;
  };
  Color$Companion.prototype.asArgb_0 = function (red, green, blue, alpha) {
    if (alpha === void 0)
      alpha = 255;
    return this.bounded_0(alpha) << 24 | this.bounded_0(red) << 16 | this.bounded_0(green) << 8 | this.bounded_0(blue);
  };
  Color$Companion.prototype.asArgb_2 = function (red, green, blue, alpha) {
    if (alpha === void 0)
      alpha = toByte(255);
    return this.bounded_1(alpha) << 24 | this.bounded_1(red) << 16 | this.bounded_1(green) << 8 | this.bounded_1(blue);
  };
  Color$Companion.prototype.bounded_2 = function (f) {
    var b = Math_0.min(1.0, f);
    return Math_0.max(0.0, b);
  };
  Color$Companion.prototype.bounded_0 = function (i) {
    var b = Math_0.min(255, i);
    return Math_0.max(0, b);
  };
  Color$Companion.prototype.bounded_1 = function (b) {
    return b & 255;
  };
  Color$Companion.prototype.asInt_0 = function (f) {
    return numberToInt(this.bounded_2(f) * 255);
  };
  Object.defineProperty(Color$Companion.prototype, 'descriptor', {
    get: function () {
      return this.descriptor_dxdv46$_0;
    }
  });
  Color$Companion.prototype.serialize_awe97i$ = function (encoder, obj) {
    encoder.encodeInt_za3lpa$(obj.argb);
  };
  Color$Companion.prototype.deserialize_nts5qn$ = function (decoder) {
    return new Color(decoder.decodeInt());
  };
  Color$Companion.prototype.serializer = function () {
    return Color$Companion_getInstance();
  };
  Color$Companion.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'Companion',
    interfaces: [KSerializer]
  };
  var Color$Companion_instance = null;
  function Color$Companion_getInstance() {
    if (Color$Companion_instance === null) {
      new Color$Companion();
    }
    return Color$Companion_instance;
  }
  Color.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Color',
    interfaces: []
  };
  function Color_init_0(red, green, blue, alpha, $this) {
    if (alpha === void 0)
      alpha = 1.0;
    $this = $this || Object.create(Color.prototype);
    Color.call($this, Color$Companion_getInstance().asArgb_1(red, green, blue, alpha));
    return $this;
  }
  function Color_init_1(red, green, blue, alpha, $this) {
    if (alpha === void 0)
      alpha = 255;
    $this = $this || Object.create(Color.prototype);
    Color.call($this, Color$Companion_getInstance().asArgb_0(red, green, blue, alpha));
    return $this;
  }
  function Color_init_2(red, green, blue, alpha, $this) {
    if (alpha === void 0)
      alpha = toByte(255);
    $this = $this || Object.create(Color.prototype);
    Color.call($this, Color$Companion_getInstance().asArgb_2(red, green, blue, alpha));
    return $this;
  }
  Color.prototype.component1 = function () {
    return this.argb;
  };
  Color.prototype.copy_za3lpa$ = function (argb) {
    return new Color(argb === void 0 ? this.argb : argb);
  };
  Color.prototype.hashCode = function () {
    var result = 0;
    result = result * 31 + Kotlin.hashCode(this.argb) | 0;
    return result;
  };
  Color.prototype.equals = function (other) {
    return this === other || (other !== null && (typeof other === 'object' && (Object.getPrototypeOf(this) === Object.getPrototypeOf(other) && Kotlin.equals(this.argb, other.argb))));
  };
  function Config() {
    Config$Companion_getInstance();
  }
  function Config$Companion() {
    Config$Companion_instance = this;
    this.DMX_DEVICES = mapOf([new Pair('leftEye', 1), new Pair('rightEye', 17)]);
  }
  Config$Companion.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'Companion',
    interfaces: []
  };
  var Config$Companion_instance = null;
  function Config$Companion_getInstance() {
    if (Config$Companion_instance === null) {
      new Config$Companion();
    }
    return Config$Companion_instance;
  }
  function Config$MovingHeadConfig(deviceType, baseChannel) {
    this.deviceType = deviceType;
    this.baseChannel = baseChannel;
  }
  Config$MovingHeadConfig.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'MovingHeadConfig',
    interfaces: []
  };
  Config.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Config',
    interfaces: []
  };
  function Display() {
  }
  Display.$metadata$ = {
    kind: Kind_INTERFACE,
    simpleName: 'Display',
    interfaces: []
  };
  function NetworkDisplay() {
  }
  NetworkDisplay.$metadata$ = {
    kind: Kind_INTERFACE,
    simpleName: 'NetworkDisplay',
    interfaces: []
  };
  function PinkyDisplay() {
  }
  PinkyDisplay.$metadata$ = {
    kind: Kind_INTERFACE,
    simpleName: 'PinkyDisplay',
    interfaces: []
  };
  function StubPinkyDisplay() {
    this.brainCount_cnxvq4$_0 = 0;
    this.beat_6xdvy1$_0 = 0;
    this.bpm_ijxrzc$_0 = 0;
    this.onShowChange_33sz01$_0 = StubPinkyDisplay$onShowChange$lambda;
    this.selectedShow_fwpmt$_0 = null;
    this.showFrameMs_1he7j5$_0 = 0;
    this.stats_6me6mw$_0 = null;
  }
  StubPinkyDisplay.prototype.listShows_3lsa6o$ = function (shows) {
  };
  Object.defineProperty(StubPinkyDisplay.prototype, 'brainCount', {
    get: function () {
      return this.brainCount_cnxvq4$_0;
    },
    set: function (brainCount) {
      this.brainCount_cnxvq4$_0 = brainCount;
    }
  });
  Object.defineProperty(StubPinkyDisplay.prototype, 'beat', {
    get: function () {
      return this.beat_6xdvy1$_0;
    },
    set: function (beat) {
      this.beat_6xdvy1$_0 = beat;
    }
  });
  Object.defineProperty(StubPinkyDisplay.prototype, 'bpm', {
    get: function () {
      return this.bpm_ijxrzc$_0;
    },
    set: function (bpm) {
      this.bpm_ijxrzc$_0 = bpm;
    }
  });
  Object.defineProperty(StubPinkyDisplay.prototype, 'onShowChange', {
    get: function () {
      return this.onShowChange_33sz01$_0;
    },
    set: function (onShowChange) {
      this.onShowChange_33sz01$_0 = onShowChange;
    }
  });
  Object.defineProperty(StubPinkyDisplay.prototype, 'selectedShow', {
    get: function () {
      return this.selectedShow_fwpmt$_0;
    },
    set: function (selectedShow) {
      this.selectedShow_fwpmt$_0 = selectedShow;
    }
  });
  Object.defineProperty(StubPinkyDisplay.prototype, 'showFrameMs', {
    get: function () {
      return this.showFrameMs_1he7j5$_0;
    },
    set: function (showFrameMs) {
      this.showFrameMs_1he7j5$_0 = showFrameMs;
    }
  });
  Object.defineProperty(StubPinkyDisplay.prototype, 'stats', {
    get: function () {
      return this.stats_6me6mw$_0;
    },
    set: function (stats) {
      this.stats_6me6mw$_0 = stats;
    }
  });
  function StubPinkyDisplay$onShowChange$lambda() {
    return Unit;
  }
  StubPinkyDisplay.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'StubPinkyDisplay',
    interfaces: [PinkyDisplay]
  };
  function BrainDisplay() {
  }
  BrainDisplay.$metadata$ = {
    kind: Kind_INTERFACE,
    simpleName: 'BrainDisplay',
    interfaces: []
  };
  function VisualizerDisplay() {
  }
  VisualizerDisplay.$metadata$ = {
    kind: Kind_INTERFACE,
    simpleName: 'VisualizerDisplay',
    interfaces: []
  };
  function Dmx() {
  }
  function Dmx$Universe() {
  }
  Dmx$Universe.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Universe',
    interfaces: []
  };
  function Dmx$Buffer(channels, baseChannel, channelCount) {
    this.channels_0 = channels;
    this.baseChannel = baseChannel;
    this.channelCount = channelCount;
  }
  Dmx$Buffer.prototype.get_6ui4v4$ = function (channel) {
    return this.get_za3lpa$(channel.offset);
  };
  Dmx$Buffer.prototype.get_za3lpa$ = function (index) {
    this.boundsCheck_0(index);
    return this.channels_0[this.baseChannel + index | 0];
  };
  Dmx$Buffer.prototype.set_h90ill$ = function (channel, value) {
    this.set_6t1wet$(channel.offset, value);
  };
  Dmx$Buffer.prototype.set_6t1wet$ = function (index, value) {
    this.boundsCheck_0(index);
    this.channels_0[this.baseChannel + index | 0] = value;
  };
  Dmx$Buffer.prototype.boundsCheck_0 = function (index) {
    if (index < 0 || index >= this.channelCount) {
      throw Exception_init('index out of bounds: ' + index + ' >= ' + this.channelCount);
    }
  };
  Dmx$Buffer.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Buffer',
    interfaces: []
  };
  function Dmx$Channel() {
  }
  Dmx$Channel.$metadata$ = {
    kind: Kind_INTERFACE,
    simpleName: 'Channel',
    interfaces: []
  };
  function Dmx$DeviceType(channelCount) {
    this.channelCount = channelCount;
  }
  Dmx$DeviceType.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'DeviceType',
    interfaces: []
  };
  Dmx.$metadata$ = {
    kind: Kind_INTERFACE,
    simpleName: 'Dmx',
    interfaces: []
  };
  function FirmwareDaddy() {
  }
  FirmwareDaddy.$metadata$ = {
    kind: Kind_INTERFACE,
    simpleName: 'FirmwareDaddy',
    interfaces: []
  };
  function PermissiveFirmwareDaddy() {
  }
  PermissiveFirmwareDaddy.prototype.doesntLikeThisVersion_pdl1vj$ = function (firmwareVersion) {
    return false;
  };
  Object.defineProperty(PermissiveFirmwareDaddy.prototype, 'urlForPreferredVersion', {
    get: function () {
      return '';
    }
  });
  PermissiveFirmwareDaddy.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'PermissiveFirmwareDaddy',
    interfaces: [FirmwareDaddy]
  };
  function StrictFirmwareDaddy(version, url) {
    this.version_0 = version;
    this.url_0 = url;
  }
  StrictFirmwareDaddy.prototype.doesntLikeThisVersion_pdl1vj$ = function (firmwareVersion) {
    return equals(this.version_0, firmwareVersion);
  };
  Object.defineProperty(StrictFirmwareDaddy.prototype, 'urlForPreferredVersion', {
    get: function () {
      return this.url_0;
    }
  });
  StrictFirmwareDaddy.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'StrictFirmwareDaddy',
    interfaces: [FirmwareDaddy]
  };
  function Gadget() {
    this.listeners_zg49rb$_0 = LinkedHashSet_init();
    this.state = HashMap_init();
  }
  Gadget.prototype.listen = function (gadgetListener) {
    if (!this.listeners_zg49rb$_0.add_11rb$(gadgetListener))
      throw IllegalStateException_init(gadgetListener.toString() + ' already listening to ' + this);
  };
  Gadget.prototype.unlisten = function (gadgetListener) {
    if (!this.listeners_zg49rb$_0.remove_11rb$(gadgetListener))
      throw IllegalStateException_init(gadgetListener.toString() + " isn't listening to " + this);
  };
  Gadget.prototype.changed = function () {
    var tmp$;
    tmp$ = this.listeners_zg49rb$_0.iterator();
    while (tmp$.hasNext()) {
      var element = tmp$.next();
      element(this);
    }
  };
  Gadget.prototype.withoutTriggering_s6xcqc$ = function (gadgetListener, fn) {
    this.unlisten(gadgetListener);
    try {
      fn();
    }
    finally {
      this.listen(gadgetListener);
    }
  };
  function Gadget$updatable$lambda(this$Gadget) {
    return function () {
      this$Gadget.changed();
      return Unit;
    };
  }
  Gadget.prototype.updatable_t7zvzq$ = function (name, initialValue, serializer) {
    return new GadgetValueObserver(name, initialValue, serializer, this.state, Gadget$updatable$lambda(this));
  };
  Gadget.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Gadget',
    interfaces: []
  };
  function GadgetValueObserver(name, initialValue, serializer, data, onChange) {
    this.name = name;
    this.initialValue = initialValue;
    this.serializer_0 = serializer;
    this.data = data;
    this.onChange = onChange;
  }
  GadgetValueObserver.prototype.getValue_lrcp0p$ = function (thisRef, property) {
    var tmp$;
    var value = this.data.get_11rb$(this.name);
    if (value == null)
      tmp$ = this.initialValue;
    else {
      tmp$ = jsonParser.fromJson_htt2tq$(this.serializer_0, value);
    }
    return tmp$;
  };
  GadgetValueObserver.prototype.setValue_9rddgb$ = function (thisRef, property, value) {
    if (!equals(this.getValue_lrcp0p$(thisRef, property), value)) {
      var $receiver = this.data;
      var key = this.name;
      var value_0 = jsonParser.toJson_tf03ej$(this.serializer_0, value);
      $receiver.put_xwzc9p$(key, value_0);
      this.onChange();
    }
  };
  GadgetValueObserver.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'GadgetValueObserver',
    interfaces: [ReadWriteProperty]
  };
  function GadgetData(name, gadget, topicName) {
    GadgetData$Companion_getInstance();
    this.name = name;
    this.gadget = gadget;
    this.topicName = topicName;
  }
  function GadgetData$Companion() {
    GadgetData$Companion_instance = this;
  }
  GadgetData$Companion.prototype.serializer = function () {
    return GadgetData$$serializer_getInstance();
  };
  GadgetData$Companion.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'Companion',
    interfaces: []
  };
  var GadgetData$Companion_instance = null;
  function GadgetData$Companion_getInstance() {
    if (GadgetData$Companion_instance === null) {
      new GadgetData$Companion();
    }
    return GadgetData$Companion_instance;
  }
  function GadgetData$$serializer() {
    this.descriptor_d3e1xb$_0 = new SerialClassDescImpl('baaahs.GadgetData', this);
    this.descriptor.addElement_ivxn3r$('name', false);
    this.descriptor.addElement_ivxn3r$('gadget', false);
    this.descriptor.addElement_ivxn3r$('topicName', false);
    GadgetData$$serializer_instance = this;
  }
  Object.defineProperty(GadgetData$$serializer.prototype, 'descriptor', {
    get: function () {
      return this.descriptor_d3e1xb$_0;
    }
  });
  GadgetData$$serializer.prototype.serialize_awe97i$ = function (encoder, obj) {
    var output = encoder.beginStructure_r0sa6z$(this.descriptor, []);
    output.encodeStringElement_bgm7zs$(this.descriptor, 0, obj.name);
    output.encodeSerializableElement_blecud$(this.descriptor, 1, new PolymorphicSerializer(getKClass(Gadget)), obj.gadget);
    output.encodeStringElement_bgm7zs$(this.descriptor, 2, obj.topicName);
    output.endStructure_qatsm0$(this.descriptor);
  };
  GadgetData$$serializer.prototype.deserialize_nts5qn$ = function (decoder) {
    var index, readAll = false;
    var bitMask0 = 0;
    var local0
    , local1
    , local2;
    var input = decoder.beginStructure_r0sa6z$(this.descriptor, []);
    loopLabel: while (true) {
      index = input.decodeElementIndex_qatsm0$(this.descriptor);
      switch (index) {
        case -2:
          readAll = true;
        case 0:
          local0 = input.decodeStringElement_3zr2iy$(this.descriptor, 0);
          bitMask0 |= 1;
          if (!readAll)
            break;
        case 1:
          local1 = (bitMask0 & 2) === 0 ? input.decodeSerializableElement_s44l7r$(this.descriptor, 1, new PolymorphicSerializer(getKClass(Gadget))) : input.updateSerializableElement_ehubvl$(this.descriptor, 1, new PolymorphicSerializer(getKClass(Gadget)), local1);
          bitMask0 |= 2;
          if (!readAll)
            break;
        case 2:
          local2 = input.decodeStringElement_3zr2iy$(this.descriptor, 2);
          bitMask0 |= 4;
          if (!readAll)
            break;
        case -1:
          break loopLabel;
        default:throw new UnknownFieldException(index);
      }
    }
    input.endStructure_qatsm0$(this.descriptor);
    return GadgetData_init(bitMask0, local0, local1, local2, null);
  };
  GadgetData$$serializer.prototype.childSerializers = function () {
    return [internal.StringSerializer, new PolymorphicSerializer(getKClass(Gadget)), internal.StringSerializer];
  };
  GadgetData$$serializer.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: '$serializer',
    interfaces: [GeneratedSerializer]
  };
  var GadgetData$$serializer_instance = null;
  function GadgetData$$serializer_getInstance() {
    if (GadgetData$$serializer_instance === null) {
      new GadgetData$$serializer();
    }
    return GadgetData$$serializer_instance;
  }
  function GadgetData_init(seen1, name, gadget, topicName, serializationConstructorMarker) {
    var $this = serializationConstructorMarker || Object.create(GadgetData.prototype);
    if ((seen1 & 1) === 0)
      throw new MissingFieldException('name');
    else
      $this.name = name;
    if ((seen1 & 2) === 0)
      throw new MissingFieldException('gadget');
    else
      $this.gadget = gadget;
    if ((seen1 & 4) === 0)
      throw new MissingFieldException('topicName');
    else
      $this.topicName = topicName;
    return $this;
  }
  GadgetData.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'GadgetData',
    interfaces: []
  };
  var GadgetDataSerializer;
  function GadgetDisplay(pubSub, onUpdatedGadgets) {
    this.activeGadgets = ArrayList_init();
    this.channels = HashMap_init();
    pubSub.subscribe(Topics_getInstance().activeGadgets, GadgetDisplay_init$lambda(this, pubSub, onUpdatedGadgets));
  }
  function GadgetDisplay_init$lambda$lambda$lambda(this$GadgetDisplay, closure$topicName) {
    return function (it) {
      var observer = this$GadgetDisplay.channels.get_11rb$(closure$topicName);
      if (observer == null) {
        println('Huh, no observer for ' + closure$topicName + '; discarding update (know about ' + this$GadgetDisplay.channels.keys + ')');
      }
       else {
        observer.onChange(it.state);
      }
      return Unit;
    };
  }
  function GadgetDisplay_init$lambda$lambda$lambda$lambda$lambda(closure$gadget, closure$json) {
    return function () {
      closure$gadget.state.putAll_a2k3zr$(closure$json);
      closure$gadget.changed();
      return Unit;
    };
  }
  function GadgetDisplay_init$lambda$lambda$lambda_0(closure$gadget, closure$listener) {
    return function (json) {
      var $receiver = closure$gadget;
      $receiver.withoutTriggering_s6xcqc$(closure$listener, GadgetDisplay_init$lambda$lambda$lambda$lambda$lambda(closure$gadget, json));
      return Unit;
    };
  }
  function GadgetDisplay_init$lambda(this$GadgetDisplay, closure$pubSub, closure$onUpdatedGadgets) {
    return function (gadgetDatas) {
      this$GadgetDisplay.activeGadgets.clear();
      var tmp$;
      tmp$ = this$GadgetDisplay.channels.entries.iterator();
      while (tmp$.hasNext()) {
        var element = tmp$.next();
        element.value.unsubscribe();
      }
      this$GadgetDisplay.channels.clear();
      var tmp$_0;
      tmp$_0 = gadgetDatas.iterator();
      while (tmp$_0.hasNext()) {
        var element_0 = tmp$_0.next();
        var this$GadgetDisplay_0 = this$GadgetDisplay;
        var closure$pubSub_0 = closure$pubSub;
        var gadget = element_0.gadget;
        var topicName = element_0.topicName;
        var listener = GadgetDisplay_init$lambda$lambda$lambda(this$GadgetDisplay_0, topicName);
        gadget.listen(listener);
        var $receiver = this$GadgetDisplay_0.channels;
        var value = closure$pubSub_0.subscribe(new PubSub$Topic(topicName, GadgetDataSerializer), GadgetDisplay_init$lambda$lambda$lambda_0(gadget, listener));
        $receiver.put_xwzc9p$(topicName, value);
        this$GadgetDisplay_0.activeGadgets.add_11rb$(element_0);
      }
      closure$onUpdatedGadgets(copyToArray(this$GadgetDisplay.activeGadgets));
      return Unit;
    };
  }
  GadgetDisplay.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'GadgetDisplay',
    interfaces: []
  };
  function gadgetModule$lambda$lambda($receiver) {
    $receiver.with_kmpi2j$(getKClass(ColorPicker), ColorPicker$Companion_getInstance().serializer());
    $receiver.with_kmpi2j$(getKClass(PalettePicker), PalettePicker$Companion_getInstance().serializer());
    $receiver.with_kmpi2j$(getKClass(Slider), Slider$Companion_getInstance().serializer());
    return Unit;
  }
  function gadgetModule$lambda($receiver) {
    $receiver.polymorphic_myr6su$(getKClass(Gadget), [], gadgetModule$lambda$lambda);
    return Unit;
  }
  var gadgetModule;
  var jsonParser;
  function array($receiver, kKlass) {
    return new ReferenceArraySerializer(kKlass, $receiver);
  }
  function GadgetManager(pubSub) {
    this.pubSub_0 = pubSub;
    this.activeGadgets_0 = ArrayList_init();
    this.activeGadgetChannel_0 = this.pubSub_0.publish_oiz02e$(Topics_getInstance().activeGadgets, this.activeGadgets_0, GadgetManager$activeGadgetChannel$lambda);
    this.gadgets_0 = LinkedHashMap_init();
    this.priorRequestedGadgets_0 = ArrayList_init();
    this.nextGadgetId_0 = 1;
  }
  GadgetManager.prototype.clear = function () {
    var tmp$;
    tmp$ = this.gadgets_0.values.iterator();
    while (tmp$.hasNext()) {
      var element = tmp$.next();
      element.channel.unsubscribe();
    }
    this.gadgets_0.clear();
    this.activeGadgets_0.clear();
  };
  function GadgetManager$sync$lambda$lambda(closure$newGadget) {
    return function (updated) {
      closure$newGadget.state.putAll_a2k3zr$(updated);
      return Unit;
    };
  }
  function GadgetManager$sync$lambda$lambda_0(closure$gadget) {
    return function (updated) {
      closure$gadget.state.putAll_a2k3zr$(updated);
      return Unit;
    };
  }
  GadgetManager.prototype.sync_7kvwdj$ = function (requestedGadgets, restoreState) {
    if (restoreState === void 0)
      restoreState = emptyMap();
    var tmp$;
    tmp$ = requestedGadgets.iterator();
    while (tmp$.hasNext()) {
      var element = tmp$.next();
      var name = element.component1()
      , gadget = element.component2();
      var tmp$_0;
      if ((tmp$_0 = restoreState.get_11rb$(name)) != null) {
        gadget.state.putAll_a2k3zr$(tmp$_0);
      }
    }
    if (equals(this.priorRequestedGadgets_0, requestedGadgets)) {
      var tmp$_1;
      tmp$_1 = zip(requestedGadgets, this.priorRequestedGadgets_0).iterator();
      while (tmp$_1.hasNext()) {
        var element_0 = tmp$_1.next();
        var new_0 = element_0.component1()
        , old = element_0.component2();
        var name_0 = old.component1()
        , oldGadget = old.component2();
        var newGadget = new_0.second;
        var gadgetInfo = ensureNotNull(this.gadgets_0.get_11rb$(name_0));
        gadgetInfo.channel.replaceOnUpdate_qlkmfe$(GadgetManager$sync$lambda$lambda(newGadget));
        gadgetInfo.gadgetData.gadget = newGadget;
        if (!equals(oldGadget.state, newGadget.state)) {
          gadgetInfo.channel.onChange(newGadget.state);
        }
      }
    }
     else {
      logger$Companion_getInstance().debug_61zpoe$("Gadgets don't match!\n" + ('old: ' + this.priorRequestedGadgets_0 + '\n') + ('new: ' + requestedGadgets));
      this.activeGadgets_0.clear();
      var tmp$_2;
      tmp$_2 = requestedGadgets.iterator();
      while (tmp$_2.hasNext()) {
        var element_1 = tmp$_2.next();
        var name_1 = element_1.component1()
        , gadget_0 = element_1.component2();
        var tmp$_3;
        var gadgetId = (tmp$_3 = this.nextGadgetId_0, this.nextGadgetId_0 = tmp$_3 + 1 | 0, tmp$_3);
        var topic = new PubSub$Topic('/gadgets/' + toString_0(Kotlin.getKClassFromExpression(gadget_0).simpleName) + '/' + gadgetId, GadgetDataSerializer);
        var channel = this.pubSub_0.publish_oiz02e$(topic, gadget_0.state, GadgetManager$sync$lambda$lambda_0(gadget_0));
        var gadgetData = new GadgetData(name_1, gadget_0, topic.name);
        this.activeGadgets_0.add_11rb$(gadgetData);
        var $receiver = this.gadgets_0;
        var value = new GadgetManager$GadgetInfo(topic, channel, gadgetData);
        $receiver.put_xwzc9p$(name_1, value);
      }
      this.activeGadgetChannel_0.onChange(this.activeGadgets_0);
    }
    this.priorRequestedGadgets_0.clear();
    this.priorRequestedGadgets_0.addAll_brywnq$(requestedGadgets);
  };
  GadgetManager.prototype.getGadgetsState = function () {
    var $receiver = this.activeGadgets_0;
    var capacity = coerceAtLeast(mapCapacity(collectionSizeOrDefault($receiver, 10)), 16);
    var destination = LinkedHashMap_init_0(capacity);
    var tmp$;
    tmp$ = $receiver.iterator();
    while (tmp$.hasNext()) {
      var element = tmp$.next();
      var pair = to(element.name, element.gadget.state);
      destination.put_xwzc9p$(pair.first, pair.second);
    }
    return destination;
  };
  GadgetManager.prototype.findGadget_y4putb$ = function (name) {
    var tmp$, tmp$_0;
    return (tmp$_0 = (tmp$ = this.gadgets_0.get_11rb$(name)) != null ? tmp$.gadgetData : null) != null ? tmp$_0.gadget : null;
  };
  GadgetManager.prototype.findGadgetInfo_y4putb$ = function (name) {
    return this.gadgets_0.get_11rb$(name);
  };
  function GadgetManager$GadgetInfo(topic, channel, gadgetData) {
    this.topic = topic;
    this.channel = channel;
    this.gadgetData = gadgetData;
  }
  GadgetManager$GadgetInfo.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'GadgetInfo',
    interfaces: []
  };
  function GadgetManager$activeGadgetChannel$lambda(it) {
    return Unit;
  }
  GadgetManager.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'GadgetManager',
    interfaces: []
  };
  function Comparator$ObjectLiteral(closure$comparison) {
    this.closure$comparison = closure$comparison;
  }
  Comparator$ObjectLiteral.prototype.compare = function (a, b) {
    return this.closure$comparison(a, b);
  };
  Comparator$ObjectLiteral.$metadata$ = {kind: Kind_CLASS, interfaces: [Comparator]};
  var compareByDescending$lambda = wrapFunction(function () {
    var compareValues = Kotlin.kotlin.comparisons.compareValues_s00gnj$;
    return function (closure$selector) {
      return function (a, b) {
        var selector = closure$selector;
        return compareValues(selector(b), selector(a));
      };
    };
  });
  function Mapper(network, sheepModel, mapperUi, mediaDevices, pinkyAddress) {
    this.network_0 = network;
    this.mapperUi_0 = mapperUi;
    this.mediaDevices_0 = mediaDevices;
    this.pinkyAddress_0 = pinkyAddress;
    this.$delegate_9rrh7p$_0 = MainScope();
    this.maxPixelsPerBrain_0 = 120;
    this.camera_iftyng$_0 = this.camera_iftyng$_0;
    this.baseBitmap_0 = null;
    this.link_tktc8n$_0 = this.link_tktc8n$_0;
    this.udpSocket_eiksen$_0 = this.udpSocket_eiksen$_0;
    this.mapperClient_pocmit$_0 = this.mapperClient_pocmit$_0;
    this.isRunning_0 = false;
    this.isPaused_0 = false;
    this.newIncomingImage_0 = null;
    this.suppressShowsJob_0 = null;
    this.brainsToMap_0 = LinkedHashMap_init();
    this.activeColor_0 = Color_init_1(7, 255, 7);
    this.inactiveColor_0 = Color_init_1(7, 0, 7);
    this.detectors = [Mapper$Detector$GREEN_getInstance(), Mapper$Detector$GREEN_getInstance(), Mapper$Detector$GREEN_getInstance()];
    this.redRgbaIndex_0 = 0;
    this.greenRgbaIndex_0 = 1;
    this.blueRgbaIndex_0 = 2;
    this.signalRgbaIndex_0 = this.blueRgbaIndex_0;
    this.indicatorRgbaIndex_0 = this.greenRgbaIndex_0;
    this.mapperUi_0.listen_97503t$(this);
    this.mapperUi_0.addWireframe_9u144y$(sheepModel);
    this.deliverer_0 = new Mapper$ReliableShaderMessageDeliverer(this);
  }
  Object.defineProperty(Mapper.prototype, 'camera', {
    get: function () {
      if (this.camera_iftyng$_0 == null)
        return throwUPAE('camera');
      return this.camera_iftyng$_0;
    },
    set: function (camera) {
      this.camera_iftyng$_0 = camera;
    }
  });
  Object.defineProperty(Mapper.prototype, 'link_0', {
    get: function () {
      if (this.link_tktc8n$_0 == null)
        return throwUPAE('link');
      return this.link_tktc8n$_0;
    },
    set: function (link) {
      this.link_tktc8n$_0 = link;
    }
  });
  Object.defineProperty(Mapper.prototype, 'udpSocket_0', {
    get: function () {
      if (this.udpSocket_eiksen$_0 == null)
        return throwUPAE('udpSocket');
      return this.udpSocket_eiksen$_0;
    },
    set: function (udpSocket) {
      this.udpSocket_eiksen$_0 = udpSocket;
    }
  });
  Object.defineProperty(Mapper.prototype, 'mapperClient_0', {
    get: function () {
      if (this.mapperClient_pocmit$_0 == null)
        return throwUPAE('mapperClient');
      return this.mapperClient_pocmit$_0;
    },
    set: function (mapperClient) {
      this.mapperClient_pocmit$_0 = mapperClient;
    }
  });
  function Mapper$Detector(name, ordinal, rgbaIndex, color, alternateColor) {
    Enum.call(this);
    this.rgbaIndex = rgbaIndex;
    this.color = color;
    this.alternateColor = alternateColor;
    this.name$ = name;
    this.ordinal$ = ordinal;
  }
  function Mapper$Detector_initFields() {
    Mapper$Detector_initFields = function () {
    };
    Mapper$Detector$RED_instance = new Mapper$Detector('RED', 0, 0, Color$Companion_getInstance().RED, Color$Companion_getInstance().CYAN);
    Mapper$Detector$GREEN_instance = new Mapper$Detector('GREEN', 1, 1, Color_init_1(1, 255, 1), Color_init_1(1, 0, 1));
    Mapper$Detector$BLUE_instance = new Mapper$Detector('BLUE', 2, 2, Color$Companion_getInstance().BLUE, Color$Companion_getInstance().YELLOW);
  }
  var Mapper$Detector$RED_instance;
  function Mapper$Detector$RED_getInstance() {
    Mapper$Detector_initFields();
    return Mapper$Detector$RED_instance;
  }
  var Mapper$Detector$GREEN_instance;
  function Mapper$Detector$GREEN_getInstance() {
    Mapper$Detector_initFields();
    return Mapper$Detector$GREEN_instance;
  }
  var Mapper$Detector$BLUE_instance;
  function Mapper$Detector$BLUE_getInstance() {
    Mapper$Detector_initFields();
    return Mapper$Detector$BLUE_instance;
  }
  Mapper$Detector.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Detector',
    interfaces: [Enum]
  };
  function Mapper$Detector$values() {
    return [Mapper$Detector$RED_getInstance(), Mapper$Detector$GREEN_getInstance(), Mapper$Detector$BLUE_getInstance()];
  }
  Mapper$Detector.values = Mapper$Detector$values;
  function Mapper$Detector$valueOf(name) {
    switch (name) {
      case 'RED':
        return Mapper$Detector$RED_getInstance();
      case 'GREEN':
        return Mapper$Detector$GREEN_getInstance();
      case 'BLUE':
        return Mapper$Detector$BLUE_getInstance();
      default:throwISE('No enum constant baaahs.Mapper.Detector.' + name);
    }
  }
  Mapper$Detector.valueOf_61zpoe$ = Mapper$Detector$valueOf;
  function Coroutine$Mapper$start$lambda$lambda(this$Mapper_0, $receiver_0, controller, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.$controller = controller;
    this.exceptionState_0 = 1;
    this.local$this$Mapper = this$Mapper_0;
  }
  Coroutine$Mapper$start$lambda$lambda.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$Mapper$start$lambda$lambda.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$Mapper$start$lambda$lambda.prototype.constructor = Coroutine$Mapper$start$lambda$lambda;
  Coroutine$Mapper$start$lambda$lambda.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.state_0 = 2;
            this.result_0 = this.local$this$Mapper.mapperClient_0.listSessions(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 1:
            throw this.exception_0;
          case 2:
            var tmp$;
            tmp$ = this.result_0.iterator();
            while (tmp$.hasNext()) {
              var element = tmp$.next();
              this.local$this$Mapper.mapperUi_0.addExistingSession_61zpoe$(element);
            }

            return Unit;
          default:this.state_0 = 1;
            throw new Error('State Machine Unreachable execution');
        }
      }
       catch (e) {
        if (this.state_0 === 1) {
          this.exceptionState_0 = this.state_0;
          throw e;
        }
         else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  function Mapper$start$lambda$lambda(this$Mapper_0) {
    return function ($receiver_0, continuation_0, suspended) {
      var instance = new Coroutine$Mapper$start$lambda$lambda(this$Mapper_0, $receiver_0, this, continuation_0);
      if (suspended)
        return instance;
      else
        return instance.doResume(null);
    };
  }
  function Coroutine$Mapper$start$lambda(this$Mapper_0, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.exceptionState_0 = 1;
    this.local$this$Mapper = this$Mapper_0;
  }
  Coroutine$Mapper$start$lambda.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$Mapper$start$lambda.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$Mapper$start$lambda.prototype.constructor = Coroutine$Mapper$start$lambda;
  Coroutine$Mapper$start$lambda.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.local$this$Mapper.link_0 = new FragmentingUdpLink(this.local$this$Mapper.network_0.link());
            this.local$this$Mapper.udpSocket_0 = this.local$this$Mapper.link_0.listenUdp_a6m852$(0, this.local$this$Mapper);
            this.local$this$Mapper.mapperClient_0 = new MapperClient(this.local$this$Mapper.link_0, this.local$this$Mapper.pinkyAddress_0);
            return launch(this.local$this$Mapper, void 0, void 0, Mapper$start$lambda$lambda(this.local$this$Mapper));
          case 1:
            throw this.exception_0;
          default:this.state_0 = 1;
            throw new Error('State Machine Unreachable execution');
        }
      }
       catch (e) {
        if (this.state_0 === 1) {
          this.exceptionState_0 = this.state_0;
          throw e;
        }
         else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  function Mapper$start$lambda(this$Mapper_0) {
    return function (continuation_0, suspended) {
      var instance = new Coroutine$Mapper$start$lambda(this$Mapper_0, continuation_0);
      if (suspended)
        return instance;
      else
        return instance.doResume(null);
    };
  }
  Mapper.prototype.start = function () {
    doRunBlocking(Mapper$start$lambda(this));
  };
  function Mapper$onStart$lambda$lambda(this$Mapper) {
    return function (image) {
      this$Mapper.haveImage_0(image);
      return Unit;
    };
  }
  function Coroutine$Mapper$onStart$lambda(this$Mapper_0, $receiver_0, controller, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.$controller = controller;
    this.exceptionState_0 = 1;
    this.local$this$Mapper = this$Mapper_0;
  }
  Coroutine$Mapper$onStart$lambda.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$Mapper$onStart$lambda.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$Mapper$onStart$lambda.prototype.constructor = Coroutine$Mapper$onStart$lambda;
  Coroutine$Mapper$onStart$lambda.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.state_0 = 2;
            this.result_0 = this.local$this$Mapper.startNewSession_0(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 1:
            throw this.exception_0;
          case 2:
            return this.result_0;
          default:this.state_0 = 1;
            throw new Error('State Machine Unreachable execution');
        }
      }
       catch (e) {
        if (this.state_0 === 1) {
          this.exceptionState_0 = this.state_0;
          throw e;
        }
         else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  function Mapper$onStart$lambda(this$Mapper_0) {
    return function ($receiver_0, continuation_0, suspended) {
      var instance = new Coroutine$Mapper$onStart$lambda(this$Mapper_0, $receiver_0, this, continuation_0);
      if (suspended)
        return instance;
      else
        return instance.doResume(null);
    };
  }
  Mapper.prototype.onStart = function () {
    this.isPaused_0 = false;
    if (!this.isRunning_0) {
      var $receiver = this.mediaDevices_0.getCamera();
      $receiver.onImage = Mapper$onStart$lambda$lambda(this);
      this.camera = $receiver;
      this.isRunning_0 = true;
      launch(this, void 0, void 0, Mapper$onStart$lambda(this));
    }
  };
  Mapper.prototype.onPause = function () {
    this.isPaused_0 = true;
  };
  Mapper.prototype.onStop = function () {
    this.onClose();
  };
  Mapper.prototype.onClose = function () {
    var tmp$;
    println('Shutting down Mapper...');
    this.isRunning_0 = false;
    this.camera.close();
    (tmp$ = this.suppressShowsJob_0) != null ? (tmp$.cancel_m4sck1$(), Unit) : null;
    this.udpSocket_0.broadcastUdp_68hu5j$(8002, new MapperHelloMessage(false));
    this.mapperUi_0.close();
  };
  function Coroutine$Mapper$startNewSession$lambda(this$Mapper_0, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.exceptionState_0 = 1;
    this.local$this$Mapper = this$Mapper_0;
  }
  Coroutine$Mapper$startNewSession$lambda.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$Mapper$startNewSession$lambda.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$Mapper$startNewSession$lambda.prototype.constructor = Coroutine$Mapper$startNewSession$lambda;
  Coroutine$Mapper$startNewSession$lambda.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.local$this$Mapper.udpSocket_0.broadcastUdp_68hu5j$(8002, new MapperHelloMessage(true));
            this.state_0 = 2;
            this.result_0 = delay(L1000, this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 1:
            throw this.exception_0;
          case 2:
            return this.local$this$Mapper.udpSocket_0.broadcastUdp_68hu5j$(8003, this.local$this$Mapper.solidColor_0(this.local$this$Mapper.inactiveColor_0)), Unit;
          default:this.state_0 = 1;
            throw new Error('State Machine Unreachable execution');
        }
      }
       catch (e) {
        if (this.state_0 === 1) {
          this.exceptionState_0 = this.state_0;
          throw e;
        }
         else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  function Mapper$startNewSession$lambda(this$Mapper_0) {
    return function (continuation_0, suspended) {
      var instance = new Coroutine$Mapper$startNewSession$lambda(this$Mapper_0, continuation_0);
      if (suspended)
        return instance;
      else
        return instance.doResume(null);
    };
  }
  function Coroutine$Mapper$startNewSession$lambda$lambda(this$Mapper_0, $receiver_0, controller, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.$controller = controller;
    this.exceptionState_0 = 1;
    this.local$this$Mapper = this$Mapper_0;
  }
  Coroutine$Mapper$startNewSession$lambda$lambda.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$Mapper$startNewSession$lambda$lambda.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$Mapper$startNewSession$lambda$lambda.prototype.constructor = Coroutine$Mapper$startNewSession$lambda$lambda;
  Coroutine$Mapper$startNewSession$lambda$lambda.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.local$this$Mapper.udpSocket_0.broadcastUdp_68hu5j$(8003, new BrainIdRequest());
            this.state_0 = 2;
            this.result_0 = delay(L2000, this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 1:
            throw this.exception_0;
          case 2:
            return this.result_0;
          default:this.state_0 = 1;
            throw new Error('State Machine Unreachable execution');
        }
      }
       catch (e) {
        if (this.state_0 === 1) {
          this.exceptionState_0 = this.state_0;
          throw e;
        }
         else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  function Mapper$startNewSession$lambda$lambda(this$Mapper_0) {
    return function ($receiver_0, continuation_0, suspended) {
      var instance = new Coroutine$Mapper$startNewSession$lambda$lambda(this$Mapper_0, $receiver_0, this, continuation_0);
      if (suspended)
        return instance;
      else
        return instance.doResume(null);
    };
  }
  function Coroutine$Mapper$startNewSession$lambda_0(this$Mapper_0, $receiver_0, controller, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.$controller = controller;
    this.exceptionState_0 = 1;
    this.local$this$Mapper = this$Mapper_0;
    this.local$$receiver = $receiver_0;
  }
  Coroutine$Mapper$startNewSession$lambda_0.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$Mapper$startNewSession$lambda_0.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$Mapper$startNewSession$lambda_0.prototype.constructor = Coroutine$Mapper$startNewSession$lambda_0;
  Coroutine$Mapper$startNewSession$lambda_0.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            return launch(this.local$$receiver, void 0, void 0, Mapper$startNewSession$lambda$lambda(this.local$this$Mapper));
          case 1:
            throw this.exception_0;
          default:this.state_0 = 1;
            throw new Error('State Machine Unreachable execution');
        }
      }
       catch (e) {
        if (this.state_0 === 1) {
          this.exceptionState_0 = this.state_0;
          throw e;
        }
         else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  function Mapper$startNewSession$lambda_0(this$Mapper_0) {
    return function ($receiver_0, continuation_0, suspended) {
      var instance = new Coroutine$Mapper$startNewSession$lambda_0(this$Mapper_0, $receiver_0, this, continuation_0);
      if (suspended)
        return instance;
      else
        return instance.doResume(null);
    };
  }
  function Mapper$startNewSession$lambda_1(it) {
    return it.modelSurface.name;
  }
  function Mapper$startNewSession$lambda$lambda_0(closure$thresholdValue, closure$sampleLocations) {
    return function (x, y, value) {
      if (value >= closure$thresholdValue && Random.Default.nextFloat() < 0.05) {
        closure$sampleLocations.add_11rb$(to(x, y));
      }
      return Unit;
    };
  }
  function Mapper$startNewSession$ObjectLiteral() {
    this.pixelCount_r4rxw5$_0 = 2048;
  }
  Object.defineProperty(Mapper$startNewSession$ObjectLiteral.prototype, 'pixelCount', {
    get: function () {
      return this.pixelCount_r4rxw5$_0;
    }
  });
  Mapper$startNewSession$ObjectLiteral.prototype.describe = function () {
    return 'Mapper surface';
  };
  Mapper$startNewSession$ObjectLiteral.$metadata$ = {
    kind: Kind_CLASS,
    interfaces: [Surface]
  };
  function Mapper$startNewSession$resetToBase(closure$buffer) {
    return function () {
      var $receiver = closure$buffer.indices;
      var tmp$;
      tmp$ = $receiver.iterator();
      while (tmp$.hasNext()) {
        var element = tmp$.next();
        closure$buffer.set_vux9f0$(element, element % 3);
      }
    };
  }
  function Mapper$startNewSession$actualPixelIndex(closure$pixelStep, this$Mapper) {
    return function (pixelIndexX) {
      return Kotlin.imul(pixelIndexX, closure$pixelStep) % this$Mapper.maxPixelsPerBrain_0 + (Kotlin.imul(pixelIndexX, closure$pixelStep) / this$Mapper.maxPixelsPerBrain_0 | 0) | 0;
    };
  }
  function Coroutine$Mapper$startNewSession$allPixelsOff(closure$resetToBase_0, closure$buffer_0, this$Mapper_0, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.exceptionState_0 = 1;
    this.local$closure$resetToBase = closure$resetToBase_0;
    this.local$closure$buffer = closure$buffer_0;
    this.local$this$Mapper = this$Mapper_0;
  }
  Coroutine$Mapper$startNewSession$allPixelsOff.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$Mapper$startNewSession$allPixelsOff.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$Mapper$startNewSession$allPixelsOff.prototype.constructor = Coroutine$Mapper$startNewSession$allPixelsOff;
  Coroutine$Mapper$startNewSession$allPixelsOff.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.local$closure$resetToBase();
            this.state_0 = 2;
            this.result_0 = this.local$this$Mapper.sendToAllReliably_0(this.local$closure$buffer, this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 1:
            throw this.exception_0;
          case 2:
            return;
          default:this.state_0 = 1;
            throw new Error('State Machine Unreachable execution');
        }
      }
       catch (e) {
        if (this.state_0 === 1) {
          this.exceptionState_0 = this.state_0;
          throw e;
        }
         else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  function Mapper$startNewSession$allPixelsOff(closure$resetToBase_0, closure$buffer_0, this$Mapper_0) {
    return function (continuation_0, suspended) {
      var instance = new Coroutine$Mapper$startNewSession$allPixelsOff(closure$resetToBase_0, closure$buffer_0, this$Mapper_0, continuation_0);
      if (suspended)
        return instance;
      else
        return instance.doResume(null);
    };
  }
  function Coroutine$Mapper$startNewSession$turnOnPixel(closure$resetToBase_0, closure$whitePaletteIndex_0, closure$buffer_0, this$Mapper_0, pixelIndex_0, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.exceptionState_0 = 1;
    this.local$closure$resetToBase = closure$resetToBase_0;
    this.local$closure$whitePaletteIndex = closure$whitePaletteIndex_0;
    this.local$closure$buffer = closure$buffer_0;
    this.local$this$Mapper = this$Mapper_0;
    this.local$pixelIndex = pixelIndex_0;
  }
  Coroutine$Mapper$startNewSession$turnOnPixel.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$Mapper$startNewSession$turnOnPixel.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$Mapper$startNewSession$turnOnPixel.prototype.constructor = Coroutine$Mapper$startNewSession$turnOnPixel;
  Coroutine$Mapper$startNewSession$turnOnPixel.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.local$closure$resetToBase();
            this.local$closure$buffer.set_vux9f0$(this.local$pixelIndex, this.local$closure$whitePaletteIndex);
            this.state_0 = 2;
            this.result_0 = this.local$this$Mapper.sendToAllReliably_0(this.local$closure$buffer, this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 1:
            throw this.exception_0;
          case 2:
            return;
          default:this.state_0 = 1;
            throw new Error('State Machine Unreachable execution');
        }
      }
       catch (e) {
        if (this.state_0 === 1) {
          this.exceptionState_0 = this.state_0;
          throw e;
        }
         else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  function Mapper$startNewSession$turnOnPixel(closure$resetToBase_0, closure$whitePaletteIndex_0, closure$buffer_0, this$Mapper_0) {
    return function (pixelIndex_0, continuation_0, suspended) {
      var instance = new Coroutine$Mapper$startNewSession$turnOnPixel(closure$resetToBase_0, closure$whitePaletteIndex_0, closure$buffer_0, this$Mapper_0, pixelIndex_0, continuation_0);
      if (suspended)
        return instance;
      else
        return instance.doResume(null);
    };
  }
  function Coroutine$Mapper$startNewSession$lambda_1(this$Mapper_0, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.exceptionState_0 = 1;
    this.local$this$Mapper = this$Mapper_0;
  }
  Coroutine$Mapper$startNewSession$lambda_1.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$Mapper$startNewSession$lambda_1.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$Mapper$startNewSession$lambda_1.prototype.constructor = Coroutine$Mapper$startNewSession$lambda_1;
  Coroutine$Mapper$startNewSession$lambda_1.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            return this.local$this$Mapper.udpSocket_0.broadcastUdp_68hu5j$(8002, new MapperHelloMessage(this.local$this$Mapper.isRunning_0)), Unit;
          case 1:
            throw this.exception_0;
          default:this.state_0 = 1;
            throw new Error('State Machine Unreachable execution');
        }
      }
       catch (e) {
        if (this.state_0 === 1) {
          this.exceptionState_0 = this.state_0;
          throw e;
        }
         else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  function Mapper$startNewSession$lambda_2(this$Mapper_0) {
    return function (continuation_0, suspended) {
      var instance = new Coroutine$Mapper$startNewSession$lambda_1(this$Mapper_0, continuation_0);
      if (suspended)
        return instance;
      else
        return instance.doResume(null);
    };
  }
  function Coroutine$startNewSession_0($this, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.exceptionState_0 = 42;
    this.$this = $this;
    this.local$tmp$ = void 0;
    this.local$brainIdRequestJob = void 0;
    this.local$sessionStartTime = void 0;
    this.local$visibleSurfaces = void 0;
    this.local$bitmap = void 0;
    this.local$baseImageName = void 0;
    this.local$deltaBitmap = void 0;
    this.local$surfaceScheme = void 0;
    this.local$tmp$_0 = void 0;
    this.local$index = void 0;
    this.local$item = void 0;
    this.local$buffer = void 0;
    this.local$whitePaletteIndex = void 0;
    this.local$resetToBase = void 0;
    this.local$actualPixelIndex = void 0;
    this.local$allPixelsOff = void 0;
    this.local$turnOnPixel = void 0;
    this.local$pixelIndexX = void 0;
    this.local$pixelIndex = void 0;
    this.local$detector = void 0;
    this.local$pixelOnBitmap = void 0;
    this.local$pixelOnImageName = void 0;
    this.local$tmp$_1 = void 0;
  }
  Coroutine$startNewSession_0.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$startNewSession_0.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$startNewSession_0.prototype.constructor = Coroutine$startNewSession_0;
  Coroutine$startNewSession_0.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.$this.mapperUi_0.showMessage_61zpoe$('ESTABLISHING UPLINK\u2026');
            this.state_0 = 1;
            this.result_0 = this.$this.retry_0(Mapper$startNewSession$lambda(this.$this), this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 1:
            this.$this.suppressShows_0();
            this.state_0 = 2;
            this.result_0 = coroutineScope(Mapper$startNewSession$lambda_0(this.$this), this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 2:
            this.local$brainIdRequestJob = this.result_0;
            this.$this.pauseForUserInteraction_0('PRESS PLAY WHEN READY');
            this.$this.mapperUi_0.showMessage_61zpoe$(this.$this.brainsToMap_0.size.toString() + ' SURFACES DISCOVERED!');
            this.state_0 = 3;
            this.result_0 = this.$this.waitUntilUnpaused_0(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 3:
            this.state_0 = 4;
            this.result_0 = cancelAndJoin(this.local$brainIdRequestJob, this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 4:
            if (this.$this.brainsToMap_0.isEmpty()) {
              this.$this.mapperUi_0.showMessage_61zpoe$('NO SURFACES DISCOVERED! TRY AGAIN!');
              this.$this.isRunning_0 = false;
              return;
            }
             else {
              this.state_0 = 5;
              continue;
            }

          case 5:
            this.$this.mapperUi_0.showMessage_61zpoe$('READY PLAYER ONE\u2026');
            this.$this.pauseForUserInteraction_0('ALIGN MODEL AND PRESS PLAY WHEN READY');
            this.state_0 = 6;
            this.result_0 = this.$this.waitUntilUnpaused_0(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 6:
            this.local$sessionStartTime = DateTime.Companion.now();
            this.$this.mapperUi_0.showMessage_61zpoe$('CALIBRATING\u2026');
            this.local$visibleSurfaces = this.$this.mapperUi_0.getVisibleSurfaces();
            println('Visible surfaces: ' + joinToString(this.local$visibleSurfaces, void 0, void 0, void 0, void 0, void 0, Mapper$startNewSession$lambda_1));
            this.state_0 = 7;
            this.result_0 = this.$this.sendToAllReliably_0(this.$this.solidColorBuffer_0(this.$this.inactiveColor_0), this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 7:
            this.state_0 = 8;
            this.result_0 = delay(L1000, this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 8:
            this.state_0 = 9;
            this.result_0 = this.$this.getBrightImageBitmap_0(5, this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 9:
            this.local$bitmap = this.result_0;
            this.$this.baseBitmap_0 = this.local$bitmap;
            this.state_0 = 10;
            this.result_0 = this.$this.mapperClient_0.saveImage_39j694$(this.local$sessionStartTime, 'base', this.local$bitmap, this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 10:
            this.local$baseImageName = this.result_0;
            this.local$deltaBitmap = new NativeBitmap(this.local$bitmap.width, this.local$bitmap.height);
            var cameraOrientation = this.$this.mapperUi_0.lockUi();
            this.$this.mapperUi_0.showMessage_61zpoe$('MAPPING\u2026');
            this.$this.mapperUi_0.showStats_qt1dr2$(this.$this.brainsToMap_0.size, 0, -1);
            this.local$surfaceScheme = Mapper$Detector$GREEN_getInstance();
            this.exceptionState_0 = 38;
            println('identify surfaces...');
            var tmp$_0;
            this.local$index = 0;
            this.local$tmp$_0 = this.$this.brainsToMap_0.values.iterator();
            this.state_0 = 11;
            continue;
          case 11:
            if (!this.local$tmp$_0.hasNext()) {
              this.state_0 = 18;
              continue;
            }

            this.local$item = this.local$tmp$_0.next();
            this.$this.mapperUi_0.showMessage_61zpoe$('MAPPING SURFACE ' + checkIndexOverflow((tmp$_0 = this.local$index, this.local$index = tmp$_0 + 1 | 0, tmp$_0)) + ' / ' + this.$this.brainsToMap_0.size + ' (' + this.local$item.brainId + ')\u2026');
            this.$this.deliverer_0.send_b2qy7x$(this.local$item, this.$this.solidColorBuffer_0(this.$this.activeColor_0));
            this.state_0 = 12;
            this.result_0 = this.$this.deliverer_0.await_yhmem3$(void 0, void 0, this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 12:
            this.state_0 = 13;
            this.result_0 = this.$this.slowCamDelay_0(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 13:
            this.state_0 = 14;
            this.result_0 = this.$this.getBrightImageBitmap_0(3, this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 14:
            var surfaceOnBitmap = this.result_0;
            var surfaceAnalysis = ImageProcessing$Companion_getInstance().diff_pkkjvd$(surfaceOnBitmap, ensureNotNull(this.$this.baseBitmap_0), this.local$deltaBitmap, this.local$surfaceScheme);
            var surfaceChangeRegion = surfaceAnalysis.detectChangeRegion_mx4ult$(0.25);
            println('surfaceChangeRegion(' + this.local$item.brainId + ') =' + (' ' + surfaceChangeRegion + ' ' + surfaceChangeRegion.width + 'x' + surfaceChangeRegion.height));
            this.$this.mapperUi_0.showDiffImage_oa2j07$(this.local$deltaBitmap, surfaceChangeRegion);
            this.local$item.changeRegion = surfaceChangeRegion;
            var thresholdValue = surfaceAnalysis.thresholdValueFor_mx4ult$(0.25);
            var sampleLocations = ArrayList_init();
            ImageProcessing$Companion_getInstance().pixels_jkgb9c$(surfaceOnBitmap, this.local$surfaceScheme, surfaceChangeRegion, Mapper$startNewSession$lambda$lambda_0(thresholdValue, sampleLocations));
            var surfaceBallot = new Mapper$Ballot();
            while (surfaceBallot.totalVotes < 10) {
              var tmp$ = ensureNotNull(random(sampleLocations));
              var x = tmp$.component1()
              , y = tmp$.component2();
              var visibleSurface = this.$this.mapperUi_0.intersectingSurface_4c3mt7$(x, y, this.local$visibleSurfaces);
              var surface = visibleSurface != null ? visibleSurface.modelSurface : null;
              if (surface != null) {
                surfaceBallot.cast_yuqcw7$(surface.name, visibleSurface);
              }
            }

            var firstGuess = surfaceBallot.winner();
            var firstGuessSurface = firstGuess.modelSurface;
            this.$this.mapperUi_0.showMessage2_61zpoe$('Candidate panels: ' + surfaceBallot.summarize());
            println('Guessed panel ' + firstGuessSurface.name + ' for ' + this.local$item.brainId);
            this.local$item.guessedModelSurface = firstGuessSurface;
            this.local$item.guessedVisibleSurface = firstGuess;
            this.local$item.panelDeltaBitmap = this.local$deltaBitmap.clone();
            this.state_0 = 15;
            this.result_0 = this.$this.mapperClient_0.saveImage_39j694$(this.local$sessionStartTime, 'brain-' + this.local$item.brainId, this.local$deltaBitmap, this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 15:
            this.local$item.deltaImageName = this.result_0;
            this.$this.pauseForUserInteraction_0();
            this.state_0 = 16;
            this.result_0 = this.$this.waitUntilUnpaused_0(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 16:
            this.$this.deliverer_0.send_b2qy7x$(this.local$item, this.$this.solidColorBuffer_0(this.$this.inactiveColor_0));
            this.state_0 = 17;
            this.result_0 = this.$this.deliverer_0.await_yhmem3$(void 0, void 0, this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 17:
            this.state_0 = 11;
            continue;
          case 18:
            this.state_0 = 19;
            this.result_0 = delay(L1000, this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 19:
            println('identify pixels...');
            var pixelShader = new PixelShader(PixelShader$Encoding$INDEXED_4_getInstance());
            var $receiver = pixelShader.createBuffer_ppt8xj$(new Mapper$startNewSession$ObjectLiteral());
            $receiver.palette[0] = this.$this.detectors[0].alternateColor;
            $receiver.palette[1] = this.$this.detectors[1].alternateColor;
            $receiver.palette[2] = this.$this.detectors[2].alternateColor;
            $receiver.palette[3] = Color$Companion_getInstance().WHITE;
            $receiver.setAll_za3lpa$(0);
            this.local$buffer = $receiver;
            this.local$whitePaletteIndex = 3;
            this.local$resetToBase = Mapper$startNewSession$resetToBase(this.local$buffer);
            this.local$resetToBase();
            this.state_0 = 20;
            this.result_0 = this.$this.sendToAllReliably_0(this.local$buffer, this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 20:
            this.state_0 = 21;
            this.result_0 = delay(L1000, this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 21:
            var pixelStep = 4;
            this.local$actualPixelIndex = Mapper$startNewSession$actualPixelIndex(pixelStep, this.$this);
            this.local$allPixelsOff = Mapper$startNewSession$allPixelsOff(this.local$resetToBase, this.local$buffer, this.$this);
            this.local$turnOnPixel = Mapper$startNewSession$turnOnPixel(this.local$resetToBase, this.local$whitePaletteIndex, this.local$buffer, this.$this);
            this.local$tmp$ = this.$this.maxPixelsPerBrain_0;
            this.local$pixelIndexX = 0;
            this.state_0 = 22;
            continue;
          case 22:
            if (this.local$pixelIndexX >= this.local$tmp$) {
              this.state_0 = 36;
              continue;
            }

            this.local$pixelIndex = this.local$actualPixelIndex(this.local$pixelIndexX);
            this.local$detector = this.$this.detectors[this.local$pixelIndex % this.$this.detectors.length];
            this.$this.mapperUi_0.showMessage_61zpoe$('MAPPING PIXEL ' + this.local$pixelIndex + ' / ' + this.$this.maxPixelsPerBrain_0 + '\u2026');
            if (this.local$pixelIndex % 128 === 0)
              println('pixel ' + this.local$pixelIndex + '... isRunning is ' + this.$this.isRunning_0);
            this.state_0 = 23;
            this.result_0 = this.local$turnOnPixel(this.local$pixelIndex, this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 23:
            this.state_0 = 24;
            this.result_0 = this.$this.slowCamDelay_0(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 24:
            this.state_0 = 25;
            this.result_0 = this.$this.getBrightImageBitmap_0(2, this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 25:
            this.local$pixelOnBitmap = this.result_0;
            ImageProcessing$Companion_getInstance().diff_pkkjvd$(this.local$pixelOnBitmap, ensureNotNull(this.$this.baseBitmap_0), this.local$deltaBitmap, this.local$detector);
            this.$this.mapperUi_0.showDiffImage_oa2j07$(this.local$deltaBitmap);
            this.state_0 = 26;
            this.result_0 = this.$this.mapperClient_0.saveImage_39j694$(this.local$sessionStartTime, 'pixel-' + this.local$pixelIndex, this.local$deltaBitmap, this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 26:
            this.local$pixelOnImageName = this.result_0;
            var nextPixelIndex = this.local$actualPixelIndex(this.local$pixelIndexX + 1 | 0);
            if (nextPixelIndex < this.$this.maxPixelsPerBrain_0) {
              this.state_0 = 27;
              this.result_0 = this.local$turnOnPixel(this.local$pixelIndex, this);
              if (this.result_0 === COROUTINE_SUSPENDED)
                return COROUTINE_SUSPENDED;
              continue;
            }
             else {
              this.state_0 = 28;
              continue;
            }

          case 27:
            this.state_0 = 28;
            continue;
          case 28:
            this.local$tmp$_1 = this.$this.brainsToMap_0.values.iterator();
            this.state_0 = 29;
            continue;
          case 29:
            if (!this.local$tmp$_1.hasNext()) {
              this.state_0 = 32;
              continue;
            }

            var element = this.local$tmp$_1.next();
            var tmp$_1;
            this.$this.mapperUi_0.showMessage_61zpoe$('MAPPING PIXEL ' + this.local$pixelIndex + ' / ' + this.$this.maxPixelsPerBrain_0 + ' (' + element.brainId + ')\u2026');
            var surfaceChangeRegion_0 = element.changeRegion;
            var visibleSurface_0 = element.guessedVisibleSurface;
            if (surfaceChangeRegion_0 != null && surfaceChangeRegion_0.sqPix() > 0 && visibleSurface_0 != null) {
              this.$this.mapperUi_0.showAfter_5151av$(ensureNotNull(element.panelDeltaBitmap));
              var analysis = ImageProcessing$Companion_getInstance().diff_pkkjvd$(this.local$pixelOnBitmap, ensureNotNull(this.$this.baseBitmap_0), this.local$deltaBitmap, this.local$detector, ensureNotNull(element.panelDeltaBitmap), surfaceChangeRegion_0);
              var pixelChangeRegion = analysis.detectChangeRegion_mx4ult$(0.5);
              println('pixelChangeRegion(' + this.local$pixelIndex + ',' + toString_0((tmp$_1 = element.guessedModelSurface) != null ? tmp$_1.name : null) + ' =' + (' ' + pixelChangeRegion + ' ' + pixelChangeRegion.width + 'x' + pixelChangeRegion.height));
              this.$this.mapperUi_0.showDiffImage_oa2j07$(this.local$deltaBitmap, pixelChangeRegion);
              this.$this.mapperUi_0.showBefore_5151av$(this.local$pixelOnBitmap);
              this.$this.mapperUi_0.showAfter_5151av$(ensureNotNull(element.panelDeltaBitmap));
              if (!pixelChangeRegion.isEmpty()) {
                var center = new SheepModel$Point((pixelChangeRegion.centerX - surfaceChangeRegion_0.x0 | 0) / surfaceChangeRegion_0.width, (pixelChangeRegion.centerY - surfaceChangeRegion_0.y0 | 0) / surfaceChangeRegion_0.height, 0.0);
                visibleSurface_0.addPixel_nhq4am$(this.local$pixelIndex, pixelChangeRegion.centerX, pixelChangeRegion.centerY);
                var $receiver_0 = element.pixelMapData;
                var value = new Mapper$PixelMapData(pixelChangeRegion, this.local$pixelOnImageName);
                $receiver_0.put_xwzc9p$(this.local$pixelIndex, value);
                println(this.local$pixelIndex.toString() + '/' + element.brainId + ': center = ' + center);
              }
            }

            this.state_0 = 30;
            this.result_0 = delay(L1, this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 30:
            this.state_0 = 31;
            this.result_0 = this.$this.waitUntilUnpaused_0(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 31:
            this.state_0 = 29;
            continue;
          case 32:
            this.state_0 = 33;
            this.result_0 = this.$this.waitUntilUnpaused_0(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 33:
            this.state_0 = 34;
            this.result_0 = this.local$allPixelsOff(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 34:
            this.state_0 = 35;
            continue;
          case 35:
            this.local$pixelIndexX++;
            this.state_0 = 22;
            continue;
          case 36:
            println('done identifying pixels...');
            println('done identifying things... ' + this.$this.isRunning_0);
            this.$this.mapperUi_0.showMessage_61zpoe$('++LEVEL UNLOCKED++');
            this.state_0 = 37;
            this.result_0 = delay(L1000, this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 37:
            this.exceptionState_0 = 42;
            this.state_0 = 39;
            continue;
          case 38:
            this.exceptionState_0 = 42;
            var e = this.exception_0;
            if (Kotlin.isType(e, Mapper$TimeoutException)) {
              this.$this.mapperUi_0.showMessage_61zpoe$('Failed: ' + toString_0(e.message));
              logger$Companion_getInstance().error_61zpoe$(ensureNotNull(e.message));
            }
             else
              throw e;
            this.state_0 = 39;
            continue;
          case 39:
            this.$this.isRunning_0 = false;
            this.$this.mapperUi_0.unlockUi();
            this.state_0 = 40;
            this.result_0 = this.$this.retry_0(Mapper$startNewSession$lambda_2(this.$this), this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 40:
            println("Here's what we learned!");
            var surfaces = ArrayList_init();
            var tmp$_2;
            tmp$_2 = this.$this.brainsToMap_0.entries.iterator();
            while (tmp$_2.hasNext()) {
              var element_0 = tmp$_2.next();
              var address = element_0.key;
              var brainToMap = element_0.value;
              println('Brain ID: ' + brainToMap.brainId + ' at ' + address + ':');
              println('  Surface: ' + toString_0(brainToMap.guessedModelSurface));
              println('  Pixels:');
              var visibleSurface_1 = brainToMap.guessedVisibleSurface;
              if (visibleSurface_1 != null) {
                visibleSurface_1.showPixels();
                var tmp$_3;
                tmp$_3 = brainToMap.pixelMapData.entries.iterator();
                while (tmp$_3.hasNext()) {
                  var element_1 = tmp$_3.next();
                  var pixelIndex = element_1.key;
                  var mapData = element_1.value;
                  var changeRegion = mapData.pixelChangeRegion;
                  var position = visibleSurface_1.translatePixelToPanelSpace_dleff0$(changeRegion.centerX, changeRegion.centerY);
                  println('    ' + pixelIndex + ' -> ' + toString_0(position != null ? position.x : null) + ',' + toString_0(position != null ? position.y : null));
                }
                var $receiver_1 = visibleSurface_1.pixelsInModelSpace;
                var destination = ArrayList_init_1(collectionSizeOrDefault($receiver_1, 10));
                var tmp$_4, tmp$_0_0;
                var index = 0;
                tmp$_4 = $receiver_1.iterator();
                while (tmp$_4.hasNext()) {
                  var item = tmp$_4.next();
                  var tmp$_5 = destination.add_11rb$;
                  var pixelMapData = brainToMap.pixelMapData.get_11rb$(checkIndexOverflow((tmp$_0_0 = index, index = tmp$_0_0 + 1 | 0, tmp$_0_0)));
                  var pixelChangeRegion_0 = pixelMapData != null ? pixelMapData.pixelChangeRegion : null;
                  var screenPosition = pixelChangeRegion_0 != null ? visibleSurface_1.translatePixelToPanelSpace_dleff0$(pixelChangeRegion_0.centerX, pixelChangeRegion_0.centerY) : null;
                  tmp$_5.call(destination, new MappingSession$SurfaceData$PixelData(item, screenPosition, pixelMapData != null ? pixelMapData.deltaImageName : null));
                }
                var pixels = destination;
                surfaces.add_11rb$(new MappingSession$SurfaceData(brainToMap.brainId, visibleSurface_1.modelSurface.name, pixels, brainToMap.deltaImageName, null, null));
              }
            }

            var cameraMatrix = new Matrix4([]);
            var mappingSession = new MappingSession(this.local$sessionStartTime.unixMillis, surfaces, cameraMatrix, this.local$baseImageName);
            this.state_0 = 41;
            this.result_0 = this.$this.mapperClient_0.saveSession_x3z8ep$(mappingSession, this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 41:
            return;
          case 42:
            throw this.exception_0;
          default:this.state_0 = 42;
            throw new Error('State Machine Unreachable execution');
        }
      }
       catch (e) {
        if (this.state_0 === 42) {
          this.exceptionState_0 = this.state_0;
          throw e;
        }
         else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  Mapper.prototype.startNewSession_0 = function (continuation_0, suspended) {
    var instance = new Coroutine$startNewSession_0(this, continuation_0);
    if (suspended)
      return instance;
    else
      return instance.doResume(null);
  };
  function Coroutine$slowCamDelay_0($this, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.exceptionState_0 = 1;
    this.$this = $this;
  }
  Coroutine$slowCamDelay_0.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$slowCamDelay_0.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$slowCamDelay_0.prototype.constructor = Coroutine$slowCamDelay_0;
  Coroutine$slowCamDelay_0.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.state_0 = 2;
            this.result_0 = this.$this.getImage_0(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 1:
            throw this.exception_0;
          case 2:
            this.state_0 = 3;
            this.result_0 = this.$this.getImage_0(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 3:
            this.state_0 = 4;
            this.result_0 = this.$this.getImage_0(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 4:
            return;
          default:this.state_0 = 1;
            throw new Error('State Machine Unreachable execution');
        }
      }
       catch (e) {
        if (this.state_0 === 1) {
          this.exceptionState_0 = this.state_0;
          throw e;
        }
         else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  Mapper.prototype.slowCamDelay_0 = function (continuation_0, suspended) {
    var instance = new Coroutine$slowCamDelay_0(this, continuation_0);
    if (suspended)
      return instance;
    else
      return instance.doResume(null);
  };
  function Coroutine$getBrightImageBitmap_0($this, samples_0, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.exceptionState_0 = 1;
    this.$this = $this;
    this.local$bitmap = void 0;
    this.local$i = void 0;
    this.local$samples = samples_0;
  }
  Coroutine$getBrightImageBitmap_0.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$getBrightImageBitmap_0.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$getBrightImageBitmap_0.prototype.constructor = Coroutine$getBrightImageBitmap_0;
  Coroutine$getBrightImageBitmap_0.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.state_0 = 2;
            this.result_0 = this.$this.getImage_0(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 1:
            throw this.exception_0;
          case 2:
            this.local$bitmap = this.result_0.toBitmap();
            this.local$i = 1;
            this.state_0 = 3;
            continue;
          case 3:
            if (this.local$i >= this.local$samples) {
              this.state_0 = 6;
              continue;
            }

            this.state_0 = 4;
            this.result_0 = this.$this.getImage_0(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 4:
            this.local$bitmap.lighten_5151av$(this.result_0.toBitmap());
            this.state_0 = 5;
            continue;
          case 5:
            this.local$i++;
            this.state_0 = 3;
            continue;
          case 6:
            return this.local$bitmap;
          default:this.state_0 = 1;
            throw new Error('State Machine Unreachable execution');
        }
      }
       catch (e) {
        if (this.state_0 === 1) {
          this.exceptionState_0 = this.state_0;
          throw e;
        }
         else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  Mapper.prototype.getBrightImageBitmap_0 = function (samples_0, continuation_0, suspended) {
    var instance = new Coroutine$getBrightImageBitmap_0(this, samples_0, continuation_0);
    if (suspended)
      return instance;
    else
      return instance.doResume(null);
  };
  Mapper.prototype.pauseForUserInteraction_0 = function (message) {
    if (message === void 0)
      message = 'PRESS PLAY WHEN READY';
    this.isPaused_0 = true;
    this.mapperUi_0.pauseForUserInteraction();
    this.mapperUi_0.showMessage2_61zpoe$(message);
  };
  function Coroutine$waitUntilUnpaused_0($this, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.exceptionState_0 = 1;
    this.$this = $this;
  }
  Coroutine$waitUntilUnpaused_0.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$waitUntilUnpaused_0.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$waitUntilUnpaused_0.prototype.constructor = Coroutine$waitUntilUnpaused_0;
  Coroutine$waitUntilUnpaused_0.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.state_0 = 2;
            continue;
          case 1:
            throw this.exception_0;
          case 2:
            if (!this.$this.isPaused_0) {
              this.state_0 = 4;
              continue;
            }

            this.state_0 = 3;
            this.result_0 = delay(L50, this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 3:
            this.state_0 = 2;
            continue;
          case 4:
            this.$this.mapperUi_0.showMessage2_61zpoe$('');
            return;
          default:this.state_0 = 1;
            throw new Error('State Machine Unreachable execution');
        }
      }
       catch (e) {
        if (this.state_0 === 1) {
          this.exceptionState_0 = this.state_0;
          throw e;
        }
         else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  Mapper.prototype.waitUntilUnpaused_0 = function (continuation_0, suspended) {
    var instance = new Coroutine$waitUntilUnpaused_0(this, continuation_0);
    if (suspended)
      return instance;
    else
      return instance.doResume(null);
  };
  function Coroutine$sendToAllReliably_0($this, buffer_0, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.exceptionState_0 = 1;
    this.$this = $this;
    this.local$buffer = buffer_0;
  }
  Coroutine$sendToAllReliably_0.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$sendToAllReliably_0.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$sendToAllReliably_0.prototype.constructor = Coroutine$sendToAllReliably_0;
  Coroutine$sendToAllReliably_0.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            var tmp$;
            tmp$ = this.$this.brainsToMap_0.values.iterator();
            while (tmp$.hasNext()) {
              var element = tmp$.next();
              this.$this.deliverer_0.send_b2qy7x$(element, this.local$buffer);
            }

            this.state_0 = 2;
            this.result_0 = this.$this.deliverer_0.await_yhmem3$(L5000, void 0, this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 1:
            throw this.exception_0;
          case 2:
            return;
          default:this.state_0 = 1;
            throw new Error('State Machine Unreachable execution');
        }
      }
       catch (e) {
        if (this.state_0 === 1) {
          this.exceptionState_0 = this.state_0;
          throw e;
        }
         else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  Mapper.prototype.sendToAllReliably_0 = function (buffer_0, continuation_0, suspended) {
    var instance = new Coroutine$sendToAllReliably_0(this, buffer_0, continuation_0);
    if (suspended)
      return instance;
    else
      return instance.doResume(null);
  };
  function Coroutine$retry_0($this, fn_0, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.exceptionState_0 = 1;
    this.$this = $this;
    this.local$fn = fn_0;
  }
  Coroutine$retry_0.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$retry_0.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$retry_0.prototype.constructor = Coroutine$retry_0;
  Coroutine$retry_0.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.state_0 = 2;
            this.result_0 = this.local$fn(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 1:
            throw this.exception_0;
          case 2:
            this.state_0 = 3;
            this.result_0 = delay(L10, this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 3:
            this.state_0 = 4;
            this.result_0 = this.local$fn(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 4:
            return;
          default:this.state_0 = 1;
            throw new Error('State Machine Unreachable execution');
        }
      }
       catch (e) {
        if (this.state_0 === 1) {
          this.exceptionState_0 = this.state_0;
          throw e;
        }
         else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  Mapper.prototype.retry_0 = function (fn_0, continuation_0, suspended) {
    var instance = new Coroutine$retry_0(this, fn_0, continuation_0);
    if (suspended)
      return instance;
    else
      return instance.doResume(null);
  };
  function Coroutine$Mapper$suppressShows$lambda(this$Mapper_0, $receiver_0, controller, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.$controller = controller;
    this.exceptionState_0 = 1;
    this.local$this$Mapper = this$Mapper_0;
  }
  Coroutine$Mapper$suppressShows$lambda.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$Mapper$suppressShows$lambda.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$Mapper$suppressShows$lambda.prototype.constructor = Coroutine$Mapper$suppressShows$lambda;
  Coroutine$Mapper$suppressShows$lambda.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.state_0 = 2;
            continue;
          case 1:
            throw this.exception_0;
          case 2:
            if (!this.local$this$Mapper.isRunning_0) {
              this.state_0 = 4;
              continue;
            }

            this.state_0 = 3;
            this.result_0 = delay(L10000, this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 3:
            this.local$this$Mapper.udpSocket_0.broadcastUdp_68hu5j$(8002, new MapperHelloMessage(this.local$this$Mapper.isRunning_0));
            this.state_0 = 2;
            continue;
          case 4:
            return Unit;
          default:this.state_0 = 1;
            throw new Error('State Machine Unreachable execution');
        }
      }
       catch (e) {
        if (this.state_0 === 1) {
          this.exceptionState_0 = this.state_0;
          throw e;
        }
         else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  function Mapper$suppressShows$lambda(this$Mapper_0) {
    return function ($receiver_0, continuation_0, suspended) {
      var instance = new Coroutine$Mapper$suppressShows$lambda(this$Mapper_0, $receiver_0, this, continuation_0);
      if (suspended)
        return instance;
      else
        return instance.doResume(null);
    };
  }
  Mapper.prototype.suppressShows_0 = function () {
    this.suppressShowsJob_0 = launch(this, new CoroutineName('Suppress Pinky'), void 0, Mapper$suppressShows$lambda(this));
  };
  Mapper.prototype.solidColor_0 = function (color) {
    var buf = this.solidColorBuffer_0(color);
    return new BrainShaderMessage(buf.shader, buf);
  };
  function Mapper$solidColorBuffer$ObjectLiteral() {
    this.pixelCount_r99guf$_0 = 2048;
  }
  Object.defineProperty(Mapper$solidColorBuffer$ObjectLiteral.prototype, 'pixelCount', {
    get: function () {
      return this.pixelCount_r99guf$_0;
    }
  });
  Mapper$solidColorBuffer$ObjectLiteral.prototype.describe = function () {
    return 'Mapper surface';
  };
  Mapper$solidColorBuffer$ObjectLiteral.$metadata$ = {
    kind: Kind_CLASS,
    interfaces: [Surface]
  };
  Mapper.prototype.solidColorBuffer_0 = function (color) {
    var solidShader = new SolidShader();
    var $receiver = solidShader.createBuffer_ppt8xj$(new Mapper$solidColorBuffer$ObjectLiteral());
    $receiver.color = color;
    var buffer = $receiver;
    return buffer;
  };
  function Mapper$ReliableShaderMessageDeliverer($outer) {
    this.$outer = $outer;
    this.outstanding = LinkedHashMap_init();
    this.pongs = Channel();
  }
  Mapper$ReliableShaderMessageDeliverer.prototype.send_b2qy7x$ = function (brainToMap, buffer) {
    var deliveryAttempt = new Mapper$DeliveryAttempt(this.$outer, brainToMap, buffer);
    var $receiver = this.outstanding;
    var key = deliveryAttempt.key;
    $receiver.put_xwzc9p$(key, deliveryAttempt);
    deliveryAttempt.attemptDelivery();
  };
  function Coroutine$Mapper$ReliableShaderMessageDeliverer$await$lambda(this$ReliableShaderMessageDeliverer_0, $receiver_0, controller, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.$controller = controller;
    this.exceptionState_0 = 1;
    this.local$this$ReliableShaderMessageDeliverer = this$ReliableShaderMessageDeliverer_0;
  }
  Coroutine$Mapper$ReliableShaderMessageDeliverer$await$lambda.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$Mapper$ReliableShaderMessageDeliverer$await$lambda.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$Mapper$ReliableShaderMessageDeliverer$await$lambda.prototype.constructor = Coroutine$Mapper$ReliableShaderMessageDeliverer$await$lambda;
  Coroutine$Mapper$ReliableShaderMessageDeliverer$await$lambda.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.state_0 = 2;
            continue;
          case 1:
            throw this.exception_0;
          case 2:
            if (this.local$this$ReliableShaderMessageDeliverer.outstanding.isEmpty()) {
              this.state_0 = 4;
              continue;
            }

            this.state_0 = 3;
            this.result_0 = this.local$this$ReliableShaderMessageDeliverer.pongs.receive(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 3:
            var pingMessage = this.result_0;
            var deliveryAttempt = this.local$this$ReliableShaderMessageDeliverer.outstanding.remove_11rb$(toList(pingMessage.data));
            if (deliveryAttempt != null) {
              deliveryAttempt.succeeded();
            }

            this.state_0 = 2;
            continue;
          case 4:
            return Unit;
          default:this.state_0 = 1;
            throw new Error('State Machine Unreachable execution');
        }
      }
       catch (e) {
        if (this.state_0 === 1) {
          this.exceptionState_0 = this.state_0;
          throw e;
        }
         else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  function Mapper$ReliableShaderMessageDeliverer$await$lambda(this$ReliableShaderMessageDeliverer_0) {
    return function ($receiver_0, continuation_0, suspended) {
      var instance = new Coroutine$Mapper$ReliableShaderMessageDeliverer$await$lambda(this$ReliableShaderMessageDeliverer_0, $receiver_0, this, continuation_0);
      if (suspended)
        return instance;
      else
        return instance.doResume(null);
    };
  }
  function Coroutine$await_yhmem3$($this, retryAfterMillis_0, tries_0, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.exceptionState_0 = 1;
    this.$this = $this;
    this.local$retryAfterMillis = retryAfterMillis_0;
    this.local$tries = tries_0;
  }
  Coroutine$await_yhmem3$.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$await_yhmem3$.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$await_yhmem3$.prototype.constructor = Coroutine$await_yhmem3$;
  Coroutine$await_yhmem3$.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            if (this.local$retryAfterMillis === void 0)
              this.local$retryAfterMillis = L2000;
            if (this.local$tries === void 0)
              this.local$tries = 3;
            var tmp$ = logger$Companion_getInstance();
            var $receiver = this.$this.outstanding.values;
            var destination = ArrayList_init_1(collectionSizeOrDefault($receiver, 10));
            var tmp$_0;
            tmp$_0 = $receiver.iterator();
            while (tmp$_0.hasNext()) {
              var item = tmp$_0.next();
              destination.add_11rb$(item.brainToMap.brainId);
            }

            tmp$.debug_61zpoe$('Waiting for pongs from ' + destination + '...');
            if (this.local$tries === 0) {
              var $receiver_0 = this.$this.outstanding.values;
              var destination_0 = ArrayList_init_1(collectionSizeOrDefault($receiver_0, 10));
              var tmp$_1;
              tmp$_1 = $receiver_0.iterator();
              while (tmp$_1.hasNext()) {
                var item_0 = tmp$_1.next();
                destination_0.add_11rb$(item_0.brainToMap.brainId);
              }
              var remaining = ArrayList_init_0(destination_0);
              this.$this.outstanding.clear();
              throw new Mapper$TimeoutException('Timed out waiting for ' + remaining);
            }

            this.state_0 = 2;
            this.result_0 = withTimeoutOrNull(this.local$retryAfterMillis, Mapper$ReliableShaderMessageDeliverer$await$lambda(this.$this), this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 1:
            throw this.exception_0;
          case 2:
            if (this.$this.outstanding.isEmpty()) {
              return;
            }
             else {
              this.state_0 = 3;
              continue;
            }

          case 3:
            var retry = ArrayList_init_0(this.$this.outstanding.values);
            this.$this.outstanding.clear();
            var tmp$_2;
            tmp$_2 = retry.iterator();
            while (tmp$_2.hasNext()) {
              var element = tmp$_2.next();
              logger$Companion_getInstance().warn_61zpoe$("Didn't hear from " + element.brainToMap.brainId + ' after ' + this.local$retryAfterMillis.toString() + ', retrying...');
              this.$this.send_b2qy7x$(element.brainToMap, element.buffer);
            }

            this.state_0 = 4;
            this.result_0 = this.$this.await_yhmem3$(this.local$retryAfterMillis, this.local$tries - 1 | 0, this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 4:
            return;
          default:this.state_0 = 1;
            throw new Error('State Machine Unreachable execution');
        }
      }
       catch (e) {
        if (this.state_0 === 1) {
          this.exceptionState_0 = this.state_0;
          throw e;
        }
         else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  Mapper$ReliableShaderMessageDeliverer.prototype.await_yhmem3$ = function (retryAfterMillis_0, tries_0, continuation_0, suspended) {
    var instance = new Coroutine$await_yhmem3$(this, retryAfterMillis_0, tries_0, continuation_0);
    if (suspended)
      return instance;
    else
      return instance.doResume(null);
  };
  function Coroutine$Mapper$ReliableShaderMessageDeliverer$gotPong$lambda(this$ReliableShaderMessageDeliverer_0, closure$pingMessage_0, $receiver_0, controller, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.$controller = controller;
    this.exceptionState_0 = 1;
    this.local$this$ReliableShaderMessageDeliverer = this$ReliableShaderMessageDeliverer_0;
    this.local$closure$pingMessage = closure$pingMessage_0;
  }
  Coroutine$Mapper$ReliableShaderMessageDeliverer$gotPong$lambda.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$Mapper$ReliableShaderMessageDeliverer$gotPong$lambda.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$Mapper$ReliableShaderMessageDeliverer$gotPong$lambda.prototype.constructor = Coroutine$Mapper$ReliableShaderMessageDeliverer$gotPong$lambda;
  Coroutine$Mapper$ReliableShaderMessageDeliverer$gotPong$lambda.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.state_0 = 2;
            this.result_0 = this.local$this$ReliableShaderMessageDeliverer.pongs.send_11rb$(this.local$closure$pingMessage, this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 1:
            throw this.exception_0;
          case 2:
            return this.result_0;
          default:this.state_0 = 1;
            throw new Error('State Machine Unreachable execution');
        }
      }
       catch (e) {
        if (this.state_0 === 1) {
          this.exceptionState_0 = this.state_0;
          throw e;
        }
         else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  function Mapper$ReliableShaderMessageDeliverer$gotPong$lambda(this$ReliableShaderMessageDeliverer_0, closure$pingMessage_0) {
    return function ($receiver_0, continuation_0, suspended) {
      var instance = new Coroutine$Mapper$ReliableShaderMessageDeliverer$gotPong$lambda(this$ReliableShaderMessageDeliverer_0, closure$pingMessage_0, $receiver_0, this, continuation_0);
      if (suspended)
        return instance;
      else
        return instance.doResume(null);
    };
  }
  Mapper$ReliableShaderMessageDeliverer.prototype.gotPong_cill2t$ = function (pingMessage) {
    launch(this.$outer, void 0, void 0, Mapper$ReliableShaderMessageDeliverer$gotPong$lambda(this, pingMessage));
  };
  Mapper$ReliableShaderMessageDeliverer.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'ReliableShaderMessageDeliverer',
    interfaces: []
  };
  function Mapper$TimeoutException(message) {
    Exception_init(message, this);
    this.name = 'Mapper$TimeoutException';
  }
  Mapper$TimeoutException.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'TimeoutException',
    interfaces: [Exception]
  };
  function Mapper$DeliveryAttempt($outer, brainToMap, buffer) {
    this.$outer = $outer;
    this.brainToMap = brainToMap;
    this.buffer = buffer;
    this.tag_0 = Random.Default.nextBytes_za3lpa$(8);
    this.sentAt_0 = getTimeMillis().toNumber();
  }
  Object.defineProperty(Mapper$DeliveryAttempt.prototype, 'key', {
    get: function () {
      return toList(this.tag_0);
    }
  });
  Mapper$DeliveryAttempt.prototype.attemptDelivery = function () {
    this.$outer.udpSocket_0.sendUdp_wpmaqi$(this.brainToMap.address, this.brainToMap.port, new BrainShaderMessage(this.buffer.shader, this.buffer, this.tag_0));
  };
  Mapper$DeliveryAttempt.prototype.succeeded = function () {
    logger$Companion_getInstance().debug_61zpoe$(this.brainToMap.brainId + ' shader message pong after ' + (getTimeMillis().toNumber() - this.sentAt_0) + 'ms');
  };
  Mapper$DeliveryAttempt.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'DeliveryAttempt',
    interfaces: []
  };
  function Mapper$receive$lambda(this$Mapper) {
    return function () {
      return this$Mapper.solidColor_0(Color$Companion_getInstance().GREEN);
    };
  }
  Mapper.prototype.receive_ytpeqp$ = function (fromAddress, fromPort, bytes) {
    var tmp$;
    println('Mapper received message from ' + fromAddress + ':' + fromPort + ' ' + bytes[0]);
    var message = parse(bytes);
    if (Kotlin.isType(message, BrainHelloMessage)) {
      println('Heard from Brain ' + message.brainId + ' surface=' + ((tmp$ = message.surfaceName) != null ? tmp$ : 'unknown'));
      var $receiver = this.brainsToMap_0;
      var tmp$_0;
      var value = $receiver.get_11rb$(fromAddress);
      if (value == null) {
        var answer = new Mapper$BrainToMap(this, fromAddress, message.brainId);
        $receiver.put_xwzc9p$(fromAddress, answer);
        tmp$_0 = answer;
      }
       else {
        tmp$_0 = value;
      }
      var brainToMap = tmp$_0;
      this.mapperUi_0.showMessage_61zpoe$(this.brainsToMap_0.size.toString() + ' SURFACES DISCOVERED!');
      brainToMap.shade_s74fr6$(Mapper$receive$lambda(this));
    }
     else if (Kotlin.isType(message, PingMessage))
      if (message.isPong) {
        this.deliverer_0.gotPong_cill2t$(message);
      }
  };
  Mapper.prototype.haveImage_0 = function (image) {
    this.mapperUi_0.showCamImage_q5ica7$(image);
    this.newIncomingImage_0 = image;
  };
  function Coroutine$getImage_0($this, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.exceptionState_0 = 1;
    this.$this = $this;
  }
  Coroutine$getImage_0.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$getImage_0.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$getImage_0.prototype.constructor = Coroutine$getImage_0;
  Coroutine$getImage_0.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.$this.newIncomingImage_0 = null;
            this.state_0 = 2;
            continue;
          case 1:
            throw this.exception_0;
          case 2:
            if (this.$this.newIncomingImage_0 != null) {
              this.state_0 = 4;
              continue;
            }

            this.state_0 = 3;
            this.result_0 = delay(L2, this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 3:
            this.state_0 = 2;
            continue;
          case 4:
            var image = ensureNotNull(this.$this.newIncomingImage_0);
            this.$this.newIncomingImage_0 = null;
            return image;
          default:this.state_0 = 1;
            throw new Error('State Machine Unreachable execution');
        }
      }
       catch (e) {
        if (this.state_0 === 1) {
          this.exceptionState_0 = this.state_0;
          throw e;
        }
         else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  Mapper.prototype.getImage_0 = function (continuation_0, suspended) {
    var instance = new Coroutine$getImage_0(this, continuation_0);
    if (suspended)
      return instance;
    else
      return instance.doResume(null);
  };
  function Coroutine$getImage_1($this, tries_0, test_0, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.exceptionState_0 = 1;
    this.$this = $this;
    this.local$image = void 0;
    this.local$remainingTries = void 0;
    this.local$tries = tries_0;
    this.local$test = test_0;
  }
  Coroutine$getImage_1.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$getImage_1.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$getImage_1.prototype.constructor = Coroutine$getImage_1;
  Coroutine$getImage_1.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            if (this.local$tries === void 0)
              this.local$tries = 5;
            var tmp$;
            this.state_0 = 2;
            this.result_0 = this.$this.getImage_0(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 1:
            throw this.exception_0;
          case 2:
            this.local$image = this.result_0;
            this.local$remainingTries = this.local$tries - 1 | 0;
            this.state_0 = 3;
            continue;
          case 3:
            if (this.local$test(this.local$image) || (tmp$ = this.local$remainingTries, this.local$remainingTries = tmp$ - 1 | 0, tmp$) <= 0) {
              this.state_0 = 5;
              continue;
            }

            this.state_0 = 4;
            this.result_0 = this.$this.getImage_0(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 4:
            this.local$image = this.result_0;
            this.state_0 = 3;
            continue;
          case 5:
            return this.local$image;
          default:this.state_0 = 1;
            throw new Error('State Machine Unreachable execution');
        }
      }
       catch (e) {
        if (this.state_0 === 1) {
          this.exceptionState_0 = this.state_0;
          throw e;
        }
         else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  Mapper.prototype.getImage_1 = function (tries_0, test_0, continuation_0, suspended) {
    var instance = new Coroutine$getImage_1(this, tries_0, test_0, continuation_0);
    if (suspended)
      return instance;
    else
      return instance.doResume(null);
  };
  function Mapper$BrainToMap($outer, address, brainId) {
    this.$outer = $outer;
    this.address = address;
    this.brainId = brainId;
    this.changeRegion = null;
    this.guessedModelSurface = null;
    this.guessedVisibleSurface = null;
    this.panelDeltaBitmap = null;
    this.deltaImageName = null;
    this.pixelMapData = LinkedHashMap_init();
  }
  Object.defineProperty(Mapper$BrainToMap.prototype, 'port', {
    get: function () {
      return 8003;
    }
  });
  Mapper$BrainToMap.prototype.shade_s74fr6$ = function (shaderMessage) {
    this.$outer.udpSocket_0.sendUdp_wpmaqi$(this.address, 8003, shaderMessage());
  };
  Mapper$BrainToMap.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'BrainToMap',
    interfaces: []
  };
  function Mapper$PixelMapData(pixelChangeRegion, deltaImageName) {
    this.pixelChangeRegion = pixelChangeRegion;
    this.deltaImageName = deltaImageName;
  }
  Mapper$PixelMapData.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'PixelMapData',
    interfaces: []
  };
  function Mapper$Ballot() {
    this.box_0 = HashMap_init();
    this.totalVotes_4mbrhw$_0 = 0;
  }
  Object.defineProperty(Mapper$Ballot.prototype, 'totalVotes', {
    get: function () {
      return this.totalVotes_4mbrhw$_0;
    },
    set: function (totalVotes) {
      this.totalVotes_4mbrhw$_0 = totalVotes;
    }
  });
  Mapper$Ballot.prototype.cast_yuqcw7$ = function (key, value) {
    var tmp$;
    var $receiver = this.box_0;
    var tmp$_0;
    var value_0 = $receiver.get_11rb$(key);
    if (value_0 == null) {
      var answer = new Mapper$Ballot$Vote(value);
      $receiver.put_xwzc9p$(key, answer);
      tmp$_0 = answer;
    }
     else {
      tmp$_0 = value_0;
    }
    tmp$ = tmp$_0;
    tmp$.votes = tmp$.votes + 1 | 0;
    this.totalVotes = this.totalVotes + 1 | 0;
  };
  function Mapper$Ballot$winner$lambda(it) {
    return it.votes;
  }
  Mapper$Ballot.prototype.winner = function () {
    return first(sortedWith(this.box_0.values, new Comparator$ObjectLiteral(compareByDescending$lambda(Mapper$Ballot$winner$lambda)))).item;
  };
  function Mapper$Ballot$summarize$lambda(f) {
    var k = f.key;
    var v = f.value;
    return v.votes;
  }
  Mapper$Ballot.prototype.summarize = function () {
    var $receiver = sortedWith(this.box_0.entries, new Comparator$ObjectLiteral(compareByDescending$lambda(Mapper$Ballot$summarize$lambda)));
    var destination = ArrayList_init_1(collectionSizeOrDefault($receiver, 10));
    var tmp$;
    tmp$ = $receiver.iterator();
    while (tmp$.hasNext()) {
      var item = tmp$.next();
      var tmp$_0 = destination.add_11rb$;
      var k = item.key;
      var v = item.value;
      tmp$_0.call(destination, k + '=' + v + '.votes');
    }
    return joinToString(destination, ', ');
  };
  function Mapper$Ballot$Vote(item) {
    this.item = item;
    this.votes = 0;
  }
  Mapper$Ballot$Vote.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Vote',
    interfaces: []
  };
  Mapper$Ballot.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Ballot',
    interfaces: []
  };
  Object.defineProperty(Mapper.prototype, 'coroutineContext', {
    get: function () {
      return this.$delegate_9rrh7p$_0.coroutineContext;
    }
  });
  Mapper.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Mapper',
    interfaces: [CoroutineScope, MapperUi$Listener, Network$UdpListener]
  };
  function MapperUi() {
  }
  MapperUi.prototype.showCamImage_q5ica7$ = function (image, changeRegion, callback$default) {
    if (changeRegion === void 0)
      changeRegion = null;
    callback$default ? callback$default(image, changeRegion) : this.showCamImage_q5ica7$$default(image, changeRegion);
  };
  MapperUi.prototype.showDiffImage_oa2j07$ = function (deltaBitmap, changeRegion, callback$default) {
    if (changeRegion === void 0)
      changeRegion = null;
    callback$default ? callback$default(deltaBitmap, changeRegion) : this.showDiffImage_oa2j07$$default(deltaBitmap, changeRegion);
  };
  function MapperUi$Listener() {
  }
  MapperUi$Listener.$metadata$ = {
    kind: Kind_INTERFACE,
    simpleName: 'Listener',
    interfaces: []
  };
  function MapperUi$VisibleSurface() {
  }
  MapperUi$VisibleSurface.$metadata$ = {
    kind: Kind_INTERFACE,
    simpleName: 'VisibleSurface',
    interfaces: []
  };
  function MapperUi$CameraOrientation() {
  }
  MapperUi$CameraOrientation.$metadata$ = {
    kind: Kind_INTERFACE,
    simpleName: 'CameraOrientation',
    interfaces: []
  };
  MapperUi.$metadata$ = {
    kind: Kind_INTERFACE,
    simpleName: 'MapperUi',
    interfaces: []
  };
  function MediaDevices() {
  }
  function MediaDevices$Camera() {
  }
  MediaDevices$Camera.$metadata$ = {
    kind: Kind_INTERFACE,
    simpleName: 'Camera',
    interfaces: []
  };
  function MediaDevices$Region(x0, y0, x1, y1) {
    MediaDevices$Region$Companion_getInstance();
    this.x0 = x0;
    this.y0 = y0;
    this.x1 = x1;
    this.y1 = y1;
    this.width = this.x1 - this.x0 | 0;
    this.height = this.y1 - this.y0 | 0;
    this.centerX = ((this.x1 - this.x0 | 0) / 2 | 0) + this.x0 | 0;
    this.centerY = ((this.y1 - this.y0 | 0) / 2 | 0) + this.y0 | 0;
    this.xRange = until(this.x0, this.x1);
    this.yRange = until(this.y0, this.y1);
  }
  MediaDevices$Region.prototype.distanceTo_gdgylh$ = function (other) {
    var dX = this.centerX - other.centerX | 0;
    var dY = this.centerY - other.centerY | 0;
    var x = Kotlin.imul(dX, dX) + Kotlin.imul(dY, dY) | 0;
    return Math_0.sqrt(x);
  };
  MediaDevices$Region.prototype.intersectionWith_gdgylh$ = function (other) {
    var tmp$;
    var a = this.x0;
    var b = other.x0;
    var leftX = Math_0.max(a, b);
    var a_0 = this.x1;
    var b_0 = this.x1;
    var rightX = Math_0.min(a_0, b_0);
    var a_1 = this.y0;
    var b_1 = other.y0;
    var topY = Math_0.max(a_1, b_1);
    var a_2 = this.y1;
    var b_2 = other.y1;
    var bottomY = Math_0.min(a_2, b_2);
    if (leftX < rightX && topY < bottomY) {
      tmp$ = new MediaDevices$Region(leftX, topY, rightX, bottomY);
    }
     else {
      tmp$ = MediaDevices$Region$Companion_getInstance().EMPTY;
    }
    return tmp$;
  };
  MediaDevices$Region.prototype.sqPix = function () {
    var $receiver = this.x1 - this.x0;
    var tmp$ = Math_0.pow($receiver, 2);
    var $receiver_0 = this.y1 - this.y0;
    var x = tmp$ + Math_0.pow($receiver_0, 2);
    return Math_0.sqrt(x);
  };
  MediaDevices$Region.prototype.scaled_tjonv8$ = function (fromX, fromY, toX, toY) {
    return new MediaDevices$Region(numberToInt(this.x0 / fromX * toX), numberToInt(this.y0 / fromX * toX), numberToInt(this.x1 / fromY * toY), numberToInt(this.y1 / fromY * toY));
  };
  MediaDevices$Region.prototype.isEmpty = function () {
    return this.width <= 0 || this.height <= 0;
  };
  function MediaDevices$Region$Companion() {
    MediaDevices$Region$Companion_instance = this;
    this.EMPTY = new MediaDevices$Region(-1, -1, -1, -1);
  }
  MediaDevices$Region$Companion.prototype.containing_5151av$ = function (bitmap) {
    return new MediaDevices$Region(0, 0, bitmap.width, bitmap.height);
  };
  MediaDevices$Region$Companion.prototype.containing_6tj0gx$ = function (image) {
    return new MediaDevices$Region(0, 0, image.width, image.height);
  };
  MediaDevices$Region$Companion.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'Companion',
    interfaces: []
  };
  var MediaDevices$Region$Companion_instance = null;
  function MediaDevices$Region$Companion_getInstance() {
    if (MediaDevices$Region$Companion_instance === null) {
      new MediaDevices$Region$Companion();
    }
    return MediaDevices$Region$Companion_instance;
  }
  MediaDevices$Region.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Region',
    interfaces: []
  };
  MediaDevices$Region.prototype.component1 = function () {
    return this.x0;
  };
  MediaDevices$Region.prototype.component2 = function () {
    return this.y0;
  };
  MediaDevices$Region.prototype.component3 = function () {
    return this.x1;
  };
  MediaDevices$Region.prototype.component4 = function () {
    return this.y1;
  };
  MediaDevices$Region.prototype.copy_tjonv8$ = function (x0, y0, x1, y1) {
    return new MediaDevices$Region(x0 === void 0 ? this.x0 : x0, y0 === void 0 ? this.y0 : y0, x1 === void 0 ? this.x1 : x1, y1 === void 0 ? this.y1 : y1);
  };
  MediaDevices$Region.prototype.toString = function () {
    return 'Region(x0=' + Kotlin.toString(this.x0) + (', y0=' + Kotlin.toString(this.y0)) + (', x1=' + Kotlin.toString(this.x1)) + (', y1=' + Kotlin.toString(this.y1)) + ')';
  };
  MediaDevices$Region.prototype.hashCode = function () {
    var result = 0;
    result = result * 31 + Kotlin.hashCode(this.x0) | 0;
    result = result * 31 + Kotlin.hashCode(this.y0) | 0;
    result = result * 31 + Kotlin.hashCode(this.x1) | 0;
    result = result * 31 + Kotlin.hashCode(this.y1) | 0;
    return result;
  };
  MediaDevices$Region.prototype.equals = function (other) {
    return this === other || (other !== null && (typeof other === 'object' && (Object.getPrototypeOf(this) === Object.getPrototypeOf(other) && (Kotlin.equals(this.x0, other.x0) && Kotlin.equals(this.y0, other.y0) && Kotlin.equals(this.x1, other.x1) && Kotlin.equals(this.y1, other.y1)))));
  };
  MediaDevices.$metadata$ = {
    kind: Kind_INTERFACE,
    simpleName: 'MediaDevices',
    interfaces: []
  };
  function MovingHead(name, origin) {
    MovingHead$Companion_getInstance();
    this.name = name;
    this.origin = origin;
  }
  function MovingHead$ColorMode(name, ordinal) {
    Enum.call(this);
    this.name$ = name;
    this.ordinal$ = ordinal;
  }
  function MovingHead$ColorMode_initFields() {
    MovingHead$ColorMode_initFields = function () {
    };
    MovingHead$ColorMode$ColorWheel_instance = new MovingHead$ColorMode('ColorWheel', 0);
    MovingHead$ColorMode$RGB_instance = new MovingHead$ColorMode('RGB', 1);
    MovingHead$ColorMode$RGBW_instance = new MovingHead$ColorMode('RGBW', 2);
  }
  var MovingHead$ColorMode$ColorWheel_instance;
  function MovingHead$ColorMode$ColorWheel_getInstance() {
    MovingHead$ColorMode_initFields();
    return MovingHead$ColorMode$ColorWheel_instance;
  }
  var MovingHead$ColorMode$RGB_instance;
  function MovingHead$ColorMode$RGB_getInstance() {
    MovingHead$ColorMode_initFields();
    return MovingHead$ColorMode$RGB_instance;
  }
  var MovingHead$ColorMode$RGBW_instance;
  function MovingHead$ColorMode$RGBW_getInstance() {
    MovingHead$ColorMode_initFields();
    return MovingHead$ColorMode$RGBW_instance;
  }
  MovingHead$ColorMode.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'ColorMode',
    interfaces: [Enum]
  };
  function MovingHead$ColorMode$values() {
    return [MovingHead$ColorMode$ColorWheel_getInstance(), MovingHead$ColorMode$RGB_getInstance(), MovingHead$ColorMode$RGBW_getInstance()];
  }
  MovingHead$ColorMode.values = MovingHead$ColorMode$values;
  function MovingHead$ColorMode$valueOf(name) {
    switch (name) {
      case 'ColorWheel':
        return MovingHead$ColorMode$ColorWheel_getInstance();
      case 'RGB':
        return MovingHead$ColorMode$RGB_getInstance();
      case 'RGBW':
        return MovingHead$ColorMode$RGBW_getInstance();
      default:throwISE('No enum constant baaahs.MovingHead.ColorMode.' + name);
    }
  }
  MovingHead$ColorMode.valueOf_61zpoe$ = MovingHead$ColorMode$valueOf;
  function MovingHead$Buffer() {
  }
  Object.defineProperty(MovingHead$Buffer.prototype, 'supportsFinePositioning', {
    get: function () {
      return this.panFineChannel != null && this.tiltFineChannel != null;
    }
  });
  Object.defineProperty(MovingHead$Buffer.prototype, 'pan', {
    get: function () {
      return this.getFloat_gej297$_0(this.panChannel, this.panFineChannel);
    },
    set: function (value) {
      this.setFloat_8xjzlm$_0(this.panChannel, this.panFineChannel, value);
    }
  });
  Object.defineProperty(MovingHead$Buffer.prototype, 'tilt', {
    get: function () {
      return this.getFloat_gej297$_0(this.tiltChannel, this.tiltFineChannel);
    },
    set: function (value) {
      this.setFloat_8xjzlm$_0(this.tiltChannel, this.tiltFineChannel, value);
    }
  });
  Object.defineProperty(MovingHead$Buffer.prototype, 'dimmer', {
    get: function () {
      return this.getFloat_b37jry$_0(this.dimmerChannel);
    },
    set: function (value) {
      this.setFloat_vl9zqr$_0(this.dimmerChannel, value);
    }
  });
  MovingHead$Buffer.prototype.closestColorFor_rny0jj$ = function (color) {
    var bestMatch = {v: Shenzarpy$WheelColor$WHITE_getInstance()};
    var bestDistance = {v: 1.0};
    var tmp$;
    tmp$ = this.colorWheelColors.iterator();
    while (tmp$.hasNext()) {
      var element = tmp$.next();
      var distance = element.color.distanceTo_rny0jj$(color);
      if (distance < bestDistance.v) {
        bestMatch.v = element;
        bestDistance.v = distance;
      }
    }
    return toByte(bestMatch.v.ordinal);
  };
  MovingHead$Buffer.prototype.getFloat_b37jry$_0 = function (channel) {
    var byteVal = this.buffer.get_6ui4v4$(channel) & 255;
    return ((byteVal << 8) + byteVal | 0) / 65535.0;
  };
  MovingHead$Buffer.prototype.getFloat_gej297$_0 = function (coarseChannel, fineChannel) {
    if (fineChannel == null) {
      return this.getFloat_b37jry$_0(coarseChannel);
    }
    var firstByte = this.buffer.get_6ui4v4$(coarseChannel) & 255;
    var secondByte = this.buffer.get_6ui4v4$(fineChannel) & 255;
    var scaled = (firstByte * 256 | 0) + secondByte | 0;
    return scaled / 65535.0;
  };
  MovingHead$Buffer.prototype.setFloat_vl9zqr$_0 = function (channel, value) {
    var scaled = numberToInt(value * 65535);
    this.buffer.set_h90ill$(channel, toByte(scaled >> 8));
  };
  MovingHead$Buffer.prototype.setFloat_8xjzlm$_0 = function (coarseChannel, fineChannel, value) {
    if (fineChannel == null) {
      return this.setFloat_vl9zqr$_0(coarseChannel, value);
    }
    var scaled = numberToInt(value * 65535);
    this.buffer.set_h90ill$(coarseChannel, toByte(scaled >> 8));
    this.buffer.set_h90ill$(fineChannel, toByte(scaled & 255));
  };
  MovingHead$Buffer.$metadata$ = {
    kind: Kind_INTERFACE,
    simpleName: 'Buffer',
    interfaces: []
  };
  function MovingHead$MovingHeadPosition(x, y) {
    MovingHead$MovingHeadPosition$Companion_getInstance();
    this.x = x;
    this.y = y;
  }
  function MovingHead$MovingHeadPosition$Companion() {
    MovingHead$MovingHeadPosition$Companion_instance = this;
  }
  MovingHead$MovingHeadPosition$Companion.prototype.serializer = function () {
    return MovingHead$MovingHeadPosition$$serializer_getInstance();
  };
  MovingHead$MovingHeadPosition$Companion.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'Companion',
    interfaces: []
  };
  var MovingHead$MovingHeadPosition$Companion_instance = null;
  function MovingHead$MovingHeadPosition$Companion_getInstance() {
    if (MovingHead$MovingHeadPosition$Companion_instance === null) {
      new MovingHead$MovingHeadPosition$Companion();
    }
    return MovingHead$MovingHeadPosition$Companion_instance;
  }
  function MovingHead$MovingHeadPosition$$serializer() {
    this.descriptor_d5pxl4$_0 = new SerialClassDescImpl('baaahs.MovingHead.MovingHeadPosition', this);
    this.descriptor.addElement_ivxn3r$('x', false);
    this.descriptor.addElement_ivxn3r$('y', false);
    MovingHead$MovingHeadPosition$$serializer_instance = this;
  }
  Object.defineProperty(MovingHead$MovingHeadPosition$$serializer.prototype, 'descriptor', {
    get: function () {
      return this.descriptor_d5pxl4$_0;
    }
  });
  MovingHead$MovingHeadPosition$$serializer.prototype.serialize_awe97i$ = function (encoder, obj) {
    var output = encoder.beginStructure_r0sa6z$(this.descriptor, []);
    output.encodeIntElement_4wpqag$(this.descriptor, 0, obj.x);
    output.encodeIntElement_4wpqag$(this.descriptor, 1, obj.y);
    output.endStructure_qatsm0$(this.descriptor);
  };
  MovingHead$MovingHeadPosition$$serializer.prototype.deserialize_nts5qn$ = function (decoder) {
    var index, readAll = false;
    var bitMask0 = 0;
    var local0
    , local1;
    var input = decoder.beginStructure_r0sa6z$(this.descriptor, []);
    loopLabel: while (true) {
      index = input.decodeElementIndex_qatsm0$(this.descriptor);
      switch (index) {
        case -2:
          readAll = true;
        case 0:
          local0 = input.decodeIntElement_3zr2iy$(this.descriptor, 0);
          bitMask0 |= 1;
          if (!readAll)
            break;
        case 1:
          local1 = input.decodeIntElement_3zr2iy$(this.descriptor, 1);
          bitMask0 |= 2;
          if (!readAll)
            break;
        case -1:
          break loopLabel;
        default:throw new UnknownFieldException(index);
      }
    }
    input.endStructure_qatsm0$(this.descriptor);
    return MovingHead$MovingHead$MovingHeadPosition_init(bitMask0, local0, local1, null);
  };
  MovingHead$MovingHeadPosition$$serializer.prototype.childSerializers = function () {
    return [internal.IntSerializer, internal.IntSerializer];
  };
  MovingHead$MovingHeadPosition$$serializer.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: '$serializer',
    interfaces: [GeneratedSerializer]
  };
  var MovingHead$MovingHeadPosition$$serializer_instance = null;
  function MovingHead$MovingHeadPosition$$serializer_getInstance() {
    if (MovingHead$MovingHeadPosition$$serializer_instance === null) {
      new MovingHead$MovingHeadPosition$$serializer();
    }
    return MovingHead$MovingHeadPosition$$serializer_instance;
  }
  function MovingHead$MovingHead$MovingHeadPosition_init(seen1, x, y, serializationConstructorMarker) {
    var $this = serializationConstructorMarker || Object.create(MovingHead$MovingHeadPosition.prototype);
    if ((seen1 & 1) === 0)
      throw new MissingFieldException('x');
    else
      $this.x = x;
    if ((seen1 & 2) === 0)
      throw new MissingFieldException('y');
    else
      $this.y = y;
    return $this;
  }
  MovingHead$MovingHeadPosition.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'MovingHeadPosition',
    interfaces: []
  };
  MovingHead$MovingHeadPosition.prototype.component1 = function () {
    return this.x;
  };
  MovingHead$MovingHeadPosition.prototype.component2 = function () {
    return this.y;
  };
  MovingHead$MovingHeadPosition.prototype.copy_vux9f0$ = function (x, y) {
    return new MovingHead$MovingHeadPosition(x === void 0 ? this.x : x, y === void 0 ? this.y : y);
  };
  MovingHead$MovingHeadPosition.prototype.toString = function () {
    return 'MovingHeadPosition(x=' + Kotlin.toString(this.x) + (', y=' + Kotlin.toString(this.y)) + ')';
  };
  MovingHead$MovingHeadPosition.prototype.hashCode = function () {
    var result = 0;
    result = result * 31 + Kotlin.hashCode(this.x) | 0;
    result = result * 31 + Kotlin.hashCode(this.y) | 0;
    return result;
  };
  MovingHead$MovingHeadPosition.prototype.equals = function (other) {
    return this === other || (other !== null && (typeof other === 'object' && (Object.getPrototypeOf(this) === Object.getPrototypeOf(other) && (Kotlin.equals(this.x, other.x) && Kotlin.equals(this.y, other.y)))));
  };
  function MovingHead$Companion() {
    MovingHead$Companion_instance = this;
  }
  MovingHead$Companion.prototype.serializer = function () {
    return MovingHead$$serializer_getInstance();
  };
  MovingHead$Companion.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'Companion',
    interfaces: []
  };
  var MovingHead$Companion_instance = null;
  function MovingHead$Companion_getInstance() {
    if (MovingHead$Companion_instance === null) {
      new MovingHead$Companion();
    }
    return MovingHead$Companion_instance;
  }
  function MovingHead$$serializer() {
    this.descriptor_gmlx87$_0 = new SerialClassDescImpl('baaahs.MovingHead', this);
    this.descriptor.addElement_ivxn3r$('name', false);
    this.descriptor.addElement_ivxn3r$('origin', false);
    MovingHead$$serializer_instance = this;
  }
  Object.defineProperty(MovingHead$$serializer.prototype, 'descriptor', {
    get: function () {
      return this.descriptor_gmlx87$_0;
    }
  });
  MovingHead$$serializer.prototype.serialize_awe97i$ = function (encoder, obj) {
    var output = encoder.beginStructure_r0sa6z$(this.descriptor, []);
    output.encodeStringElement_bgm7zs$(this.descriptor, 0, obj.name);
    output.encodeSerializableElement_blecud$(this.descriptor, 1, SheepModel$Point$$serializer_getInstance(), obj.origin);
    output.endStructure_qatsm0$(this.descriptor);
  };
  MovingHead$$serializer.prototype.deserialize_nts5qn$ = function (decoder) {
    var index, readAll = false;
    var bitMask0 = 0;
    var local0
    , local1;
    var input = decoder.beginStructure_r0sa6z$(this.descriptor, []);
    loopLabel: while (true) {
      index = input.decodeElementIndex_qatsm0$(this.descriptor);
      switch (index) {
        case -2:
          readAll = true;
        case 0:
          local0 = input.decodeStringElement_3zr2iy$(this.descriptor, 0);
          bitMask0 |= 1;
          if (!readAll)
            break;
        case 1:
          local1 = (bitMask0 & 2) === 0 ? input.decodeSerializableElement_s44l7r$(this.descriptor, 1, SheepModel$Point$$serializer_getInstance()) : input.updateSerializableElement_ehubvl$(this.descriptor, 1, SheepModel$Point$$serializer_getInstance(), local1);
          bitMask0 |= 2;
          if (!readAll)
            break;
        case -1:
          break loopLabel;
        default:throw new UnknownFieldException(index);
      }
    }
    input.endStructure_qatsm0$(this.descriptor);
    return MovingHead_init(bitMask0, local0, local1, null);
  };
  MovingHead$$serializer.prototype.childSerializers = function () {
    return [internal.StringSerializer, SheepModel$Point$$serializer_getInstance()];
  };
  MovingHead$$serializer.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: '$serializer',
    interfaces: [GeneratedSerializer]
  };
  var MovingHead$$serializer_instance = null;
  function MovingHead$$serializer_getInstance() {
    if (MovingHead$$serializer_instance === null) {
      new MovingHead$$serializer();
    }
    return MovingHead$$serializer_instance;
  }
  function MovingHead_init(seen1, name, origin, serializationConstructorMarker) {
    var $this = serializationConstructorMarker || Object.create(MovingHead.prototype);
    if ((seen1 & 1) === 0)
      throw new MissingFieldException('name');
    else
      $this.name = name;
    if ((seen1 & 2) === 0)
      throw new MissingFieldException('origin');
    else
      $this.origin = origin;
    return $this;
  }
  MovingHead.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'MovingHead',
    interfaces: []
  };
  MovingHead.prototype.component1 = function () {
    return this.name;
  };
  MovingHead.prototype.component2 = function () {
    return this.origin;
  };
  MovingHead.prototype.copy_s2rku2$ = function (name, origin) {
    return new MovingHead(name === void 0 ? this.name : name, origin === void 0 ? this.origin : origin);
  };
  MovingHead.prototype.toString = function () {
    return 'MovingHead(name=' + Kotlin.toString(this.name) + (', origin=' + Kotlin.toString(this.origin)) + ')';
  };
  MovingHead.prototype.hashCode = function () {
    var result = 0;
    result = result * 31 + Kotlin.hashCode(this.name) | 0;
    result = result * 31 + Kotlin.hashCode(this.origin) | 0;
    return result;
  };
  MovingHead.prototype.equals = function (other) {
    return this === other || (other !== null && (typeof other === 'object' && (Object.getPrototypeOf(this) === Object.getPrototypeOf(other) && (Kotlin.equals(this.name, other.name) && Kotlin.equals(this.origin, other.origin)))));
  };
  function MovingHeadManager(fs, pubSub, movingHeads) {
    this.fs_0 = fs;
    this.pubSub_0 = pubSub;
    this.movingHeadsChannel_0 = this.pubSub_0.publish_oiz02e$(Topics_getInstance().movingHeads, movingHeads, MovingHeadManager$movingHeadsChannel$lambda);
    this.defaultPosition_0 = new MovingHead$MovingHeadPosition(127, 127);
    this.currentPositions_0 = LinkedHashMap_init();
    this.listeners_0 = LinkedHashMap_init();
    this.movingHeadPresets_0 = LinkedHashMap_init();
    this.json_0 = new Json(JsonConfiguration.Companion.Stable);
    this.presetsFileName_0 = 'presets/moving-head-positions.json';
    var presetsJson = this.fs_0.loadFile_61zpoe$(this.presetsFileName_0);
    if (presetsJson != null) {
      var map = this.json_0.parse_awif5v$(Topics_getInstance().movingHeadPresets.serializer, presetsJson);
      this.movingHeadPresets_0.putAll_a2k3zr$(map);
    }
    this.movingHeadPresetsChannel_0 = this.pubSub_0.publish_oiz02e$(Topics_getInstance().movingHeadPresets, mutableMapOf([to('Disco Balls', new MovingHead$MovingHeadPosition(123, 200))]), MovingHeadManager$movingHeadPresetsChannel$lambda(this));
    var destination = ArrayList_init_1(collectionSizeOrDefault(movingHeads, 10));
    var tmp$;
    tmp$ = movingHeads.iterator();
    while (tmp$.hasNext()) {
      var item = tmp$.next();
      var tmp$_0 = destination.add_11rb$;
      var topic = new PubSub$Topic('movingHead/' + item.name, MovingHead$MovingHeadPosition$Companion_getInstance().serializer());
      var $receiver = this.currentPositions_0;
      var value = this.defaultPosition_0;
      $receiver.put_xwzc9p$(item, value);
      tmp$_0.call(destination, this.pubSub_0.publish_oiz02e$(topic, this.defaultPosition_0, MovingHeadManager_init$lambda$lambda(this, item)));
    }
  }
  MovingHeadManager.prototype.listen_proz6e$ = function (movingHead, onUpdate) {
    this.listeners_0.put_xwzc9p$(movingHead, onUpdate);
  };
  function MovingHeadManager$movingHeadsChannel$lambda(it) {
    return Unit;
  }
  function MovingHeadManager$movingHeadPresetsChannel$lambda(this$MovingHeadManager) {
    return function (map) {
      this$MovingHeadManager.fs_0.createFile_qz9155$(this$MovingHeadManager.presetsFileName_0, this$MovingHeadManager.json_0.stringify_tf03ej$(Topics_getInstance().movingHeadPresets.serializer, map), true);
      println('Saved ' + map + ' to disk!');
      return Unit;
    };
  }
  function MovingHeadManager_init$lambda$lambda(this$MovingHeadManager, closure$movingHead) {
    return function (onUpdate) {
      var tmp$;
      var $receiver = this$MovingHeadManager.currentPositions_0;
      var key = closure$movingHead;
      $receiver.put_xwzc9p$(key, onUpdate);
      (tmp$ = this$MovingHeadManager.listeners_0.get_11rb$(closure$movingHead)) != null ? tmp$(onUpdate) : null;
      return Unit;
    };
  }
  MovingHeadManager.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'MovingHeadManager',
    interfaces: []
  };
  function MovingHeadDisplay(pubSub, onUpdatedMovingHeads) {
    this.pubSub = pubSub;
    this.pubSub.subscribe(Topics_getInstance().movingHeads, MovingHeadDisplay_init$lambda(this, onUpdatedMovingHeads));
    this.presets_0 = LinkedHashMap_init();
    this.presetsListeners_0 = ArrayList_init();
    this.movingHeadPresetsChannel_0 = this.pubSub.subscribe(Topics_getInstance().movingHeadPresets, MovingHeadDisplay$movingHeadPresetsChannel$lambda(this));
  }
  MovingHeadDisplay.prototype.notifyPresetsListeners_0 = function () {
    var json = Json.Companion.stringify_tf03ej$(Topics_getInstance().movingHeadPresets.serializer, this.presets_0);
    var tmp$;
    tmp$ = this.presetsListeners_0.iterator();
    while (tmp$.hasNext()) {
      var element = tmp$.next();
      element(json);
    }
  };
  MovingHeadDisplay.prototype.savePreset = function (name, position) {
    this.presets_0.put_xwzc9p$(name, position);
    this.movingHeadPresetsChannel_0.onChange(this.presets_0);
    this.notifyPresetsListeners_0();
  };
  MovingHeadDisplay.prototype.addPresetsListener = function (callback) {
    this.presetsListeners_0.add_11rb$(callback);
  };
  MovingHeadDisplay.prototype.removePresetsListener = function (callback) {
    this.presetsListeners_0.remove_11rb$(callback);
  };
  function MovingHeadDisplay$Wrapper(movingHead, pubSub) {
    this.movingHead = movingHead;
    this.topic_0 = new PubSub$Topic('movingHead/' + this.movingHead.name, MovingHead$MovingHeadPosition$Companion_getInstance().serializer());
    this.listeners_0 = ArrayList_init();
    this.channel_0 = pubSub.subscribe(this.topic_0, MovingHeadDisplay$Wrapper$channel$lambda(this));
    this.position_a5md14$_0 = null;
  }
  Object.defineProperty(MovingHeadDisplay$Wrapper.prototype, 'name', {
    get: function () {
      return this.movingHead.name;
    }
  });
  Object.defineProperty(MovingHeadDisplay$Wrapper.prototype, 'position', {
    get: function () {
      return this.position_a5md14$_0;
    },
    set: function (value) {
      this.position_a5md14$_0 = value;
      if (value != null)
        this.notifyListeners_0(value);
    }
  });
  MovingHeadDisplay$Wrapper.prototype.notifyListeners_0 = function (value) {
    var tmp$;
    (tmp$ = this.channel_0) != null ? (tmp$.onChange(value), Unit) : null;
    var tmp$_0;
    tmp$_0 = this.listeners_0.iterator();
    while (tmp$_0.hasNext()) {
      var element = tmp$_0.next();
      element(value);
    }
  };
  MovingHeadDisplay$Wrapper.prototype.addListener = function (callback) {
    this.listeners_0.add_11rb$(callback);
  };
  MovingHeadDisplay$Wrapper.prototype.removeListener = function (callback) {
    this.listeners_0.remove_11rb$(callback);
  };
  function MovingHeadDisplay$Wrapper$channel$lambda(this$Wrapper) {
    return function (onUpdate) {
      this$Wrapper.position = onUpdate;
      return Unit;
    };
  }
  MovingHeadDisplay$Wrapper.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Wrapper',
    interfaces: []
  };
  function MovingHeadDisplay_init$lambda(this$MovingHeadDisplay, closure$onUpdatedMovingHeads) {
    return function (movingHeads) {
      var destination = ArrayList_init_1(collectionSizeOrDefault(movingHeads, 10));
      var tmp$;
      tmp$ = movingHeads.iterator();
      while (tmp$.hasNext()) {
        var item = tmp$.next();
        destination.add_11rb$(new MovingHeadDisplay$Wrapper(item, this$MovingHeadDisplay.pubSub));
      }
      var wrappers = destination;
      closure$onUpdatedMovingHeads(copyToArray(wrappers));
      return Unit;
    };
  }
  function MovingHeadDisplay$movingHeadPresetsChannel$lambda(this$MovingHeadDisplay) {
    return function (map) {
      this$MovingHeadDisplay.presets_0.clear();
      this$MovingHeadDisplay.presets_0.putAll_a2k3zr$(map);
      this$MovingHeadDisplay.notifyPresetsListeners_0();
      return Unit;
    };
  }
  MovingHeadDisplay.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'MovingHeadDisplay',
    interfaces: []
  };
  function Pinky(model, shows, network, dmxUniverse, beatSource, clock, fs, firmwareDaddy, display, prerenderPixels) {
    if (prerenderPixels === void 0)
      prerenderPixels = false;
    this.model = model;
    this.shows = shows;
    this.network = network;
    this.dmxUniverse = dmxUniverse;
    this.beatSource = beatSource;
    this.clock = clock;
    this.fs = fs;
    this.firmwareDaddy = firmwareDaddy;
    this.display = display;
    this.prerenderPixels_0 = prerenderPixels;
    this.storage_0 = new Storage(this.fs);
    this.mappingResults_0 = this.storage_0.loadMappingData_ld9ij$(this.model);
    this.link_0 = new FragmentingUdpLink(this.network.link());
    this.httpServer = this.link_0.startHttpServer_za3lpa$(8004);
    this.beatProvider_0 = new Pinky$PinkyBeatDisplayer(this, this.beatSource);
    this.mapperIsRunning_0 = false;
    this.selectedShow_vpdlot$_0 = first(this.shows);
    var $receiver = new PubSub$Server(this.httpServer);
    $receiver.install_stpyu4$(gadgetModule);
    this.pubSub_0 = $receiver;
    this.gadgetManager_0 = new GadgetManager(this.pubSub_0);
    this.movingHeadManager_0 = new MovingHeadManager(this.fs, this.pubSub_0, this.model.movingHeads);
    this.showRunner_0 = new ShowRunner(this.model, this.selectedShow_0, this.gadgetManager_0, this.beatSource, this.dmxUniverse, this.movingHeadManager_0, this.clock);
    this.brainToSurfaceMap_CHEAT_0 = LinkedHashMap_init();
    this.surfaceToPixelLocationMap_CHEAT_0 = LinkedHashMap_init();
    this.brainInfos_0 = LinkedHashMap_init();
    this.pendingBrainInfos_0 = LinkedHashMap_init();
    this.networkStats_0 = new Pinky$NetworkStats();
    this.udpSocket_0 = this.link_0.listenUdp_a6m852$(8002, this);
    this.httpServer.listenWebSocket_brdh44$('/ws/mapper', Pinky_init$lambda(this));
    this.poolingRenderContext = new Pinky$PoolingRenderContext();
    this.lastSentAt = L0;
  }
  Object.defineProperty(Pinky.prototype, 'selectedShow_0', {
    get: function () {
      return this.selectedShow_vpdlot$_0;
    },
    set: function (value) {
      this.selectedShow_vpdlot$_0 = value;
      this.display.selectedShow = value;
      this.showRunner_0.nextShow = this.selectedShow_0;
    }
  });
  Object.defineProperty(Pinky.prototype, 'address', {
    get: function () {
      return this.link_0.myAddress;
    }
  });
  function Coroutine$Pinky$run$lambda(this$Pinky_0, $receiver_0, controller, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.$controller = controller;
    this.exceptionState_0 = 1;
    this.local$this$Pinky = this$Pinky_0;
  }
  Coroutine$Pinky$run$lambda.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$Pinky$run$lambda.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$Pinky$run$lambda.prototype.constructor = Coroutine$Pinky$run$lambda;
  Coroutine$Pinky$run$lambda.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.state_0 = 2;
            this.result_0 = this.local$this$Pinky.beatProvider_0.run(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 1:
            throw this.exception_0;
          case 2:
            return this.result_0;
          default:this.state_0 = 1;
            throw new Error('State Machine Unreachable execution');
        }
      }
       catch (e) {
        if (this.state_0 === 1) {
          this.exceptionState_0 = this.state_0;
          throw e;
        }
         else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  function Pinky$run$lambda(this$Pinky_0) {
    return function ($receiver_0, continuation_0, suspended) {
      var instance = new Coroutine$Pinky$run$lambda(this$Pinky_0, $receiver_0, this, continuation_0);
      if (suspended)
        return instance;
      else
        return instance.doResume(null);
    };
  }
  function Pinky$run$lambda_0(it) {
    return Unit;
  }
  function Pinky$run$lambda_1(this$Pinky) {
    return function (selectedShow) {
      var tmp$ = this$Pinky;
      var $receiver = this$Pinky.shows;
      var firstOrNull$result;
      firstOrNull$break: do {
        var tmp$_0;
        tmp$_0 = $receiver.iterator();
        while (tmp$_0.hasNext()) {
          var element = tmp$_0.next();
          if (equals(element.name, selectedShow)) {
            firstOrNull$result = element;
            break firstOrNull$break;
          }
        }
        firstOrNull$result = null;
      }
       while (false);
      tmp$.selectedShow_0 = ensureNotNull(firstOrNull$result);
      return Unit;
    };
  }
  function Pinky$run$lambda_2(this$Pinky, closure$selectedShowChannel) {
    return function () {
      this$Pinky.selectedShow_0 = ensureNotNull(this$Pinky.display.selectedShow);
      closure$selectedShowChannel.onChange(this$Pinky.selectedShow_0.name);
      return Unit;
    };
  }
  function Coroutine$Pinky$run$lambda_0(this$Pinky_0, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.exceptionState_0 = 1;
    this.local$this$Pinky = this$Pinky_0;
  }
  Coroutine$Pinky$run$lambda_0.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$Pinky$run$lambda_0.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$Pinky$run$lambda_0.prototype.constructor = Coroutine$Pinky$run$lambda_0;
  Coroutine$Pinky$run$lambda_0.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            return this.local$this$Pinky.drawNextFrame_8be2vx$(), Unit;
          case 1:
            throw this.exception_0;
          default:this.state_0 = 1;
            throw new Error('State Machine Unreachable execution');
        }
      }
       catch (e) {
        if (this.state_0 === 1) {
          this.exceptionState_0 = this.state_0;
          throw e;
        }
         else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  function Pinky$run$lambda_3(this$Pinky_0) {
    return function (continuation_0, suspended) {
      var instance = new Coroutine$Pinky$run$lambda_0(this$Pinky_0, continuation_0);
      if (suspended)
        return instance;
      else
        return instance.doResume(null);
    };
  }
  function Coroutine$run_0($this, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.exceptionState_0 = 1;
    this.$this = $this;
  }
  Coroutine$run_0.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$run_0.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$run_0.prototype.constructor = Coroutine$run_0;
  Coroutine$run_0.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            launch(coroutines.GlobalScope, void 0, void 0, Pinky$run$lambda(this.$this));
            this.$this.display.listShows_3lsa6o$(this.$this.shows);
            this.$this.display.selectedShow = this.$this.selectedShow_0;
            var tmp$ = this.$this.pubSub_0;
            var tmp$_0 = Topics_getInstance().availableShows;
            var $receiver = this.$this.shows;
            var destination = ArrayList_init_1(collectionSizeOrDefault($receiver, 10));
            var tmp$_1;
            tmp$_1 = $receiver.iterator();
            while (tmp$_1.hasNext()) {
              var item = tmp$_1.next();
              destination.add_11rb$(item.name);
            }

            tmp$.publish_oiz02e$(tmp$_0, destination, Pinky$run$lambda_0);
            var selectedShowChannel = this.$this.pubSub_0.publish_oiz02e$(Topics_getInstance().selectedShow, this.$this.shows.get_za3lpa$(0).name, Pinky$run$lambda_1(this.$this));
            this.$this.display.onShowChange = Pinky$run$lambda_2(this.$this, selectedShowChannel);
            this.state_0 = 2;
            continue;
          case 1:
            throw this.exception_0;
          case 2:
            if (this.$this.mapperIsRunning_0) {
              this.$this.disableDmx_0();
              this.state_0 = 3;
              this.result_0 = delay(L50, this);
              if (this.result_0 === COROUTINE_SUSPENDED)
                return COROUTINE_SUSPENDED;
              continue;
            }
             else {
              this.state_0 = 4;
              continue;
            }

          case 3:
            this.state_0 = 2;
            continue;
          case 4:
            this.$this.updateSurfaces_8be2vx$();
            this.$this.networkStats_0.reset_8be2vx$();
            this.state_0 = 5;
            this.result_0 = time(Pinky$run$lambda_3(this.$this), this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 5:
            var elapsedMs = this.result_0;
            this.$this.display.showFrameMs = elapsedMs.toInt();
            this.$this.display.stats = this.$this.networkStats_0;
            this.state_0 = 6;
            this.result_0 = delay(L50, this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 6:
            this.state_0 = 2;
            continue;
          default:this.state_0 = 1;
            throw new Error('State Machine Unreachable execution');
        }
      }
       catch (e) {
        if (this.state_0 === 1) {
          this.exceptionState_0 = this.state_0;
          throw e;
        }
         else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  Pinky.prototype.run = function (continuation_0, suspended) {
    var instance = new Coroutine$run_0(this, continuation_0);
    if (suspended)
      return instance;
    else
      return instance.doResume(null);
  };
  Pinky.prototype.updateSurfaces_8be2vx$ = function () {
    if (!this.pendingBrainInfos_0.isEmpty()) {
      var brainSurfacesToRemove = ArrayList_init();
      var brainSurfacesToAdd = ArrayList_init();
      var tmp$;
      tmp$ = this.pendingBrainInfos_0.entries.iterator();
      while (tmp$.hasNext()) {
        var element = tmp$.next();
        var brainId = element.key;
        var brainInfo = element.value;
        var priorBrainInfo = this.brainInfos_0.get_11rb$(brainId);
        if (priorBrainInfo != null) {
          brainSurfacesToRemove.add_11rb$(priorBrainInfo.surfaceReceiver);
        }
        brainSurfacesToAdd.add_11rb$(brainInfo.surfaceReceiver);
        this.brainInfos_0.put_xwzc9p$(brainId, brainInfo);
      }
      this.showRunner_0.surfacesChanged_ji9tfc$(brainSurfacesToAdd, brainSurfacesToRemove);
      this.pendingBrainInfos_0.clear();
    }
    this.display.brainCount = this.brainInfos_0.size;
  };
  function Pinky$drawNextFrame$lambda(this$Pinky) {
    return function () {
      this$Pinky.showRunner_0.nextFrame();
      return Unit;
    };
  }
  Pinky.prototype.drawNextFrame_8be2vx$ = function () {
    this.aroundNextFrame_0(Pinky$drawNextFrame$lambda(this));
  };
  Pinky.prototype.disableDmx_0 = function () {
    this.dmxUniverse.allOff();
  };
  Pinky.prototype.receive_ytpeqp$ = function (fromAddress, fromPort, bytes) {
    var message = parse(bytes);
    if (Kotlin.isType(message, BrainHelloMessage))
      this.foundBrain_0(fromAddress, message);
    else if (Kotlin.isType(message, MapperHelloMessage)) {
      println('Mapper isRunning=' + message.isRunning);
      this.mapperIsRunning_0 = message.isRunning;
    }
     else if (Kotlin.isType(message, PingMessage))
      if (message.isPong)
        this.receivedPong_0(message, fromAddress);
  };
  function Pinky$foundBrain$lambda(closure$brainAddress, this$Pinky) {
    return function (shaderBuffer) {
      this$Pinky.sendBrainShaderMessage_0(closure$brainAddress, shaderBuffer);
      return Unit;
    };
  }
  Pinky.prototype.foundBrain_0 = function (brainAddress, msg) {
    var tmp$, tmp$_0, tmp$_1, tmp$_2;
    var brainId = new BrainId(msg.brainId);
    var surfaceName = msg.surfaceName;
    println('foundBrain(' + brainAddress + ', ' + msg + ')');
    if (this.firmwareDaddy.doesntLikeThisVersion_pdl1vj$(msg.firmwareVersion)) {
      println("The firmware daddy doesn't like " + brainId + ' having ' + toString_0(msg.firmwareVersion) + " so we'll send " + this.firmwareDaddy.urlForPreferredVersion);
      var newHotness = new UseFirmwareMessage(this.firmwareDaddy.urlForPreferredVersion);
      this.udpSocket_0.sendUdp_wpmaqi$(brainAddress, 8003, newHotness);
    }
    var dataFor = (tmp$ = this.mappingResults_0.dataFor_77gxvx$(brainId)) != null ? tmp$ : this.findMappingInfo_CHEAT_0(surfaceName, brainId);
    var tmp$_3;
    if (dataFor != null) {
      var tmp$_4;
      var pixelVertices = dataFor.pixelLocations;
      var pixelCount = (tmp$_4 = pixelVertices != null ? pixelVertices.size : null) != null ? tmp$_4 : 2048;
      var mappingMsg = new BrainMappingMessage(brainId, dataFor.surface.name, null, new Vector2F(0.0, 0.0), new Vector2F(0.0, 0.0), pixelCount, pixelVertices != null ? pixelVertices : emptyList());
      this.udpSocket_0.sendUdp_wpmaqi$(brainAddress, 8003, mappingMsg);
      tmp$_3 = new IdentifiedSurface(dataFor.surface, pixelCount, dataFor.pixelLocations);
    }
     else
      tmp$_3 = null;
    var surface = (tmp$_0 = tmp$_3) != null ? tmp$_0 : new AnonymousSurface(brainId);
    var priorBrainInfo = this.brainInfos_0.get_11rb$(brainId);
    if (priorBrainInfo != null) {
      if (((tmp$_1 = priorBrainInfo.brainId) != null ? tmp$_1.equals(brainId) : null) && equals(priorBrainInfo.surface, surface)) {
        return;
      }
    }
    var sendFn = Pinky$foundBrain$lambda(brainAddress, this);
    if (this.prerenderPixels_0) {
      tmp$_2 = new Pinky$PrerenderingSurfaceReceiver(this, surface, sendFn);
    }
     else {
      tmp$_2 = new ShowRunner$SurfaceReceiver(surface, sendFn);
    }
    var surfaceReceiver = tmp$_2;
    var brainInfo = new BrainInfo(brainAddress, brainId, surface, msg.firmwareVersion, msg.idfVersion, surfaceReceiver);
    this.pendingBrainInfos_0.put_xwzc9p$(brainId, brainInfo);
  };
  Pinky.prototype.sendBrainShaderMessage_0 = function (brainAddress, shaderBuffer) {
    var tmp$;
    var $receiver = new ByteArrayWriter();
    $receiver.writeLong_s8cxhz$(getTimeMillis());
    var pongData = $receiver.toBytes();
    var message = (new BrainShaderMessage(shaderBuffer.shader, shaderBuffer, pongData)).toBytes();
    this.udpSocket_0.sendUdp_ytpeqp$(brainAddress, 8003, message);
    var tmp$_0;
    tmp$_0 = this.networkStats_0;
    tmp$_0.packetsSent = tmp$_0.packetsSent + 1 | 0;
    tmp$ = this.networkStats_0;
    tmp$.bytesSent = tmp$.bytesSent + message.length | 0;
  };
  Pinky.prototype.findMappingInfo_CHEAT_0 = function (surfaceName, brainId) {
    var tmp$, tmp$_0;
    var modelSurface = (tmp$ = surfaceName != null ? this.model.findModelSurface_61zpoe$(surfaceName) : null) != null ? tmp$ : this.brainToSurfaceMap_CHEAT_0.get_11rb$(brainId);
    if (modelSurface != null) {
      tmp$_0 = new MappingResults$Info(modelSurface, this.surfaceToPixelLocationMap_CHEAT_0.get_11rb$(modelSurface));
    }
     else {
      tmp$_0 = null;
    }
    return tmp$_0;
  };
  Pinky.prototype.receivedPong_0 = function (message, fromAddress) {
    var originalSentAt = (new ByteArrayReader(message.data)).readLong();
    var elapsedMs = getTimeMillis().subtract(originalSentAt);
  };
  Pinky.prototype.providePanelMapping_CHEAT_iegnfh$ = function (brainId, surface) {
    this.brainToSurfaceMap_CHEAT_0.put_xwzc9p$(brainId, surface);
  };
  Pinky.prototype.providePixelMapping_CHEAT_cafowi$ = function (surface, pixelLocations) {
    this.surfaceToPixelLocationMap_CHEAT_0.put_xwzc9p$(surface, pixelLocations);
  };
  function Pinky$PinkyBeatDisplayer($outer, beatSource) {
    this.$outer = $outer;
    this.beatSource = beatSource;
  }
  function Coroutine$run_1($this, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.exceptionState_0 = 1;
    this.$this = $this;
  }
  Coroutine$run_1.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$run_1.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$run_1.prototype.constructor = Coroutine$run_1;
  Coroutine$run_1.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.state_0 = 2;
            continue;
          case 1:
            throw this.exception_0;
          case 2:
            var beatData = this.$this.beatSource.getBeatData();
            this.$this.$outer.display.beat = numberToInt(beatData.beatWithinMeasure_rnw5ii$(this.$this.$outer.clock));
            this.$this.$outer.display.bpm = numberToInt(beatData.bpm);
            this.state_0 = 3;
            this.result_0 = delay(L10, this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 3:
            this.state_0 = 2;
            continue;
          default:this.state_0 = 1;
            throw new Error('State Machine Unreachable execution');
        }
      }
       catch (e) {
        if (this.state_0 === 1) {
          this.exceptionState_0 = this.state_0;
          throw e;
        }
         else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  Pinky$PinkyBeatDisplayer.prototype.run = function (continuation_0, suspended) {
    var instance = new Coroutine$run_1(this, continuation_0);
    if (suspended)
      return instance;
    else
      return instance.doResume(null);
  };
  Pinky$PinkyBeatDisplayer.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'PinkyBeatDisplayer',
    interfaces: []
  };
  function Pinky$NetworkStats(bytesSent, packetsSent) {
    if (bytesSent === void 0)
      bytesSent = 0;
    if (packetsSent === void 0)
      packetsSent = 0;
    this.bytesSent = bytesSent;
    this.packetsSent = packetsSent;
  }
  Pinky$NetworkStats.prototype.reset_8be2vx$ = function () {
    this.bytesSent = 0;
    this.packetsSent = 0;
  };
  Pinky$NetworkStats.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'NetworkStats',
    interfaces: []
  };
  function Pinky$PrerenderingSurfaceReceiver($outer, surface, sendFn) {
    this.$outer = $outer;
    ShowRunner$SurfaceReceiver.call(this, surface, sendFn);
    this.currentRenderTree = null;
    this.currentPoolKey_0 = null;
    this.pixels = null;
    this.currentBuffer = null;
  }
  function Pinky$PrerenderingSurfaceReceiver$send$ObjectLiteral(closure$newPoolKey, this$Pinky) {
    this.closure$newPoolKey = closure$newPoolKey;
    this.this$Pinky = this$Pinky;
  }
  Pinky$PrerenderingSurfaceReceiver$send$ObjectLiteral.prototype.registerPooled_7d3fln$ = function (key, fn) {
    this.closure$newPoolKey.v = key;
    return this.this$Pinky.poolingRenderContext.registerPooled_7d3fln$(key, fn);
  };
  Pinky$PrerenderingSurfaceReceiver$send$ObjectLiteral.$metadata$ = {
    kind: Kind_CLASS,
    interfaces: [RenderContext]
  };
  Pinky$PrerenderingSurfaceReceiver.prototype.send_i8eued$ = function (shaderBuffer) {
    var tmp$, tmp$_0;
    var shader = Kotlin.isType(tmp$ = shaderBuffer.shader, Shader) ? tmp$ : throwCCE();
    var renderTree = this.currentRenderTree;
    if (renderTree == null || !equals(renderTree.shader, shader)) {
      var priorPoolKey = this.currentPoolKey_0;
      var newPoolKey = {v: null};
      var renderer = shader.createRenderer_omlfoo$(this.surface, new Pinky$PrerenderingSurfaceReceiver$send$ObjectLiteral(newPoolKey, this.$outer));
      if (!equals(newPoolKey.v, priorPoolKey)) {
        if (priorPoolKey != null) {
          this.$outer.poolingRenderContext.decrement_za3rmp$(priorPoolKey);
        }
        this.currentPoolKey_0 = newPoolKey.v;
      }
      renderTree = new Brain$RenderTree(shader, renderer, shaderBuffer);
      this.currentRenderTree = renderTree;
      if (this.pixels == null) {
        var pixelBuffer = (new PixelShader(PixelShader$Encoding$DIRECT_RGB_getInstance())).createBuffer_ppt8xj$(this.surface);
        this.pixels = new Pinky$PixelsAdapter(pixelBuffer);
      }
    }
    var renderer_0 = Kotlin.isType(tmp$_0 = ensureNotNull(this.currentRenderTree).renderer, Shader$Renderer) ? tmp$_0 : throwCCE();
    renderer_0.beginFrame_b23bvv$(shaderBuffer, ensureNotNull(this.pixels).size);
    this.currentBuffer = shaderBuffer;
  };
  Pinky$PrerenderingSurfaceReceiver.prototype.actuallySend = function () {
    var tmp$, tmp$_0, tmp$_1, tmp$_2, tmp$_3;
    var renderTree = this.currentRenderTree;
    if (renderTree != null) {
      var renderer = Kotlin.isType(tmp$ = renderTree.renderer, Shader$Renderer) ? tmp$ : throwCCE();
      var pixels = ensureNotNull(this.pixels);
      var currentBuffer = ensureNotNull(this.currentBuffer);
      tmp$_0 = pixels.indices;
      tmp$_1 = tmp$_0.first;
      tmp$_2 = tmp$_0.last;
      tmp$_3 = tmp$_0.step;
      for (var i = tmp$_1; i <= tmp$_2; i += tmp$_3) {
        pixels.set_ibd5tj$(i, renderer.draw_b23bvv$(currentBuffer, i));
      }
      this.currentBuffer = null;
      renderer.endFrame();
      pixels.finishedFrame();
      renderTree.draw_bbfl1t$(pixels);
      ShowRunner$SurfaceReceiver.prototype.send_i8eued$.call(this, pixels.buffer_8be2vx$);
    }
  };
  Pinky$PrerenderingSurfaceReceiver.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'PrerenderingSurfaceReceiver',
    interfaces: [ShowRunner$SurfaceReceiver]
  };
  function Pinky$aroundNextFrame$lambda(this$Pinky) {
    return function () {
      this$Pinky.poolingRenderContext.preDraw();
      return Unit;
    };
  }
  function Pinky$aroundNextFrame$lambda_0(this$Pinky) {
    return function () {
      var tmp$;
      tmp$ = this$Pinky.brainInfos_0.values.iterator();
      while (tmp$.hasNext()) {
        var element = tmp$.next();
        var tmp$_0;
        var surfaceReceiver = Kotlin.isType(tmp$_0 = element.surfaceReceiver, Pinky$PrerenderingSurfaceReceiver) ? tmp$_0 : throwCCE();
        surfaceReceiver.actuallySend();
      }
      return Unit;
    };
  }
  Pinky.prototype.aroundNextFrame_0 = function (callNextFrame) {
    callNextFrame();
    if (this.prerenderPixels_0) {
      var preDrawElapsed = timeSync(Pinky$aroundNextFrame$lambda(this));
      var sendElapsed = timeSync(Pinky$aroundNextFrame$lambda_0(this));
    }
    var now = getTimeMillis();
    var elapsedMs = now.subtract(this.lastSentAt);
    this.lastSentAt = now;
  };
  function Pinky$PoolingRenderContext() {
    this.pooledRenderers_0 = HashMap_init();
  }
  Pinky$PoolingRenderContext.prototype.registerPooled_7d3fln$ = function (key, fn) {
    var tmp$;
    var $receiver = this.pooledRenderers_0;
    var tmp$_0;
    var value = $receiver.get_11rb$(key);
    if (value == null) {
      var answer = new Pinky$PoolingRenderContext$Holder(fn());
      $receiver.put_xwzc9p$(key, answer);
      tmp$_0 = answer;
    }
     else {
      tmp$_0 = value;
    }
    var holder = tmp$_0;
    holder.count = holder.count + 1 | 0;
    return Kotlin.isType(tmp$ = holder.pooledRenderer, PooledRenderer) ? tmp$ : throwCCE();
  };
  Pinky$PoolingRenderContext.prototype.decrement_za3rmp$ = function (key) {
    var holder = ensureNotNull(this.pooledRenderers_0.get_11rb$(key));
    holder.count = holder.count - 1 | 0;
    if (holder.count === 0) {
      logger$Companion_getInstance().debug_61zpoe$('Removing pooled renderer for ' + key.toString());
      this.pooledRenderers_0.remove_11rb$(key);
    }
  };
  Pinky$PoolingRenderContext.prototype.preDraw = function () {
    var tmp$;
    tmp$ = this.pooledRenderers_0.values.iterator();
    while (tmp$.hasNext()) {
      var element = tmp$.next();
      element.pooledRenderer.preDraw();
    }
  };
  function Pinky$PoolingRenderContext$Holder(pooledRenderer, count) {
    if (count === void 0)
      count = 0;
    this.pooledRenderer = pooledRenderer;
    this.count = count;
  }
  Pinky$PoolingRenderContext$Holder.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Holder',
    interfaces: []
  };
  Pinky$PoolingRenderContext.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'PoolingRenderContext',
    interfaces: [RenderContext]
  };
  function Pinky$PixelsAdapter(buffer) {
    this.buffer_8be2vx$ = buffer;
    this.size_fczu5a$_0 = this.buffer_8be2vx$.colors.size;
  }
  Object.defineProperty(Pinky$PixelsAdapter.prototype, 'size', {
    get: function () {
      return this.size_fczu5a$_0;
    }
  });
  Pinky$PixelsAdapter.prototype.get_za3lpa$ = function (i) {
    return this.buffer_8be2vx$.colors.get_za3lpa$(i);
  };
  Pinky$PixelsAdapter.prototype.set_ibd5tj$ = function (i, color) {
    this.buffer_8be2vx$.colors.set_wxm5ur$(i, color);
  };
  Pinky$PixelsAdapter.prototype.set_tmuqsv$ = function (colors) {
    var tmp$;
    var b = this.size;
    tmp$ = Math_0.min(colors.length, b);
    for (var i = 0; i < tmp$; i++) {
      this.buffer_8be2vx$.colors.set_wxm5ur$(i, colors[i]);
    }
  };
  Pinky$PixelsAdapter.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'PixelsAdapter',
    interfaces: [Pixels]
  };
  function Pinky_init$lambda(this$Pinky) {
    return function (it) {
      return new MapperEndpoint(this$Pinky.storage_0);
    };
  }
  Pinky.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Pinky',
    interfaces: [Network$UdpListener]
  };
  function BrainId(uuid) {
    this.uuid = uuid;
  }
  BrainId.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'BrainId',
    interfaces: []
  };
  BrainId.prototype.component1 = function () {
    return this.uuid;
  };
  BrainId.prototype.copy_61zpoe$ = function (uuid) {
    return new BrainId(uuid === void 0 ? this.uuid : uuid);
  };
  BrainId.prototype.toString = function () {
    return 'BrainId(uuid=' + Kotlin.toString(this.uuid) + ')';
  };
  BrainId.prototype.hashCode = function () {
    var result = 0;
    result = result * 31 + Kotlin.hashCode(this.uuid) | 0;
    return result;
  };
  BrainId.prototype.equals = function (other) {
    return this === other || (other !== null && (typeof other === 'object' && (Object.getPrototypeOf(this) === Object.getPrototypeOf(other) && Kotlin.equals(this.uuid, other.uuid))));
  };
  function BrainInfo(address, brainId, surface, firmwareVersion, idfVersion, surfaceReceiver) {
    this.address = address;
    this.brainId = brainId;
    this.surface = surface;
    this.firmwareVersion = firmwareVersion;
    this.idfVersion = idfVersion;
    this.surfaceReceiver = surfaceReceiver;
  }
  BrainInfo.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'BrainInfo',
    interfaces: []
  };
  function PubSub() {
    PubSub$Companion_getInstance();
  }
  function PubSub$Companion() {
    PubSub$Companion_instance = this;
  }
  PubSub$Companion.prototype.listen_76wx40$ = function (httpServer) {
    return new PubSub$Server(httpServer);
  };
  PubSub$Companion.prototype.connect_4z1eem$ = function (networkLink, address, port) {
    return new PubSub$Client(networkLink, address, port);
  };
  PubSub$Companion.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'Companion',
    interfaces: []
  };
  var PubSub$Companion_instance = null;
  function PubSub$Companion_getInstance() {
    if (PubSub$Companion_instance === null) {
      new PubSub$Companion();
    }
    return PubSub$Companion_instance;
  }
  function PubSub$Origin() {
  }
  PubSub$Origin.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Origin',
    interfaces: []
  };
  function PubSub$Channel() {
  }
  PubSub$Channel.$metadata$ = {
    kind: Kind_INTERFACE,
    simpleName: 'Channel',
    interfaces: []
  };
  function PubSub$Topic(name, serializer) {
    this.name = name;
    this.serializer = serializer;
  }
  PubSub$Topic.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Topic',
    interfaces: []
  };
  PubSub$Topic.prototype.component1 = function () {
    return this.name;
  };
  PubSub$Topic.prototype.component2 = function () {
    return this.serializer;
  };
  PubSub$Topic.prototype.copy_polik9$ = function (name, serializer) {
    return new PubSub$Topic(name === void 0 ? this.name : name, serializer === void 0 ? this.serializer : serializer);
  };
  PubSub$Topic.prototype.toString = function () {
    return 'Topic(name=' + Kotlin.toString(this.name) + (', serializer=' + Kotlin.toString(this.serializer)) + ')';
  };
  PubSub$Topic.prototype.hashCode = function () {
    var result = 0;
    result = result * 31 + Kotlin.hashCode(this.name) | 0;
    result = result * 31 + Kotlin.hashCode(this.serializer) | 0;
    return result;
  };
  PubSub$Topic.prototype.equals = function (other) {
    return this === other || (other !== null && (typeof other === 'object' && (Object.getPrototypeOf(this) === Object.getPrototypeOf(other) && (Kotlin.equals(this.name, other.name) && Kotlin.equals(this.serializer, other.serializer)))));
  };
  function PubSub$Listener(origin_0) {
    this.origin_fg10in$_0 = origin_0;
  }
  PubSub$Listener.prototype.onUpdate_btyzc5$ = function (data, fromOrigin) {
    if (this.origin_fg10in$_0 !== fromOrigin) {
      this.onUpdate_61zpoe$(data);
    }
  };
  PubSub$Listener.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Listener',
    interfaces: []
  };
  function PubSub$TopicInfo(name, data) {
    if (data === void 0)
      data = null;
    this.name = name;
    this.data = data;
    this.listeners = ArrayList_init();
  }
  PubSub$TopicInfo.prototype.notify_btyzc5$ = function (jsonData, origin) {
    this.data = jsonData;
    var tmp$;
    tmp$ = this.listeners.iterator();
    while (tmp$.hasNext()) {
      var element = tmp$.next();
      element.onUpdate_btyzc5$(jsonData, origin);
    }
  };
  PubSub$TopicInfo.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'TopicInfo',
    interfaces: []
  };
  function PubSub$Connection(name, topics) {
    PubSub$Origin.call(this);
    this.name_qs3czq$_0 = name;
    this.topics_okivn7$_0 = topics;
    this.connection = null;
    this.toSend_p0j902$_0 = ArrayList_init();
  }
  PubSub$Connection.prototype.connected_67ozxy$ = function (tcpConnection) {
    this.debug_6bynea$_0('connection ' + this + ' established');
    this.connection = tcpConnection;
    var tmp$;
    tmp$ = this.toSend_p0j902$_0.iterator();
    while (tmp$.hasNext()) {
      var element = tmp$.next();
      tcpConnection.send_fqrh44$(element);
    }
    this.toSend_p0j902$_0.clear();
  };
  function PubSub$Connection$receive$ObjectLiteral(closure$topicName, this$Connection, origin_0) {
    this.closure$topicName = closure$topicName;
    this.this$Connection = this$Connection;
    PubSub$Listener.call(this, origin_0);
  }
  PubSub$Connection$receive$ObjectLiteral.prototype.onUpdate_61zpoe$ = function (data) {
    this.this$Connection.sendTopicUpdate_puj7f4$(this.closure$topicName, data);
  };
  PubSub$Connection$receive$ObjectLiteral.$metadata$ = {
    kind: Kind_CLASS,
    interfaces: [PubSub$Listener]
  };
  PubSub$Connection.prototype.receive_r00qii$ = function (tcpConnection, bytes) {
    var reader = new ByteArrayReader(bytes);
    var command = reader.readString();
    switch (command) {
      case 'sub':
        var topicName = reader.readString();
        var $receiver = this.topics_okivn7$_0;
        var tmp$;
        var value = $receiver.get_11rb$(topicName);
        if (value == null) {
          var answer = new PubSub$TopicInfo(topicName);
          $receiver.put_xwzc9p$(topicName, answer);
          tmp$ = answer;
        }
         else {
          tmp$ = value;
        }

        var topicInfo = tmp$;
        var listener = new PubSub$Connection$receive$ObjectLiteral(topicName, this, this);
        topicInfo.listeners.add_11rb$(listener);
        var topicData = topicInfo.data;
        if (topicData != null) {
          listener.onUpdate_61zpoe$(topicData);
        }

        break;
      case 'update':
        var topicName_0 = reader.readString();
        var data = reader.readString();
        var topicInfo_0 = this.topics_okivn7$_0.get_11rb$(topicName_0);
        topicInfo_0 != null ? (topicInfo_0.notify_btyzc5$(data, this), Unit) : null;
        break;
      default:throw IllegalArgumentException_init("huh? don't know what to do with " + command);
    }
  };
  PubSub$Connection.prototype.sendTopicUpdate_puj7f4$ = function (name, data) {
    this.debug_6bynea$_0('update ' + name + ' ' + data);
    var writer = new ByteArrayWriter();
    writer.writeString_61zpoe$('update');
    writer.writeString_61zpoe$(name);
    writer.writeString_61zpoe$(data);
    this.sendCommand_su7uv8$_0(writer.toBytes());
  };
  PubSub$Connection.prototype.sendTopicSub_61zpoe$ = function (topicName) {
    this.debug_6bynea$_0('sub ' + topicName);
    var writer = new ByteArrayWriter();
    writer.writeString_61zpoe$('sub');
    writer.writeString_61zpoe$(topicName);
    this.sendCommand_su7uv8$_0(writer.toBytes());
  };
  PubSub$Connection.prototype.reset_67ozxy$ = function (tcpConnection) {
    throw new NotImplementedError_init('An operation is not implemented: ' + 'PubSub.Connection.reset not implemented');
  };
  PubSub$Connection.prototype.sendCommand_su7uv8$_0 = function (bytes) {
    var tcpConnection = this.connection;
    if (tcpConnection == null) {
      this.toSend_p0j902$_0.add_11rb$(bytes);
    }
     else {
      tcpConnection.send_fqrh44$(bytes);
    }
  };
  PubSub$Connection.prototype.debug_6bynea$_0 = function (message) {
    var tmp$, tmp$_0;
    logger$Companion_getInstance().debug_61zpoe$('[PubSub ' + this.name_qs3czq$_0 + ' -> ' + ((tmp$_0 = (tmp$ = this.connection) != null ? tmp$.toAddress : null) != null ? tmp$_0 : '(deferred)').toString() + ']: ' + message);
  };
  PubSub$Connection.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Connection',
    interfaces: [Network$WebSocketListener, PubSub$Origin]
  };
  function PubSub$Endpoint() {
    this.serialModule = modules.EmptyModule;
    this.json = new Json(JsonConfiguration.Companion.Stable, this.serialModule);
  }
  PubSub$Endpoint.prototype.install_stpyu4$ = function (toInstall) {
    this.serialModule = plus(this.serialModule, toInstall);
    this.json = new Json(JsonConfiguration.Companion.Stable, this.serialModule);
  };
  PubSub$Endpoint.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Endpoint',
    interfaces: []
  };
  function PubSub$Server(httpServer) {
    PubSub$Endpoint.call(this);
    this.topics_0 = HashMap_init();
    httpServer.listenWebSocket_brdh44$('/sm/ws', PubSub$PubSub$Server_init$lambda(this));
  }
  function PubSub$Server$publish$ObjectLiteral(closure$topicInfo, this$Server, closure$topic, closure$publisher, closure$listener) {
    this.closure$topicInfo = closure$topicInfo;
    this.this$Server = this$Server;
    this.closure$topic = closure$topic;
    this.closure$publisher = closure$publisher;
    this.closure$listener = closure$listener;
  }
  PubSub$Server$publish$ObjectLiteral.prototype.onChange = function (t) {
    this.closure$topicInfo.notify_btyzc5$(this.this$Server.json.stringify_tf03ej$(this.closure$topic.serializer, t), this.closure$publisher);
  };
  PubSub$Server$publish$ObjectLiteral.prototype.replaceOnUpdate_qlkmfe$ = function (onUpdate) {
    this.closure$listener.onUpdate = onUpdate;
  };
  PubSub$Server$publish$ObjectLiteral.prototype.unsubscribe = function () {
  };
  PubSub$Server$publish$ObjectLiteral.$metadata$ = {
    kind: Kind_CLASS,
    interfaces: [PubSub$Channel]
  };
  PubSub$Server.prototype.publish_oiz02e$ = function (topic, data, onUpdate) {
    var publisher = new PubSub$Origin();
    var topicName = topic.name;
    var jsonData = this.json.stringify_tf03ej$(topic.serializer, data);
    var $receiver = this.topics_0;
    var tmp$;
    var value = $receiver.get_11rb$(topicName);
    if (value == null) {
      var answer = new PubSub$TopicInfo(topicName);
      $receiver.put_xwzc9p$(topicName, answer);
      tmp$ = answer;
    }
     else {
      tmp$ = value;
    }
    var topicInfo = tmp$;
    var listener = new PubSub$Server$PublisherListener(this, topic, publisher, onUpdate);
    topicInfo.listeners.add_11rb$(listener);
    topicInfo.notify_btyzc5$(jsonData, publisher);
    return new PubSub$Server$publish$ObjectLiteral(topicInfo, this, topic, publisher, listener);
  };
  PubSub$Server.prototype.getTopicInfo_y4putb$ = function (topicName) {
    return this.topics_0.get_11rb$(topicName);
  };
  function PubSub$Server$PublisherListener($outer, topic, origin, onUpdate) {
    this.$outer = $outer;
    PubSub$Listener.call(this, origin);
    this.topic_0 = topic;
    this.onUpdate = onUpdate;
  }
  PubSub$Server$PublisherListener.prototype.onUpdate_61zpoe$ = function (data) {
    this.onUpdate(this.$outer.json.parse_awif5v$(this.topic_0.serializer, data));
  };
  PubSub$Server$PublisherListener.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'PublisherListener',
    interfaces: [PubSub$Listener]
  };
  function PubSub$PubSub$Server_init$lambda(this$Server) {
    return function (incomingConnection) {
      return new PubSub$Connection('server at ' + incomingConnection.toAddress, this$Server.topics_0);
    };
  }
  PubSub$Server.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Server',
    interfaces: [PubSub$Endpoint]
  };
  function PubSub$Client(link, serverAddress, port) {
    PubSub$Endpoint.call(this);
    this.topics_0 = HashMap_init();
    this.server_0 = new PubSub$Connection('client at ' + link.myAddress, this.topics_0);
    link.connectWebSocket_t0j9bj$(serverAddress, port, '/sm/ws', this.server_0);
  }
  function PubSub$Client$subscribe$lambda$lambda$ObjectLiteral(this$Client, closure$topicName, origin_0) {
    this.this$Client = this$Client;
    this.closure$topicName = closure$topicName;
    PubSub$Listener.call(this, origin_0);
  }
  PubSub$Client$subscribe$lambda$lambda$ObjectLiteral.prototype.onUpdate_61zpoe$ = function (data) {
    this.this$Client.server_0.sendTopicUpdate_puj7f4$(this.closure$topicName, data);
  };
  PubSub$Client$subscribe$lambda$lambda$ObjectLiteral.$metadata$ = {
    kind: Kind_CLASS,
    interfaces: [PubSub$Listener]
  };
  function PubSub$Client$subscribe$ObjectLiteral(closure$onUpdate, this$Client, closure$topic, origin_0) {
    this.closure$onUpdate = closure$onUpdate;
    this.this$Client = this$Client;
    this.closure$topic = closure$topic;
    PubSub$Listener.call(this, origin_0);
  }
  PubSub$Client$subscribe$ObjectLiteral.prototype.onUpdate_61zpoe$ = function (data) {
    this.closure$onUpdate(this.this$Client.json.parse_awif5v$(this.closure$topic.serializer, data));
  };
  PubSub$Client$subscribe$ObjectLiteral.$metadata$ = {
    kind: Kind_CLASS,
    interfaces: [PubSub$Listener]
  };
  function PubSub$Client$subscribe$ObjectLiteral_0(this$Client, closure$topic, closure$topicInfo, closure$subscriber) {
    this.this$Client = this$Client;
    this.closure$topic = closure$topic;
    this.closure$topicInfo = closure$topicInfo;
    this.closure$subscriber = closure$subscriber;
  }
  PubSub$Client$subscribe$ObjectLiteral_0.prototype.onChange = function (t) {
    var jsonData = this.this$Client.json.stringify_tf03ej$(this.closure$topic.serializer, t);
    this.closure$topicInfo.notify_btyzc5$(jsonData, this.closure$subscriber);
  };
  PubSub$Client$subscribe$ObjectLiteral_0.prototype.replaceOnUpdate_qlkmfe$ = function (onUpdate) {
    throw new NotImplementedError_init('An operation is not implemented: ' + 'Client.channel.replaceOnUpdate not implemented');
  };
  PubSub$Client$subscribe$ObjectLiteral_0.prototype.unsubscribe = function () {
  };
  PubSub$Client$subscribe$ObjectLiteral_0.$metadata$ = {
    kind: Kind_CLASS,
    interfaces: [PubSub$Channel]
  };
  PubSub$Client.prototype.subscribe = function (topic, onUpdate) {
    var subscriber = new PubSub$Origin();
    var topicName = topic.name;
    var $receiver = this.topics_0;
    var tmp$;
    var value = $receiver.get_11rb$(topicName);
    if (value == null) {
      var $receiver_0 = new PubSub$TopicInfo(topicName);
      $receiver_0.listeners.add_11rb$(new PubSub$Client$subscribe$lambda$lambda$ObjectLiteral(this, topicName, this.server_0));
      this.server_0.sendTopicSub_61zpoe$(topicName);
      var answer = $receiver_0;
      $receiver.put_xwzc9p$(topicName, answer);
      tmp$ = answer;
    }
     else {
      tmp$ = value;
    }
    var topicInfo = tmp$;
    var listener = new PubSub$Client$subscribe$ObjectLiteral(onUpdate, this, topic, subscriber);
    topicInfo.listeners.add_11rb$(listener);
    var data = topicInfo.data;
    if (data != null) {
      listener.onUpdate_61zpoe$(data);
    }
    return new PubSub$Client$subscribe$ObjectLiteral_0(this, topic, topicInfo, subscriber);
  };
  PubSub$Client.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Client',
    interfaces: [PubSub$Endpoint]
  };
  PubSub.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'PubSub',
    interfaces: []
  };
  function ShaderId(name, ordinal, reader) {
    Enum.call(this);
    this.reader = reader;
    this.name$ = name;
    this.ordinal$ = ordinal;
  }
  function ShaderId_initFields() {
    ShaderId_initFields = function () {
    };
    ShaderId$SOLID_instance = new ShaderId('SOLID', 0, SolidShader$Companion_getInstance());
    ShaderId$PIXEL_instance = new ShaderId('PIXEL', 1, PixelShader$Companion_getInstance());
    ShaderId$SINE_WAVE_instance = new ShaderId('SINE_WAVE', 2, SineWaveShader$Companion_getInstance());
    ShaderId$COMPOSITOR_instance = new ShaderId('COMPOSITOR', 3, CompositorShader$Companion_getInstance());
    ShaderId$SPARKLE_instance = new ShaderId('SPARKLE', 4, SparkleShader$Companion_getInstance());
    ShaderId$SIMPLE_SPATIAL_instance = new ShaderId('SIMPLE_SPATIAL', 5, SimpleSpatialShader$Companion_getInstance());
    ShaderId$HEART_instance = new ShaderId('HEART', 6, HeartShader$Companion_getInstance());
    ShaderId$RANDOM_instance = new ShaderId('RANDOM', 7, RandomShader$Companion_getInstance());
    ShaderId$GLSL_SANDBOX_55301_instance = new ShaderId('GLSL_SANDBOX_55301', 8, GlslSandbox55301Shader$Companion_getInstance());
    ShaderId$GLSL_SHADER_instance = new ShaderId('GLSL_SHADER', 9, GlslShader$Companion_getInstance());
    ShaderId$Companion_getInstance();
  }
  var ShaderId$SOLID_instance;
  function ShaderId$SOLID_getInstance() {
    ShaderId_initFields();
    return ShaderId$SOLID_instance;
  }
  var ShaderId$PIXEL_instance;
  function ShaderId$PIXEL_getInstance() {
    ShaderId_initFields();
    return ShaderId$PIXEL_instance;
  }
  var ShaderId$SINE_WAVE_instance;
  function ShaderId$SINE_WAVE_getInstance() {
    ShaderId_initFields();
    return ShaderId$SINE_WAVE_instance;
  }
  var ShaderId$COMPOSITOR_instance;
  function ShaderId$COMPOSITOR_getInstance() {
    ShaderId_initFields();
    return ShaderId$COMPOSITOR_instance;
  }
  var ShaderId$SPARKLE_instance;
  function ShaderId$SPARKLE_getInstance() {
    ShaderId_initFields();
    return ShaderId$SPARKLE_instance;
  }
  var ShaderId$SIMPLE_SPATIAL_instance;
  function ShaderId$SIMPLE_SPATIAL_getInstance() {
    ShaderId_initFields();
    return ShaderId$SIMPLE_SPATIAL_instance;
  }
  var ShaderId$HEART_instance;
  function ShaderId$HEART_getInstance() {
    ShaderId_initFields();
    return ShaderId$HEART_instance;
  }
  var ShaderId$RANDOM_instance;
  function ShaderId$RANDOM_getInstance() {
    ShaderId_initFields();
    return ShaderId$RANDOM_instance;
  }
  var ShaderId$GLSL_SANDBOX_55301_instance;
  function ShaderId$GLSL_SANDBOX_55301_getInstance() {
    ShaderId_initFields();
    return ShaderId$GLSL_SANDBOX_55301_instance;
  }
  var ShaderId$GLSL_SHADER_instance;
  function ShaderId$GLSL_SHADER_getInstance() {
    ShaderId_initFields();
    return ShaderId$GLSL_SHADER_instance;
  }
  function ShaderId$Companion() {
    ShaderId$Companion_instance = this;
    this.values = ShaderId$values();
  }
  ShaderId$Companion.prototype.get_s8j3t7$ = function (i) {
    if (i > this.values.length || i < 0) {
      throw Kotlin.newThrowable('bad index for ShaderId: ' + i);
    }
    return this.values[i];
  };
  ShaderId$Companion.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'Companion',
    interfaces: []
  };
  var ShaderId$Companion_instance = null;
  function ShaderId$Companion_getInstance() {
    ShaderId_initFields();
    if (ShaderId$Companion_instance === null) {
      new ShaderId$Companion();
    }
    return ShaderId$Companion_instance;
  }
  ShaderId.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'ShaderId',
    interfaces: [Enum]
  };
  function ShaderId$values() {
    return [ShaderId$SOLID_getInstance(), ShaderId$PIXEL_getInstance(), ShaderId$SINE_WAVE_getInstance(), ShaderId$COMPOSITOR_getInstance(), ShaderId$SPARKLE_getInstance(), ShaderId$SIMPLE_SPATIAL_getInstance(), ShaderId$HEART_getInstance(), ShaderId$RANDOM_getInstance(), ShaderId$GLSL_SANDBOX_55301_getInstance(), ShaderId$GLSL_SHADER_getInstance()];
  }
  ShaderId.values = ShaderId$values;
  function ShaderId$valueOf(name) {
    switch (name) {
      case 'SOLID':
        return ShaderId$SOLID_getInstance();
      case 'PIXEL':
        return ShaderId$PIXEL_getInstance();
      case 'SINE_WAVE':
        return ShaderId$SINE_WAVE_getInstance();
      case 'COMPOSITOR':
        return ShaderId$COMPOSITOR_getInstance();
      case 'SPARKLE':
        return ShaderId$SPARKLE_getInstance();
      case 'SIMPLE_SPATIAL':
        return ShaderId$SIMPLE_SPATIAL_getInstance();
      case 'HEART':
        return ShaderId$HEART_getInstance();
      case 'RANDOM':
        return ShaderId$RANDOM_getInstance();
      case 'GLSL_SANDBOX_55301':
        return ShaderId$GLSL_SANDBOX_55301_getInstance();
      case 'GLSL_SHADER':
        return ShaderId$GLSL_SHADER_getInstance();
      default:throwISE('No enum constant baaahs.ShaderId.' + name);
    }
  }
  ShaderId.valueOf_61zpoe$ = ShaderId$valueOf;
  function ShaderReader() {
  }
  ShaderReader.$metadata$ = {
    kind: Kind_INTERFACE,
    simpleName: 'ShaderReader',
    interfaces: []
  };
  function RenderContext() {
  }
  RenderContext.$metadata$ = {
    kind: Kind_INTERFACE,
    simpleName: 'RenderContext',
    interfaces: []
  };
  function Shader(id) {
    Shader$Companion_getInstance();
    this.id = id;
    this.descriptorBytes_lr4403$_0 = lazy(Shader$descriptorBytes$lambda(this));
  }
  Shader.prototype.createRenderer_omlfoo$ = function (surface, renderContext) {
    return this.createRenderer_ppt8xj$(surface);
  };
  Object.defineProperty(Shader.prototype, 'descriptorBytes', {
    get: function () {
      return this.descriptorBytes_lr4403$_0.value;
    }
  });
  Shader.prototype.serialize_3kjoo0$ = function (writer) {
    writer.writeByte_s8j3t7$(toByte(this.id.ordinal));
    this.serializeConfig_3kjoo0$(writer);
  };
  Shader.prototype.serializeConfig_3kjoo0$ = function (writer) {
  };
  Shader.prototype.toBytes_zbs1bl$_0 = function () {
    var writer = new ByteArrayWriter();
    this.serialize_3kjoo0$(writer);
    return writer.toBytes();
  };
  function Shader$Companion() {
    Shader$Companion_instance = this;
  }
  Shader$Companion.prototype.parse_100t80$ = function (reader) {
    var shaderTypeI = reader.readByte();
    var shaderType = ShaderId$Companion_getInstance().get_s8j3t7$(shaderTypeI);
    return shaderType.reader.parse_100t80$(reader);
  };
  Shader$Companion.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'Companion',
    interfaces: []
  };
  var Shader$Companion_instance = null;
  function Shader$Companion_getInstance() {
    if (Shader$Companion_instance === null) {
      new Shader$Companion();
    }
    return Shader$Companion_instance;
  }
  function Shader$Buffer() {
  }
  Shader$Buffer.$metadata$ = {
    kind: Kind_INTERFACE,
    simpleName: 'Buffer',
    interfaces: []
  };
  function Shader$Renderer() {
  }
  Shader$Renderer.prototype.beginFrame_b23bvv$ = function (buffer, pixelCount) {
  };
  Shader$Renderer.prototype.endFrame = function () {
  };
  Shader$Renderer.$metadata$ = {
    kind: Kind_INTERFACE,
    simpleName: 'Renderer',
    interfaces: []
  };
  function Shader$descriptorBytes$lambda(this$Shader) {
    return function () {
      return this$Shader.toBytes_zbs1bl$_0();
    };
  }
  Shader.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Shader',
    interfaces: []
  };
  function PooledRenderer() {
  }
  PooledRenderer.$metadata$ = {
    kind: Kind_INTERFACE,
    simpleName: 'PooledRenderer',
    interfaces: []
  };
  function Pixels() {
  }
  Object.defineProperty(Pixels.prototype, 'indices', {
    get: function () {
      return new IntRange(0, this.size - 1 | 0);
    }
  });
  Pixels.prototype.finishedFrame = function () {
  };
  function Pixels$iterator$ObjectLiteral(this$Pixels) {
    this.this$Pixels = this$Pixels;
    this.i_0 = 0;
  }
  Pixels$iterator$ObjectLiteral.prototype.hasNext = function () {
    return this.i_0 < this.this$Pixels.size;
  };
  Pixels$iterator$ObjectLiteral.prototype.next = function () {
    var tmp$, tmp$_0;
    tmp$_0 = (tmp$ = this.i_0, this.i_0 = tmp$ + 1 | 0, tmp$);
    return this.this$Pixels.get_za3lpa$(tmp$_0);
  };
  Pixels$iterator$ObjectLiteral.$metadata$ = {
    kind: Kind_CLASS,
    interfaces: [Iterator]
  };
  Pixels.prototype.iterator = function () {
    return new Pixels$iterator$ObjectLiteral(this);
  };
  Pixels.$metadata$ = {
    kind: Kind_INTERFACE,
    simpleName: 'Pixels',
    interfaces: [Iterable]
  };
  function Model() {
    this.allSurfacesByName_x6q8q$_2ixtr1$_0 = lazy(Model$allSurfacesByName$lambda(this));
  }
  Object.defineProperty(Model.prototype, 'allSurfacesByName_x6q8q$_0', {
    get: function () {
      return this.allSurfacesByName_x6q8q$_2ixtr1$_0.value;
    }
  });
  Model.prototype.findModelSurface_61zpoe$ = function (name) {
    var tmp$;
    tmp$ = this.allSurfacesByName_x6q8q$_0.get_11rb$(name);
    if (tmp$ == null) {
      throw RuntimeException_init('no such model surface ' + name);
    }
    return tmp$;
  };
  function Model$Surface() {
  }
  Model$Surface.$metadata$ = {
    kind: Kind_INTERFACE,
    simpleName: 'Surface',
    interfaces: []
  };
  function Model$allSurfacesByName$lambda(this$Model) {
    return function () {
      var $receiver = this$Model.allSurfaces;
      var capacity = coerceAtLeast(mapCapacity(collectionSizeOrDefault($receiver, 10)), 16);
      var destination = LinkedHashMap_init_0(capacity);
      var tmp$;
      tmp$ = $receiver.iterator();
      while (tmp$.hasNext()) {
        var element = tmp$.next();
        destination.put_xwzc9p$(element.name, element);
      }
      return destination;
    };
  }
  Model.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Model',
    interfaces: []
  };
  function SheepModel() {
    Model.call(this);
    this.vertices_mqvov9$_0 = this.vertices_mqvov9$_0;
    this.panels_kixrwx$_0 = this.panels_kixrwx$_0;
    this.eyes_j3l09w$_0 = this.eyes_j3l09w$_0;
    this.panelNeighbors_z1po1r$_0 = this.panelNeighbors_z1po1r$_0;
  }
  Object.defineProperty(SheepModel.prototype, 'vertices', {
    get: function () {
      if (this.vertices_mqvov9$_0 == null)
        return throwUPAE('vertices');
      return this.vertices_mqvov9$_0;
    },
    set: function (vertices) {
      this.vertices_mqvov9$_0 = vertices;
    }
  });
  Object.defineProperty(SheepModel.prototype, 'panels', {
    get: function () {
      if (this.panels_kixrwx$_0 == null)
        return throwUPAE('panels');
      return this.panels_kixrwx$_0;
    },
    set: function (panels) {
      this.panels_kixrwx$_0 = panels;
    }
  });
  Object.defineProperty(SheepModel.prototype, 'eyes', {
    get: function () {
      if (this.eyes_j3l09w$_0 == null)
        return throwUPAE('eyes');
      return this.eyes_j3l09w$_0;
    },
    set: function (eyes) {
      this.eyes_j3l09w$_0 = eyes;
    }
  });
  Object.defineProperty(SheepModel.prototype, 'allPanels', {
    get: function () {
      return this.panels;
    }
  });
  Object.defineProperty(SheepModel.prototype, 'partySide', {
    get: function () {
      var $receiver = this.panels;
      var destination = ArrayList_init();
      var tmp$;
      tmp$ = $receiver.iterator();
      while (tmp$.hasNext()) {
        var element = tmp$.next();
        if (Regex_init('P$').matches_6bul2c$(element.name))
          destination.add_11rb$(element);
      }
      return destination;
    }
  });
  Object.defineProperty(SheepModel.prototype, 'movingHeads', {
    get: function () {
      return this.eyes;
    }
  });
  Object.defineProperty(SheepModel.prototype, 'allSurfaces', {
    get: function () {
      return this.allPanels;
    }
  });
  Object.defineProperty(SheepModel.prototype, 'panelNeighbors', {
    get: function () {
      if (this.panelNeighbors_z1po1r$_0 == null)
        return throwUPAE('panelNeighbors');
      return this.panelNeighbors_z1po1r$_0;
    },
    set: function (panelNeighbors) {
      this.panelNeighbors_z1po1r$_0 = panelNeighbors;
    }
  });
  function SheepModel$load$neighborsOf(closure$edgesByPanel, closure$panelsByEdge) {
    return function (panel) {
      var tmp$, tmp$_0, tmp$_1;
      var tmp$_2;
      if ((tmp$ = closure$edgesByPanel.get_11rb$(panel)) != null) {
        var destination = ArrayList_init();
        var tmp$_3;
        tmp$_3 = tmp$.iterator();
        while (tmp$_3.hasNext()) {
          var element = tmp$_3.next();
          var tmp$_4, tmp$_5;
          var list = (tmp$_5 = (tmp$_4 = closure$panelsByEdge.get_11rb$(element)) != null ? toList_0(tmp$_4) : null) != null ? tmp$_5 : emptyList();
          addAll(destination, list);
        }
        tmp$_2 = destination;
      }
       else
        tmp$_2 = null;
      var tmp$_6;
      if ((tmp$_0 = tmp$_2) != null) {
        var destination_0 = ArrayList_init();
        var tmp$_7;
        tmp$_7 = tmp$_0.iterator();
        while (tmp$_7.hasNext()) {
          var element_0 = tmp$_7.next();
          if (!(element_0 != null ? element_0.equals(panel) : null))
            destination_0.add_11rb$(element_0);
        }
        tmp$_6 = destination_0;
      }
       else
        tmp$_6 = null;
      return (tmp$_1 = tmp$_6) != null ? tmp$_1 : emptyList();
    };
  }
  SheepModel.prototype.load = function () {
    var vertices = ArrayList_init();
    var panels = ArrayList_init();
    var currentPanel = {v: new SheepModel$Panel('initial')};
    var panelsByEdge = LinkedHashMap_init();
    var edgesByPanel = LinkedHashMap_init();
    var $receiver = split(getResource('baaahs-model.obj'), ['\n']);
    var destination = ArrayList_init_1(collectionSizeOrDefault($receiver, 10));
    var tmp$;
    tmp$ = $receiver.iterator();
    while (tmp$.hasNext()) {
      var item = tmp$.next();
      var tmp$_0;
      destination.add_11rb$(trim(Kotlin.isCharSequence(tmp$_0 = item) ? tmp$_0 : throwCCE()).toString());
    }
    var tmp$_1;
    tmp$_1 = destination.iterator();
    while (tmp$_1.hasNext()) {
      var element = tmp$_1.next();
      var tmp$_2;
      var parts = split(element, [' ']);
      var args = parts.subList_vux9f0$(1, parts.size);
      switch (parts.get_za3lpa$(0)) {
        case 'v':
          if (args.size !== 3)
            throw Exception_init('invalid vertex line: ' + element);
          var destination_0 = ArrayList_init_1(collectionSizeOrDefault(args, 10));
          var tmp$_3;
          tmp$_3 = args.iterator();
          while (tmp$_3.hasNext()) {
            var item_0 = tmp$_3.next();
            destination_0.add_11rb$(toDouble(item_0));
          }

          var coords = destination_0;
          vertices.add_11rb$(new SheepModel$Point(coords.get_za3lpa$(0), coords.get_za3lpa$(1), coords.get_za3lpa$(2)));
          break;
        case 'o':
          var name = joinToString(args, ' ');
          currentPanel.v = new SheepModel$Panel(name);
          panels.add_11rb$(currentPanel.v);
          break;
        case 'f':
          var destination_1 = ArrayList_init_1(collectionSizeOrDefault(args, 10));
          var tmp$_4;
          tmp$_4 = args.iterator();
          while (tmp$_4.hasNext()) {
            var item_1 = tmp$_4.next();
            destination_1.add_11rb$(toInt_0(item_1) - 1 | 0);
          }

          var verts = destination_1;
          currentPanel.v.faces.faces.add_11rb$(new SheepModel$Face(verts));
          break;
        case 'l':
          var destination_2 = ArrayList_init_1(collectionSizeOrDefault(args, 10));
          var tmp$_5;
          tmp$_5 = args.iterator();
          while (tmp$_5.hasNext()) {
            var item_2 = tmp$_5.next();
            destination_2.add_11rb$(toInt_0(item_2) - 1 | 0);
          }

          var verts_0 = destination_2;
          var points = ArrayList_init();
          tmp$_2 = verts_0.iterator();
          while (tmp$_2.hasNext()) {
            var vi = tmp$_2.next();
            var v = vertices.get_za3lpa$(vi);
            points.add_11rb$(v);
          }

          var sortedVerts = sorted(verts_0);
          var tmp$_6;
          var value = panelsByEdge.get_11rb$(sortedVerts);
          if (value == null) {
            var answer = ArrayList_init();
            panelsByEdge.put_xwzc9p$(sortedVerts, answer);
            tmp$_6 = answer;
          }
           else {
            tmp$_6 = value;
          }

          tmp$_6.add_11rb$(currentPanel.v);
          var key = currentPanel.v;
          var tmp$_7;
          var value_0 = edgesByPanel.get_11rb$(key);
          if (value_0 == null) {
            var answer_0 = ArrayList_init();
            edgesByPanel.put_xwzc9p$(key, answer_0);
            tmp$_7 = answer_0;
          }
           else {
            tmp$_7 = value_0;
          }

          tmp$_7.add_11rb$(sortedVerts);
          currentPanel.v.lines.add_11rb$(new SheepModel$Line(points));
          break;
      }
    }
    println('Sheep model has ' + panels.size + ' panels (and ' + vertices.size + ' vertices)!');
    this.vertices = vertices;
    this.panels = panels;
    var neighborsOf = SheepModel$load$neighborsOf(edgesByPanel, panelsByEdge);
    var $receiver_0 = this.allPanels;
    var result = LinkedHashMap_init_0(coerceAtLeast(mapCapacity(collectionSizeOrDefault($receiver_0, 10)), 16));
    var tmp$_8;
    tmp$_8 = $receiver_0.iterator();
    while (tmp$_8.hasNext()) {
      var element_0 = tmp$_8.next();
      result.put_xwzc9p$(element_0, neighborsOf(element_0));
    }
    this.panelNeighbors = result;
    this.eyes = arrayListOf([new MovingHead('leftEye', new SheepModel$Point(-163.738, 204.361, 439.302)), new MovingHead('rightEye', new SheepModel$Point(-103.738, 204.361, 439.302))]);
  };
  SheepModel.prototype.neighborsOf_jfju1k$ = function (panel) {
    var tmp$;
    return (tmp$ = this.panelNeighbors.get_11rb$(panel)) != null ? tmp$ : emptyList();
  };
  function SheepModel$Point(x, y, z) {
    SheepModel$Point$Companion_getInstance();
    this.x = x;
    this.y = y;
    this.z = z;
  }
  function SheepModel$Point$Companion() {
    SheepModel$Point$Companion_instance = this;
  }
  SheepModel$Point$Companion.prototype.serializer = function () {
    return SheepModel$Point$$serializer_getInstance();
  };
  SheepModel$Point$Companion.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'Companion',
    interfaces: []
  };
  var SheepModel$Point$Companion_instance = null;
  function SheepModel$Point$Companion_getInstance() {
    if (SheepModel$Point$Companion_instance === null) {
      new SheepModel$Point$Companion();
    }
    return SheepModel$Point$Companion_instance;
  }
  function SheepModel$Point$$serializer() {
    this.descriptor_6sorev$_0 = new SerialClassDescImpl('baaahs.SheepModel.Point', this);
    this.descriptor.addElement_ivxn3r$('x', false);
    this.descriptor.addElement_ivxn3r$('y', false);
    this.descriptor.addElement_ivxn3r$('z', false);
    SheepModel$Point$$serializer_instance = this;
  }
  Object.defineProperty(SheepModel$Point$$serializer.prototype, 'descriptor', {
    get: function () {
      return this.descriptor_6sorev$_0;
    }
  });
  SheepModel$Point$$serializer.prototype.serialize_awe97i$ = function (encoder, obj) {
    var output = encoder.beginStructure_r0sa6z$(this.descriptor, []);
    output.encodeFloatElement_t7qhdx$(this.descriptor, 0, obj.x);
    output.encodeFloatElement_t7qhdx$(this.descriptor, 1, obj.y);
    output.encodeFloatElement_t7qhdx$(this.descriptor, 2, obj.z);
    output.endStructure_qatsm0$(this.descriptor);
  };
  SheepModel$Point$$serializer.prototype.deserialize_nts5qn$ = function (decoder) {
    var index, readAll = false;
    var bitMask0 = 0;
    var local0
    , local1
    , local2;
    var input = decoder.beginStructure_r0sa6z$(this.descriptor, []);
    loopLabel: while (true) {
      index = input.decodeElementIndex_qatsm0$(this.descriptor);
      switch (index) {
        case -2:
          readAll = true;
        case 0:
          local0 = input.decodeFloatElement_3zr2iy$(this.descriptor, 0);
          bitMask0 |= 1;
          if (!readAll)
            break;
        case 1:
          local1 = input.decodeFloatElement_3zr2iy$(this.descriptor, 1);
          bitMask0 |= 2;
          if (!readAll)
            break;
        case 2:
          local2 = input.decodeFloatElement_3zr2iy$(this.descriptor, 2);
          bitMask0 |= 4;
          if (!readAll)
            break;
        case -1:
          break loopLabel;
        default:throw new UnknownFieldException(index);
      }
    }
    input.endStructure_qatsm0$(this.descriptor);
    return SheepModel$SheepModel$Point_init(bitMask0, local0, local1, local2, null);
  };
  SheepModel$Point$$serializer.prototype.childSerializers = function () {
    return [internal.FloatSerializer, internal.FloatSerializer, internal.FloatSerializer];
  };
  SheepModel$Point$$serializer.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: '$serializer',
    interfaces: [GeneratedSerializer]
  };
  var SheepModel$Point$$serializer_instance = null;
  function SheepModel$Point$$serializer_getInstance() {
    if (SheepModel$Point$$serializer_instance === null) {
      new SheepModel$Point$$serializer();
    }
    return SheepModel$Point$$serializer_instance;
  }
  function SheepModel$SheepModel$Point_init(seen1, x, y, z, serializationConstructorMarker) {
    var $this = serializationConstructorMarker || Object.create(SheepModel$Point.prototype);
    if ((seen1 & 1) === 0)
      throw new MissingFieldException('x');
    else
      $this.x = x;
    if ((seen1 & 2) === 0)
      throw new MissingFieldException('y');
    else
      $this.y = y;
    if ((seen1 & 4) === 0)
      throw new MissingFieldException('z');
    else
      $this.z = z;
    return $this;
  }
  SheepModel$Point.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Point',
    interfaces: []
  };
  SheepModel$Point.prototype.component1 = function () {
    return this.x;
  };
  SheepModel$Point.prototype.component2 = function () {
    return this.y;
  };
  SheepModel$Point.prototype.component3 = function () {
    return this.z;
  };
  SheepModel$Point.prototype.copy_y2kzbl$ = function (x, y, z) {
    return new SheepModel$Point(x === void 0 ? this.x : x, y === void 0 ? this.y : y, z === void 0 ? this.z : z);
  };
  SheepModel$Point.prototype.toString = function () {
    return 'Point(x=' + Kotlin.toString(this.x) + (', y=' + Kotlin.toString(this.y)) + (', z=' + Kotlin.toString(this.z)) + ')';
  };
  SheepModel$Point.prototype.hashCode = function () {
    var result = 0;
    result = result * 31 + Kotlin.hashCode(this.x) | 0;
    result = result * 31 + Kotlin.hashCode(this.y) | 0;
    result = result * 31 + Kotlin.hashCode(this.z) | 0;
    return result;
  };
  SheepModel$Point.prototype.equals = function (other) {
    return this === other || (other !== null && (typeof other === 'object' && (Object.getPrototypeOf(this) === Object.getPrototypeOf(other) && (Kotlin.equals(this.x, other.x) && Kotlin.equals(this.y, other.y) && Kotlin.equals(this.z, other.z)))));
  };
  function SheepModel$Line(points) {
    this.points = points;
  }
  SheepModel$Line.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Line',
    interfaces: []
  };
  SheepModel$Line.prototype.component1 = function () {
    return this.points;
  };
  SheepModel$Line.prototype.copy_5otmf7$ = function (points) {
    return new SheepModel$Line(points === void 0 ? this.points : points);
  };
  SheepModel$Line.prototype.toString = function () {
    return 'Line(points=' + Kotlin.toString(this.points) + ')';
  };
  SheepModel$Line.prototype.hashCode = function () {
    var result = 0;
    result = result * 31 + Kotlin.hashCode(this.points) | 0;
    return result;
  };
  SheepModel$Line.prototype.equals = function (other) {
    return this === other || (other !== null && (typeof other === 'object' && (Object.getPrototypeOf(this) === Object.getPrototypeOf(other) && Kotlin.equals(this.points, other.points))));
  };
  function SheepModel$Face(vertexIds) {
    this.vertexIds = vertexIds;
  }
  SheepModel$Face.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Face',
    interfaces: []
  };
  function SheepModel$Faces() {
    this.vertices = ArrayList_init();
    this.faces = ArrayList_init();
  }
  SheepModel$Faces.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Faces',
    interfaces: []
  };
  function SheepModel$Panel(name) {
    this.name_lqwv93$_0 = name;
    this.faces = new SheepModel$Faces();
    this.lines = ArrayList_init();
    this.description_fh6eyk$_0 = 'Panel ' + this.name;
  }
  Object.defineProperty(SheepModel$Panel.prototype, 'name', {
    get: function () {
      return this.name_lqwv93$_0;
    }
  });
  Object.defineProperty(SheepModel$Panel.prototype, 'description', {
    get: function () {
      return this.description_fh6eyk$_0;
    }
  });
  SheepModel$Panel.prototype.equals = function (other) {
    return Kotlin.isType(other, SheepModel$Panel) && equals(this.name, other.name);
  };
  SheepModel$Panel.prototype.hashCode = function () {
    return hashCode(this.name);
  };
  SheepModel$Panel.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Panel',
    interfaces: [Model$Surface]
  };
  SheepModel.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'SheepModel',
    interfaces: [Model]
  };
  function Show(name) {
    this.name = name;
  }
  function Show$Renderer() {
  }
  Show$Renderer.prototype.surfacesChanged_yroyvo$ = function (newSurfaces, removedSurfaces) {
    throw new Show$RestartShowException();
  };
  Show$Renderer.$metadata$ = {
    kind: Kind_INTERFACE,
    simpleName: 'Renderer',
    interfaces: []
  };
  function Show$RestartShowException() {
    Exception_init_0(this);
    this.name = 'Show$RestartShowException';
  }
  Show$RestartShowException.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'RestartShowException',
    interfaces: [Exception]
  };
  Show.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Show',
    interfaces: []
  };
  function ShowRunner(model, initialShow, gadgetManager, beatSource, dmxUniverse, movingHeadManager, clock) {
    this.model_0 = model;
    this.gadgetManager_0 = gadgetManager;
    this.beatSource_0 = beatSource;
    this.dmxUniverse_0 = dmxUniverse;
    this.movingHeadManager_0 = movingHeadManager;
    this.clock_0 = clock;
    this.nextShow = initialShow;
    this.currentShow_0 = null;
    this.currentShowRenderer_0 = null;
    this.changedSurfaces_0 = ArrayList_init();
    this.totalSurfaceReceivers_0 = 0;
    this.shaderBuffers_0 = HashMap_init();
    this.requestedGadgets_0 = LinkedHashMap_init();
    this.shadersLocked_0 = true;
    this.gadgetsLocked_0 = true;
    this.surfaceReceivers_0 = LinkedHashMap_init();
  }
  Object.defineProperty(ShowRunner.prototype, 'allSurfaces', {
    get: function () {
      return toList_0(this.surfaceReceivers_0.keys);
    }
  });
  Object.defineProperty(ShowRunner.prototype, 'allUnusedSurfaces', {
    get: function () {
      return minus(this.allSurfaces, this.shaderBuffers_0.keys);
    }
  });
  Object.defineProperty(ShowRunner.prototype, 'allMovingHeads', {
    get: function () {
      return this.model_0.movingHeads;
    }
  });
  Object.defineProperty(ShowRunner.prototype, 'currentBeat', {
    get: function () {
      return this.beatSource_0.getBeatData().beatWithinMeasure_rnw5ii$(this.clock_0);
    }
  });
  ShowRunner.prototype.getBeatSource = function () {
    return this.beatSource_0;
  };
  ShowRunner.prototype.recordShader_0 = function (surface, shaderBuffer) {
    var $receiver = this.shaderBuffers_0;
    var tmp$;
    var value = $receiver.get_11rb$(surface);
    if (value == null) {
      var answer = ArrayList_init();
      $receiver.put_xwzc9p$(surface, answer);
      tmp$ = answer;
    }
     else {
      tmp$ = value;
    }
    var buffersForSurface = tmp$;
    if (Kotlin.isType(shaderBuffer, CompositorShader$Buffer)) {
      if (!buffersForSurface.remove_11rb$(shaderBuffer.bufferA) || !buffersForSurface.remove_11rb$(shaderBuffer.bufferB)) {
        throw IllegalStateException_init('Composite of unknown shader buffers!');
      }
    }
    buffersForSurface.add_11rb$(shaderBuffer);
  };
  ShowRunner.prototype.getShaderBuffer_9rhubp$ = function (surface, shader) {
    if (this.shadersLocked_0)
      throw IllegalStateException_init("Shaders can't be obtained during #nextFrame()");
    var buffer = shader.createBuffer_ppt8xj$(surface);
    this.recordShader_0(surface, buffer);
    return buffer;
  };
  ShowRunner.prototype.getCompositorBuffer_cn6wln$ = function (surface, bufferA, bufferB, mode, fade) {
    if (mode === void 0)
      mode = CompositingMode$NORMAL_getInstance();
    if (fade === void 0)
      fade = 0.5;
    if (this.shadersLocked_0)
      throw IllegalStateException_init("Shaders can't be obtained during #nextFrame()");
    var $receiver = (new CompositorShader(bufferA.shader, bufferB.shader)).createBuffer_ytrflg$(bufferA, bufferB);
    $receiver.mode = mode;
    $receiver.fade = fade;
    this.recordShader_0(surface, $receiver);
    return $receiver;
  };
  ShowRunner.prototype.getDmxBuffer_0 = function (baseChannel, channelCount) {
    return this.dmxUniverse_0.writer_vux9f0$(baseChannel, channelCount);
  };
  function ShowRunner$getMovingHeadBuffer$lambda(closure$movingHead, closure$movingHeadBuffer) {
    return function (updated) {
      println('Moving head ' + closure$movingHead.name + ' moved to ' + updated.x + ' ' + updated.y);
      closure$movingHeadBuffer.pan = updated.x / 255.0;
      closure$movingHeadBuffer.tilt = updated.y / 255.0;
      return Unit;
    };
  }
  ShowRunner.prototype.getMovingHeadBuffer_d2e776$ = function (movingHead) {
    if (this.shadersLocked_0)
      throw IllegalStateException_init("Moving heads can't be obtained during #nextFrame()");
    var baseChannel = ensureNotNull(Config$Companion_getInstance().DMX_DEVICES.get_11rb$(movingHead.name));
    var movingHeadBuffer = new LixadaMiniMovingHead(this.getDmxBuffer_0(baseChannel, 16));
    this.movingHeadManager_0.listen_proz6e$(movingHead, ShowRunner$getMovingHeadBuffer$lambda(movingHead, movingHeadBuffer));
    return movingHeadBuffer;
  };
  ShowRunner.prototype.getGadget_vedre8$ = function (name, gadget) {
    if (this.gadgetsLocked_0)
      throw IllegalStateException_init("Gadgets can't be obtained during #nextFrame()");
    var oldValue = this.requestedGadgets_0.put_xwzc9p$(name, gadget);
    if (oldValue != null)
      throw IllegalStateException_init('Gadget names must be unique (' + name + ')');
    return gadget;
  };
  ShowRunner.prototype.surfacesChanged_ji9tfc$ = function (addedSurfaces, removedSurfaces) {
    this.changedSurfaces_0.add_11rb$(new ShowRunner$SurfacesChanges(ArrayList_init_0(addedSurfaces), ArrayList_init_0(removedSurfaces)));
  };
  ShowRunner.prototype.nextFrame = function () {
    var tmp$;
    if ((tmp$ = this.currentShowRenderer_0) != null) {
      tmp$.nextFrame();
      this.send();
    }
    this.housekeeping_0();
  };
  ShowRunner.prototype.housekeeping_0 = function () {
    var tmp$, tmp$_0, tmp$_1, tmp$_2, tmp$_3, tmp$_4;
    tmp$ = this.changedSurfaces_0.iterator();
    while (tmp$.hasNext()) {
      var tmp$_5 = tmp$.next();
      var added = tmp$_5.component1()
      , removed = tmp$_5.component2();
      println('ShowRunner surfaces changed! ' + added.size + ' added, ' + removed.size + ' removed');
      tmp$_0 = removed.iterator();
      while (tmp$_0.hasNext()) {
        var receiver = tmp$_0.next();
        this.removeReceiver_0(receiver);
      }
      tmp$_1 = added.iterator();
      while (tmp$_1.hasNext()) {
        var receiver_0 = tmp$_1.next();
        this.addReceiver_0(receiver_0);
      }
      if (this.nextShow == null) {
        this.shadersLocked_0 = false;
        try {
          if ((tmp$_2 = this.currentShowRenderer_0) != null) {
            var destination = ArrayList_init_1(collectionSizeOrDefault(added, 10));
            var tmp$_6;
            tmp$_6 = added.iterator();
            while (tmp$_6.hasNext()) {
              var item = tmp$_6.next();
              destination.add_11rb$(item.surface);
            }
            var destination_0 = ArrayList_init_1(collectionSizeOrDefault(removed, 10));
            var tmp$_7;
            tmp$_7 = removed.iterator();
            while (tmp$_7.hasNext()) {
              var item_0 = tmp$_7.next();
              destination_0.add_11rb$(item_0.surface);
            }
            tmp$_2.surfacesChanged_yroyvo$(destination, destination_0);
          }
          logger$Companion_getInstance().info_61zpoe$('Show ' + ensureNotNull(this.currentShow_0).name + ' updated; ' + (this.shaderBuffers_0.size.toString() + ' surfaces'));
        }
         catch (e) {
          if (Kotlin.isType(e, Show$RestartShowException)) {
            this.nextShow = (tmp$_3 = this.currentShow_0) != null ? tmp$_3 : this.nextShow;
          }
           else
            throw e;
        }
        this.shadersLocked_0 = true;
      }
    }
    this.changedSurfaces_0.clear();
    if (this.totalSurfaceReceivers_0 > 0) {
      if ((tmp$_4 = this.nextShow) != null) {
        this.createShowRenderer_0(tmp$_4);
        this.currentShow_0 = this.nextShow;
        this.nextShow = null;
      }
    }
  };
  function ShowRunner$createShowRenderer$lambda(closure$startingShow, this$ShowRunner) {
    return function () {
      this$ShowRunner.currentShowRenderer_0 = closure$startingShow.createRenderer_ccj26o$(this$ShowRunner.model_0, this$ShowRunner);
      return Unit;
    };
  }
  ShowRunner.prototype.createShowRenderer_0 = function (startingShow) {
    this.shaderBuffers_0.clear();
    var restartingSameShow = equals(this.nextShow, this.currentShow_0);
    var gadgetsState = restartingSameShow ? this.gadgetManager_0.getGadgetsState() : emptyMap();
    this.unlockShadersAndGadgets_0(ShowRunner$createShowRenderer$lambda(startingShow, this));
    logger$Companion_getInstance().info_61zpoe$('New show ' + startingShow.name + ' created; ' + (this.shaderBuffers_0.size.toString() + ' surfaces ') + ('and ' + this.requestedGadgets_0.size + ' gadgets'));
    this.gadgetManager_0.sync_7kvwdj$(toList_1(this.requestedGadgets_0), gadgetsState);
    this.requestedGadgets_0.clear();
  };
  ShowRunner.prototype.unlockShadersAndGadgets_0 = function (fn) {
    this.shadersLocked_0 = false;
    this.gadgetsLocked_0 = false;
    try {
      fn();
    }
    finally {
      this.shadersLocked_0 = true;
      this.gadgetsLocked_0 = true;
    }
  };
  ShowRunner.prototype.addReceiver_0 = function (receiver) {
    this.receiversFor_0(receiver.surface).add_11rb$(receiver);
    this.totalSurfaceReceivers_0 = this.totalSurfaceReceivers_0 + 1 | 0;
  };
  ShowRunner.prototype.removeReceiver_0 = function (receiver) {
    this.receiversFor_0(receiver.surface).remove_11rb$(receiver);
    this.shaderBuffers_0.remove_11rb$(receiver.surface);
    this.totalSurfaceReceivers_0 = this.totalSurfaceReceivers_0 - 1 | 0;
  };
  ShowRunner.prototype.receiversFor_0 = function (surface) {
    var $receiver = this.surfaceReceivers_0;
    var tmp$;
    var value = $receiver.get_11rb$(surface);
    if (value == null) {
      var answer = ArrayList_init();
      $receiver.put_xwzc9p$(surface, answer);
      tmp$ = answer;
    }
     else {
      tmp$ = value;
    }
    return tmp$;
  };
  ShowRunner.prototype.send = function () {
    var tmp$;
    tmp$ = this.shaderBuffers_0.entries.iterator();
    while (tmp$.hasNext()) {
      var element = tmp$.next();
      var surface = element.key;
      var shaderBuffers = element.value;
      if (shaderBuffers.size !== 1) {
        throw IllegalStateException_init('Too many shader buffers for ' + surface.describe() + ': ' + shaderBuffers);
      }
      var shaderBuffer = first(shaderBuffers);
      var tmp$_0;
      tmp$_0 = this.receiversFor_0(surface).iterator();
      while (tmp$_0.hasNext()) {
        var element_0 = tmp$_0.next();
        element_0.send_i8eued$(shaderBuffer);
      }
    }
    this.dmxUniverse_0.sendFrame();
  };
  ShowRunner.prototype.shutDown = function () {
    this.gadgetManager_0.clear();
  };
  function ShowRunner$SurfacesChanges(added, removed) {
    this.added = added;
    this.removed = removed;
  }
  ShowRunner$SurfacesChanges.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'SurfacesChanges',
    interfaces: []
  };
  ShowRunner$SurfacesChanges.prototype.component1 = function () {
    return this.added;
  };
  ShowRunner$SurfacesChanges.prototype.component2 = function () {
    return this.removed;
  };
  ShowRunner$SurfacesChanges.prototype.copy_ji9tfc$ = function (added, removed) {
    return new ShowRunner$SurfacesChanges(added === void 0 ? this.added : added, removed === void 0 ? this.removed : removed);
  };
  ShowRunner$SurfacesChanges.prototype.toString = function () {
    return 'SurfacesChanges(added=' + Kotlin.toString(this.added) + (', removed=' + Kotlin.toString(this.removed)) + ')';
  };
  ShowRunner$SurfacesChanges.prototype.hashCode = function () {
    var result = 0;
    result = result * 31 + Kotlin.hashCode(this.added) | 0;
    result = result * 31 + Kotlin.hashCode(this.removed) | 0;
    return result;
  };
  ShowRunner$SurfacesChanges.prototype.equals = function (other) {
    return this === other || (other !== null && (typeof other === 'object' && (Object.getPrototypeOf(this) === Object.getPrototypeOf(other) && (Kotlin.equals(this.added, other.added) && Kotlin.equals(this.removed, other.removed)))));
  };
  function ShowRunner$SurfaceReceiver(surface, sendFn) {
    this.surface = surface;
    this.sendFn_24k43f$_0 = sendFn;
  }
  ShowRunner$SurfaceReceiver.prototype.send_i8eued$ = function (shaderBuffer) {
    this.sendFn_24k43f$_0(shaderBuffer);
  };
  ShowRunner$SurfaceReceiver.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'SurfaceReceiver',
    interfaces: []
  };
  ShowRunner.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'ShowRunner',
    interfaces: []
  };
  function SparkleMotion() {
    SparkleMotion_instance = this;
    this.MAX_PIXEL_COUNT = 2048;
    this.DEFAULT_PIXEL_COUNT = 512;
    this.PIXEL_COUNT_UNKNOWN = -1;
  }
  SparkleMotion.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'SparkleMotion',
    interfaces: []
  };
  var SparkleMotion_instance = null;
  function SparkleMotion_getInstance() {
    if (SparkleMotion_instance === null) {
      new SparkleMotion();
    }
    return SparkleMotion_instance;
  }
  function Surface() {
  }
  Surface.$metadata$ = {
    kind: Kind_INTERFACE,
    simpleName: 'Surface',
    interfaces: []
  };
  function IdentifiedSurface(modelSurface, pixelCount, pixelVertices) {
    if (pixelVertices === void 0)
      pixelVertices = emptyList();
    this.modelSurface = modelSurface;
    this.pixelCount_o665an$_0 = pixelCount;
    this.pixelVertices = pixelVertices;
    this.name = this.modelSurface.name;
  }
  Object.defineProperty(IdentifiedSurface.prototype, 'pixelCount', {
    get: function () {
      return this.pixelCount_o665an$_0;
    }
  });
  IdentifiedSurface.prototype.describe = function () {
    return this.modelSurface.description;
  };
  IdentifiedSurface.prototype.equals = function (other) {
    var tmp$, tmp$_0;
    if (this === other)
      return true;
    if (other == null || !((tmp$ = Kotlin.getKClassFromExpression(this)) != null ? tmp$.equals(Kotlin.getKClassFromExpression(other)) : null))
      return false;
    Kotlin.isType(tmp$_0 = other, IdentifiedSurface) ? tmp$_0 : throwCCE();
    if (!equals(this.modelSurface, other.modelSurface))
      return false;
    return true;
  };
  IdentifiedSurface.prototype.hashCode = function () {
    return hashCode(this.modelSurface);
  };
  IdentifiedSurface.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'IdentifiedSurface',
    interfaces: [Surface]
  };
  function AnonymousSurface(brainId) {
    this.brainId = brainId;
    this.pixelCount_3v96cn$_0 = 2048;
  }
  Object.defineProperty(AnonymousSurface.prototype, 'pixelCount', {
    get: function () {
      return this.pixelCount_3v96cn$_0;
    }
  });
  AnonymousSurface.prototype.describe = function () {
    return 'Anonymous surface at ' + this.brainId;
  };
  AnonymousSurface.prototype.equals = function (other) {
    return Kotlin.isType(other, AnonymousSurface) && this.brainId.equals(other.brainId);
  };
  AnonymousSurface.prototype.hashCode = function () {
    return this.brainId.hashCode();
  };
  AnonymousSurface.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'AnonymousSurface',
    interfaces: [Surface]
  };
  function Topics() {
    Topics_instance = this;
    this.availableShows = new PubSub$Topic('availableShows', get_list(serializer(kotlin_js_internal_StringCompanionObject)));
    this.selectedShow = new PubSub$Topic('selectedShow', serializer(kotlin_js_internal_StringCompanionObject));
    this.activeGadgets = new PubSub$Topic('activeGadgets', get_list(GadgetData$Companion_getInstance().serializer()));
    this.movingHeads = new PubSub$Topic('movingHeads', get_list(MovingHead$Companion_getInstance().serializer()));
    this.movingHeadPresets = new PubSub$Topic('movingHeadPresets', get_map(to(serializer(kotlin_js_internal_StringCompanionObject), MovingHead$MovingHeadPosition$Companion_getInstance().serializer())));
  }
  Topics.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'Topics',
    interfaces: []
  };
  var Topics_instance = null;
  function Topics_getInstance() {
    if (Topics_instance === null) {
      new Topics();
    }
    return Topics_instance;
  }
  function LixadaMiniMovingHead(buffer) {
    Dmx$DeviceType.call(this, 9);
    this.buffer_5ntmif$_0 = buffer;
    this.dimmer = 34304 / 65535.0;
    this.buffer.set_h90ill$(LixadaMiniMovingHead$Channel$WHITE_getInstance(), toByte(255));
    this.buffer.set_h90ill$(LixadaMiniMovingHead$Channel$RED_getInstance(), toByte(255));
    this.buffer.set_h90ill$(LixadaMiniMovingHead$Channel$GREEN_getInstance(), toByte(255));
    this.buffer.set_h90ill$(LixadaMiniMovingHead$Channel$BLUE_getInstance(), toByte(255));
  }
  Object.defineProperty(LixadaMiniMovingHead.prototype, 'buffer', {
    get: function () {
      return this.buffer_5ntmif$_0;
    }
  });
  Object.defineProperty(LixadaMiniMovingHead.prototype, 'panChannel', {
    get: function () {
      return LixadaMiniMovingHead$Channel$PAN_getInstance();
    }
  });
  Object.defineProperty(LixadaMiniMovingHead.prototype, 'panFineChannel', {
    get: function () {
      return null;
    }
  });
  Object.defineProperty(LixadaMiniMovingHead.prototype, 'tiltChannel', {
    get: function () {
      return LixadaMiniMovingHead$Channel$TILT_getInstance();
    }
  });
  Object.defineProperty(LixadaMiniMovingHead.prototype, 'tiltFineChannel', {
    get: function () {
      return null;
    }
  });
  Object.defineProperty(LixadaMiniMovingHead.prototype, 'dimmerChannel', {
    get: function () {
      return LixadaMiniMovingHead$Channel$DIMMER_getInstance();
    }
  });
  Object.defineProperty(LixadaMiniMovingHead.prototype, 'color', {
    get: function () {
      return Color_init_2(this.buffer.get_6ui4v4$(LixadaMiniMovingHead$Channel$RED_getInstance()), this.buffer.get_6ui4v4$(LixadaMiniMovingHead$Channel$GREEN_getInstance()), this.buffer.get_6ui4v4$(LixadaMiniMovingHead$Channel$BLUE_getInstance()));
    },
    set: function (value) {
      this.buffer.set_h90ill$(LixadaMiniMovingHead$Channel$RED_getInstance(), toByte(value.redI));
      this.buffer.set_h90ill$(LixadaMiniMovingHead$Channel$GREEN_getInstance(), toByte(value.greenI));
      this.buffer.set_h90ill$(LixadaMiniMovingHead$Channel$BLUE_getInstance(), toByte(value.blueI));
    }
  });
  Object.defineProperty(LixadaMiniMovingHead.prototype, 'colorMode', {
    get: function () {
      return MovingHead$ColorMode$RGBW_getInstance();
    }
  });
  Object.defineProperty(LixadaMiniMovingHead.prototype, 'colorWheelColors', {
    get: function () {
      throw UnsupportedOperationException_init();
    }
  });
  function LixadaMiniMovingHead$Channel(name, ordinal) {
    Enum.call(this);
    this.name$ = name;
    this.ordinal$ = ordinal;
    this.offset_fr1z2f$_0 = this.ordinal;
  }
  function LixadaMiniMovingHead$Channel_initFields() {
    LixadaMiniMovingHead$Channel_initFields = function () {
    };
    LixadaMiniMovingHead$Channel$PAN_instance = new LixadaMiniMovingHead$Channel('PAN', 0);
    LixadaMiniMovingHead$Channel$TILT_instance = new LixadaMiniMovingHead$Channel('TILT', 1);
    LixadaMiniMovingHead$Channel$DIMMER_instance = new LixadaMiniMovingHead$Channel('DIMMER', 2);
    LixadaMiniMovingHead$Channel$RED_instance = new LixadaMiniMovingHead$Channel('RED', 3);
    LixadaMiniMovingHead$Channel$GREEN_instance = new LixadaMiniMovingHead$Channel('GREEN', 4);
    LixadaMiniMovingHead$Channel$BLUE_instance = new LixadaMiniMovingHead$Channel('BLUE', 5);
    LixadaMiniMovingHead$Channel$WHITE_instance = new LixadaMiniMovingHead$Channel('WHITE', 6);
    LixadaMiniMovingHead$Channel$PAN_TILT_SPEED_instance = new LixadaMiniMovingHead$Channel('PAN_TILT_SPEED', 7);
    LixadaMiniMovingHead$Channel$COLOR_RESET_instance = new LixadaMiniMovingHead$Channel('COLOR_RESET', 8);
  }
  var LixadaMiniMovingHead$Channel$PAN_instance;
  function LixadaMiniMovingHead$Channel$PAN_getInstance() {
    LixadaMiniMovingHead$Channel_initFields();
    return LixadaMiniMovingHead$Channel$PAN_instance;
  }
  var LixadaMiniMovingHead$Channel$TILT_instance;
  function LixadaMiniMovingHead$Channel$TILT_getInstance() {
    LixadaMiniMovingHead$Channel_initFields();
    return LixadaMiniMovingHead$Channel$TILT_instance;
  }
  var LixadaMiniMovingHead$Channel$DIMMER_instance;
  function LixadaMiniMovingHead$Channel$DIMMER_getInstance() {
    LixadaMiniMovingHead$Channel_initFields();
    return LixadaMiniMovingHead$Channel$DIMMER_instance;
  }
  var LixadaMiniMovingHead$Channel$RED_instance;
  function LixadaMiniMovingHead$Channel$RED_getInstance() {
    LixadaMiniMovingHead$Channel_initFields();
    return LixadaMiniMovingHead$Channel$RED_instance;
  }
  var LixadaMiniMovingHead$Channel$GREEN_instance;
  function LixadaMiniMovingHead$Channel$GREEN_getInstance() {
    LixadaMiniMovingHead$Channel_initFields();
    return LixadaMiniMovingHead$Channel$GREEN_instance;
  }
  var LixadaMiniMovingHead$Channel$BLUE_instance;
  function LixadaMiniMovingHead$Channel$BLUE_getInstance() {
    LixadaMiniMovingHead$Channel_initFields();
    return LixadaMiniMovingHead$Channel$BLUE_instance;
  }
  var LixadaMiniMovingHead$Channel$WHITE_instance;
  function LixadaMiniMovingHead$Channel$WHITE_getInstance() {
    LixadaMiniMovingHead$Channel_initFields();
    return LixadaMiniMovingHead$Channel$WHITE_instance;
  }
  var LixadaMiniMovingHead$Channel$PAN_TILT_SPEED_instance;
  function LixadaMiniMovingHead$Channel$PAN_TILT_SPEED_getInstance() {
    LixadaMiniMovingHead$Channel_initFields();
    return LixadaMiniMovingHead$Channel$PAN_TILT_SPEED_instance;
  }
  var LixadaMiniMovingHead$Channel$COLOR_RESET_instance;
  function LixadaMiniMovingHead$Channel$COLOR_RESET_getInstance() {
    LixadaMiniMovingHead$Channel_initFields();
    return LixadaMiniMovingHead$Channel$COLOR_RESET_instance;
  }
  Object.defineProperty(LixadaMiniMovingHead$Channel.prototype, 'offset', {
    get: function () {
      return this.offset_fr1z2f$_0;
    }
  });
  LixadaMiniMovingHead$Channel.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Channel',
    interfaces: [Dmx$Channel, Enum]
  };
  function LixadaMiniMovingHead$Channel$values() {
    return [LixadaMiniMovingHead$Channel$PAN_getInstance(), LixadaMiniMovingHead$Channel$TILT_getInstance(), LixadaMiniMovingHead$Channel$DIMMER_getInstance(), LixadaMiniMovingHead$Channel$RED_getInstance(), LixadaMiniMovingHead$Channel$GREEN_getInstance(), LixadaMiniMovingHead$Channel$BLUE_getInstance(), LixadaMiniMovingHead$Channel$WHITE_getInstance(), LixadaMiniMovingHead$Channel$PAN_TILT_SPEED_getInstance(), LixadaMiniMovingHead$Channel$COLOR_RESET_getInstance()];
  }
  LixadaMiniMovingHead$Channel.values = LixadaMiniMovingHead$Channel$values;
  function LixadaMiniMovingHead$Channel$valueOf(name) {
    switch (name) {
      case 'PAN':
        return LixadaMiniMovingHead$Channel$PAN_getInstance();
      case 'TILT':
        return LixadaMiniMovingHead$Channel$TILT_getInstance();
      case 'DIMMER':
        return LixadaMiniMovingHead$Channel$DIMMER_getInstance();
      case 'RED':
        return LixadaMiniMovingHead$Channel$RED_getInstance();
      case 'GREEN':
        return LixadaMiniMovingHead$Channel$GREEN_getInstance();
      case 'BLUE':
        return LixadaMiniMovingHead$Channel$BLUE_getInstance();
      case 'WHITE':
        return LixadaMiniMovingHead$Channel$WHITE_getInstance();
      case 'PAN_TILT_SPEED':
        return LixadaMiniMovingHead$Channel$PAN_TILT_SPEED_getInstance();
      case 'COLOR_RESET':
        return LixadaMiniMovingHead$Channel$COLOR_RESET_getInstance();
      default:throwISE('No enum constant baaahs.dmx.LixadaMiniMovingHead.Channel.' + name);
    }
  }
  LixadaMiniMovingHead$Channel.valueOf_61zpoe$ = LixadaMiniMovingHead$Channel$valueOf;
  LixadaMiniMovingHead.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'LixadaMiniMovingHead',
    interfaces: [MovingHead$Buffer, Dmx$DeviceType]
  };
  function Shenzarpy(buffer) {
    Shenzarpy$Companion_getInstance();
    Dmx$DeviceType.call(this, 16);
    this.buffer_ljatoh$_0 = buffer;
    this.colorWheelColors_m14kjt$_0 = toList_2(Shenzarpy$WheelColor$Companion_getInstance().values);
    this.dimmer = 1.0;
  }
  Object.defineProperty(Shenzarpy.prototype, 'buffer', {
    get: function () {
      return this.buffer_ljatoh$_0;
    }
  });
  Object.defineProperty(Shenzarpy.prototype, 'panChannel', {
    get: function () {
      return Shenzarpy$Channel$PAN_getInstance();
    }
  });
  Object.defineProperty(Shenzarpy.prototype, 'panFineChannel', {
    get: function () {
      return Shenzarpy$Channel$PAN_FINE_getInstance();
    }
  });
  Object.defineProperty(Shenzarpy.prototype, 'tiltChannel', {
    get: function () {
      return Shenzarpy$Channel$TILT_getInstance();
    }
  });
  Object.defineProperty(Shenzarpy.prototype, 'tiltFineChannel', {
    get: function () {
      return Shenzarpy$Channel$TILT_FINE_getInstance();
    }
  });
  Object.defineProperty(Shenzarpy.prototype, 'dimmerChannel', {
    get: function () {
      return Shenzarpy$Channel$DIMMER_getInstance();
    }
  });
  Object.defineProperty(Shenzarpy.prototype, 'color', {
    get: function () {
      return Shenzarpy$WheelColor$Companion_getInstance().values[this.colorWheel].color;
    },
    set: function (value) {
      this.colorWheel = this.closestColorFor_rny0jj$(value);
    }
  });
  Object.defineProperty(Shenzarpy.prototype, 'colorMode', {
    get: function () {
      return MovingHead$ColorMode$ColorWheel_getInstance();
    }
  });
  Object.defineProperty(Shenzarpy.prototype, 'colorWheelColors', {
    get: function () {
      return this.colorWheelColors_m14kjt$_0;
    }
  });
  function Shenzarpy$Companion() {
    Shenzarpy$Companion_instance = this;
    this.panRange = rangeTo(toRadians(0.0), toRadians(540.0));
    this.tiltRange = rangeTo(toRadians(-110.0), toRadians(110.0));
  }
  Shenzarpy$Companion.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'Companion',
    interfaces: []
  };
  var Shenzarpy$Companion_instance = null;
  function Shenzarpy$Companion_getInstance() {
    if (Shenzarpy$Companion_instance === null) {
      new Shenzarpy$Companion();
    }
    return Shenzarpy$Companion_instance;
  }
  function Shenzarpy$WheelColor(name, ordinal, color) {
    Enum.call(this);
    this.color = color;
    this.name$ = name;
    this.ordinal$ = ordinal;
  }
  function Shenzarpy$WheelColor_initFields() {
    Shenzarpy$WheelColor_initFields = function () {
    };
    Shenzarpy$WheelColor$RED_instance = new Shenzarpy$WheelColor('RED', 0, Color$Companion_getInstance().fromInt(12721698));
    Shenzarpy$WheelColor$ORANGE_instance = new Shenzarpy$WheelColor('ORANGE', 1, Color$Companion_getInstance().fromInt(15434294));
    Shenzarpy$WheelColor$AQUAMARINE_instance = new Shenzarpy$WheelColor('AQUAMARINE', 2, Color$Companion_getInstance().fromInt(8174724));
    Shenzarpy$WheelColor$DEEP_GREEN_instance = new Shenzarpy$WheelColor('DEEP_GREEN', 3, Color$Companion_getInstance().fromInt(1212719));
    Shenzarpy$WheelColor$LIGHT_GREEN_instance = new Shenzarpy$WheelColor('LIGHT_GREEN', 4, Color$Companion_getInstance().fromInt(10469695));
    Shenzarpy$WheelColor$LAVENDER_instance = new Shenzarpy$WheelColor('LAVENDER', 5, Color$Companion_getInstance().fromInt(9401515));
    Shenzarpy$WheelColor$PINK_instance = new Shenzarpy$WheelColor('PINK', 6, Color$Companion_getInstance().fromInt(15434114));
    Shenzarpy$WheelColor$YELLOW_instance = new Shenzarpy$WheelColor('YELLOW', 7, Color$Companion_getInstance().fromInt(16706356));
    Shenzarpy$WheelColor$MAGENTA_instance = new Shenzarpy$WheelColor('MAGENTA', 8, Color$Companion_getInstance().fromInt(14750594));
    Shenzarpy$WheelColor$CYAN_instance = new Shenzarpy$WheelColor('CYAN', 9, Color$Companion_getInstance().fromInt(1812456));
    Shenzarpy$WheelColor$CTO2_instance = new Shenzarpy$WheelColor('CTO2', 10, Color$Companion_getInstance().fromInt(16041553));
    Shenzarpy$WheelColor$CTO1_instance = new Shenzarpy$WheelColor('CTO1', 11, Color$Companion_getInstance().fromInt(16046218));
    Shenzarpy$WheelColor$CTB_instance = new Shenzarpy$WheelColor('CTB', 12, Color$Companion_getInstance().fromInt(9947064));
    Shenzarpy$WheelColor$DARK_BLUE_instance = new Shenzarpy$WheelColor('DARK_BLUE', 13, Color$Companion_getInstance().fromInt(545175));
    Shenzarpy$WheelColor$WHITE_instance = new Shenzarpy$WheelColor('WHITE', 14, Color$Companion_getInstance().fromInt(16777215));
    Shenzarpy$WheelColor$Companion_getInstance();
  }
  var Shenzarpy$WheelColor$RED_instance;
  function Shenzarpy$WheelColor$RED_getInstance() {
    Shenzarpy$WheelColor_initFields();
    return Shenzarpy$WheelColor$RED_instance;
  }
  var Shenzarpy$WheelColor$ORANGE_instance;
  function Shenzarpy$WheelColor$ORANGE_getInstance() {
    Shenzarpy$WheelColor_initFields();
    return Shenzarpy$WheelColor$ORANGE_instance;
  }
  var Shenzarpy$WheelColor$AQUAMARINE_instance;
  function Shenzarpy$WheelColor$AQUAMARINE_getInstance() {
    Shenzarpy$WheelColor_initFields();
    return Shenzarpy$WheelColor$AQUAMARINE_instance;
  }
  var Shenzarpy$WheelColor$DEEP_GREEN_instance;
  function Shenzarpy$WheelColor$DEEP_GREEN_getInstance() {
    Shenzarpy$WheelColor_initFields();
    return Shenzarpy$WheelColor$DEEP_GREEN_instance;
  }
  var Shenzarpy$WheelColor$LIGHT_GREEN_instance;
  function Shenzarpy$WheelColor$LIGHT_GREEN_getInstance() {
    Shenzarpy$WheelColor_initFields();
    return Shenzarpy$WheelColor$LIGHT_GREEN_instance;
  }
  var Shenzarpy$WheelColor$LAVENDER_instance;
  function Shenzarpy$WheelColor$LAVENDER_getInstance() {
    Shenzarpy$WheelColor_initFields();
    return Shenzarpy$WheelColor$LAVENDER_instance;
  }
  var Shenzarpy$WheelColor$PINK_instance;
  function Shenzarpy$WheelColor$PINK_getInstance() {
    Shenzarpy$WheelColor_initFields();
    return Shenzarpy$WheelColor$PINK_instance;
  }
  var Shenzarpy$WheelColor$YELLOW_instance;
  function Shenzarpy$WheelColor$YELLOW_getInstance() {
    Shenzarpy$WheelColor_initFields();
    return Shenzarpy$WheelColor$YELLOW_instance;
  }
  var Shenzarpy$WheelColor$MAGENTA_instance;
  function Shenzarpy$WheelColor$MAGENTA_getInstance() {
    Shenzarpy$WheelColor_initFields();
    return Shenzarpy$WheelColor$MAGENTA_instance;
  }
  var Shenzarpy$WheelColor$CYAN_instance;
  function Shenzarpy$WheelColor$CYAN_getInstance() {
    Shenzarpy$WheelColor_initFields();
    return Shenzarpy$WheelColor$CYAN_instance;
  }
  var Shenzarpy$WheelColor$CTO2_instance;
  function Shenzarpy$WheelColor$CTO2_getInstance() {
    Shenzarpy$WheelColor_initFields();
    return Shenzarpy$WheelColor$CTO2_instance;
  }
  var Shenzarpy$WheelColor$CTO1_instance;
  function Shenzarpy$WheelColor$CTO1_getInstance() {
    Shenzarpy$WheelColor_initFields();
    return Shenzarpy$WheelColor$CTO1_instance;
  }
  var Shenzarpy$WheelColor$CTB_instance;
  function Shenzarpy$WheelColor$CTB_getInstance() {
    Shenzarpy$WheelColor_initFields();
    return Shenzarpy$WheelColor$CTB_instance;
  }
  var Shenzarpy$WheelColor$DARK_BLUE_instance;
  function Shenzarpy$WheelColor$DARK_BLUE_getInstance() {
    Shenzarpy$WheelColor_initFields();
    return Shenzarpy$WheelColor$DARK_BLUE_instance;
  }
  var Shenzarpy$WheelColor$WHITE_instance;
  function Shenzarpy$WheelColor$WHITE_getInstance() {
    Shenzarpy$WheelColor_initFields();
    return Shenzarpy$WheelColor$WHITE_instance;
  }
  function Shenzarpy$WheelColor$Companion() {
    Shenzarpy$WheelColor$Companion_instance = this;
    this.values = Shenzarpy$WheelColor$values();
  }
  Shenzarpy$WheelColor$Companion.prototype.get_s8j3t7$ = function (i) {
    return this.values[i];
  };
  Shenzarpy$WheelColor$Companion.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'Companion',
    interfaces: []
  };
  var Shenzarpy$WheelColor$Companion_instance = null;
  function Shenzarpy$WheelColor$Companion_getInstance() {
    Shenzarpy$WheelColor_initFields();
    if (Shenzarpy$WheelColor$Companion_instance === null) {
      new Shenzarpy$WheelColor$Companion();
    }
    return Shenzarpy$WheelColor$Companion_instance;
  }
  Shenzarpy$WheelColor.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'WheelColor',
    interfaces: [Enum]
  };
  function Shenzarpy$WheelColor$values() {
    return [Shenzarpy$WheelColor$RED_getInstance(), Shenzarpy$WheelColor$ORANGE_getInstance(), Shenzarpy$WheelColor$AQUAMARINE_getInstance(), Shenzarpy$WheelColor$DEEP_GREEN_getInstance(), Shenzarpy$WheelColor$LIGHT_GREEN_getInstance(), Shenzarpy$WheelColor$LAVENDER_getInstance(), Shenzarpy$WheelColor$PINK_getInstance(), Shenzarpy$WheelColor$YELLOW_getInstance(), Shenzarpy$WheelColor$MAGENTA_getInstance(), Shenzarpy$WheelColor$CYAN_getInstance(), Shenzarpy$WheelColor$CTO2_getInstance(), Shenzarpy$WheelColor$CTO1_getInstance(), Shenzarpy$WheelColor$CTB_getInstance(), Shenzarpy$WheelColor$DARK_BLUE_getInstance(), Shenzarpy$WheelColor$WHITE_getInstance()];
  }
  Shenzarpy$WheelColor.values = Shenzarpy$WheelColor$values;
  function Shenzarpy$WheelColor$valueOf(name) {
    switch (name) {
      case 'RED':
        return Shenzarpy$WheelColor$RED_getInstance();
      case 'ORANGE':
        return Shenzarpy$WheelColor$ORANGE_getInstance();
      case 'AQUAMARINE':
        return Shenzarpy$WheelColor$AQUAMARINE_getInstance();
      case 'DEEP_GREEN':
        return Shenzarpy$WheelColor$DEEP_GREEN_getInstance();
      case 'LIGHT_GREEN':
        return Shenzarpy$WheelColor$LIGHT_GREEN_getInstance();
      case 'LAVENDER':
        return Shenzarpy$WheelColor$LAVENDER_getInstance();
      case 'PINK':
        return Shenzarpy$WheelColor$PINK_getInstance();
      case 'YELLOW':
        return Shenzarpy$WheelColor$YELLOW_getInstance();
      case 'MAGENTA':
        return Shenzarpy$WheelColor$MAGENTA_getInstance();
      case 'CYAN':
        return Shenzarpy$WheelColor$CYAN_getInstance();
      case 'CTO2':
        return Shenzarpy$WheelColor$CTO2_getInstance();
      case 'CTO1':
        return Shenzarpy$WheelColor$CTO1_getInstance();
      case 'CTB':
        return Shenzarpy$WheelColor$CTB_getInstance();
      case 'DARK_BLUE':
        return Shenzarpy$WheelColor$DARK_BLUE_getInstance();
      case 'WHITE':
        return Shenzarpy$WheelColor$WHITE_getInstance();
      default:throwISE('No enum constant baaahs.dmx.Shenzarpy.WheelColor.' + name);
    }
  }
  Shenzarpy$WheelColor.valueOf_61zpoe$ = Shenzarpy$WheelColor$valueOf;
  function Shenzarpy$Channel(name, ordinal) {
    Enum.call(this);
    this.name$ = name;
    this.ordinal$ = ordinal;
    this.offset_ydpwan$_0 = this.ordinal;
  }
  function Shenzarpy$Channel_initFields() {
    Shenzarpy$Channel_initFields = function () {
    };
    Shenzarpy$Channel$COLOR_WHEEL_instance = new Shenzarpy$Channel('COLOR_WHEEL', 0);
    Shenzarpy$Channel$SHUTTER_instance = new Shenzarpy$Channel('SHUTTER', 1);
    Shenzarpy$Channel$DIMMER_instance = new Shenzarpy$Channel('DIMMER', 2);
    Shenzarpy$Channel$GOBO_WHEEL_instance = new Shenzarpy$Channel('GOBO_WHEEL', 3);
    Shenzarpy$Channel$PRISM_instance = new Shenzarpy$Channel('PRISM', 4);
    Shenzarpy$Channel$PRISM_ROTATION_instance = new Shenzarpy$Channel('PRISM_ROTATION', 5);
    Shenzarpy$Channel$MACRO_instance = new Shenzarpy$Channel('MACRO', 6);
    Shenzarpy$Channel$FROST_instance = new Shenzarpy$Channel('FROST', 7);
    Shenzarpy$Channel$FOCUS_instance = new Shenzarpy$Channel('FOCUS', 8);
    Shenzarpy$Channel$PAN_instance = new Shenzarpy$Channel('PAN', 9);
    Shenzarpy$Channel$PAN_FINE_instance = new Shenzarpy$Channel('PAN_FINE', 10);
    Shenzarpy$Channel$TILT_instance = new Shenzarpy$Channel('TILT', 11);
    Shenzarpy$Channel$TILT_FINE_instance = new Shenzarpy$Channel('TILT_FINE', 12);
    Shenzarpy$Channel$PAN_TILT_SPEED_instance = new Shenzarpy$Channel('PAN_TILT_SPEED', 13);
    Shenzarpy$Channel$RESET_instance = new Shenzarpy$Channel('RESET', 14);
    Shenzarpy$Channel$LAMP_CONTROL_instance = new Shenzarpy$Channel('LAMP_CONTROL', 15);
    Shenzarpy$Channel$BLANK_instance = new Shenzarpy$Channel('BLANK', 16);
    Shenzarpy$Channel$COLOR_WHEEL_SPEED_instance = new Shenzarpy$Channel('COLOR_WHEEL_SPEED', 17);
    Shenzarpy$Channel$DIM_PRISM_ATOM_SPEED_instance = new Shenzarpy$Channel('DIM_PRISM_ATOM_SPEED', 18);
    Shenzarpy$Channel$GOBO_WHEEL_SPEED_instance = new Shenzarpy$Channel('GOBO_WHEEL_SPEED', 19);
    Shenzarpy$Channel$Companion_getInstance();
  }
  var Shenzarpy$Channel$COLOR_WHEEL_instance;
  function Shenzarpy$Channel$COLOR_WHEEL_getInstance() {
    Shenzarpy$Channel_initFields();
    return Shenzarpy$Channel$COLOR_WHEEL_instance;
  }
  var Shenzarpy$Channel$SHUTTER_instance;
  function Shenzarpy$Channel$SHUTTER_getInstance() {
    Shenzarpy$Channel_initFields();
    return Shenzarpy$Channel$SHUTTER_instance;
  }
  var Shenzarpy$Channel$DIMMER_instance;
  function Shenzarpy$Channel$DIMMER_getInstance() {
    Shenzarpy$Channel_initFields();
    return Shenzarpy$Channel$DIMMER_instance;
  }
  var Shenzarpy$Channel$GOBO_WHEEL_instance;
  function Shenzarpy$Channel$GOBO_WHEEL_getInstance() {
    Shenzarpy$Channel_initFields();
    return Shenzarpy$Channel$GOBO_WHEEL_instance;
  }
  var Shenzarpy$Channel$PRISM_instance;
  function Shenzarpy$Channel$PRISM_getInstance() {
    Shenzarpy$Channel_initFields();
    return Shenzarpy$Channel$PRISM_instance;
  }
  var Shenzarpy$Channel$PRISM_ROTATION_instance;
  function Shenzarpy$Channel$PRISM_ROTATION_getInstance() {
    Shenzarpy$Channel_initFields();
    return Shenzarpy$Channel$PRISM_ROTATION_instance;
  }
  var Shenzarpy$Channel$MACRO_instance;
  function Shenzarpy$Channel$MACRO_getInstance() {
    Shenzarpy$Channel_initFields();
    return Shenzarpy$Channel$MACRO_instance;
  }
  var Shenzarpy$Channel$FROST_instance;
  function Shenzarpy$Channel$FROST_getInstance() {
    Shenzarpy$Channel_initFields();
    return Shenzarpy$Channel$FROST_instance;
  }
  var Shenzarpy$Channel$FOCUS_instance;
  function Shenzarpy$Channel$FOCUS_getInstance() {
    Shenzarpy$Channel_initFields();
    return Shenzarpy$Channel$FOCUS_instance;
  }
  var Shenzarpy$Channel$PAN_instance;
  function Shenzarpy$Channel$PAN_getInstance() {
    Shenzarpy$Channel_initFields();
    return Shenzarpy$Channel$PAN_instance;
  }
  var Shenzarpy$Channel$PAN_FINE_instance;
  function Shenzarpy$Channel$PAN_FINE_getInstance() {
    Shenzarpy$Channel_initFields();
    return Shenzarpy$Channel$PAN_FINE_instance;
  }
  var Shenzarpy$Channel$TILT_instance;
  function Shenzarpy$Channel$TILT_getInstance() {
    Shenzarpy$Channel_initFields();
    return Shenzarpy$Channel$TILT_instance;
  }
  var Shenzarpy$Channel$TILT_FINE_instance;
  function Shenzarpy$Channel$TILT_FINE_getInstance() {
    Shenzarpy$Channel_initFields();
    return Shenzarpy$Channel$TILT_FINE_instance;
  }
  var Shenzarpy$Channel$PAN_TILT_SPEED_instance;
  function Shenzarpy$Channel$PAN_TILT_SPEED_getInstance() {
    Shenzarpy$Channel_initFields();
    return Shenzarpy$Channel$PAN_TILT_SPEED_instance;
  }
  var Shenzarpy$Channel$RESET_instance;
  function Shenzarpy$Channel$RESET_getInstance() {
    Shenzarpy$Channel_initFields();
    return Shenzarpy$Channel$RESET_instance;
  }
  var Shenzarpy$Channel$LAMP_CONTROL_instance;
  function Shenzarpy$Channel$LAMP_CONTROL_getInstance() {
    Shenzarpy$Channel_initFields();
    return Shenzarpy$Channel$LAMP_CONTROL_instance;
  }
  var Shenzarpy$Channel$BLANK_instance;
  function Shenzarpy$Channel$BLANK_getInstance() {
    Shenzarpy$Channel_initFields();
    return Shenzarpy$Channel$BLANK_instance;
  }
  var Shenzarpy$Channel$COLOR_WHEEL_SPEED_instance;
  function Shenzarpy$Channel$COLOR_WHEEL_SPEED_getInstance() {
    Shenzarpy$Channel_initFields();
    return Shenzarpy$Channel$COLOR_WHEEL_SPEED_instance;
  }
  var Shenzarpy$Channel$DIM_PRISM_ATOM_SPEED_instance;
  function Shenzarpy$Channel$DIM_PRISM_ATOM_SPEED_getInstance() {
    Shenzarpy$Channel_initFields();
    return Shenzarpy$Channel$DIM_PRISM_ATOM_SPEED_instance;
  }
  var Shenzarpy$Channel$GOBO_WHEEL_SPEED_instance;
  function Shenzarpy$Channel$GOBO_WHEEL_SPEED_getInstance() {
    Shenzarpy$Channel_initFields();
    return Shenzarpy$Channel$GOBO_WHEEL_SPEED_instance;
  }
  function Shenzarpy$Channel$Companion() {
    Shenzarpy$Channel$Companion_instance = this;
    this.values = Shenzarpy$Channel$values();
  }
  Shenzarpy$Channel$Companion.prototype.get_s8j3t7$ = function (i) {
    return this.values[i];
  };
  Shenzarpy$Channel$Companion.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'Companion',
    interfaces: []
  };
  var Shenzarpy$Channel$Companion_instance = null;
  function Shenzarpy$Channel$Companion_getInstance() {
    Shenzarpy$Channel_initFields();
    if (Shenzarpy$Channel$Companion_instance === null) {
      new Shenzarpy$Channel$Companion();
    }
    return Shenzarpy$Channel$Companion_instance;
  }
  Object.defineProperty(Shenzarpy$Channel.prototype, 'offset', {
    get: function () {
      return this.offset_ydpwan$_0;
    }
  });
  Shenzarpy$Channel.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Channel',
    interfaces: [Dmx$Channel, Enum]
  };
  function Shenzarpy$Channel$values() {
    return [Shenzarpy$Channel$COLOR_WHEEL_getInstance(), Shenzarpy$Channel$SHUTTER_getInstance(), Shenzarpy$Channel$DIMMER_getInstance(), Shenzarpy$Channel$GOBO_WHEEL_getInstance(), Shenzarpy$Channel$PRISM_getInstance(), Shenzarpy$Channel$PRISM_ROTATION_getInstance(), Shenzarpy$Channel$MACRO_getInstance(), Shenzarpy$Channel$FROST_getInstance(), Shenzarpy$Channel$FOCUS_getInstance(), Shenzarpy$Channel$PAN_getInstance(), Shenzarpy$Channel$PAN_FINE_getInstance(), Shenzarpy$Channel$TILT_getInstance(), Shenzarpy$Channel$TILT_FINE_getInstance(), Shenzarpy$Channel$PAN_TILT_SPEED_getInstance(), Shenzarpy$Channel$RESET_getInstance(), Shenzarpy$Channel$LAMP_CONTROL_getInstance(), Shenzarpy$Channel$BLANK_getInstance(), Shenzarpy$Channel$COLOR_WHEEL_SPEED_getInstance(), Shenzarpy$Channel$DIM_PRISM_ATOM_SPEED_getInstance(), Shenzarpy$Channel$GOBO_WHEEL_SPEED_getInstance()];
  }
  Shenzarpy$Channel.values = Shenzarpy$Channel$values;
  function Shenzarpy$Channel$valueOf(name) {
    switch (name) {
      case 'COLOR_WHEEL':
        return Shenzarpy$Channel$COLOR_WHEEL_getInstance();
      case 'SHUTTER':
        return Shenzarpy$Channel$SHUTTER_getInstance();
      case 'DIMMER':
        return Shenzarpy$Channel$DIMMER_getInstance();
      case 'GOBO_WHEEL':
        return Shenzarpy$Channel$GOBO_WHEEL_getInstance();
      case 'PRISM':
        return Shenzarpy$Channel$PRISM_getInstance();
      case 'PRISM_ROTATION':
        return Shenzarpy$Channel$PRISM_ROTATION_getInstance();
      case 'MACRO':
        return Shenzarpy$Channel$MACRO_getInstance();
      case 'FROST':
        return Shenzarpy$Channel$FROST_getInstance();
      case 'FOCUS':
        return Shenzarpy$Channel$FOCUS_getInstance();
      case 'PAN':
        return Shenzarpy$Channel$PAN_getInstance();
      case 'PAN_FINE':
        return Shenzarpy$Channel$PAN_FINE_getInstance();
      case 'TILT':
        return Shenzarpy$Channel$TILT_getInstance();
      case 'TILT_FINE':
        return Shenzarpy$Channel$TILT_FINE_getInstance();
      case 'PAN_TILT_SPEED':
        return Shenzarpy$Channel$PAN_TILT_SPEED_getInstance();
      case 'RESET':
        return Shenzarpy$Channel$RESET_getInstance();
      case 'LAMP_CONTROL':
        return Shenzarpy$Channel$LAMP_CONTROL_getInstance();
      case 'BLANK':
        return Shenzarpy$Channel$BLANK_getInstance();
      case 'COLOR_WHEEL_SPEED':
        return Shenzarpy$Channel$COLOR_WHEEL_SPEED_getInstance();
      case 'DIM_PRISM_ATOM_SPEED':
        return Shenzarpy$Channel$DIM_PRISM_ATOM_SPEED_getInstance();
      case 'GOBO_WHEEL_SPEED':
        return Shenzarpy$Channel$GOBO_WHEEL_SPEED_getInstance();
      default:throwISE('No enum constant baaahs.dmx.Shenzarpy.Channel.' + name);
    }
  }
  Shenzarpy$Channel.valueOf_61zpoe$ = Shenzarpy$Channel$valueOf;
  Object.defineProperty(Shenzarpy.prototype, 'colorWheel', {
    get: function () {
      return this.buffer.get_6ui4v4$(Shenzarpy$Channel$COLOR_WHEEL_getInstance());
    },
    set: function (value) {
      this.buffer.set_h90ill$(Shenzarpy$Channel$COLOR_WHEEL_getInstance(), value);
    }
  });
  Shenzarpy.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Shenzarpy',
    interfaces: [MovingHead$Buffer, Dmx$DeviceType]
  };
  function ColorPicker(name, initialValue) {
    ColorPicker$Companion_getInstance();
    if (initialValue === void 0)
      initialValue = Color$Companion_getInstance().WHITE;
    Gadget.call(this);
    this.name = name;
    this.initialValue = initialValue;
    this.color_u6ly2p$_0 = this.updatable_t7zvzq$('color', this.initialValue, Color$Companion_getInstance().serializer());
  }
  var ColorPicker$color_metadata = new PropertyMetadata('color');
  Object.defineProperty(ColorPicker.prototype, 'color', {
    get: function () {
      return this.color_u6ly2p$_0.getValue_lrcp0p$(this, ColorPicker$color_metadata);
    },
    set: function (color) {
      this.color_u6ly2p$_0.setValue_9rddgb$(this, ColorPicker$color_metadata, color);
    }
  });
  function ColorPicker$Companion() {
    ColorPicker$Companion_instance = this;
  }
  ColorPicker$Companion.prototype.serializer = function () {
    return ColorPicker$$serializer_getInstance();
  };
  ColorPicker$Companion.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'Companion',
    interfaces: []
  };
  var ColorPicker$Companion_instance = null;
  function ColorPicker$Companion_getInstance() {
    if (ColorPicker$Companion_instance === null) {
      new ColorPicker$Companion();
    }
    return ColorPicker$Companion_instance;
  }
  function ColorPicker$$serializer() {
    this.descriptor_epb33f$_0 = new SerialClassDescImpl('baaahs.gadgets.ColorPicker', this);
    this.descriptor.addElement_ivxn3r$('name', false);
    this.descriptor.addElement_ivxn3r$('initialValue', true);
    ColorPicker$$serializer_instance = this;
  }
  Object.defineProperty(ColorPicker$$serializer.prototype, 'descriptor', {
    get: function () {
      return this.descriptor_epb33f$_0;
    }
  });
  ColorPicker$$serializer.prototype.serialize_awe97i$ = function (encoder, obj) {
    var output = encoder.beginStructure_r0sa6z$(this.descriptor, []);
    output.encodeStringElement_bgm7zs$(this.descriptor, 0, obj.name);
    if (!equals(obj.initialValue, Color$Companion_getInstance().WHITE) || output.shouldEncodeElementDefault_3zr2iy$(this.descriptor, 1))
      output.encodeSerializableElement_blecud$(this.descriptor, 1, Color$Companion_getInstance(), obj.initialValue);
    output.endStructure_qatsm0$(this.descriptor);
  };
  ColorPicker$$serializer.prototype.deserialize_nts5qn$ = function (decoder) {
    var index, readAll = false;
    var bitMask0 = 0;
    var local0
    , local1;
    var input = decoder.beginStructure_r0sa6z$(this.descriptor, []);
    loopLabel: while (true) {
      index = input.decodeElementIndex_qatsm0$(this.descriptor);
      switch (index) {
        case -2:
          readAll = true;
        case 0:
          local0 = input.decodeStringElement_3zr2iy$(this.descriptor, 0);
          bitMask0 |= 1;
          if (!readAll)
            break;
        case 1:
          local1 = (bitMask0 & 2) === 0 ? input.decodeSerializableElement_s44l7r$(this.descriptor, 1, Color$Companion_getInstance()) : input.updateSerializableElement_ehubvl$(this.descriptor, 1, Color$Companion_getInstance(), local1);
          bitMask0 |= 2;
          if (!readAll)
            break;
        case -1:
          break loopLabel;
        default:throw new UnknownFieldException(index);
      }
    }
    input.endStructure_qatsm0$(this.descriptor);
    return ColorPicker_init(bitMask0, local0, local1, null);
  };
  ColorPicker$$serializer.prototype.childSerializers = function () {
    return [internal.StringSerializer, Color$Companion_getInstance()];
  };
  ColorPicker$$serializer.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: '$serializer',
    interfaces: [GeneratedSerializer]
  };
  var ColorPicker$$serializer_instance = null;
  function ColorPicker$$serializer_getInstance() {
    if (ColorPicker$$serializer_instance === null) {
      new ColorPicker$$serializer();
    }
    return ColorPicker$$serializer_instance;
  }
  function ColorPicker_init(seen1, name, initialValue, serializationConstructorMarker) {
    var $this = serializationConstructorMarker || Object.create(ColorPicker.prototype);
    Gadget.call($this);
    if ((seen1 & 1) === 0)
      throw new MissingFieldException('name');
    else
      $this.name = name;
    if ((seen1 & 2) === 0)
      $this.initialValue = Color$Companion_getInstance().WHITE;
    else
      $this.initialValue = initialValue;
    $this.color_u6ly2p$_0 = $this.updatable_t7zvzq$('color', $this.initialValue, Color$Companion_getInstance().serializer());
    return $this;
  }
  ColorPicker.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'ColorPicker',
    interfaces: [Gadget]
  };
  ColorPicker.prototype.component1 = function () {
    return this.name;
  };
  ColorPicker.prototype.component2 = function () {
    return this.initialValue;
  };
  ColorPicker.prototype.copy_ancvbn$ = function (name, initialValue) {
    return new ColorPicker(name === void 0 ? this.name : name, initialValue === void 0 ? this.initialValue : initialValue);
  };
  ColorPicker.prototype.toString = function () {
    return 'ColorPicker(name=' + Kotlin.toString(this.name) + (', initialValue=' + Kotlin.toString(this.initialValue)) + ')';
  };
  ColorPicker.prototype.hashCode = function () {
    var result = 0;
    result = result * 31 + Kotlin.hashCode(this.name) | 0;
    result = result * 31 + Kotlin.hashCode(this.initialValue) | 0;
    return result;
  };
  ColorPicker.prototype.equals = function (other) {
    return this === other || (other !== null && (typeof other === 'object' && (Object.getPrototypeOf(this) === Object.getPrototypeOf(other) && (Kotlin.equals(this.name, other.name) && Kotlin.equals(this.initialValue, other.initialValue)))));
  };
  function PalettePicker(name, initialColors) {
    PalettePicker$Companion_getInstance();
    if (initialColors === void 0)
      initialColors = emptyList();
    Gadget.call(this);
    this.name = name;
    this.initialColors = initialColors;
    this.colors_a88nfe$_0 = this.updatable_t7zvzq$('colors', this.initialColors, get_list(Color$Companion_getInstance().serializer()));
  }
  var PalettePicker$colors_metadata = new PropertyMetadata('colors');
  Object.defineProperty(PalettePicker.prototype, 'colors', {
    get: function () {
      return this.colors_a88nfe$_0.getValue_lrcp0p$(this, PalettePicker$colors_metadata);
    },
    set: function (colors) {
      this.colors_a88nfe$_0.setValue_9rddgb$(this, PalettePicker$colors_metadata, colors);
    }
  });
  function PalettePicker$Companion() {
    PalettePicker$Companion_instance = this;
  }
  PalettePicker$Companion.prototype.serializer = function () {
    return PalettePicker$$serializer_getInstance();
  };
  PalettePicker$Companion.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'Companion',
    interfaces: []
  };
  var PalettePicker$Companion_instance = null;
  function PalettePicker$Companion_getInstance() {
    if (PalettePicker$Companion_instance === null) {
      new PalettePicker$Companion();
    }
    return PalettePicker$Companion_instance;
  }
  function PalettePicker$$serializer() {
    this.descriptor_inlol9$_0 = new SerialClassDescImpl('baaahs.gadgets.PalettePicker', this);
    this.descriptor.addElement_ivxn3r$('name', false);
    this.descriptor.addElement_ivxn3r$('initialColors', true);
    PalettePicker$$serializer_instance = this;
  }
  Object.defineProperty(PalettePicker$$serializer.prototype, 'descriptor', {
    get: function () {
      return this.descriptor_inlol9$_0;
    }
  });
  PalettePicker$$serializer.prototype.serialize_awe97i$ = function (encoder, obj) {
    var output = encoder.beginStructure_r0sa6z$(this.descriptor, []);
    output.encodeStringElement_bgm7zs$(this.descriptor, 0, obj.name);
    if (!equals(obj.initialColors, emptyList()) || output.shouldEncodeElementDefault_3zr2iy$(this.descriptor, 1))
      output.encodeSerializableElement_blecud$(this.descriptor, 1, new ArrayListSerializer(Color$Companion_getInstance()), obj.initialColors);
    output.endStructure_qatsm0$(this.descriptor);
  };
  PalettePicker$$serializer.prototype.deserialize_nts5qn$ = function (decoder) {
    var index, readAll = false;
    var bitMask0 = 0;
    var local0
    , local1;
    var input = decoder.beginStructure_r0sa6z$(this.descriptor, []);
    loopLabel: while (true) {
      index = input.decodeElementIndex_qatsm0$(this.descriptor);
      switch (index) {
        case -2:
          readAll = true;
        case 0:
          local0 = input.decodeStringElement_3zr2iy$(this.descriptor, 0);
          bitMask0 |= 1;
          if (!readAll)
            break;
        case 1:
          local1 = (bitMask0 & 2) === 0 ? input.decodeSerializableElement_s44l7r$(this.descriptor, 1, new ArrayListSerializer(Color$Companion_getInstance())) : input.updateSerializableElement_ehubvl$(this.descriptor, 1, new ArrayListSerializer(Color$Companion_getInstance()), local1);
          bitMask0 |= 2;
          if (!readAll)
            break;
        case -1:
          break loopLabel;
        default:throw new UnknownFieldException(index);
      }
    }
    input.endStructure_qatsm0$(this.descriptor);
    return PalettePicker_init(bitMask0, local0, local1, null);
  };
  PalettePicker$$serializer.prototype.childSerializers = function () {
    return [internal.StringSerializer, new ArrayListSerializer(Color$Companion_getInstance())];
  };
  PalettePicker$$serializer.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: '$serializer',
    interfaces: [GeneratedSerializer]
  };
  var PalettePicker$$serializer_instance = null;
  function PalettePicker$$serializer_getInstance() {
    if (PalettePicker$$serializer_instance === null) {
      new PalettePicker$$serializer();
    }
    return PalettePicker$$serializer_instance;
  }
  function PalettePicker_init(seen1, name, initialColors, serializationConstructorMarker) {
    var $this = serializationConstructorMarker || Object.create(PalettePicker.prototype);
    Gadget.call($this);
    if ((seen1 & 1) === 0)
      throw new MissingFieldException('name');
    else
      $this.name = name;
    if ((seen1 & 2) === 0)
      $this.initialColors = emptyList();
    else
      $this.initialColors = initialColors;
    $this.colors_a88nfe$_0 = $this.updatable_t7zvzq$('colors', $this.initialColors, get_list(Color$Companion_getInstance().serializer()));
    return $this;
  }
  PalettePicker.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'PalettePicker',
    interfaces: [Gadget]
  };
  PalettePicker.prototype.component1 = function () {
    return this.name;
  };
  PalettePicker.prototype.component2 = function () {
    return this.initialColors;
  };
  PalettePicker.prototype.copy_dm5ovu$ = function (name, initialColors) {
    return new PalettePicker(name === void 0 ? this.name : name, initialColors === void 0 ? this.initialColors : initialColors);
  };
  PalettePicker.prototype.toString = function () {
    return 'PalettePicker(name=' + Kotlin.toString(this.name) + (', initialColors=' + Kotlin.toString(this.initialColors)) + ')';
  };
  PalettePicker.prototype.hashCode = function () {
    var result = 0;
    result = result * 31 + Kotlin.hashCode(this.name) | 0;
    result = result * 31 + Kotlin.hashCode(this.initialColors) | 0;
    return result;
  };
  PalettePicker.prototype.equals = function (other) {
    return this === other || (other !== null && (typeof other === 'object' && (Object.getPrototypeOf(this) === Object.getPrototypeOf(other) && (Kotlin.equals(this.name, other.name) && Kotlin.equals(this.initialColors, other.initialColors)))));
  };
  function Slider(name, initialValue, minValue, maxValue) {
    Slider$Companion_getInstance();
    if (initialValue === void 0)
      initialValue = 1.0;
    if (minValue === void 0)
      minValue = null;
    if (maxValue === void 0)
      maxValue = null;
    Gadget.call(this);
    this.name = name;
    this.initialValue = initialValue;
    this.minValue = minValue;
    this.maxValue = maxValue;
    this.value_2xmiz9$_0 = this.updatable_t7zvzq$('value', this.initialValue, serializer_0(kotlin_js_internal_FloatCompanionObject));
  }
  var Slider$value_metadata = new PropertyMetadata('value');
  Object.defineProperty(Slider.prototype, 'value', {
    get: function () {
      return this.value_2xmiz9$_0.getValue_lrcp0p$(this, Slider$value_metadata);
    },
    set: function (value) {
      this.value_2xmiz9$_0.setValue_9rddgb$(this, Slider$value_metadata, value);
    }
  });
  function Slider$Companion() {
    Slider$Companion_instance = this;
  }
  Slider$Companion.prototype.serializer = function () {
    return Slider$$serializer_getInstance();
  };
  Slider$Companion.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'Companion',
    interfaces: []
  };
  var Slider$Companion_instance = null;
  function Slider$Companion_getInstance() {
    if (Slider$Companion_instance === null) {
      new Slider$Companion();
    }
    return Slider$Companion_instance;
  }
  function Slider$$serializer() {
    this.descriptor_htru8f$_0 = new SerialClassDescImpl('baaahs.gadgets.Slider', this);
    this.descriptor.addElement_ivxn3r$('name', false);
    this.descriptor.addElement_ivxn3r$('initialValue', true);
    this.descriptor.addElement_ivxn3r$('minValue', true);
    this.descriptor.addElement_ivxn3r$('maxValue', true);
    Slider$$serializer_instance = this;
  }
  Object.defineProperty(Slider$$serializer.prototype, 'descriptor', {
    get: function () {
      return this.descriptor_htru8f$_0;
    }
  });
  Slider$$serializer.prototype.serialize_awe97i$ = function (encoder, obj) {
    var output = encoder.beginStructure_r0sa6z$(this.descriptor, []);
    output.encodeStringElement_bgm7zs$(this.descriptor, 0, obj.name);
    if (!equals(obj.initialValue, 1.0) || output.shouldEncodeElementDefault_3zr2iy$(this.descriptor, 1))
      output.encodeFloatElement_t7qhdx$(this.descriptor, 1, obj.initialValue);
    if (!equals(obj.minValue, null) || output.shouldEncodeElementDefault_3zr2iy$(this.descriptor, 2))
      output.encodeNullableSerializableElement_orpvvi$(this.descriptor, 2, internal.FloatSerializer, obj.minValue);
    if (!equals(obj.maxValue, null) || output.shouldEncodeElementDefault_3zr2iy$(this.descriptor, 3))
      output.encodeNullableSerializableElement_orpvvi$(this.descriptor, 3, internal.FloatSerializer, obj.maxValue);
    output.endStructure_qatsm0$(this.descriptor);
  };
  Slider$$serializer.prototype.deserialize_nts5qn$ = function (decoder) {
    var index, readAll = false;
    var bitMask0 = 0;
    var local0
    , local1
    , local2
    , local3;
    var input = decoder.beginStructure_r0sa6z$(this.descriptor, []);
    loopLabel: while (true) {
      index = input.decodeElementIndex_qatsm0$(this.descriptor);
      switch (index) {
        case -2:
          readAll = true;
        case 0:
          local0 = input.decodeStringElement_3zr2iy$(this.descriptor, 0);
          bitMask0 |= 1;
          if (!readAll)
            break;
        case 1:
          local1 = input.decodeFloatElement_3zr2iy$(this.descriptor, 1);
          bitMask0 |= 2;
          if (!readAll)
            break;
        case 2:
          local2 = (bitMask0 & 4) === 0 ? input.decodeNullableSerializableElement_cwlm4k$(this.descriptor, 2, internal.FloatSerializer) : input.updateNullableSerializableElement_u33s02$(this.descriptor, 2, internal.FloatSerializer, local2);
          bitMask0 |= 4;
          if (!readAll)
            break;
        case 3:
          local3 = (bitMask0 & 8) === 0 ? input.decodeNullableSerializableElement_cwlm4k$(this.descriptor, 3, internal.FloatSerializer) : input.updateNullableSerializableElement_u33s02$(this.descriptor, 3, internal.FloatSerializer, local3);
          bitMask0 |= 8;
          if (!readAll)
            break;
        case -1:
          break loopLabel;
        default:throw new UnknownFieldException(index);
      }
    }
    input.endStructure_qatsm0$(this.descriptor);
    return Slider_init(bitMask0, local0, local1, local2, local3, null);
  };
  Slider$$serializer.prototype.childSerializers = function () {
    return [internal.StringSerializer, internal.FloatSerializer, new NullableSerializer(internal.FloatSerializer), new NullableSerializer(internal.FloatSerializer)];
  };
  Slider$$serializer.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: '$serializer',
    interfaces: [GeneratedSerializer]
  };
  var Slider$$serializer_instance = null;
  function Slider$$serializer_getInstance() {
    if (Slider$$serializer_instance === null) {
      new Slider$$serializer();
    }
    return Slider$$serializer_instance;
  }
  function Slider_init(seen1, name, initialValue, minValue, maxValue, serializationConstructorMarker) {
    var $this = serializationConstructorMarker || Object.create(Slider.prototype);
    Gadget.call($this);
    if ((seen1 & 1) === 0)
      throw new MissingFieldException('name');
    else
      $this.name = name;
    if ((seen1 & 2) === 0)
      $this.initialValue = 1.0;
    else
      $this.initialValue = initialValue;
    if ((seen1 & 4) === 0)
      $this.minValue = null;
    else
      $this.minValue = minValue;
    if ((seen1 & 8) === 0)
      $this.maxValue = null;
    else
      $this.maxValue = maxValue;
    $this.value_2xmiz9$_0 = $this.updatable_t7zvzq$('value', $this.initialValue, serializer_0(kotlin_js_internal_FloatCompanionObject));
    return $this;
  }
  Slider.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Slider',
    interfaces: [Gadget]
  };
  Slider.prototype.component1 = function () {
    return this.name;
  };
  Slider.prototype.component2 = function () {
    return this.initialValue;
  };
  Slider.prototype.component3 = function () {
    return this.minValue;
  };
  Slider.prototype.component4 = function () {
    return this.maxValue;
  };
  Slider.prototype.copy_553zi3$ = function (name, initialValue, minValue, maxValue) {
    return new Slider(name === void 0 ? this.name : name, initialValue === void 0 ? this.initialValue : initialValue, minValue === void 0 ? this.minValue : minValue, maxValue === void 0 ? this.maxValue : maxValue);
  };
  Slider.prototype.toString = function () {
    return 'Slider(name=' + Kotlin.toString(this.name) + (', initialValue=' + Kotlin.toString(this.initialValue)) + (', minValue=' + Kotlin.toString(this.minValue)) + (', maxValue=' + Kotlin.toString(this.maxValue)) + ')';
  };
  Slider.prototype.hashCode = function () {
    var result = 0;
    result = result * 31 + Kotlin.hashCode(this.name) | 0;
    result = result * 31 + Kotlin.hashCode(this.initialValue) | 0;
    result = result * 31 + Kotlin.hashCode(this.minValue) | 0;
    result = result * 31 + Kotlin.hashCode(this.maxValue) | 0;
    return result;
  };
  Slider.prototype.equals = function (other) {
    return this === other || (other !== null && (typeof other === 'object' && (Object.getPrototypeOf(this) === Object.getPrototypeOf(other) && (Kotlin.equals(this.name, other.name) && Kotlin.equals(this.initialValue, other.initialValue) && Kotlin.equals(this.minValue, other.minValue) && Kotlin.equals(this.maxValue, other.maxValue)))));
  };
  function Matrix4(elements) {
    Matrix4$Companion_getInstance();
    this.elements = elements;
  }
  function Matrix4$Companion() {
    Matrix4$Companion_instance = this;
  }
  Matrix4$Companion.prototype.serializer = function () {
    return Matrix4$$serializer_getInstance();
  };
  Matrix4$Companion.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'Companion',
    interfaces: []
  };
  var Matrix4$Companion_instance = null;
  function Matrix4$Companion_getInstance() {
    if (Matrix4$Companion_instance === null) {
      new Matrix4$Companion();
    }
    return Matrix4$Companion_instance;
  }
  function Matrix4$$serializer() {
    this.descriptor_cgumyy$_0 = new SerialClassDescImpl('baaahs.geom.Matrix4', this);
    this.descriptor.addElement_ivxn3r$('elements', false);
    Matrix4$$serializer_instance = this;
  }
  Object.defineProperty(Matrix4$$serializer.prototype, 'descriptor', {
    get: function () {
      return this.descriptor_cgumyy$_0;
    }
  });
  Matrix4$$serializer.prototype.serialize_awe97i$ = function (encoder, obj) {
    var output = encoder.beginStructure_r0sa6z$(this.descriptor, []);
    output.encodeSerializableElement_blecud$(this.descriptor, 0, new ReferenceArraySerializer(PrimitiveClasses$doubleClass, internal.DoubleSerializer), obj.elements);
    output.endStructure_qatsm0$(this.descriptor);
  };
  Matrix4$$serializer.prototype.deserialize_nts5qn$ = function (decoder) {
    var index, readAll = false;
    var bitMask0 = 0;
    var local0;
    var input = decoder.beginStructure_r0sa6z$(this.descriptor, []);
    loopLabel: while (true) {
      index = input.decodeElementIndex_qatsm0$(this.descriptor);
      switch (index) {
        case -2:
          readAll = true;
        case 0:
          local0 = (bitMask0 & 1) === 0 ? input.decodeSerializableElement_s44l7r$(this.descriptor, 0, new ReferenceArraySerializer(PrimitiveClasses$doubleClass, internal.DoubleSerializer)) : input.updateSerializableElement_ehubvl$(this.descriptor, 0, new ReferenceArraySerializer(PrimitiveClasses$doubleClass, internal.DoubleSerializer), local0);
          bitMask0 |= 1;
          if (!readAll)
            break;
        case -1:
          break loopLabel;
        default:throw new UnknownFieldException(index);
      }
    }
    input.endStructure_qatsm0$(this.descriptor);
    return Matrix4_init_0(bitMask0, local0, null);
  };
  Matrix4$$serializer.prototype.childSerializers = function () {
    return [new ReferenceArraySerializer(PrimitiveClasses$doubleClass, internal.DoubleSerializer)];
  };
  Matrix4$$serializer.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: '$serializer',
    interfaces: [GeneratedSerializer]
  };
  var Matrix4$$serializer_instance = null;
  function Matrix4$$serializer_getInstance() {
    if (Matrix4$$serializer_instance === null) {
      new Matrix4$$serializer();
    }
    return Matrix4$$serializer_instance;
  }
  function Matrix4_init_0(seen1, elements, serializationConstructorMarker) {
    var $this = serializationConstructorMarker || Object.create(Matrix4.prototype);
    if ((seen1 & 1) === 0)
      throw new MissingFieldException('elements');
    else
      $this.elements = elements;
    return $this;
  }
  Matrix4.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Matrix4',
    interfaces: []
  };
  function Vector2F(x, y) {
    Vector2F$Companion_getInstance();
    this.x = x;
    this.y = y;
  }
  Vector2F.prototype.component1 = function () {
    return this.x;
  };
  Vector2F.prototype.component2 = function () {
    return this.y;
  };
  Vector2F.prototype.toString = function () {
    return 'Vector2F(x=' + this.x + ', y=' + this.y + ')';
  };
  function Vector2F$Companion() {
    Vector2F$Companion_instance = this;
  }
  Vector2F$Companion.prototype.serializer = function () {
    return Vector2F$$serializer_getInstance();
  };
  Vector2F$Companion.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'Companion',
    interfaces: []
  };
  var Vector2F$Companion_instance = null;
  function Vector2F$Companion_getInstance() {
    if (Vector2F$Companion_instance === null) {
      new Vector2F$Companion();
    }
    return Vector2F$Companion_instance;
  }
  function Vector2F$$serializer() {
    this.descriptor_3d9kie$_0 = new SerialClassDescImpl('baaahs.geom.Vector2F', this);
    this.descriptor.addElement_ivxn3r$('x', false);
    this.descriptor.addElement_ivxn3r$('y', false);
    Vector2F$$serializer_instance = this;
  }
  Object.defineProperty(Vector2F$$serializer.prototype, 'descriptor', {
    get: function () {
      return this.descriptor_3d9kie$_0;
    }
  });
  Vector2F$$serializer.prototype.serialize_awe97i$ = function (encoder, obj) {
    var output = encoder.beginStructure_r0sa6z$(this.descriptor, []);
    output.encodeFloatElement_t7qhdx$(this.descriptor, 0, obj.x);
    output.encodeFloatElement_t7qhdx$(this.descriptor, 1, obj.y);
    output.endStructure_qatsm0$(this.descriptor);
  };
  Vector2F$$serializer.prototype.deserialize_nts5qn$ = function (decoder) {
    var index, readAll = false;
    var bitMask0 = 0;
    var local0
    , local1;
    var input = decoder.beginStructure_r0sa6z$(this.descriptor, []);
    loopLabel: while (true) {
      index = input.decodeElementIndex_qatsm0$(this.descriptor);
      switch (index) {
        case -2:
          readAll = true;
        case 0:
          local0 = input.decodeFloatElement_3zr2iy$(this.descriptor, 0);
          bitMask0 |= 1;
          if (!readAll)
            break;
        case 1:
          local1 = input.decodeFloatElement_3zr2iy$(this.descriptor, 1);
          bitMask0 |= 2;
          if (!readAll)
            break;
        case -1:
          break loopLabel;
        default:throw new UnknownFieldException(index);
      }
    }
    input.endStructure_qatsm0$(this.descriptor);
    return Vector2F_init(bitMask0, local0, local1, null);
  };
  Vector2F$$serializer.prototype.childSerializers = function () {
    return [internal.FloatSerializer, internal.FloatSerializer];
  };
  Vector2F$$serializer.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: '$serializer',
    interfaces: [GeneratedSerializer]
  };
  var Vector2F$$serializer_instance = null;
  function Vector2F$$serializer_getInstance() {
    if (Vector2F$$serializer_instance === null) {
      new Vector2F$$serializer();
    }
    return Vector2F$$serializer_instance;
  }
  function Vector2F_init(seen1, x, y, serializationConstructorMarker) {
    var $this = serializationConstructorMarker || Object.create(Vector2F.prototype);
    if ((seen1 & 1) === 0)
      throw new MissingFieldException('x');
    else
      $this.x = x;
    if ((seen1 & 2) === 0)
      throw new MissingFieldException('y');
    else
      $this.y = y;
    return $this;
  }
  Vector2F.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Vector2F',
    interfaces: []
  };
  function Vector3F(x, y, z) {
    Vector3F$Companion_getInstance();
    this.x = x;
    this.y = y;
    this.z = z;
  }
  function Vector3F$Companion() {
    Vector3F$Companion_instance = this;
  }
  Vector3F$Companion.prototype.serializer = function () {
    return Vector3F$$serializer_getInstance();
  };
  Vector3F$Companion.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'Companion',
    interfaces: []
  };
  var Vector3F$Companion_instance = null;
  function Vector3F$Companion_getInstance() {
    if (Vector3F$Companion_instance === null) {
      new Vector3F$Companion();
    }
    return Vector3F$Companion_instance;
  }
  function Vector3F$$serializer() {
    this.descriptor_2usph3$_0 = new SerialClassDescImpl('baaahs.geom.Vector3F', this);
    this.descriptor.addElement_ivxn3r$('x', false);
    this.descriptor.addElement_ivxn3r$('y', false);
    this.descriptor.addElement_ivxn3r$('z', false);
    Vector3F$$serializer_instance = this;
  }
  Object.defineProperty(Vector3F$$serializer.prototype, 'descriptor', {
    get: function () {
      return this.descriptor_2usph3$_0;
    }
  });
  Vector3F$$serializer.prototype.serialize_awe97i$ = function (encoder, obj) {
    var output = encoder.beginStructure_r0sa6z$(this.descriptor, []);
    output.encodeFloatElement_t7qhdx$(this.descriptor, 0, obj.x);
    output.encodeFloatElement_t7qhdx$(this.descriptor, 1, obj.y);
    output.encodeFloatElement_t7qhdx$(this.descriptor, 2, obj.z);
    output.endStructure_qatsm0$(this.descriptor);
  };
  Vector3F$$serializer.prototype.deserialize_nts5qn$ = function (decoder) {
    var index, readAll = false;
    var bitMask0 = 0;
    var local0
    , local1
    , local2;
    var input = decoder.beginStructure_r0sa6z$(this.descriptor, []);
    loopLabel: while (true) {
      index = input.decodeElementIndex_qatsm0$(this.descriptor);
      switch (index) {
        case -2:
          readAll = true;
        case 0:
          local0 = input.decodeFloatElement_3zr2iy$(this.descriptor, 0);
          bitMask0 |= 1;
          if (!readAll)
            break;
        case 1:
          local1 = input.decodeFloatElement_3zr2iy$(this.descriptor, 1);
          bitMask0 |= 2;
          if (!readAll)
            break;
        case 2:
          local2 = input.decodeFloatElement_3zr2iy$(this.descriptor, 2);
          bitMask0 |= 4;
          if (!readAll)
            break;
        case -1:
          break loopLabel;
        default:throw new UnknownFieldException(index);
      }
    }
    input.endStructure_qatsm0$(this.descriptor);
    return Vector3F_init(bitMask0, local0, local1, local2, null);
  };
  Vector3F$$serializer.prototype.childSerializers = function () {
    return [internal.FloatSerializer, internal.FloatSerializer, internal.FloatSerializer];
  };
  Vector3F$$serializer.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: '$serializer',
    interfaces: [GeneratedSerializer]
  };
  var Vector3F$$serializer_instance = null;
  function Vector3F$$serializer_getInstance() {
    if (Vector3F$$serializer_instance === null) {
      new Vector3F$$serializer();
    }
    return Vector3F$$serializer_instance;
  }
  function Vector3F_init(seen1, x, y, z, serializationConstructorMarker) {
    var $this = serializationConstructorMarker || Object.create(Vector3F.prototype);
    if ((seen1 & 1) === 0)
      throw new MissingFieldException('x');
    else
      $this.x = x;
    if ((seen1 & 2) === 0)
      throw new MissingFieldException('y');
    else
      $this.y = y;
    if ((seen1 & 4) === 0)
      throw new MissingFieldException('z');
    else
      $this.z = z;
    return $this;
  }
  Vector3F.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Vector3F',
    interfaces: []
  };
  Vector3F.prototype.component1 = function () {
    return this.x;
  };
  Vector3F.prototype.component2 = function () {
    return this.y;
  };
  Vector3F.prototype.component3 = function () {
    return this.z;
  };
  Vector3F.prototype.copy_y2kzbl$ = function (x, y, z) {
    return new Vector3F(x === void 0 ? this.x : x, y === void 0 ? this.y : y, z === void 0 ? this.z : z);
  };
  Vector3F.prototype.toString = function () {
    return 'Vector3F(x=' + Kotlin.toString(this.x) + (', y=' + Kotlin.toString(this.y)) + (', z=' + Kotlin.toString(this.z)) + ')';
  };
  Vector3F.prototype.hashCode = function () {
    var result = 0;
    result = result * 31 + Kotlin.hashCode(this.x) | 0;
    result = result * 31 + Kotlin.hashCode(this.y) | 0;
    result = result * 31 + Kotlin.hashCode(this.z) | 0;
    return result;
  };
  Vector3F.prototype.equals = function (other) {
    return this === other || (other !== null && (typeof other === 'object' && (Object.getPrototypeOf(this) === Object.getPrototypeOf(other) && (Kotlin.equals(this.x, other.x) && Kotlin.equals(this.y, other.y) && Kotlin.equals(this.z, other.z)))));
  };
  function GlslManager() {
  }
  GlslManager.$metadata$ = {
    kind: Kind_INTERFACE,
    simpleName: 'GlslManager',
    interfaces: []
  };
  function GlslRenderer(fragShader, adjustableValues) {
    this.fragShader = fragShader;
    this.adjustableValues = adjustableValues;
    this.surfacesToAdd = ArrayList_init();
    this.pixelCount = 0;
    this.nextPixelOffset = 0;
    this.nextSurfaceOffset = 0;
    this.glslSurfaces = ArrayList_init();
    this.uvCoordTextureIndex = 0;
    this.surfaceOrdinalTextureIndex = 1;
    this.nextTextureIndex = 2;
    var $receiver = this.adjustableValues;
    var destination = ArrayList_init_1(collectionSizeOrDefault($receiver, 10));
    var tmp$;
    tmp$ = $receiver.iterator();
    while (tmp$.hasNext()) {
      var item = tmp$.next();
      var tmp$_0;
      destination.add_11rb$((tmp$_0 = this.nextTextureIndex, this.nextTextureIndex = tmp$_0 + 1 | 0, tmp$_0));
    }
    this.adjustableValueUniformIndices = destination;
    this.uvCoordsLocation_8be2vx$ = null;
    this.matLocation_8be2vx$ = null;
    this.resolutionLocation_8be2vx$ = null;
    this.timeLocation_8be2vx$ = null;
    this.instance_sgrkp0$_0 = this.instance_sgrkp0$_0;
  }
  Object.defineProperty(GlslRenderer.prototype, 'instance', {
    get: function () {
      if (this.instance_sgrkp0$_0 == null)
        return throwUPAE('instance');
      return this.instance_sgrkp0$_0;
    },
    set: function (instance) {
      this.instance_sgrkp0$_0 = instance;
    }
  });
  GlslRenderer.prototype.findUniforms = function () {
    this.uvCoordsLocation_8be2vx$ = this.getUniformLocation_61zpoe$('sm_uvCoords');
    this.resolutionLocation_8be2vx$ = this.getUniformLocation_61zpoe$('resolution');
    this.timeLocation_8be2vx$ = this.getUniformLocation_61zpoe$('time');
  };
  GlslRenderer.prototype.addSurface_ppt8xj$ = function (surface) {
    var tmp$;
    if (Kotlin.isType(surface, IdentifiedSurface) && surface.pixelVertices != null) {
      var surfacePixels = new GlslSurface(this.createSurfacePixels_x0dork$(surface, this.nextPixelOffset), new GlslRenderer$Uniforms(this, (tmp$ = this.nextSurfaceOffset, this.nextSurfaceOffset = tmp$ + 1 | 0, tmp$)));
      this.surfacesToAdd.add_11rb$(surfacePixels);
      this.nextPixelOffset = this.nextPixelOffset + surface.pixelCount | 0;
      return surfacePixels;
    }
    return null;
  };
  function GlslRenderer$incorporateNewSurfaces$lambda(this$GlslRenderer) {
    return function () {
      this$GlslRenderer.instance.release();
      return Unit;
    };
  }
  function GlslRenderer$incorporateNewSurfaces$lambda_0(closure$newPixelCount, closure$newUvCoords, this$GlslRenderer) {
    return function () {
      this$GlslRenderer.instance = this$GlslRenderer.createInstance_mfoo9a$(closure$newPixelCount, closure$newUvCoords, this$GlslRenderer.nextSurfaceOffset);
      this$GlslRenderer.instance.bindUvCoordTexture_msnecj$(this$GlslRenderer.uvCoordTextureIndex, ensureNotNull(this$GlslRenderer.uvCoordsLocation_8be2vx$));
      return Unit;
    };
  }
  GlslRenderer.prototype.incorporateNewSurfaces = function () {
    if (!this.surfacesToAdd.isEmpty()) {
      var oldUvCoords = this.instance.uvCoords;
      var newPixelCount = this.nextPixelOffset;
      this.withGlContext_klfg04$(GlslRenderer$incorporateNewSurfaces$lambda(this));
      var newUvCoords = new Float32Array(this.get_bufSize_s8ev3n$(newPixelCount) * 2 | 0);
      arrayCopy(oldUvCoords, newUvCoords, 0, 0, oldUvCoords.length);
      var tmp$;
      tmp$ = this.surfacesToAdd.iterator();
      while (tmp$.hasNext()) {
        var element = tmp$.next();
        var tmp$_0;
        var surface = element.pixels.surface;
        var pixelVertices = ensureNotNull(surface.pixelVertices);
        tmp$_0 = surface.pixelCount;
        for (var i = 0; i < tmp$_0; i++) {
          var uvOffset = (element.pixels.pixel0Index + i | 0) * 2 | 0;
          newUvCoords[uvOffset] = pixelVertices.get_za3lpa$(i).x;
          newUvCoords[uvOffset + 1 | 0] = pixelVertices.get_za3lpa$(i).y;
        }
      }
      this.withGlContext_klfg04$(GlslRenderer$incorporateNewSurfaces$lambda_0(newPixelCount, newUvCoords, this));
      this.pixelCount = newPixelCount;
      println('Now managing ' + this.pixelCount + ' pixels.');
      this.glslSurfaces.addAll_brywnq$(this.surfacesToAdd);
      this.surfacesToAdd.clear();
    }
  };
  function GlslRenderer$AdjustibleUniform() {
  }
  GlslRenderer$AdjustibleUniform.$metadata$ = {
    kind: Kind_INTERFACE,
    simpleName: 'AdjustibleUniform',
    interfaces: []
  };
  function GlslRenderer$Instance($outer, pixelCount, uvCoords, surfaceCount) {
    this.$outer = $outer;
    this.pixelCount = pixelCount;
    this.uvCoords = uvCoords;
    this.surfaceCount = surfaceCount;
  }
  GlslRenderer$Instance.prototype.bindUniforms = function () {
    var tmp$;
    tmp$ = this.adjustableUniforms.iterator();
    while (tmp$.hasNext()) {
      var element = tmp$.next();
      element.bind();
    }
  };
  GlslRenderer$Instance.prototype.setUniform_jv3efz$ = function (adjustableValue, surfaceOrdinal, value) {
    this.adjustableUniforms.get_za3lpa$(adjustableValue.ordinal).setValue_6t2rgq$(surfaceOrdinal, value);
  };
  GlslRenderer$Instance.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Instance',
    interfaces: []
  };
  function GlslRenderer$Uniform(locationInternal) {
    this.locationInternal = locationInternal;
  }
  GlslRenderer$Uniform.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Uniform',
    interfaces: []
  };
  GlslRenderer.prototype.get_bufWidth_s8ev3n$ = function ($receiver) {
    var b = Math_0.min($receiver, 1024);
    return Math_0.max(1, b);
  };
  GlslRenderer.prototype.get_bufHeight_s8ev3n$ = function ($receiver) {
    return ($receiver / 1024 | 0) + 1 | 0;
  };
  GlslRenderer.prototype.get_bufSize_s8ev3n$ = function ($receiver) {
    return Kotlin.imul(this.get_bufWidth_s8ev3n$($receiver), this.get_bufHeight_s8ev3n$($receiver));
  };
  function GlslRenderer$Uniforms($outer, surfaceOrdinal) {
    this.$outer = $outer;
    this.surfaceOrdinal_8be2vx$ = surfaceOrdinal;
  }
  GlslRenderer$Uniforms.prototype.updateFrom_eg9ycu$ = function (values) {
    var $receiver = this.$outer.adjustableValues;
    this.$outer;
    var tmp$;
    tmp$ = $receiver.iterator();
    while (tmp$.hasNext()) {
      var element = tmp$.next();
      this.$outer.instance.setUniform_jv3efz$(element, this.surfaceOrdinal_8be2vx$, values[element.ordinal]);
    }
  };
  GlslRenderer$Uniforms.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Uniforms',
    interfaces: []
  };
  GlslRenderer.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'GlslRenderer',
    interfaces: []
  };
  function GlslSurface(pixels, uniforms) {
    this.pixels = pixels;
    this.uniforms = uniforms;
  }
  GlslSurface.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'GlslSurface',
    interfaces: []
  };
  function SurfacePixels(surface, pixel0Index) {
    this.surface = surface;
    this.pixel0Index = pixel0Index;
    this.size_l2kxw9$_0 = this.surface.pixelCount;
  }
  Object.defineProperty(SurfacePixels.prototype, 'size', {
    get: function () {
      return this.size_l2kxw9$_0;
    }
  });
  SurfacePixels.prototype.set_ibd5tj$ = function (i, color) {
    throw new NotImplementedError_init('An operation is not implemented: ' + 'set not implemented');
  };
  SurfacePixels.prototype.set_tmuqsv$ = function (colors) {
    throw new NotImplementedError_init('An operation is not implemented: ' + 'set not implemented');
  };
  SurfacePixels.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'SurfacePixels',
    interfaces: [Pixels]
  };
  function Image() {
  }
  Image.$metadata$ = {
    kind: Kind_INTERFACE,
    simpleName: 'Image',
    interfaces: []
  };
  function Bitmap() {
  }
  Bitmap.prototype.withData_u0v8ny$ = function (region, fn, callback$default) {
    if (region === void 0)
      region = MediaDevices$Region$Companion_getInstance().containing_5151av$(this);
    callback$default ? callback$default(region, fn) : this.withData_u0v8ny$$default(region, fn);
  };
  Bitmap.$metadata$ = {
    kind: Kind_INTERFACE,
    simpleName: 'Bitmap',
    interfaces: []
  };
  function UByteClampedArray() {
  }
  UByteClampedArray.$metadata$ = {
    kind: Kind_INTERFACE,
    simpleName: 'UByteClampedArray',
    interfaces: []
  };
  function ByteArrayReader(bytes, offset) {
    if (offset === void 0)
      offset = 0;
    this.bytes = bytes;
    this.offset_gb4pop$_0 = offset;
  }
  Object.defineProperty(ByteArrayReader.prototype, 'offset', {
    get: function () {
      return this.offset_gb4pop$_0;
    },
    set: function (value) {
      if (value > this.bytes.length) {
        throw IllegalStateException_init('array index out of bounds');
      }
      this.offset_gb4pop$_0 = value;
    }
  });
  ByteArrayReader.prototype.readBoolean = function () {
    var tmp$;
    return this.bytes[tmp$ = this.offset, this.offset = tmp$ + 1 | 0, tmp$] !== 0;
  };
  ByteArrayReader.prototype.readByte = function () {
    var tmp$;
    return this.bytes[tmp$ = this.offset, this.offset = tmp$ + 1 | 0, tmp$];
  };
  ByteArrayReader.prototype.readShort = function () {
    var tmp$, tmp$_0;
    return toShort((this.bytes[tmp$ = this.offset, this.offset = tmp$ + 1 | 0, tmp$] & 255) << 8 | this.bytes[tmp$_0 = this.offset, this.offset = tmp$_0 + 1 | 0, tmp$_0] & 255);
  };
  ByteArrayReader.prototype.readChar = function () {
    return toBoxedChar(toChar(this.readShort()));
  };
  ByteArrayReader.prototype.readInt = function () {
    var tmp$, tmp$_0, tmp$_1, tmp$_2;
    return (this.bytes[tmp$ = this.offset, this.offset = tmp$ + 1 | 0, tmp$] & 255) << 24 | (this.bytes[tmp$_0 = this.offset, this.offset = tmp$_0 + 1 | 0, tmp$_0] & 255) << 16 | (this.bytes[tmp$_1 = this.offset, this.offset = tmp$_1 + 1 | 0, tmp$_1] & 255) << 8 | this.bytes[tmp$_2 = this.offset, this.offset = tmp$_2 + 1 | 0, tmp$_2] & 255;
  };
  ByteArrayReader.prototype.readLong = function () {
    return Kotlin.Long.fromInt(this.readInt()).and(L4294967295).shiftLeft(32).or(Kotlin.Long.fromInt(this.readInt()).and(L4294967295));
  };
  ByteArrayReader.prototype.readFloat = function () {
    var bits = this.readInt();
    return Kotlin.floatFromBits(bits);
  };
  ByteArrayReader.prototype.readString = function () {
    return decodeToString(this.readBytes());
  };
  ByteArrayReader.prototype.readNullableString = function () {
    return this.readBoolean() ? this.readString() : null;
  };
  ByteArrayReader.prototype.readBytes = function () {
    var count = this.readInt();
    return this.readNBytes_za3lpa$(count);
  };
  ByteArrayReader.prototype.readNBytes_za3lpa$ = function (count) {
    var bytes = copyOfRange(this.bytes, this.offset, this.offset + count | 0);
    this.offset = this.offset + count | 0;
    return bytes;
  };
  ByteArrayReader.prototype.readBytes_fqrh44$ = function (buffer) {
    var count = this.readInt();
    var toCopy = Math_0.min(buffer.length, count);
    arrayCopy(this.bytes, buffer, 0, this.offset, this.offset + toCopy | 0);
    this.offset = this.offset + count | 0;
    return toCopy;
  };
  ByteArrayReader.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'ByteArrayReader',
    interfaces: []
  };
  function ByteArrayWriter(bytes, offset) {
    if (bytes === void 0)
      bytes = new Int8Array(128);
    if (offset === void 0)
      offset = 0;
    this.bytes_0 = bytes;
    this.offset = offset;
  }
  ByteArrayWriter.prototype.writeBoolean_6taknv$ = function (b) {
    var tmp$;
    this.growIfNecessary_0(1);
    this.bytes_0[tmp$ = this.offset, this.offset = tmp$ + 1 | 0, tmp$] = b ? 1 : 0;
  };
  ByteArrayWriter.prototype.writeByte_s8j3t7$ = function (b) {
    var tmp$;
    this.growIfNecessary_0(1);
    this.bytes_0[tmp$ = this.offset, this.offset = tmp$ + 1 | 0, tmp$] = b;
  };
  ByteArrayWriter.prototype.writeShort_za3lpa$ = function (i) {
    if ((i & 65535) !== i) {
      throw IllegalArgumentException_init(i.toString() + " doesn't fit in a short");
    }
    this.writeShort_mq22fl$(toShort(i));
  };
  ByteArrayWriter.prototype.writeShort_mq22fl$ = function (s) {
    var tmp$, tmp$_0;
    this.growIfNecessary_0(2);
    this.bytes_0[tmp$ = this.offset, this.offset = tmp$ + 1 | 0, tmp$] = toByte(s >> 8 & 255);
    this.bytes_0[tmp$_0 = this.offset, this.offset = tmp$_0 + 1 | 0, tmp$_0] = toByte(s & 255);
  };
  ByteArrayWriter.prototype.writeChar_s8itvh$ = function (c) {
    this.writeShort_mq22fl$(toShort(c | 0));
  };
  ByteArrayWriter.prototype.writeInt_za3lpa$ = function (i) {
    var tmp$, tmp$_0, tmp$_1, tmp$_2;
    this.growIfNecessary_0(4);
    this.bytes_0[tmp$ = this.offset, this.offset = tmp$ + 1 | 0, tmp$] = toByte(i >> 24 & 255);
    this.bytes_0[tmp$_0 = this.offset, this.offset = tmp$_0 + 1 | 0, tmp$_0] = toByte(i >> 16 & 255);
    this.bytes_0[tmp$_1 = this.offset, this.offset = tmp$_1 + 1 | 0, tmp$_1] = toByte(i >> 8 & 255);
    this.bytes_0[tmp$_2 = this.offset, this.offset = tmp$_2 + 1 | 0, tmp$_2] = toByte(i & 255);
  };
  ByteArrayWriter.prototype.writeLong_s8cxhz$ = function (l) {
    this.growIfNecessary_0(8);
    this.writeInt_za3lpa$(l.shiftRight(32).and(L4294967295).toInt());
    this.writeInt_za3lpa$(l.and(L4294967295).toInt());
  };
  ByteArrayWriter.prototype.writeFloat_mx4ult$ = function (f) {
    this.writeInt_za3lpa$(toBits(f));
  };
  ByteArrayWriter.prototype.writeString_61zpoe$ = function (s) {
    this.writeBytes_mj6st8$(encodeToByteArray(s));
  };
  ByteArrayWriter.prototype.writeNullableString_pdl1vj$ = function (s) {
    this.writeBoolean_6taknv$(s != null);
    if (s != null) {
      this.writeString_61zpoe$(s);
    }
  };
  ByteArrayWriter.prototype.writeBytes_mj6st8$ = function (data, startIndex, endIndex) {
    if (startIndex === void 0)
      startIndex = 0;
    if (endIndex === void 0)
      endIndex = data.length;
    var size = endIndex - startIndex | 0;
    this.growIfNecessary_0(4 + size | 0);
    this.writeInt_za3lpa$(size);
    arrayCopy(data, this.bytes_0, this.offset, startIndex, endIndex);
    this.offset = this.offset + size | 0;
  };
  ByteArrayWriter.prototype.writeNBytes_mj6st8$ = function (data, startIndex, endIndex) {
    if (startIndex === void 0)
      startIndex = 0;
    if (endIndex === void 0)
      endIndex = data.length;
    var size = endIndex - startIndex | 0;
    this.growIfNecessary_0(size);
    arrayCopy(data, this.bytes_0, this.offset, startIndex, endIndex);
    this.offset = this.offset + size | 0;
  };
  ByteArrayWriter.prototype.toBytes = function () {
    return copyOf(this.bytes_0, this.offset);
  };
  ByteArrayWriter.prototype.growIfNecessary_0 = function (by) {
    if ((this.offset + by | 0) > this.bytes_0.length) {
      var newSize = this.bytes_0.length * 2 | 0;
      while ((this.offset + by | 0) > newSize)
        newSize = newSize * 2 | 0;
      this.bytes_0 = copyOf(this.bytes_0, newSize);
    }
  };
  ByteArrayWriter.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'ByteArrayWriter',
    interfaces: []
  };
  function ByteArrayWriter_init(size, $this) {
    $this = $this || Object.create(ByteArrayWriter.prototype);
    ByteArrayWriter.call($this, new Int8Array(size));
    return $this;
  }
  function Fs() {
  }
  Fs.prototype.createFile_7x97xx$ = function (path, content, allowOverwrite, callback$default) {
    if (allowOverwrite === void 0)
      allowOverwrite = false;
    callback$default ? callback$default(path, content, allowOverwrite) : this.createFile_7x97xx$$default(path, content, allowOverwrite);
  };
  Fs.prototype.createFile_qz9155$ = function (path, content, allowOverwrite, callback$default) {
    if (allowOverwrite === void 0)
      allowOverwrite = false;
    callback$default ? callback$default(path, content, allowOverwrite) : this.createFile_qz9155$$default(path, content, allowOverwrite);
  };
  Fs.$metadata$ = {
    kind: Kind_INTERFACE,
    simpleName: 'Fs',
    interfaces: []
  };
  function DateTimeSerializer() {
    DateTimeSerializer_instance = this;
    this.descriptor_b8hcmr$_0 = withName(internal.StringDescriptor, 'DateTime');
  }
  Object.defineProperty(DateTimeSerializer.prototype, 'descriptor', {
    get: function () {
      return this.descriptor_b8hcmr$_0;
    }
  });
  DateTimeSerializer.prototype.deserialize_nts5qn$ = function (decoder) {
    return new DateTime(decoder.decodeDouble());
  };
  DateTimeSerializer.prototype.serialize_awe97i$ = function (encoder, obj) {
    encoder.encodeDouble_14dthe$(obj.unixMillis);
  };
  DateTimeSerializer.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'DateTimeSerializer',
    interfaces: [KSerializer]
  };
  var DateTimeSerializer_instance = null;
  function DateTimeSerializer_getInstance() {
    if (DateTimeSerializer_instance === null) {
      new DateTimeSerializer();
    }
    return DateTimeSerializer_instance;
  }
  function ImageProcessing() {
    ImageProcessing$Companion_getInstance();
  }
  function ImageProcessing$Histogram(data, total) {
    this.data = data;
    this.total = total;
  }
  ImageProcessing$Histogram.prototype.sumValues_n8acyv$ = function (range) {
    var tmp$, tmp$_0, tmp$_1;
    var total = 0;
    tmp$ = range.first;
    tmp$_0 = range.last;
    tmp$_1 = range.step;
    for (var i = tmp$; i <= tmp$_0; i += tmp$_1) {
      total = total + this.data[i] | 0;
    }
    return total;
  };
  ImageProcessing$Histogram.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Histogram',
    interfaces: []
  };
  function ImageProcessing$Companion() {
    ImageProcessing$Companion_instance = this;
  }
  function ImageProcessing$Companion$channelHistogram$lambda(closure$channel, closure$hist) {
    return function (it) {
      var totalBytes = it.size;
      for (var i = closure$channel; i < totalBytes; i += 4) {
        var tmp$;
        tmp$ = it.get_za3lpa$(i) & 255;
        closure$hist[tmp$] = closure$hist[tmp$] + 1 | 0;
      }
      return false;
    };
  }
  ImageProcessing$Companion.prototype.channelHistogram_7c0di7$ = function (channel, bitmap) {
    var array = new Int32Array(256);
    var tmp$;
    tmp$ = array.length - 1 | 0;
    for (var i = 0; i <= tmp$; i++) {
      array[i] = 0;
    }
    var hist = array;
    bitmap.withData_u0v8ny$(void 0, ImageProcessing$Companion$channelHistogram$lambda(channel, hist));
    return new ImageProcessing$Histogram(hist, Kotlin.imul(bitmap.width, bitmap.height));
  };
  ImageProcessing$Companion.prototype.diff_pkkjvd$ = function (newBitmap, baseBitmap, deltaBitmap, detector, maskBitmap, withinRegion) {
    if (maskBitmap === void 0)
      maskBitmap = null;
    if (withinRegion === void 0)
      withinRegion = MediaDevices$Region$Companion_getInstance().containing_5151av$(baseBitmap);
    deltaBitmap.copyFrom_5151av$(baseBitmap);
    deltaBitmap.subtract_5151av$(newBitmap);
    if (maskBitmap != null) {
      deltaBitmap.darken_5151av$(maskBitmap);
    }
    return this.analyze_cpx17j$(deltaBitmap, detector, withinRegion);
  };
  function ImageProcessing$Companion$pixels$lambda(closure$regionOfInterest, closure$bitmap, closure$detector, closure$fn) {
    return function (data) {
      var tmp$, tmp$_0, tmp$_1, tmp$_2, tmp$_3, tmp$_4, tmp$_5, tmp$_6;
      tmp$ = closure$regionOfInterest.yRange;
      tmp$_0 = tmp$.first;
      tmp$_1 = tmp$.last;
      tmp$_2 = tmp$.step;
      for (var y = tmp$_0; y <= tmp$_1; y += tmp$_2) {
        tmp$_3 = closure$regionOfInterest.xRange;
        tmp$_4 = tmp$_3.first;
        tmp$_5 = tmp$_3.last;
        tmp$_6 = tmp$_3.step;
        for (var x = tmp$_4; x <= tmp$_5; x += tmp$_6) {
          var pixelByteIndex = (x + Kotlin.imul(y, closure$bitmap.width) | 0) * 4 | 0;
          var pixValue = data.get_za3lpa$(pixelByteIndex + closure$detector.rgbaIndex | 0);
          closure$fn(x, y, pixValue);
        }
      }
      return false;
    };
  }
  ImageProcessing$Companion.prototype.pixels_jkgb9c$ = function (bitmap, detector, regionOfInterest, fn) {
    if (regionOfInterest === void 0)
      regionOfInterest = MediaDevices$Region$Companion_getInstance().containing_5151av$(bitmap);
    bitmap.withData_u0v8ny$(void 0, ImageProcessing$Companion$pixels$lambda(regionOfInterest, bitmap, detector, fn));
  };
  function ImageProcessing$Companion$analyze$lambda(closure$regionOfInterest, closure$bitmap, closure$detector, closure$xMin, closure$xMax, closure$yMin, closure$yMax, closure$hist) {
    return function (data) {
      var tmp$, tmp$_0, tmp$_1, tmp$_2, tmp$_3, tmp$_4, tmp$_5, tmp$_6;
      tmp$ = closure$regionOfInterest.yRange;
      tmp$_0 = tmp$.first;
      tmp$_1 = tmp$.last;
      tmp$_2 = tmp$.step;
      for (var y = tmp$_0; y <= tmp$_1; y += tmp$_2) {
        tmp$_3 = closure$regionOfInterest.xRange;
        tmp$_4 = tmp$_3.first;
        tmp$_5 = tmp$_3.last;
        tmp$_6 = tmp$_3.step;
        for (var x = tmp$_4; x <= tmp$_5; x += tmp$_6) {
          var pixelByteIndex = (x + Kotlin.imul(y, closure$bitmap.width) | 0) * 4 | 0;
          var pixValue = toShort(data.get_za3lpa$(pixelByteIndex + closure$detector.rgbaIndex | 0));
          if (pixValue < closure$xMin[x])
            closure$xMin[x] = pixValue;
          if (pixValue > closure$xMax[x])
            closure$xMax[x] = pixValue;
          if (pixValue < closure$yMin[y])
            closure$yMin[y] = pixValue;
          if (pixValue > closure$yMax[y])
            closure$yMax[y] = pixValue;
          closure$hist[pixValue] = closure$hist[pixValue] + 1 | 0;
        }
      }
      return false;
    };
  }
  ImageProcessing$Companion.prototype.analyze_cpx17j$ = function (bitmap, detector, regionOfInterest) {
    if (regionOfInterest === void 0)
      regionOfInterest = MediaDevices$Region$Companion_getInstance().containing_5151av$(bitmap);
    var array = new Int32Array(256);
    var tmp$;
    tmp$ = array.length - 1 | 0;
    for (var i = 0; i <= tmp$; i++) {
      array[i] = 0;
    }
    var hist = array;
    var array_0 = new Int16Array(bitmap.width);
    var tmp$_0;
    tmp$_0 = array_0.length - 1 | 0;
    for (var i_0 = 0; i_0 <= tmp$_0; i_0++) {
      array_0[i_0] = regionOfInterest.xRange.contains_mef7kx$(i_0) ? 255 : 0;
    }
    var xMin = array_0;
    var array_1 = new Int16Array(bitmap.width);
    var tmp$_1;
    tmp$_1 = array_1.length - 1 | 0;
    for (var i_1 = 0; i_1 <= tmp$_1; i_1++) {
      array_1[i_1] = 0;
    }
    var xMax = array_1;
    var array_2 = new Int16Array(bitmap.height);
    var tmp$_2;
    tmp$_2 = array_2.length - 1 | 0;
    for (var i_2 = 0; i_2 <= tmp$_2; i_2++) {
      array_2[i_2] = regionOfInterest.yRange.contains_mef7kx$(i_2) ? 255 : 0;
    }
    var yMin = array_2;
    var array_3 = new Int16Array(bitmap.height);
    var tmp$_3;
    tmp$_3 = array_3.length - 1 | 0;
    for (var i_3 = 0; i_3 <= tmp$_3; i_3++) {
      array_3[i_3] = 0;
    }
    var yMax = array_3;
    bitmap.withData_u0v8ny$(void 0, ImageProcessing$Companion$analyze$lambda(regionOfInterest, bitmap, detector, xMin, xMax, yMin, yMax, hist));
    return new ImageProcessing$Analysis(bitmap.width, bitmap.height, regionOfInterest, new ImageProcessing$Histogram(hist, Kotlin.imul(bitmap.width, bitmap.height)), xMin, xMax, yMin, yMax);
  };
  ImageProcessing$Companion.prototype.histogram_jnr2u7$ = function ($receiver, range) {
    var hist = new Int32Array(range.last - range.first | 0);
    var tmp$;
    tmp$ = $receiver.iterator();
    while (tmp$.hasNext()) {
      var element = tmp$.next();
      var tmp$_0;
      tmp$_0 = element - range.first | 0;
      hist[tmp$_0] = hist[tmp$_0] + 1 | 0;
    }
    return hist;
  };
  ImageProcessing$Companion.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'Companion',
    interfaces: []
  };
  var ImageProcessing$Companion_instance = null;
  function ImageProcessing$Companion_getInstance() {
    if (ImageProcessing$Companion_instance === null) {
      new ImageProcessing$Companion();
    }
    return ImageProcessing$Companion_instance;
  }
  function ImageProcessing$Analysis(width, height, regionOfInterest, hist, xMin, xMax, yMin, yMax) {
    this.width = width;
    this.height = height;
    this.regionOfInterest = regionOfInterest;
    this.hist = hist;
    this.xMin = xMin;
    this.xMax = xMax;
    this.yMin = yMin;
    this.yMax = yMax;
    this.minValue_lzsrym$_0 = lazy(ImageProcessing$Analysis$minValue$lambda(this));
    this.maxValue_yrpo8s$_0 = lazy(ImageProcessing$Analysis$maxValue$lambda(this));
    this.minChangeToDetect = 10.0;
    this.scale_h1tmnt$_0 = lazy(ImageProcessing$Analysis$scale$lambda(this));
  }
  Object.defineProperty(ImageProcessing$Analysis.prototype, 'minValue', {
    get: function () {
      return this.minValue_lzsrym$_0.value;
    }
  });
  Object.defineProperty(ImageProcessing$Analysis.prototype, 'maxValue', {
    get: function () {
      return this.maxValue_yrpo8s$_0.value;
    }
  });
  Object.defineProperty(ImageProcessing$Analysis.prototype, 'scale', {
    get: function () {
      return this.scale_h1tmnt$_0.value;
    }
  });
  ImageProcessing$Analysis.prototype.thresholdValueFor_mx4ult$ = function (threshold) {
    return toShort(numberToInt(threshold * this.scale)) + this.minValue;
  };
  ImageProcessing$Analysis.prototype.detectChangeRegion_mx4ult$ = function (threshold) {
    var thresholdValue = this.thresholdValueFor_mx4ult$(threshold);
    var $receiver = this.xMax;
    var indexOfFirst$result;
    indexOfFirst$break: do {
      for (var index = 0; index !== $receiver.length; ++index) {
        if ($receiver[index] >= thresholdValue) {
          indexOfFirst$result = index;
          break indexOfFirst$break;
        }
      }
      indexOfFirst$result = -1;
    }
     while (false);
    var minX = indexOfFirst$result;
    var $receiver_0 = this.yMax;
    var indexOfFirst$result_0;
    indexOfFirst$break: do {
      for (var index_0 = 0; index_0 !== $receiver_0.length; ++index_0) {
        if ($receiver_0[index_0] >= thresholdValue) {
          indexOfFirst$result_0 = index_0;
          break indexOfFirst$break;
        }
      }
      indexOfFirst$result_0 = -1;
    }
     while (false);
    var minY = indexOfFirst$result_0;
    var $receiver_1 = this.xMax;
    var indexOfLast$result;
    indexOfLast$break: do {
      var tmp$;
      tmp$ = reversed(get_indices($receiver_1)).iterator();
      while (tmp$.hasNext()) {
        var index_1 = tmp$.next();
        if ($receiver_1[index_1] >= thresholdValue) {
          indexOfLast$result = index_1;
          break indexOfLast$break;
        }
      }
      indexOfLast$result = -1;
    }
     while (false);
    var maxX = indexOfLast$result;
    var $receiver_2 = this.yMax;
    var indexOfLast$result_0;
    indexOfLast$break: do {
      var tmp$_0;
      tmp$_0 = reversed(get_indices($receiver_2)).iterator();
      while (tmp$_0.hasNext()) {
        var index_2 = tmp$_0.next();
        if ($receiver_2[index_2] >= thresholdValue) {
          indexOfLast$result_0 = index_2;
          break indexOfLast$break;
        }
      }
      indexOfLast$result_0 = -1;
    }
     while (false);
    var maxY = indexOfLast$result_0;
    return new MediaDevices$Region(minX, minY, maxX, maxY);
  };
  ImageProcessing$Analysis.prototype.copyOfRange_0 = function ($receiver, intRange) {
    return copyOfRange_0($receiver, intRange.first, intRange.last);
  };
  function ImageProcessing$Analysis$minValue$lambda(this$Analysis) {
    return function () {
      return ensureNotNull(min(this$Analysis.copyOfRange_0(this$Analysis.xMin, this$Analysis.regionOfInterest.xRange)));
    };
  }
  function ImageProcessing$Analysis$maxValue$lambda(this$Analysis) {
    return function () {
      return ensureNotNull(max(this$Analysis.copyOfRange_0(this$Analysis.xMax, this$Analysis.regionOfInterest.xRange)));
    };
  }
  function ImageProcessing$Analysis$scale$lambda(this$Analysis) {
    return function () {
      var a = this$Analysis.minChangeToDetect;
      var b = this$Analysis.maxValue - this$Analysis.minValue | 0;
      return Math_0.max(a, b);
    };
  }
  ImageProcessing$Analysis.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Analysis',
    interfaces: []
  };
  ImageProcessing.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'ImageProcessing',
    interfaces: []
  };
  function MapperClient(link, address) {
    this.$delegate_jh0n3f$_0 = MainScope();
    this.tcpConnection_co05n0$_0 = this.tcpConnection_co05n0$_0;
    this.connected_0 = false;
    this.responses_pa3azj$_0 = this.responses_pa3azj$_0;
    link.connectWebSocket_t0j9bj$(address, 0, '/ws/mapper', this);
  }
  Object.defineProperty(MapperClient.prototype, 'tcpConnection_0', {
    get: function () {
      if (this.tcpConnection_co05n0$_0 == null)
        return throwUPAE('tcpConnection');
      return this.tcpConnection_co05n0$_0;
    },
    set: function (tcpConnection) {
      this.tcpConnection_co05n0$_0 = tcpConnection;
    }
  });
  Object.defineProperty(MapperClient.prototype, 'responses_0', {
    get: function () {
      if (this.responses_pa3azj$_0 == null)
        return throwUPAE('responses');
      return this.responses_pa3azj$_0;
    },
    set: function (responses) {
      this.responses_pa3azj$_0 = responses;
    }
  });
  function Coroutine$listSessions($this, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.exceptionState_0 = 1;
    this.$this = $this;
    this.local$tmp$ = void 0;
    this.local$tmp$_0 = void 0;
  }
  Coroutine$listSessions.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$listSessions.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$listSessions.prototype.constructor = Coroutine$listSessions;
  Coroutine$listSessions.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.local$tmp$_0 = MapperEndpoint$Companion_getInstance().json;
            this.local$tmp$ = get_list(serializer(kotlin_js_internal_StringCompanionObject));
            this.state_0 = 2;
            this.result_0 = this.$this.sendCommand_0('listSessions', [], this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 1:
            throw this.exception_0;
          case 2:
            return this.local$tmp$_0.fromJson_htt2tq$(this.local$tmp$, this.result_0);
          default:this.state_0 = 1;
            throw new Error('State Machine Unreachable execution');
        }
      }
       catch (e) {
        if (this.state_0 === 1) {
          this.exceptionState_0 = this.state_0;
          throw e;
        }
         else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  MapperClient.prototype.listSessions = function (continuation_0, suspended) {
    var instance = new Coroutine$listSessions(this, continuation_0);
    if (suspended)
      return instance;
    else
      return instance.doResume(null);
  };
  function Coroutine$saveImage_39j694$($this, sessionStartTime_0, name_0, bitmap_0, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.exceptionState_0 = 1;
    this.$this = $this;
    this.local$filename = void 0;
    this.local$sessionStartTime = sessionStartTime_0;
    this.local$name = name_0;
    this.local$bitmap = bitmap_0;
  }
  Coroutine$saveImage_39j694$.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$saveImage_39j694$.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$saveImage_39j694$.prototype.constructor = Coroutine$saveImage_39j694$;
  Coroutine$saveImage_39j694$.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.local$filename = Storage$Companion_getInstance().formatDateTime_mw5vjr$(this.local$sessionStartTime) + '/' + this.local$name + '.webp';
            var dataUrl = this.local$bitmap.toDataUrl();
            var startOfData = ';base64,';
            var i = indexOf(dataUrl, startOfData);
            if (i === -1) {
              throw IllegalArgumentException_init('failed to save image ' + dataUrl);
            }

            var tmp$ = JsonPrimitive(this.local$filename);
            var startIndex = i + startOfData.length | 0;
            this.state_0 = 2;
            this.result_0 = this.$this.sendCommand_0('saveImage', [tmp$, JsonPrimitive(dataUrl.substring(startIndex))], this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 1:
            throw this.exception_0;
          case 2:
            return this.local$filename;
          default:this.state_0 = 1;
            throw new Error('State Machine Unreachable execution');
        }
      }
       catch (e) {
        if (this.state_0 === 1) {
          this.exceptionState_0 = this.state_0;
          throw e;
        }
         else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  MapperClient.prototype.saveImage_39j694$ = function (sessionStartTime_0, name_0, bitmap_0, continuation_0, suspended) {
    var instance = new Coroutine$saveImage_39j694$(this, sessionStartTime_0, name_0, bitmap_0, continuation_0);
    if (suspended)
      return instance;
    else
      return instance.doResume(null);
  };
  function Coroutine$saveSession_x3z8ep$($this, mappingSession_0, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.exceptionState_0 = 1;
    this.$this = $this;
    this.local$mappingSession = mappingSession_0;
  }
  Coroutine$saveSession_x3z8ep$.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$saveSession_x3z8ep$.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$saveSession_x3z8ep$.prototype.constructor = Coroutine$saveSession_x3z8ep$;
  Coroutine$saveSession_x3z8ep$.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.state_0 = 2;
            this.result_0 = this.$this.sendCommand_0('saveSession', [MapperEndpoint$Companion_getInstance().json.toJson_tf03ej$(MappingSession$Companion_getInstance().serializer(), this.local$mappingSession)], this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 1:
            throw this.exception_0;
          case 2:
            return;
          default:this.state_0 = 1;
            throw new Error('State Machine Unreachable execution');
        }
      }
       catch (e) {
        if (this.state_0 === 1) {
          this.exceptionState_0 = this.state_0;
          throw e;
        }
         else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  MapperClient.prototype.saveSession_x3z8ep$ = function (mappingSession_0, continuation_0, suspended) {
    var instance = new Coroutine$saveSession_x3z8ep$(this, mappingSession_0, continuation_0);
    if (suspended)
      return instance;
    else
      return instance.doResume(null);
  };
  function MapperClient$sendCommand$lambda(closure$command, closure$args) {
    return function ($receiver) {
      $receiver.unaryPlus_5cw0du$(closure$command);
      var $receiver_0 = closure$args;
      var tmp$;
      for (tmp$ = 0; tmp$ !== $receiver_0.length; ++tmp$) {
        var element = $receiver_0[tmp$];
        $receiver.unaryPlus_u3sd3g$(element);
      }
      return Unit;
    };
  }
  function Coroutine$sendCommand_0($this, command_0, args_0, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.exceptionState_0 = 7;
    this.$this = $this;
    this.local$content = void 0;
    this.local$responseJsonStr = void 0;
    this.local$command = command_0;
    this.local$args = args_0;
  }
  Coroutine$sendCommand_0.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$sendCommand_0.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$sendCommand_0.prototype.constructor = Coroutine$sendCommand_0;
  Coroutine$sendCommand_0.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.local$content = jsonArray(MapperClient$sendCommand$lambda(this.local$command, this.local$args));
            this.state_0 = 1;
            continue;
          case 1:
            if (this.$this.connected_0) {
              this.state_0 = 3;
              continue;
            }

            this.state_0 = 2;
            this.result_0 = delay(L5, this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 2:
            this.state_0 = 1;
            continue;
          case 3:
            this.$this.tcpConnection_0.send_fqrh44$(encodeToByteArray(MapperEndpoint$Companion_getInstance().json.stringify_tf03ej$(json.JsonArraySerializer, this.local$content)));
            this.state_0 = 4;
            this.result_0 = this.$this.responses_0.receive(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 4:
            this.local$responseJsonStr = decodeToString(this.result_0);
            this.exceptionState_0 = 5;
            var responseJson = MapperEndpoint$Companion_getInstance().json.parseJson_61zpoe$(this.local$responseJsonStr);
            var status = responseJson.jsonObject.getPrimitive_61zpoe$('status');
            var response = getValue(responseJson.jsonObject, 'response');
            switch (status.contentOrNull) {
              case 'success':
                return response;
              case 'error':
                throw RuntimeException_init(get_contentOrNull(response));
            }

          case 5:
            this.exceptionState_0 = 7;
            var e = this.exception_0;
            if (Kotlin.isType(e, JsonParsingException)) {
              logger$Companion_getInstance().error_61zpoe$("can't parse response to " + this.local$command + ' ' + this.local$args + ': ' + this.local$responseJsonStr);
              throw e;
            }
             else
              throw e;
          case 6:
            return;
          case 7:
            throw this.exception_0;
          default:this.state_0 = 7;
            throw new Error('State Machine Unreachable execution');
        }
      }
       catch (e) {
        if (this.state_0 === 7) {
          this.exceptionState_0 = this.state_0;
          throw e;
        }
         else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  MapperClient.prototype.sendCommand_0 = function (command_0, args_0, continuation_0, suspended) {
    var instance = new Coroutine$sendCommand_0(this, command_0, args_0, continuation_0);
    if (suspended)
      return instance;
    else
      return instance.doResume(null);
  };
  MapperClient.prototype.connected_67ozxy$ = function (tcpConnection) {
    println('Mapper connected to Pinky!');
    this.tcpConnection_0 = tcpConnection;
    this.responses_0 = Channel(1);
    this.connected_0 = true;
  };
  function Coroutine$MapperClient$receive$lambda(this$MapperClient_0, closure$bytes_0, $receiver_0, controller, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.$controller = controller;
    this.exceptionState_0 = 1;
    this.local$this$MapperClient = this$MapperClient_0;
    this.local$closure$bytes = closure$bytes_0;
  }
  Coroutine$MapperClient$receive$lambda.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$MapperClient$receive$lambda.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$MapperClient$receive$lambda.prototype.constructor = Coroutine$MapperClient$receive$lambda;
  Coroutine$MapperClient$receive$lambda.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.state_0 = 2;
            this.result_0 = this.local$this$MapperClient.responses_0.send_11rb$(this.local$closure$bytes, this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 1:
            throw this.exception_0;
          case 2:
            return this.result_0;
          default:this.state_0 = 1;
            throw new Error('State Machine Unreachable execution');
        }
      }
       catch (e) {
        if (this.state_0 === 1) {
          this.exceptionState_0 = this.state_0;
          throw e;
        }
         else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  function MapperClient$receive$lambda(this$MapperClient_0, closure$bytes_0) {
    return function ($receiver_0, continuation_0, suspended) {
      var instance = new Coroutine$MapperClient$receive$lambda(this$MapperClient_0, closure$bytes_0, $receiver_0, this, continuation_0);
      if (suspended)
        return instance;
      else
        return instance.doResume(null);
    };
  }
  MapperClient.prototype.receive_r00qii$ = function (tcpConnection, bytes) {
    println('Received ' + decodeToString(bytes));
    launch(this, void 0, void 0, MapperClient$receive$lambda(this, bytes));
  };
  MapperClient.prototype.reset_67ozxy$ = function (tcpConnection) {
    this.responses_0.close_dbl4no$();
    throw new NotImplementedError_init('An operation is not implemented: ' + 'MapperClient.reset not implemented');
  };
  Object.defineProperty(MapperClient.prototype, 'coroutineContext', {
    get: function () {
      return this.$delegate_jh0n3f$_0.coroutineContext;
    }
  });
  MapperClient.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'MapperClient',
    interfaces: [CoroutineScope, Network$WebSocketListener]
  };
  function MapperEndpoint(storage) {
    MapperEndpoint$Companion_getInstance();
    this.storage = storage;
  }
  function MapperEndpoint$Companion() {
    MapperEndpoint$Companion_instance = this;
    this.json = new Json(JsonConfiguration.Companion.Stable);
  }
  MapperEndpoint$Companion.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'Companion',
    interfaces: []
  };
  var MapperEndpoint$Companion_instance = null;
  function MapperEndpoint$Companion_getInstance() {
    if (MapperEndpoint$Companion_instance === null) {
      new MapperEndpoint$Companion();
    }
    return MapperEndpoint$Companion_instance;
  }
  MapperEndpoint.prototype.connected_67ozxy$ = function (tcpConnection) {
    println('Received connection from ' + tcpConnection.fromAddress);
  };
  function MapperEndpoint$receive$lambda(closure$status, closure$response) {
    return function ($receiver) {
      $receiver.to_npuxma$('status', closure$status.v);
      $receiver.to_ahl3kc$('response', closure$response.v);
      return Unit;
    };
  }
  MapperEndpoint.prototype.receive_r00qii$ = function (tcpConnection, bytes) {
    var parts = MapperEndpoint$Companion_getInstance().json.parseJson_61zpoe$(decodeToString(bytes)).jsonArray;
    var command = get_contentOrNull(first(parts));
    var status = {v: 'success'};
    var response = {v: null};
    try {
      switch (command) {
        case 'listSessions':
          response.v = MapperEndpoint$Companion_getInstance().json.toJson_tf03ej$(get_list(serializer(kotlin_js_internal_StringCompanionObject)), this.storage.listSessions());
          break;
        case 'saveImage':
          var name = parts.get_za3lpa$(1).primitive.contentOrNull;
          var imageDataBase64 = parts.get_za3lpa$(2).primitive.contentOrNull;
          var imageData = decodeBase64(ensureNotNull(imageDataBase64));
          this.storage.saveImage_yzgtim$(ensureNotNull(name), imageData);
          response.v = json.JsonNull;
          break;
        case 'saveSession':
          var mappingSession = MapperEndpoint$Companion_getInstance().json.fromJson_htt2tq$(MappingSession$Companion_getInstance().serializer(), parts.get_za3lpa$(1));
          this.storage.saveSession_x3z8ep$(mappingSession);
          response.v = json.JsonNull;
          break;
        default:throw UnsupportedOperationException_init_0('unknown command ' + '"' + toString_0(command) + '"');
      }
    }
     catch (e) {
      if (Kotlin.isType(e, Exception)) {
        status.v = 'error';
        response.v = JsonPrimitive(e.toString());
        logger$Companion_getInstance().error_61zpoe$('Command failed: ' + parts);
        logger$Companion_getInstance().error_61zpoe$(e.toString());
      }
       else
        throw e;
    }
    println('Command: ' + parts + ' -> ' + status.v + ' ' + response.v);
    tcpConnection.send_fqrh44$(encodeToByteArray(MapperEndpoint$Companion_getInstance().json.stringify_tf03ej$(json.JsonElementSerializer, json_0(MapperEndpoint$receive$lambda(status, response)))));
  };
  MapperEndpoint.prototype.reset_67ozxy$ = function (tcpConnection) {
  };
  MapperEndpoint.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'MapperEndpoint',
    interfaces: [Network$WebSocketListener]
  };
  function MappingResults() {
  }
  function MappingResults$Info(surface, pixelLocations) {
    this.surface = surface;
    this.pixelLocations = pixelLocations;
  }
  MappingResults$Info.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Info',
    interfaces: []
  };
  MappingResults.$metadata$ = {
    kind: Kind_INTERFACE,
    simpleName: 'MappingResults',
    interfaces: []
  };
  function SessionMappingResults(model, mappingSession) {
    this.brainData = LinkedHashMap_init();
    if (mappingSession != null) {
      var tmp$;
      tmp$ = mappingSession.surfaces.iterator();
      while (tmp$.hasNext()) {
        var element = tmp$.next();
        var brainId = new BrainId(element.brainId);
        var surfaceName = element.surfaceName;
        var $receiver = element.pixels;
        var destination = ArrayList_init_1(collectionSizeOrDefault($receiver, 10));
        var tmp$_0;
        tmp$_0 = $receiver.iterator();
        while (tmp$_0.hasNext()) {
          var item = tmp$_0.next();
          var tmp$_1 = destination.add_11rb$;
          var tmp$_2, tmp$_3;
          var tmp$_4;
          if ((tmp$_2 = item != null ? item.screenPosition : null) != null) {
            var x = tmp$_2.component1()
            , y = tmp$_2.component2();
            tmp$_4 = new Vector2F(x, y);
          }
           else
            tmp$_4 = null;
          tmp$_1.call(destination, (tmp$_3 = tmp$_4) != null ? tmp$_3 : new Vector2F(0.0, 0.0));
        }
        var pixelLocations = destination;
        var modelSurface = model.findModelSurface_61zpoe$(surfaceName);
        var $receiver_0 = this.brainData;
        var value = new MappingResults$Info(modelSurface, pixelLocations);
        $receiver_0.put_xwzc9p$(brainId, value);
      }
    }
  }
  SessionMappingResults.prototype.dataFor_77gxvx$ = function (brainId) {
    return this.brainData.get_11rb$(brainId);
  };
  SessionMappingResults.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'SessionMappingResults',
    interfaces: [MappingResults]
  };
  function MappingSession(startedAt, surfaces, cameraMatrix, baseImage, version, savedAt, notes) {
    MappingSession$Companion_getInstance();
    if (version === void 0)
      version = 0;
    if (savedAt === void 0)
      savedAt = DateTime.Companion.nowUnix();
    if (notes === void 0)
      notes = null;
    this.startedAt = startedAt;
    this.surfaces = surfaces;
    this.cameraMatrix = cameraMatrix;
    this.baseImage = baseImage;
    this.version = version;
    this.savedAt = savedAt;
    this.notes = notes;
  }
  Object.defineProperty(MappingSession.prototype, 'startedAtDateTime', {
    get: function () {
      return new DateTime(this.startedAt);
    }
  });
  function MappingSession$SurfaceData(brainId, panelName, pixels, deltaImage, screenAreaInSqPixels, screenAngle) {
    MappingSession$SurfaceData$Companion_getInstance();
    this.brainId = brainId;
    this.panelName = panelName;
    this.pixels = pixels;
    this.deltaImage = deltaImage;
    this.screenAreaInSqPixels = screenAreaInSqPixels;
    this.screenAngle = screenAngle;
  }
  Object.defineProperty(MappingSession$SurfaceData.prototype, 'surfaceName', {
    get: function () {
      return this.panelName;
    }
  });
  function MappingSession$SurfaceData$PixelData(modelPosition, screenPosition, deltaImage) {
    MappingSession$SurfaceData$PixelData$Companion_getInstance();
    this.modelPosition = modelPosition;
    this.screenPosition = screenPosition;
    this.deltaImage = deltaImage;
  }
  function MappingSession$SurfaceData$PixelData$Companion() {
    MappingSession$SurfaceData$PixelData$Companion_instance = this;
  }
  MappingSession$SurfaceData$PixelData$Companion.prototype.serializer = function () {
    return MappingSession$SurfaceData$PixelData$$serializer_getInstance();
  };
  MappingSession$SurfaceData$PixelData$Companion.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'Companion',
    interfaces: []
  };
  var MappingSession$SurfaceData$PixelData$Companion_instance = null;
  function MappingSession$SurfaceData$PixelData$Companion_getInstance() {
    if (MappingSession$SurfaceData$PixelData$Companion_instance === null) {
      new MappingSession$SurfaceData$PixelData$Companion();
    }
    return MappingSession$SurfaceData$PixelData$Companion_instance;
  }
  function MappingSession$SurfaceData$PixelData$$serializer() {
    this.descriptor_l6520x$_0 = new SerialClassDescImpl('baaahs.mapper.MappingSession.SurfaceData.PixelData', this);
    this.descriptor.addElement_ivxn3r$('modelPosition', false);
    this.descriptor.addElement_ivxn3r$('screenPosition', false);
    this.descriptor.addElement_ivxn3r$('deltaImage', false);
    MappingSession$SurfaceData$PixelData$$serializer_instance = this;
  }
  Object.defineProperty(MappingSession$SurfaceData$PixelData$$serializer.prototype, 'descriptor', {
    get: function () {
      return this.descriptor_l6520x$_0;
    }
  });
  MappingSession$SurfaceData$PixelData$$serializer.prototype.serialize_awe97i$ = function (encoder, obj) {
    var output = encoder.beginStructure_r0sa6z$(this.descriptor, []);
    output.encodeNullableSerializableElement_orpvvi$(this.descriptor, 0, Vector3F$$serializer_getInstance(), obj.modelPosition);
    output.encodeNullableSerializableElement_orpvvi$(this.descriptor, 1, Vector2F$$serializer_getInstance(), obj.screenPosition);
    output.encodeNullableSerializableElement_orpvvi$(this.descriptor, 2, internal.StringSerializer, obj.deltaImage);
    output.endStructure_qatsm0$(this.descriptor);
  };
  MappingSession$SurfaceData$PixelData$$serializer.prototype.deserialize_nts5qn$ = function (decoder) {
    var index, readAll = false;
    var bitMask0 = 0;
    var local0
    , local1
    , local2;
    var input = decoder.beginStructure_r0sa6z$(this.descriptor, []);
    loopLabel: while (true) {
      index = input.decodeElementIndex_qatsm0$(this.descriptor);
      switch (index) {
        case -2:
          readAll = true;
        case 0:
          local0 = (bitMask0 & 1) === 0 ? input.decodeNullableSerializableElement_cwlm4k$(this.descriptor, 0, Vector3F$$serializer_getInstance()) : input.updateNullableSerializableElement_u33s02$(this.descriptor, 0, Vector3F$$serializer_getInstance(), local0);
          bitMask0 |= 1;
          if (!readAll)
            break;
        case 1:
          local1 = (bitMask0 & 2) === 0 ? input.decodeNullableSerializableElement_cwlm4k$(this.descriptor, 1, Vector2F$$serializer_getInstance()) : input.updateNullableSerializableElement_u33s02$(this.descriptor, 1, Vector2F$$serializer_getInstance(), local1);
          bitMask0 |= 2;
          if (!readAll)
            break;
        case 2:
          local2 = (bitMask0 & 4) === 0 ? input.decodeNullableSerializableElement_cwlm4k$(this.descriptor, 2, internal.StringSerializer) : input.updateNullableSerializableElement_u33s02$(this.descriptor, 2, internal.StringSerializer, local2);
          bitMask0 |= 4;
          if (!readAll)
            break;
        case -1:
          break loopLabel;
        default:throw new UnknownFieldException(index);
      }
    }
    input.endStructure_qatsm0$(this.descriptor);
    return MappingSession$SurfaceData$MappingSession$SurfaceData$PixelData_init(bitMask0, local0, local1, local2, null);
  };
  MappingSession$SurfaceData$PixelData$$serializer.prototype.childSerializers = function () {
    return [new NullableSerializer(Vector3F$$serializer_getInstance()), new NullableSerializer(Vector2F$$serializer_getInstance()), new NullableSerializer(internal.StringSerializer)];
  };
  MappingSession$SurfaceData$PixelData$$serializer.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: '$serializer',
    interfaces: [GeneratedSerializer]
  };
  var MappingSession$SurfaceData$PixelData$$serializer_instance = null;
  function MappingSession$SurfaceData$PixelData$$serializer_getInstance() {
    if (MappingSession$SurfaceData$PixelData$$serializer_instance === null) {
      new MappingSession$SurfaceData$PixelData$$serializer();
    }
    return MappingSession$SurfaceData$PixelData$$serializer_instance;
  }
  function MappingSession$SurfaceData$MappingSession$SurfaceData$PixelData_init(seen1, modelPosition, screenPosition, deltaImage, serializationConstructorMarker) {
    var $this = serializationConstructorMarker || Object.create(MappingSession$SurfaceData$PixelData.prototype);
    if ((seen1 & 1) === 0)
      throw new MissingFieldException('modelPosition');
    else
      $this.modelPosition = modelPosition;
    if ((seen1 & 2) === 0)
      throw new MissingFieldException('screenPosition');
    else
      $this.screenPosition = screenPosition;
    if ((seen1 & 4) === 0)
      throw new MissingFieldException('deltaImage');
    else
      $this.deltaImage = deltaImage;
    return $this;
  }
  MappingSession$SurfaceData$PixelData.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'PixelData',
    interfaces: []
  };
  MappingSession$SurfaceData$PixelData.prototype.component1 = function () {
    return this.modelPosition;
  };
  MappingSession$SurfaceData$PixelData.prototype.component2 = function () {
    return this.screenPosition;
  };
  MappingSession$SurfaceData$PixelData.prototype.component3 = function () {
    return this.deltaImage;
  };
  MappingSession$SurfaceData$PixelData.prototype.copy_tuchvg$ = function (modelPosition, screenPosition, deltaImage) {
    return new MappingSession$SurfaceData$PixelData(modelPosition === void 0 ? this.modelPosition : modelPosition, screenPosition === void 0 ? this.screenPosition : screenPosition, deltaImage === void 0 ? this.deltaImage : deltaImage);
  };
  MappingSession$SurfaceData$PixelData.prototype.toString = function () {
    return 'PixelData(modelPosition=' + Kotlin.toString(this.modelPosition) + (', screenPosition=' + Kotlin.toString(this.screenPosition)) + (', deltaImage=' + Kotlin.toString(this.deltaImage)) + ')';
  };
  MappingSession$SurfaceData$PixelData.prototype.hashCode = function () {
    var result = 0;
    result = result * 31 + Kotlin.hashCode(this.modelPosition) | 0;
    result = result * 31 + Kotlin.hashCode(this.screenPosition) | 0;
    result = result * 31 + Kotlin.hashCode(this.deltaImage) | 0;
    return result;
  };
  MappingSession$SurfaceData$PixelData.prototype.equals = function (other) {
    return this === other || (other !== null && (typeof other === 'object' && (Object.getPrototypeOf(this) === Object.getPrototypeOf(other) && (Kotlin.equals(this.modelPosition, other.modelPosition) && Kotlin.equals(this.screenPosition, other.screenPosition) && Kotlin.equals(this.deltaImage, other.deltaImage)))));
  };
  function MappingSession$SurfaceData$Companion() {
    MappingSession$SurfaceData$Companion_instance = this;
  }
  MappingSession$SurfaceData$Companion.prototype.serializer = function () {
    return MappingSession$SurfaceData$$serializer_getInstance();
  };
  MappingSession$SurfaceData$Companion.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'Companion',
    interfaces: []
  };
  var MappingSession$SurfaceData$Companion_instance = null;
  function MappingSession$SurfaceData$Companion_getInstance() {
    if (MappingSession$SurfaceData$Companion_instance === null) {
      new MappingSession$SurfaceData$Companion();
    }
    return MappingSession$SurfaceData$Companion_instance;
  }
  function MappingSession$SurfaceData$$serializer() {
    this.descriptor_7hc8jj$_0 = new SerialClassDescImpl('baaahs.mapper.MappingSession.SurfaceData', this);
    this.descriptor.addElement_ivxn3r$('brainId', false);
    this.descriptor.addElement_ivxn3r$('panelName', false);
    this.descriptor.addElement_ivxn3r$('pixels', false);
    this.descriptor.addElement_ivxn3r$('deltaImage', false);
    this.descriptor.addElement_ivxn3r$('screenAreaInSqPixels', false);
    this.descriptor.addElement_ivxn3r$('screenAngle', false);
    MappingSession$SurfaceData$$serializer_instance = this;
  }
  Object.defineProperty(MappingSession$SurfaceData$$serializer.prototype, 'descriptor', {
    get: function () {
      return this.descriptor_7hc8jj$_0;
    }
  });
  MappingSession$SurfaceData$$serializer.prototype.serialize_awe97i$ = function (encoder, obj) {
    var output = encoder.beginStructure_r0sa6z$(this.descriptor, []);
    output.encodeStringElement_bgm7zs$(this.descriptor, 0, obj.brainId);
    output.encodeStringElement_bgm7zs$(this.descriptor, 1, obj.panelName);
    output.encodeSerializableElement_blecud$(this.descriptor, 2, new ArrayListSerializer(new NullableSerializer(MappingSession$SurfaceData$PixelData$$serializer_getInstance())), obj.pixels);
    output.encodeNullableSerializableElement_orpvvi$(this.descriptor, 3, internal.StringSerializer, obj.deltaImage);
    output.encodeNullableSerializableElement_orpvvi$(this.descriptor, 4, internal.FloatSerializer, obj.screenAreaInSqPixels);
    output.encodeNullableSerializableElement_orpvvi$(this.descriptor, 5, internal.FloatSerializer, obj.screenAngle);
    output.endStructure_qatsm0$(this.descriptor);
  };
  MappingSession$SurfaceData$$serializer.prototype.deserialize_nts5qn$ = function (decoder) {
    var index, readAll = false;
    var bitMask0 = 0;
    var local0
    , local1
    , local2
    , local3
    , local4
    , local5;
    var input = decoder.beginStructure_r0sa6z$(this.descriptor, []);
    loopLabel: while (true) {
      index = input.decodeElementIndex_qatsm0$(this.descriptor);
      switch (index) {
        case -2:
          readAll = true;
        case 0:
          local0 = input.decodeStringElement_3zr2iy$(this.descriptor, 0);
          bitMask0 |= 1;
          if (!readAll)
            break;
        case 1:
          local1 = input.decodeStringElement_3zr2iy$(this.descriptor, 1);
          bitMask0 |= 2;
          if (!readAll)
            break;
        case 2:
          local2 = (bitMask0 & 4) === 0 ? input.decodeSerializableElement_s44l7r$(this.descriptor, 2, new ArrayListSerializer(new NullableSerializer(MappingSession$SurfaceData$PixelData$$serializer_getInstance()))) : input.updateSerializableElement_ehubvl$(this.descriptor, 2, new ArrayListSerializer(new NullableSerializer(MappingSession$SurfaceData$PixelData$$serializer_getInstance())), local2);
          bitMask0 |= 4;
          if (!readAll)
            break;
        case 3:
          local3 = (bitMask0 & 8) === 0 ? input.decodeNullableSerializableElement_cwlm4k$(this.descriptor, 3, internal.StringSerializer) : input.updateNullableSerializableElement_u33s02$(this.descriptor, 3, internal.StringSerializer, local3);
          bitMask0 |= 8;
          if (!readAll)
            break;
        case 4:
          local4 = (bitMask0 & 16) === 0 ? input.decodeNullableSerializableElement_cwlm4k$(this.descriptor, 4, internal.FloatSerializer) : input.updateNullableSerializableElement_u33s02$(this.descriptor, 4, internal.FloatSerializer, local4);
          bitMask0 |= 16;
          if (!readAll)
            break;
        case 5:
          local5 = (bitMask0 & 32) === 0 ? input.decodeNullableSerializableElement_cwlm4k$(this.descriptor, 5, internal.FloatSerializer) : input.updateNullableSerializableElement_u33s02$(this.descriptor, 5, internal.FloatSerializer, local5);
          bitMask0 |= 32;
          if (!readAll)
            break;
        case -1:
          break loopLabel;
        default:throw new UnknownFieldException(index);
      }
    }
    input.endStructure_qatsm0$(this.descriptor);
    return MappingSession$MappingSession$SurfaceData_init(bitMask0, local0, local1, local2, local3, local4, local5, null);
  };
  MappingSession$SurfaceData$$serializer.prototype.childSerializers = function () {
    return [internal.StringSerializer, internal.StringSerializer, new ArrayListSerializer(new NullableSerializer(MappingSession$SurfaceData$PixelData$$serializer_getInstance())), new NullableSerializer(internal.StringSerializer), new NullableSerializer(internal.FloatSerializer), new NullableSerializer(internal.FloatSerializer)];
  };
  MappingSession$SurfaceData$$serializer.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: '$serializer',
    interfaces: [GeneratedSerializer]
  };
  var MappingSession$SurfaceData$$serializer_instance = null;
  function MappingSession$SurfaceData$$serializer_getInstance() {
    if (MappingSession$SurfaceData$$serializer_instance === null) {
      new MappingSession$SurfaceData$$serializer();
    }
    return MappingSession$SurfaceData$$serializer_instance;
  }
  function MappingSession$MappingSession$SurfaceData_init(seen1, brainId, panelName, pixels, deltaImage, screenAreaInSqPixels, screenAngle, serializationConstructorMarker) {
    var $this = serializationConstructorMarker || Object.create(MappingSession$SurfaceData.prototype);
    if ((seen1 & 1) === 0)
      throw new MissingFieldException('brainId');
    else
      $this.brainId = brainId;
    if ((seen1 & 2) === 0)
      throw new MissingFieldException('panelName');
    else
      $this.panelName = panelName;
    if ((seen1 & 4) === 0)
      throw new MissingFieldException('pixels');
    else
      $this.pixels = pixels;
    if ((seen1 & 8) === 0)
      throw new MissingFieldException('deltaImage');
    else
      $this.deltaImage = deltaImage;
    if ((seen1 & 16) === 0)
      throw new MissingFieldException('screenAreaInSqPixels');
    else
      $this.screenAreaInSqPixels = screenAreaInSqPixels;
    if ((seen1 & 32) === 0)
      throw new MissingFieldException('screenAngle');
    else
      $this.screenAngle = screenAngle;
    return $this;
  }
  MappingSession$SurfaceData.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'SurfaceData',
    interfaces: []
  };
  MappingSession$SurfaceData.prototype.component1 = function () {
    return this.brainId;
  };
  MappingSession$SurfaceData.prototype.component2 = function () {
    return this.panelName;
  };
  MappingSession$SurfaceData.prototype.component3 = function () {
    return this.pixels;
  };
  MappingSession$SurfaceData.prototype.component4 = function () {
    return this.deltaImage;
  };
  MappingSession$SurfaceData.prototype.component5 = function () {
    return this.screenAreaInSqPixels;
  };
  MappingSession$SurfaceData.prototype.component6 = function () {
    return this.screenAngle;
  };
  MappingSession$SurfaceData.prototype.copy_k79yyr$ = function (brainId, panelName, pixels, deltaImage, screenAreaInSqPixels, screenAngle) {
    return new MappingSession$SurfaceData(brainId === void 0 ? this.brainId : brainId, panelName === void 0 ? this.panelName : panelName, pixels === void 0 ? this.pixels : pixels, deltaImage === void 0 ? this.deltaImage : deltaImage, screenAreaInSqPixels === void 0 ? this.screenAreaInSqPixels : screenAreaInSqPixels, screenAngle === void 0 ? this.screenAngle : screenAngle);
  };
  MappingSession$SurfaceData.prototype.toString = function () {
    return 'SurfaceData(brainId=' + Kotlin.toString(this.brainId) + (', panelName=' + Kotlin.toString(this.panelName)) + (', pixels=' + Kotlin.toString(this.pixels)) + (', deltaImage=' + Kotlin.toString(this.deltaImage)) + (', screenAreaInSqPixels=' + Kotlin.toString(this.screenAreaInSqPixels)) + (', screenAngle=' + Kotlin.toString(this.screenAngle)) + ')';
  };
  MappingSession$SurfaceData.prototype.hashCode = function () {
    var result = 0;
    result = result * 31 + Kotlin.hashCode(this.brainId) | 0;
    result = result * 31 + Kotlin.hashCode(this.panelName) | 0;
    result = result * 31 + Kotlin.hashCode(this.pixels) | 0;
    result = result * 31 + Kotlin.hashCode(this.deltaImage) | 0;
    result = result * 31 + Kotlin.hashCode(this.screenAreaInSqPixels) | 0;
    result = result * 31 + Kotlin.hashCode(this.screenAngle) | 0;
    return result;
  };
  MappingSession$SurfaceData.prototype.equals = function (other) {
    return this === other || (other !== null && (typeof other === 'object' && (Object.getPrototypeOf(this) === Object.getPrototypeOf(other) && (Kotlin.equals(this.brainId, other.brainId) && Kotlin.equals(this.panelName, other.panelName) && Kotlin.equals(this.pixels, other.pixels) && Kotlin.equals(this.deltaImage, other.deltaImage) && Kotlin.equals(this.screenAreaInSqPixels, other.screenAreaInSqPixels) && Kotlin.equals(this.screenAngle, other.screenAngle)))));
  };
  function MappingSession$Companion() {
    MappingSession$Companion_instance = this;
  }
  MappingSession$Companion.prototype.serializer = function () {
    return MappingSession$$serializer_getInstance();
  };
  MappingSession$Companion.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'Companion',
    interfaces: []
  };
  var MappingSession$Companion_instance = null;
  function MappingSession$Companion_getInstance() {
    if (MappingSession$Companion_instance === null) {
      new MappingSession$Companion();
    }
    return MappingSession$Companion_instance;
  }
  function MappingSession$$serializer() {
    this.descriptor_5u0ch6$_0 = new SerialClassDescImpl('baaahs.mapper.MappingSession', this);
    this.descriptor.addElement_ivxn3r$('startedAt', false);
    this.descriptor.addElement_ivxn3r$('surfaces', false);
    this.descriptor.addElement_ivxn3r$('cameraMatrix', false);
    this.descriptor.addElement_ivxn3r$('baseImage', false);
    this.descriptor.addElement_ivxn3r$('version', true);
    this.descriptor.addElement_ivxn3r$('savedAt', true);
    this.descriptor.addElement_ivxn3r$('notes', true);
    MappingSession$$serializer_instance = this;
  }
  Object.defineProperty(MappingSession$$serializer.prototype, 'descriptor', {
    get: function () {
      return this.descriptor_5u0ch6$_0;
    }
  });
  MappingSession$$serializer.prototype.serialize_awe97i$ = function (encoder, obj) {
    var output = encoder.beginStructure_r0sa6z$(this.descriptor, []);
    output.encodeDoubleElement_imzr5k$(this.descriptor, 0, obj.startedAt);
    output.encodeSerializableElement_blecud$(this.descriptor, 1, new ArrayListSerializer(MappingSession$SurfaceData$$serializer_getInstance()), obj.surfaces);
    output.encodeSerializableElement_blecud$(this.descriptor, 2, Matrix4$$serializer_getInstance(), obj.cameraMatrix);
    output.encodeNullableSerializableElement_orpvvi$(this.descriptor, 3, internal.StringSerializer, obj.baseImage);
    if (!equals(obj.version, 0) || output.shouldEncodeElementDefault_3zr2iy$(this.descriptor, 4))
      output.encodeIntElement_4wpqag$(this.descriptor, 4, obj.version);
    if (!equals(obj.savedAt, DateTime.Companion.nowUnix()) || output.shouldEncodeElementDefault_3zr2iy$(this.descriptor, 5))
      output.encodeDoubleElement_imzr5k$(this.descriptor, 5, obj.savedAt);
    if (!equals(obj.notes, null) || output.shouldEncodeElementDefault_3zr2iy$(this.descriptor, 6))
      output.encodeNullableSerializableElement_orpvvi$(this.descriptor, 6, internal.StringSerializer, obj.notes);
    output.endStructure_qatsm0$(this.descriptor);
  };
  MappingSession$$serializer.prototype.deserialize_nts5qn$ = function (decoder) {
    var index, readAll = false;
    var bitMask0 = 0;
    var local0
    , local1
    , local2
    , local3
    , local4
    , local5
    , local6;
    var input = decoder.beginStructure_r0sa6z$(this.descriptor, []);
    loopLabel: while (true) {
      index = input.decodeElementIndex_qatsm0$(this.descriptor);
      switch (index) {
        case -2:
          readAll = true;
        case 0:
          local0 = input.decodeDoubleElement_3zr2iy$(this.descriptor, 0);
          bitMask0 |= 1;
          if (!readAll)
            break;
        case 1:
          local1 = (bitMask0 & 2) === 0 ? input.decodeSerializableElement_s44l7r$(this.descriptor, 1, new ArrayListSerializer(MappingSession$SurfaceData$$serializer_getInstance())) : input.updateSerializableElement_ehubvl$(this.descriptor, 1, new ArrayListSerializer(MappingSession$SurfaceData$$serializer_getInstance()), local1);
          bitMask0 |= 2;
          if (!readAll)
            break;
        case 2:
          local2 = (bitMask0 & 4) === 0 ? input.decodeSerializableElement_s44l7r$(this.descriptor, 2, Matrix4$$serializer_getInstance()) : input.updateSerializableElement_ehubvl$(this.descriptor, 2, Matrix4$$serializer_getInstance(), local2);
          bitMask0 |= 4;
          if (!readAll)
            break;
        case 3:
          local3 = (bitMask0 & 8) === 0 ? input.decodeNullableSerializableElement_cwlm4k$(this.descriptor, 3, internal.StringSerializer) : input.updateNullableSerializableElement_u33s02$(this.descriptor, 3, internal.StringSerializer, local3);
          bitMask0 |= 8;
          if (!readAll)
            break;
        case 4:
          local4 = input.decodeIntElement_3zr2iy$(this.descriptor, 4);
          bitMask0 |= 16;
          if (!readAll)
            break;
        case 5:
          local5 = input.decodeDoubleElement_3zr2iy$(this.descriptor, 5);
          bitMask0 |= 32;
          if (!readAll)
            break;
        case 6:
          local6 = (bitMask0 & 64) === 0 ? input.decodeNullableSerializableElement_cwlm4k$(this.descriptor, 6, internal.StringSerializer) : input.updateNullableSerializableElement_u33s02$(this.descriptor, 6, internal.StringSerializer, local6);
          bitMask0 |= 64;
          if (!readAll)
            break;
        case -1:
          break loopLabel;
        default:throw new UnknownFieldException(index);
      }
    }
    input.endStructure_qatsm0$(this.descriptor);
    return MappingSession_init(bitMask0, local0, local1, local2, local3, local4, local5, local6, null);
  };
  MappingSession$$serializer.prototype.childSerializers = function () {
    return [internal.DoubleSerializer, new ArrayListSerializer(MappingSession$SurfaceData$$serializer_getInstance()), Matrix4$$serializer_getInstance(), new NullableSerializer(internal.StringSerializer), internal.IntSerializer, internal.DoubleSerializer, new NullableSerializer(internal.StringSerializer)];
  };
  MappingSession$$serializer.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: '$serializer',
    interfaces: [GeneratedSerializer]
  };
  var MappingSession$$serializer_instance = null;
  function MappingSession$$serializer_getInstance() {
    if (MappingSession$$serializer_instance === null) {
      new MappingSession$$serializer();
    }
    return MappingSession$$serializer_instance;
  }
  function MappingSession_init(seen1, startedAt, surfaces, cameraMatrix, baseImage, version, savedAt, notes, serializationConstructorMarker) {
    var $this = serializationConstructorMarker || Object.create(MappingSession.prototype);
    if ((seen1 & 1) === 0)
      throw new MissingFieldException('startedAt');
    else
      $this.startedAt = startedAt;
    if ((seen1 & 2) === 0)
      throw new MissingFieldException('surfaces');
    else
      $this.surfaces = surfaces;
    if ((seen1 & 4) === 0)
      throw new MissingFieldException('cameraMatrix');
    else
      $this.cameraMatrix = cameraMatrix;
    if ((seen1 & 8) === 0)
      throw new MissingFieldException('baseImage');
    else
      $this.baseImage = baseImage;
    if ((seen1 & 16) === 0)
      $this.version = 0;
    else
      $this.version = version;
    if ((seen1 & 32) === 0)
      $this.savedAt = DateTime.Companion.nowUnix();
    else
      $this.savedAt = savedAt;
    if ((seen1 & 64) === 0)
      $this.notes = null;
    else
      $this.notes = notes;
    return $this;
  }
  MappingSession.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'MappingSession',
    interfaces: []
  };
  MappingSession.prototype.component1 = function () {
    return this.startedAt;
  };
  MappingSession.prototype.component2 = function () {
    return this.surfaces;
  };
  MappingSession.prototype.component3 = function () {
    return this.cameraMatrix;
  };
  MappingSession.prototype.component4 = function () {
    return this.baseImage;
  };
  MappingSession.prototype.component5 = function () {
    return this.version;
  };
  MappingSession.prototype.component6 = function () {
    return this.savedAt;
  };
  MappingSession.prototype.component7 = function () {
    return this.notes;
  };
  MappingSession.prototype.copy_vvpc76$ = function (startedAt, surfaces, cameraMatrix, baseImage, version, savedAt, notes) {
    return new MappingSession(startedAt === void 0 ? this.startedAt : startedAt, surfaces === void 0 ? this.surfaces : surfaces, cameraMatrix === void 0 ? this.cameraMatrix : cameraMatrix, baseImage === void 0 ? this.baseImage : baseImage, version === void 0 ? this.version : version, savedAt === void 0 ? this.savedAt : savedAt, notes === void 0 ? this.notes : notes);
  };
  MappingSession.prototype.toString = function () {
    return 'MappingSession(startedAt=' + Kotlin.toString(this.startedAt) + (', surfaces=' + Kotlin.toString(this.surfaces)) + (', cameraMatrix=' + Kotlin.toString(this.cameraMatrix)) + (', baseImage=' + Kotlin.toString(this.baseImage)) + (', version=' + Kotlin.toString(this.version)) + (', savedAt=' + Kotlin.toString(this.savedAt)) + (', notes=' + Kotlin.toString(this.notes)) + ')';
  };
  MappingSession.prototype.hashCode = function () {
    var result = 0;
    result = result * 31 + Kotlin.hashCode(this.startedAt) | 0;
    result = result * 31 + Kotlin.hashCode(this.surfaces) | 0;
    result = result * 31 + Kotlin.hashCode(this.cameraMatrix) | 0;
    result = result * 31 + Kotlin.hashCode(this.baseImage) | 0;
    result = result * 31 + Kotlin.hashCode(this.version) | 0;
    result = result * 31 + Kotlin.hashCode(this.savedAt) | 0;
    result = result * 31 + Kotlin.hashCode(this.notes) | 0;
    return result;
  };
  MappingSession.prototype.equals = function (other) {
    return this === other || (other !== null && (typeof other === 'object' && (Object.getPrototypeOf(this) === Object.getPrototypeOf(other) && (Kotlin.equals(this.startedAt, other.startedAt) && Kotlin.equals(this.surfaces, other.surfaces) && Kotlin.equals(this.cameraMatrix, other.cameraMatrix) && Kotlin.equals(this.baseImage, other.baseImage) && Kotlin.equals(this.version, other.version) && Kotlin.equals(this.savedAt, other.savedAt) && Kotlin.equals(this.notes, other.notes)))));
  };
  function Storage(fs) {
    Storage$Companion_getInstance();
    this.fs = fs;
  }
  function Storage$Companion() {
    Storage$Companion_instance = this;
    this.json = new Json(JsonConfiguration.Companion.Stable.copy_qyeq5q$(void 0, false));
    this.format_0 = DateFormat.Companion.invoke_61zpoe$("yyyy''MM''dd'-'HH''mm''ss");
  }
  Storage$Companion.prototype.formatDateTime_mw5vjr$ = function (dateTime) {
    return dateTime.format_cgtbg3$(this.format_0);
  };
  Storage$Companion.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'Companion',
    interfaces: []
  };
  var Storage$Companion_instance = null;
  function Storage$Companion_getInstance() {
    if (Storage$Companion_instance === null) {
      new Storage$Companion();
    }
    return Storage$Companion_instance;
  }
  Storage.prototype.listSessions = function () {
    var $receiver = this.fs.listFiles_61zpoe$('mapping-sessions');
    var destination = ArrayList_init();
    var tmp$;
    tmp$ = $receiver.iterator();
    while (tmp$.hasNext()) {
      var element = tmp$.next();
      if (endsWith(element, '.json'))
        destination.add_11rb$(element);
    }
    return destination;
  };
  Storage.prototype.saveSession_x3z8ep$ = function (mappingSession) {
    this.fs.createFile_qz9155$('mapping-sessions/' + Storage$Companion_getInstance().formatDateTime_mw5vjr$(mappingSession.startedAtDateTime) + '-v' + mappingSession.version + '.json', Storage$Companion_getInstance().json.stringify_tf03ej$(MappingSession$Companion_getInstance().serializer(), mappingSession));
  };
  Storage.prototype.saveImage_yzgtim$ = function (name, imageData) {
    this.fs.createFile_7x97xx$('mapping-sessions/images/' + name, imageData);
  };
  Storage.prototype.loadMappingData_ld9ij$ = function (model) {
    var mappingJson = this.fs.loadFile_61zpoe$('mapping-presets.json');
    if (mappingJson != null) {
      return new SessionMappingResults(model, Storage$Companion_getInstance().json.parse_awif5v$(MappingSession$Companion_getInstance().serializer(), mappingJson));
    }
     else {
      return new SessionMappingResults(model, null);
    }
  };
  Storage.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Storage',
    interfaces: []
  };
  function Comparator$ObjectLiteral_0(closure$comparison) {
    this.closure$comparison = closure$comparison;
  }
  Comparator$ObjectLiteral_0.prototype.compare = function (a, b) {
    return this.closure$comparison(a, b);
  };
  Comparator$ObjectLiteral_0.$metadata$ = {kind: Kind_CLASS, interfaces: [Comparator]};
  var compareBy$lambda = wrapFunction(function () {
    var compareValues = Kotlin.kotlin.comparisons.compareValues_s00gnj$;
    return function (closure$selector) {
      return function (a, b) {
        var selector = closure$selector;
        return compareValues(selector(a), selector(b));
      };
    };
  });
  function FragmentingUdpLink(wrappedLink) {
    FragmentingUdpLink$Companion_getInstance();
    this.wrappedLink_0 = wrappedLink;
    this.mtu_0 = this.wrappedLink_0.udpMtu;
    this.nextMessageId_0 = 0;
    this.fragments_0 = ArrayList_init();
  }
  Object.defineProperty(FragmentingUdpLink.prototype, 'myAddress', {
    get: function () {
      return this.wrappedLink_0.myAddress;
    }
  });
  Object.defineProperty(FragmentingUdpLink.prototype, 'udpMtu', {
    get: function () {
      return this.wrappedLink_0.udpMtu;
    }
  });
  function FragmentingUdpLink$Companion() {
    FragmentingUdpLink$Companion_instance = this;
    this.headerSize = 12;
  }
  FragmentingUdpLink$Companion.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'Companion',
    interfaces: []
  };
  var FragmentingUdpLink$Companion_instance = null;
  function FragmentingUdpLink$Companion_getInstance() {
    if (FragmentingUdpLink$Companion_instance === null) {
      new FragmentingUdpLink$Companion();
    }
    return FragmentingUdpLink$Companion_instance;
  }
  function FragmentingUdpLink$Fragment(messageId, offset, bytes) {
    this.messageId = messageId;
    this.offset = offset;
    this.bytes = bytes;
  }
  FragmentingUdpLink$Fragment.prototype.toString = function () {
    return 'Fragment(messageId=' + this.messageId + ', offset=' + this.offset + ', bytes=' + this.bytes.length + ')';
  };
  FragmentingUdpLink$Fragment.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Fragment',
    interfaces: []
  };
  function FragmentingUdpLink$listenUdp$ObjectLiteral(closure$udpListener, this$FragmentingUdpLink) {
    this.closure$udpListener = closure$udpListener;
    this.this$FragmentingUdpLink = this$FragmentingUdpLink;
  }
  FragmentingUdpLink$listenUdp$ObjectLiteral.prototype.receive_ytpeqp$ = function (fromAddress, fromPort, bytes) {
    var reader = new ByteArrayReader(bytes);
    var messageId = reader.readShort();
    var size = reader.readShort();
    var totalSize = reader.readInt();
    var offset = reader.readInt();
    if ((size + 12 | 0) > bytes.length) {
      logger$Companion_getInstance().debug_61zpoe$('Discarding short UDP message: ' + (size + 12 | 0) + ' > ' + bytes.length + ' available');
      return;
    }
    var frameBytes = reader.readNBytes_za3lpa$(size);
    if (offset === 0 && size === totalSize) {
      this.closure$udpListener.receive_ytpeqp$(fromAddress, fromPort, frameBytes);
    }
     else {
      var thisFragment = new FragmentingUdpLink$Fragment(messageId, offset, frameBytes);
      this.this$FragmentingUdpLink;
      this.this$FragmentingUdpLink;
      this.this$FragmentingUdpLink.fragments_0.add_11rb$(thisFragment);
      if ((offset + size | 0) === totalSize) {
        var myFragments = this.this$FragmentingUdpLink.removeMessageId_0(messageId);
        var destination = ArrayList_init_1(collectionSizeOrDefault(myFragments, 10));
        var tmp$;
        tmp$ = myFragments.iterator();
        while (tmp$.hasNext()) {
          var item = tmp$.next();
          destination.add_11rb$(item.bytes.length);
        }
        var iterator = destination.iterator();
        if (!iterator.hasNext())
          throw UnsupportedOperationException_init_0("Empty collection can't be reduced.");
        var accumulator = iterator.next();
        while (iterator.hasNext()) {
          accumulator = accumulator + iterator.next() | 0;
        }
        var actualTotalSize = accumulator;
        if (actualTotalSize === totalSize) {
          var reassembleBytes = new Int8Array(totalSize);
          var tmp$_0;
          tmp$_0 = myFragments.iterator();
          while (tmp$_0.hasNext()) {
            var element = tmp$_0.next();
            var $receiver = element.bytes;
            arrayCopy($receiver, reassembleBytes, element.offset, 0, $receiver.length);
          }
          this.closure$udpListener.receive_ytpeqp$(fromAddress, fromPort, reassembleBytes);
        }
         else {
          var tmp$_1 = logger$Companion_getInstance();
          var tmp$_2 = 'incomplete fragmented UDP packet from ' + fromAddress + ':' + fromPort + ':' + (' actualTotalSize=' + actualTotalSize + ' != totalSize=' + totalSize) + (' for messageId=' + messageId);
          var destination_0 = ArrayList_init_1(collectionSizeOrDefault(myFragments, 10));
          var tmp$_3;
          tmp$_3 = myFragments.iterator();
          while (tmp$_3.hasNext()) {
            var item_0 = tmp$_3.next();
            destination_0.add_11rb$(item_0.bytes.length);
          }
          tmp$_1.warn_61zpoe$(tmp$_2 + (' (have ' + joinToString(destination_0, ',') + ')'));
          this.this$FragmentingUdpLink;
          this.this$FragmentingUdpLink;
          this.this$FragmentingUdpLink.fragments_0.addAll_brywnq$(myFragments);
        }
      }
    }
  };
  FragmentingUdpLink$listenUdp$ObjectLiteral.$metadata$ = {
    kind: Kind_CLASS,
    interfaces: [Network$UdpListener]
  };
  FragmentingUdpLink.prototype.listenUdp_a6m852$ = function (port, udpListener) {
    return new FragmentingUdpLink$FragmentingUdpSocket(this, this.wrappedLink_0.listenUdp_a6m852$(port, new FragmentingUdpLink$listenUdp$ObjectLiteral(udpListener, this)));
  };
  function FragmentingUdpLink$removeMessageId$lambda$lambda(closure$messageId, closure$myFragments) {
    return function (fragment) {
      var remove = fragment.messageId === closure$messageId;
      if (remove)
        closure$myFragments.add_11rb$(fragment);
      return remove;
    };
  }
  function FragmentingUdpLink$removeMessageId$lambda$lambda_0(closure$offsets, closure$myFragments) {
    return function (fragment) {
      var alreadyThere = !closure$offsets.add_11rb$(fragment.offset);
      if (alreadyThere) {
        println('already there: ' + fragment);
        println('from: ' + closure$myFragments);
      }
      return alreadyThere;
    };
  }
  function FragmentingUdpLink$removeMessageId$lambda(it) {
    return it.offset;
  }
  FragmentingUdpLink.prototype.removeMessageId_0 = function (messageId) {
    var myFragments = ArrayList_init();
    removeAll(this.fragments_0, FragmentingUdpLink$removeMessageId$lambda$lambda(messageId, myFragments));
    var offsets = HashSet_init();
    removeAll(myFragments, FragmentingUdpLink$removeMessageId$lambda$lambda_0(offsets, myFragments));
    if (myFragments.isEmpty()) {
      println('remaining fragments = ' + this.fragments_0);
    }
    return sortedWith(myFragments, new Comparator$ObjectLiteral_0(compareBy$lambda(FragmentingUdpLink$removeMessageId$lambda)));
  };
  function FragmentingUdpLink$FragmentingUdpSocket($outer, delegate) {
    this.$outer = $outer;
    this.delegate_0 = delegate;
  }
  Object.defineProperty(FragmentingUdpLink$FragmentingUdpSocket.prototype, 'serverPort', {
    get: function () {
      return this.delegate_0.serverPort;
    }
  });
  function FragmentingUdpLink$FragmentingUdpSocket$sendUdp$lambda(this$FragmentingUdpSocket, closure$toAddress, closure$port) {
    return function (fragment) {
      this$FragmentingUdpSocket.delegate_0.sendUdp_ytpeqp$(closure$toAddress, closure$port, fragment);
      return Unit;
    };
  }
  FragmentingUdpLink$FragmentingUdpSocket.prototype.sendUdp_ytpeqp$ = function (toAddress, port, bytes) {
    this.transmitMultipartUdp_0(bytes, FragmentingUdpLink$FragmentingUdpSocket$sendUdp$lambda(this, toAddress, port));
  };
  function FragmentingUdpLink$FragmentingUdpSocket$broadcastUdp$lambda(this$FragmentingUdpSocket, closure$port) {
    return function (fragment) {
      this$FragmentingUdpSocket.delegate_0.broadcastUdp_3fbn1q$(closure$port, fragment);
      return Unit;
    };
  }
  FragmentingUdpLink$FragmentingUdpSocket.prototype.broadcastUdp_3fbn1q$ = function (port, bytes) {
    this.transmitMultipartUdp_0(bytes, FragmentingUdpLink$FragmentingUdpSocket$broadcastUdp$lambda(this, port));
  };
  FragmentingUdpLink$FragmentingUdpSocket.prototype.transmitMultipartUdp_0 = function (bytes, fn) {
    var tmp$;
    var maxSize = 131070;
    if (bytes.length > maxSize) {
      throw IllegalArgumentException_init('buffer too big! ' + bytes.length + ' must be < ' + maxSize);
    }
    var messageId = (tmp$ = this.$outer.nextMessageId_0, this.$outer.nextMessageId_0 = toShort(tmp$ + 1), tmp$);
    var messageCount = ((bytes.length - 1 | 0) / (this.$outer.mtu_0 - 12 | 0) | 0) + 1 | 0;
    var buf = new Int8Array(this.$outer.mtu_0);
    var offset = 0;
    for (var i = 0; i < messageCount; i++) {
      var writer = new ByteArrayWriter(buf);
      var a = this.$outer.mtu_0 - 12 | 0;
      var b = bytes.length - offset | 0;
      var thisFrameSize = Math_0.min(a, b);
      writer.writeShort_mq22fl$(messageId);
      writer.writeShort_mq22fl$(toShort(thisFrameSize));
      writer.writeInt_za3lpa$(bytes.length);
      writer.writeInt_za3lpa$(offset);
      writer.writeNBytes_mj6st8$(bytes, offset, offset + thisFrameSize | 0);
      fn(writer.toBytes());
      offset = offset + thisFrameSize | 0;
    }
  };
  FragmentingUdpLink$FragmentingUdpSocket.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'FragmentingUdpSocket',
    interfaces: [Network$UdpSocket]
  };
  FragmentingUdpLink.prototype.startHttpServer_za3lpa$ = function (port) {
    return this.wrappedLink_0.startHttpServer_za3lpa$(port);
  };
  FragmentingUdpLink.prototype.connectWebSocket_t0j9bj$ = function (toAddress, port, path, webSocketListener) {
    return this.wrappedLink_0.connectWebSocket_t0j9bj$(toAddress, port, path, webSocketListener);
  };
  FragmentingUdpLink.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'FragmentingUdpLink',
    interfaces: [Network$Link]
  };
  function Network() {
  }
  function Network$Link() {
  }
  Network$Link.$metadata$ = {
    kind: Kind_INTERFACE,
    simpleName: 'Link',
    interfaces: []
  };
  function Network$Address() {
  }
  Network$Address.$metadata$ = {
    kind: Kind_INTERFACE,
    simpleName: 'Address',
    interfaces: []
  };
  function Network$UdpListener() {
  }
  Network$UdpListener.$metadata$ = {
    kind: Kind_INTERFACE,
    simpleName: 'UdpListener',
    interfaces: []
  };
  function Network$UdpSocket() {
  }
  Network$UdpSocket.prototype.sendUdp_wpmaqi$ = function (toAddress, port, message) {
    this.sendUdp_ytpeqp$(toAddress, port, message.toBytes());
  };
  Network$UdpSocket.prototype.broadcastUdp_68hu5j$ = function (port, message) {
    this.broadcastUdp_3fbn1q$(port, message.toBytes());
  };
  Network$UdpSocket.$metadata$ = {
    kind: Kind_INTERFACE,
    simpleName: 'UdpSocket',
    interfaces: []
  };
  function Network$TcpConnection() {
  }
  Network$TcpConnection.prototype.send_chrig3$ = function (message) {
    this.send_fqrh44$(message.toBytes());
  };
  Network$TcpConnection.$metadata$ = {
    kind: Kind_INTERFACE,
    simpleName: 'TcpConnection',
    interfaces: []
  };
  function Network$HttpServer() {
  }
  function Network$HttpServer$listenWebSocket$lambda(closure$webSocketListener) {
    return function (it) {
      return closure$webSocketListener;
    };
  }
  Network$HttpServer.prototype.listenWebSocket_w9i1ik$ = function (path, webSocketListener) {
    this.listenWebSocket_brdh44$(path, Network$HttpServer$listenWebSocket$lambda(webSocketListener));
  };
  Network$HttpServer.$metadata$ = {
    kind: Kind_INTERFACE,
    simpleName: 'HttpServer',
    interfaces: []
  };
  function Network$WebSocketListener() {
  }
  Network$WebSocketListener.$metadata$ = {
    kind: Kind_INTERFACE,
    simpleName: 'WebSocketListener',
    interfaces: []
  };
  function Network$UdpProxy() {
    Network$UdpProxy_instance = this;
    this.BROADCAST_OP = toBoxedChar(66);
    this.LISTEN_OP = toBoxedChar(76);
    this.SEND_OP = toBoxedChar(83);
    this.RECEIVE_OP = toBoxedChar(82);
  }
  Network$UdpProxy.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'UdpProxy',
    interfaces: []
  };
  var Network$UdpProxy_instance = null;
  function Network$UdpProxy_getInstance() {
    if (Network$UdpProxy_instance === null) {
      new Network$UdpProxy();
    }
    return Network$UdpProxy_instance;
  }
  Network.$metadata$ = {
    kind: Kind_INTERFACE,
    simpleName: 'Network',
    interfaces: []
  };
  function Ports() {
    Ports_instance = this;
    this.PINKY = 8002;
    this.BRAIN = 8003;
    this.PINKY_UI_TCP = 8004;
    this.PINKY_MAPPER_TCP = 8005;
    this.SIMULATOR_BRIDGE_TCP = 8006;
  }
  Ports.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'Ports',
    interfaces: []
  };
  var Ports_instance = null;
  function Ports_getInstance() {
    if (Ports_instance === null) {
      new Ports();
    }
    return Ports_instance;
  }
  function Type(name, ordinal) {
    Enum.call(this);
    this.name$ = name;
    this.ordinal$ = ordinal;
  }
  function Type_initFields() {
    Type_initFields = function () {
    };
    Type$BRAIN_HELLO_instance = new Type('BRAIN_HELLO', 0);
    Type$BRAIN_PANEL_SHADE_instance = new Type('BRAIN_PANEL_SHADE', 1);
    Type$MAPPER_HELLO_instance = new Type('MAPPER_HELLO', 2);
    Type$BRAIN_ID_REQUEST_instance = new Type('BRAIN_ID_REQUEST', 3);
    Type$BRAIN_MAPPING_instance = new Type('BRAIN_MAPPING', 4);
    Type$PING_instance = new Type('PING', 5);
    Type$USE_FIRMWARE_instance = new Type('USE_FIRMWARE', 6);
    Type$Companion_getInstance();
  }
  var Type$BRAIN_HELLO_instance;
  function Type$BRAIN_HELLO_getInstance() {
    Type_initFields();
    return Type$BRAIN_HELLO_instance;
  }
  var Type$BRAIN_PANEL_SHADE_instance;
  function Type$BRAIN_PANEL_SHADE_getInstance() {
    Type_initFields();
    return Type$BRAIN_PANEL_SHADE_instance;
  }
  var Type$MAPPER_HELLO_instance;
  function Type$MAPPER_HELLO_getInstance() {
    Type_initFields();
    return Type$MAPPER_HELLO_instance;
  }
  var Type$BRAIN_ID_REQUEST_instance;
  function Type$BRAIN_ID_REQUEST_getInstance() {
    Type_initFields();
    return Type$BRAIN_ID_REQUEST_instance;
  }
  var Type$BRAIN_MAPPING_instance;
  function Type$BRAIN_MAPPING_getInstance() {
    Type_initFields();
    return Type$BRAIN_MAPPING_instance;
  }
  var Type$PING_instance;
  function Type$PING_getInstance() {
    Type_initFields();
    return Type$PING_instance;
  }
  var Type$USE_FIRMWARE_instance;
  function Type$USE_FIRMWARE_getInstance() {
    Type_initFields();
    return Type$USE_FIRMWARE_instance;
  }
  function Type$Companion() {
    Type$Companion_instance = this;
    this.values = Type$values();
  }
  Type$Companion.prototype.get_s8j3t7$ = function (i) {
    return this.values[i];
  };
  Type$Companion.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'Companion',
    interfaces: []
  };
  var Type$Companion_instance = null;
  function Type$Companion_getInstance() {
    Type_initFields();
    if (Type$Companion_instance === null) {
      new Type$Companion();
    }
    return Type$Companion_instance;
  }
  Type.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Type',
    interfaces: [Enum]
  };
  function Type$values() {
    return [Type$BRAIN_HELLO_getInstance(), Type$BRAIN_PANEL_SHADE_getInstance(), Type$MAPPER_HELLO_getInstance(), Type$BRAIN_ID_REQUEST_getInstance(), Type$BRAIN_MAPPING_getInstance(), Type$PING_getInstance(), Type$USE_FIRMWARE_getInstance()];
  }
  Type.values = Type$values;
  function Type$valueOf(name) {
    switch (name) {
      case 'BRAIN_HELLO':
        return Type$BRAIN_HELLO_getInstance();
      case 'BRAIN_PANEL_SHADE':
        return Type$BRAIN_PANEL_SHADE_getInstance();
      case 'MAPPER_HELLO':
        return Type$MAPPER_HELLO_getInstance();
      case 'BRAIN_ID_REQUEST':
        return Type$BRAIN_ID_REQUEST_getInstance();
      case 'BRAIN_MAPPING':
        return Type$BRAIN_MAPPING_getInstance();
      case 'PING':
        return Type$PING_getInstance();
      case 'USE_FIRMWARE':
        return Type$USE_FIRMWARE_getInstance();
      default:throwISE('No enum constant baaahs.proto.Type.' + name);
    }
  }
  Type.valueOf_61zpoe$ = Type$valueOf;
  function parse(bytes) {
    var tmp$;
    var reader = new ByteArrayReader(bytes);
    switch (Type$Companion_getInstance().get_s8j3t7$(reader.readByte()).name) {
      case 'BRAIN_HELLO':
        tmp$ = BrainHelloMessage$Companion_getInstance().parse_100t80$(reader);
        break;
      case 'BRAIN_PANEL_SHADE':
        tmp$ = BrainShaderMessage$Companion_getInstance().parse_100t80$(reader);
        break;
      case 'MAPPER_HELLO':
        tmp$ = MapperHelloMessage$Companion_getInstance().parse_100t80$(reader);
        break;
      case 'BRAIN_ID_REQUEST':
        tmp$ = BrainIdRequest$Companion_getInstance().parse_100t80$(reader);
        break;
      case 'BRAIN_MAPPING':
        tmp$ = BrainMappingMessage$Companion_getInstance().parse_100t80$(reader);
        break;
      case 'PING':
        tmp$ = PingMessage$Companion_getInstance().parse_100t80$(reader);
        break;
      case 'USE_FIRMWARE':
        tmp$ = UseFirmwareMessage$Companion_getInstance().parse_100t80$(reader);
        break;
      default:tmp$ = Kotlin.noWhenBranchMatched();
        break;
    }
    return tmp$;
  }
  function BrainHelloMessage(brainId, surfaceName, firmwareVersion, idfVersion) {
    BrainHelloMessage$Companion_getInstance();
    if (firmwareVersion === void 0)
      firmwareVersion = null;
    if (idfVersion === void 0)
      idfVersion = null;
    Message.call(this, Type$BRAIN_HELLO_getInstance());
    this.brainId = brainId;
    this.surfaceName = surfaceName;
    this.firmwareVersion = firmwareVersion;
    this.idfVersion = idfVersion;
  }
  function BrainHelloMessage$Companion() {
    BrainHelloMessage$Companion_instance = this;
  }
  BrainHelloMessage$Companion.prototype.parse_100t80$ = function (reader) {
    return new BrainHelloMessage(reader.readString(), reader.readNullableString(), reader.readNullableString(), reader.readNullableString());
  };
  BrainHelloMessage$Companion.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'Companion',
    interfaces: []
  };
  var BrainHelloMessage$Companion_instance = null;
  function BrainHelloMessage$Companion_getInstance() {
    if (BrainHelloMessage$Companion_instance === null) {
      new BrainHelloMessage$Companion();
    }
    return BrainHelloMessage$Companion_instance;
  }
  BrainHelloMessage.prototype.serialize_3kjoo0$ = function (writer) {
    writer.writeString_61zpoe$(this.brainId);
    writer.writeNullableString_pdl1vj$(this.surfaceName);
    writer.writeNullableString_pdl1vj$(this.firmwareVersion);
    writer.writeNullableString_pdl1vj$(this.idfVersion);
  };
  BrainHelloMessage.prototype.toString = function () {
    return 'BrainHello ' + this.brainId + ', ' + toString_0(this.surfaceName) + ', ' + toString_0(this.firmwareVersion) + ', ' + toString_0(this.idfVersion);
  };
  BrainHelloMessage.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'BrainHelloMessage',
    interfaces: [Message]
  };
  function BrainShaderMessage(shader, buffer, pongData) {
    BrainShaderMessage$Companion_getInstance();
    if (pongData === void 0)
      pongData = null;
    Message.call(this, Type$BRAIN_PANEL_SHADE_getInstance());
    this.shader = shader;
    this.buffer = buffer;
    this.pongData = pongData;
  }
  function BrainShaderMessage$Companion() {
    BrainShaderMessage$Companion_instance = this;
  }
  BrainShaderMessage$Companion.prototype.parse_100t80$ = function (reader) {
    var pongData = reader.readBoolean() ? reader.readBytes() : null;
    var shaderDesc = reader.readBytes();
    var shader = Shader$Companion_getInstance().parse_100t80$(new ByteArrayReader(shaderDesc));
    var buffer = shader.readBuffer_100t80$(reader);
    return new BrainShaderMessage(shader, buffer, pongData);
  };
  BrainShaderMessage$Companion.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'Companion',
    interfaces: []
  };
  var BrainShaderMessage$Companion_instance = null;
  function BrainShaderMessage$Companion_getInstance() {
    if (BrainShaderMessage$Companion_instance === null) {
      new BrainShaderMessage$Companion();
    }
    return BrainShaderMessage$Companion_instance;
  }
  BrainShaderMessage.prototype.serialize_3kjoo0$ = function (writer) {
    writer.writeBoolean_6taknv$(this.pongData != null);
    if (this.pongData != null)
      writer.writeBytes_mj6st8$(this.pongData);
    writer.writeBytes_mj6st8$(this.shader.descriptorBytes);
    this.buffer.serialize_3kjoo0$(writer);
  };
  BrainShaderMessage.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'BrainShaderMessage',
    interfaces: [Message]
  };
  function UseFirmwareMessage(url) {
    UseFirmwareMessage$Companion_getInstance();
    Message.call(this, Type$USE_FIRMWARE_getInstance());
    this.url = url;
  }
  function UseFirmwareMessage$Companion() {
    UseFirmwareMessage$Companion_instance = this;
  }
  UseFirmwareMessage$Companion.prototype.parse_100t80$ = function (reader) {
    return new UseFirmwareMessage(reader.readString());
  };
  UseFirmwareMessage$Companion.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'Companion',
    interfaces: []
  };
  var UseFirmwareMessage$Companion_instance = null;
  function UseFirmwareMessage$Companion_getInstance() {
    if (UseFirmwareMessage$Companion_instance === null) {
      new UseFirmwareMessage$Companion();
    }
    return UseFirmwareMessage$Companion_instance;
  }
  UseFirmwareMessage.prototype.serialize_3kjoo0$ = function (writer) {
    writer.writeString_61zpoe$(this.url);
  };
  UseFirmwareMessage.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'UseFirmwareMessage',
    interfaces: [Message]
  };
  function MapperHelloMessage(isRunning) {
    MapperHelloMessage$Companion_getInstance();
    Message.call(this, Type$MAPPER_HELLO_getInstance());
    this.isRunning = isRunning;
  }
  function MapperHelloMessage$Companion() {
    MapperHelloMessage$Companion_instance = this;
  }
  MapperHelloMessage$Companion.prototype.parse_100t80$ = function (reader) {
    return new MapperHelloMessage(reader.readBoolean());
  };
  MapperHelloMessage$Companion.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'Companion',
    interfaces: []
  };
  var MapperHelloMessage$Companion_instance = null;
  function MapperHelloMessage$Companion_getInstance() {
    if (MapperHelloMessage$Companion_instance === null) {
      new MapperHelloMessage$Companion();
    }
    return MapperHelloMessage$Companion_instance;
  }
  MapperHelloMessage.prototype.serialize_3kjoo0$ = function (writer) {
    writer.writeBoolean_6taknv$(this.isRunning);
  };
  MapperHelloMessage.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'MapperHelloMessage',
    interfaces: [Message]
  };
  function BrainIdRequest() {
    BrainIdRequest$Companion_getInstance();
    Message.call(this, Type$BRAIN_ID_REQUEST_getInstance());
  }
  function BrainIdRequest$Companion() {
    BrainIdRequest$Companion_instance = this;
  }
  BrainIdRequest$Companion.prototype.parse_100t80$ = function (reader) {
    return new BrainIdRequest();
  };
  BrainIdRequest$Companion.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'Companion',
    interfaces: []
  };
  var BrainIdRequest$Companion_instance = null;
  function BrainIdRequest$Companion_getInstance() {
    if (BrainIdRequest$Companion_instance === null) {
      new BrainIdRequest$Companion();
    }
    return BrainIdRequest$Companion_instance;
  }
  BrainIdRequest.prototype.serialize_3kjoo0$ = function (writer) {
  };
  BrainIdRequest.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'BrainIdRequest',
    interfaces: [Message]
  };
  function BrainMappingMessage(brainId, surfaceName, uvMapName, panelUvTopLeft, panelUvBottomRight, pixelCount, pixelVertices) {
    BrainMappingMessage$Companion_getInstance();
    Message.call(this, Type$BRAIN_MAPPING_getInstance());
    this.brainId = brainId;
    this.surfaceName = surfaceName;
    this.uvMapName = uvMapName;
    this.panelUvTopLeft = panelUvTopLeft;
    this.panelUvBottomRight = panelUvBottomRight;
    this.pixelCount = pixelCount;
    this.pixelVertices = pixelVertices;
  }
  function BrainMappingMessage$Companion() {
    BrainMappingMessage$Companion_instance = this;
  }
  BrainMappingMessage$Companion.prototype.parse_100t80$ = function (reader) {
    return new BrainMappingMessage(new BrainId(reader.readString()), reader.readNullableString(), reader.readNullableString(), this.readVector2F_0(reader), this.readVector2F_0(reader), reader.readInt(), this.readRelativeVerticesList_0(reader));
  };
  BrainMappingMessage$Companion.prototype.readVector2F_0 = function ($receiver) {
    return new Vector2F($receiver.readFloat(), $receiver.readFloat());
  };
  BrainMappingMessage$Companion.prototype.writeVector2F_0 = function ($receiver, v) {
    $receiver.writeFloat_mx4ult$(v.x);
    $receiver.writeFloat_mx4ult$(v.y);
  };
  BrainMappingMessage$Companion.prototype.readRelativeVerticesList_0 = function ($receiver) {
    var vertexCount = $receiver.readInt();
    var $receiver_0 = until(0, vertexCount);
    var destination = ArrayList_init_1(collectionSizeOrDefault($receiver_0, 10));
    var tmp$;
    tmp$ = $receiver_0.iterator();
    while (tmp$.hasNext()) {
      var item = tmp$.next();
      destination.add_11rb$(new Vector2F($receiver.readShort() / 65536.0, $receiver.readShort() / 65536.0));
    }
    return destination;
  };
  BrainMappingMessage$Companion.prototype.writeRelativeVerticesList_0 = function ($receiver, pixelVertices) {
    $receiver.writeInt_za3lpa$(pixelVertices.size);
    var tmp$;
    tmp$ = pixelVertices.iterator();
    while (tmp$.hasNext()) {
      var element = tmp$.next();
      $receiver.writeShort_mq22fl$(toShort(numberToInt(element.x * 65536)));
      $receiver.writeShort_mq22fl$(toShort(numberToInt(element.y * 65536)));
    }
  };
  BrainMappingMessage$Companion.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'Companion',
    interfaces: []
  };
  var BrainMappingMessage$Companion_instance = null;
  function BrainMappingMessage$Companion_getInstance() {
    if (BrainMappingMessage$Companion_instance === null) {
      new BrainMappingMessage$Companion();
    }
    return BrainMappingMessage$Companion_instance;
  }
  BrainMappingMessage.prototype.serialize_3kjoo0$ = function (writer) {
    writer.writeString_61zpoe$(this.brainId.uuid);
    writer.writeNullableString_pdl1vj$(this.surfaceName);
    writer.writeNullableString_pdl1vj$(this.uvMapName);
    BrainMappingMessage$Companion_getInstance().writeVector2F_0(writer, this.panelUvTopLeft);
    BrainMappingMessage$Companion_getInstance().writeVector2F_0(writer, this.panelUvBottomRight);
    writer.writeInt_za3lpa$(this.pixelCount);
    var vertexCount = this.pixelVertices.size;
    writer.writeInt_za3lpa$(vertexCount);
    BrainMappingMessage$Companion_getInstance().writeRelativeVerticesList_0(writer, this.pixelVertices);
  };
  BrainMappingMessage.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'BrainMappingMessage',
    interfaces: [Message]
  };
  function PingMessage(data, isPong) {
    PingMessage$Companion_getInstance();
    if (isPong === void 0)
      isPong = false;
    Message.call(this, Type$PING_getInstance());
    this.data = data;
    this.isPong = isPong;
  }
  function PingMessage$Companion() {
    PingMessage$Companion_instance = this;
  }
  PingMessage$Companion.prototype.parse_100t80$ = function (reader) {
    var isPong = reader.readBoolean();
    var data = reader.readBytes();
    return new PingMessage(data, isPong);
  };
  PingMessage$Companion.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'Companion',
    interfaces: []
  };
  var PingMessage$Companion_instance = null;
  function PingMessage$Companion_getInstance() {
    if (PingMessage$Companion_instance === null) {
      new PingMessage$Companion();
    }
    return PingMessage$Companion_instance;
  }
  PingMessage.prototype.serialize_3kjoo0$ = function (writer) {
    writer.writeBoolean_6taknv$(this.isPong);
    writer.writeBytes_mj6st8$(this.data);
  };
  PingMessage.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'PingMessage',
    interfaces: [Message]
  };
  function Message(type) {
    this.type = type;
  }
  Message.prototype.toBytes = function () {
    var writer = ByteArrayWriter_init(1 + this.size() | 0);
    writer.writeByte_s8j3t7$(toByte(this.type.ordinal));
    this.serialize_3kjoo0$(writer);
    return writer.toBytes();
  };
  Message.prototype.serialize_3kjoo0$ = function (writer) {
  };
  Message.prototype.size = function () {
    return 127;
  };
  Message.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Message',
    interfaces: []
  };
  function CompositorShader(aShader, bShader) {
    CompositorShader$Companion_getInstance();
    Shader.call(this, ShaderId$COMPOSITOR_getInstance());
    this.aShader = aShader;
    this.bShader = bShader;
  }
  CompositorShader.prototype.createBuffer_ppt8xj$ = function (surface) {
    return new CompositorShader$Buffer(this, this.aShader.createBuffer_ppt8xj$(surface), this.bShader.createBuffer_ppt8xj$(surface));
  };
  CompositorShader.prototype.serializeConfig_3kjoo0$ = function (writer) {
    this.aShader.serialize_3kjoo0$(writer);
    this.bShader.serialize_3kjoo0$(writer);
  };
  CompositorShader.prototype.createRenderer_omlfoo$ = function (surface, renderContext) {
    var rendererA = this.aShader.createRenderer_omlfoo$(surface, renderContext);
    var rendererB = this.bShader.createRenderer_omlfoo$(surface, renderContext);
    return new CompositorShader$Renderer(rendererA, rendererB);
  };
  CompositorShader.prototype.createRenderer_ppt8xj$ = function (surface) {
    var rendererA = this.aShader.createRenderer_ppt8xj$(surface);
    var rendererB = this.bShader.createRenderer_ppt8xj$(surface);
    return new CompositorShader$Renderer(rendererA, rendererB);
  };
  CompositorShader.prototype.readBuffer_100t80$ = function (reader) {
    return new CompositorShader$Buffer(this, this.aShader.readBuffer_100t80$(reader), this.bShader.readBuffer_100t80$(reader), CompositingMode$Companion_getInstance().get_s8j3t7$(reader.readByte()), reader.readFloat());
  };
  CompositorShader.prototype.createBuffer_ytrflg$ = function (bufferA, bufferB) {
    return new CompositorShader$Buffer(this, bufferA, bufferB);
  };
  function CompositorShader$Companion() {
    CompositorShader$Companion_instance = this;
  }
  CompositorShader$Companion.prototype.parse_100t80$ = function (reader) {
    var shaderA = Shader$Companion_getInstance().parse_100t80$(reader);
    var shaderB = Shader$Companion_getInstance().parse_100t80$(reader);
    return new CompositorShader(shaderA, shaderB);
  };
  CompositorShader$Companion.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'Companion',
    interfaces: [ShaderReader]
  };
  var CompositorShader$Companion_instance = null;
  function CompositorShader$Companion_getInstance() {
    if (CompositorShader$Companion_instance === null) {
      new CompositorShader$Companion();
    }
    return CompositorShader$Companion_instance;
  }
  function CompositorShader$Buffer($outer, bufferA, bufferB, mode, fade) {
    this.$outer = $outer;
    if (mode === void 0)
      mode = CompositingMode$NORMAL_getInstance();
    if (fade === void 0)
      fade = 0.5;
    this.bufferA = bufferA;
    this.bufferB = bufferB;
    this.mode = mode;
    this.fade = fade;
    this.shader_20svf$_0 = this.$outer;
  }
  Object.defineProperty(CompositorShader$Buffer.prototype, 'shader', {
    get: function () {
      return this.shader_20svf$_0;
    }
  });
  CompositorShader$Buffer.prototype.serialize_3kjoo0$ = function (writer) {
    this.bufferA.serialize_3kjoo0$(writer);
    this.bufferB.serialize_3kjoo0$(writer);
    writer.writeByte_s8j3t7$(toByte(this.mode.ordinal));
    writer.writeFloat_mx4ult$(this.fade);
  };
  CompositorShader$Buffer.prototype.read_100t80$ = function (reader) {
    this.bufferA.read_100t80$(reader);
    this.bufferB.read_100t80$(reader);
    this.mode = CompositingMode$Companion_getInstance().get_s8j3t7$(reader.readByte());
    this.fade = reader.readFloat();
  };
  CompositorShader$Buffer.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Buffer',
    interfaces: [Shader$Buffer]
  };
  function CompositorShader$Renderer(rendererA, rendererB) {
    this.rendererA_0 = rendererA;
    this.rendererB_0 = rendererB;
  }
  CompositorShader$Renderer.prototype.beginFrame_b23bvv$ = function (buffer, pixelCount) {
    var tmp$, tmp$_0;
    this.rendererA_0.beginFrame_b23bvv$(Kotlin.isType(tmp$ = buffer.bufferA, Shader$Buffer) ? tmp$ : throwCCE(), pixelCount);
    this.rendererB_0.beginFrame_b23bvv$(Kotlin.isType(tmp$_0 = buffer.bufferB, Shader$Buffer) ? tmp$_0 : throwCCE(), pixelCount);
  };
  CompositorShader$Renderer.prototype.draw_b23bvv$ = function (buffer, pixelIndex) {
    var tmp$, tmp$_0;
    var dest = this.rendererA_0.draw_b23bvv$(Kotlin.isType(tmp$ = buffer.bufferA, Shader$Buffer) ? tmp$ : throwCCE(), pixelIndex);
    var src = this.rendererB_0.draw_b23bvv$(Kotlin.isType(tmp$_0 = buffer.bufferB, Shader$Buffer) ? tmp$_0 : throwCCE(), pixelIndex);
    return dest.fade_6zkv30$(buffer.mode.composite_dggbqs$(src, dest), buffer.fade);
  };
  CompositorShader$Renderer.prototype.endFrame = function () {
    this.rendererA_0.endFrame();
    this.rendererB_0.endFrame();
  };
  CompositorShader$Renderer.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Renderer',
    interfaces: [Shader$Renderer]
  };
  CompositorShader.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'CompositorShader',
    interfaces: [Shader]
  };
  function CompositingMode(name, ordinal) {
    Enum.call(this);
    this.name$ = name;
    this.ordinal$ = ordinal;
  }
  function CompositingMode_initFields() {
    CompositingMode_initFields = function () {
    };
    new CompositingMode$NORMAL();
    new CompositingMode$ADD();
    CompositingMode$Companion_getInstance();
  }
  function CompositingMode$NORMAL() {
    CompositingMode$NORMAL_instance = this;
    CompositingMode.call(this, 'NORMAL', 0);
  }
  CompositingMode$NORMAL.prototype.composite_dggbqs$ = function (src, dest) {
    return src;
  };
  CompositingMode$NORMAL.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'NORMAL',
    interfaces: [CompositingMode]
  };
  var CompositingMode$NORMAL_instance = null;
  function CompositingMode$NORMAL_getInstance() {
    CompositingMode_initFields();
    return CompositingMode$NORMAL_instance;
  }
  function CompositingMode$ADD() {
    CompositingMode$ADD_instance = this;
    CompositingMode.call(this, 'ADD', 1);
  }
  CompositingMode$ADD.prototype.composite_dggbqs$ = function (src, dest) {
    return dest.plus_rny0jj$(src);
  };
  CompositingMode$ADD.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'ADD',
    interfaces: [CompositingMode]
  };
  var CompositingMode$ADD_instance = null;
  function CompositingMode$ADD_getInstance() {
    CompositingMode_initFields();
    return CompositingMode$ADD_instance;
  }
  function CompositingMode$Companion() {
    CompositingMode$Companion_instance = this;
    this.values = CompositingMode$values();
  }
  CompositingMode$Companion.prototype.get_s8j3t7$ = function (i) {
    return this.values[i];
  };
  CompositingMode$Companion.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'Companion',
    interfaces: []
  };
  var CompositingMode$Companion_instance = null;
  function CompositingMode$Companion_getInstance() {
    CompositingMode_initFields();
    if (CompositingMode$Companion_instance === null) {
      new CompositingMode$Companion();
    }
    return CompositingMode$Companion_instance;
  }
  CompositingMode.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'CompositingMode',
    interfaces: [Enum]
  };
  function CompositingMode$values() {
    return [CompositingMode$NORMAL_getInstance(), CompositingMode$ADD_getInstance()];
  }
  CompositingMode.values = CompositingMode$values;
  function CompositingMode$valueOf(name) {
    switch (name) {
      case 'NORMAL':
        return CompositingMode$NORMAL_getInstance();
      case 'ADD':
        return CompositingMode$ADD_getInstance();
      default:throwISE('No enum constant baaahs.shaders.CompositingMode.' + name);
    }
  }
  CompositingMode.valueOf_61zpoe$ = CompositingMode$valueOf;
  function GlslSandbox55301Shader() {
    GlslSandbox55301Shader$Companion_getInstance();
    Shader.call(this, ShaderId$GLSL_SANDBOX_55301_getInstance());
  }
  GlslSandbox55301Shader.prototype.createBuffer_ppt8xj$ = function (surface) {
    return new GlslSandbox55301Shader$Buffer(this);
  };
  GlslSandbox55301Shader.prototype.readBuffer_100t80$ = function (reader) {
    var $receiver = new GlslSandbox55301Shader$Buffer(this);
    $receiver.read_100t80$(reader);
    return $receiver;
  };
  GlslSandbox55301Shader.prototype.createRenderer_ppt8xj$ = function (surface) {
    return new GlslSandbox55301Shader$Renderer(surface);
  };
  function GlslSandbox55301Shader$Companion() {
    GlslSandbox55301Shader$Companion_instance = this;
  }
  GlslSandbox55301Shader$Companion.prototype.parse_100t80$ = function (reader) {
    return new GlslSandbox55301Shader();
  };
  GlslSandbox55301Shader$Companion.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'Companion',
    interfaces: [ShaderReader]
  };
  var GlslSandbox55301Shader$Companion_instance = null;
  function GlslSandbox55301Shader$Companion_getInstance() {
    if (GlslSandbox55301Shader$Companion_instance === null) {
      new GlslSandbox55301Shader$Companion();
    }
    return GlslSandbox55301Shader$Companion_instance;
  }
  function GlslSandbox55301Shader$Buffer($outer) {
    this.$outer = $outer;
  }
  Object.defineProperty(GlslSandbox55301Shader$Buffer.prototype, 'shader', {
    get: function () {
      return this.$outer;
    }
  });
  GlslSandbox55301Shader$Buffer.prototype.serialize_3kjoo0$ = function (writer) {
  };
  GlslSandbox55301Shader$Buffer.prototype.read_100t80$ = function (reader) {
  };
  GlslSandbox55301Shader$Buffer.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Buffer',
    interfaces: [Shader$Buffer]
  };
  function GlslSandbox55301Shader$Renderer(surface) {
    var tmp$, tmp$_0;
    this.pixelVertices_0 = (tmp$_0 = Kotlin.isType(tmp$ = surface, IdentifiedSurface) ? tmp$ : null) != null ? tmp$_0.pixelVertices : null;
  }
  GlslSandbox55301Shader$Renderer.prototype.draw_b23bvv$ = function (buffer, pixelIndex) {
    if (this.pixelVertices_0 == null || pixelIndex >= this.pixelVertices_0.size)
      return Color$Companion_getInstance().BLACK;
    var tmp$ = this.pixelVertices_0.get_za3lpa$(pixelIndex);
    var pixX = tmp$.component1()
    , pixY = tmp$.component2();
    var time = getTimeMillis().toNumber() / 1000.0;
    var resolution = new GlslSandbox55301Shader$Vector2(1.0, 1.0);
    return this.glslSandbox55301_0(new GlslSandbox55301Shader$Vector2(pixX, pixY), resolution, time);
  };
  GlslSandbox55301Shader$Renderer.prototype.glslSandbox55301_0 = function (coord, resolution, time) {
    var v = coord.minus_mx4ult$(resolution.x * 0.5);
    var t = time * 0.4;
    var r = 0.0;
    var N = 6;
    for (var i = 0; i <= 5; i++) {
      var d = 3.1415927 / N * (i * 5.0);
      r += v.length + 0.01;
      var tmp$ = v.x;
      var tmp$_0 = v.y;
      var x = r;
      var x_0 = tmp$_0 + Math_0.cos(x) + d;
      var tmp$_1 = tmp$ + Math_0.cos(x_0) + Math_0.cos(t);
      var tmp$_2 = v.y;
      var tmp$_3 = v.x;
      var x_1 = r;
      var x_2 = tmp$_3 + Math_0.cos(x_1) + d;
      v = new GlslSandbox55301Shader$Vector2(tmp$_1, tmp$_2 - Math_0.sin(x_2) + Math_0.sin(t));
    }
    var x_3 = r * 0.1;
    r = Math_0.sin(x_3) * 0.5 + 0.5;
    var $receiver = r;
    r = Math_0.pow($receiver, 128.0);
    var tmp$_4 = r;
    var a = r - 0.75;
    var $receiver_0 = Math_0.max(a, 0.0) * 4.0;
    var tmp$_5 = Math_0.pow($receiver_0, 2.0);
    var a_0 = r - 0.875;
    var $receiver_1 = Math_0.max(a_0, 0.0) * 8.0;
    return Color_init_0(tmp$_4, tmp$_5, Math_0.pow($receiver_1, 4.0));
  };
  GlslSandbox55301Shader$Renderer.prototype.glslSandbox55306_0 = function (coord, resolution, time) {
    var position = coord.div_mx4ult$(resolution.x).minus_mx4ult$(0.5);
    var r = position.length;
    var x = position.y / position.x;
    var a = Math_0.atan(x);
    var t = time + 100.0 / (r + 1.0);
    var tmp$ = Math_0.sin(t);
    var x_0 = time + a * 8.0;
    var x_1 = 0.05 * (tmp$ + Math_0.sin(x_0));
    var light = 15.0 * Math_0.abs(x_1);
    var tmp$_0 = Color$Companion_getInstance();
    var tmp$_1 = r * 5.0 - a - time;
    var x_2 = r + t;
    var x_3 = tmp$_1 + Math_0.sin(x_2);
    var tmp$_2 = -Math_0.sin(x_3);
    var tmp$_3 = r * 3.0 + a - Math_0.cos(time);
    var x_4 = r + t;
    var x_5 = tmp$_3 + Math_0.sin(x_4);
    var tmp$_4 = Math_0.sin(x_5);
    var tmp$_5 = r + a * 2.0;
    var x_6 = 5.001 - a / 4.0;
    var x_7 = tmp$_5 + Math_0.log(x_6) + time;
    var tmp$_6 = Math_0.cos(x_7);
    var x_8 = r + t;
    var color = normalized(tmp$_0, tmp$_2, tmp$_4, tmp$_6 - Math_0.sin(x_8));
    return Color_init_0((color.redF + 0.9) * light, (color.greenF + 0.9) * light, (color.blueF + 0.9) * light, 1.0);
  };
  GlslSandbox55301Shader$Renderer.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Renderer',
    interfaces: [Shader$Renderer]
  };
  function GlslSandbox55301Shader$Vector2(x, y) {
    this.x = x;
    this.y = y;
  }
  GlslSandbox55301Shader$Vector2.prototype.div_mx4ult$ = function (scalar) {
    return new GlslSandbox55301Shader$Vector2(this.x / scalar, this.y / scalar);
  };
  GlslSandbox55301Shader$Vector2.prototype.minus_mx4ult$ = function (scalar) {
    return new GlslSandbox55301Shader$Vector2(this.x - scalar, this.y - scalar);
  };
  GlslSandbox55301Shader$Vector2.prototype.minus_b949h1$ = function (other) {
    return new GlslSandbox55301Shader$Vector2(this.x - other.x, this.y - other.y);
  };
  Object.defineProperty(GlslSandbox55301Shader$Vector2.prototype, 'length', {
    get: function () {
      var x = this.x * this.x + this.y * this.y;
      return Math_0.sqrt(x);
    }
  });
  GlslSandbox55301Shader$Vector2.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Vector2',
    interfaces: []
  };
  GlslSandbox55301Shader.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'GlslSandbox55301Shader',
    interfaces: [Shader]
  };
  function normalized($receiver, r, g, b) {
    var x = r * r + g * g + b * b;
    var length = Math_0.sqrt(x);
    return Color_init_0(r / length, g / length, b / length);
  }
  function GlslShader(glslProgram, adjustableValues) {
    GlslShader$Companion_getInstance();
    if (adjustableValues === void 0)
      adjustableValues = GlslShader$Companion_getInstance().findAdjustableValues_61zpoe$(glslProgram);
    Shader.call(this, ShaderId$GLSL_SHADER_getInstance());
    this.glslProgram = glslProgram;
    this.adjustableValues = adjustableValues;
  }
  function GlslShader$Companion() {
    GlslShader$Companion_instance = this;
    this.json_0 = new Json(JsonConfiguration.Companion.Stable.copy_qyeq5q$(void 0, false));
    this.gadgetPattern_0 = Regex_init('\\s*//\\s*SPARKLEMOTION GADGET:\\s*([^\\s]+)\\s+(\\{.*})\\s*\n' + '\\s*uniform\\s+([^\\s]+)\\s+([^\\s]+);');
  }
  GlslShader$Companion.prototype.parse_100t80$ = function (reader) {
    var glslProgram = reader.readString();
    var adjustableValueCount = reader.readShort();
    var $receiver = until_0(0, adjustableValueCount);
    var destination = ArrayList_init_1(collectionSizeOrDefault($receiver, 10));
    var tmp$;
    tmp$ = $receiver.iterator();
    while (tmp$.hasNext()) {
      var item = tmp$.next();
      destination.add_11rb$(GlslShader$AdjustableValue$Companion_getInstance().parse_kbpt9e$(reader, item));
    }
    var adjustableValues = destination;
    return new GlslShader(glslProgram, adjustableValues);
  };
  function GlslShader$Companion$findAdjustableValues$lambda(this$GlslShader$, closure$i) {
    return function (matchResult) {
      var tmp$, tmp$_0;
      println('matches: ' + matchResult.groupValues);
      var tmp$_1 = matchResult.destructured;
      var gadgetType = tmp$_1.match.groupValues.get_za3lpa$(1);
      var configJson = tmp$_1.match.groupValues.get_za3lpa$(2);
      var valueTypeName = tmp$_1.match.groupValues.get_za3lpa$(3);
      var varName = tmp$_1.match.groupValues.get_za3lpa$(4);
      var configData = this$GlslShader$.json_0.parseJson_61zpoe$(configJson);
      switch (valueTypeName) {
        case 'int':
          tmp$ = GlslShader$AdjustableValue$Type$INT_getInstance();
          break;
        case 'float':
          tmp$ = GlslShader$AdjustableValue$Type$FLOAT_getInstance();
          break;
        case 'vec3':
          tmp$ = GlslShader$AdjustableValue$Type$VEC3_getInstance();
          break;
        default:throw IllegalArgumentException_init('unsupported type ' + valueTypeName);
      }
      var valueType = tmp$;
      return new GlslShader$AdjustableValue(varName, gadgetType, valueType, configData.jsonObject, (tmp$_0 = closure$i.v, closure$i.v = tmp$_0 + 1 | 0, tmp$_0));
    };
  }
  GlslShader$Companion.prototype.findAdjustableValues_61zpoe$ = function (glslFragmentShader) {
    var i = {v: 0};
    return toList_3(map(this.gadgetPattern_0.findAll_905azu$(glslFragmentShader), GlslShader$Companion$findAdjustableValues$lambda(this, i)));
  };
  GlslShader$Companion.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'Companion',
    interfaces: [ShaderReader]
  };
  var GlslShader$Companion_instance = null;
  function GlslShader$Companion_getInstance() {
    if (GlslShader$Companion_instance === null) {
      new GlslShader$Companion();
    }
    return GlslShader$Companion_instance;
  }
  GlslShader.prototype.serializeConfig_3kjoo0$ = function (writer) {
    writer.writeString_61zpoe$(this.glslProgram);
    writer.writeShort_za3lpa$(this.adjustableValues.size);
    var tmp$;
    tmp$ = this.adjustableValues.iterator();
    while (tmp$.hasNext()) {
      var element = tmp$.next();
      element.serializeConfig_3kjoo0$(writer);
    }
  };
  function GlslShader$createRenderer$lambda(this$GlslShader) {
    return function () {
      return new GlslShader$PooledRenderer(this$GlslShader.glslProgram, this$GlslShader.adjustableValues);
    };
  }
  GlslShader.prototype.createRenderer_omlfoo$ = function (surface, renderContext) {
    var poolKey = to(getKClass(GlslShader), this.glslProgram);
    var pooledRenderer = renderContext.registerPooled_7d3fln$(poolKey, GlslShader$createRenderer$lambda(this));
    var glslSurface = pooledRenderer.glslRenderer.addSurface_ppt8xj$(surface);
    return new GlslShader$Renderer(glslSurface);
  };
  GlslShader.prototype.createRenderer_ppt8xj$ = function (surface) {
    var glslRenderer = GlslBase_getInstance().manager.createRenderer_3kbl32$(this.glslProgram, this.adjustableValues);
    var glslSurface = glslRenderer.addSurface_ppt8xj$(surface);
    return new GlslShader$Renderer(glslSurface);
  };
  function GlslShader$Renderer(glslSurface) {
    this.glslSurface_0 = glslSurface;
  }
  GlslShader$Renderer.prototype.beginFrame_b23bvv$ = function (buffer, pixelCount) {
    if (this.glslSurface_0 != null) {
      this.glslSurface_0.uniforms.updateFrom_eg9ycu$(buffer.values);
    }
  };
  GlslShader$Renderer.prototype.draw_b23bvv$ = function (buffer, pixelIndex) {
    return this.glslSurface_0 != null ? this.glslSurface_0.pixels.get_za3lpa$(pixelIndex) : Color$Companion_getInstance().BLACK;
  };
  GlslShader$Renderer.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Renderer',
    interfaces: [Shader$Renderer]
  };
  function GlslShader$PooledRenderer(glslProgram, adjustableValues) {
    this.glslRenderer = GlslBase_getInstance().manager.createRenderer_3kbl32$(glslProgram, adjustableValues);
  }
  GlslShader$PooledRenderer.prototype.preDraw = function () {
    this.glslRenderer.draw();
  };
  GlslShader$PooledRenderer.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'PooledRenderer',
    interfaces: [PooledRenderer]
  };
  GlslShader.prototype.createBuffer_ppt8xj$ = function (surface) {
    return new GlslShader$Buffer(this);
  };
  GlslShader.prototype.readBuffer_100t80$ = function (reader) {
    var $receiver = new GlslShader$Buffer(this);
    $receiver.read_100t80$(reader);
    return $receiver;
  };
  function GlslShader$Buffer($outer) {
    this.$outer = $outer;
    var array = Array_0(this.$outer.adjustableValues.size);
    var tmp$;
    tmp$ = array.length - 1 | 0;
    for (var i = 0; i <= tmp$; i++) {
      array[i] = Unit;
    }
    this.values = array;
  }
  Object.defineProperty(GlslShader$Buffer.prototype, 'shader', {
    get: function () {
      return this.$outer;
    }
  });
  GlslShader$Buffer.prototype.update_egvix6$ = function (adjustableValue, value) {
    this.values[adjustableValue.ordinal] = value;
  };
  GlslShader$Buffer.prototype.serialize_3kjoo0$ = function (writer) {
    var tmp$;
    tmp$ = this.$outer.adjustableValues.iterator();
    while (tmp$.hasNext()) {
      var element = tmp$.next();
      element.serializeValue_8f9ar8$(this.values[element.ordinal], writer);
    }
  };
  GlslShader$Buffer.prototype.read_100t80$ = function (reader) {
    var tmp$;
    tmp$ = this.$outer.adjustableValues.iterator();
    while (tmp$.hasNext()) {
      var element = tmp$.next();
      this.values[element.ordinal] = element.readValue_100t80$(reader);
    }
  };
  GlslShader$Buffer.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Buffer',
    interfaces: [Shader$Buffer]
  };
  function GlslShader$AdjustableValue(varName, gadgetType, valueType, config, ordinal) {
    GlslShader$AdjustableValue$Companion_getInstance();
    this.varName = varName;
    this.gadgetType = gadgetType;
    this.valueType = valueType;
    this.config = config;
    this.ordinal = ordinal;
  }
  function GlslShader$AdjustableValue$Type(name, ordinal) {
    Enum.call(this);
    this.name$ = name;
    this.ordinal$ = ordinal;
  }
  function GlslShader$AdjustableValue$Type_initFields() {
    GlslShader$AdjustableValue$Type_initFields = function () {
    };
    GlslShader$AdjustableValue$Type$INT_instance = new GlslShader$AdjustableValue$Type('INT', 0);
    GlslShader$AdjustableValue$Type$FLOAT_instance = new GlslShader$AdjustableValue$Type('FLOAT', 1);
    GlslShader$AdjustableValue$Type$VEC3_instance = new GlslShader$AdjustableValue$Type('VEC3', 2);
  }
  var GlslShader$AdjustableValue$Type$INT_instance;
  function GlslShader$AdjustableValue$Type$INT_getInstance() {
    GlslShader$AdjustableValue$Type_initFields();
    return GlslShader$AdjustableValue$Type$INT_instance;
  }
  var GlslShader$AdjustableValue$Type$FLOAT_instance;
  function GlslShader$AdjustableValue$Type$FLOAT_getInstance() {
    GlslShader$AdjustableValue$Type_initFields();
    return GlslShader$AdjustableValue$Type$FLOAT_instance;
  }
  var GlslShader$AdjustableValue$Type$VEC3_instance;
  function GlslShader$AdjustableValue$Type$VEC3_getInstance() {
    GlslShader$AdjustableValue$Type_initFields();
    return GlslShader$AdjustableValue$Type$VEC3_instance;
  }
  GlslShader$AdjustableValue$Type.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Type',
    interfaces: [Enum]
  };
  function GlslShader$AdjustableValue$Type$values() {
    return [GlslShader$AdjustableValue$Type$INT_getInstance(), GlslShader$AdjustableValue$Type$FLOAT_getInstance(), GlslShader$AdjustableValue$Type$VEC3_getInstance()];
  }
  GlslShader$AdjustableValue$Type.values = GlslShader$AdjustableValue$Type$values;
  function GlslShader$AdjustableValue$Type$valueOf(name) {
    switch (name) {
      case 'INT':
        return GlslShader$AdjustableValue$Type$INT_getInstance();
      case 'FLOAT':
        return GlslShader$AdjustableValue$Type$FLOAT_getInstance();
      case 'VEC3':
        return GlslShader$AdjustableValue$Type$VEC3_getInstance();
      default:throwISE('No enum constant baaahs.shaders.GlslShader.AdjustableValue.Type.' + name);
    }
  }
  GlslShader$AdjustableValue$Type.valueOf_61zpoe$ = GlslShader$AdjustableValue$Type$valueOf;
  GlslShader$AdjustableValue.prototype.serializeConfig_3kjoo0$ = function (writer) {
    writer.writeString_61zpoe$(this.varName);
    writer.writeByte_s8j3t7$(toByte(this.valueType.ordinal));
  };
  GlslShader$AdjustableValue.prototype.serializeValue_8f9ar8$ = function (value, writer) {
    var tmp$, tmp$_0, tmp$_1, tmp$_2, tmp$_3;
    switch (this.valueType.name) {
      case 'INT':
        writer.writeInt_za3lpa$((tmp$_0 = (tmp$ = value) == null || typeof tmp$ === 'number' ? tmp$ : throwCCE()) != null ? tmp$_0 : 0);
        break;
      case 'FLOAT':
        writer.writeFloat_mx4ult$(typeof (tmp$_1 = value) === 'number' ? tmp$_1 : throwCCE());
        break;
      case 'VEC3':
        writer.writeInt_za3lpa$(((tmp$_3 = (tmp$_2 = value) == null || Kotlin.isType(tmp$_2, Color) ? tmp$_2 : throwCCE()) != null ? tmp$_3 : Color$Companion_getInstance().WHITE).argb);
        break;
    }
  };
  GlslShader$AdjustableValue.prototype.readValue_100t80$ = function (reader) {
    var tmp$;
    switch (this.valueType.name) {
      case 'INT':
        tmp$ = reader.readInt();
        break;
      case 'FLOAT':
        tmp$ = reader.readFloat();
        break;
      case 'VEC3':
        tmp$ = new Color(reader.readInt());
        break;
      default:tmp$ = Kotlin.noWhenBranchMatched();
        break;
    }
    return tmp$;
  };
  function GlslShader$AdjustableValue$Companion() {
    GlslShader$AdjustableValue$Companion_instance = this;
    this.types_0 = GlslShader$AdjustableValue$Type$values();
  }
  GlslShader$AdjustableValue$Companion.prototype.parse_kbpt9e$ = function (reader, ordinal) {
    var varName = reader.readString();
    var valueType = this.types_0[reader.readByte()];
    return new GlslShader$AdjustableValue(varName, '', valueType, new JsonObject(emptyMap()), ordinal);
  };
  GlslShader$AdjustableValue$Companion.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'Companion',
    interfaces: []
  };
  var GlslShader$AdjustableValue$Companion_instance = null;
  function GlslShader$AdjustableValue$Companion_getInstance() {
    if (GlslShader$AdjustableValue$Companion_instance === null) {
      new GlslShader$AdjustableValue$Companion();
    }
    return GlslShader$AdjustableValue$Companion_instance;
  }
  GlslShader$AdjustableValue.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'AdjustableValue',
    interfaces: []
  };
  GlslShader.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'GlslShader',
    interfaces: [Shader]
  };
  function HeartShader() {
    HeartShader$Companion_getInstance();
    Shader.call(this, ShaderId$HEART_getInstance());
  }
  HeartShader.prototype.createBuffer_ppt8xj$ = function (surface) {
    return new HeartShader$Buffer(this);
  };
  HeartShader.prototype.readBuffer_100t80$ = function (reader) {
    var $receiver = new HeartShader$Buffer(this);
    $receiver.read_100t80$(reader);
    return $receiver;
  };
  HeartShader.prototype.createRenderer_ppt8xj$ = function (surface) {
    return new HeartShader$Renderer(surface);
  };
  function HeartShader$Companion() {
    HeartShader$Companion_instance = this;
  }
  HeartShader$Companion.prototype.parse_100t80$ = function (reader) {
    return new HeartShader();
  };
  HeartShader$Companion.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'Companion',
    interfaces: [ShaderReader]
  };
  var HeartShader$Companion_instance = null;
  function HeartShader$Companion_getInstance() {
    if (HeartShader$Companion_instance === null) {
      new HeartShader$Companion();
    }
    return HeartShader$Companion_instance;
  }
  function HeartShader$Buffer($outer) {
    this.$outer = $outer;
    this.edgeColor = Color$Companion_getInstance().RED;
    this.centerColor = Color$Companion_getInstance().RED.fade_6zkv30$(Color$Companion_getInstance().WHITE, 0.2);
    this.heartSize = 1.0;
    this.strokeSize = 1.0;
    this.xOff = 0.0;
    this.yOff = 0.0;
  }
  Object.defineProperty(HeartShader$Buffer.prototype, 'shader', {
    get: function () {
      return this.$outer;
    }
  });
  HeartShader$Buffer.prototype.serialize_3kjoo0$ = function (writer) {
    writer.writeFloat_mx4ult$(this.heartSize);
    writer.writeFloat_mx4ult$(this.strokeSize);
    writer.writeFloat_mx4ult$(this.xOff);
    writer.writeFloat_mx4ult$(this.yOff);
  };
  HeartShader$Buffer.prototype.read_100t80$ = function (reader) {
    this.heartSize = reader.readFloat();
    this.strokeSize = reader.readFloat();
    this.xOff = reader.readFloat();
    this.yOff = reader.readFloat();
  };
  HeartShader$Buffer.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Buffer',
    interfaces: [Shader$Buffer]
  };
  function HeartShader$Renderer(surface) {
    var tmp$, tmp$_0;
    this.pixelVertices_0 = (tmp$_0 = Kotlin.isType(tmp$ = surface, IdentifiedSurface) ? tmp$ : null) != null ? tmp$_0.pixelVertices : null;
  }
  HeartShader$Renderer.prototype.draw_b23bvv$ = function (buffer, pixelIndex) {
    var tmp$, tmp$_0;
    if (this.pixelVertices_0 == null) {
      return Color$Companion_getInstance().BLACK;
    }
    var tmp$_1 = this.pixelVertices_0.get_za3lpa$(pixelIndex);
    var x = tmp$_1.component1()
    , y = tmp$_1.component2();
    x -= 0.5 + buffer.xOff - 0.5;
    x *= 1.1;
    y -= 0.5 + buffer.yOff - 0.5;
    x /= buffer.heartSize;
    y /= buffer.heartSize;
    var $receiver = Math_0.abs(x) - 1;
    var upperCurveDist = y - (1 - Math_0.pow($receiver, 2));
    var x_0 = 1 - Math_0.abs(x);
    var lowerCurveDist = y - (Math_0.acos(x_0) - math.PI);
    if (y >= 0) {
      if (upperCurveDist < 0) {
        if (Math_0.abs(upperCurveDist) < buffer.strokeSize) {
          tmp$ = 0.0;
        }
         else {
          var x_1 = upperCurveDist / buffer.heartSize;
          tmp$ = Math_0.abs(x_1);
        }
        var fadeAmount = tmp$;
        return buffer.edgeColor.fade_6zkv30$(buffer.centerColor, fadeAmount);
      }
       else {
        return Color$Companion_getInstance().TRANSPARENT;
      }
    }
     else if (lowerCurveDist > 0) {
      if (lowerCurveDist < buffer.strokeSize) {
        tmp$_0 = 1.0;
      }
       else {
        tmp$_0 = lowerCurveDist / buffer.heartSize;
      }
      var fadeAmount_0 = tmp$_0;
      return buffer.edgeColor.fade_6zkv30$(buffer.centerColor, fadeAmount_0);
    }
     else {
      return Color$Companion_getInstance().TRANSPARENT;
    }
  };
  HeartShader$Renderer.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Renderer',
    interfaces: [Shader$Renderer]
  };
  HeartShader.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'HeartShader',
    interfaces: [Shader]
  };
  function PixelShader(encoding) {
    PixelShader$Companion_getInstance();
    if (encoding === void 0)
      encoding = PixelShader$Encoding$DIRECT_ARGB_getInstance();
    Shader.call(this, ShaderId$PIXEL_getInstance());
    this.encoding_0 = encoding;
  }
  function PixelShader$Encoding(name, ordinal) {
    Enum.call(this);
    this.name$ = name;
    this.ordinal$ = ordinal;
  }
  function PixelShader$Encoding_initFields() {
    PixelShader$Encoding_initFields = function () {
    };
    new PixelShader$Encoding$DIRECT_ARGB();
    new PixelShader$Encoding$DIRECT_RGB();
    new PixelShader$Encoding$INDEXED_2();
    new PixelShader$Encoding$INDEXED_4();
    new PixelShader$Encoding$INDEXED_16();
    PixelShader$Encoding$Companion_getInstance();
  }
  function PixelShader$Encoding$DIRECT_ARGB() {
    PixelShader$Encoding$DIRECT_ARGB_instance = this;
    PixelShader$Encoding.call(this, 'DIRECT_ARGB', 0);
  }
  PixelShader$Encoding$DIRECT_ARGB.prototype.createBuffer_aycglj$ = function (shader, pixelCount) {
    return new PixelShader$DirectColorBuffer(shader, pixelCount);
  };
  PixelShader$Encoding$DIRECT_ARGB.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'DIRECT_ARGB',
    interfaces: [PixelShader$Encoding]
  };
  var PixelShader$Encoding$DIRECT_ARGB_instance = null;
  function PixelShader$Encoding$DIRECT_ARGB_getInstance() {
    PixelShader$Encoding_initFields();
    return PixelShader$Encoding$DIRECT_ARGB_instance;
  }
  function PixelShader$Encoding$DIRECT_RGB() {
    PixelShader$Encoding$DIRECT_RGB_instance = this;
    PixelShader$Encoding.call(this, 'DIRECT_RGB', 1);
  }
  PixelShader$Encoding$DIRECT_RGB.prototype.createBuffer_aycglj$ = function (shader, pixelCount) {
    return new PixelShader$DirectColorBuffer(shader, pixelCount, true);
  };
  PixelShader$Encoding$DIRECT_RGB.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'DIRECT_RGB',
    interfaces: [PixelShader$Encoding]
  };
  var PixelShader$Encoding$DIRECT_RGB_instance = null;
  function PixelShader$Encoding$DIRECT_RGB_getInstance() {
    PixelShader$Encoding_initFields();
    return PixelShader$Encoding$DIRECT_RGB_instance;
  }
  function PixelShader$Encoding$INDEXED_2() {
    PixelShader$Encoding$INDEXED_2_instance = this;
    PixelShader$Encoding.call(this, 'INDEXED_2', 2);
  }
  PixelShader$Encoding$INDEXED_2.prototype.createBuffer_aycglj$ = function (shader, pixelCount) {
    return new PixelShader$IndexedBuffer(shader, 1, pixelCount);
  };
  PixelShader$Encoding$INDEXED_2.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'INDEXED_2',
    interfaces: [PixelShader$Encoding]
  };
  var PixelShader$Encoding$INDEXED_2_instance = null;
  function PixelShader$Encoding$INDEXED_2_getInstance() {
    PixelShader$Encoding_initFields();
    return PixelShader$Encoding$INDEXED_2_instance;
  }
  function PixelShader$Encoding$INDEXED_4() {
    PixelShader$Encoding$INDEXED_4_instance = this;
    PixelShader$Encoding.call(this, 'INDEXED_4', 3);
  }
  PixelShader$Encoding$INDEXED_4.prototype.createBuffer_aycglj$ = function (shader, pixelCount) {
    return new PixelShader$IndexedBuffer(shader, 2, pixelCount);
  };
  PixelShader$Encoding$INDEXED_4.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'INDEXED_4',
    interfaces: [PixelShader$Encoding]
  };
  var PixelShader$Encoding$INDEXED_4_instance = null;
  function PixelShader$Encoding$INDEXED_4_getInstance() {
    PixelShader$Encoding_initFields();
    return PixelShader$Encoding$INDEXED_4_instance;
  }
  function PixelShader$Encoding$INDEXED_16() {
    PixelShader$Encoding$INDEXED_16_instance = this;
    PixelShader$Encoding.call(this, 'INDEXED_16', 4);
  }
  PixelShader$Encoding$INDEXED_16.prototype.createBuffer_aycglj$ = function (shader, pixelCount) {
    return new PixelShader$IndexedBuffer(shader, 4, pixelCount);
  };
  PixelShader$Encoding$INDEXED_16.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'INDEXED_16',
    interfaces: [PixelShader$Encoding]
  };
  var PixelShader$Encoding$INDEXED_16_instance = null;
  function PixelShader$Encoding$INDEXED_16_getInstance() {
    PixelShader$Encoding_initFields();
    return PixelShader$Encoding$INDEXED_16_instance;
  }
  function PixelShader$Encoding$Companion() {
    PixelShader$Encoding$Companion_instance = this;
    this.values = PixelShader$Encoding$values();
  }
  PixelShader$Encoding$Companion.prototype.get_s8j3t7$ = function (i) {
    return this.values[i];
  };
  PixelShader$Encoding$Companion.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'Companion',
    interfaces: []
  };
  var PixelShader$Encoding$Companion_instance = null;
  function PixelShader$Encoding$Companion_getInstance() {
    PixelShader$Encoding_initFields();
    if (PixelShader$Encoding$Companion_instance === null) {
      new PixelShader$Encoding$Companion();
    }
    return PixelShader$Encoding$Companion_instance;
  }
  PixelShader$Encoding.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Encoding',
    interfaces: [Enum]
  };
  function PixelShader$Encoding$values() {
    return [PixelShader$Encoding$DIRECT_ARGB_getInstance(), PixelShader$Encoding$DIRECT_RGB_getInstance(), PixelShader$Encoding$INDEXED_2_getInstance(), PixelShader$Encoding$INDEXED_4_getInstance(), PixelShader$Encoding$INDEXED_16_getInstance()];
  }
  PixelShader$Encoding.values = PixelShader$Encoding$values;
  function PixelShader$Encoding$valueOf(name) {
    switch (name) {
      case 'DIRECT_ARGB':
        return PixelShader$Encoding$DIRECT_ARGB_getInstance();
      case 'DIRECT_RGB':
        return PixelShader$Encoding$DIRECT_RGB_getInstance();
      case 'INDEXED_2':
        return PixelShader$Encoding$INDEXED_2_getInstance();
      case 'INDEXED_4':
        return PixelShader$Encoding$INDEXED_4_getInstance();
      case 'INDEXED_16':
        return PixelShader$Encoding$INDEXED_16_getInstance();
      default:throwISE('No enum constant baaahs.shaders.PixelShader.Encoding.' + name);
    }
  }
  PixelShader$Encoding.valueOf_61zpoe$ = PixelShader$Encoding$valueOf;
  PixelShader.prototype.serializeConfig_3kjoo0$ = function (writer) {
    writer.writeByte_s8j3t7$(toByte(this.encoding_0.ordinal));
  };
  PixelShader.prototype.createBuffer_ppt8xj$ = function (surface) {
    var tmp$;
    if (surface.pixelCount === -1) {
      tmp$ = 512;
    }
     else {
      tmp$ = surface.pixelCount;
    }
    var pixelCount = tmp$;
    return this.encoding_0.createBuffer_aycglj$(this, pixelCount);
  };
  PixelShader.prototype.createRenderer_ppt8xj$ = function (surface) {
    return new PixelShader$Renderer();
  };
  PixelShader.prototype.readBuffer_100t80$ = function (reader) {
    var incomingPixelCount = reader.readShort();
    var buf = this.encoding_0.createBuffer_aycglj$(this, incomingPixelCount);
    buf.read_kbpt9e$(reader, incomingPixelCount);
    return buf;
  };
  function PixelShader$Companion() {
    PixelShader$Companion_instance = this;
  }
  PixelShader$Companion.prototype.parse_100t80$ = function (reader) {
    var encoding = PixelShader$Encoding$Companion_getInstance().get_s8j3t7$(reader.readByte());
    return new PixelShader(encoding);
  };
  PixelShader$Companion.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'Companion',
    interfaces: [ShaderReader]
  };
  var PixelShader$Companion_instance = null;
  function PixelShader$Companion_getInstance() {
    if (PixelShader$Companion_instance === null) {
      new PixelShader$Companion();
    }
    return PixelShader$Companion_instance;
  }
  function PixelShader$Buffer($outer) {
    this.$outer = $outer;
  }
  Object.defineProperty(PixelShader$Buffer.prototype, 'shader', {
    get: function () {
      return this.$outer;
    }
  });
  PixelShader$Buffer.prototype.read_100t80$ = function (reader) {
    var incomingPixelCount = reader.readShort();
    this.read_kbpt9e$(reader, incomingPixelCount);
  };
  PixelShader$Buffer.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Buffer',
    interfaces: [Shader$Buffer]
  };
  function PixelShader$DirectColorBuffer($outer, pixelCount, rgb24BitMode) {
    this.$outer = $outer;
    if (rgb24BitMode === void 0)
      rgb24BitMode = false;
    PixelShader$Buffer.call(this, this.$outer);
    this.pixelCount_0 = pixelCount;
    this.rgb24BitMode_0 = rgb24BitMode;
    this.palette_j72hve$_0 = [];
    var array = Array_0(this.pixelCount_0);
    var tmp$;
    tmp$ = array.length - 1 | 0;
    for (var i = 0; i <= tmp$; i++) {
      array[i] = Color$Companion_getInstance().WHITE;
    }
    this.colorsBuf_0 = array;
    this.indices_4idwzy$_0 = get_indices_0(this.colorsBuf_0);
  }
  Object.defineProperty(PixelShader$DirectColorBuffer.prototype, 'palette', {
    get: function () {
      return this.palette_j72hve$_0;
    }
  });
  function PixelShader$DirectColorBuffer$get_PixelShader$DirectColorBuffer$colors$ObjectLiteral(this$DirectColorBuffer) {
    this.this$DirectColorBuffer = this$DirectColorBuffer;
    AbstractMutableList.call(this);
    this.size_hgyj2k$_0 = this$DirectColorBuffer.pixelCount_0;
  }
  PixelShader$DirectColorBuffer$get_PixelShader$DirectColorBuffer$colors$ObjectLiteral.prototype.add_wxm5ur$ = function (index, element) {
    throw UnsupportedOperationException_init();
  };
  PixelShader$DirectColorBuffer$get_PixelShader$DirectColorBuffer$colors$ObjectLiteral.prototype.removeAt_za3lpa$ = function (index) {
    throw UnsupportedOperationException_init();
  };
  PixelShader$DirectColorBuffer$get_PixelShader$DirectColorBuffer$colors$ObjectLiteral.prototype.set_wxm5ur$ = function (index, element) {
    var oldValue = this.get_za3lpa$(index);
    this.this$DirectColorBuffer.set_ibd5tj$(index, element);
    return oldValue;
  };
  Object.defineProperty(PixelShader$DirectColorBuffer$get_PixelShader$DirectColorBuffer$colors$ObjectLiteral.prototype, 'size', {
    get: function () {
      return this.size_hgyj2k$_0;
    }
  });
  PixelShader$DirectColorBuffer$get_PixelShader$DirectColorBuffer$colors$ObjectLiteral.prototype.get_za3lpa$ = function (index) {
    return this.this$DirectColorBuffer.get_za3lpa$(index);
  };
  PixelShader$DirectColorBuffer$get_PixelShader$DirectColorBuffer$colors$ObjectLiteral.$metadata$ = {
    kind: Kind_CLASS,
    interfaces: [AbstractMutableList]
  };
  Object.defineProperty(PixelShader$DirectColorBuffer.prototype, 'colors', {
    get: function () {
      return new PixelShader$DirectColorBuffer$get_PixelShader$DirectColorBuffer$colors$ObjectLiteral(this);
    }
  });
  PixelShader$DirectColorBuffer.prototype.serialize_3kjoo0$ = function (writer) {
    writer.writeShort_za3lpa$(this.pixelCount_0);
    var $receiver = this.colorsBuf_0;
    var tmp$;
    for (tmp$ = 0; tmp$ !== $receiver.length; ++tmp$) {
      var element = $receiver[tmp$];
      this.writeColor_0(element, writer);
    }
  };
  PixelShader$DirectColorBuffer.prototype.read_kbpt9e$ = function (reader, incomingPixelCount) {
    var tmp$;
    var a = this.colorsBuf_0.length;
    var countFromBuffer = Math_0.min(a, incomingPixelCount);
    for (var i = 0; i < countFromBuffer; i++) {
      this.colorsBuf_0[i] = this.readColor_0(reader);
    }
    tmp$ = this.colorsBuf_0.length;
    for (var i_0 = countFromBuffer; i_0 < tmp$; i_0++) {
      this.colorsBuf_0[i_0] = this.colorsBuf_0[i_0 % countFromBuffer];
    }
  };
  PixelShader$DirectColorBuffer.prototype.writeColor_0 = function (color, writer) {
    if (this.rgb24BitMode_0) {
      writer.writeByte_s8j3t7$(color.redB);
      writer.writeByte_s8j3t7$(color.greenB);
      writer.writeByte_s8j3t7$(color.blueB);
    }
     else {
      writer.writeInt_za3lpa$(color.argb);
    }
  };
  PixelShader$DirectColorBuffer.prototype.readColor_0 = function (reader) {
    var tmp$;
    if (this.rgb24BitMode_0) {
      tmp$ = Color_init_2(reader.readByte(), reader.readByte(), reader.readByte());
    }
     else {
      tmp$ = new Color(reader.readInt());
    }
    return tmp$;
  };
  PixelShader$DirectColorBuffer.prototype.get_za3lpa$ = function (pixelIndex) {
    return this.colorsBuf_0[pixelIndex];
  };
  PixelShader$DirectColorBuffer.prototype.set_ibd5tj$ = function (pixelIndex, color) {
    this.colorsBuf_0[pixelIndex] = color;
  };
  PixelShader$DirectColorBuffer.prototype.set_vux9f0$ = function (pixelIndex, paletteIndex) {
    throw UnsupportedOperationException_init_0("Indexed colors aren't available in this mode");
  };
  PixelShader$DirectColorBuffer.prototype.setAll_rny0jj$ = function (color) {
    var tmp$;
    tmp$ = this.colorsBuf_0;
    for (var i = 0; i !== tmp$.length; ++i) {
      this.set_ibd5tj$(i, color);
    }
  };
  PixelShader$DirectColorBuffer.prototype.setAll_za3lpa$ = function (paletteIndex) {
    throw UnsupportedOperationException_init_0("Indexed colors aren't available in this mode");
  };
  Object.defineProperty(PixelShader$DirectColorBuffer.prototype, 'indices', {
    get: function () {
      return this.indices_4idwzy$_0;
    }
  });
  PixelShader$DirectColorBuffer.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'DirectColorBuffer',
    interfaces: [PixelShader$Buffer]
  };
  function PixelShader$IndexedBuffer($outer, bitsPerPixel, pixelCount) {
    this.$outer = $outer;
    PixelShader$Buffer.call(this, this.$outer);
    this.bitsPerPixel_0 = bitsPerPixel;
    this.pixelCount_0 = pixelCount;
    var array = Array_0(1 << this.bitsPerPixel_0);
    var tmp$;
    tmp$ = array.length - 1 | 0;
    for (var i = 0; i <= tmp$; i++) {
      array[i] = Color$Companion_getInstance().WHITE;
    }
    this.palette_ieg50d$_0 = array;
    var array_0 = new Int8Array(this.bufferSizeFor_0(this.pixelCount_0));
    var tmp$_0;
    tmp$_0 = array_0.length - 1 | 0;
    for (var i_0 = 0; i_0 <= tmp$_0; i_0++) {
      array_0[i_0] = 0;
    }
    this.dataBuf_8be2vx$ = array_0;
    this.indices_x34pvt$_0 = until(0, this.pixelCount_0);
  }
  Object.defineProperty(PixelShader$IndexedBuffer.prototype, 'palette', {
    get: function () {
      return this.palette_ieg50d$_0;
    }
  });
  function PixelShader$IndexedBuffer$get_PixelShader$IndexedBuffer$colors$ObjectLiteral(this$IndexedBuffer) {
    this.this$IndexedBuffer = this$IndexedBuffer;
    AbstractMutableList.call(this);
    this.size_nv01kz$_0 = this$IndexedBuffer.pixelCount_0;
  }
  Object.defineProperty(PixelShader$IndexedBuffer$get_PixelShader$IndexedBuffer$colors$ObjectLiteral.prototype, 'size', {
    get: function () {
      return this.size_nv01kz$_0;
    }
  });
  PixelShader$IndexedBuffer$get_PixelShader$IndexedBuffer$colors$ObjectLiteral.prototype.add_wxm5ur$ = function (index, element) {
    throw UnsupportedOperationException_init();
  };
  PixelShader$IndexedBuffer$get_PixelShader$IndexedBuffer$colors$ObjectLiteral.prototype.removeAt_za3lpa$ = function (index) {
    throw UnsupportedOperationException_init();
  };
  PixelShader$IndexedBuffer$get_PixelShader$IndexedBuffer$colors$ObjectLiteral.prototype.set_wxm5ur$ = function (index, element) {
    throw IllegalArgumentException_init("Can't set color directly when using indexed color buffers");
  };
  PixelShader$IndexedBuffer$get_PixelShader$IndexedBuffer$colors$ObjectLiteral.prototype.get_za3lpa$ = function (index) {
    return this.this$IndexedBuffer.get_za3lpa$(index);
  };
  PixelShader$IndexedBuffer$get_PixelShader$IndexedBuffer$colors$ObjectLiteral.$metadata$ = {
    kind: Kind_CLASS,
    interfaces: [AbstractMutableList]
  };
  Object.defineProperty(PixelShader$IndexedBuffer.prototype, 'colors', {
    get: function () {
      return new PixelShader$IndexedBuffer$get_PixelShader$IndexedBuffer$colors$ObjectLiteral(this);
    }
  });
  PixelShader$IndexedBuffer.prototype.get_za3lpa$ = function (pixelIndex) {
    return this.palette[this.paletteIndex_0(pixelIndex)];
  };
  PixelShader$IndexedBuffer.prototype.set_ibd5tj$ = function (pixelIndex, color) {
    throw IllegalArgumentException_init("Can't set color directly when using indexed color buffers");
  };
  PixelShader$IndexedBuffer.prototype.set_vux9f0$ = function (pixelIndex, paletteIndex) {
    var mask;
    var pixelsPerByte;
    var maxIndex;
    switch (this.bitsPerPixel_0) {
      case 1:
        mask = 1;
        pixelsPerByte = 8;
        maxIndex = 1;
        break;
      case 2:
        mask = 3;
        pixelsPerByte = 4;
        maxIndex = 3;
        break;
      case 4:
        mask = 15;
        pixelsPerByte = 2;
        maxIndex = 15;
        break;
      default:throw IllegalStateException_init_0();
    }
    if (paletteIndex < 0 || paletteIndex > maxIndex)
      throw IllegalArgumentException_init('Invalid color index ' + paletteIndex);
    var bufOffset = (pixelIndex / pixelsPerByte | 0) % this.dataBuf_8be2vx$.length;
    var positionInByte = pixelsPerByte - pixelIndex % pixelsPerByte - 1 | 0;
    var bitShift = Kotlin.imul(positionInByte, this.bitsPerPixel_0);
    var byte = this.dataBuf_8be2vx$[bufOffset] & ~(mask << bitShift) | paletteIndex << bitShift;
    this.dataBuf_8be2vx$[bufOffset] = toByte(byte);
  };
  PixelShader$IndexedBuffer.prototype.serialize_3kjoo0$ = function (writer) {
    writer.writeShort_za3lpa$(this.pixelCount_0);
    var $receiver = this.palette;
    var tmp$;
    for (tmp$ = 0; tmp$ !== $receiver.length; ++tmp$) {
      var element = $receiver[tmp$];
      writer.writeInt_za3lpa$(element.argb);
    }
    writer.writeBytes_mj6st8$(this.dataBuf_8be2vx$);
  };
  PixelShader$IndexedBuffer.prototype.read_kbpt9e$ = function (reader, incomingPixelCount) {
    var tmp$;
    tmp$ = get_indices_0(this.palette).iterator();
    while (tmp$.hasNext()) {
      var element = tmp$.next();
      this.palette[element] = Color$Companion_getInstance().fromInt(reader.readInt());
    }
    reader.readBytes_fqrh44$(this.dataBuf_8be2vx$);
  };
  PixelShader$IndexedBuffer.prototype.setAll_rny0jj$ = function (color) {
    throw IllegalArgumentException_init("Can't set color directly when using indexed color buffers");
  };
  PixelShader$IndexedBuffer.prototype.setAll_za3lpa$ = function (paletteIndex) {
    var tmp$, tmp$_0, tmp$_1, tmp$_2;
    tmp$ = this.indices;
    tmp$_0 = tmp$.first;
    tmp$_1 = tmp$.last;
    tmp$_2 = tmp$.step;
    for (var i = tmp$_0; i <= tmp$_1; i += tmp$_2)
      this.set_vux9f0$(i, paletteIndex);
  };
  Object.defineProperty(PixelShader$IndexedBuffer.prototype, 'indices', {
    get: function () {
      return this.indices_x34pvt$_0;
    }
  });
  PixelShader$IndexedBuffer.prototype.paletteIndex_0 = function (pixelIndex) {
    var mask;
    var pixelsPerByte;
    switch (this.bitsPerPixel_0) {
      case 1:
        mask = 1;
        pixelsPerByte = 8;
        break;
      case 2:
        mask = 3;
        pixelsPerByte = 4;
        break;
      case 4:
        mask = 15;
        pixelsPerByte = 2;
        break;
      default:throw IllegalStateException_init_0();
    }
    var bufOffset = (pixelIndex / pixelsPerByte | 0) % this.dataBuf_8be2vx$.length;
    var positionInByte = pixelsPerByte - pixelIndex % pixelsPerByte - 1 | 0;
    var bitShift = Kotlin.imul(positionInByte, this.bitsPerPixel_0);
    return this.dataBuf_8be2vx$[bufOffset] >> bitShift & mask;
  };
  PixelShader$IndexedBuffer.prototype.bufferSizeFor_0 = function (pixelCount) {
    var tmp$;
    switch (this.bitsPerPixel_0) {
      case 1:
        tmp$ = (pixelCount + 7 | 0) / 8 | 0;
        break;
      case 2:
        tmp$ = (pixelCount + 3 | 0) / 4 | 0;
        break;
      case 4:
        tmp$ = (pixelCount + 1 | 0) / 2 | 0;
        break;
      default:throw IllegalStateException_init_0();
    }
    return tmp$;
  };
  PixelShader$IndexedBuffer.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'IndexedBuffer',
    interfaces: [PixelShader$Buffer]
  };
  function PixelShader$Renderer() {
  }
  PixelShader$Renderer.prototype.draw_b23bvv$ = function (buffer, pixelIndex) {
    return buffer.colors.get_za3lpa$(pixelIndex);
  };
  PixelShader$Renderer.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Renderer',
    interfaces: [Shader$Renderer]
  };
  PixelShader.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'PixelShader',
    interfaces: [Shader]
  };
  function RandomShader() {
    RandomShader$Companion_getInstance();
    Shader.call(this, ShaderId$RANDOM_getInstance());
  }
  RandomShader.prototype.createBuffer_ppt8xj$ = function (surface) {
    return new RandomShader$Buffer(this);
  };
  RandomShader.prototype.readBuffer_100t80$ = function (reader) {
    var $receiver = new RandomShader$Buffer(this);
    $receiver.read_100t80$(reader);
    return $receiver;
  };
  RandomShader.prototype.createRenderer_ppt8xj$ = function (surface) {
    return new RandomShader$Renderer();
  };
  function RandomShader$Companion() {
    RandomShader$Companion_instance = this;
  }
  RandomShader$Companion.prototype.parse_100t80$ = function (reader) {
    return new RandomShader();
  };
  RandomShader$Companion.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'Companion',
    interfaces: [ShaderReader]
  };
  var RandomShader$Companion_instance = null;
  function RandomShader$Companion_getInstance() {
    if (RandomShader$Companion_instance === null) {
      new RandomShader$Companion();
    }
    return RandomShader$Companion_instance;
  }
  function RandomShader$Buffer($outer) {
    this.$outer = $outer;
  }
  Object.defineProperty(RandomShader$Buffer.prototype, 'shader', {
    get: function () {
      return this.$outer;
    }
  });
  RandomShader$Buffer.prototype.serialize_3kjoo0$ = function (writer) {
  };
  RandomShader$Buffer.prototype.read_100t80$ = function (reader) {
  };
  RandomShader$Buffer.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Buffer',
    interfaces: [Shader$Buffer]
  };
  function RandomShader$Renderer() {
  }
  RandomShader$Renderer.prototype.draw_b23bvv$ = function (buffer, pixelIndex) {
    return Color$Companion_getInstance().fromInt(Random.Default.nextInt_za3lpa$(16777215) | 0);
  };
  RandomShader$Renderer.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Renderer',
    interfaces: [Shader$Renderer]
  };
  RandomShader.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'RandomShader',
    interfaces: [Shader]
  };
  function SimpleSpatialShader() {
    SimpleSpatialShader$Companion_getInstance();
    Shader.call(this, ShaderId$SIMPLE_SPATIAL_getInstance());
  }
  SimpleSpatialShader.prototype.createBuffer_ppt8xj$ = function (surface) {
    return new SimpleSpatialShader$Buffer(this);
  };
  SimpleSpatialShader.prototype.readBuffer_100t80$ = function (reader) {
    var $receiver = new SimpleSpatialShader$Buffer(this);
    $receiver.read_100t80$(reader);
    return $receiver;
  };
  SimpleSpatialShader.prototype.createRenderer_ppt8xj$ = function (surface) {
    return new SimpleSpatialShader$Renderer(surface);
  };
  function SimpleSpatialShader$Companion() {
    SimpleSpatialShader$Companion_instance = this;
  }
  SimpleSpatialShader$Companion.prototype.parse_100t80$ = function (reader) {
    return new SimpleSpatialShader();
  };
  SimpleSpatialShader$Companion.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'Companion',
    interfaces: [ShaderReader]
  };
  var SimpleSpatialShader$Companion_instance = null;
  function SimpleSpatialShader$Companion_getInstance() {
    if (SimpleSpatialShader$Companion_instance === null) {
      new SimpleSpatialShader$Companion();
    }
    return SimpleSpatialShader$Companion_instance;
  }
  function SimpleSpatialShader$Buffer($outer) {
    this.$outer = $outer;
    this.color = Color$Companion_getInstance().WHITE;
    this.centerX = 0.5;
    this.centerY = 0.5;
    this.radius = 0.75;
  }
  Object.defineProperty(SimpleSpatialShader$Buffer.prototype, 'shader', {
    get: function () {
      return this.$outer;
    }
  });
  SimpleSpatialShader$Buffer.prototype.serialize_3kjoo0$ = function (writer) {
    this.color.serialize_3kjoo0$(writer);
    writer.writeFloat_mx4ult$(this.centerX);
    writer.writeFloat_mx4ult$(this.centerY);
    writer.writeFloat_mx4ult$(this.radius);
  };
  SimpleSpatialShader$Buffer.prototype.read_100t80$ = function (reader) {
    this.color = Color$Companion_getInstance().parse_100t80$(reader);
    this.centerX = reader.readFloat();
    this.centerY = reader.readFloat();
    this.radius = reader.readFloat();
  };
  SimpleSpatialShader$Buffer.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Buffer',
    interfaces: [Shader$Buffer]
  };
  function SimpleSpatialShader$Renderer(surface) {
    var tmp$, tmp$_0;
    this.pixelVertices_0 = (tmp$_0 = Kotlin.isType(tmp$ = surface, IdentifiedSurface) ? tmp$ : null) != null ? tmp$_0.pixelVertices : null;
  }
  SimpleSpatialShader$Renderer.prototype.draw_b23bvv$ = function (buffer, pixelIndex) {
    var tmp$;
    if (this.pixelVertices_0 == null || pixelIndex >= this.pixelVertices_0.size)
      return Color$Companion_getInstance().BLACK;
    var tmp$_0 = this.pixelVertices_0.get_za3lpa$(pixelIndex);
    var pixX = tmp$_0.component1()
    , pixY = tmp$_0.component2();
    var distX = pixX - buffer.centerX;
    var distY = pixY - buffer.centerY;
    var x = distX * distX + distY * distY;
    var dist = Math_0.sqrt(x);
    if (dist < buffer.radius - 0.025)
      tmp$ = buffer.color;
    else if (dist < buffer.radius + 0.025)
      tmp$ = Color$Companion_getInstance().BLACK;
    else
      tmp$ = buffer.color.fade_6zkv30$(Color$Companion_getInstance().BLACK, dist * 2);
    return tmp$;
  };
  SimpleSpatialShader$Renderer.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Renderer',
    interfaces: [Shader$Renderer]
  };
  SimpleSpatialShader.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'SimpleSpatialShader',
    interfaces: [Shader]
  };
  function SineWaveShader() {
    SineWaveShader$Companion_getInstance();
    Shader.call(this, ShaderId$SINE_WAVE_getInstance());
  }
  SineWaveShader.prototype.createBuffer_ppt8xj$ = function (surface) {
    return new SineWaveShader$Buffer(this);
  };
  SineWaveShader.prototype.readBuffer_100t80$ = function (reader) {
    var $receiver = new SineWaveShader$Buffer(this);
    $receiver.read_100t80$(reader);
    return $receiver;
  };
  SineWaveShader.prototype.createRenderer_ppt8xj$ = function (surface) {
    return new SineWaveShader$Renderer();
  };
  function SineWaveShader$Companion() {
    SineWaveShader$Companion_instance = this;
  }
  SineWaveShader$Companion.prototype.parse_100t80$ = function (reader) {
    return new SineWaveShader();
  };
  SineWaveShader$Companion.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'Companion',
    interfaces: [ShaderReader]
  };
  var SineWaveShader$Companion_instance = null;
  function SineWaveShader$Companion_getInstance() {
    if (SineWaveShader$Companion_instance === null) {
      new SineWaveShader$Companion();
    }
    return SineWaveShader$Companion_instance;
  }
  function SineWaveShader$Buffer($outer) {
    this.$outer = $outer;
    this.color = Color$Companion_getInstance().WHITE;
    this.theta = 0.0;
    this.density = 1.0;
  }
  Object.defineProperty(SineWaveShader$Buffer.prototype, 'shader', {
    get: function () {
      return this.$outer;
    }
  });
  SineWaveShader$Buffer.prototype.serialize_3kjoo0$ = function (writer) {
    this.color.serialize_3kjoo0$(writer);
    writer.writeFloat_mx4ult$(this.theta);
    writer.writeFloat_mx4ult$(this.density);
  };
  SineWaveShader$Buffer.prototype.read_100t80$ = function (reader) {
    this.color = Color$Companion_getInstance().parse_100t80$(reader);
    this.theta = reader.readFloat();
    this.density = reader.readFloat();
  };
  SineWaveShader$Buffer.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Buffer',
    interfaces: [Shader$Buffer]
  };
  function SineWaveShader$Renderer() {
    this.pixelCount_0 = 1;
  }
  SineWaveShader$Renderer.prototype.beginFrame_b23bvv$ = function (buffer, pixelCount) {
    this.pixelCount_0 = pixelCount;
  };
  SineWaveShader$Renderer.prototype.draw_b23bvv$ = function (buffer, pixelIndex) {
    var theta = buffer.theta;
    var density = buffer.density;
    var x = theta + 2 * math.PI * (pixelIndex / this.pixelCount_0 * density);
    var v = Math_0.sin(x) / 2 + 0.5;
    return Color$Companion_getInstance().BLACK.fade_6zkv30$(buffer.color, v);
  };
  SineWaveShader$Renderer.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Renderer',
    interfaces: [Shader$Renderer]
  };
  SineWaveShader.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'SineWaveShader',
    interfaces: [Shader]
  };
  function SolidShader() {
    SolidShader$Companion_getInstance();
    Shader.call(this, ShaderId$SOLID_getInstance());
  }
  SolidShader.prototype.createBuffer_ppt8xj$ = function (surface) {
    return new SolidShader$Buffer(this);
  };
  SolidShader.prototype.readBuffer_100t80$ = function (reader) {
    var $receiver = new SolidShader$Buffer(this);
    $receiver.read_100t80$(reader);
    return $receiver;
  };
  SolidShader.prototype.createRenderer_ppt8xj$ = function (surface) {
    return new SolidShader$Renderer();
  };
  function SolidShader$Companion() {
    SolidShader$Companion_instance = this;
  }
  SolidShader$Companion.prototype.parse_100t80$ = function (reader) {
    return new SolidShader();
  };
  SolidShader$Companion.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'Companion',
    interfaces: [ShaderReader]
  };
  var SolidShader$Companion_instance = null;
  function SolidShader$Companion_getInstance() {
    if (SolidShader$Companion_instance === null) {
      new SolidShader$Companion();
    }
    return SolidShader$Companion_instance;
  }
  function SolidShader$Buffer($outer) {
    this.$outer = $outer;
    this.color = Color$Companion_getInstance().WHITE;
  }
  Object.defineProperty(SolidShader$Buffer.prototype, 'shader', {
    get: function () {
      return this.$outer;
    }
  });
  SolidShader$Buffer.prototype.serialize_3kjoo0$ = function (writer) {
    this.color.serialize_3kjoo0$(writer);
  };
  SolidShader$Buffer.prototype.read_100t80$ = function (reader) {
    this.color = Color$Companion_getInstance().parse_100t80$(reader);
  };
  SolidShader$Buffer.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Buffer',
    interfaces: [Shader$Buffer]
  };
  function SolidShader$Renderer() {
  }
  SolidShader$Renderer.prototype.draw_b23bvv$ = function (buffer, pixelIndex) {
    return buffer.color;
  };
  SolidShader$Renderer.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Renderer',
    interfaces: [Shader$Renderer]
  };
  SolidShader.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'SolidShader',
    interfaces: [Shader]
  };
  function SparkleShader() {
    SparkleShader$Companion_getInstance();
    Shader.call(this, ShaderId$SPARKLE_getInstance());
  }
  SparkleShader.prototype.createBuffer_ppt8xj$ = function (surface) {
    return new SparkleShader$Buffer(this);
  };
  SparkleShader.prototype.readBuffer_100t80$ = function (reader) {
    var $receiver = new SparkleShader$Buffer(this);
    $receiver.read_100t80$(reader);
    return $receiver;
  };
  SparkleShader.prototype.createRenderer_ppt8xj$ = function (surface) {
    return new SparkleShader$Renderer();
  };
  function SparkleShader$Companion() {
    SparkleShader$Companion_instance = this;
  }
  SparkleShader$Companion.prototype.parse_100t80$ = function (reader) {
    return new SparkleShader();
  };
  SparkleShader$Companion.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'Companion',
    interfaces: [ShaderReader]
  };
  var SparkleShader$Companion_instance = null;
  function SparkleShader$Companion_getInstance() {
    if (SparkleShader$Companion_instance === null) {
      new SparkleShader$Companion();
    }
    return SparkleShader$Companion_instance;
  }
  function SparkleShader$Buffer($outer) {
    this.$outer = $outer;
    this.shader_g8lvgi$_0 = this.$outer;
    this.color = Color$Companion_getInstance().WHITE;
    this.sparkliness = 0.1;
  }
  Object.defineProperty(SparkleShader$Buffer.prototype, 'shader', {
    get: function () {
      return this.shader_g8lvgi$_0;
    }
  });
  SparkleShader$Buffer.prototype.serialize_3kjoo0$ = function (writer) {
    this.color.serialize_3kjoo0$(writer);
    writer.writeFloat_mx4ult$(this.sparkliness);
  };
  SparkleShader$Buffer.prototype.read_100t80$ = function (reader) {
    this.color = Color$Companion_getInstance().parse_100t80$(reader);
    this.sparkliness = reader.readFloat();
  };
  SparkleShader$Buffer.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Buffer',
    interfaces: [Shader$Buffer]
  };
  function SparkleShader$Renderer() {
  }
  SparkleShader$Renderer.prototype.draw_b23bvv$ = function (buffer, pixelIndex) {
    var tmp$;
    if (Random.Default.nextFloat() < buffer.sparkliness) {
      tmp$ = buffer.color;
    }
     else {
      tmp$ = Color$Companion_getInstance().BLACK;
    }
    return tmp$;
  };
  SparkleShader$Renderer.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Renderer',
    interfaces: [Shader$Renderer]
  };
  SparkleShader.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'SparkleShader',
    interfaces: [Shader]
  };
  function AllShows() {
    AllShows$Companion_getInstance();
  }
  function AllShows$Companion() {
    AllShows$Companion_instance = this;
    this.allShows = listOf([SolidColorShow_getInstance(), SomeDumbShow_getInstance(), RandomShow_getInstance(), CompositeShow_getInstance(), ThumpShow_getInstance(), PanelTweenShow_getInstance(), PixelTweenShow_getInstance(), LifeyShow_getInstance(), SimpleSpatialShow_getInstance(), HeartbleatShow_getInstance(), CreepingPixelsShow_getInstance(), GlslSandbox55301KotlinShow_getInstance(), GlslSandbox55301Show_getInstance(), GlslOtherShow_getInstance(), GlslSandbox56594Show_getInstance(), GlslSandbox56555Show_getInstance()]);
  }
  AllShows$Companion.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'Companion',
    interfaces: []
  };
  var AllShows$Companion_instance = null;
  function AllShows$Companion_getInstance() {
    if (AllShows$Companion_instance === null) {
      new AllShows$Companion();
    }
    return AllShows$Companion_instance;
  }
  AllShows.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'AllShows',
    interfaces: []
  };
  function CompositeShow() {
    CompositeShow_instance = this;
    Show.call(this, 'Composite');
  }
  function CompositeShow$createRenderer$ObjectLiteral(closure$showRunner) {
    this.closure$showRunner = closure$showRunner;
    this.colorPicker = closure$showRunner.getGadget_vedre8$('color', new ColorPicker('Color'));
    this.solidShader = new SolidShader();
    this.sineWaveShader = new SineWaveShader();
    var $receiver = closure$showRunner.allSurfaces;
    var result = LinkedHashMap_init_0(coerceAtLeast(mapCapacity(collectionSizeOrDefault($receiver, 10)), 16));
    var tmp$;
    tmp$ = $receiver.iterator();
    while (tmp$.hasNext()) {
      var element = tmp$.next();
      result.put_xwzc9p$(element, this.shaderBufsFor_0(element));
    }
    this.shaderBufs_0 = toMutableMap(result);
    var $receiver_0 = closure$showRunner.allMovingHeads;
    var destination = ArrayList_init_1(collectionSizeOrDefault($receiver_0, 10));
    var tmp$_0;
    tmp$_0 = $receiver_0.iterator();
    while (tmp$_0.hasNext()) {
      var item = tmp$_0.next();
      destination.add_11rb$(closure$showRunner.getMovingHeadBuffer_d2e776$(item));
    }
    this.movingHeadBuffers_0 = destination;
  }
  CompositeShow$createRenderer$ObjectLiteral.prototype.shaderBufsFor_0 = function (surface) {
    var solidShaderBuffer = this.closure$showRunner.getShaderBuffer_9rhubp$(surface, this.solidShader);
    var $receiver = this.closure$showRunner.getShaderBuffer_9rhubp$(surface, this.sineWaveShader);
    $receiver.density = Random.Default.nextFloat() * 20;
    var sineWaveShaderBuffer = $receiver;
    var compositorShaderBuffer = this.closure$showRunner.getCompositorBuffer_cn6wln$(surface, solidShaderBuffer, sineWaveShaderBuffer, CompositingMode$ADD_getInstance());
    return new CompositeShow$ShaderBufs(solidShaderBuffer, sineWaveShaderBuffer, compositorShaderBuffer);
  };
  CompositeShow$createRenderer$ObjectLiteral.prototype.nextFrame = function () {
    var theta = getTimeMillis().modulo(Kotlin.Long.fromInt(10000)).toNumber() / 1000.0 % (2 * math.PI);
    var i = {v: 0};
    var tmp$;
    tmp$ = this.shaderBufs_0.values.iterator();
    while (tmp$.hasNext()) {
      var element = tmp$.next();
      var tmp$_0;
      element.solidShaderBuffer.color = this.colorPicker.color;
      element.sineWaveShaderBuffer.color = Color$Companion_getInstance().WHITE;
      element.sineWaveShaderBuffer.theta = theta + (tmp$_0 = i.v, i.v = tmp$_0 + 1 | 0, tmp$_0);
      element.compositorShaderBuffer.mode = CompositingMode$ADD_getInstance();
      element.compositorShaderBuffer.fade = 1.0;
    }
    var tmp$_1;
    tmp$_1 = this.movingHeadBuffers_0.iterator();
    while (tmp$_1.hasNext()) {
      var element_0 = tmp$_1.next();
      element_0.color = this.colorPicker.color;
      element_0.pan = math.PI / 2;
      element_0.tilt = theta / 2;
    }
  };
  CompositeShow$createRenderer$ObjectLiteral.prototype.surfacesChanged_yroyvo$ = function (newSurfaces, removedSurfaces) {
    var tmp$;
    tmp$ = removedSurfaces.iterator();
    while (tmp$.hasNext()) {
      var element = tmp$.next();
      this.shaderBufs_0.remove_11rb$(element);
    }
    var tmp$_0;
    tmp$_0 = newSurfaces.iterator();
    while (tmp$_0.hasNext()) {
      var element_0 = tmp$_0.next();
      var $receiver = this.shaderBufs_0;
      var value = this.shaderBufsFor_0(element_0);
      $receiver.put_xwzc9p$(element_0, value);
    }
  };
  CompositeShow$createRenderer$ObjectLiteral.$metadata$ = {
    kind: Kind_CLASS,
    interfaces: [Show$Renderer]
  };
  CompositeShow.prototype.createRenderer_ccj26o$ = function (model, showRunner) {
    return new CompositeShow$createRenderer$ObjectLiteral(showRunner);
  };
  function CompositeShow$ShaderBufs(solidShaderBuffer, sineWaveShaderBuffer, compositorShaderBuffer) {
    this.solidShaderBuffer = solidShaderBuffer;
    this.sineWaveShaderBuffer = sineWaveShaderBuffer;
    this.compositorShaderBuffer = compositorShaderBuffer;
  }
  CompositeShow$ShaderBufs.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'ShaderBufs',
    interfaces: []
  };
  CompositeShow.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'CompositeShow',
    interfaces: [Show]
  };
  var CompositeShow_instance = null;
  function CompositeShow_getInstance() {
    if (CompositeShow_instance === null) {
      new CompositeShow();
    }
    return CompositeShow_instance;
  }
  function CreepingPixelsShow() {
    CreepingPixelsShow_instance = this;
    Show.call(this, 'Creeping Pixels');
  }
  function CreepingPixelsShow$createRenderer$ObjectLiteral(closure$colorPicker, closure$shaderBuffers) {
    this.closure$colorPicker = closure$colorPicker;
    this.closure$shaderBuffers = closure$shaderBuffers;
    this.i = 0;
  }
  CreepingPixelsShow$createRenderer$ObjectLiteral.prototype.nextFrame = function () {
    var color = this.closure$colorPicker.color;
    var tmp$;
    tmp$ = this.closure$shaderBuffers.iterator();
    while (tmp$.hasNext()) {
      var element = tmp$.next();
      element.palette[1] = color;
      element.setAll_za3lpa$(0);
      element.set_vux9f0$(this.i % element.colors.size, 1);
    }
    this.i = this.i + 1 | 0;
  };
  CreepingPixelsShow$createRenderer$ObjectLiteral.$metadata$ = {
    kind: Kind_CLASS,
    interfaces: [Show$Renderer]
  };
  CreepingPixelsShow.prototype.createRenderer_ccj26o$ = function (model, showRunner) {
    var colorPicker = showRunner.getGadget_vedre8$('color', new ColorPicker('Color'));
    var shader = new PixelShader(PixelShader$Encoding$INDEXED_2_getInstance());
    var $receiver = showRunner.allSurfaces;
    var destination = ArrayList_init_1(collectionSizeOrDefault($receiver, 10));
    var tmp$;
    tmp$ = $receiver.iterator();
    while (tmp$.hasNext()) {
      var item = tmp$.next();
      var tmp$_0 = destination.add_11rb$;
      var $receiver_0 = showRunner.getShaderBuffer_9rhubp$(item, shader);
      $receiver_0.palette[0] = Color$Companion_getInstance().BLACK;
      tmp$_0.call(destination, $receiver_0);
    }
    var shaderBuffers = destination;
    return new CreepingPixelsShow$createRenderer$ObjectLiteral(colorPicker, shaderBuffers);
  };
  CreepingPixelsShow.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'CreepingPixelsShow',
    interfaces: [Show]
  };
  var CreepingPixelsShow_instance = null;
  function CreepingPixelsShow_getInstance() {
    if (CreepingPixelsShow_instance === null) {
      new CreepingPixelsShow();
    }
    return CreepingPixelsShow_instance;
  }
  function GlslOtherShow() {
    GlslOtherShow_instance = this;
    GlslShow.call(this, 'GlslSandbox Some Other One (OpenGL)');
    this.program_t4pxqn$_0 = trimIndent('\nuniform float time;\nuniform vec2 resolution;\n\n#define PI 3.14159265359\n\nvoid main(void)\n{\n\tvec2 uv = gl_FragCoord.xy / resolution.xy;\n\tgl_FragColor = vec4(0);\n    gl_FragColor.r = sin(88.*uv.x) + sin(55.*uv.y) + 0.5+2.*sin(time*2.);\n}\n    ');
  }
  Object.defineProperty(GlslOtherShow.prototype, 'program', {
    get: function () {
      return this.program_t4pxqn$_0;
    }
  });
  GlslOtherShow.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'GlslOtherShow',
    interfaces: [GlslShow]
  };
  var GlslOtherShow_instance = null;
  function GlslOtherShow_getInstance() {
    if (GlslOtherShow_instance === null) {
      new GlslOtherShow();
    }
    return GlslOtherShow_instance;
  }
  function GlslSandbox55301KotlinShow() {
    GlslSandbox55301KotlinShow_instance = this;
    Show.call(this, 'GlslSandbox 55301 (kt)');
  }
  function GlslSandbox55301KotlinShow$createRenderer$ObjectLiteral() {
  }
  GlslSandbox55301KotlinShow$createRenderer$ObjectLiteral.prototype.nextFrame = function () {
  };
  GlslSandbox55301KotlinShow$createRenderer$ObjectLiteral.$metadata$ = {
    kind: Kind_CLASS,
    interfaces: [Show$Renderer]
  };
  GlslSandbox55301KotlinShow.prototype.createRenderer_ccj26o$ = function (model, showRunner) {
    var shader = new GlslSandbox55301Shader();
    var $receiver = showRunner.allSurfaces;
    var destination = ArrayList_init_1(collectionSizeOrDefault($receiver, 10));
    var tmp$;
    tmp$ = $receiver.iterator();
    while (tmp$.hasNext()) {
      var item = tmp$.next();
      destination.add_11rb$(showRunner.getShaderBuffer_9rhubp$(item, shader));
    }
    return new GlslSandbox55301KotlinShow$createRenderer$ObjectLiteral();
  };
  GlslSandbox55301KotlinShow.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'GlslSandbox55301KotlinShow',
    interfaces: [Show]
  };
  var GlslSandbox55301KotlinShow_instance = null;
  function GlslSandbox55301KotlinShow_getInstance() {
    if (GlslSandbox55301KotlinShow_instance === null) {
      new GlslSandbox55301KotlinShow();
    }
    return GlslSandbox55301KotlinShow_instance;
  }
  function GlslSandbox55301Show() {
    GlslSandbox55301Show_instance = this;
    GlslShow.call(this, 'GlslSandbox 55301 (OpenGL)');
    this.program_q080n4$_0 = '\n#ifdef GL_ES\nprecision mediump float;\n#endif\n\n// SPARKLEMOTION GADGET: Slider {name: "Scale", initialValue: 10.0, minValue: 0.0, maxValue: 100.0}\nuniform float scale;\n\nuniform float time;\nuniform vec2 resolution;\n\n#define N 6\n\nvoid main( void ) {\n\tvec2 v= (gl_FragCoord.xy-(resolution*0.5))/min(resolution.y,resolution.x)*scale;\n\tfloat t=time * 0.4,r=0.0;\n\tfor (int i=0;i<N;i++){\n\t\tfloat d=(3.14159265 / float(N))*(float(i)*5.0);\n\t\tr+=length(vec2(v.x,v.y))+0.01;\n\t\tv = vec2(v.x+cos(v.y+cos(r)+d)+cos(t),v.y-sin(v.x+cos(r)+d)+sin(t));\n\t}\n        r = (sin(r*0.1)*0.5)+0.5;\n\tr = pow(r, 128.0);\n\tgl_FragColor = vec4(r,pow(max(r-0.75,0.0)*4.0,2.0),pow(max(r-0.875,0.0)*8.0,4.0), 1.0 );\n//\tgl_FragColor = vec4(gl_FragCoord.x, gl_FragCoord.y, r, 1.0);\n}\n';
  }
  Object.defineProperty(GlslSandbox55301Show.prototype, 'program', {
    get: function () {
      return this.program_q080n4$_0;
    }
  });
  GlslSandbox55301Show.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'GlslSandbox55301Show',
    interfaces: [GlslShow]
  };
  var GlslSandbox55301Show_instance = null;
  function GlslSandbox55301Show_getInstance() {
    if (GlslSandbox55301Show_instance === null) {
      new GlslSandbox55301Show();
    }
    return GlslSandbox55301Show_instance;
  }
  function GlslSandbox56555Show() {
    GlslSandbox56555Show_instance = this;
    GlslShow.call(this, 'GlslSandbox 56555 (OpenGL)');
    this.program_jce69s$_0 = '\n/* lame-ass tunnel by kusma */\n\n#ifdef GL_ES\nprecision mediump float;\n#endif\n\nuniform float time;\nuniform vec2 mouse;\nuniform vec2 resolution;\n\nvoid main( void ) {\n\tvec2 position = (gl_FragCoord.xy - resolution * 0.5) / resolution.yy;\n\tfloat th = atan(position.y, position.x) / (2.0 * 3.1415926);\n\tth += mod(time*0.2, 1.0);\n\tfloat dd = length(position)+0.2*sin(th*2.0*3.1415926*6.0)*length(position);\n\tdd = pow(dd,1.0);\n\tfloat d = 0.5 / dd + 1.4*time;\n\n\tvec3 uv = vec3(th + d, th - d, th + sin(d));\n\tfloat a = 0.5 + cos(uv.x * 3.1415926 * 2.0) * 0.3;\n\tfloat b = 0.5 + cos(uv.y * 3.1415926 * 8.0) * 0.3;\n\tfloat c = 0.5 + cos(uv.z * 3.1415926 * 6.0) * 0.5;\n\tfloat f = abs(sin(time*2.0));\n\t\n\t\n\tvec3 color = mix(vec3(1.0, 0.8, 1.0-f), vec3(0.5*f, 0, 0), pow(a, 0.2)) * 3.;\n\tcolor += mix(vec3(0.8, 0.9, 1.0), vec3(0.1, 0.1, 0.2),  pow(b, 0.1)) * 0.75;\n\tcolor += mix(vec3(0.9, 0.8, 1.0), vec3(0.1, 0.2, 0.2),  pow(c, 0.1)) * 0.75;\n\tgl_FragColor = vec4(color * clamp(dd, 0.0, 1.0), 1.0);\n}\n';
  }
  Object.defineProperty(GlslSandbox56555Show.prototype, 'program', {
    get: function () {
      return this.program_jce69s$_0;
    }
  });
  GlslSandbox56555Show.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'GlslSandbox56555Show',
    interfaces: [GlslShow]
  };
  var GlslSandbox56555Show_instance = null;
  function GlslSandbox56555Show_getInstance() {
    if (GlslSandbox56555Show_instance === null) {
      new GlslSandbox56555Show();
    }
    return GlslSandbox56555Show_instance;
  }
  function GlslSandbox56594Show() {
    GlslSandbox56594Show_instance = this;
    GlslShow.call(this, 'GlslSandbox 56594 (OpenGL)');
    this.program_hppwyt$_0 = '\n/*\n * Original shader from: https://www.shadertoy.com/view/wt2GWK\n */\n\n#ifdef GL_ES\nprecision mediump float;\n#endif\n\n// glslsandbox uniforms\nuniform float time;\nuniform vec2 resolution;\n\n// shadertoy emulation\n#define iTime time\n#define iResolution resolution\n\n// --------[ Original ShaderToy begins here ]---------- //\n#define t iTime\n\nvec2 hash22(vec2 p) \n{\n    return fract(sin(vec2(dot(p,vec2(263.135,198.742)),dot(p,vec2(624.235,321.217))))*13.635); \n}\n\nfloat vorohearts( vec2 x)\n{\n    vec2 n = floor(-x);\n    vec2 f = fract(-x);\n    \n\tfloat md = 1.0;\n    \n    for( int j=-1; j<=3; j++ )\n\t{\n    \tfor( int i=-1; i<=3; i++ )\n    \t{\n        \tvec2  g = vec2(float(i), float(j));\n        \tvec2  o = hash22( n + g );\n\t    \tvec2  r = g - f + (.5+.5*sin(t+o*6.28));\n    \t\tr.x *= .8*(1.+sin(.5+sin(6.*t))*.03);\n    \t\tr.y *= .95+sin(1.5+sin(3.*t))*.07;\n    \t\tr.y += 1.08;\n\t\t\tr.y -= sqrt(abs(r.x))*.52;\n    \t\tfloat d = length(r);\n        \tif( d < md ) {md = d;}\n    \t}\n    }\n    return md;\n}\n\nvoid mainImage( out vec4 fragColor, in vec2 fragCoord )\n{\n    vec2 uv = (fragCoord - 10.5*iResolution.xy)/iResolution.y;\n    \n    //Heart uv\n    uv = uv*3.;\n\tfloat d = vorohearts(uv);\n    d = smoothstep(.8, 0.15, d);\n    \n    //Heart color\n    vec3 p = vec3(1.6*d*(.6+sin(.5+sin(6.*t))*.1), d*(1.8+sin(.5+sin(6.*t))*.3), 2.1*(1.+sin(.5+sin(6.*t))*.3));\n    vec3 col = vec3(d) * p;\n    \n    //Background\n    col += vorohearts(uv) * .35 * p;\n    \n    fragColor = vec4(col,1.);\n}\n// --------[ Original ShaderToy ends here ]---------- //\n\nvoid main(void)\n{\n    mainImage(gl_FragColor, gl_FragCoord.xy);\n}\n';
  }
  Object.defineProperty(GlslSandbox56594Show.prototype, 'program', {
    get: function () {
      return this.program_hppwyt$_0;
    }
  });
  GlslSandbox56594Show.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'GlslSandbox56594Show',
    interfaces: [GlslShow]
  };
  var GlslSandbox56594Show_instance = null;
  function GlslSandbox56594Show_getInstance() {
    if (GlslSandbox56594Show_instance === null) {
      new GlslSandbox56594Show();
    }
    return GlslSandbox56594Show_instance;
  }
  function GlslShow(name) {
    Show.call(this, name);
  }
  function GlslShow$createRenderer$ObjectLiteral(closure$buffers, closure$adjustableValuesToGadgets) {
    this.closure$buffers = closure$buffers;
    this.closure$adjustableValuesToGadgets = closure$adjustableValuesToGadgets;
  }
  GlslShow$createRenderer$ObjectLiteral.prototype.nextFrame = function () {
    var $receiver = this.closure$buffers;
    this.closure$adjustableValuesToGadgets;
    var tmp$;
    tmp$ = $receiver.iterator();
    while (tmp$.hasNext()) {
      var element = tmp$.next();
      var tmp$_0;
      tmp$_0 = this.closure$adjustableValuesToGadgets.entries.iterator();
      while (tmp$_0.hasNext()) {
        var element_0 = tmp$_0.next();
        var adjustableValue = element_0.key;
        var gadget = element_0.value;
        var tmp$_1;
        if (Kotlin.isType(gadget, Slider))
          tmp$_1 = gadget.value;
        else if (Kotlin.isType(gadget, ColorPicker))
          tmp$_1 = gadget.color;
        else
          throw IllegalArgumentException_init('unsupported gadget ' + gadget);
        var value = tmp$_1;
        element.update_egvix6$(adjustableValue, value);
      }
    }
  };
  GlslShow$createRenderer$ObjectLiteral.$metadata$ = {
    kind: Kind_CLASS,
    interfaces: [Show$Renderer]
  };
  GlslShow.prototype.createRenderer_ccj26o$ = function (model, showRunner) {
    var shader = new GlslShader(this.program);
    var $receiver = shader.adjustableValues;
    var result = LinkedHashMap_init_0(coerceAtLeast(mapCapacity(collectionSizeOrDefault($receiver, 10)), 16));
    var tmp$;
    tmp$ = $receiver.iterator();
    while (tmp$.hasNext()) {
      var element = tmp$.next();
      result.put_xwzc9p$(element, this.createGadget_j5u65$(element, showRunner));
    }
    var adjustableValuesToGadgets = result;
    var $receiver_0 = showRunner.allSurfaces;
    var destination = ArrayList_init_1(collectionSizeOrDefault($receiver_0, 10));
    var tmp$_0;
    tmp$_0 = $receiver_0.iterator();
    while (tmp$_0.hasNext()) {
      var item = tmp$_0.next();
      destination.add_11rb$(showRunner.getShaderBuffer_9rhubp$(item, shader));
    }
    var buffers = destination;
    return new GlslShow$createRenderer$ObjectLiteral(buffers, adjustableValuesToGadgets);
  };
  GlslShow.prototype.createGadget_j5u65$ = function ($receiver, showRunner) {
    var tmp$, tmp$_0, tmp$_1;
    var config = $receiver.config;
    var name = (tmp$ = config.getPrimitive_61zpoe$('name').contentOrNull) != null ? tmp$ : $receiver.varName;
    switch ($receiver.gadgetType) {
      case 'Slider':
        tmp$_1 = new Slider(name, (tmp$_0 = config.getPrimitive_61zpoe$('initialValue').floatOrNull) != null ? tmp$_0 : 1.0, config.getPrimitive_61zpoe$('minValue').floatOrNull, config.getPrimitive_61zpoe$('maxValue').floatOrNull);
        break;
      case 'ColorPicker':
        tmp$_1 = new ColorPicker(name);
        break;
      default:throw IllegalArgumentException_init('unknown gadget ' + $receiver.gadgetType);
    }
    var gadget = tmp$_1;
    return showRunner.getGadget_vedre8$(name, gadget);
  };
  GlslShow.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'GlslShow',
    interfaces: [Show]
  };
  function HeartbleatShow() {
    HeartbleatShow_instance = this;
    Show.call(this, 'Heartbleat');
  }
  function HeartbleatShow$createRenderer$ObjectLiteral(closure$showRunner) {
    this.closure$showRunner = closure$showRunner;
    var $receiver = closure$showRunner.allSurfaces;
    var destination = ArrayList_init();
    var tmp$;
    tmp$ = $receiver.iterator();
    while (tmp$.hasNext()) {
      var element = tmp$.next();
      if (Kotlin.isType(element, IdentifiedSurface) && HeartbleatShow_getInstance().get_number_kki6pr$(element) === 7)
        destination.add_11rb$(element);
    }
    var destination_0 = ArrayList_init_1(collectionSizeOrDefault(destination, 10));
    var tmp$_0;
    tmp$_0 = destination.iterator();
    while (tmp$_0.hasNext()) {
      var item = tmp$_0.next();
      destination_0.add_11rb$(closure$showRunner.getShaderBuffer_9rhubp$(item, new HeartShader()));
    }
    this.hearts = destination_0;
    this.heartSizeGadget = closure$showRunner.getGadget_vedre8$('heartSize', new Slider('Heart Size', 0.16));
    this.strokeSize = closure$showRunner.getGadget_vedre8$('strokeSize', new Slider('Stroke Size', 0.5));
    this.xOff = closure$showRunner.getGadget_vedre8$('xOff', new Slider('X Offset', 0.4));
    this.yOff = closure$showRunner.getGadget_vedre8$('yOff', new Slider('Y Offset', 0.67));
    var $receiver_0 = closure$showRunner.allUnusedSurfaces;
    var destination_1 = ArrayList_init_1(collectionSizeOrDefault($receiver_0, 10));
    var tmp$_1;
    tmp$_1 = $receiver_0.iterator();
    while (tmp$_1.hasNext()) {
      var item_0 = tmp$_1.next();
      destination_1.add_11rb$(closure$showRunner.getShaderBuffer_9rhubp$(item_0, new SolidShader()));
    }
    this.otherSurfaces = destination_1;
  }
  HeartbleatShow$createRenderer$ObjectLiteral.prototype.nextFrame = function () {
    var tmp$, tmp$_0;
    var currentBeat = this.closure$showRunner.currentBeat;
    var phase = currentBeat % 1.0 * 3.0;
    tmp$_0 = this.heartSizeGadget.value;
    if (phase > 1.5 && phase < 2.5) {
      var x = phase - 2;
      tmp$ = 1.0 + (0.5 - Math_0.abs(x)) / 4;
    }
     else if (phase > 2.5 || phase < 0.5) {
      if (phase > 2.5)
        phase -= 3;
      tmp$ = 1.0 + (0.5 - Math_0.abs(phase)) / 2;
    }
     else {
      tmp$ = 1.0;
    }
    var heartSize = tmp$_0 * tmp$;
    var tmp$_1;
    tmp$_1 = this.hearts.iterator();
    while (tmp$_1.hasNext()) {
      var element = tmp$_1.next();
      element.heartSize = heartSize;
      element.strokeSize = this.strokeSize.value;
      element.xOff = this.xOff.value;
      element.yOff = this.yOff.value;
    }
    var tmp$_2;
    tmp$_2 = this.otherSurfaces.iterator();
    while (tmp$_2.hasNext()) {
      var element_0 = tmp$_2.next();
      var tmp$_3 = Color_init_0(0.25, 0.25, 0.25);
      var tmp$_4 = Color_init_0(0.75, 0.3, 0.3);
      var x_0 = currentBeat / 4.0 * math.PI;
      element_0.color = tmp$_3.fade_6zkv30$(tmp$_4, Math_0.sin(x_0));
    }
  };
  HeartbleatShow$createRenderer$ObjectLiteral.$metadata$ = {
    kind: Kind_CLASS,
    interfaces: [Show$Renderer]
  };
  HeartbleatShow.prototype.createRenderer_ccj26o$ = function (model, showRunner) {
    var tmp$;
    Kotlin.isType(tmp$ = model, SheepModel) ? tmp$ : throwCCE();
    return new HeartbleatShow$createRenderer$ObjectLiteral(showRunner);
  };
  HeartbleatShow.prototype.get_number_kki6pr$ = function ($receiver) {
    var tmp$, tmp$_0, tmp$_1;
    return (tmp$_1 = (tmp$_0 = (tmp$ = Regex_init('\\d+').find_905azu$($receiver.name)) != null ? tmp$.value : null) != null ? toInt_0(tmp$_0) : null) != null ? tmp$_1 : -1;
  };
  HeartbleatShow.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'HeartbleatShow',
    interfaces: [Show]
  };
  var HeartbleatShow_instance = null;
  function HeartbleatShow_getInstance() {
    if (HeartbleatShow_instance === null) {
      new HeartbleatShow();
    }
    return HeartbleatShow_instance;
  }
  function LifeyShow() {
    LifeyShow_instance = this;
    Show.call(this, 'Lifey');
  }
  function LifeyShow$createRenderer$neighbors(closure$model) {
    return function ($receiver) {
      return closure$model.neighborsOf_jfju1k$($receiver);
    };
  }
  function LifeyShow$createRenderer$isSelected(closure$selectedPanels) {
    return function ($receiver) {
      return closure$selectedPanels.contains_11rb$($receiver);
    };
  }
  function LifeyShow$createRenderer$neighborsSelected(closure$neighbors, closure$selectedPanels) {
    return function ($receiver) {
      var $receiver_0 = closure$neighbors($receiver);
      var destination = ArrayList_init();
      var tmp$;
      tmp$ = $receiver_0.iterator();
      while (tmp$.hasNext()) {
        var element = tmp$.next();
        if (closure$selectedPanels.contains_11rb$(element))
          destination.add_11rb$(element);
      }
      return destination.size;
    };
  }
  function LifeyShow$createRenderer$ObjectLiteral(closure$speedSlider, closure$lastUpdateMs, closure$selectedPanels, closure$model, closure$isSelected, closure$neighborsSelected, closure$neighbors, closure$shaderBuffers) {
    this.closure$speedSlider = closure$speedSlider;
    this.closure$lastUpdateMs = closure$lastUpdateMs;
    this.closure$selectedPanels = closure$selectedPanels;
    this.closure$model = closure$model;
    this.closure$isSelected = closure$isSelected;
    this.closure$neighborsSelected = closure$neighborsSelected;
    this.closure$neighbors = closure$neighbors;
    this.closure$shaderBuffers = closure$shaderBuffers;
  }
  LifeyShow$createRenderer$ObjectLiteral.prototype.nextFrame = function () {
    var nowMs = getTimeMillis();
    var intervalMs = Kotlin.Long.fromNumber((1.0 - this.closure$speedSlider.value) * 1000);
    if (nowMs.compareTo_11rb$(this.closure$lastUpdateMs.v.add(intervalMs)) > 0) {
      if (this.closure$selectedPanels.isEmpty()) {
        var tmp$ = this.closure$selectedPanels;
        var $receiver = this.closure$model.allPanels;
        var destination = ArrayList_init();
        var tmp$_0;
        tmp$_0 = $receiver.iterator();
        while (tmp$_0.hasNext()) {
          var element = tmp$_0.next();
          if (Random.Default.nextFloat() < 0.5)
            destination.add_11rb$(element);
        }
        tmp$.addAll_brywnq$(destination);
      }
       else {
        var newSelectedPanels = ArrayList_init();
        var $receiver_0 = this.closure$selectedPanels;
        this.closure$isSelected;
        this.closure$neighborsSelected;
        this.closure$neighbors;
        var tmp$_1;
        tmp$_1 = $receiver_0.iterator();
        while (tmp$_1.hasNext()) {
          var element_0 = tmp$_1.next();
          var closure$isSelected = this.closure$isSelected;
          var closure$neighborsSelected = this.closure$neighborsSelected;
          var closure$neighbors = this.closure$neighbors;
          var living = closure$isSelected(element_0);
          var neighborsSelected = closure$neighborsSelected(element_0);
          if (living) {
            if (neighborsSelected < 1 || neighborsSelected > 3) {
              living = false;
              if (neighborsSelected === 0) {
                var moveToNeighbor = random(closure$neighbors(element_0));
                if (moveToNeighbor != null) {
                  newSelectedPanels.add_11rb$(moveToNeighbor);
                }
                living = false;
              }
            }
          }
           else {
            if (neighborsSelected === 2 || neighborsSelected === 3) {
              living = true;
            }
          }
          if (Random.Default.nextFloat() < 0.1) {
            living = !living;
          }
          if (living) {
            newSelectedPanels.add_11rb$(element_0);
          }
        }
        this.closure$selectedPanels.clear();
        this.closure$selectedPanels.addAll_brywnq$(newSelectedPanels);
      }
      this.closure$lastUpdateMs.v = nowMs;
    }
    var $receiver_1 = this.closure$shaderBuffers;
    this.closure$selectedPanels;
    var tmp$_2;
    tmp$_2 = $receiver_1.entries.iterator();
    while (tmp$_2.hasNext()) {
      var element_1 = tmp$_2.next();
      var closure$selectedPanels = this.closure$selectedPanels;
      var surface = element_1.key;
      var buffer = element_1.value;
      buffer.color = Kotlin.isType(surface, IdentifiedSurface) && contains(closure$selectedPanels, surface.modelSurface) ? Color$Companion_getInstance().WHITE : Color$Companion_getInstance().BLACK;
    }
  };
  LifeyShow$createRenderer$ObjectLiteral.$metadata$ = {
    kind: Kind_CLASS,
    interfaces: [Show$Renderer]
  };
  LifeyShow.prototype.createRenderer_ccj26o$ = function (model, showRunner) {
    var tmp$;
    Kotlin.isType(tmp$ = model, SheepModel) ? tmp$ : throwCCE();
    var speedSlider = showRunner.getGadget_vedre8$('speed', new Slider('Speed', 0.25));
    var shader = new SolidShader();
    var $receiver = showRunner.allSurfaces;
    var result = LinkedHashMap_init_0(coerceAtLeast(mapCapacity(collectionSizeOrDefault($receiver, 10)), 16));
    var tmp$_0;
    tmp$_0 = $receiver.iterator();
    while (tmp$_0.hasNext()) {
      var element = tmp$_0.next();
      var tmp$_1 = result.put_xwzc9p$;
      var $receiver_0 = showRunner.getShaderBuffer_9rhubp$(element, shader);
      $receiver_0.color = Color$Companion_getInstance().WHITE;
      tmp$_1.call(result, element, $receiver_0);
    }
    var shaderBuffers = result;
    var selectedPanels = ArrayList_init();
    var lastUpdateMs = {v: L0};
    var neighbors = LifeyShow$createRenderer$neighbors(model);
    var isSelected = LifeyShow$createRenderer$isSelected(selectedPanels);
    var neighborsSelected = LifeyShow$createRenderer$neighborsSelected(neighbors, selectedPanels);
    return new LifeyShow$createRenderer$ObjectLiteral(speedSlider, lastUpdateMs, selectedPanels, model, isSelected, neighborsSelected, neighbors, shaderBuffers);
  };
  LifeyShow.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'LifeyShow',
    interfaces: [Show]
  };
  var LifeyShow_instance = null;
  function LifeyShow_getInstance() {
    if (LifeyShow_instance === null) {
      new LifeyShow();
    }
    return LifeyShow_instance;
  }
  function PanelTweenShow() {
    PanelTweenShow_instance = this;
    Show.call(this, 'PanelTweenShow');
  }
  function PanelTweenShow$createRenderer$ObjectLiteral(closure$showRunner, closure$initialColors) {
    this.palettePicker = closure$showRunner.getGadget_vedre8$('palette', new PalettePicker('Palette', closure$initialColors));
    this.slider = closure$showRunner.getGadget_vedre8$('sparkliness', new Slider('Sparkliness', 0.0));
    this.solidShader = new SolidShader();
    this.sparkleShader = new SparkleShader();
    var $receiver = closure$showRunner.allSurfaces;
    var destination = ArrayList_init_1(collectionSizeOrDefault($receiver, 10));
    var tmp$;
    tmp$ = $receiver.iterator();
    while (tmp$.hasNext()) {
      var item = tmp$.next();
      var tmp$_0 = destination.add_11rb$;
      var solidShaderBuffer = closure$showRunner.getShaderBuffer_9rhubp$(item, this.solidShader);
      var sparkleShaderBuffer = closure$showRunner.getShaderBuffer_9rhubp$(item, this.sparkleShader);
      var compositorShaderBuffer = closure$showRunner.getCompositorBuffer_cn6wln$(item, solidShaderBuffer, sparkleShaderBuffer, CompositingMode$ADD_getInstance(), 1.0);
      tmp$_0.call(destination, new PanelTweenShow$Shaders(solidShaderBuffer, sparkleShaderBuffer, compositorShaderBuffer));
    }
    this.shaderBuffers = destination;
    this.fadeTimeMs = 500;
  }
  PanelTweenShow$createRenderer$ObjectLiteral.prototype.nextFrame = function () {
    var now = getTimeMillis().and(L268435455).toInt();
    var colors = this.palettePicker.colors;
    var tmp$, tmp$_0;
    var index = 0;
    tmp$ = this.shaderBuffers.iterator();
    while (tmp$.hasNext()) {
      var item = tmp$.next();
      var colorIndex = ((now / this.fadeTimeMs | 0) + checkIndexOverflow((tmp$_0 = index, index = tmp$_0 + 1 | 0, tmp$_0)) | 0) % colors.size;
      var startColor = colors.get_za3lpa$(colorIndex);
      var endColor = colors.get_za3lpa$((colorIndex + 1 | 0) % colors.size);
      var tweenedColor = startColor.fade_6zkv30$(endColor, now % this.fadeTimeMs / this.fadeTimeMs);
      item.solidShader.color = tweenedColor;
      item.sparkleShader.color = Color$Companion_getInstance().WHITE;
      item.sparkleShader.sparkliness = this.slider.value;
    }
  };
  PanelTweenShow$createRenderer$ObjectLiteral.$metadata$ = {
    kind: Kind_CLASS,
    interfaces: [Show$Renderer]
  };
  PanelTweenShow.prototype.createRenderer_ccj26o$ = function (model, showRunner) {
    var initialColors = listOf([Color$Companion_getInstance().fromString('#FF8A47'), Color$Companion_getInstance().fromString('#FC6170'), Color$Companion_getInstance().fromString('#8CEEEE'), Color$Companion_getInstance().fromString('#26BFBF'), Color$Companion_getInstance().fromString('#FFD747')]);
    return new PanelTweenShow$createRenderer$ObjectLiteral(showRunner, initialColors);
  };
  function PanelTweenShow$Shaders(solidShader, sparkleShader, compositorShader) {
    this.solidShader = solidShader;
    this.sparkleShader = sparkleShader;
    this.compositorShader = compositorShader;
  }
  PanelTweenShow$Shaders.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Shaders',
    interfaces: []
  };
  PanelTweenShow.prototype.get_number_y56fi1$ = function ($receiver) {
    var tmp$, tmp$_0, tmp$_1;
    return (tmp$_1 = (tmp$_0 = (tmp$ = Regex_init('\\d+').find_905azu$($receiver.name)) != null ? tmp$.value : null) != null ? toInt_0(tmp$_0) : null) != null ? tmp$_1 : -1;
  };
  PanelTweenShow.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'PanelTweenShow',
    interfaces: [Show]
  };
  var PanelTweenShow_instance = null;
  function PanelTweenShow_getInstance() {
    if (PanelTweenShow_instance === null) {
      new PanelTweenShow();
    }
    return PanelTweenShow_instance;
  }
  function PixelTweenShow() {
    PixelTweenShow_instance = this;
    Show.call(this, 'PixelTweenShow');
  }
  function PixelTweenShow$createRenderer$ObjectLiteral(closure$colorArray, closure$showRunner) {
    this.closure$colorArray = closure$colorArray;
    var $receiver = closure$showRunner.allSurfaces;
    var destination = ArrayList_init_1(collectionSizeOrDefault($receiver, 10));
    var tmp$;
    tmp$ = $receiver.iterator();
    while (tmp$.hasNext()) {
      var item = tmp$.next();
      destination.add_11rb$(closure$showRunner.getShaderBuffer_9rhubp$(item, new PixelShader()));
    }
    this.shaderBuffers = destination;
    this.fadeTimeMs = 1000;
  }
  PixelTweenShow$createRenderer$ObjectLiteral.prototype.nextFrame = function () {
    var now = getTimeMillis().and(L268435455).toInt();
    var $receiver = this.shaderBuffers;
    this.closure$colorArray;
    var tmp$, tmp$_0;
    var index = 0;
    tmp$ = $receiver.iterator();
    while (tmp$.hasNext()) {
      var item = tmp$.next();
      var closure$colorArray = this.closure$colorArray;
      var colorIndex = ((now / this.fadeTimeMs | 0) + checkIndexOverflow((tmp$_0 = index, index = tmp$_0 + 1 | 0, tmp$_0)) | 0) % closure$colorArray.length;
      var startColor = closure$colorArray[colorIndex];
      var endColor = closure$colorArray[(colorIndex + 1 | 0) % closure$colorArray.length];
      var colors = item.colors;
      var tmp$_1, tmp$_0_0;
      var index_0 = 0;
      tmp$_1 = colors.iterator();
      while (tmp$_1.hasNext()) {
        var item_0 = tmp$_1.next();
        var index_1 = checkIndexOverflow((tmp$_0_0 = index_0, index_0 = tmp$_0_0 + 1 | 0, tmp$_0_0));
        if (Random.Default.nextFloat() < 0.1) {
          colors.set_wxm5ur$(index_1, Color$Companion_getInstance().WHITE);
        }
         else {
          var tweenedColor = startColor.fade_6zkv30$(endColor, (now + index_1 | 0) % this.fadeTimeMs / this.fadeTimeMs);
          colors.set_wxm5ur$(index_1, tweenedColor);
        }
      }
    }
  };
  PixelTweenShow$createRenderer$ObjectLiteral.$metadata$ = {
    kind: Kind_CLASS,
    interfaces: [Show$Renderer]
  };
  PixelTweenShow.prototype.createRenderer_ccj26o$ = function (model, showRunner) {
    var colorArray = [Color$Companion_getInstance().fromString('#FF8A47'), Color$Companion_getInstance().fromString('#FC6170'), Color$Companion_getInstance().fromString('#8CEEEE'), Color$Companion_getInstance().fromString('#26BFBF'), Color$Companion_getInstance().fromString('#FFD747')];
    return new PixelTweenShow$createRenderer$ObjectLiteral(colorArray, showRunner);
  };
  PixelTweenShow.prototype.get_number_y56fi1$ = function ($receiver) {
    var tmp$, tmp$_0, tmp$_1;
    return (tmp$_1 = (tmp$_0 = (tmp$ = Regex_init('\\d+').find_905azu$($receiver.name)) != null ? tmp$.value : null) != null ? toInt_0(tmp$_0) : null) != null ? tmp$_1 : -1;
  };
  PixelTweenShow.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'PixelTweenShow',
    interfaces: [Show]
  };
  var PixelTweenShow_instance = null;
  function PixelTweenShow_getInstance() {
    if (PixelTweenShow_instance === null) {
      new PixelTweenShow();
    }
    return PixelTweenShow_instance;
  }
  function RandomShow() {
    RandomShow_instance = this;
    Show.call(this, 'Random');
  }
  function RandomShow$createRenderer$ObjectLiteral(closure$showRunner, closure$model) {
    var $receiver = closure$showRunner.allSurfaces;
    var destination = ArrayList_init_1(collectionSizeOrDefault($receiver, 10));
    var tmp$;
    tmp$ = $receiver.iterator();
    while (tmp$.hasNext()) {
      var item = tmp$.next();
      destination.add_11rb$(closure$showRunner.getShaderBuffer_9rhubp$(item, new RandomShader()));
    }
    var $receiver_0 = closure$model.movingHeads;
    var destination_0 = ArrayList_init_1(collectionSizeOrDefault($receiver_0, 10));
    var tmp$_0;
    tmp$_0 = $receiver_0.iterator();
    while (tmp$_0.hasNext()) {
      var item_0 = tmp$_0.next();
      destination_0.add_11rb$(closure$showRunner.getMovingHeadBuffer_d2e776$(item_0));
    }
    this.movingHeadBuffers = destination_0;
  }
  RandomShow$createRenderer$ObjectLiteral.prototype.nextFrame = function () {
    var tmp$;
    tmp$ = this.movingHeadBuffers.iterator();
    while (tmp$.hasNext()) {
      var element = tmp$.next();
      element.color = Color$Companion_getInstance().random();
      element.pan = Random.Default.nextFloat() * Shenzarpy$Companion_getInstance().panRange.endInclusive;
      element.tilt = Random.Default.nextFloat() * Shenzarpy$Companion_getInstance().tiltRange.endInclusive;
    }
  };
  RandomShow$createRenderer$ObjectLiteral.$metadata$ = {
    kind: Kind_CLASS,
    interfaces: [Show$Renderer]
  };
  RandomShow.prototype.createRenderer_ccj26o$ = function (model, showRunner) {
    return new RandomShow$createRenderer$ObjectLiteral(showRunner, model);
  };
  RandomShow.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'RandomShow',
    interfaces: [Show]
  };
  var RandomShow_instance = null;
  function RandomShow_getInstance() {
    if (RandomShow_instance === null) {
      new RandomShow();
    }
    return RandomShow_instance;
  }
  function SimpleSpatialShow() {
    SimpleSpatialShow_instance = this;
    Show.call(this, 'Spatial');
  }
  function SimpleSpatialShow$createRenderer$ObjectLiteral(closure$shaderBuffers, closure$colorPicker, closure$centerXSlider, closure$centerYSlider, closure$radiusSlider) {
    this.closure$shaderBuffers = closure$shaderBuffers;
    this.closure$colorPicker = closure$colorPicker;
    this.closure$centerXSlider = closure$centerXSlider;
    this.closure$centerYSlider = closure$centerYSlider;
    this.closure$radiusSlider = closure$radiusSlider;
  }
  SimpleSpatialShow$createRenderer$ObjectLiteral.prototype.nextFrame = function () {
    var $receiver = this.closure$shaderBuffers;
    this.closure$colorPicker;
    this.closure$centerXSlider;
    this.closure$centerYSlider;
    this.closure$radiusSlider;
    var tmp$;
    tmp$ = $receiver.iterator();
    while (tmp$.hasNext()) {
      var element = tmp$.next();
      var closure$colorPicker = this.closure$colorPicker;
      var closure$centerXSlider = this.closure$centerXSlider;
      var closure$centerYSlider = this.closure$centerYSlider;
      var closure$radiusSlider = this.closure$radiusSlider;
      element.color = closure$colorPicker.color;
      element.centerX = closure$centerXSlider.value;
      element.centerY = closure$centerYSlider.value;
      element.radius = closure$radiusSlider.value;
    }
  };
  SimpleSpatialShow$createRenderer$ObjectLiteral.$metadata$ = {
    kind: Kind_CLASS,
    interfaces: [Show$Renderer]
  };
  SimpleSpatialShow.prototype.createRenderer_ccj26o$ = function (model, showRunner) {
    var colorPicker = showRunner.getGadget_vedre8$('color', new ColorPicker('Color'));
    var centerXSlider = showRunner.getGadget_vedre8$('centerX', new Slider('center X', 0.5));
    var centerYSlider = showRunner.getGadget_vedre8$('centerY', new Slider('center Y', 0.5));
    var radiusSlider = showRunner.getGadget_vedre8$('radius', new Slider('radius', 0.25));
    var shader = new SimpleSpatialShader();
    var $receiver = showRunner.allSurfaces;
    var destination = ArrayList_init_1(collectionSizeOrDefault($receiver, 10));
    var tmp$;
    tmp$ = $receiver.iterator();
    while (tmp$.hasNext()) {
      var item = tmp$.next();
      destination.add_11rb$(showRunner.getShaderBuffer_9rhubp$(item, shader));
    }
    var shaderBuffers = destination;
    return new SimpleSpatialShow$createRenderer$ObjectLiteral(shaderBuffers, colorPicker, centerXSlider, centerYSlider, radiusSlider);
  };
  SimpleSpatialShow.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'SimpleSpatialShow',
    interfaces: [Show]
  };
  var SimpleSpatialShow_instance = null;
  function SimpleSpatialShow_getInstance() {
    if (SimpleSpatialShow_instance === null) {
      new SimpleSpatialShow();
    }
    return SimpleSpatialShow_instance;
  }
  function SolidColorShow() {
    SolidColorShow_instance = this;
    Show.call(this, 'Solid Color');
  }
  function SolidColorShow$createRenderer$ObjectLiteral(closure$colorPicker, closure$shaderBuffers, closure$eyes) {
    this.closure$colorPicker = closure$colorPicker;
    this.closure$shaderBuffers = closure$shaderBuffers;
    this.closure$eyes = closure$eyes;
    this.priorColor = closure$colorPicker.color;
  }
  SolidColorShow$createRenderer$ObjectLiteral.prototype.nextFrame = function () {
    var tmp$;
    var color = {v: this.closure$colorPicker.color};
    if (!((tmp$ = color.v) != null ? tmp$.equals(this.priorColor) : null)) {
      var tmp$_0;
      tmp$_0 = this.closure$shaderBuffers.iterator();
      while (tmp$_0.hasNext()) {
        var element = tmp$_0.next();
        element.color = color.v;
      }
      var tmp$_1;
      tmp$_1 = this.closure$eyes.iterator();
      while (tmp$_1.hasNext()) {
        var element_0 = tmp$_1.next();
        element_0.color = color.v;
      }
      this.priorColor = color.v;
    }
  };
  SolidColorShow$createRenderer$ObjectLiteral.$metadata$ = {
    kind: Kind_CLASS,
    interfaces: [Show$Renderer]
  };
  SolidColorShow.prototype.createRenderer_ccj26o$ = function (model, showRunner) {
    var colorPicker = showRunner.getGadget_vedre8$('color', new ColorPicker('Color'));
    var shader = new SolidShader();
    var $receiver = showRunner.allSurfaces;
    var destination = ArrayList_init_1(collectionSizeOrDefault($receiver, 10));
    var tmp$;
    tmp$ = $receiver.iterator();
    while (tmp$.hasNext()) {
      var item = tmp$.next();
      var tmp$_0 = destination.add_11rb$;
      var $receiver_0 = showRunner.getShaderBuffer_9rhubp$(item, shader);
      $receiver_0.color = Color$Companion_getInstance().WHITE;
      tmp$_0.call(destination, $receiver_0);
    }
    var shaderBuffers = destination;
    var $receiver_1 = model.movingHeads;
    var destination_0 = ArrayList_init_1(collectionSizeOrDefault($receiver_1, 10));
    var tmp$_1;
    tmp$_1 = $receiver_1.iterator();
    while (tmp$_1.hasNext()) {
      var item_0 = tmp$_1.next();
      destination_0.add_11rb$(showRunner.getMovingHeadBuffer_d2e776$(item_0));
    }
    var eyes = destination_0;
    return new SolidColorShow$createRenderer$ObjectLiteral(colorPicker, shaderBuffers, eyes);
  };
  SolidColorShow.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'SolidColorShow',
    interfaces: [Show]
  };
  var SolidColorShow_instance = null;
  function SolidColorShow_getInstance() {
    if (SolidColorShow_instance === null) {
      new SolidColorShow();
    }
    return SolidColorShow_instance;
  }
  function SomeDumbShow() {
    SomeDumbShow_instance = this;
    Show.call(this, 'SomeDumbShow');
  }
  function SomeDumbShow$createRenderer$ObjectLiteral(closure$showRunner, closure$model) {
    this.colorPicker = closure$showRunner.getGadget_vedre8$('color', new ColorPicker('Color'));
    this.pixelShader = new PixelShader();
    var $receiver = closure$showRunner.allSurfaces;
    var destination = ArrayList_init_1(collectionSizeOrDefault($receiver, 10));
    var tmp$;
    tmp$ = $receiver.iterator();
    while (tmp$.hasNext()) {
      var item = tmp$.next();
      destination.add_11rb$(closure$showRunner.getShaderBuffer_9rhubp$(item, this.pixelShader));
    }
    this.pixelShaderBuffers = destination;
    var $receiver_0 = closure$model.movingHeads;
    var destination_0 = ArrayList_init_1(collectionSizeOrDefault($receiver_0, 10));
    var tmp$_0;
    tmp$_0 = $receiver_0.iterator();
    while (tmp$_0.hasNext()) {
      var item_0 = tmp$_0.next();
      destination_0.add_11rb$(closure$showRunner.getMovingHeadBuffer_d2e776$(item_0));
    }
    this.movingHeads = destination_0;
  }
  function SomeDumbShow$createRenderer$ObjectLiteral$nextFrame$nextTimeShiftedFloat(closure$now) {
    return function ($receiver) {
      var x = $receiver.nextFloat() + closure$now.toNumber() / 1000.0;
      return Math_0.sin(x);
    };
  }
  function SomeDumbShow$createRenderer$ObjectLiteral$nextFrame$desaturateRandomishly($receiver, baseSaturation, seed) {
    var x = seed.nextFloat();
    return $receiver.withSaturation_mx4ult$(baseSaturation * Math_0.abs(x));
  }
  SomeDumbShow$createRenderer$ObjectLiteral.prototype.nextFrame = function () {
    var seed = Random_0(0);
    var now = getTimeMillis();
    var nextTimeShiftedFloat = SomeDumbShow$createRenderer$ObjectLiteral$nextFrame$nextTimeShiftedFloat(now);
    var desaturateRandomishly = SomeDumbShow$createRenderer$ObjectLiteral$nextFrame$desaturateRandomishly;
    var tmp$;
    tmp$ = this.pixelShaderBuffers.iterator();
    while (tmp$.hasNext()) {
      var element = tmp$.next();
      var tmp$_0;
      var baseSaturation = seed.nextFloat();
      var panelColor = nextTimeShiftedFloat(seed) < 0.1 ? Color$Companion_getInstance().random() : this.colorPicker.color;
      tmp$_0 = element.colors;
      for (var i = 0; i !== tmp$_0.size; ++i) {
        element.colors.set_wxm5ur$(i, desaturateRandomishly(panelColor, baseSaturation, seed));
      }
    }
    var tmp$_1;
    tmp$_1 = this.movingHeads.iterator();
    while (tmp$_1.hasNext()) {
      var element_0 = tmp$_1.next();
      element_0.color = this.colorPicker.color;
      element_0.pan = element_0.pan + (nextTimeShiftedFloat(seed) - 0.5) / 5;
      element_0.tilt = element_0.tilt + (nextTimeShiftedFloat(seed) - 0.5) / 5;
    }
  };
  SomeDumbShow$createRenderer$ObjectLiteral.$metadata$ = {
    kind: Kind_CLASS,
    interfaces: [Show$Renderer]
  };
  SomeDumbShow.prototype.createRenderer_ccj26o$ = function (model, showRunner) {
    return new SomeDumbShow$createRenderer$ObjectLiteral(showRunner, model);
  };
  SomeDumbShow.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'SomeDumbShow',
    interfaces: [Show]
  };
  var SomeDumbShow_instance = null;
  function SomeDumbShow_getInstance() {
    if (SomeDumbShow_instance === null) {
      new SomeDumbShow();
    }
    return SomeDumbShow_instance;
  }
  function ThumpShow() {
    ThumpShow_instance = this;
    Show.call(this, 'Thump');
  }
  function ThumpShow$createRenderer$ObjectLiteral(closure$showRunner, closure$model) {
    this.closure$showRunner = closure$showRunner;
    this.beatSource_0 = closure$showRunner.getBeatSource();
    this.colorPicker = closure$showRunner.getGadget_vedre8$('color', new ColorPicker('Color'));
    this.solidShader = new SolidShader();
    this.sineWaveShader = new SineWaveShader();
    this.compositorShader = new CompositorShader(this.solidShader, this.sineWaveShader);
    var $receiver = closure$showRunner.allSurfaces;
    var destination = ArrayList_init_1(collectionSizeOrDefault($receiver, 10));
    var tmp$;
    tmp$ = $receiver.iterator();
    while (tmp$.hasNext()) {
      var item = tmp$.next();
      var tmp$_0 = destination.add_11rb$;
      var solidShaderBuffer = closure$showRunner.getShaderBuffer_9rhubp$(item, this.solidShader);
      var $receiver_0 = closure$showRunner.getShaderBuffer_9rhubp$(item, this.sineWaveShader);
      $receiver_0.density = Random.Default.nextFloat() * 20;
      var sineWaveShaderBuffer = $receiver_0;
      var compositorShaderBuffer = closure$showRunner.getCompositorBuffer_cn6wln$(item, solidShaderBuffer, sineWaveShaderBuffer, CompositingMode$ADD_getInstance(), 1.0);
      tmp$_0.call(destination, new ThumpShow$ShaderBufs(solidShaderBuffer, sineWaveShaderBuffer, compositorShaderBuffer));
    }
    this.shaderBufs_0 = destination;
    var $receiver_1 = closure$model.movingHeads;
    var destination_0 = ArrayList_init_1(collectionSizeOrDefault($receiver_1, 10));
    var tmp$_1;
    tmp$_1 = $receiver_1.iterator();
    while (tmp$_1.hasNext()) {
      var item_0 = tmp$_1.next();
      destination_0.add_11rb$(closure$showRunner.getMovingHeadBuffer_d2e776$(item_0));
    }
    this.movingHeadBuffers_0 = destination_0;
  }
  ThumpShow$createRenderer$ObjectLiteral.prototype.nextFrame = function () {
    var beat = this.closure$showRunner.currentBeat;
    var i = 0;
    var beatColor = Color$Companion_getInstance().WHITE.fade_6zkv30$(this.colorPicker.color, beat % 1);
    var tmp$;
    tmp$ = this.shaderBufs_0.iterator();
    while (tmp$.hasNext()) {
      var element = tmp$.next();
      element.solidShaderBuffer.color = beatColor;
      element.sineWaveShaderBuffer.color = beatColor;
      element.compositorShaderBuffer.mode = CompositingMode$ADD_getInstance();
      element.compositorShaderBuffer.fade = 1.0;
    }
    var tmp$_0;
    tmp$_0 = this.movingHeadBuffers_0.iterator();
    while (tmp$_0.hasNext()) {
      var element_0 = tmp$_0.next();
      element_0.color = beatColor;
      element_0.pan = roundToInt(beat) / 2;
      element_0.tilt = roundToInt(beat) / 2;
    }
  };
  ThumpShow$createRenderer$ObjectLiteral.$metadata$ = {
    kind: Kind_CLASS,
    interfaces: [Show$Renderer]
  };
  ThumpShow.prototype.createRenderer_ccj26o$ = function (model, showRunner) {
    return new ThumpShow$createRenderer$ObjectLiteral(showRunner, model);
  };
  function ThumpShow$ShaderBufs(solidShaderBuffer, sineWaveShaderBuffer, compositorShaderBuffer) {
    this.solidShaderBuffer = solidShaderBuffer;
    this.sineWaveShaderBuffer = sineWaveShaderBuffer;
    this.compositorShaderBuffer = compositorShaderBuffer;
  }
  ThumpShow$ShaderBufs.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'ShaderBufs',
    interfaces: []
  };
  ThumpShow.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'ThumpShow',
    interfaces: [Show]
  };
  var ThumpShow_instance = null;
  function ThumpShow_getInstance() {
    if (ThumpShow_instance === null) {
      new ThumpShow();
    }
    return ThumpShow_instance;
  }
  function FakeDmxUniverse() {
    Dmx$Universe.call(this);
    this.channelsOut_0 = new Int8Array(512);
    this.channelsIn_0 = new Int8Array(512);
    this.listeners_0 = ArrayList_init();
  }
  FakeDmxUniverse.prototype.writer_vux9f0$ = function (baseChannel, channelCount) {
    return new Dmx$Buffer(this.channelsOut_0, baseChannel, channelCount);
  };
  FakeDmxUniverse.prototype.reader_sxjeop$ = function (baseChannel, channelCount, listener) {
    this.listeners_0.add_11rb$(listener);
    return new Dmx$Buffer(this.channelsIn_0, baseChannel, channelCount);
  };
  FakeDmxUniverse.prototype.sendFrame = function () {
    var $receiver = this.channelsOut_0;
    arrayCopy($receiver, this.channelsIn_0, 0, 0, $receiver.length);
    this.updateListeners_0();
  };
  FakeDmxUniverse.prototype.allOff = function () {
    for (var i = 0; i < 512; i++)
      this.channelsIn_0[i] = 0;
    this.updateListeners_0();
  };
  FakeDmxUniverse.prototype.updateListeners_0 = function () {
    var tmp$;
    tmp$ = this.listeners_0.iterator();
    while (tmp$.hasNext()) {
      var element = tmp$.next();
      element();
    }
  };
  FakeDmxUniverse.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'FakeDmxUniverse',
    interfaces: [Dmx$Universe]
  };
  function FakeFs() {
    this.files_0 = LinkedHashMap_init();
  }
  FakeFs.prototype.listFiles_61zpoe$ = function (path) {
    logger$Companion_getInstance().debug_61zpoe$('FakeFs.listFiles(' + path + ')');
    var $receiver = this.files_0.keys;
    var destination = ArrayList_init();
    var tmp$;
    tmp$ = $receiver.iterator();
    while (tmp$.hasNext()) {
      var element = tmp$.next();
      if (startsWith(element, path + '/'))
        destination.add_11rb$(element);
    }
    return destination;
  };
  FakeFs.prototype.loadFile_61zpoe$ = function (path) {
    var tmp$;
    logger$Companion_getInstance().debug_61zpoe$('FakeFs.loadFile(' + path + ')');
    return (tmp$ = this.files_0.get_11rb$(path)) != null ? decodeToString(tmp$) : null;
  };
  FakeFs.prototype.createFile_7x97xx$$default = function (path, content, allowOverwrite) {
    logger$Companion_getInstance().debug_61zpoe$('FakeFs.createFile(' + path + ') -> ' + content.length + ' bytes');
    this.addFile_0(path, content);
  };
  FakeFs.prototype.createFile_qz9155$$default = function (path, content, allowOverwrite) {
    this.createFile_7x97xx$(path, encodeToByteArray(content), allowOverwrite);
  };
  FakeFs.prototype.addFile_0 = function (path, content) {
    if (this.files_0.containsKey_11rb$(path)) {
      throw Exception_init(path + ' already exists');
    }
    this.files_0.put_xwzc9p$(path, content);
  };
  FakeFs.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'FakeFs',
    interfaces: [Fs]
  };
  function FakeNetwork(networkDelay, display, coroutineContext) {
    if (networkDelay === void 0)
      networkDelay = 1;
    if (display === void 0)
      display = null;
    if (coroutineContext === void 0)
      coroutineContext = coroutines_0.EmptyCoroutineContext;
    this.networkDelay_1 = networkDelay;
    this.display_0 = display;
    this.coroutineScope_0 = new FakeNetwork$coroutineScope$ObjectLiteral(coroutineContext);
    this.nextAddress_0 = 45071;
    this.udpListeners_0 = HashMap_init();
    this.udpListenersByPort_0 = HashMap_init();
    this.httpServersByPort_0 = HashMap_init();
  }
  FakeNetwork.prototype.link = function () {
    var tmp$;
    var address = new FakeNetwork$FakeAddress((tmp$ = this.nextAddress_0, this.nextAddress_0 = tmp$ + 1 | 0, tmp$));
    return new FakeNetwork$FakeLink(this, address);
  };
  FakeNetwork.prototype.sendPacketSuccess_0 = function () {
    return Random.Default.nextFloat() > this.packetLossRate_0() / 2;
  };
  FakeNetwork.prototype.receivePacketSuccess_0 = function () {
    return Random.Default.nextFloat() > this.packetLossRate_0() / 2;
  };
  FakeNetwork.prototype.packetLossRate_0 = function () {
    var tmp$, tmp$_0;
    return (tmp$_0 = (tmp$ = this.display_0) != null ? tmp$.packetLossRate : null) != null ? tmp$_0 : 0.0;
  };
  function FakeNetwork$FakeLink($outer, myAddress) {
    this.$outer = $outer;
    this.myAddress_npb8zl$_0 = myAddress;
    this.udpMtu_jnv15u$_0 = 1500;
    this.nextAvailablePort_0 = 65000;
  }
  Object.defineProperty(FakeNetwork$FakeLink.prototype, 'myAddress', {
    get: function () {
      return this.myAddress_npb8zl$_0;
    }
  });
  Object.defineProperty(FakeNetwork$FakeLink.prototype, 'udpMtu', {
    get: function () {
      return this.udpMtu_jnv15u$_0;
    }
  });
  FakeNetwork$FakeLink.prototype.listenUdp_a6m852$ = function (port, udpListener) {
    var tmp$;
    var serverPort = port === 0 ? (tmp$ = this.nextAvailablePort_0, this.nextAvailablePort_0 = tmp$ + 1 | 0, tmp$) : port;
    this.$outer.udpListeners_0.put_xwzc9p$(new Pair(this.myAddress, serverPort), udpListener);
    var $receiver = this.$outer.udpListenersByPort_0;
    var tmp$_0;
    var value = $receiver.get_11rb$(serverPort);
    if (value == null) {
      var answer = ArrayList_init();
      $receiver.put_xwzc9p$(serverPort, answer);
      tmp$_0 = answer;
    }
     else {
      tmp$_0 = value;
    }
    var portListeners = tmp$_0;
    portListeners.add_11rb$(udpListener);
    return new FakeNetwork$FakeLink$FakeUdpSocket(this, serverPort);
  };
  FakeNetwork$FakeLink.prototype.startHttpServer_za3lpa$ = function (port) {
    var fakeHttpServer = new FakeNetwork$FakeLink$FakeHttpServer(this, port);
    var $receiver = this.$outer.httpServersByPort_0;
    var key = to(this.myAddress, port);
    $receiver.put_xwzc9p$(key, fakeHttpServer);
    return fakeHttpServer;
  };
  function Coroutine$FakeNetwork$FakeLink$connectWebSocket$lambda(this$FakeNetwork_0, closure$webSocketListener_0, closure$connection_0, $receiver_0, controller, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.$controller = controller;
    this.exceptionState_0 = 1;
    this.local$this$FakeNetwork = this$FakeNetwork_0;
    this.local$closure$webSocketListener = closure$webSocketListener_0;
    this.local$closure$connection = closure$connection_0;
  }
  Coroutine$FakeNetwork$FakeLink$connectWebSocket$lambda.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$FakeNetwork$FakeLink$connectWebSocket$lambda.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$FakeNetwork$FakeLink$connectWebSocket$lambda.prototype.constructor = Coroutine$FakeNetwork$FakeLink$connectWebSocket$lambda;
  Coroutine$FakeNetwork$FakeLink$connectWebSocket$lambda.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.state_0 = 2;
            this.result_0 = this.local$this$FakeNetwork.networkDelay_0(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 1:
            throw this.exception_0;
          case 2:
            return this.local$closure$webSocketListener.reset_67ozxy$(this.local$closure$connection), Unit;
          default:this.state_0 = 1;
            throw new Error('State Machine Unreachable execution');
        }
      }
       catch (e) {
        if (this.state_0 === 1) {
          this.exceptionState_0 = this.state_0;
          throw e;
        }
         else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  function FakeNetwork$FakeLink$connectWebSocket$lambda(this$FakeNetwork_0, closure$webSocketListener_0, closure$connection_0) {
    return function ($receiver_0, continuation_0, suspended) {
      var instance = new Coroutine$FakeNetwork$FakeLink$connectWebSocket$lambda(this$FakeNetwork_0, closure$webSocketListener_0, closure$connection_0, $receiver_0, this, continuation_0);
      if (suspended)
        return instance;
      else
        return instance.doResume(null);
    };
  }
  function FakeNetwork$FakeLink$connectWebSocket$lambda_0(closure$clientSideConnection) {
    return function () {
      return closure$clientSideConnection.v == null ? throwUPAE('clientSideConnection') : closure$clientSideConnection.v;
    };
  }
  function FakeNetwork$FakeLink$connectWebSocket$lambda_1(closure$serverSideConnection) {
    return function () {
      return closure$serverSideConnection;
    };
  }
  function Coroutine$FakeNetwork$FakeLink$connectWebSocket$lambda_0(this$FakeNetwork_0, closure$webSocketListener_0, closure$clientSideConnection_0, $receiver_0, controller, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.$controller = controller;
    this.exceptionState_0 = 1;
    this.local$this$FakeNetwork = this$FakeNetwork_0;
    this.local$closure$webSocketListener = closure$webSocketListener_0;
    this.local$closure$clientSideConnection = closure$clientSideConnection_0;
  }
  Coroutine$FakeNetwork$FakeLink$connectWebSocket$lambda_0.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$FakeNetwork$FakeLink$connectWebSocket$lambda_0.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$FakeNetwork$FakeLink$connectWebSocket$lambda_0.prototype.constructor = Coroutine$FakeNetwork$FakeLink$connectWebSocket$lambda_0;
  Coroutine$FakeNetwork$FakeLink$connectWebSocket$lambda_0.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.state_0 = 2;
            this.result_0 = this.local$this$FakeNetwork.networkDelay_0(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 1:
            throw this.exception_0;
          case 2:
            return this.local$closure$webSocketListener.connected_67ozxy$(this.local$closure$clientSideConnection.v == null ? throwUPAE('clientSideConnection') : this.local$closure$clientSideConnection.v), Unit;
          default:this.state_0 = 1;
            throw new Error('State Machine Unreachable execution');
        }
      }
       catch (e) {
        if (this.state_0 === 1) {
          this.exceptionState_0 = this.state_0;
          throw e;
        }
         else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  function FakeNetwork$FakeLink$connectWebSocket$lambda_2(this$FakeNetwork_0, closure$webSocketListener_0, closure$clientSideConnection_0) {
    return function ($receiver_0, continuation_0, suspended) {
      var instance = new Coroutine$FakeNetwork$FakeLink$connectWebSocket$lambda_0(this$FakeNetwork_0, closure$webSocketListener_0, closure$clientSideConnection_0, $receiver_0, this, continuation_0);
      if (suspended)
        return instance;
      else
        return instance.doResume(null);
    };
  }
  function Coroutine$FakeNetwork$FakeLink$connectWebSocket$lambda_1(this$FakeNetwork_0, closure$serverListener_0, closure$serverSideConnection_0, $receiver_0, controller, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.$controller = controller;
    this.exceptionState_0 = 1;
    this.local$this$FakeNetwork = this$FakeNetwork_0;
    this.local$closure$serverListener = closure$serverListener_0;
    this.local$closure$serverSideConnection = closure$serverSideConnection_0;
  }
  Coroutine$FakeNetwork$FakeLink$connectWebSocket$lambda_1.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$FakeNetwork$FakeLink$connectWebSocket$lambda_1.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$FakeNetwork$FakeLink$connectWebSocket$lambda_1.prototype.constructor = Coroutine$FakeNetwork$FakeLink$connectWebSocket$lambda_1;
  Coroutine$FakeNetwork$FakeLink$connectWebSocket$lambda_1.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.state_0 = 2;
            this.result_0 = this.local$this$FakeNetwork.networkDelay_0(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 1:
            throw this.exception_0;
          case 2:
            return this.local$closure$serverListener.connected_67ozxy$(this.local$closure$serverSideConnection), Unit;
          default:this.state_0 = 1;
            throw new Error('State Machine Unreachable execution');
        }
      }
       catch (e) {
        if (this.state_0 === 1) {
          this.exceptionState_0 = this.state_0;
          throw e;
        }
         else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  function FakeNetwork$FakeLink$connectWebSocket$lambda_3(this$FakeNetwork_0, closure$serverListener_0, closure$serverSideConnection_0) {
    return function ($receiver_0, continuation_0, suspended) {
      var instance = new Coroutine$FakeNetwork$FakeLink$connectWebSocket$lambda_1(this$FakeNetwork_0, closure$serverListener_0, closure$serverSideConnection_0, $receiver_0, this, continuation_0);
      if (suspended)
        return instance;
      else
        return instance.doResume(null);
    };
  }
  FakeNetwork$FakeLink.prototype.connectWebSocket_t0j9bj$ = function (toAddress, port, path, webSocketListener) {
    var tmp$;
    var fakeHttpServer = this.$outer.httpServersByPort_0.get_11rb$(to(toAddress, port));
    var onConnectCallback = (tmp$ = fakeHttpServer != null ? fakeHttpServer.webSocketListeners : null) != null ? tmp$.get_11rb$(path) : null;
    if (onConnectCallback == null) {
      var connection = new FakeNetwork$FakeLink$FakeTcpConnection(this, this.myAddress, toAddress, port, null);
      launch(this.$outer.coroutineScope_0, void 0, void 0, FakeNetwork$FakeLink$connectWebSocket$lambda(this.$outer, webSocketListener, connection));
      return connection;
    }
    var clientSideConnection = {v: null};
    var serverSideConnection = new FakeNetwork$FakeLink$FakeTcpConnection(this, this.myAddress, toAddress, port, webSocketListener, FakeNetwork$FakeLink$connectWebSocket$lambda_0(clientSideConnection));
    var serverListener = onConnectCallback(serverSideConnection);
    clientSideConnection.v = new FakeNetwork$FakeLink$FakeTcpConnection(this, this.myAddress, toAddress, port, serverListener, FakeNetwork$FakeLink$connectWebSocket$lambda_1(serverSideConnection));
    launch(this.$outer.coroutineScope_0, void 0, void 0, FakeNetwork$FakeLink$connectWebSocket$lambda_2(this.$outer, webSocketListener, clientSideConnection));
    launch(this.$outer.coroutineScope_0, void 0, void 0, FakeNetwork$FakeLink$connectWebSocket$lambda_3(this.$outer, serverListener, serverSideConnection));
    return clientSideConnection.v == null ? throwUPAE('clientSideConnection') : clientSideConnection.v;
  };
  function FakeNetwork$FakeLink$FakeTcpConnection($outer, fromAddress, toAddress, port, webSocketListener, otherListener) {
    this.$outer = $outer;
    if (webSocketListener === void 0)
      webSocketListener = null;
    if (otherListener === void 0)
      otherListener = null;
    this.fromAddress_kb44jh$_0 = fromAddress;
    this.toAddress_ubzij8$_0 = toAddress;
    this.port_8z3c1y$_0 = port;
    this.webSocketListener_0 = webSocketListener;
    this.otherListener_0 = otherListener;
  }
  Object.defineProperty(FakeNetwork$FakeLink$FakeTcpConnection.prototype, 'fromAddress', {
    get: function () {
      return this.fromAddress_kb44jh$_0;
    }
  });
  Object.defineProperty(FakeNetwork$FakeLink$FakeTcpConnection.prototype, 'toAddress', {
    get: function () {
      return this.toAddress_ubzij8$_0;
    }
  });
  Object.defineProperty(FakeNetwork$FakeLink$FakeTcpConnection.prototype, 'port', {
    get: function () {
      return this.port_8z3c1y$_0;
    }
  });
  function Coroutine$FakeNetwork$FakeLink$FakeTcpConnection$send$lambda(this$FakeTcpConnection_0, closure$bytes_0, $receiver_0, controller, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.$controller = controller;
    this.exceptionState_0 = 1;
    this.local$this$FakeTcpConnection = this$FakeTcpConnection_0;
    this.local$closure$bytes = closure$bytes_0;
  }
  Coroutine$FakeNetwork$FakeLink$FakeTcpConnection$send$lambda.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$FakeNetwork$FakeLink$FakeTcpConnection$send$lambda.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$FakeNetwork$FakeLink$FakeTcpConnection$send$lambda.prototype.constructor = Coroutine$FakeNetwork$FakeLink$FakeTcpConnection$send$lambda;
  Coroutine$FakeNetwork$FakeLink$FakeTcpConnection$send$lambda.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            var tmp$;
            return (tmp$ = this.local$this$FakeTcpConnection.webSocketListener_0) != null ? (tmp$.receive_r00qii$(ensureNotNull(this.local$this$FakeTcpConnection.otherListener_0)(), this.local$closure$bytes), Unit) : null;
          case 1:
            throw this.exception_0;
          default:this.state_0 = 1;
            throw new Error('State Machine Unreachable execution');
        }
      }
       catch (e) {
        if (this.state_0 === 1) {
          this.exceptionState_0 = this.state_0;
          throw e;
        }
         else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  function FakeNetwork$FakeLink$FakeTcpConnection$send$lambda(this$FakeTcpConnection_0, closure$bytes_0) {
    return function ($receiver_0, continuation_0, suspended) {
      var instance = new Coroutine$FakeNetwork$FakeLink$FakeTcpConnection$send$lambda(this$FakeTcpConnection_0, closure$bytes_0, $receiver_0, this, continuation_0);
      if (suspended)
        return instance;
      else
        return instance.doResume(null);
    };
  }
  FakeNetwork$FakeLink$FakeTcpConnection.prototype.send_fqrh44$ = function (bytes) {
    launch(this.$outer.$outer.coroutineScope_0, void 0, void 0, FakeNetwork$FakeLink$FakeTcpConnection$send$lambda(this, bytes));
  };
  FakeNetwork$FakeLink$FakeTcpConnection.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'FakeTcpConnection',
    interfaces: [Network$TcpConnection]
  };
  function FakeNetwork$FakeLink$FakeUdpSocket($outer, serverPort) {
    this.$outer = $outer;
    this.serverPort_e2ivp4$_0 = serverPort;
  }
  Object.defineProperty(FakeNetwork$FakeLink$FakeUdpSocket.prototype, 'serverPort', {
    get: function () {
      return this.serverPort_e2ivp4$_0;
    }
  });
  FakeNetwork$FakeLink$FakeUdpSocket.prototype.sendUdp_ytpeqp$ = function (toAddress, port, bytes) {
    var tmp$;
    if (!this.$outer.$outer.sendPacketSuccess_0()) {
      (tmp$ = this.$outer.$outer.display_0) != null ? (tmp$.droppedPacket(), Unit) : null;
      return;
    }
    var listener = this.$outer.$outer.udpListeners_0.get_11rb$(new Pair(toAddress, port));
    if (listener != null)
      this.transmitUdp_0(this.$outer.myAddress, this.serverPort, listener, bytes);
  };
  FakeNetwork$FakeLink$FakeUdpSocket.prototype.broadcastUdp_3fbn1q$ = function (port, bytes) {
    var tmp$, tmp$_0;
    if (!this.$outer.$outer.sendPacketSuccess_0()) {
      (tmp$ = this.$outer.$outer.display_0) != null ? (tmp$.droppedPacket(), Unit) : null;
      return;
    }
    if ((tmp$_0 = this.$outer.$outer.udpListenersByPort_0.get_11rb$(port)) != null) {
      this.$outer;
      var tmp$_1;
      tmp$_1 = tmp$_0.iterator();
      while (tmp$_1.hasNext()) {
        var element = tmp$_1.next();
        this.transmitUdp_0(this.$outer.myAddress, this.serverPort, element, bytes);
      }
    }
  };
  function Coroutine$FakeNetwork$FakeLink$FakeUdpSocket$transmitUdp$lambda(this$FakeNetwork_0, closure$udpListener_0, closure$fromAddress_0, closure$fromPort_0, closure$bytes_0, $receiver_0, controller, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.$controller = controller;
    this.exceptionState_0 = 1;
    this.local$this$FakeNetwork = this$FakeNetwork_0;
    this.local$closure$udpListener = closure$udpListener_0;
    this.local$closure$fromAddress = closure$fromAddress_0;
    this.local$closure$fromPort = closure$fromPort_0;
    this.local$closure$bytes = closure$bytes_0;
  }
  Coroutine$FakeNetwork$FakeLink$FakeUdpSocket$transmitUdp$lambda.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$FakeNetwork$FakeLink$FakeUdpSocket$transmitUdp$lambda.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$FakeNetwork$FakeLink$FakeUdpSocket$transmitUdp$lambda.prototype.constructor = Coroutine$FakeNetwork$FakeLink$FakeUdpSocket$transmitUdp$lambda;
  Coroutine$FakeNetwork$FakeLink$FakeUdpSocket$transmitUdp$lambda.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            var tmp$, tmp$_0;
            this.state_0 = 2;
            this.result_0 = this.local$this$FakeNetwork.networkDelay_0(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 1:
            throw this.exception_0;
          case 2:
            if (!this.local$this$FakeNetwork.receivePacketSuccess_0()) {
              return (tmp$ = this.local$this$FakeNetwork.display_0) != null ? (tmp$.droppedPacket(), Unit) : null;
            }
             else {
              (tmp$_0 = this.local$this$FakeNetwork.display_0) != null ? (tmp$_0.receivedPacket(), Unit) : null;
              return this.local$closure$udpListener.receive_ytpeqp$(this.local$closure$fromAddress, this.local$closure$fromPort, this.local$closure$bytes), Unit;
            }

          case 3:
            return;
          default:this.state_0 = 1;
            throw new Error('State Machine Unreachable execution');
        }
      }
       catch (e) {
        if (this.state_0 === 1) {
          this.exceptionState_0 = this.state_0;
          throw e;
        }
         else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  function FakeNetwork$FakeLink$FakeUdpSocket$transmitUdp$lambda(this$FakeNetwork_0, closure$udpListener_0, closure$fromAddress_0, closure$fromPort_0, closure$bytes_0) {
    return function ($receiver_0, continuation_0, suspended) {
      var instance = new Coroutine$FakeNetwork$FakeLink$FakeUdpSocket$transmitUdp$lambda(this$FakeNetwork_0, closure$udpListener_0, closure$fromAddress_0, closure$fromPort_0, closure$bytes_0, $receiver_0, this, continuation_0);
      if (suspended)
        return instance;
      else
        return instance.doResume(null);
    };
  }
  FakeNetwork$FakeLink$FakeUdpSocket.prototype.transmitUdp_0 = function (fromAddress, fromPort, udpListener, bytes) {
    launch(this.$outer.$outer.coroutineScope_0, void 0, void 0, FakeNetwork$FakeLink$FakeUdpSocket$transmitUdp$lambda(this.$outer.$outer, udpListener, fromAddress, fromPort, bytes));
  };
  FakeNetwork$FakeLink$FakeUdpSocket.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'FakeUdpSocket',
    interfaces: [Network$UdpSocket]
  };
  function FakeNetwork$FakeLink$FakeHttpServer($outer, port) {
    this.$outer = $outer;
    this.port = port;
    this.webSocketListeners = LinkedHashMap_init();
  }
  FakeNetwork$FakeLink$FakeHttpServer.prototype.listenWebSocket_brdh44$ = function (path, onConnect) {
    this.webSocketListeners.put_xwzc9p$(path, onConnect);
  };
  FakeNetwork$FakeLink$FakeHttpServer.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'FakeHttpServer',
    interfaces: [Network$HttpServer]
  };
  FakeNetwork$FakeLink.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'FakeLink',
    interfaces: [Network$Link]
  };
  function Coroutine$networkDelay_0($this, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.exceptionState_0 = 1;
    this.$this = $this;
  }
  Coroutine$networkDelay_0.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$networkDelay_0.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$networkDelay_0.prototype.constructor = Coroutine$networkDelay_0;
  Coroutine$networkDelay_0.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            if (this.$this.networkDelay_1 !== 0) {
              this.state_0 = 2;
              this.result_0 = delay(Kotlin.Long.fromInt(this.$this.networkDelay_1), this);
              if (this.result_0 === COROUTINE_SUSPENDED)
                return COROUTINE_SUSPENDED;
              continue;
            }
             else {
              this.state_0 = 3;
              continue;
            }

          case 1:
            throw this.exception_0;
          case 2:
            this.state_0 = 3;
            continue;
          case 3:
            return;
          default:this.state_0 = 1;
            throw new Error('State Machine Unreachable execution');
        }
      }
       catch (e) {
        if (this.state_0 === 1) {
          this.exceptionState_0 = this.state_0;
          throw e;
        }
         else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  FakeNetwork.prototype.networkDelay_0 = function (continuation_0, suspended) {
    var instance = new Coroutine$networkDelay_0(this, continuation_0);
    if (suspended)
      return instance;
    else
      return instance.doResume(null);
  };
  function FakeNetwork$FakeAddress(id) {
    this.id = id;
  }
  FakeNetwork$FakeAddress.prototype.toString = function () {
    return 'x' + toString(this.id, 16);
  };
  FakeNetwork$FakeAddress.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'FakeAddress',
    interfaces: [Network$Address]
  };
  FakeNetwork$FakeAddress.prototype.component1 = function () {
    return this.id;
  };
  FakeNetwork$FakeAddress.prototype.copy_za3lpa$ = function (id) {
    return new FakeNetwork$FakeAddress(id === void 0 ? this.id : id);
  };
  FakeNetwork$FakeAddress.prototype.hashCode = function () {
    var result = 0;
    result = result * 31 + Kotlin.hashCode(this.id) | 0;
    return result;
  };
  FakeNetwork$FakeAddress.prototype.equals = function (other) {
    return this === other || (other !== null && (typeof other === 'object' && (Object.getPrototypeOf(this) === Object.getPrototypeOf(other) && Kotlin.equals(this.id, other.id))));
  };
  function FakeNetwork$coroutineScope$ObjectLiteral(closure$coroutineContext) {
    this.closure$coroutineContext = closure$coroutineContext;
  }
  Object.defineProperty(FakeNetwork$coroutineScope$ObjectLiteral.prototype, 'coroutineContext', {
    get: function () {
      return this.closure$coroutineContext;
    }
  });
  FakeNetwork$coroutineScope$ObjectLiteral.$metadata$ = {
    kind: Kind_CLASS,
    interfaces: [CoroutineScope]
  };
  FakeNetwork.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'FakeNetwork',
    interfaces: [Network]
  };
  function random($receiver) {
    return $receiver.size > 0 ? $receiver.get_za3lpa$(Random.Default.nextInt_za3lpa$($receiver.size)) : null;
  }
  function random_0($receiver, random) {
    return $receiver.size > 0 ? $receiver.get_za3lpa$(random.nextInt_za3lpa$($receiver.size)) : null;
  }
  function only($receiver, description) {
    if (description === void 0)
      description = 'item';
    if ($receiver.size !== 1)
      throw IllegalArgumentException_init('Expected one ' + description + ', found ' + $receiver.size + ': ' + $receiver);
    else
      return $receiver.iterator().next();
  }
  function toRadians(degrees) {
    return degrees * math.PI / 180;
  }
  function Coroutine$randomDelay(timeMs_0, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.exceptionState_0 = 1;
    this.local$timeMs = timeMs_0;
  }
  Coroutine$randomDelay.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$randomDelay.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$randomDelay.prototype.constructor = Coroutine$randomDelay;
  Coroutine$randomDelay.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.state_0 = 2;
            this.result_0 = delay(Kotlin.Long.fromInt(Random.Default.nextInt_za3lpa$(this.local$timeMs)), this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 1:
            throw this.exception_0;
          case 2:
            return;
          default:this.state_0 = 1;
            throw new Error('State Machine Unreachable execution');
        }
      }
       catch (e) {
        if (this.state_0 === 1) {
          this.exceptionState_0 = this.state_0;
          throw e;
        }
         else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  function randomDelay(timeMs_0, continuation_0, suspended) {
    var instance = new Coroutine$randomDelay(timeMs_0, continuation_0);
    if (suspended)
      return instance;
    else
      return instance.doResume(null);
  }
  function logger() {
    logger$Companion_getInstance();
  }
  function logger$Companion() {
    logger$Companion_instance = this;
  }
  logger$Companion.prototype.debug_61zpoe$ = function (message) {
    println('DEBUG: ' + message);
  };
  logger$Companion.prototype.info_61zpoe$ = function (message) {
    println('INFO: ' + message);
  };
  logger$Companion.prototype.warn_61zpoe$ = function (message) {
    println('WARN: ' + message);
  };
  logger$Companion.prototype.error_61zpoe$ = function (message) {
    println('ERROR: ' + message);
  };
  logger$Companion.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'Companion',
    interfaces: []
  };
  var logger$Companion_instance = null;
  function logger$Companion_getInstance() {
    if (logger$Companion_instance === null) {
      new logger$Companion();
    }
    return logger$Companion_instance;
  }
  logger.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'logger',
    interfaces: []
  };
  function Coroutine$time(function_1, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.exceptionState_0 = 1;
    this.local$now = void 0;
    this.local$function = function_1;
  }
  Coroutine$time.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$time.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$time.prototype.constructor = Coroutine$time;
  Coroutine$time.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.local$now = getTimeMillis();
            this.state_0 = 2;
            this.result_0 = this.local$function(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 1:
            throw this.exception_0;
          case 2:
            return getTimeMillis().subtract(this.local$now);
          default:this.state_0 = 1;
            throw new Error('State Machine Unreachable execution');
        }
      }
       catch (e) {
        if (this.state_0 === 1) {
          this.exceptionState_0 = this.state_0;
          throw e;
        }
         else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  function time(function_1, continuation_0, suspended) {
    var instance = new Coroutine$time(function_1, continuation_0);
    if (suspended)
      return instance;
    else
      return instance.doResume(null);
  }
  function timeSync(function_0) {
    var now = getTimeMillis();
    function_0();
    return getTimeMillis().subtract(now);
  }
  function JsDisplay() {
  }
  JsDisplay.prototype.forNetwork = function () {
    return new JsNetworkDisplay(document);
  };
  JsDisplay.prototype.forPinky = function () {
    return new JsPinkyDisplay(ensureNotNull(document.getElementById('pinkyView')));
  };
  JsDisplay.prototype.forBrain = function () {
    return new JsBrainDisplay(ensureNotNull(document.getElementById('brainsView')), ensureNotNull(document.getElementById('brainDetails')));
  };
  JsDisplay.prototype.forVisualizer = function () {
    return new JsVisualizerDisplay();
  };
  JsDisplay.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'JsDisplay',
    interfaces: [Display]
  };
  function JsNetworkDisplay(document) {
    var $receiver = ensureNotNull(document.getElementById('networkPacketLossRate'));
    $receiver.addEventListener('click', JsNetworkDisplay$packetLossRateSpan$lambda$lambda(this));
    this.packetLossRateSpan_0 = $receiver;
    this.packetLossRate_q9z6ua$_0 = 0.05;
    this.packetLossRate = 0.0;
    this.packetsReceivedSpan_0 = ensureNotNull(document.getElementById('networkPacketsReceived'));
    this.packetsDroppedSpan_0 = ensureNotNull(document.getElementById('networkPacketsDropped'));
    this.packetsReceived_0 = 0;
    this.packetsDropped_0 = 0;
  }
  Object.defineProperty(JsNetworkDisplay.prototype, 'packetLossRate', {
    get: function () {
      return this.packetLossRate_q9z6ua$_0;
    },
    set: function (value) {
      this.packetLossRateSpan_0.textContent = numberToInt(value * 100).toString() + '%';
      this.packetLossRate_q9z6ua$_0 = value;
    }
  });
  JsNetworkDisplay.prototype.receivedPacket = function () {
    var tmp$;
    this.packetsReceivedSpan_0.textContent = (tmp$ = this.packetsReceived_0, this.packetsReceived_0 = tmp$ + 1 | 0, tmp$).toString();
  };
  JsNetworkDisplay.prototype.droppedPacket = function () {
    var tmp$;
    this.packetsDroppedSpan_0.textContent = (tmp$ = this.packetsDropped_0, this.packetsDropped_0 = tmp$ + 1 | 0, tmp$).toString();
  };
  function JsNetworkDisplay$packetLossRateSpan$lambda$lambda(this$JsNetworkDisplay) {
    return function (it) {
      this$JsNetworkDisplay.packetLossRate = toDouble(ensureNotNull(window.prompt('Packet loss rate (%):', numberToInt(this$JsNetworkDisplay.packetLossRate * 100).toString()))) / 100;
      return Unit;
    };
  }
  JsNetworkDisplay.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'JsNetworkDisplay',
    interfaces: [NetworkDisplay]
  };
  function JsPinkyDisplay(element) {
    this.onShowChange_ii9f5g$_0 = JsPinkyDisplay$onShowChange$lambda;
    this.selectedShow_l65oio$_0 = null;
    this.showFrameMs_vaj1y2$_0 = 0;
    this.stats_qcsqqr$_0 = null;
    this.brainCountDiv_0 = null;
    this.beat1_0 = null;
    this.beat2_0 = null;
    this.beat3_0 = null;
    this.beat4_0 = null;
    this.beats_0 = null;
    this.bpmSpan_0 = null;
    this.showList_0 = emptyList();
    this.showListInput_0 = null;
    this.showFramerate_0 = ensureNotNull(document.getElementById('showFramerate'));
    this.showElapsedMs_0 = ensureNotNull(document.getElementById('showElapsedMs'));
    this.statsSpan_0 = null;
    var tmp$;
    appendText(element, 'Brains online: ');
    this.brainCountDiv_0 = appendElement(element, 'span', JsPinkyDisplay_init$lambda);
    var beatsDiv = appendElement(element, 'div', JsPinkyDisplay_init$lambda_0);
    this.bpmSpan_0 = appendElement(beatsDiv, 'h1', JsPinkyDisplay_init$lambda_1);
    this.bpmSpan_0.classList.add('bpmDisplay-beatOff');
    this.beat1_0 = appendElement(beatsDiv, 'span', JsPinkyDisplay_init$lambda_2);
    this.beat2_0 = appendElement(beatsDiv, 'span', JsPinkyDisplay_init$lambda_3);
    this.beat3_0 = appendElement(beatsDiv, 'span', JsPinkyDisplay_init$lambda_4);
    this.beat4_0 = appendElement(beatsDiv, 'span', JsPinkyDisplay_init$lambda_5);
    this.beats_0 = listOf([this.beat1_0, this.beat2_0, this.beat3_0, this.beat4_0]);
    appendElement(element, 'b', JsPinkyDisplay_init$lambda_6);
    this.showListInput_0 = Kotlin.isType(tmp$ = appendElement(element, 'select', JsPinkyDisplay_init$lambda_7), HTMLSelectElement) ? tmp$ : throwCCE();
    this.showListInput_0.onchange = JsPinkyDisplay_init$lambda_8(this);
    appendElement(element, 'br', JsPinkyDisplay_init$lambda_9);
    appendElement(element, 'b', JsPinkyDisplay_init$lambda_10);
    this.statsSpan_0 = appendElement(element, 'span', JsPinkyDisplay_init$lambda_11);
    this.brainCount_tt9c5b$_0 = 0;
    this.beat_o13evy$_0 = 0;
    this.bpm_32dxyb$_0 = 0;
  }
  Object.defineProperty(JsPinkyDisplay.prototype, 'onShowChange', {
    get: function () {
      return this.onShowChange_ii9f5g$_0;
    },
    set: function (onShowChange) {
      this.onShowChange_ii9f5g$_0 = onShowChange;
    }
  });
  Object.defineProperty(JsPinkyDisplay.prototype, 'selectedShow', {
    get: function () {
      return this.selectedShow_l65oio$_0;
    },
    set: function (value) {
      var tmp$, tmp$_0;
      this.selectedShow_l65oio$_0 = value;
      var options = this.showListInput_0.options;
      tmp$ = options.length;
      for (var i = 0; i < tmp$; i++) {
        if (equals((tmp$_0 = options[i]) != null ? tmp$_0.textContent : null, value != null ? value.name : null))
          this.showListInput_0.selectedIndex = i;
      }
    }
  });
  Object.defineProperty(JsPinkyDisplay.prototype, 'showFrameMs', {
    get: function () {
      return this.showFrameMs_vaj1y2$_0;
    },
    set: function (value) {
      this.showFrameMs_vaj1y2$_0 = value;
      this.showFramerate_0.textContent = (1000 / value | 0).toString() + 'fps';
      this.showElapsedMs_0.textContent = value.toString() + 'ms';
    }
  });
  Object.defineProperty(JsPinkyDisplay.prototype, 'stats', {
    get: function () {
      return this.stats_qcsqqr$_0;
    },
    set: function (value) {
      var tmp$;
      this.stats_qcsqqr$_0 = value;
      this.statsSpan_0.textContent = (tmp$ = value != null ? value.bytesSent.toString() + ' bytes / ' + value.packetsSent + ' packets sent' : null) != null ? tmp$ : '?';
    }
  });
  function JsPinkyDisplay$listShows$lambda$lambda(closure$it) {
    return function ($receiver) {
      appendText($receiver, closure$it.name);
      return Unit;
    };
  }
  JsPinkyDisplay.prototype.listShows_3lsa6o$ = function (shows) {
    clear(this.showListInput_0);
    this.showList_0 = shows;
    var tmp$;
    tmp$ = shows.iterator();
    while (tmp$.hasNext()) {
      var element = tmp$.next();
      appendElement(this.showListInput_0, 'option', JsPinkyDisplay$listShows$lambda$lambda(element));
    }
  };
  Object.defineProperty(JsPinkyDisplay.prototype, 'brainCount', {
    get: function () {
      return this.brainCount_tt9c5b$_0;
    },
    set: function (value) {
      clear(this.brainCountDiv_0);
      appendText(this.brainCountDiv_0, value.toString());
      this.brainCount_tt9c5b$_0 = value;
    }
  });
  Object.defineProperty(JsPinkyDisplay.prototype, 'beat', {
    get: function () {
      return this.beat_o13evy$_0;
    },
    set: function (value) {
      var tmp$;
      try {
        clear_0(this.beats_0.get_za3lpa$(this.beat_o13evy$_0).classList);
        this.beats_0.get_za3lpa$(value).classList.add('selected');
        if (value % 2 === 1) {
          this.bpmSpan_0.classList.add('bpmDisplay-beatOn');
          this.bpmSpan_0.textContent = this.bpmSpan_0.textContent + ' !!';
        }
         else {
          this.bpmSpan_0.classList.remove('bpmDisplay-beatOn');
          (tmp$ = this.bpmSpan_0.textContent) != null ? removeSuffix(tmp$, ' !!') : null;
        }
      }
       catch (e) {
        if (Kotlin.isType(e, Exception)) {
          println('durrr error ' + e);
        }
         else
          throw e;
      }
      this.beat_o13evy$_0 = value;
    }
  });
  Object.defineProperty(JsPinkyDisplay.prototype, 'bpm', {
    get: function () {
      return this.bpm_32dxyb$_0;
    },
    set: function (value) {
      this.bpmSpan_0.textContent = value.toString() + ' BPM !!';
      this.bpm_32dxyb$_0 = value;
    }
  });
  function JsPinkyDisplay$onShowChange$lambda() {
    return Unit;
  }
  function JsPinkyDisplay_init$lambda($receiver) {
    return Unit;
  }
  function JsPinkyDisplay_init$lambda$lambda($receiver) {
    appendText($receiver, 'Beats: ');
    return Unit;
  }
  function JsPinkyDisplay_init$lambda$lambda_0($receiver) {
    return Unit;
  }
  function JsPinkyDisplay_init$lambda_0($receiver) {
    $receiver.id = 'beatsDiv';
    appendElement($receiver, 'b', JsPinkyDisplay_init$lambda$lambda);
    appendElement($receiver, 'br', JsPinkyDisplay_init$lambda$lambda_0);
    return Unit;
  }
  function JsPinkyDisplay_init$lambda_1($receiver) {
    appendText($receiver, '\u2026BPM !!');
    return Unit;
  }
  function JsPinkyDisplay_init$lambda_2($receiver) {
    appendText($receiver, '1');
    return Unit;
  }
  function JsPinkyDisplay_init$lambda_3($receiver) {
    appendText($receiver, '2');
    return Unit;
  }
  function JsPinkyDisplay_init$lambda_4($receiver) {
    appendText($receiver, '3');
    return Unit;
  }
  function JsPinkyDisplay_init$lambda_5($receiver) {
    appendText($receiver, '4');
    return Unit;
  }
  function JsPinkyDisplay_init$lambda_6($receiver) {
    appendText($receiver, 'Renderer: ');
    return Unit;
  }
  function JsPinkyDisplay_init$lambda_7($receiver) {
    $receiver.className = 'showsDiv';
    return Unit;
  }
  function JsPinkyDisplay_init$lambda_8(this$JsPinkyDisplay) {
    return function (it) {
      var tmp$ = this$JsPinkyDisplay;
      var $receiver = this$JsPinkyDisplay.showList_0;
      var firstOrNull$result;
      firstOrNull$break: do {
        var tmp$_0;
        tmp$_0 = $receiver.iterator();
        while (tmp$_0.hasNext()) {
          var element = tmp$_0.next();
          var tmp$_1;
          if (equals(element.name, (tmp$_1 = this$JsPinkyDisplay.showListInput_0.selectedOptions[0]) != null ? tmp$_1.textContent : null)) {
            firstOrNull$result = element;
            break firstOrNull$break;
          }
        }
        firstOrNull$result = null;
      }
       while (false);
      tmp$.selectedShow = firstOrNull$result;
      this$JsPinkyDisplay.onShowChange();
      return Unit;
    };
  }
  function JsPinkyDisplay_init$lambda_9($receiver) {
    return Unit;
  }
  function JsPinkyDisplay_init$lambda_10($receiver) {
    appendText($receiver, 'Data to Brains: ');
    return Unit;
  }
  function JsPinkyDisplay_init$lambda_11($receiver) {
    return Unit;
  }
  JsPinkyDisplay.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'JsPinkyDisplay',
    interfaces: [PinkyDisplay]
  };
  function JsBrainDisplay(container, detailsContainer) {
    this.id_227d6y$_0 = null;
    this.surface_hioyda$_0 = null;
    this.onReset_xet84f$_0 = JsBrainDisplay$onReset$lambda;
    this.myDiv_0 = appendElement(container, 'div', JsBrainDisplay$myDiv$lambda(this, detailsContainer));
  }
  Object.defineProperty(JsBrainDisplay.prototype, 'id', {
    get: function () {
      return this.id_227d6y$_0;
    },
    set: function (id) {
      this.id_227d6y$_0 = id;
    }
  });
  Object.defineProperty(JsBrainDisplay.prototype, 'surface', {
    get: function () {
      return this.surface_hioyda$_0;
    },
    set: function (surface) {
      this.surface_hioyda$_0 = surface;
    }
  });
  Object.defineProperty(JsBrainDisplay.prototype, 'onReset', {
    get: function () {
      return this.onReset_xet84f$_0;
    },
    set: function (onReset) {
      this.onReset_xet84f$_0 = onReset;
    }
  });
  JsBrainDisplay.prototype.haveLink_9m0ekx$ = function (link) {
    this.myDiv_0.classList.remove('brain-offline');
    this.myDiv_0.classList.add('brain-link');
  };
  function Coroutine$JsBrainDisplay$onReset$lambda(continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.exceptionState_0 = 1;
  }
  Coroutine$JsBrainDisplay$onReset$lambda.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$JsBrainDisplay$onReset$lambda.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$JsBrainDisplay$onReset$lambda.prototype.constructor = Coroutine$JsBrainDisplay$onReset$lambda;
  Coroutine$JsBrainDisplay$onReset$lambda.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            return Unit;
          case 1:
            throw this.exception_0;
          default:this.state_0 = 1;
            throw new Error('State Machine Unreachable execution');
        }
      }
       catch (e) {
        if (this.state_0 === 1) {
          this.exceptionState_0 = this.state_0;
          throw e;
        }
         else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  function JsBrainDisplay$onReset$lambda(continuation_0, suspended) {
    var instance = new Coroutine$JsBrainDisplay$onReset$lambda(continuation_0);
    if (suspended)
      return instance;
    else
      return instance.doResume(null);
  }
  function Coroutine$JsBrainDisplay$myDiv$lambda$lambda$lambda(this$JsBrainDisplay_0, $receiver_0, controller, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.$controller = controller;
    this.exceptionState_0 = 1;
    this.local$this$JsBrainDisplay = this$JsBrainDisplay_0;
  }
  Coroutine$JsBrainDisplay$myDiv$lambda$lambda$lambda.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$JsBrainDisplay$myDiv$lambda$lambda$lambda.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$JsBrainDisplay$myDiv$lambda$lambda$lambda.prototype.constructor = Coroutine$JsBrainDisplay$myDiv$lambda$lambda$lambda;
  Coroutine$JsBrainDisplay$myDiv$lambda$lambda$lambda.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.state_0 = 2;
            this.result_0 = this.local$this$JsBrainDisplay.onReset(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 1:
            throw this.exception_0;
          case 2:
            return this.result_0;
          default:this.state_0 = 1;
            throw new Error('State Machine Unreachable execution');
        }
      }
       catch (e) {
        if (this.state_0 === 1) {
          this.exceptionState_0 = this.state_0;
          throw e;
        }
         else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  function JsBrainDisplay$myDiv$lambda$lambda$lambda(this$JsBrainDisplay_0) {
    return function ($receiver_0, continuation_0, suspended) {
      var instance = new Coroutine$JsBrainDisplay$myDiv$lambda$lambda$lambda(this$JsBrainDisplay_0, $receiver_0, this, continuation_0);
      if (suspended)
        return instance;
      else
        return instance.doResume(null);
    };
  }
  function JsBrainDisplay$myDiv$lambda$lambda(this$JsBrainDisplay) {
    return function (it) {
      launch(coroutines.GlobalScope, void 0, void 0, JsBrainDisplay$myDiv$lambda$lambda$lambda(this$JsBrainDisplay));
      return Unit;
    };
  }
  function JsBrainDisplay$myDiv$lambda$lambda$lambda_0($receiver) {
    return Unit;
  }
  function JsBrainDisplay$myDiv$lambda$lambda$lambda_1(this$JsBrainDisplay) {
    return function ($receiver) {
      appendText($receiver, 'Brain ' + toString_0(this$JsBrainDisplay.id));
      return Unit;
    };
  }
  function JsBrainDisplay$myDiv$lambda$lambda$lambda_2(this$JsBrainDisplay) {
    return function ($receiver) {
      var tmp$;
      appendText($receiver, 'Surface: ' + toString_0((tmp$ = this$JsBrainDisplay.surface) != null ? tmp$.describe() : null));
      return Unit;
    };
  }
  function JsBrainDisplay$myDiv$lambda$lambda_0(closure$detailsContainer, this$JsBrainDisplay) {
    return function (it) {
      clear(closure$detailsContainer);
      appendElement(closure$detailsContainer, 'hr', JsBrainDisplay$myDiv$lambda$lambda$lambda_0);
      appendElement(closure$detailsContainer, 'b', JsBrainDisplay$myDiv$lambda$lambda$lambda_1(this$JsBrainDisplay));
      appendElement(closure$detailsContainer, 'div', JsBrainDisplay$myDiv$lambda$lambda$lambda_2(this$JsBrainDisplay));
      return Unit;
    };
  }
  function JsBrainDisplay$myDiv$lambda(this$JsBrainDisplay, closure$detailsContainer) {
    return function ($receiver) {
      addClass($receiver, ['brain-box', 'brain-offline']);
      $receiver.addEventListener('click', JsBrainDisplay$myDiv$lambda$lambda(this$JsBrainDisplay));
      $receiver.addEventListener('mouseover', JsBrainDisplay$myDiv$lambda$lambda_0(closure$detailsContainer, this$JsBrainDisplay));
      return Unit;
    };
  }
  JsBrainDisplay.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'JsBrainDisplay',
    interfaces: [BrainDisplay]
  };
  function JsVisualizerDisplay() {
    this.visualizerFramerate_0 = ensureNotNull(document.getElementById('visualizerFramerate'));
    this.visualizerElapsedMs_0 = ensureNotNull(document.getElementById('visualizerElapsedMs'));
    this.renderMs_lh9op3$_0 = 0;
  }
  Object.defineProperty(JsVisualizerDisplay.prototype, 'renderMs', {
    get: function () {
      return this.renderMs_lh9op3$_0;
    },
    set: function (value) {
      this.renderMs_lh9op3$_0 = value;
      this.visualizerFramerate_0.textContent = (1000 / value | 0).toString() + 'fps';
      this.visualizerElapsedMs_0.textContent = value.toString() + 'ms';
    }
  });
  JsVisualizerDisplay.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'JsVisualizerDisplay',
    interfaces: [VisualizerDisplay]
  };
  function JsMapperUi(statusListener) {
    if (statusListener === void 0)
      statusListener = null;
    this.statusListener_0 = statusListener;
    this.listener_pdjnza$_0 = this.listener_pdjnza$_0;
    this.width_0 = 512;
    this.height_0 = 384;
    this.uiWidth_0 = 512;
    this.uiHeight_0 = 384;
    this.haveCamDimensions_0 = false;
    this.camWidth_0 = 0;
    this.camHeight_0 = 0;
    this.clock_0 = new Clock();
    this.uiRenderer_0 = new WebGLRenderer_init({alpha: true});
    this.uiScene_0 = new Scene();
    this.uiCamera_0 = new PerspectiveCamera_init(45, this.width_0 / this.height_0, 1, 10000);
    this.uiControls_0 = null;
    this.wireframe_0 = new Object3D();
    this.selectedSurfaces_0 = ArrayList_init();
    this.uiLocked_0 = false;
    this.screen_0 = div_0(get_create(document), 'mapperUi-screen', JsMapperUi$screen$lambda(this));
    this.ui2dCanvas_0 = first_1(this.screen_0, 'mapperUi-2d-canvas');
    this.ui2dCtx_0 = context2d(this.ui2dCanvas_0);
    this.ui3dDiv_0 = first_1(this.screen_0, 'mapperUi-3d-div');
    var tmp$, tmp$_0;
    this.ui3dCanvas_0 = Kotlin.isType(tmp$ = this.uiRenderer_0.domElement, HTMLCanvasElement) ? tmp$ : throwCCE();
    this.diffCanvas_0 = first_1(this.screen_0, 'mapperUi-diff-canvas');
    this.diffCtx_0 = context2d(this.diffCanvas_0);
    this.beforeCanvas_0 = first_1(this.screen_0, 'mapperUi-before-canvas');
    this.afterCanvas_0 = first_1(this.screen_0, 'mapperUi-after-canvas');
    this.statsDiv_0 = first_1(this.screen_0, 'mapperUi-stats');
    this.messageDiv_0 = first_1(this.screen_0, 'mapperUi-message');
    this.message2Div_0 = first_1(this.screen_0, 'mapperUi-message2');
    this.table_0 = first_1(this.screen_0, 'mapperUi-table');
    this.sessionSelector_0 = first_1(this.screen_0, 'mapperUi-sessionSelector');
    this.playButton_0 = first_1(this.screen_0, 'fa-play');
    this.pauseButton_0 = first_1(this.screen_0, 'fa-pause');
    this.modelSurfaceInfos_0 = LinkedHashMap_init();
    this.commandProgress_0 = '';
    this.cameraZRotation_0 = 0.0;
    (tmp$_0 = this.statusListener_0) != null ? (tmp$_0.mapperStatusChanged_6taknv$(true), Unit) : null;
    this.ui3dDiv_0.appendChild(this.ui3dCanvas_0);
    this.uiCamera_0.position.z = 1000.0;
    this.uiScene_0.add(this.uiCamera_0);
    this.uiControls_0 = document.createCameraControls(this.uiCamera_0, this.uiRenderer_0.domElement);
    this.screen_0.focus();
    this.screen_0.addEventListener('keydown', JsMapperUi_init$lambda(this));
    this.drawAnimationFrame_0();
    this.diffCanvasScale_0 = 1 / 3.0;
  }
  Object.defineProperty(JsMapperUi.prototype, 'listener_0', {
    get: function () {
      if (this.listener_pdjnza$_0 == null)
        return throwUPAE('listener');
      return this.listener_pdjnza$_0;
    },
    set: function (listener) {
      this.listener_pdjnza$_0 = listener;
    }
  });
  JsMapperUi.prototype.listen_97503t$ = function (listener) {
    this.listener_0 = listener;
  };
  JsMapperUi.prototype.gotUiKeypress_0 = function (event) {
    if (equals(event.code, 'Enter')) {
      var $receiver = this.commandProgress_0;
      var tmp$;
      this.processCommand_0(trim(Kotlin.isCharSequence(tmp$ = $receiver) ? tmp$ : throwCCE()).toString());
      this.commandProgress_0 = '';
    }
     else if (equals(event.code, 'Backspace')) {
      if (this.commandProgress_0.length > 0) {
        this.commandProgress_0 = substring(this.commandProgress_0, new IntRange(0, this.commandProgress_0.length - 2 | 0));
      }
      this.checkProgress_0();
    }
     else {
      if (this.commandProgress_0.length === 0 && equals(event.code, 'KeyQ')) {
        this.updateCameraRotation_0(event.shiftKey ? -0.025 : -0.1);
      }
       else {
        if (this.commandProgress_0.length === 0 && equals(event.code, 'KeyW')) {
          this.updateCameraRotation_0(event.shiftKey ? 0.025 : 0.1);
        }
         else {
          if (this.commandProgress_0.length === 0 && equals(event.code, 'Digit0')) {
            this.cameraZRotation_0 = 0.0;
          }
           else if (event.key.length === 1) {
            this.commandProgress_0 += event.key;
            this.checkProgress_0();
          }
        }
      }
    }
    this.showMessage2_61zpoe$(this.commandProgress_0);
  };
  JsMapperUi.prototype.checkProgress_0 = function () {
    if (startsWith(this.commandProgress_0, '/') && this.commandProgress_0.length > 1) {
      this.selectSurfacesMatching_0(this.commandProgress_0.substring(1));
    }
  };
  function JsMapperUi$addExistingSession$lambda(closure$name) {
    return function ($receiver) {
      $receiver.label = closure$name;
      $receiver.value = closure$name;
      return Unit;
    };
  }
  JsMapperUi.prototype.addExistingSession_61zpoe$ = function (name) {
    var tmp$ = this.sessionSelector_0;
    var tmp$_0 = option(get_create(document), void 0, JsMapperUi$addExistingSession$lambda(name));
    var $receiver = asList(this.sessionSelector_0.childNodes);
    var firstOrNull$result;
    firstOrNull$break: do {
      var tmp$_1;
      tmp$_1 = $receiver.iterator();
      while (tmp$_1.hasNext()) {
        var element = tmp$_1.next();
        var tmp$_2;
        if (Kotlin.compareTo((Kotlin.isType(tmp$_2 = element, HTMLOptionElement) ? tmp$_2 : throwCCE()).value, name) > 0) {
          firstOrNull$result = element;
          break firstOrNull$break;
        }
      }
      firstOrNull$result = null;
    }
     while (false);
    tmp$.insertBefore(tmp$_0, firstOrNull$result);
  };
  JsMapperUi.prototype.resetCameraRotation_0 = function () {
    this.cameraZRotation_0 = 0.0;
    this.updateCameraRotation_0(0.0);
  };
  JsMapperUi.prototype.updateCameraRotation_0 = function (angle) {
    this.cameraZRotation_0 += angle;
    this.uiCamera_0.up.set(0, 1, 0);
    this.uiCamera_0.up.applyMatrix4((new Matrix4_init()).makeRotationZ(this.cameraZRotation_0));
  };
  JsMapperUi.prototype.selectSurfacesMatching_0 = function (pattern) {
    var tmp$;
    tmp$ = this.selectedSurfaces_0.iterator();
    while (tmp$.hasNext()) {
      var element = tmp$.next();
      element.deselect();
    }
    this.selectedSurfaces_0.clear();
    var tmp$_0 = this.selectedSurfaces_0;
    var $receiver = this.modelSurfaceInfos_0.values;
    var destination = ArrayList_init();
    var tmp$_1;
    tmp$_1 = $receiver.iterator();
    while (tmp$_1.hasNext()) {
      var element_0 = tmp$_1.next();
      if (contains_0(element_0.name, pattern, true))
        destination.add_11rb$(element_0);
    }
    tmp$_0.addAll_brywnq$(destination);
    var tmp$_2;
    tmp$_2 = this.selectedSurfaces_0.iterator();
    while (tmp$_2.hasNext()) {
      var element_1 = tmp$_2.next();
      element_1.select();
    }
  };
  JsMapperUi.prototype.processCommand_0 = function (command) {
    console.log(command);
    if (startsWith(command, 'g', true) || startsWith(command, '/')) {
      var $receiver = command.substring(1);
      var tmp$;
      var surfaceName = trim(Kotlin.isCharSequence(tmp$ = $receiver) ? tmp$ : throwCCE()).toString();
      this.goToSurface_0(surfaceName.toUpperCase());
    }
  };
  function JsMapperUi$drawAnimationFrame$lambda(this$JsMapperUi) {
    return function (it) {
      this$JsMapperUi.drawAnimationFrame_0();
      return Unit;
    };
  }
  JsMapperUi.prototype.drawAnimationFrame_0 = function () {
    if (!this.uiLocked_0) {
      this.uiControls_0.update(this.clock_0.getDelta());
    }
    this.uiRenderer_0.render(this.uiScene_0, this.uiCamera_0);
    window.requestAnimationFrame(JsMapperUi$drawAnimationFrame$lambda(this));
  };
  function JsMapperUi$render$lambda(closure$parentNode, this$JsMapperUi) {
    return function (it) {
      this$JsMapperUi.resizeTo_0(closure$parentNode.offsetWidth, this$JsMapperUi.heightOrWindowHeight_0(closure$parentNode));
      return Unit;
    };
  }
  JsMapperUi.prototype.render = function (parentNode) {
    parentNode.appendChild(this.screen_0);
    this.resizeTo_0(parentNode.offsetWidth, this.heightOrWindowHeight_0(parentNode));
    parentNode.onresize = JsMapperUi$render$lambda(parentNode, this);
  };
  JsMapperUi.prototype.heightOrWindowHeight_0 = function (parentNode) {
    return parentNode.offsetHeight === 0 ? window.innerHeight : parentNode.offsetHeight;
  };
  JsMapperUi.prototype.onClose = function () {
    var tmp$;
    (tmp$ = this.statusListener_0) != null ? (tmp$.mapperStatusChanged_6taknv$(false), Unit) : null;
    this.listener_0.onClose();
  };
  JsMapperUi.prototype.resizeTo_0 = function (width, height) {
    this.width_0 = width;
    this.height_0 = height;
    if (!this.haveCamDimensions_0) {
      this.camWidth_0 = width;
      this.camHeight_0 = height;
    }
    var a = (width - 10 | 0) / this.camWidth_0;
    var b = (height - 10 | 0) / this.camHeight_0;
    var b_0 = Math_0.min(a, b);
    var scale = Math_0.min(1.0, b_0);
    this.uiWidth_0 = this.camWidth_0 - 10 | 0;
    this.uiHeight_0 = this.camHeight_0 - 10 | 0;
    this.uiCamera_0.aspect = this.uiWidth_0 / this.uiHeight_0;
    this.uiCamera_0.updateProjectionMatrix();
    this.uiRenderer_0.setSize(this.uiWidth_0, this.uiHeight_0, true);
    this.ui3dCanvas_0.width = this.uiWidth_0;
    this.ui3dCanvas_0.height = this.uiHeight_0;
    this.ui2dCanvas_0.width = this.uiWidth_0;
    this.ui2dCanvas_0.height = this.uiHeight_0;
    this.ui2dCanvas_0.style.transform = 'scale(' + scale + ')';
    this.diffCanvas_0.width = numberToInt(this.uiWidth_0 * this.diffCanvasScale_0);
    this.diffCanvas_0.height = numberToInt(this.uiHeight_0 * this.diffCanvasScale_0);
    this.beforeCanvas_0.width = numberToInt(this.uiWidth_0 * this.diffCanvasScale_0);
    this.beforeCanvas_0.height = numberToInt(this.uiHeight_0 * this.diffCanvasScale_0);
    this.afterCanvas_0.width = numberToInt(this.uiWidth_0 * this.diffCanvasScale_0);
    this.afterCanvas_0.height = numberToInt(this.uiHeight_0 * this.diffCanvasScale_0);
  };
  JsMapperUi.prototype.addWireframe_9u144y$ = function (sheepModel) {
    var $receiver = sheepModel.vertices;
    var destination = ArrayList_init_1(collectionSizeOrDefault($receiver, 10));
    var tmp$;
    tmp$ = $receiver.iterator();
    while (tmp$.hasNext()) {
      var item = tmp$.next();
      destination.add_11rb$(new Vector3(item.x, item.y, item.z));
    }
    var vertices = copyToArray(destination);
    var tmp$_0;
    tmp$_0 = sheepModel.panels.iterator();
    while (tmp$_0.hasNext()) {
      var element = tmp$_0.next();
      var geom = new Geometry();
      var allFaces = ArrayList_init();
      geom.vertices = vertices;
      var panelFaces = ArrayList_init();
      var faceNormalAcc = {v: new Vector3()};
      var tmp$_1;
      tmp$_1 = element.faces.faces.iterator();
      while (tmp$_1.hasNext()) {
        var element_0 = tmp$_1.next();
        var face3 = new Face3_init(element_0.vertexIds.get_za3lpa$(0), element_0.vertexIds.get_za3lpa$(1), element_0.vertexIds.get_za3lpa$(2), new Vector3(0, 0, 0));
        allFaces.add_11rb$(face3);
        panelFaces.add_11rb$(face3);
        geom.faces = [face3];
        geom.computeFaceNormals();
        faceNormalAcc.v.add(ensureNotNull(face3.normal));
      }
      var surfaceNormal = faceNormalAcc.v.divideScalar(element.faces.faces.size);
      var $receiver_0 = new MeshBasicMaterial();
      $receiver_0.color = new Color_init(0, 0, 0);
      var panelMaterial = $receiver_0;
      var mesh = new Mesh_init(geom, panelMaterial);
      mesh.name = element.name;
      this.uiScene_0.add(mesh);
      var $receiver_1 = new LineBasicMaterial();
      $receiver_1.color = new Color_init(0.0, 1.0, 0.0);
      $receiver_1.linewidth = 2.0;
      var lineMaterial = $receiver_1;
      var tmp$_2;
      tmp$_2 = element.lines.iterator();
      while (tmp$_2.hasNext()) {
        var element_1 = tmp$_2.next();
        var lineGeom = new BufferGeometry();
        var $receiver_2 = element_1.points;
        var destination_0 = ArrayList_init_1(collectionSizeOrDefault($receiver_2, 10));
        var tmp$_3;
        tmp$_3 = $receiver_2.iterator();
        while (tmp$_3.hasNext()) {
          var item_0 = tmp$_3.next();
          destination_0.add_11rb$(plus_0(new Vector3(item_0.x, item_0.y, item_0.z), surfaceNormal));
        }
        lineGeom.setFromPoints(copyToArray(destination_0));
        this.wireframe_0.add(new Line_init(lineGeom, lineMaterial));
      }
      geom.faces = copyToArray(allFaces);
      geom.computeFaceNormals();
      geom.computeVertexNormals();
      var $receiver_3 = this.modelSurfaceInfos_0;
      var value = new PanelInfo(element.name, panelFaces, mesh, geom, lineMaterial);
      $receiver_3.put_xwzc9p$(element, value);
    }
    this.uiScene_0.add(this.wireframe_0);
    var tmp$_4 = new SphereBufferGeometry(1, 32, 32);
    var $receiver_4 = new MeshBasicMaterial();
    $receiver_4.color = new Color_init(16711680);
    var originMarker = new Mesh_init(tmp$_4, $receiver_4);
    this.uiScene_0.add(originMarker);
    var boundingBox = (new Box3()).setFromObject(this.wireframe_0);
    this.uiControls_0.fitTo(boundingBox, false);
  };
  JsMapperUi.prototype.lockUi = function () {
    this.uiLocked_0 = true;
    return JsMapperUi$CameraOrientation$Companion_getInstance().from_pak8zx$(this.uiCamera_0);
  };
  JsMapperUi.prototype.unlockUi = function () {
    this.uiLocked_0 = false;
  };
  JsMapperUi.prototype.getVisibleSurfaces = function () {
    var visibleSurfaces = ArrayList_init();
    var screenBox = this.getScreenBox_0();
    var screenCenter = get_center(screenBox);
    var cameraOrientation = JsMapperUi$CameraOrientation$Companion_getInstance().from_pak8zx$(this.uiCamera_0);
    var tmp$;
    tmp$ = this.modelSurfaceInfos_0.entries.iterator();
    while (tmp$.hasNext()) {
      var element = tmp$.next();
      var panel = element.key;
      var panelInfo = element.value;
      var panelPosition = panelInfo.geom.vertices[panelInfo.faces.get_za3lpa$(0).a];
      var dirToCamera = this.uiCamera_0.position.clone().sub(panelPosition);
      dirToCamera.normalize();
      var angle = ensureNotNull(panelInfo.faces.get_za3lpa$(0).normal).dot(dirToCamera);
      if (angle > 0) {
        panelInfo.mesh.updateMatrixWorld();
        var panelBoundingBox = project(panelInfo.boundingBox, this.uiCamera_0);
        var panelBoxOnScreen = this.calcBoundingBoxOnScreen_0(panelBoundingBox, screenCenter);
        panelInfo.boxOnScreen = panelBoxOnScreen;
        if (panelBoxOnScreen.intersectsBox(screenBox)) {
          var region = new MediaDevices$Region(roundToInt(panelBoxOnScreen.min.x), roundToInt(panelBoxOnScreen.min.y), roundToInt(panelBoxOnScreen.max.x), roundToInt(panelBoxOnScreen.max.y));
          visibleSurfaces.add_11rb$(new JsMapperUi$VisibleSurface(this, panel, region, panelInfo, cameraOrientation));
        }
      }
    }
    return visibleSurfaces;
  };
  function JsMapperUi$VisibleSurface($outer, modelSurface, boxOnScreen, panelInfo, cameraOrientation) {
    this.$outer = $outer;
    this.modelSurface_eleb8v$_0 = modelSurface;
    this.boxOnScreen_pa7emj$_0 = boxOnScreen;
    this.panelInfo = panelInfo;
    this.camera_0 = cameraOrientation.createCamera();
    this.geom_0 = new Geometry();
    var $receiver = new PointsMaterial();
    $receiver.color = new Color_init(65280);
    $receiver.size = 5;
    this.material_0 = $receiver;
    var $receiver_0 = new Points();
    $receiver_0.geometry = this.geom_0;
    $receiver_0.material = this.material_0;
    this.points_0 = $receiver_0;
    this.pixels_0 = LinkedHashMap_init();
  }
  Object.defineProperty(JsMapperUi$VisibleSurface.prototype, 'modelSurface', {
    get: function () {
      return this.modelSurface_eleb8v$_0;
    }
  });
  Object.defineProperty(JsMapperUi$VisibleSurface.prototype, 'boxOnScreen', {
    get: function () {
      return this.boxOnScreen_pa7emj$_0;
    }
  });
  JsMapperUi$VisibleSurface.prototype.addPixel_nhq4am$ = function (pixelIndex, x, y) {
    var tmp$ = this.pixels_0;
    var $receiver = new JsMapperUi$VisibleSurface$VisiblePixel(this, pixelIndex, x, y);
    $receiver.addToGeom();
    tmp$.put_xwzc9p$(pixelIndex, $receiver);
  };
  JsMapperUi$VisibleSurface.prototype.translatePixelToPanelSpace_dleff0$ = function (screenX, screenY) {
    var tmp$;
    tmp$ = this.findIntersection_0(screenX, screenY);
    if (tmp$ == null) {
      return null;
    }
    var intersection = tmp$;
    var point = this.panelInfo.toPanelSpace_as37vi$(intersection.point.clone());
    console.log('   ---->', point.x, point.y, point.z);
    return new Vector2F(point.x, point.y);
  };
  Object.defineProperty(JsMapperUi$VisibleSurface.prototype, 'pixelsInModelSpace', {
    get: function () {
      var tmp$, tmp$_0;
      var vectors = ArrayList_init();
      tmp$ = ensureNotNull(max_0(this.pixels_0.keys));
      for (var i = 0; i <= tmp$; i++) {
        var position = (tmp$_0 = this.pixels_0.get_11rb$(i)) != null ? tmp$_0.positionInModel : null;
        vectors.add_11rb$(position != null ? new Vector3F(position.x, position.y, position.z) : null);
      }
      return vectors;
    }
  });
  JsMapperUi$VisibleSurface.prototype.findIntersection_0 = function (x, y) {
    var raycaster = new Raycaster_init();
    var pixelVector = new Vector2(x / this.$outer.uiWidth_0 * 2 - 1, -(y / this.$outer.uiHeight_0 * 2 - 1));
    raycaster.setFromCamera(pixelVector, this.camera_0);
    var intersections = raycaster.intersectObject(this.panelInfo.mesh, false);
    if (intersections.length === 0) {
      intersections = raycaster.intersectObject(this.$outer.uiScene_0, true);
      console.log("Couldn't find point in " + this.modelSurface.name + '...', intersections);
    }
    if (!(intersections.length === 0)) {
      return first_0(intersections);
    }
     else {
      return null;
    }
  };
  JsMapperUi$VisibleSurface.prototype.showPixels = function () {
    this.$outer.uiScene_0.add(this.points_0);
  };
  JsMapperUi$VisibleSurface.prototype.hidePixels = function () {
    this.$outer.uiScene_0.remove(this.points_0);
  };
  function JsMapperUi$VisibleSurface$VisiblePixel($outer, pixelIndex, cameraX, cameraY) {
    this.$outer = $outer;
    this.pixelIndex = pixelIndex;
    this.cameraX = cameraX;
    this.cameraY = cameraY;
    this.intersect_vx7gy6$_0 = lazy(JsMapperUi$VisibleSurface$VisiblePixel$intersect$lambda(this, this.$outer));
    var tmp$;
    this.positionInModel = (tmp$ = this.intersect_0) != null ? tmp$.point : null;
    this.panelSpaceCoords_2lzbc7$_0 = lazy(JsMapperUi$VisibleSurface$VisiblePixel$panelSpaceCoords$lambda(this, this.$outer));
  }
  Object.defineProperty(JsMapperUi$VisibleSurface$VisiblePixel.prototype, 'intersect_0', {
    get: function () {
      return this.intersect_vx7gy6$_0.value;
    }
  });
  JsMapperUi$VisibleSurface$VisiblePixel.prototype.addToGeom = function () {
    if (this.intersect_0 != null) {
      while (this.$outer.geom_0.vertices.length < this.pixelIndex) {
        this.$outer.geom_0.vertices[this.$outer.geom_0.vertices.length] = new Vector3(0, 0, 0);
      }
      this.$outer.geom_0.vertices[this.pixelIndex] = ensureNotNull(this.intersect_0).point;
    }
  };
  Object.defineProperty(JsMapperUi$VisibleSurface$VisiblePixel.prototype, 'panelSpaceCoords', {
    get: function () {
      return this.panelSpaceCoords_2lzbc7$_0.value;
    }
  });
  function JsMapperUi$VisibleSurface$VisiblePixel$intersect$lambda(this$VisiblePixel, this$VisibleSurface) {
    return function () {
      return this$VisibleSurface.findIntersection_0(this$VisiblePixel.cameraX, this$VisiblePixel.cameraY);
    };
  }
  function JsMapperUi$VisibleSurface$VisiblePixel$panelSpaceCoords$lambda(this$VisiblePixel, this$VisibleSurface) {
    return function () {
      if (this$VisiblePixel.positionInModel == null) {
        return null;
      }
       else {
        this$VisibleSurface.panelInfo.toPanelSpace_as37vi$(this$VisiblePixel.positionInModel);
        return to(this$VisiblePixel.positionInModel.x, this$VisiblePixel.positionInModel.y);
      }
    };
  }
  JsMapperUi$VisibleSurface$VisiblePixel.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'VisiblePixel',
    interfaces: []
  };
  JsMapperUi$VisibleSurface.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'VisibleSurface',
    interfaces: [MapperUi$VisibleSurface]
  };
  function JsMapperUi$CameraOrientation(cameraMatrix, aspect) {
    JsMapperUi$CameraOrientation$Companion_getInstance();
    this.cameraMatrix_sjfjmt$_0 = cameraMatrix;
    this.aspect_3ldvkj$_0 = aspect;
  }
  Object.defineProperty(JsMapperUi$CameraOrientation.prototype, 'cameraMatrix', {
    get: function () {
      return this.cameraMatrix_sjfjmt$_0;
    }
  });
  Object.defineProperty(JsMapperUi$CameraOrientation.prototype, 'aspect', {
    get: function () {
      return this.aspect_3ldvkj$_0;
    }
  });
  JsMapperUi$CameraOrientation.prototype.createCamera = function () {
    var $receiver = new PerspectiveCamera_init(45, this.aspect, 1, 10000);
    $receiver.matrix.fromArray(toDoubleArray(this.cameraMatrix.elements));
    $receiver.matrix.decompose($receiver.position, $receiver.quaternion, $receiver.scale);
    $receiver.updateMatrixWorld();
    return $receiver;
  };
  function JsMapperUi$CameraOrientation$Companion() {
    JsMapperUi$CameraOrientation$Companion_instance = this;
  }
  JsMapperUi$CameraOrientation$Companion.prototype.from_pak8zx$ = function (camera) {
    return new JsMapperUi$CameraOrientation(new Matrix4(toTypedArray(camera.matrix.toArray(undefined))), camera.aspect);
  };
  JsMapperUi$CameraOrientation$Companion.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'Companion',
    interfaces: []
  };
  var JsMapperUi$CameraOrientation$Companion_instance = null;
  function JsMapperUi$CameraOrientation$Companion_getInstance() {
    if (JsMapperUi$CameraOrientation$Companion_instance === null) {
      new JsMapperUi$CameraOrientation$Companion();
    }
    return JsMapperUi$CameraOrientation$Companion_instance;
  }
  JsMapperUi$CameraOrientation.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'CameraOrientation',
    interfaces: [MapperUi$CameraOrientation]
  };
  JsMapperUi$CameraOrientation.prototype.component1 = function () {
    return this.cameraMatrix;
  };
  JsMapperUi$CameraOrientation.prototype.component2 = function () {
    return this.aspect;
  };
  JsMapperUi$CameraOrientation.prototype.copy_lmdufr$ = function (cameraMatrix, aspect) {
    return new JsMapperUi$CameraOrientation(cameraMatrix === void 0 ? this.cameraMatrix : cameraMatrix, aspect === void 0 ? this.aspect : aspect);
  };
  JsMapperUi$CameraOrientation.prototype.toString = function () {
    return 'CameraOrientation(cameraMatrix=' + Kotlin.toString(this.cameraMatrix) + (', aspect=' + Kotlin.toString(this.aspect)) + ')';
  };
  JsMapperUi$CameraOrientation.prototype.hashCode = function () {
    var result = 0;
    result = result * 31 + Kotlin.hashCode(this.cameraMatrix) | 0;
    result = result * 31 + Kotlin.hashCode(this.aspect) | 0;
    return result;
  };
  JsMapperUi$CameraOrientation.prototype.equals = function (other) {
    return this === other || (other !== null && (typeof other === 'object' && (Object.getPrototypeOf(this) === Object.getPrototypeOf(other) && (Kotlin.equals(this.cameraMatrix, other.cameraMatrix) && Kotlin.equals(this.aspect, other.aspect)))));
  };
  function JsMapperUi$showCandidates$lambda$lambda$lambda$lambda($receiver) {
    $receiver.unaryPlus_pdl1vz$('Panel');
    return Unit;
  }
  function JsMapperUi$showCandidates$lambda$lambda$lambda$lambda_0($receiver) {
    $receiver.unaryPlus_pdl1vz$('Centroid dist');
    return Unit;
  }
  function JsMapperUi$showCandidates$lambda$lambda$lambda($receiver) {
    th($receiver, void 0, void 0, JsMapperUi$showCandidates$lambda$lambda$lambda$lambda);
    th($receiver, void 0, void 0, JsMapperUi$showCandidates$lambda$lambda$lambda$lambda_0);
    return Unit;
  }
  function JsMapperUi$showCandidates$lambda$lambda$lambda$lambda$lambda(closure$visibleSurface) {
    return function ($receiver) {
      $receiver.unaryPlus_pdl1vz$(closure$visibleSurface.modelSurface.name);
      return Unit;
    };
  }
  function JsMapperUi$showCandidates$lambda$lambda$lambda$lambda$lambda_0(closure$distance) {
    return function ($receiver) {
      $receiver.unaryPlus_pdl1vz$(closure$distance.toString());
      return Unit;
    };
  }
  function JsMapperUi$showCandidates$lambda$lambda$lambda$lambda_1(closure$visibleSurface, closure$distance) {
    return function ($receiver) {
      td($receiver, void 0, JsMapperUi$showCandidates$lambda$lambda$lambda$lambda$lambda(closure$visibleSurface));
      td($receiver, void 0, JsMapperUi$showCandidates$lambda$lambda$lambda$lambda$lambda_0(closure$distance));
      return Unit;
    };
  }
  function JsMapperUi$showCandidates$lambda$lambda(closure$orderedPanels) {
    return function ($receiver) {
      tr($receiver, void 0, JsMapperUi$showCandidates$lambda$lambda$lambda);
      var tmp$ = closure$orderedPanels;
      var b = closure$orderedPanels.size;
      var tmp$_0;
      tmp$_0 = tmp$.subList_vux9f0$(0, Math_0.min(5, b)).iterator();
      while (tmp$_0.hasNext()) {
        var element = tmp$_0.next();
        var visibleSurface = element.component1()
        , distance = element.component2();
        tr($receiver, void 0, JsMapperUi$showCandidates$lambda$lambda$lambda$lambda_1(visibleSurface, distance));
      }
      return Unit;
    };
  }
  function JsMapperUi$showCandidates$lambda(closure$orderedPanels) {
    return function ($receiver) {
      table($receiver, void 0, JsMapperUi$showCandidates$lambda$lambda(closure$orderedPanels));
      return Unit;
    };
  }
  JsMapperUi.prototype.showCandidates_kgc28x$ = function (orderedPanels) {
    var tmp$, tmp$_0;
    Kotlin.isType(tmp$ = orderedPanels, List) ? tmp$ : throwCCE();
    var firstGuess = first(orderedPanels);
    (Kotlin.isType(tmp$_0 = firstGuess.first.panelInfo.mesh.material, MeshBasicMaterial) ? tmp$_0 : throwCCE()).color.r = (Kotlin.isType(tmp$_0 = firstGuess.first.panelInfo.mesh.material, MeshBasicMaterial) ? tmp$_0 : throwCCE()).color.r + 0.25;
    clear(this.table_0);
    append(this.table_0, JsMapperUi$showCandidates$lambda(orderedPanels));
  };
  JsMapperUi.prototype.intersectingSurface_4c3mt7$ = function (x, y, visibleSurfaces) {
    var raycaster = new Raycaster_init();
    var pixelVector = new Vector2(x / this.uiWidth_0 * 2 - 1, -(y / this.uiHeight_0 * 2 - 1));
    raycaster.setFromCamera(pixelVector, this.uiCamera_0);
    var intersections = raycaster.intersectObject(this.uiScene_0, true);
    if (!(intersections.length === 0)) {
      var intersect = first_0(intersections);
      var firstOrNull$result;
      firstOrNull$break: do {
        var tmp$;
        tmp$ = visibleSurfaces.iterator();
        while (tmp$.hasNext()) {
          var element = tmp$.next();
          if (equals(element.modelSurface.name, intersect.object.name)) {
            firstOrNull$result = element;
            break firstOrNull$break;
          }
        }
        firstOrNull$result = null;
      }
       while (false);
      return firstOrNull$result;
    }
     else {
      return null;
    }
  };
  JsMapperUi.prototype.getScreenBox_0 = function () {
    return new Box2(new Vector2(0, 0), new Vector2(this.width_0, this.height_0));
  };
  JsMapperUi.prototype.calcBoundingBoxOnScreen_0 = function (box, screenCenter) {
    var minX = numberToInt(box.min.x * screenCenter.x + screenCenter.x);
    var maxX = numberToInt(box.max.x * screenCenter.x + screenCenter.x);
    var minY = numberToInt(-box.max.y * screenCenter.y + screenCenter.y);
    var maxY = numberToInt(-box.min.y * screenCenter.y + screenCenter.y);
    return new Box2(new Vector2(minX, minY), new Vector2(maxX, maxY));
  };
  JsMapperUi.prototype.showCamImage_q5ica7$$default = function (image, changeRegion) {
    if (!this.haveCamDimensions_0) {
      this.camWidth_0 = image.width;
      this.camHeight_0 = image.height;
      this.haveCamDimensions_0 = true;
      this.resizeTo_0(this.width_0, this.height_0);
    }
    this.ui2dCtx_0.resetTransform();
    (new CanvasBitmap(this.ui2dCanvas_0)).drawImage_6tj0gx$(image);
    if (changeRegion != null) {
      this.ui2dCtx_0.lineWidth = 2.0;
      this.ui2dCtx_0.strokeStyle = '#ff0000';
      this.ui2dCtx_0.strokeRect(changeRegion.x0, changeRegion.y0, changeRegion.width, changeRegion.height);
    }
  };
  JsMapperUi.prototype.showDiffImage_oa2j07$$default = function (deltaBitmap, changeRegion) {
    this.diffCtx_0.resetTransform();
    this.diffCtx_0.scale(this.diffCanvasScale_0, this.diffCanvasScale_0);
    (new CanvasBitmap(this.diffCanvas_0)).drawImage_6tj0gx$(deltaBitmap.asImage());
    if (changeRegion != null) {
      this.diffCtx_0.strokeStyle = '#ff0000';
      this.diffCtx_0.lineWidth = 1 / this.diffCanvasScale_0;
      this.diffCtx_0.strokeRect(changeRegion.x0, changeRegion.y0, changeRegion.width, changeRegion.height);
    }
  };
  JsMapperUi.prototype.showMessage_61zpoe$ = function (message) {
    this.messageDiv_0.innerText = message;
    console.log('Message:', message);
  };
  JsMapperUi.prototype.showMessage2_61zpoe$ = function (message) {
    this.message2Div_0.innerText = message;
  };
  JsMapperUi.prototype.showBefore_5151av$ = function (bitmap) {
    var tmp$, tmp$_0;
    var beforeCanvas = first_1(ensureNotNull(document.body), 'mapperUi-before-canvas');
    var beforeCtx = Kotlin.isType(tmp$ = beforeCanvas.getContext('2d'), CanvasRenderingContext2D) ? tmp$ : throwCCE();
    beforeCtx.resetTransform();
    beforeCtx.scale(0.3, 0.3);
    if (Kotlin.isType(bitmap, NativeBitmap))
      tmp$_0 = bitmap.canvas_8be2vx$;
    else if (Kotlin.isType(bitmap, CanvasBitmap))
      tmp$_0 = bitmap.canvas_8be2vx$;
    else
      tmp$_0 = bitmap;
    var renderBitmap = tmp$_0;
    beforeCtx.drawImage(renderBitmap, 0.0, 0.0);
  };
  JsMapperUi.prototype.showAfter_5151av$ = function (bitmap) {
    var tmp$, tmp$_0;
    var afterCanvas = first_1(ensureNotNull(document.body), 'mapperUi-after-canvas');
    var afterCtx = Kotlin.isType(tmp$ = afterCanvas.getContext('2d'), CanvasRenderingContext2D) ? tmp$ : throwCCE();
    afterCtx.resetTransform();
    afterCtx.scale(0.3, 0.3);
    if (Kotlin.isType(bitmap, NativeBitmap))
      tmp$_0 = bitmap.canvas_8be2vx$;
    else if (Kotlin.isType(bitmap, CanvasBitmap))
      tmp$_0 = bitmap.canvas_8be2vx$;
    else
      tmp$_0 = bitmap;
    var renderBitmap = tmp$_0;
    afterCtx.drawImage(renderBitmap, 0.0, 0.0);
  };
  JsMapperUi.prototype.showStats_qt1dr2$ = function (total, mapped, visible) {
    this.statsDiv_0.innerHTML = '<i class=' + '"' + 'fas fa-triangle' + '"' + '><\/i>Mapped: ' + mapped + ' / ' + total + '<br/>Visible: ' + visible;
  };
  JsMapperUi.prototype.pauseForUserInteraction = function () {
    this.clickedPause_0();
  };
  JsMapperUi.prototype.clickedPlay_0 = function () {
    this.showPauseMode_0(false);
    this.listener_0.onStart();
  };
  JsMapperUi.prototype.clickedPause_0 = function () {
    this.showPauseMode_0(true);
    this.listener_0.onPause();
  };
  JsMapperUi.prototype.showPauseMode_0 = function (isPaused) {
    this.pauseButton_0.style.opacity = isPaused ? '.5' : '1';
    this.playButton_0.style.opacity = !isPaused ? '.5' : '1';
  };
  JsMapperUi.prototype.clickedStop_0 = function () {
    this.listener_0.onStop();
  };
  JsMapperUi.prototype.goToSurface_0 = function (name) {
    var $receiver = this.modelSurfaceInfos_0.keys;
    var firstOrNull$result;
    firstOrNull$break: do {
      var tmp$;
      tmp$ = $receiver.iterator();
      while (tmp$.hasNext()) {
        var element = tmp$.next();
        if (equals(element.name, name)) {
          firstOrNull$result = element;
          break firstOrNull$break;
        }
      }
      firstOrNull$result = null;
    }
     while (false);
    var surface = firstOrNull$result;
    if (surface != null) {
      var panelInfo = ensureNotNull(this.modelSurfaceInfos_0.get_11rb$(surface));
      panelInfo.geom.computeBoundingBox();
      var surfaceCenter = panelInfo.center;
      var surfaceNormal = panelInfo.surfaceNormal;
      var newCamPosition = surfaceCenter.clone();
      newCamPosition.add(surfaceNormal.clone().multiplyScalar(100));
      this.resetCameraRotation_0();
      this.uiControls_0.setLookAt(newCamPosition.x, newCamPosition.y, newCamPosition.z, surfaceCenter.x, surfaceCenter.y, surfaceCenter.z, true);
    }
  };
  JsMapperUi.prototype.close = function () {
  };
  function JsMapperUi$StatusListener() {
  }
  JsMapperUi$StatusListener.$metadata$ = {
    kind: Kind_INTERFACE,
    simpleName: 'StatusListener',
    interfaces: []
  };
  function JsMapperUi$screen$lambda$lambda$lambda$lambda(this$JsMapperUi) {
    return function (it) {
      this$JsMapperUi.wireframe_0.position.y = this$JsMapperUi.wireframe_0.position.y + 10;
      return Unit;
    };
  }
  function JsMapperUi$screen$lambda$lambda$lambda(this$JsMapperUi) {
    return function ($receiver) {
      $receiver.unaryPlus_pdl1vz$('\u25B2');
      set_onClickFunction($receiver, JsMapperUi$screen$lambda$lambda$lambda$lambda(this$JsMapperUi));
      return Unit;
    };
  }
  function JsMapperUi$screen$lambda$lambda$lambda$lambda_0(this$JsMapperUi) {
    return function (it) {
      this$JsMapperUi.wireframe_0.position.y = this$JsMapperUi.wireframe_0.position.y - 10;
      return Unit;
    };
  }
  function JsMapperUi$screen$lambda$lambda$lambda_0(this$JsMapperUi) {
    return function ($receiver) {
      $receiver.unaryPlus_pdl1vz$('\u25BC');
      set_onClickFunction($receiver, JsMapperUi$screen$lambda$lambda$lambda$lambda_0(this$JsMapperUi));
      return Unit;
    };
  }
  function JsMapperUi$screen$lambda$lambda$lambda$lambda_1(this$JsMapperUi) {
    return function (it) {
      this$JsMapperUi.clickedPlay_0();
      return Unit;
    };
  }
  function JsMapperUi$screen$lambda$lambda$lambda_1(this$JsMapperUi) {
    return function ($receiver) {
      i($receiver, 'fas fa-play');
      set_onClickFunction($receiver, JsMapperUi$screen$lambda$lambda$lambda$lambda_1(this$JsMapperUi));
      return Unit;
    };
  }
  function JsMapperUi$screen$lambda$lambda$lambda$lambda_2(this$JsMapperUi) {
    return function (it) {
      this$JsMapperUi.clickedPause_0();
      return Unit;
    };
  }
  function JsMapperUi$screen$lambda$lambda$lambda_2(this$JsMapperUi) {
    return function ($receiver) {
      i($receiver, 'fas fa-pause');
      set_onClickFunction($receiver, JsMapperUi$screen$lambda$lambda$lambda$lambda_2(this$JsMapperUi));
      return Unit;
    };
  }
  function JsMapperUi$screen$lambda$lambda$lambda$lambda_3(this$JsMapperUi) {
    return function (it) {
      this$JsMapperUi.clickedStop_0();
      return Unit;
    };
  }
  function JsMapperUi$screen$lambda$lambda$lambda_3(this$JsMapperUi) {
    return function ($receiver) {
      i($receiver, 'fas fa-stop');
      set_onClickFunction($receiver, JsMapperUi$screen$lambda$lambda$lambda$lambda_3(this$JsMapperUi));
      return Unit;
    };
  }
  function JsMapperUi$screen$lambda$lambda$lambda$lambda_4(this$JsMapperUi) {
    return function (it) {
      var surfaceName = window.prompt('Surface:');
      var tmp$ = surfaceName != null;
      if (tmp$) {
        tmp$ = surfaceName.length > 0;
      }
      if (tmp$) {
        this$JsMapperUi.goToSurface_0(surfaceName.toUpperCase());
      }
      return Unit;
    };
  }
  function JsMapperUi$screen$lambda$lambda$lambda_4(this$JsMapperUi) {
    return function ($receiver) {
      i($receiver, 'fas fa-sign-in-alt');
      set_onClickFunction($receiver, JsMapperUi$screen$lambda$lambda$lambda$lambda_4(this$JsMapperUi));
      return Unit;
    };
  }
  function JsMapperUi$screen$lambda$lambda$lambda_5($receiver) {
    return Unit;
  }
  function JsMapperUi$screen$lambda$lambda(this$JsMapperUi) {
    return function ($receiver) {
      button($receiver, void 0, void 0, void 0, void 0, void 0, JsMapperUi$screen$lambda$lambda$lambda(this$JsMapperUi));
      button($receiver, void 0, void 0, void 0, void 0, void 0, JsMapperUi$screen$lambda$lambda$lambda_0(this$JsMapperUi));
      button($receiver, void 0, void 0, void 0, void 0, void 0, JsMapperUi$screen$lambda$lambda$lambda_1(this$JsMapperUi));
      button($receiver, void 0, void 0, void 0, void 0, void 0, JsMapperUi$screen$lambda$lambda$lambda_2(this$JsMapperUi));
      button($receiver, void 0, void 0, void 0, void 0, void 0, JsMapperUi$screen$lambda$lambda$lambda_3(this$JsMapperUi));
      button($receiver, void 0, void 0, void 0, void 0, void 0, JsMapperUi$screen$lambda$lambda$lambda_4(this$JsMapperUi));
      select($receiver, 'mapperUi-sessionSelector', JsMapperUi$screen$lambda$lambda$lambda_5);
      return Unit;
    };
  }
  function JsMapperUi$screen$lambda$lambda_0(this$JsMapperUi) {
    return function ($receiver) {
      $receiver.width = this$JsMapperUi.width_0.toString() + 'px';
      $receiver.height = this$JsMapperUi.height_0.toString() + 'px';
      return Unit;
    };
  }
  function JsMapperUi$screen$lambda$lambda_1($receiver) {
    return Unit;
  }
  function JsMapperUi$screen$lambda$lambda_2(this$JsMapperUi) {
    return function ($receiver) {
      $receiver.width = this$JsMapperUi.width_0.toString() + 'px';
      $receiver.height = this$JsMapperUi.height_0.toString() + 'px';
      return Unit;
    };
  }
  function JsMapperUi$screen$lambda$lambda_3(this$JsMapperUi) {
    return function ($receiver) {
      $receiver.width = this$JsMapperUi.width_0.toString() + 'px';
      $receiver.height = this$JsMapperUi.height_0.toString() + 'px';
      return Unit;
    };
  }
  function JsMapperUi$screen$lambda$lambda_4(this$JsMapperUi) {
    return function ($receiver) {
      $receiver.width = this$JsMapperUi.width_0.toString() + 'px';
      $receiver.height = this$JsMapperUi.height_0.toString() + 'px';
      return Unit;
    };
  }
  function JsMapperUi$screen$lambda$lambda_5($receiver) {
    return Unit;
  }
  function JsMapperUi$screen$lambda$lambda_6($receiver) {
    return Unit;
  }
  function JsMapperUi$screen$lambda$lambda_7($receiver) {
    return Unit;
  }
  function JsMapperUi$screen$lambda$lambda_8($receiver) {
    return Unit;
  }
  function JsMapperUi$screen$lambda(this$JsMapperUi) {
    return function ($receiver) {
      set_tabIndex($receiver, '-1');
      div($receiver, 'mapperUi-controls', JsMapperUi$screen$lambda$lambda(this$JsMapperUi));
      canvas($receiver, 'mapperUi-2d-canvas', JsMapperUi$screen$lambda$lambda_0(this$JsMapperUi));
      div($receiver, 'mapperUi-3d-div', JsMapperUi$screen$lambda$lambda_1);
      canvas($receiver, 'mapperUi-diff-canvas', JsMapperUi$screen$lambda$lambda_2(this$JsMapperUi));
      canvas($receiver, 'mapperUi-before-canvas', JsMapperUi$screen$lambda$lambda_3(this$JsMapperUi));
      canvas($receiver, 'mapperUi-after-canvas', JsMapperUi$screen$lambda$lambda_4(this$JsMapperUi));
      div($receiver, 'mapperUi-stats', JsMapperUi$screen$lambda$lambda_5);
      div($receiver, 'mapperUi-message', JsMapperUi$screen$lambda$lambda_6);
      div($receiver, 'mapperUi-message2', JsMapperUi$screen$lambda$lambda_7);
      div($receiver, 'mapperUi-table', JsMapperUi$screen$lambda$lambda_8);
      return Unit;
    };
  }
  function JsMapperUi_init$lambda(this$JsMapperUi) {
    return function (event) {
      var tmp$, tmp$_0;
      tmp$_0 = Kotlin.isType(tmp$ = event, KeyboardEvent) ? tmp$ : throwCCE();
      this$JsMapperUi.gotUiKeypress_0(tmp$_0);
      return Unit;
    };
  }
  JsMapperUi.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'JsMapperUi',
    interfaces: [HostedWebApp, MapperUi]
  };
  function get_center($receiver) {
    return $receiver.max.clone().sub($receiver.min).divideScalar(2).add($receiver.min);
  }
  function project($receiver, camera) {
    $receiver.min.project(camera);
    $receiver.max.project(camera);
    if ($receiver.min.x > $receiver.max.x) {
      var temp = $receiver.min.x;
      $receiver.min.x = $receiver.max.x;
      $receiver.max.x = temp;
    }
    if ($receiver.min.y > $receiver.max.y) {
      var temp_0 = $receiver.min.y;
      $receiver.min.y = $receiver.max.y;
      $receiver.max.y = temp_0;
    }
    if ($receiver.min.z > $receiver.max.z) {
      var temp_1 = $receiver.min.z;
      $receiver.min.z = $receiver.max.z;
      $receiver.max.z = temp_1;
    }
    return $receiver;
  }
  function PanelInfo(name, faces, mesh, geom, lineMaterial) {
    this.name = name;
    this.faces = faces;
    this.mesh = mesh;
    this.geom = geom;
    this.lineMaterial = lineMaterial;
    this._boundingBox_w1eeoe$_0 = lazy(PanelInfo$_boundingBox$lambda(this));
    this.rotator_awqgkt$_0 = lazy(PanelInfo$rotator$lambda(this));
    this.normalBoundingBox_5laili$_0 = lazy(PanelInfo$normalBoundingBox$lambda(this));
    this.normalBoundingBoxVolume_u6vqlc$_0 = lazy(PanelInfo$normalBoundingBoxVolume$lambda(this));
    this._surfaceNormal_xndh7f$_0 = lazy(PanelInfo$_surfaceNormal$lambda(this));
    this.boxOnScreen = null;
  }
  Object.defineProperty(PanelInfo.prototype, 'vertices', {
    get: function () {
      var tmp$;
      var v = LinkedHashSet_init();
      tmp$ = this.faces.iterator();
      while (tmp$.hasNext()) {
        var face = tmp$.next();
        v.add_11rb$(this.geom.vertices[face.a]);
        v.add_11rb$(this.geom.vertices[face.b]);
        v.add_11rb$(this.geom.vertices[face.c]);
      }
      return v;
    }
  });
  Object.defineProperty(PanelInfo.prototype, '_boundingBox', {
    get: function () {
      return this._boundingBox_w1eeoe$_0.value;
    }
  });
  Object.defineProperty(PanelInfo.prototype, 'boundingBox', {
    get: function () {
      return this._boundingBox.clone();
    }
  });
  Object.defineProperty(PanelInfo.prototype, 'rotator_0', {
    get: function () {
      return this.rotator_awqgkt$_0.value;
    }
  });
  PanelInfo.prototype.toSurfaceNormal_as37vi$ = function (point) {
    this.rotator_0.rotate_22wt45$([point]);
    return point;
  };
  Object.defineProperty(PanelInfo.prototype, 'normalBoundingBox_0', {
    get: function () {
      return this.normalBoundingBox_5laili$_0.value;
    }
  });
  Object.defineProperty(PanelInfo.prototype, 'normalBoundingBoxVolume_0', {
    get: function () {
      return this.normalBoundingBoxVolume_u6vqlc$_0.value;
    }
  });
  PanelInfo.prototype.toPanelSpace_as37vi$ = function (point) {
    var pt = point.clone();
    pt = this.toSurfaceNormal_as37vi$(pt);
    pt.sub(this.normalBoundingBox_0.min);
    pt.divide(this.normalBoundingBoxVolume_0);
    return pt;
  };
  PanelInfo.prototype.select = function () {
    this.lineMaterial.color.r = 1.0;
    this.lineMaterial.color.g = 0.0;
  };
  PanelInfo.prototype.deselect = function () {
    this.lineMaterial.color.r = 0.0;
    this.lineMaterial.color.g = 1.0;
  };
  Object.defineProperty(PanelInfo.prototype, 'center', {
    get: function () {
      return this.boundingBox.getCenter();
    }
  });
  Object.defineProperty(PanelInfo.prototype, 'isMultiFaced', {
    get: function () {
      return this.faces.size > 1;
    }
  });
  Object.defineProperty(PanelInfo.prototype, '_surfaceNormal', {
    get: function () {
      return this._surfaceNormal_xndh7f$_0.value;
    }
  });
  Object.defineProperty(PanelInfo.prototype, 'surfaceNormal', {
    get: function () {
      return this._surfaceNormal.clone();
    }
  });
  function PanelInfo$_boundingBox$lambda(this$PanelInfo) {
    return function () {
      var tmp$;
      var boundingBox = new Box3();
      tmp$ = this$PanelInfo.vertices.iterator();
      while (tmp$.hasNext()) {
        var vertex = tmp$.next();
        boundingBox.expandByPoint(vertex);
      }
      return boundingBox.translate(this$PanelInfo.mesh.getWorldPosition());
    };
  }
  function PanelInfo$rotator$lambda(this$PanelInfo) {
    return function () {
      return new Rotator(this$PanelInfo.surfaceNormal, new Vector3(0, 0, 1));
    };
  }
  function PanelInfo$normalBoundingBox$lambda(this$PanelInfo) {
    return function () {
      var tmp$;
      var worldPos = this$PanelInfo.mesh.getWorldPosition();
      var boundingBox = new Box3();
      tmp$ = this$PanelInfo.vertices.iterator();
      while (tmp$.hasNext()) {
        var vertex = tmp$.next();
        boundingBox.expandByPoint(this$PanelInfo.toSurfaceNormal_as37vi$(vertex).add(worldPos));
      }
      return boundingBox;
    };
  }
  function PanelInfo$normalBoundingBoxVolume$lambda(this$PanelInfo) {
    return function () {
      return this$PanelInfo.normalBoundingBox_0.max.clone().sub(this$PanelInfo.normalBoundingBox_0.min);
    };
  }
  function PanelInfo$_surfaceNormal$lambda(this$PanelInfo) {
    return function () {
      var tmp$, tmp$_0;
      var faceNormalSum = new Vector3();
      var totalArea = 0.0;
      tmp$ = this$PanelInfo.faces.iterator();
      while (tmp$.hasNext()) {
        var face = tmp$.next();
        var triangle = new Triangle(this$PanelInfo.geom.vertices[face.a], this$PanelInfo.geom.vertices[face.b], this$PanelInfo.geom.vertices[face.c]);
        var faceArea = typeof (tmp$_0 = triangle.getArea()) === 'number' ? tmp$_0 : throwCCE();
        faceNormalSum.addScaledVector(ensureNotNull(face.normal), faceArea);
        totalArea += faceArea;
      }
      return faceNormalSum.divideScalar(totalArea);
    };
  }
  PanelInfo.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'PanelInfo',
    interfaces: []
  };
  function Launcher(parentNode) {
    this.parentNode = parentNode;
    this.fakeDomContainer_0 = new FakeDomContainer();
  }
  function Launcher$add$lambda$lambda(closure$name, this$, this$Launcher, closure$onLaunch) {
    return function (it) {
      console.log('Launch ' + closure$name, this$);
      return this$Launcher.fakeDomContainer_0.createFrame_56nt9y$(closure$name, closure$onLaunch());
    };
  }
  function Launcher$add$lambda(closure$name, this$Launcher, closure$onLaunch) {
    return function ($receiver) {
      var tmp$;
      appendText($receiver, closure$name);
      (Kotlin.isType(tmp$ = $receiver, HTMLElement) ? tmp$ : throwCCE()).onclick = Launcher$add$lambda$lambda(closure$name, $receiver, this$Launcher, closure$onLaunch);
      return Unit;
    };
  }
  Launcher.prototype.add_yfl68i$ = function (name, onLaunch) {
    var tmp$;
    return Kotlin.isType(tmp$ = appendElement(this.parentNode, 'button', Launcher$add$lambda(name, this, onLaunch)), HTMLButtonElement) ? tmp$ : throwCCE();
  };
  Launcher.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Launcher',
    interfaces: []
  };
  function Comparator$ObjectLiteral_1(closure$comparison) {
    this.closure$comparison = closure$comparison;
  }
  Comparator$ObjectLiteral_1.prototype.compare = function (a, b) {
    return this.closure$comparison(a, b);
  };
  Comparator$ObjectLiteral_1.$metadata$ = {kind: Kind_CLASS, interfaces: [Comparator]};
  var compareBy$lambda_0 = wrapFunction(function () {
    var compareValues = Kotlin.kotlin.comparisons.compareValues_s00gnj$;
    return function (closure$selector) {
      return function (a, b) {
        var selector = closure$selector;
        return compareValues(selector(a), selector(b));
      };
    };
  });
  function SheepSimulator() {
    this.display_0 = new JsDisplay();
    this.network_0 = new FakeNetwork(void 0, this.display_0.forNetwork());
    this.dmxUniverse_0 = new FakeDmxUniverse();
    var $receiver = new SheepModel();
    $receiver.load();
    this.sheepModel_0 = $receiver;
    this.shows_0 = AllShows$Companion_getInstance().allShows;
    this.visualizer_0 = new Visualizer(this.sheepModel_0, this.display_0.forVisualizer());
    this.fs_0 = new FakeFs();
    this.beatSource_0 = new BridgedBeatSource(window.location.hostname + ':' + '8006');
    this.pinky_0 = new Pinky(this.sheepModel_0, this.shows_0, this.network_0, this.dmxUniverse_0, this.beatSource_0, new JsClock(), this.fs_0, new PermissiveFirmwareDaddy(), this.display_0.forPinky(), true);
    this.pinkyScope_0 = CoroutineScope_0(coroutines.Dispatchers.Main);
    this.brainScope_0 = CoroutineScope_0(coroutines.Dispatchers.Main);
    this.mapperScope_0 = CoroutineScope_0(coroutines.Dispatchers.Main);
  }
  function Coroutine$SheepSimulator$start$lambda$lambda(this$SheepSimulator_0, $receiver_0, controller, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.$controller = controller;
    this.exceptionState_0 = 1;
    this.local$this$SheepSimulator = this$SheepSimulator_0;
  }
  Coroutine$SheepSimulator$start$lambda$lambda.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$SheepSimulator$start$lambda$lambda.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$SheepSimulator$start$lambda$lambda.prototype.constructor = Coroutine$SheepSimulator$start$lambda$lambda;
  Coroutine$SheepSimulator$start$lambda$lambda.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.state_0 = 2;
            this.result_0 = this.local$this$SheepSimulator.pinky_0.run(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 1:
            throw this.exception_0;
          case 2:
            return this.result_0;
          default:this.state_0 = 1;
            throw new Error('State Machine Unreachable execution');
        }
      }
       catch (e) {
        if (this.state_0 === 1) {
          this.exceptionState_0 = this.state_0;
          throw e;
        }
         else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  function SheepSimulator$start$lambda$lambda(this$SheepSimulator_0) {
    return function ($receiver_0, continuation_0, suspended) {
      var instance = new Coroutine$SheepSimulator$start$lambda$lambda(this$SheepSimulator_0, $receiver_0, this, continuation_0);
      if (suspended)
        return instance;
      else
        return instance.doResume(null);
    };
  }
  function SheepSimulator$start$lambda$lambda_0(this$SheepSimulator) {
    return function () {
      var webUiClientLink = this$SheepSimulator.network_0.link();
      var $receiver = new PubSub$Client(webUiClientLink, this$SheepSimulator.pinky_0.address, 8004);
      $receiver.install_stpyu4$(gadgetModule);
      var pubSub = $receiver;
      return document.createUiApp(pubSub);
    };
  }
  function Coroutine$SheepSimulator$start$lambda$lambda$lambda(closure$mapper_0, $receiver_0, controller, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.$controller = controller;
    this.exceptionState_0 = 1;
    this.local$closure$mapper = closure$mapper_0;
  }
  Coroutine$SheepSimulator$start$lambda$lambda$lambda.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$SheepSimulator$start$lambda$lambda$lambda.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$SheepSimulator$start$lambda$lambda$lambda.prototype.constructor = Coroutine$SheepSimulator$start$lambda$lambda$lambda;
  Coroutine$SheepSimulator$start$lambda$lambda$lambda.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            return this.local$closure$mapper.start(), Unit;
          case 1:
            throw this.exception_0;
          default:this.state_0 = 1;
            throw new Error('State Machine Unreachable execution');
        }
      }
       catch (e) {
        if (this.state_0 === 1) {
          this.exceptionState_0 = this.state_0;
          throw e;
        }
         else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  function SheepSimulator$start$lambda$lambda$lambda(closure$mapper_0) {
    return function ($receiver_0, continuation_0, suspended) {
      var instance = new Coroutine$SheepSimulator$start$lambda$lambda$lambda(closure$mapper_0, $receiver_0, this, continuation_0);
      if (suspended)
        return instance;
      else
        return instance.doResume(null);
    };
  }
  function SheepSimulator$start$lambda$lambda_1(this$SheepSimulator) {
    return function () {
      var mapperUi = new JsMapperUi(this$SheepSimulator.visualizer_0);
      var mediaDevices = new FakeMediaDevices(this$SheepSimulator.visualizer_0);
      var mapper = new Mapper(this$SheepSimulator.network_0, this$SheepSimulator.sheepModel_0, mapperUi, mediaDevices, this$SheepSimulator.pinky_0.address);
      launch(this$SheepSimulator.mapperScope_0, void 0, void 0, SheepSimulator$start$lambda$lambda$lambda(mapper));
      return mapperUi;
    };
  }
  function Coroutine$SheepSimulator$start$lambda$lambda$lambda_0(closure$brain_0, $receiver_0, controller, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.$controller = controller;
    this.exceptionState_0 = 1;
    this.local$closure$brain = closure$brain_0;
  }
  Coroutine$SheepSimulator$start$lambda$lambda$lambda_0.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$SheepSimulator$start$lambda$lambda$lambda_0.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$SheepSimulator$start$lambda$lambda$lambda_0.prototype.constructor = Coroutine$SheepSimulator$start$lambda$lambda$lambda_0;
  Coroutine$SheepSimulator$start$lambda$lambda$lambda_0.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.state_0 = 2;
            this.result_0 = randomDelay(1000, this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 1:
            throw this.exception_0;
          case 2:
            this.state_0 = 3;
            this.result_0 = this.local$closure$brain.run(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 3:
            return this.result_0;
          default:this.state_0 = 1;
            throw new Error('State Machine Unreachable execution');
        }
      }
       catch (e) {
        if (this.state_0 === 1) {
          this.exceptionState_0 = this.state_0;
          throw e;
        }
         else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  function SheepSimulator$start$lambda$lambda$lambda_0(closure$brain_0) {
    return function ($receiver_0, continuation_0, suspended) {
      var instance = new Coroutine$SheepSimulator$start$lambda$lambda$lambda_0(closure$brain_0, $receiver_0, this, continuation_0);
      if (suspended)
        return instance;
      else
        return instance.doResume(null);
    };
  }
  function Coroutine$SheepSimulator$start$lambda$lambda_0(continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.exceptionState_0 = 1;
  }
  Coroutine$SheepSimulator$start$lambda$lambda_0.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$SheepSimulator$start$lambda$lambda_0.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$SheepSimulator$start$lambda$lambda_0.prototype.constructor = Coroutine$SheepSimulator$start$lambda$lambda_0;
  Coroutine$SheepSimulator$start$lambda$lambda_0.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.state_0 = 2;
            this.result_0 = delay(L200000, this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 1:
            throw this.exception_0;
          case 2:
            return this.result_0;
          default:this.state_0 = 1;
            throw new Error('State Machine Unreachable execution');
        }
      }
       catch (e) {
        if (this.state_0 === 1) {
          this.exceptionState_0 = this.state_0;
          throw e;
        }
         else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  function SheepSimulator$start$lambda$lambda_2(continuation_0, suspended) {
    var instance = new Coroutine$SheepSimulator$start$lambda$lambda_0(continuation_0);
    if (suspended)
      return instance;
    else
      return instance.doResume(null);
  }
  function Coroutine$SheepSimulator$start$lambda(this$SheepSimulator_0, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.exceptionState_0 = 1;
    this.local$this$SheepSimulator = this$SheepSimulator_0;
    this.local$queryParams = void 0;
    this.local$launcher = void 0;
    this.local$$receiver = void 0;
  }
  Coroutine$SheepSimulator$start$lambda.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$SheepSimulator$start$lambda.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$SheepSimulator$start$lambda.prototype.constructor = Coroutine$SheepSimulator$start$lambda;
  Coroutine$SheepSimulator$start$lambda.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.local$queryParams = decodeQueryParams(ensureNotNull(document.location));
            launch(this.local$this$SheepSimulator.pinkyScope_0, void 0, void 0, SheepSimulator$start$lambda$lambda(this.local$this$SheepSimulator));
            this.local$launcher = new Launcher(ensureNotNull(document.getElementById('launcher')));
            this.local$$receiver = this.local$launcher.add_yfl68i$('Web UI', SheepSimulator$start$lambda$lambda_0(this.local$this$SheepSimulator));
            this.state_0 = 2;
            this.result_0 = delay(L1000, this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 1:
            throw this.exception_0;
          case 2:
            this.local$$receiver.click();
            this.local$launcher.add_yfl68i$('Mapper', SheepSimulator$start$lambda$lambda_1(this.local$this$SheepSimulator));
            var key = 'pixelDensity';
            var tmp$;
            var pixelDensity = toDouble((tmp$ = this.local$queryParams.get_11rb$(key)) != null ? tmp$ : '0.2');
            var key_0 = 'pixelSpacing';
            var tmp$_0;
            var pixelSpacing = toDouble((tmp$_0 = this.local$queryParams.get_11rb$(key_0)) != null ? tmp$_0 : '3');
            var pixelArranger = new SwirlyPixelArranger(pixelDensity, pixelSpacing);
            var totalPixels = {v: 0};
            var tmp$_1, tmp$_0_0;
            var index = 0;
            tmp$_1 = sortedWith(this.local$this$SheepSimulator.sheepModel_0.panels, new Comparator$ObjectLiteral_1(compareBy$lambda_0(getPropertyCallableRef('name', 1, function ($receiver) {
              return $receiver.name;
            })))).iterator();
            while (tmp$_1.hasNext()) {
              var item = tmp$_1.next();
              var this$SheepSimulator = this.local$this$SheepSimulator;
              var index_0 = checkIndexOverflow((tmp$_0_0 = index, index = tmp$_0_0 + 1 | 0, tmp$_0_0));
              var tmp$_2;
              var vizPanel = this$SheepSimulator.visualizer_0.addPanel_jfju1k$(item);
              var pixelPositions = pixelArranger.arrangePixels_zdreix$(vizPanel);
              vizPanel.vizPixels = new VizPanel$VizPixels(vizPanel, pixelPositions);
              totalPixels.v = totalPixels.v + pixelPositions.length | 0;
              document.getElementById('visualizerPixelCount').innerText = totalPixels.v.toString();
              var $receiver = ensureNotNull(vizPanel.getPixelLocations());
              var destination = ArrayList_init_1($receiver.length);
              var tmp$_3;
              for (tmp$_3 = 0; tmp$_3 !== $receiver.length; ++tmp$_3) {
                var item_0 = $receiver[tmp$_3];
                destination.add_11rb$(new Vector2F(item_0.x, item_0.y));
              }
              var pixelLocations = destination;
              this$SheepSimulator.pinky_0.providePixelMapping_CHEAT_cafowi$(item, pixelLocations);
              var brain = new Brain('brain//' + index_0, this$SheepSimulator.network_0, this$SheepSimulator.display_0.forBrain(), (tmp$_2 = vizPanel.vizPixels) != null ? tmp$_2 : SheepSimulator$NullPixels_getInstance());
              this$SheepSimulator.pinky_0.providePanelMapping_CHEAT_iegnfh$(new BrainId(brain.id), item);
              launch(this$SheepSimulator.brainScope_0, void 0, void 0, SheepSimulator$start$lambda$lambda$lambda_0(brain));
            }

            var tmp$_4;
            tmp$_4 = this.local$this$SheepSimulator.sheepModel_0.eyes.iterator();
            while (tmp$_4.hasNext()) {
              var element = tmp$_4.next();
              var this$SheepSimulator_0 = this.local$this$SheepSimulator;
              this$SheepSimulator_0.visualizer_0.addMovingHead_g9d0gu$(element, this$SheepSimulator_0.dmxUniverse_0);
            }

            return doRunBlocking(SheepSimulator$start$lambda$lambda_2), Unit;
          default:this.state_0 = 1;
            throw new Error('State Machine Unreachable execution');
        }
      }
       catch (e) {
        if (this.state_0 === 1) {
          this.exceptionState_0 = this.state_0;
          throw e;
        }
         else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  function SheepSimulator$start$lambda(this$SheepSimulator_0) {
    return function (continuation_0, suspended) {
      var instance = new Coroutine$SheepSimulator$start$lambda(this$SheepSimulator_0, continuation_0);
      if (suspended)
        return instance;
      else
        return instance.doResume(null);
    };
  }
  SheepSimulator.prototype.start = function () {
    doRunBlocking(SheepSimulator$start$lambda(this));
  };
  function SheepSimulator$NullPixels() {
    SheepSimulator$NullPixels_instance = this;
    this.size_dint0g$_0 = 0;
  }
  Object.defineProperty(SheepSimulator$NullPixels.prototype, 'size', {
    get: function () {
      return this.size_dint0g$_0;
    }
  });
  SheepSimulator$NullPixels.prototype.get_za3lpa$ = function (i) {
    return Color$Companion_getInstance().BLACK;
  };
  SheepSimulator$NullPixels.prototype.set_ibd5tj$ = function (i, color) {
  };
  SheepSimulator$NullPixels.prototype.set_tmuqsv$ = function (colors) {
  };
  SheepSimulator$NullPixels.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'NullPixels',
    interfaces: [Pixels]
  };
  var SheepSimulator$NullPixels_instance = null;
  function SheepSimulator$NullPixels_getInstance() {
    if (SheepSimulator$NullPixels_instance === null) {
      new SheepSimulator$NullPixels();
    }
    return SheepSimulator$NullPixels_instance;
  }
  SheepSimulator.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'SheepSimulator',
    interfaces: []
  };
  function JsClock() {
  }
  JsClock.prototype.now = function () {
    return Date.now();
  };
  JsClock.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'JsClock',
    interfaces: [Clock_0]
  };
  function BridgedBeatSource(url) {
    this.beatData_0 = new BeatData(0.0, 0, void 0, 0.0);
    this.webSocket_0 = new WebSocket('ws://' + url + '/bridge/beatSource');
    this.json_0 = new Json(JsonConfiguration.Companion.Stable);
    this.webSocket_0.onopen = BridgedBeatSource_init$lambda;
    this.webSocket_0.onmessage = BridgedBeatSource_init$lambda_0(this);
    this.webSocket_0.onerror = BridgedBeatSource_init$lambda_1(this);
    this.webSocket_0.onclose = BridgedBeatSource_init$lambda_2;
  }
  BridgedBeatSource.prototype.getBeatData = function () {
    return this.beatData_0;
  };
  function BridgedBeatSource_init$lambda(it) {
    console.log('WebSocket open!', it);
    return Unit;
  }
  function BridgedBeatSource_init$lambda_0(this$BridgedBeatSource) {
    return function (it) {
      var tmp$;
      var buf = typeof (tmp$ = it.data) === 'string' ? tmp$ : throwCCE();
      println('buf is ' + buf);
      this$BridgedBeatSource.beatData_0 = this$BridgedBeatSource.json_0.parse_awif5v$(BeatData$Companion_getInstance().serializer(), buf);
      return null;
    };
  }
  function BridgedBeatSource_init$lambda_1(this$BridgedBeatSource) {
    return function (it) {
      this$BridgedBeatSource.beatData_0 = new BeatData(0.0, 500, 4);
      console.log('WebSocket error!', it);
      return Unit;
    };
  }
  function BridgedBeatSource_init$lambda_2(it) {
    console.log('WebSocket close!', it);
    return Unit;
  }
  BridgedBeatSource.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'BridgedBeatSource',
    interfaces: [BeatSource]
  };
  function get_disabled($receiver) {
    return equals($receiver.getAttribute('disabled'), 'disabled');
  }
  function set_disabled($receiver, value) {
    if (value) {
      $receiver.setAttribute('disabled', 'disabled');
    }
     else {
      $receiver.removeAttribute('disabled');
    }
  }
  function forEach($receiver, action) {
    var tmp$;
    tmp$ = $receiver.length;
    for (var i = 0; i < tmp$; i++) {
      action(ensureNotNull($receiver.item(i)));
    }
  }
  function clear_0($receiver) {
    while ($receiver.length > 0) {
      $receiver.remove(ensureNotNull($receiver.item(0)));
    }
  }
  function first_1($receiver, className) {
    var tmp$;
    return ensureNotNull((tmp$ = $receiver.getElementsByClassName(className)[0]) == null || Kotlin.isType(tmp$, HTMLElement) ? tmp$ : throwCCE());
  }
  function context2d($receiver) {
    var tmp$;
    return Kotlin.isType(tmp$ = ensureNotNull($receiver.getContext('2d')), CanvasRenderingContext2D) ? tmp$ : throwCCE();
  }
  function HostedWebApp() {
  }
  HostedWebApp.$metadata$ = {
    kind: Kind_INTERFACE,
    simpleName: 'HostedWebApp',
    interfaces: []
  };
  function DomContainer() {
  }
  function DomContainer$Frame() {
  }
  DomContainer$Frame.$metadata$ = {
    kind: Kind_INTERFACE,
    simpleName: 'Frame',
    interfaces: []
  };
  DomContainer.$metadata$ = {
    kind: Kind_INTERFACE,
    simpleName: 'DomContainer',
    interfaces: []
  };
  function FakeDomContainer() {
  }
  FakeDomContainer.prototype.createFrame_56nt9y$ = function (name, hostedWebApp) {
    return document.createFakeClientDevice(name, hostedWebApp);
  };
  FakeDomContainer.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'FakeDomContainer',
    interfaces: [DomContainer]
  };
  function WebUi() {
    WebUi_instance = this;
  }
  WebUi.prototype.createPubSubClient = function (network, pinkyAddress) {
    var $receiver = new PubSub$Client(network.link(), pinkyAddress, 8004);
    $receiver.install_stpyu4$(gadgetModule);
    return $receiver;
  };
  WebUi.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'WebUi',
    interfaces: []
  };
  var WebUi_instance = null;
  function WebUi_getInstance() {
    if (WebUi_instance === null) {
      new WebUi();
    }
    return WebUi_instance;
  }
  function RealMediaDevices() {
    this.$delegate_ikujlt$_0 = MainScope();
  }
  function RealMediaDevices$getCamera$ObjectLiteral(this$RealMediaDevices) {
    var tmp$ = window.navigator.mediaDevices;
    var video = {width: {min: 1024, ideal: 1280, max: 1920}, height: {min: 776, ideal: 720, max: 1080}};
    var o = {};
    o['video'] = video;
    o['audio'] = false;
    this.camPromise = tmp$.getUserMedia(o);
    this.videoTrack_k1tc6d$_0 = this.videoTrack_k1tc6d$_0;
    var tmp$_0;
    this.videoEl = Kotlin.isType(tmp$_0 = document.createElement('video'), HTMLVideoElement) ? tmp$_0 : throwCCE();
    this.videoEl.autoplay = true;
    this.camPromise.then(RealMediaDevices$getCamera$RealMediaDevices$getCamera$ObjectLiteral_init$lambda(this, this$RealMediaDevices)).catch(RealMediaDevices$getCamera$RealMediaDevices$getCamera$ObjectLiteral_init$lambda_0);
    this.onImage_ocpcir$_0 = RealMediaDevices$getCamera$ObjectLiteral$onImage$lambda;
  }
  Object.defineProperty(RealMediaDevices$getCamera$ObjectLiteral.prototype, 'videoTrack', {
    get: function () {
      if (this.videoTrack_k1tc6d$_0 == null)
        return throwUPAE('videoTrack');
      return this.videoTrack_k1tc6d$_0;
    },
    set: function (videoTrack) {
      this.videoTrack_k1tc6d$_0 = videoTrack;
    }
  });
  Object.defineProperty(RealMediaDevices$getCamera$ObjectLiteral.prototype, 'onImage', {
    get: function () {
      return this.onImage_ocpcir$_0;
    },
    set: function (onImage) {
      this.onImage_ocpcir$_0 = onImage;
    }
  });
  RealMediaDevices$getCamera$ObjectLiteral.prototype.close = function () {
  };
  function Coroutine$capture($this, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.exceptionState_0 = 1;
    this.$this = $this;
  }
  Coroutine$capture.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$capture.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$capture.prototype.constructor = Coroutine$capture;
  Coroutine$capture.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.$this.onImage(new VideoElementImage(this.$this.videoEl));
            this.state_0 = 2;
            this.result_0 = delay(L50, this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 1:
            throw this.exception_0;
          case 2:
            this.state_0 = 3;
            this.result_0 = this.$this.capture(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 3:
            return;
          default:this.state_0 = 1;
            throw new Error('State Machine Unreachable execution');
        }
      }
       catch (e) {
        if (this.state_0 === 1) {
          this.exceptionState_0 = this.state_0;
          throw e;
        }
         else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  RealMediaDevices$getCamera$ObjectLiteral.prototype.capture = function (continuation_0, suspended) {
    var instance = new Coroutine$capture(this, continuation_0);
    if (suspended)
      return instance;
    else
      return instance.doResume(null);
  };
  function Coroutine$RealMediaDevices$getCamera$RealMediaDevices$getCamera$ObjectLiteral_init$lambda$lambda$lambda(this$_0, $receiver_0, controller, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.$controller = controller;
    this.exceptionState_0 = 1;
    this.local$this$ = this$_0;
  }
  Coroutine$RealMediaDevices$getCamera$RealMediaDevices$getCamera$ObjectLiteral_init$lambda$lambda$lambda.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$RealMediaDevices$getCamera$RealMediaDevices$getCamera$ObjectLiteral_init$lambda$lambda$lambda.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$RealMediaDevices$getCamera$RealMediaDevices$getCamera$ObjectLiteral_init$lambda$lambda$lambda.prototype.constructor = Coroutine$RealMediaDevices$getCamera$RealMediaDevices$getCamera$ObjectLiteral_init$lambda$lambda$lambda;
  Coroutine$RealMediaDevices$getCamera$RealMediaDevices$getCamera$ObjectLiteral_init$lambda$lambda$lambda.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.state_0 = 2;
            this.result_0 = this.local$this$.capture(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 1:
            throw this.exception_0;
          case 2:
            return this.result_0;
          default:this.state_0 = 1;
            throw new Error('State Machine Unreachable execution');
        }
      }
       catch (e) {
        if (this.state_0 === 1) {
          this.exceptionState_0 = this.state_0;
          throw e;
        }
         else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  function RealMediaDevices$getCamera$RealMediaDevices$getCamera$ObjectLiteral_init$lambda$lambda$lambda(this$_0) {
    return function ($receiver_0, continuation_0, suspended) {
      var instance = new Coroutine$RealMediaDevices$getCamera$RealMediaDevices$getCamera$ObjectLiteral_init$lambda$lambda$lambda(this$_0, $receiver_0, this, continuation_0);
      if (suspended)
        return instance;
      else
        return instance.doResume(null);
    };
  }
  function RealMediaDevices$getCamera$RealMediaDevices$getCamera$ObjectLiteral_init$lambda$lambda(this$, this$RealMediaDevices) {
    return function (it) {
      println('oncanplay');
      return launch(this$RealMediaDevices, void 0, void 0, RealMediaDevices$getCamera$RealMediaDevices$getCamera$ObjectLiteral_init$lambda$lambda$lambda(this$));
    };
  }
  function RealMediaDevices$getCamera$RealMediaDevices$getCamera$ObjectLiteral_init$lambda$lambda_0(it) {
    println('onended');
    return Unit;
  }
  function RealMediaDevices$getCamera$RealMediaDevices$getCamera$ObjectLiteral_init$lambda$lambda_1(it) {
    println('onloadeddata');
    return Unit;
  }
  function RealMediaDevices$getCamera$RealMediaDevices$getCamera$ObjectLiteral_init$lambda(this$, this$RealMediaDevices) {
    return function (stream) {
      this$.videoTrack = stream.getVideoTracks()[0];
      this$.videoEl.srcObject = stream;
      this$.videoEl.controls = true;
      this$.videoEl.play();
      this$.videoEl.oncanplay = RealMediaDevices$getCamera$RealMediaDevices$getCamera$ObjectLiteral_init$lambda$lambda(this$, this$RealMediaDevices);
      this$.videoEl.onended = RealMediaDevices$getCamera$RealMediaDevices$getCamera$ObjectLiteral_init$lambda$lambda_0;
      this$.videoEl.onloadeddata = RealMediaDevices$getCamera$RealMediaDevices$getCamera$ObjectLiteral_init$lambda$lambda_1;
      return Unit;
    };
  }
  function RealMediaDevices$getCamera$RealMediaDevices$getCamera$ObjectLiteral_init$lambda_0(t) {
    println('caught ' + t);
    return Unit;
  }
  function RealMediaDevices$getCamera$ObjectLiteral$onImage$lambda(it) {
    return Unit;
  }
  RealMediaDevices$getCamera$ObjectLiteral.$metadata$ = {
    kind: Kind_CLASS,
    interfaces: [MediaDevices$Camera]
  };
  RealMediaDevices.prototype.getCamera = function () {
    return new RealMediaDevices$getCamera$ObjectLiteral(this);
  };
  Object.defineProperty(RealMediaDevices.prototype, 'coroutineContext', {
    get: function () {
      return this.$delegate_ikujlt$_0.coroutineContext;
    }
  });
  RealMediaDevices.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'RealMediaDevices',
    interfaces: [CoroutineScope, MediaDevices]
  };
  function Vector2_0(x, y) {
    Vector2.call(this, x, y);
  }
  Vector2_0.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Vector2',
    interfaces: []
  };
  function GlslBase() {
    GlslBase_instance = this;
    this.manager_cd4dvk$_0 = lazy(GlslBase$manager$lambda);
  }
  Object.defineProperty(GlslBase.prototype, 'manager', {
    get: function () {
      return this.manager_cd4dvk$_0.value;
    }
  });
  function GlslBase$manager$lambda() {
    return new JsGlslManager();
  }
  GlslBase.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'GlslBase',
    interfaces: []
  };
  var GlslBase_instance = null;
  function GlslBase_getInstance() {
    if (GlslBase_instance === null) {
      new GlslBase();
    }
    return GlslBase_instance;
  }
  function JsGlslManager() {
  }
  JsGlslManager.prototype.createRenderer_3kbl32$ = function (program, adjustableValues) {
    return new JsGlslRenderer(program, adjustableValues);
  };
  JsGlslManager.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'JsGlslManager',
    interfaces: [GlslManager]
  };
  function JsGlslRenderer(fragShader, adjustableValues) {
    JsGlslRenderer$Companion_getInstance();
    GlslRenderer.call(this, fragShader, adjustableValues);
    var tmp$, tmp$_0;
    this.canvas = Kotlin.isType(tmp$ = document.createElement('canvas'), HTMLCanvasElement) ? tmp$ : throwCCE();
    this.gl = Kotlin.isType(tmp$_0 = ensureNotNull(this.canvas.getContext('webgl2')), WebGL2RenderingContext) ? tmp$_0 : throwCCE();
    this.program_0 = null;
    this.quad_0 = null;
    this.gl_klfg04$(JsGlslRenderer_init$lambda(this));
    this.program_0 = this.createShaderProgram_0();
    this.quad_0 = new JsGlslRenderer$Quad(this);
    this.findUniforms();
    this.instance = this.createInstance_mfoo9a$(1, new Float32Array(2), this.nextSurfaceOffset);
  }
  function JsGlslRenderer$getUniformLocation$lambda(this$JsGlslRenderer, closure$name) {
    return function () {
      return this$JsGlslRenderer.gl.getUniformLocation(this$JsGlslRenderer.program_0, closure$name);
    };
  }
  JsGlslRenderer.prototype.getUniformLocation_61zpoe$ = function (name) {
    var tmp$;
    tmp$ = this.gl_klfg04$(JsGlslRenderer$getUniformLocation$lambda(this, name));
    if (tmp$ == null) {
      throw IllegalStateException_init("Couldn't find uniform " + name);
    }
    var loc = tmp$;
    return new GlslRenderer$Uniform(loc);
  };
  function JsGlslRenderer$Quad($outer) {
    this.$outer = $outer;
    this.vertices_0 = [1.0, 1.0, -1.0, 1.0, -1.0, -1.0, -1.0, -1.0, 1.0, -1.0, 1.0, 1.0];
    this.quadVertexBuffer_0 = null;
    this.quadVertexBuffer_0 = this.$outer.gl.createBuffer();
    this.$outer.gl_klfg04$(JsGlslRenderer$JsGlslRenderer$Quad_init$lambda(this.$outer, this));
    this.$outer.gl_klfg04$(JsGlslRenderer$JsGlslRenderer$Quad_init$lambda_0(this.$outer, this));
    var vertexAttr = this.$outer.gl_klfg04$(JsGlslRenderer$JsGlslRenderer$Quad_init$lambda_1(this.$outer));
    this.$outer.gl_klfg04$(JsGlslRenderer$JsGlslRenderer$Quad_init$lambda_2(this.$outer, vertexAttr));
    this.$outer.gl_klfg04$(JsGlslRenderer$JsGlslRenderer$Quad_init$lambda_3(this.$outer, vertexAttr));
    this.$outer.gl_klfg04$(JsGlslRenderer$JsGlslRenderer$Quad_init$lambda_4(this.$outer));
  }
  function JsGlslRenderer$Quad$render$lambda(this$JsGlslRenderer, this$Quad) {
    return function () {
      this$JsGlslRenderer.gl.bindBuffer(JsGlslRenderer$Companion_getInstance().GL.ARRAY_BUFFER, this$Quad.quadVertexBuffer_0);
      return Unit;
    };
  }
  function JsGlslRenderer$Quad$render$lambda_0(this$JsGlslRenderer) {
    return function () {
      this$JsGlslRenderer.gl.drawArrays(JsGlslRenderer$Companion_getInstance().GL.TRIANGLES, 0, 6);
      return Unit;
    };
  }
  function JsGlslRenderer$Quad$render$lambda_1(this$JsGlslRenderer) {
    return function () {
      this$JsGlslRenderer.gl.bindBuffer(JsGlslRenderer$Companion_getInstance().GL.ARRAY_BUFFER, null);
      return Unit;
    };
  }
  JsGlslRenderer$Quad.prototype.render_8be2vx$ = function () {
    this.$outer.gl_klfg04$(JsGlslRenderer$Quad$render$lambda(this.$outer, this));
    this.$outer.gl_klfg04$(JsGlslRenderer$Quad$render$lambda_0(this.$outer));
    this.$outer.gl_klfg04$(JsGlslRenderer$Quad$render$lambda_1(this.$outer));
  };
  function JsGlslRenderer$Quad$release$lambda(this$JsGlslRenderer, this$Quad) {
    return function () {
      this$JsGlslRenderer.gl.deleteBuffer(this$Quad.quadVertexBuffer_0);
      return Unit;
    };
  }
  JsGlslRenderer$Quad.prototype.release_0 = function () {
    this.$outer.gl_klfg04$(JsGlslRenderer$Quad$release$lambda(this.$outer, this));
  };
  function JsGlslRenderer$JsGlslRenderer$Quad_init$lambda(this$JsGlslRenderer, this$Quad) {
    return function () {
      this$JsGlslRenderer.gl.bindBuffer(JsGlslRenderer$Companion_getInstance().GL.ARRAY_BUFFER, this$Quad.quadVertexBuffer_0);
      return Unit;
    };
  }
  function JsGlslRenderer$JsGlslRenderer$Quad_init$lambda_0(this$JsGlslRenderer, this$Quad) {
    return function () {
      this$JsGlslRenderer.gl.bufferData(JsGlslRenderer$Companion_getInstance().GL.ARRAY_BUFFER, new Float32Array(this$Quad.vertices_0), JsGlslRenderer$Companion_getInstance().GL.STATIC_DRAW);
      return Unit;
    };
  }
  function JsGlslRenderer$JsGlslRenderer$Quad_init$lambda_1(this$JsGlslRenderer) {
    return function () {
      return this$JsGlslRenderer.gl.getAttribLocation(this$JsGlslRenderer.program_0, 'Vertex');
    };
  }
  function JsGlslRenderer$JsGlslRenderer$Quad_init$lambda_2(this$JsGlslRenderer, closure$vertexAttr) {
    return function () {
      this$JsGlslRenderer.gl.vertexAttribPointer(closure$vertexAttr, 2, JsGlslRenderer$Companion_getInstance().GL.FLOAT, false, 0, 0);
      return Unit;
    };
  }
  function JsGlslRenderer$JsGlslRenderer$Quad_init$lambda_3(this$JsGlslRenderer, closure$vertexAttr) {
    return function () {
      this$JsGlslRenderer.gl.enableVertexAttribArray(closure$vertexAttr);
      return Unit;
    };
  }
  function JsGlslRenderer$JsGlslRenderer$Quad_init$lambda_4(this$JsGlslRenderer) {
    return function () {
      this$JsGlslRenderer.gl.bindBuffer(JsGlslRenderer$Companion_getInstance().GL.ARRAY_BUFFER, null);
      return Unit;
    };
  }
  JsGlslRenderer$Quad.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Quad',
    interfaces: []
  };
  JsGlslRenderer.prototype.createSurfacePixels_x0dork$ = function (surface, pixelOffset) {
    return new JsGlslRenderer$SurfacePixels(this, surface, pixelOffset);
  };
  JsGlslRenderer.prototype.createInstance_mfoo9a$ = function (pixelCount, uvCoords, surfaceCount) {
    return new JsGlslRenderer$Instance(this, pixelCount, uvCoords, surfaceCount);
  };
  function JsGlslRenderer$draw$lambda$lambda(this$JsGlslRenderer) {
    return function () {
      this$JsGlslRenderer.incorporateNewSurfaces();
      return Unit;
    };
  }
  function JsGlslRenderer$draw$lambda$lambda_0(this$JsGlslRenderer) {
    return function () {
      this$JsGlslRenderer.instance.bindFramebuffer();
      return Unit;
    };
  }
  function JsGlslRenderer$draw$lambda$lambda_1(this$JsGlslRenderer) {
    return function () {
      this$JsGlslRenderer.render_0();
      return Unit;
    };
  }
  function JsGlslRenderer$draw$lambda$lambda_2(this$JsGlslRenderer) {
    return function () {
      this$JsGlslRenderer.instance.copyToPixelBuffer();
      return Unit;
    };
  }
  function JsGlslRenderer$draw$lambda$lambda_3(this$JsGlslRenderer) {
    return function () {
      this$JsGlslRenderer.gl.finish();
      return Unit;
    };
  }
  function JsGlslRenderer$draw$lambda(this$JsGlslRenderer) {
    return function () {
      var addSurfacesMs = timeSync(JsGlslRenderer$draw$lambda$lambda(this$JsGlslRenderer));
      var bindFbMs = timeSync(JsGlslRenderer$draw$lambda$lambda_0(this$JsGlslRenderer));
      var renderMs = timeSync(JsGlslRenderer$draw$lambda$lambda_1(this$JsGlslRenderer));
      var readPxMs = timeSync(JsGlslRenderer$draw$lambda$lambda_2(this$JsGlslRenderer));
      this$JsGlslRenderer.gl_klfg04$(JsGlslRenderer$draw$lambda$lambda_3(this$JsGlslRenderer));
      return Unit;
    };
  }
  JsGlslRenderer.prototype.draw = function () {
    this.withGlContext_klfg04$(JsGlslRenderer$draw$lambda(this));
  };
  function JsGlslRenderer$render$lambda(this$JsGlslRenderer) {
    return function () {
      this$JsGlslRenderer.gl.uniform2f(this$JsGlslRenderer.get_location_0(this$JsGlslRenderer.resolutionLocation_8be2vx$), 1.0, 1.0);
      return Unit;
    };
  }
  function JsGlslRenderer$render$lambda_0(this$JsGlslRenderer, closure$thisTime) {
    return function () {
      this$JsGlslRenderer.gl.uniform1f(this$JsGlslRenderer.get_location_0(this$JsGlslRenderer.timeLocation_8be2vx$), closure$thisTime);
      return Unit;
    };
  }
  function JsGlslRenderer$render$lambda_1(this$JsGlslRenderer) {
    return function () {
      this$JsGlslRenderer.gl.viewport(0, 0, this$JsGlslRenderer.get_bufWidth_s8ev3n$(this$JsGlslRenderer.pixelCount), this$JsGlslRenderer.get_bufHeight_s8ev3n$(this$JsGlslRenderer.pixelCount));
      return Unit;
    };
  }
  function JsGlslRenderer$render$lambda_2(this$JsGlslRenderer) {
    return function () {
      this$JsGlslRenderer.gl.clear(JsGlslRenderer$Companion_getInstance().GL.COLOR_BUFFER_BIT | JsGlslRenderer$Companion_getInstance().GL.DEPTH_BUFFER_BIT);
      return Unit;
    };
  }
  function JsGlslRenderer$render$lambda_3(this$JsGlslRenderer) {
    return function () {
      this$JsGlslRenderer.gl.finish();
      return Unit;
    };
  }
  function JsGlslRenderer$render$lambda_4(this$JsGlslRenderer) {
    return function () {
      return this$JsGlslRenderer.gl.getProgramInfoLog(this$JsGlslRenderer.program_0);
    };
  }
  JsGlslRenderer.prototype.render_0 = function () {
    var thisTime = getTimeMillis().and(L134217727).toNumber() / 1000.0;
    this.gl_klfg04$(JsGlslRenderer$render$lambda(this));
    this.gl_klfg04$(JsGlslRenderer$render$lambda_0(this, thisTime));
    this.instance.bindUvCoordTexture_msnecj$(this.uvCoordTextureIndex, ensureNotNull(this.uvCoordsLocation_8be2vx$));
    this.instance.bindUniforms();
    this.gl_klfg04$(JsGlslRenderer$render$lambda_1(this));
    this.gl_klfg04$(JsGlslRenderer$render$lambda_2(this));
    this.quad_0.render_8be2vx$();
    this.gl_klfg04$(JsGlslRenderer$render$lambda_3(this));
    var programLog = this.gl_klfg04$(JsGlslRenderer$render$lambda_4(this));
    var tmp$ = programLog != null;
    if (tmp$) {
      tmp$ = programLog.length > 0;
    }
    if (tmp$)
      println('ProgramInfoLog: ' + toString_0(programLog));
  };
  function JsGlslRenderer$SurfacePixels($outer, surface, pixel0Index) {
    this.$outer = $outer;
    SurfacePixels.call(this, surface, pixel0Index);
  }
  JsGlslRenderer$SurfacePixels.prototype.get_za3lpa$ = function (i) {
    return this.$outer.instance.getPixel_za3lpa$(this.pixel0Index + i | 0);
  };
  JsGlslRenderer$SurfacePixels.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'SurfacePixels',
    interfaces: [SurfacePixels]
  };
  function JsGlslRenderer$createShaderProgram$lambda(this$JsGlslRenderer) {
    return function () {
      return this$JsGlslRenderer.gl.createProgram();
    };
  }
  function JsGlslRenderer$createShaderProgram$lambda_0(this$JsGlslRenderer) {
    return function () {
      return this$JsGlslRenderer.gl.createShader(JsGlslRenderer$Companion_getInstance().GL.VERTEX_SHADER);
    };
  }
  function JsGlslRenderer$createShaderProgram$lambda_1(this$JsGlslRenderer, closure$vs) {
    return function () {
      this$JsGlslRenderer.gl.shaderSource(closure$vs, '#version 300 es\n\nprecision lowp float;\n\n// xy = vertex position in normalized device coordinates ([-1,+1] range).\nin vec2 Vertex;\n\nconst vec2 scale = vec2(0.5, 0.5);\n\nvoid main()\n{\n    vec2 vTexCoords  = Vertex * scale + scale; // scale vertex attribute to [0,1] range\n    gl_Position = vec4(Vertex, 0.0, 1.0);\n}\n');
      return Unit;
    };
  }
  function JsGlslRenderer$createShaderProgram$lambda_2(this$JsGlslRenderer, closure$program, closure$vs) {
    return function () {
      this$JsGlslRenderer.gl.attachShader(closure$program, closure$vs);
      return Unit;
    };
  }
  function JsGlslRenderer$createShaderProgram$lambda_3(this$JsGlslRenderer) {
    return function () {
      return this$JsGlslRenderer.gl.createShader(JsGlslRenderer$Companion_getInstance().GL.FRAGMENT_SHADER);
    };
  }
  function JsGlslRenderer$createShaderProgram$lambda_4(this$JsGlslRenderer, closure$fs, closure$src) {
    return function () {
      this$JsGlslRenderer.gl.shaderSource(closure$fs, closure$src);
      return Unit;
    };
  }
  function JsGlslRenderer$createShaderProgram$lambda_5(this$JsGlslRenderer, closure$program, closure$fs) {
    return function () {
      this$JsGlslRenderer.gl.attachShader(closure$program, closure$fs);
      return Unit;
    };
  }
  function JsGlslRenderer$createShaderProgram$lambda_6(this$JsGlslRenderer, closure$program) {
    return function () {
      this$JsGlslRenderer.gl.linkProgram(closure$program);
      return Unit;
    };
  }
  function JsGlslRenderer$createShaderProgram$lambda_7(this$JsGlslRenderer, closure$program) {
    return function () {
      this$JsGlslRenderer.gl.useProgram(closure$program);
      return Unit;
    };
  }
  JsGlslRenderer.prototype.createShaderProgram_0 = function () {
    var program = this.gl_klfg04$(JsGlslRenderer$createShaderProgram$lambda(this));
    var vs = this.gl_klfg04$(JsGlslRenderer$createShaderProgram$lambda_0(this));
    this.gl_klfg04$(JsGlslRenderer$createShaderProgram$lambda_1(this, vs));
    this.compileShader_0(vs);
    this.gl_klfg04$(JsGlslRenderer$createShaderProgram$lambda_2(this, program, vs));
    var fs = this.gl_klfg04$(JsGlslRenderer$createShaderProgram$lambda_3(this));
    var tmp$ = '#version 300 es' + '\n' + '\n' + '#ifdef GL_ES' + '\n' + 'precision mediump float;' + '\n' + '#endif' + '\n' + '\n' + 'uniform sampler2D sm_uvCoords;' + '\n' + '\n' + 'out vec4 sm_fragColor;' + '\n' + '\n';
    var $receiver = this.fragShader;
    var regex = Regex_init('void main\\s*\\(\\s*void\\s*\\)');
    var replacement = 'void sm_main(vec2 sm_pixelCoord)';
    var src = tmp$ + replace(replace(regex.replace_x2uqeu$($receiver, replacement), 'gl_FragCoord', 'sm_pixelCoord'), 'gl_FragColor', 'sm_fragColor') + '\n' + '\n' + '// Coming in, `gl_FragCoord` is a vec2 where `x` and `y` correspond to positions in `sm_uvCoords`.' + '\n' + '// We look up the `u` and `v` coordinates (which should be floats `[0..1]` in the mapping space) and' + '\n' + "// pass them to the shader's original `main()` method." + '\n' + 'void main(void) {' + '\n' + '    int uvX = int(gl_FragCoord.x);' + '\n' + '    int uvY = int(gl_FragCoord.y);' + '\n' + '    ' + '\n' + '    vec2 pixelCoord = vec2(' + '\n' + '        texelFetch(sm_uvCoords, ivec2(uvX * 2, uvY), 0).r,    // u' + '\n' + '        texelFetch(sm_uvCoords, ivec2(uvX * 2 + 1, uvY), 0).r // v' + '\n' + '    );' + '\n' + '\n' + '    sm_main(pixelCoord);' + '\n' + '}' + '\n';
    println(src);
    this.gl_klfg04$(JsGlslRenderer$createShaderProgram$lambda_4(this, fs, src));
    this.compileShader_0(fs);
    this.gl_klfg04$(JsGlslRenderer$createShaderProgram$lambda_5(this, program, fs));
    this.gl_klfg04$(JsGlslRenderer$createShaderProgram$lambda_6(this, program));
    if (equals(this.gl.getProgramParameter(program, JsGlslRenderer$Companion_getInstance().GL.LINK_STATUS), false)) {
      throw RuntimeException_init('ProgramInfoLog: ' + toString_0(this.gl.getProgramInfoLog(program)));
    }
    this.gl_klfg04$(JsGlslRenderer$createShaderProgram$lambda_7(this, program));
    return program;
  };
  function JsGlslRenderer$compileShader$lambda(this$JsGlslRenderer, closure$shader) {
    return function () {
      this$JsGlslRenderer.gl.compileShader(closure$shader);
      return Unit;
    };
  }
  JsGlslRenderer.prototype.compileShader_0 = function (shader) {
    this.gl_klfg04$(JsGlslRenderer$compileShader$lambda(this, shader));
    if (equals(this.gl.getShaderParameter(shader, JsGlslRenderer$Companion_getInstance().GL.COMPILE_STATUS), false)) {
      window.alert('Failed to compile shader: ' + toString_0(this.gl.getShaderInfoLog(shader)) + '\n' + ('Version: ' + toString_0(this.gl.getParameter(JsGlslRenderer$Companion_getInstance().GL.VERSION)) + '\n') + ('GLSL Version: ' + toString_0(this.gl.getParameter(JsGlslRenderer$Companion_getInstance().GL.SHADING_LANGUAGE_VERSION)) + '\n'));
      throw RuntimeException_init('Failed to compile shader: ' + toString_0(this.gl.getShaderInfoLog(shader)));
    }
  };
  JsGlslRenderer.prototype.gl_klfg04$ = function (fn) {
    var result = fn();
    JsGlslRenderer$Companion_getInstance().checkForGlError_tnk6ih$(this.gl);
    return result;
  };
  JsGlslRenderer.prototype.withGlContext_klfg04$ = function (fn) {
    return fn();
  };
  function JsGlslRenderer$Companion() {
    JsGlslRenderer$Companion_instance = this;
    this.GL = WebGLRenderingContext;
    this.GL2 = WebGL2RenderingContext;
  }
  JsGlslRenderer$Companion.prototype.checkForGlError_tnk6ih$ = function (gl) {
    var tmp$;
    while (true) {
      var error = gl.getError();
      if (error === this.GL.INVALID_ENUM)
        tmp$ = 'GL_INVALID_ENUM';
      else if (error === this.GL.INVALID_VALUE)
        tmp$ = 'GL_INVALID_VALUE';
      else if (error === this.GL.INVALID_OPERATION)
        tmp$ = 'GL_INVALID_OPERATION';
      else if (error === this.GL.INVALID_FRAMEBUFFER_OPERATION)
        tmp$ = 'GL_INVALID_FRAMEBUFFER_OPERATION';
      else if (error === this.GL2.FRAMEBUFFER_INCOMPLETE_ATTACHMENT)
        tmp$ = 'FRAMEBUFFER_INCOMPLETE_ATTACHMENT';
      else if (error === this.GL.CONTEXT_LOST_WEBGL)
        tmp$ = 'GL_CONTEXT_LOST_WEBGL';
      else if (error === this.GL.OUT_OF_MEMORY)
        tmp$ = 'GL_OUT_OF_MEMORY';
      else
        tmp$ = 'unknown error ' + error;
      var code = tmp$;
      if (error !== 0)
        throw RuntimeException_init('OpenGL Error: ' + code);
      else
        return;
    }
  };
  JsGlslRenderer$Companion.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'Companion',
    interfaces: []
  };
  var JsGlslRenderer$Companion_instance = null;
  function JsGlslRenderer$Companion_getInstance() {
    if (JsGlslRenderer$Companion_instance === null) {
      new JsGlslRenderer$Companion();
    }
    return JsGlslRenderer$Companion_instance;
  }
  function JsGlslRenderer$UnifyingAdjustableUniform($outer, adjustableValue, surfaceCount) {
    this.$outer = $outer;
    this.adjustableValue = adjustableValue;
    this.surfaceCount = surfaceCount;
    this.uniformLocation = this.$outer.gl_klfg04$(JsGlslRenderer$UnifyingAdjustableUniform$uniformLocation$lambda(this, this.$outer));
    this.buffer = null;
  }
  function JsGlslRenderer$UnifyingAdjustableUniform$bind$lambda(this$JsGlslRenderer, closure$location, this$UnifyingAdjustableUniform) {
    return function () {
      var tmp$;
      this$JsGlslRenderer.gl.uniform1i(closure$location, typeof (tmp$ = this$UnifyingAdjustableUniform.buffer) === 'number' ? tmp$ : throwCCE());
      return Unit;
    };
  }
  function JsGlslRenderer$UnifyingAdjustableUniform$bind$lambda_0(this$JsGlslRenderer, closure$location, this$UnifyingAdjustableUniform) {
    return function () {
      var tmp$;
      this$JsGlslRenderer.gl.uniform1f(closure$location, typeof (tmp$ = this$UnifyingAdjustableUniform.buffer) === 'number' ? tmp$ : throwCCE());
      return Unit;
    };
  }
  function JsGlslRenderer$UnifyingAdjustableUniform$bind$lambda_1(this$JsGlslRenderer, closure$location, closure$color) {
    return function () {
      this$JsGlslRenderer.gl.uniform3f(closure$location, closure$color.redF, closure$color.greenF, closure$color.blueF);
      return Unit;
    };
  }
  JsGlslRenderer$UnifyingAdjustableUniform.prototype.bind = function () {
    var tmp$;
    if (this.buffer != null) {
      var location = this.$outer.get_location_0(this.uniformLocation);
      switch (this.adjustableValue.valueType.name) {
        case 'INT':
          this.$outer.gl_klfg04$(JsGlslRenderer$UnifyingAdjustableUniform$bind$lambda(this.$outer, location, this));
          break;
        case 'FLOAT':
          this.$outer.gl_klfg04$(JsGlslRenderer$UnifyingAdjustableUniform$bind$lambda_0(this.$outer, location, this));
          break;
        case 'VEC3':
          var color = Kotlin.isType(tmp$ = this.buffer, Color) ? tmp$ : throwCCE();
          this.$outer.gl_klfg04$(JsGlslRenderer$UnifyingAdjustableUniform$bind$lambda_1(this.$outer, location, color));
          break;
      }
    }
  };
  JsGlslRenderer$UnifyingAdjustableUniform.prototype.setValue_6t2rgq$ = function (surfaceOrdinal, value) {
    this.buffer = value;
  };
  function JsGlslRenderer$UnifyingAdjustableUniform$uniformLocation$lambda(this$UnifyingAdjustableUniform, this$JsGlslRenderer) {
    return function () {
      return this$JsGlslRenderer.getUniformLocation_61zpoe$(this$UnifyingAdjustableUniform.adjustableValue.varName);
    };
  }
  JsGlslRenderer$UnifyingAdjustableUniform.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'UnifyingAdjustableUniform',
    interfaces: [GlslRenderer$AdjustibleUniform]
  };
  function JsGlslRenderer$AwesomerAdjustableUniform($outer, adjustableValue, surfaceCount) {
    this.$outer = $outer;
    this.adjustableValue = adjustableValue;
    this.surfaceCount = surfaceCount;
    var tmp$;
    switch (this.adjustableValue.valueType.name) {
      case 'INT':
        tmp$ = new Uint32Array(this.elementCount);
        break;
      case 'FLOAT':
        tmp$ = new Float32Array(this.elementCount);
        break;
      case 'VEC3':
        tmp$ = new Float32Array(this.elementCount);
        break;
      default:tmp$ = Kotlin.noWhenBranchMatched();
        break;
    }
    this.buffer = tmp$;
    this.texture = this.$outer.gl_klfg04$(JsGlslRenderer$AwesomerAdjustableUniform$texture$lambda(this.$outer));
    this.textureIndex = this.$outer.adjustableValueUniformIndices.get_za3lpa$(this.adjustableValue.ordinal);
    this.uniformLocation = this.$outer.gl_klfg04$(JsGlslRenderer$AwesomerAdjustableUniform$uniformLocation$lambda(this.$outer, this));
    this.$outer.gl_klfg04$(JsGlslRenderer$JsGlslRenderer$AwesomerAdjustableUniform_init$lambda(this.$outer, this));
    this.$outer.gl_klfg04$(JsGlslRenderer$JsGlslRenderer$AwesomerAdjustableUniform_init$lambda_0(this.$outer, this));
    this.$outer.gl_klfg04$(JsGlslRenderer$JsGlslRenderer$AwesomerAdjustableUniform_init$lambda_1(this.$outer));
    this.$outer.gl_klfg04$(JsGlslRenderer$JsGlslRenderer$AwesomerAdjustableUniform_init$lambda_2(this.$outer));
    this.$outer.gl_klfg04$(JsGlslRenderer$JsGlslRenderer$AwesomerAdjustableUniform_init$lambda_3(this.$outer, this));
    this.$outer.gl_klfg04$(JsGlslRenderer$JsGlslRenderer$AwesomerAdjustableUniform_init$lambda_4(this.$outer, this));
  }
  Object.defineProperty(JsGlslRenderer$AwesomerAdjustableUniform.prototype, 'elementCount', {
    get: function () {
      switch (this.adjustableValue.valueType.name) {
        case 'INT':
          return this.surfaceCount;
        case 'FLOAT':
          return this.surfaceCount;
        case 'VEC3':
          return this.surfaceCount * 3 | 0;
        default:return Kotlin.noWhenBranchMatched();
      }
    }
  });
  Object.defineProperty(JsGlslRenderer$AwesomerAdjustableUniform.prototype, 'internalFormat', {
    get: function () {
      switch (this.adjustableValue.valueType.name) {
        case 'INT':
          return JsGlslRenderer$Companion_getInstance().GL.INT;
        case 'FLOAT':
          return JsGlslRenderer$Companion_getInstance().GL2.R32F;
        case 'VEC3':
          return JsGlslRenderer$Companion_getInstance().GL.RGB;
        default:return Kotlin.noWhenBranchMatched();
      }
    }
  });
  function JsGlslRenderer$AwesomerAdjustableUniform$texture$lambda(this$JsGlslRenderer) {
    return function () {
      return this$JsGlslRenderer.gl.createTexture();
    };
  }
  function JsGlslRenderer$AwesomerAdjustableUniform$uniformLocation$lambda(this$JsGlslRenderer, this$AwesomerAdjustableUniform) {
    return function () {
      return this$JsGlslRenderer.gl.getUniformLocation(this$JsGlslRenderer.program_0, this$AwesomerAdjustableUniform.adjustableValue.varName);
    };
  }
  function JsGlslRenderer$JsGlslRenderer$AwesomerAdjustableUniform_init$lambda(this$JsGlslRenderer, this$AwesomerAdjustableUniform) {
    return function () {
      this$JsGlslRenderer.gl.activeTexture(JsGlslRenderer$Companion_getInstance().GL.TEXTURE0 + this$AwesomerAdjustableUniform.textureIndex | 0);
      return Unit;
    };
  }
  function JsGlslRenderer$JsGlslRenderer$AwesomerAdjustableUniform_init$lambda_0(this$JsGlslRenderer, this$AwesomerAdjustableUniform) {
    return function () {
      this$JsGlslRenderer.gl.bindTexture(JsGlslRenderer$Companion_getInstance().GL.TEXTURE_2D, this$AwesomerAdjustableUniform.texture);
      return Unit;
    };
  }
  function JsGlslRenderer$JsGlslRenderer$AwesomerAdjustableUniform_init$lambda_1(this$JsGlslRenderer) {
    return function () {
      this$JsGlslRenderer.gl.texParameteri(JsGlslRenderer$Companion_getInstance().GL.TEXTURE_2D, JsGlslRenderer$Companion_getInstance().GL.TEXTURE_MIN_FILTER, JsGlslRenderer$Companion_getInstance().GL.NEAREST);
      return Unit;
    };
  }
  function JsGlslRenderer$JsGlslRenderer$AwesomerAdjustableUniform_init$lambda_2(this$JsGlslRenderer) {
    return function () {
      this$JsGlslRenderer.gl.texParameteri(JsGlslRenderer$Companion_getInstance().GL.TEXTURE_2D, JsGlslRenderer$Companion_getInstance().GL.TEXTURE_MAG_FILTER, JsGlslRenderer$Companion_getInstance().GL.NEAREST);
      return Unit;
    };
  }
  function JsGlslRenderer$JsGlslRenderer$AwesomerAdjustableUniform_init$lambda_3(this$JsGlslRenderer, this$AwesomerAdjustableUniform) {
    return function () {
      this$JsGlslRenderer.gl.texImage2D(JsGlslRenderer$Companion_getInstance().GL.TEXTURE_2D, 0, JsGlslRenderer$Companion_getInstance().GL2.R32F, this$AwesomerAdjustableUniform.elementCount, 1, 0, JsGlslRenderer$Companion_getInstance().GL2.RED, JsGlslRenderer$Companion_getInstance().GL.FLOAT, null);
      return Unit;
    };
  }
  function JsGlslRenderer$JsGlslRenderer$AwesomerAdjustableUniform_init$lambda_4(this$JsGlslRenderer, this$AwesomerAdjustableUniform) {
    return function () {
      this$JsGlslRenderer.gl.uniform1i(this$JsGlslRenderer.get_location_0(this$JsGlslRenderer.uvCoordsLocation_8be2vx$), this$AwesomerAdjustableUniform.textureIndex);
      return Unit;
    };
  }
  JsGlslRenderer$AwesomerAdjustableUniform.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'AwesomerAdjustableUniform',
    interfaces: []
  };
  function JsGlslRenderer$Instance($outer, pixelCount, uvCoords, surfaceCount) {
    this.$outer = $outer;
    GlslRenderer$Instance.call(this, this.$outer, pixelCount, uvCoords, surfaceCount);
    var $receiver = this.$outer.adjustableValues;
    this.$outer;
    var destination = ArrayList_init_1(collectionSizeOrDefault($receiver, 10));
    var tmp$;
    tmp$ = $receiver.iterator();
    while (tmp$.hasNext()) {
      var item = tmp$.next();
      destination.add_11rb$(new JsGlslRenderer$UnifyingAdjustableUniform(this.$outer, item, surfaceCount));
    }
    this.adjustableUniforms_edok6n$_0 = destination;
    this.uvCoordTexture_0 = this.$outer.gl_klfg04$(JsGlslRenderer$Instance$uvCoordTexture$lambda(this.$outer));
    this.frameBuffer_0 = this.$outer.gl_klfg04$(JsGlslRenderer$Instance$frameBuffer$lambda(this.$outer));
    this.renderBuffer_0 = this.$outer.gl_klfg04$(JsGlslRenderer$Instance$renderBuffer$lambda(this.$outer));
    this.pixelBuffer = new Uint8Array(this.$outer.get_bufSize_s8ev3n$(pixelCount) * 4 | 0);
    this.uvCoordsFloat32_0 = new Float32Array(toTypedArray_0(uvCoords));
  }
  Object.defineProperty(JsGlslRenderer$Instance.prototype, 'adjustableUniforms', {
    get: function () {
      return this.adjustableUniforms_edok6n$_0;
    }
  });
  function JsGlslRenderer$Instance$bindFramebuffer$lambda(this$JsGlslRenderer, this$Instance) {
    return function () {
      this$JsGlslRenderer.gl.bindFramebuffer(JsGlslRenderer$Companion_getInstance().GL.FRAMEBUFFER, this$Instance.frameBuffer_0);
      return Unit;
    };
  }
  function JsGlslRenderer$Instance$bindFramebuffer$lambda_0(this$JsGlslRenderer, this$Instance) {
    return function () {
      this$JsGlslRenderer.gl.bindRenderbuffer(JsGlslRenderer$Companion_getInstance().GL.RENDERBUFFER, this$Instance.renderBuffer_0);
      return Unit;
    };
  }
  function JsGlslRenderer$Instance$bindFramebuffer$lambda_1(this$JsGlslRenderer, this$Instance) {
    return function () {
      this$JsGlslRenderer.gl.renderbufferStorage(JsGlslRenderer$Companion_getInstance().GL.RENDERBUFFER, JsGlslRenderer$Companion_getInstance().GL.RGBA4, this$JsGlslRenderer.get_bufWidth_s8ev3n$(this$Instance.pixelCount), this$JsGlslRenderer.get_bufHeight_s8ev3n$(this$Instance.pixelCount));
      return Unit;
    };
  }
  function JsGlslRenderer$Instance$bindFramebuffer$lambda_2(this$JsGlslRenderer, this$Instance) {
    return function () {
      this$JsGlslRenderer.gl.framebufferRenderbuffer(JsGlslRenderer$Companion_getInstance().GL.FRAMEBUFFER, JsGlslRenderer$Companion_getInstance().GL.COLOR_ATTACHMENT0, JsGlslRenderer$Companion_getInstance().GL.RENDERBUFFER, this$Instance.renderBuffer_0);
      return Unit;
    };
  }
  function JsGlslRenderer$Instance$bindFramebuffer$lambda_3(this$JsGlslRenderer) {
    return function () {
      return this$JsGlslRenderer.gl.checkFramebufferStatus(JsGlslRenderer$Companion_getInstance().GL.FRAMEBUFFER);
    };
  }
  JsGlslRenderer$Instance.prototype.bindFramebuffer = function () {
    this.$outer.gl_klfg04$(JsGlslRenderer$Instance$bindFramebuffer$lambda(this.$outer, this));
    this.$outer.gl_klfg04$(JsGlslRenderer$Instance$bindFramebuffer$lambda_0(this.$outer, this));
    this.$outer.gl_klfg04$(JsGlslRenderer$Instance$bindFramebuffer$lambda_1(this.$outer, this));
    this.$outer.gl_klfg04$(JsGlslRenderer$Instance$bindFramebuffer$lambda_2(this.$outer, this));
    var status = this.$outer.gl_klfg04$(JsGlslRenderer$Instance$bindFramebuffer$lambda_3(this.$outer));
    if (status !== JsGlslRenderer$Companion_getInstance().GL.FRAMEBUFFER_COMPLETE) {
      throw RuntimeException_init('FrameBuffer huh? ' + status);
    }
  };
  function JsGlslRenderer$Instance$bindUvCoordTexture$lambda(this$JsGlslRenderer, closure$textureIndex) {
    return function () {
      this$JsGlslRenderer.gl.activeTexture(JsGlslRenderer$Companion_getInstance().GL.TEXTURE0 + closure$textureIndex | 0);
      return Unit;
    };
  }
  function JsGlslRenderer$Instance$bindUvCoordTexture$lambda_0(this$JsGlslRenderer, this$Instance) {
    return function () {
      this$JsGlslRenderer.gl.bindTexture(JsGlslRenderer$Companion_getInstance().GL.TEXTURE_2D, this$Instance.uvCoordTexture_0);
      return Unit;
    };
  }
  function JsGlslRenderer$Instance$bindUvCoordTexture$lambda_1(this$JsGlslRenderer) {
    return function () {
      this$JsGlslRenderer.gl.texParameteri(JsGlslRenderer$Companion_getInstance().GL.TEXTURE_2D, JsGlslRenderer$Companion_getInstance().GL.TEXTURE_MIN_FILTER, JsGlslRenderer$Companion_getInstance().GL.NEAREST);
      return Unit;
    };
  }
  function JsGlslRenderer$Instance$bindUvCoordTexture$lambda_2(this$JsGlslRenderer) {
    return function () {
      this$JsGlslRenderer.gl.texParameteri(JsGlslRenderer$Companion_getInstance().GL.TEXTURE_2D, JsGlslRenderer$Companion_getInstance().GL.TEXTURE_MAG_FILTER, JsGlslRenderer$Companion_getInstance().GL.NEAREST);
      return Unit;
    };
  }
  function JsGlslRenderer$Instance$bindUvCoordTexture$lambda_3(this$JsGlslRenderer, this$Instance) {
    return function () {
      this$JsGlslRenderer.gl.texImage2D(JsGlslRenderer$Companion_getInstance().GL.TEXTURE_2D, 0, JsGlslRenderer$Companion_getInstance().GL2.R32F, this$JsGlslRenderer.get_bufWidth_s8ev3n$(this$Instance.pixelCount) * 2 | 0, this$JsGlslRenderer.get_bufHeight_s8ev3n$(this$Instance.pixelCount), 0, JsGlslRenderer$Companion_getInstance().GL2.RED, JsGlslRenderer$Companion_getInstance().GL.FLOAT, this$Instance.uvCoordsFloat32_0);
      return Unit;
    };
  }
  function JsGlslRenderer$Instance$bindUvCoordTexture$lambda_4(this$JsGlslRenderer, closure$uvCoordsLocation, closure$textureIndex) {
    return function () {
      this$JsGlslRenderer.gl.uniform1i(this$JsGlslRenderer.get_location_0(closure$uvCoordsLocation), closure$textureIndex);
      return Unit;
    };
  }
  JsGlslRenderer$Instance.prototype.bindUvCoordTexture_msnecj$ = function (textureIndex, uvCoordsLocation) {
    this.$outer.gl_klfg04$(JsGlslRenderer$Instance$bindUvCoordTexture$lambda(this.$outer, textureIndex));
    this.$outer.gl_klfg04$(JsGlslRenderer$Instance$bindUvCoordTexture$lambda_0(this.$outer, this));
    this.$outer.gl_klfg04$(JsGlslRenderer$Instance$bindUvCoordTexture$lambda_1(this.$outer));
    this.$outer.gl_klfg04$(JsGlslRenderer$Instance$bindUvCoordTexture$lambda_2(this.$outer));
    this.$outer.gl_klfg04$(JsGlslRenderer$Instance$bindUvCoordTexture$lambda_3(this.$outer, this));
    this.$outer.gl_klfg04$(JsGlslRenderer$Instance$bindUvCoordTexture$lambda_4(this.$outer, uvCoordsLocation, textureIndex));
  };
  JsGlslRenderer$Instance.prototype.getPixel_za3lpa$ = function (pixelIndex) {
    var offset = pixelIndex * 4 | 0;
    return Color_init_2(this.pixelBuffer[offset], this.pixelBuffer[offset + 1 | 0], this.pixelBuffer[offset + 2 | 0], this.pixelBuffer[offset + 3 | 0]);
  };
  function JsGlslRenderer$Instance$copyToPixelBuffer$lambda(this$JsGlslRenderer, this$Instance) {
    return function () {
      this$JsGlslRenderer.gl.readPixels(0, 0, this$JsGlslRenderer.get_bufWidth_s8ev3n$(this$Instance.pixelCount), this$JsGlslRenderer.get_bufHeight_s8ev3n$(this$Instance.pixelCount), JsGlslRenderer$Companion_getInstance().GL.RGBA, JsGlslRenderer$Companion_getInstance().GL.UNSIGNED_BYTE, this$Instance.pixelBuffer);
      return Unit;
    };
  }
  JsGlslRenderer$Instance.prototype.copyToPixelBuffer = function () {
    this.$outer.gl_klfg04$(JsGlslRenderer$Instance$copyToPixelBuffer$lambda(this.$outer, this));
  };
  function JsGlslRenderer$Instance$release$lambda(this$JsGlslRenderer) {
    return function () {
      this$JsGlslRenderer.gl.bindRenderbuffer(JsGlslRenderer$Companion_getInstance().GL.RENDERBUFFER, null);
      return Unit;
    };
  }
  function JsGlslRenderer$Instance$release$lambda_0(this$JsGlslRenderer) {
    return function () {
      this$JsGlslRenderer.gl.bindFramebuffer(JsGlslRenderer$Companion_getInstance().GL.FRAMEBUFFER, null);
      return Unit;
    };
  }
  function JsGlslRenderer$Instance$release$lambda_1(this$JsGlslRenderer) {
    return function () {
      this$JsGlslRenderer.gl.bindTexture(JsGlslRenderer$Companion_getInstance().GL.TEXTURE_2D, null);
      return Unit;
    };
  }
  function JsGlslRenderer$Instance$release$lambda_2(this$JsGlslRenderer, this$Instance) {
    return function () {
      this$JsGlslRenderer.gl.deleteFramebuffer(this$Instance.frameBuffer_0);
      return Unit;
    };
  }
  function JsGlslRenderer$Instance$release$lambda_3(this$JsGlslRenderer, this$Instance) {
    return function () {
      this$JsGlslRenderer.gl.deleteRenderbuffer(this$Instance.renderBuffer_0);
      return Unit;
    };
  }
  function JsGlslRenderer$Instance$release$lambda_4(this$JsGlslRenderer, this$Instance) {
    return function () {
      this$JsGlslRenderer.gl.deleteTexture(this$Instance.uvCoordTexture_0);
      return Unit;
    };
  }
  JsGlslRenderer$Instance.prototype.release = function () {
    println('Release ' + this + ' with ' + this.pixelCount + ' pixels and ' + this.uvCoords.length + ' uvs');
    this.$outer.gl_klfg04$(JsGlslRenderer$Instance$release$lambda(this.$outer));
    this.$outer.gl_klfg04$(JsGlslRenderer$Instance$release$lambda_0(this.$outer));
    this.$outer.gl_klfg04$(JsGlslRenderer$Instance$release$lambda_1(this.$outer));
    this.$outer.gl_klfg04$(JsGlslRenderer$Instance$release$lambda_2(this.$outer, this));
    this.$outer.gl_klfg04$(JsGlslRenderer$Instance$release$lambda_3(this.$outer, this));
    this.$outer.gl_klfg04$(JsGlslRenderer$Instance$release$lambda_4(this.$outer, this));
  };
  function JsGlslRenderer$Instance$uvCoordTexture$lambda(this$JsGlslRenderer) {
    return function () {
      return this$JsGlslRenderer.gl.createTexture();
    };
  }
  function JsGlslRenderer$Instance$frameBuffer$lambda(this$JsGlslRenderer) {
    return function () {
      return this$JsGlslRenderer.gl.createFramebuffer();
    };
  }
  function JsGlslRenderer$Instance$renderBuffer$lambda(this$JsGlslRenderer) {
    return function () {
      return this$JsGlslRenderer.gl.createRenderbuffer();
    };
  }
  JsGlslRenderer$Instance.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Instance',
    interfaces: [GlslRenderer$Instance]
  };
  JsGlslRenderer.prototype.get_location_0 = function ($receiver) {
    var tmp$;
    return (tmp$ = $receiver != null ? $receiver.locationInternal : null) == null || Kotlin.isType(tmp$, WebGLUniformLocation) ? tmp$ : throwCCE();
  };
  function JsGlslRenderer_init$lambda(this$JsGlslRenderer) {
    return function () {
      this$JsGlslRenderer.gl.clearColor(0.0, 0.5, 0.0, 1.0);
      return Unit;
    };
  }
  JsGlslRenderer.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'JsGlslRenderer',
    interfaces: [GlslRenderer]
  };
  function NativeBitmap(width, height) {
    CanvasBitmap.call(this, createCanvas(width, height));
    this.width_geohfm$_0 = width;
    this.height_2j0r6t$_0 = height;
  }
  Object.defineProperty(NativeBitmap.prototype, 'width', {
    get: function () {
      return this.width_geohfm$_0;
    }
  });
  Object.defineProperty(NativeBitmap.prototype, 'height', {
    get: function () {
      return this.height_2j0r6t$_0;
    }
  });
  NativeBitmap.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'NativeBitmap',
    interfaces: [CanvasBitmap, Bitmap]
  };
  function createCanvas$lambda(closure$width, closure$height) {
    return function ($receiver) {
      $receiver.width = closure$width.toString() + 'px';
      $receiver.height = closure$height.toString() + 'px';
      return Unit;
    };
  }
  function createCanvas(width, height) {
    return canvas_0(get_create(document), void 0, createCanvas$lambda(width, height));
  }
  function CanvasBitmap(canvas) {
    this.canvas_8be2vx$ = canvas;
    this.width_4c4jfj$_0 = this.canvas_8be2vx$.width;
    this.height_19yhui$_0 = this.canvas_8be2vx$.height;
    this.ctx_8be2vx$ = context2d(this.canvas_8be2vx$);
  }
  Object.defineProperty(CanvasBitmap.prototype, 'width', {
    get: function () {
      return this.width_4c4jfj$_0;
    }
  });
  Object.defineProperty(CanvasBitmap.prototype, 'height', {
    get: function () {
      return this.height_19yhui$_0;
    }
  });
  CanvasBitmap.prototype.drawImage_6tj0gx$ = function (image) {
    var tmp$;
    (Kotlin.isType(tmp$ = image, JsImage) ? tmp$ : throwCCE()).draw_as725m$(this.ctx_8be2vx$, 0, 0);
  };
  CanvasBitmap.prototype.drawImage_daf0v5$ = function (image, sX, sY, sWidth, sHeight, dX, dY, dWidth, dHeight) {
    var tmp$;
    (Kotlin.isType(tmp$ = image, JsImage) ? tmp$ : throwCCE()).draw_wveyom$(this.ctx_8be2vx$, sX, sY, sWidth, sHeight, dX, dY, dWidth, dHeight);
  };
  CanvasBitmap.prototype.copyFrom_5151av$ = function (other) {
    var tmp$;
    this.assertSameSizeAs_ffnq1x$_0(other);
    this.ctx_8be2vx$.resetTransform();
    this.ctx_8be2vx$.globalCompositeOperation = 'source-over';
    this.ctx_8be2vx$.drawImage((Kotlin.isType(tmp$ = other, CanvasBitmap) ? tmp$ : throwCCE()).canvas_8be2vx$, 0.0, 0.0);
    this.ctx_8be2vx$.resetTransform();
  };
  CanvasBitmap.prototype.apply_hkodsy$_0 = function (other, operation) {
    var tmp$;
    Kotlin.isType(tmp$ = other, CanvasBitmap) ? tmp$ : throwCCE();
    this.assertSameSizeAs_ffnq1x$_0(other);
    this.ctx_8be2vx$.resetTransform();
    this.ctx_8be2vx$.globalCompositeOperation = operation;
    this.ctx_8be2vx$.drawImage(other.canvas_8be2vx$, 0.0, 0.0);
    this.ctx_8be2vx$.resetTransform();
  };
  CanvasBitmap.prototype.lighten_5151av$ = function (other) {
    this.apply_hkodsy$_0(other, 'lighten');
  };
  CanvasBitmap.prototype.darken_5151av$ = function (other) {
    this.apply_hkodsy$_0(other, 'darken');
  };
  CanvasBitmap.prototype.subtract_5151av$ = function (other) {
    this.apply_hkodsy$_0(other, 'difference');
  };
  CanvasBitmap.prototype.toDataUrl = function () {
    return this.canvas_8be2vx$.toDataURL('image/webp');
  };
  CanvasBitmap.prototype.withData_u0v8ny$$default = function (region, fn) {
    var x = region.x0;
    var y = region.y0;
    var width = region.width;
    var height = region.height;
    var imageData = this.ctx_8be2vx$.getImageData(x, y, width, height);
    if (fn(new JsUByteClampedArray(imageData.data))) {
      this.ctx_8be2vx$.putImageData(imageData, x, y, x, y, width, height);
    }
  };
  function CanvasBitmap$asImage$ObjectLiteral(this$CanvasBitmap) {
    this.this$CanvasBitmap = this$CanvasBitmap;
    JsImage.call(this);
    this.width_8z8elj$_0 = this$CanvasBitmap.width;
    this.height_eohisg$_0 = this$CanvasBitmap.height;
  }
  Object.defineProperty(CanvasBitmap$asImage$ObjectLiteral.prototype, 'width', {
    get: function () {
      return this.width_8z8elj$_0;
    }
  });
  Object.defineProperty(CanvasBitmap$asImage$ObjectLiteral.prototype, 'height', {
    get: function () {
      return this.height_eohisg$_0;
    }
  });
  CanvasBitmap$asImage$ObjectLiteral.prototype.toBitmap = function () {
    return this.this$CanvasBitmap;
  };
  CanvasBitmap$asImage$ObjectLiteral.prototype.draw_as725m$ = function (ctx, x, y) {
    ctx.drawImage(this.this$CanvasBitmap.canvas_8be2vx$, 0.0, 0.0);
  };
  CanvasBitmap$asImage$ObjectLiteral.prototype.draw_wveyom$ = function (ctx, sX, sY, sWidth, sHeight, dX, dY, dWidth, dHeight) {
    ctx.drawImage(this.this$CanvasBitmap.canvas_8be2vx$, sX, sY, sWidth, sHeight, dX, dY, dWidth, dHeight);
  };
  CanvasBitmap$asImage$ObjectLiteral.$metadata$ = {
    kind: Kind_CLASS,
    interfaces: [JsImage]
  };
  CanvasBitmap.prototype.asImage = function () {
    return new CanvasBitmap$asImage$ObjectLiteral(this);
  };
  CanvasBitmap.prototype.clone = function () {
    var tmp$, tmp$_0;
    var newCanvas = Kotlin.isType(tmp$ = document.createElement('canvas'), HTMLCanvasElement) ? tmp$ : throwCCE();
    newCanvas.width = this.canvas_8be2vx$.width;
    newCanvas.height = this.canvas_8be2vx$.height;
    var ctx = Kotlin.isType(tmp$_0 = newCanvas.getContext('2d'), CanvasRenderingContext2D) ? tmp$_0 : throwCCE();
    ctx.drawImage(this.canvas_8be2vx$, 0.0, 0.0);
    return new CanvasBitmap(newCanvas);
  };
  CanvasBitmap.prototype.assertSameSizeAs_ffnq1x$_0 = function (other) {
    if (this.width !== other.width || this.height !== other.height) {
      throw IllegalArgumentException_init('other bitmap is not the same size' + (' (' + this.width + 'x' + this.height + ' != ' + other.width + 'x' + other.height + ')'));
    }
  };
  CanvasBitmap.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'CanvasBitmap',
    interfaces: [Bitmap]
  };
  function JsImage() {
  }
  JsImage.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'JsImage',
    interfaces: [Image]
  };
  function ImageBitmapImage(imageBitmap) {
    JsImage.call(this);
    this.imageBitmap_0 = imageBitmap;
    this.width_vq1fd5$_0 = this.imageBitmap_0.width;
    this.height_242nww$_0 = this.imageBitmap_0.height;
  }
  Object.defineProperty(ImageBitmapImage.prototype, 'width', {
    get: function () {
      return this.width_vq1fd5$_0;
    }
  });
  Object.defineProperty(ImageBitmapImage.prototype, 'height', {
    get: function () {
      return this.height_242nww$_0;
    }
  });
  ImageBitmapImage.prototype.toBitmap = function () {
    var bitmap = new NativeBitmap(this.imageBitmap_0.width, this.imageBitmap_0.height);
    bitmap.drawImage_6tj0gx$(this);
    return bitmap;
  };
  ImageBitmapImage.prototype.draw_as725m$ = function (ctx, x, y) {
    ctx.drawImage(this.imageBitmap_0, 0.0, 0.0);
  };
  ImageBitmapImage.prototype.draw_wveyom$ = function (ctx, sX, sY, sWidth, sHeight, dX, dY, dWidth, dHeight) {
    ctx.drawImage(this.imageBitmap_0, sX, sY, sWidth, sHeight, dX, dY, dWidth, dHeight);
  };
  ImageBitmapImage.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'ImageBitmapImage',
    interfaces: [JsImage]
  };
  function VideoElementImage(videoEl) {
    JsImage.call(this);
    this.videoEl_0 = videoEl;
  }
  Object.defineProperty(VideoElementImage.prototype, 'width', {
    get: function () {
      return this.videoEl_0.videoWidth;
    }
  });
  Object.defineProperty(VideoElementImage.prototype, 'height', {
    get: function () {
      return this.videoEl_0.videoHeight;
    }
  });
  VideoElementImage.prototype.toBitmap = function () {
    var bitmap = new NativeBitmap(this.videoEl_0.videoWidth, this.videoEl_0.videoHeight);
    bitmap.drawImage_6tj0gx$(this);
    return bitmap;
  };
  VideoElementImage.prototype.draw_as725m$ = function (ctx, x, y) {
    ctx.drawImage(this.videoEl_0, 0.0, 0.0);
  };
  VideoElementImage.prototype.draw_wveyom$ = function (ctx, sX, sY, sWidth, sHeight, dX, dY, dWidth, dHeight) {
    ctx.drawImage(this.videoEl_0, sX, sY, sWidth, sHeight, dX, dY, dWidth, dHeight);
  };
  VideoElementImage.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'VideoElementImage',
    interfaces: [JsImage]
  };
  function JsUByteClampedArray(delegate) {
    this.delegate = delegate;
  }
  Object.defineProperty(JsUByteClampedArray.prototype, 'size', {
    get: function () {
      return this.delegate.length;
    }
  });
  JsUByteClampedArray.prototype.get_za3lpa$ = function (index) {
    return this.delegate[index];
  };
  JsUByteClampedArray.prototype.set_2c6cbe$ = function (index, value) {
    this.delegate[index] = value;
  };
  JsUByteClampedArray.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'JsUByteClampedArray',
    interfaces: [UByteClampedArray]
  };
  function Coroutine$doRunBlocking$lambda(closure$block_0, $receiver_0, controller, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.$controller = controller;
    this.exceptionState_0 = 1;
    this.local$closure$block = closure$block_0;
  }
  Coroutine$doRunBlocking$lambda.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$doRunBlocking$lambda.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$doRunBlocking$lambda.prototype.constructor = Coroutine$doRunBlocking$lambda;
  Coroutine$doRunBlocking$lambda.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.state_0 = 2;
            this.result_0 = this.local$closure$block(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 1:
            throw this.exception_0;
          case 2:
            return this.result_0;
          default:this.state_0 = 1;
            throw new Error('State Machine Unreachable execution');
        }
      }
       catch (e) {
        if (this.state_0 === 1) {
          this.exceptionState_0 = this.state_0;
          throw e;
        }
         else {
          this.state_0 = this.exceptionState_0;
          this.exception_0 = e;
        }
      }
     while (true);
  };
  function doRunBlocking$lambda(closure$block_0) {
    return function ($receiver_0, continuation_0, suspended) {
      var instance = new Coroutine$doRunBlocking$lambda(closure$block_0, $receiver_0, this, continuation_0);
      if (suspended)
        return instance;
      else
        return instance.doResume(null);
    };
  }
  function doRunBlocking(block) {
    promise(coroutines.GlobalScope, void 0, void 0, doRunBlocking$lambda(block));
    return;
  }
  function getResource(name) {
    var xhr = new XMLHttpRequest();
    xhr.open('GET', name, false);
    xhr.send();
    if (equals(xhr.status, 200)) {
      return xhr.responseText;
    }
    throw Exception_init('failed to load resource ' + name + ': ' + xhr.status + ' ' + xhr.responseText);
  }
  function getTimeMillis() {
    return Kotlin.Long.fromNumber(Date.now());
  }
  function decodeBase64(s) {
    throw new NotImplementedError_init('An operation is not implemented: ' + 'decodeBase64 not implemented');
  }
  function BrowserNetwork(udpProxyAddress, udpProxyPort) {
    if (udpProxyAddress === void 0)
      udpProxyAddress = null;
    if (udpProxyPort === void 0)
      udpProxyPort = 0;
    this.udpProxyAddress_0 = udpProxyAddress;
    this.udpProxyPort_0 = udpProxyPort;
  }
  function BrowserNetwork$link$ObjectLiteral(this$BrowserNetwork) {
    this.myAddress_4sgley$_0 = new BrowserNetwork$link$ObjectLiteral$myAddress$ObjectLiteral();
    this.udpProxy = null;
    var tmp$;
    if ((tmp$ = this$BrowserNetwork.udpProxyAddress_0) != null) {
      this.udpProxy = new BrowserUdpProxy(this, tmp$, this$BrowserNetwork.udpProxyPort_0);
    }
    this.udpMtu_1mlzjd$_0 = 1500;
  }
  Object.defineProperty(BrowserNetwork$link$ObjectLiteral.prototype, 'myAddress', {
    get: function () {
      return this.myAddress_4sgley$_0;
    }
  });
  Object.defineProperty(BrowserNetwork$link$ObjectLiteral.prototype, 'udpMtu', {
    get: function () {
      return this.udpMtu_1mlzjd$_0;
    }
  });
  BrowserNetwork$link$ObjectLiteral.prototype.listenUdp_a6m852$ = function (port, udpListener) {
    return ensureNotNull(this.udpProxy).listenUdp_a6m852$(port, udpListener);
  };
  BrowserNetwork$link$ObjectLiteral.prototype.startHttpServer_za3lpa$ = function (port) {
    throw new NotImplementedError_init('An operation is not implemented: ' + 'BrowserNetwork.startHttpServer not implemented');
  };
  function BrowserNetwork$link$ObjectLiteral$connectWebSocket$ObjectLiteral(closure$port, closure$webSocket, this$) {
    this.closure$port = closure$port;
    this.closure$webSocket = closure$webSocket;
    this.fromAddress_rtwpt4$_0 = this$.myAddress;
    this.toAddress_z3r81z$_0 = this$.myAddress;
  }
  Object.defineProperty(BrowserNetwork$link$ObjectLiteral$connectWebSocket$ObjectLiteral.prototype, 'fromAddress', {
    get: function () {
      return this.fromAddress_rtwpt4$_0;
    }
  });
  Object.defineProperty(BrowserNetwork$link$ObjectLiteral$connectWebSocket$ObjectLiteral.prototype, 'toAddress', {
    get: function () {
      return this.toAddress_z3r81z$_0;
    }
  });
  Object.defineProperty(BrowserNetwork$link$ObjectLiteral$connectWebSocket$ObjectLiteral.prototype, 'port', {
    get: function () {
      return this.closure$port;
    }
  });
  BrowserNetwork$link$ObjectLiteral$connectWebSocket$ObjectLiteral.prototype.send_fqrh44$ = function (bytes) {
    this.closure$webSocket.send(new Int8Array(toTypedArray_1(bytes)));
  };
  BrowserNetwork$link$ObjectLiteral$connectWebSocket$ObjectLiteral.$metadata$ = {
    kind: Kind_CLASS,
    interfaces: [Network$TcpConnection]
  };
  function BrowserNetwork$link$ObjectLiteral$connectWebSocket$lambda(closure$webSocketListener, closure$tcpConnection) {
    return function (it) {
      console.log('WebSocket open!', it);
      closure$webSocketListener.connected_67ozxy$(closure$tcpConnection);
      return Unit;
    };
  }
  function BrowserNetwork$link$ObjectLiteral$connectWebSocket$lambda_0(closure$webSocketListener, closure$tcpConnection) {
    return function (it) {
      var tmp$, tmp$_0;
      var buf = Kotlin.isType(tmp$ = it.data, ArrayBuffer) ? tmp$ : throwCCE();
      var byteBuf = new Int8Array(buf);
      var bytes = new Int8Array(byteBuf.length);
      tmp$_0 = byteBuf.length;
      for (var i = 0; i < tmp$_0; i++) {
        bytes[i] = byteBuf[i];
      }
      closure$webSocketListener.receive_r00qii$(closure$tcpConnection, bytes);
      return Unit;
    };
  }
  function BrowserNetwork$link$ObjectLiteral$connectWebSocket$lambda_1(it) {
    console.log('WebSocket error!', it);
    return Unit;
  }
  function BrowserNetwork$link$ObjectLiteral$connectWebSocket$lambda_2(it) {
    console.log('WebSocket close!', it);
    return Unit;
  }
  BrowserNetwork$link$ObjectLiteral.prototype.connectWebSocket_t0j9bj$ = function (toAddress, port, path, webSocketListener) {
    var tmp$;
    var webSocket = new WebSocket(trimEnd((Kotlin.isType(tmp$ = toAddress, BrowserNetwork$BrowserAddress) ? tmp$ : throwCCE()).urlString, Kotlin.charArrayOf(47)) + path);
    webSocket.binaryType = 'arraybuffer';
    var tcpConnection = new BrowserNetwork$link$ObjectLiteral$connectWebSocket$ObjectLiteral(port, webSocket, this);
    webSocket.onopen = BrowserNetwork$link$ObjectLiteral$connectWebSocket$lambda(webSocketListener, tcpConnection);
    webSocket.onmessage = BrowserNetwork$link$ObjectLiteral$connectWebSocket$lambda_0(webSocketListener, tcpConnection);
    webSocket.onerror = BrowserNetwork$link$ObjectLiteral$connectWebSocket$lambda_1;
    webSocket.onclose = BrowserNetwork$link$ObjectLiteral$connectWebSocket$lambda_2;
    return tcpConnection;
  };
  function BrowserNetwork$link$ObjectLiteral$myAddress$ObjectLiteral() {
  }
  BrowserNetwork$link$ObjectLiteral$myAddress$ObjectLiteral.$metadata$ = {
    kind: Kind_CLASS,
    interfaces: [Network$Address]
  };
  BrowserNetwork$link$ObjectLiteral.$metadata$ = {
    kind: Kind_CLASS,
    interfaces: [Network$Link]
  };
  BrowserNetwork.prototype.link = function () {
    return new BrowserNetwork$link$ObjectLiteral(this);
  };
  function BrowserNetwork$BrowserAddress(urlString) {
    this.urlString = urlString;
  }
  BrowserNetwork$BrowserAddress.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'BrowserAddress',
    interfaces: [Network$Address]
  };
  BrowserNetwork.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'BrowserNetwork',
    interfaces: [Network]
  };
  function BrowserUdpProxy(link, address, port) {
    this.udpListener_0 = null;
    this.tcpConnection = link.connectWebSocket_t0j9bj$(address, port, '/sm/udpProxy', this);
    this.connected = false;
    this.toSend = ArrayList_init();
  }
  BrowserUdpProxy.prototype.connected_67ozxy$ = function (tcpConnection) {
    this.connected = true;
    var tmp$;
    tmp$ = this.toSend.iterator();
    while (tmp$.hasNext()) {
      var element = tmp$.next();
      tcpConnection.send_fqrh44$(element);
    }
    this.toSend.clear();
  };
  BrowserUdpProxy.prototype.receive_r00qii$ = function (tcpConnection, bytes) {
    var $receiver = new ByteArrayReader(bytes);
    var op = $receiver.readByte();
    if (op === toByte(unboxChar(Network$UdpProxy_getInstance().RECEIVE_OP) | 0)) {
      var fromAddress = new BrowserUdpProxy$UdpProxyAddress($receiver.readBytes());
      var fromPort = $receiver.readInt();
      var data = $receiver.readBytes();
      ensureNotNull(this.udpListener_0).receive_ytpeqp$(fromAddress, fromPort, data);
    }
  };
  BrowserUdpProxy.prototype.reset_67ozxy$ = function (tcpConnection) {
    throw new NotImplementedError_init('An operation is not implemented: ' + 'UdpProxy.reset not implemented');
  };
  BrowserUdpProxy.prototype.listenUdp_a6m852$ = function (port, udpListener) {
    if (this.udpListener_0 != null) {
      throw IllegalStateException_init('UDP proxy is already listening');
    }
    this.udpListener_0 = udpListener;
    if (port !== 0) {
      throw IllegalArgumentException_init("UDP proxy can't listen on a specific port, sorry!");
    }
    var $receiver = new ByteArrayWriter();
    $receiver.writeByte_s8j3t7$(toByte(unboxChar(Network$UdpProxy_getInstance().LISTEN_OP) | 0));
    this.log_0('UDP: Listen');
    this.tcpConnectionSend_0($receiver.toBytes());
    return new BrowserUdpProxy$UdpSocketProxy(this, port);
  };
  function BrowserUdpProxy$UdpSocketProxy($outer, requestedPort) {
    this.$outer = $outer;
    this.serverPort_r2mu8g$_0 = requestedPort;
  }
  Object.defineProperty(BrowserUdpProxy$UdpSocketProxy.prototype, 'serverPort', {
    get: function () {
      return this.serverPort_r2mu8g$_0;
    }
  });
  BrowserUdpProxy$UdpSocketProxy.prototype.sendUdp_ytpeqp$ = function (toAddress, port, bytes) {
    if (!Kotlin.isType(toAddress, BrowserUdpProxy$UdpProxyAddress)) {
      throw IllegalArgumentException_init("UDP proxy can't send to " + toAddress + '!');
    }
    var tmp$ = this.$outer;
    var $receiver = new ByteArrayWriter();
    $receiver.writeByte_s8j3t7$(toByte(unboxChar(Network$UdpProxy_getInstance().SEND_OP) | 0));
    $receiver.writeBytes_mj6st8$(toAddress.bytes);
    $receiver.writeInt_za3lpa$(port);
    $receiver.writeBytes_mj6st8$(bytes);
    tmp$.tcpConnectionSend_0($receiver.toBytes());
  };
  BrowserUdpProxy$UdpSocketProxy.prototype.broadcastUdp_3fbn1q$ = function (port, bytes) {
    var tmp$ = this.$outer;
    var $receiver = new ByteArrayWriter();
    $receiver.writeByte_s8j3t7$(toByte(unboxChar(Network$UdpProxy_getInstance().BROADCAST_OP) | 0));
    $receiver.writeInt_za3lpa$(port);
    $receiver.writeBytes_mj6st8$(bytes);
    tmp$.tcpConnectionSend_0($receiver.toBytes());
  };
  BrowserUdpProxy$UdpSocketProxy.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'UdpSocketProxy',
    interfaces: [Network$UdpSocket]
  };
  BrowserUdpProxy.prototype.tcpConnectionSend_0 = function (bytes) {
    if (this.connected) {
      this.tcpConnection.send_fqrh44$(bytes);
    }
     else {
      this.toSend.add_11rb$(bytes);
    }
  };
  BrowserUdpProxy.prototype.log_0 = function (s) {
    println(s);
  };
  function BrowserUdpProxy$UdpProxyAddress(bytes) {
    this.bytes = bytes;
  }
  function BrowserUdpProxy$UdpProxyAddress$toString$lambda(it) {
    return (it & 255).toString();
  }
  BrowserUdpProxy$UdpProxyAddress.prototype.toString = function () {
    return joinToString_0(this.bytes, '.', void 0, void 0, void 0, void 0, BrowserUdpProxy$UdpProxyAddress$toString$lambda);
  };
  BrowserUdpProxy$UdpProxyAddress.prototype.equals = function (other) {
    var tmp$;
    if (this === other)
      return true;
    if (other == null || !equals(get_js(Kotlin.getKClassFromExpression(this)), get_js(Kotlin.getKClassFromExpression(other))))
      return false;
    Kotlin.isType(tmp$ = other, BrowserUdpProxy$UdpProxyAddress) ? tmp$ : throwCCE();
    if (!contentEquals(this.bytes, other.bytes))
      return false;
    return true;
  };
  BrowserUdpProxy$UdpProxyAddress.prototype.hashCode = function () {
    return contentHashCode(this.bytes);
  };
  BrowserUdpProxy$UdpProxyAddress.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'UdpProxyAddress',
    interfaces: [Network$Address]
  };
  BrowserUdpProxy$UdpProxyAddress.prototype.component1 = function () {
    return this.bytes;
  };
  BrowserUdpProxy$UdpProxyAddress.prototype.copy_fqrh44$ = function (bytes) {
    return new BrowserUdpProxy$UdpProxyAddress(bytes === void 0 ? this.bytes : bytes);
  };
  BrowserUdpProxy.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'BrowserUdpProxy',
    interfaces: [Network$WebSocketListener]
  };
  function FakeMediaDevices(visualizer) {
    this.visualizer_0 = visualizer;
    this.currentCam = null;
  }
  FakeMediaDevices.prototype.getCurrentCam = function () {
    return this.currentCam;
  };
  FakeMediaDevices.prototype.getCamera = function () {
    var $receiver = new FakeMediaDevices$FakeCamera(this, 640, 480);
    this.visualizer_0.addFrameListener_imgev1$($receiver);
    return $receiver;
  };
  function FakeMediaDevices$FakeCamera($outer, width, height) {
    this.$outer = $outer;
    this.width = width;
    this.height = height;
    var $receiver = new WebGLRenderer_init({preserveDrawingBuffer: true});
    $receiver.setSize(this.width, this.height);
    this.camRenderer = $receiver;
    var tmp$;
    this.camCtx_0 = ensureNotNull((Kotlin.isType(tmp$ = this.camRenderer.domElement, HTMLCanvasElement) ? tmp$ : throwCCE()).getContext('webgl'));
    this.altCamera_0 = new PerspectiveCamera_init(45, 1.0, 1, 10000);
    this.pixelBuffer_0 = new Uint8ClampedArray(Kotlin.imul(this.width, this.height) * 4 | 0);
    this.imageData_0 = new ImageData(this.pixelBuffer_0, this.width, this.height);
    this.onImage_f8p0b7$_0 = FakeMediaDevices$FakeCamera$onImage$lambda;
  }
  function FakeMediaDevices$FakeCamera$onFrameReady$lambda(this$FakeCamera) {
    return function (it) {
      this$FakeCamera.onImage(new ImageBitmapImage(it));
      return Unit;
    };
  }
  FakeMediaDevices$FakeCamera.prototype.onFrameReady = function (scene, camera) {
    this.altCamera_0.copy(camera, true);
    this.altCamera_0.aspect = this.width / this.height;
    this.altCamera_0.updateProjectionMatrix();
    this.camRenderer.render(scene, this.altCamera_0);
    this.camCtx_0.readPixels(0, 0, this.width, this.height, this.camCtx_0.RGBA, this.camCtx_0.UNSIGNED_BYTE, new Uint8Array(this.pixelBuffer_0.buffer));
    var tmp$ = window;
    var tmp$_0 = this.imageData_0;
    var imageOrientation;
    var premultiplyAlpha;
    var colorSpaceConversion;
    var resizeWidth;
    var resizeHeight;
    var resizeQuality;
    if (imageOrientation === void 0) {
      imageOrientation = 'none';
    }
    if (premultiplyAlpha === void 0) {
      premultiplyAlpha = 'default';
    }
    if (colorSpaceConversion === void 0) {
      colorSpaceConversion = 'default';
    }
    if (resizeWidth === void 0)
      resizeWidth = undefined;
    if (resizeHeight === void 0)
      resizeHeight = undefined;
    if (resizeQuality === void 0) {
      resizeQuality = 'low';
    }
    var o = {};
    o['imageOrientation'] = imageOrientation;
    o['premultiplyAlpha'] = premultiplyAlpha;
    o['colorSpaceConversion'] = colorSpaceConversion;
    o['resizeWidth'] = resizeWidth;
    o['resizeHeight'] = resizeHeight;
    o['resizeQuality'] = resizeQuality;
    o.imageOrientation = 'flipY';
    tmp$.createImageBitmap(tmp$_0, o).then(FakeMediaDevices$FakeCamera$onFrameReady$lambda(this));
  };
  Object.defineProperty(FakeMediaDevices$FakeCamera.prototype, 'onImage', {
    get: function () {
      return this.onImage_f8p0b7$_0;
    },
    set: function (onImage) {
      this.onImage_f8p0b7$_0 = onImage;
    }
  });
  function FakeMediaDevices$FakeCamera$close$lambda(f) {
    return Unit;
  }
  FakeMediaDevices$FakeCamera.prototype.close = function () {
    this.onImage = FakeMediaDevices$FakeCamera$close$lambda;
    this.$outer.visualizer_0.removeFrameListener_imgev1$(this);
  };
  function FakeMediaDevices$FakeCamera$onImage$lambda(f) {
    return Unit;
  }
  FakeMediaDevices$FakeCamera.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'FakeCamera',
    interfaces: [Visualizer$FrameListener, MediaDevices$Camera]
  };
  FakeMediaDevices.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'FakeMediaDevices',
    interfaces: [MediaDevices]
  };
  function decodeQueryParams(location) {
    var query = location.search;
    if (startsWith(query, '?')) {
      return decodeQueryParams_0(query.substring(1));
    }
     else {
      return emptyMap();
    }
  }
  function decodeHashParams(location) {
    var hash = location.hash;
    if (startsWith(hash, '#')) {
      return decodeQueryParams_0(hash.substring(1));
    }
     else {
      return emptyMap();
    }
  }
  function decodeQueryParams_0($receiver) {
    var $receiver_0 = split($receiver, ['&']);
    var destination = ArrayList_init_1(collectionSizeOrDefault($receiver_0, 10));
    var tmp$;
    tmp$ = $receiver_0.iterator();
    while (tmp$.hasNext()) {
      var item = tmp$.next();
      var tmp$_0 = destination.add_11rb$;
      var tmp$_1 = split(item, ['='], void 0, 2);
      var k = tmp$_1.get_za3lpa$(0);
      var v = tmp$_1.get_za3lpa$(1);
      tmp$_0.call(destination, to(decodeURIComponent(k), decodeURIComponent(v)));
    }
    return toMap(destination);
  }
  function SwirlyPixelArranger(pixelDensity, pixelSpacing) {
    if (pixelDensity === void 0)
      pixelDensity = 0.2;
    if (pixelSpacing === void 0)
      pixelSpacing = 2.0;
    this.pixelDensity_0 = pixelDensity;
    this.pixelSpacing_0 = pixelSpacing;
  }
  SwirlyPixelArranger.prototype.arrangePixels_zdreix$ = function (vizPanel) {
    return (new SwirlyPixelArranger$PanelArranger(this, vizPanel)).arrangePixels();
  };
  function SwirlyPixelArranger$PanelArranger($outer, vizPanel) {
    this.$outer = $outer;
    var x = vizPanel.area * this.$outer.pixelDensity_0;
    this.pixelCount_0 = numberToInt(Math_0.floor(x));
    this.panelGeometry_0 = vizPanel.geometry_8be2vx$.clone();
    this.vertices_0 = this.panelGeometry_0.vertices;
    this.isMultiFaced_0 = vizPanel.isMultiFaced;
    this.edgeNeighbors_0 = vizPanel.edgeNeighbors_8be2vx$;
  }
  SwirlyPixelArranger$PanelArranger.prototype.arrangePixels = function () {
    var tmp$;
    this.panelGeometry_0.computeFaceNormals();
    var pixelsGeometry = new Geometry();
    var quaternion = new Quaternion();
    var panelFaces = this.panelGeometry_0.faces;
    var curFace = panelFaces[0];
    var revertToNormal = ensureNotNull(curFace.normal).clone();
    var straightOnNormal = new Vector3(0, 0, 1);
    quaternion.setFromUnitVectors(ensureNotNull(curFace.normal), straightOnNormal);
    var matrix = new Matrix4_init();
    matrix.makeRotationFromQuaternion(quaternion);
    this.panelGeometry_0.applyMatrix(matrix);
    pixelsGeometry.applyMatrix(matrix);
    var pos = this.randomLocation_ihye3j$(curFace, this.vertices_0);
    var nextPos = new Vector3();
    pixelsGeometry.vertices.push(pos.clone());
    var tries = 1000;
    var angleRad = Random.Default.nextFloat() * 2 * math.PI;
    var angleRadDelta = Random.Default.nextFloat() * 0.5 - 0.5;
    var pixelsSinceEdge = 0;
    var pixelI = 1;
    while (pixelI < this.pixelCount_0) {
      var tmp$_0 = pos.x;
      var tmp$_1 = this.$outer.pixelSpacing_0;
      var x = angleRad;
      nextPos.x = tmp$_0 + tmp$_1 * Math_0.sin(x);
      var tmp$_2 = pos.y;
      var tmp$_3 = this.$outer.pixelSpacing_0;
      var x_0 = angleRad;
      nextPos.y = tmp$_2 + tmp$_3 * Math_0.cos(x_0);
      nextPos.z = pos.z;
      if (!this.isInsideFace_xcftfz$(curFace, nextPos)) {
        var newFace = this.getFaceForPoint_1srbse$(curFace, nextPos);
        if (newFace != null) {
          quaternion.setFromUnitVectors(straightOnNormal, revertToNormal);
          matrix.makeRotationFromQuaternion(quaternion);
          this.panelGeometry_0.applyMatrix(matrix);
          pixelsGeometry.applyMatrix(matrix);
          nextPos.applyMatrix4(matrix);
          curFace = newFace;
          revertToNormal = ensureNotNull(curFace.normal).clone();
          quaternion.setFromUnitVectors(ensureNotNull(curFace.normal), straightOnNormal);
          matrix.makeRotationFromQuaternion(quaternion);
          this.panelGeometry_0.applyMatrix(matrix);
          pixelsGeometry.applyMatrix(matrix);
          nextPos.applyMatrix4(matrix);
          nextPos.z = this.panelGeometry_0.vertices[newFace.a].z;
          if (!this.isInsideFace_xcftfz$(curFace, nextPos)) {
            nextPos.copy(this.randomLocation_ihye3j$(curFace, this.vertices_0));
          }
        }
         else {
          angleRad = Random.Default.nextFloat() * 2 * math.PI;
          if ((tmp$ = tries, tries = tmp$ - 1 | 0, tmp$) < 0)
            break;
          pixelsSinceEdge = 0;
          continue;
        }
      }
      pixelsGeometry.vertices.push(nextPos.clone());
      angleRad += angleRadDelta;
      angleRadDelta *= 1 - Random.Default.nextFloat() * 0.2 + 0.1;
      if (pixelsSinceEdge > (this.pixelCount_0 / 10 | 0)) {
        angleRad = Random.Default.nextFloat() * 2 * math.PI;
        angleRadDelta = Random.Default.nextFloat() * 0.5 - 0.5;
        pixelsSinceEdge = 0;
      }
      pos.copy(nextPos);
      pixelsSinceEdge = pixelsSinceEdge + 1 | 0;
      pixelI = pixelI + 1 | 0;
    }
    quaternion.setFromUnitVectors(straightOnNormal, revertToNormal);
    matrix.makeRotationFromQuaternion(quaternion);
    this.panelGeometry_0.applyMatrix(matrix);
    pixelsGeometry.applyMatrix(matrix);
    return pixelsGeometry.vertices;
  };
  SwirlyPixelArranger$PanelArranger.prototype.randomLocation_ihye3j$ = function (face, vertices) {
    var v = (new Vector3()).copy(vertices[face.a]);
    v.addScaledVector((new Vector3()).copy(vertices[face.b]).sub(v), Random.Default.nextFloat());
    v.addScaledVector((new Vector3()).copy(vertices[face.c]).sub(v), Random.Default.nextFloat());
    return v;
  };
  SwirlyPixelArranger$PanelArranger.prototype.isInsideFace_xcftfz$ = function (curFace, v) {
    var vertices = this.panelGeometry_0.vertices;
    return this.isInside_9mi9sy$(this.xy_as37vi$(v), [this.xy_as37vi$(vertices[curFace.a]), this.xy_as37vi$(vertices[curFace.b]), this.xy_as37vi$(vertices[curFace.c])]);
  };
  SwirlyPixelArranger$PanelArranger.prototype.isInside_9mi9sy$ = function (point, vs) {
    var tmp$;
    var x = point.component1()
    , y = point.component2();
    var inside = false;
    var i = 0;
    var j = vs.length - 1 | 0;
    while (i < vs.length) {
      var xi = vs[i].x;
      var yi = vs[i].y;
      var xj = vs[j].x;
      var yj = vs[j].y;
      var intersect = yi > y !== yj > y && x < (xj - xi) * (y - yi) / (yj - yi) + xi;
      if (intersect) {
        inside = !inside;
      }
      j = (tmp$ = i, i = tmp$ + 1 | 0, tmp$);
    }
    return inside;
  };
  SwirlyPixelArranger$PanelArranger.prototype.xy_as37vi$ = function (v) {
    return new VizPanel$Point2(v.x, v.y);
  };
  SwirlyPixelArranger$PanelArranger.prototype.getFaceForPoint_1srbse$ = function (curFace, v) {
    var tmp$;
    if (this.isMultiFaced_0) {
      var vertices = this.panelGeometry_0.vertices;
      var closestEdge = {v: [-1, -1]};
      var bestDistance = {v: kotlin_js_internal_FloatCompanionObject.POSITIVE_INFINITY};
      var $receiver = segments(curFace);
      var tmp$_0;
      for (tmp$_0 = 0; tmp$_0 !== $receiver.length; ++tmp$_0) {
        var element = $receiver[tmp$_0];
        var closestPointOnEdge = new Vector3();
        var v0 = element[0];
        var v1 = element[1];
        (new Line3(vertices[v0], vertices[v1])).closestPointToPoint(v, true, closestPointOnEdge);
        var thisDistance = closestPointOnEdge.distanceTo(v);
        if (thisDistance < bestDistance.v) {
          closestEdge.v = element;
          bestDistance.v = thisDistance;
        }
      }
      var edgeId = asKey(closestEdge.v);
      var neighbors = this.edgeNeighbors_0.get_11rb$(edgeId);
      var tmp$_1;
      if (neighbors != null) {
        var destination = ArrayList_init();
        var tmp$_2;
        tmp$_2 = neighbors.iterator();
        while (tmp$_2.hasNext()) {
          var element_0 = tmp$_2.next();
          if (element_0 !== curFace)
            destination.add_11rb$(element_0);
        }
        tmp$_1 = destination;
      }
       else
        tmp$_1 = null;
      var neighbor = (tmp$ = tmp$_1) != null ? tmp$ : emptyList();
      if (neighbor.size === 0) {
        return null;
      }
       else
        neighbor.size;
      return neighbor.get_za3lpa$(0);
    }
    return null;
  };
  SwirlyPixelArranger$PanelArranger.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'PanelArranger',
    interfaces: []
  };
  SwirlyPixelArranger.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'SwirlyPixelArranger',
    interfaces: []
  };
  function Visualizer(sheepModel, display) {
    this.display_0 = display;
    this.mapperIsRunning_y90e96$_0 = false;
    this.frameListeners_0 = ArrayList_init();
    this.controls_0 = null;
    this.camera_0 = null;
    this.scene_0 = null;
    this.renderer_0 = null;
    this.geom_0 = null;
    this.obj_0 = new Object3D();
    this.pointMaterial_0 = null;
    this.lineMaterial_0 = null;
    this.panelMaterial_0 = null;
    this.raycaster_0 = null;
    this.mouse_0 = null;
    this.sphere_0 = null;
    this.rendererListeners_0 = ArrayList_init();
    this.vizPanels_0 = ArrayList_init();
    var tmp$, tmp$_0;
    this.sheepView_0 = Kotlin.isType(tmp$ = ensureNotNull(document.getElementById('sheepView')), HTMLDivElement) ? tmp$ : throwCCE();
    this.selectionInfo_0 = Kotlin.isType(tmp$_0 = ensureNotNull(document.getElementById('selectionInfo')), HTMLDivElement) ? tmp$_0 : throwCCE();
    this.sheepView_0.addEventListener('mousedown', Visualizer_init$lambda(this), false);
    this.camera_0 = new PerspectiveCamera_init(45, this.sheepView_0.offsetWidth / this.sheepView_0.offsetHeight, 1, 10000);
    this.camera_0.position.z = 1000.0;
    this.controls_0 = new OrbitControls(this.camera_0, this.sheepView_0);
    this.controls_0.minPolarAngle = math.PI / 2 - 0.25;
    this.controls_0.maxPolarAngle = math.PI / 2 + 0.25;
    this.scene_0 = new Scene();
    var $receiver = new PointsMaterial();
    $receiver.color.set(16777215);
    this.pointMaterial_0 = $receiver;
    var $receiver_0 = new LineBasicMaterial();
    $receiver_0.color.set(11184810);
    this.lineMaterial_0 = $receiver_0;
    var $receiver_1 = new LineBasicMaterial();
    $receiver_1.color.set(11184810);
    $receiver_1.linewidth = 3.0;
    this.panelMaterial_0 = $receiver_1;
    this.scene_0.add(this.camera_0);
    this.renderer_0 = new WebGLRenderer_init();
    this.renderer_0.setPixelRatio(window.devicePixelRatio);
    this.renderer_0.setSize(this.sheepView_0.offsetWidth, this.sheepView_0.offsetHeight);
    this.sheepView_0.appendChild(this.renderer_0.domElement);
    this.geom_0 = new Geometry();
    this.raycaster_0 = new Raycaster_init();
    this.raycaster_0.params.Points.threshold = 1;
    var tmp$_1 = new SphereBufferGeometry(1, 32, 32);
    var $receiver_2 = new MeshBasicMaterial();
    $receiver_2.color.set(16711680);
    this.sphere_0 = new Mesh_init(tmp$_1, $receiver_2);
    this.scene_0.add(this.sphere_0);
    var tmp$_2;
    tmp$_2 = sheepModel.vertices.iterator();
    while (tmp$_2.hasNext()) {
      var element = tmp$_2.next();
      this.geom_0.vertices.push(new Vector3(element.x, element.y, element.z));
    }
    this.startRender_0();
    var resizeTaskId = {v: null};
    window.addEventListener('resize', Visualizer_init$lambda_0(resizeTaskId, this));
    this.REFRESH_DELAY_0 = 50;
    this.resizeDelay_0 = 100;
  }
  Object.defineProperty(Visualizer.prototype, 'rotate_0', {
    get: function () {
      return this.getVizRotationEl_0().checked;
    },
    set: function (value) {
      this.getVizRotationEl_0().checked = value;
    }
  });
  Object.defineProperty(Visualizer.prototype, 'mapperIsRunning', {
    get: function () {
      return this.mapperIsRunning_y90e96$_0;
    },
    set: function (isRunning) {
      this.mapperIsRunning_y90e96$_0 = isRunning;
      var tmp$;
      tmp$ = this.vizPanels_0.iterator();
      while (tmp$.hasNext()) {
        var element = tmp$.next();
        element.faceMaterial_8be2vx$.transparent = !isRunning;
      }
      if (isRunning) {
        this.rotate_0 = false;
      }
    }
  });
  Visualizer.prototype.addFrameListener_imgev1$ = function (frameListener) {
    this.frameListeners_0.add_11rb$(frameListener);
  };
  Visualizer.prototype.removeFrameListener_imgev1$ = function (frameListener) {
    this.frameListeners_0.remove_11rb$(frameListener);
  };
  Visualizer.prototype.onMouseDown_tfvzir$ = function (event) {
    this.mouse_0 = new Vector2(event.clientX / this.sheepView_0.offsetWidth * 2 - 1, -(event.clientY / this.sheepView_0.offsetHeight) * 2 + 1);
  };
  Visualizer.prototype.addPanel_jfju1k$ = function (p) {
    var vizPanel = new VizPanel(p, this.geom_0, this.scene_0);
    this.vizPanels_0.add_11rb$(vizPanel);
    return vizPanel;
  };
  Visualizer.prototype.addMovingHead_g9d0gu$ = function (movingHead, dmxUniverse) {
    return new Visualizer$VizMovingHead(this, movingHead, dmxUniverse);
  };
  function Visualizer$VizMovingHead($outer, movingHead, dmxUniverse) {
    this.$outer = $outer;
    this.baseChannel_0 = ensureNotNull(Config$Companion_getInstance().DMX_DEVICES.get_11rb$(movingHead.name));
    this.device_0 = new LixadaMiniMovingHead(dmxUniverse.reader_sxjeop$(this.baseChannel_0, 16, Visualizer$VizMovingHead$device$lambda(this)));
    this.geometry_0 = new ConeBufferGeometry(50, 1000);
    var $receiver = new MeshBasicMaterial();
    $receiver.color.set(16776960);
    this.material_0 = $receiver;
    this.cone_0 = new Mesh_init(this.geometry_0, this.material_0);
    this.geometry_0.applyMatrix((new Matrix4_init()).makeTranslation(0.0, -500.0, 0.0));
    this.material_0.transparent = true;
    this.material_0.opacity = 0.75;
    this.cone_0.position.set(movingHead.origin.x, movingHead.origin.y, movingHead.origin.z);
    this.cone_0.rotation.x = -math.PI / 2;
    this.$outer.scene_0.add(this.cone_0);
  }
  Visualizer$VizMovingHead.prototype.receivedDmxFrame_0 = function () {
    this.material_0.color.set(this.device_0.color.rgb);
    this.material_0.visible = this.device_0.dimmer > 0.1;
    this.cone_0.rotation.x = -math.PI / 2 + this.device_0.tilt;
    this.cone_0.rotation.z = this.device_0.pan;
  };
  function Visualizer$VizMovingHead$device$lambda(this$VizMovingHead) {
    return function () {
      this$VizMovingHead.receivedDmxFrame_0();
      return Unit;
    };
  }
  Visualizer$VizMovingHead.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'VizMovingHead',
    interfaces: []
  };
  Visualizer.prototype.getVizRotationEl_0 = function () {
    var tmp$;
    return Kotlin.isType(tmp$ = document.getElementById('vizRotation'), HTMLInputElement) ? tmp$ : throwCCE();
  };
  Visualizer.prototype.startRender_0 = function () {
    this.geom_0.computeBoundingSphere();
    var $receiver = new Points();
    $receiver.geometry = this.geom_0;
    $receiver.material = this.pointMaterial_0;
    this.obj_0 = $receiver;
    this.scene_0.add(this.obj_0);
    var target = this.geom_0.boundingSphere.center.clone();
    this.controls_0.target = target;
    this.camera_0.lookAt(target);
    this.render();
  };
  function Visualizer$render$lambda$lambda(this$Visualizer) {
    return function (it) {
      this$Visualizer.render();
      return Unit;
    };
  }
  function Visualizer$render$lambda(this$Visualizer) {
    return function () {
      window.requestAnimationFrame(Visualizer$render$lambda$lambda(this$Visualizer));
    };
  }
  Visualizer.prototype.render = function () {
    var tmp$;
    window.setTimeout(Visualizer$render$lambda(this), this.REFRESH_DELAY_0);
    if ((tmp$ = this.mouse_0) != null) {
      this.mouse_0 = null;
      this.raycaster_0.setFromCamera(tmp$, this.camera_0);
      var intersections = this.raycaster_0.intersectObjects(this.scene_0.children, false);
      var tmp$_0;
      for (tmp$_0 = 0; tmp$_0 !== intersections.length; ++tmp$_0) {
        var element = intersections[tmp$_0];
        var intersectedObject = element.object;
        var vizPanel = VizPanel$Companion_getInstance().getFromObject_pz83aj$(intersectedObject);
        if (vizPanel != null) {
          this.selectionInfo_0.innerText = 'Selected: ' + vizPanel.name;
        }
      }
    }
    if (!this.mapperIsRunning) {
      if (this.getVizRotationEl_0().checked) {
        var rotSpeed = 0.01;
        var x = this.camera_0.position.x;
        var z = this.camera_0.position.z;
        this.camera_0.position.x = x * Math_0.cos(rotSpeed) + z * Math_0.sin(rotSpeed);
        var tmp$_1 = this.camera_0.position;
        var x_0 = rotSpeed * 2;
        var tmp$_2 = z * Math_0.cos(x_0);
        var x_1 = rotSpeed * 2;
        tmp$_1.z = tmp$_2 - x * Math_0.sin(x_1);
        this.camera_0.lookAt(this.scene_0.position);
      }
    }
    this.controls_0.update();
    var startMs = getTimeMillis();
    this.renderer_0.render(this.scene_0, this.camera_0);
    this.display_0.renderMs = getTimeMillis().subtract(startMs).toInt();
    var tmp$_3;
    tmp$_3 = this.frameListeners_0.iterator();
    while (tmp$_3.hasNext()) {
      var element_0 = tmp$_3.next();
      element_0.onFrameReady(this.scene_0, this.camera_0);
    }
    var tmp$_4;
    tmp$_4 = this.rendererListeners_0.iterator();
    while (tmp$_4.hasNext()) {
      var element_1 = tmp$_4.next();
      element_1();
    }
  };
  Visualizer.prototype.doResize_0 = function () {
    this.camera_0.aspect = this.sheepView_0.offsetWidth / this.sheepView_0.offsetHeight;
    this.camera_0.updateProjectionMatrix();
    this.renderer_0.setSize(this.sheepView_0.offsetWidth, this.sheepView_0.offsetHeight);
  };
  Visualizer.prototype.mapperStatusChanged_6taknv$ = function (isRunning) {
    this.mapperIsRunning = isRunning;
  };
  function Visualizer$FrameListener() {
  }
  Visualizer$FrameListener.$metadata$ = {
    kind: Kind_INTERFACE,
    simpleName: 'FrameListener',
    interfaces: []
  };
  function Visualizer_init$lambda(this$Visualizer) {
    return function (event) {
      var tmp$, tmp$_0;
      tmp$_0 = Kotlin.isType(tmp$ = event, MouseEvent) ? tmp$ : throwCCE();
      this$Visualizer.onMouseDown_tfvzir$(tmp$_0);
      return Unit;
    };
  }
  function Visualizer_init$lambda$lambda(closure$resizeTaskId, this$Visualizer) {
    return function () {
      closure$resizeTaskId.v = null;
      this$Visualizer.doResize_0();
      return Unit;
    };
  }
  function Visualizer_init$lambda_0(closure$resizeTaskId, this$Visualizer) {
    return function (it) {
      if (closure$resizeTaskId.v !== null) {
        window.clearTimeout(ensureNotNull(closure$resizeTaskId.v));
      }
      closure$resizeTaskId.v = window.setTimeout(Visualizer_init$lambda$lambda(closure$resizeTaskId, this$Visualizer), this$Visualizer.resizeDelay_0);
      return Unit;
    };
  }
  Visualizer.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Visualizer',
    interfaces: [JsMapperUi$StatusListener]
  };
  function VizPanel(panel, geom, scene) {
    VizPanel$Companion_getInstance();
    this.geom_0 = geom;
    this.scene_0 = scene;
    this.name = panel.name;
    this.geometry_8be2vx$ = new Geometry();
    this.area = 0.0;
    this.panelNormal_0 = null;
    this.isMultiFaced = false;
    this.edgeNeighbors_8be2vx$ = null;
    var $receiver = new LineBasicMaterial();
    $receiver.color.set(11184810);
    this.lineMaterial_0 = $receiver;
    this.faceMaterial_8be2vx$ = null;
    this.mesh_0 = null;
    this.lines_0 = null;
    this.vizPixels_6qsto5$_0 = null;
    var panelGeometry = this.geometry_8be2vx$;
    var panelVertices = panelGeometry.vertices;
    var triangle = new Triangle();
    var faceAreas = ArrayList_init();
    var $receiver_0 = panel.faces.faces;
    var destination = ArrayList_init_1(collectionSizeOrDefault($receiver_0, 10));
    var tmp$;
    tmp$ = $receiver_0.iterator();
    while (tmp$.hasNext()) {
      var item = tmp$.next();
      var tmp$_0 = destination.add_11rb$;
      var tmp$_1;
      var $receiver_1 = item.vertexIds;
      var destination_0 = ArrayList_init_1(collectionSizeOrDefault($receiver_1, 10));
      var tmp$_2;
      tmp$_2 = $receiver_1.iterator();
      while (tmp$_2.hasNext()) {
        var item_0 = tmp$_2.next();
        var tmp$_3 = destination_0.add_11rb$;
        var v = this.geom_0.vertices[item_0];
        var lvi = indexOf_0(panelVertices, v);
        if (lvi === -1) {
          lvi = panelVertices.length;
          panelVertices.push(v);
        }
        tmp$_3.call(destination_0, lvi);
      }
      var localVerts = destination_0;
      triangle.set(panelVertices[localVerts.get_za3lpa$(0)], panelVertices[localVerts.get_za3lpa$(1)], panelVertices[localVerts.get_za3lpa$(2)]);
      var faceArea = typeof (tmp$_1 = triangle.getArea()) === 'number' ? tmp$_1 : throwCCE();
      faceAreas.add_11rb$(faceArea);
      this.area = this.area + faceArea;
      var normal = document['non-existant-key'];
      tmp$_0.call(destination, new Face3_init(localVerts.get_za3lpa$(0), localVerts.get_za3lpa$(1), localVerts.get_za3lpa$(2), normal));
    }
    panelGeometry.faces = copyToArray(destination);
    this.isMultiFaced = panelGeometry.faces.length > 1;
    panelGeometry.computeFaceNormals();
    var faceNormalSum = new Vector3();
    var $receiver_2 = panelGeometry.faces;
    var tmp$_4, tmp$_0_0;
    var index = 0;
    for (tmp$_4 = 0; tmp$_4 !== $receiver_2.length; ++tmp$_4) {
      var item_1 = $receiver_2[tmp$_4];
      var faceArea_0 = faceAreas.get_za3lpa$((tmp$_0_0 = index, index = tmp$_0_0 + 1 | 0, tmp$_0_0));
      faceNormalSum.addScaledVector(ensureNotNull(item_1.normal), faceArea_0);
    }
    this.panelNormal_0 = faceNormalSum.divideScalar(this.area);
    var edgeNeighbors = LinkedHashMap_init();
    var $receiver_3 = panelGeometry.faces;
    var tmp$_5;
    for (tmp$_5 = 0; tmp$_5 !== $receiver_3.length; ++tmp$_5) {
      var element = $receiver_3[tmp$_5];
      var $receiver_4 = segments(element);
      var tmp$_6;
      for (tmp$_6 = 0; tmp$_6 !== $receiver_4.length; ++tmp$_6) {
        var element_0 = $receiver_4[tmp$_6];
        var vsKey = asKey(element_0);
        var tmp$_7;
        var value = edgeNeighbors.get_11rb$(vsKey);
        if (value == null) {
          var answer = ArrayList_init();
          edgeNeighbors.put_xwzc9p$(vsKey, answer);
          tmp$_7 = answer;
        }
         else {
          tmp$_7 = value;
        }
        var neighbors = tmp$_7;
        neighbors.add_11rb$(element);
      }
    }
    this.edgeNeighbors_8be2vx$ = edgeNeighbors;
    this.geom_0.computeVertexNormals();
    var $receiver_5 = panel.lines;
    var destination_1 = ArrayList_init_1(collectionSizeOrDefault($receiver_5, 10));
    var tmp$_8;
    tmp$_8 = $receiver_5.iterator();
    while (tmp$_8.hasNext()) {
      var item_2 = tmp$_8.next();
      var tmp$_9 = destination_1.add_11rb$;
      var lineGeo = new Geometry();
      var $receiver_6 = item_2.points;
      var destination_2 = ArrayList_init_1(collectionSizeOrDefault($receiver_6, 10));
      var tmp$_10;
      tmp$_10 = $receiver_6.iterator();
      while (tmp$_10.hasNext()) {
        var item_3 = tmp$_10.next();
        destination_2.add_11rb$(new Vector3(item_3.x, item_3.y, item_3.z));
      }
      lineGeo.vertices = copyToArray(destination_2);
      tmp$_9.call(destination_1, lineGeo);
    }
    var lines = destination_1;
    var $receiver_7 = new MeshBasicMaterial();
    $receiver_7.color.set(2236962);
    this.faceMaterial_8be2vx$ = $receiver_7;
    this.faceMaterial_8be2vx$.side = THREE.FrontSide;
    this.faceMaterial_8be2vx$.transparent = false;
    this.mesh_0 = new Mesh_init(panelGeometry, this.faceMaterial_8be2vx$);
    this.mesh_0.name = 'Surface: ' + this.name;
    this.mesh_0.userData['VizPanel'] = this;
    this.scene_0.add(this.mesh_0);
    var destination_3 = ArrayList_init_1(collectionSizeOrDefault(lines, 10));
    var tmp$_11;
    tmp$_11 = lines.iterator();
    while (tmp$_11.hasNext()) {
      var item_4 = tmp$_11.next();
      destination_3.add_11rb$(new Line_init(item_4, this.lineMaterial_0));
    }
    this.lines_0 = destination_3;
    var tmp$_12;
    tmp$_12 = this.lines_0.iterator();
    while (tmp$_12.hasNext()) {
      var element_1 = tmp$_12.next();
      this.scene_0.add(element_1);
    }
  }
  function VizPanel$Companion() {
    VizPanel$Companion_instance = this;
    this.roundLightTx_0 = (new TextureLoader_init()).load('./visualizer/textures/round.png', VizPanel$Companion$roundLightTx$lambda, VizPanel$Companion$roundLightTx$lambda_0, VizPanel$Companion$roundLightTx$lambda_1);
  }
  VizPanel$Companion.prototype.getFromObject_pz83aj$ = function (object3D) {
    var tmp$;
    return (tmp$ = object3D.userData['VizPanel']) == null || Kotlin.isType(tmp$, VizPanel) ? tmp$ : throwCCE();
  };
  function VizPanel$Companion$roundLightTx$lambda(it) {
    println('loaded!');
    return Unit;
  }
  function VizPanel$Companion$roundLightTx$lambda_0(it) {
    println('progress!');
    return Unit;
  }
  function VizPanel$Companion$roundLightTx$lambda_1(it) {
    println('error!');
    return Unit;
  }
  VizPanel$Companion.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'Companion',
    interfaces: []
  };
  var VizPanel$Companion_instance = null;
  function VizPanel$Companion_getInstance() {
    if (VizPanel$Companion_instance === null) {
      new VizPanel$Companion();
    }
    return VizPanel$Companion_instance;
  }
  Object.defineProperty(VizPanel.prototype, 'vizPixels', {
    get: function () {
      return this.vizPixels_6qsto5$_0;
    },
    set: function (value) {
      var tmp$;
      (tmp$ = this.vizPixels_6qsto5$_0) != null ? (tmp$.removeFromScene_smv6vb$(this.scene_0), Unit) : null;
      value != null ? (value.addToScene_smv6vb$(this.scene_0), Unit) : null;
      this.vizPixels_6qsto5$_0 = value;
    }
  });
  function VizPanel$Point2(x, y) {
    this.x = x;
    this.y = y;
  }
  VizPanel$Point2.prototype.component1 = function () {
    return this.x;
  };
  VizPanel$Point2.prototype.component2 = function () {
    return this.y;
  };
  VizPanel$Point2.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Point2',
    interfaces: []
  };
  function VizPanel$VizPixels(vizPanel, positions) {
    this.size_cgbufu$_0 = positions.length;
    this.pixGeometry_0 = new BufferGeometry();
    this.planeGeometry_0 = null;
    this.vertexColorBufferAttr_0 = null;
    this.colorsAsInts_0 = new Int32Array(this.size);
    var positionsArray = new Float32Array(this.size * 3 | 0);
    var tmp$, tmp$_0;
    var index = 0;
    for (tmp$ = 0; tmp$ !== positions.length; ++tmp$) {
      var item = positions[tmp$];
      var i = (tmp$_0 = index, index = tmp$_0 + 1 | 0, tmp$_0);
      positionsArray[i * 3 | 0] = item.x;
      positionsArray[(i * 3 | 0) + 1 | 0] = item.y;
      positionsArray[(i * 3 | 0) + 2 | 0] = item.z;
    }
    var positionsBufferAttr = new Float32BufferAttribute(positionsArray, 3);
    this.pixGeometry_0.addAttribute('position', positionsBufferAttr);
    this.vertexColorBufferAttr_0 = new Float32BufferAttribute(new Float32Array((this.size * 3 | 0) * 4 | 0), 3);
    this.vertexColorBufferAttr_0.dynamic = true;
    var rotator = new Rotator(new Vector3(0, 0, 1), vizPanel.panelNormal_0);
    var destination = ArrayList_init_1(positions.length);
    var tmp$_1;
    for (tmp$_1 = 0; tmp$_1 !== positions.length; ++tmp$_1) {
      var item_0 = positions[tmp$_1];
      var tmp$_2 = destination.add_11rb$;
      var geometry = new PlaneBufferGeometry(2 + Random.Default.nextFloat() * 8, 2 + Random.Default.nextFloat() * 8);
      rotator.rotate_lbyolm$([geometry]);
      geometry.translate(item_0.x, item_0.y, item_0.z);
      tmp$_2.call(destination, geometry);
    }
    this.planeGeometry_0 = BufferGeometryUtils$Companion.mergeBufferGeometries(copyToArray(destination));
    this.planeGeometry_0.addAttribute('color', this.vertexColorBufferAttr_0);
    var tmp$_3 = this.planeGeometry_0;
    var $receiver = new MeshBasicMaterial();
    $receiver.side = THREE.FrontSide;
    $receiver.transparent = true;
    $receiver.blending = THREE.AdditiveBlending;
    $receiver.depthTest = false;
    $receiver.depthWrite = false;
    $receiver.vertexColors = THREE.VertexColors;
    $receiver.map = VizPanel$Companion_getInstance().roundLightTx_0;
    this.pixelsMesh_0 = new Mesh_init(tmp$_3, $receiver);
  }
  Object.defineProperty(VizPanel$VizPixels.prototype, 'size', {
    get: function () {
      return this.size_cgbufu$_0;
    }
  });
  VizPanel$VizPixels.prototype.addToScene_smv6vb$ = function (scene) {
    scene.add(this.pixelsMesh_0);
  };
  VizPanel$VizPixels.prototype.removeFromScene_smv6vb$ = function (scene) {
    scene.remove(this.pixelsMesh_0);
  };
  VizPanel$VizPixels.prototype.get_za3lpa$ = function (i) {
    return new Color(this.colorsAsInts_0[i]);
  };
  VizPanel$VizPixels.prototype.set_ibd5tj$ = function (i, color) {
    this.colorsAsInts_0[i] = color.argb;
    var redF = color.redF / 2;
    var greenF = color.greenF / 2;
    var blueF = color.blueF / 2;
    var rgb3Buf = this.vertexColorBufferAttr_0;
    rgb3Buf.setXYZ(i * 4 | 0, redF, greenF, blueF);
    rgb3Buf.setXYZ((i * 4 | 0) + 1 | 0, redF, greenF, blueF);
    rgb3Buf.setXYZ((i * 4 | 0) + 2 | 0, redF, greenF, blueF);
    rgb3Buf.setXYZ((i * 4 | 0) + 3 | 0, redF, greenF, blueF);
    this.vertexColorBufferAttr_0.needsUpdate = true;
  };
  VizPanel$VizPixels.prototype.set_tmuqsv$ = function (colors) {
    var a = this.size;
    var maxCount = Math_0.min(a, colors.length);
    var rgbBuf = this.vertexColorBufferAttr_0.array;
    for (var i = 0; i < maxCount; i++) {
      this.colorsAsInts_0[i] = colors[i].argb;
      var pColor = colors[i];
      rgbBuf[i * 3 | 0] = pColor.redF / 2;
      rgbBuf[(i * 3 | 0) + 1 | 0] = pColor.greenF / 2;
      rgbBuf[(i * 3 | 0) + 2 | 0] = pColor.blueF / 2;
    }
    this.vertexColorBufferAttr_0.needsUpdate = true;
  };
  VizPanel$VizPixels.prototype.getPixelLocationsInPanelSpace_zdreix$ = function (vizPanel) {
    var tmp$, tmp$_0;
    var panelGeom = vizPanel.geometry_8be2vx$.clone();
    var pixGeom = this.pixGeometry_0.clone();
    var straightOnNormal = new Vector3(0, 0, 1);
    var rotator = new Rotator(vizPanel.panelNormal_0, straightOnNormal);
    rotator.rotate_htojx2$([panelGeom]);
    rotator.rotate_lbyolm$([pixGeom]);
    panelGeom.computeBoundingBox();
    var boundingBox = ensureNotNull(panelGeom.boundingBox);
    var min = boundingBox.min;
    var size = minus_0(boundingBox.max, boundingBox.min);
    var translate = (new Matrix4_init()).makeTranslation(-min.x, -min.y, -min.z);
    panelGeom.applyMatrix(translate);
    pixGeom.applyMatrix(translate);
    var scale = (new Matrix4_init()).makeScale(1.0 / size.x, 1.0 / size.y, 1.0);
    panelGeom.applyMatrix(scale);
    pixGeom.applyMatrix(scale);
    var pixelVs = ArrayList_init();
    var pixelPositions = pixGeom.getAttribute('position');
    var array = Kotlin.isType(tmp$ = pixelPositions.array, Float32Array) ? tmp$ : throwCCE();
    tmp$_0 = pixelPositions.count * 3 | 0;
    for (var i = 0; i < tmp$_0; i += 3) {
      var v = new Vector2_0(this.clamp_mx4ult$(array[i]), this.clamp_mx4ult$(array[i + 1 | 0]));
      pixelVs.add_11rb$(v);
    }
    return copyToArray(pixelVs);
  };
  VizPanel$VizPixels.prototype.clamp_mx4ult$ = function (f) {
    var b = Math_0.max(f, 0.0);
    return Math_0.min(1.0, b);
  };
  VizPanel$VizPixels.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'VizPixels',
    interfaces: [Pixels]
  };
  VizPanel.prototype.getPixelLocations = function () {
    var tmp$;
    return (tmp$ = this.vizPixels) != null ? tmp$.getPixelLocationsInPanelSpace_zdreix$(this) : null;
  };
  VizPanel.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'VizPanel',
    interfaces: []
  };
  function segments($receiver) {
    return [[$receiver.a, $receiver.b], [$receiver.b, $receiver.c], [$receiver.c, $receiver.a]];
  }
  function asKey($receiver) {
    return joinToString(sorted_0($receiver), '-');
  }
  function Rotator(from, to) {
    this.from = from;
    this.to = to;
    this.quaternion_0 = new Quaternion();
    this.matrix_0 = new Matrix4_init();
    this.quaternion_0.setFromUnitVectors(this.from, this.to);
    this.matrix_0.makeRotationFromQuaternion(this.quaternion_0);
  }
  Rotator.prototype.rotate_htojx2$ = function (geoms) {
    var tmp$;
    for (tmp$ = 0; tmp$ !== geoms.length; ++tmp$) {
      var element = geoms[tmp$];
      element.applyMatrix(this.matrix_0);
    }
  };
  Rotator.prototype.rotate_lbyolm$ = function (geoms) {
    var tmp$;
    for (tmp$ = 0; tmp$ !== geoms.length; ++tmp$) {
      var element = geoms[tmp$];
      element.applyMatrix(this.matrix_0);
    }
  };
  Rotator.prototype.rotate_22wt45$ = function (vectors) {
    var tmp$;
    for (tmp$ = 0; tmp$ !== vectors.length; ++tmp$) {
      var element = vectors[tmp$];
      element.applyMatrix4(this.matrix_0);
    }
  };
  Rotator.prototype.invert = function () {
    return new Rotator(this.to, this.from);
  };
  Rotator.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Rotator',
    interfaces: []
  };
  Object.defineProperty(BeatData, 'Companion', {
    get: BeatData$Companion_getInstance
  });
  Object.defineProperty(BeatData, '$serializer', {
    get: BeatData$$serializer_getInstance
  });
  var package$baaahs = _.baaahs || (_.baaahs = {});
  package$baaahs.BeatData = BeatData;
  package$baaahs.BeatSource = BeatSource;
  package$baaahs.Clock = Clock_0;
  Brain.RenderTree = Brain$RenderTree;
  Brain.FakeModelSurface = Brain$FakeModelSurface;
  package$baaahs.Brain = Brain;
  Object.defineProperty(Color, 'Companion', {
    get: Color$Companion_getInstance
  });
  package$baaahs.Color_init_7b5o5w$ = Color_init_0;
  package$baaahs.Color_init_tjonv8$ = Color_init_1;
  package$baaahs.Color_init_sz6or4$ = Color_init_2;
  package$baaahs.Color = Color;
  Object.defineProperty(Config, 'Companion', {
    get: Config$Companion_getInstance
  });
  Config.MovingHeadConfig = Config$MovingHeadConfig;
  package$baaahs.Config = Config;
  package$baaahs.Display = Display;
  package$baaahs.NetworkDisplay = NetworkDisplay;
  package$baaahs.PinkyDisplay = PinkyDisplay;
  package$baaahs.StubPinkyDisplay = StubPinkyDisplay;
  package$baaahs.BrainDisplay = BrainDisplay;
  package$baaahs.VisualizerDisplay = VisualizerDisplay;
  Dmx.Universe = Dmx$Universe;
  Dmx.Buffer = Dmx$Buffer;
  Dmx.Channel = Dmx$Channel;
  Dmx.DeviceType = Dmx$DeviceType;
  package$baaahs.Dmx = Dmx;
  package$baaahs.FirmwareDaddy = FirmwareDaddy;
  package$baaahs.PermissiveFirmwareDaddy = PermissiveFirmwareDaddy;
  package$baaahs.StrictFirmwareDaddy = StrictFirmwareDaddy;
  package$baaahs.Gadget = Gadget;
  package$baaahs.GadgetValueObserver = GadgetValueObserver;
  Object.defineProperty(GadgetData, 'Companion', {
    get: GadgetData$Companion_getInstance
  });
  Object.defineProperty(GadgetData, '$serializer', {
    get: GadgetData$$serializer_getInstance
  });
  package$baaahs.GadgetData = GadgetData;
  Object.defineProperty(package$baaahs, 'GadgetDataSerializer', {
    get: function () {
      return GadgetDataSerializer;
    }
  });
  package$baaahs.GadgetDisplay = GadgetDisplay;
  Object.defineProperty(package$baaahs, 'gadgetModule', {
    get: function () {
      return gadgetModule;
    }
  });
  package$baaahs.array_byae19$ = array;
  GadgetManager.GadgetInfo = GadgetManager$GadgetInfo;
  package$baaahs.GadgetManager = GadgetManager;
  Object.defineProperty(Mapper$Detector, 'RED', {
    get: Mapper$Detector$RED_getInstance
  });
  Object.defineProperty(Mapper$Detector, 'GREEN', {
    get: Mapper$Detector$GREEN_getInstance
  });
  Object.defineProperty(Mapper$Detector, 'BLUE', {
    get: Mapper$Detector$BLUE_getInstance
  });
  Mapper.Detector = Mapper$Detector;
  Mapper.ReliableShaderMessageDeliverer = Mapper$ReliableShaderMessageDeliverer;
  Mapper.TimeoutException = Mapper$TimeoutException;
  Mapper.DeliveryAttempt = Mapper$DeliveryAttempt;
  Mapper.BrainToMap = Mapper$BrainToMap;
  Mapper.PixelMapData = Mapper$PixelMapData;
  package$baaahs.Mapper = Mapper;
  MapperUi.Listener = MapperUi$Listener;
  MapperUi.VisibleSurface = MapperUi$VisibleSurface;
  MapperUi.CameraOrientation = MapperUi$CameraOrientation;
  package$baaahs.MapperUi = MapperUi;
  MediaDevices.Camera = MediaDevices$Camera;
  Object.defineProperty(MediaDevices$Region, 'Companion', {
    get: MediaDevices$Region$Companion_getInstance
  });
  MediaDevices.Region = MediaDevices$Region;
  package$baaahs.MediaDevices = MediaDevices;
  Object.defineProperty(MovingHead$ColorMode, 'ColorWheel', {
    get: MovingHead$ColorMode$ColorWheel_getInstance
  });
  Object.defineProperty(MovingHead$ColorMode, 'RGB', {
    get: MovingHead$ColorMode$RGB_getInstance
  });
  Object.defineProperty(MovingHead$ColorMode, 'RGBW', {
    get: MovingHead$ColorMode$RGBW_getInstance
  });
  MovingHead.ColorMode = MovingHead$ColorMode;
  MovingHead.Buffer = MovingHead$Buffer;
  Object.defineProperty(MovingHead$MovingHeadPosition, 'Companion', {
    get: MovingHead$MovingHeadPosition$Companion_getInstance
  });
  Object.defineProperty(MovingHead$MovingHeadPosition, '$serializer', {
    get: MovingHead$MovingHeadPosition$$serializer_getInstance
  });
  MovingHead.MovingHeadPosition = MovingHead$MovingHeadPosition;
  Object.defineProperty(MovingHead, 'Companion', {
    get: MovingHead$Companion_getInstance
  });
  Object.defineProperty(MovingHead, '$serializer', {
    get: MovingHead$$serializer_getInstance
  });
  package$baaahs.MovingHead = MovingHead;
  package$baaahs.MovingHeadManager = MovingHeadManager;
  MovingHeadDisplay.Wrapper = MovingHeadDisplay$Wrapper;
  package$baaahs.MovingHeadDisplay = MovingHeadDisplay;
  Pinky.PinkyBeatDisplayer = Pinky$PinkyBeatDisplayer;
  Pinky.NetworkStats = Pinky$NetworkStats;
  Pinky$PoolingRenderContext.Holder = Pinky$PoolingRenderContext$Holder;
  Pinky.PoolingRenderContext = Pinky$PoolingRenderContext;
  package$baaahs.Pinky = Pinky;
  package$baaahs.BrainId = BrainId;
  package$baaahs.BrainInfo = BrainInfo;
  Object.defineProperty(PubSub, 'Companion', {
    get: PubSub$Companion_getInstance
  });
  PubSub.Origin = PubSub$Origin;
  PubSub.Channel = PubSub$Channel;
  PubSub.Topic = PubSub$Topic;
  PubSub.Listener = PubSub$Listener;
  PubSub.TopicInfo = PubSub$TopicInfo;
  PubSub.Connection = PubSub$Connection;
  PubSub.Endpoint = PubSub$Endpoint;
  PubSub$Server.PublisherListener = PubSub$Server$PublisherListener;
  PubSub.Server = PubSub$Server;
  PubSub.Client = PubSub$Client;
  package$baaahs.PubSub = PubSub;
  Object.defineProperty(ShaderId, 'SOLID', {
    get: ShaderId$SOLID_getInstance
  });
  Object.defineProperty(ShaderId, 'PIXEL', {
    get: ShaderId$PIXEL_getInstance
  });
  Object.defineProperty(ShaderId, 'SINE_WAVE', {
    get: ShaderId$SINE_WAVE_getInstance
  });
  Object.defineProperty(ShaderId, 'COMPOSITOR', {
    get: ShaderId$COMPOSITOR_getInstance
  });
  Object.defineProperty(ShaderId, 'SPARKLE', {
    get: ShaderId$SPARKLE_getInstance
  });
  Object.defineProperty(ShaderId, 'SIMPLE_SPATIAL', {
    get: ShaderId$SIMPLE_SPATIAL_getInstance
  });
  Object.defineProperty(ShaderId, 'HEART', {
    get: ShaderId$HEART_getInstance
  });
  Object.defineProperty(ShaderId, 'RANDOM', {
    get: ShaderId$RANDOM_getInstance
  });
  Object.defineProperty(ShaderId, 'GLSL_SANDBOX_55301', {
    get: ShaderId$GLSL_SANDBOX_55301_getInstance
  });
  Object.defineProperty(ShaderId, 'GLSL_SHADER', {
    get: ShaderId$GLSL_SHADER_getInstance
  });
  Object.defineProperty(ShaderId, 'Companion', {
    get: ShaderId$Companion_getInstance
  });
  package$baaahs.ShaderId = ShaderId;
  package$baaahs.ShaderReader = ShaderReader;
  package$baaahs.RenderContext = RenderContext;
  Object.defineProperty(Shader, 'Companion', {
    get: Shader$Companion_getInstance
  });
  Shader.Buffer = Shader$Buffer;
  Shader.Renderer = Shader$Renderer;
  package$baaahs.Shader = Shader;
  package$baaahs.PooledRenderer = PooledRenderer;
  package$baaahs.Pixels = Pixels;
  Model.Surface = Model$Surface;
  package$baaahs.Model = Model;
  Object.defineProperty(SheepModel$Point, 'Companion', {
    get: SheepModel$Point$Companion_getInstance
  });
  Object.defineProperty(SheepModel$Point, '$serializer', {
    get: SheepModel$Point$$serializer_getInstance
  });
  SheepModel.Point = SheepModel$Point;
  SheepModel.Line = SheepModel$Line;
  SheepModel.Face = SheepModel$Face;
  SheepModel.Faces = SheepModel$Faces;
  SheepModel.Panel = SheepModel$Panel;
  package$baaahs.SheepModel = SheepModel;
  Show.Renderer = Show$Renderer;
  Show.RestartShowException = Show$RestartShowException;
  package$baaahs.Show = Show;
  ShowRunner.SurfacesChanges = ShowRunner$SurfacesChanges;
  ShowRunner.SurfaceReceiver = ShowRunner$SurfaceReceiver;
  package$baaahs.ShowRunner = ShowRunner;
  Object.defineProperty(package$baaahs, 'SparkleMotion', {
    get: SparkleMotion_getInstance
  });
  package$baaahs.Surface = Surface;
  package$baaahs.IdentifiedSurface = IdentifiedSurface;
  package$baaahs.AnonymousSurface = AnonymousSurface;
  Object.defineProperty(package$baaahs, 'Topics', {
    get: Topics_getInstance
  });
  Object.defineProperty(LixadaMiniMovingHead$Channel, 'PAN', {
    get: LixadaMiniMovingHead$Channel$PAN_getInstance
  });
  Object.defineProperty(LixadaMiniMovingHead$Channel, 'TILT', {
    get: LixadaMiniMovingHead$Channel$TILT_getInstance
  });
  Object.defineProperty(LixadaMiniMovingHead$Channel, 'DIMMER', {
    get: LixadaMiniMovingHead$Channel$DIMMER_getInstance
  });
  Object.defineProperty(LixadaMiniMovingHead$Channel, 'RED', {
    get: LixadaMiniMovingHead$Channel$RED_getInstance
  });
  Object.defineProperty(LixadaMiniMovingHead$Channel, 'GREEN', {
    get: LixadaMiniMovingHead$Channel$GREEN_getInstance
  });
  Object.defineProperty(LixadaMiniMovingHead$Channel, 'BLUE', {
    get: LixadaMiniMovingHead$Channel$BLUE_getInstance
  });
  Object.defineProperty(LixadaMiniMovingHead$Channel, 'WHITE', {
    get: LixadaMiniMovingHead$Channel$WHITE_getInstance
  });
  Object.defineProperty(LixadaMiniMovingHead$Channel, 'PAN_TILT_SPEED', {
    get: LixadaMiniMovingHead$Channel$PAN_TILT_SPEED_getInstance
  });
  Object.defineProperty(LixadaMiniMovingHead$Channel, 'COLOR_RESET', {
    get: LixadaMiniMovingHead$Channel$COLOR_RESET_getInstance
  });
  LixadaMiniMovingHead.Channel = LixadaMiniMovingHead$Channel;
  var package$dmx = package$baaahs.dmx || (package$baaahs.dmx = {});
  package$dmx.LixadaMiniMovingHead = LixadaMiniMovingHead;
  Object.defineProperty(Shenzarpy, 'Companion', {
    get: Shenzarpy$Companion_getInstance
  });
  Object.defineProperty(Shenzarpy$WheelColor, 'RED', {
    get: Shenzarpy$WheelColor$RED_getInstance
  });
  Object.defineProperty(Shenzarpy$WheelColor, 'ORANGE', {
    get: Shenzarpy$WheelColor$ORANGE_getInstance
  });
  Object.defineProperty(Shenzarpy$WheelColor, 'AQUAMARINE', {
    get: Shenzarpy$WheelColor$AQUAMARINE_getInstance
  });
  Object.defineProperty(Shenzarpy$WheelColor, 'DEEP_GREEN', {
    get: Shenzarpy$WheelColor$DEEP_GREEN_getInstance
  });
  Object.defineProperty(Shenzarpy$WheelColor, 'LIGHT_GREEN', {
    get: Shenzarpy$WheelColor$LIGHT_GREEN_getInstance
  });
  Object.defineProperty(Shenzarpy$WheelColor, 'LAVENDER', {
    get: Shenzarpy$WheelColor$LAVENDER_getInstance
  });
  Object.defineProperty(Shenzarpy$WheelColor, 'PINK', {
    get: Shenzarpy$WheelColor$PINK_getInstance
  });
  Object.defineProperty(Shenzarpy$WheelColor, 'YELLOW', {
    get: Shenzarpy$WheelColor$YELLOW_getInstance
  });
  Object.defineProperty(Shenzarpy$WheelColor, 'MAGENTA', {
    get: Shenzarpy$WheelColor$MAGENTA_getInstance
  });
  Object.defineProperty(Shenzarpy$WheelColor, 'CYAN', {
    get: Shenzarpy$WheelColor$CYAN_getInstance
  });
  Object.defineProperty(Shenzarpy$WheelColor, 'CTO2', {
    get: Shenzarpy$WheelColor$CTO2_getInstance
  });
  Object.defineProperty(Shenzarpy$WheelColor, 'CTO1', {
    get: Shenzarpy$WheelColor$CTO1_getInstance
  });
  Object.defineProperty(Shenzarpy$WheelColor, 'CTB', {
    get: Shenzarpy$WheelColor$CTB_getInstance
  });
  Object.defineProperty(Shenzarpy$WheelColor, 'DARK_BLUE', {
    get: Shenzarpy$WheelColor$DARK_BLUE_getInstance
  });
  Object.defineProperty(Shenzarpy$WheelColor, 'WHITE', {
    get: Shenzarpy$WheelColor$WHITE_getInstance
  });
  Object.defineProperty(Shenzarpy$WheelColor, 'Companion', {
    get: Shenzarpy$WheelColor$Companion_getInstance
  });
  Shenzarpy.WheelColor = Shenzarpy$WheelColor;
  Object.defineProperty(Shenzarpy$Channel, 'COLOR_WHEEL', {
    get: Shenzarpy$Channel$COLOR_WHEEL_getInstance
  });
  Object.defineProperty(Shenzarpy$Channel, 'SHUTTER', {
    get: Shenzarpy$Channel$SHUTTER_getInstance
  });
  Object.defineProperty(Shenzarpy$Channel, 'DIMMER', {
    get: Shenzarpy$Channel$DIMMER_getInstance
  });
  Object.defineProperty(Shenzarpy$Channel, 'GOBO_WHEEL', {
    get: Shenzarpy$Channel$GOBO_WHEEL_getInstance
  });
  Object.defineProperty(Shenzarpy$Channel, 'PRISM', {
    get: Shenzarpy$Channel$PRISM_getInstance
  });
  Object.defineProperty(Shenzarpy$Channel, 'PRISM_ROTATION', {
    get: Shenzarpy$Channel$PRISM_ROTATION_getInstance
  });
  Object.defineProperty(Shenzarpy$Channel, 'MACRO', {
    get: Shenzarpy$Channel$MACRO_getInstance
  });
  Object.defineProperty(Shenzarpy$Channel, 'FROST', {
    get: Shenzarpy$Channel$FROST_getInstance
  });
  Object.defineProperty(Shenzarpy$Channel, 'FOCUS', {
    get: Shenzarpy$Channel$FOCUS_getInstance
  });
  Object.defineProperty(Shenzarpy$Channel, 'PAN', {
    get: Shenzarpy$Channel$PAN_getInstance
  });
  Object.defineProperty(Shenzarpy$Channel, 'PAN_FINE', {
    get: Shenzarpy$Channel$PAN_FINE_getInstance
  });
  Object.defineProperty(Shenzarpy$Channel, 'TILT', {
    get: Shenzarpy$Channel$TILT_getInstance
  });
  Object.defineProperty(Shenzarpy$Channel, 'TILT_FINE', {
    get: Shenzarpy$Channel$TILT_FINE_getInstance
  });
  Object.defineProperty(Shenzarpy$Channel, 'PAN_TILT_SPEED', {
    get: Shenzarpy$Channel$PAN_TILT_SPEED_getInstance
  });
  Object.defineProperty(Shenzarpy$Channel, 'RESET', {
    get: Shenzarpy$Channel$RESET_getInstance
  });
  Object.defineProperty(Shenzarpy$Channel, 'LAMP_CONTROL', {
    get: Shenzarpy$Channel$LAMP_CONTROL_getInstance
  });
  Object.defineProperty(Shenzarpy$Channel, 'BLANK', {
    get: Shenzarpy$Channel$BLANK_getInstance
  });
  Object.defineProperty(Shenzarpy$Channel, 'COLOR_WHEEL_SPEED', {
    get: Shenzarpy$Channel$COLOR_WHEEL_SPEED_getInstance
  });
  Object.defineProperty(Shenzarpy$Channel, 'DIM_PRISM_ATOM_SPEED', {
    get: Shenzarpy$Channel$DIM_PRISM_ATOM_SPEED_getInstance
  });
  Object.defineProperty(Shenzarpy$Channel, 'GOBO_WHEEL_SPEED', {
    get: Shenzarpy$Channel$GOBO_WHEEL_SPEED_getInstance
  });
  Object.defineProperty(Shenzarpy$Channel, 'Companion', {
    get: Shenzarpy$Channel$Companion_getInstance
  });
  Shenzarpy.Channel = Shenzarpy$Channel;
  package$dmx.Shenzarpy = Shenzarpy;
  Object.defineProperty(ColorPicker, 'Companion', {
    get: ColorPicker$Companion_getInstance
  });
  Object.defineProperty(ColorPicker, '$serializer', {
    get: ColorPicker$$serializer_getInstance
  });
  var package$gadgets = package$baaahs.gadgets || (package$baaahs.gadgets = {});
  package$gadgets.ColorPicker = ColorPicker;
  Object.defineProperty(PalettePicker, 'Companion', {
    get: PalettePicker$Companion_getInstance
  });
  Object.defineProperty(PalettePicker, '$serializer', {
    get: PalettePicker$$serializer_getInstance
  });
  package$gadgets.PalettePicker = PalettePicker;
  Object.defineProperty(Slider, 'Companion', {
    get: Slider$Companion_getInstance
  });
  Object.defineProperty(Slider, '$serializer', {
    get: Slider$$serializer_getInstance
  });
  package$gadgets.Slider = Slider;
  Object.defineProperty(Matrix4, 'Companion', {
    get: Matrix4$Companion_getInstance
  });
  Object.defineProperty(Matrix4, '$serializer', {
    get: Matrix4$$serializer_getInstance
  });
  var package$geom = package$baaahs.geom || (package$baaahs.geom = {});
  package$geom.Matrix4 = Matrix4;
  Object.defineProperty(Vector2F, 'Companion', {
    get: Vector2F$Companion_getInstance
  });
  Object.defineProperty(Vector2F, '$serializer', {
    get: Vector2F$$serializer_getInstance
  });
  package$geom.Vector2F = Vector2F;
  Object.defineProperty(Vector3F, 'Companion', {
    get: Vector3F$Companion_getInstance
  });
  Object.defineProperty(Vector3F, '$serializer', {
    get: Vector3F$$serializer_getInstance
  });
  package$geom.Vector3F = Vector3F;
  var package$glsl = package$baaahs.glsl || (package$baaahs.glsl = {});
  package$glsl.GlslManager = GlslManager;
  GlslRenderer.AdjustibleUniform = GlslRenderer$AdjustibleUniform;
  GlslRenderer.Instance = GlslRenderer$Instance;
  GlslRenderer.Uniform = GlslRenderer$Uniform;
  GlslRenderer.Uniforms = GlslRenderer$Uniforms;
  package$glsl.GlslRenderer = GlslRenderer;
  package$glsl.GlslSurface = GlslSurface;
  package$glsl.SurfacePixels = SurfacePixels;
  var package$imaging = package$baaahs.imaging || (package$baaahs.imaging = {});
  package$imaging.Image = Image;
  package$imaging.Bitmap = Bitmap;
  package$imaging.UByteClampedArray = UByteClampedArray;
  var package$io = package$baaahs.io || (package$baaahs.io = {});
  package$io.ByteArrayReader = ByteArrayReader;
  package$io.ByteArrayWriter_init_za3lpa$ = ByteArrayWriter_init;
  package$io.ByteArrayWriter = ByteArrayWriter;
  package$io.Fs = Fs;
  var package$mapper = package$baaahs.mapper || (package$baaahs.mapper = {});
  Object.defineProperty(package$mapper, 'DateTimeSerializer', {
    get: DateTimeSerializer_getInstance
  });
  ImageProcessing.Histogram = ImageProcessing$Histogram;
  Object.defineProperty(ImageProcessing, 'Companion', {
    get: ImageProcessing$Companion_getInstance
  });
  ImageProcessing.Analysis = ImageProcessing$Analysis;
  package$mapper.ImageProcessing = ImageProcessing;
  package$mapper.MapperClient = MapperClient;
  Object.defineProperty(MapperEndpoint, 'Companion', {
    get: MapperEndpoint$Companion_getInstance
  });
  package$mapper.MapperEndpoint = MapperEndpoint;
  MappingResults.Info = MappingResults$Info;
  package$mapper.MappingResults = MappingResults;
  package$mapper.SessionMappingResults = SessionMappingResults;
  Object.defineProperty(MappingSession$SurfaceData$PixelData, 'Companion', {
    get: MappingSession$SurfaceData$PixelData$Companion_getInstance
  });
  Object.defineProperty(MappingSession$SurfaceData$PixelData, '$serializer', {
    get: MappingSession$SurfaceData$PixelData$$serializer_getInstance
  });
  MappingSession$SurfaceData.PixelData = MappingSession$SurfaceData$PixelData;
  Object.defineProperty(MappingSession$SurfaceData, 'Companion', {
    get: MappingSession$SurfaceData$Companion_getInstance
  });
  Object.defineProperty(MappingSession$SurfaceData, '$serializer', {
    get: MappingSession$SurfaceData$$serializer_getInstance
  });
  MappingSession.SurfaceData = MappingSession$SurfaceData;
  Object.defineProperty(MappingSession, 'Companion', {
    get: MappingSession$Companion_getInstance
  });
  Object.defineProperty(MappingSession, '$serializer', {
    get: MappingSession$$serializer_getInstance
  });
  package$mapper.MappingSession = MappingSession;
  Object.defineProperty(Storage, 'Companion', {
    get: Storage$Companion_getInstance
  });
  package$mapper.Storage = Storage;
  Object.defineProperty(FragmentingUdpLink, 'Companion', {
    get: FragmentingUdpLink$Companion_getInstance
  });
  FragmentingUdpLink.Fragment = FragmentingUdpLink$Fragment;
  FragmentingUdpLink.FragmentingUdpSocket = FragmentingUdpLink$FragmentingUdpSocket;
  var package$net = package$baaahs.net || (package$baaahs.net = {});
  package$net.FragmentingUdpLink = FragmentingUdpLink;
  Network.Link = Network$Link;
  Network.Address = Network$Address;
  Network.UdpListener = Network$UdpListener;
  Network.UdpSocket = Network$UdpSocket;
  Network.TcpConnection = Network$TcpConnection;
  Network.HttpServer = Network$HttpServer;
  Network.WebSocketListener = Network$WebSocketListener;
  Object.defineProperty(Network, 'UdpProxy', {
    get: Network$UdpProxy_getInstance
  });
  package$net.Network = Network;
  var package$proto = package$baaahs.proto || (package$baaahs.proto = {});
  Object.defineProperty(package$proto, 'Ports', {
    get: Ports_getInstance
  });
  Object.defineProperty(Type, 'BRAIN_HELLO', {
    get: Type$BRAIN_HELLO_getInstance
  });
  Object.defineProperty(Type, 'BRAIN_PANEL_SHADE', {
    get: Type$BRAIN_PANEL_SHADE_getInstance
  });
  Object.defineProperty(Type, 'MAPPER_HELLO', {
    get: Type$MAPPER_HELLO_getInstance
  });
  Object.defineProperty(Type, 'BRAIN_ID_REQUEST', {
    get: Type$BRAIN_ID_REQUEST_getInstance
  });
  Object.defineProperty(Type, 'BRAIN_MAPPING', {
    get: Type$BRAIN_MAPPING_getInstance
  });
  Object.defineProperty(Type, 'PING', {
    get: Type$PING_getInstance
  });
  Object.defineProperty(Type, 'USE_FIRMWARE', {
    get: Type$USE_FIRMWARE_getInstance
  });
  Object.defineProperty(Type, 'Companion', {
    get: Type$Companion_getInstance
  });
  package$proto.Type = Type;
  package$proto.parse_fqrh44$ = parse;
  Object.defineProperty(BrainHelloMessage, 'Companion', {
    get: BrainHelloMessage$Companion_getInstance
  });
  package$proto.BrainHelloMessage = BrainHelloMessage;
  Object.defineProperty(BrainShaderMessage, 'Companion', {
    get: BrainShaderMessage$Companion_getInstance
  });
  package$proto.BrainShaderMessage = BrainShaderMessage;
  Object.defineProperty(UseFirmwareMessage, 'Companion', {
    get: UseFirmwareMessage$Companion_getInstance
  });
  package$proto.UseFirmwareMessage = UseFirmwareMessage;
  Object.defineProperty(MapperHelloMessage, 'Companion', {
    get: MapperHelloMessage$Companion_getInstance
  });
  package$proto.MapperHelloMessage = MapperHelloMessage;
  Object.defineProperty(BrainIdRequest, 'Companion', {
    get: BrainIdRequest$Companion_getInstance
  });
  package$proto.BrainIdRequest = BrainIdRequest;
  Object.defineProperty(BrainMappingMessage, 'Companion', {
    get: BrainMappingMessage$Companion_getInstance
  });
  package$proto.BrainMappingMessage = BrainMappingMessage;
  Object.defineProperty(PingMessage, 'Companion', {
    get: PingMessage$Companion_getInstance
  });
  package$proto.PingMessage = PingMessage;
  package$proto.Message = Message;
  Object.defineProperty(CompositorShader, 'Companion', {
    get: CompositorShader$Companion_getInstance
  });
  CompositorShader.Buffer = CompositorShader$Buffer;
  CompositorShader.Renderer = CompositorShader$Renderer;
  var package$shaders = package$baaahs.shaders || (package$baaahs.shaders = {});
  package$shaders.CompositorShader = CompositorShader;
  Object.defineProperty(CompositingMode, 'NORMAL', {
    get: CompositingMode$NORMAL_getInstance
  });
  Object.defineProperty(CompositingMode, 'ADD', {
    get: CompositingMode$ADD_getInstance
  });
  Object.defineProperty(CompositingMode, 'Companion', {
    get: CompositingMode$Companion_getInstance
  });
  package$shaders.CompositingMode = CompositingMode;
  Object.defineProperty(GlslSandbox55301Shader, 'Companion', {
    get: GlslSandbox55301Shader$Companion_getInstance
  });
  GlslSandbox55301Shader.Buffer = GlslSandbox55301Shader$Buffer;
  GlslSandbox55301Shader.Renderer = GlslSandbox55301Shader$Renderer;
  GlslSandbox55301Shader.Vector2 = GlslSandbox55301Shader$Vector2;
  package$shaders.GlslSandbox55301Shader = GlslSandbox55301Shader;
  Object.defineProperty(GlslShader, 'Companion', {
    get: GlslShader$Companion_getInstance
  });
  GlslShader.Renderer = GlslShader$Renderer;
  GlslShader.PooledRenderer = GlslShader$PooledRenderer;
  GlslShader.Buffer = GlslShader$Buffer;
  Object.defineProperty(GlslShader$AdjustableValue$Type, 'INT', {
    get: GlslShader$AdjustableValue$Type$INT_getInstance
  });
  Object.defineProperty(GlslShader$AdjustableValue$Type, 'FLOAT', {
    get: GlslShader$AdjustableValue$Type$FLOAT_getInstance
  });
  Object.defineProperty(GlslShader$AdjustableValue$Type, 'VEC3', {
    get: GlslShader$AdjustableValue$Type$VEC3_getInstance
  });
  GlslShader$AdjustableValue.Type = GlslShader$AdjustableValue$Type;
  Object.defineProperty(GlslShader$AdjustableValue, 'Companion', {
    get: GlslShader$AdjustableValue$Companion_getInstance
  });
  GlslShader.AdjustableValue = GlslShader$AdjustableValue;
  package$shaders.GlslShader = GlslShader;
  Object.defineProperty(HeartShader, 'Companion', {
    get: HeartShader$Companion_getInstance
  });
  HeartShader.Buffer = HeartShader$Buffer;
  HeartShader.Renderer = HeartShader$Renderer;
  package$shaders.HeartShader = HeartShader;
  Object.defineProperty(PixelShader$Encoding, 'DIRECT_ARGB', {
    get: PixelShader$Encoding$DIRECT_ARGB_getInstance
  });
  Object.defineProperty(PixelShader$Encoding, 'DIRECT_RGB', {
    get: PixelShader$Encoding$DIRECT_RGB_getInstance
  });
  Object.defineProperty(PixelShader$Encoding, 'INDEXED_2', {
    get: PixelShader$Encoding$INDEXED_2_getInstance
  });
  Object.defineProperty(PixelShader$Encoding, 'INDEXED_4', {
    get: PixelShader$Encoding$INDEXED_4_getInstance
  });
  Object.defineProperty(PixelShader$Encoding, 'INDEXED_16', {
    get: PixelShader$Encoding$INDEXED_16_getInstance
  });
  Object.defineProperty(PixelShader$Encoding, 'Companion', {
    get: PixelShader$Encoding$Companion_getInstance
  });
  PixelShader.Encoding = PixelShader$Encoding;
  Object.defineProperty(PixelShader, 'Companion', {
    get: PixelShader$Companion_getInstance
  });
  PixelShader.Buffer = PixelShader$Buffer;
  PixelShader.DirectColorBuffer = PixelShader$DirectColorBuffer;
  PixelShader.IndexedBuffer = PixelShader$IndexedBuffer;
  PixelShader.Renderer = PixelShader$Renderer;
  package$shaders.PixelShader = PixelShader;
  Object.defineProperty(RandomShader, 'Companion', {
    get: RandomShader$Companion_getInstance
  });
  RandomShader.Buffer = RandomShader$Buffer;
  RandomShader.Renderer = RandomShader$Renderer;
  package$shaders.RandomShader = RandomShader;
  Object.defineProperty(SimpleSpatialShader, 'Companion', {
    get: SimpleSpatialShader$Companion_getInstance
  });
  SimpleSpatialShader.Buffer = SimpleSpatialShader$Buffer;
  SimpleSpatialShader.Renderer = SimpleSpatialShader$Renderer;
  package$shaders.SimpleSpatialShader = SimpleSpatialShader;
  Object.defineProperty(SineWaveShader, 'Companion', {
    get: SineWaveShader$Companion_getInstance
  });
  SineWaveShader.Buffer = SineWaveShader$Buffer;
  SineWaveShader.Renderer = SineWaveShader$Renderer;
  package$shaders.SineWaveShader = SineWaveShader;
  Object.defineProperty(SolidShader, 'Companion', {
    get: SolidShader$Companion_getInstance
  });
  SolidShader.Buffer = SolidShader$Buffer;
  SolidShader.Renderer = SolidShader$Renderer;
  package$shaders.SolidShader = SolidShader;
  Object.defineProperty(SparkleShader, 'Companion', {
    get: SparkleShader$Companion_getInstance
  });
  SparkleShader.Buffer = SparkleShader$Buffer;
  SparkleShader.Renderer = SparkleShader$Renderer;
  package$shaders.SparkleShader = SparkleShader;
  Object.defineProperty(AllShows, 'Companion', {
    get: AllShows$Companion_getInstance
  });
  var package$shows = package$baaahs.shows || (package$baaahs.shows = {});
  package$shows.AllShows = AllShows;
  CompositeShow.prototype.ShaderBufs = CompositeShow$ShaderBufs;
  Object.defineProperty(package$shows, 'CompositeShow', {
    get: CompositeShow_getInstance
  });
  Object.defineProperty(package$shows, 'CreepingPixelsShow', {
    get: CreepingPixelsShow_getInstance
  });
  Object.defineProperty(package$shows, 'GlslOtherShow', {
    get: GlslOtherShow_getInstance
  });
  Object.defineProperty(package$shows, 'GlslSandbox55301KotlinShow', {
    get: GlslSandbox55301KotlinShow_getInstance
  });
  Object.defineProperty(package$shows, 'GlslSandbox55301Show', {
    get: GlslSandbox55301Show_getInstance
  });
  Object.defineProperty(package$shows, 'GlslSandbox56555Show', {
    get: GlslSandbox56555Show_getInstance
  });
  Object.defineProperty(package$shows, 'GlslSandbox56594Show', {
    get: GlslSandbox56594Show_getInstance
  });
  package$shows.GlslShow = GlslShow;
  Object.defineProperty(package$shows, 'HeartbleatShow', {
    get: HeartbleatShow_getInstance
  });
  Object.defineProperty(package$shows, 'LifeyShow', {
    get: LifeyShow_getInstance
  });
  PanelTweenShow.prototype.Shaders = PanelTweenShow$Shaders;
  Object.defineProperty(package$shows, 'PanelTweenShow', {
    get: PanelTweenShow_getInstance
  });
  Object.defineProperty(package$shows, 'PixelTweenShow', {
    get: PixelTweenShow_getInstance
  });
  Object.defineProperty(package$shows, 'RandomShow', {
    get: RandomShow_getInstance
  });
  Object.defineProperty(package$shows, 'SimpleSpatialShow', {
    get: SimpleSpatialShow_getInstance
  });
  Object.defineProperty(package$shows, 'SolidColorShow', {
    get: SolidColorShow_getInstance
  });
  Object.defineProperty(package$shows, 'SomeDumbShow', {
    get: SomeDumbShow_getInstance
  });
  ThumpShow.prototype.ShaderBufs = ThumpShow$ShaderBufs;
  Object.defineProperty(package$shows, 'ThumpShow', {
    get: ThumpShow_getInstance
  });
  var package$sim = package$baaahs.sim || (package$baaahs.sim = {});
  package$sim.FakeDmxUniverse = FakeDmxUniverse;
  package$sim.FakeFs = FakeFs;
  package$sim.FakeNetwork = FakeNetwork;
  package$baaahs.random_2p1efm$ = random;
  package$baaahs.random_hhb8gh$ = random_0;
  package$baaahs.only_hxlr6s$ = only;
  package$baaahs.toRadians_mx4ult$ = toRadians;
  package$baaahs.randomDelay_za3lpa$ = randomDelay;
  Object.defineProperty(logger, 'Companion', {
    get: logger$Companion_getInstance
  });
  package$baaahs.logger = logger;
  package$baaahs.time_66u77s$ = time;
  package$baaahs.timeSync_ls4sck$ = timeSync;
  package$baaahs.JsDisplay = JsDisplay;
  package$baaahs.JsNetworkDisplay = JsNetworkDisplay;
  package$baaahs.JsPinkyDisplay = JsPinkyDisplay;
  package$baaahs.JsBrainDisplay = JsBrainDisplay;
  package$baaahs.JsVisualizerDisplay = JsVisualizerDisplay;
  JsMapperUi$VisibleSurface.VisiblePixel = JsMapperUi$VisibleSurface$VisiblePixel;
  JsMapperUi.VisibleSurface = JsMapperUi$VisibleSurface;
  Object.defineProperty(JsMapperUi$CameraOrientation, 'Companion', {
    get: JsMapperUi$CameraOrientation$Companion_getInstance
  });
  JsMapperUi.CameraOrientation = JsMapperUi$CameraOrientation;
  JsMapperUi.StatusListener = JsMapperUi$StatusListener;
  package$baaahs.JsMapperUi = JsMapperUi;
  package$baaahs.PanelInfo = PanelInfo;
  package$baaahs.Launcher = Launcher;
  Object.defineProperty(SheepSimulator, 'NullPixels', {
    get: SheepSimulator$NullPixels_getInstance
  });
  package$baaahs.SheepSimulator = SheepSimulator;
  package$baaahs.JsClock = JsClock;
  package$baaahs.BridgedBeatSource = BridgedBeatSource;
  package$baaahs.get_disabled_ejp6nk$ = get_disabled;
  package$baaahs.set_disabled_juh0kr$ = set_disabled;
  package$baaahs.forEach_dokpt5$ = forEach;
  package$baaahs.clear_u75qir$ = clear_0;
  package$baaahs.first_m814eh$ = first_1;
  package$baaahs.context2d_ng27xv$ = context2d;
  package$baaahs.HostedWebApp = HostedWebApp;
  DomContainer.Frame = DomContainer$Frame;
  package$baaahs.DomContainer = DomContainer;
  package$baaahs.FakeDomContainer = FakeDomContainer;
  Object.defineProperty(package$baaahs, 'WebUi', {
    get: WebUi_getInstance
  });
  var package$browser = package$baaahs.browser || (package$baaahs.browser = {});
  package$browser.RealMediaDevices = RealMediaDevices;
  package$geom.Vector2 = Vector2_0;
  Object.defineProperty(package$glsl, 'GlslBase', {
    get: GlslBase_getInstance
  });
  package$glsl.JsGlslManager = JsGlslManager;
  JsGlslRenderer.Quad = JsGlslRenderer$Quad;
  JsGlslRenderer.SurfacePixels = JsGlslRenderer$SurfacePixels;
  Object.defineProperty(JsGlslRenderer, 'Companion', {
    get: JsGlslRenderer$Companion_getInstance
  });
  JsGlslRenderer.UnifyingAdjustableUniform = JsGlslRenderer$UnifyingAdjustableUniform;
  JsGlslRenderer.AwesomerAdjustableUniform = JsGlslRenderer$AwesomerAdjustableUniform;
  JsGlslRenderer.Instance = JsGlslRenderer$Instance;
  package$glsl.JsGlslRenderer = JsGlslRenderer;
  package$imaging.NativeBitmap = NativeBitmap;
  package$imaging.createCanvas_vux9f0$ = createCanvas;
  package$imaging.CanvasBitmap = CanvasBitmap;
  package$imaging.JsImage = JsImage;
  package$imaging.ImageBitmapImage = ImageBitmapImage;
  package$imaging.VideoElementImage = VideoElementImage;
  package$imaging.JsUByteClampedArray = JsUByteClampedArray;
  package$baaahs.doRunBlocking_g2bo5h$ = doRunBlocking;
  package$baaahs.getResource_61zpoe$ = getResource;
  package$baaahs.getTimeMillis = getTimeMillis;
  package$baaahs.decodeBase64_61zpoe$ = decodeBase64;
  BrowserNetwork.BrowserAddress = BrowserNetwork$BrowserAddress;
  package$net.BrowserNetwork = BrowserNetwork;
  BrowserUdpProxy.UdpSocketProxy = BrowserUdpProxy$UdpSocketProxy;
  package$net.BrowserUdpProxy = BrowserUdpProxy;
  FakeMediaDevices.FakeCamera = FakeMediaDevices$FakeCamera;
  package$sim.FakeMediaDevices = FakeMediaDevices;
  _.decodeQueryParams_h13imq$ = decodeQueryParams;
  _.decodeHashParams_h13imq$ = decodeHashParams;
  _.decodeQueryParams_pdl1vz$ = decodeQueryParams_0;
  SwirlyPixelArranger.PanelArranger = SwirlyPixelArranger$PanelArranger;
  var package$visualizer = package$baaahs.visualizer || (package$baaahs.visualizer = {});
  package$visualizer.SwirlyPixelArranger = SwirlyPixelArranger;
  Visualizer.VizMovingHead = Visualizer$VizMovingHead;
  Visualizer.FrameListener = Visualizer$FrameListener;
  package$visualizer.Visualizer = Visualizer;
  Object.defineProperty(VizPanel, 'Companion', {
    get: VizPanel$Companion_getInstance
  });
  VizPanel.Point2 = VizPanel$Point2;
  VizPanel.VizPixels = VizPanel$VizPixels;
  package$visualizer.VizPanel = VizPanel;
  package$visualizer.segments_182k4$ = segments;
  package$visualizer.asKey_eko7cz$ = asKey;
  package$visualizer.Rotator = Rotator;
  BeatData$$serializer.prototype.patch_mynpiu$ = GeneratedSerializer.prototype.patch_mynpiu$;
  Color$Companion.prototype.patch_mynpiu$ = KSerializer.prototype.patch_mynpiu$;
  GadgetData$$serializer.prototype.patch_mynpiu$ = GeneratedSerializer.prototype.patch_mynpiu$;
  MovingHead$MovingHeadPosition$$serializer.prototype.patch_mynpiu$ = GeneratedSerializer.prototype.patch_mynpiu$;
  MovingHead$$serializer.prototype.patch_mynpiu$ = GeneratedSerializer.prototype.patch_mynpiu$;
  Object.defineProperty(Pinky$PixelsAdapter.prototype, 'indices', Object.getOwnPropertyDescriptor(Pixels.prototype, 'indices'));
  Pinky$PixelsAdapter.prototype.finishedFrame = Pixels.prototype.finishedFrame;
  Pinky$PixelsAdapter.prototype.iterator = Pixels.prototype.iterator;
  SheepModel$Point$$serializer.prototype.patch_mynpiu$ = GeneratedSerializer.prototype.patch_mynpiu$;
  Object.defineProperty(LixadaMiniMovingHead.prototype, 'supportsFinePositioning', Object.getOwnPropertyDescriptor(MovingHead$Buffer.prototype, 'supportsFinePositioning'));
  Object.defineProperty(LixadaMiniMovingHead.prototype, 'pan', Object.getOwnPropertyDescriptor(MovingHead$Buffer.prototype, 'pan'));
  Object.defineProperty(LixadaMiniMovingHead.prototype, 'tilt', Object.getOwnPropertyDescriptor(MovingHead$Buffer.prototype, 'tilt'));
  Object.defineProperty(LixadaMiniMovingHead.prototype, 'dimmer', Object.getOwnPropertyDescriptor(MovingHead$Buffer.prototype, 'dimmer'));
  LixadaMiniMovingHead.prototype.closestColorFor_rny0jj$ = MovingHead$Buffer.prototype.closestColorFor_rny0jj$;
  LixadaMiniMovingHead.prototype.getFloat_b37jry$_0 = MovingHead$Buffer.prototype.getFloat_b37jry$_0;
  LixadaMiniMovingHead.prototype.getFloat_gej297$_0 = MovingHead$Buffer.prototype.getFloat_gej297$_0;
  LixadaMiniMovingHead.prototype.setFloat_vl9zqr$_0 = MovingHead$Buffer.prototype.setFloat_vl9zqr$_0;
  LixadaMiniMovingHead.prototype.setFloat_8xjzlm$_0 = MovingHead$Buffer.prototype.setFloat_8xjzlm$_0;
  Object.defineProperty(Shenzarpy.prototype, 'supportsFinePositioning', Object.getOwnPropertyDescriptor(MovingHead$Buffer.prototype, 'supportsFinePositioning'));
  Object.defineProperty(Shenzarpy.prototype, 'pan', Object.getOwnPropertyDescriptor(MovingHead$Buffer.prototype, 'pan'));
  Object.defineProperty(Shenzarpy.prototype, 'tilt', Object.getOwnPropertyDescriptor(MovingHead$Buffer.prototype, 'tilt'));
  Object.defineProperty(Shenzarpy.prototype, 'dimmer', Object.getOwnPropertyDescriptor(MovingHead$Buffer.prototype, 'dimmer'));
  Shenzarpy.prototype.closestColorFor_rny0jj$ = MovingHead$Buffer.prototype.closestColorFor_rny0jj$;
  Shenzarpy.prototype.getFloat_b37jry$_0 = MovingHead$Buffer.prototype.getFloat_b37jry$_0;
  Shenzarpy.prototype.getFloat_gej297$_0 = MovingHead$Buffer.prototype.getFloat_gej297$_0;
  Shenzarpy.prototype.setFloat_vl9zqr$_0 = MovingHead$Buffer.prototype.setFloat_vl9zqr$_0;
  Shenzarpy.prototype.setFloat_8xjzlm$_0 = MovingHead$Buffer.prototype.setFloat_8xjzlm$_0;
  ColorPicker$$serializer.prototype.patch_mynpiu$ = GeneratedSerializer.prototype.patch_mynpiu$;
  PalettePicker$$serializer.prototype.patch_mynpiu$ = GeneratedSerializer.prototype.patch_mynpiu$;
  Slider$$serializer.prototype.patch_mynpiu$ = GeneratedSerializer.prototype.patch_mynpiu$;
  Matrix4$$serializer.prototype.patch_mynpiu$ = GeneratedSerializer.prototype.patch_mynpiu$;
  Vector2F$$serializer.prototype.patch_mynpiu$ = GeneratedSerializer.prototype.patch_mynpiu$;
  Vector3F$$serializer.prototype.patch_mynpiu$ = GeneratedSerializer.prototype.patch_mynpiu$;
  Object.defineProperty(SurfacePixels.prototype, 'indices', Object.getOwnPropertyDescriptor(Pixels.prototype, 'indices'));
  SurfacePixels.prototype.finishedFrame = Pixels.prototype.finishedFrame;
  SurfacePixels.prototype.iterator = Pixels.prototype.iterator;
  DateTimeSerializer.prototype.patch_mynpiu$ = KSerializer.prototype.patch_mynpiu$;
  MappingSession$SurfaceData$PixelData$$serializer.prototype.patch_mynpiu$ = GeneratedSerializer.prototype.patch_mynpiu$;
  MappingSession$SurfaceData$$serializer.prototype.patch_mynpiu$ = GeneratedSerializer.prototype.patch_mynpiu$;
  MappingSession$$serializer.prototype.patch_mynpiu$ = GeneratedSerializer.prototype.patch_mynpiu$;
  FragmentingUdpLink$FragmentingUdpSocket.prototype.sendUdp_wpmaqi$ = Network$UdpSocket.prototype.sendUdp_wpmaqi$;
  FragmentingUdpLink$FragmentingUdpSocket.prototype.broadcastUdp_68hu5j$ = Network$UdpSocket.prototype.broadcastUdp_68hu5j$;
  GlslSandbox55301Shader$Renderer.prototype.beginFrame_b23bvv$ = Shader$Renderer.prototype.beginFrame_b23bvv$;
  GlslSandbox55301Shader$Renderer.prototype.endFrame = Shader$Renderer.prototype.endFrame;
  GlslShader$Renderer.prototype.endFrame = Shader$Renderer.prototype.endFrame;
  HeartShader$Renderer.prototype.beginFrame_b23bvv$ = Shader$Renderer.prototype.beginFrame_b23bvv$;
  HeartShader$Renderer.prototype.endFrame = Shader$Renderer.prototype.endFrame;
  PixelShader$Renderer.prototype.beginFrame_b23bvv$ = Shader$Renderer.prototype.beginFrame_b23bvv$;
  PixelShader$Renderer.prototype.endFrame = Shader$Renderer.prototype.endFrame;
  RandomShader$Renderer.prototype.beginFrame_b23bvv$ = Shader$Renderer.prototype.beginFrame_b23bvv$;
  RandomShader$Renderer.prototype.endFrame = Shader$Renderer.prototype.endFrame;
  SimpleSpatialShader$Renderer.prototype.beginFrame_b23bvv$ = Shader$Renderer.prototype.beginFrame_b23bvv$;
  SimpleSpatialShader$Renderer.prototype.endFrame = Shader$Renderer.prototype.endFrame;
  SineWaveShader$Renderer.prototype.endFrame = Shader$Renderer.prototype.endFrame;
  SolidShader$Renderer.prototype.beginFrame_b23bvv$ = Shader$Renderer.prototype.beginFrame_b23bvv$;
  SolidShader$Renderer.prototype.endFrame = Shader$Renderer.prototype.endFrame;
  SparkleShader$Renderer.prototype.beginFrame_b23bvv$ = Shader$Renderer.prototype.beginFrame_b23bvv$;
  SparkleShader$Renderer.prototype.endFrame = Shader$Renderer.prototype.endFrame;
  CreepingPixelsShow$createRenderer$ObjectLiteral.prototype.surfacesChanged_yroyvo$ = Show$Renderer.prototype.surfacesChanged_yroyvo$;
  GlslSandbox55301KotlinShow$createRenderer$ObjectLiteral.prototype.surfacesChanged_yroyvo$ = Show$Renderer.prototype.surfacesChanged_yroyvo$;
  GlslShow$createRenderer$ObjectLiteral.prototype.surfacesChanged_yroyvo$ = Show$Renderer.prototype.surfacesChanged_yroyvo$;
  HeartbleatShow$createRenderer$ObjectLiteral.prototype.surfacesChanged_yroyvo$ = Show$Renderer.prototype.surfacesChanged_yroyvo$;
  LifeyShow$createRenderer$ObjectLiteral.prototype.surfacesChanged_yroyvo$ = Show$Renderer.prototype.surfacesChanged_yroyvo$;
  PanelTweenShow$createRenderer$ObjectLiteral.prototype.surfacesChanged_yroyvo$ = Show$Renderer.prototype.surfacesChanged_yroyvo$;
  PixelTweenShow$createRenderer$ObjectLiteral.prototype.surfacesChanged_yroyvo$ = Show$Renderer.prototype.surfacesChanged_yroyvo$;
  RandomShow$createRenderer$ObjectLiteral.prototype.surfacesChanged_yroyvo$ = Show$Renderer.prototype.surfacesChanged_yroyvo$;
  SimpleSpatialShow$createRenderer$ObjectLiteral.prototype.surfacesChanged_yroyvo$ = Show$Renderer.prototype.surfacesChanged_yroyvo$;
  SolidColorShow$createRenderer$ObjectLiteral.prototype.surfacesChanged_yroyvo$ = Show$Renderer.prototype.surfacesChanged_yroyvo$;
  SomeDumbShow$createRenderer$ObjectLiteral.prototype.surfacesChanged_yroyvo$ = Show$Renderer.prototype.surfacesChanged_yroyvo$;
  ThumpShow$createRenderer$ObjectLiteral.prototype.surfacesChanged_yroyvo$ = Show$Renderer.prototype.surfacesChanged_yroyvo$;
  FakeFs.prototype.createFile_7x97xx$ = Fs.prototype.createFile_7x97xx$;
  FakeFs.prototype.createFile_qz9155$ = Fs.prototype.createFile_qz9155$;
  FakeNetwork$FakeLink$FakeTcpConnection.prototype.send_chrig3$ = Network$TcpConnection.prototype.send_chrig3$;
  FakeNetwork$FakeLink$FakeUdpSocket.prototype.sendUdp_wpmaqi$ = Network$UdpSocket.prototype.sendUdp_wpmaqi$;
  FakeNetwork$FakeLink$FakeUdpSocket.prototype.broadcastUdp_68hu5j$ = Network$UdpSocket.prototype.broadcastUdp_68hu5j$;
  FakeNetwork$FakeLink$FakeHttpServer.prototype.listenWebSocket_w9i1ik$ = Network$HttpServer.prototype.listenWebSocket_w9i1ik$;
  JsMapperUi.prototype.showCamImage_q5ica7$ = MapperUi.prototype.showCamImage_q5ica7$;
  JsMapperUi.prototype.showDiffImage_oa2j07$ = MapperUi.prototype.showDiffImage_oa2j07$;
  Object.defineProperty(SheepSimulator$NullPixels.prototype, 'indices', Object.getOwnPropertyDescriptor(Pixels.prototype, 'indices'));
  SheepSimulator$NullPixels.prototype.finishedFrame = Pixels.prototype.finishedFrame;
  SheepSimulator$NullPixels.prototype.iterator = Pixels.prototype.iterator;
  CanvasBitmap.prototype.withData_u0v8ny$ = Bitmap.prototype.withData_u0v8ny$;
  BrowserNetwork$link$ObjectLiteral$connectWebSocket$ObjectLiteral.prototype.send_chrig3$ = Network$TcpConnection.prototype.send_chrig3$;
  BrowserUdpProxy$UdpSocketProxy.prototype.sendUdp_wpmaqi$ = Network$UdpSocket.prototype.sendUdp_wpmaqi$;
  BrowserUdpProxy$UdpSocketProxy.prototype.broadcastUdp_68hu5j$ = Network$UdpSocket.prototype.broadcastUdp_68hu5j$;
  Object.defineProperty(VizPanel$VizPixels.prototype, 'indices', Object.getOwnPropertyDescriptor(Pixels.prototype, 'indices'));
  VizPanel$VizPixels.prototype.finishedFrame = Pixels.prototype.finishedFrame;
  VizPanel$VizPixels.prototype.iterator = Pixels.prototype.iterator;
  GadgetDataSerializer = get_map(to(serializer(kotlin_js_internal_StringCompanionObject), JsonElement.Companion.serializer()));
  gadgetModule = SerializersModule(gadgetModule$lambda);
  jsonParser = new Json(JsonConfiguration.Companion.Stable);
  Kotlin.defineModule('sparklemotion', _);
  return _;
}));

//# sourceMappingURL=sparklemotion.js.map
