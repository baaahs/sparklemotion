(function (root, factory) {
  if (typeof define === 'function' && define.amd)
    define(['exports', 'kotlin', 'kotlinx-coroutines-core', 'kotlinx-serialization-runtime', 'threejs-wrapper', 'kotlinx-html-js'], factory);
  else if (typeof exports === 'object')
    factory(module.exports, require('kotlin'), require('kotlinx-coroutines-core'), require('kotlinx-serialization-runtime'), require('threejs-wrapper'), require('kotlinx-html-js'));
  else {
    if (typeof kotlin === 'undefined') {
      throw new Error("Error loading module 'sparklemotion'. Its dependency 'kotlin' was not found. Please, check whether 'kotlin' is loaded prior to 'sparklemotion'.");
    }
    if (typeof this['kotlinx-coroutines-core'] === 'undefined') {
      throw new Error("Error loading module 'sparklemotion'. Its dependency 'kotlinx-coroutines-core' was not found. Please, check whether 'kotlinx-coroutines-core' is loaded prior to 'sparklemotion'.");
    }
    if (typeof this['kotlinx-serialization-runtime'] === 'undefined') {
      throw new Error("Error loading module 'sparklemotion'. Its dependency 'kotlinx-serialization-runtime' was not found. Please, check whether 'kotlinx-serialization-runtime' is loaded prior to 'sparklemotion'.");
    }
    if (typeof this['threejs-wrapper'] === 'undefined') {
      throw new Error("Error loading module 'sparklemotion'. Its dependency 'threejs-wrapper' was not found. Please, check whether 'threejs-wrapper' is loaded prior to 'sparklemotion'.");
    }
    if (typeof this['kotlinx-html-js'] === 'undefined') {
      throw new Error("Error loading module 'sparklemotion'. Its dependency 'kotlinx-html-js' was not found. Please, check whether 'kotlinx-html-js' is loaded prior to 'sparklemotion'.");
    }
    root.sparklemotion = factory(typeof sparklemotion === 'undefined' ? {} : sparklemotion, kotlin, this['kotlinx-coroutines-core'], this['kotlinx-serialization-runtime'], this['threejs-wrapper'], this['kotlinx-html-js']);
  }
}(this, function (_, Kotlin, $module$kotlinx_coroutines_core, $module$kotlinx_serialization_runtime, $module$threejs_wrapper, $module$kotlinx_html_js) {
  'use strict';
  var throwUPAE = Kotlin.throwUPAE;
  var COROUTINE_SUSPENDED = Kotlin.kotlin.coroutines.intrinsics.COROUTINE_SUSPENDED;
  var CoroutineImpl = Kotlin.kotlin.coroutines.CoroutineImpl;
  var println = Kotlin.kotlin.io.println_s8jyv4$;
  var L0 = Kotlin.Long.ZERO;
  var equals = Kotlin.equals;
  var L5000 = Kotlin.Long.fromInt(5000);
  var delay = $module$kotlinx_coroutines_core.kotlinx.coroutines.delay_s8cxhz$;
  var contentEquals = Kotlin.arrayEquals;
  var throwCCE = Kotlin.throwCCE;
  var ensureNotNull = Kotlin.ensureNotNull;
  var Kind_CLASS = Kotlin.Kind.CLASS;
  var Exception_init = Kotlin.kotlin.Exception_init_pdl1vj$;
  var toString = Kotlin.kotlin.text.toString_dqglrj$;
  var Random = Kotlin.kotlin.random.Random;
  var trimStart = Kotlin.kotlin.text.trimStart_wqw3xr$;
  var toInt = Kotlin.kotlin.text.toInt_6ic1pp$;
  var IllegalArgumentException_init = Kotlin.kotlin.IllegalArgumentException_init_pdl1vj$;
  var numberToInt = Kotlin.numberToInt;
  var internal = $module$kotlinx_serialization_runtime.kotlinx.serialization.internal;
  var withName = $module$kotlinx_serialization_runtime.kotlinx.serialization.withName_8new1j$;
  var Kind_OBJECT = Kotlin.Kind.OBJECT;
  var KSerializer = $module$kotlinx_serialization_runtime.kotlinx.serialization.KSerializer;
  var Math_0 = Math;
  var Pair = Kotlin.kotlin.Pair;
  var mapOf = Kotlin.kotlin.collections.mapOf_qfcya0$;
  var Kind_INTERFACE = Kotlin.Kind.INTERFACE;
  var Unit = Kotlin.kotlin.Unit;
  var IllegalStateException_init = Kotlin.kotlin.IllegalStateException_init_pdl1vj$;
  var ReadWriteProperty = Kotlin.kotlin.properties.ReadWriteProperty;
  var SerialClassDescImpl = $module$kotlinx_serialization_runtime.kotlinx.serialization.internal.SerialClassDescImpl;
  var getKClass = Kotlin.getKClass;
  var PolymorphicSerializer = $module$kotlinx_serialization_runtime.kotlinx.serialization.PolymorphicSerializer;
  var UnknownFieldException = $module$kotlinx_serialization_runtime.kotlinx.serialization.UnknownFieldException;
  var GeneratedSerializer = $module$kotlinx_serialization_runtime.kotlinx.serialization.internal.GeneratedSerializer;
  var MissingFieldException = $module$kotlinx_serialization_runtime.kotlinx.serialization.MissingFieldException;
  var kotlin_js_internal_StringCompanionObject = Kotlin.kotlin.js.internal.StringCompanionObject;
  var serializer = $module$kotlinx_serialization_runtime.kotlinx.serialization.serializer_6eet4j$;
  var JsonElement = $module$kotlinx_serialization_runtime.kotlinx.serialization.json.JsonElement;
  var to = Kotlin.kotlin.to_ujzrz7$;
  var get_map = $module$kotlinx_serialization_runtime.kotlinx.serialization.get_map_kgqhr1$;
  var SerializersModule = $module$kotlinx_serialization_runtime.kotlinx.serialization.modules.SerializersModule_q4tcel$;
  var JsonConfiguration = $module$kotlinx_serialization_runtime.kotlinx.serialization.json.JsonConfiguration;
  var Json = $module$kotlinx_serialization_runtime.kotlinx.serialization.json.Json;
  var ReferenceArraySerializer = $module$kotlinx_serialization_runtime.kotlinx.serialization.internal.ReferenceArraySerializer;
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
  var launch = $module$kotlinx_coroutines_core.kotlinx.coroutines.launch_s496o7$;
  var L1000 = Kotlin.Long.fromInt(1000);
  var L250 = Kotlin.Long.fromInt(250);
  var L500 = Kotlin.Long.fromInt(500);
  var L34 = Kotlin.Long.fromInt(34);
  var L10 = Kotlin.Long.fromInt(10);
  var first = Kotlin.kotlin.collections.first_2p1efm$;
  var CoroutineName = $module$kotlinx_coroutines_core.kotlinx.coroutines.CoroutineName;
  var L10000 = Kotlin.Long.fromInt(10000);
  var L100 = Kotlin.Long.fromInt(100);
  var MainScope = $module$kotlinx_coroutines_core.kotlinx.coroutines.MainScope;
  var IntRange = Kotlin.kotlin.ranges.IntRange;
  var CoroutineScope = $module$kotlinx_coroutines_core.kotlinx.coroutines.CoroutineScope;
  var ArrayList_init_0 = Kotlin.kotlin.collections.ArrayList_init_ww73n8$;
  var coroutines = $module$kotlinx_coroutines_core.kotlinx.coroutines;
  var L50 = Kotlin.Long.fromInt(50);
  var emptyList = Kotlin.kotlin.collections.emptyList_287e2$;
  var plus = $module$kotlinx_serialization_runtime.kotlinx.serialization.modules.plus_7n7cf$;
  var modules = $module$kotlinx_serialization_runtime.kotlinx.serialization.modules;
  var NotImplementedError_init = Kotlin.kotlin.NotImplementedError;
  var Enum = Kotlin.kotlin.Enum;
  var throwISE = Kotlin.throwISE;
  var toByte = Kotlin.toByte;
  var lazy = Kotlin.kotlin.lazy_klfg04$;
  var Iterator = Kotlin.kotlin.collections.Iterator;
  var Iterable = Kotlin.kotlin.collections.Iterable;
  var Regex_init = Kotlin.kotlin.text.Regex_init_61zpoe$;
  var split = Kotlin.kotlin.text.split_ip8yn$;
  var joinToString = Kotlin.kotlin.collections.joinToString_fmv235$;
  var toInt_0 = Kotlin.kotlin.text.toInt_pdl1vz$;
  var sorted = Kotlin.kotlin.collections.sorted_exjks8$;
  var toList = Kotlin.kotlin.collections.toList_7wnvza$;
  var arrayListOf = Kotlin.kotlin.collections.arrayListOf_i5x0yv$;
  var hashCode = Kotlin.hashCode;
  var trim = Kotlin.kotlin.text.trim_gw00vp$;
  var toDouble = Kotlin.kotlin.text.toDouble_pdl1vz$;
  var addAll = Kotlin.kotlin.collections.addAll_ipc267$;
  var rangeTo = Kotlin.kotlin.ranges.rangeTo_38ydlf$;
  var Exception_init_0 = Kotlin.kotlin.Exception_init;
  var Exception = Kotlin.kotlin.Exception;
  var minus = Kotlin.kotlin.collections.minus_q4559j$;
  var ArrayList_init_1 = Kotlin.kotlin.collections.ArrayList_init_mqih57$;
  var toList_0 = Kotlin.kotlin.collections.toList_abgq59$;
  var get_list = $module$kotlinx_serialization_runtime.kotlinx.serialization.get_list_gekvwj$;
  var PropertyMetadata = Kotlin.PropertyMetadata;
  var ArrayListSerializer = $module$kotlinx_serialization_runtime.kotlinx.serialization.internal.ArrayListSerializer;
  var kotlin_js_internal_FloatCompanionObject = Kotlin.kotlin.js.internal.FloatCompanionObject;
  var serializer_0 = $module$kotlinx_serialization_runtime.kotlinx.serialization.serializer_y9phqa$;
  var toShort = Kotlin.toShort;
  var toChar = Kotlin.toChar;
  var toBoxedChar = Kotlin.toBoxedChar;
  var StringBuilder_init = Kotlin.kotlin.text.StringBuilder_init_za3lpa$;
  var unboxChar = Kotlin.unboxChar;
  var copyOfRange = Kotlin.kotlin.collections.copyOfRange_ietg8x$;
  var toBits = Kotlin.floatToBits;
  var get_indices = Kotlin.kotlin.text.get_indices_gw00vp$;
  var copyOf = Kotlin.kotlin.collections.copyOf_mrm5p$;
  var arrayCopy = Kotlin.kotlin.collections.arrayCopy;
  var removeAll = Kotlin.kotlin.collections.removeAll_qafx1e$;
  var UnsupportedOperationException_init = Kotlin.kotlin.UnsupportedOperationException_init_pdl1vj$;
  var until = Kotlin.kotlin.ranges.until_dqglrj$;
  var math = Kotlin.kotlin.math;
  var Array_0 = Array;
  var listOf = Kotlin.kotlin.collections.listOf_i5x0yv$;
  var toMutableMap = Kotlin.kotlin.collections.toMutableMap_abgq59$;
  var L268435455 = Kotlin.Long.fromInt(268435455);
  var checkIndexOverflow = Kotlin.kotlin.collections.checkIndexOverflow_za3lpa$;
  var Random_0 = Kotlin.kotlin.random.Random_za3lpa$;
  var coroutines_0 = Kotlin.kotlin.coroutines;
  var clear = Kotlin.kotlin.dom.clear_asww5s$;
  var appendText = Kotlin.kotlin.dom.appendText_46n0ku$;
  var appendElement = Kotlin.kotlin.dom.appendElement_ldvnw0$;
  var addClass = Kotlin.kotlin.dom.addClass_hhb33f$;
  var LineBasicMaterial = THREE.LineBasicMaterial;
  var Color_init = THREE.Color;
  var Vector3 = THREE.Vector3;
  var Object3D = THREE.Object3D;
  var Geometry = THREE.Geometry;
  var Face3_init = THREE.Face3;
  var MeshBasicMaterial = THREE.MeshBasicMaterial;
  var Mesh_init = THREE.Mesh;
  var BufferGeometry = THREE.BufferGeometry;
  var plus_0 = $module$threejs_wrapper.info.laht.threekt.math.plus_gulir3$;
  var Line_init = THREE.Line;
  var SphereBufferGeometry = THREE.SphereBufferGeometry;
  var Box3 = THREE.Box3;
  var th = $module$kotlinx_html_js.kotlinx.html.th_bncpyi$;
  var tr = $module$kotlinx_html_js.kotlinx.html.tr_7wec05$;
  var td = $module$kotlinx_html_js.kotlinx.html.td_vlzo05$;
  var table = $module$kotlinx_html_js.kotlinx.html.js.table_uk5qws$;
  var append = $module$kotlinx_html_js.kotlinx.html.dom.append_k9bwru$;
  var roundToInt = Kotlin.kotlin.math.roundToInt_yrwdxr$;
  var Clock = THREE.Clock;
  var WebGLRenderer_init = THREE.WebGLRenderer;
  var Scene = THREE.Scene;
  var PerspectiveCamera_init = THREE.PerspectiveCamera;
  var get_create = $module$kotlinx_html_js.kotlinx.html.dom.get_create_4wc2mh$;
  var set_onClickFunction = $module$kotlinx_html_js.kotlinx.html.js.set_onClickFunction_pszlq2$;
  var button = $module$kotlinx_html_js.kotlinx.html.button_i4xb7r$;
  var i = $module$kotlinx_html_js.kotlinx.html.i_5g1p9k$;
  var div = $module$kotlinx_html_js.kotlinx.html.div_ri36nr$;
  var canvas = $module$kotlinx_html_js.kotlinx.html.canvas_dwb9fz$;
  var div_0 = $module$kotlinx_html_js.kotlinx.html.div_59el9d$;
  var sortedWith = Kotlin.kotlin.collections.sortedWith_eknfly$;
  var wrapFunction = Kotlin.wrapFunction;
  var Comparator = Kotlin.kotlin.Comparator;
  var getPropertyCallableRef = Kotlin.getPropertyCallableRef;
  var L200000 = Kotlin.Long.fromInt(200000);
  var CoroutineScope_0 = $module$kotlinx_coroutines_core.kotlinx.coroutines.CoroutineScope_1fupul$;
  var Vector2 = THREE.Vector2;
  var canvas_0 = $module$kotlinx_html_js.kotlinx.html.js.canvas_o2d15m$;
  var promise = $module$kotlinx_coroutines_core.kotlinx.coroutines.promise_pda6u4$;
  var toTypedArray = Kotlin.kotlin.collections.toTypedArray_964n91$;
  var startsWith = Kotlin.kotlin.text.startsWith_7epoxm$;
  var toMap = Kotlin.kotlin.collections.toMap_6hr0sd$;
  var Quaternion = THREE.Quaternion;
  var Matrix4_init = THREE.Matrix4;
  var Line3 = THREE.Line3;
  var ConeBufferGeometry = THREE.ConeBufferGeometry;
  var Points = THREE.Points;
  var OrbitControls = THREE.OrbitControls;
  var PointsMaterial = THREE.PointsMaterial;
  var Raycaster_init = THREE.Raycaster;
  var minus_0 = $module$threejs_wrapper.info.laht.threekt.math.minus_gulir3$;
  var Float32BufferAttribute = THREE.Float32BufferAttribute;
  var Triangle = THREE.Triangle;
  var indexOf = Kotlin.kotlin.collections.indexOf_mjy6jw$;
  var sorted_0 = Kotlin.kotlin.collections.sorted_pbinho$;
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
  Shenzarpy$WheelColor.prototype = Object.create(Enum.prototype);
  Shenzarpy$WheelColor.prototype.constructor = Shenzarpy$WheelColor;
  Shenzarpy$Channel.prototype = Object.create(Enum.prototype);
  Shenzarpy$Channel.prototype.constructor = Shenzarpy$Channel;
  Shenzarpy.prototype = Object.create(Dmx$DeviceType.prototype);
  Shenzarpy.prototype.constructor = Shenzarpy;
  Show$RestartShowException.prototype = Object.create(Exception.prototype);
  Show$RestartShowException.prototype.constructor = Show$RestartShowException;
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
  MapperHelloMessage.prototype = Object.create(Message.prototype);
  MapperHelloMessage.prototype.constructor = MapperHelloMessage;
  BrainIdRequest.prototype = Object.create(Message.prototype);
  BrainIdRequest.prototype.constructor = BrainIdRequest;
  BrainIdResponse.prototype = Object.create(Message.prototype);
  BrainIdResponse.prototype.constructor = BrainIdResponse;
  BrainMappingMessage.prototype = Object.create(Message.prototype);
  BrainMappingMessage.prototype.constructor = BrainMappingMessage;
  PinkyPongMessage.prototype = Object.create(Message.prototype);
  PinkyPongMessage.prototype.constructor = PinkyPongMessage;
  CompositorShader.prototype = Object.create(Shader.prototype);
  CompositorShader.prototype.constructor = CompositorShader;
  CompositingMode.prototype = Object.create(Enum.prototype);
  CompositingMode.prototype.constructor = CompositingMode;
  CompositingMode$NORMAL.prototype = Object.create(CompositingMode.prototype);
  CompositingMode$NORMAL.prototype.constructor = CompositingMode$NORMAL;
  CompositingMode$ADD.prototype = Object.create(CompositingMode.prototype);
  CompositingMode$ADD.prototype.constructor = CompositingMode$ADD;
  HeartShader.prototype = Object.create(Shader.prototype);
  HeartShader.prototype.constructor = HeartShader;
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
  NativeBitmap.prototype = Object.create(CanvasBitmap.prototype);
  NativeBitmap.prototype.constructor = NativeBitmap;
  CanvasBitmap$asImage$ObjectLiteral.prototype = Object.create(JsImage.prototype);
  CanvasBitmap$asImage$ObjectLiteral.prototype.constructor = CanvasBitmap$asImage$ObjectLiteral;
  ImageBitmapImage.prototype = Object.create(JsImage.prototype);
  ImageBitmapImage.prototype.constructor = ImageBitmapImage;
  function Brain(id, network, display, pixels) {
    this.id = id;
    this.network_0 = network;
    this.display_0 = display;
    this.pixels_0 = pixels;
    this.link_q2tdi4$_0 = this.link_q2tdi4$_0;
    this.lastInstructionsReceivedAtMs_0 = L0;
    this.surfaceName_0 = null;
    this.surface_6p23av$_0 = new Brain$UnmappedSurface(this);
    this.currentShaderDesc_0 = null;
    this.currentShaderBits_0 = null;
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
            this.$this.link_0.listenUdp_a6m852$(8003, this.$this);
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
            this.$this.surface_0 = new Brain$UnmappedSurface(this.$this);
            this.$this.currentShaderDesc_0 = null;
            this.$this.currentShaderBits_0 = null;
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
              this.$this.link_0.broadcastUdp_68hu5j$(8002, new BrainHelloMessage(this.$this.id, this.$this.surfaceName_0));
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
  Brain.prototype.receive_rq4egf$ = function (fromAddress, bytes) {
    var tmp$, tmp$_0;
    var now = getTimeMillis();
    this.lastInstructionsReceivedAtMs_0 = now;
    var reader = new ByteArrayReader(bytes);
    var type = Type$Companion_getInstance().get_s8j3t7$(reader.readByte());
    switch (type.name) {
      case 'BRAIN_PANEL_SHADE':
        var shaderDesc = reader.readBytes();
        var theCurrentShaderDesc = this.currentShaderDesc_0;
        if (theCurrentShaderDesc == null || !contentEquals(theCurrentShaderDesc, shaderDesc)) {
          this.currentShaderDesc_0 = shaderDesc;
          var shader = Kotlin.isType(tmp$ = Shader$Companion_getInstance().parse_100t80$(new ByteArrayReader(shaderDesc)), Shader) ? tmp$ : throwCCE();
          this.currentShaderBits_0 = new Brain$ShaderBits(shader, shader.createRenderer_ppt8xj$(this.surface_0), shader.createBuffer_ppt8xj$(this.surface_0));
        }

        var $receiver = ensureNotNull(this.currentShaderBits_0);
        $receiver.read_100t80$(reader);
        $receiver.draw_bbfl1t$(this.pixels_0);
        break;
      case 'BRAIN_ID_REQUEST':
        var message = BrainIdRequest$Companion_getInstance().parse_100t80$(reader);
        this.link_0.sendUdp_wpmaqi$(fromAddress, message.port, new BrainIdResponse(this.id, this.surfaceName_0));
        break;
      case 'BRAIN_MAPPING':
        var message_0 = BrainMappingMessage$Companion_getInstance().parse_100t80$(reader);
        this.surfaceName_0 = message_0.surfaceName;
        if (message_0.surfaceName != null) {
          tmp$_0 = new Brain$MappedSurface(this, message_0.pixelCount, message_0.pixelVertices, message_0.surfaceName);
        }
         else {
          tmp$_0 = new Brain$UnmappedSurface(this);
        }

        this.surface_0 = tmp$_0;
        this.currentShaderDesc_0 = null;
        this.currentShaderBits_0 = null;
        this.link_0.broadcastUdp_68hu5j$(8002, new BrainHelloMessage(this.id, this.surfaceName_0));
        break;
      default:break;
    }
  };
  function Brain$ShaderBits(shader, renderer, buffer) {
    this.shader = shader;
    this.renderer = renderer;
    this.buffer = buffer;
  }
  Brain$ShaderBits.prototype.read_100t80$ = function (reader) {
    this.buffer.read_100t80$(reader);
  };
  Brain$ShaderBits.prototype.draw_bbfl1t$ = function (pixels) {
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
  };
  Brain$ShaderBits.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'ShaderBits',
    interfaces: []
  };
  function Brain$UnmappedSurface($outer) {
    this.$outer = $outer;
    this.pixelCount_k16s1k$_0 = 2048;
  }
  Object.defineProperty(Brain$UnmappedSurface.prototype, 'pixelCount', {
    get: function () {
      return this.pixelCount_k16s1k$_0;
    }
  });
  Brain$UnmappedSurface.prototype.describe = function () {
    return 'unmapped';
  };
  Brain$UnmappedSurface.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'UnmappedSurface',
    interfaces: [Surface]
  };
  function Brain$MappedSurface($outer, pixelCount, pixelVertices, name) {
    this.$outer = $outer;
    if (pixelVertices === void 0)
      pixelVertices = null;
    this.pixelCount_vi6r5t$_0 = pixelCount;
    this.pixelVertices = pixelVertices;
    this.name_0 = name;
  }
  Object.defineProperty(Brain$MappedSurface.prototype, 'pixelCount', {
    get: function () {
      return this.pixelCount_vi6r5t$_0;
    }
  });
  Brain$MappedSurface.prototype.describe = function () {
    return this.name_0;
  };
  Brain$MappedSurface.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'MappedSurface',
    interfaces: [Surface]
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
    this.BLUE = Color_init_1(0, 0, 255);
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
    var hexDigits = trimStart(hex, Kotlin.charArrayOf(35));
    if (hexDigits.length === 6) {
      var l = -16777216;
      return new Color(l | toInt(hexDigits, 16));
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
  Color$Companion.prototype.bounded_0 = function (i) {
    var b = Math_0.min(255, i);
    return Math_0.max(0, b);
  };
  Color$Companion.prototype.bounded_1 = function (f) {
    var b = Math_0.min(1.0, f);
    return Math_0.max(0.0, b);
  };
  Color$Companion.prototype.asInt_0 = function (f) {
    return numberToInt(this.bounded_1(f) * 255);
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
    this.onShowChange_33sz01$_0 = StubPinkyDisplay$onShowChange$lambda;
    this.selectedShow_fwpmt$_0 = null;
    this.nextFrameMs_1o69ux$_0 = 0;
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
  Object.defineProperty(StubPinkyDisplay.prototype, 'nextFrameMs', {
    get: function () {
      return this.nextFrameMs_1o69ux$_0;
    },
    set: function (nextFrameMs) {
      this.nextFrameMs_1o69ux$_0 = nextFrameMs;
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
  Dmx$Buffer.prototype.get_za3lpa$ = function (index) {
    this.boundsCheck_0(index);
    return this.channels_0[this.baseChannel + index | 0];
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
  function Mapper(network, sheepModel, mapperDisplay, mediaDevices) {
    this.network_0 = network;
    this.mapperDisplay_0 = mapperDisplay;
    this.$delegate_9rrh7p$_0 = MainScope();
    this.maxPixelsPerBrain_0 = 2048;
    this.width = 640;
    this.height = 300;
    var $receiver = mediaDevices.getCamera_vux9f0$(this.width, this.height);
    $receiver.onImage = Mapper$camera$lambda$lambda(this);
    this.camera = $receiver;
    this.baseBitmap_0 = null;
    this.deltaBitmap_hn3lh8$_0 = this.deltaBitmap_hn3lh8$_0;
    this.newChangeRegion_0 = null;
    this.link_tktc8n$_0 = this.link_tktc8n$_0;
    this.isRunning_0 = true;
    this.isAligned_0 = false;
    this.isPaused_0 = false;
    this.captureBaseImage_0 = false;
    this.suppressShowsJob_0 = null;
    this.brainMappers_0 = LinkedHashMap_init();
    this.mapperDisplay_0.listen_uasn0l$(this);
    this.mapperDisplay_0.addWireframe_9u144y$(sheepModel);
    this.retries_0 = new IntRange(0, 1);
  }
  Object.defineProperty(Mapper.prototype, 'deltaBitmap_0', {
    get: function () {
      if (this.deltaBitmap_hn3lh8$_0 == null)
        return throwUPAE('deltaBitmap');
      return this.deltaBitmap_hn3lh8$_0;
    },
    set: function (deltaBitmap) {
      this.deltaBitmap_hn3lh8$_0 = deltaBitmap;
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
            this.result_0 = this.local$this$Mapper.run(this);
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
            this.local$this$Mapper.link_0.listenUdp_a6m852$(8001, this.local$this$Mapper);
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
  Mapper.prototype.onStart = function () {
    this.isAligned_0 = true;
  };
  Mapper.prototype.onPause = function () {
    this.isPaused_0 = !this.isPaused_0;
  };
  Mapper.prototype.onStop = function () {
    this.isAligned_0 = false;
  };
  Mapper.prototype.onClose = function () {
    var tmp$;
    println('Shutting down Mapper...');
    this.isRunning_0 = false;
    this.camera.close();
    (tmp$ = this.suppressShowsJob_0) != null ? (tmp$.cancel_m4sck1$(), Unit) : null;
    this.link_0.broadcastUdp_68hu5j$(8002, new MapperHelloMessage(false));
    this.mapperDisplay_0.close();
  };
  function Coroutine$Mapper$run$lambda(this$Mapper_0, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.exceptionState_0 = 1;
    this.local$this$Mapper = this$Mapper_0;
  }
  Coroutine$Mapper$run$lambda.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$Mapper$run$lambda.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$Mapper$run$lambda.prototype.constructor = Coroutine$Mapper$run$lambda;
  Coroutine$Mapper$run$lambda.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.local$this$Mapper.link_0.broadcastUdp_68hu5j$(8002, new MapperHelloMessage(true));
            this.state_0 = 2;
            this.result_0 = delay(L1000, this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 1:
            throw this.exception_0;
          case 2:
            return this.local$this$Mapper.link_0.broadcastUdp_68hu5j$(8003, this.local$this$Mapper.solidColor_0(Color$Companion_getInstance().BLACK)), Unit;
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
  function Mapper$run$lambda(this$Mapper_0) {
    return function (continuation_0, suspended) {
      var instance = new Coroutine$Mapper$run$lambda(this$Mapper_0, continuation_0);
      if (suspended)
        return instance;
      else
        return instance.doResume(null);
    };
  }
  function Coroutine$Mapper$run$lambda_0(this$Mapper_0, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.exceptionState_0 = 1;
    this.local$this$Mapper = this$Mapper_0;
  }
  Coroutine$Mapper$run$lambda_0.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$Mapper$run$lambda_0.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$Mapper$run$lambda_0.prototype.constructor = Coroutine$Mapper$run$lambda_0;
  Coroutine$Mapper$run$lambda_0.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.local$this$Mapper.link_0.broadcastUdp_68hu5j$(8003, new BrainIdRequest(8001));
            this.state_0 = 2;
            this.result_0 = delay(L1000, this);
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
  function Mapper$run$lambda_0(this$Mapper_0) {
    return function (continuation_0, suspended) {
      var instance = new Coroutine$Mapper$run$lambda_0(this$Mapper_0, continuation_0);
      if (suspended)
        return instance;
      else
        return instance.doResume(null);
    };
  }
  function Coroutine$Mapper$run$lambda_1(this$Mapper_0, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.exceptionState_0 = 1;
    this.local$this$Mapper = this$Mapper_0;
  }
  Coroutine$Mapper$run$lambda_1.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$Mapper$run$lambda_1.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$Mapper$run$lambda_1.prototype.constructor = Coroutine$Mapper$run$lambda_1;
  Coroutine$Mapper$run$lambda_1.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.local$this$Mapper.link_0.broadcastUdp_68hu5j$(8003, this.local$this$Mapper.solidColor_0(Color$Companion_getInstance().BLACK));
            this.state_0 = 2;
            this.result_0 = delay(L250, this);
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
  function Mapper$run$lambda_1(this$Mapper_0) {
    return function (continuation_0, suspended) {
      var instance = new Coroutine$Mapper$run$lambda_1(this$Mapper_0, continuation_0);
      if (suspended)
        return instance;
      else
        return instance.doResume(null);
    };
  }
  function Coroutine$Mapper$run$lambda_2(this$Mapper_0, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.exceptionState_0 = 1;
    this.local$this$Mapper = this$Mapper_0;
  }
  Coroutine$Mapper$run$lambda_2.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$Mapper$run$lambda_2.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$Mapper$run$lambda_2.prototype.constructor = Coroutine$Mapper$run$lambda_2;
  Coroutine$Mapper$run$lambda_2.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.local$this$Mapper.link_0.broadcastUdp_68hu5j$(8003, this.local$this$Mapper.solidColor_0(Color$Companion_getInstance().WHITE));
            this.state_0 = 2;
            this.result_0 = delay(L250, this);
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
  function Mapper$run$lambda_2(this$Mapper_0) {
    return function (continuation_0, suspended) {
      var instance = new Coroutine$Mapper$run$lambda_2(this$Mapper_0, continuation_0);
      if (suspended)
        return instance;
      else
        return instance.doResume(null);
    };
  }
  function Coroutine$Mapper$run$lambda_3(this$Mapper_0, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.exceptionState_0 = 1;
    this.local$this$Mapper = this$Mapper_0;
  }
  Coroutine$Mapper$run$lambda_3.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$Mapper$run$lambda_3.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$Mapper$run$lambda_3.prototype.constructor = Coroutine$Mapper$run$lambda_3;
  Coroutine$Mapper$run$lambda_3.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.local$this$Mapper.link_0.broadcastUdp_68hu5j$(8003, this.local$this$Mapper.solidColor_0(Color$Companion_getInstance().BLACK));
            this.state_0 = 2;
            this.result_0 = delay(L250, this);
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
  function Mapper$run$lambda_3(this$Mapper_0) {
    return function (continuation_0, suspended) {
      var instance = new Coroutine$Mapper$run$lambda_3(this$Mapper_0, continuation_0);
      if (suspended)
        return instance;
      else
        return instance.doResume(null);
    };
  }
  function Mapper$run$lambda$lambda$lambda(this$Mapper) {
    return function () {
      return this$Mapper.solidColor_0(Color$Companion_getInstance().WHITE);
    };
  }
  function Coroutine$Mapper$run$lambda$lambda(closure$brainMapper_0, this$Mapper_0, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.exceptionState_0 = 1;
    this.local$closure$brainMapper = closure$brainMapper_0;
    this.local$this$Mapper = this$Mapper_0;
  }
  Coroutine$Mapper$run$lambda$lambda.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$Mapper$run$lambda$lambda.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$Mapper$run$lambda$lambda.prototype.constructor = Coroutine$Mapper$run$lambda$lambda;
  Coroutine$Mapper$run$lambda$lambda.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            return this.local$closure$brainMapper.shade_s74fr6$(Mapper$run$lambda$lambda$lambda(this.local$this$Mapper)), Unit;
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
  function Mapper$run$lambda$lambda(closure$brainMapper_0, this$Mapper_0) {
    return function (continuation_0, suspended) {
      var instance = new Coroutine$Mapper$run$lambda$lambda(closure$brainMapper_0, this$Mapper_0, continuation_0);
      if (suspended)
        return instance;
      else
        return instance.doResume(null);
    };
  }
  function Mapper$run$lambda$lambda$lambda_0(this$Mapper) {
    return function () {
      return this$Mapper.solidColor_0(Color$Companion_getInstance().BLACK);
    };
  }
  function Coroutine$Mapper$run$lambda$lambda_0(closure$brainMapper_0, this$Mapper_0, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.exceptionState_0 = 1;
    this.local$closure$brainMapper = closure$brainMapper_0;
    this.local$this$Mapper = this$Mapper_0;
  }
  Coroutine$Mapper$run$lambda$lambda_0.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$Mapper$run$lambda$lambda_0.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$Mapper$run$lambda$lambda_0.prototype.constructor = Coroutine$Mapper$run$lambda$lambda_0;
  Coroutine$Mapper$run$lambda$lambda_0.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            return this.local$closure$brainMapper.shade_s74fr6$(Mapper$run$lambda$lambda$lambda_0(this.local$this$Mapper)), Unit;
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
  function Mapper$run$lambda$lambda_0(closure$brainMapper_0, this$Mapper_0) {
    return function (continuation_0, suspended) {
      var instance = new Coroutine$Mapper$run$lambda$lambda_0(closure$brainMapper_0, this$Mapper_0, continuation_0);
      if (suspended)
        return instance;
      else
        return instance.doResume(null);
    };
  }
  function Mapper$run$ObjectLiteral() {
    this.pixelCount_na9trc$_0 = 2048;
  }
  Object.defineProperty(Mapper$run$ObjectLiteral.prototype, 'pixelCount', {
    get: function () {
      return this.pixelCount_na9trc$_0;
    }
  });
  Mapper$run$ObjectLiteral.prototype.describe = function () {
    return 'Mapper surface';
  };
  Mapper$run$ObjectLiteral.$metadata$ = {
    kind: Kind_CLASS,
    interfaces: [Surface]
  };
  function Coroutine$Mapper$run$lambda_4(this$Mapper_0, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.exceptionState_0 = 1;
    this.local$this$Mapper = this$Mapper_0;
  }
  Coroutine$Mapper$run$lambda_4.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$Mapper$run$lambda_4.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$Mapper$run$lambda_4.prototype.constructor = Coroutine$Mapper$run$lambda_4;
  Coroutine$Mapper$run$lambda_4.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            return this.local$this$Mapper.link_0.broadcastUdp_68hu5j$(8002, new MapperHelloMessage(this.local$this$Mapper.isRunning_0)), Unit;
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
  function Mapper$run$lambda_4(this$Mapper_0) {
    return function (continuation_0, suspended) {
      var instance = new Coroutine$Mapper$run$lambda_4(this$Mapper_0, continuation_0);
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
    this.local$tmp$ = void 0;
    this.local$tmp$_0 = void 0;
    this.local$element = void 0;
    this.local$pixelShader = void 0;
    this.local$buffer = void 0;
    this.local$i = void 0;
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
            this.$this.mapperDisplay_0.showMessage_61zpoe$('ESTABLISHING UPLINK\u2026');
            this.state_0 = 2;
            this.result_0 = this.$this.retry_0(Mapper$run$lambda(this.$this), this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 1:
            throw this.exception_0;
          case 2:
            this.$this.suppressShows_0();
            this.state_0 = 3;
            this.result_0 = this.$this.retry_0(Mapper$run$lambda_0(this.$this), this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 3:
            this.state_0 = 4;
            this.result_0 = delay(L1000, this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 4:
            this.state_0 = 5;
            this.result_0 = this.$this.retry_0(Mapper$run$lambda_1(this.$this), this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 5:
            this.state_0 = 6;
            this.result_0 = delay(L250, this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 6:
            this.$this.mapperDisplay_0.showMessage_61zpoe$('READY PLAYER ONE\u2026');
            this.state_0 = 7;
            this.result_0 = this.$this.retry_0(Mapper$run$lambda_2(this.$this), this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 7:
            this.state_0 = 8;
            this.result_0 = delay(L250, this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 8:
            this.state_0 = 9;
            continue;
          case 9:
            if (this.$this.isAligned_0) {
              this.state_0 = 11;
              continue;
            }

            this.state_0 = 10;
            this.result_0 = delay(L500, this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 10:
            if (Random.Default.nextFloat() < 0.1) {
              this.$this.mapperDisplay_0.showMessage_61zpoe$('READY PLAYER ONE\u2026');
            }
             else if (Random.Default.nextFloat() < 0.1) {
              this.$this.mapperDisplay_0.showMessage_61zpoe$('ALIGN THY SHEEP\u2026');
            }

            this.state_0 = 9;
            continue;
          case 11:
            this.$this.mapperDisplay_0.showMessage_61zpoe$('CALIBRATING\u2026');
            this.state_0 = 12;
            this.result_0 = this.$this.retry_0(Mapper$run$lambda_3(this.$this), this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 12:
            this.state_0 = 13;
            this.result_0 = delay(L250, this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 13:
            this.$this.captureBaseImage_0 = true;
            this.state_0 = 14;
            this.result_0 = delay(L250, this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 14:
            this.$this.mapperDisplay_0.showMessage_61zpoe$('MAPPING\u2026');
            this.$this.mapperDisplay_0.showStats_qt1dr2$(this.$this.brainMappers_0.size, 0, -1);
            this.state_0 = 15;
            continue;
          case 15:
            if (!this.$this.isRunning_0) {
              this.state_0 = 32;
              continue;
            }

            println('identify brains...');
            this.local$tmp$_0 = this.$this.brainMappers_0.values.iterator();
            this.state_0 = 16;
            continue;
          case 16:
            if (!this.local$tmp$_0.hasNext()) {
              this.state_0 = 24;
              continue;
            }

            this.local$element = this.local$tmp$_0.next();
            this.state_0 = 17;
            this.result_0 = this.$this.retry_0(Mapper$run$lambda$lambda(this.local$element, this.$this), this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 17:
            this.state_0 = 18;
            this.result_0 = delay(L34, this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 18:
            this.state_0 = 19;
            continue;
          case 19:
            if (this.$this.newChangeRegion_0 != null) {
              this.state_0 = 21;
              continue;
            }

            this.state_0 = 20;
            this.result_0 = delay(L10, this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 20:
            this.state_0 = 19;
            continue;
          case 21:
            var changeRegion = ensureNotNull(this.$this.newChangeRegion_0);
            this.$this.newChangeRegion_0 = null;
            var candidates = this.$this.mapperDisplay_0.getCandidateSurfaces_gdgylh$(changeRegion);
            var tmp$ = this.$this.mapperDisplay_0;
            var b = candidates.size;
            var $receiver = candidates.subList_vux9f0$(0, Math_0.min(5, b));
            var destination = ArrayList_init_0(collectionSizeOrDefault($receiver, 10));
            var tmp$_0;
            tmp$_0 = $receiver.iterator();
            while (tmp$_0.hasNext()) {
              var item = tmp$_0.next();
              destination.add_11rb$(item.name);
            }

            tmp$.showMessage2_61zpoe$('Candidate panels: ' + destination);
            println('Guessed panel ' + first(candidates).name + ' for ' + this.local$element.brainId);
            this.state_0 = 22;
            this.result_0 = this.$this.maybePause_0(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 22:
            this.state_0 = 23;
            this.result_0 = this.$this.retry_0(Mapper$run$lambda$lambda_0(this.local$element, this.$this), this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 23:
            this.state_0 = 16;
            continue;
          case 24:
            this.state_0 = 25;
            this.result_0 = delay(L1000, this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 25:
            println('identify pixels...');
            this.local$pixelShader = new PixelShader();
            this.local$buffer = this.local$pixelShader.createBuffer_ppt8xj$(new Mapper$run$ObjectLiteral());
            this.local$buffer.setAll_rny0jj$(Color$Companion_getInstance().BLACK);
            this.local$tmp$ = this.$this.maxPixelsPerBrain_0;
            this.local$i = 0;
            this.state_0 = 26;
            continue;
          case 26:
            if (this.local$i >= this.local$tmp$) {
              this.state_0 = 30;
              continue;
            }

            if (this.local$i % 128 === 0)
              println('pixel ' + this.local$i + '... isRunning is ' + this.$this.isRunning_0);
            this.local$buffer.colors[this.local$i] = Color$Companion_getInstance().WHITE;
            this.$this.link_0.broadcastUdp_68hu5j$(8003, new BrainShaderMessage(this.local$pixelShader, this.local$buffer));
            this.local$buffer.colors[this.local$i] = Color$Companion_getInstance().BLACK;
            this.state_0 = 27;
            this.result_0 = delay(L34, this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 27:
            this.state_0 = 28;
            this.result_0 = this.$this.maybePause_0(this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 28:
            this.state_0 = 29;
            continue;
          case 29:
            this.local$i++;
            this.state_0 = 26;
            continue;
          case 30:
            println('done identifying pixels...');
            this.state_0 = 31;
            this.result_0 = delay(L1000, this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 31:
            this.state_0 = 15;
            continue;
          case 32:
            println('done identifying things... ' + this.$this.isRunning_0);
            this.state_0 = 33;
            this.result_0 = this.$this.retry_0(Mapper$run$lambda_4(this.$this), this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 33:
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
  Mapper.prototype.run = function (continuation_0, suspended) {
    var instance = new Coroutine$run_0(this, continuation_0);
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
            this.result_0 = this.local$fn(this);
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
            this.local$this$Mapper.link_0.broadcastUdp_68hu5j$(8002, new MapperHelloMessage(this.local$this$Mapper.isRunning_0));
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
  function Coroutine$maybePause_0($this, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.exceptionState_0 = 1;
    this.$this = $this;
  }
  Coroutine$maybePause_0.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$maybePause_0.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$maybePause_0.prototype.constructor = Coroutine$maybePause_0;
  Coroutine$maybePause_0.prototype.doResume = function () {
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
            this.result_0 = delay(L100, this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 3:
            this.state_0 = 2;
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
  Mapper.prototype.maybePause_0 = function (continuation_0, suspended) {
    var instance = new Coroutine$maybePause_0(this, continuation_0);
    if (suspended)
      return instance;
    else
      return instance.doResume(null);
  };
  function Mapper$solidColor$ObjectLiteral() {
    this.pixelCount_xcgg0p$_0 = 2048;
  }
  Object.defineProperty(Mapper$solidColor$ObjectLiteral.prototype, 'pixelCount', {
    get: function () {
      return this.pixelCount_xcgg0p$_0;
    }
  });
  Mapper$solidColor$ObjectLiteral.prototype.describe = function () {
    return 'Mapper surface';
  };
  Mapper$solidColor$ObjectLiteral.$metadata$ = {
    kind: Kind_CLASS,
    interfaces: [Surface]
  };
  Mapper.prototype.solidColor_0 = function (color) {
    var solidShader = new SolidShader();
    var $receiver = solidShader.createBuffer_ppt8xj$(new Mapper$solidColor$ObjectLiteral());
    $receiver.color = color;
    var buffer = $receiver;
    return new BrainShaderMessage(solidShader, buffer);
  };
  function Mapper$receive$lambda(this$Mapper) {
    return function () {
      return this$Mapper.solidColor_0(Color$Companion_getInstance().GREEN);
    };
  }
  Mapper.prototype.receive_rq4egf$ = function (fromAddress, bytes) {
    var message = parse(bytes);
    if (Kotlin.isType(message, BrainIdResponse)) {
      var $receiver = this.brainMappers_0;
      var tmp$;
      var value = $receiver.get_11rb$(fromAddress);
      if (value == null) {
        var answer = new Mapper$BrainMapper(this, fromAddress, message.id);
        $receiver.put_xwzc9p$(fromAddress, answer);
        tmp$ = answer;
      }
       else {
        tmp$ = value;
      }
      var brainMapper = tmp$;
      brainMapper.shade_s74fr6$(Mapper$receive$lambda(this));
    }
     else if (Kotlin.isType(message, PinkyPongMessage)) {
      var tmp$_0;
      tmp$_0 = message.brainIds.iterator();
      while (tmp$_0.hasNext()) {
        var element = tmp$_0.next();
        println('id = ' + element);
      }
    }
  };
  Mapper.prototype.haveImage_0 = function (image) {
    this.mapperDisplay_0.showCamImage_6tj0gx$(image);
    var bitmap = image.toBitmap();
    if (this.captureBaseImage_0) {
      this.baseBitmap_0 = bitmap;
      this.deltaBitmap_0 = new NativeBitmap(bitmap.width, bitmap.height);
      this.captureBaseImage_0 = false;
    }
     else if (this.baseBitmap_0 != null) {
      this.deltaBitmap_0.copyFrom_5151av$(ensureNotNull(this.baseBitmap_0));
      this.deltaBitmap_0.subtract_5151av$(bitmap);
      var changeRegion = this.detectChangeRegion_0();
      this.newChangeRegion_0 = changeRegion;
      println('changeRegion = ' + changeRegion + ' ' + changeRegion.width + ' ' + changeRegion.height);
      this.mapperDisplay_0.showDiffImage_qpnjw8$(this.deltaBitmap_0, changeRegion);
    }
  };
  function Mapper$detectChangeRegion$lambda(this$Mapper, closure$changeRegion) {
    return function (data) {
      var tmp$, tmp$_0;
      var x0 = -1;
      var y0 = -1;
      var x1 = -1;
      var y1 = -1;
      tmp$ = this$Mapper.height;
      for (var y = 0; y < tmp$; y++) {
        var yAnyDiff = false;
        tmp$_0 = this$Mapper.width;
        for (var x = 0; x < tmp$_0; x++) {
          var pixDiff = data[((x + Kotlin.imul(y, this$Mapper.width) | 0) * 4 | 0) + 2 | 0];
          if (pixDiff !== 0) {
            if (x0 === -1 || x0 > x)
              x0 = x;
            if (x > x1)
              x1 = x;
            yAnyDiff = true;
          }
        }
        if (yAnyDiff) {
          if (y0 === -1)
            y0 = y;
          y1 = y;
        }
      }
      closure$changeRegion.v = new MediaDevices$Region(x0, y0, x1, y1);
      return false;
    };
  }
  Mapper.prototype.detectChangeRegion_0 = function () {
    var changeRegion = {v: new MediaDevices$Region(-1, -1, -1, -1)};
    this.deltaBitmap_0.withData_c37y77$(Mapper$detectChangeRegion$lambda(this, changeRegion));
    return changeRegion.v;
  };
  function Mapper$BrainMapper($outer, address, brainId) {
    this.$outer = $outer;
    this.address_0 = address;
    this.brainId = brainId;
  }
  Mapper$BrainMapper.prototype.shade_s74fr6$ = function (shaderMessage) {
    this.$outer.link_0.sendUdp_wpmaqi$(this.address_0, 8003, shaderMessage());
  };
  Mapper$BrainMapper.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'BrainMapper',
    interfaces: []
  };
  Object.defineProperty(Mapper.prototype, 'coroutineContext', {
    get: function () {
      return this.$delegate_9rrh7p$_0.coroutineContext;
    }
  });
  function Mapper$camera$lambda$lambda(this$Mapper) {
    return function (image) {
      this$Mapper.haveImage_0(image);
      return Unit;
    };
  }
  Mapper.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Mapper',
    interfaces: [CoroutineScope, MapperDisplay$Listener, Network$UdpListener]
  };
  function MapperDisplay() {
  }
  function MapperDisplay$Listener() {
  }
  MapperDisplay$Listener.$metadata$ = {
    kind: Kind_INTERFACE,
    simpleName: 'Listener',
    interfaces: []
  };
  MapperDisplay.$metadata$ = {
    kind: Kind_INTERFACE,
    simpleName: 'MapperDisplay',
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
    this.x0 = x0;
    this.y0 = y0;
    this.x1 = x1;
    this.y1 = y1;
    this.width = this.x1 - this.x0 | 0;
    this.height = this.y1 - this.y0 | 0;
    this.centerX = this.x1 - this.x0 | 0;
    this.centerY = this.y1 - this.y0 | 0;
  }
  MediaDevices$Region.prototype.distanceTo_gdgylh$ = function (other) {
    var dX = this.centerX - other.centerX | 0;
    var dY = this.centerY - other.centerY | 0;
    var x = Kotlin.imul(dX, dX) + Kotlin.imul(dY, dY) | 0;
    return Math_0.sqrt(x);
  };
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
  function Pinky(sheepModel, shows, network, dmxUniverse, display) {
    this.sheepModel = sheepModel;
    this.shows = shows;
    this.network = network;
    this.dmxUniverse = dmxUniverse;
    this.display = display;
    this.link_0 = new FragmentingUdpLink(this.network.link());
    this.beatProvider_0 = new Pinky$PinkyBeatProvider(this, 120.0);
    this.mapperIsRunning_0 = false;
    this.selectedShow_vpdlot$_0 = first(this.shows);
    var $receiver = new PubSub$Server(this.link_0, 8004);
    $receiver.install_stpyu4$(gadgetModule);
    this.pubSub_0 = $receiver;
    this.gadgetManager_0 = new GadgetManager(this.pubSub_0);
    this.showRunner_0 = new ShowRunner(this.sheepModel, this.selectedShow_0, this.gadgetManager_0, this.beatProvider_0, this.dmxUniverse);
    var $receiver_0 = this.sheepModel.allPanels;
    var capacity = coerceAtLeast(mapCapacity(collectionSizeOrDefault($receiver_0, 10)), 16);
    var destination = LinkedHashMap_init_0(capacity);
    var tmp$;
    tmp$ = $receiver_0.iterator();
    while (tmp$.hasNext()) {
      var element = tmp$.next();
      destination.put_xwzc9p$(element.name, element);
    }
    this.surfacesByName_0 = destination;
    this.pixelsBySurface_0 = LinkedHashMap_init();
    this.surfaceMappingsByBrain_0 = LinkedHashMap_init();
    this.brainInfos_0 = LinkedHashMap_init();
    this.pendingBrainInfos_0 = LinkedHashMap_init();
    this.networkStats_0 = new Pinky$NetworkStats();
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
  function Pinky$run$lambda_3(this$Pinky) {
    return function () {
      this$Pinky.drawNextFrame_8be2vx$();
      return Unit;
    };
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
            launch(coroutines.GlobalScope, void 0, void 0, Pinky$run$lambda(this.$this));
            this.$this.link_0.listenUdp_a6m852$(8002, this.$this);
            this.$this.display.listShows_3lsa6o$(this.$this.shows);
            this.$this.display.selectedShow = this.$this.selectedShow_0;
            var tmp$ = this.$this.pubSub_0;
            var tmp$_0 = Topics_getInstance().availableShows;
            var $receiver = this.$this.shows;
            var destination = ArrayList_init_0(collectionSizeOrDefault($receiver, 10));
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
            var elapsedMs = time(Pinky$run$lambda_3(this.$this));
            this.$this.display.nextFrameMs = elapsedMs.toInt();
            this.$this.display.stats = this.$this.networkStats_0;
            this.state_0 = 5;
            this.result_0 = delay(L50, this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 5:
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
    var instance = new Coroutine$run_1(this, continuation_0);
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
  Pinky.prototype.drawNextFrame_8be2vx$ = function () {
    this.showRunner_0.nextFrame();
  };
  Pinky.prototype.disableDmx_0 = function () {
    this.dmxUniverse.allOff();
  };
  Pinky.prototype.receive_rq4egf$ = function (fromAddress, bytes) {
    var message = parse(bytes);
    if (Kotlin.isType(message, BrainHelloMessage))
      this.foundBrain_0(fromAddress, new BrainId(message.brainId), message.surfaceName);
    else if (Kotlin.isType(message, MapperHelloMessage))
      this.mapperIsRunning_0 = message.isRunning;
  };
  Pinky.prototype.maybeSendMapping_0 = function (address, brainId) {
    var tmp$, tmp$_0;
    var surface = this.surfaceMappingsByBrain_0.get_11rb$(brainId);
    if (surface != null && Kotlin.isType(surface, SheepModel$Panel)) {
      var pixelLocations = this.pixelsBySurface_0.get_11rb$(surface);
      var pixelCount = (tmp$ = pixelLocations != null ? pixelLocations.length : null) != null ? tmp$ : -1;
      var tmp$_1;
      if (pixelLocations != null) {
        var destination = ArrayList_init_0(pixelLocations.length);
        var tmp$_2;
        for (tmp$_2 = 0; tmp$_2 !== pixelLocations.length; ++tmp$_2) {
          var item = pixelLocations[tmp$_2];
          destination.add_11rb$(new Vector2F(item.x, item.y));
        }
        tmp$_1 = destination;
      }
       else
        tmp$_1 = null;
      var pixelVertices = (tmp$_0 = tmp$_1) != null ? tmp$_0 : emptyList();
      var mappingMsg = new BrainMappingMessage(brainId, surface.name, pixelCount, pixelVertices);
      this.link_0.sendUdp_wpmaqi$(address, 8003, mappingMsg);
    }
  };
  function Pinky$UnknownSurface(brainId) {
    this.brainId = brainId;
    this.pixelCount_8pkgid$_0 = -1;
  }
  Object.defineProperty(Pinky$UnknownSurface.prototype, 'pixelCount', {
    get: function () {
      return this.pixelCount_8pkgid$_0;
    }
  });
  Pinky$UnknownSurface.prototype.describe = function () {
    return 'Unknown surface for ' + this.brainId;
  };
  Pinky$UnknownSurface.prototype.equals = function (other) {
    return Kotlin.isType(other, Pinky$UnknownSurface) && this.brainId.equals(other.brainId);
  };
  Pinky$UnknownSurface.prototype.hashCode = function () {
    return this.brainId.hashCode();
  };
  Pinky$UnknownSurface.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'UnknownSurface',
    interfaces: [Surface]
  };
  function Pinky$foundBrain$lambda(this$Pinky, closure$brainAddress) {
    return function (shaderBuffer) {
      var tmp$;
      var message = (new BrainShaderMessage(shaderBuffer.shader, shaderBuffer)).toBytes();
      this$Pinky.link_0.sendUdp_ytpeqp$(closure$brainAddress, 8003, message);
      var tmp$_0;
      tmp$_0 = this$Pinky.networkStats_0;
      tmp$_0.packetsSent = tmp$_0.packetsSent + 1 | 0;
      tmp$ = this$Pinky.networkStats_0;
      tmp$.bytesSent = tmp$.bytesSent + message.length | 0;
      return Unit;
    };
  }
  Pinky.prototype.foundBrain_0 = function (brainAddress, brainId, surfaceName) {
    var tmp$, tmp$_0;
    var surface = (tmp$ = surfaceName != null ? this.surfacesByName_0.get_11rb$(surfaceName) : null) != null ? tmp$ : new Pinky$UnknownSurface(brainId);
    if (Kotlin.isType(surface, Pinky$UnknownSurface))
      this.maybeSendMapping_0(brainAddress, brainId);
    var priorBrainInfo = this.brainInfos_0.get_11rb$(brainId);
    if (priorBrainInfo != null) {
      if (((tmp$_0 = priorBrainInfo.brainId) != null ? tmp$_0.equals(brainId) : null) && equals(priorBrainInfo.surface, surface)) {
        return;
      }
    }
    var surfaceReceiver = new ShowRunner$SurfaceReceiver(surface, Pinky$foundBrain$lambda(this, brainAddress));
    var brainInfo = new BrainInfo(brainAddress, brainId, surface, surfaceReceiver);
    this.pendingBrainInfos_0.put_xwzc9p$(brainId, brainInfo);
  };
  Pinky.prototype.providePanelMapping_epc2uw$ = function (brainId, surface) {
    this.surfaceMappingsByBrain_0.put_xwzc9p$(brainId, surface);
  };
  Pinky.prototype.providePixelMapping_td2c2y$ = function (surface, pixelLocations) {
    this.pixelsBySurface_0.put_xwzc9p$(surface, pixelLocations);
  };
  function Pinky$BeatProvider() {
  }
  Pinky$BeatProvider.$metadata$ = {
    kind: Kind_INTERFACE,
    simpleName: 'BeatProvider',
    interfaces: []
  };
  function Pinky$PinkyBeatProvider($outer, bpm) {
    this.$outer = $outer;
    this.bpm_gkcixa$_0 = bpm;
    this.startTimeMillis_0 = L0;
    this.beatsPerMeasure_0 = 4;
    this.millisPerBeat_0 = 1000 / (this.bpm / 60);
  }
  Object.defineProperty(Pinky$PinkyBeatProvider.prototype, 'bpm', {
    get: function () {
      return this.bpm_gkcixa$_0;
    },
    set: function (bpm) {
      this.bpm_gkcixa$_0 = bpm;
    }
  });
  Object.defineProperty(Pinky$PinkyBeatProvider.prototype, 'beat', {
    get: function () {
      var now = getTimeMillis();
      return now.subtract(this.startTimeMillis_0).toNumber() / this.millisPerBeat_0 % this.beatsPerMeasure_0;
    }
  });
  function Coroutine$run_2($this, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.exceptionState_0 = 1;
    this.$this = $this;
  }
  Coroutine$run_2.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$run_2.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$run_2.prototype.constructor = Coroutine$run_2;
  Coroutine$run_2.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.$this.startTimeMillis_0 = getTimeMillis();
            this.state_0 = 2;
            continue;
          case 1:
            throw this.exception_0;
          case 2:
            this.$this.$outer.display.beat = numberToInt(this.$this.beat);
            var offsetMillis = getTimeMillis().subtract(this.$this.startTimeMillis_0);
            var millsPer = this.$this.millisPerBeat_0;
            var delayTimeMillis = millsPer - offsetMillis.toNumber() % millsPer;
            this.state_0 = 3;
            this.result_0 = delay(Kotlin.Long.fromNumber(delayTimeMillis), this);
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
  Pinky$PinkyBeatProvider.prototype.run = function (continuation_0, suspended) {
    var instance = new Coroutine$run_2(this, continuation_0);
    if (suspended)
      return instance;
    else
      return instance.doResume(null);
  };
  Pinky$PinkyBeatProvider.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'PinkyBeatProvider',
    interfaces: [Pinky$BeatProvider]
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
  function BrainInfo(address, brainId, surface, surfaceReceiver) {
    this.address = address;
    this.brainId = brainId;
    this.surface = surface;
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
  PubSub$Companion.prototype.listen_qwvhmn$ = function (networkLink, port) {
    return new PubSub$Server(networkLink, port);
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
      default:IllegalArgumentException_init("huh? don't know what to do with " + command);
        break;
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
    interfaces: [Network$TcpListener, PubSub$Origin]
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
  function PubSub$Server(link, port) {
    PubSub$Endpoint.call(this);
    this.topics_0 = HashMap_init();
    link.listenTcp_kd29r4$(port, this);
  }
  PubSub$Server.prototype.incomingConnection_67ozxy$ = function (fromConnection) {
    return new PubSub$Connection('server at ' + fromConnection.toAddress, this.topics_0);
  };
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
  PubSub$Server.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Server',
    interfaces: [Network$TcpServerSocketListener, PubSub$Endpoint]
  };
  function PubSub$Client(link, serverAddress, port) {
    PubSub$Endpoint.call(this);
    this.topics_0 = HashMap_init();
    this.server_0 = new PubSub$Connection('client at ' + link.myAddress, this.topics_0);
    link.connectTcp_dy234z$(serverAddress, port, this.server_0);
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
    return [ShaderId$SOLID_getInstance(), ShaderId$PIXEL_getInstance(), ShaderId$SINE_WAVE_getInstance(), ShaderId$COMPOSITOR_getInstance(), ShaderId$SPARKLE_getInstance(), ShaderId$SIMPLE_SPATIAL_getInstance(), ShaderId$HEART_getInstance(), ShaderId$RANDOM_getInstance()];
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
      default:throwISE('No enum constant baaahs.ShaderId.' + name);
    }
  }
  ShaderId.valueOf_61zpoe$ = ShaderId$valueOf;
  function Surface() {
  }
  Surface.$metadata$ = {
    kind: Kind_INTERFACE,
    simpleName: 'Surface',
    interfaces: []
  };
  function ShaderReader() {
  }
  ShaderReader.$metadata$ = {
    kind: Kind_INTERFACE,
    simpleName: 'ShaderReader',
    interfaces: []
  };
  function Shader(id) {
    Shader$Companion_getInstance();
    this.id = id;
    this.descriptorBytes_lr4403$_0 = lazy(Shader$descriptorBytes$lambda(this));
  }
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
  function Pixels() {
  }
  Object.defineProperty(Pixels.prototype, 'indices', {
    get: function () {
      return new IntRange(0, this.size - 1 | 0);
    }
  });
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
  function SheepModel() {
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
          var list = (tmp$_5 = (tmp$_4 = closure$panelsByEdge.get_11rb$(element)) != null ? toList(tmp$_4) : null) != null ? tmp$_5 : emptyList();
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
    var $receiver = split(getResource('newsheep_processed.obj'), ['\n']);
    var destination = ArrayList_init_0(collectionSizeOrDefault($receiver, 10));
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
          var destination_0 = ArrayList_init_0(collectionSizeOrDefault(args, 10));
          var tmp$_3;
          tmp$_3 = args.iterator();
          while (tmp$_3.hasNext()) {
            var item_0 = tmp$_3.next();
            destination_0.add_11rb$(toDouble(item_0));
          }

          var coords = destination_0;
          vertices.add_11rb$(new SheepModel$Point(coords.get_za3lpa$(0), coords.get_za3lpa$(1), coords.get_za3lpa$(2)));
          break;
        case 'g':
          var name = joinToString(args, ' ');
          var match = Regex_init('^G_0?([^_]+).*?$').matchEntire_6bul2c$(name);
          if (match != null) {
            name = ensureNotNull(match.groups.get_za3lpa$(1)).value;
          }

          currentPanel.v = new SheepModel$Panel(name);
          panels.add_11rb$(currentPanel.v);
          break;
        case 'f':
          var destination_1 = ArrayList_init_0(collectionSizeOrDefault(args, 10));
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
          var destination_2 = ArrayList_init_0(collectionSizeOrDefault(args, 10));
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
    this.eyes = arrayListOf([new SheepModel$MovingHead('leftEye', new SheepModel$Point(-163.738, 204.361, 439.302)), new SheepModel$MovingHead('rightEye', new SheepModel$Point(-103.738, 204.361, 439.302))]);
  };
  SheepModel.prototype.neighborsOf_jfju1k$ = function (panel) {
    var tmp$;
    return (tmp$ = this.panelNeighbors.get_11rb$(panel)) != null ? tmp$ : emptyList();
  };
  function SheepModel$Point(x, y, z) {
    this.x = x;
    this.y = y;
    this.z = z;
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
    this.name = name;
    this.pixelCount_tjn0yd$_0 = -1;
    this.faces = new SheepModel$Faces();
    this.lines = ArrayList_init();
  }
  Object.defineProperty(SheepModel$Panel.prototype, 'pixelCount', {
    get: function () {
      return this.pixelCount_tjn0yd$_0;
    }
  });
  SheepModel$Panel.prototype.describe = function () {
    return 'Panel ' + this.name;
  };
  SheepModel$Panel.prototype.equals = function (other) {
    return Kotlin.isType(other, SheepModel$Panel) && equals(this.name, other.name);
  };
  SheepModel$Panel.prototype.hashCode = function () {
    return hashCode(this.name);
  };
  SheepModel$Panel.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Panel',
    interfaces: [Surface]
  };
  function SheepModel$MovingHead(name, origin) {
    this.name = name;
    this.origin = origin;
  }
  SheepModel$MovingHead.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'MovingHead',
    interfaces: []
  };
  SheepModel.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'SheepModel',
    interfaces: []
  };
  function Shenzarpy(buffer) {
    Shenzarpy$Companion_getInstance();
    Dmx$DeviceType.call(this, 16);
    this.buffer_0 = buffer;
    this.dimmer = 1.0;
  }
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
      default:throwISE('No enum constant baaahs.Shenzarpy.WheelColor.' + name);
    }
  }
  Shenzarpy$WheelColor.valueOf_61zpoe$ = Shenzarpy$WheelColor$valueOf;
  function Shenzarpy$Channel(name, ordinal) {
    Enum.call(this);
    this.name$ = name;
    this.ordinal$ = ordinal;
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
  Shenzarpy$Channel.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Channel',
    interfaces: [Enum]
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
      default:throwISE('No enum constant baaahs.Shenzarpy.Channel.' + name);
    }
  }
  Shenzarpy$Channel.valueOf_61zpoe$ = Shenzarpy$Channel$valueOf;
  Object.defineProperty(Shenzarpy.prototype, 'colorWheel', {
    get: function () {
      return this.get_0(this.buffer_0, Shenzarpy$Channel$COLOR_WHEEL_getInstance());
    },
    set: function (value) {
      this.set_0(this.buffer_0, Shenzarpy$Channel$COLOR_WHEEL_getInstance(), value);
    }
  });
  Object.defineProperty(Shenzarpy.prototype, 'dimmer', {
    get: function () {
      return (this.get_0(this.buffer_0, Shenzarpy$Channel$DIMMER_getInstance()) & 255) / 255.0;
    },
    set: function (value) {
      this.set_0(this.buffer_0, Shenzarpy$Channel$DIMMER_getInstance(), toByte(numberToInt(value * 255) & 255));
    }
  });
  Object.defineProperty(Shenzarpy.prototype, 'pan', {
    get: function () {
      var firstByte = this.get_0(this.buffer_0, Shenzarpy$Channel$PAN_getInstance()) & 255;
      var secondByte = this.get_0(this.buffer_0, Shenzarpy$Channel$PAN_FINE_getInstance()) & 255;
      var scaled = (firstByte * 256 | 0) + secondByte | 0;
      return scaled / 65535.0;
    },
    set: function (value) {
      var x = value % Shenzarpy$Companion_getInstance().panRange.endInclusive;
      var modVal = Math_0.abs(x);
      var scaled = numberToInt(modVal * 65535);
      this.set_0(this.buffer_0, Shenzarpy$Channel$PAN_getInstance(), toByte(scaled >> 8));
      this.set_0(this.buffer_0, Shenzarpy$Channel$PAN_FINE_getInstance(), toByte(scaled & 255));
    }
  });
  Object.defineProperty(Shenzarpy.prototype, 'tilt', {
    get: function () {
      var firstByte = this.get_0(this.buffer_0, Shenzarpy$Channel$TILT_getInstance()) & 255;
      var secondByte = this.get_0(this.buffer_0, Shenzarpy$Channel$TILT_FINE_getInstance()) & 255;
      var scaled = (firstByte * 256 | 0) + secondByte | 0;
      return scaled / 65535.0;
    },
    set: function (value) {
      var x = value % Shenzarpy$Companion_getInstance().tiltRange.endInclusive;
      var modVal = Math_0.abs(x);
      var scaled = numberToInt(modVal * 65535);
      this.set_0(this.buffer_0, Shenzarpy$Channel$TILT_getInstance(), toByte(scaled >> 8));
      this.set_0(this.buffer_0, Shenzarpy$Channel$TILT_FINE_getInstance(), toByte(scaled & 255));
    }
  });
  Shenzarpy.prototype.set_0 = function ($receiver, channel, value) {
    this.buffer_0.set_6t1wet$(channel.ordinal, value);
  };
  Shenzarpy.prototype.get_0 = function ($receiver, channel) {
    return this.buffer_0.get_za3lpa$(channel.ordinal);
  };
  Shenzarpy.prototype.closestColorFor_rny0jj$ = function (color) {
    var bestMatch = {v: Shenzarpy$WheelColor$WHITE_getInstance()};
    var bestDistance = {v: 1.0};
    var $receiver = Shenzarpy$WheelColor$Companion_getInstance().values;
    var tmp$;
    for (tmp$ = 0; tmp$ !== $receiver.length; ++tmp$) {
      var element = $receiver[tmp$];
      var distance = element.color.distanceTo_rny0jj$(color);
      if (distance < bestDistance.v) {
        bestMatch.v = element;
        bestDistance.v = distance;
      }
    }
    return toByte(bestMatch.v.ordinal);
  };
  Shenzarpy.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Shenzarpy',
    interfaces: [Dmx$DeviceType]
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
  function ShowRunner(model, initialShow, gadgetManager, beatProvider, dmxUniverse) {
    this.model_0 = model;
    this.gadgetManager_0 = gadgetManager;
    this.beatProvider_0 = beatProvider;
    this.dmxUniverse_0 = dmxUniverse;
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
      return toList(this.surfaceReceivers_0.keys);
    }
  });
  Object.defineProperty(ShowRunner.prototype, 'allUnusedSurfaces', {
    get: function () {
      return minus(this.allSurfaces, this.shaderBuffers_0.keys);
    }
  });
  ShowRunner.prototype.getBeatProvider = function () {
    return this.beatProvider_0;
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
  ShowRunner.prototype.getMovingHead_1hma8m$ = function (movingHead) {
    if (this.shadersLocked_0)
      throw IllegalStateException_init("Moving heads can't be obtained during #nextFrame()");
    var baseChannel = ensureNotNull(Config$Companion_getInstance().DMX_DEVICES.get_11rb$(movingHead.name));
    return new Shenzarpy(this.getDmxBuffer_0(baseChannel, 16));
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
    this.changedSurfaces_0.add_11rb$(new ShowRunner$SurfacesChanges(ArrayList_init_1(addedSurfaces), ArrayList_init_1(removedSurfaces)));
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
            var destination = ArrayList_init_0(collectionSizeOrDefault(added, 10));
            var tmp$_6;
            tmp$_6 = added.iterator();
            while (tmp$_6.hasNext()) {
              var item = tmp$_6.next();
              destination.add_11rb$(item.surface);
            }
            var destination_0 = ArrayList_init_0(collectionSizeOrDefault(removed, 10));
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
      this$ShowRunner.currentShowRenderer_0 = closure$startingShow.createRenderer_h1b9op$(this$ShowRunner.model_0, this$ShowRunner);
      return Unit;
    };
  }
  ShowRunner.prototype.createShowRenderer_0 = function (startingShow) {
    this.shaderBuffers_0.clear();
    var restartingSameShow = equals(this.nextShow, this.currentShow_0);
    var gadgetsState = restartingSameShow ? this.gadgetManager_0.getGadgetsState() : emptyMap();
    this.unlockShadersAndGadgets_0(ShowRunner$createShowRenderer$lambda(startingShow, this));
    logger$Companion_getInstance().info_61zpoe$('New show ' + startingShow.name + ' created; ' + (this.shaderBuffers_0.size.toString() + ' surfaces ') + ('and ' + this.requestedGadgets_0.size + ' gadgets'));
    this.gadgetManager_0.sync_7kvwdj$(toList_0(this.requestedGadgets_0), gadgetsState);
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
        element_0.sendFn(shaderBuffer);
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
    this.sendFn = sendFn;
  }
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
  function Topics() {
    Topics_instance = this;
    this.availableShows = new PubSub$Topic('availableShows', get_list(serializer(kotlin_js_internal_StringCompanionObject)));
    this.selectedShow = new PubSub$Topic('selectedShow', serializer(kotlin_js_internal_StringCompanionObject));
    this.activeGadgets = new PubSub$Topic('activeGadgets', get_list(GadgetData$Companion_getInstance().serializer()));
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
  function Slider(name, initialValue) {
    Slider$Companion_getInstance();
    if (initialValue === void 0)
      initialValue = 1.0;
    Gadget.call(this);
    this.name = name;
    this.initialValue = initialValue;
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
    output.endStructure_qatsm0$(this.descriptor);
  };
  Slider$$serializer.prototype.deserialize_nts5qn$ = function (decoder) {
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
    return Slider_init(bitMask0, local0, local1, null);
  };
  Slider$$serializer.prototype.childSerializers = function () {
    return [internal.StringSerializer, internal.FloatSerializer];
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
  function Slider_init(seen1, name, initialValue, serializationConstructorMarker) {
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
  Slider.prototype.copy_9sobi5$ = function (name, initialValue) {
    return new Slider(name === void 0 ? this.name : name, initialValue === void 0 ? this.initialValue : initialValue);
  };
  Slider.prototype.toString = function () {
    return 'Slider(name=' + Kotlin.toString(this.name) + (', initialValue=' + Kotlin.toString(this.initialValue)) + ')';
  };
  Slider.prototype.hashCode = function () {
    var result = 0;
    result = result * 31 + Kotlin.hashCode(this.name) | 0;
    result = result * 31 + Kotlin.hashCode(this.initialValue) | 0;
    return result;
  };
  Slider.prototype.equals = function (other) {
    return this === other || (other !== null && (typeof other === 'object' && (Object.getPrototypeOf(this) === Object.getPrototypeOf(other) && (Kotlin.equals(this.name, other.name) && Kotlin.equals(this.initialValue, other.initialValue)))));
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
  Bitmap.$metadata$ = {
    kind: Kind_INTERFACE,
    simpleName: 'Bitmap',
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
  ByteArrayReader.prototype.readFloat = function () {
    var bits = this.readInt();
    return Kotlin.floatFromBits(bits);
  };
  ByteArrayReader.prototype.readString = function () {
    var length = this.readInt();
    var buf = StringBuilder_init(length);
    for (var i = 0; i < length; i++) {
      buf.append_s8itvh$(unboxChar(this.readChar()));
    }
    return buf.toString();
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
  ByteArrayWriter.prototype.writeShort_mq22fl$ = function (s) {
    var tmp$, tmp$_0;
    this.growIfNecessary_0(2);
    this.bytes_0[tmp$ = this.offset, this.offset = tmp$ + 1 | 0, tmp$] = toByte(s >> 8 & 255);
    this.bytes_0[tmp$_0 = this.offset, this.offset = tmp$_0 + 1 | 0, tmp$_0] = toByte(s & 255);
  };
  ByteArrayWriter.prototype.writeChar_s8itvh$ = function (c) {
    this.writeShort_mq22fl$(toShort(c | 0));
  };
  ByteArrayWriter.prototype.writeInt_za3lpa$ = function (l) {
    var tmp$, tmp$_0, tmp$_1, tmp$_2;
    this.growIfNecessary_0(4);
    this.bytes_0[tmp$ = this.offset, this.offset = tmp$ + 1 | 0, tmp$] = toByte(l >> 24 & 255);
    this.bytes_0[tmp$_0 = this.offset, this.offset = tmp$_0 + 1 | 0, tmp$_0] = toByte(l >> 16 & 255);
    this.bytes_0[tmp$_1 = this.offset, this.offset = tmp$_1 + 1 | 0, tmp$_1] = toByte(l >> 8 & 255);
    this.bytes_0[tmp$_2 = this.offset, this.offset = tmp$_2 + 1 | 0, tmp$_2] = toByte(l & 255);
  };
  ByteArrayWriter.prototype.writeFloat_mx4ult$ = function (f) {
    this.writeInt_za3lpa$(toBits(f));
  };
  ByteArrayWriter.prototype.writeString_61zpoe$ = function (s) {
    var tmp$, tmp$_0, tmp$_1, tmp$_2;
    this.growIfNecessary_0(4 + (2 * s.length | 0) | 0);
    this.writeInt_za3lpa$(s.length);
    tmp$ = get_indices(s);
    tmp$_0 = tmp$.first;
    tmp$_1 = tmp$.last;
    tmp$_2 = tmp$.step;
    for (var i = tmp$_0; i <= tmp$_1; i += tmp$_2) {
      this.writeChar_s8itvh$(s.charCodeAt(i));
    }
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
      this.bytes_0 = copyOf(this.bytes_0, this.bytes_0.length * 2 | 0);
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
  FragmentingUdpLink$Fragment.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Fragment',
    interfaces: []
  };
  function FragmentingUdpLink$listenUdp$ObjectLiteral(closure$udpListener, this$FragmentingUdpLink) {
    this.closure$udpListener = closure$udpListener;
    this.this$FragmentingUdpLink = this$FragmentingUdpLink;
  }
  function FragmentingUdpLink$listenUdp$ObjectLiteral$receive$lambda(closure$messageId, closure$myFragments) {
    return function (fragment) {
      var remove = fragment.messageId === closure$messageId;
      if (remove)
        closure$myFragments.add_11rb$(fragment);
      return remove;
    };
  }
  FragmentingUdpLink$listenUdp$ObjectLiteral.prototype.receive_rq4egf$ = function (fromAddress, bytes) {
    var reader = new ByteArrayReader(bytes);
    var messageId = reader.readShort();
    var size = reader.readShort();
    var totalSize = reader.readInt();
    var offset = reader.readInt();
    var frameBytes = reader.readNBytes_za3lpa$(size);
    if (offset === 0 && size === totalSize) {
      this.closure$udpListener.receive_rq4egf$(fromAddress, frameBytes);
    }
     else {
      var thisFragment = new FragmentingUdpLink$Fragment(messageId, offset, frameBytes);
      this.this$FragmentingUdpLink.fragments_0.add_11rb$(thisFragment);
      if (offset + size === totalSize) {
        var myFragments = ArrayList_init();
        removeAll(this.this$FragmentingUdpLink.fragments_0, FragmentingUdpLink$listenUdp$ObjectLiteral$receive$lambda(messageId, myFragments));
        this.this$FragmentingUdpLink.fragments_0.isEmpty();
        var destination = ArrayList_init_0(collectionSizeOrDefault(myFragments, 10));
        var tmp$;
        tmp$ = myFragments.iterator();
        while (tmp$.hasNext()) {
          var item = tmp$.next();
          destination.add_11rb$(item.bytes.length);
        }
        var iterator = destination.iterator();
        if (!iterator.hasNext())
          throw UnsupportedOperationException_init("Empty collection can't be reduced.");
        var accumulator = iterator.next();
        while (iterator.hasNext()) {
          accumulator = accumulator + iterator.next() | 0;
        }
        var actualTotalSize = accumulator;
        if (actualTotalSize !== totalSize) {
          IllegalArgumentException_init("can't reassemble packet, " + actualTotalSize + ' != ' + totalSize + ' for ' + messageId);
        }
        var reassembleBytes = new Int8Array(totalSize);
        var tmp$_0;
        tmp$_0 = myFragments.iterator();
        while (tmp$_0.hasNext()) {
          var element = tmp$_0.next();
          var $receiver = element.bytes;
          arrayCopy($receiver, reassembleBytes, element.offset, 0, $receiver.length);
        }
        this.closure$udpListener.receive_rq4egf$(fromAddress, reassembleBytes);
      }
    }
  };
  FragmentingUdpLink$listenUdp$ObjectLiteral.$metadata$ = {
    kind: Kind_CLASS,
    interfaces: [Network$UdpListener]
  };
  FragmentingUdpLink.prototype.listenUdp_a6m852$ = function (port, udpListener) {
    this.wrappedLink_0.listenUdp_a6m852$(port, new FragmentingUdpLink$listenUdp$ObjectLiteral(udpListener, this));
  };
  function FragmentingUdpLink$sendUdp$lambda(this$FragmentingUdpLink, closure$toAddress, closure$port) {
    return function (fragment) {
      this$FragmentingUdpLink.wrappedLink_0.sendUdp_ytpeqp$(closure$toAddress, closure$port, fragment);
      return Unit;
    };
  }
  FragmentingUdpLink.prototype.sendUdp_ytpeqp$ = function (toAddress, port, bytes) {
    this.transmitMultipartUdp_0(bytes, FragmentingUdpLink$sendUdp$lambda(this, toAddress, port));
  };
  function FragmentingUdpLink$broadcastUdp$lambda(this$FragmentingUdpLink, closure$port) {
    return function (fragment) {
      this$FragmentingUdpLink.wrappedLink_0.broadcastUdp_3fbn1q$(closure$port, fragment);
      return Unit;
    };
  }
  FragmentingUdpLink.prototype.broadcastUdp_3fbn1q$ = function (port, bytes) {
    this.transmitMultipartUdp_0(bytes, FragmentingUdpLink$broadcastUdp$lambda(this, port));
  };
  FragmentingUdpLink.prototype.sendUdp_wpmaqi$ = function (toAddress, port, message) {
    this.sendUdp_ytpeqp$(toAddress, port, message.toBytes());
  };
  FragmentingUdpLink.prototype.broadcastUdp_68hu5j$ = function (port, message) {
    this.broadcastUdp_3fbn1q$(port, message.toBytes());
  };
  FragmentingUdpLink.prototype.transmitMultipartUdp_0 = function (bytes, fn) {
    var tmp$;
    if (bytes.length > 65535) {
      IllegalArgumentException_init('buffer too big! ' + bytes.length + ' must be < 65536');
    }
    var messageId = (tmp$ = this.nextMessageId_0, this.nextMessageId_0 = toShort(tmp$ + 1), tmp$);
    var messageCount = ((bytes.length - 1 | 0) / (this.mtu_0 - 12 | 0) | 0) + 1 | 0;
    var buf = new Int8Array(this.mtu_0);
    var offset = 0;
    for (var i = 0; i < messageCount; i++) {
      var writer = new ByteArrayWriter(buf);
      var a = this.mtu_0 - 12 | 0;
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
  FragmentingUdpLink.prototype.listenTcp_kd29r4$ = function (port, tcpServerSocketListener) {
    this.wrappedLink_0.listenTcp_kd29r4$(port, tcpServerSocketListener);
  };
  FragmentingUdpLink.prototype.connectTcp_dy234z$ = function (toAddress, port, tcpListener) {
    return this.wrappedLink_0.connectTcp_dy234z$(toAddress, port, tcpListener);
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
  Network$Link.prototype.sendUdp_wpmaqi$ = function (toAddress, port, message) {
    this.sendUdp_ytpeqp$(toAddress, port, message.toBytes());
  };
  Network$Link.prototype.broadcastUdp_68hu5j$ = function (port, message) {
    this.broadcastUdp_3fbn1q$(port, message.toBytes());
  };
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
  function Network$TcpListener() {
  }
  Network$TcpListener.$metadata$ = {
    kind: Kind_INTERFACE,
    simpleName: 'TcpListener',
    interfaces: []
  };
  function Network$TcpServerSocketListener() {
  }
  Network$TcpServerSocketListener.$metadata$ = {
    kind: Kind_INTERFACE,
    simpleName: 'TcpServerSocketListener',
    interfaces: []
  };
  Network.$metadata$ = {
    kind: Kind_INTERFACE,
    simpleName: 'Network',
    interfaces: []
  };
  function Ports() {
    Ports_instance = this;
    this.MAPPER = 8001;
    this.PINKY = 8002;
    this.BRAIN = 8003;
    this.PINKY_UI_TCP = 8004;
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
    Type$BRAIN_ID_RESPONSE_instance = new Type('BRAIN_ID_RESPONSE', 4);
    Type$BRAIN_MAPPING_instance = new Type('BRAIN_MAPPING', 5);
    Type$PINKY_PONG_instance = new Type('PINKY_PONG', 6);
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
  var Type$BRAIN_ID_RESPONSE_instance;
  function Type$BRAIN_ID_RESPONSE_getInstance() {
    Type_initFields();
    return Type$BRAIN_ID_RESPONSE_instance;
  }
  var Type$BRAIN_MAPPING_instance;
  function Type$BRAIN_MAPPING_getInstance() {
    Type_initFields();
    return Type$BRAIN_MAPPING_instance;
  }
  var Type$PINKY_PONG_instance;
  function Type$PINKY_PONG_getInstance() {
    Type_initFields();
    return Type$PINKY_PONG_instance;
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
    return [Type$BRAIN_HELLO_getInstance(), Type$BRAIN_PANEL_SHADE_getInstance(), Type$MAPPER_HELLO_getInstance(), Type$BRAIN_ID_REQUEST_getInstance(), Type$BRAIN_ID_RESPONSE_getInstance(), Type$BRAIN_MAPPING_getInstance(), Type$PINKY_PONG_getInstance()];
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
      case 'BRAIN_ID_RESPONSE':
        return Type$BRAIN_ID_RESPONSE_getInstance();
      case 'BRAIN_MAPPING':
        return Type$BRAIN_MAPPING_getInstance();
      case 'PINKY_PONG':
        return Type$PINKY_PONG_getInstance();
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
      case 'BRAIN_ID_RESPONSE':
        tmp$ = BrainIdResponse$Companion_getInstance().parse_100t80$(reader);
        break;
      case 'BRAIN_MAPPING':
        tmp$ = BrainMappingMessage$Companion_getInstance().parse_100t80$(reader);
        break;
      case 'PINKY_PONG':
        tmp$ = PinkyPongMessage$Companion_getInstance().parse_100t80$(reader);
        break;
      default:tmp$ = Kotlin.noWhenBranchMatched();
        break;
    }
    return tmp$;
  }
  function BrainHelloMessage(brainId, surfaceName) {
    BrainHelloMessage$Companion_getInstance();
    Message.call(this, Type$BRAIN_HELLO_getInstance());
    this.brainId = brainId;
    this.surfaceName = surfaceName;
  }
  function BrainHelloMessage$Companion() {
    BrainHelloMessage$Companion_instance = this;
  }
  BrainHelloMessage$Companion.prototype.parse_100t80$ = function (reader) {
    return new BrainHelloMessage(reader.readString(), reader.readNullableString());
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
  };
  BrainHelloMessage.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'BrainHelloMessage',
    interfaces: [Message]
  };
  function BrainShaderMessage(shader, buffer) {
    BrainShaderMessage$Companion_getInstance();
    Message.call(this, Type$BRAIN_PANEL_SHADE_getInstance());
    this.shader = shader;
    this.buffer = buffer;
  }
  function BrainShaderMessage$Companion() {
    BrainShaderMessage$Companion_instance = this;
  }
  BrainShaderMessage$Companion.prototype.parse_100t80$ = function (reader) {
    var shaderDesc = reader.readBytes();
    var shader = Shader$Companion_getInstance().parse_100t80$(new ByteArrayReader(shaderDesc));
    var buffer = shader.readBuffer_100t80$(reader);
    return new BrainShaderMessage(shader, buffer);
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
    writer.writeBytes_mj6st8$(this.shader.descriptorBytes);
    this.buffer.serialize_3kjoo0$(writer);
  };
  BrainShaderMessage.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'BrainShaderMessage',
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
  function BrainIdRequest(port) {
    BrainIdRequest$Companion_getInstance();
    Message.call(this, Type$BRAIN_ID_REQUEST_getInstance());
    this.port = port;
  }
  function BrainIdRequest$Companion() {
    BrainIdRequest$Companion_instance = this;
  }
  BrainIdRequest$Companion.prototype.parse_100t80$ = function (reader) {
    return new BrainIdRequest(reader.readInt());
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
    writer.writeInt_za3lpa$(this.port);
  };
  BrainIdRequest.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'BrainIdRequest',
    interfaces: [Message]
  };
  function BrainIdResponse(id, surfaceName) {
    BrainIdResponse$Companion_getInstance();
    Message.call(this, Type$BRAIN_ID_RESPONSE_getInstance());
    this.id = id;
    this.surfaceName = surfaceName;
  }
  function BrainIdResponse$Companion() {
    BrainIdResponse$Companion_instance = this;
  }
  BrainIdResponse$Companion.prototype.parse_100t80$ = function (reader) {
    return new BrainIdResponse(reader.readString(), reader.readNullableString());
  };
  BrainIdResponse$Companion.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'Companion',
    interfaces: []
  };
  var BrainIdResponse$Companion_instance = null;
  function BrainIdResponse$Companion_getInstance() {
    if (BrainIdResponse$Companion_instance === null) {
      new BrainIdResponse$Companion();
    }
    return BrainIdResponse$Companion_instance;
  }
  BrainIdResponse.prototype.serialize_3kjoo0$ = function (writer) {
    writer.writeString_61zpoe$(this.id);
    writer.writeNullableString_pdl1vj$(this.surfaceName);
  };
  BrainIdResponse.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'BrainIdResponse',
    interfaces: [Message]
  };
  function BrainMappingMessage(brainId, surfaceName, pixelCount, pixelVertices) {
    BrainMappingMessage$Companion_getInstance();
    Message.call(this, Type$BRAIN_MAPPING_getInstance());
    this.brainId = brainId;
    this.surfaceName = surfaceName;
    this.pixelCount = pixelCount;
    this.pixelVertices = pixelVertices;
  }
  function BrainMappingMessage$Companion() {
    BrainMappingMessage$Companion_instance = this;
  }
  BrainMappingMessage$Companion.prototype.readListOfVertices_v0p5xb$ = function ($receiver) {
    var vertexCount = $receiver.readInt();
    var $receiver_0 = until(0, vertexCount);
    var destination = ArrayList_init_0(collectionSizeOrDefault($receiver_0, 10));
    var tmp$;
    tmp$ = $receiver_0.iterator();
    while (tmp$.hasNext()) {
      var item = tmp$.next();
      destination.add_11rb$(new Vector2F($receiver.readFloat(), $receiver.readFloat()));
    }
    return destination;
  };
  BrainMappingMessage$Companion.prototype.parse_100t80$ = function (reader) {
    return new BrainMappingMessage(new BrainId(reader.readString()), reader.readNullableString(), reader.readInt(), this.readListOfVertices_v0p5xb$(reader));
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
    writer.writeInt_za3lpa$(this.pixelCount);
    var vertexCount = this.pixelVertices.size;
    writer.writeInt_za3lpa$(vertexCount);
    var tmp$;
    tmp$ = this.pixelVertices.iterator();
    while (tmp$.hasNext()) {
      var element = tmp$.next();
      writer.writeFloat_mx4ult$(element.x);
      writer.writeFloat_mx4ult$(element.y);
    }
  };
  BrainMappingMessage.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'BrainMappingMessage',
    interfaces: [Message]
  };
  function Vector2F(x, y) {
    this.x = x;
    this.y = y;
  }
  Vector2F.prototype.component1 = function () {
    return this.x;
  };
  Vector2F.prototype.component2 = function () {
    return this.y;
  };
  Vector2F.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Vector2F',
    interfaces: []
  };
  function PinkyPongMessage(brainIds) {
    PinkyPongMessage$Companion_getInstance();
    Message.call(this, Type$PINKY_PONG_getInstance());
    this.brainIds = brainIds;
  }
  function PinkyPongMessage$Companion() {
    PinkyPongMessage$Companion_instance = this;
  }
  PinkyPongMessage$Companion.prototype.parse_100t80$ = function (reader) {
    var brainCount = reader.readInt();
    var brainIds = ArrayList_init();
    for (var i = 0; i < brainCount; i++) {
      brainIds.add_11rb$(reader.readString());
    }
    return new PinkyPongMessage(brainIds);
  };
  PinkyPongMessage$Companion.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'Companion',
    interfaces: []
  };
  var PinkyPongMessage$Companion_instance = null;
  function PinkyPongMessage$Companion_getInstance() {
    if (PinkyPongMessage$Companion_instance === null) {
      new PinkyPongMessage$Companion();
    }
    return PinkyPongMessage$Companion_instance;
  }
  PinkyPongMessage.prototype.serialize_3kjoo0$ = function (writer) {
    writer.writeInt_za3lpa$(this.brainIds.size);
    var tmp$;
    tmp$ = this.brainIds.iterator();
    while (tmp$.hasNext()) {
      var element = tmp$.next();
      writer.writeString_61zpoe$(element);
    }
  };
  PinkyPongMessage.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'PinkyPongMessage',
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
  CompositorShader.prototype.createRenderer_ppt8xj$ = function (surface) {
    return new CompositorShader$Renderer(surface, this.aShader, this.bShader);
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
  function CompositorShader$Renderer(surface, aShader, bShader) {
    this.rendererA_0 = aShader.createRenderer_ppt8xj$(surface);
    this.rendererB_0 = bShader.createRenderer_ppt8xj$(surface);
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
    this.pixelVertices_0 = (tmp$_0 = Kotlin.isType(tmp$ = surface, Brain$MappedSurface) ? tmp$ : null) != null ? tmp$_0.pixelVertices : null;
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
  function PixelShader() {
    PixelShader$Companion_getInstance();
    Shader.call(this, ShaderId$PIXEL_getInstance());
  }
  PixelShader.prototype.createBuffer_ppt8xj$ = function (surface) {
    return new PixelShader$Buffer(this, surface.pixelCount);
  };
  PixelShader.prototype.createRenderer_ppt8xj$ = function (surface) {
    return new PixelShader$Renderer();
  };
  PixelShader.prototype.readBuffer_100t80$ = function (reader) {
    var incomingColorCount = reader.readInt();
    var buf = new PixelShader$Buffer(this, incomingColorCount);
    var tmp$;
    tmp$ = until(0, incomingColorCount).iterator();
    while (tmp$.hasNext()) {
      var element = tmp$.next();
      buf.colors[element] = Color$Companion_getInstance().parse_100t80$(reader);
    }
    return buf;
  };
  function PixelShader$Companion() {
    PixelShader$Companion_instance = this;
  }
  PixelShader$Companion.prototype.parse_100t80$ = function (reader) {
    return new PixelShader();
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
  function PixelShader$Buffer($outer, pixelCount) {
    this.$outer = $outer;
    this.colors = null;
    var tmp$;
    if (pixelCount === -1) {
      tmp$ = 2048;
    }
     else {
      tmp$ = pixelCount;
    }
    var bufPixelCount = tmp$;
    var array = Array_0(bufPixelCount);
    var tmp$_0;
    tmp$_0 = array.length - 1 | 0;
    for (var i = 0; i <= tmp$_0; i++) {
      array[i] = Color$Companion_getInstance().WHITE;
    }
    this.colors = array;
  }
  Object.defineProperty(PixelShader$Buffer.prototype, 'shader', {
    get: function () {
      return this.$outer;
    }
  });
  PixelShader$Buffer.prototype.serialize_3kjoo0$ = function (writer) {
    writer.writeInt_za3lpa$(this.colors.length);
    var $receiver = this.colors;
    var tmp$;
    for (tmp$ = 0; tmp$ !== $receiver.length; ++tmp$) {
      var element = $receiver[tmp$];
      element.serialize_3kjoo0$(writer);
    }
  };
  PixelShader$Buffer.prototype.read_100t80$ = function (reader) {
    var tmp$;
    var incomingColorCount = reader.readInt();
    var a = this.colors.length;
    var countFromBuffer = Math_0.min(a, incomingColorCount);
    for (var i = 0; i < countFromBuffer; i++) {
      this.colors[i] = Color$Companion_getInstance().parse_100t80$(reader);
    }
    tmp$ = this.colors.length;
    for (var i_0 = countFromBuffer; i_0 < tmp$; i_0++) {
      this.colors[i_0] = this.colors[i_0 % countFromBuffer];
    }
  };
  PixelShader$Buffer.prototype.setAll_rny0jj$ = function (color) {
    var tmp$;
    tmp$ = this.colors;
    for (var i = 0; i !== tmp$.length; ++i) {
      this.colors[i] = color;
    }
  };
  PixelShader$Buffer.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Buffer',
    interfaces: [Shader$Buffer]
  };
  function PixelShader$Renderer() {
  }
  PixelShader$Renderer.prototype.draw_b23bvv$ = function (buffer, pixelIndex) {
    return buffer.colors[pixelIndex];
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
    this.pixelVertices_0 = (tmp$_0 = Kotlin.isType(tmp$ = surface, Brain$MappedSurface) ? tmp$ : null) != null ? tmp$_0.pixelVertices : null;
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
    this.allShows = listOf([SolidColorShow_getInstance(), SomeDumbShow_getInstance(), RandomShow_getInstance(), CompositeShow_getInstance(), ThumpShow_getInstance(), PanelTweenShow_getInstance(), PixelTweenShow_getInstance(), LifeyShow_getInstance(), SimpleSpatialShow_getInstance(), HeartbleatShow_getInstance()]);
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
  function CompositeShow$createRenderer$ObjectLiteral(closure$showRunner, closure$sheepModel) {
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
    var $receiver_0 = closure$sheepModel.eyes;
    var destination = ArrayList_init_0(collectionSizeOrDefault($receiver_0, 10));
    var tmp$_0;
    tmp$_0 = $receiver_0.iterator();
    while (tmp$_0.hasNext()) {
      var item = tmp$_0.next();
      destination.add_11rb$(closure$showRunner.getMovingHead_1hma8m$(item));
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
      element_0.colorWheel = element_0.closestColorFor_rny0jj$(this.colorPicker.color);
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
  CompositeShow.prototype.createRenderer_h1b9op$ = function (sheepModel, showRunner) {
    return new CompositeShow$createRenderer$ObjectLiteral(showRunner, sheepModel);
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
  function HeartbleatShow() {
    HeartbleatShow_instance = this;
    Show.call(this, 'Heartbleat');
  }
  function HeartbleatShow$createRenderer$ObjectLiteral(closure$showRunner, closure$sheepModel) {
    this.beatProvider = closure$showRunner.getBeatProvider();
    var $receiver = closure$sheepModel.allPanels;
    var destination = ArrayList_init();
    var tmp$;
    tmp$ = $receiver.iterator();
    while (tmp$.hasNext()) {
      var element = tmp$.next();
      if (HeartbleatShow_getInstance().get_number_y56fi1$(element) === 7)
        destination.add_11rb$(element);
    }
    var destination_0 = ArrayList_init_0(collectionSizeOrDefault(destination, 10));
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
    var destination_1 = ArrayList_init_0(collectionSizeOrDefault($receiver_0, 10));
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
    var phase = this.beatProvider.beat % 1.0 * 3.0;
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
      var x_0 = this.beatProvider.beat / 4.0 * math.PI;
      element_0.color = tmp$_3.fade_6zkv30$(tmp$_4, Math_0.sin(x_0));
    }
  };
  HeartbleatShow$createRenderer$ObjectLiteral.$metadata$ = {
    kind: Kind_CLASS,
    interfaces: [Show$Renderer]
  };
  HeartbleatShow.prototype.createRenderer_h1b9op$ = function (sheepModel, showRunner) {
    return new HeartbleatShow$createRenderer$ObjectLiteral(showRunner, sheepModel);
  };
  HeartbleatShow.prototype.get_number_y56fi1$ = function ($receiver) {
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
  function LifeyShow$createRenderer$neighbors(closure$sheepModel) {
    return function ($receiver) {
      return closure$sheepModel.neighborsOf_jfju1k$($receiver);
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
  function LifeyShow$createRenderer$ObjectLiteral(closure$speedSlider, closure$lastUpdateMs, closure$selectedPanels, closure$sheepModel, closure$isSelected, closure$neighborsSelected, closure$neighbors, closure$shaderBuffers) {
    this.closure$speedSlider = closure$speedSlider;
    this.closure$lastUpdateMs = closure$lastUpdateMs;
    this.closure$selectedPanels = closure$selectedPanels;
    this.closure$sheepModel = closure$sheepModel;
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
        var $receiver = this.closure$sheepModel.allPanels;
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
      var panel = element_1.key;
      var buffer = element_1.value;
      buffer.color = closure$selectedPanels.contains_11rb$(panel) ? Color$Companion_getInstance().WHITE : Color$Companion_getInstance().BLACK;
    }
  };
  LifeyShow$createRenderer$ObjectLiteral.$metadata$ = {
    kind: Kind_CLASS,
    interfaces: [Show$Renderer]
  };
  LifeyShow.prototype.createRenderer_h1b9op$ = function (sheepModel, showRunner) {
    var speedSlider = showRunner.getGadget_vedre8$('speed', new Slider('Speed', 0.25));
    var shader = new SolidShader();
    var $receiver = sheepModel.allPanels;
    var result = LinkedHashMap_init_0(coerceAtLeast(mapCapacity(collectionSizeOrDefault($receiver, 10)), 16));
    var tmp$;
    tmp$ = $receiver.iterator();
    while (tmp$.hasNext()) {
      var element = tmp$.next();
      var tmp$_0 = result.put_xwzc9p$;
      var $receiver_0 = showRunner.getShaderBuffer_9rhubp$(element, shader);
      $receiver_0.color = Color$Companion_getInstance().WHITE;
      tmp$_0.call(result, element, $receiver_0);
    }
    var shaderBuffers = result;
    var selectedPanels = ArrayList_init();
    var lastUpdateMs = {v: L0};
    var neighbors = LifeyShow$createRenderer$neighbors(sheepModel);
    var isSelected = LifeyShow$createRenderer$isSelected(selectedPanels);
    var neighborsSelected = LifeyShow$createRenderer$neighborsSelected(neighbors, selectedPanels);
    return new LifeyShow$createRenderer$ObjectLiteral(speedSlider, lastUpdateMs, selectedPanels, sheepModel, isSelected, neighborsSelected, neighbors, shaderBuffers);
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
    var destination = ArrayList_init_0(collectionSizeOrDefault($receiver, 10));
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
  PanelTweenShow.prototype.createRenderer_h1b9op$ = function (sheepModel, showRunner) {
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
    var destination = ArrayList_init_0(collectionSizeOrDefault($receiver, 10));
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
      for (tmp$_1 = 0; tmp$_1 !== colors.length; ++tmp$_1) {
        var item_0 = colors[tmp$_1];
        var index_1 = (tmp$_0_0 = index_0, index_0 = tmp$_0_0 + 1 | 0, tmp$_0_0);
        if (Random.Default.nextFloat() < 0.1) {
          colors[index_1] = Color$Companion_getInstance().WHITE;
        }
         else {
          var tweenedColor = startColor.fade_6zkv30$(endColor, (now + index_1 | 0) % this.fadeTimeMs / this.fadeTimeMs);
          colors[index_1] = tweenedColor;
        }
      }
    }
  };
  PixelTweenShow$createRenderer$ObjectLiteral.$metadata$ = {
    kind: Kind_CLASS,
    interfaces: [Show$Renderer]
  };
  PixelTweenShow.prototype.createRenderer_h1b9op$ = function (sheepModel, showRunner) {
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
  function RandomShow$createRenderer$ObjectLiteral(closure$showRunner, closure$sheepModel) {
    var $receiver = closure$showRunner.allSurfaces;
    var destination = ArrayList_init_0(collectionSizeOrDefault($receiver, 10));
    var tmp$;
    tmp$ = $receiver.iterator();
    while (tmp$.hasNext()) {
      var item = tmp$.next();
      destination.add_11rb$(closure$showRunner.getShaderBuffer_9rhubp$(item, new RandomShader()));
    }
    var $receiver_0 = closure$sheepModel.eyes;
    var destination_0 = ArrayList_init_0(collectionSizeOrDefault($receiver_0, 10));
    var tmp$_0;
    tmp$_0 = $receiver_0.iterator();
    while (tmp$_0.hasNext()) {
      var item_0 = tmp$_0.next();
      destination_0.add_11rb$(closure$showRunner.getMovingHead_1hma8m$(item_0));
    }
    this.movingHeadBuffers = destination_0;
  }
  RandomShow$createRenderer$ObjectLiteral.prototype.nextFrame = function () {
    var tmp$;
    tmp$ = this.movingHeadBuffers.iterator();
    while (tmp$.hasNext()) {
      var element = tmp$.next();
      element.colorWheel = element.closestColorFor_rny0jj$(Color$Companion_getInstance().random());
      element.pan = Random.Default.nextFloat() * Shenzarpy$Companion_getInstance().panRange.endInclusive;
      element.tilt = Random.Default.nextFloat() * Shenzarpy$Companion_getInstance().tiltRange.endInclusive;
    }
  };
  RandomShow$createRenderer$ObjectLiteral.$metadata$ = {
    kind: Kind_CLASS,
    interfaces: [Show$Renderer]
  };
  RandomShow.prototype.createRenderer_h1b9op$ = function (sheepModel, showRunner) {
    return new RandomShow$createRenderer$ObjectLiteral(showRunner, sheepModel);
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
  SimpleSpatialShow.prototype.createRenderer_h1b9op$ = function (sheepModel, showRunner) {
    var colorPicker = showRunner.getGadget_vedre8$('color', new ColorPicker('Color'));
    var centerXSlider = showRunner.getGadget_vedre8$('centerX', new Slider('center X', 0.5));
    var centerYSlider = showRunner.getGadget_vedre8$('centerY', new Slider('center Y', 0.5));
    var radiusSlider = showRunner.getGadget_vedre8$('radius', new Slider('radius', 0.25));
    var shader = new SimpleSpatialShader();
    var $receiver = showRunner.allSurfaces;
    var destination = ArrayList_init_0(collectionSizeOrDefault($receiver, 10));
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
  function SolidColorShow$createRenderer$ObjectLiteral(closure$colorPicker, closure$shaderBuffers) {
    this.closure$colorPicker = closure$colorPicker;
    this.closure$shaderBuffers = closure$shaderBuffers;
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
      this.priorColor = color.v;
    }
  };
  SolidColorShow$createRenderer$ObjectLiteral.$metadata$ = {
    kind: Kind_CLASS,
    interfaces: [Show$Renderer]
  };
  SolidColorShow.prototype.createRenderer_h1b9op$ = function (sheepModel, showRunner) {
    var colorPicker = showRunner.getGadget_vedre8$('color', new ColorPicker('Color'));
    var shader = new SolidShader();
    var $receiver = showRunner.allSurfaces;
    var destination = ArrayList_init_0(collectionSizeOrDefault($receiver, 10));
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
    return new SolidColorShow$createRenderer$ObjectLiteral(colorPicker, shaderBuffers);
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
  function SomeDumbShow$createRenderer$ObjectLiteral(closure$showRunner, closure$sheepModel) {
    this.colorPicker = closure$showRunner.getGadget_vedre8$('color', new ColorPicker('Color'));
    this.pixelShader = new PixelShader();
    var $receiver = closure$showRunner.allSurfaces;
    var destination = ArrayList_init_0(collectionSizeOrDefault($receiver, 10));
    var tmp$;
    tmp$ = $receiver.iterator();
    while (tmp$.hasNext()) {
      var item = tmp$.next();
      destination.add_11rb$(closure$showRunner.getShaderBuffer_9rhubp$(item, this.pixelShader));
    }
    this.pixelShaderBuffers = destination;
    var $receiver_0 = closure$sheepModel.eyes;
    var destination_0 = ArrayList_init_0(collectionSizeOrDefault($receiver_0, 10));
    var tmp$_0;
    tmp$_0 = $receiver_0.iterator();
    while (tmp$_0.hasNext()) {
      var item_0 = tmp$_0.next();
      destination_0.add_11rb$(closure$showRunner.getMovingHead_1hma8m$(item_0));
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
      for (var i = 0; i !== tmp$_0.length; ++i) {
        element.colors[i] = desaturateRandomishly(panelColor, baseSaturation, seed);
      }
    }
    var tmp$_1;
    tmp$_1 = this.movingHeads.iterator();
    while (tmp$_1.hasNext()) {
      var element_0 = tmp$_1.next();
      element_0.colorWheel = element_0.closestColorFor_rny0jj$(this.colorPicker.color);
      element_0.pan = element_0.pan + (nextTimeShiftedFloat(seed) - 0.5) / 5;
      element_0.tilt = element_0.tilt + (nextTimeShiftedFloat(seed) - 0.5) / 5;
    }
  };
  SomeDumbShow$createRenderer$ObjectLiteral.$metadata$ = {
    kind: Kind_CLASS,
    interfaces: [Show$Renderer]
  };
  SomeDumbShow.prototype.createRenderer_h1b9op$ = function (sheepModel, showRunner) {
    return new SomeDumbShow$createRenderer$ObjectLiteral(showRunner, sheepModel);
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
  function ThumpShow$createRenderer$ObjectLiteral(closure$showRunner, closure$sheepModel) {
    this.beatProvider_0 = closure$showRunner.getBeatProvider();
    this.colorPicker = closure$showRunner.getGadget_vedre8$('color', new ColorPicker('Color'));
    this.solidShader = new SolidShader();
    this.sineWaveShader = new SineWaveShader();
    this.compositorShader = new CompositorShader(this.solidShader, this.sineWaveShader);
    var $receiver = closure$showRunner.allSurfaces;
    var destination = ArrayList_init_0(collectionSizeOrDefault($receiver, 10));
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
    var $receiver_1 = closure$sheepModel.eyes;
    var destination_0 = ArrayList_init_0(collectionSizeOrDefault($receiver_1, 10));
    var tmp$_1;
    tmp$_1 = $receiver_1.iterator();
    while (tmp$_1.hasNext()) {
      var item_0 = tmp$_1.next();
      destination_0.add_11rb$(closure$showRunner.getMovingHead_1hma8m$(item_0));
    }
    this.movingHeadBuffers_0 = destination_0;
  }
  ThumpShow$createRenderer$ObjectLiteral.prototype.nextFrame = function () {
    var theta = getTimeMillis().toNumber() / 1000.0 % (2 * math.PI);
    var beat = this.beatProvider_0.beat;
    var i = {v: 0};
    var tmp$;
    tmp$ = this.shaderBufs_0.iterator();
    while (tmp$.hasNext()) {
      var element = tmp$.next();
      var tmp$_0;
      element.solidShaderBuffer.color = Color$Companion_getInstance().BLACK.fade_6zkv30$(this.colorPicker.color, beat % 1.0);
      element.sineWaveShaderBuffer.color = beat < 0.2 ? Color$Companion_getInstance().WHITE : Color$Companion_getInstance().ORANGE;
      element.sineWaveShaderBuffer.theta = theta + (tmp$_0 = i.v, i.v = tmp$_0 + 1 | 0, tmp$_0);
      element.compositorShaderBuffer.mode = CompositingMode$ADD_getInstance();
      element.compositorShaderBuffer.fade = 1.0;
    }
    var tmp$_1;
    tmp$_1 = this.movingHeadBuffers_0.iterator();
    while (tmp$_1.hasNext()) {
      var element_0 = tmp$_1.next();
      element_0.colorWheel = element_0.closestColorFor_rny0jj$(this.colorPicker.color);
      element_0.pan = math.PI / 2;
      element_0.tilt = beat / math.PI;
    }
  };
  ThumpShow$createRenderer$ObjectLiteral.$metadata$ = {
    kind: Kind_CLASS,
    interfaces: [Show$Renderer]
  };
  ThumpShow.prototype.createRenderer_h1b9op$ = function (sheepModel, showRunner) {
    return new ThumpShow$createRenderer$ObjectLiteral(showRunner, sheepModel);
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
    for (var i = 0; i <= 512; i++)
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
    this.tcpServerSocketsByPort_0 = HashMap_init();
  }
  FakeNetwork.prototype.link = function () {
    var tmp$;
    var address = new FakeNetwork$FakeAddress((tmp$ = this.nextAddress_0, this.nextAddress_0 = tmp$ + 1 | 0, tmp$));
    return new FakeNetwork$FakeLink(this, address);
  };
  FakeNetwork.prototype.listenUdp_0 = function (address, port, udpListener) {
    this.udpListeners_0.put_xwzc9p$(new Pair(address, port), udpListener);
    var $receiver = this.udpListenersByPort_0;
    var tmp$;
    var value = $receiver.get_11rb$(port);
    if (value == null) {
      var answer = ArrayList_init();
      $receiver.put_xwzc9p$(port, answer);
      tmp$ = answer;
    }
     else {
      tmp$ = value;
    }
    var portListeners = tmp$;
    portListeners.add_11rb$(udpListener);
  };
  FakeNetwork.prototype.sendUdp_0 = function (fromAddress, toAddress, port, bytes) {
    var tmp$;
    if (!this.sendPacketSuccess_0()) {
      (tmp$ = this.display_0) != null ? (tmp$.droppedPacket(), Unit) : null;
      return;
    }
    var listener = this.udpListeners_0.get_11rb$(new Pair(toAddress, port));
    if (listener != null)
      this.transmitUdp_0(fromAddress, listener, bytes);
  };
  FakeNetwork.prototype.broadcastUdp_0 = function (fromAddress, port, bytes) {
    var tmp$, tmp$_0;
    if (!this.sendPacketSuccess_0()) {
      (tmp$ = this.display_0) != null ? (tmp$.droppedPacket(), Unit) : null;
      return;
    }
    if ((tmp$_0 = this.udpListenersByPort_0.get_11rb$(port)) != null) {
      var tmp$_1;
      tmp$_1 = tmp$_0.iterator();
      while (tmp$_1.hasNext()) {
        var element = tmp$_1.next();
        this.transmitUdp_0(fromAddress, element, bytes);
      }
    }
  };
  function Coroutine$FakeNetwork$transmitUdp$lambda(this$FakeNetwork_0, closure$udpListener_0, closure$fromAddress_0, closure$bytes_0, $receiver_0, controller, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.$controller = controller;
    this.exceptionState_0 = 1;
    this.local$this$FakeNetwork = this$FakeNetwork_0;
    this.local$closure$udpListener = closure$udpListener_0;
    this.local$closure$fromAddress = closure$fromAddress_0;
    this.local$closure$bytes = closure$bytes_0;
  }
  Coroutine$FakeNetwork$transmitUdp$lambda.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$FakeNetwork$transmitUdp$lambda.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$FakeNetwork$transmitUdp$lambda.prototype.constructor = Coroutine$FakeNetwork$transmitUdp$lambda;
  Coroutine$FakeNetwork$transmitUdp$lambda.prototype.doResume = function () {
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
              return this.local$closure$udpListener.receive_rq4egf$(this.local$closure$fromAddress, this.local$closure$bytes), Unit;
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
  function FakeNetwork$transmitUdp$lambda(this$FakeNetwork_0, closure$udpListener_0, closure$fromAddress_0, closure$bytes_0) {
    return function ($receiver_0, continuation_0, suspended) {
      var instance = new Coroutine$FakeNetwork$transmitUdp$lambda(this$FakeNetwork_0, closure$udpListener_0, closure$fromAddress_0, closure$bytes_0, $receiver_0, this, continuation_0);
      if (suspended)
        return instance;
      else
        return instance.doResume(null);
    };
  }
  FakeNetwork.prototype.transmitUdp_0 = function (fromAddress, udpListener, bytes) {
    launch(this.coroutineScope_0, void 0, void 0, FakeNetwork$transmitUdp$lambda(this, udpListener, fromAddress, bytes));
  };
  FakeNetwork.prototype.listenTcp_0 = function (myAddress, port, tcpServerSocketListener) {
    var $receiver = this.tcpServerSocketsByPort_0;
    var key = new Pair(myAddress, port);
    $receiver.put_xwzc9p$(key, tcpServerSocketListener);
  };
  function Coroutine$FakeNetwork$connectTcp$lambda(this$FakeNetwork_0, closure$clientListener_0, closure$connection_0, $receiver_0, controller, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.$controller = controller;
    this.exceptionState_0 = 1;
    this.local$this$FakeNetwork = this$FakeNetwork_0;
    this.local$closure$clientListener = closure$clientListener_0;
    this.local$closure$connection = closure$connection_0;
  }
  Coroutine$FakeNetwork$connectTcp$lambda.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$FakeNetwork$connectTcp$lambda.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$FakeNetwork$connectTcp$lambda.prototype.constructor = Coroutine$FakeNetwork$connectTcp$lambda;
  Coroutine$FakeNetwork$connectTcp$lambda.prototype.doResume = function () {
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
            return this.local$closure$clientListener.reset_67ozxy$(this.local$closure$connection), Unit;
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
  function FakeNetwork$connectTcp$lambda(this$FakeNetwork_0, closure$clientListener_0, closure$connection_0) {
    return function ($receiver_0, continuation_0, suspended) {
      var instance = new Coroutine$FakeNetwork$connectTcp$lambda(this$FakeNetwork_0, closure$clientListener_0, closure$connection_0, $receiver_0, this, continuation_0);
      if (suspended)
        return instance;
      else
        return instance.doResume(null);
    };
  }
  function FakeNetwork$connectTcp$lambda_0(closure$clientSideConnection) {
    return function () {
      return closure$clientSideConnection.v == null ? throwUPAE('clientSideConnection') : closure$clientSideConnection.v;
    };
  }
  function FakeNetwork$connectTcp$lambda_1(closure$serverSideConnection) {
    return function () {
      return closure$serverSideConnection;
    };
  }
  function Coroutine$FakeNetwork$connectTcp$lambda_0(this$FakeNetwork_0, closure$clientListener_0, closure$clientSideConnection_0, $receiver_0, controller, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.$controller = controller;
    this.exceptionState_0 = 1;
    this.local$this$FakeNetwork = this$FakeNetwork_0;
    this.local$closure$clientListener = closure$clientListener_0;
    this.local$closure$clientSideConnection = closure$clientSideConnection_0;
  }
  Coroutine$FakeNetwork$connectTcp$lambda_0.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$FakeNetwork$connectTcp$lambda_0.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$FakeNetwork$connectTcp$lambda_0.prototype.constructor = Coroutine$FakeNetwork$connectTcp$lambda_0;
  Coroutine$FakeNetwork$connectTcp$lambda_0.prototype.doResume = function () {
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
            return this.local$closure$clientListener.connected_67ozxy$(this.local$closure$clientSideConnection.v == null ? throwUPAE('clientSideConnection') : this.local$closure$clientSideConnection.v), Unit;
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
  function FakeNetwork$connectTcp$lambda_2(this$FakeNetwork_0, closure$clientListener_0, closure$clientSideConnection_0) {
    return function ($receiver_0, continuation_0, suspended) {
      var instance = new Coroutine$FakeNetwork$connectTcp$lambda_0(this$FakeNetwork_0, closure$clientListener_0, closure$clientSideConnection_0, $receiver_0, this, continuation_0);
      if (suspended)
        return instance;
      else
        return instance.doResume(null);
    };
  }
  function Coroutine$FakeNetwork$connectTcp$lambda_1(this$FakeNetwork_0, closure$serverListener_0, closure$serverSideConnection_0, $receiver_0, controller, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.$controller = controller;
    this.exceptionState_0 = 1;
    this.local$this$FakeNetwork = this$FakeNetwork_0;
    this.local$closure$serverListener = closure$serverListener_0;
    this.local$closure$serverSideConnection = closure$serverSideConnection_0;
  }
  Coroutine$FakeNetwork$connectTcp$lambda_1.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$FakeNetwork$connectTcp$lambda_1.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$FakeNetwork$connectTcp$lambda_1.prototype.constructor = Coroutine$FakeNetwork$connectTcp$lambda_1;
  Coroutine$FakeNetwork$connectTcp$lambda_1.prototype.doResume = function () {
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
  function FakeNetwork$connectTcp$lambda_3(this$FakeNetwork_0, closure$serverListener_0, closure$serverSideConnection_0) {
    return function ($receiver_0, continuation_0, suspended) {
      var instance = new Coroutine$FakeNetwork$connectTcp$lambda_1(this$FakeNetwork_0, closure$serverListener_0, closure$serverSideConnection_0, $receiver_0, this, continuation_0);
      if (suspended)
        return instance;
      else
        return instance.doResume(null);
    };
  }
  FakeNetwork.prototype.connectTcp_0 = function (clientAddress, serverAddress, serverPort, clientListener) {
    var serverSocketListener = this.tcpServerSocketsByPort_0.get_11rb$(new Pair(serverAddress, serverPort));
    if (serverSocketListener == null) {
      var connection = new FakeNetwork$FakeTcpConnection(this, clientAddress, serverAddress, serverPort, null);
      launch(this.coroutineScope_0, void 0, void 0, FakeNetwork$connectTcp$lambda(this, clientListener, connection));
      return connection;
    }
    var clientSideConnection = {v: null};
    var serverSideConnection = new FakeNetwork$FakeTcpConnection(this, clientAddress, serverAddress, serverPort, clientListener, FakeNetwork$connectTcp$lambda_0(clientSideConnection));
    var serverListener = serverSocketListener.incomingConnection_67ozxy$(serverSideConnection);
    clientSideConnection.v = new FakeNetwork$FakeTcpConnection(this, clientAddress, serverAddress, serverPort, serverListener, FakeNetwork$connectTcp$lambda_1(serverSideConnection));
    launch(this.coroutineScope_0, void 0, void 0, FakeNetwork$connectTcp$lambda_2(this, clientListener, clientSideConnection));
    launch(this.coroutineScope_0, void 0, void 0, FakeNetwork$connectTcp$lambda_3(this, serverListener, serverSideConnection));
    return clientSideConnection.v == null ? throwUPAE('clientSideConnection') : clientSideConnection.v;
  };
  function FakeNetwork$FakeTcpConnection($outer, fromAddress, toAddress, port, tcpListener, otherListener) {
    this.$outer = $outer;
    if (tcpListener === void 0)
      tcpListener = null;
    if (otherListener === void 0)
      otherListener = null;
    this.fromAddress_v9jzbw$_0 = fromAddress;
    this.toAddress_vzjv7v$_0 = toAddress;
    this.port_djnxqd$_0 = port;
    this.tcpListener_0 = tcpListener;
    this.otherListener_0 = otherListener;
  }
  Object.defineProperty(FakeNetwork$FakeTcpConnection.prototype, 'fromAddress', {
    get: function () {
      return this.fromAddress_v9jzbw$_0;
    }
  });
  Object.defineProperty(FakeNetwork$FakeTcpConnection.prototype, 'toAddress', {
    get: function () {
      return this.toAddress_vzjv7v$_0;
    }
  });
  Object.defineProperty(FakeNetwork$FakeTcpConnection.prototype, 'port', {
    get: function () {
      return this.port_djnxqd$_0;
    }
  });
  function Coroutine$FakeNetwork$FakeTcpConnection$send$lambda(this$FakeTcpConnection_0, closure$bytes_0, $receiver_0, controller, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.$controller = controller;
    this.exceptionState_0 = 1;
    this.local$this$FakeTcpConnection = this$FakeTcpConnection_0;
    this.local$closure$bytes = closure$bytes_0;
  }
  Coroutine$FakeNetwork$FakeTcpConnection$send$lambda.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$FakeNetwork$FakeTcpConnection$send$lambda.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$FakeNetwork$FakeTcpConnection$send$lambda.prototype.constructor = Coroutine$FakeNetwork$FakeTcpConnection$send$lambda;
  Coroutine$FakeNetwork$FakeTcpConnection$send$lambda.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            var tmp$;
            return (tmp$ = this.local$this$FakeTcpConnection.tcpListener_0) != null ? (tmp$.receive_r00qii$(ensureNotNull(this.local$this$FakeTcpConnection.otherListener_0)(), this.local$closure$bytes), Unit) : null;
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
  function FakeNetwork$FakeTcpConnection$send$lambda(this$FakeTcpConnection_0, closure$bytes_0) {
    return function ($receiver_0, continuation_0, suspended) {
      var instance = new Coroutine$FakeNetwork$FakeTcpConnection$send$lambda(this$FakeTcpConnection_0, closure$bytes_0, $receiver_0, this, continuation_0);
      if (suspended)
        return instance;
      else
        return instance.doResume(null);
    };
  }
  FakeNetwork$FakeTcpConnection.prototype.send_fqrh44$ = function (bytes) {
    launch(this.$outer.coroutineScope_0, void 0, void 0, FakeNetwork$FakeTcpConnection$send$lambda(this, bytes));
  };
  FakeNetwork$FakeTcpConnection.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'FakeTcpConnection',
    interfaces: [Network$TcpConnection]
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
    this.$outer.listenUdp_0(this.myAddress, port, udpListener);
  };
  FakeNetwork$FakeLink.prototype.sendUdp_ytpeqp$ = function (toAddress, port, bytes) {
    this.$outer.sendUdp_0(this.myAddress, toAddress, port, bytes);
  };
  FakeNetwork$FakeLink.prototype.broadcastUdp_3fbn1q$ = function (port, bytes) {
    this.$outer.broadcastUdp_0(this.myAddress, port, bytes);
  };
  FakeNetwork$FakeLink.prototype.listenTcp_kd29r4$ = function (port, tcpServerSocketListener) {
    this.$outer.listenTcp_0(this.myAddress, port, tcpServerSocketListener);
  };
  FakeNetwork$FakeLink.prototype.connectTcp_dy234z$ = function (toAddress, port, tcpListener) {
    return this.$outer.connectTcp_0(this.myAddress, toAddress, port, tcpListener);
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
  function time(function_0) {
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
    this.packetLossRate = 0.05;
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
    this.nextFrameMs_yg3jc4$_0 = 0;
    this.stats_qcsqqr$_0 = null;
    this.brainCountDiv_0 = null;
    this.beat1_0 = null;
    this.beat2_0 = null;
    this.beat3_0 = null;
    this.beat4_0 = null;
    this.beats_0 = null;
    this.showList_0 = emptyList();
    this.showListInput_0 = null;
    this.nextFrameElapsed_0 = null;
    this.statsSpan_0 = null;
    var tmp$;
    appendText(element, 'Brains online: ');
    this.brainCountDiv_0 = appendElement(element, 'span', JsPinkyDisplay_init$lambda);
    var beatsDiv = appendElement(element, 'div', JsPinkyDisplay_init$lambda_0);
    this.beat1_0 = appendElement(beatsDiv, 'span', JsPinkyDisplay_init$lambda_1);
    this.beat2_0 = appendElement(beatsDiv, 'span', JsPinkyDisplay_init$lambda_2);
    this.beat3_0 = appendElement(beatsDiv, 'span', JsPinkyDisplay_init$lambda_3);
    this.beat4_0 = appendElement(beatsDiv, 'span', JsPinkyDisplay_init$lambda_4);
    this.beats_0 = listOf([this.beat1_0, this.beat2_0, this.beat3_0, this.beat4_0]);
    appendElement(element, 'b', JsPinkyDisplay_init$lambda_5);
    this.showListInput_0 = Kotlin.isType(tmp$ = appendElement(element, 'select', JsPinkyDisplay_init$lambda_6), HTMLSelectElement) ? tmp$ : throwCCE();
    this.showListInput_0.onchange = JsPinkyDisplay_init$lambda_7(this);
    appendText(element, '.nextFrame(): ');
    this.nextFrameElapsed_0 = appendElement(element, 'span', JsPinkyDisplay_init$lambda_8);
    appendElement(element, 'br', JsPinkyDisplay_init$lambda_9);
    appendElement(element, 'b', JsPinkyDisplay_init$lambda_10);
    this.statsSpan_0 = appendElement(element, 'span', JsPinkyDisplay_init$lambda_11);
    this.brainCount_tt9c5b$_0 = 0;
    this.beat_o13evy$_0 = 0;
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
  Object.defineProperty(JsPinkyDisplay.prototype, 'nextFrameMs', {
    get: function () {
      return this.nextFrameMs_yg3jc4$_0;
    },
    set: function (value) {
      this.nextFrameMs_yg3jc4$_0 = value;
      this.nextFrameElapsed_0.textContent = value.toString() + 'ms';
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
      clear_0(this.beats_0.get_za3lpa$(this.beat_o13evy$_0).classList);
      this.beats_0.get_za3lpa$(value).classList.add('selected');
      this.beat_o13evy$_0 = value;
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
    appendText($receiver, '1');
    return Unit;
  }
  function JsPinkyDisplay_init$lambda_2($receiver) {
    appendText($receiver, '2');
    return Unit;
  }
  function JsPinkyDisplay_init$lambda_3($receiver) {
    appendText($receiver, '3');
    return Unit;
  }
  function JsPinkyDisplay_init$lambda_4($receiver) {
    appendText($receiver, '4');
    return Unit;
  }
  function JsPinkyDisplay_init$lambda_5($receiver) {
    appendText($receiver, 'Renderer: ');
    return Unit;
  }
  function JsPinkyDisplay_init$lambda_6($receiver) {
    $receiver.className = 'showsDiv';
    return Unit;
  }
  function JsPinkyDisplay_init$lambda_7(this$JsPinkyDisplay) {
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
  function JsPinkyDisplay_init$lambda_8($receiver) {
    return Unit;
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
  function Comparator$ObjectLiteral(closure$comparison) {
    this.closure$comparison = closure$comparison;
  }
  Comparator$ObjectLiteral.prototype.compare = function (a, b) {
    return this.closure$comparison(a, b);
  };
  Comparator$ObjectLiteral.$metadata$ = {kind: Kind_CLASS, interfaces: [Comparator]};
  var compareBy$lambda = wrapFunction(function () {
    var compareValues = Kotlin.kotlin.comparisons.compareValues_s00gnj$;
    return function (closure$selector) {
      return function (a, b) {
        var selector = closure$selector;
        return compareValues(selector(a), selector(b));
      };
    };
  });
  function JsMapperDisplay(visualizer) {
    this.visualizer_0 = visualizer;
    this.listener_h0bbis$_0 = this.listener_h0bbis$_0;
    this.width_0 = 512;
    this.height_0 = 384;
    this.clock_0 = new Clock();
    this.uiRenderer_0 = new WebGLRenderer_init({alpha: true});
    this.uiScene_0 = new Scene();
    this.uiCamera_0 = new PerspectiveCamera_init(45, this.width_0 / this.height_0, 1, 10000);
    this.uiControls_0 = null;
    this.wireframe_0 = new Object3D();
    this.screen_0 = div_0(get_create(document), 'mapperUi-screen', JsMapperDisplay$screen$lambda(this));
    this.ui2dCanvas_0 = first_0(this.screen_0, 'mapperUi-2d-canvas');
    this.ui2dCtx_0 = context2d(this.ui2dCanvas_0);
    this.ui3dDiv_0 = first_0(this.screen_0, 'mapperUi-3d-div');
    var tmp$;
    this.ui3dCanvas_0 = Kotlin.isType(tmp$ = this.uiRenderer_0.domElement, HTMLCanvasElement) ? tmp$ : throwCCE();
    this.diffCanvas_0 = first_0(this.screen_0, 'mapperUi-diff-canvas');
    this.diffCtx_0 = context2d(this.diffCanvas_0);
    this.changeRegion_0 = null;
    this.statsDiv_0 = first_0(this.screen_0, 'mapperUi-stats');
    this.messageDiv_0 = first_0(this.screen_0, 'mapperUi-message');
    this.message2Div_0 = first_0(this.screen_0, 'mapperUi-message2');
    this.table_0 = first_0(this.screen_0, 'mapperUi-table');
    this.visiblePanels_0 = ArrayList_init();
    this.panelInfos_0 = LinkedHashMap_init();
    this.visualizer_0.mapperIsRunning = true;
    this.ui3dDiv_0.appendChild(this.ui3dCanvas_0);
    this.uiCamera_0.position.z = 1000.0;
    this.uiScene_0.add(this.uiCamera_0);
    this.uiControls_0 = document.createCameraControls(this.uiCamera_0, this.uiRenderer_0.domElement);
  }
  Object.defineProperty(JsMapperDisplay.prototype, 'listener_0', {
    get: function () {
      if (this.listener_h0bbis$_0 == null)
        return throwUPAE('listener');
      return this.listener_h0bbis$_0;
    },
    set: function (listener) {
      this.listener_h0bbis$_0 = listener;
    }
  });
  JsMapperDisplay.prototype.listen_uasn0l$ = function (listener) {
    this.listener_0 = listener;
  };
  function JsMapperDisplay$render$lambda(closure$parentNode, this$JsMapperDisplay) {
    return function (it) {
      this$JsMapperDisplay.resizeTo_0(closure$parentNode.offsetWidth, closure$parentNode.offsetHeight);
      return Unit;
    };
  }
  JsMapperDisplay.prototype.render = function (parentNode) {
    parentNode.appendChild(this.screen_0);
    parentNode.onresize = JsMapperDisplay$render$lambda(parentNode, this);
    this.resizeTo_0(this.width_0, this.height_0);
  };
  JsMapperDisplay.prototype.onClose = function () {
    this.visualizer_0.mapperIsRunning = false;
    this.listener_0.onClose();
  };
  JsMapperDisplay.prototype.resizeTo_0 = function (width, height) {
    var tmp$, tmp$_0;
    this.width_0 = width;
    this.height_0 = height;
    this.uiCamera_0.aspect = width / height;
    this.uiCamera_0.updateProjectionMatrix();
    this.uiRenderer_0.setSize(width, height);
    this.uiRenderer_0.setPixelRatio(width / height);
    (Kotlin.isType(tmp$ = this.uiRenderer_0.domElement, HTMLCanvasElement) ? tmp$ : throwCCE()).width = width;
    (Kotlin.isType(tmp$_0 = this.uiRenderer_0.domElement, HTMLCanvasElement) ? tmp$_0 : throwCCE()).height = height;
    this.ui2dCanvas_0.width = width;
    this.ui2dCanvas_0.height = height;
    this.diffCanvas_0.width = width;
    this.diffCanvas_0.height = height;
  };
  JsMapperDisplay.prototype.addWireframe_9u144y$ = function (sheepModel) {
    var $receiver = new LineBasicMaterial();
    $receiver.color = new Color_init(0.0, 1.0, 0.0);
    var lineMaterial = $receiver;
    var $receiver_0 = sheepModel.vertices;
    var destination = ArrayList_init_0(collectionSizeOrDefault($receiver_0, 10));
    var tmp$;
    tmp$ = $receiver_0.iterator();
    while (tmp$.hasNext()) {
      var item = tmp$.next();
      destination.add_11rb$(new Vector3(item.x, item.y, item.z));
    }
    var vertices = copyToArray(destination);
    var container = new Object3D();
    var tmp$_0;
    tmp$_0 = sheepModel.panels.iterator();
    while (tmp$_0.hasNext()) {
      var element = tmp$_0.next();
      var geom = new Geometry();
      var allFaces = ArrayList_init();
      geom.vertices = vertices;
      var panelFaces = ArrayList_init();
      var faceNormal = {v: new Vector3()};
      var tmp$_1;
      tmp$_1 = element.faces.faces.iterator();
      while (tmp$_1.hasNext()) {
        var element_0 = tmp$_1.next();
        var face3 = new Face3_init(element_0.vertexIds.get_za3lpa$(0), element_0.vertexIds.get_za3lpa$(1), element_0.vertexIds.get_za3lpa$(2), new Vector3(0, 0, 0));
        allFaces.add_11rb$(face3);
        panelFaces.add_11rb$(face3);
        geom.faces = [face3];
        geom.computeFaceNormals();
        faceNormal.v = ensureNotNull(face3.normal);
      }
      var $receiver_1 = new MeshBasicMaterial();
      $receiver_1.color = new Color_init(0, 0, 0);
      var panelMaterial = $receiver_1;
      var mesh = new Mesh_init(geom, panelMaterial);
      this.uiScene_0.add(mesh);
      var tmp$_2;
      tmp$_2 = element.lines.iterator();
      while (tmp$_2.hasNext()) {
        var element_1 = tmp$_2.next();
        var lineGeom = new BufferGeometry();
        var $receiver_2 = element_1.points;
        var destination_0 = ArrayList_init_0(collectionSizeOrDefault($receiver_2, 10));
        var tmp$_3;
        tmp$_3 = $receiver_2.iterator();
        while (tmp$_3.hasNext()) {
          var item_0 = tmp$_3.next();
          destination_0.add_11rb$(plus_0(new Vector3(item_0.x, item_0.y, item_0.z), faceNormal.v));
        }
        lineGeom.setFromPoints(copyToArray(destination_0));
        this.wireframe_0.add(new Line_init(lineGeom, lineMaterial));
      }
      geom.faces = copyToArray(allFaces);
      geom.computeFaceNormals();
      geom.computeVertexNormals();
      var $receiver_3 = this.panelInfos_0;
      var value = new PanelInfo(panelFaces, mesh, geom);
      $receiver_3.put_xwzc9p$(element, value);
    }
    this.uiScene_0.add(this.wireframe_0);
    var tmp$_4 = new SphereBufferGeometry(1, 32, 32);
    var $receiver_4 = new MeshBasicMaterial();
    $receiver_4.color = new Color_init(16711680);
    var originMarker = new Mesh_init(tmp$_4, $receiver_4);
    this.uiScene_0.add(originMarker);
    var boundingBox = (new Box3()).setFromObject(container);
    var centerOfSheep = boundingBox.getCenter().clone();
    this.uiCamera_0.lookAt(centerOfSheep);
  };
  function JsMapperDisplay$getCandidateSurfaces$lambda(closure$panelRects, closure$changeRegion) {
    return function (f) {
      var panel = f.component1();
      return ensureNotNull(closure$panelRects.get_11rb$(panel)).distanceTo_gdgylh$(closure$changeRegion);
    };
  }
  function JsMapperDisplay$getCandidateSurfaces$lambda$lambda$lambda$lambda($receiver) {
    $receiver.unaryPlus_pdl1vz$('Panel');
    return Unit;
  }
  function JsMapperDisplay$getCandidateSurfaces$lambda$lambda$lambda$lambda_0($receiver) {
    $receiver.unaryPlus_pdl1vz$('Centroid dist');
    return Unit;
  }
  function JsMapperDisplay$getCandidateSurfaces$lambda$lambda$lambda($receiver) {
    th($receiver, void 0, void 0, JsMapperDisplay$getCandidateSurfaces$lambda$lambda$lambda$lambda);
    th($receiver, void 0, void 0, JsMapperDisplay$getCandidateSurfaces$lambda$lambda$lambda$lambda_0);
    return Unit;
  }
  function JsMapperDisplay$getCandidateSurfaces$lambda$lambda$lambda$lambda$lambda(closure$panel) {
    return function ($receiver) {
      $receiver.unaryPlus_pdl1vz$(closure$panel.name);
      return Unit;
    };
  }
  function JsMapperDisplay$getCandidateSurfaces$lambda$lambda$lambda$lambda$lambda_0(closure$panelRects, closure$panel, closure$changeRegion) {
    return function ($receiver) {
      $receiver.unaryPlus_pdl1vz$(ensureNotNull(closure$panelRects.get_11rb$(closure$panel)).distanceTo_gdgylh$(closure$changeRegion).toString());
      return Unit;
    };
  }
  function JsMapperDisplay$getCandidateSurfaces$lambda$lambda$lambda$lambda_1(closure$panel, closure$panelRects, closure$changeRegion) {
    return function ($receiver) {
      td($receiver, void 0, JsMapperDisplay$getCandidateSurfaces$lambda$lambda$lambda$lambda$lambda(closure$panel));
      td($receiver, void 0, JsMapperDisplay$getCandidateSurfaces$lambda$lambda$lambda$lambda$lambda_0(closure$panelRects, closure$panel, closure$changeRegion));
      return Unit;
    };
  }
  function JsMapperDisplay$getCandidateSurfaces$lambda$lambda(closure$orderedPanels, closure$panelRects, closure$changeRegion) {
    return function ($receiver) {
      tr($receiver, void 0, JsMapperDisplay$getCandidateSurfaces$lambda$lambda$lambda);
      var tmp$ = closure$orderedPanels;
      var b = closure$orderedPanels.size;
      var $receiver_0 = tmp$.subList_vux9f0$(0, Math_0.min(5, b));
      var tmp$_0;
      tmp$_0 = $receiver_0.iterator();
      while (tmp$_0.hasNext()) {
        var element = tmp$_0.next();
        var closure$panelRects_0 = closure$panelRects;
        var closure$changeRegion_0 = closure$changeRegion;
        var panel = element.component1();
        tr($receiver, void 0, JsMapperDisplay$getCandidateSurfaces$lambda$lambda$lambda$lambda_1(panel, closure$panelRects_0, closure$changeRegion_0));
      }
      return Unit;
    };
  }
  function JsMapperDisplay$getCandidateSurfaces$lambda_0(closure$orderedPanels, closure$panelRects, closure$changeRegion) {
    return function ($receiver) {
      table($receiver, void 0, JsMapperDisplay$getCandidateSurfaces$lambda$lambda(closure$orderedPanels, closure$panelRects, closure$changeRegion));
      return Unit;
    };
  }
  JsMapperDisplay.prototype.getCandidateSurfaces_gdgylh$ = function (changeRegion) {
    var tmp$;
    var $receiver = this.visiblePanels_0;
    var capacity = coerceAtLeast(mapCapacity(collectionSizeOrDefault($receiver, 10)), 16);
    var destination = LinkedHashMap_init_0(capacity);
    var tmp$_0;
    tmp$_0 = $receiver.iterator();
    while (tmp$_0.hasNext()) {
      var element = tmp$_0.next();
      var panel = element.component1()
      , panelInfo = element.component2();
      var tmp$_1, tmp$_2, tmp$_3;
      panelInfo.mesh.updateMatrixWorld();
      var panelBasePosition = panelInfo.mesh.position;
      var minX = 2147483647;
      var maxX = -2147483648;
      var minY = 2147483647;
      var maxY = -2147483648;
      var widthHalf = this.width_0 / 2.0;
      var heightHalf = this.height_0 / 2.0;
      tmp$_1 = panelInfo.faces.iterator();
      while (tmp$_1.hasNext()) {
        var face = tmp$_1.next();
        tmp$_2 = [face.a, face.b, face.c];
        for (tmp$_3 = 0; tmp$_3 !== tmp$_2.length; ++tmp$_3) {
          var vertexI = tmp$_2[tmp$_3];
          var v = plus_0(panelBasePosition.clone(), panelInfo.geom.vertices[vertexI]);
          v.project(this.uiCamera_0);
          var x = numberToInt(v.x * widthHalf + widthHalf);
          var y = numberToInt(-(v.y * heightHalf) + heightHalf);
          if (x < minX)
            minX = x;
          if (x > maxX)
            maxX = x;
          if (y < minY)
            minY = y;
          if (y > maxY)
            maxY = y;
        }
      }
      var pair = new Pair(panel, new MediaDevices$Region(minX, minY, maxX, maxY));
      destination.put_xwzc9p$(pair.first, pair.second);
    }
    var panelRects = destination;
    var orderedPanels = sortedWith(this.visiblePanels_0, new Comparator$ObjectLiteral(compareBy$lambda(JsMapperDisplay$getCandidateSurfaces$lambda(panelRects, changeRegion))));
    var first_0 = first(orderedPanels);
    (Kotlin.isType(tmp$ = first_0.second.mesh.material, MeshBasicMaterial) ? tmp$ : throwCCE()).color.r = (Kotlin.isType(tmp$ = first_0.second.mesh.material, MeshBasicMaterial) ? tmp$ : throwCCE()).color.r + 0.25;
    clear(this.table_0);
    append(this.table_0, JsMapperDisplay$getCandidateSurfaces$lambda_0(orderedPanels, panelRects, changeRegion));
    var destination_0 = ArrayList_init_0(collectionSizeOrDefault(orderedPanels, 10));
    var tmp$_4;
    tmp$_4 = orderedPanels.iterator();
    while (tmp$_4.hasNext()) {
      var item = tmp$_4.next();
      var tmp$_5 = destination_0.add_11rb$;
      var panel_0 = item.component1();
      tmp$_5.call(destination_0, panel_0);
    }
    return destination_0;
  };
  JsMapperDisplay.prototype.showCamImage_6tj0gx$ = function (image) {
    var tmp$;
    this.ui2dCtx_0.resetTransform();
    var a = this.width_0 / image.width;
    var b = this.height_0 / image.height;
    var scale = Math_0.max(a, b);
    var imgWidth = roundToInt(image.width * scale);
    var imgHeight = roundToInt(image.height * scale);
    var widthDiff = this.width_0 - imgWidth | 0;
    var heightDiff = this.height_0 - imgHeight | 0;
    var widthOff = widthDiff / 2.0;
    var heightOff = heightDiff / 2.0;
    (new CanvasBitmap(this.ui2dCanvas_0)).drawImage_daf0v5$(image, 0, 0, image.width, image.height, widthDiff / 2 | 0, heightDiff / 2 | 0, imgWidth, imgHeight);
    this.ui2dCtx_0.strokeStyle = '#006600';
    this.ui2dCtx_0.strokeRect(widthOff, heightOff, imgWidth, imgHeight);
    if ((tmp$ = this.changeRegion_0) != null) {
      this.ui2dCtx_0.strokeStyle = '#ff0000';
      this.ui2dCtx_0.strokeRect(tmp$.x0 * scale + widthOff, tmp$.y0 * scale + heightOff, tmp$.width * scale, tmp$.height * scale);
    }
    this.uiControls_0.update(this.clock_0.getDelta());
    this.uiRenderer_0.render(this.uiScene_0, this.uiCamera_0);
  };
  JsMapperDisplay.prototype.showDiffImage_qpnjw8$ = function (deltaBitmap, changeRegion) {
    this.changeRegion_0 = changeRegion;
    (new CanvasBitmap(this.diffCanvas_0)).drawImage_6tj0gx$(deltaBitmap.asImage());
    this.diffCtx_0.strokeStyle = '#ff0000';
    this.diffCtx_0.strokeRect(changeRegion.x0, changeRegion.y0, changeRegion.width, changeRegion.height);
  };
  JsMapperDisplay.prototype.showMessage_61zpoe$ = function (message) {
    this.messageDiv_0.innerText = message;
  };
  JsMapperDisplay.prototype.showMessage2_61zpoe$ = function (message) {
    this.message2Div_0.innerText = message;
  };
  JsMapperDisplay.prototype.showStats_qt1dr2$ = function (total, mapped, visible) {
    this.statsDiv_0.innerHTML = '<i class=' + '"' + 'fas fa-triangle' + '"' + '><\/i>Mapped: ' + mapped + ' / ' + total + '<br/>Visible: ' + visible;
  };
  JsMapperDisplay.prototype.go_0 = function () {
    this.listener_0.onStart();
    this.computeVisiblePanels_0();
  };
  JsMapperDisplay.prototype.computeVisiblePanels_0 = function () {
    this.visiblePanels_0.clear();
    var tmp$;
    tmp$ = this.panelInfos_0.entries.iterator();
    while (tmp$.hasNext()) {
      var element = tmp$.next();
      var panel = element.key;
      var panelInfo = element.value;
      var panelPosition = panelInfo.geom.vertices[panelInfo.faces.get_za3lpa$(0).a];
      var dirToCamera = this.uiCamera_0.position.clone().sub(panelPosition);
      dirToCamera.normalize();
      var angle = ensureNotNull(panelInfo.faces.get_za3lpa$(0).normal).dot(dirToCamera);
      println('Angle for ' + panel.name + ' is ' + angle);
      if (angle > 0) {
        this.visiblePanels_0.add_11rb$(new Pair(panel, panelInfo));
      }
    }
  };
  JsMapperDisplay.prototype.close = function () {
  };
  function JsMapperDisplay$screen$lambda$lambda$lambda$lambda(this$JsMapperDisplay) {
    return function (it) {
      this$JsMapperDisplay.wireframe_0.position.y = this$JsMapperDisplay.wireframe_0.position.y + 10;
      return Unit;
    };
  }
  function JsMapperDisplay$screen$lambda$lambda$lambda(this$JsMapperDisplay) {
    return function ($receiver) {
      $receiver.unaryPlus_pdl1vz$('\u25B2');
      set_onClickFunction($receiver, JsMapperDisplay$screen$lambda$lambda$lambda$lambda(this$JsMapperDisplay));
      return Unit;
    };
  }
  function JsMapperDisplay$screen$lambda$lambda$lambda$lambda_0(this$JsMapperDisplay) {
    return function (it) {
      this$JsMapperDisplay.wireframe_0.position.y = this$JsMapperDisplay.wireframe_0.position.y - 10;
      return Unit;
    };
  }
  function JsMapperDisplay$screen$lambda$lambda$lambda_0(this$JsMapperDisplay) {
    return function ($receiver) {
      $receiver.unaryPlus_pdl1vz$('\u25BC');
      set_onClickFunction($receiver, JsMapperDisplay$screen$lambda$lambda$lambda$lambda_0(this$JsMapperDisplay));
      return Unit;
    };
  }
  function JsMapperDisplay$screen$lambda$lambda$lambda$lambda_1(this$JsMapperDisplay) {
    return function (it) {
      this$JsMapperDisplay.go_0();
      return Unit;
    };
  }
  function JsMapperDisplay$screen$lambda$lambda$lambda_1(this$JsMapperDisplay) {
    return function ($receiver) {
      i($receiver, 'fas fa-play');
      set_onClickFunction($receiver, JsMapperDisplay$screen$lambda$lambda$lambda$lambda_1(this$JsMapperDisplay));
      return Unit;
    };
  }
  function JsMapperDisplay$screen$lambda$lambda$lambda$lambda_2(this$JsMapperDisplay) {
    return function (it) {
      this$JsMapperDisplay.listener_0.onPause();
      return Unit;
    };
  }
  function JsMapperDisplay$screen$lambda$lambda$lambda_2(this$JsMapperDisplay) {
    return function ($receiver) {
      i($receiver, 'fas fa-pause');
      set_onClickFunction($receiver, JsMapperDisplay$screen$lambda$lambda$lambda$lambda_2(this$JsMapperDisplay));
      return Unit;
    };
  }
  function JsMapperDisplay$screen$lambda$lambda$lambda$lambda_3(this$JsMapperDisplay) {
    return function (it) {
      this$JsMapperDisplay.listener_0.onStop();
      return Unit;
    };
  }
  function JsMapperDisplay$screen$lambda$lambda$lambda_3(this$JsMapperDisplay) {
    return function ($receiver) {
      i($receiver, 'fas fa-stop');
      set_onClickFunction($receiver, JsMapperDisplay$screen$lambda$lambda$lambda$lambda_3(this$JsMapperDisplay));
      $receiver.disabled = true;
      return Unit;
    };
  }
  function JsMapperDisplay$screen$lambda$lambda(this$JsMapperDisplay) {
    return function ($receiver) {
      button($receiver, void 0, void 0, void 0, void 0, void 0, JsMapperDisplay$screen$lambda$lambda$lambda(this$JsMapperDisplay));
      button($receiver, void 0, void 0, void 0, void 0, void 0, JsMapperDisplay$screen$lambda$lambda$lambda_0(this$JsMapperDisplay));
      button($receiver, void 0, void 0, void 0, void 0, void 0, JsMapperDisplay$screen$lambda$lambda$lambda_1(this$JsMapperDisplay));
      button($receiver, void 0, void 0, void 0, void 0, void 0, JsMapperDisplay$screen$lambda$lambda$lambda_2(this$JsMapperDisplay));
      button($receiver, void 0, void 0, void 0, void 0, void 0, JsMapperDisplay$screen$lambda$lambda$lambda_3(this$JsMapperDisplay));
      return Unit;
    };
  }
  function JsMapperDisplay$screen$lambda$lambda_0(this$JsMapperDisplay) {
    return function ($receiver) {
      $receiver.width = this$JsMapperDisplay.width_0.toString() + 'px';
      $receiver.height = this$JsMapperDisplay.height_0.toString() + 'px';
      return Unit;
    };
  }
  function JsMapperDisplay$screen$lambda$lambda_1($receiver) {
    return Unit;
  }
  function JsMapperDisplay$screen$lambda$lambda_2(this$JsMapperDisplay) {
    return function ($receiver) {
      $receiver.width = this$JsMapperDisplay.width_0.toString() + 'px';
      $receiver.height = this$JsMapperDisplay.height_0.toString() + 'px';
      return Unit;
    };
  }
  function JsMapperDisplay$screen$lambda$lambda_3($receiver) {
    return Unit;
  }
  function JsMapperDisplay$screen$lambda$lambda_4($receiver) {
    return Unit;
  }
  function JsMapperDisplay$screen$lambda$lambda_5($receiver) {
    return Unit;
  }
  function JsMapperDisplay$screen$lambda$lambda_6($receiver) {
    return Unit;
  }
  function JsMapperDisplay$screen$lambda(this$JsMapperDisplay) {
    return function ($receiver) {
      div($receiver, 'mapperUi-controls', JsMapperDisplay$screen$lambda$lambda(this$JsMapperDisplay));
      canvas($receiver, 'mapperUi-2d-canvas', JsMapperDisplay$screen$lambda$lambda_0(this$JsMapperDisplay));
      div($receiver, 'mapperUi-3d-div', JsMapperDisplay$screen$lambda$lambda_1);
      canvas($receiver, 'mapperUi-diff-canvas', JsMapperDisplay$screen$lambda$lambda_2(this$JsMapperDisplay));
      div($receiver, 'mapperUi-stats', JsMapperDisplay$screen$lambda$lambda_3);
      div($receiver, 'mapperUi-message', JsMapperDisplay$screen$lambda$lambda_4);
      div($receiver, 'mapperUi-message2', JsMapperDisplay$screen$lambda$lambda_5);
      div($receiver, 'mapperUi-table', JsMapperDisplay$screen$lambda$lambda_6);
      return Unit;
    };
  }
  JsMapperDisplay.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'JsMapperDisplay',
    interfaces: [HostedWebApp, MapperDisplay]
  };
  function PanelInfo(faces, mesh, geom) {
    this.faces = faces;
    this.mesh = mesh;
    this.geom = geom;
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
  function Comparator$ObjectLiteral_0(closure$comparison) {
    this.closure$comparison = closure$comparison;
  }
  Comparator$ObjectLiteral_0.prototype.compare = function (a, b) {
    return this.closure$comparison(a, b);
  };
  Comparator$ObjectLiteral_0.$metadata$ = {kind: Kind_CLASS, interfaces: [Comparator]};
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
    this.visualizer_0 = new Visualizer(this.sheepModel_0);
    this.pinky_0 = new Pinky(this.sheepModel_0, this.shows_0, this.network_0, this.dmxUniverse_0, this.display_0.forPinky());
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
      var mapperDisplay = new JsMapperDisplay(this$SheepSimulator.visualizer_0);
      var mapper = new Mapper(this$SheepSimulator.network_0, this$SheepSimulator.sheepModel_0, mapperDisplay, new FakeMediaDevices(this$SheepSimulator.visualizer_0));
      launch(this$SheepSimulator.mapperScope_0, void 0, void 0, SheepSimulator$start$lambda$lambda$lambda(mapper));
      return mapperDisplay;
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
            var pixelSpacing = toDouble((tmp$_0 = this.local$queryParams.get_11rb$(key_0)) != null ? tmp$_0 : '2');
            var pixelArranger = new SwirlyPixelArranger(pixelDensity, pixelSpacing);
            var totalPixels = {v: 0};
            var tmp$_1, tmp$_0_0;
            var index = 0;
            tmp$_1 = sortedWith(this.local$this$SheepSimulator.sheepModel_0.panels, new Comparator$ObjectLiteral_0(compareBy$lambda_0(getPropertyCallableRef('name', 1, function ($receiver) {
              return $receiver.name;
            })))).iterator();
            while (tmp$_1.hasNext()) {
              var item = tmp$_1.next();
              var this$SheepSimulator = this.local$this$SheepSimulator;
              var index_0 = checkIndexOverflow((tmp$_0_0 = index, index = tmp$_0_0 + 1 | 0, tmp$_0_0));
              var tmp$_2;
              var vizPanel = this$SheepSimulator.visualizer_0.addPanel_jfju1k$(item);
              var pixelPositions = pixelArranger.arrangePixels_zdreix$(vizPanel);
              vizPanel.vizPixels = new VizPanel$VizPixels(pixelPositions);
              totalPixels.v = totalPixels.v + pixelPositions.length | 0;
              document.getElementById('visualizerPixelCount').innerText = totalPixels.v.toString();
              var pixelLocations = ensureNotNull(vizPanel.getPixelLocations());
              this$SheepSimulator.pinky_0.providePixelMapping_td2c2y$(item, pixelLocations);
              var brain = new Brain('brain//' + index_0, this$SheepSimulator.network_0, this$SheepSimulator.display_0.forBrain(), (tmp$_2 = vizPanel.vizPixels) != null ? tmp$_2 : SheepSimulator$NullPixels_getInstance());
              this$SheepSimulator.pinky_0.providePanelMapping_epc2uw$(new BrainId(brain.id), item);
              launch(this$SheepSimulator.brainScope_0, void 0, void 0, SheepSimulator$start$lambda$lambda$lambda_0(brain));
            }

            var tmp$_3;
            tmp$_3 = this.local$this$SheepSimulator.sheepModel_0.eyes.iterator();
            while (tmp$_3.hasNext()) {
              var element = tmp$_3.next();
              var this$SheepSimulator_0 = this.local$this$SheepSimulator;
              this$SheepSimulator_0.visualizer_0.addMovingHead_nmqlne$(element, this$SheepSimulator_0.dmxUniverse_0);
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
  function first_0($receiver, className) {
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
  function Vector2_0(x, y) {
    Vector2.call(this, x, y);
  }
  Vector2_0.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Vector2',
    interfaces: []
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
    this.canvas_5fzy0b$_0 = canvas;
    this.width_4c4jfj$_0 = this.canvas_5fzy0b$_0.width;
    this.height_19yhui$_0 = this.canvas_5fzy0b$_0.height;
    this.ctx_8be2vx$ = context2d(this.canvas_5fzy0b$_0);
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
    this.ctx_8be2vx$.globalCompositeOperation = 'source-over';
    this.ctx_8be2vx$.drawImage((Kotlin.isType(tmp$ = other, CanvasBitmap) ? tmp$ : throwCCE()).canvas_5fzy0b$_0, 0.0, 0.0);
  };
  CanvasBitmap.prototype.subtract_5151av$ = function (other) {
    var tmp$;
    this.assertSameSizeAs_ffnq1x$_0(other);
    this.ctx_8be2vx$.globalCompositeOperation = 'difference';
    this.ctx_8be2vx$.drawImage((Kotlin.isType(tmp$ = other, CanvasBitmap) ? tmp$ : throwCCE()).canvas_5fzy0b$_0, 0.0, 0.0);
  };
  CanvasBitmap.prototype.withData_c37y77$ = function (fn) {
    var imageData = this.ctx_8be2vx$.getImageData(0.0, 0.0, this.width, this.height);
    if (fn(imageData.data)) {
      this.ctx_8be2vx$.putImageData(imageData, 0.0, 0.0);
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
    ctx.drawImage(this.this$CanvasBitmap.canvas_5fzy0b$_0, 0.0, 0.0);
  };
  CanvasBitmap$asImage$ObjectLiteral.prototype.draw_wveyom$ = function (ctx, sX, sY, sWidth, sHeight, dX, dY, dWidth, dHeight) {
    ctx.drawImage(this.this$CanvasBitmap.canvas_5fzy0b$_0, sX, sY, sWidth, sHeight, dX, dY, dWidth, dHeight);
  };
  CanvasBitmap$asImage$ObjectLiteral.$metadata$ = {
    kind: Kind_CLASS,
    interfaces: [JsImage]
  };
  CanvasBitmap.prototype.asImage = function () {
    return new CanvasBitmap$asImage$ObjectLiteral(this);
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
  function BrowserNetwork() {
  }
  function BrowserNetwork$link$ObjectLiteral() {
    this.myAddress_4sgley$_0 = new BrowserNetwork$link$ObjectLiteral$myAddress$ObjectLiteral();
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
    throw new NotImplementedError_init('An operation is not implemented: ' + 'BrowserNetwork.listenUdp not implemented');
  };
  BrowserNetwork$link$ObjectLiteral.prototype.sendUdp_ytpeqp$ = function (toAddress, port, bytes) {
    throw new NotImplementedError_init('An operation is not implemented: ' + 'BrowserNetwork.sendUdp not implemented');
  };
  BrowserNetwork$link$ObjectLiteral.prototype.broadcastUdp_3fbn1q$ = function (port, bytes) {
    throw new NotImplementedError_init('An operation is not implemented: ' + 'BrowserNetwork.broadcastUdp not implemented');
  };
  BrowserNetwork$link$ObjectLiteral.prototype.listenTcp_kd29r4$ = function (port, tcpServerSocketListener) {
    throw new NotImplementedError_init('An operation is not implemented: ' + 'BrowserNetwork.listenTcp not implemented');
  };
  function BrowserNetwork$link$ObjectLiteral$connectTcp$ObjectLiteral(closure$port, closure$webSocket, this$) {
    this.closure$port = closure$port;
    this.closure$webSocket = closure$webSocket;
    this.fromAddress_u3qrj2$_0 = this$.myAddress;
    this.toAddress_f64ygj$_0 = this$.myAddress;
  }
  Object.defineProperty(BrowserNetwork$link$ObjectLiteral$connectTcp$ObjectLiteral.prototype, 'fromAddress', {
    get: function () {
      return this.fromAddress_u3qrj2$_0;
    }
  });
  Object.defineProperty(BrowserNetwork$link$ObjectLiteral$connectTcp$ObjectLiteral.prototype, 'toAddress', {
    get: function () {
      return this.toAddress_f64ygj$_0;
    }
  });
  Object.defineProperty(BrowserNetwork$link$ObjectLiteral$connectTcp$ObjectLiteral.prototype, 'port', {
    get: function () {
      return this.closure$port;
    }
  });
  BrowserNetwork$link$ObjectLiteral$connectTcp$ObjectLiteral.prototype.send_fqrh44$ = function (bytes) {
    this.closure$webSocket.send(new Int8Array(toTypedArray(bytes)));
  };
  BrowserNetwork$link$ObjectLiteral$connectTcp$ObjectLiteral.$metadata$ = {
    kind: Kind_CLASS,
    interfaces: [Network$TcpConnection]
  };
  function BrowserNetwork$link$ObjectLiteral$connectTcp$lambda(closure$tcpListener, closure$tcpConnection) {
    return function (it) {
      console.log('WebSocket open!', it);
      closure$tcpListener.connected_67ozxy$(closure$tcpConnection);
      return Unit;
    };
  }
  function BrowserNetwork$link$ObjectLiteral$connectTcp$lambda_0(closure$tcpListener, closure$tcpConnection) {
    return function (it) {
      var tmp$, tmp$_0;
      var buf = Kotlin.isType(tmp$ = it.data, ArrayBuffer) ? tmp$ : throwCCE();
      var byteBuf = new Int8Array(buf);
      var bytes = new Int8Array(byteBuf.length);
      tmp$_0 = byteBuf.length;
      for (var i = 0; i < tmp$_0; i++) {
        bytes[i] = byteBuf[i];
      }
      closure$tcpListener.receive_r00qii$(closure$tcpConnection, bytes);
      return Unit;
    };
  }
  function BrowserNetwork$link$ObjectLiteral$connectTcp$lambda_1(it) {
    console.log('WebSocket error!', it);
    return Unit;
  }
  function BrowserNetwork$link$ObjectLiteral$connectTcp$lambda_2(it) {
    console.log('WebSocket close!', it);
    return Unit;
  }
  BrowserNetwork$link$ObjectLiteral.prototype.connectTcp_dy234z$ = function (toAddress, port, tcpListener) {
    var tmp$;
    var webSocket = new WebSocket((Kotlin.isType(tmp$ = toAddress, BrowserNetwork$BrowserAddress) ? tmp$ : throwCCE()).urlString + 'sm/ws');
    webSocket.binaryType = 'arraybuffer';
    var tcpConnection = new BrowserNetwork$link$ObjectLiteral$connectTcp$ObjectLiteral(port, webSocket, this);
    webSocket.onopen = BrowserNetwork$link$ObjectLiteral$connectTcp$lambda(tcpListener, tcpConnection);
    webSocket.onmessage = BrowserNetwork$link$ObjectLiteral$connectTcp$lambda_0(tcpListener, tcpConnection);
    webSocket.onerror = BrowserNetwork$link$ObjectLiteral$connectTcp$lambda_1;
    webSocket.onclose = BrowserNetwork$link$ObjectLiteral$connectTcp$lambda_2;
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
    return new BrowserNetwork$link$ObjectLiteral();
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
  function FakeMediaDevices(visualizer) {
    this.visualizer_0 = visualizer;
    this.currentCam = null;
  }
  FakeMediaDevices.prototype.getCurrentCam = function () {
    return this.currentCam;
  };
  FakeMediaDevices.prototype.getCamera_vux9f0$ = function (width, height) {
    var $receiver = new FakeMediaDevices$FakeCamera(this, width, height);
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
    this.altCamera_0 = new PerspectiveCamera_init(45, 1.0, 1, 1000);
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
    var destination = ArrayList_init_0(collectionSizeOrDefault($receiver_0, 10));
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
  function Visualizer(sheepModel) {
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
    this.mouse_0 = new Vector2();
    this.sphere_0 = null;
    this.rendererListeners_0 = ArrayList_init();
    this.vizPanels_0 = ArrayList_init();
    var tmp$;
    this.sheepView_0 = Kotlin.isType(tmp$ = ensureNotNull(document.getElementById('sheepView')), HTMLDivElement) ? tmp$ : throwCCE();
    this.sheepView_0.addEventListener('mousemove', Visualizer_init$lambda(this), false);
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
    this.raycaster_0 = new Raycaster_init(undefined, undefined, undefined, undefined);
    this.raycaster_0.params.Points.threshold = 1;
    var tmp$_0 = new SphereBufferGeometry(1, 32, 32);
    var $receiver_2 = new MeshBasicMaterial();
    $receiver_2.color.set(16711680);
    this.sphere_0 = new Mesh_init(tmp$_0, $receiver_2);
    this.scene_0.add(this.sphere_0);
    var tmp$_1;
    tmp$_1 = sheepModel.vertices.iterator();
    while (tmp$_1.hasNext()) {
      var element = tmp$_1.next();
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
  Visualizer.prototype.onMouseMove_tfvzir$ = function (event) {
    event.preventDefault();
    this.mouse_0.x = event.clientX / this.sheepView_0.offsetWidth * 2 - 1;
    this.mouse_0.y = -(event.clientY / this.sheepView_0.offsetHeight) * 2 + 1;
  };
  Visualizer.prototype.addPanel_jfju1k$ = function (p) {
    var vizPanel = new VizPanel(p, this.geom_0, this.scene_0);
    this.vizPanels_0.add_11rb$(vizPanel);
    return vizPanel;
  };
  Visualizer.prototype.addMovingHead_nmqlne$ = function (movingHead, dmxUniverse) {
    return new Visualizer$VizMovingHead(this, movingHead, dmxUniverse);
  };
  function Visualizer$VizMovingHead($outer, movingHead, dmxUniverse) {
    this.$outer = $outer;
    this.baseChannel_0 = ensureNotNull(Config$Companion_getInstance().DMX_DEVICES.get_11rb$(movingHead.name));
    this.device_0 = new Shenzarpy(dmxUniverse.reader_sxjeop$(this.baseChannel_0, 16, Visualizer$VizMovingHead$device$lambda(this)));
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
    var colorWheelV = this.device_0.colorWheel;
    var wheelColor = Shenzarpy$WheelColor$Companion_getInstance().get_s8j3t7$(colorWheelV);
    this.material_0.color.set(wheelColor.color.rgb);
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
    if (!this.mapperIsRunning) {
      if (this.getVizRotationEl_0().checked) {
        var rotSpeed = 0.01;
        var x = this.camera_0.position.x;
        var z = this.camera_0.position.z;
        this.camera_0.position.x = x * Math_0.cos(rotSpeed) + z * Math_0.sin(rotSpeed);
        var tmp$_0 = this.camera_0.position;
        var x_0 = rotSpeed * 2;
        var tmp$_1 = z * Math_0.cos(x_0);
        var x_1 = rotSpeed * 2;
        tmp$_0.z = tmp$_1 - x * Math_0.sin(x_1);
        this.camera_0.lookAt(this.scene_0.position);
      }
    }
    this.controls_0.update();
    this.raycaster_0.setFromCamera(this.mouse_0, this.camera_0);
    var intersections = this.raycaster_0.intersectObjects(this.scene_0.children, false);
    if (intersections.size > 0) {
      var intersection = intersections.get_za3lpa$(0);
      if (intersection.object.panel) {
        (Kotlin.isType(tmp$ = document.getElementById('selectionInfo'), HTMLDivElement) ? tmp$ : throwCCE()).innerText = 'Selected: ' + toString_0(intersections.get_za3lpa$(0).object.panel.name);
      }
    }
    this.renderer_0.render(this.scene_0, this.camera_0);
    var tmp$_2;
    tmp$_2 = this.frameListeners_0.iterator();
    while (tmp$_2.hasNext()) {
      var element = tmp$_2.next();
      element.onFrameReady(this.scene_0, this.camera_0);
    }
    var tmp$_3;
    tmp$_3 = this.rendererListeners_0.iterator();
    while (tmp$_3.hasNext()) {
      var element_0 = tmp$_3.next();
      element_0();
    }
  };
  Visualizer.prototype.doResize_0 = function () {
    this.camera_0.aspect = this.sheepView_0.offsetWidth / this.sheepView_0.offsetHeight;
    this.camera_0.updateProjectionMatrix();
    this.renderer_0.setSize(this.sheepView_0.offsetWidth, this.sheepView_0.offsetHeight);
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
      this$Visualizer.onMouseMove_tfvzir$(tmp$_0);
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
    interfaces: []
  };
  function VizPanel(panel, geom, scene) {
    this.geom_0 = geom;
    this.scene_0 = scene;
    this.name_0 = panel.name;
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
    var destination = ArrayList_init_0(collectionSizeOrDefault($receiver_0, 10));
    var tmp$;
    tmp$ = $receiver_0.iterator();
    while (tmp$.hasNext()) {
      var item = tmp$.next();
      var tmp$_0 = destination.add_11rb$;
      var tmp$_1;
      var $receiver_1 = item.vertexIds;
      var destination_0 = ArrayList_init_0(collectionSizeOrDefault($receiver_1, 10));
      var tmp$_2;
      tmp$_2 = $receiver_1.iterator();
      while (tmp$_2.hasNext()) {
        var item_0 = tmp$_2.next();
        var tmp$_3 = destination_0.add_11rb$;
        var v = this.geom_0.vertices[item_0];
        var lvi = indexOf(panelVertices, v);
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
    var destination_1 = ArrayList_init_0(collectionSizeOrDefault($receiver_5, 10));
    var tmp$_8;
    tmp$_8 = $receiver_5.iterator();
    while (tmp$_8.hasNext()) {
      var item_2 = tmp$_8.next();
      var tmp$_9 = destination_1.add_11rb$;
      var lineGeo = new Geometry();
      var $receiver_6 = item_2.points;
      var destination_2 = ArrayList_init_0(collectionSizeOrDefault($receiver_6, 10));
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
    this.mesh_0.panel = this;
    this.scene_0.add(this.mesh_0);
    var destination_3 = ArrayList_init_0(collectionSizeOrDefault(lines, 10));
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
  function VizPanel$VizPixels(positions) {
    this.size_cgbufu$_0 = positions.length;
    this.points_0 = null;
    this.pixGeometry_0 = new BufferGeometry();
    this.colorsBufferAttr_0 = null;
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
    this.colorsBufferAttr_0 = new Float32BufferAttribute(new Float32Array(this.size * 3 | 0), 3);
    this.colorsBufferAttr_0.dynamic = true;
    this.pixGeometry_0.addAttribute('color', this.colorsBufferAttr_0);
    var $receiver = new PointsMaterial();
    $receiver.size = 3;
    $receiver.vertexColors = THREE.VertexColors;
    var material = $receiver;
    var $receiver_0 = new Points();
    $receiver_0.geometry = this.pixGeometry_0;
    $receiver_0.material = material;
    this.points_0 = $receiver_0;
  }
  Object.defineProperty(VizPanel$VizPixels.prototype, 'size', {
    get: function () {
      return this.size_cgbufu$_0;
    }
  });
  VizPanel$VizPixels.prototype.addToScene_smv6vb$ = function (scene) {
    scene.add(this.points_0);
  };
  VizPanel$VizPixels.prototype.removeFromScene_smv6vb$ = function (scene) {
    scene.remove(this.points_0);
  };
  VizPanel$VizPixels.prototype.get_za3lpa$ = function (i) {
    return new Color(this.colorsAsInts_0[i]);
  };
  VizPanel$VizPixels.prototype.set_ibd5tj$ = function (i, color) {
    this.colorsAsInts_0[i] = color.argb;
    var rgbBuf = this.colorsBufferAttr_0.array;
    rgbBuf[i * 3 | 0] = color.redF;
    rgbBuf[(i * 3 | 0) + 1 | 0] = color.greenF;
    rgbBuf[(i * 3 | 0) + 2 | 0] = color.blueF;
    this.colorsBufferAttr_0.needsUpdate = true;
  };
  VizPanel$VizPixels.prototype.set_tmuqsv$ = function (colors) {
    var a = this.size;
    var maxCount = Math_0.min(a, colors.length);
    var rgbBuf = this.colorsBufferAttr_0.array;
    for (var i = 0; i < maxCount; i++) {
      this.colorsAsInts_0[i] = colors[i].argb;
      var pColor = colors[i];
      rgbBuf[i * 3 | 0] = pColor.redF;
      rgbBuf[(i * 3 | 0) + 1 | 0] = pColor.greenF;
      rgbBuf[(i * 3 | 0) + 2 | 0] = pColor.blueF;
    }
    this.colorsBufferAttr_0.needsUpdate = true;
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
  Rotator.prototype.invert = function () {
    return new Rotator(this.to, this.from);
  };
  Rotator.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Rotator',
    interfaces: []
  };
  Brain.ShaderBits = Brain$ShaderBits;
  Brain.UnmappedSurface = Brain$UnmappedSurface;
  Brain.MappedSurface = Brain$MappedSurface;
  var package$baaahs = _.baaahs || (_.baaahs = {});
  package$baaahs.Brain = Brain;
  Object.defineProperty(Color, 'Companion', {
    get: Color$Companion_getInstance
  });
  package$baaahs.Color_init_7b5o5w$ = Color_init_0;
  package$baaahs.Color_init_tjonv8$ = Color_init_1;
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
  Dmx.Universe = Dmx$Universe;
  Dmx.Buffer = Dmx$Buffer;
  Dmx.DeviceType = Dmx$DeviceType;
  package$baaahs.Dmx = Dmx;
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
  Mapper.BrainMapper = Mapper$BrainMapper;
  package$baaahs.Mapper = Mapper;
  MapperDisplay.Listener = MapperDisplay$Listener;
  package$baaahs.MapperDisplay = MapperDisplay;
  MediaDevices.Camera = MediaDevices$Camera;
  MediaDevices.Region = MediaDevices$Region;
  package$baaahs.MediaDevices = MediaDevices;
  Pinky.UnknownSurface = Pinky$UnknownSurface;
  Pinky.BeatProvider = Pinky$BeatProvider;
  Pinky.PinkyBeatProvider = Pinky$PinkyBeatProvider;
  Pinky.NetworkStats = Pinky$NetworkStats;
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
  Object.defineProperty(ShaderId, 'Companion', {
    get: ShaderId$Companion_getInstance
  });
  package$baaahs.ShaderId = ShaderId;
  package$baaahs.Surface = Surface;
  package$baaahs.ShaderReader = ShaderReader;
  Object.defineProperty(Shader, 'Companion', {
    get: Shader$Companion_getInstance
  });
  Shader.Buffer = Shader$Buffer;
  Shader.Renderer = Shader$Renderer;
  package$baaahs.Shader = Shader;
  package$baaahs.Pixels = Pixels;
  SheepModel.Point = SheepModel$Point;
  SheepModel.Line = SheepModel$Line;
  SheepModel.Face = SheepModel$Face;
  SheepModel.Faces = SheepModel$Faces;
  SheepModel.Panel = SheepModel$Panel;
  SheepModel.MovingHead = SheepModel$MovingHead;
  package$baaahs.SheepModel = SheepModel;
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
  package$baaahs.Shenzarpy = Shenzarpy;
  Show.Renderer = Show$Renderer;
  Show.RestartShowException = Show$RestartShowException;
  package$baaahs.Show = Show;
  ShowRunner.SurfacesChanges = ShowRunner$SurfacesChanges;
  ShowRunner.SurfaceReceiver = ShowRunner$SurfaceReceiver;
  package$baaahs.ShowRunner = ShowRunner;
  Object.defineProperty(package$baaahs, 'SparkleMotion', {
    get: SparkleMotion_getInstance
  });
  Object.defineProperty(package$baaahs, 'Topics', {
    get: Topics_getInstance
  });
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
  var package$imaging = package$baaahs.imaging || (package$baaahs.imaging = {});
  package$imaging.Image = Image;
  package$imaging.Bitmap = Bitmap;
  var package$io = package$baaahs.io || (package$baaahs.io = {});
  package$io.ByteArrayReader = ByteArrayReader;
  package$io.ByteArrayWriter_init_za3lpa$ = ByteArrayWriter_init;
  package$io.ByteArrayWriter = ByteArrayWriter;
  Object.defineProperty(FragmentingUdpLink, 'Companion', {
    get: FragmentingUdpLink$Companion_getInstance
  });
  FragmentingUdpLink.Fragment = FragmentingUdpLink$Fragment;
  var package$net = package$baaahs.net || (package$baaahs.net = {});
  package$net.FragmentingUdpLink = FragmentingUdpLink;
  Network.Link = Network$Link;
  Network.Address = Network$Address;
  Network.UdpListener = Network$UdpListener;
  Network.TcpConnection = Network$TcpConnection;
  Network.TcpListener = Network$TcpListener;
  Network.TcpServerSocketListener = Network$TcpServerSocketListener;
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
  Object.defineProperty(Type, 'BRAIN_ID_RESPONSE', {
    get: Type$BRAIN_ID_RESPONSE_getInstance
  });
  Object.defineProperty(Type, 'BRAIN_MAPPING', {
    get: Type$BRAIN_MAPPING_getInstance
  });
  Object.defineProperty(Type, 'PINKY_PONG', {
    get: Type$PINKY_PONG_getInstance
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
  Object.defineProperty(MapperHelloMessage, 'Companion', {
    get: MapperHelloMessage$Companion_getInstance
  });
  package$proto.MapperHelloMessage = MapperHelloMessage;
  Object.defineProperty(BrainIdRequest, 'Companion', {
    get: BrainIdRequest$Companion_getInstance
  });
  package$proto.BrainIdRequest = BrainIdRequest;
  Object.defineProperty(BrainIdResponse, 'Companion', {
    get: BrainIdResponse$Companion_getInstance
  });
  package$proto.BrainIdResponse = BrainIdResponse;
  Object.defineProperty(BrainMappingMessage, 'Companion', {
    get: BrainMappingMessage$Companion_getInstance
  });
  package$proto.BrainMappingMessage = BrainMappingMessage;
  package$proto.Vector2F = Vector2F;
  Object.defineProperty(PinkyPongMessage, 'Companion', {
    get: PinkyPongMessage$Companion_getInstance
  });
  package$proto.PinkyPongMessage = PinkyPongMessage;
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
  Object.defineProperty(HeartShader, 'Companion', {
    get: HeartShader$Companion_getInstance
  });
  HeartShader.Buffer = HeartShader$Buffer;
  HeartShader.Renderer = HeartShader$Renderer;
  package$shaders.HeartShader = HeartShader;
  Object.defineProperty(PixelShader, 'Companion', {
    get: PixelShader$Companion_getInstance
  });
  PixelShader.Buffer = PixelShader$Buffer;
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
  FakeNetwork.FakeTcpConnection = FakeNetwork$FakeTcpConnection;
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
  package$baaahs.time_ls4sck$ = time;
  package$baaahs.JsDisplay = JsDisplay;
  package$baaahs.JsNetworkDisplay = JsNetworkDisplay;
  package$baaahs.JsPinkyDisplay = JsPinkyDisplay;
  package$baaahs.JsBrainDisplay = JsBrainDisplay;
  package$baaahs.JsMapperDisplay = JsMapperDisplay;
  package$baaahs.PanelInfo = PanelInfo;
  package$baaahs.Launcher = Launcher;
  Object.defineProperty(SheepSimulator, 'NullPixels', {
    get: SheepSimulator$NullPixels_getInstance
  });
  package$baaahs.SheepSimulator = SheepSimulator;
  package$baaahs.get_disabled_ejp6nk$ = get_disabled;
  package$baaahs.set_disabled_juh0kr$ = set_disabled;
  package$baaahs.forEach_dokpt5$ = forEach;
  package$baaahs.clear_u75qir$ = clear_0;
  package$baaahs.first_m814eh$ = first_0;
  package$baaahs.context2d_ng27xv$ = context2d;
  package$baaahs.HostedWebApp = HostedWebApp;
  DomContainer.Frame = DomContainer$Frame;
  package$baaahs.DomContainer = DomContainer;
  package$baaahs.FakeDomContainer = FakeDomContainer;
  Object.defineProperty(package$baaahs, 'WebUi', {
    get: WebUi_getInstance
  });
  var package$geom = package$baaahs.geom || (package$baaahs.geom = {});
  package$geom.Vector2 = Vector2_0;
  package$imaging.NativeBitmap = NativeBitmap;
  package$imaging.createCanvas_vux9f0$ = createCanvas;
  package$imaging.CanvasBitmap = CanvasBitmap;
  package$imaging.JsImage = JsImage;
  package$imaging.ImageBitmapImage = ImageBitmapImage;
  package$baaahs.doRunBlocking_g2bo5h$ = doRunBlocking;
  package$baaahs.getResource_61zpoe$ = getResource;
  package$baaahs.getTimeMillis = getTimeMillis;
  BrowserNetwork.BrowserAddress = BrowserNetwork$BrowserAddress;
  package$net.BrowserNetwork = BrowserNetwork;
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
  VizPanel.Point2 = VizPanel$Point2;
  VizPanel.VizPixels = VizPanel$VizPixels;
  package$visualizer.VizPanel = VizPanel;
  package$visualizer.segments_182k4$ = segments;
  package$visualizer.asKey_eko7cz$ = asKey;
  package$visualizer.Rotator = Rotator;
  Color$Companion.prototype.patch_mynpiu$ = KSerializer.prototype.patch_mynpiu$;
  GadgetData$$serializer.prototype.patch_mynpiu$ = GeneratedSerializer.prototype.patch_mynpiu$;
  ColorPicker$$serializer.prototype.patch_mynpiu$ = GeneratedSerializer.prototype.patch_mynpiu$;
  PalettePicker$$serializer.prototype.patch_mynpiu$ = GeneratedSerializer.prototype.patch_mynpiu$;
  Slider$$serializer.prototype.patch_mynpiu$ = GeneratedSerializer.prototype.patch_mynpiu$;
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
  HeartbleatShow$createRenderer$ObjectLiteral.prototype.surfacesChanged_yroyvo$ = Show$Renderer.prototype.surfacesChanged_yroyvo$;
  LifeyShow$createRenderer$ObjectLiteral.prototype.surfacesChanged_yroyvo$ = Show$Renderer.prototype.surfacesChanged_yroyvo$;
  PanelTweenShow$createRenderer$ObjectLiteral.prototype.surfacesChanged_yroyvo$ = Show$Renderer.prototype.surfacesChanged_yroyvo$;
  PixelTweenShow$createRenderer$ObjectLiteral.prototype.surfacesChanged_yroyvo$ = Show$Renderer.prototype.surfacesChanged_yroyvo$;
  RandomShow$createRenderer$ObjectLiteral.prototype.surfacesChanged_yroyvo$ = Show$Renderer.prototype.surfacesChanged_yroyvo$;
  SimpleSpatialShow$createRenderer$ObjectLiteral.prototype.surfacesChanged_yroyvo$ = Show$Renderer.prototype.surfacesChanged_yroyvo$;
  SolidColorShow$createRenderer$ObjectLiteral.prototype.surfacesChanged_yroyvo$ = Show$Renderer.prototype.surfacesChanged_yroyvo$;
  SomeDumbShow$createRenderer$ObjectLiteral.prototype.surfacesChanged_yroyvo$ = Show$Renderer.prototype.surfacesChanged_yroyvo$;
  ThumpShow$createRenderer$ObjectLiteral.prototype.surfacesChanged_yroyvo$ = Show$Renderer.prototype.surfacesChanged_yroyvo$;
  FakeNetwork$FakeTcpConnection.prototype.send_chrig3$ = Network$TcpConnection.prototype.send_chrig3$;
  FakeNetwork$FakeLink.prototype.sendUdp_wpmaqi$ = Network$Link.prototype.sendUdp_wpmaqi$;
  FakeNetwork$FakeLink.prototype.broadcastUdp_68hu5j$ = Network$Link.prototype.broadcastUdp_68hu5j$;
  Object.defineProperty(SheepSimulator$NullPixels.prototype, 'indices', Object.getOwnPropertyDescriptor(Pixels.prototype, 'indices'));
  SheepSimulator$NullPixels.prototype.iterator = Pixels.prototype.iterator;
  BrowserNetwork$link$ObjectLiteral$connectTcp$ObjectLiteral.prototype.send_chrig3$ = Network$TcpConnection.prototype.send_chrig3$;
  BrowserNetwork$link$ObjectLiteral.prototype.sendUdp_wpmaqi$ = Network$Link.prototype.sendUdp_wpmaqi$;
  BrowserNetwork$link$ObjectLiteral.prototype.broadcastUdp_68hu5j$ = Network$Link.prototype.broadcastUdp_68hu5j$;
  Object.defineProperty(VizPanel$VizPixels.prototype, 'indices', Object.getOwnPropertyDescriptor(Pixels.prototype, 'indices'));
  VizPanel$VizPixels.prototype.iterator = Pixels.prototype.iterator;
  GadgetDataSerializer = get_map(to(serializer(kotlin_js_internal_StringCompanionObject), JsonElement.Companion.serializer()));
  gadgetModule = SerializersModule(gadgetModule$lambda);
  jsonParser = new Json(JsonConfiguration.Companion.Stable);
  Kotlin.defineModule('sparklemotion', _);
  return _;
}));

//# sourceMappingURL=sparklemotion.js.map
