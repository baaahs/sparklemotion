if (typeof kotlin === 'undefined') {
  throw new Error("Error loading module 'play'. Its dependency 'kotlin' was not found. Please, check whether 'kotlin' is loaded prior to 'play'.");
}
if (typeof this['kotlinx-coroutines-core'] === 'undefined') {
  throw new Error("Error loading module 'play'. Its dependency 'kotlinx-coroutines-core' was not found. Please, check whether 'kotlinx-coroutines-core' is loaded prior to 'play'.");
}
var play = function (_, Kotlin, $module$kotlinx_coroutines_core) {
  'use strict';
  var Kind_INTERFACE = Kotlin.Kind.INTERFACE;
  var throwUPAE = Kotlin.throwUPAE;
  var coroutines = $module$kotlinx_coroutines_core.kotlinx.coroutines;
  var Random = Kotlin.kotlin.random.Random;
  var delay = $module$kotlinx_coroutines_core.kotlinx.coroutines.delay_s8cxhz$;
  var Unit = Kotlin.kotlin.Unit;
  var COROUTINE_SUSPENDED = Kotlin.kotlin.coroutines.intrinsics.COROUTINE_SUSPENDED;
  var CoroutineImpl = Kotlin.kotlin.coroutines.CoroutineImpl;
  var launch = $module$kotlinx_coroutines_core.kotlinx.coroutines.launch_s496o7$;
  var L60000 = Kotlin.Long.fromInt(60000);
  var Kind_CLASS = Kotlin.Kind.CLASS;
  var toByte = Kotlin.toByte;
  var Exception_init = Kotlin.kotlin.Exception_init_pdl1vj$;
  var toString = Kotlin.kotlin.text.toString_dqglrj$;
  var numberToInt = Kotlin.numberToInt;
  var Kind_OBJECT = Kotlin.Kind.OBJECT;
  var Pair = Kotlin.kotlin.Pair;
  var mapOf = Kotlin.kotlin.collections.mapOf_qfcya0$;
  var math = Kotlin.kotlin.math;
  var println = Kotlin.kotlin.io.println_s8jyv4$;
  var L200000 = Kotlin.Long.fromInt(200000);
  var L1000 = Kotlin.Long.fromInt(1000);
  var L10000 = Kotlin.Long.fromInt(10000);
  var L1 = Kotlin.Long.ONE;
  var toList = Kotlin.kotlin.collections.toList_7wnvza$;
  var L50 = Kotlin.Long.fromInt(50);
  var L0 = Kotlin.Long.ZERO;
  var equals = Kotlin.equals;
  var ensureNotNull = Kotlin.ensureNotNull;
  var Enum = Kotlin.kotlin.Enum;
  var throwISE = Kotlin.throwISE;
  var Regex_init = Kotlin.kotlin.text.Regex_init_61zpoe$;
  var split = Kotlin.kotlin.text.split_ip8yn$;
  var joinToString = Kotlin.kotlin.collections.joinToString_fmv235$;
  var toInt = Kotlin.kotlin.text.toInt_pdl1vz$;
  var arrayListOf = Kotlin.kotlin.collections.arrayListOf_i5x0yv$;
  var until = Kotlin.kotlin.ranges.until_dqglrj$;
  var IntRange = Kotlin.kotlin.ranges.IntRange;
  var toMutableList = Kotlin.kotlin.collections.toMutableList_4c7yge$;
  var Random_0 = Kotlin.kotlin.random.Random_za3lpa$;
  var toShort = Kotlin.toShort;
  var get_indices = Kotlin.kotlin.text.get_indices_gw00vp$;
  var copyOf = Kotlin.kotlin.collections.copyOf_mrm5p$;
  var toChar = Kotlin.toChar;
  var toBoxedChar = Kotlin.toBoxedChar;
  var StringBuilder_init = Kotlin.kotlin.text.StringBuilder_init_za3lpa$;
  var unboxChar = Kotlin.unboxChar;
  var promise = $module$kotlinx_coroutines_core.kotlinx.coroutines.promise_pda6u4$;
  var clear = Kotlin.kotlin.dom.clear_asww5s$;
  var appendText = Kotlin.kotlin.dom.appendText_46n0ku$;
  var appendElement = Kotlin.kotlin.dom.appendElement_ldvnw0$;
  var listOf = Kotlin.kotlin.collections.listOf_i5x0yv$;
  var addClass = Kotlin.kotlin.dom.addClass_hhb33f$;
  FakeDmxUniverse.prototype = Object.create(Dmx$Universe.prototype);
  FakeDmxUniverse.prototype.constructor = FakeDmxUniverse;
  Cat.prototype = Object.create(Animal.prototype);
  Cat.prototype.constructor = Cat;
  Dog.prototype = Object.create(Animal.prototype);
  Dog.prototype.constructor = Dog;
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
  Shenzarpy$WheelColor.prototype = Object.create(Enum.prototype);
  Shenzarpy$WheelColor.prototype.constructor = Shenzarpy$WheelColor;
  Shenzarpy$Channel.prototype = Object.create(Enum.prototype);
  Shenzarpy$Channel.prototype.constructor = Shenzarpy$Channel;
  Shenzarpy.prototype = Object.create(Dmx$DeviceType.prototype);
  Shenzarpy.prototype.constructor = Shenzarpy;
  ShaderType.prototype = Object.create(Enum.prototype);
  ShaderType.prototype.constructor = ShaderType;
  SolidShaderBuffer.prototype = Object.create(ShaderBuffer.prototype);
  SolidShaderBuffer.prototype.constructor = SolidShaderBuffer;
  PixelShaderBuffer.prototype = Object.create(ShaderBuffer.prototype);
  PixelShaderBuffer.prototype.constructor = PixelShaderBuffer;
  function Brain() {
  }
  Brain.$metadata$ = {
    kind: Kind_INTERFACE,
    simpleName: 'Brain',
    interfaces: []
  };
  function SimBrain(network, display, jsPanel, illicitPanelHint) {
    this.network_0 = network;
    this.display_0 = display;
    this.jsPanel_0 = jsPanel;
    this.illicitPanelHint_0 = illicitPanelHint;
    this.link_sq3vvv$_0 = this.link_sq3vvv$_0;
    this.receivingInstructions_0 = false;
  }
  Object.defineProperty(SimBrain.prototype, 'link_0', {
    get: function () {
      if (this.link_sq3vvv$_0 == null)
        return throwUPAE('link');
      return this.link_sq3vvv$_0;
    },
    set: function (link) {
      this.link_sq3vvv$_0 = link;
    }
  });
  function Coroutine$SimBrain$start$lambda(this$SimBrain_0, $receiver_0, controller, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.$controller = controller;
    this.exceptionState_0 = 1;
    this.local$this$SimBrain = this$SimBrain_0;
  }
  Coroutine$SimBrain$start$lambda.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$SimBrain$start$lambda.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$SimBrain$start$lambda.prototype.constructor = Coroutine$SimBrain$start$lambda;
  Coroutine$SimBrain$start$lambda.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            var timeMillis = Random.Default.nextInt() % 1000;
            this.state_0 = 2;
            this.result_0 = delay(Kotlin.Long.fromInt(timeMillis), this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 1:
            throw this.exception_0;
          case 2:
            this.state_0 = 3;
            this.result_0 = this.local$this$SimBrain.run(this);
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
  function SimBrain$start$lambda(this$SimBrain_0) {
    return function ($receiver_0, continuation_0, suspended) {
      var instance = new Coroutine$SimBrain$start$lambda(this$SimBrain_0, $receiver_0, this, continuation_0);
      if (suspended)
        return instance;
      else
        return instance.doResume(null);
    };
  }
  SimBrain.prototype.start = function () {
    launch(coroutines.GlobalScope, void 0, void 0, SimBrain$start$lambda(this));
  };
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
            this.$this.link_0.listen_nmsgsy$(Ports$Companion_getInstance().BRAIN, this.$this);
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
  SimBrain.prototype.run = function (continuation_0, suspended) {
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
              this.$this.link_0.broadcast_ecsl0t$(Ports$Companion_getInstance().PINKY, new BrainHelloMessage(this.$this.illicitPanelHint_0.name));
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
  SimBrain.prototype.sendHello_0 = function (continuation_0, suspended) {
    var instance = new Coroutine$sendHello_0(this, continuation_0);
    if (suspended)
      return instance;
    else
      return instance.doResume(null);
  };
  SimBrain.prototype.receive_cm0rz4$ = function (fromAddress, bytes) {
    var tmp$;
    var message = parse(bytes);
    if (Kotlin.isType(message, BrainShaderMessage)) {
      tmp$ = message.shaderBuffer;
      if (Kotlin.isType(tmp$, SolidShaderBuffer))
        this.jsPanel_0.setAllPixelsTo_rny0jj$(message.shaderBuffer.color);
      else if (Kotlin.isType(tmp$, PixelShaderBuffer))
        this.jsPanel_0.setPixelsTo_ux15y7$(message.shaderBuffer.colors);
    }
     else if (Kotlin.isType(message, BrainIdRequest))
      this.link_0.send_bkw8fl$(fromAddress, message.port, new BrainIdResponse(''));
  };
  SimBrain.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'SimBrain',
    interfaces: [Network$Listener, Brain]
  };
  function Color(red, green, blue) {
    Color$Companion_getInstance();
    this.red = red;
    this.green = green;
    this.blue = blue;
  }
  Color.prototype.serialize_ep8mow$ = function (writer) {
    writer.writeByte_s8j3t7$(toByte(this.red & 255));
    writer.writeByte_s8j3t7$(toByte(this.green & 255));
    writer.writeByte_s8j3t7$(toByte(this.blue & 255));
  };
  Object.defineProperty(Color.prototype, 'redF', {
    get: function () {
      return this.red / 255;
    }
  });
  Object.defineProperty(Color.prototype, 'greenF', {
    get: function () {
      return this.green / 255;
    }
  });
  Object.defineProperty(Color.prototype, 'blueF', {
    get: function () {
      return this.blue / 255;
    }
  });
  Color.prototype.toInt = function () {
    return this.red << 16 & 16711680 | this.green << 8 & 65280 | this.blue & 255;
  };
  Color.prototype.toHexString = function () {
    return this.toHexString_s8ev3n$(this.red) + this.toHexString_s8ev3n$(this.green) + this.toHexString_s8ev3n$(this.blue);
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
  var Math_0 = Math;
  Color.prototype.withSaturation_mx4ult$ = function (saturation) {
    var desaturation = 1 - saturation;
    var b = this.red + numberToInt((255 - this.red | 0) * desaturation) | 0;
    var tmp$ = Math_0.min(255, b);
    var b_0 = this.green + numberToInt((255 - this.green | 0) * desaturation) | 0;
    var tmp$_0 = Math_0.min(255, b_0);
    var b_1 = this.blue + numberToInt((255 - this.blue | 0) * desaturation) | 0;
    return new Color(tmp$, tmp$_0, Math_0.min(255, b_1));
  };
  Color.prototype.distanceTo_rny0jj$ = function (other) {
    var dist = this.square_0(other.redF - this.redF) + this.square_0(other.greenF - this.greenF) + this.square_0(other.blueF - this.blueF);
    var x = dist / 3;
    return Math_0.sqrt(x);
  };
  Color.prototype.square_0 = function (f) {
    return f * f;
  };
  function Color$Companion() {
    Color$Companion_instance = this;
    this.BLACK = new Color(0, 0, 0);
    this.WHITE = new Color(255, 255, 255);
    this.RED = new Color(255, 0, 0);
    this.ORANGE = new Color(255, 127, 0);
    this.YELLOW = new Color(255, 255, 0);
    this.GREEN = new Color(0, 255, 0);
    this.BLUE = new Color(0, 0, 255);
    this.PURPLE = new Color(200, 0, 212);
  }
  Color$Companion.prototype.random = function () {
    return new Color(Random.Default.nextInt() & 255, Random.Default.nextInt() & 255, Random.Default.nextInt() & 255);
  };
  Color$Companion.prototype.parse_c4pr8w$ = function (reader) {
    return new Color(reader.readByte() & 255, reader.readByte() & 255, reader.readByte() & 255);
  };
  Color$Companion.prototype.from_za3lpa$ = function (i) {
    return new Color(i >> 16 & 255, i >> 8 & 255, i & 255);
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
  Color.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Color',
    interfaces: []
  };
  Color.prototype.component1 = function () {
    return this.red;
  };
  Color.prototype.component2 = function () {
    return this.green;
  };
  Color.prototype.component3 = function () {
    return this.blue;
  };
  Color.prototype.copy_qt1dr2$ = function (red, green, blue) {
    return new Color(red === void 0 ? this.red : red, green === void 0 ? this.green : green, blue === void 0 ? this.blue : blue);
  };
  Color.prototype.toString = function () {
    return 'Color(red=' + Kotlin.toString(this.red) + (', green=' + Kotlin.toString(this.green)) + (', blue=' + Kotlin.toString(this.blue)) + ')';
  };
  Color.prototype.hashCode = function () {
    var result = 0;
    result = result * 31 + Kotlin.hashCode(this.red) | 0;
    result = result * 31 + Kotlin.hashCode(this.green) | 0;
    result = result * 31 + Kotlin.hashCode(this.blue) | 0;
    return result;
  };
  Color.prototype.equals = function (other) {
    return this === other || (other !== null && (typeof other === 'object' && (Object.getPrototypeOf(this) === Object.getPrototypeOf(other) && (Kotlin.equals(this.red, other.red) && Kotlin.equals(this.green, other.green) && Kotlin.equals(this.blue, other.blue)))));
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
  function MapperDisplay() {
  }
  MapperDisplay.$metadata$ = {
    kind: Kind_INTERFACE,
    simpleName: 'MapperDisplay',
    interfaces: []
  };
  function Dmx() {
  }
  function Dmx$Universe() {
    this.channelsOut_z0wri6$_0 = new Int8Array(512);
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
  var ArrayList_init = Kotlin.kotlin.collections.ArrayList_init_287e2$;
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
  var arrayCopy = Kotlin.kotlin.collections.arrayCopy;
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
  FakeDmxUniverse.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'FakeDmxUniverse',
    interfaces: [Dmx$Universe]
  };
  function ThingWithMass() {
  }
  ThingWithMass.$metadata$ = {
    kind: Kind_INTERFACE,
    simpleName: 'ThingWithMass',
    interfaces: []
  };
  function Animal(age) {
    this.age = age;
  }
  Animal.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Animal',
    interfaces: []
  };
  function Cat(age) {
    Animal.call(this, age);
  }
  Cat.prototype.weightInKilograms = function () {
    return 2.0 * this.age;
  };
  Cat.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Cat',
    interfaces: [ThingWithMass, Animal]
  };
  function Dog(age) {
    Animal.call(this, age);
  }
  Dog.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Dog',
    interfaces: [Animal]
  };
  function Glass(height, diameter, fullness) {
    this.height = height;
    this.diameter = diameter;
    this.fullness = fullness;
  }
  Glass.prototype.weightInKilograms = function () {
    return this.computeVolume() / 1000;
  };
  Glass.prototype.computeVolume = function () {
    return this.diameter / 2 * (this.diameter / 2) * math.PI * this.height;
  };
  Glass.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Glass',
    interfaces: [ThingWithMass]
  };
  function kevmoMain() {
    new Cat(3);
    var kitty = new Cat(5);
    var myGlass = new Glass(3.5, 4.0, 0.3);
    println('volume of the glass is ' + myGlass.computeVolume());
    var yourglass = new Glass(6.0, 12.0, 0.7);
    println('your glass contains ' + yourglass.computeVolume() + ' milliliters');
    var totalWeight = kitty.weightInKilograms() + myGlass.weightInKilograms() + yourglass.weightInKilograms();
    println('The weightInKilograms of our cats and glasses is: ' + totalWeight);
  }
  var main;
  function get_main() {
    if (main == null)
      return throwUPAE('main');
    return main;
  }
  function set_main(main_0) {
    main = main_0;
  }
  function Main() {
    this.display = getDisplay();
    this.network = new FakeNetwork(void 0, this.display.forNetwork());
    this.dmxUniverse = new FakeDmxUniverse();
    this.sheepModel = new SheepModel();
    this.pinky = new Pinky(this.sheepModel, this.network, this.dmxUniverse, this.display.forPinky());
    this.mapper = new Mapper(this.network, this.display.forMapper());
    this.visualizer = new Visualizer(this.sheepModel, this.dmxUniverse);
  }
  function Coroutine$Main$start$lambda(continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.exceptionState_0 = 1;
  }
  Coroutine$Main$start$lambda.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$Main$start$lambda.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$Main$start$lambda.prototype.constructor = Coroutine$Main$start$lambda;
  Coroutine$Main$start$lambda.prototype.doResume = function () {
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
  function Main$start$lambda(continuation_0, suspended) {
    var instance = new Coroutine$Main$start$lambda(continuation_0);
    if (suspended)
      return instance;
    else
      return instance.doResume(null);
  }
  Main.prototype.start = function () {
    this.sheepModel.load();
    this.mapper.start();
    this.pinky.start();
    this.visualizer.start();
    var tmp$;
    tmp$ = this.sheepModel.panels.iterator();
    while (tmp$.hasNext()) {
      var element = tmp$.next();
      var jsPanel = this.visualizer.showPanel_jfju1k$(element);
      (new SimBrain(this.network, this.display.forBrain(), jsPanel, element)).start();
    }
    var tmp$_0;
    tmp$_0 = this.sheepModel.eyes.iterator();
    while (tmp$_0.hasNext()) {
      var element_0 = tmp$_0.next();
      this.visualizer.addEye_1hma8m$(element_0);
      Config$Companion_getInstance().DMX_DEVICES.get_11rb$(element_0.name);
    }
    doRunBlocking(Main$start$lambda);
  };
  Main.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Main',
    interfaces: []
  };
  function Mapper(network, display) {
    this.network = network;
    this.display = display;
    this.link_tktc8n$_0 = this.link_tktc8n$_0;
    this.isRunning_0 = false;
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
  function Mapper$start$lambda(this$Mapper) {
    return function () {
      if (!this$Mapper.isRunning_0) {
        this$Mapper.isRunning_0 = true;
        launch(coroutines.GlobalScope, void 0, void 0, Mapper$start$lambda$lambda(this$Mapper));
      }
      return Unit;
    };
  }
  function Mapper$start$lambda_0(this$Mapper) {
    return function () {
      if (this$Mapper.isRunning_0) {
        this$Mapper.isRunning_0 = false;
      }
      return Unit;
    };
  }
  Mapper.prototype.start = function () {
    this.link_0 = this.network.link();
    this.link_0.listen_nmsgsy$(Ports$Companion_getInstance().MAPPER, this);
    this.display.onStart = Mapper$start$lambda(this);
    this.display.onStop = Mapper$start$lambda_0(this);
  };
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
            this.$this.link_0.broadcast_ecsl0t$(Ports$Companion_getInstance().PINKY, new MapperHelloMessage(this.$this.isRunning_0));
            this.state_0 = 2;
            this.result_0 = delay(L1000, this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 1:
            throw this.exception_0;
          case 2:
            var tmp$ = this.$this.link_0;
            var tmp$_0 = Ports$Companion_getInstance().BRAIN;
            var $receiver = new SolidShaderBuffer();
            $receiver.color = Color$Companion_getInstance().BLACK;
            tmp$.broadcast_ecsl0t$(tmp$_0, new BrainShaderMessage($receiver));
            this.$this.link_0.broadcast_ecsl0t$(Ports$Companion_getInstance().PINKY, new MapperHelloMessage(this.$this.isRunning_0));
            this.state_0 = 3;
            this.result_0 = delay(L1000, this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 3:
            var tmp$_1 = this.$this.link_0;
            var tmp$_2 = Ports$Companion_getInstance().BRAIN;
            var $receiver_0 = new SolidShaderBuffer();
            $receiver_0.color = Color$Companion_getInstance().BLACK;
            tmp$_1.broadcast_ecsl0t$(tmp$_2, new BrainShaderMessage($receiver_0));
            this.$this.link_0.broadcast_ecsl0t$(Ports$Companion_getInstance().BRAIN, new BrainIdRequest(Ports$Companion_getInstance().MAPPER));
            this.state_0 = 4;
            continue;
          case 4:
            if (!this.$this.isRunning_0) {
              this.state_0 = 6;
              continue;
            }

            this.$this.link_0.broadcast_ecsl0t$(Ports$Companion_getInstance().PINKY, new MapperHelloMessage(this.$this.isRunning_0));
            this.state_0 = 5;
            this.result_0 = delay(L10000, this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 5:
            this.state_0 = 4;
            continue;
          case 6:
            this.$this.link_0.broadcast_ecsl0t$(Ports$Companion_getInstance().PINKY, new MapperHelloMessage(this.$this.isRunning_0));
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
  Mapper.prototype.receive_cm0rz4$ = function (fromAddress, bytes) {
    var message = parse(bytes);
    if (Kotlin.isType(message, BrainIdResponse)) {
      var tmp$ = this.link_0;
      var tmp$_0 = Ports$Companion_getInstance().BRAIN;
      var $receiver = new SolidShaderBuffer();
      $receiver.color = Color$Companion_getInstance().WHITE;
      tmp$.send_bkw8fl$(fromAddress, tmp$_0, new BrainShaderMessage($receiver));
    }
     else if (Kotlin.isType(message, PinkyPongMessage)) {
      var tmp$_1;
      tmp$_1 = message.brainIds.iterator();
      while (tmp$_1.hasNext()) {
        var element = tmp$_1.next();
        println('id = ' + element);
      }
    }
  };
  Mapper.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Mapper',
    interfaces: [Network$Listener]
  };
  function Network() {
  }
  function Network$Link() {
  }
  Network$Link.prototype.send_bkw8fl$ = function (toAddress, port, message) {
    this.send_z62edq$(toAddress, port, message.toBytes());
  };
  Network$Link.prototype.broadcast_ecsl0t$ = function (port, message) {
    this.broadcast_3fbn1q$(port, message.toBytes());
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
  function Network$Listener() {
  }
  Network$Listener.$metadata$ = {
    kind: Kind_INTERFACE,
    simpleName: 'Listener',
    interfaces: []
  };
  Network.$metadata$ = {
    kind: Kind_INTERFACE,
    simpleName: 'Network',
    interfaces: []
  };
  var HashMap_init = Kotlin.kotlin.collections.HashMap_init_q3lmfv$;
  function FakeNetwork(networkDelay, display) {
    if (networkDelay === void 0)
      networkDelay = L1;
    this.networkDelay_0 = networkDelay;
    this.display_0 = display;
    this.listeners_0 = HashMap_init();
    this.listenersByPort_0 = HashMap_init();
    this.nextAddress_0 = 45071;
  }
  FakeNetwork.prototype.link = function () {
    var tmp$;
    var address = new FakeAddress((tmp$ = this.nextAddress_0, this.nextAddress_0 = tmp$ + 1 | 0, tmp$));
    return new FakeNetwork$FakeLink(this, address);
  };
  FakeNetwork.prototype.listen_0 = function (address, port, listener) {
    this.listeners_0.put_xwzc9p$(new Pair(address, port), listener);
    var $receiver = this.listenersByPort_0;
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
    portListeners.add_11rb$(listener);
  };
  FakeNetwork.prototype.send_0 = function (fromAddress, toAddress, port, bytes) {
    var listener = this.listeners_0.get_11rb$(new Pair(toAddress, port));
    if (listener != null)
      this.transmit_0(fromAddress, listener, bytes);
  };
  FakeNetwork.prototype.broadcast_0 = function (fromAddress, port, bytes) {
    var tmp$;
    if ((tmp$ = this.listenersByPort_0.get_11rb$(port)) != null) {
      var tmp$_0;
      tmp$_0 = tmp$.iterator();
      while (tmp$_0.hasNext()) {
        var element = tmp$_0.next();
        this.transmit_0(fromAddress, element, bytes);
      }
    }
  };
  function Coroutine$FakeNetwork$transmit$lambda(this$FakeNetwork_0, closure$listener_0, closure$fromAddress_0, closure$bytes_0, $receiver_0, controller, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.$controller = controller;
    this.exceptionState_0 = 1;
    this.local$this$FakeNetwork = this$FakeNetwork_0;
    this.local$closure$listener = closure$listener_0;
    this.local$closure$fromAddress = closure$fromAddress_0;
    this.local$closure$bytes = closure$bytes_0;
  }
  Coroutine$FakeNetwork$transmit$lambda.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$FakeNetwork$transmit$lambda.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$FakeNetwork$transmit$lambda.prototype.constructor = Coroutine$FakeNetwork$transmit$lambda;
  Coroutine$FakeNetwork$transmit$lambda.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.state_0 = 2;
            this.result_0 = delay(this.local$this$FakeNetwork.networkDelay_0, this);
            if (this.result_0 === COROUTINE_SUSPENDED)
              return COROUTINE_SUSPENDED;
            continue;
          case 1:
            throw this.exception_0;
          case 2:
            if (Random.Default.nextInt() % 10 !== 1) {
              this.local$this$FakeNetwork.display_0.receivedPacket();
              return this.local$closure$listener.receive_cm0rz4$(this.local$closure$fromAddress, this.local$closure$bytes), Unit;
            }
             else {
              return this.local$this$FakeNetwork.display_0.droppedPacket(), Unit;
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
  function FakeNetwork$transmit$lambda(this$FakeNetwork_0, closure$listener_0, closure$fromAddress_0, closure$bytes_0) {
    return function ($receiver_0, continuation_0, suspended) {
      var instance = new Coroutine$FakeNetwork$transmit$lambda(this$FakeNetwork_0, closure$listener_0, closure$fromAddress_0, closure$bytes_0, $receiver_0, this, continuation_0);
      if (suspended)
        return instance;
      else
        return instance.doResume(null);
    };
  }
  FakeNetwork.prototype.transmit_0 = function (fromAddress, listener, bytes) {
    launch(coroutines.GlobalScope, void 0, void 0, FakeNetwork$transmit$lambda(this, listener, fromAddress, bytes));
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
  FakeNetwork$FakeLink.prototype.listen_nmsgsy$ = function (port, listener) {
    this.$outer.listen_0(this.myAddress, port, listener);
  };
  FakeNetwork$FakeLink.prototype.send_z62edq$ = function (toAddress, port, bytes) {
    this.$outer.send_0(this.myAddress, toAddress, port, bytes);
  };
  FakeNetwork$FakeLink.prototype.broadcast_3fbn1q$ = function (port, bytes) {
    this.$outer.broadcast_0(this.myAddress, port, bytes);
  };
  FakeNetwork$FakeLink.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'FakeLink',
    interfaces: [Network$Link]
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
  var LinkedHashMap_init = Kotlin.kotlin.collections.LinkedHashMap_init_q3lmfv$;
  function Pinky(sheepModel, network, dmxUniverse, display) {
    this.sheepModel = sheepModel;
    this.network = network;
    this.dmxUniverse = dmxUniverse;
    this.display = display;
    this.link_e4s3v3$_0 = this.link_e4s3v3$_0;
    this.brains_0 = LinkedHashMap_init();
    this.beatProvider_0 = new Pinky$BeatProvider(this, 120.0);
    this.mapperIsRunning_0 = false;
    this.brainsChanged_0 = true;
  }
  Object.defineProperty(Pinky.prototype, 'link_0', {
    get: function () {
      if (this.link_e4s3v3$_0 == null)
        return throwUPAE('link');
      return this.link_e4s3v3$_0;
    },
    set: function (link) {
      this.link_e4s3v3$_0 = link;
    }
  });
  Pinky.prototype.run = function () {
    this.link_0 = this.network.link();
    this.link_0.listen_nmsgsy$(Ports$Companion_getInstance().PINKY, this);
  };
  function Coroutine$Pinky$start$lambda(this$Pinky_0, $receiver_0, controller, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.$controller = controller;
    this.exceptionState_0 = 1;
    this.local$this$Pinky = this$Pinky_0;
  }
  Coroutine$Pinky$start$lambda.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$Pinky$start$lambda.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$Pinky$start$lambda.prototype.constructor = Coroutine$Pinky$start$lambda;
  Coroutine$Pinky$start$lambda.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            return this.local$this$Pinky.run(), Unit;
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
  function Pinky$start$lambda(this$Pinky_0) {
    return function ($receiver_0, continuation_0, suspended) {
      var instance = new Coroutine$Pinky$start$lambda(this$Pinky_0, $receiver_0, this, continuation_0);
      if (suspended)
        return instance;
      else
        return instance.doResume(null);
    };
  }
  function Coroutine$Pinky$start$lambda_0(this$Pinky_0, $receiver_0, controller, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.$controller = controller;
    this.exceptionState_0 = 1;
    this.local$this$Pinky = this$Pinky_0;
  }
  Coroutine$Pinky$start$lambda_0.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$Pinky$start$lambda_0.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$Pinky$start$lambda_0.prototype.constructor = Coroutine$Pinky$start$lambda_0;
  Coroutine$Pinky$start$lambda_0.prototype.doResume = function () {
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
  function Pinky$start$lambda_0(this$Pinky_0) {
    return function ($receiver_0, continuation_0, suspended) {
      var instance = new Coroutine$Pinky$start$lambda_0(this$Pinky_0, $receiver_0, this, continuation_0);
      if (suspended)
        return instance;
      else
        return instance.doResume(null);
    };
  }
  function Coroutine$Pinky$start$lambda_1(this$Pinky_0, $receiver_0, controller, continuation_0) {
    CoroutineImpl.call(this, continuation_0);
    this.$controller = controller;
    this.exceptionState_0 = 1;
    this.local$this$Pinky = this$Pinky_0;
    this.local$showContext = void 0;
    this.local$show = void 0;
  }
  Coroutine$Pinky$start$lambda_1.$metadata$ = {
    kind: Kotlin.Kind.CLASS,
    simpleName: null,
    interfaces: [CoroutineImpl]
  };
  Coroutine$Pinky$start$lambda_1.prototype = Object.create(CoroutineImpl.prototype);
  Coroutine$Pinky$start$lambda_1.prototype.constructor = Coroutine$Pinky$start$lambda_1;
  Coroutine$Pinky$start$lambda_1.prototype.doResume = function () {
    do
      try {
        switch (this.state_0) {
          case 0:
            this.local$showContext = new ShowRunner(this.local$this$Pinky.display, toList(this.local$this$Pinky.brains_0.values), this.local$this$Pinky.dmxUniverse);
            this.local$show = new SomeDumbShow(this.local$this$Pinky.sheepModel, this.local$showContext);
            this.state_0 = 2;
            continue;
          case 1:
            throw this.exception_0;
          case 2:
            if (!this.local$this$Pinky.mapperIsRunning_0) {
              if (this.local$this$Pinky.brainsChanged_0) {
                this.local$showContext = new ShowRunner(this.local$this$Pinky.display, toList(this.local$this$Pinky.brains_0.values), this.local$this$Pinky.dmxUniverse);
                this.local$show = new SomeDumbShow(this.local$this$Pinky.sheepModel, this.local$showContext);
                this.local$this$Pinky.brainsChanged_0 = false;
              }
              this.local$show.nextFrame();
              this.local$showContext.send_6qu7we$(this.local$this$Pinky.link_0);
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
  function Pinky$start$lambda_1(this$Pinky_0) {
    return function ($receiver_0, continuation_0, suspended) {
      var instance = new Coroutine$Pinky$start$lambda_1(this$Pinky_0, $receiver_0, this, continuation_0);
      if (suspended)
        return instance;
      else
        return instance.doResume(null);
    };
  }
  Pinky.prototype.start = function () {
    launch(coroutines.GlobalScope, void 0, void 0, Pinky$start$lambda(this));
    launch(coroutines.GlobalScope, void 0, void 0, Pinky$start$lambda_0(this));
    launch(coroutines.GlobalScope, void 0, void 0, Pinky$start$lambda_1(this));
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
    var instance = new Coroutine$run_1(this, continuation_0);
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
    interfaces: [Network$Listener]
  };
  function ShowRunner(pinkyDisplay, brains, dmxUniverse) {
    this.pinkyDisplay_0 = pinkyDisplay;
    this.brains_0 = brains;
    this.dmxUniverse_0 = dmxUniverse;
    this.brainBuffers_0 = ArrayList_init();
  }
  ShowRunner.prototype.getColorPicker = function () {
    return new ColorPicker(this.pinkyDisplay_0);
  };
  ShowRunner.prototype.getSolidShaderBuffer_jfju1k$ = function (panel) {
    var $receiver = this.brains_0;
    var firstOrNull$result;
    firstOrNull$break: do {
      var tmp$;
      tmp$ = $receiver.iterator();
      while (tmp$.hasNext()) {
        var element = tmp$.next();
        if (equals(element.panelName, panel.name)) {
          firstOrNull$result = element;
          break firstOrNull$break;
        }
      }
      firstOrNull$result = null;
    }
     while (false);
    var remoteBrain = firstOrNull$result;
    var buffer = new SolidShaderBuffer();
    this.brainBuffers_0.add_11rb$(new Pair(remoteBrain, buffer));
    return buffer;
  };
  ShowRunner.prototype.getPixelShaderBuffer_jfju1k$ = function (panel) {
    var $receiver = this.brains_0;
    var firstOrNull$result;
    firstOrNull$break: do {
      var tmp$;
      tmp$ = $receiver.iterator();
      while (tmp$.hasNext()) {
        var element = tmp$.next();
        if (equals(element.panelName, panel.name)) {
          firstOrNull$result = element;
          break firstOrNull$break;
        }
      }
      firstOrNull$result = null;
    }
     while (false);
    var remoteBrain = firstOrNull$result;
    var buffer = new PixelShaderBuffer();
    this.brainBuffers_0.add_11rb$(new Pair(remoteBrain, buffer));
    return buffer;
  };
  ShowRunner.prototype.getDmxBuffer_vux9f0$ = function (baseChannel, channelCount) {
    return this.dmxUniverse_0.writer_vux9f0$(baseChannel, channelCount);
  };
  ShowRunner.prototype.getMovingHeadBuffer_1hma8m$ = function (movingHead) {
    var baseChannel = ensureNotNull(Config$Companion_getInstance().DMX_DEVICES.get_11rb$(movingHead.name));
    return new Shenzarpy(this.getDmxBuffer_vux9f0$(baseChannel, 16));
  };
  ShowRunner.prototype.send_6qu7we$ = function (link) {
    var tmp$;
    tmp$ = this.brainBuffers_0.iterator();
    while (tmp$.hasNext()) {
      var element = tmp$.next();
      var remoteBrain = element.first;
      var shaderBuffer = element.second;
      if (remoteBrain != null) {
        link.send_bkw8fl$(remoteBrain.address, Ports$Companion_getInstance().BRAIN, new BrainShaderMessage(shaderBuffer));
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
    switch (Type$values()[reader.readByte()].name) {
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
  function BrainShaderMessage(shaderBuffer) {
    BrainShaderMessage$Companion_getInstance();
    Message.call(this, Type$BRAIN_PANEL_SHADE_getInstance());
    this.shaderBuffer = shaderBuffer;
  }
  function BrainShaderMessage$Companion() {
    BrainShaderMessage$Companion_instance = this;
  }
  BrainShaderMessage$Companion.prototype.parse_c4pr8w$ = function (reader) {
    var tmp$;
    var shaderType = ShaderType$values()[reader.readInt()];
    switch (shaderType.name) {
      case 'SOLID':
        tmp$ = SolidShaderBuffer$Companion_getInstance().parse_c4pr8w$(reader);
        break;
      case 'PIXEL':
        tmp$ = PixelShaderBuffer$Companion_getInstance().parse_c4pr8w$(reader);
        break;
      default:tmp$ = Kotlin.noWhenBranchMatched();
        break;
    }
    var shaderBuffer = tmp$;
    return new BrainShaderMessage(shaderBuffer);
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
    writer.writeInt_za3lpa$(this.shaderBuffer.type.ordinal);
    this.shaderBuffer.serialize_ep8mow$(writer);
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
  var throwCCE = Kotlin.throwCCE;
  var trim = Kotlin.kotlin.text.trim_gw00vp$;
  var toDouble = Kotlin.kotlin.text.toDouble_pdl1vz$;
  var collectionSizeOrDefault = Kotlin.kotlin.collections.collectionSizeOrDefault_ba2ldo$;
  var ArrayList_init_0 = Kotlin.kotlin.collections.ArrayList_init_ww73n8$;
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
            destination_1.add_11rb$(toInt(item_1) - 1 | 0);
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
            destination_2.add_11rb$(toInt(item_2) - 1 | 0);
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
  }
  var rangeTo = Kotlin.kotlin.ranges.rangeTo_38ydlf$;
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
    Shenzarpy$WheelColor$RED_instance = new Shenzarpy$WheelColor('RED', 0, Color$Companion_getInstance().from_za3lpa$(12721698));
    Shenzarpy$WheelColor$ORANGE_instance = new Shenzarpy$WheelColor('ORANGE', 1, Color$Companion_getInstance().from_za3lpa$(15434294));
    Shenzarpy$WheelColor$AQUAMARINE_instance = new Shenzarpy$WheelColor('AQUAMARINE', 2, Color$Companion_getInstance().from_za3lpa$(8174724));
    Shenzarpy$WheelColor$DEEP_GREEN_instance = new Shenzarpy$WheelColor('DEEP_GREEN', 3, Color$Companion_getInstance().from_za3lpa$(1212719));
    Shenzarpy$WheelColor$LIGHT_GREEN_instance = new Shenzarpy$WheelColor('LIGHT_GREEN', 4, Color$Companion_getInstance().from_za3lpa$(10469695));
    Shenzarpy$WheelColor$LAVENDER_instance = new Shenzarpy$WheelColor('LAVENDER', 5, Color$Companion_getInstance().from_za3lpa$(9401515));
    Shenzarpy$WheelColor$PINK_instance = new Shenzarpy$WheelColor('PINK', 6, Color$Companion_getInstance().from_za3lpa$(15434114));
    Shenzarpy$WheelColor$YELLOW_instance = new Shenzarpy$WheelColor('YELLOW', 7, Color$Companion_getInstance().from_za3lpa$(16706356));
    Shenzarpy$WheelColor$MAGENTA_instance = new Shenzarpy$WheelColor('MAGENTA', 8, Color$Companion_getInstance().from_za3lpa$(14750594));
    Shenzarpy$WheelColor$CYAN_instance = new Shenzarpy$WheelColor('CYAN', 9, Color$Companion_getInstance().from_za3lpa$(1812456));
    Shenzarpy$WheelColor$CTO2_instance = new Shenzarpy$WheelColor('CTO2', 10, Color$Companion_getInstance().from_za3lpa$(16041553));
    Shenzarpy$WheelColor$CTO1_instance = new Shenzarpy$WheelColor('CTO1', 11, Color$Companion_getInstance().from_za3lpa$(16046218));
    Shenzarpy$WheelColor$CTB_instance = new Shenzarpy$WheelColor('CTB', 12, Color$Companion_getInstance().from_za3lpa$(9947064));
    Shenzarpy$WheelColor$DARK_BLUE_instance = new Shenzarpy$WheelColor('DARK_BLUE', 13, Color$Companion_getInstance().from_za3lpa$(545175));
    Shenzarpy$WheelColor$WHITE_instance = new Shenzarpy$WheelColor('WHITE', 14, Color$Companion_getInstance().from_za3lpa$(16777215));
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
  Shenzarpy.prototype.set_0 = function ($receiver, channel, value) {
    this.buffer_0.set_6t1wet$(channel.ordinal, value);
  };
  Shenzarpy.prototype.get_0 = function ($receiver, channel) {
    return this.buffer_0.get_za3lpa$(channel.ordinal);
  };
  Shenzarpy.prototype.closestColorFor_rny0jj$ = function (color) {
    var bestMatch = {v: Shenzarpy$WheelColor$WHITE_getInstance()};
    var bestDistance = {v: 1.0};
    var $receiver = Shenzarpy$WheelColor$values();
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
  Object.defineProperty(Shenzarpy.prototype, 'colorWheel', {
    get: function () {
      return this.get_0(this.buffer_0, Shenzarpy$Channel$COLOR_WHEEL_getInstance());
    },
    set: function (value) {
      this.set_0(this.buffer_0, Shenzarpy$Channel$COLOR_WHEEL_getInstance(), value);
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
  Shenzarpy.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Shenzarpy',
    interfaces: [Dmx$DeviceType]
  };
  function ShaderType(name, ordinal) {
    Enum.call(this);
    this.name$ = name;
    this.ordinal$ = ordinal;
  }
  function ShaderType_initFields() {
    ShaderType_initFields = function () {
    };
    ShaderType$SOLID_instance = new ShaderType('SOLID', 0);
    ShaderType$PIXEL_instance = new ShaderType('PIXEL', 1);
  }
  var ShaderType$SOLID_instance;
  function ShaderType$SOLID_getInstance() {
    ShaderType_initFields();
    return ShaderType$SOLID_instance;
  }
  var ShaderType$PIXEL_instance;
  function ShaderType$PIXEL_getInstance() {
    ShaderType_initFields();
    return ShaderType$PIXEL_instance;
  }
  ShaderType.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'ShaderType',
    interfaces: [Enum]
  };
  function ShaderType$values() {
    return [ShaderType$SOLID_getInstance(), ShaderType$PIXEL_getInstance()];
  }
  ShaderType.values = ShaderType$values;
  function ShaderType$valueOf(name) {
    switch (name) {
      case 'SOLID':
        return ShaderType$SOLID_getInstance();
      case 'PIXEL':
        return ShaderType$PIXEL_getInstance();
      default:throwISE('No enum constant baaahs.ShaderType.' + name);
    }
  }
  ShaderType.valueOf_61zpoe$ = ShaderType$valueOf;
  function ShaderBuffer(type) {
    this.type = type;
  }
  ShaderBuffer.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'ShaderBuffer',
    interfaces: []
  };
  function SolidShaderBuffer() {
    SolidShaderBuffer$Companion_getInstance();
    ShaderBuffer.call(this, ShaderType$SOLID_getInstance());
    this.color = Color$Companion_getInstance().WHITE;
  }
  SolidShaderBuffer.prototype.serialize_ep8mow$ = function (writer) {
    this.color.serialize_ep8mow$(writer);
  };
  function SolidShaderBuffer$Companion() {
    SolidShaderBuffer$Companion_instance = this;
  }
  SolidShaderBuffer$Companion.prototype.parse_c4pr8w$ = function (reader) {
    var buf = new SolidShaderBuffer();
    buf.color = Color$Companion_getInstance().parse_c4pr8w$(reader);
    return buf;
  };
  SolidShaderBuffer$Companion.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'Companion',
    interfaces: []
  };
  var SolidShaderBuffer$Companion_instance = null;
  function SolidShaderBuffer$Companion_getInstance() {
    if (SolidShaderBuffer$Companion_instance === null) {
      new SolidShaderBuffer$Companion();
    }
    return SolidShaderBuffer$Companion_instance;
  }
  SolidShaderBuffer.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'SolidShaderBuffer',
    interfaces: [ShaderBuffer]
  };
  function PixelShaderBuffer() {
    PixelShaderBuffer$Companion_getInstance();
    ShaderBuffer.call(this, ShaderType$PIXEL_getInstance());
    this.fakeyTerribleHardCodedNumberOfPixels = 1337;
    var $receiver = new IntRange(0, this.fakeyTerribleHardCodedNumberOfPixels);
    var destination = ArrayList_init_0(collectionSizeOrDefault($receiver, 10));
    var tmp$;
    tmp$ = $receiver.iterator();
    while (tmp$.hasNext()) {
      var item = tmp$.next();
      destination.add_11rb$(Color$Companion_getInstance().WHITE);
    }
    this.colors = toMutableList(destination);
  }
  PixelShaderBuffer.prototype.serialize_ep8mow$ = function (writer) {
    writer.writeInt_za3lpa$(this.colors.size);
    var tmp$;
    tmp$ = this.colors.iterator();
    while (tmp$.hasNext()) {
      var element = tmp$.next();
      element.serialize_ep8mow$(writer);
    }
  };
  function PixelShaderBuffer$Companion() {
    PixelShaderBuffer$Companion_instance = this;
  }
  PixelShaderBuffer$Companion.prototype.parse_c4pr8w$ = function (reader) {
    var buf = new PixelShaderBuffer();
    var tmp$;
    tmp$ = until(0, reader.readInt()).iterator();
    while (tmp$.hasNext()) {
      var element = tmp$.next();
      buf.colors.set_wxm5ur$(element, Color$Companion_getInstance().parse_c4pr8w$(reader));
    }
    return buf;
  };
  PixelShaderBuffer$Companion.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'Companion',
    interfaces: []
  };
  var PixelShaderBuffer$Companion_instance = null;
  function PixelShaderBuffer$Companion_getInstance() {
    if (PixelShaderBuffer$Companion_instance === null) {
      new PixelShaderBuffer$Companion();
    }
    return PixelShaderBuffer$Companion_instance;
  }
  PixelShaderBuffer.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'PixelShaderBuffer',
    interfaces: [ShaderBuffer]
  };
  function Show() {
  }
  Show.$metadata$ = {
    kind: Kind_INTERFACE,
    simpleName: 'Show',
    interfaces: []
  };
  function SomeDumbShow(sheepModel, showRunner) {
    this.colorPicker = showRunner.getColorPicker();
    var $receiver = sheepModel.allPanels;
    var destination = ArrayList_init_0(collectionSizeOrDefault($receiver, 10));
    var tmp$;
    tmp$ = $receiver.iterator();
    while (tmp$.hasNext()) {
      var item = tmp$.next();
      destination.add_11rb$(showRunner.getPixelShaderBuffer_jfju1k$(item));
    }
    this.pixelShaderBuffers = destination;
    var $receiver_0 = sheepModel.eyes;
    var destination_0 = ArrayList_init_0(collectionSizeOrDefault($receiver_0, 10));
    var tmp$_0;
    tmp$_0 = $receiver_0.iterator();
    while (tmp$_0.hasNext()) {
      var item_0 = tmp$_0.next();
      destination_0.add_11rb$(showRunner.getMovingHeadBuffer_1hma8m$(item_0));
    }
    this.movingHeadBuffers = destination_0;
    println('Creating new SomeDumbShow, we have ' + this.pixelShaderBuffers.size + ' buffers');
  }
  var checkIndexOverflow = Kotlin.kotlin.collections.checkIndexOverflow_za3lpa$;
  SomeDumbShow.prototype.nextFrame = function () {
    var seed = Random_0(0);
    var tmp$;
    tmp$ = this.pixelShaderBuffers.iterator();
    while (tmp$.hasNext()) {
      var element = tmp$.next();
      var baseSaturation = seed.nextFloat();
      var panelColor = seed.nextFloat() < 0.1 ? Color$Companion_getInstance().random() : this.colorPicker.color;
      var tmp$_0, tmp$_0_0;
      var index = 0;
      tmp$_0 = element.colors.iterator();
      while (tmp$_0.hasNext()) {
        var item = tmp$_0.next();
        element.colors.set_wxm5ur$(checkIndexOverflow((tmp$_0_0 = index, index = tmp$_0_0 + 1 | 0, tmp$_0_0)), this.desaturateRandomishly_0(baseSaturation, seed, panelColor));
      }
    }
    var tmp$_1;
    tmp$_1 = this.movingHeadBuffers.iterator();
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
  SomeDumbShow.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'SomeDumbShow',
    interfaces: [Show]
  };
  function Visualizer(sheepModel, dmxUniverse) {
    this.sheepModel_0 = sheepModel;
    this.dmxUniverse_0 = dmxUniverse;
  }
  Visualizer.prototype.start = function () {
    initThreeJs(this.sheepModel_0);
  };
  Visualizer.prototype.showPanel_jfju1k$ = function (panel) {
    return new JsPanel(addPanel(panel));
  };
  Visualizer.prototype.addEye_1hma8m$ = function (eye) {
    new MovingHeadView(eye, this.dmxUniverse_0);
  };
  Visualizer.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Visualizer',
    interfaces: []
  };
  function JsPanel(jsPanelObj) {
    this.jsPanelObj_0 = jsPanelObj;
    this.color_1o5p8y$_0 = Color$Companion_getInstance().BLACK;
  }
  JsPanel.prototype.setAllPixelsTo_rny0jj$ = function (color) {
    var tmp$ = this.jsPanelObj_0;
    var tmp$_0 = Color$Companion_getInstance().WHITE;
    var $receiver = new IntRange(0, 300);
    var destination = ArrayList_init_0(collectionSizeOrDefault($receiver, 10));
    var tmp$_1;
    tmp$_1 = $receiver.iterator();
    while (tmp$_1.hasNext()) {
      var item = tmp$_1.next();
      destination.add_11rb$(color);
    }
    setPanelColor(tmp$, tmp$_0, destination);
  };
  JsPanel.prototype.setPixelsTo_ux15y7$ = function (colors) {
    setPanelColor(this.jsPanelObj_0, Color$Companion_getInstance().WHITE, colors);
  };
  Object.defineProperty(JsPanel.prototype, 'color', {
    get: function () {
      return this.color_1o5p8y$_0;
    },
    set: function (value) {
      var tmp$ = this.jsPanelObj_0;
      var $receiver = new IntRange(0, 300);
      var destination = ArrayList_init_0(collectionSizeOrDefault($receiver, 10));
      var tmp$_0;
      tmp$_0 = $receiver.iterator();
      while (tmp$_0.hasNext()) {
        var item = tmp$_0.next();
        destination.add_11rb$(value);
      }
      setPanelColor(tmp$, value, toList(destination));
      this.color_1o5p8y$_0 = this.color;
    }
  });
  JsPanel.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'JsPanel',
    interfaces: []
  };
  function MovingHeadView(movingHead, dmxUniverse) {
    this.movingHead_0 = movingHead;
    this.baseChannel = ensureNotNull(Config$Companion_getInstance().DMX_DEVICES.get_11rb$(this.movingHead_0.name));
    this.device = new Shenzarpy(dmxUniverse.reader_sxjeop$(this.baseChannel, 16, MovingHeadView$device$lambda(this)));
    this.movingHeadJs = addMovingHead(this.movingHead_0);
  }
  MovingHeadView.prototype.receivedDmxFrame_0 = function () {
    var colorWheelV = this.device.colorWheel;
    var wheelColor = Shenzarpy$WheelColor$values()[colorWheelV];
    adjustMovingHead(this.movingHeadJs, wheelColor.color, this.device.pan, this.device.tilt);
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
  ByteArrayReader.prototype.readString = function () {
    var length = this.readInt();
    var buf = StringBuilder_init(length);
    for (var i = 0; i < length; i++) {
      buf.append_s8itvh$(unboxChar(this.readChar()));
    }
    return buf.toString();
  };
  ByteArrayReader.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'ByteArrayReader',
    interfaces: []
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
  JsDisplay.prototype.forMapper = function () {
    return new JsMapperDisplay(ensureNotNull(document.getElementById('mapperView')));
  };
  JsDisplay.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'JsDisplay',
    interfaces: [Display]
  };
  function JsNetworkDisplay(document) {
    this.packetsReceivedSpan_0 = ensureNotNull(document.getElementById('networkPacketsReceived'));
    this.packetsDroppedSpan_0 = ensureNotNull(document.getElementById('networkPacketsDropped'));
    this.packetsReceived_0 = 0;
    this.packetsDropped_0 = 0;
  }
  JsNetworkDisplay.prototype.receivedPacket = function () {
    var tmp$;
    clear(this.packetsReceivedSpan_0);
    appendText(this.packetsReceivedSpan_0, (tmp$ = this.packetsReceived_0, this.packetsReceived_0 = tmp$ + 1 | 0, tmp$).toString());
  };
  JsNetworkDisplay.prototype.droppedPacket = function () {
    var tmp$;
    clear(this.packetsDroppedSpan_0);
    appendText(this.packetsDroppedSpan_0, (tmp$ = this.packetsDropped_0, this.packetsDropped_0 = tmp$ + 1 | 0, tmp$).toString());
  };
  JsNetworkDisplay.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'JsNetworkDisplay',
    interfaces: [NetworkDisplay]
  };
  function JsPinkyDisplay(element) {
    this.color_y02qw7$_0 = null;
    this.consoleDiv_0 = null;
    this.beat1_0 = null;
    this.beat2_0 = null;
    this.beat3_0 = null;
    this.beat4_0 = null;
    this.beats_0 = null;
    this.colorButtons_0 = null;
    this.brainCountDiv_0 = null;
    appendText(element, 'Brains online: ');
    this.brainCountDiv_0 = appendElement(element, 'span', JsPinkyDisplay_init$lambda);
    var beatsDiv = appendElement(element, 'div', JsPinkyDisplay_init$lambda_0);
    this.beat1_0 = appendElement(beatsDiv, 'span', JsPinkyDisplay_init$lambda_1);
    this.beat2_0 = appendElement(beatsDiv, 'span', JsPinkyDisplay_init$lambda_2);
    this.beat3_0 = appendElement(beatsDiv, 'span', JsPinkyDisplay_init$lambda_3);
    this.beat4_0 = appendElement(beatsDiv, 'span', JsPinkyDisplay_init$lambda_4);
    this.beats_0 = listOf([this.beat1_0, this.beat2_0, this.beat3_0, this.beat4_0]);
    var colorsDiv = appendElement(element, 'div', JsPinkyDisplay_init$lambda_5);
    this.colorButtons_0 = listOf([new JsPinkyDisplay$ColorButton(Color$Companion_getInstance().WHITE, appendElement(colorsDiv, 'span', JsPinkyDisplay_init$lambda_6)), new JsPinkyDisplay$ColorButton(Color$Companion_getInstance().RED, appendElement(colorsDiv, 'span', JsPinkyDisplay_init$lambda_7)), new JsPinkyDisplay$ColorButton(Color$Companion_getInstance().ORANGE, appendElement(colorsDiv, 'span', JsPinkyDisplay_init$lambda_8)), new JsPinkyDisplay$ColorButton(Color$Companion_getInstance().YELLOW, appendElement(colorsDiv, 'span', JsPinkyDisplay_init$lambda_9)), new JsPinkyDisplay$ColorButton(Color$Companion_getInstance().GREEN, appendElement(colorsDiv, 'span', JsPinkyDisplay_init$lambda_10)), new JsPinkyDisplay$ColorButton(Color$Companion_getInstance().BLUE, appendElement(colorsDiv, 'span', JsPinkyDisplay_init$lambda_11)), new JsPinkyDisplay$ColorButton(Color$Companion_getInstance().PURPLE, appendElement(colorsDiv, 'span', JsPinkyDisplay_init$lambda_12))]);
    var tmp$;
    tmp$ = this.colorButtons_0.iterator();
    while (tmp$.hasNext()) {
      var element_0 = tmp$.next();
      element_0.allButtons = this.colorButtons_0;
      element_0.onSelect = JsPinkyDisplay_init$lambda$lambda_1(this);
    }
    ensureNotNull(random(this.colorButtons_0)).select();
    this.consoleDiv_0 = appendElement(element, 'div', JsPinkyDisplay_init$lambda_13);
    this.brainCount_tt9c5b$_0 = 0;
    this.beat_o13evy$_0 = 0;
  }
  Object.defineProperty(JsPinkyDisplay.prototype, 'color', {
    get: function () {
      return this.color_y02qw7$_0;
    },
    set: function (color) {
      this.color_y02qw7$_0 = color;
    }
  });
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
  function JsPinkyDisplay$ColorButton(color, button) {
    this.color = color;
    this.button = button;
    this.allButtons_nmnvz3$_0 = this.allButtons_nmnvz3$_0;
    this.onSelect = null;
    this.button.setAttribute('style', 'background-color: #' + this.color.toHexString());
    this.button.addEventListener('click', JsPinkyDisplay$JsPinkyDisplay$ColorButton_init$lambda(this));
  }
  Object.defineProperty(JsPinkyDisplay$ColorButton.prototype, 'allButtons', {
    get: function () {
      if (this.allButtons_nmnvz3$_0 == null)
        return throwUPAE('allButtons');
      return this.allButtons_nmnvz3$_0;
    },
    set: function (allButtons) {
      this.allButtons_nmnvz3$_0 = allButtons;
    }
  });
  JsPinkyDisplay$ColorButton.prototype.select = function () {
    var tmp$;
    var tmp$_0;
    tmp$_0 = this.allButtons.iterator();
    while (tmp$_0.hasNext()) {
      var element = tmp$_0.next();
      clear_0(element.button.classList);
    }
    this.button.classList.add('selected');
    (tmp$ = this.onSelect) != null ? tmp$(this.color) : null;
  };
  function JsPinkyDisplay$JsPinkyDisplay$ColorButton_init$lambda(this$ColorButton) {
    return function (it) {
      this$ColorButton.select();
      return Unit;
    };
  }
  JsPinkyDisplay$ColorButton.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'ColorButton',
    interfaces: []
  };
  function JsPinkyDisplay_init$lambda($receiver) {
    return Unit;
  }
  function JsPinkyDisplay_init$lambda$lambda($receiver) {
    appendText($receiver, 'Beats: ');
    return Unit;
  }
  function JsPinkyDisplay_init$lambda_0($receiver) {
    $receiver.id = 'beatsDiv';
    appendElement($receiver, 'b', JsPinkyDisplay_init$lambda$lambda);
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
  function JsPinkyDisplay_init$lambda$lambda_0($receiver) {
    appendText($receiver, 'Colors: ');
    return Unit;
  }
  function JsPinkyDisplay_init$lambda_5($receiver) {
    $receiver.id = 'colorsDiv';
    appendElement($receiver, 'b', JsPinkyDisplay_init$lambda$lambda_0);
    return Unit;
  }
  function JsPinkyDisplay_init$lambda_6($receiver) {
    return Unit;
  }
  function JsPinkyDisplay_init$lambda_7($receiver) {
    return Unit;
  }
  function JsPinkyDisplay_init$lambda_8($receiver) {
    return Unit;
  }
  function JsPinkyDisplay_init$lambda_9($receiver) {
    return Unit;
  }
  function JsPinkyDisplay_init$lambda_10($receiver) {
    return Unit;
  }
  function JsPinkyDisplay_init$lambda_11($receiver) {
    return Unit;
  }
  function JsPinkyDisplay_init$lambda_12($receiver) {
    return Unit;
  }
  function JsPinkyDisplay_init$lambda$lambda_1(this$JsPinkyDisplay) {
    return function (it) {
      this$JsPinkyDisplay.color = it;
      return Unit;
    };
  }
  function JsPinkyDisplay_init$lambda_13($receiver) {
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
  function JsMapperDisplay(element) {
    this.element = element;
    this.startButton_0 = ensureNotNull(ensureNotNull(this.element.ownerDocument).getElementById('mapperStartButton'));
    this.stopButton_0 = ensureNotNull(ensureNotNull(this.element.ownerDocument).getElementById('mapperStopButton'));
    this.onStart_4s3d1r$_0 = null;
    this.onStop_dwgv5t$_0 = null;
    this.updateButtons_0(false);
    this.startButton_0.addEventListener('click', JsMapperDisplay_init$lambda(this));
    this.stopButton_0.addEventListener('click', JsMapperDisplay_init$lambda_0(this));
  }
  Object.defineProperty(JsMapperDisplay.prototype, 'onStart', {
    get: function () {
      return this.onStart_4s3d1r$_0;
    },
    set: function (onStart) {
      this.onStart_4s3d1r$_0 = onStart;
    }
  });
  Object.defineProperty(JsMapperDisplay.prototype, 'onStop', {
    get: function () {
      return this.onStop_dwgv5t$_0;
    },
    set: function (onStop) {
      this.onStop_dwgv5t$_0 = onStop;
    }
  });
  JsMapperDisplay.prototype.updateButtons_0 = function (isRunning) {
    set_disabled(this.startButton_0, isRunning);
    set_disabled(this.stopButton_0, !isRunning);
  };
  function JsMapperDisplay_init$lambda(this$JsMapperDisplay) {
    return function (it) {
      var tmp$;
      this$JsMapperDisplay.updateButtons_0(true);
      (tmp$ = this$JsMapperDisplay.onStart) != null ? tmp$() : null;
      return Unit;
    };
  }
  function JsMapperDisplay_init$lambda_0(this$JsMapperDisplay) {
    return function (it) {
      var tmp$;
      this$JsMapperDisplay.updateButtons_0(false);
      (tmp$ = this$JsMapperDisplay.onStop) != null ? tmp$() : null;
      return Unit;
    };
  }
  JsMapperDisplay.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'JsMapperDisplay',
    interfaces: [MapperDisplay]
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
  function clear_0($receiver) {
    while ($receiver.length > 0) {
      $receiver.remove(ensureNotNull($receiver.item(0)));
    }
  }
  function forEach($receiver, action) {
    var tmp$;
    tmp$ = $receiver.length;
    for (var i = 0; i < tmp$; i++) {
      action(ensureNotNull($receiver.item(i)));
    }
  }
  function getTimeMillis() {
    return Kotlin.Long.fromNumber((new Date()).getTime());
  }
  var package$baaahs = _.baaahs || (_.baaahs = {});
  package$baaahs.Brain = Brain;
  package$baaahs.SimBrain = SimBrain;
  Object.defineProperty(Color, 'Companion', {
    get: Color$Companion_getInstance
  });
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
  package$baaahs.MapperDisplay = MapperDisplay;
  Dmx.Universe = Dmx$Universe;
  Dmx.Buffer = Dmx$Buffer;
  Dmx.DeviceType = Dmx$DeviceType;
  package$baaahs.Dmx = Dmx;
  package$baaahs.FakeDmxUniverse = FakeDmxUniverse;
  _.ThingWithMass = ThingWithMass;
  _.Animal = Animal;
  _.Cat = Cat;
  _.Dog = Dog;
  _.Glass = Glass;
  _.kevmoMain = kevmoMain;
  Object.defineProperty(package$baaahs, 'main', {
    get: get_main,
    set: set_main
  });
  package$baaahs.Main = Main;
  package$baaahs.Mapper = Mapper;
  Network.Link = Network$Link;
  Network.Address = Network$Address;
  Network.Listener = Network$Listener;
  package$baaahs.Network = Network;
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
  Shenzarpy.Channel = Shenzarpy$Channel;
  package$baaahs.Shenzarpy = Shenzarpy;
  Object.defineProperty(ShaderType, 'SOLID', {
    get: ShaderType$SOLID_getInstance
  });
  Object.defineProperty(ShaderType, 'PIXEL', {
    get: ShaderType$PIXEL_getInstance
  });
  package$baaahs.ShaderType = ShaderType;
  package$baaahs.ShaderBuffer = ShaderBuffer;
  Object.defineProperty(SolidShaderBuffer, 'Companion', {
    get: SolidShaderBuffer$Companion_getInstance
  });
  package$baaahs.SolidShaderBuffer = SolidShaderBuffer;
  Object.defineProperty(PixelShaderBuffer, 'Companion', {
    get: PixelShaderBuffer$Companion_getInstance
  });
  package$baaahs.PixelShaderBuffer = PixelShaderBuffer;
  package$baaahs.Show = Show;
  package$baaahs.SomeDumbShow = SomeDumbShow;
  package$baaahs.Visualizer = Visualizer;
  package$baaahs.JsPanel = JsPanel;
  package$baaahs.MovingHeadView = MovingHeadView;
  package$baaahs.ByteArrayWriter_init_za3lpa$ = ByteArrayWriter_init;
  package$baaahs.ByteArrayWriter = ByteArrayWriter;
  package$baaahs.ByteArrayReader = ByteArrayReader;
  package$baaahs.random_2p1efm$ = random;
  package$baaahs.random_hhb8gh$ = random_0;
  package$baaahs.toRadians_mx4ult$ = toRadians;
  package$baaahs.doRunBlocking_g2bo5h$ = doRunBlocking;
  package$baaahs.getResource_61zpoe$ = getResource;
  package$baaahs.getDisplay = getDisplay;
  package$baaahs.JsDisplay = JsDisplay;
  package$baaahs.JsNetworkDisplay = JsNetworkDisplay;
  package$baaahs.JsPinkyDisplay = JsPinkyDisplay;
  package$baaahs.JsBrainDisplay = JsBrainDisplay;
  package$baaahs.JsMapperDisplay = JsMapperDisplay;
  package$baaahs.forEach_dokpt5$ = forEach;
  package$baaahs.getTimeMillis = getTimeMillis;
  FakeNetwork$FakeLink.prototype.send_bkw8fl$ = Network$Link.prototype.send_bkw8fl$;
  FakeNetwork$FakeLink.prototype.broadcast_ecsl0t$ = Network$Link.prototype.broadcast_ecsl0t$;
  Kotlin.defineModule('play', _);
  return _;
}(typeof play === 'undefined' ? {} : play, kotlin, this['kotlinx-coroutines-core']);
