if (typeof kotlin === 'undefined') {
  throw new Error("Error loading module 'sparklemotion'. Its dependency 'kotlin' was not found. Please, check whether 'kotlin' is loaded prior to 'sparklemotion'.");
}
if (typeof this['kotlinx-coroutines-core'] === 'undefined') {
  throw new Error("Error loading module 'sparklemotion'. Its dependency 'kotlinx-coroutines-core' was not found. Please, check whether 'kotlinx-coroutines-core' is loaded prior to 'sparklemotion'.");
}
if (typeof this['kotlinx-serialization-runtime-js'] === 'undefined') {
  throw new Error("Error loading module 'sparklemotion'. Its dependency 'kotlinx-serialization-runtime-js' was not found. Please, check whether 'kotlinx-serialization-runtime-js' is loaded prior to 'sparklemotion'.");
}
if (typeof this['threejs-wrapper'] === 'undefined') {
  throw new Error("Error loading module 'sparklemotion'. Its dependency 'threejs-wrapper' was not found. Please, check whether 'threejs-wrapper' is loaded prior to 'sparklemotion'.");
}
if (typeof this['kotlinx-html-js'] === 'undefined') {
  throw new Error("Error loading module 'sparklemotion'. Its dependency 'kotlinx-html-js' was not found. Please, check whether 'kotlinx-html-js' is loaded prior to 'sparklemotion'.");
}
var sparklemotion = function (_, Kotlin, $module$kotlinx_coroutines_core, $module$kotlinx_serialization_runtime_js, $module$threejs_wrapper, $module$kotlinx_html_js) {
  'use strict';
  var throwUPAE = Kotlin.throwUPAE;
  var COROUTINE_SUSPENDED = Kotlin.kotlin.coroutines.intrinsics.COROUTINE_SUSPENDED;
  var CoroutineImpl = Kotlin.kotlin.coroutines.CoroutineImpl;
  var L5000 = Kotlin.Long.fromInt(5000);
  var delay = $module$kotlinx_coroutines_core.kotlinx.coroutines.delay_s8cxhz$;
  var contentEquals = Kotlin.arrayEquals;
  var throwCCE = Kotlin.throwCCE;
  var ensureNotNull = Kotlin.ensureNotNull;
  var Kind_CLASS = Kotlin.Kind.CLASS;
  var L0 = Kotlin.Long.ZERO;
  var Exception_init = Kotlin.kotlin.Exception_init_pdl1vj$;
  var toString = Kotlin.kotlin.text.toString_dqglrj$;
  var Random = Kotlin.kotlin.random.Random;
  var trimStart = Kotlin.kotlin.text.trimStart_wqw3xr$;
  var toInt = Kotlin.kotlin.text.toInt_6ic1pp$;
  var IllegalArgumentException_init = Kotlin.kotlin.IllegalArgumentException_init_pdl1vj$;
  var numberToInt = Kotlin.numberToInt;
  var Kind_OBJECT = Kotlin.Kind.OBJECT;
  var SerialClassDescImpl = $module$kotlinx_serialization_runtime_js.kotlinx.serialization.internal.SerialClassDescImpl;
  var UnknownFieldException = $module$kotlinx_serialization_runtime_js.kotlinx.serialization.UnknownFieldException;
  var internal = $module$kotlinx_serialization_runtime_js.kotlinx.serialization.internal;
  var GeneratedSerializer = $module$kotlinx_serialization_runtime_js.kotlinx.serialization.internal.GeneratedSerializer;
  var MissingFieldException = $module$kotlinx_serialization_runtime_js.kotlinx.serialization.MissingFieldException;
  var Math_0 = Math;
  var Pair = Kotlin.kotlin.Pair;
  var mapOf = Kotlin.kotlin.collections.mapOf_qfcya0$;
  var Kind_INTERFACE = Kotlin.Kind.INTERFACE;
  var Unit = Kotlin.kotlin.Unit;
  var IllegalStateException_init = Kotlin.kotlin.IllegalStateException_init_pdl1vj$;
  var equals = Kotlin.equals;
  var ObservableProperty = Kotlin.kotlin.properties.ObservableProperty;
  var getKClass = Kotlin.getKClass;
  var PolymorphicSerializer = $module$kotlinx_serialization_runtime_js.kotlinx.serialization.PolymorphicSerializer;
  var JsonConfiguration = $module$kotlinx_serialization_runtime_js.kotlinx.serialization.json.JsonConfiguration;
  var Json = $module$kotlinx_serialization_runtime_js.kotlinx.serialization.json.Json;
  var println = Kotlin.kotlin.io.println_s8jyv4$;
  var kotlin_js_internal_StringCompanionObject = Kotlin.kotlin.js.internal.StringCompanionObject;
  var serializer = $module$kotlinx_serialization_runtime_js.kotlinx.serialization.serializer_6eet4j$;
  var SerializersModule = $module$kotlinx_serialization_runtime_js.kotlinx.serialization.modules.SerializersModule_q4tcel$;
  var LinkedHashSet_init = Kotlin.kotlin.collections.LinkedHashSet_init_287e2$;
  var ArrayList_init = Kotlin.kotlin.collections.ArrayList_init_287e2$;
  var HashMap_init = Kotlin.kotlin.collections.HashMap_init_q3lmfv$;
  var copyToArray = Kotlin.kotlin.collections.copyToArray;
  var toString_0 = Kotlin.toString;
  var LinkedHashMap_init = Kotlin.kotlin.collections.LinkedHashMap_init_q3lmfv$;
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
  var collectionSizeOrDefault = Kotlin.kotlin.collections.collectionSizeOrDefault_ba2ldo$;
  var ArrayList_init_0 = Kotlin.kotlin.collections.ArrayList_init_ww73n8$;
  var coroutines = $module$kotlinx_coroutines_core.kotlinx.coroutines;
  var toList = Kotlin.kotlin.collections.toList_7wnvza$;
  var L50 = Kotlin.Long.fromInt(50);
  var emptyList = Kotlin.kotlin.collections.emptyList_287e2$;
  var mapCapacity = Kotlin.kotlin.collections.mapCapacity_za3lpa$;
  var coerceAtLeast = Kotlin.kotlin.ranges.coerceAtLeast_dqglrj$;
  var LinkedHashMap_init_0 = Kotlin.kotlin.collections.LinkedHashMap_init_bwtc7$;
  var plus = $module$kotlinx_serialization_runtime_js.kotlinx.serialization.modules.plus_7n7cf$;
  var modules = $module$kotlinx_serialization_runtime_js.kotlinx.serialization.modules;
  var NotImplementedError_init = Kotlin.kotlin.NotImplementedError;
  var Enum = Kotlin.kotlin.Enum;
  var throwISE = Kotlin.throwISE;
  var toByte = Kotlin.toByte;
  var lazy = Kotlin.kotlin.lazy_klfg04$;
  var Regex_init = Kotlin.kotlin.text.Regex_init_61zpoe$;
  var split = Kotlin.kotlin.text.split_ip8yn$;
  var joinToString = Kotlin.kotlin.collections.joinToString_fmv235$;
  var toInt_0 = Kotlin.kotlin.text.toInt_pdl1vz$;
  var sorted = Kotlin.kotlin.collections.sorted_exjks8$;
  var arrayListOf = Kotlin.kotlin.collections.arrayListOf_i5x0yv$;
  var trim = Kotlin.kotlin.text.trim_gw00vp$;
  var toDouble = Kotlin.kotlin.text.toDouble_pdl1vz$;
  var addAll = Kotlin.kotlin.collections.addAll_ipc267$;
  var rangeTo = Kotlin.kotlin.ranges.rangeTo_38ydlf$;
  var get_list = $module$kotlinx_serialization_runtime_js.kotlinx.serialization.get_list_gekvwj$;
  var PropertyMetadata = Kotlin.PropertyMetadata;
  var JsonPrimitive = $module$kotlinx_serialization_runtime_js.kotlinx.serialization.json.JsonPrimitive_rcaewn$;
  var to = Kotlin.kotlin.to_ujzrz7$;
  var mapOf_0 = Kotlin.kotlin.collections.mapOf_x2b85n$;
  var JsonObject = $module$kotlinx_serialization_runtime_js.kotlinx.serialization.json.JsonObject;
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
  var Array_0 = Array;
  var math = Kotlin.kotlin.math;
  var listOf = Kotlin.kotlin.collections.listOf_i5x0yv$;
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
  var Quaternion = THREE.Quaternion;
  var Matrix4_init = THREE.Matrix4;
  var kotlin_js_internal_FloatCompanionObject = Kotlin.kotlin.js.internal.FloatCompanionObject;
  var Line3 = THREE.Line3;
  var ConeBufferGeometry = THREE.ConeBufferGeometry;
  var Points = THREE.Points;
  var getCallableRef = Kotlin.getCallableRef;
  var OrbitControls = THREE.OrbitControls;
  var PointsMaterial = THREE.PointsMaterial;
  var Raycaster_init = THREE.Raycaster;
  var minus = $module$threejs_wrapper.info.laht.threekt.math.minus_gulir3$;
  var Float32BufferAttribute = THREE.Float32BufferAttribute;
  var Triangle = THREE.Triangle;
  var indexOf = Kotlin.kotlin.collections.indexOf_mjy6jw$;
  var sorted_0 = Kotlin.kotlin.collections.sorted_pbinho$;
  GadgetValueObserver.prototype = Object.create(ObservableProperty.prototype);
  GadgetValueObserver.prototype.constructor = GadgetValueObserver;
  PubSub$Connection$receive$ObjectLiteral.prototype = Object.create(PubSub$Listener.prototype);
  PubSub$Connection$receive$ObjectLiteral.prototype.constructor = PubSub$Connection$receive$ObjectLiteral;
  PubSub$Connection.prototype = Object.create(PubSub$Origin.prototype);
  PubSub$Connection.prototype.constructor = PubSub$Connection;
  PubSub$Server$publish$ObjectLiteral.prototype = Object.create(PubSub$Listener.prototype);
  PubSub$Server$publish$ObjectLiteral.prototype.constructor = PubSub$Server$publish$ObjectLiteral;
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
  ColorPicker.prototype = Object.create(Gadget.prototype);
  ColorPicker.prototype.constructor = ColorPicker;
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
  BrainMapping.prototype = Object.create(Message.prototype);
  BrainMapping.prototype.constructor = BrainMapping;
  PinkyPongMessage.prototype = Object.create(Message.prototype);
  PinkyPongMessage.prototype.constructor = PinkyPongMessage;
  CompositorShader.prototype = Object.create(Shader.prototype);
  CompositorShader.prototype.constructor = CompositorShader;
  CompositingMode.prototype = Object.create(Enum.prototype);
  CompositingMode.prototype.constructor = CompositingMode;
  CompositingMode$OVERLAY.prototype = Object.create(CompositingMode.prototype);
  CompositingMode$OVERLAY.prototype.constructor = CompositingMode$OVERLAY;
  CompositingMode$ADD.prototype = Object.create(CompositingMode.prototype);
  CompositingMode$ADD.prototype.constructor = CompositingMode$ADD;
  PixelShader.prototype = Object.create(Shader.prototype);
  PixelShader.prototype.constructor = PixelShader;
  SimpleSpatialShader.prototype = Object.create(Shader.prototype);
  SimpleSpatialShader.prototype.constructor = SimpleSpatialShader;
  SineWaveShader.prototype = Object.create(Shader.prototype);
  SineWaveShader.prototype.constructor = SineWaveShader;
  SolidShader.prototype = Object.create(Shader.prototype);
  SolidShader.prototype.constructor = SolidShader;
  SparkleShader.prototype = Object.create(Shader.prototype);
  SparkleShader.prototype.constructor = SparkleShader;
  CompositeShow.prototype = Object.create(Show$MetaData.prototype);
  CompositeShow.prototype.constructor = CompositeShow;
  LifeyShow.prototype = Object.create(Show$MetaData.prototype);
  LifeyShow.prototype.constructor = LifeyShow;
  PanelTweenShow.prototype = Object.create(Show$MetaData.prototype);
  PanelTweenShow.prototype.constructor = PanelTweenShow;
  PixelTweenShow.prototype = Object.create(Show$MetaData.prototype);
  PixelTweenShow.prototype.constructor = PixelTweenShow;
  RandomShow.prototype = Object.create(Show$MetaData.prototype);
  RandomShow.prototype.constructor = RandomShow;
  SimpleSpatialShow.prototype = Object.create(Show$MetaData.prototype);
  SimpleSpatialShow.prototype.constructor = SimpleSpatialShow;
  SolidColorShow.prototype = Object.create(Show$MetaData.prototype);
  SolidColorShow.prototype.constructor = SolidColorShow;
  SomeDumbShow.prototype = Object.create(Show$MetaData.prototype);
  SomeDumbShow.prototype.constructor = SomeDumbShow;
  ThumpShow.prototype = Object.create(Show$MetaData.prototype);
  ThumpShow.prototype.constructor = ThumpShow;
  FakeDmxUniverse.prototype = Object.create(Dmx$Universe.prototype);
  FakeDmxUniverse.prototype.constructor = FakeDmxUniverse;
  JsPinkyDisplay$ShowButton.prototype = Object.create(Button.prototype);
  JsPinkyDisplay$ShowButton.prototype.constructor = JsPinkyDisplay$ShowButton;
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
    this.surface_0 = new Brain$UnmappedSurface(this);
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
            this.$this.display_0.haveLink_9m0ekx$(this.$this.link_0);
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
            var tmp$;
            this.state_0 = 2;
            continue;
          case 1:
            throw this.exception_0;
          case 2:
            if (this.$this.lastInstructionsReceivedAtMs_0.compareTo_11rb$(getTimeMillis().subtract(Kotlin.Long.fromInt(10000))) < 0) {
              this.$this.link_0.broadcastUdp_68hu5j$(8002, new BrainHelloMessage(this.$this.id, (tmp$ = this.$this.surfaceName_0) != null ? tmp$ : ''));
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
          this.currentShaderBits_0 = new Brain$ShaderBits(shader, shader.createRenderer_onphn6$(this.surface_0, this.pixels_0), shader.createBuffer_ppt8xj$(this.surface_0));
        }

        var $receiver = ensureNotNull(this.currentShaderBits_0);
        $receiver.read_100t80$(reader);
        $receiver.draw();
        break;
      case 'BRAIN_ID_REQUEST':
        var message = BrainIdRequest$Companion_getInstance().parse_100t80$(reader);
        this.link_0.sendUdp_wpmaqi$(fromAddress, message.port, new BrainIdResponse(this.id, this.surfaceName_0));
        break;
      case 'BRAIN_MAPPING':
        var message_0 = BrainMapping$Companion_getInstance().parse_100t80$(reader);
        this.surfaceName_0 = message_0.surfaceName;
        this.surface_0 = new Brain$MappedSurface(this, message_0.pixelCount, message_0.pixelVertices);
        this.currentShaderDesc_0 = null;
        this.currentShaderBits_0 = null;
        this.link_0.broadcastUdp_68hu5j$(8002, new BrainHelloMessage(this.id, (tmp$_0 = this.surfaceName_0) != null ? tmp$_0 : ''));
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
  Brain$ShaderBits.prototype.draw = function () {
    this.renderer.draw_433sc5$(this.buffer);
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
  Brain$UnmappedSurface.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'UnmappedSurface',
    interfaces: [Surface]
  };
  function Brain$MappedSurface($outer, pixelCount, pixelVertices) {
    this.$outer = $outer;
    if (pixelVertices === void 0)
      pixelVertices = null;
    this.pixelCount_vi6r5t$_0 = pixelCount;
    this.pixelVertices = pixelVertices;
  }
  Object.defineProperty(Brain$MappedSurface.prototype, 'pixelCount', {
    get: function () {
      return this.pixelCount_vi6r5t$_0;
    }
  });
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
    return Color_init_1(this.redF + (1 - this.redF) * desaturation, this.greenF + (1 - this.greenF) * desaturation, this.blueF + (1 - this.blueF) * desaturation, this.alphaF);
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
    return Color_init_2(this.redI + other.redI | 0, this.greenI + other.greenI | 0, this.blueI + other.blueI | 0, this.alphaI);
  };
  Color.prototype.fade_6zkv30$ = function (other, amount) {
    if (amount === void 0)
      amount = 0.5;
    var amountThis = 1 - amount;
    return Color_init_1(this.redF * amountThis + other.redF * amount, this.greenF * amountThis + other.greenF * amount, this.blueF * amountThis + other.blueF * amount, this.alphaF * amountThis + other.alphaF * amount);
  };
  Color.prototype.toString = function () {
    return 'Color(' + this.toHexString() + ')';
  };
  function Color$Companion() {
    Color$Companion_instance = this;
    this.BLACK = Color_init_2(0, 0, 0);
    this.WHITE = Color_init_2(255, 255, 255);
    this.RED = Color_init_2(255, 0, 0);
    this.ORANGE = Color_init_2(255, 127, 0);
    this.YELLOW = Color_init_2(255, 255, 0);
    this.GREEN = Color_init_2(0, 255, 0);
    this.BLUE = Color_init_2(0, 0, 255);
    this.PURPLE = Color_init_2(200, 0, 212);
  }
  Color$Companion.prototype.random = function () {
    return Color_init_2(Random.Default.nextInt() & 255, Random.Default.nextInt() & 255, Random.Default.nextInt() & 255);
  };
  Color$Companion.prototype.parse_100t80$ = function (reader) {
    return new Color(reader.readInt());
  };
  Color$Companion.prototype.fromInts = function (i) {
    return new Color(i);
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
  Color$Companion.prototype.serializer = function () {
    return Color$$serializer_getInstance();
  };
  Color$Companion.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'Companion',
    interfaces: []
  };
  var Color$Companion_instance = null;
  function Color$Companion_getInstance() {
    if (Color$Companion_instance === null) {
      new Color$Companion();
    }
    return Color$Companion_instance;
  }
  function Color$$serializer() {
    this.descriptor_h91vo$_0 = new SerialClassDescImpl('baaahs.Color', this);
    this.descriptor.addElement_ivxn3r$('argb', false);
    Color$$serializer_instance = this;
  }
  Object.defineProperty(Color$$serializer.prototype, 'descriptor', {
    get: function () {
      return this.descriptor_h91vo$_0;
    }
  });
  Color$$serializer.prototype.serialize_awe97i$ = function (encoder, obj) {
    var output = encoder.beginStructure_r0sa6z$(this.descriptor, []);
    output.encodeIntElement_4wpqag$(this.descriptor, 0, obj.argb);
    output.endStructure_qatsm0$(this.descriptor);
  };
  Color$$serializer.prototype.deserialize_nts5qn$ = function (decoder) {
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
          local0 = input.decodeIntElement_3zr2iy$(this.descriptor, 0);
          bitMask0 |= 1;
          if (!readAll)
            break;
        case -1:
          break loopLabel;
        default:throw new UnknownFieldException(index);
      }
    }
    input.endStructure_qatsm0$(this.descriptor);
    return Color_init_0(bitMask0, local0, null);
  };
  Color$$serializer.prototype.childSerializers = function () {
    return [internal.IntSerializer];
  };
  Color$$serializer.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: '$serializer',
    interfaces: [GeneratedSerializer]
  };
  var Color$$serializer_instance = null;
  function Color$$serializer_getInstance() {
    if (Color$$serializer_instance === null) {
      new Color$$serializer();
    }
    return Color$$serializer_instance;
  }
  function Color_init_0(seen1, argb, serializationConstructorMarker) {
    var $this = serializationConstructorMarker || Object.create(Color.prototype);
    if ((seen1 & 1) === 0)
      throw new MissingFieldException('argb');
    else
      $this.argb = argb;
    return $this;
  }
  Color.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Color',
    interfaces: []
  };
  function Color_init_1(red, green, blue, alpha, $this) {
    if (alpha === void 0)
      alpha = 1.0;
    $this = $this || Object.create(Color.prototype);
    Color.call($this, Color$Companion_getInstance().asArgb_1(red, green, blue, alpha));
    return $this;
  }
  function Color_init_2(red, green, blue, alpha, $this) {
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
  StubPinkyDisplay.prototype.listShows_qxmw8h$ = function (showMetas) {
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
  }
  Gadget.prototype.listen = function (gadgetListener) {
    if (!this.listeners_zg49rb$_0.add_11rb$(gadgetListener))
      throw IllegalStateException_init(gadgetListener.toString() + ' already listening to ' + this);
  };
  Gadget.prototype.unlisten = function (gadgetListener) {
    if (!this.listeners_zg49rb$_0.remove_11rb$(gadgetListener))
      throw IllegalStateException_init(gadgetListener.toString() + " isn't listening to " + this);
  };
  Gadget.prototype.withoutTriggering = function () {
    var tmp$;
    tmp$ = this.listeners_zg49rb$_0.iterator();
    while (tmp$.hasNext()) {
      var element = tmp$.next();
      element.onChanged(this);
    }
  };
  Gadget.prototype.withoutTriggering_zde6sx$ = function (gadgetListener, fn) {
    this.unlisten(gadgetListener);
    try {
      fn();
    }
    finally {
      this.listen(gadgetListener);
    }
  };
  function Gadget$watchForChanges$lambda(this$Gadget) {
    return function () {
      this$Gadget.withoutTriggering();
      return Unit;
    };
  }
  Gadget.prototype.watchForChanges_mh5how$ = function (initialValue) {
    return new GadgetValueObserver(initialValue, Gadget$watchForChanges$lambda(this));
  };
  Gadget.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Gadget',
    interfaces: []
  };
  function GadgetListener() {
  }
  GadgetListener.$metadata$ = {
    kind: Kind_INTERFACE,
    simpleName: 'GadgetListener',
    interfaces: []
  };
  function GadgetValueObserver(initialValue, onChange) {
    ObservableProperty.call(this, initialValue);
    this.onChange = onChange;
  }
  GadgetValueObserver.prototype.afterChange_jxtfl0$ = function (property, oldValue, newValue) {
    if (!equals(newValue, oldValue))
      this.onChange();
  };
  GadgetValueObserver.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'GadgetValueObserver',
    interfaces: [ObservableProperty]
  };
  function GadgetData(gadget, topicName) {
    GadgetData$Companion_getInstance();
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
    output.encodeSerializableElement_blecud$(this.descriptor, 0, new PolymorphicSerializer(getKClass(Gadget)), obj.gadget);
    output.encodeStringElement_bgm7zs$(this.descriptor, 1, obj.topicName);
    output.endStructure_qatsm0$(this.descriptor);
  };
  GadgetData$$serializer.prototype.deserialize_nts5qn$ = function (decoder) {
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
          local0 = (bitMask0 & 1) === 0 ? input.decodeSerializableElement_s44l7r$(this.descriptor, 0, new PolymorphicSerializer(getKClass(Gadget))) : input.updateSerializableElement_ehubvl$(this.descriptor, 0, new PolymorphicSerializer(getKClass(Gadget)), local0);
          bitMask0 |= 1;
          if (!readAll)
            break;
        case 1:
          local1 = input.decodeStringElement_3zr2iy$(this.descriptor, 1);
          bitMask0 |= 2;
          if (!readAll)
            break;
        case -1:
          break loopLabel;
        default:throw new UnknownFieldException(index);
      }
    }
    input.endStructure_qatsm0$(this.descriptor);
    return GadgetData_init(bitMask0, local0, local1, null);
  };
  GadgetData$$serializer.prototype.childSerializers = function () {
    return [new PolymorphicSerializer(getKClass(Gadget)), internal.StringSerializer];
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
  function GadgetData_init(seen1, gadget, topicName, serializationConstructorMarker) {
    var $this = serializationConstructorMarker || Object.create(GadgetData.prototype);
    if ((seen1 & 1) === 0)
      throw new MissingFieldException('gadget');
    else
      $this.gadget = gadget;
    if ((seen1 & 2) === 0)
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
  function GadgetDisplay(pubSub, onUpdatedGadgets) {
    this.activeGadgets = ArrayList_init();
    this.channels = HashMap_init();
    this.jsonParser = new Json(JsonConfiguration.Companion.Stable);
    pubSub.subscribe(Topics_getInstance().activeGadgets, GadgetDisplay_init$lambda(this, pubSub, onUpdatedGadgets));
  }
  function GadgetDisplay_init$lambda$lambda$ObjectLiteral(this$GadgetDisplay, closure$topicName) {
    this.this$GadgetDisplay = this$GadgetDisplay;
    this.closure$topicName = closure$topicName;
  }
  GadgetDisplay_init$lambda$lambda$ObjectLiteral.prototype.onChanged = function (gadget) {
    var observer = this.this$GadgetDisplay.channels.get_11rb$(this.closure$topicName);
    if (observer == null) {
      println('Huh, no observer for ' + this.closure$topicName + '; discarding update (know about ' + this.this$GadgetDisplay.channels.keys + ')');
    }
     else {
      observer.onChange(gadget.toJson().toString());
    }
  };
  GadgetDisplay_init$lambda$lambda$ObjectLiteral.$metadata$ = {
    kind: Kind_CLASS,
    interfaces: [GadgetListener]
  };
  function GadgetDisplay_init$lambda$lambda$lambda$lambda$lambda(this$GadgetDisplay, closure$json, this$) {
    return function () {
      this$.setFromJson_qiw0cd$(this$GadgetDisplay.jsonParser.parseJson_61zpoe$(closure$json));
      return Unit;
    };
  }
  function GadgetDisplay_init$lambda$lambda$lambda(closure$gadget, closure$listener, this$GadgetDisplay) {
    return function (json) {
      var $receiver = closure$gadget;
      $receiver.withoutTriggering_zde6sx$(closure$listener, GadgetDisplay_init$lambda$lambda$lambda$lambda$lambda(this$GadgetDisplay, json, $receiver));
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
        var listener = new GadgetDisplay_init$lambda$lambda$ObjectLiteral(this$GadgetDisplay_0, topicName);
        gadget.listen(listener);
        var $receiver = this$GadgetDisplay_0.channels;
        var value = closure$pubSub_0.subscribe(new PubSub$Topic(topicName, serializer(kotlin_js_internal_StringCompanionObject)), GadgetDisplay_init$lambda$lambda$lambda(gadget, listener, this$GadgetDisplay_0));
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
    $receiver.with_kmpi2j$(getKClass(Slider), Slider$Companion_getInstance().serializer());
    return Unit;
  }
  function gadgetModule$lambda($receiver) {
    $receiver.polymorphic_myr6su$(getKClass(Gadget), [], gadgetModule$lambda$lambda);
    return Unit;
  }
  var gadgetModule;
  function GadgetProvider(pubSub) {
    this.pubSub_0 = pubSub;
    this.jsonParser = new Json(JsonConfiguration.Companion.Stable);
    this.activeGadgets_0 = ArrayList_init();
    this.activeGadgetChannel_0 = this.pubSub_0.publish_oiz02e$(Topics_getInstance().activeGadgets, this.activeGadgets_0, GadgetProvider$activeGadgetChannel$lambda);
    this.gadgets_0 = LinkedHashMap_init();
    this.nextGadgetId_0 = 1;
  }
  function GadgetProvider$getGadget$lambda(closure$gadget, this$GadgetProvider) {
    return function (updated) {
      closure$gadget.setFromJson_qiw0cd$(this$GadgetProvider.jsonParser.parseJson_61zpoe$(updated));
      return Unit;
    };
  }
  GadgetProvider.prototype.getGadget_87gk9q$ = function (gadget) {
    var tmp$;
    var gadgetId = (tmp$ = this.nextGadgetId_0, this.nextGadgetId_0 = tmp$ + 1 | 0, tmp$);
    var topic = new PubSub$Topic('/gadgets/' + toString_0(Kotlin.getKClassFromExpression(gadget).simpleName) + '/' + gadgetId, serializer(kotlin_js_internal_StringCompanionObject));
    var channel = this.pubSub_0.publish_oiz02e$(topic, gadget.toJson().toString(), GadgetProvider$getGadget$lambda(gadget, this));
    var $receiver = this.gadgets_0;
    var value = new GadgetProvider$GadgetChannel(topic, channel);
    $receiver.put_xwzc9p$(gadget, value);
    this.activeGadgets_0.add_11rb$(new GadgetData(gadget, topic.name));
    return gadget;
  };
  GadgetProvider.prototype.clear = function () {
    var tmp$;
    tmp$ = this.gadgets_0.values.iterator();
    while (tmp$.hasNext()) {
      var element = tmp$.next();
      element.channel.unsubscribe();
    }
    this.gadgets_0.clear();
    this.activeGadgets_0.clear();
  };
  GadgetProvider.prototype.sync = function () {
    this.activeGadgetChannel_0.onChange(this.activeGadgets_0);
  };
  function GadgetProvider$GadgetChannel(topic, channel) {
    this.topic = topic;
    this.channel = channel;
  }
  GadgetProvider$GadgetChannel.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'GadgetChannel',
    interfaces: []
  };
  function GadgetProvider$activeGadgetChannel$lambda(it) {
    return Unit;
  }
  GadgetProvider.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'GadgetProvider',
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
  function Pinky(sheepModel, showMetas, network, dmxUniverse, display) {
    this.sheepModel = sheepModel;
    this.showMetas = showMetas;
    this.network = network;
    this.dmxUniverse = dmxUniverse;
    this.display = display;
    this.link_0 = new FragmentingUdpLink(this.network.link());
    this.brains_0 = LinkedHashMap_init();
    this.beatProvider_0 = new Pinky$PinkyBeatProvider(this, 120.0);
    this.mapperIsRunning_0 = false;
    this.brainsChanged_0 = true;
    this.selectedShow_vpdlot$_0 = first(this.showMetas);
    this.showRunner_vuzmgu$_0 = this.showRunner_vuzmgu$_0;
    var $receiver = this.sheepModel.allPanels;
    var capacity = coerceAtLeast(mapCapacity(collectionSizeOrDefault($receiver, 10)), 16);
    var destination = LinkedHashMap_init_0(capacity);
    var tmp$;
    tmp$ = $receiver.iterator();
    while (tmp$.hasNext()) {
      var element = tmp$.next();
      destination.put_xwzc9p$(element.name, element);
    }
    this.surfacesByName_0 = destination;
    this.pixelsBySurface_0 = LinkedHashMap_init();
    this.surfacesByBrainId_0 = LinkedHashMap_init();
  }
  Object.defineProperty(Pinky.prototype, 'selectedShow_0', {
    get: function () {
      return this.selectedShow_vpdlot$_0;
    },
    set: function (value) {
      this.selectedShow_vpdlot$_0 = value;
      this.display.selectedShow = value;
    }
  });
  Object.defineProperty(Pinky.prototype, 'showRunner_0', {
    get: function () {
      if (this.showRunner_vuzmgu$_0 == null)
        return throwUPAE('showRunner');
      return this.showRunner_vuzmgu$_0;
    },
    set: function (showRunner) {
      this.showRunner_vuzmgu$_0 = showRunner;
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
      var $receiver = this$Pinky.showMetas;
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
  function Pinky$run$lambda_3(closure$gadgetProvider, this$Pinky) {
    return function () {
      return new ShowRunner(closure$gadgetProvider, toList(this$Pinky.brains_0.values), this$Pinky.beatProvider_0, this$Pinky.dmxUniverse);
    };
  }
  function Pinky$run$lambda_4(this$Pinky, closure$currentShowMetaData, closure$gadgetProvider) {
    return function () {
      var $receiver = this$Pinky.selectedShow_0.createShow_h1b9op$(this$Pinky.sheepModel, this$Pinky.showRunner_0);
      var this$Pinky_0 = this$Pinky;
      closure$currentShowMetaData.v = this$Pinky_0.selectedShow_0;
      closure$gadgetProvider.sync();
      return $receiver;
    };
  }
  function Pinky$run$lambda_5(closure$show) {
    return function () {
      closure$show.v.nextFrame();
      return Unit;
    };
  }
  function Coroutine$run_1($this, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.exceptionState_0 = 1;
    this.$this = $this;
    this.local$buildShowRunner = void 0;
    this.local$currentShowMetaData = void 0;
    this.local$buildShow = void 0;
    this.local$show = void 0;
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
            this.$this.display.listShows_qxmw8h$(this.$this.showMetas);
            this.$this.display.selectedShow = this.$this.selectedShow_0;
            var pubSub = new PubSub$Server(this.$this.link_0, 8004);
            pubSub.install_stpyu4$(gadgetModule);
            var tmp$ = Topics_getInstance().availableShows;
            var $receiver = this.$this.showMetas;
            var destination = ArrayList_init_0(collectionSizeOrDefault($receiver, 10));
            var tmp$_0;
            tmp$_0 = $receiver.iterator();
            while (tmp$_0.hasNext()) {
              var item = tmp$_0.next();
              destination.add_11rb$(item.name);
            }

            pubSub.publish_oiz02e$(tmp$, destination, Pinky$run$lambda_0);
            var selectedShowChannel = pubSub.publish_oiz02e$(Topics_getInstance().selectedShow, this.$this.showMetas.get_za3lpa$(0).name, Pinky$run$lambda_1(this.$this));
            this.$this.display.onShowChange = Pinky$run$lambda_2(this.$this, selectedShowChannel);
            var gadgetProvider = new GadgetProvider(pubSub);
            this.local$buildShowRunner = Pinky$run$lambda_3(gadgetProvider, this.$this);
            this.local$currentShowMetaData = {v: this.$this.selectedShow_0};
            this.local$buildShow = Pinky$run$lambda_4(this.$this, this.local$currentShowMetaData, gadgetProvider);
            this.$this.showRunner_0 = this.local$buildShowRunner();
            this.local$show = {v: this.local$buildShow()};
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
            if (this.$this.brainsChanged_0 || !equals(this.$this.selectedShow_0, this.local$currentShowMetaData.v)) {
              if (this.$this.brainsChanged_0) {
                logger$Companion_getInstance().debug_61zpoe$('Brains changed!');
              }
              this.$this.showRunner_0.shutDown();
              this.$this.showRunner_0 = this.local$buildShowRunner();
              this.local$show.v = this.local$buildShow();
              this.$this.brainsChanged_0 = false;
            }

            var elapsedMs = time(Pinky$run$lambda_5(this.local$show));
            this.$this.display.nextFrameMs = elapsedMs.toInt();
            var stats = new ShowRunner$Stats();
            this.$this.showRunner_0.send_359yhm$(this.$this.link_0, stats);
            this.$this.display.stats = stats;
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
  Pinky.prototype.disableDmx_0 = function () {
    this.dmxUniverse.allOff();
  };
  Pinky.prototype.receive_rq4egf$ = function (fromAddress, bytes) {
    var message = parse(bytes);
    if (Kotlin.isType(message, BrainHelloMessage)) {
      var panelName = message.panelName;
      var surface = this.surfacesByName_0.get_11rb$(panelName);
      if (surface == null) {
        this.maybeMoreMapping_0(fromAddress, message);
      }
       else {
        this.foundBrain_0(new RemoteBrain(fromAddress, message.brainId, surface));
      }
    }
     else if (Kotlin.isType(message, MapperHelloMessage))
      this.mapperIsRunning_0 = message.isRunning;
  };
  Pinky.prototype.maybeMoreMapping_0 = function (address, message) {
    var tmp$, tmp$_0;
    var surface = this.surfacesByBrainId_0.get_11rb$(message.brainId);
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
      var mappingMsg = new BrainMapping(message.brainId, surface.name, pixelCount, pixelVertices);
      this.link_0.sendUdp_wpmaqi$(address, 8003, mappingMsg);
    }
  };
  function Pinky$unknownSurface$ObjectLiteral() {
    this.pixelCount_mxdgq0$_0 = -1;
  }
  Object.defineProperty(Pinky$unknownSurface$ObjectLiteral.prototype, 'pixelCount', {
    get: function () {
      return this.pixelCount_mxdgq0$_0;
    }
  });
  Pinky$unknownSurface$ObjectLiteral.$metadata$ = {
    kind: Kind_CLASS,
    interfaces: [Surface]
  };
  Pinky.prototype.unknownSurface_0 = function () {
    return new Pinky$unknownSurface$ObjectLiteral();
  };
  Pinky.prototype.foundBrain_0 = function (remoteBrain) {
    this.brains_0.put_xwzc9p$(remoteBrain.address, remoteBrain);
    this.display.brainCount = this.brains_0.size;
    this.brainsChanged_0 = true;
  };
  Pinky.prototype.providePanelMapping_jm2l9z$ = function (brainId, surface) {
    this.surfacesByBrainId_0.put_xwzc9p$(brainId, surface);
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
  Pinky.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Pinky',
    interfaces: [Network$UdpListener]
  };
  function RemoteBrain(address, brainId, surface) {
    this.address = address;
    this.brainId = brainId;
    this.surface = surface;
  }
  RemoteBrain.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'RemoteBrain',
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
  function PubSub$Listener(origin) {
    this.origin_fg10in$_0 = origin;
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
    logger$Companion_getInstance().debug_61zpoe$('[' + tcpConnection.fromAddress + ' -> ' + this.name_qs3czq$_0 + '] PubSub: new ' + this + ' connection');
    this.connection = tcpConnection;
    var tmp$;
    tmp$ = this.toSend_p0j902$_0.iterator();
    while (tmp$.hasNext()) {
      var element = tmp$.next();
      tcpConnection.send_fqrh44$(element);
    }
    this.toSend_p0j902$_0.clear();
  };
  function PubSub$Connection$receive$ObjectLiteral(closure$topicName, this$Connection, origin) {
    this.closure$topicName = closure$topicName;
    this.this$Connection = this$Connection;
    PubSub$Listener.call(this, origin);
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
        println('[' + tcpConnection.fromAddress + ' -> ' + this.name_qs3czq$_0 + '] sub ' + topicName);
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
        var topic = new PubSub$Topic(topicName_0, serializer(kotlin_js_internal_StringCompanionObject));
        var data = reader.readString();
        println('[' + tcpConnection.fromAddress + ' -> ' + this.name_qs3czq$_0 + '] update ' + topicName_0 + ' ' + data);
        var topicInfo_0 = this.topics_okivn7$_0.get_11rb$(topic.name);
        topicInfo_0 != null ? (topicInfo_0.notify_btyzc5$(data, this), Unit) : null;
        break;
      default:IllegalArgumentException_init("huh? don't know what to do with " + command);
        break;
    }
  };
  PubSub$Connection.prototype.sendTopicUpdate_puj7f4$ = function (name, data) {
    var tmp$;
    var writer = new ByteArrayWriter();
    println('-> update ' + name + ' ' + data + ' to ' + toString_0((tmp$ = this.connection) != null ? tmp$.toAddress : null));
    writer.writeString_61zpoe$('update');
    writer.writeString_61zpoe$(name);
    writer.writeString_61zpoe$(data);
    this.sendCommand_su7uv8$_0(writer.toBytes());
  };
  PubSub$Connection.prototype.sendTopicSub_61zpoe$ = function (topicName) {
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
    return new PubSub$Connection('server', this.topics_0);
  };
  function PubSub$Server$publish$ObjectLiteral(closure$onUpdate, this$Server, closure$topic, origin) {
    this.closure$onUpdate = closure$onUpdate;
    this.this$Server = this$Server;
    this.closure$topic = closure$topic;
    PubSub$Listener.call(this, origin);
  }
  PubSub$Server$publish$ObjectLiteral.prototype.onUpdate_61zpoe$ = function (data) {
    this.closure$onUpdate(this.this$Server.json.parse_awif5v$(this.closure$topic.serializer, data));
  };
  PubSub$Server$publish$ObjectLiteral.$metadata$ = {
    kind: Kind_CLASS,
    interfaces: [PubSub$Listener]
  };
  function PubSub$Server$publish$ObjectLiteral_0(closure$topicInfo, this$Server, closure$topic, closure$publisher) {
    this.closure$topicInfo = closure$topicInfo;
    this.this$Server = this$Server;
    this.closure$topic = closure$topic;
    this.closure$publisher = closure$publisher;
  }
  PubSub$Server$publish$ObjectLiteral_0.prototype.onChange = function (t) {
    this.closure$topicInfo.notify_btyzc5$(this.this$Server.json.stringify_tf03ej$(this.closure$topic.serializer, t), this.closure$publisher);
  };
  PubSub$Server$publish$ObjectLiteral_0.prototype.unsubscribe = function () {
  };
  PubSub$Server$publish$ObjectLiteral_0.$metadata$ = {
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
    topicInfo.listeners.add_11rb$(new PubSub$Server$publish$ObjectLiteral(onUpdate, this, topic, publisher));
    topicInfo.notify_btyzc5$(jsonData, publisher);
    return new PubSub$Server$publish$ObjectLiteral_0(topicInfo, this, topic, publisher);
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
  function PubSub$Client$subscribe$lambda$lambda$ObjectLiteral(this$Client, closure$topicName, origin) {
    this.this$Client = this$Client;
    this.closure$topicName = closure$topicName;
    PubSub$Listener.call(this, origin);
  }
  PubSub$Client$subscribe$lambda$lambda$ObjectLiteral.prototype.onUpdate_61zpoe$ = function (data) {
    this.this$Client.server_0.sendTopicUpdate_puj7f4$(this.closure$topicName, data);
  };
  PubSub$Client$subscribe$lambda$lambda$ObjectLiteral.$metadata$ = {
    kind: Kind_CLASS,
    interfaces: [PubSub$Listener]
  };
  function PubSub$Client$subscribe$ObjectLiteral(closure$onUpdate, this$Client, closure$topic, origin) {
    this.closure$onUpdate = closure$onUpdate;
    this.this$Client = this$Client;
    this.closure$topic = closure$topic;
    PubSub$Listener.call(this, origin);
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
  function ShaderId(name, ordinal, parser) {
    Enum.call(this);
    this.parser = parser;
    this.name$ = name;
    this.ordinal$ = ordinal;
  }
  function ShaderId_initFields() {
    ShaderId_initFields = function () {
    };
    ShaderId$SOLID_instance = new ShaderId('SOLID', 0, ShaderId$ShaderId$SOLID_init$lambda);
    ShaderId$PIXEL_instance = new ShaderId('PIXEL', 1, ShaderId$ShaderId$PIXEL_init$lambda);
    ShaderId$SINE_WAVE_instance = new ShaderId('SINE_WAVE', 2, ShaderId$ShaderId$SINE_WAVE_init$lambda);
    ShaderId$COMPOSITOR_instance = new ShaderId('COMPOSITOR', 3, ShaderId$ShaderId$COMPOSITOR_init$lambda);
    ShaderId$SPARKLE_instance = new ShaderId('SPARKLE', 4, ShaderId$ShaderId$SPARKLE_init$lambda);
    ShaderId$SIMPLE_SPATIAL_instance = new ShaderId('SIMPLE_SPATIAL', 5, ShaderId$ShaderId$SIMPLE_SPATIAL_init$lambda);
    ShaderId$Companion_getInstance();
  }
  function ShaderId$ShaderId$SOLID_init$lambda(reader) {
    return SolidShader$Companion_getInstance().parse_100t80$(reader);
  }
  var ShaderId$SOLID_instance;
  function ShaderId$SOLID_getInstance() {
    ShaderId_initFields();
    return ShaderId$SOLID_instance;
  }
  function ShaderId$ShaderId$PIXEL_init$lambda(reader) {
    return PixelShader$Companion_getInstance().parse_100t80$(reader);
  }
  var ShaderId$PIXEL_instance;
  function ShaderId$PIXEL_getInstance() {
    ShaderId_initFields();
    return ShaderId$PIXEL_instance;
  }
  function ShaderId$ShaderId$SINE_WAVE_init$lambda(reader) {
    return SineWaveShader$Companion_getInstance().parse_100t80$(reader);
  }
  var ShaderId$SINE_WAVE_instance;
  function ShaderId$SINE_WAVE_getInstance() {
    ShaderId_initFields();
    return ShaderId$SINE_WAVE_instance;
  }
  function ShaderId$ShaderId$COMPOSITOR_init$lambda(reader) {
    return CompositorShader$Companion_getInstance().parse_100t80$(reader);
  }
  var ShaderId$COMPOSITOR_instance;
  function ShaderId$COMPOSITOR_getInstance() {
    ShaderId_initFields();
    return ShaderId$COMPOSITOR_instance;
  }
  function ShaderId$ShaderId$SPARKLE_init$lambda(reader) {
    return SparkleShader$Companion_getInstance().parse_100t80$(reader);
  }
  var ShaderId$SPARKLE_instance;
  function ShaderId$SPARKLE_getInstance() {
    ShaderId_initFields();
    return ShaderId$SPARKLE_instance;
  }
  function ShaderId$ShaderId$SIMPLE_SPATIAL_init$lambda(reader) {
    return SimpleSpatialShader$Companion_getInstance().parse_100t80$(reader);
  }
  var ShaderId$SIMPLE_SPATIAL_instance;
  function ShaderId$SIMPLE_SPATIAL_getInstance() {
    ShaderId_initFields();
    return ShaderId$SIMPLE_SPATIAL_instance;
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
    return [ShaderId$SOLID_getInstance(), ShaderId$PIXEL_getInstance(), ShaderId$SINE_WAVE_getInstance(), ShaderId$COMPOSITOR_getInstance(), ShaderId$SPARKLE_getInstance(), ShaderId$SIMPLE_SPATIAL_getInstance()];
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
    return shaderType.parser(reader);
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
  Pixels.$metadata$ = {
    kind: Kind_INTERFACE,
    simpleName: 'Pixels',
    interfaces: []
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
          if (!equals(element_0, panel))
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
          var match = Regex_init('^G_([^_]+).*?$').matchEntire_6bul2c$(name);
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
    Shenzarpy$WheelColor$RED_instance = new Shenzarpy$WheelColor('RED', 0, Color$Companion_getInstance().fromInts(12721698));
    Shenzarpy$WheelColor$ORANGE_instance = new Shenzarpy$WheelColor('ORANGE', 1, Color$Companion_getInstance().fromInts(15434294));
    Shenzarpy$WheelColor$AQUAMARINE_instance = new Shenzarpy$WheelColor('AQUAMARINE', 2, Color$Companion_getInstance().fromInts(8174724));
    Shenzarpy$WheelColor$DEEP_GREEN_instance = new Shenzarpy$WheelColor('DEEP_GREEN', 3, Color$Companion_getInstance().fromInts(1212719));
    Shenzarpy$WheelColor$LIGHT_GREEN_instance = new Shenzarpy$WheelColor('LIGHT_GREEN', 4, Color$Companion_getInstance().fromInts(10469695));
    Shenzarpy$WheelColor$LAVENDER_instance = new Shenzarpy$WheelColor('LAVENDER', 5, Color$Companion_getInstance().fromInts(9401515));
    Shenzarpy$WheelColor$PINK_instance = new Shenzarpy$WheelColor('PINK', 6, Color$Companion_getInstance().fromInts(15434114));
    Shenzarpy$WheelColor$YELLOW_instance = new Shenzarpy$WheelColor('YELLOW', 7, Color$Companion_getInstance().fromInts(16706356));
    Shenzarpy$WheelColor$MAGENTA_instance = new Shenzarpy$WheelColor('MAGENTA', 8, Color$Companion_getInstance().fromInts(14750594));
    Shenzarpy$WheelColor$CYAN_instance = new Shenzarpy$WheelColor('CYAN', 9, Color$Companion_getInstance().fromInts(1812456));
    Shenzarpy$WheelColor$CTO2_instance = new Shenzarpy$WheelColor('CTO2', 10, Color$Companion_getInstance().fromInts(16041553));
    Shenzarpy$WheelColor$CTO1_instance = new Shenzarpy$WheelColor('CTO1', 11, Color$Companion_getInstance().fromInts(16046218));
    Shenzarpy$WheelColor$CTB_instance = new Shenzarpy$WheelColor('CTB', 12, Color$Companion_getInstance().fromInts(9947064));
    Shenzarpy$WheelColor$DARK_BLUE_instance = new Shenzarpy$WheelColor('DARK_BLUE', 13, Color$Companion_getInstance().fromInts(545175));
    Shenzarpy$WheelColor$WHITE_instance = new Shenzarpy$WheelColor('WHITE', 14, Color$Companion_getInstance().fromInts(16777215));
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
  function Show() {
  }
  function Show$MetaData(name) {
    this.name = name;
  }
  Show$MetaData.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'MetaData',
    interfaces: []
  };
  Show.$metadata$ = {
    kind: Kind_INTERFACE,
    simpleName: 'Show',
    interfaces: []
  };
  function ShowRunner(gadgetProvider, brains, beatProvider, dmxUniverse) {
    this.gadgetProvider_0 = gadgetProvider;
    this.beatProvider_0 = beatProvider;
    this.dmxUniverse_0 = dmxUniverse;
    var destination = LinkedHashMap_init();
    var tmp$;
    tmp$ = brains.iterator();
    while (tmp$.hasNext()) {
      var element = tmp$.next();
      var key = element.surface;
      var tmp$_0;
      var value = destination.get_11rb$(key);
      if (value == null) {
        var answer = ArrayList_init();
        destination.put_xwzc9p$(key, answer);
        tmp$_0 = answer;
      }
       else {
        tmp$_0 = value;
      }
      var list = tmp$_0;
      list.add_11rb$(element);
    }
    this.brainsBySurface_0 = destination;
    this.shaderBuffers_0 = HashMap_init();
  }
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
    var buffer = shader.createBuffer_ppt8xj$(surface);
    this.recordShader_0(surface, buffer);
    return buffer;
  };
  ShowRunner.prototype.getCompositorBuffer_cn6wln$ = function (surface, bufferA, bufferB, mode, fade) {
    if (mode === void 0)
      mode = CompositingMode$OVERLAY_getInstance();
    if (fade === void 0)
      fade = 0.5;
    var $receiver = (new CompositorShader(bufferA.shader, bufferB.shader)).createBuffer_ytrflg$(bufferA, bufferB);
    $receiver.mode = mode;
    $receiver.fade = fade;
    this.recordShader_0(surface, $receiver);
    return $receiver;
  };
  ShowRunner.prototype.getDmxBuffer_vux9f0$ = function (baseChannel, channelCount) {
    return this.dmxUniverse_0.writer_vux9f0$(baseChannel, channelCount);
  };
  ShowRunner.prototype.getMovingHead_1hma8m$ = function (movingHead) {
    var baseChannel = ensureNotNull(Config$Companion_getInstance().DMX_DEVICES.get_11rb$(movingHead.name));
    return new Shenzarpy(this.getDmxBuffer_vux9f0$(baseChannel, 16));
  };
  ShowRunner.prototype.send_359yhm$ = function (link, stats) {
    if (stats === void 0)
      stats = null;
    var tmp$;
    tmp$ = this.shaderBuffers_0.entries.iterator();
    while (tmp$.hasNext()) {
      var element = tmp$.next();
      var surface = element.key;
      var shaderBuffers = element.value;
      if (shaderBuffers.size !== 1) {
        throw IllegalStateException_init('Too many shader buffers for ' + surface + ': ' + shaderBuffers);
      }
      var shaderBuffer = first(shaderBuffers);
      var remoteBrains = this.brainsBySurface_0.get_11rb$(surface);
      var tmp$_0 = remoteBrains != null;
      if (tmp$_0) {
        tmp$_0 = !remoteBrains.isEmpty();
      }
      if (tmp$_0) {
        var messageBytes = (new BrainShaderMessage(shaderBuffer.shader, shaderBuffer)).toBytes();
        var tmp$_1;
        tmp$_1 = remoteBrains.iterator();
        while (tmp$_1.hasNext()) {
          var element_0 = tmp$_1.next();
          link.sendUdp_ytpeqp$(element_0.address, 8003, messageBytes);
        }
        if (stats != null) {
          stats.bytesSent = stats.bytesSent + messageBytes.length | 0;
          stats.packetsSent = stats.packetsSent + 1 | 0;
        }
      }
    }
    this.dmxUniverse_0.sendFrame();
  };
  ShowRunner.prototype.getGadget_87gk9q$ = function (gadget) {
    return this.gadgetProvider_0.getGadget_87gk9q$(gadget);
  };
  ShowRunner.prototype.shutDown = function () {
    this.gadgetProvider_0.clear();
  };
  function ShowRunner$Stats(bytesSent, packetsSent) {
    if (bytesSent === void 0)
      bytesSent = 0;
    if (packetsSent === void 0)
      packetsSent = 0;
    this.bytesSent = bytesSent;
    this.packetsSent = packetsSent;
  }
  ShowRunner$Stats.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Stats',
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
    this.color_u6ly2p$_0 = this.watchForChanges_mh5how$(this.initialValue);
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
  ColorPicker.prototype.toJson = function () {
    return new JsonObject(mapOf_0(to('color', JsonPrimitive(this.color.toInt()))));
  };
  ColorPicker.prototype.setFromJson_qiw0cd$ = function (jsonElement) {
    var jsonObject = jsonElement.jsonObject;
    this.color = Color$Companion_getInstance().fromInts(ensureNotNull(jsonObject.get_11rb$('color')).primitive.int);
  };
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
      output.encodeSerializableElement_blecud$(this.descriptor, 1, Color$$serializer_getInstance(), obj.initialValue);
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
          local1 = (bitMask0 & 2) === 0 ? input.decodeSerializableElement_s44l7r$(this.descriptor, 1, Color$$serializer_getInstance()) : input.updateSerializableElement_ehubvl$(this.descriptor, 1, Color$$serializer_getInstance(), local1);
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
    return [internal.StringSerializer, Color$$serializer_getInstance()];
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
    $this.color_u6ly2p$_0 = $this.watchForChanges_mh5how$($this.initialValue);
    return $this;
  }
  ColorPicker.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'ColorPicker',
    interfaces: [Gadget]
  };
  function Slider(name, initialValue) {
    Slider$Companion_getInstance();
    if (initialValue === void 0)
      initialValue = 1.0;
    Gadget.call(this);
    this.name = name;
    this.initialValue = initialValue;
    this.value_2xmiz9$_0 = this.watchForChanges_mh5how$(this.initialValue);
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
  Slider.prototype.toJson = function () {
    return new JsonObject(mapOf_0(to('value', JsonPrimitive(this.value))));
  };
  Slider.prototype.setFromJson_qiw0cd$ = function (jsonElement) {
    var jsonObject = jsonElement.jsonObject;
    this.value = ensureNotNull(jsonObject.get_11rb$('value')).primitive.float;
  };
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
    $this.value_2xmiz9$_0 = $this.watchForChanges_mh5how$($this.initialValue);
    return $this;
  }
  Slider.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Slider',
    interfaces: [Gadget]
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
  function FragmentingUdpLink(link) {
    this.link_0 = link;
    this.mtu_0 = this.link_0.udpMtu;
    this.headerSize_0 = 12;
    this.nextMessageId_0 = 0;
    this.fragments_0 = ArrayList_init();
  }
  Object.defineProperty(FragmentingUdpLink.prototype, 'myAddress', {
    get: function () {
      return this.link_0.myAddress;
    }
  });
  Object.defineProperty(FragmentingUdpLink.prototype, 'udpMtu', {
    get: function () {
      return this.link_0.udpMtu;
    }
  });
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
    this.link_0.listenUdp_a6m852$(port, new FragmentingUdpLink$listenUdp$ObjectLiteral(udpListener, this));
  };
  function FragmentingUdpLink$sendUdp$lambda(this$FragmentingUdpLink, closure$toAddress, closure$port) {
    return function (fragment) {
      this$FragmentingUdpLink.link_0.sendUdp_ytpeqp$(closure$toAddress, closure$port, fragment);
      return Unit;
    };
  }
  FragmentingUdpLink.prototype.sendUdp_ytpeqp$ = function (toAddress, port, bytes) {
    this.transmitMultipartUdp_0(bytes, FragmentingUdpLink$sendUdp$lambda(this, toAddress, port));
  };
  function FragmentingUdpLink$broadcastUdp$lambda(this$FragmentingUdpLink, closure$port) {
    return function (fragment) {
      this$FragmentingUdpLink.link_0.broadcastUdp_3fbn1q$(closure$port, fragment);
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
    var messageCount = ((bytes.length - 1 | 0) / (this.mtu_0 - this.headerSize_0 | 0) | 0) + 1 | 0;
    var buf = new Int8Array(this.mtu_0);
    var offset = 0;
    for (var i = 0; i < messageCount; i++) {
      var writer = new ByteArrayWriter(buf);
      var a = this.mtu_0 - this.headerSize_0 | 0;
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
    this.link_0.listenTcp_kd29r4$(port, tcpServerSocketListener);
  };
  FragmentingUdpLink.prototype.connectTcp_dy234z$ = function (toAddress, port, tcpListener) {
    return this.link_0.connectTcp_dy234z$(toAddress, port, tcpListener);
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
        tmp$ = BrainMapping$Companion_getInstance().parse_100t80$(reader);
        break;
      case 'PINKY_PONG':
        tmp$ = PinkyPongMessage$Companion_getInstance().parse_100t80$(reader);
        break;
      default:tmp$ = Kotlin.noWhenBranchMatched();
        break;
    }
    return tmp$;
  }
  function BrainHelloMessage(brainId, panelName) {
    BrainHelloMessage$Companion_getInstance();
    Message.call(this, Type$BRAIN_HELLO_getInstance());
    this.brainId = brainId;
    this.panelName = panelName;
  }
  function BrainHelloMessage$Companion() {
    BrainHelloMessage$Companion_instance = this;
  }
  BrainHelloMessage$Companion.prototype.parse_100t80$ = function (reader) {
    return new BrainHelloMessage(reader.readString(), reader.readString());
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
    writer.writeString_61zpoe$(this.panelName);
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
  function BrainMapping(brainId, surfaceName, pixelCount, pixelVertices) {
    BrainMapping$Companion_getInstance();
    Message.call(this, Type$BRAIN_MAPPING_getInstance());
    this.brainId = brainId;
    this.surfaceName = surfaceName;
    this.pixelCount = pixelCount;
    this.pixelVertices = pixelVertices;
  }
  function BrainMapping$Companion() {
    BrainMapping$Companion_instance = this;
  }
  BrainMapping$Companion.prototype.readListOfVertices_v0p5xb$ = function ($receiver) {
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
  BrainMapping$Companion.prototype.parse_100t80$ = function (reader) {
    return new BrainMapping(reader.readString(), reader.readNullableString(), reader.readInt(), this.readListOfVertices_v0p5xb$(reader));
  };
  BrainMapping$Companion.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'Companion',
    interfaces: []
  };
  var BrainMapping$Companion_instance = null;
  function BrainMapping$Companion_getInstance() {
    if (BrainMapping$Companion_instance === null) {
      new BrainMapping$Companion();
    }
    return BrainMapping$Companion_instance;
  }
  BrainMapping.prototype.serialize_3kjoo0$ = function (writer) {
    writer.writeString_61zpoe$(this.brainId);
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
  BrainMapping.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'BrainMapping',
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
  CompositorShader.prototype.createRenderer_onphn6$ = function (surface, pixels) {
    return new CompositorShader$Renderer(surface, pixels, this.aShader, this.bShader);
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
    interfaces: []
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
      mode = CompositingMode$OVERLAY_getInstance();
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
  function CompositorShader$Renderer(surface, pixels, aShader, bShader) {
    this.pixels = pixels;
    var array = Array_0(this.pixels.count);
    var tmp$;
    tmp$ = array.length - 1 | 0;
    for (var i = 0; i <= tmp$; i++) {
      array[i] = Color$Companion_getInstance().WHITE;
    }
    this.colors_0 = array;
    this.aPixels_0 = new CompositorShader$Renderer$PixelBuf(this.pixels.count);
    this.bPixels_0 = new CompositorShader$Renderer$PixelBuf(this.pixels.count);
    this.rendererA_0 = aShader.createRenderer_onphn6$(surface, this.aPixels_0);
    this.rendererB_0 = bShader.createRenderer_onphn6$(surface, this.bPixels_0);
  }
  CompositorShader$Renderer.prototype.draw_433sc5$ = function (buffer) {
    var tmp$, tmp$_0, tmp$_1;
    this.rendererA_0.draw_433sc5$(Kotlin.isType(tmp$ = buffer.bufferA, Shader$Buffer) ? tmp$ : throwCCE());
    this.rendererB_0.draw_433sc5$(Kotlin.isType(tmp$_0 = buffer.bufferB, Shader$Buffer) ? tmp$_0 : throwCCE());
    var mode = buffer.mode;
    tmp$_1 = this.colors_0;
    for (var i = 0; i !== tmp$_1.length; ++i) {
      var aColor = this.aPixels_0.colors[i];
      var bColor = this.bPixels_0.colors[i];
      this.colors_0[i] = aColor.fade_6zkv30$(mode.composite_dggbqs$(aColor, bColor), buffer.fade);
    }
    this.pixels.set_tmuqsv$(this.colors_0);
  };
  function CompositorShader$Renderer$PixelBuf(count) {
    this.count_e7ehet$_0 = count;
    var array = Array_0(this.count);
    var tmp$;
    tmp$ = array.length - 1 | 0;
    for (var i = 0; i <= tmp$; i++) {
      array[i] = Color$Companion_getInstance().WHITE;
    }
    this.colors = array;
  }
  Object.defineProperty(CompositorShader$Renderer$PixelBuf.prototype, 'count', {
    get: function () {
      return this.count_e7ehet$_0;
    }
  });
  CompositorShader$Renderer$PixelBuf.prototype.set_tmuqsv$ = function (colors) {
    arrayCopy(colors, this.colors, 0, 0, colors.length);
  };
  CompositorShader$Renderer$PixelBuf.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'PixelBuf',
    interfaces: [Pixels]
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
    new CompositingMode$OVERLAY();
    new CompositingMode$ADD();
    CompositingMode$Companion_getInstance();
  }
  function CompositingMode$OVERLAY() {
    CompositingMode$OVERLAY_instance = this;
    CompositingMode.call(this, 'OVERLAY', 0);
  }
  CompositingMode$OVERLAY.prototype.composite_dggbqs$ = function (src, dest) {
    return src;
  };
  CompositingMode$OVERLAY.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'OVERLAY',
    interfaces: [CompositingMode]
  };
  var CompositingMode$OVERLAY_instance = null;
  function CompositingMode$OVERLAY_getInstance() {
    CompositingMode_initFields();
    return CompositingMode$OVERLAY_instance;
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
    return [CompositingMode$OVERLAY_getInstance(), CompositingMode$ADD_getInstance()];
  }
  CompositingMode.values = CompositingMode$values;
  function CompositingMode$valueOf(name) {
    switch (name) {
      case 'OVERLAY':
        return CompositingMode$OVERLAY_getInstance();
      case 'ADD':
        return CompositingMode$ADD_getInstance();
      default:throwISE('No enum constant baaahs.shaders.CompositingMode.' + name);
    }
  }
  CompositingMode.valueOf_61zpoe$ = CompositingMode$valueOf;
  function PixelShader() {
    PixelShader$Companion_getInstance();
    Shader.call(this, ShaderId$PIXEL_getInstance());
  }
  PixelShader.prototype.createBuffer_ppt8xj$ = function (surface) {
    return new PixelShader$Buffer(this, surface.pixelCount);
  };
  PixelShader.prototype.createRenderer_onphn6$ = function (surface, pixels) {
    return new PixelShader$Renderer(pixels);
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
    interfaces: []
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
  function PixelShader$Renderer(pixels) {
    this.pixels = pixels;
  }
  PixelShader$Renderer.prototype.draw_433sc5$ = function (buffer) {
    this.pixels.set_tmuqsv$(buffer.colors);
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
  SimpleSpatialShader.prototype.createRenderer_onphn6$ = function (surface, pixels) {
    return new SimpleSpatialShader$Renderer(surface, pixels);
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
    interfaces: []
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
  function SimpleSpatialShader$Renderer(surface, pixels) {
    this.surface_0 = surface;
    this.pixels_0 = pixels;
    var array = Array_0(this.pixels_0.count);
    var tmp$;
    tmp$ = array.length - 1 | 0;
    for (var i = 0; i <= tmp$; i++) {
      array[i] = Color$Companion_getInstance().WHITE;
    }
    this.colors_0 = array;
    var tmp$_0, tmp$_1;
    this.pixelVertices_0 = (tmp$_1 = Kotlin.isType(tmp$_0 = this.surface_0, Brain$MappedSurface) ? tmp$_0 : null) != null ? tmp$_1.pixelVertices : null;
  }
  SimpleSpatialShader$Renderer.prototype.draw_433sc5$ = function (buffer) {
    var tmp$, tmp$_0;
    if (this.pixelVertices_0 == null)
      return;
    var a = this.colors_0.length;
    var b = this.pixelVertices_0.size;
    tmp$ = Math_0.min(a, b);
    for (var i = 0; i < tmp$; i++) {
      var tmp$_1 = this.pixelVertices_0.get_za3lpa$(i);
      var pixX = tmp$_1.component1()
      , pixY = tmp$_1.component2();
      var distX = pixX - buffer.centerX;
      var distY = pixY - buffer.centerY;
      var x = distX * distX + distY * distY;
      var dist = Math_0.sqrt(x);
      if (dist < buffer.radius - 0.025) {
        tmp$_0 = buffer.color;
      }
       else if (dist < buffer.radius + 0.025) {
        tmp$_0 = Color$Companion_getInstance().BLACK;
      }
       else {
        tmp$_0 = buffer.color.fade_6zkv30$(Color$Companion_getInstance().BLACK, dist * 2);
      }
      this.colors_0[i] = tmp$_0;
    }
    this.pixels_0.set_tmuqsv$(this.colors_0);
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
  SineWaveShader.prototype.createRenderer_onphn6$ = function (surface, pixels) {
    return new SineWaveShader$Renderer(pixels);
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
    interfaces: []
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
  function SineWaveShader$Renderer(pixels) {
    this.pixels = pixels;
    var array = Array_0(this.pixels.count);
    var tmp$;
    tmp$ = array.length - 1 | 0;
    for (var i = 0; i <= tmp$; i++) {
      array[i] = Color$Companion_getInstance().WHITE;
    }
    this.colors_0 = array;
  }
  SineWaveShader$Renderer.prototype.draw_433sc5$ = function (buffer) {
    var tmp$;
    var theta = buffer.theta;
    var pixelCount = this.pixels.count;
    var density = buffer.density;
    tmp$ = this.colors_0;
    for (var i = 0; i !== tmp$.length; ++i) {
      var x = theta + 2 * math.PI * (i / pixelCount * density);
      var v = Math_0.sin(x) / 2 + 0.5;
      this.colors_0[i] = Color$Companion_getInstance().BLACK.fade_6zkv30$(buffer.color, v);
    }
    this.pixels.set_tmuqsv$(this.colors_0);
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
  SolidShader.prototype.createRenderer_onphn6$ = function (surface, pixels) {
    return new SolidShader$Renderer(pixels);
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
    interfaces: []
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
  function SolidShader$Renderer(pixels) {
    this.pixels = pixels;
    var array = Array_0(this.pixels.count);
    var tmp$;
    tmp$ = array.length - 1 | 0;
    for (var i = 0; i <= tmp$; i++) {
      array[i] = Color$Companion_getInstance().WHITE;
    }
    this.colors_0 = array;
  }
  SolidShader$Renderer.prototype.draw_433sc5$ = function (buffer) {
    var tmp$;
    tmp$ = this.colors_0;
    for (var i = 0; i !== tmp$.length; ++i) {
      this.colors_0[i] = buffer.color;
    }
    this.pixels.set_tmuqsv$(this.colors_0);
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
  SparkleShader.prototype.createRenderer_onphn6$ = function (surface, pixels) {
    return new SparkleShader$Renderer(pixels);
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
    interfaces: []
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
  function SparkleShader$Renderer(pixels) {
    this.pixels = pixels;
    var array = Array_0(this.pixels.count);
    var tmp$;
    tmp$ = array.length - 1 | 0;
    for (var i = 0; i <= tmp$; i++) {
      array[i] = Color$Companion_getInstance().WHITE;
    }
    this.colors_0 = array;
  }
  SparkleShader$Renderer.prototype.draw_433sc5$ = function (buffer) {
    var tmp$;
    tmp$ = this.colors_0;
    for (var i = 0; i !== tmp$.length; ++i) {
      var tmp$_0;
      if (Random.Default.nextFloat() < buffer.sparkliness) {
        tmp$_0 = buffer.color;
      }
       else {
        tmp$_0 = Color$Companion_getInstance().BLACK;
      }
      this.colors_0[i] = tmp$_0;
    }
    this.pixels.set_tmuqsv$(this.colors_0);
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
    this.allShows = listOf([SolidColorShow_getInstance(), SomeDumbShow_getInstance(), RandomShow_getInstance(), CompositeShow_getInstance(), ThumpShow_getInstance(), PanelTweenShow_getInstance(), PixelTweenShow_getInstance(), LifeyShow_getInstance(), SimpleSpatialShow_getInstance()]);
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
    Show$MetaData.call(this, 'Composite');
  }
  function CompositeShow$createShow$ObjectLiteral(closure$showRunner, closure$sheepModel) {
    this.colorPicker = closure$showRunner.getGadget_87gk9q$(new ColorPicker('Color'));
    this.solidShader = new SolidShader();
    this.sineWaveShader = new SineWaveShader();
    var $receiver = closure$sheepModel.allPanels;
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
      var compositorShaderBuffer = closure$showRunner.getCompositorBuffer_cn6wln$(item, solidShaderBuffer, sineWaveShaderBuffer, CompositingMode$ADD_getInstance());
      tmp$_0.call(destination, new CompositeShow$ShaderBufs(solidShaderBuffer, sineWaveShaderBuffer, compositorShaderBuffer));
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
  CompositeShow$createShow$ObjectLiteral.prototype.nextFrame = function () {
    var theta = getTimeMillis().modulo(Kotlin.Long.fromInt(10000)).toNumber() / 1000.0 % (2 * math.PI);
    var i = {v: 0};
    var tmp$;
    tmp$ = this.shaderBufs_0.iterator();
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
  CompositeShow$createShow$ObjectLiteral.$metadata$ = {
    kind: Kind_CLASS,
    interfaces: [Show]
  };
  CompositeShow.prototype.createShow_h1b9op$ = function (sheepModel, showRunner) {
    return new CompositeShow$createShow$ObjectLiteral(showRunner, sheepModel);
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
    interfaces: [Show$MetaData]
  };
  var CompositeShow_instance = null;
  function CompositeShow_getInstance() {
    if (CompositeShow_instance === null) {
      new CompositeShow();
    }
    return CompositeShow_instance;
  }
  function LifeyShow() {
    LifeyShow_instance = this;
    Show$MetaData.call(this, 'Lifey');
  }
  function LifeyShow$createShow$neighbors(closure$sheepModel) {
    return function ($receiver) {
      return closure$sheepModel.neighborsOf_jfju1k$($receiver);
    };
  }
  function LifeyShow$createShow$isSelected(closure$selectedPanels) {
    return function ($receiver) {
      return closure$selectedPanels.contains_11rb$($receiver);
    };
  }
  function LifeyShow$createShow$neighborsSelected(closure$neighbors, closure$selectedPanels) {
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
  function LifeyShow$createShow$ObjectLiteral(closure$speedSlider, closure$lastUpdateMs, closure$selectedPanels, closure$sheepModel, closure$isSelected, closure$neighborsSelected, closure$neighbors, closure$shaderBuffers) {
    this.closure$speedSlider = closure$speedSlider;
    this.closure$lastUpdateMs = closure$lastUpdateMs;
    this.closure$selectedPanels = closure$selectedPanels;
    this.closure$sheepModel = closure$sheepModel;
    this.closure$isSelected = closure$isSelected;
    this.closure$neighborsSelected = closure$neighborsSelected;
    this.closure$neighbors = closure$neighbors;
    this.closure$shaderBuffers = closure$shaderBuffers;
  }
  LifeyShow$createShow$ObjectLiteral.prototype.nextFrame = function () {
    var nowMs = getTimeMillis();
    var intervalMs = Kotlin.Long.fromNumber(this.closure$speedSlider.value * 1000);
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
  LifeyShow$createShow$ObjectLiteral.$metadata$ = {
    kind: Kind_CLASS,
    interfaces: [Show]
  };
  LifeyShow.prototype.createShow_h1b9op$ = function (sheepModel, showRunner) {
    var speedSlider = showRunner.getGadget_87gk9q$(new Slider('Speed', 0.25));
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
    var neighbors = LifeyShow$createShow$neighbors(sheepModel);
    var isSelected = LifeyShow$createShow$isSelected(selectedPanels);
    var neighborsSelected = LifeyShow$createShow$neighborsSelected(neighbors, selectedPanels);
    return new LifeyShow$createShow$ObjectLiteral(speedSlider, lastUpdateMs, selectedPanels, sheepModel, isSelected, neighborsSelected, neighbors, shaderBuffers);
  };
  LifeyShow.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'LifeyShow',
    interfaces: [Show$MetaData]
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
    Show$MetaData.call(this, 'PanelTweenShow');
  }
  function PanelTweenShow$createShow$ObjectLiteral(closure$colorArray, closure$showRunner, closure$sheepModel) {
    this.closure$colorArray = closure$colorArray;
    this.slider = closure$showRunner.getGadget_87gk9q$(new Slider('Sparkliness', 0.0));
    this.solidShader = new SolidShader();
    this.sparkleShader = new SparkleShader();
    var $receiver = closure$sheepModel.allPanels;
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
  PanelTweenShow$createShow$ObjectLiteral.prototype.nextFrame = function () {
    var $receiver = this.shaderBuffers;
    this.closure$colorArray;
    var tmp$, tmp$_0;
    var index = 0;
    tmp$ = $receiver.iterator();
    while (tmp$.hasNext()) {
      var item = tmp$.next();
      var closure$colorArray = this.closure$colorArray;
      var number = checkIndexOverflow((tmp$_0 = index, index = tmp$_0 + 1 | 0, tmp$_0));
      var now = getTimeMillis().and(L268435455).toInt();
      var colorIndex = ((now / this.fadeTimeMs | 0) + number | 0) % closure$colorArray.length;
      var startColor = closure$colorArray[colorIndex];
      var endColor = closure$colorArray[(colorIndex + 1 | 0) % closure$colorArray.length];
      var tweenedColor = startColor.fade_6zkv30$(endColor, now % this.fadeTimeMs / this.fadeTimeMs);
      item.solidShader.color = tweenedColor;
      item.sparkleShader.color = Color$Companion_getInstance().WHITE;
      item.sparkleShader.sparkliness = this.slider.value;
    }
  };
  PanelTweenShow$createShow$ObjectLiteral.$metadata$ = {
    kind: Kind_CLASS,
    interfaces: [Show]
  };
  PanelTweenShow.prototype.createShow_h1b9op$ = function (sheepModel, showRunner) {
    var colorArray = [Color$Companion_getInstance().fromString('#FF8A47'), Color$Companion_getInstance().fromString('#FC6170'), Color$Companion_getInstance().fromString('#8CEEEE'), Color$Companion_getInstance().fromString('#26BFBF'), Color$Companion_getInstance().fromString('#FFD747')];
    return new PanelTweenShow$createShow$ObjectLiteral(colorArray, showRunner, sheepModel);
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
    interfaces: [Show$MetaData]
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
    Show$MetaData.call(this, 'PixelTweenShow');
  }
  function PixelTweenShow$createShow$ObjectLiteral(closure$colorArray, closure$sheepModel, closure$showRunner) {
    this.closure$colorArray = closure$colorArray;
    var $receiver = closure$sheepModel.allPanels;
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
  PixelTweenShow$createShow$ObjectLiteral.prototype.nextFrame = function () {
    var $receiver = this.shaderBuffers;
    this.closure$colorArray;
    var tmp$, tmp$_0;
    var index = 0;
    tmp$ = $receiver.iterator();
    while (tmp$.hasNext()) {
      var item = tmp$.next();
      var closure$colorArray = this.closure$colorArray;
      var i = checkIndexOverflow((tmp$_0 = index, index = tmp$_0 + 1 | 0, tmp$_0));
      var now = getTimeMillis().and(L268435455).toInt();
      var colorIndex = ((now / this.fadeTimeMs | 0) + i | 0) % closure$colorArray.length;
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
  PixelTweenShow$createShow$ObjectLiteral.$metadata$ = {
    kind: Kind_CLASS,
    interfaces: [Show]
  };
  PixelTweenShow.prototype.createShow_h1b9op$ = function (sheepModel, showRunner) {
    var colorArray = [Color$Companion_getInstance().fromString('#FF8A47'), Color$Companion_getInstance().fromString('#FC6170'), Color$Companion_getInstance().fromString('#8CEEEE'), Color$Companion_getInstance().fromString('#26BFBF'), Color$Companion_getInstance().fromString('#FFD747')];
    return new PixelTweenShow$createShow$ObjectLiteral(colorArray, sheepModel, showRunner);
  };
  PixelTweenShow.prototype.get_number_y56fi1$ = function ($receiver) {
    var tmp$, tmp$_0, tmp$_1;
    return (tmp$_1 = (tmp$_0 = (tmp$ = Regex_init('\\d+').find_905azu$($receiver.name)) != null ? tmp$.value : null) != null ? toInt_0(tmp$_0) : null) != null ? tmp$_1 : -1;
  };
  PixelTweenShow.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'PixelTweenShow',
    interfaces: [Show$MetaData]
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
    Show$MetaData.call(this, 'Random');
  }
  function RandomShow$createShow$ObjectLiteral(closure$sheepModel, closure$showRunner) {
    var $receiver = closure$sheepModel.allPanels;
    var destination = ArrayList_init_0(collectionSizeOrDefault($receiver, 10));
    var tmp$;
    tmp$ = $receiver.iterator();
    while (tmp$.hasNext()) {
      var item = tmp$.next();
      destination.add_11rb$(closure$showRunner.getShaderBuffer_9rhubp$(item, new PixelShader()));
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
    this.movingHeadBuffers = destination_0;
  }
  RandomShow$createShow$ObjectLiteral.prototype.nextFrame = function () {
    var tmp$;
    tmp$ = this.pixelShaderBuffers.iterator();
    while (tmp$.hasNext()) {
      var element = tmp$.next();
      var $receiver = element.colors;
      var tmp$_0, tmp$_0_0;
      var index = 0;
      for (tmp$_0 = 0; tmp$_0 !== $receiver.length; ++tmp$_0) {
        var item = $receiver[tmp$_0];
        element.colors[tmp$_0_0 = index, index = tmp$_0_0 + 1 | 0, tmp$_0_0] = Color$Companion_getInstance().random();
      }
    }
    var tmp$_1;
    tmp$_1 = this.movingHeadBuffers.iterator();
    while (tmp$_1.hasNext()) {
      var element_0 = tmp$_1.next();
      element_0.colorWheel = element_0.closestColorFor_rny0jj$(Color$Companion_getInstance().random());
      element_0.pan = Random.Default.nextFloat() * Shenzarpy$Companion_getInstance().panRange.endInclusive;
      element_0.tilt = Random.Default.nextFloat() * Shenzarpy$Companion_getInstance().tiltRange.endInclusive;
    }
  };
  RandomShow$createShow$ObjectLiteral.$metadata$ = {
    kind: Kind_CLASS,
    interfaces: [Show]
  };
  RandomShow.prototype.createShow_h1b9op$ = function (sheepModel, showRunner) {
    return new RandomShow$createShow$ObjectLiteral(sheepModel, showRunner);
  };
  RandomShow.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'RandomShow',
    interfaces: [Show$MetaData]
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
    Show$MetaData.call(this, 'Spatial');
  }
  function SimpleSpatialShow$createShow$ObjectLiteral(closure$shaderBuffers, closure$colorPicker, closure$centerXSlider, closure$centerYSlider, closure$radiusSlider) {
    this.closure$shaderBuffers = closure$shaderBuffers;
    this.closure$colorPicker = closure$colorPicker;
    this.closure$centerXSlider = closure$centerXSlider;
    this.closure$centerYSlider = closure$centerYSlider;
    this.closure$radiusSlider = closure$radiusSlider;
  }
  SimpleSpatialShow$createShow$ObjectLiteral.prototype.nextFrame = function () {
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
  SimpleSpatialShow$createShow$ObjectLiteral.$metadata$ = {
    kind: Kind_CLASS,
    interfaces: [Show]
  };
  SimpleSpatialShow.prototype.createShow_h1b9op$ = function (sheepModel, showRunner) {
    var colorPicker = showRunner.getGadget_87gk9q$(new ColorPicker('Color'));
    var centerXSlider = showRunner.getGadget_87gk9q$(new Slider('center X', 0.5));
    var centerYSlider = showRunner.getGadget_87gk9q$(new Slider('center Y', 0.5));
    var radiusSlider = showRunner.getGadget_87gk9q$(new Slider('radius', 0.25));
    var shader = new SimpleSpatialShader();
    var $receiver = sheepModel.allPanels;
    var destination = ArrayList_init_0(collectionSizeOrDefault($receiver, 10));
    var tmp$;
    tmp$ = $receiver.iterator();
    while (tmp$.hasNext()) {
      var item = tmp$.next();
      destination.add_11rb$(showRunner.getShaderBuffer_9rhubp$(item, shader));
    }
    var shaderBuffers = destination;
    return new SimpleSpatialShow$createShow$ObjectLiteral(shaderBuffers, colorPicker, centerXSlider, centerYSlider, radiusSlider);
  };
  SimpleSpatialShow.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'SimpleSpatialShow',
    interfaces: [Show$MetaData]
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
    Show$MetaData.call(this, 'Solid Color');
  }
  function SolidColorShow$createShow$ObjectLiteral(closure$colorPicker, closure$shaderBuffers) {
    this.closure$colorPicker = closure$colorPicker;
    this.closure$shaderBuffers = closure$shaderBuffers;
    this.priorColor = closure$colorPicker.color;
  }
  SolidColorShow$createShow$ObjectLiteral.prototype.nextFrame = function () {
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
  SolidColorShow$createShow$ObjectLiteral.$metadata$ = {
    kind: Kind_CLASS,
    interfaces: [Show]
  };
  SolidColorShow.prototype.createShow_h1b9op$ = function (sheepModel, showRunner) {
    var colorPicker = showRunner.getGadget_87gk9q$(new ColorPicker('Color'));
    var shader = new SolidShader();
    var $receiver = sheepModel.allPanels;
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
    return new SolidColorShow$createShow$ObjectLiteral(colorPicker, shaderBuffers);
  };
  SolidColorShow.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'SolidColorShow',
    interfaces: [Show$MetaData]
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
    Show$MetaData.call(this, 'SomeDumbShow');
  }
  function SomeDumbShow$createShow$ObjectLiteral(closure$showRunner, closure$sheepModel) {
    this.colorPicker = closure$showRunner.getGadget_87gk9q$(new ColorPicker('Color'));
    this.pixelShader = new PixelShader();
    var $receiver = closure$sheepModel.allPanels;
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
  function SomeDumbShow$createShow$ObjectLiteral$nextFrame$nextTimeShiftedFloat(closure$now) {
    return function ($receiver) {
      var x = $receiver.nextFloat() + closure$now.toNumber() / 1000.0;
      return Math_0.sin(x);
    };
  }
  function SomeDumbShow$createShow$ObjectLiteral$nextFrame$desaturateRandomishly($receiver, baseSaturation, seed) {
    var x = seed.nextFloat();
    return $receiver.withSaturation_mx4ult$(baseSaturation * Math_0.abs(x));
  }
  SomeDumbShow$createShow$ObjectLiteral.prototype.nextFrame = function () {
    var seed = Random_0(0);
    var now = getTimeMillis();
    var nextTimeShiftedFloat = SomeDumbShow$createShow$ObjectLiteral$nextFrame$nextTimeShiftedFloat(now);
    var desaturateRandomishly = SomeDumbShow$createShow$ObjectLiteral$nextFrame$desaturateRandomishly;
    var tmp$;
    tmp$ = this.pixelShaderBuffers.iterator();
    while (tmp$.hasNext()) {
      var element = tmp$.next();
      var baseSaturation = seed.nextFloat();
      var panelColor = nextTimeShiftedFloat(seed) < 0.1 ? Color$Companion_getInstance().random() : this.colorPicker.color;
      var $receiver = element.colors;
      var tmp$_0, tmp$_0_0;
      var index = 0;
      for (tmp$_0 = 0; tmp$_0 !== $receiver.length; ++tmp$_0) {
        var item = $receiver[tmp$_0];
        element.colors[tmp$_0_0 = index, index = tmp$_0_0 + 1 | 0, tmp$_0_0] = desaturateRandomishly(panelColor, baseSaturation, seed);
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
  SomeDumbShow$createShow$ObjectLiteral.$metadata$ = {
    kind: Kind_CLASS,
    interfaces: [Show]
  };
  SomeDumbShow.prototype.createShow_h1b9op$ = function (sheepModel, showRunner) {
    return new SomeDumbShow$createShow$ObjectLiteral(showRunner, sheepModel);
  };
  SomeDumbShow.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'SomeDumbShow',
    interfaces: [Show$MetaData]
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
    Show$MetaData.call(this, 'Thump');
  }
  function ThumpShow$createShow$ObjectLiteral(closure$showRunner, closure$sheepModel) {
    this.beatProvider_0 = closure$showRunner.getBeatProvider();
    this.colorPicker = closure$showRunner.getGadget_87gk9q$(new ColorPicker('Color'));
    this.solidShader = new SolidShader();
    this.sineWaveShader = new SineWaveShader();
    var $receiver = closure$sheepModel.allPanels;
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
  ThumpShow$createShow$ObjectLiteral.prototype.nextFrame = function () {
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
  ThumpShow$createShow$ObjectLiteral.$metadata$ = {
    kind: Kind_CLASS,
    interfaces: [Show]
  };
  ThumpShow.prototype.createShow_h1b9op$ = function (sheepModel, showRunner) {
    return new ThumpShow$createShow$ObjectLiteral(showRunner, sheepModel);
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
    interfaces: [Show$MetaData]
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
  logger$Companion.prototype.warn_61zpoe$ = function (message) {
    println('WARN: ' + message);
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
    return new JsBrainDisplay(ensureNotNull(document.getElementById('brainsView')));
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
  JsPinkyDisplay.prototype.listShows_qxmw8h$ = function (showMetas) {
    clear(this.showListInput_0);
    this.showList_0 = showMetas;
    var tmp$;
    tmp$ = showMetas.iterator();
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
  function JsPinkyDisplay$ShowButton(showMeta, element) {
    Button.call(this, showMeta, element);
  }
  JsPinkyDisplay$ShowButton.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'ShowButton',
    interfaces: [Button]
  };
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
    appendText($receiver, 'Show: ');
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
  function JsBrainDisplay(element) {
    this.myDiv_0 = appendElement(element, 'div', JsBrainDisplay$myDiv$lambda);
  }
  JsBrainDisplay.prototype.haveLink_9m0ekx$ = function (link) {
    this.clearClasses_0();
    this.myDiv_0.classList.add('brain-link');
  };
  JsBrainDisplay.prototype.clearClasses_0 = function () {
    clear_0(this.myDiv_0.classList);
  };
  function JsBrainDisplay$myDiv$lambda($receiver) {
    addClass($receiver, ['brain-offline']);
    return Unit;
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
  function Launcher$add$lambda$lambda(this$Launcher, closure$name, closure$onLaunch) {
    return function (it) {
      return this$Launcher.fakeDomContainer_0.createFrame_56nt9y$(closure$name, closure$onLaunch());
    };
  }
  function Launcher$add$lambda(closure$name, this$Launcher, closure$onLaunch) {
    return function ($receiver) {
      var tmp$;
      console.log('launcher for ' + closure$name, $receiver);
      appendText($receiver, closure$name);
      (Kotlin.isType(tmp$ = $receiver, HTMLElement) ? tmp$ : throwCCE()).onclick = Launcher$add$lambda$lambda(this$Launcher, closure$name, closure$onLaunch);
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
    this.showMetas_0 = AllShows$Companion_getInstance().allShows;
    this.visualizer_0 = new Visualizer(this.sheepModel_0);
    this.pinky_0 = new Pinky(this.sheepModel_0, this.showMetas_0, this.network_0, this.dmxUniverse_0, this.display_0.forPinky());
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
  function Coroutine$SheepSimulator$start$lambda$lambda_0(this$SheepSimulator_0, $receiver_0, controller, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.$controller = controller;
    this.exceptionState_0 = 1;
    this.local$this$SheepSimulator = this$SheepSimulator_0;
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
  function SheepSimulator$start$lambda$lambda_2(this$SheepSimulator_0) {
    return function ($receiver_0, continuation_0, suspended) {
      var instance = new Coroutine$SheepSimulator$start$lambda$lambda_0(this$SheepSimulator_0, $receiver_0, this, continuation_0);
      if (suspended)
        return instance;
      else
        return instance.doResume(null);
    };
  }
  function Coroutine$SheepSimulator$start$lambda$lambda_1(continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.exceptionState_0 = 1;
  }
  Coroutine$SheepSimulator$start$lambda$lambda_1.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$SheepSimulator$start$lambda$lambda_1.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$SheepSimulator$start$lambda$lambda_1.prototype.constructor = Coroutine$SheepSimulator$start$lambda$lambda_1;
  Coroutine$SheepSimulator$start$lambda$lambda_1.prototype.doResume = function () {
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
  function SheepSimulator$start$lambda$lambda_3(continuation_0, suspended) {
    var instance = new Coroutine$SheepSimulator$start$lambda$lambda_1(continuation_0);
    if (suspended)
      return instance;
    else
      return instance.doResume(null);
  }
  function Coroutine$SheepSimulator$start$lambda(this$SheepSimulator_0, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.exceptionState_0 = 1;
    this.local$this$SheepSimulator = this$SheepSimulator_0;
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
            var tmp$, tmp$_0;
            var index = 0;
            tmp$ = sortedWith(this.local$this$SheepSimulator.sheepModel_0.panels, new Comparator$ObjectLiteral_0(compareBy$lambda_0(getPropertyCallableRef('name', 1, function ($receiver) {
              return $receiver.name;
            })))).iterator();
            while (tmp$.hasNext()) {
              var item = tmp$.next();
              var this$SheepSimulator = this.local$this$SheepSimulator;
              var index_0 = checkIndexOverflow((tmp$_0 = index, index = tmp$_0 + 1 | 0, tmp$_0));
              var tmp$_1;
              var jsPanel = this$SheepSimulator.visualizer_0.addPanel_jfju1k$(item);
              var pixelLocations = ensureNotNull(jsPanel.getPixelLocations());
              this$SheepSimulator.pinky_0.providePixelMapping_td2c2y$(item, pixelLocations);
              var brain = new Brain('brain//' + index_0, this$SheepSimulator.network_0, this$SheepSimulator.display_0.forBrain(), (tmp$_1 = jsPanel.vizPixels) != null ? tmp$_1 : SheepSimulator$NullPixels_getInstance());
              this$SheepSimulator.pinky_0.providePanelMapping_jm2l9z$(brain.id, item);
              launch(this$SheepSimulator.brainScope_0, void 0, void 0, SheepSimulator$start$lambda$lambda$lambda_0(brain));
            }

            launch(this.local$this$SheepSimulator.pinkyScope_0, void 0, void 0, SheepSimulator$start$lambda$lambda_2(this.local$this$SheepSimulator));
            var tmp$_2;
            tmp$_2 = this.local$this$SheepSimulator.sheepModel_0.eyes.iterator();
            while (tmp$_2.hasNext()) {
              var element = tmp$_2.next();
              var this$SheepSimulator_0 = this.local$this$SheepSimulator;
              this$SheepSimulator_0.visualizer_0.addMovingHead_nmqlne$(element, this$SheepSimulator_0.dmxUniverse_0);
            }

            return doRunBlocking(SheepSimulator$start$lambda$lambda_3), Unit;
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
    this.count_dbt3w$_0 = 0;
  }
  Object.defineProperty(SheepSimulator$NullPixels.prototype, 'count', {
    get: function () {
      return this.count_dbt3w$_0;
    }
  });
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
  function Button(data, element) {
    this.data = data;
    this.element = element;
    this.allButtons_pdnxqa$_0 = this.allButtons_pdnxqa$_0;
    this.onSelect = null;
    this.element.addEventListener('click', Button_init$lambda(this));
  }
  Object.defineProperty(Button.prototype, 'allButtons', {
    get: function () {
      if (this.allButtons_pdnxqa$_0 == null)
        return throwUPAE('allButtons');
      return this.allButtons_pdnxqa$_0;
    },
    set: function (allButtons) {
      this.allButtons_pdnxqa$_0 = allButtons;
    }
  });
  Button.prototype.setSelected_6taknv$ = function (isSelected) {
    this.element.classList.toggle('selected', isSelected);
  };
  Button.prototype.onClick = function () {
    var tmp$;
    this.setSelected_6taknv$(true);
    var tmp$_0;
    tmp$_0 = this.allButtons.iterator();
    while (tmp$_0.hasNext()) {
      var element = tmp$_0.next();
      element.setSelected_6taknv$(false);
    }
    (tmp$ = this.onSelect) != null ? tmp$(this.data) : null;
  };
  function Button_init$lambda(this$Button) {
    return function (it) {
      this$Button.onClick();
      return Unit;
    };
  }
  Button.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Button',
    interfaces: []
  };
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
  function SwirlyPixelArranger(pixelDensity, pixelSpacing) {
    if (pixelDensity === void 0)
      pixelDensity = 0.2;
    if (pixelSpacing === void 0)
      pixelSpacing = 2;
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
    this.renderPixels_0 = true;
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
    var tmp$, tmp$_0;
    this.select_0 = Kotlin.isType(tmp$ = ensureNotNull(document.getElementById('panelSelect')), HTMLSelectElement) ? tmp$ : throwCCE();
    this.sheepView_0 = Kotlin.isType(tmp$_0 = ensureNotNull(document.getElementById('sheepView')), HTMLDivElement) ? tmp$_0 : throwCCE();
    this.pixelDensity_0 = 0.2;
    this.omitPanels_0 = ['60R', '60L', 'Face', 'Tail'];
    this.totalPixels = 0;
    this.select_0.onchange = Visualizer_init$lambda(this);
    this.sheepView_0.addEventListener('mousemove', Visualizer_init$lambda_0(this), false);
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
    this.startRender();
    var resizeTaskId = {v: null};
    window.addEventListener('resize', Visualizer_init$lambda_1(resizeTaskId, this));
    this.REFRESH_DELAY = 50;
    this.resizeDelay = 100;
  }
  Object.defineProperty(Visualizer.prototype, 'rotate', {
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
        this.rotate = false;
      }
    }
  });
  Visualizer.prototype.addFrameListener_imgev1$ = function (frameListener) {
    this.frameListeners_0.add_11rb$(frameListener);
  };
  Visualizer.prototype.removeFrameListener_imgev1$ = function (frameListener) {
    this.frameListeners_0.remove_11rb$(frameListener);
  };
  Visualizer.prototype.onSelectChange_9ojx7i$ = function (event) {
  };
  Visualizer.prototype.onMouseMove_tfvzir$ = function (event) {
    event.preventDefault();
    this.mouse_0.x = event.clientX / this.sheepView_0.offsetWidth * 2 - 1;
    this.mouse_0.y = -(event.clientY / this.sheepView_0.offsetHeight) * 2 + 1;
  };
  Visualizer.prototype.selectPanel_7it1pd$ = function (panel, isSelected) {
  };
  Visualizer.prototype.addPanel_jfju1k$ = function (p) {
    var vizPanel = new VizPanel(p, this.geom_0, this.scene_0);
    this.vizPanels_0.add_11rb$(vizPanel);
    if (this.renderPixels_0) {
      var pixelArranger = new SwirlyPixelArranger(this.pixelDensity_0, 2);
      var pixelPositions = pixelArranger.arrangePixels_zdreix$(vizPanel);
      vizPanel.vizPixels = new VizPanel$VizPixels(pixelPositions);
      this.totalPixels = this.totalPixels + pixelPositions.length | 0;
    }
    document.getElementById('visualizerPixelCount').innerText = this.totalPixels.toString();
    this.select_0.options[this.select_0.options.length] = new Option(p.name, (this.vizPanels_0.size - 1 | 0).toString());
    return vizPanel;
  };
  Visualizer.prototype.addMovingHead_nmqlne$ = function (movingHead, dmxUniverse) {
    return new Visualizer$VizMovingHead(this, movingHead, dmxUniverse);
  };
  function Visualizer$VizMovingHead($outer, movingHead, dmxUniverse) {
    this.$outer = $outer;
    this.baseChannel = ensureNotNull(Config$Companion_getInstance().DMX_DEVICES.get_11rb$(movingHead.name));
    this.device = new Shenzarpy(dmxUniverse.reader_sxjeop$(this.baseChannel, 16, Visualizer$VizMovingHead$device$lambda(this)));
    this.geometry = new ConeBufferGeometry(50, 1000);
    var $receiver = new MeshBasicMaterial();
    $receiver.color.set(16776960);
    this.material = $receiver;
    this.cone = new Mesh_init(this.geometry, this.material);
    this.geometry.applyMatrix((new Matrix4_init()).makeTranslation(0.0, -500.0, 0.0));
    this.material.transparent = true;
    this.material.opacity = 0.75;
    this.cone.position.set(movingHead.origin.x, movingHead.origin.y, movingHead.origin.z);
    this.cone.rotation.x = -math.PI / 2;
    this.$outer.scene_0.add(this.cone);
  }
  Visualizer$VizMovingHead.prototype.receivedDmxFrame_0 = function () {
    var colorWheelV = this.device.colorWheel;
    var wheelColor = Shenzarpy$WheelColor$Companion_getInstance().get_s8j3t7$(colorWheelV);
    this.material.color.set(wheelColor.color.rgb);
    this.material.visible = this.device.dimmer > 0.1;
    this.cone.rotation.x = -math.PI / 2 + this.device.tilt;
    this.cone.rotation.z = this.device.pan;
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
  Visualizer.prototype.startRender = function () {
    this.geom_0.computeBoundingSphere();
    var $receiver = new Points();
    $receiver.geometry = this.geom_0;
    $receiver.material = this.pointMaterial_0;
    this.obj_0 = $receiver;
    this.scene_0.add(this.obj_0);
    var target = this.geom_0.boundingSphere.center.clone();
    this.controls_0.target = target;
    this.camera_0.lookAt(target);
    this.render_14dthe$(0.0);
  };
  function Visualizer$render$lambda(this$Visualizer) {
    return function () {
      window.requestAnimationFrame(getCallableRef('render', function ($receiver, timestamp) {
        return $receiver.render_14dthe$(timestamp), Unit;
      }.bind(null, this$Visualizer)));
    };
  }
  Visualizer.prototype.render_14dthe$ = function (timestamp) {
    var tmp$;
    window.setTimeout(Visualizer$render$lambda(this), this.REFRESH_DELAY);
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
  Visualizer.prototype.doResize_9ojx7i$ = function (evt) {
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
      this$Visualizer.onSelectChange_9ojx7i$(event);
      return Unit;
    };
  }
  function Visualizer_init$lambda_0(this$Visualizer) {
    return function (event) {
      var tmp$, tmp$_0;
      tmp$_0 = Kotlin.isType(tmp$ = event, MouseEvent) ? tmp$ : throwCCE();
      this$Visualizer.onMouseMove_tfvzir$(tmp$_0);
      return Unit;
    };
  }
  function Visualizer_init$lambda$lambda(closure$resizeTaskId, closure$evt, this$Visualizer) {
    return function () {
      closure$resizeTaskId.v = null;
      this$Visualizer.doResize_9ojx7i$(closure$evt);
      return Unit;
    };
  }
  function Visualizer_init$lambda_1(closure$resizeTaskId, this$Visualizer) {
    return function (evt) {
      if (closure$resizeTaskId.v !== null) {
        window.clearTimeout(ensureNotNull(closure$resizeTaskId.v));
      }
      closure$resizeTaskId.v = window.setTimeout(Visualizer_init$lambda$lambda(closure$resizeTaskId, evt, this$Visualizer), this$Visualizer.resizeDelay);
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
    this.count_nf4s6i$_0 = positions.length;
    this.points_0 = null;
    this.pixGeometry_0 = new BufferGeometry();
    this.colorsBufferAttr_0 = null;
    var positionsArray = new Float32Array(this.count * 3 | 0);
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
    this.colorsBufferAttr_0 = new Float32BufferAttribute(new Float32Array(this.count * 3 | 0), 3);
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
  Object.defineProperty(VizPanel$VizPixels.prototype, 'count', {
    get: function () {
      return this.count_nf4s6i$_0;
    }
  });
  VizPanel$VizPixels.prototype.addToScene_smv6vb$ = function (scene) {
    scene.add(this.points_0);
  };
  VizPanel$VizPixels.prototype.removeFromScene_smv6vb$ = function (scene) {
    scene.remove(this.points_0);
  };
  VizPanel$VizPixels.prototype.set_tmuqsv$ = function (colors) {
    var a = this.count;
    var maxCount = Math_0.min(a, colors.length);
    var rgbBuf = this.colorsBufferAttr_0.array;
    for (var i = 0; i < maxCount; i++) {
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
    var size = minus(boundingBox.max, boundingBox.min);
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
  Object.defineProperty(Color, '$serializer', {
    get: Color$$serializer_getInstance
  });
  package$baaahs.Color_init_7b5o5w$ = Color_init_1;
  package$baaahs.Color_init_tjonv8$ = Color_init_2;
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
  package$baaahs.GadgetListener = GadgetListener;
  package$baaahs.GadgetValueObserver = GadgetValueObserver;
  Object.defineProperty(GadgetData, 'Companion', {
    get: GadgetData$Companion_getInstance
  });
  Object.defineProperty(GadgetData, '$serializer', {
    get: GadgetData$$serializer_getInstance
  });
  package$baaahs.GadgetData = GadgetData;
  package$baaahs.GadgetDisplay = GadgetDisplay;
  Object.defineProperty(package$baaahs, 'gadgetModule', {
    get: function () {
      return gadgetModule;
    }
  });
  GadgetProvider.GadgetChannel = GadgetProvider$GadgetChannel;
  package$baaahs.GadgetProvider = GadgetProvider;
  Mapper.BrainMapper = Mapper$BrainMapper;
  package$baaahs.Mapper = Mapper;
  MapperDisplay.Listener = MapperDisplay$Listener;
  package$baaahs.MapperDisplay = MapperDisplay;
  MediaDevices.Camera = MediaDevices$Camera;
  MediaDevices.Region = MediaDevices$Region;
  package$baaahs.MediaDevices = MediaDevices;
  Pinky.BeatProvider = Pinky$BeatProvider;
  Pinky.PinkyBeatProvider = Pinky$PinkyBeatProvider;
  package$baaahs.Pinky = Pinky;
  package$baaahs.RemoteBrain = RemoteBrain;
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
  Object.defineProperty(ShaderId, 'Companion', {
    get: ShaderId$Companion_getInstance
  });
  package$baaahs.ShaderId = ShaderId;
  package$baaahs.Surface = Surface;
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
  Show.MetaData = Show$MetaData;
  package$baaahs.Show = Show;
  ShowRunner.Stats = ShowRunner$Stats;
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
  Object.defineProperty(BrainMapping, 'Companion', {
    get: BrainMapping$Companion_getInstance
  });
  package$proto.BrainMapping = BrainMapping;
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
  CompositorShader$Renderer.PixelBuf = CompositorShader$Renderer$PixelBuf;
  CompositorShader.Renderer = CompositorShader$Renderer;
  var package$shaders = package$baaahs.shaders || (package$baaahs.shaders = {});
  package$shaders.CompositorShader = CompositorShader;
  Object.defineProperty(CompositingMode, 'OVERLAY', {
    get: CompositingMode$OVERLAY_getInstance
  });
  Object.defineProperty(CompositingMode, 'ADD', {
    get: CompositingMode$ADD_getInstance
  });
  Object.defineProperty(CompositingMode, 'Companion', {
    get: CompositingMode$Companion_getInstance
  });
  package$shaders.CompositingMode = CompositingMode;
  Object.defineProperty(PixelShader, 'Companion', {
    get: PixelShader$Companion_getInstance
  });
  PixelShader.Buffer = PixelShader$Buffer;
  PixelShader.Renderer = PixelShader$Renderer;
  package$shaders.PixelShader = PixelShader;
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
  package$baaahs.Button = Button;
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
  Color$$serializer.prototype.patch_mynpiu$ = GeneratedSerializer.prototype.patch_mynpiu$;
  GadgetData$$serializer.prototype.patch_mynpiu$ = GeneratedSerializer.prototype.patch_mynpiu$;
  ColorPicker$$serializer.prototype.patch_mynpiu$ = GeneratedSerializer.prototype.patch_mynpiu$;
  Slider$$serializer.prototype.patch_mynpiu$ = GeneratedSerializer.prototype.patch_mynpiu$;
  FakeNetwork$FakeTcpConnection.prototype.send_chrig3$ = Network$TcpConnection.prototype.send_chrig3$;
  FakeNetwork$FakeLink.prototype.sendUdp_wpmaqi$ = Network$Link.prototype.sendUdp_wpmaqi$;
  FakeNetwork$FakeLink.prototype.broadcastUdp_68hu5j$ = Network$Link.prototype.broadcastUdp_68hu5j$;
  BrowserNetwork$link$ObjectLiteral$connectTcp$ObjectLiteral.prototype.send_chrig3$ = Network$TcpConnection.prototype.send_chrig3$;
  BrowserNetwork$link$ObjectLiteral.prototype.sendUdp_wpmaqi$ = Network$Link.prototype.sendUdp_wpmaqi$;
  BrowserNetwork$link$ObjectLiteral.prototype.broadcastUdp_68hu5j$ = Network$Link.prototype.broadcastUdp_68hu5j$;
  gadgetModule = SerializersModule(gadgetModule$lambda);
  Kotlin.defineModule('sparklemotion', _);
  return _;
}(typeof sparklemotion === 'undefined' ? {} : sparklemotion, kotlin, this['kotlinx-coroutines-core'], this['kotlinx-serialization-runtime-js'], this['threejs-wrapper'], this['kotlinx-html-js']);

//# sourceMappingURL=sparklemotion.js.map
