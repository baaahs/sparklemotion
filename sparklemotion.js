if (typeof kotlin === 'undefined') {
  throw new Error("Error loading module 'sparklemotion'. Its dependency 'kotlin' was not found. Please, check whether 'kotlin' is loaded prior to 'sparklemotion'.");
}
if (typeof this['kotlinx-coroutines-core'] === 'undefined') {
  throw new Error("Error loading module 'sparklemotion'. Its dependency 'kotlinx-coroutines-core' was not found. Please, check whether 'kotlinx-coroutines-core' is loaded prior to 'sparklemotion'.");
}
if (typeof this['kotlinx-serialization-runtime-js'] === 'undefined') {
  throw new Error("Error loading module 'sparklemotion'. Its dependency 'kotlinx-serialization-runtime-js' was not found. Please, check whether 'kotlinx-serialization-runtime-js' is loaded prior to 'sparklemotion'.");
}
if (typeof this['kotlinx-html-js'] === 'undefined') {
  throw new Error("Error loading module 'sparklemotion'. Its dependency 'kotlinx-html-js' was not found. Please, check whether 'kotlinx-html-js' is loaded prior to 'sparklemotion'.");
}
var sparklemotion = function (_, Kotlin, $module$kotlinx_coroutines_core, $module$kotlinx_serialization_runtime_js, $module$kotlinx_html_js) {
  'use strict';
  var $$importsForInline$$ = _.$$importsForInline$$ || (_.$$importsForInline$$ = {});
  var throwUPAE = Kotlin.throwUPAE;
  var COROUTINE_SUSPENDED = Kotlin.kotlin.coroutines.intrinsics.COROUTINE_SUSPENDED;
  var CoroutineImpl = Kotlin.kotlin.coroutines.CoroutineImpl;
  var L60000 = Kotlin.Long.fromInt(60000);
  var delay = $module$kotlinx_coroutines_core.kotlinx.coroutines.delay_s8cxhz$;
  var Kind_CLASS = Kotlin.Kind.CLASS;
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
  var ArrayList_init = Kotlin.kotlin.collections.ArrayList_init_287e2$;
  var arrayCopy = Kotlin.kotlin.collections.arrayCopy;
  var coroutines = $module$kotlinx_coroutines_core.kotlinx.coroutines;
  var CoroutineScope = $module$kotlinx_coroutines_core.kotlinx.coroutines.CoroutineScope_1fupul$;
  var Unit = Kotlin.kotlin.Unit;
  var launch = $module$kotlinx_coroutines_core.kotlinx.coroutines.launch_s496o7$;
  var L1000 = Kotlin.Long.fromInt(1000);
  var L250 = Kotlin.Long.fromInt(250);
  var L10000 = Kotlin.Long.fromInt(10000);
  var println = Kotlin.kotlin.io.println_s8jyv4$;
  var L34 = Kotlin.Long.fromInt(34);
  var getCallableRef = Kotlin.getCallableRef;
  var UByteArray_init = Kotlin.kotlin.UByteArray_init_za3lpa$;
  var IntRange = Kotlin.kotlin.ranges.IntRange;
  var LinkedHashMap_init = Kotlin.kotlin.collections.LinkedHashMap_init_q3lmfv$;
  var Job = $module$kotlinx_coroutines_core.kotlinx.coroutines.Job;
  var IllegalStateException_init = Kotlin.kotlin.IllegalStateException_init_pdl1vj$;
  var toByte = Kotlin.toByte;
  var UByte_init = Kotlin.kotlin.UByte;
  var ensureNotNull = Kotlin.ensureNotNull;
  var coroutines_0 = Kotlin.kotlin.coroutines;
  var CoroutineScope_0 = $module$kotlinx_coroutines_core.kotlinx.coroutines.CoroutineScope;
  var HashMap_init = Kotlin.kotlin.collections.HashMap_init_q3lmfv$;
  var toList = Kotlin.kotlin.collections.toList_7wnvza$;
  var equals = Kotlin.equals;
  var L50 = Kotlin.Long.fromInt(50);
  var L0 = Kotlin.Long.ZERO;
  var toMutableList = Kotlin.kotlin.collections.toMutableList_4c7yge$;
  var Enum = Kotlin.kotlin.Enum;
  var throwISE = Kotlin.throwISE;
  var kotlin_js_internal_StringCompanionObject = Kotlin.kotlin.js.internal.StringCompanionObject;
  var serializer = $module$kotlinx_serialization_runtime_js.kotlinx.serialization.serializer_6eet4j$;
  var toString_0 = Kotlin.toString;
  var Json = $module$kotlinx_serialization_runtime_js.kotlinx.serialization.json.Json;
  var NotImplementedError_init = Kotlin.kotlin.NotImplementedError;
  var Regex_init = Kotlin.kotlin.text.Regex_init_61zpoe$;
  var split = Kotlin.kotlin.text.split_ip8yn$;
  var joinToString = Kotlin.kotlin.collections.joinToString_fmv235$;
  var toInt_0 = Kotlin.kotlin.text.toInt_pdl1vz$;
  var arrayListOf = Kotlin.kotlin.collections.arrayListOf_i5x0yv$;
  var throwCCE = Kotlin.throwCCE;
  var trim = Kotlin.kotlin.text.trim_gw00vp$;
  var toDouble = Kotlin.kotlin.text.toDouble_pdl1vz$;
  var collectionSizeOrDefault = Kotlin.kotlin.collections.collectionSizeOrDefault_ba2ldo$;
  var ArrayList_init_0 = Kotlin.kotlin.collections.ArrayList_init_ww73n8$;
  var rangeTo = Kotlin.kotlin.ranges.rangeTo_38ydlf$;
  var toShort = Kotlin.toShort;
  var toBits = Kotlin.floatToBits;
  var get_indices = Kotlin.kotlin.text.get_indices_gw00vp$;
  var copyOf = Kotlin.kotlin.collections.copyOf_mrm5p$;
  var toChar = Kotlin.toChar;
  var toBoxedChar = Kotlin.toBoxedChar;
  var StringBuilder_init = Kotlin.kotlin.text.StringBuilder_init_za3lpa$;
  var unboxChar = Kotlin.unboxChar;
  var copyOfRange = Kotlin.kotlin.collections.copyOfRange_ietg8x$;
  var Array_0 = Array;
  var until = Kotlin.kotlin.ranges.until_dqglrj$;
  var math = Kotlin.kotlin.math;
  var Random_0 = Kotlin.kotlin.random.Random_za3lpa$;
  var clear = Kotlin.kotlin.dom.clear_asww5s$;
  var appendText = Kotlin.kotlin.dom.appendText_46n0ku$;
  var appendElement = Kotlin.kotlin.dom.appendElement_ldvnw0$;
  var listOf = Kotlin.kotlin.collections.listOf_i5x0yv$;
  var addClass = Kotlin.kotlin.dom.addClass_hhb33f$;
  var WebGLRenderer_init = THREE.WebGLRenderer;
  var Geometry = THREE.Geometry;
  var LineBasicMaterial = THREE.LineBasicMaterial;
  var Color_init = THREE.Color;
  var MeshBasicMaterial = THREE.MeshBasicMaterial;
  var Vector3 = THREE.Vector3;
  var Face3_init = THREE.Face3;
  var Mesh_init = THREE.Mesh;
  var BufferGeometry = THREE.BufferGeometry;
  var Line_init = THREE.Line;
  var roundToInt = Kotlin.kotlin.math.roundToInt_yrwdxr$;
  var Scene = THREE.Scene;
  var PerspectiveCamera_init = THREE.PerspectiveCamera;
  var Object3D = THREE.Object3D;
  var get_create = $module$kotlinx_html_js.kotlinx.html.dom.get_create_4wc2mh$;
  var set_onClickFunction = $module$kotlinx_html_js.kotlinx.html.js.set_onClickFunction_pszlq2$;
  var button = $module$kotlinx_html_js.kotlinx.html.button_i4xb7r$;
  var div = $module$kotlinx_html_js.kotlinx.html.div_ri36nr$;
  var canvas = $module$kotlinx_html_js.kotlinx.html.canvas_dwb9fz$;
  var div_0 = $module$kotlinx_html_js.kotlinx.html.div_59el9d$;
  var OrbitControls = THREE.OrbitControls;
  var copyToArray = Kotlin.kotlin.collections.copyToArray;
  var L200000 = Kotlin.Long.fromInt(200000);
  var promise = $module$kotlinx_coroutines_core.kotlinx.coroutines.promise_pda6u4$;
  FakeDmxUniverse.prototype = Object.create(Dmx$Universe.prototype);
  FakeDmxUniverse.prototype.constructor = FakeDmxUniverse;
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
  PinkyPongMessage.prototype = Object.create(Message.prototype);
  PinkyPongMessage.prototype.constructor = PinkyPongMessage;
  PubSub$Connection$receive$ObjectLiteral.prototype = Object.create(PubSub$Listener.prototype);
  PubSub$Connection$receive$ObjectLiteral.prototype.constructor = PubSub$Connection$receive$ObjectLiteral;
  PubSub$Connection.prototype = Object.create(PubSub$Origin.prototype);
  PubSub$Connection.prototype.constructor = PubSub$Connection;
  PubSub$Server$publish$ObjectLiteral.prototype = Object.create(PubSub$Listener.prototype);
  PubSub$Server$publish$ObjectLiteral.prototype.constructor = PubSub$Server$publish$ObjectLiteral;
  PubSub$Client$subscribe$lambda$lambda$ObjectLiteral.prototype = Object.create(PubSub$Listener.prototype);
  PubSub$Client$subscribe$lambda$lambda$ObjectLiteral.prototype.constructor = PubSub$Client$subscribe$lambda$lambda$ObjectLiteral;
  PubSub$Client$subscribe$ObjectLiteral.prototype = Object.create(PubSub$Listener.prototype);
  PubSub$Client$subscribe$ObjectLiteral.prototype.constructor = PubSub$Client$subscribe$ObjectLiteral;
  ShaderType.prototype = Object.create(Enum.prototype);
  ShaderType.prototype.constructor = ShaderType;
  Shenzarpy$WheelColor.prototype = Object.create(Enum.prototype);
  Shenzarpy$WheelColor.prototype.constructor = Shenzarpy$WheelColor;
  Shenzarpy$Channel.prototype = Object.create(Enum.prototype);
  Shenzarpy$Channel.prototype.constructor = Shenzarpy$Channel;
  Shenzarpy.prototype = Object.create(Dmx$DeviceType.prototype);
  Shenzarpy.prototype.constructor = Shenzarpy;
  CompositorShader.prototype = Object.create(Shader.prototype);
  CompositorShader.prototype.constructor = CompositorShader;
  CompositingMode.prototype = Object.create(Enum.prototype);
  CompositingMode.prototype.constructor = CompositingMode;
  PixelShader.prototype = Object.create(Shader.prototype);
  PixelShader.prototype.constructor = PixelShader;
  SineWaveShader.prototype = Object.create(Shader.prototype);
  SineWaveShader.prototype.constructor = SineWaveShader;
  SolidShader.prototype = Object.create(Shader.prototype);
  SolidShader.prototype.constructor = SolidShader;
  CompositeShow$Meta.prototype = Object.create(ShowMeta.prototype);
  CompositeShow$Meta.prototype.constructor = CompositeShow$Meta;
  RandomShow$Meta.prototype = Object.create(ShowMeta.prototype);
  RandomShow$Meta.prototype.constructor = RandomShow$Meta;
  SomeDumbShow$Meta.prototype = Object.create(ShowMeta.prototype);
  SomeDumbShow$Meta.prototype.constructor = SomeDumbShow$Meta;
  JsPinkyDisplay$ShowButton.prototype = Object.create(Button.prototype);
  JsPinkyDisplay$ShowButton.prototype.constructor = JsPinkyDisplay$ShowButton;
  ColorPickerView$ColorButton.prototype = Object.create(Button.prototype);
  ColorPickerView$ColorButton.prototype.constructor = ColorPickerView$ColorButton;
  function Brain(network, display, pixels, illicitPanelHint) {
    this.network_0 = network;
    this.display_0 = display;
    this.pixels_0 = pixels;
    this.illicitPanelHint_0 = illicitPanelHint;
    this.link_q2tdi4$_0 = this.link_q2tdi4$_0;
    this.receivingInstructions_0 = false;
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
            this.$this.link_0 = this.$this.network_0.link();
            this.$this.link_0.listenUdp_ury2hn$(Ports$Companion_getInstance().BRAIN, this.$this);
            this.$this.display_0.haveLink_6qu7we$(this.$this.link_0);
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
            this.state_0 = 2;
            continue;
          case 1:
            throw this.exception_0;
          case 2:
            if (!this.$this.receivingInstructions_0) {
              this.$this.link_0.broadcastUdp_ecsl0t$(Ports$Companion_getInstance().PINKY, new BrainHelloMessage(this.$this.illicitPanelHint_0.name));
            }

            this.state_0 = 3;
            this.result_0 = delay(L60000, this);
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
  Brain.prototype.receive_cm0rz4$ = function (fromAddress, bytes) {
    var message = parse(bytes);
    if (Kotlin.isType(message, BrainShaderMessage)) {
      var shaderImpl = message.shader.createImpl_bbfl1t$(this.pixels_0);
      shaderImpl.draw();
    }
     else if (Kotlin.isType(message, BrainIdRequest))
      this.link_0.sendUdp_bkw8fl$(fromAddress, message.port, new BrainIdResponse(''));
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
  Color.prototype.serialize_ep8mow$ = function (writer) {
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
  Color$Companion.prototype.parse_c4pr8w$ = function (reader) {
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
    var tmp$;
    tmp$ = this.listeners_0.iterator();
    while (tmp$.hasNext()) {
      var element = tmp$.next();
      element();
    }
  };
  FakeDmxUniverse.prototype.allOff = function () {
    for (var i = 0; i <= 512; i++)
      this.channelsIn_0[i] = 0;
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
  function Mapper(network, sheepModel, mapperDisplay, mediaDevices) {
    this.network_0 = network;
    this.sheepModel_0 = sheepModel;
    this.mapperDisplay_0 = mapperDisplay;
    this.maxPixelsPerBrain = 512;
    this.width = 640;
    this.height = 300;
    var $receiver = mediaDevices.getCamera_vux9f0$(this.width, this.height);
    $receiver.onImage = getCallableRef('haveImage', function ($receiver, image) {
      return $receiver.haveImage_0(image), Unit;
    }.bind(null, this));
    this.camera = $receiver;
    this.baseBitmap = UByteArray_init(Kotlin.imul(this.width, this.height) * 4 | 0);
    this.displayBitmap = UByteArray_init(Kotlin.imul(this.width, this.height) * 4 | 0);
    this.closeListeners_0 = ArrayList_init();
    this.link_tktc8n$_0 = this.link_tktc8n$_0;
    this.isRunning_0 = true;
    this.scope = CoroutineScope(coroutines.Dispatchers.Main);
    this.brainMappers_0 = LinkedHashMap_init();
    this.mapperDisplay_0.onClose = Mapper_init$lambda(this);
    this.mapperDisplay_0.addWireframe_9u144y$(this.sheepModel_0);
    this.retries_0 = new IntRange(0, 1);
  }
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
            this.local$this$Mapper.link_0 = this.local$this$Mapper.network_0.link();
            this.local$this$Mapper.link_0.listenUdp_ury2hn$(Ports$Companion_getInstance().MAPPER, this.local$this$Mapper);
            this.local$this$Mapper.scope = CoroutineScope(coroutines.Dispatchers.Main);
            return launch(this.local$this$Mapper.scope, void 0, void 0, Mapper$start$lambda$lambda(this.local$this$Mapper));
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
    return doRunBlocking(Mapper$start$lambda(this));
  };
  Mapper.prototype.onClose_0 = function () {
    this.isRunning_0 = false;
    this.camera.close();
    var $receiver = this.scope;
    var tmp$;
    var tmp$_0;
    if ((tmp$ = $receiver.coroutineContext.get_j3r2sn$(Job.Key)) != null)
      tmp$_0 = tmp$;
    else {
      throw IllegalStateException_init(('Scope cannot be cancelled because it does not have a job: ' + $receiver).toString());
    }
    var job = tmp$_0;
    job.cancel();
    this.link_0.broadcastUdp_ecsl0t$(Ports$Companion_getInstance().PINKY, new MapperHelloMessage(false));
    var tmp$_1;
    tmp$_1 = this.closeListeners_0.iterator();
    while (tmp$_1.hasNext()) {
      var element = tmp$_1.next();
      element();
    }
    this.mapperDisplay_0.close();
  };
  Mapper.prototype.haveImage_0 = function (image) {
    this.mapperDisplay_0.showCamImage_u6jj7u$(image);
    var toBitmap = image.toMonoBitmap();
  };
  function Coroutine$Mapper$run$lambda(this$Mapper_0, $receiver_0, controller, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.$controller = controller;
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
            this.state_0 = 2;
            continue;
          case 1:
            throw this.exception_0;
          case 2:
            if (!this.local$this$Mapper.isRunning_0) {
              this.state_0 = 4;
              continue;
            }

            this.local$this$Mapper.link_0.broadcastUdp_ecsl0t$(Ports$Companion_getInstance().PINKY, new MapperHelloMessage(this.local$this$Mapper.isRunning_0));
            this.state_0 = 3;
            this.result_0 = delay(L10000, this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 3:
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
  function Mapper$run$lambda(this$Mapper_0) {
    return function ($receiver_0, continuation_0, suspended) {
      var instance = new Coroutine$Mapper$run$lambda(this$Mapper_0, $receiver_0, this, continuation_0);
      if (suspended)
        return instance;
      else
        return instance.doResume(null);
    };
  }
  function Mapper$run$lambda$lambda$lambda$lambda(this$Mapper) {
    return function () {
      return this$Mapper.solidColor_0(Color$Companion_getInstance().WHITE);
    };
  }
  function Mapper$run$lambda$lambda$lambda$lambda_0(this$Mapper) {
    return function () {
      return this$Mapper.solidColor_0(Color$Companion_getInstance().BLACK);
    };
  }
  function Coroutine$Mapper$run$lambda_0(this$Mapper_0, $receiver_0, controller, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.$controller = controller;
    this.exceptionState_0 = 1;
    this.local$this$Mapper = this$Mapper_0;
    this.local$tmp$ = void 0;
    this.local$tmp$_0 = void 0;
    this.local$element = void 0;
    this.local$this$Mapper_0 = void 0;
    this.local$pixelShader = void 0;
    this.local$i = void 0;
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
            this.state_0 = 2;
            continue;
          case 1:
            throw this.exception_0;
          case 2:
            if (!this.local$this$Mapper.isRunning_0) {
              this.state_0 = 12;
              continue;
            }

            println('identify brains...');
            this.local$tmp$_0 = this.local$this$Mapper.brainMappers_0.values.iterator();
            this.state_0 = 3;
            continue;
          case 3:
            if (!this.local$tmp$_0.hasNext()) {
              this.state_0 = 5;
              continue;
            }

            this.local$element = this.local$tmp$_0.next();
            this.local$this$Mapper_0 = this.local$this$Mapper;
            var tmp$;
            tmp$ = this.local$this$Mapper_0.retries_0.iterator();
            while (tmp$.hasNext()) {
              var element = tmp$.next();
              this.local$element.shade_rbov5k$(Mapper$run$lambda$lambda$lambda$lambda(this.local$this$Mapper_0));
            }

            this.state_0 = 4;
            this.result_0 = delay(L34, this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 4:
            var tmp$_0;
            tmp$_0 = this.local$this$Mapper_0.retries_0.iterator();
            while (tmp$_0.hasNext()) {
              var element_0 = tmp$_0.next();
              this.local$element.shade_rbov5k$(Mapper$run$lambda$lambda$lambda$lambda_0(this.local$this$Mapper_0));
            }

            this.state_0 = 3;
            continue;
          case 5:
            this.state_0 = 6;
            this.result_0 = delay(L1000, this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 6:
            println('identify pixels...');
            this.local$pixelShader = new PixelShader();
            this.local$pixelShader.buffer.setAll_rny0jj$(Color$Companion_getInstance().BLACK);
            this.local$tmp$ = this.local$this$Mapper.maxPixelsPerBrain;
            this.local$i = 0;
            this.state_0 = 7;
            continue;
          case 7:
            if (this.local$i >= this.local$tmp$) {
              this.state_0 = 10;
              continue;
            }

            if (this.local$i % 128 === 0)
              println('pixel ' + this.local$i + '... isRunning is ' + this.local$this$Mapper.isRunning_0);
            this.local$pixelShader.buffer.colors[this.local$i] = Color$Companion_getInstance().WHITE;
            this.local$this$Mapper.link_0.broadcastUdp_ecsl0t$(Ports$Companion_getInstance().BRAIN, new BrainShaderMessage(this.local$pixelShader));
            this.local$pixelShader.buffer.colors[this.local$i] = Color$Companion_getInstance().BLACK;
            this.state_0 = 8;
            this.result_0 = delay(L34, this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 8:
            this.state_0 = 9;
            continue;
          case 9:
            this.local$i++;
            this.state_0 = 7;
            continue;
          case 10:
            println('done identifying pixels...');
            this.state_0 = 11;
            this.result_0 = delay(L1000, this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 11:
            this.state_0 = 2;
            continue;
          case 12:
            return println('done identifying things... ' + this.local$this$Mapper.isRunning_0), Unit;
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
    return function ($receiver_0, continuation_0, suspended) {
      var instance = new Coroutine$Mapper$run$lambda_0(this$Mapper_0, $receiver_0, this, continuation_0);
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
    this.local$tmp$_1 = void 0;
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
            this.local$tmp$ = this.$this.retries_0.iterator();
            this.state_0 = 2;
            continue;
          case 1:
            throw this.exception_0;
          case 2:
            if (!this.local$tmp$.hasNext()) {
              this.state_0 = 4;
              continue;
            }

            var element = this.local$tmp$.next();
            this.$this.link_0.broadcastUdp_ecsl0t$(Ports$Companion_getInstance().PINKY, new MapperHelloMessage(true));
            this.state_0 = 3;
            this.result_0 = delay(L1000, this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 3:
            this.$this.link_0.broadcastUdp_ecsl0t$(Ports$Companion_getInstance().BRAIN, this.$this.solidColor_0(Color$Companion_getInstance().BLACK));
            this.state_0 = 2;
            continue;
          case 4:
            this.local$tmp$_0 = this.$this.retries_0.iterator();
            this.state_0 = 5;
            continue;
          case 5:
            if (!this.local$tmp$_0.hasNext()) {
              this.state_0 = 7;
              continue;
            }

            var element_0 = this.local$tmp$_0.next();
            this.$this.link_0.broadcastUdp_ecsl0t$(Ports$Companion_getInstance().BRAIN, new BrainIdRequest(Ports$Companion_getInstance().MAPPER));
            this.state_0 = 6;
            this.result_0 = delay(L1000, this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 6:
            this.state_0 = 5;
            continue;
          case 7:
            this.state_0 = 8;
            this.result_0 = delay(L1000, this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 8:
            this.local$tmp$_1 = this.$this.retries_0.iterator();
            this.state_0 = 9;
            continue;
          case 9:
            if (!this.local$tmp$_1.hasNext()) {
              this.state_0 = 11;
              continue;
            }

            var element_1 = this.local$tmp$_1.next();
            this.$this.link_0.broadcastUdp_ecsl0t$(Ports$Companion_getInstance().BRAIN, this.$this.solidColor_0(Color$Companion_getInstance().BLACK));
            this.state_0 = 10;
            this.result_0 = delay(L250, this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 10:
            this.state_0 = 9;
            continue;
          case 11:
            this.state_0 = 12;
            this.result_0 = delay(L250, this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 12:
            launch(this.$this.scope, void 0, void 0, Mapper$run$lambda(this.$this));
            launch(this.$this.scope, void 0, void 0, Mapper$run$lambda_0(this.$this));
            println('Mapper isRunning: ' + this.$this.isRunning_0);
            this.$this.link_0.broadcastUdp_ecsl0t$(Ports$Companion_getInstance().PINKY, new MapperHelloMessage(this.$this.isRunning_0));
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
  Mapper.prototype.solidColor_0 = function (color) {
    var $receiver = new SolidShader();
    $receiver.buffer.color = color;
    return new BrainShaderMessage($receiver);
  };
  function Mapper$receive$lambda(this$Mapper) {
    return function () {
      return this$Mapper.solidColor_0(Color$Companion_getInstance().GREEN);
    };
  }
  Mapper.prototype.receive_cm0rz4$ = function (fromAddress, bytes) {
    var message = parse(bytes);
    if (Kotlin.isType(message, BrainIdResponse)) {
      var $receiver = this.brainMappers_0;
      var tmp$;
      var value = $receiver.get_11rb$(fromAddress);
      if (value == null) {
        var answer = new Mapper$BrainMapper(this, fromAddress);
        $receiver.put_xwzc9p$(fromAddress, answer);
        tmp$ = answer;
      }
       else {
        tmp$ = value;
      }
      var brainMapper = tmp$;
      brainMapper.shade_rbov5k$(Mapper$receive$lambda(this));
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
  Mapper.prototype.addCloseListener_o14v8n$ = function (listener) {
    this.closeListeners_0.add_11rb$(listener);
  };
  function Mapper$BrainMapper($outer, address) {
    this.$outer = $outer;
    this.address_0 = address;
  }
  Mapper$BrainMapper.prototype.shade_rbov5k$ = function (shaderMessage) {
    this.$outer.link_0.sendUdp_bkw8fl$(this.address_0, Ports$Companion_getInstance().BRAIN, shaderMessage());
  };
  Mapper$BrainMapper.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'BrainMapper',
    interfaces: []
  };
  function Mapper_init$lambda(this$Mapper) {
    return function () {
      this$Mapper.onClose_0();
      return Unit;
    };
  }
  Mapper.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Mapper',
    interfaces: [Network$UdpListener]
  };
  function MapperDisplay() {
  }
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
  function MediaDevices$Image() {
  }
  MediaDevices$Image.$metadata$ = {
    kind: Kind_INTERFACE,
    simpleName: 'Image',
    interfaces: []
  };
  function MediaDevices$MonoBitmap(width, height, data) {
    if (data === void 0)
      data = UByteArray_init(Kotlin.imul(width, height));
    this.width = width;
    this.height = height;
    this.data = data;
  }
  MediaDevices$MonoBitmap.prototype.subtract_9x70wt$ = function (other) {
    var tmp$;
    if (this.data.size !== other.data.size)
      throw IllegalStateException_init("Bitmap sizes don't match");
    tmp$ = this.data.size;
    for (var i = 0; i < tmp$; i++) {
      this.data.set_2c6cbe$(i, new UByte_init(toByte((this.data.get_za3lpa$(i).data & 255) - (other.data.get_za3lpa$(i).data & 255) | 0)));
    }
  };
  MediaDevices$MonoBitmap.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'MonoBitmap',
    interfaces: []
  };
  MediaDevices.$metadata$ = {
    kind: Kind_INTERFACE,
    simpleName: 'MediaDevices',
    interfaces: []
  };
  function Network() {
  }
  function Network$Link() {
  }
  Network$Link.prototype.sendUdp_bkw8fl$ = function (toAddress, port, message) {
    this.sendUdp_z62edq$(toAddress, port, message.toBytes());
  };
  Network$Link.prototype.broadcastUdp_ecsl0t$ = function (port, message) {
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
  Network$TcpConnection.prototype.send_kq3aw3$ = function (message) {
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
    var address = new FakeAddress((tmp$ = this.nextAddress_0, this.nextAddress_0 = tmp$ + 1 | 0, tmp$));
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
              return this.local$closure$udpListener.receive_cm0rz4$(this.local$closure$fromAddress, this.local$closure$bytes), Unit;
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
            return this.local$closure$clientListener.reset_t1snl1$(this.local$closure$connection), Unit;
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
            return this.local$closure$clientListener.connected_t1snl1$(this.local$closure$clientSideConnection.v == null ? throwUPAE('clientSideConnection') : this.local$closure$clientSideConnection.v), Unit;
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
            return this.local$closure$serverListener.connected_t1snl1$(this.local$closure$serverSideConnection), Unit;
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
    var serverListener = serverSocketListener.incomingConnection_t1snl1$(serverSideConnection);
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
    this.fromAddress_gytzal$_0 = fromAddress;
    this.toAddress_ytzwi$_0 = toAddress;
    this.port_8i3uvw$_0 = port;
    this.tcpListener_0 = tcpListener;
    this.otherListener_0 = otherListener;
  }
  Object.defineProperty(FakeNetwork$FakeTcpConnection.prototype, 'fromAddress', {
    get: function () {
      return this.fromAddress_gytzal$_0;
    }
  });
  Object.defineProperty(FakeNetwork$FakeTcpConnection.prototype, 'toAddress', {
    get: function () {
      return this.toAddress_ytzwi$_0;
    }
  });
  Object.defineProperty(FakeNetwork$FakeTcpConnection.prototype, 'port', {
    get: function () {
      return this.port_8i3uvw$_0;
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
            return (tmp$ = this.local$this$FakeTcpConnection.tcpListener_0) != null ? (tmp$.receive_rg1vmd$(ensureNotNull(this.local$this$FakeTcpConnection.otherListener_0)(), this.local$closure$bytes), Unit) : null;
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
    this.myAddress_1ma6oo$_0 = myAddress;
  }
  Object.defineProperty(FakeNetwork$FakeLink.prototype, 'myAddress', {
    get: function () {
      return this.myAddress_1ma6oo$_0;
    }
  });
  FakeNetwork$FakeLink.prototype.listenUdp_ury2hn$ = function (port, udpListener) {
    this.$outer.listenUdp_0(this.myAddress, port, udpListener);
  };
  FakeNetwork$FakeLink.prototype.sendUdp_z62edq$ = function (toAddress, port, bytes) {
    this.$outer.sendUdp_0(this.myAddress, toAddress, port, bytes);
  };
  FakeNetwork$FakeLink.prototype.broadcastUdp_3fbn1q$ = function (port, bytes) {
    this.$outer.broadcastUdp_0(this.myAddress, port, bytes);
  };
  FakeNetwork$FakeLink.prototype.listenTcp_5na2rz$ = function (port, tcpServerSocketListener) {
    this.$outer.listenTcp_0(this.myAddress, port, tcpServerSocketListener);
  };
  FakeNetwork$FakeLink.prototype.connectTcp_cb4f41$ = function (toAddress, port, tcpListener) {
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
    interfaces: [CoroutineScope_0]
  };
  FakeNetwork.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'FakeNetwork',
    interfaces: [Network]
  };
  function FakeAddress(id) {
    this.id = id;
  }
  FakeAddress.prototype.toString = function () {
    return 'x' + toString(this.id, 16);
  };
  FakeAddress.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'FakeAddress',
    interfaces: [Network$Address]
  };
  FakeAddress.prototype.component1 = function () {
    return this.id;
  };
  FakeAddress.prototype.copy_za3lpa$ = function (id) {
    return new FakeAddress(id === void 0 ? this.id : id);
  };
  FakeAddress.prototype.hashCode = function () {
    var result = 0;
    result = result * 31 + Kotlin.hashCode(this.id) | 0;
    return result;
  };
  FakeAddress.prototype.equals = function (other) {
    return this === other || (other !== null && (typeof other === 'object' && (Object.getPrototypeOf(this) === Object.getPrototypeOf(other) && Kotlin.equals(this.id, other.id))));
  };
  function Pinky(sheepModel, showMetas, network, dmxUniverse, display) {
    this.sheepModel = sheepModel;
    this.showMetas = showMetas;
    this.network = network;
    this.dmxUniverse = dmxUniverse;
    this.display = display;
    this.link_0 = this.network.link();
    this.brains_0 = LinkedHashMap_init();
    this.beatProvider_0 = new Pinky$BeatProvider(this, 120.0);
    this.mapperIsRunning_0 = false;
    this.brainsChanged_0 = true;
    this.showRunner_0 = new ShowRunner(this.display, toList(this.brains_0.values), this.dmxUniverse);
  }
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
  function Pinky$run$lambda_0(this$Pinky) {
    return function (it) {
      this$Pinky.display.color = it;
      println('display.color = ' + it);
      return Unit;
    };
  }
  function Pinky$run$lambda_1(closure$primaryColorChannel, this$Pinky) {
    return function () {
      closure$primaryColorChannel.onChange(ensureNotNull(this$Pinky.display.color));
      return Unit;
    };
  }
  function Pinky$run$lambda_2(closure$currentShowMeta, this$Pinky) {
    return function () {
      return closure$currentShowMeta.v.createShow_h1b9op$(this$Pinky.sheepModel, this$Pinky.showRunner_0);
    };
  }
  function Coroutine$run_1($this, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.exceptionState_0 = 1;
    this.$this = $this;
    this.local$prevSelectedShow = void 0;
    this.local$currentShowMeta = void 0;
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
            this.$this.link_0.listenUdp_ury2hn$(Ports$Companion_getInstance().PINKY, this.$this);
            this.$this.display.listShows_5ucgt1$(this.$this.showMetas);
            var pubSub = new PubSub$Server(this.$this.link_0, Ports$Companion_getInstance().PINKY_UI_TCP);
            var color = this.$this.display.color;
            if (color != null) {
              var primaryColorChannel = pubSub.publish_oiz02e$(Topics_getInstance().primaryColor, color, Pinky$run$lambda_0(this.$this));
              this.$this.display.onPrimaryColorChange = Pinky$run$lambda_1(primaryColorChannel, this.$this);
            }

            this.$this.showRunner_0 = new ShowRunner(this.$this.display, toList(this.$this.brains_0.values), this.$this.dmxUniverse);
            this.local$prevSelectedShow = this.$this.display.selectedShow;
            this.local$currentShowMeta = {v: this.local$prevSelectedShow != null ? this.local$prevSelectedShow : ensureNotNull(random(this.$this.showMetas))};
            this.local$buildShow = Pinky$run$lambda_2(this.local$currentShowMeta, this.$this);
            this.local$show = this.local$buildShow();
            this.state_0 = 2;
            continue;
          case 1:
            throw this.exception_0;
          case 2:
            if (!this.$this.mapperIsRunning_0) {
              if (this.$this.brainsChanged_0 || !equals(this.$this.display.selectedShow, this.local$currentShowMeta.v)) {
                this.local$currentShowMeta.v = this.local$prevSelectedShow != null ? this.local$prevSelectedShow : ensureNotNull(random(this.$this.showMetas));
                this.$this.showRunner_0 = new ShowRunner(this.$this.display, toList(this.$this.brains_0.values), this.$this.dmxUniverse);
                this.local$show = this.local$buildShow();
                this.$this.brainsChanged_0 = false;
              }
              this.local$show.nextFrame();
              this.$this.showRunner_0.send_6qu7we$(this.$this.link_0);
            }
             else {
              this.$this.disableDmx_0();
            }

            this.state_0 = 3;
            this.result_0 = delay(L50, this);
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
  Pinky.prototype.receive_cm0rz4$ = function (fromAddress, bytes) {
    var message = parse(bytes);
    if (Kotlin.isType(message, BrainHelloMessage))
      this.foundBrain_0(new RemoteBrain(fromAddress, message.panelName));
    else if (Kotlin.isType(message, MapperHelloMessage))
      this.mapperIsRunning_0 = message.isRunning;
  };
  Pinky.prototype.foundBrain_0 = function (remoteBrain) {
    this.brains_0.put_xwzc9p$(remoteBrain.address, remoteBrain);
    this.display.brainCount = this.brains_0.size;
    this.brainsChanged_0 = true;
  };
  function Pinky$BeatProvider($outer, bpm) {
    this.$outer = $outer;
    this.bpm = bpm;
    this.startTimeMillis = L0;
    this.beat = 0;
    this.beatsPerMeasure = 4;
  }
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
            this.$this.startTimeMillis = getTimeMillis();
            this.state_0 = 2;
            continue;
          case 1:
            throw this.exception_0;
          case 2:
            this.$this.$outer.display.beat = this.$this.beat;
            var offsetMillis = getTimeMillis().subtract(this.$this.startTimeMillis);
            var millisPerBeat = Kotlin.Long.fromNumber(1000 / (this.$this.bpm / 60));
            var delayTimeMillis = millisPerBeat.subtract(offsetMillis.modulo(millisPerBeat));
            this.state_0 = 3;
            this.result_0 = delay(delayTimeMillis, this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 3:
            this.$this.beat = (this.$this.beat + 1 | 0) % this.$this.beatsPerMeasure;
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
  Pinky$BeatProvider.prototype.run = function (continuation_0, suspended) {
    var instance = new Coroutine$run_2(this, continuation_0);
    if (suspended)
      return instance;
    else
      return instance.doResume(null);
  };
  Pinky$BeatProvider.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'BeatProvider',
    interfaces: []
  };
  Pinky.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Pinky',
    interfaces: [Network$UdpListener]
  };
  function ShowRunner(pinkyDisplay, brains, dmxUniverse) {
    this.pinkyDisplay_0 = pinkyDisplay;
    this.brains_0 = brains;
    this.dmxUniverse_0 = dmxUniverse;
    this.shaders_0 = HashMap_init();
  }
  ShowRunner.prototype.getColorPicker = function () {
    return new ColorPicker(this.pinkyDisplay_0);
  };
  ShowRunner.prototype.recordShader_0 = function (panel, shader) {
    var tmp$ = this.shaders_0;
    var $receiver = this.brains_0;
    var destination = ArrayList_init();
    var tmp$_0;
    tmp$_0 = $receiver.iterator();
    while (tmp$_0.hasNext()) {
      var element = tmp$_0.next();
      if (equals(element.panelName, panel.name))
        destination.add_11rb$(element);
    }
    var value = toMutableList(destination);
    tmp$.put_xwzc9p$(shader, value);
  };
  ShowRunner.prototype.getSolidShader_jfju1k$ = function (panel) {
    var $receiver = new SolidShader();
    this.recordShader_0(panel, $receiver);
    return $receiver;
  };
  ShowRunner.prototype.getPixelShader_jfju1k$ = function (panel) {
    var $receiver = new PixelShader();
    this.recordShader_0(panel, $receiver);
    return $receiver;
  };
  ShowRunner.prototype.getSineWaveShader_jfju1k$ = function (panel) {
    var $receiver = new SineWaveShader();
    this.recordShader_0(panel, $receiver);
    return $receiver;
  };
  ShowRunner.prototype.getCompositorShader_626mua$ = function (panel, shaderA, shaderB) {
    var shaderABrains = ensureNotNull(this.shaders_0.get_11rb$(shaderA));
    var shaderBBrains = ensureNotNull(this.shaders_0.get_11rb$(shaderB));
    this.shaders_0.remove_11rb$(shaderA);
    this.shaders_0.remove_11rb$(shaderB);
    var $receiver = new CompositorShader(shaderA, shaderB);
    this.recordShader_0(panel, $receiver);
    return $receiver;
  };
  ShowRunner.prototype.getDmxBuffer_vux9f0$ = function (baseChannel, channelCount) {
    return this.dmxUniverse_0.writer_vux9f0$(baseChannel, channelCount);
  };
  ShowRunner.prototype.getMovingHead_1hma8m$ = function (movingHead) {
    var baseChannel = ensureNotNull(Config$Companion_getInstance().DMX_DEVICES.get_11rb$(movingHead.name));
    return new Shenzarpy(this.getDmxBuffer_vux9f0$(baseChannel, 16));
  };
  ShowRunner.prototype.send_6qu7we$ = function (link) {
    var tmp$;
    tmp$ = this.shaders_0.entries.iterator();
    while (tmp$.hasNext()) {
      var element = tmp$.next();
      var shader = element.key;
      var remoteBrains = element.value;
      var tmp$_0;
      tmp$_0 = remoteBrains.iterator();
      while (tmp$_0.hasNext()) {
        var element_0 = tmp$_0.next();
        link.sendUdp_bkw8fl$(element_0.address, Ports$Companion_getInstance().BRAIN, new BrainShaderMessage(shader));
      }
    }
    this.dmxUniverse_0.sendFrame();
  };
  ShowRunner.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'ShowRunner',
    interfaces: []
  };
  function ColorPicker(pinkyDisplay) {
    this.pinkyDisplay_0 = pinkyDisplay;
  }
  Object.defineProperty(ColorPicker.prototype, 'color', {
    get: function () {
      var tmp$;
      return (tmp$ = this.pinkyDisplay_0.color) != null ? tmp$ : Color$Companion_getInstance().WHITE;
    }
  });
  ColorPicker.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'ColorPicker',
    interfaces: []
  };
  function RemoteBrain(address, panelName) {
    this.address = address;
    this.panelName = panelName;
  }
  RemoteBrain.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'RemoteBrain',
    interfaces: []
  };
  function Ports() {
    Ports$Companion_getInstance();
  }
  function Ports$Companion() {
    Ports$Companion_instance = this;
    this.MAPPER = 8001;
    this.PINKY = 8002;
    this.BRAIN = 8003;
    this.PINKY_UI_TCP = 8004;
  }
  Ports$Companion.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'Companion',
    interfaces: []
  };
  var Ports$Companion_instance = null;
  function Ports$Companion_getInstance() {
    if (Ports$Companion_instance === null) {
      new Ports$Companion();
    }
    return Ports$Companion_instance;
  }
  Ports.$metadata$ = {
    kind: Kind_INTERFACE,
    simpleName: 'Ports',
    interfaces: []
  };
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
    Type$PINKY_PONG_instance = new Type('PINKY_PONG', 5);
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
    return [Type$BRAIN_HELLO_getInstance(), Type$BRAIN_PANEL_SHADE_getInstance(), Type$MAPPER_HELLO_getInstance(), Type$BRAIN_ID_REQUEST_getInstance(), Type$BRAIN_ID_RESPONSE_getInstance(), Type$PINKY_PONG_getInstance()];
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
      case 'PINKY_PONG':
        return Type$PINKY_PONG_getInstance();
      default:throwISE('No enum constant baaahs.Type.' + name);
    }
  }
  Type.valueOf_61zpoe$ = Type$valueOf;
  function parse(bytes) {
    var tmp$;
    var reader = new ByteArrayReader(bytes);
    switch (Type$Companion_getInstance().get_s8j3t7$(reader.readByte()).name) {
      case 'BRAIN_HELLO':
        tmp$ = BrainHelloMessage$Companion_getInstance().parse_c4pr8w$(reader);
        break;
      case 'BRAIN_PANEL_SHADE':
        tmp$ = BrainShaderMessage$Companion_getInstance().parse_c4pr8w$(reader);
        break;
      case 'MAPPER_HELLO':
        tmp$ = MapperHelloMessage$Companion_getInstance().parse_c4pr8w$(reader);
        break;
      case 'BRAIN_ID_REQUEST':
        tmp$ = BrainIdRequest$Companion_getInstance().parse_c4pr8w$(reader);
        break;
      case 'BRAIN_ID_RESPONSE':
        tmp$ = BrainIdResponse$Companion_getInstance().parse_c4pr8w$(reader);
        break;
      case 'PINKY_PONG':
        tmp$ = PinkyPongMessage$Companion_getInstance().parse_c4pr8w$(reader);
        break;
      default:tmp$ = Kotlin.noWhenBranchMatched();
        break;
    }
    return tmp$;
  }
  function BrainHelloMessage(panelName) {
    BrainHelloMessage$Companion_getInstance();
    Message.call(this, Type$BRAIN_HELLO_getInstance());
    this.panelName = panelName;
  }
  function BrainHelloMessage$Companion() {
    BrainHelloMessage$Companion_instance = this;
  }
  BrainHelloMessage$Companion.prototype.parse_c4pr8w$ = function (reader) {
    return new BrainHelloMessage(reader.readString());
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
  BrainHelloMessage.prototype.serialize_ep8mow$ = function (writer) {
    writer.writeString_61zpoe$(this.panelName);
  };
  BrainHelloMessage.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'BrainHelloMessage',
    interfaces: [Message]
  };
  function BrainShaderMessage(shader) {
    BrainShaderMessage$Companion_getInstance();
    Message.call(this, Type$BRAIN_PANEL_SHADE_getInstance());
    this.shader = shader;
  }
  function BrainShaderMessage$Companion() {
    BrainShaderMessage$Companion_instance = this;
  }
  BrainShaderMessage$Companion.prototype.parse_c4pr8w$ = function (reader) {
    var shader = Shader$Companion_getInstance().parse_c4pr8w$(reader);
    shader.readBuffer_c4pr8w$(reader);
    return new BrainShaderMessage(shader);
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
  BrainShaderMessage.prototype.serialize_ep8mow$ = function (writer) {
    this.shader.serialize_ep8mow$(writer);
    this.shader.serializeBuffer_ep8mow$(writer);
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
  MapperHelloMessage$Companion.prototype.parse_c4pr8w$ = function (reader) {
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
  MapperHelloMessage.prototype.serialize_ep8mow$ = function (writer) {
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
  BrainIdRequest$Companion.prototype.parse_c4pr8w$ = function (reader) {
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
  BrainIdRequest.prototype.serialize_ep8mow$ = function (writer) {
    writer.writeInt_za3lpa$(this.port);
  };
  BrainIdRequest.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'BrainIdRequest',
    interfaces: [Message]
  };
  function BrainIdResponse(name) {
    BrainIdResponse$Companion_getInstance();
    Message.call(this, Type$BRAIN_ID_RESPONSE_getInstance());
    this.name = name;
  }
  function BrainIdResponse$Companion() {
    BrainIdResponse$Companion_instance = this;
  }
  BrainIdResponse$Companion.prototype.parse_c4pr8w$ = function (reader) {
    return new BrainIdResponse(reader.readString());
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
  BrainIdResponse.prototype.serialize_ep8mow$ = function (writer) {
    writer.writeString_61zpoe$(this.name);
  };
  BrainIdResponse.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'BrainIdResponse',
    interfaces: [Message]
  };
  function PinkyPongMessage(brainIds) {
    PinkyPongMessage$Companion_getInstance();
    Message.call(this, Type$PINKY_PONG_getInstance());
    this.brainIds = brainIds;
  }
  function PinkyPongMessage$Companion() {
    PinkyPongMessage$Companion_instance = this;
  }
  PinkyPongMessage$Companion.prototype.parse_c4pr8w$ = function (reader) {
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
  PinkyPongMessage.prototype.serialize_ep8mow$ = function (writer) {
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
    this.serialize_ep8mow$(writer);
    return writer.toBytes();
  };
  Message.prototype.serialize_ep8mow$ = function (writer) {
  };
  Message.prototype.size = function () {
    return 127;
  };
  Message.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Message',
    interfaces: []
  };
  function PubSub(networkLink) {
    PubSub$Companion_getInstance();
    this.networkLink_0 = networkLink;
  }
  function PubSub$Origin() {
  }
  PubSub$Origin.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Origin',
    interfaces: []
  };
  function PubSub$Observer() {
  }
  PubSub$Observer.$metadata$ = {
    kind: Kind_INTERFACE,
    simpleName: 'Observer',
    interfaces: []
  };
  function PubSub$Companion() {
    PubSub$Companion_instance = this;
  }
  PubSub$Companion.prototype.listen_tx2csw$ = function (networkLink, port) {
    return new PubSub$Server(networkLink, port);
  };
  PubSub$Companion.prototype.connect_mbxyi8$ = function (networkLink, address, port) {
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
  PubSub$Connection.prototype.connected_t1snl1$ = function (tcpConnection) {
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
  PubSub$Connection.prototype.receive_rg1vmd$ = function (tcpConnection, bytes) {
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
    }
  };
  PubSub$Connection.prototype.sendTopicUpdate_puj7f4$ = function (name, data) {
    var tmp$;
    var writer = new ByteArrayWriter();
    println('-> update ' + name + ' ' + data + ' to ' + toString_0((tmp$ = this.connection) != null ? tmp$.toAddress : null));
    writer.writeString_61zpoe$('update');
    writer.writeString_61zpoe$(name);
    writer.writeString_61zpoe$(data);
    this.sendCommand_ma41of$(writer.toBytes());
  };
  PubSub$Connection.prototype.sendTopicSub_61zpoe$ = function (topicName) {
    var writer = new ByteArrayWriter();
    writer.writeString_61zpoe$('sub');
    writer.writeString_61zpoe$(topicName);
    this.sendCommand_ma41of$(writer.toBytes());
  };
  PubSub$Connection.prototype.reset_t1snl1$ = function (tcpConnection) {
    throw new NotImplementedError_init('An operation is not implemented: ' + 'PubSub.Connection.reset not implemented');
  };
  PubSub$Connection.prototype.sendCommand_ma41of$ = function (bytes) {
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
  function PubSub$Server(link, port) {
    this.topics_0 = HashMap_init();
    link.listenTcp_5na2rz$(port, this);
  }
  PubSub$Server.prototype.incomingConnection_t1snl1$ = function (fromAddress) {
    return new PubSub$Connection('server', this.topics_0);
  };
  function PubSub$Server$publish$ObjectLiteral(closure$onUpdate, closure$topic, origin) {
    this.closure$onUpdate = closure$onUpdate;
    this.closure$topic = closure$topic;
    PubSub$Listener.call(this, origin);
  }
  PubSub$Server$publish$ObjectLiteral.prototype.onUpdate_61zpoe$ = function (data) {
    this.closure$onUpdate(Json.Companion.parse_awif5v$(this.closure$topic.serializer, data));
  };
  PubSub$Server$publish$ObjectLiteral.$metadata$ = {
    kind: Kind_CLASS,
    interfaces: [PubSub$Listener]
  };
  function PubSub$Server$publish$ObjectLiteral_0(closure$topicInfo, closure$topic, closure$publisher) {
    this.closure$topicInfo = closure$topicInfo;
    this.closure$topic = closure$topic;
    this.closure$publisher = closure$publisher;
  }
  PubSub$Server$publish$ObjectLiteral_0.prototype.onChange = function (t) {
    this.closure$topicInfo.notify_btyzc5$(Json.Companion.stringify_tf03ej$(this.closure$topic.serializer, t), this.closure$publisher);
  };
  PubSub$Server$publish$ObjectLiteral_0.$metadata$ = {
    kind: Kind_CLASS,
    interfaces: [PubSub$Observer]
  };
  PubSub$Server.prototype.publish_oiz02e$ = function (topic, data, onUpdate) {
    var publisher = new PubSub$Origin();
    var topicName = topic.name;
    var jsonData = Json.Companion.stringify_tf03ej$(topic.serializer, data);
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
    topicInfo.data = jsonData;
    topicInfo.listeners.add_11rb$(new PubSub$Server$publish$ObjectLiteral(onUpdate, topic, publisher));
    topicInfo.notify_btyzc5$(jsonData, publisher);
    return new PubSub$Server$publish$ObjectLiteral_0(topicInfo, topic, publisher);
  };
  PubSub$Server.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Server',
    interfaces: [Network$TcpServerSocketListener]
  };
  function PubSub$Client(link, serverAddress, port) {
    this.topics_0 = HashMap_init();
    this.server_0 = new PubSub$Connection('client at ' + link.myAddress, this.topics_0);
    link.connectTcp_cb4f41$(serverAddress, port, this.server_0);
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
  function PubSub$Client$subscribe$ObjectLiteral(closure$onUpdate, closure$topic, origin) {
    this.closure$onUpdate = closure$onUpdate;
    this.closure$topic = closure$topic;
    PubSub$Listener.call(this, origin);
  }
  PubSub$Client$subscribe$ObjectLiteral.prototype.onUpdate_61zpoe$ = function (data) {
    this.closure$onUpdate(Json.Companion.parse_awif5v$(this.closure$topic.serializer, data));
  };
  PubSub$Client$subscribe$ObjectLiteral.$metadata$ = {
    kind: Kind_CLASS,
    interfaces: [PubSub$Listener]
  };
  function PubSub$Client$subscribe$ObjectLiteral_0(closure$topic, closure$topicInfo, closure$subscriber) {
    this.closure$topic = closure$topic;
    this.closure$topicInfo = closure$topicInfo;
    this.closure$subscriber = closure$subscriber;
  }
  PubSub$Client$subscribe$ObjectLiteral_0.prototype.onChange = function (t) {
    var jsonData = Json.Companion.stringify_tf03ej$(this.closure$topic.serializer, t);
    this.closure$topicInfo.notify_btyzc5$(jsonData, this.closure$subscriber);
  };
  PubSub$Client$subscribe$ObjectLiteral_0.$metadata$ = {
    kind: Kind_CLASS,
    interfaces: [PubSub$Observer]
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
    var listener = new PubSub$Client$subscribe$ObjectLiteral(onUpdate, topic, subscriber);
    topicInfo.listeners.add_11rb$(listener);
    var data = topicInfo.data;
    if (data != null) {
      listener.onUpdate_61zpoe$(data);
    }
    return new PubSub$Client$subscribe$ObjectLiteral_0(topic, topicInfo, subscriber);
  };
  PubSub$Client.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Client',
    interfaces: []
  };
  PubSub.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'PubSub',
    interfaces: []
  };
  function ShaderType(name, ordinal, parser) {
    Enum.call(this);
    this.parser = parser;
    this.name$ = name;
    this.ordinal$ = ordinal;
  }
  function ShaderType_initFields() {
    ShaderType_initFields = function () {
    };
    ShaderType$SOLID_instance = new ShaderType('SOLID', 0, ShaderType$ShaderType$SOLID_init$lambda);
    ShaderType$PIXEL_instance = new ShaderType('PIXEL', 1, ShaderType$ShaderType$PIXEL_init$lambda);
    ShaderType$SINE_WAVE_instance = new ShaderType('SINE_WAVE', 2, ShaderType$ShaderType$SINE_WAVE_init$lambda);
    ShaderType$COMPOSITOR_instance = new ShaderType('COMPOSITOR', 3, ShaderType$ShaderType$COMPOSITOR_init$lambda);
    ShaderType$Companion_getInstance();
  }
  function ShaderType$ShaderType$SOLID_init$lambda(reader) {
    return SolidShader$Companion_getInstance().parse_c4pr8w$(reader);
  }
  var ShaderType$SOLID_instance;
  function ShaderType$SOLID_getInstance() {
    ShaderType_initFields();
    return ShaderType$SOLID_instance;
  }
  function ShaderType$ShaderType$PIXEL_init$lambda(reader) {
    return PixelShader$Companion_getInstance().parse_c4pr8w$(reader);
  }
  var ShaderType$PIXEL_instance;
  function ShaderType$PIXEL_getInstance() {
    ShaderType_initFields();
    return ShaderType$PIXEL_instance;
  }
  function ShaderType$ShaderType$SINE_WAVE_init$lambda(reader) {
    return SineWaveShader$Companion_getInstance().parse_c4pr8w$(reader);
  }
  var ShaderType$SINE_WAVE_instance;
  function ShaderType$SINE_WAVE_getInstance() {
    ShaderType_initFields();
    return ShaderType$SINE_WAVE_instance;
  }
  function ShaderType$ShaderType$COMPOSITOR_init$lambda(reader) {
    return CompositorShader$Companion_getInstance().parse_c4pr8w$(reader);
  }
  var ShaderType$COMPOSITOR_instance;
  function ShaderType$COMPOSITOR_getInstance() {
    ShaderType_initFields();
    return ShaderType$COMPOSITOR_instance;
  }
  function ShaderType$Companion() {
    ShaderType$Companion_instance = this;
    this.values = ShaderType$values();
  }
  ShaderType$Companion.prototype.get_s8j3t7$ = function (i) {
    if (i > this.values.length || i < 0) {
      throw Kotlin.newThrowable('bad index for ShaderType: ' + i);
    }
    return this.values[i];
  };
  ShaderType$Companion.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'Companion',
    interfaces: []
  };
  var ShaderType$Companion_instance = null;
  function ShaderType$Companion_getInstance() {
    ShaderType_initFields();
    if (ShaderType$Companion_instance === null) {
      new ShaderType$Companion();
    }
    return ShaderType$Companion_instance;
  }
  ShaderType.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'ShaderType',
    interfaces: [Enum]
  };
  function ShaderType$values() {
    return [ShaderType$SOLID_getInstance(), ShaderType$PIXEL_getInstance(), ShaderType$SINE_WAVE_getInstance(), ShaderType$COMPOSITOR_getInstance()];
  }
  ShaderType.values = ShaderType$values;
  function ShaderType$valueOf(name) {
    switch (name) {
      case 'SOLID':
        return ShaderType$SOLID_getInstance();
      case 'PIXEL':
        return ShaderType$PIXEL_getInstance();
      case 'SINE_WAVE':
        return ShaderType$SINE_WAVE_getInstance();
      case 'COMPOSITOR':
        return ShaderType$COMPOSITOR_getInstance();
      default:throwISE('No enum constant baaahs.ShaderType.' + name);
    }
  }
  ShaderType.valueOf_61zpoe$ = ShaderType$valueOf;
  function Shader(type) {
    Shader$Companion_getInstance();
    this.type = type;
  }
  Shader.prototype.serialize_ep8mow$ = function (writer) {
    writer.writeByte_s8j3t7$(toByte(this.type.ordinal));
  };
  Shader.prototype.serializeBuffer_ep8mow$ = function (writer) {
    this.buffer.serialize_ep8mow$(writer);
  };
  Shader.prototype.readBuffer_c4pr8w$ = function (reader) {
    this.buffer.read_c4pr8w$(reader);
  };
  function Shader$Companion() {
    Shader$Companion_instance = this;
  }
  Shader$Companion.prototype.parse_c4pr8w$ = function (reader) {
    var shaderTypeI = reader.readByte();
    var shaderType = ShaderType$Companion_getInstance().get_s8j3t7$(shaderTypeI);
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
  Shader.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Shader',
    interfaces: []
  };
  function ShaderBuffer() {
  }
  ShaderBuffer.$metadata$ = {
    kind: Kind_INTERFACE,
    simpleName: 'ShaderBuffer',
    interfaces: []
  };
  function ShaderImpl() {
  }
  ShaderImpl.$metadata$ = {
    kind: Kind_INTERFACE,
    simpleName: 'ShaderImpl',
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
  SheepModel.prototype.load = function () {
    var vertices = ArrayList_init();
    var panels = ArrayList_init();
    var currentPanel = {v: new SheepModel$Panel('initial')};
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

          currentPanel.v.lines.add_11rb$(new SheepModel$Line(points));
          break;
      }
    }
    println('Sheep model has ' + panels.size + ' panels (and ' + vertices.size + ' vertices)!');
    this.vertices = vertices;
    this.panels = panels;
    this.eyes = arrayListOf([new SheepModel$MovingHead('leftEye', new SheepModel$Point(-163.738, 204.361, 439.302)), new SheepModel$MovingHead('rightEye', new SheepModel$Point(-103.738, 204.361, 439.302))]);
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
    this.faces = new SheepModel$Faces();
    this.lines = ArrayList_init();
  }
  SheepModel$Panel.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Panel',
    interfaces: []
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
  function ShowMeta(name) {
    this.name = name;
  }
  ShowMeta.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'ShowMeta',
    interfaces: []
  };
  function Show() {
  }
  Show.$metadata$ = {
    kind: Kind_INTERFACE,
    simpleName: 'Show',
    interfaces: []
  };
  function Topics() {
    Topics_instance = this;
    this.primaryColor = new PubSub$Topic('primaryColor', Color$Companion_getInstance().serializer());
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
  function Ui(network, pinkyAddress, display) {
    this.network = network;
    this.pinkyAddress = pinkyAddress;
    this.display = display;
    this.link = this.network.link();
    this.pubSub_r2ifbz$_0 = this.pubSub_r2ifbz$_0;
    this.connect();
  }
  Object.defineProperty(Ui.prototype, 'pubSub_0', {
    get: function () {
      if (this.pubSub_r2ifbz$_0 == null)
        return throwUPAE('pubSub');
      return this.pubSub_r2ifbz$_0;
    },
    set: function (pubSub) {
      this.pubSub_r2ifbz$_0 = pubSub;
    }
  });
  Ui.prototype.connect = function () {
    var pubSub = new PubSub$Client(this.link, this.pinkyAddress, Ports$Companion_getInstance().PINKY_UI_TCP);
    var context = new UiContext(pubSub);
    this.display.createApp_kvdprd$(context);
  };
  Ui.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Ui',
    interfaces: []
  };
  function UiContext(pubSub) {
    this.pubSub = pubSub;
  }
  UiContext.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'UiContext',
    interfaces: []
  };
  function UiDisplay() {
  }
  UiDisplay.$metadata$ = {
    kind: Kind_INTERFACE,
    simpleName: 'UiDisplay',
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
  ByteArrayWriter.prototype.writeBytes_fqrh44$ = function (data) {
    this.growIfNecessary_0(4 + data.length | 0);
    this.writeInt_za3lpa$(data.length);
    arrayCopy(data, this.bytes_0, this.offset, 0, data.length);
    this.offset = this.offset + data.length | 0;
  };
  ByteArrayWriter.prototype.toBytes = function () {
    return copyOf(this.bytes_0, this.offset);
  };
  ByteArrayWriter.prototype.growIfNecessary_0 = function (by) {
    if ((this.offset + by | 0) >= this.bytes_0.length) {
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
  function ByteArrayReader(bytes, offset) {
    if (offset === void 0)
      offset = 0;
    this.bytes = bytes;
    this.offset = offset;
  }
  ByteArrayReader.prototype.readBoolean = function () {
    return this.bytes[this.offset] !== toByte(0);
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
  ByteArrayReader.prototype.readBytes = function () {
    var count = this.readInt();
    var bytes = copyOfRange(this.bytes, this.offset, this.offset + count | 0);
    this.offset = this.offset + count | 0;
    return bytes;
  };
  ByteArrayReader.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'ByteArrayReader',
    interfaces: []
  };
  function CompositorShader(aShader, bShader) {
    CompositorShader$Companion_getInstance();
    Shader.call(this, ShaderType$COMPOSITOR_getInstance());
    this.aShader = aShader;
    this.bShader = bShader;
    this.buffer_t0nanq$_0 = new CompositorShaderBuffer();
  }
  Object.defineProperty(CompositorShader.prototype, 'buffer', {
    get: function () {
      return this.buffer_t0nanq$_0;
    }
  });
  CompositorShader.prototype.serialize_ep8mow$ = function (writer) {
    Shader.prototype.serialize_ep8mow$.call(this, writer);
    this.aShader.serialize_ep8mow$(writer);
    this.bShader.serialize_ep8mow$(writer);
  };
  CompositorShader.prototype.serializeBuffer_ep8mow$ = function (writer) {
    Shader.prototype.serializeBuffer_ep8mow$.call(this, writer);
    this.aShader.serializeBuffer_ep8mow$(writer);
    this.bShader.serializeBuffer_ep8mow$(writer);
  };
  CompositorShader.prototype.createImpl_bbfl1t$ = function (pixels) {
    return new CompositorShaderImpl(this.aShader, this.bShader, this.buffer, pixels);
  };
  CompositorShader.prototype.readBuffer_c4pr8w$ = function (reader) {
    Shader.prototype.readBuffer_c4pr8w$.call(this, reader);
    this.aShader.readBuffer_c4pr8w$(reader);
    this.bShader.readBuffer_c4pr8w$(reader);
  };
  function CompositorShader$Companion() {
    CompositorShader$Companion_instance = this;
  }
  CompositorShader$Companion.prototype.parse_c4pr8w$ = function (reader) {
    var shaderA = Shader$Companion_getInstance().parse_c4pr8w$(reader);
    var shaderB = Shader$Companion_getInstance().parse_c4pr8w$(reader);
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
  CompositorShader.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'CompositorShader',
    interfaces: [Shader]
  };
  function CompositorShaderImpl(aShader, bShader, buffer, pixels) {
    this.buffer = buffer;
    this.pixels = pixels;
    var array = Array_0(this.pixels.count);
    var tmp$;
    tmp$ = array.length - 1 | 0;
    for (var i = 0; i <= tmp$; i++) {
      array[i] = Color$Companion_getInstance().WHITE;
    }
    this.colors_0 = array;
    this.aPixels_0 = new PixelBuf(this.pixels.count);
    this.bPixels_0 = new PixelBuf(this.pixels.count);
    this.shaderAImpl_0 = aShader.createImpl_bbfl1t$(this.aPixels_0);
    this.shaderBImpl_0 = bShader.createImpl_bbfl1t$(this.bPixels_0);
  }
  function CompositorShaderImpl$draw$lambda(a, b) {
    return a.plus_rny0jj$(b);
  }
  function CompositorShaderImpl$draw$lambda_0(a, b) {
    return b;
  }
  CompositorShaderImpl.prototype.draw = function () {
    var tmp$, tmp$_0;
    this.shaderAImpl_0.draw();
    this.shaderBImpl_0.draw();
    var operation;
    switch (this.buffer.mode.name) {
      case 'ADD':
        tmp$ = CompositorShaderImpl$draw$lambda;
        break;
      case 'OVERLAY':
        tmp$ = CompositorShaderImpl$draw$lambda_0;
        break;
      default:tmp$ = Kotlin.noWhenBranchMatched();
        break;
    }
    operation = tmp$;
    tmp$_0 = this.colors_0;
    for (var i = 0; i !== tmp$_0.length; ++i) {
      var aColor = this.aPixels_0.colors[i];
      var bColor = this.bPixels_0.colors[i];
      this.colors_0[i] = aColor.fade_6zkv30$(operation(aColor, bColor), this.buffer.fade);
    }
    this.pixels.set_tmuqsv$(this.colors_0);
  };
  CompositorShaderImpl.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'CompositorShaderImpl',
    interfaces: [ShaderImpl]
  };
  function PixelBuf(count) {
    this.count_ntsq8o$_0 = count;
    var array = Array_0(this.count);
    var tmp$;
    tmp$ = array.length - 1 | 0;
    for (var i = 0; i <= tmp$; i++) {
      array[i] = Color$Companion_getInstance().WHITE;
    }
    this.colors = array;
  }
  Object.defineProperty(PixelBuf.prototype, 'count', {
    get: function () {
      return this.count_ntsq8o$_0;
    }
  });
  PixelBuf.prototype.set_tmuqsv$ = function (colors) {
    arrayCopy(colors, this.colors, 0, 0, colors.length);
  };
  PixelBuf.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'PixelBuf',
    interfaces: [Pixels]
  };
  function CompositorShaderBuffer(mode, fade) {
    if (mode === void 0)
      mode = CompositingMode$OVERLAY_getInstance();
    if (fade === void 0)
      fade = 0.5;
    this.mode = mode;
    this.fade = fade;
  }
  CompositorShaderBuffer.prototype.serialize_ep8mow$ = function (writer) {
    writer.writeByte_s8j3t7$(toByte(this.mode.ordinal));
    writer.writeFloat_mx4ult$(this.fade);
  };
  CompositorShaderBuffer.prototype.read_c4pr8w$ = function (reader) {
    this.mode = CompositingMode$Companion_getInstance().get_s8j3t7$(reader.readByte());
    this.fade = reader.readFloat();
  };
  CompositorShaderBuffer.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'CompositorShaderBuffer',
    interfaces: [ShaderBuffer]
  };
  function CompositingMode(name, ordinal) {
    Enum.call(this);
    this.name$ = name;
    this.ordinal$ = ordinal;
  }
  function CompositingMode_initFields() {
    CompositingMode_initFields = function () {
    };
    CompositingMode$OVERLAY_instance = new CompositingMode('OVERLAY', 0);
    CompositingMode$ADD_instance = new CompositingMode('ADD', 1);
    CompositingMode$Companion_getInstance();
  }
  var CompositingMode$OVERLAY_instance;
  function CompositingMode$OVERLAY_getInstance() {
    CompositingMode_initFields();
    return CompositingMode$OVERLAY_instance;
  }
  var CompositingMode$ADD_instance;
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
    Shader.call(this, ShaderType$PIXEL_getInstance());
    this.buffer_91rl0z$_0 = new PixelShaderBuffer();
  }
  Object.defineProperty(PixelShader.prototype, 'buffer', {
    get: function () {
      return this.buffer_91rl0z$_0;
    }
  });
  PixelShader.prototype.createImpl_bbfl1t$ = function (pixels) {
    return new PixelShaderImpl(this.buffer, pixels);
  };
  function PixelShader$Companion() {
    PixelShader$Companion_instance = this;
  }
  PixelShader$Companion.prototype.parse_c4pr8w$ = function (reader) {
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
  PixelShader.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'PixelShader',
    interfaces: [Shader]
  };
  function PixelShaderImpl(buffer, pixels) {
    this.buffer = buffer;
    this.pixels = pixels;
    var array = Array_0(this.pixels.count);
    var tmp$;
    tmp$ = array.length - 1 | 0;
    for (var i = 0; i <= tmp$; i++) {
      array[i] = Color$Companion_getInstance().WHITE;
    }
    this.colors_0 = array;
  }
  PixelShaderImpl.prototype.draw = function () {
    var tmp$;
    tmp$ = this.colors_0;
    for (var i = 0; i !== tmp$.length; ++i) {
      this.colors_0[i] = this.buffer.colors[i];
    }
    this.pixels.set_tmuqsv$(this.colors_0);
  };
  PixelShaderImpl.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'PixelShaderImpl',
    interfaces: [ShaderImpl]
  };
  function PixelShaderBuffer() {
    this.fakeyTerribleHardCodedNumberOfPixels_0 = 1337;
    var array = Array_0(this.fakeyTerribleHardCodedNumberOfPixels_0);
    var tmp$;
    tmp$ = array.length - 1 | 0;
    for (var i = 0; i <= tmp$; i++) {
      array[i] = Color$Companion_getInstance().WHITE;
    }
    this.colors = array;
  }
  PixelShaderBuffer.prototype.serialize_ep8mow$ = function (writer) {
    writer.writeInt_za3lpa$(this.colors.length);
    var $receiver = this.colors;
    var tmp$;
    for (tmp$ = 0; tmp$ !== $receiver.length; ++tmp$) {
      var element = $receiver[tmp$];
      element.serialize_ep8mow$(writer);
    }
  };
  PixelShaderBuffer.prototype.read_c4pr8w$ = function (reader) {
    var incomingColorCount = reader.readInt();
    var tmp$;
    tmp$ = until(0, incomingColorCount).iterator();
    while (tmp$.hasNext()) {
      var element = tmp$.next();
      this.colors[element] = Color$Companion_getInstance().parse_c4pr8w$(reader);
    }
  };
  PixelShaderBuffer.prototype.setAll_rny0jj$ = function (color) {
    var tmp$;
    tmp$ = this.colors;
    for (var i = 0; i !== tmp$.length; ++i) {
      this.colors[i] = color;
    }
  };
  PixelShaderBuffer.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'PixelShaderBuffer',
    interfaces: [ShaderBuffer]
  };
  function SineWaveShader() {
    SineWaveShader$Companion_getInstance();
    Shader.call(this, ShaderType$SINE_WAVE_getInstance());
    this.buffer_d2wx0b$_0 = new SineWaveShaderBuffer();
  }
  Object.defineProperty(SineWaveShader.prototype, 'buffer', {
    get: function () {
      return this.buffer_d2wx0b$_0;
    }
  });
  SineWaveShader.prototype.createImpl_bbfl1t$ = function (pixels) {
    return new SineWaveShaderImpl(this.buffer, pixels);
  };
  function SineWaveShader$Companion() {
    SineWaveShader$Companion_instance = this;
  }
  SineWaveShader$Companion.prototype.parse_c4pr8w$ = function (reader) {
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
  SineWaveShader.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'SineWaveShader',
    interfaces: [Shader]
  };
  function SineWaveShaderImpl(buffer, pixels) {
    this.buffer = buffer;
    this.pixels = pixels;
    var array = Array_0(this.pixels.count);
    var tmp$;
    tmp$ = array.length - 1 | 0;
    for (var i = 0; i <= tmp$; i++) {
      array[i] = Color$Companion_getInstance().WHITE;
    }
    this.colors_0 = array;
  }
  SineWaveShaderImpl.prototype.draw = function () {
    var tmp$;
    var theta = this.buffer.theta;
    var pixelCount = this.pixels.count;
    var density = this.buffer.density;
    tmp$ = this.colors_0;
    for (var i = 0; i !== tmp$.length; ++i) {
      var x = theta + 2 * math.PI * (i / pixelCount * density);
      var v = Math_0.sin(x) / 2 + 0.5;
      this.colors_0[i] = Color$Companion_getInstance().BLACK.fade_6zkv30$(this.buffer.color, v);
    }
    this.pixels.set_tmuqsv$(this.colors_0);
  };
  SineWaveShaderImpl.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'SineWaveShaderImpl',
    interfaces: [ShaderImpl]
  };
  function SineWaveShaderBuffer() {
    SineWaveShaderBuffer$Companion_getInstance();
    this.color = Color$Companion_getInstance().WHITE;
    this.theta = 0.0;
    this.density = 1.0;
  }
  SineWaveShaderBuffer.prototype.serialize_ep8mow$ = function (writer) {
    this.color.serialize_ep8mow$(writer);
    writer.writeFloat_mx4ult$(this.theta);
    writer.writeFloat_mx4ult$(this.density);
  };
  SineWaveShaderBuffer.prototype.read_c4pr8w$ = function (reader) {
    this.color = Color$Companion_getInstance().parse_c4pr8w$(reader);
    this.theta = reader.readFloat();
    this.density = reader.readFloat();
  };
  function SineWaveShaderBuffer$Companion() {
    SineWaveShaderBuffer$Companion_instance = this;
  }
  SineWaveShaderBuffer$Companion.prototype.parse_c4pr8w$ = function (reader) {
    var buf = new SineWaveShaderBuffer();
    buf.color = Color$Companion_getInstance().parse_c4pr8w$(reader);
    buf.theta = reader.readFloat();
    buf.density = reader.readFloat();
    return buf;
  };
  SineWaveShaderBuffer$Companion.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'Companion',
    interfaces: []
  };
  var SineWaveShaderBuffer$Companion_instance = null;
  function SineWaveShaderBuffer$Companion_getInstance() {
    if (SineWaveShaderBuffer$Companion_instance === null) {
      new SineWaveShaderBuffer$Companion();
    }
    return SineWaveShaderBuffer$Companion_instance;
  }
  SineWaveShaderBuffer.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'SineWaveShaderBuffer',
    interfaces: [ShaderBuffer]
  };
  function SolidShader() {
    SolidShader$Companion_getInstance();
    Shader.call(this, ShaderType$SOLID_getInstance());
    this.buffer_5onrmg$_0 = new SolidShaderBuffer();
  }
  Object.defineProperty(SolidShader.prototype, 'buffer', {
    get: function () {
      return this.buffer_5onrmg$_0;
    }
  });
  SolidShader.prototype.createImpl_bbfl1t$ = function (pixels) {
    return new SolidShaderImpl(this.buffer, pixels);
  };
  function SolidShader$Companion() {
    SolidShader$Companion_instance = this;
  }
  SolidShader$Companion.prototype.parse_c4pr8w$ = function (reader) {
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
  SolidShader.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'SolidShader',
    interfaces: [Shader]
  };
  function SolidShaderImpl(buffer, pixels) {
    this.buffer = buffer;
    this.pixels = pixels;
    var array = Array_0(this.pixels.count);
    var tmp$;
    tmp$ = array.length - 1 | 0;
    for (var i = 0; i <= tmp$; i++) {
      array[i] = Color$Companion_getInstance().WHITE;
    }
    this.colors_0 = array;
  }
  SolidShaderImpl.prototype.draw = function () {
    var tmp$;
    tmp$ = this.colors_0;
    for (var i = 0; i !== tmp$.length; ++i) {
      this.colors_0[i] = this.buffer.color;
    }
    this.pixels.set_tmuqsv$(this.colors_0);
  };
  SolidShaderImpl.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'SolidShaderImpl',
    interfaces: [ShaderImpl]
  };
  function SolidShaderBuffer() {
    this.color = Color$Companion_getInstance().WHITE;
  }
  SolidShaderBuffer.prototype.serialize_ep8mow$ = function (writer) {
    this.color.serialize_ep8mow$(writer);
  };
  SolidShaderBuffer.prototype.read_c4pr8w$ = function (reader) {
    this.color = Color$Companion_getInstance().parse_c4pr8w$(reader);
  };
  SolidShaderBuffer.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'SolidShaderBuffer',
    interfaces: [ShaderBuffer]
  };
  function CompositeShow(sheepModel, showRunner) {
    this.colorPicker_0 = showRunner.getColorPicker();
    var $receiver = sheepModel.allPanels;
    var destination = ArrayList_init_0(collectionSizeOrDefault($receiver, 10));
    var tmp$;
    tmp$ = $receiver.iterator();
    while (tmp$.hasNext()) {
      var item = tmp$.next();
      var tmp$_0 = destination.add_11rb$;
      var solidShader = showRunner.getSolidShader_jfju1k$(item);
      var $receiver_0 = showRunner.getSineWaveShader_jfju1k$(item);
      $receiver_0.buffer.density = Random.Default.nextFloat() * 20;
      var sineWaveShader = $receiver_0;
      var compositorShader = showRunner.getCompositorShader_626mua$(item, solidShader, sineWaveShader);
      var $receiver_1 = compositorShader.buffer;
      $receiver_1.mode = CompositingMode$ADD_getInstance();
      $receiver_1.fade = 1.0;
      tmp$_0.call(destination, new ShaderBufs(solidShader.buffer, sineWaveShader.buffer, compositorShader.buffer));
    }
    this.shaderBufs_0 = destination;
    var $receiver_2 = sheepModel.eyes;
    var destination_0 = ArrayList_init_0(collectionSizeOrDefault($receiver_2, 10));
    var tmp$_1;
    tmp$_1 = $receiver_2.iterator();
    while (tmp$_1.hasNext()) {
      var item_0 = tmp$_1.next();
      destination_0.add_11rb$(showRunner.getMovingHead_1hma8m$(item_0));
    }
    this.movingHeadBuffers_0 = destination_0;
  }
  CompositeShow.prototype.nextFrame = function () {
    var theta = getTimeMillis().toNumber() / 1000.0 % (2 * math.PI);
    var i = {v: 0};
    var tmp$;
    tmp$ = this.shaderBufs_0.iterator();
    while (tmp$.hasNext()) {
      var element = tmp$.next();
      var tmp$_0;
      element.solidShaderBuffer.color = this.colorPicker_0.color;
      element.sineWaveShaderBuffer.color = Color$Companion_getInstance().WHITE;
      element.sineWaveShaderBuffer.theta = theta + (tmp$_0 = i.v, i.v = tmp$_0 + 1 | 0, tmp$_0);
      element.compositorShaderBuffer.mode = CompositingMode$ADD_getInstance();
      element.compositorShaderBuffer.fade = 1.0;
    }
    var tmp$_1;
    tmp$_1 = this.movingHeadBuffers_0.iterator();
    while (tmp$_1.hasNext()) {
      var element_0 = tmp$_1.next();
      element_0.colorWheel = element_0.closestColorFor_rny0jj$(this.colorPicker_0.color);
      element_0.pan = math.PI / 2;
      element_0.tilt = theta / 2;
    }
  };
  function CompositeShow$Meta() {
    ShowMeta.call(this, 'CompositeShow');
  }
  CompositeShow$Meta.prototype.createShow_h1b9op$ = function (sheepModel, showRunner) {
    return new CompositeShow(sheepModel, showRunner);
  };
  CompositeShow$Meta.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Meta',
    interfaces: [ShowMeta]
  };
  CompositeShow.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'CompositeShow',
    interfaces: [Show]
  };
  function ShaderBufs(solidShaderBuffer, sineWaveShaderBuffer, compositorShaderBuffer) {
    this.solidShaderBuffer = solidShaderBuffer;
    this.sineWaveShaderBuffer = sineWaveShaderBuffer;
    this.compositorShaderBuffer = compositorShaderBuffer;
  }
  ShaderBufs.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'ShaderBufs',
    interfaces: []
  };
  function RandomShow(sheepModel, showRunner) {
    var $receiver = sheepModel.allPanels;
    var destination = ArrayList_init_0(collectionSizeOrDefault($receiver, 10));
    var tmp$;
    tmp$ = $receiver.iterator();
    while (tmp$.hasNext()) {
      var item = tmp$.next();
      destination.add_11rb$(showRunner.getPixelShader_jfju1k$(item).buffer);
    }
    this.pixelShaderBuffers = destination;
    var $receiver_0 = sheepModel.eyes;
    var destination_0 = ArrayList_init_0(collectionSizeOrDefault($receiver_0, 10));
    var tmp$_0;
    tmp$_0 = $receiver_0.iterator();
    while (tmp$_0.hasNext()) {
      var item_0 = tmp$_0.next();
      destination_0.add_11rb$(showRunner.getMovingHead_1hma8m$(item_0));
    }
    this.movingHeadBuffers = destination_0;
  }
  RandomShow.prototype.nextFrame = function () {
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
  function RandomShow$Meta() {
    ShowMeta.call(this, 'RandomShow');
  }
  RandomShow$Meta.prototype.createShow_h1b9op$ = function (sheepModel, showRunner) {
    return new RandomShow(sheepModel, showRunner);
  };
  RandomShow$Meta.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Meta',
    interfaces: [ShowMeta]
  };
  RandomShow.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'RandomShow',
    interfaces: [Show]
  };
  function SomeDumbShow(sheepModel, showRunner) {
    this.colorPicker = showRunner.getColorPicker();
    var $receiver = sheepModel.allPanels;
    var destination = ArrayList_init_0(collectionSizeOrDefault($receiver, 10));
    var tmp$;
    tmp$ = $receiver.iterator();
    while (tmp$.hasNext()) {
      var item = tmp$.next();
      destination.add_11rb$(showRunner.getPixelShader_jfju1k$(item).buffer);
    }
    this.pixelShaderBuffers = destination;
    var $receiver_0 = sheepModel.eyes;
    var destination_0 = ArrayList_init_0(collectionSizeOrDefault($receiver_0, 10));
    var tmp$_0;
    tmp$_0 = $receiver_0.iterator();
    while (tmp$_0.hasNext()) {
      var item_0 = tmp$_0.next();
      destination_0.add_11rb$(showRunner.getMovingHead_1hma8m$(item_0));
    }
    this.movingHeads = destination_0;
  }
  SomeDumbShow.prototype.nextFrame = function () {
    var seed = Random_0(0);
    var tmp$;
    tmp$ = this.pixelShaderBuffers.iterator();
    while (tmp$.hasNext()) {
      var element = tmp$.next();
      var baseSaturation = seed.nextFloat();
      var panelColor = seed.nextFloat() < 0.1 ? Color$Companion_getInstance().random() : this.colorPicker.color;
      var $receiver = element.colors;
      var tmp$_0, tmp$_0_0;
      var index = 0;
      for (tmp$_0 = 0; tmp$_0 !== $receiver.length; ++tmp$_0) {
        var item = $receiver[tmp$_0];
        element.colors[tmp$_0_0 = index, index = tmp$_0_0 + 1 | 0, tmp$_0_0] = this.desaturateRandomishly_0(baseSaturation, seed, panelColor);
      }
    }
    var tmp$_1;
    tmp$_1 = this.movingHeads.iterator();
    while (tmp$_1.hasNext()) {
      var element_0 = tmp$_1.next();
      element_0.colorWheel = element_0.closestColorFor_rny0jj$(this.colorPicker.color);
      element_0.pan = element_0.pan + (this.nextRandomFloat_0(seed) - 0.5) / 5;
      element_0.tilt = element_0.tilt + (this.nextRandomFloat_0(seed) - 0.5) / 5;
    }
  };
  SomeDumbShow.prototype.desaturateRandomishly_0 = function (baseSaturation, seed, panelColor) {
    var x = this.nextRandomFloat_0(seed);
    var saturation = baseSaturation * Math_0.abs(x);
    var desaturatedColor = panelColor.withSaturation_mx4ult$(saturation);
    return desaturatedColor;
  };
  SomeDumbShow.prototype.nextRandomFloat_0 = function (seed) {
    var x = seed.nextDouble() + getTimeMillis().toNumber() / 1000;
    return Math_0.sin(x);
  };
  function SomeDumbShow$Meta() {
    ShowMeta.call(this, 'SomeDumbShow');
  }
  SomeDumbShow$Meta.prototype.createShow_h1b9op$ = function (sheepModel, showRunner) {
    return new SomeDumbShow(sheepModel, showRunner);
  };
  SomeDumbShow$Meta.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Meta',
    interfaces: [ShowMeta]
  };
  SomeDumbShow.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'SomeDumbShow',
    interfaces: [Show]
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
    this.color_y02qw7$_0 = null;
    this.onPrimaryColorChange_mf0bnc$_0 = null;
    this.selectedShow_l65oio$_0 = null;
    this.brainCountDiv_0 = null;
    this.beat1_0 = null;
    this.beat2_0 = null;
    this.beat3_0 = null;
    this.beat4_0 = null;
    this.beats_0 = null;
    this.colorPickerView_0 = null;
    this.showListDiv_0 = null;
    this.showButtons_0 = null;
    appendText(element, 'Brains online: ');
    this.brainCountDiv_0 = appendElement(element, 'span', JsPinkyDisplay_init$lambda);
    var beatsDiv = appendElement(element, 'div', JsPinkyDisplay_init$lambda_0);
    this.beat1_0 = appendElement(beatsDiv, 'span', JsPinkyDisplay_init$lambda_1);
    this.beat2_0 = appendElement(beatsDiv, 'span', JsPinkyDisplay_init$lambda_2);
    this.beat3_0 = appendElement(beatsDiv, 'span', JsPinkyDisplay_init$lambda_3);
    this.beat4_0 = appendElement(beatsDiv, 'span', JsPinkyDisplay_init$lambda_4);
    this.beats_0 = listOf([this.beat1_0, this.beat2_0, this.beat3_0, this.beat4_0]);
    this.colorPickerView_0 = new ColorPickerView(element, JsPinkyDisplay_init$lambda_5(this));
    this.color = this.colorPickerView_0.colors.get_za3lpa$(0);
    this.showListDiv_0 = appendElement(element, 'div', JsPinkyDisplay_init$lambda_6);
    this.showButtons_0 = ArrayList_init();
    this.brainCount_tt9c5b$_0 = 0;
    this.beat_o13evy$_0 = 0;
  }
  Object.defineProperty(JsPinkyDisplay.prototype, 'color', {
    get: function () {
      return this.color_y02qw7$_0;
    },
    set: function (value) {
      this.colorPickerView_0.setColor_58xt5s$(value);
      this.color_y02qw7$_0 = value;
    }
  });
  Object.defineProperty(JsPinkyDisplay.prototype, 'onPrimaryColorChange', {
    get: function () {
      return this.onPrimaryColorChange_mf0bnc$_0;
    },
    set: function (onPrimaryColorChange) {
      this.onPrimaryColorChange_mf0bnc$_0 = onPrimaryColorChange;
    }
  });
  Object.defineProperty(JsPinkyDisplay.prototype, 'selectedShow', {
    get: function () {
      return this.selectedShow_l65oio$_0;
    },
    set: function (selectedShow) {
      this.selectedShow_l65oio$_0 = selectedShow;
    }
  });
  function JsPinkyDisplay$listShows$lambda($receiver) {
    appendText($receiver, 'Shows: ');
    return Unit;
  }
  function JsPinkyDisplay$listShows$lambda_0($receiver) {
    return Unit;
  }
  function JsPinkyDisplay$listShows$lambda$lambda(closure$showMeta) {
    return function ($receiver) {
      appendText($receiver, closure$showMeta.name);
      return Unit;
    };
  }
  function JsPinkyDisplay$listShows$lambda$lambda_0(this$JsPinkyDisplay) {
    return function (it) {
      this$JsPinkyDisplay.selectedShow = it;
      return Unit;
    };
  }
  JsPinkyDisplay.prototype.listShows_5ucgt1$ = function (showMetas) {
    clear(this.showListDiv_0);
    appendElement(this.showListDiv_0, 'b', JsPinkyDisplay$listShows$lambda);
    appendElement(this.showListDiv_0, 'br', JsPinkyDisplay$listShows$lambda_0);
    this.showButtons_0.clear();
    var tmp$;
    tmp$ = showMetas.iterator();
    while (tmp$.hasNext()) {
      var element = tmp$.next();
      var element_0 = appendElement(this.showListDiv_0, 'span', JsPinkyDisplay$listShows$lambda$lambda(element));
      var showButton = new JsPinkyDisplay$ShowButton(element, element_0);
      showButton.onSelect = JsPinkyDisplay$listShows$lambda$lambda_0(this);
      showButton.allButtons = this.showButtons_0;
      this.showButtons_0.add_11rb$(showButton);
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
  function JsPinkyDisplay_init$lambda_5(this$JsPinkyDisplay) {
    return function (it) {
      var tmp$;
      this$JsPinkyDisplay.color = it;
      (tmp$ = this$JsPinkyDisplay.onPrimaryColorChange) != null ? tmp$() : null;
      return Unit;
    };
  }
  function JsPinkyDisplay_init$lambda_6($receiver) {
    $receiver.className = 'showsDiv';
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
  JsBrainDisplay.prototype.haveLink_6qu7we$ = function (link) {
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
  function FakeMediaDevices(visualizer) {
    this.visualizer_0 = visualizer;
    this.currentCam = null;
  }
  FakeMediaDevices.prototype.getCurrentCam = function () {
    return this.currentCam;
  };
  FakeMediaDevices.prototype.getCamera_vux9f0$ = function (width, height) {
    var $receiver = new FakeMediaDevices$FakeCamera(this, width, height);
    this.visualizer_0.addFrameListener_9dei3h$($receiver);
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
    this.pixelBuffer_0 = new Uint8ClampedArray(Kotlin.imul(this.width, this.height) * 4 | 0);
    this.imageData_0 = new ImageData(this.pixelBuffer_0, this.width, this.height);
    this.onImage_tirclm$_0 = FakeMediaDevices$FakeCamera$onImage$lambda;
  }
  FakeMediaDevices$FakeCamera.prototype.onFrameReady = function (scene, camera) {
    this.camRenderer.render(scene, camera);
    this.camCtx_0.readPixels(0, 0, this.width, this.height, this.camCtx_0.RGBA, this.camCtx_0.UNSIGNED_BYTE, new Uint8Array(this.pixelBuffer_0.buffer));
    this.onImage(new ImageDataImage(this.imageData_0, true));
  };
  Object.defineProperty(FakeMediaDevices$FakeCamera.prototype, 'onImage', {
    get: function () {
      return this.onImage_tirclm$_0;
    },
    set: function (onImage) {
      this.onImage_tirclm$_0 = onImage;
    }
  });
  function FakeMediaDevices$FakeCamera$close$lambda(f) {
    return Unit;
  }
  FakeMediaDevices$FakeCamera.prototype.close = function () {
    this.onImage = FakeMediaDevices$FakeCamera$close$lambda;
    this.$outer.visualizer_0.removeFrameListener_9dei3h$(this);
  };
  function FakeMediaDevices$FakeCamera$onImage$lambda(f) {
    return Unit;
  }
  FakeMediaDevices$FakeCamera.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'FakeCamera',
    interfaces: [FrameListener, MediaDevices$Camera]
  };
  FakeMediaDevices.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'FakeMediaDevices',
    interfaces: [MediaDevices]
  };
  function JsMapperDisplay(container) {
    this.onClose_cgypwl$_0 = JsMapperDisplay$onClose$lambda;
    this.width_0 = 640;
    this.height_0 = 300;
    this.uiRenderer = new WebGLRenderer_init({alpha: true});
    this.uiScene = new Scene();
    this.uiCamera = new PerspectiveCamera_init(45, this.width_0 / this.height_0, 1, 10000);
    this.uiControls = null;
    this.wireframe = new Object3D();
    this.screen_0 = div_0(get_create(document), 'mapperUi-screen', JsMapperDisplay$screen$lambda(this));
    this.frame_0 = container.getFrame_409ufb$('Mapper', this.screen_0, JsMapperDisplay$frame$lambda(this), JsMapperDisplay$frame$lambda_0(this));
    var tmp$, tmp$_0, tmp$_1, tmp$_2;
    this.ui2dCanvas = Kotlin.isType(tmp$ = ensureNotNull(this.screen_0.getElementsByClassName('mapperUi-2d-canvas')[0]), HTMLCanvasElement) ? tmp$ : throwCCE();
    this.ui2dCtx = Kotlin.isType(tmp$_0 = ensureNotNull(this.ui2dCanvas.getContext('2d')), CanvasRenderingContext2D) ? tmp$_0 : throwCCE();
    this.ui3dDiv = Kotlin.isType(tmp$_1 = ensureNotNull(this.screen_0.getElementsByClassName('mapperUi-3d-div')[0]), HTMLDivElement) ? tmp$_1 : throwCCE();
    this.ui3dCanvas = Kotlin.isType(tmp$_2 = this.uiRenderer.domElement, HTMLCanvasElement) ? tmp$_2 : throwCCE();
    this.wireframeInitialized_0 = false;
    this.jsInitialized_0 = false;
    this.ui3dDiv.appendChild(this.ui3dCanvas);
    this.uiCamera.position.z = 1000.0;
    this.uiScene.add(this.uiCamera);
    this.uiControls = new OrbitControls(this.uiCamera, this.uiRenderer.domElement);
    this.uiControls.minPolarAngle = math.PI / 2 - 0.25;
    this.uiControls.maxPolarAngle = math.PI / 2 + 0.25;
  }
  Object.defineProperty(JsMapperDisplay.prototype, 'onClose', {
    get: function () {
      return this.onClose_cgypwl$_0;
    },
    set: function (onClose) {
      this.onClose_cgypwl$_0 = onClose;
    }
  });
  JsMapperDisplay.prototype.resizeTo_0 = function (width, height) {
    var tmp$, tmp$_0;
    this.width_0 = width;
    this.height_0 = height;
    this.uiCamera.aspect = width / height;
    this.uiRenderer.setSize(width, height);
    this.uiRenderer.setPixelRatio(width / height);
    (Kotlin.isType(tmp$ = this.uiRenderer.domElement, HTMLCanvasElement) ? tmp$ : throwCCE()).width = width;
    (Kotlin.isType(tmp$_0 = this.uiRenderer.domElement, HTMLCanvasElement) ? tmp$_0 : throwCCE()).height = height;
    this.ui2dCanvas.width = width;
    this.ui2dCanvas.height = height;
  };
  JsMapperDisplay.prototype.addWireframe_9u144y$ = function (sheepModel) {
    var geom = new Geometry();
    var $receiver = new LineBasicMaterial();
    $receiver.color = new Color_init(0.0, 1.0, 0.0);
    var lineMaterial = $receiver;
    var $receiver_0 = new MeshBasicMaterial();
    $receiver_0.color = new Color_init(0, 0, 0);
    var panelMaterial = $receiver_0;
    var $receiver_1 = sheepModel.vertices;
    var destination = ArrayList_init_0(collectionSizeOrDefault($receiver_1, 10));
    var tmp$;
    tmp$ = $receiver_1.iterator();
    while (tmp$.hasNext()) {
      var item = tmp$.next();
      destination.add_11rb$(new Vector3(item.x, item.y, item.z));
    }
    geom.vertices = copyToArray(destination);
    var faces = ArrayList_init();
    var tmp$_0;
    tmp$_0 = sheepModel.panels.iterator();
    while (tmp$_0.hasNext()) {
      var element = tmp$_0.next();
      var tmp$_1;
      tmp$_1 = element.faces.faces.iterator();
      while (tmp$_1.hasNext()) {
        var element_0 = tmp$_1.next();
        var face3 = new Face3_init(element_0.vertexIds.get_za3lpa$(0), element_0.vertexIds.get_za3lpa$(1), element_0.vertexIds.get_za3lpa$(2), new Vector3(0, 0, 0));
        faces.add_11rb$(face3);
        var mesh = new Mesh_init(geom, panelMaterial);
        this.uiScene.add(mesh);
      }
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
          destination_0.add_11rb$(new Vector3(item_0.x, item_0.y, item_0.z));
        }
        lineGeom.setFromPoints(copyToArray(destination_0));
        this.wireframe.add(new Line_init(lineGeom, lineMaterial));
      }
    }
    geom.faces = copyToArray(faces);
    geom.computeVertexNormals();
    geom.computeBoundingSphere();
    this.uiScene.add(this.wireframe);
    var boundingSphere = ensureNotNull(geom.boundingSphere);
    this.uiControls.target = boundingSphere.center;
    this.uiControls.update();
  };
  function JsMapperDisplay$showCamImage$lambda(this$JsMapperDisplay, closure$imageData) {
    return function (imageBitmap) {
      var a = this$JsMapperDisplay.width_0 / closure$imageData.width;
      var b = this$JsMapperDisplay.height_0 / closure$imageData.height;
      var scale = Math_0.min(a, b);
      var imgWidth = roundToInt(closure$imageData.width * scale);
      var imgHeight = roundToInt(closure$imageData.height * scale);
      var widthDiff = this$JsMapperDisplay.width_0 - imgWidth | 0;
      var heightDiff = this$JsMapperDisplay.height_0 - imgHeight | 0;
      this$JsMapperDisplay.ui2dCtx.drawImage(imageBitmap, 0.0, 0.0, imageBitmap.width, imageBitmap.height, widthDiff / 2.0, heightDiff / 2.0, this$JsMapperDisplay.width_0 - widthDiff / 2.0, this$JsMapperDisplay.height_0 - heightDiff / 2.0);
      this$JsMapperDisplay.ui2dCtx.strokeStyle = '#006600';
      this$JsMapperDisplay.ui2dCtx.strokeRect(widthDiff / 2.0, heightDiff / 2.0, this$JsMapperDisplay.width_0 - widthDiff / 2.0, this$JsMapperDisplay.height_0 - heightDiff / 2.0);
      return Unit;
    };
  }
  JsMapperDisplay.prototype.showCamImage_u6jj7u$ = function (image) {
    var tmp$;
    this.ui2dCtx.resetTransform();
    var imageDataImage = Kotlin.isType(tmp$ = image, ImageDataImage) ? tmp$ : throwCCE();
    var imageData = imageDataImage.imageData;
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
    var options = o;
    if (image.rowsReversed) {
      options.imageOrientation = 'flipY';
    }
    window.createImageBitmap(imageData, options).then(JsMapperDisplay$showCamImage$lambda(this, imageData));
    this.uiRenderer.render(this.uiScene, this.uiCamera);
  };
  JsMapperDisplay.prototype.close = function () {
    this.frame_0.close();
  };
  function JsMapperDisplay$onClose$lambda() {
    return Unit;
  }
  function JsMapperDisplay$screen$lambda$lambda$lambda$lambda(this$JsMapperDisplay) {
    return function (it) {
      this$JsMapperDisplay.wireframe.position.y = this$JsMapperDisplay.wireframe.position.y + 10;
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
      this$JsMapperDisplay.wireframe.position.y = this$JsMapperDisplay.wireframe.position.y - 10;
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
  function JsMapperDisplay$screen$lambda$lambda(this$JsMapperDisplay) {
    return function ($receiver) {
      button($receiver, void 0, void 0, void 0, void 0, 'mapperUi-up', JsMapperDisplay$screen$lambda$lambda$lambda(this$JsMapperDisplay));
      button($receiver, void 0, void 0, void 0, void 0, 'mapperUi-down', JsMapperDisplay$screen$lambda$lambda$lambda_0(this$JsMapperDisplay));
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
  function JsMapperDisplay$screen$lambda(this$JsMapperDisplay) {
    return function ($receiver) {
      div($receiver, 'mapperUi-controls', JsMapperDisplay$screen$lambda$lambda(this$JsMapperDisplay));
      canvas($receiver, 'mapperUi-2d-canvas', JsMapperDisplay$screen$lambda$lambda_0(this$JsMapperDisplay));
      div($receiver, 'mapperUi-3d-div', JsMapperDisplay$screen$lambda$lambda_1);
      return Unit;
    };
  }
  function JsMapperDisplay$frame$lambda(this$JsMapperDisplay) {
    return function () {
      this$JsMapperDisplay.onClose();
      return Unit;
    };
  }
  function JsMapperDisplay$frame$lambda_0(this$JsMapperDisplay) {
    return function (width, height) {
      this$JsMapperDisplay.resizeTo_0(width, height);
      return Unit;
    };
  }
  JsMapperDisplay.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'JsMapperDisplay',
    interfaces: [MapperDisplay]
  };
  function ImageDataImage(imageData, rowsReversed) {
    if (rowsReversed === void 0)
      rowsReversed = false;
    this.imageData = imageData;
    this.rowsReversed = rowsReversed;
  }
  ImageDataImage.prototype.toMonoBitmap = function () {
    var tmp$, tmp$_0;
    var destBuf = UByteArray_init(Kotlin.imul(this.imageData.width, this.imageData.height));
    var srcBuf = this.imageData.data;
    var srcBytesPerPixel = 4;
    var srcBytesPerRow = Kotlin.imul(this.imageData.width, srcBytesPerPixel);
    var destBytesPerPixel = 1;
    var destBytesPerRow = Kotlin.imul(this.imageData.width, destBytesPerPixel);
    var greenOffset = 1;
    tmp$ = this.imageData.height;
    for (var row = 0; row < tmp$; row++) {
      tmp$_0 = this.imageData.width;
      for (var col = 0; col < tmp$_0; col++) {
        var srcRow = this.rowsReversed ? this.imageData.height - row | 0 : row;
        destBuf.set_2c6cbe$(Kotlin.imul(row, destBytesPerRow) + Kotlin.imul(col, destBytesPerPixel) | 0, new UByte_init(srcBuf[Kotlin.imul(srcRow, srcBytesPerRow) + Kotlin.imul(col, srcBytesPerPixel) + greenOffset | 0]));
      }
    }
    return new MediaDevices$MonoBitmap(this.imageData.width, this.imageData.height, destBuf);
  };
  ImageDataImage.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'ImageDataImage',
    interfaces: [MediaDevices$Image]
  };
  function JsUiDisplay(domContainer) {
    this.domContainer_0 = domContainer;
    this.div_0 = div_0(get_create(document), void 0, JsUiDisplay$div$lambda);
    this.frame_5g5ja2$_0 = this.frame_5g5ja2$_0;
    this.jsApp_0 = null;
  }
  Object.defineProperty(JsUiDisplay.prototype, 'frame_0', {
    get: function () {
      if (this.frame_5g5ja2$_0 == null)
        return throwUPAE('frame');
      return this.frame_5g5ja2$_0;
    },
    set: function (frame) {
      this.frame_5g5ja2$_0 = frame;
    }
  });
  function JsUiDisplay$createApp$lambda(this$JsUiDisplay) {
    return function () {
      this$JsUiDisplay.jsApp_0.close();
      return Unit;
    };
  }
  function JsUiDisplay$createApp$lambda_0(width, height) {
    println('Resize to ' + width + ', ' + height);
    return Unit;
  }
  JsUiDisplay.prototype.createApp_kvdprd$ = function (uiContext) {
    this.frame_0 = this.domContainer_0.getFrame_409ufb$('UI', this.div_0, JsUiDisplay$createApp$lambda(this), JsUiDisplay$createApp$lambda_0);
    this.jsApp_0 = document.createUiApp(this.div_0, uiContext);
  };
  function JsUiDisplay$div$lambda($receiver) {
    return Unit;
  }
  JsUiDisplay.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'JsUiDisplay',
    interfaces: [UiDisplay]
  };
  function Visualizer(sheepModel, dmxUniverse) {
    this.sheepModel_0 = sheepModel;
    this.dmxUniverse_0 = dmxUniverse;
    this.mediaDevices = new FakeMediaDevices(this);
    this.frameListeners_0 = ArrayList_init();
    this.onNewMapper = Visualizer$onNewMapper$lambda;
    this.onNewUi = Visualizer$onNewUi$lambda;
  }
  function Visualizer$start$lambda(this$Visualizer) {
    return function (it) {
      this$Visualizer.onNewMapper();
      return Unit;
    };
  }
  function Visualizer$start$lambda_0(this$Visualizer) {
    return function (it) {
      this$Visualizer.onNewUi();
      return Unit;
    };
  }
  Visualizer.prototype.start = function () {
    initThreeJs(this.sheepModel_0, this.frameListeners_0);
    ensureNotNull(document.getElementById('newMapperButton')).addEventListener('click', Visualizer$start$lambda(this));
    ensureNotNull(document.getElementById('newUiButton')).addEventListener('click', Visualizer$start$lambda_0(this));
  };
  Visualizer.prototype.showPanel_jfju1k$ = function (panel) {
    var pixelCount = 400;
    return new JsPanel(addPanel(panel, pixelCount), pixelCount);
  };
  Visualizer.prototype.addEye_1hma8m$ = function (eye) {
    new MovingHeadView(eye, this.dmxUniverse_0);
  };
  Visualizer.prototype.addFrameListener_9dei3h$ = function (frameListener) {
    this.frameListeners_0.add_11rb$(frameListener);
  };
  Visualizer.prototype.removeFrameListener_9dei3h$ = function (frameListener) {
    this.frameListeners_0.remove_11rb$(frameListener);
  };
  Visualizer.prototype.setMapperRunning_6taknv$ = function (b) {
    setMapperRunning(b);
  };
  function Visualizer$onNewMapper$lambda() {
    return Unit;
  }
  function Visualizer$onNewUi$lambda() {
    return Unit;
  }
  Visualizer.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Visualizer',
    interfaces: []
  };
  function FrameListener() {
  }
  FrameListener.$metadata$ = {
    kind: Kind_INTERFACE,
    simpleName: 'FrameListener',
    interfaces: []
  };
  function JsPanel(jsPanelObj, pixelCount) {
    this.jsPanelObj_0 = jsPanelObj;
    this.pixelCount = pixelCount;
    this.color_1o5p8y$_0 = Color$Companion_getInstance().BLACK;
  }
  JsPanel.prototype.setAllPixelsTo_rny0jj$ = function (color) {
    var tmp$ = this.jsPanelObj_0;
    var tmp$_0 = Color$Companion_getInstance().WHITE;
    var $receiver = new IntRange(0, this.pixelCount);
    var destination = ArrayList_init_0(collectionSizeOrDefault($receiver, 10));
    var tmp$_1;
    tmp$_1 = $receiver.iterator();
    while (tmp$_1.hasNext()) {
      var item = tmp$_1.next();
      destination.add_11rb$(color);
    }
    setPanelColor(tmp$, tmp$_0, copyToArray(destination));
  };
  JsPanel.prototype.setPixelsTo_tmuqsv$ = function (colors) {
    setPanelColor(this.jsPanelObj_0, Color$Companion_getInstance().WHITE, colors);
  };
  Object.defineProperty(JsPanel.prototype, 'color', {
    get: function () {
      return this.color_1o5p8y$_0;
    },
    set: function (value) {
      var tmp$ = this.jsPanelObj_0;
      var $receiver = new IntRange(0, this.pixelCount);
      var destination = ArrayList_init_0(collectionSizeOrDefault($receiver, 10));
      var tmp$_0;
      tmp$_0 = $receiver.iterator();
      while (tmp$_0.hasNext()) {
        var item = tmp$_0.next();
        destination.add_11rb$(value);
      }
      setPanelColor(tmp$, value, copyToArray(destination));
      this.color_1o5p8y$_0 = this.color;
    }
  });
  JsPanel.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'JsPanel',
    interfaces: []
  };
  function JsPixels(jsPanel) {
    this.jsPanel_0 = jsPanel;
    this.count_ky8fwf$_0 = this.jsPanel_0.pixelCount;
  }
  Object.defineProperty(JsPixels.prototype, 'count', {
    get: function () {
      return this.count_ky8fwf$_0;
    }
  });
  JsPixels.prototype.set_tmuqsv$ = function (colors) {
    this.jsPanel_0.setPixelsTo_tmuqsv$(colors);
  };
  JsPixels.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'JsPixels',
    interfaces: [Pixels]
  };
  function MovingHeadView(movingHead, dmxUniverse) {
    this.baseChannel = ensureNotNull(Config$Companion_getInstance().DMX_DEVICES.get_11rb$(movingHead.name));
    this.device = new Shenzarpy(dmxUniverse.reader_sxjeop$(this.baseChannel, 16, MovingHeadView$device$lambda(this)));
    this.movingHeadJs = addMovingHead(movingHead);
  }
  MovingHeadView.prototype.receivedDmxFrame_0 = function () {
    var colorWheelV = this.device.colorWheel;
    var wheelColor = Shenzarpy$WheelColor$Companion_getInstance().get_s8j3t7$(colorWheelV);
    adjustMovingHead(this.movingHeadJs, wheelColor.color, this.device.dimmer, this.device.pan, this.device.tilt);
  };
  function MovingHeadView$device$lambda(this$MovingHeadView) {
    return function () {
      this$MovingHeadView.receivedDmxFrame_0();
      return Unit;
    };
  }
  MovingHeadView.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'MovingHeadView',
    interfaces: []
  };
  function SheepSimulator() {
    this.display = getDisplay();
    this.network = new FakeNetwork(void 0, this.display.forNetwork());
    this.dmxUniverse = new FakeDmxUniverse();
    this.sheepModel = new SheepModel();
    this.showMetas = listOf([new SomeDumbShow$Meta(), new RandomShow$Meta(), new CompositeShow$Meta()]);
    var $receiver = new Visualizer(this.sheepModel, this.dmxUniverse);
    $receiver.onNewMapper = SheepSimulator$visualizer$lambda$lambda($receiver, this);
    $receiver.onNewUi = SheepSimulator$visualizer$lambda$lambda_0(this);
    this.visualizer = $receiver;
    this.pinky = new Pinky(this.sheepModel, this.showMetas, this.network, this.dmxUniverse, this.display.forPinky());
    this.pinkyScope = CoroutineScope(coroutines.Dispatchers.Main);
    this.brainScope = CoroutineScope(coroutines.Dispatchers.Main);
    this.mapperScope = CoroutineScope(coroutines.Dispatchers.Main);
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
            this.result_0 = this.local$this$SheepSimulator.pinky.run(this);
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
  function Coroutine$SheepSimulator$start$lambda$lambda$lambda(closure$brain_0, $receiver_0, controller, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.$controller = controller;
    this.exceptionState_0 = 1;
    this.local$closure$brain = closure$brain_0;
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
  function SheepSimulator$start$lambda$lambda$lambda(closure$brain_0) {
    return function ($receiver_0, continuation_0, suspended) {
      var instance = new Coroutine$SheepSimulator$start$lambda$lambda$lambda(closure$brain_0, $receiver_0, this, continuation_0);
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
  function SheepSimulator$start$lambda$lambda_0(continuation_0, suspended) {
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
            this.local$this$SheepSimulator.sheepModel.load();
            launch(this.local$this$SheepSimulator.pinkyScope, void 0, void 0, SheepSimulator$start$lambda$lambda(this.local$this$SheepSimulator));
            this.local$this$SheepSimulator.visualizer.start();
            var tmp$;
            tmp$ = this.local$this$SheepSimulator.sheepModel.panels.iterator();
            while (tmp$.hasNext()) {
              var element = tmp$.next();
              var this$SheepSimulator = this.local$this$SheepSimulator;
              var jsPanel = this$SheepSimulator.visualizer.showPanel_jfju1k$(element);
              var brain = new Brain(this$SheepSimulator.network, this$SheepSimulator.display.forBrain(), new JsPixels(jsPanel), element);
              launch(this$SheepSimulator.brainScope, void 0, void 0, SheepSimulator$start$lambda$lambda$lambda(brain));
            }

            var tmp$_0;
            tmp$_0 = this.local$this$SheepSimulator.sheepModel.eyes.iterator();
            while (tmp$_0.hasNext()) {
              var element_0 = tmp$_0.next();
              this.local$this$SheepSimulator.visualizer.addEye_1hma8m$(element_0);
              Config$Companion_getInstance().DMX_DEVICES.get_11rb$(element_0.name);
            }

            return doRunBlocking(SheepSimulator$start$lambda$lambda_0);
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
    return doRunBlocking(SheepSimulator$start$lambda(this));
  };
  function SheepSimulator$visualizer$lambda$lambda$lambda$lambda$lambda(closure$it) {
    return function () {
      closure$it.setMapperRunning_6taknv$(false);
      return Unit;
    };
  }
  function Coroutine$SheepSimulator$visualizer$lambda$lambda$lambda(this$SheepSimulator_0, closure$it_0, $receiver_0, controller, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.$controller = controller;
    this.exceptionState_0 = 1;
    this.local$this$SheepSimulator = this$SheepSimulator_0;
    this.local$closure$it = closure$it_0;
  }
  Coroutine$SheepSimulator$visualizer$lambda$lambda$lambda.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$SheepSimulator$visualizer$lambda$lambda$lambda.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$SheepSimulator$visualizer$lambda$lambda$lambda.prototype.constructor = Coroutine$SheepSimulator$visualizer$lambda$lambda$lambda;
  Coroutine$SheepSimulator$visualizer$lambda$lambda$lambda.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            var $receiver = new Mapper(this.local$this$SheepSimulator.network, this.local$this$SheepSimulator.sheepModel, new JsMapperDisplay(new FakeDomContainer()), this.local$closure$it.mediaDevices);
            $receiver.addCloseListener_o14v8n$(SheepSimulator$visualizer$lambda$lambda$lambda$lambda$lambda(this.local$closure$it));
            $receiver.start();
            return $receiver;
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
  function SheepSimulator$visualizer$lambda$lambda$lambda(this$SheepSimulator_0, closure$it_0) {
    return function ($receiver_0, continuation_0, suspended) {
      var instance = new Coroutine$SheepSimulator$visualizer$lambda$lambda$lambda(this$SheepSimulator_0, closure$it_0, $receiver_0, this, continuation_0);
      if (suspended)
        return instance;
      else
        return instance.doResume(null);
    };
  }
  function SheepSimulator$visualizer$lambda$lambda(closure$it, this$SheepSimulator) {
    return function () {
      closure$it.setMapperRunning_6taknv$(true);
      launch(this$SheepSimulator.mapperScope, void 0, void 0, SheepSimulator$visualizer$lambda$lambda$lambda(this$SheepSimulator, closure$it));
      return Unit;
    };
  }
  function Coroutine$SheepSimulator$visualizer$lambda$lambda$lambda_0(this$SheepSimulator_0, $receiver_0, controller, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.$controller = controller;
    this.exceptionState_0 = 1;
    this.local$this$SheepSimulator = this$SheepSimulator_0;
  }
  Coroutine$SheepSimulator$visualizer$lambda$lambda$lambda_0.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$SheepSimulator$visualizer$lambda$lambda$lambda_0.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$SheepSimulator$visualizer$lambda$lambda$lambda_0.prototype.constructor = Coroutine$SheepSimulator$visualizer$lambda$lambda$lambda_0;
  Coroutine$SheepSimulator$visualizer$lambda$lambda$lambda_0.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            return new Ui(this.local$this$SheepSimulator.network, this.local$this$SheepSimulator.pinky.address, new JsUiDisplay(new FakeDomContainer()));
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
  function SheepSimulator$visualizer$lambda$lambda$lambda_0(this$SheepSimulator_0) {
    return function ($receiver_0, continuation_0, suspended) {
      var instance = new Coroutine$SheepSimulator$visualizer$lambda$lambda$lambda_0(this$SheepSimulator_0, $receiver_0, this, continuation_0);
      if (suspended)
        return instance;
      else
        return instance.doResume(null);
    };
  }
  function SheepSimulator$visualizer$lambda$lambda_0(this$SheepSimulator) {
    return function () {
      launch(coroutines.GlobalScope, void 0, void 0, SheepSimulator$visualizer$lambda$lambda$lambda_0(this$SheepSimulator));
      return Unit;
    };
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
  function ColorPickerView(element, onSelect) {
    this.colors = listOf([Color$Companion_getInstance().WHITE, Color$Companion_getInstance().RED, Color$Companion_getInstance().ORANGE, Color$Companion_getInstance().YELLOW, Color$Companion_getInstance().GREEN, Color$Companion_getInstance().BLUE, Color$Companion_getInstance().PURPLE]);
    this.colorButtons_0 = null;
    var colorsDiv = appendElement(element, 'div', ColorPickerView_init$lambda);
    var $receiver = this.colors;
    var destination = ArrayList_init_0(collectionSizeOrDefault($receiver, 10));
    var tmp$;
    tmp$ = $receiver.iterator();
    while (tmp$.hasNext()) {
      var item = tmp$.next();
      var tmp$_0 = destination.add_11rb$;
      var $receiver_0 = new ColorPickerView$ColorButton(item, appendElement(colorsDiv, 'span', ColorPickerView_init$lambda$lambda_1));
      $receiver_0.element.setAttribute('style', 'background-color: ' + $receiver_0.data.toHexString());
      $receiver_0.onSelect = ColorPickerView_init$lambda$lambda$lambda(onSelect);
      tmp$_0.call(destination, $receiver_0);
    }
    this.colorButtons_0 = destination;
    var tmp$_1;
    tmp$_1 = this.colorButtons_0.iterator();
    while (tmp$_1.hasNext()) {
      var element_0 = tmp$_1.next();
      element_0.allButtons = this.colorButtons_0;
    }
  }
  ColorPickerView.prototype.pickRandom = function () {
    ensureNotNull(random(this.colorButtons_0)).onClick();
  };
  ColorPickerView.prototype.setColor_58xt5s$ = function (color) {
    var tmp$;
    tmp$ = this.colorButtons_0.iterator();
    while (tmp$.hasNext()) {
      var colorButton = tmp$.next();
      colorButton.setSelected_6taknv$(equals(colorButton.color, color));
    }
  };
  function ColorPickerView$ColorButton(color, element) {
    Button.call(this, color, element);
    this.color = color;
  }
  ColorPickerView$ColorButton.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'ColorButton',
    interfaces: [Button]
  };
  function ColorPickerView_init$lambda$lambda($receiver) {
    appendText($receiver, 'Colors: ');
    return Unit;
  }
  function ColorPickerView_init$lambda$lambda_0($receiver) {
    return Unit;
  }
  function ColorPickerView_init$lambda($receiver) {
    $receiver.className = 'colorsDiv';
    appendElement($receiver, 'b', ColorPickerView_init$lambda$lambda);
    appendElement($receiver, 'br', ColorPickerView_init$lambda$lambda_0);
    return Unit;
  }
  function ColorPickerView_init$lambda$lambda_1($receiver) {
    return Unit;
  }
  function ColorPickerView_init$lambda$lambda$lambda(closure$onSelect) {
    return function (it) {
      closure$onSelect(it);
      return Unit;
    };
  }
  ColorPickerView.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'ColorPickerView',
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
  FakeDomContainer.prototype.getFrame_409ufb$ = function (name, content, onClose, onResize) {
    return document.createFakeClientDevice(name, content, onClose, onResize);
  };
  FakeDomContainer.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'FakeDomContainer',
    interfaces: [DomContainer]
  };
  function createUiApp(elementId, uiContext) {
    return document.createUiApp(elementId, uiContext);
  }
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
    return promise(coroutines.GlobalScope, void 0, void 0, doRunBlocking$lambda(block));
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
  function getDisplay() {
    return new JsDisplay();
  }
  function getTimeMillis() {
    return Kotlin.Long.fromNumber(Date.now());
  }
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
  package$baaahs.BrainDisplay = BrainDisplay;
  Dmx.Universe = Dmx$Universe;
  Dmx.Buffer = Dmx$Buffer;
  Dmx.DeviceType = Dmx$DeviceType;
  package$baaahs.Dmx = Dmx;
  package$baaahs.FakeDmxUniverse = FakeDmxUniverse;
  $$importsForInline$$['kotlinx-coroutines-core'] = $module$kotlinx_coroutines_core;
  Mapper.BrainMapper = Mapper$BrainMapper;
  package$baaahs.Mapper = Mapper;
  package$baaahs.MapperDisplay = MapperDisplay;
  MediaDevices.Camera = MediaDevices$Camera;
  MediaDevices.Image = MediaDevices$Image;
  MediaDevices.MonoBitmap = MediaDevices$MonoBitmap;
  package$baaahs.MediaDevices = MediaDevices;
  Network.Link = Network$Link;
  Network.Address = Network$Address;
  Network.UdpListener = Network$UdpListener;
  Network.TcpConnection = Network$TcpConnection;
  Network.TcpListener = Network$TcpListener;
  Network.TcpServerSocketListener = Network$TcpServerSocketListener;
  package$baaahs.Network = Network;
  FakeNetwork.FakeTcpConnection = FakeNetwork$FakeTcpConnection;
  package$baaahs.FakeNetwork = FakeNetwork;
  Pinky.BeatProvider = Pinky$BeatProvider;
  package$baaahs.Pinky = Pinky;
  package$baaahs.ShowRunner = ShowRunner;
  package$baaahs.ColorPicker = ColorPicker;
  package$baaahs.RemoteBrain = RemoteBrain;
  Object.defineProperty(Ports, 'Companion', {
    get: Ports$Companion_getInstance
  });
  package$baaahs.Ports = Ports;
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
  Object.defineProperty(Type, 'PINKY_PONG', {
    get: Type$PINKY_PONG_getInstance
  });
  Object.defineProperty(Type, 'Companion', {
    get: Type$Companion_getInstance
  });
  package$baaahs.Type = Type;
  package$baaahs.parse_fqrh44$ = parse;
  Object.defineProperty(BrainHelloMessage, 'Companion', {
    get: BrainHelloMessage$Companion_getInstance
  });
  package$baaahs.BrainHelloMessage = BrainHelloMessage;
  Object.defineProperty(BrainShaderMessage, 'Companion', {
    get: BrainShaderMessage$Companion_getInstance
  });
  package$baaahs.BrainShaderMessage = BrainShaderMessage;
  Object.defineProperty(MapperHelloMessage, 'Companion', {
    get: MapperHelloMessage$Companion_getInstance
  });
  package$baaahs.MapperHelloMessage = MapperHelloMessage;
  Object.defineProperty(BrainIdRequest, 'Companion', {
    get: BrainIdRequest$Companion_getInstance
  });
  package$baaahs.BrainIdRequest = BrainIdRequest;
  Object.defineProperty(BrainIdResponse, 'Companion', {
    get: BrainIdResponse$Companion_getInstance
  });
  package$baaahs.BrainIdResponse = BrainIdResponse;
  Object.defineProperty(PinkyPongMessage, 'Companion', {
    get: PinkyPongMessage$Companion_getInstance
  });
  package$baaahs.PinkyPongMessage = PinkyPongMessage;
  package$baaahs.Message = Message;
  PubSub.Origin = PubSub$Origin;
  PubSub.Observer = PubSub$Observer;
  Object.defineProperty(PubSub, 'Companion', {
    get: PubSub$Companion_getInstance
  });
  PubSub.Topic = PubSub$Topic;
  PubSub.Listener = PubSub$Listener;
  PubSub.TopicInfo = PubSub$TopicInfo;
  PubSub.Connection = PubSub$Connection;
  PubSub.Server = PubSub$Server;
  PubSub.Client = PubSub$Client;
  package$baaahs.PubSub = PubSub;
  Object.defineProperty(ShaderType, 'SOLID', {
    get: ShaderType$SOLID_getInstance
  });
  Object.defineProperty(ShaderType, 'PIXEL', {
    get: ShaderType$PIXEL_getInstance
  });
  Object.defineProperty(ShaderType, 'SINE_WAVE', {
    get: ShaderType$SINE_WAVE_getInstance
  });
  Object.defineProperty(ShaderType, 'COMPOSITOR', {
    get: ShaderType$COMPOSITOR_getInstance
  });
  Object.defineProperty(ShaderType, 'Companion', {
    get: ShaderType$Companion_getInstance
  });
  package$baaahs.ShaderType = ShaderType;
  Object.defineProperty(Shader, 'Companion', {
    get: Shader$Companion_getInstance
  });
  package$baaahs.Shader = Shader;
  package$baaahs.ShaderBuffer = ShaderBuffer;
  package$baaahs.ShaderImpl = ShaderImpl;
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
  package$baaahs.ShowMeta = ShowMeta;
  package$baaahs.Show = Show;
  Object.defineProperty(package$baaahs, 'Topics', {
    get: Topics_getInstance
  });
  package$baaahs.Ui = Ui;
  package$baaahs.UiContext = UiContext;
  package$baaahs.UiDisplay = UiDisplay;
  package$baaahs.ByteArrayWriter_init_za3lpa$ = ByteArrayWriter_init;
  package$baaahs.ByteArrayWriter = ByteArrayWriter;
  package$baaahs.ByteArrayReader = ByteArrayReader;
  Object.defineProperty(CompositorShader, 'Companion', {
    get: CompositorShader$Companion_getInstance
  });
  var package$shaders = package$baaahs.shaders || (package$baaahs.shaders = {});
  package$shaders.CompositorShader = CompositorShader;
  package$shaders.CompositorShaderImpl = CompositorShaderImpl;
  package$shaders.PixelBuf = PixelBuf;
  package$shaders.CompositorShaderBuffer = CompositorShaderBuffer;
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
  package$shaders.PixelShader = PixelShader;
  package$shaders.PixelShaderImpl = PixelShaderImpl;
  package$shaders.PixelShaderBuffer = PixelShaderBuffer;
  Object.defineProperty(SineWaveShader, 'Companion', {
    get: SineWaveShader$Companion_getInstance
  });
  package$shaders.SineWaveShader = SineWaveShader;
  package$shaders.SineWaveShaderImpl = SineWaveShaderImpl;
  Object.defineProperty(SineWaveShaderBuffer, 'Companion', {
    get: SineWaveShaderBuffer$Companion_getInstance
  });
  package$shaders.SineWaveShaderBuffer = SineWaveShaderBuffer;
  Object.defineProperty(SolidShader, 'Companion', {
    get: SolidShader$Companion_getInstance
  });
  package$shaders.SolidShader = SolidShader;
  package$shaders.SolidShaderImpl = SolidShaderImpl;
  package$shaders.SolidShaderBuffer = SolidShaderBuffer;
  CompositeShow.Meta = CompositeShow$Meta;
  var package$shows = package$baaahs.shows || (package$baaahs.shows = {});
  package$shows.CompositeShow = CompositeShow;
  RandomShow.Meta = RandomShow$Meta;
  package$shows.RandomShow = RandomShow;
  SomeDumbShow.Meta = SomeDumbShow$Meta;
  package$shows.SomeDumbShow = SomeDumbShow;
  package$baaahs.random_2p1efm$ = random;
  package$baaahs.random_hhb8gh$ = random_0;
  package$baaahs.toRadians_mx4ult$ = toRadians;
  package$baaahs.randomDelay_za3lpa$ = randomDelay;
  Object.defineProperty(logger, 'Companion', {
    get: logger$Companion_getInstance
  });
  package$baaahs.logger = logger;
  package$baaahs.JsDisplay = JsDisplay;
  package$baaahs.JsNetworkDisplay = JsNetworkDisplay;
  package$baaahs.JsPinkyDisplay = JsPinkyDisplay;
  package$baaahs.JsBrainDisplay = JsBrainDisplay;
  FakeMediaDevices.FakeCamera = FakeMediaDevices$FakeCamera;
  package$baaahs.FakeMediaDevices = FakeMediaDevices;
  package$baaahs.JsMapperDisplay = JsMapperDisplay;
  package$baaahs.ImageDataImage = ImageDataImage;
  package$baaahs.JsUiDisplay = JsUiDisplay;
  package$baaahs.Visualizer = Visualizer;
  package$baaahs.FrameListener = FrameListener;
  package$baaahs.JsPanel = JsPanel;
  package$baaahs.JsPixels = JsPixels;
  package$baaahs.MovingHeadView = MovingHeadView;
  package$baaahs.SheepSimulator = SheepSimulator;
  package$baaahs.get_disabled_ejp6nk$ = get_disabled;
  package$baaahs.set_disabled_juh0kr$ = set_disabled;
  package$baaahs.forEach_dokpt5$ = forEach;
  package$baaahs.clear_u75qir$ = clear_0;
  package$baaahs.Button = Button;
  package$baaahs.ColorPickerView = ColorPickerView;
  DomContainer.Frame = DomContainer$Frame;
  package$baaahs.DomContainer = DomContainer;
  package$baaahs.FakeDomContainer = FakeDomContainer;
  package$baaahs.createUiApp_khyez$ = createUiApp;
  package$baaahs.doRunBlocking_g2bo5h$ = doRunBlocking;
  package$baaahs.getResource_61zpoe$ = getResource;
  package$baaahs.getDisplay = getDisplay;
  package$baaahs.getTimeMillis = getTimeMillis;
  Color$$serializer.prototype.patch_mynpiu$ = GeneratedSerializer.prototype.patch_mynpiu$;
  FakeNetwork$FakeTcpConnection.prototype.send_kq3aw3$ = Network$TcpConnection.prototype.send_kq3aw3$;
  FakeNetwork$FakeLink.prototype.sendUdp_bkw8fl$ = Network$Link.prototype.sendUdp_bkw8fl$;
  FakeNetwork$FakeLink.prototype.broadcastUdp_ecsl0t$ = Network$Link.prototype.broadcastUdp_ecsl0t$;
  Kotlin.defineModule('sparklemotion', _);
  return _;
}(typeof sparklemotion === 'undefined' ? {} : sparklemotion, kotlin, this['kotlinx-coroutines-core'], this['kotlinx-serialization-runtime-js'], this['kotlinx-html-js']);

//# sourceMappingURL=sparklemotion.js.map
